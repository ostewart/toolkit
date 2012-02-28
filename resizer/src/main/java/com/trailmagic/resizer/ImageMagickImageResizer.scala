package com.trailmagic.resizer

import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

@Service
class ImageMagickImageResizer @Autowired() (private val executor: CommandExcecutor) extends ImageResizer {
  def resizeImage(srcFile: File, imageInfo: ImageFileInfo, shortestDimensionLength: Int): File = {
    try {
      val destFile: File = File.createTempFile("image-resizer-output", ".jpg")
      executor.exec("convert -quality 80 -resize", geometryString(imageInfo, shortestDimensionLength), srcFile.getAbsolutePath, destFile.getAbsolutePath)
      destFile
    } catch {
      case e: IOException => {
        throw new ResizeFailedException(e)
      }
    }
  }

  private def geometryString(imageInfo: ImageFileInfo, shortestDimensionLength: Int): String = {
    if (imageInfo.isLandscape) "x" + shortestDimensionLength + ">"
    else shortestDimensionLength + "x>"
  }

  def writeToTempFile(imageInputStream: InputStream): File = {
    val file: File = File.createTempFile("image-resizer-input", ".jpg")
    IOUtils.copy(imageInputStream, new FileOutputStream(file))
    file
  }

  def identify(file: File): ImageFileInfo = {
    val output: String = executor.exec("identify " + file.getAbsolutePath).get(0)

    val MatchIdentity = """.*?([A-Z]+) (\d+)x(\d+).*?""".r
    output match {
      case MatchIdentity(format, width, height) => new ImageFileInfo(width.toInt, height.toInt, mimeTypeFromFormat(format), file)
      case _ => throw new CouldNotIdentifyException("Failed to match output: " + output)
    }
  }

  private def mimeTypeFromFormat(format: String): String = {
    if ("JPEG".equals(format.trim)) {
      return "image/jpeg"
    }
    throw new CouldNotIdentifyException("unknown format: " + format)
  }
}