package com.trailmagic.resizer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.io.InputStream
import scala.collection.JavaConverters._

@Service
class ImageResizeServiceImpl @Autowired()(imageResizer: ImageResizer) extends ImageResizeService {
  def scheduleResize(srcInputStream: InputStream): java.util.List[ImageFileInfo] = {
    withTempFile(writeFile(srcInputStream)) {
      scheduleResize(_)
    }
  }

  def withTempFile[T](file: File)(block: (File) => T) = {
    try {
      block(file)
    } finally {
      file.delete
    }
  }

  def scheduleResize(srcFile: File): java.util.List[ImageFileInfo] = {
    val srcFileInfo: ImageFileInfo = imageResizer.identify(srcFile)
    val resultInfoList = List[ImageFileInfo]()
    val sizes = List(128, 256, 512, 1024, 2048)

    val newManifestations = sizes filter (_ <= srcFileInfo.getWidth) map (resizeAndIdentify(srcFile, srcFileInfo, _))
    newManifestations.asJava
  }

  def identify(srcFile: File): ImageFileInfo = {
    imageResizer.identify(srcFile)
  }

  def writeFile(srcInputStream: InputStream): File = {
    try {
      imageResizer.writeToTempFile(srcInputStream)
    }
    catch {
      case e: IOException => {
        throw new ResizeFailedException("Could not write src to temp file", e)
      }
    }
  }

  private def resizeAndIdentify(srcFile: File, srcFileInfo: ImageFileInfo, size: Int): ImageFileInfo = {
    val file = imageResizer.resizeImage(srcFile, srcFileInfo, size)
    val info = imageResizer.identify(file)
    info.setFile(file)
    info
  }
}

