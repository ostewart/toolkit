package com.trailmagic.image.impl

import com.trailmagic.image.HeavyImageManifestation
import com.trailmagic.image.ImageManifestationRepository
import com.trailmagic.image.ImageManifestationService
import com.trailmagic.image.Photo
import com.trailmagic.image.security.SecurityTestHelper
import com.trailmagic.resizer.ImageFileInfo
import com.trailmagic.resizer.ImageResizeService
import com.trailmagic.user.User
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Matchers._
import org.mockito.MockitoAnnotations
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.sql.Blob
import java.util.Arrays
import org.junit.Assert.assertSame
import org.mockito.Mockito._

object ImageResizeClientImplTest {
  private final val EMPTY_INPUT_STREAM: ByteArrayInputStream = new ByteArrayInputStream(Array[Byte]())
  private final val PORTRAIT_WIDTH: Int = 1935
  private final val PORTRAIT_HEIGHT: Int = 2592
}

class ImageResizeClientImplTest {
  import ImageResizeClientImplTest._

  @Mock private var imageManifestationService: ImageManifestationService = _
  @Mock private var imageResizeService: ImageResizeService = _
  @Mock private var imageManifestationRepository: ImageManifestationRepository = _
  private var imageResizeClient: ImageResizeClient = _

  @Before def setUp() {
    MockitoAnnotations.initMocks(this)
    new SecurityTestHelper().disableSecurityInterceptor()
    imageResizeClient = new ImageResizeClientImpl(imageResizeService, imageManifestationService, imageManifestationRepository)
  }

  @SuppressWarnings(Array("ResultOfMethodCallIgnored"))
  @Test def testCreatesOriginalManifestation() {
    val srcFile: File = mockFileToBeDeleted
    val photo: Photo = new Photo("name", new User("tester"))
    val fileInfo: ImageFileInfo = testFileInfo(srcFile, PORTRAIT_WIDTH, PORTRAIT_HEIGHT)
    when(imageResizeService.writeFile(any[InputStream]())).thenReturn(srcFile)
    when(imageResizeService.identify(srcFile)).thenReturn(fileInfo)

    imageResizeClient.createOriginalManifestation(photo, EMPTY_INPUT_STREAM)

    verify(imageManifestationService).createManifestation(photo, fileInfo, true)
    verify(srcFile).delete
  }

  @SuppressWarnings(Array("ResultOfMethodCallIgnored"))
  @Test def testCreatesResizedManifestations() {
    val srcFile: File = mockFileToBeDeleted
    val resizeFile: File = mockFileToBeDeleted
    val photo: Photo = new Photo("name", new User("tester"))
    val fileInfo: ImageFileInfo = testFileInfo(resizeFile, PORTRAIT_WIDTH, PORTRAIT_HEIGHT)
    val securityContext: SecurityContextImpl = new SecurityContextImpl
    val manifestation: HeavyImageManifestation = testOriginal
    when(imageManifestationRepository.findOriginalHeavyForImage(photo.getId)).thenReturn(manifestation)
    when(imageResizeService.writeFile(any[InputStream]())).thenReturn(srcFile)
    when(imageResizeService.scheduleResize(srcFile)).thenReturn(Arrays.asList(fileInfo))
    imageResizeClient.createResizedManifestations(photo, securityContext)
    verify(imageManifestationService).createManifestation(photo, fileInfo, false)
    verify(srcFile).delete
    verify(resizeFile).delete
    assertSame(securityContext, SecurityContextHolder.getContext)
  }

  private def testOriginal: HeavyImageManifestation = {
    val manifestation: HeavyImageManifestation = new HeavyImageManifestation
    val blob: Blob = mock(classOf[Blob])
    when(blob.getBinaryStream).thenReturn(EMPTY_INPUT_STREAM)
    manifestation.setData(blob)
    manifestation
  }

  private def mockFileToBeDeleted: File = {
    val resizeTempFile: File = mock(classOf[File])
    when(resizeTempFile.delete).thenReturn(true)
    resizeTempFile
  }

  private def testFileInfo(file: File, width: Int, height: Int): ImageFileInfo = {
    val fileInfo: ImageFileInfo = new ImageFileInfo(width, height, "image/jpeg")
    fileInfo.setFile(file)
    fileInfo
  }
}