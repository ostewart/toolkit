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
package com.trailmagic.image.util;

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupFactory;
import com.trailmagic.image.ImageManifestation;
import com.trailmagic.image.security.ImageSecurityFactory;
import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import java.util.Collection;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate.SessionFactoryUtils;

public class AddPermissions {
    private ImageGroupFactory m_imageGroupFactory;
    private ImageSecurityFactory m_imageSecurityFactory;
    private UserFactory m_userFactory;
    private SessionFactory m_sessionFactory;

    private static Logger s_log = Logger.getLogger(AddPermissions.class);

    private static final String ADD_PERMISSIONS_BEAN = "addPermissions";

    public ImageGroupFactory getImageGroupFactory() {
        return m_imageGroupFactory;
    }

    public void setImageGroupFactory(ImageGroupFactory factory) {
        m_imageGroupFactory = factory;
    }

    public ImageSecurityFactory getImageSecurityFactory() {
        return m_imageSecurityFactory;
    }

    public void setImageSecurityFactory(ImageSecurityFactory factory) {
        m_imageSecurityFactory = factory;
    }

    public UserFactory getUserFactory() {
        return m_userFactory;
    }

    public void setUserFactory(UserFactory factory) {
        m_userFactory = factory;
    }

    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }

    public void doIt(String ownerName, String rollName) {
        Session session =
            SessionFactoryUtils.getSession(m_sessionFactory, false);
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            User owner = m_userFactory.getByScreenName(ownerName);
            ImageGroup group =
                m_imageGroupFactory.getRollByOwnerAndName(owner, rollName);
            m_imageSecurityFactory.makePublic(group, null);
            s_log.info("Added public permission for group: "
                       + group.getName());

            Collection<ImageFrame> frames = group.getFrames();

            for (ImageFrame frame : frames) {
                m_imageSecurityFactory.makePublic(frame, group);
                s_log.info("Added public permission for frame: "
                           + frame.getPosition() + " of group "
                           + group.getName());

                Image image = frame.getImage();
                m_imageSecurityFactory.makePublic(image, group);
                s_log.info("Added public permission for image: "
                           + image.getDisplayName());

                for (ImageManifestation mf : image.getManifestations()) {
                    m_imageSecurityFactory.makePublic(mf, image);
                    s_log.info("Added public permission for manifestation: "
                               + mf.getHeight() + "x" + mf.getWidth());
                }
            }
            transaction.commit();
            SessionFactoryUtils.releaseSession(session,
                                               m_sessionFactory);
        } catch (Exception e) {
            try {
                if (transaction != null) {
                    transaction.rollback();
                    s_log.error("Exception adding acls; "
                                + "transaction rolled back",
                                e);
                }
            } catch (HibernateException e1) {
                s_log.error("Couldn't roll back transaction", e1);
            }
        }
    }

    public static final void main(String[] args) {
        ClassPathXmlApplicationContext appContext =
            new ClassPathXmlApplicationContext(new String[]
                {"applicationContext-global.xml",
                 "applicationContext-user.xml",
                 "applicationContext-imagestore.xml",
                 "applicationContext-imagestore-authorization.xml",
                 "applicationContext-standalone.xml"});

        AddPermissions ap =
            (AddPermissions) appContext.getBean(ADD_PERMISSIONS_BEAN);
        ap.doIt(args[0], args[1]);
    }
}