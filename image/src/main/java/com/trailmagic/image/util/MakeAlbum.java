/*
 * Copyright (c) 2005 Oliver Stewart.  All Rights Reserved.
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
import com.trailmagic.image.ImageRepository;
import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.user.UserRepository;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * Run like: ant run -Drun.class=com.trailmagic.image.util.MakeAlbum -Drun.args="oliver roll-name album-name keepers-file"
 **/
public class MakeAlbum implements ApplicationContextAware {
    private SessionFactory m_sessionFactory;
    private Transaction m_transaction;
    private ApplicationContext m_appContext;

    private static final String GROUP_FACTORY_BEAN = "imageGroupRepository";
    private static final String USER_FACTORY_BEAN = "userFactory";
    private static final String IMAGE_FACTORY_BEAN = "imageFactory";
    private static final String MAKE_ALBUM_BEAN = "makeAlbum";

    private static Logger s_logger = LoggerFactory.getLogger(MakeAlbum.class);

    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        m_appContext = applicationContext;
    }

    public void doStuff(String userName, String rollName, String albumName,
                        String fileName) {
        Session m_session = SessionFactoryUtils.getSession(m_sessionFactory, false);
        try {
            BufferedReader keepers =
                new BufferedReader(new FileReader(fileName));

            m_transaction = m_session.beginTransaction();

            ImageGroupRepository gf =
                (ImageGroupRepository)m_appContext.getBean(GROUP_FACTORY_BEAN);
            UserRepository uf =
                (UserRepository)m_appContext.getBean(USER_FACTORY_BEAN);

            ImageGroup roll =
                gf.getRollByOwnerAndName(uf.getByScreenName(userName),
                                         rollName);

            if (roll == null) {
                s_logger.error("No roll found by that name");
                throw new IllegalArgumentException("Invalid roll name");
            }

            ImageGroup album = new ImageGroup(albumName, roll.getOwner(), ImageGroup.Type.ALBUM);
            album.setDisplayName(roll.getDisplayName());
            album.setDescription(roll.getDescription());
            album.setSupergroup(roll.getSupergroup());
            // XXX: not copying subgroups
            // this is probably a leaf group
            m_session.save(album);


            // read each image name from keepers file, get the image from
            // the roll, and add it to the album

            ImageRepository imgFactory =
                (ImageRepository)m_appContext.getBean(IMAGE_FACTORY_BEAN);
            String name = keepers.readLine();
            int position = 0;

            while (name != null) {
                List images = imgFactory.getByNameAndGroup(name, roll);
                if (images.size() > 1) {
                    // FIXME: this should check for uniqueness within the roll
                    s_logger.error("Search by name returned more than one "
                                   + "image. Aborting.");
                    throw new IllegalStateException("Duplicate image: "
                                                    + name);
                } else if (images.size() < 1) {
                    s_logger.error("Search by name returned no image. "
                                   + "Aborting.");
                    throw new IllegalStateException("Image not found");
                }

                Image image = (Image)images.get(0);

                ImageFrame frame = new ImageFrame(image);
                frame.setPosition(position);
                frame.setImageGroup(album);
                m_session.save(frame);

                s_logger.debug("Saved frame " + frame.getPosition()
                               + "with image " + image.getName());

                position++;
                name = keepers.readLine();
            }

            m_session.saveOrUpdate(album);
            m_transaction.commit();
            SessionFactoryUtils.releaseSession(m_session,
                                               m_sessionFactory);
            s_logger.info("Saved new album: " + album.getName());
        } catch (Exception e) {
            s_logger.error("Exception copying image frame data", e);
            try {
                m_transaction.rollback();
            } catch (HibernateException e1) {
                s_logger.error("Exception rolling back transaction!", e1);
            }
        }
    }


    private static void printUsage() {
        System.out.println("Usage: MakeAlbum <user> <roll-name> "
                           + "<album-name> <keepers-file>");
    }

    public static void main(String[] args) {
        if ( args.length != 4 ) {
            printUsage();
            System.exit(1);
        }

        ClassPathXmlApplicationContext appContext =
            new ClassPathXmlApplicationContext(new String[]
                {"applicationContext-global.xml",
                 "applicationContext-user.xml",
                 "applicationContext-imagestore.xml",
                 "applicationContext-imagestore-authorization.xml",
                 "applicationContext-standalone.xml"});
        MakeAlbum worker =
            (MakeAlbum)appContext.getBean(MAKE_ALBUM_BEAN);

        worker.doStuff(args[0], args[1], args[2], args[3]);
    }
}
