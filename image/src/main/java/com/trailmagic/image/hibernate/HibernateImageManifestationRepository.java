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
package com.trailmagic.image.hibernate;

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.ImageManifestation;
import com.trailmagic.image.ImageManifestationRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Repository("imageManifestationRepository")
public class HibernateImageManifestationRepository
        implements ImageManifestationRepository {

    private static final String ALL_FOR_IMAGE_ID_QUERY_NAME =
            "allImageManifestationsForImageId";

    private HibernateTemplate hibernateTemplate;
    private Log log = LogFactory.getLog(HibernateImageManifestationRepository.class);

    @Autowired
    public HibernateImageManifestationRepository(HibernateTemplate hibernateTemplate) {
        super();
        this.hibernateTemplate = hibernateTemplate;
    }

    public ImageManifestation getById(long id) {
        return (ImageManifestation)
                hibernateTemplate.get(ImageManifestation.class, id);
    }

    public HeavyImageManifestation getHeavyById(long id) {
        return (HeavyImageManifestation)
                hibernateTemplate.get(HeavyImageManifestation.class, id);
    }

    @Transactional(readOnly = false)
    public void saveNewImageManifestation(HeavyImageManifestation imageManifestation) {
        log.info("Saving image manifestation: " + imageManifestation);
        hibernateTemplate.save(imageManifestation);
    }

    public void cleanFromSession(ImageManifestation imageManifestation) {
        if (log.isDebugEnabled()) {
            log.debug("Flushing image manifestation state: "
                      + imageManifestation);
        }
        hibernateTemplate.flush();
        hibernateTemplate.evict(imageManifestation);
    }

    @Override
    public HeavyImageManifestation findOriginalHeavyForImage(long imageId) {
        final List results = hibernateTemplate.findByNamedQueryAndNamedParam("originalHeavyManifestationForImageId", "imageId", imageId);
        if (results.isEmpty()) {
            return null;
        } else {
            return (HeavyImageManifestation) results.get(0);
        }
    }
}
