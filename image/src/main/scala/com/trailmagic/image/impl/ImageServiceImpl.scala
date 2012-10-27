package com.trailmagic.image.impl

import com.trailmagic.image._
import com.trailmagic.image.security.ImageSecurityService
import com.trailmagic.user.User
import com.trailmagic.user.UserRepository
import com.trailmagic.util.SecurityUtil
import com.trailmagic.util.TimeSource
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.util.Calendar
import scala.collection.JavaConverters._
import collection.mutable

object ImageServiceImpl {
  private val log: Logger = LoggerFactory.getLogger(classOf[ImageServiceImpl])
}

@Service("imageService")
class ImageServiceImpl @Autowired()(imageGroupRepository: ImageGroupRepository, imageRepository: ImageRepository,
                                    imageSecurityService: ImageSecurityService, userRepository: UserRepository,
                                    securityUtil: SecurityUtil, imageInitializer: ImageInitializer,
                                    timeSource: TimeSource, imageResizeClient: ImageResizeClient) extends ImageService {
  import ImageServiceImpl.log

  @Transactional(readOnly = false)
  @Secured(Array("ROLE_USER"))
  def createDefaultImage(fileName: String): Photo = {
    val imageMetadata: ImageMetadata = createDefaultMetadata
    imageMetadata.setShortName(fileName)
    createImage(imageMetadata)
  }

  private def createDefaultMetadata: ImageMetadata = {
    val imageMetadata: ImageMetadata = new ImageMetadata
    imageMetadata.setShortName("")
    imageMetadata.setDisplayName("")
    imageMetadata.setCreator(fullNameFromUser)
    imageMetadata.setCopyright("Copyright " + timeSource.calendar.get(Calendar.YEAR))
    imageMetadata
  }

  @Transactional(readOnly = false)
  @Secured(Array("ROLE_USER"))
  def createImageAtPosition(fileName: String, inputStream: InputStream, position: Int): Photo = {
    val imageMetadata: ImageMetadata = createDefaultMetadata
    imageMetadata.setPosition(position)
    imageMetadata.setShortName(fileName)
    createImage(imageMetadata)
  }

  private def fullNameFromUser: String = {
    val user = securityUtil.getCurrentUser

    user.getFirstName + " " + user.getLastName
  }

  @Secured(Array("ROLE_USER"))
  def createManifestations(photo: Photo, imageDataInputStream: InputStream) {
    imageResizeClient.createOriginalManifestation(photo, imageDataInputStream)
    imageResizeClient.createResizedManifestations(photo, SecurityContextHolder.getContext)
  }

  def createRollWithFrames(rollName: String, selectedFrameIds: mutable.Set[Long]) {}

  @Transactional(readOnly = false)
  @Secured(Array("ROLE_USER"))
  def createImage(imageData: ImageMetadata): Photo = {
    val photo: Photo = new Photo
    photo.setCaption(imageData.getCaption)
    photo.setName(imageData.getShortName)
    photo.setDisplayName(imageData.getDisplayName)
    photo.setCopyright(imageData.getCopyright)
    photo.setCreator(imageData.getCreator)
    val currentUser: User = securityUtil.getCurrentUser
    photo.setOwner(currentUser)
    photo.setRoll(findNamedOrDefaultRoll(imageData.getRollName, currentUser))
    imageInitializer.saveNewImage(photo)
    if (imageData.getPosition == null) {
      addImageToGroup(photo, photo.getRoll)
    }
    else {
      addImageToGroup(photo, photo.getRoll, position = imageData.getPosition)
    }
    photo
  }

  @Transactional(readOnly = false)
  def findNamedOrDefaultRoll(rollName: String, owner: User): ImageGroup = {
    if (StringUtils.isBlank(rollName)) {
      findOrCreateDefaultRollForUser(owner)
    }
    else {
      val roll: ImageGroup = imageGroupRepository.getRollByOwnerAndName(owner, rollName)
      if (roll == null) {
        throw new ImageGroupNotFoundException("Roll not found: " + rollName)
      }
      roll
    }
  }

  @Transactional(readOnly = false)
  def findOrCreateDefaultRollForUser(currentUser: User): ImageGroup = {
    var defaultRoll: ImageGroup = imageGroupRepository.getRollByOwnerAndName(currentUser, ImageGroup.DEFAULT_ROLL_NAME)
    if (defaultRoll == null) {
      defaultRoll = new ImageGroup(ImageGroup.DEFAULT_ROLL_NAME, currentUser, ImageGroupType.ROLL)
      defaultRoll.setSupergroup(null)
      defaultRoll.setUploadDate(timeSource.today)
      defaultRoll.setDisplayName("Uploads")
      defaultRoll.setDescription("Uploaded Images")
      imageInitializer.saveNewImageGroup(defaultRoll)
    }
    defaultRoll
  }

  @Transactional(readOnly = false)
  @Secured(Array("ROLE_USER"))
  def addImageToGroup(image: Image, group: ImageGroup): ImageFrame = {
    addImageToGroup(image, group, group.nextFramePosition)
  }

  @Transactional(readOnly = false)
  @Secured(Array("ROLE_USER"))
  def addImageToGroup(image: Image, group: ImageGroup, position: Int): ImageFrame = {
    val frame: ImageFrame = new ImageFrame(image)
    frame.setPosition(position)
    frame.setImageGroup(group)
    group.addFrame(frame)
    imageGroupRepository.saveGroup(group)
    frame
  }

  @Transactional(readOnly = false)
  def makeImageGroupAndImagesPublic(group: ImageGroup) {
    imageSecurityService.makePublic(group)
    log.info("Added public permission for group: " + group.getName)
    group.getFrames.asScala.foreach(frame => {
      val image: Image = frame.getImage
      imageSecurityService.makePublic(image)
      log.info("Added public permission for image: " + image.getDisplayName)
    })
  }

  @Transactional(readOnly = false)
  def makeImageGroupAndImagesPublic(ownerName: String, `type` : ImageGroupType, imageGroupName: String) {
    val owner: User = userRepository.getByScreenName(ownerName)
    val group: ImageGroup = imageGroupRepository.getByOwnerNameAndTypeWithFrames(owner, imageGroupName, `type`)
    if (group == null) {
      log.error("No " + `type` + " found with name " + imageGroupName + " owned by " + owner)
    }
    makeImageGroupAndImagesPublic(group)
  }

  @Transactional(readOnly = false)
  @Secured(Array("ROLE_USER"))
  def setImageGroupPreview(imageGroupId: Long, imageId: Long) {
    val imageGroup: ImageGroup = imageGroupRepository.loadById(imageGroupId)
    val image: Image = imageRepository.loadById(imageId)
    imageGroup.setPreviewImage(image)
    imageGroupRepository.saveGroup(imageGroup)
  }
}