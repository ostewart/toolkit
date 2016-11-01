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
package com.trailmagic.image.hibernate

import com.trailmagic.image.HeavyImageManifestation
import com.trailmagic.image.ImageManifestation
import com.trailmagic.image.ImageManifestationRepository
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.hibernate3.HibernateTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.List
import scala.collection.JavaConverters._

@Transactional(readOnly = true)
@Repository("imageManifestationRepository")
class HibernateImageManifestationRepository @Autowired() (hibernateTemplate: HibernateTemplate) extends ImageManifestationRepository {
  private val log = LogFactory.getLog(classOf[HibernateImageManifestationRepository])

  def getById(id: Long): ImageManifestation = {
    hibernateTemplate.get(classOf[ImageManifestation], id)
  }

  def getHeavyById(id: Long): HeavyImageManifestation = {
    val result = hibernateTemplate.get(classOf[HeavyImageManifestation], id)
    result.getData.getBinaryStream // try to force loading within the transaction boundary
    result
  }

  @Transactional(readOnly = false)
  def saveNewImageManifestation(imageManifestation: HeavyImageManifestation) {
    log.info("Saving image manifestation: " + imageManifestation)
    hibernateTemplate.save(imageManifestation)
  }

  def cleanFromSession(imageManifestation: ImageManifestation) {
    if (log.isDebugEnabled) {
      log.debug("Flushing image manifestation state: " + imageManifestation)
    }
    hibernateTemplate.flush()
    hibernateTemplate.evict(imageManifestation)
  }

  def findOriginalHeavyForImage(imageId: Long): HeavyImageManifestation = {
    val results = hibernateTemplate.findByNamedQueryAndNamedParam("originalHeavyManifestationForImageId", "imageId", imageId).asScala
    results.headOption.map(_.asInstanceOf[HeavyImageManifestation]).getOrElse(null)
  }
}