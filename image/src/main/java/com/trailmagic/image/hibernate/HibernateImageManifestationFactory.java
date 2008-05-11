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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.hibernate.HibernateException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import com.trailmagic.image.*;

@Transactional
public class HibernateImageManifestationFactory
    implements ImageManifestationFactory {

    private static final String ALL_FOR_IMAGE_ID_QUERY_NAME =
        "allImageManifestationsForImageId";
    private SessionFactory m_sessionFactory;
        
    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }
        
    public ImageManifestation newInstance() {
        return new ImageManifestation();
    }

    public ImageManifestation getById(long id) {
        try {
            // XXX: should we allow creation here?
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);

            return (ImageManifestation)session.get(ImageManifestation.class,
                                                   new Long(id));
            
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
        
    }

    public HeavyImageManifestation getHeavyById(long id) {
        try {
            // XXX: should we allow creation here?
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);

            return (HeavyImageManifestation)
                session.get(HeavyImageManifestation.class, new Long(id));
            
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
        
    }

    public List getAllForImageId(long imageId) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ALL_FOR_IMAGE_ID_QUERY_NAME);
            qry.setLong("imageId", imageId);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }
}
