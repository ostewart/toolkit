/*
 * Copyright (c) 2006 Oliver Stewart.  All Rights Reserved.
 *
 * This file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.trailmagic.image

import java.io.InputStream

trait StreamWrapper {
  @throws(classOf[Exception])
  def stream(length: Int, contentType: String, name: String, in: InputStream)
}

trait ImageManifestationRepository {
  def getById(id: Long): ImageManifestation
  def getHeavyById(id: Long): HeavyImageManifestation
  def streamHeavyById(id: Long, stream: StreamWrapper): HeavyImageManifestation
  def saveNewImageManifestation(imageManifestation: HeavyImageManifestation)
  def cleanFromSession(imageManifestation: ImageManifestation)
  def findOriginalHeavyForImage(imageId: Long): HeavyImageManifestation
}