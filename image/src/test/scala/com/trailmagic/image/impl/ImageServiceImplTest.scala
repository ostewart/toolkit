package com.trailmagic.image.impl

import com.trailmagic.image._
import com.trailmagic.image.security.ImageSecurityService
import com.trailmagic.image.security.SecurityTestHelper
import com.trailmagic.resizer.ImageResizeService
import com.trailmagic.user.User
import com.trailmagic.user.UserRepository
import com.trailmagic.util.SecurityUtil
import com.trailmagic.util.TimeSource
import org.junit.Before
import org.junit.Test
import org.springframework.security.core.context.SecurityContext
import java.io.ByteArrayInputStream
import java.util.Calendar
import java.util.Date
import org.junit.Assert._
import org.mockito.Matchers.{any, isA}
import org.mockito.Mockito._
import org.mockito.{Matchers, Mock, Mockito, MockitoAnnotations}

object ImageServiceImplTest {
  val MANIFESTATION_ID: Long = 1234L
  val DEFAULT_GROUP_ID: Long = MANIFESTATION_ID
  val TEST_TIME: Date = new Date
  val TEST_ROLL_NAME: String = "my-awesome-roll"
  val EMPTY_INPUT_STREAM: ByteArrayInputStream = new ByteArrayInputStream(Array[Byte]())
  val FIRST_NAME: String = "Testy"
  val LAST_NAME: String = "McTesterton"
  val TEST_YEAR: Int = 2010
  val ARBITRARY_POSITION: Int = 62
}

@SuppressWarnings(Array("ResultOfMethodCallIgnored"))
class ImageServiceImplTest {
  private var imageService: ImageService = null
  @Mock private var userRepository: UserRepository = null
  @Mock private var imageManifestationRepository: ImageManifestationRepository = null
  @Mock private var imageGroupRepository: ImageGroupRepository = null
  @Mock private var imageRepository: ImageRepository = null
  @Mock private var imageSecurityService: ImageSecurityService = null
  @Mock private var imageInitializer: ImageInitializer = null
  @Mock private var securityUtil: SecurityUtil = null
  @Mock private var timeSource: TimeSource = null
  @Mock private var imageResizeService: ImageResizeService = null
  @Mock private var hibernateUtil: HibernateUtil = null
  @Mock private var imageManifestationService: ImageManifestationService = null
  @Mock private var imageResizeClient: ImageResizeClient = null
  private var defaultGroup: ImageGroup = null
  private final val securityTestHelper: SecurityTestHelper = new SecurityTestHelper
  private var testUser: User = null
  
import com.trailmagic.image.impl.ImageServiceImplTest._
  @Before
  def setUp: Unit = {
    MockitoAnnotations.initMocks(this)
    testUser = new User("testy")
    testUser.setFirstName(FIRST_NAME)
    testUser.setLastName(LAST_NAME)
    when(timeSource.today).thenReturn(TEST_TIME)
    var calendar: Calendar = Calendar.getInstance
    calendar.set(TEST_YEAR, Calendar.FEBRUARY, 28)
    when(timeSource.calendar).thenReturn(calendar)
    when(securityUtil.getCurrentUser).thenReturn(Some(testUser))
    imageService = new ImageServiceImpl(imageGroupRepository, imageRepository, imageSecurityService, userRepository, securityUtil, imageInitializer, timeSource, imageResizeClient)
  }

