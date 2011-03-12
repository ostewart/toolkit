package com.trailmagic.resizer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.io.InputStream
import scalaj.collection.Imports._

@Service
class ImageResizeServiceImpl @Autowired()(imageResizer: ImageResizer) extends ImageResizeService {
  override def scheduleResize(srcInputStream: InputStream): java.util.List[ImageFileInfo] = {
    withTempFile(writeFile(srcInputStream)) {
      scheduleResize(_)
    }
  }

  def withTempFile[T](file: File)(block: (File) => T) = {
    try {
      block(file)
    } finally {
      file.delete;
    }
  }

  def scheduleResize(srcFile: File): java.util.List[ImageFileInfo] = {
    val srcFileInfo: ImageFileInfo = imageResizer.identify(srcFile)
    val resultInfoList = List[ImageFileInfo]()


    (List[ImageFileInfo]() /: List(128, 256, 512, 1024, 2048)) {
      (infoList, size) =>
        if (size <= srcFileInfo.getWidth) resizeAndIdentify(srcFile, srcFileInfo, size) :: infoList
        else infoList
    }.asJava

    //    for {
    //      size <- List(128, 256, 512, 1024, 2048)
    //      if (size <= srcFileInfo.getWidth)
    //    } yield resultInfoList += resizeAndIdentify(srcFile, srcFileInfo, size)
  }

  def identify(srcFile: File): ImageFileInfo = {
    return imageResizer.identify(srcFile)
  }

  def writeFile(srcInputStream: InputStream): File = {
    try {
      return imageResizer.writeToTempFile(srcInputStream)
    }
    catch {
      case e: IOException => {
        throw new ResizeFailedException("Could not write src to temp file", e)
      }
    }
  }

  private def resizeAndIdentify(srcFile: File, srcFileInfo: ImageFileInfo, size: Int): ImageFileInfo = {
    var file: File = imageResizer.resizeImage(srcFile, srcFileInfo, size)
    var info: ImageFileInfo = imageResizer.identify(file)
    info.setFile(file)
    return info
  }
}

