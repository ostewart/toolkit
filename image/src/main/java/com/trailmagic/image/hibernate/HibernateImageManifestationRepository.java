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
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateImageManifestationRepository
        implements ImageManifestationRepository {

    private static final String ALL_FOR_IMAGE_ID_QUERY_NAME =
        "allImageManifestationsForImageId";
    
    private HibernateTemplate hibernateTemplate;
    private Log log = LogFactory.getLog(HibernateImageManifestationRepository.class);
        
    public HibernateImageManifestationRepository(HibernateTemplate hibernateTemplate) {
        super();
        this.hibernateTemplate = hibernateTemplate;
    }
        
    public ImageManifestation newInstance() {
        return new ImageManifestation();
    }

    public ImageManifestation getById(long id) {
        return (ImageManifestation)
            hibernateTemplate.get(ImageManifestation.class, id);
    }

    public HeavyImageManifestation getHeavyById(long id) {
        return (HeavyImageManifestation)
            hibernateTemplate.get(HeavyImageManifestation.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<ImageManifestation> getAllForImageId(long imageId) {
        List results =
            hibernateTemplate.findByNamedQueryAndNamedParam(ALL_FOR_IMAGE_ID_QUERY_NAME, "imageId", imageId);
        List<ImageManifestation> typedResults =
            new ArrayList<ImageManifestation>();
        for (Object result : results) {
            typedResults.add((ImageManifestation) result);
        }
        return typedResults;
    }
    
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
}
