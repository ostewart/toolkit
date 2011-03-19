package com.trailmagic.image.impl

import com.trailmagic.image.HeavyImageManifestation
import com.trailmagic.image.Image
import com.trailmagic.image.ImageGroup
import com.trailmagic.image.ImageGroupRepository
import com.trailmagic.image.ImageManifestationRepository
import com.trailmagic.image.ImageRepository
import com.trailmagic.image.security.ImageSecurityService
import com.trailmagic.util.SecurityUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.trailmagic.user.{User, Owned}

object ImageInitializerImpl {
  private var log: Logger = LoggerFactory.getLogger(classOf[ImageInitializerImpl])
}

@Transactional(readOnly = false)
@Service("imageInitializer")
class ImageInitializerImpl @Autowired()(imageGroupRepository: ImageGroupRepository,
                                        imageRepository: ImageRepository,
                                        imageSecurityService: ImageSecurityService,
                                        imageManifestationRepository: ImageManifestationRepository,
                                        securityUtil: SecurityUtil) extends ImageInitializer {
  import ImageInitializerImpl.log

  def ownerForOwned(owned: Owned): User = {
    securityUtil.getCurrentUser match {
      case None => throw new IllegalStateException("Can't save an image with no owner")
      case Some(user) => user
    }
  }

  def saveNewImage(image: Image): Unit = {
    log.info("Saving image: " + image)

    image.owner = ownerForOwned(image)

    imageRepository.saveNew(image)
    imageSecurityService.addOwnerAcl(image)
  }

  def saveNewImageGroup(imageGroup: ImageGroup): Unit = {
    log.info("Saving image group: " + imageGroup)

    imageGroup.setOwner(ownerForOwned(imageGroup))

    if (imageGroup.getPreviewImage == null && imageGroup.getFrames != null && imageGroup.getFrames.size > 0) {
      imageGroup.setPreviewImage(imageGroup.getFrames.first.getImage)
      if (log.isDebugEnabled) {
        log.debug("Set missing preview image to first image on group: " + imageGroup.getName)
      }
    }
    imageGroupRepository.saveNewGroup(imageGroup)
    imageSecurityService.addOwnerAcl(imageGroup)
  }

  def saveNewImageManifestation(imageManifestation: HeavyImageManifestation): Unit = {
    saveNewImageManifestation(imageManifestation, true)
  }

  def saveNewImageManifestation(imageManifestation: HeavyImageManifestation, clearFromSession: Boolean): Unit = {
    imageManifestationRepository.saveNewImageManifestation(imageManifestation)
    log.info("Saved image manifestation" + (if (clearFromSession) " (before flush/evict)" else "") + ": " + imageManifestation)
    if (clearFromSession) {
      imageManifestationRepository.cleanFromSession(imageManifestation)
    }
  }
}