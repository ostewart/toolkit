package com.trailmagic.image.impl

import com.trailmagic.image.HeavyImageManifestation
import com.trailmagic.image.Image
import com.trailmagic.image.ImageManifestationRepository
import com.trailmagic.image.ImageManifestationService
import com.trailmagic.resizer.ImageFileInfo
import com.trailmagic.resizer.ImageResizeService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.InputStream
import java.sql.SQLException
import scala.collection.JavaConverters._

@Service
class ImageResizeClientImpl @Autowired()(imageResizeService: ImageResizeService,
                                         imageManifestationService: ImageManifestationService,
                                         imageManifestationRepository: ImageManifestationRepository) extends ImageResizeClient {
  val log = LoggerFactory.getLogger(classOf[ImageResizeClientImpl])

  @Transactional(readOnly = false)
  def createOriginalManifestation(image: Image, inputStream: InputStream) {
    val srcFile: File = imageResizeService.writeFile(inputStream)
    val srcFileInfo: ImageFileInfo = imageResizeService.identify(srcFile)
    try {
      imageManifestationService.createManifestation(image, srcFileInfo, true)
    } finally {
      val deleted: Boolean = srcFile.delete
      if (!deleted) {
        log.warn("Failed to delete temporary image file: " + srcFile.getAbsolutePath)
      }
    }
  }

  @Async
  @Transactional(readOnly = false)
  def createResizedManifestations(image: Image, securityContext: SecurityContext) {
    SecurityContextHolder.setContext(securityContext)
    try {
      val original: HeavyImageManifestation = imageManifestationRepository.findOriginalHeavyForImage(image.getId)
      if (original == null) {
        throw new IllegalStateException("no original image found for image: " + image)
      }
      try {
        val srcFile: File = imageResizeService.writeFile(original.getData.getBinaryStream)
        try {
          imageResizeService.scheduleResize(srcFile).asScala.foreach {
            info =>
              imageManifestationService.createManifestation(image, info, false)
              val deleted: Boolean = info.getFile.delete
              if (!deleted) {
                log.warn("Could not delete resize temp file " + info.getFile.getAbsolutePath)
              }
          }
        } finally {
          val deleted: Boolean = srcFile.delete
          if (!deleted) {
            log.warn("Could not delete image temp file " + srcFile.getAbsolutePath)
          }
        }
      } catch {
        case e: SQLException => {
          log.error("Failed to retrieve data from original manifestation for image: " + image, e)
          throw new IllegalStateException("Failed to retrieve data from original manifestation for image: " + image, e)
        }
      }
    } catch {
      case t: Throwable => {
        log.error("Caught exception during resize for image (which may be in an inconsistent state: " + image, t)
      }
    }
  }
}