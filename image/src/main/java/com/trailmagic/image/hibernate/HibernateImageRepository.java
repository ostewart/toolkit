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

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageRepository;
import com.trailmagic.image.NoSuchImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SuppressWarnings({"unchecked"})// for query.list()
@Transactional(readOnly = true)
@Repository("imageRepository")
public class HibernateImageRepository implements ImageRepository {
    private static final String ALL_IMAGES_QUERY_NAME = "allImages";
    private static final String IMAGES_BY_NAME_GROUP_QUERY_NAME = "imagesByNameAndGroup";
    private HibernateTemplate hibernateTemplate;

    @Autowired
    public HibernateImageRepository(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public Image getById(long id) {
        return hibernateTemplate.get(Image.class, id);
    }

    public Image loadById(long imageId) {
        try {
            return hibernateTemplate.load(Image.class, imageId);
        } catch (ObjectRetrievalFailureException e) {
            throw new NoSuchImageException(imageId, e);
        }
    }

    public List<Image> getAll() {
        return hibernateTemplate.findByNamedQuery(ALL_IMAGES_QUERY_NAME);
    }

    public List<Image> getByNameAndGroup(String name, ImageGroup group) {
        return hibernateTemplate.findByNamedQueryAndNamedParam(IMAGES_BY_NAME_GROUP_QUERY_NAME,
                                                               new String[]{"name", "group"},
                                                               new Object[]{name, group});
    }

    @Transactional(readOnly = false)
    public void saveNew(Image image) {
        hibernateTemplate.save(image);
    }

    @Transactional(readOnly = false)
    public Image save(Image image) {
        return hibernateTemplate.merge(image);
    }

}