  private def withCurrentUser(currentUser: User, hasDefaultGroup: Boolean): Unit = {
    if (hasDefaultGroup) {
      defaultGroup = setupDefaultGroup(currentUser)
    }
    when(imageGroupRepository.getRollByOwnerAndName(currentUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(defaultGroup)
  }

  @Test
  def testCreateImageWithDefaultGroup: Unit = {
    var currentUser: User = testUser
    withCurrentUser(currentUser, true)
    var imageMetadata: ImageMetadata = setupImageMetadata
    val image: Photo = imageService.createImage(imageMetadata)
    assertEquals(currentUser, image.getOwner)
    assertNotNull(image.getId)
    assertEquals(defaultGroup, image.getRoll)
    verify(imageGroupRepository).getRollByOwnerAndName(currentUser, ImageGroup.DEFAULT_ROLL_NAME)
    verify(imageInitializer, Mockito.times(1)).saveNewImage(Matchers.isA(classOf[Photo]))
  }

  @Test
  def testLooksUpRoll: Unit = {
    var currentUser: User = testUser
    withCurrentUser(currentUser, true)
    var imageMetadata: ImageMetadata = setupImageMetadata
    imageMetadata.setRollName(TEST_ROLL_NAME)
    val existingRoll: ImageGroup = new ImageGroup(TEST_ROLL_NAME, currentUser, ImageGroupType.ROLL)
    when(imageGroupRepository.getRollByOwnerAndName(currentUser, TEST_ROLL_NAME)).thenReturn(existingRoll)
    val image: Photo = imageService.createImage(imageMetadata)
    assertEquals(existingRoll, image.getRoll)
  }

  @Test
  def testCreatesNewDefaultGroup: Unit = {
    var currentUser: User = testUser
    withCurrentUser(currentUser, false)
    var imageMetadata: ImageMetadata = setupImageMetadata
    val image: Photo = imageService.createImage(imageMetadata)
    val assignedGroup: ImageGroup = image.getRoll
    assertNotNull(assignedGroup)
    assertEquals(ImageGroup.DEFAULT_ROLL_NAME, assignedGroup.getName)
    assertEquals(ImageGroupType.ROLL, assignedGroup.getType)
  }

  @Test
  def testSavesImageMetaData: Unit = {
    securityTestHelper.disableSecurityInterceptor
    withCurrentUser(testUser, true)
    var imageMetadata: ImageMetadata = setupImageMetadata
    val originalManifestation: ImageManifestation = new ImageManifestation
    originalManifestation.setId(MANIFESTATION_ID)
    val photo: Photo = imageService.createImage(imageMetadata)
    assertEquals(imageMetadata.getCaption, photo.getCaption)
    assertEquals(imageMetadata.getCopyright, photo.getCopyright)
    assertEquals(imageMetadata.getCreator, photo.getCreator)
    assertEquals(imageMetadata.getDisplayName, photo.getDisplayName)
    assertEquals(imageMetadata.getShortName, photo.getName)
    verify(imageInitializer).saveNewImage(photo)
    verify(imageResizeClient, never).createOriginalManifestation(isA(classOf[Image]), Matchers.eq(EMPTY_INPUT_STREAM))
    verify(imageResizeClient, never).createResizedManifestations(isA(classOf[Image]), isA(classOf[SecurityContext]))
    verify(imageGroupRepository).saveGroup(isA(classOf[ImageGroup]))
  }

  @Test
  def testSetsDefaultMetaData: Unit = {
    var image: Photo = imageService.createDefaultImage("foo.jpg")
    assertMatchesDefaultMetadata(image)
    assertEquals("foo.jpg", image.getName)
  }

  private def assertMatchesDefaultMetadata(image: Photo): Unit = {
    assertEquals("Copyright " + TEST_YEAR, image.getCopyright)
    assertEquals(String.format("%s %s", FIRST_NAME, LAST_NAME), image.getCreator)
    assertEquals(ImageGroup.DEFAULT_ROLL_NAME, image.getRoll.getName)
  }

  @Test
  def testCreatesManifestations: Unit = {
    var image: Photo = new Photo("foo", testUser)
    imageService.createManifestations(image, EMPTY_INPUT_STREAM)
    verify(imageResizeClient).createOriginalManifestation(image, EMPTY_INPUT_STREAM)
    verify(imageResizeClient).createResizedManifestations(Matchers.eq(image), isA(classOf[SecurityContext]))
  }

  @Test
  def testSetsDefaultMetaDataWithPosition: Unit = {
    var image: Photo = imageService.createImageAtPosition("foo.jpg", EMPTY_INPUT_STREAM, ARBITRARY_POSITION)
    verify(imageResizeClient, never).createOriginalManifestation(image, EMPTY_INPUT_STREAM)
    verify(imageResizeClient, never).createResizedManifestations(Matchers.eq(image), isA(classOf[SecurityContext]))
    assertMatchesDefaultMetadata(image)
    assertEquals(ARBITRARY_POSITION, image.getRoll.getFrames.first.getPosition)
    assertEquals("foo.jpg", image.getName)
  }

  @Test
  def testFindOrCreateRollCreatesDefaultRoll: Unit = {
    when(imageGroupRepository.getRollByOwnerAndName(testUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(null)
    var roll: ImageGroup = imageService.findNamedOrDefaultRoll(null, testUser)
    verify(imageInitializer).saveNewImageGroup(any(classOf[ImageGroup]))
    assertEquals(ImageGroup.DEFAULT_ROLL_NAME, roll.getName)
    assertEquals("Uploads", roll.getDisplayName)
    assertEquals("Uploaded Images", roll.getDescription)
    assertEquals(ImageGroupType.ROLL, roll.getType)
    assertEquals(testUser, roll.getOwner)
    assertEquals(TEST_TIME, roll.getUploadDate)
    assertNull(roll.getSupergroup)
  }

  @Test
  def testFindOrCreateRollFindsDefaultRoll: Unit = {
    var expectedGroup: ImageGroup = new ImageGroup("test", testUser, ImageGroupType.ROLL)
    when(imageGroupRepository.getRollByOwnerAndName(testUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(expectedGroup)
    var roll: ImageGroup = imageService.findNamedOrDefaultRoll(null, testUser)
    verify(imageInitializer, never).saveNewImageGroup(any(classOf[ImageGroup]))
    assertEquals(expectedGroup, roll)
  }

  @Test
  def testFindOrCreateRollFindsDefaultRollWithEmptyString: Unit = {
    var expectedGroup: ImageGroup = new ImageGroup("test", testUser, ImageGroupType.ROLL)
    when(imageGroupRepository.getRollByOwnerAndName(testUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(expectedGroup)
    var roll: ImageGroup = imageService.findNamedOrDefaultRoll("", testUser)
    verify(imageInitializer, never).saveNewImageGroup(any(classOf[ImageGroup]))
    assertEquals(expectedGroup, roll)
  }

  @Test
  def testFindOrCreateRollFindsNamedRoll: Unit = {
    var expectedGroup: ImageGroup = new ImageGroup("test", testUser, ImageGroupType.ROLL)
    when(imageGroupRepository.getRollByOwnerAndName(testUser, TEST_ROLL_NAME)).thenReturn(expectedGroup)
    var roll: ImageGroup = imageService.findNamedOrDefaultRoll(TEST_ROLL_NAME, testUser)
    verify(imageInitializer, never).saveNewImageGroup(any(classOf[ImageGroup]))
    assertEquals(expectedGroup, roll)
  }

  @Test(expected = classOf[ImageGroupNotFoundException])
  def testFindOrCreateRollThrowsExceptionForMissingNamedRoll: Unit = {
    when(imageGroupRepository.getRollByOwnerAndName(testUser, "non-existent roll")).thenReturn(null)
    imageService.findNamedOrDefaultRoll(TEST_ROLL_NAME, testUser)
  }

  @Test
  def testAppendImageToGroup: Unit = {
    var group: ImageGroup = new ImageGroup("test", testUser, ImageGroupType.ROLL)
    var image: Photo = new Photo("test", testUser)
    var frame: ImageFrame = imageService.addImageToGroup(image, group)
    assertEquals(1, frame.getPosition)
    assertEquals(group, frame.getImageGroup)
    assertEquals(image, frame.getImage)
    assertEquals(testUser, frame.getOwner)
    verify(imageGroupRepository).saveGroup(group)
  }

  private def setupDefaultGroup(currentUser: User): ImageGroup = {
    val defaultGroup: ImageGroup = new ImageGroup(ImageGroup.DEFAULT_ROLL_NAME, currentUser, ImageGroupType.ROLL)
    defaultGroup.setId(DEFAULT_GROUP_ID)
    return defaultGroup
  }

  private def setupImageMetadata: ImageMetadata = {
    var imageMetadata: ImageMetadata = new ImageMetadata
    imageMetadata.setShortName("testImage")
    imageMetadata.setDisplayName("Test Image")
    imageMetadata.setCaption("This is a test image.")
    imageMetadata.setCopyright("Copyright 2009")
    imageMetadata.setCreator("Oliver Stewart")
    return imageMetadata
  }

}