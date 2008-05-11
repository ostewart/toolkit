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
import com.trailmagic.image.ImageFactory;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.Photo;
import java.util.List;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
@SuppressWarnings("unchecked") // for query.list()
public class HibernateImageFactory extends HibernateDaoSupport implements ImageFactory{
    private static final String ALL_IMAGES_QUERY_NAME = "allImages";
    private static final String IMAGES_BY_NAME_QUERY_NAME = "imagesByName";
    private static final String IMAGES_BY_NAME_GROUP_QUERY_NAME =
        "imagesByNameAndGroup";

    public Image newInstance() {
        return new Image();
    }

    public Image getById(long id) {
        return (Image) getHibernateTemplate().get(Image.class, new Long(id));
    }

    public List<Image> getAll() {
        return getHibernateTemplate().findByNamedQuery(ALL_IMAGES_QUERY_NAME);
    }

    public List<Image> getByName(String name) {
        return getHibernateTemplate().findByNamedQuery(IMAGES_BY_NAME_QUERY_NAME);
    }

    public List<Image> getByNameAndGroup(String name, ImageGroup group) {
        return getHibernateTemplate()
            .findByNamedQueryAndNamedParam(IMAGES_BY_NAME_GROUP_QUERY_NAME,
                                           new String[] {"name", "group"},
                                           new Object[] {name, group});
    }

    public Photo createPhoto() {
        // TODO Auto-generated method stub
        return new Photo();
    }

    @Transactional(readOnly=false)
    public void save(Photo newPhoto) {
        getHibernateTemplate().save(newPhoto);
    }
}
