package com.trailmagic.resizer

import java.io.File
import java.io.InputStream
import java.util.List

trait ImageResizeService {
  def scheduleResize(srcInputStream: InputStream): List[ImageFileInfo]

  def scheduleResize(srcFile: File): List[ImageFileInfo]

  def identify(srcFile: File): ImageFileInfo

  def writeFile(srcInputStream: InputStream): File
}