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

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.Image;
import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.ImageManifestation;
import com.trailmagic.image.ImageManifestationRepository;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.annotation.Transactional;


/**
 * Replaces an ImageManifestation's image data with data from a file, updating
 * metadata as necessary.
 *
 * @author <a href="mailto:oliver@trailmagic.com">Oliver Stewart</a>
 **/
@Transactional
public class ReplaceImageManifestation {
    private SessionFactory sessionFactory;
    private ImageManifestationRepository imfFactory;
    private HibernateTemplate hibernateTemplate;
    private UserRepository userRepository;
    private ImageGroupRepository imageGroupRepository;

    private static Logger s_log =
        Logger.getLogger(ReplaceImageManifestation.class);
    private static final String REPLACEIM_BEAN = "replaceImageManifestation";

    /**
     * Sets the session factory to be used to get a session.
     * @return session factory
     **/
    public void setSessionFactory(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public void setImageManifestationFactory(ImageManifestationRepository factory) {
        imfFactory = factory;
        s_log.debug("setImageManifestationFactory called on "
                    + this + " with " + imfFactory);
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setUserFactory(UserRepository repository) {
        this.userRepository = repository;
    }
    public void setImageGroupRepository(ImageGroupRepository imageGroupRepository) {
        this.imageGroupRepository = imageGroupRepository;
    }

    /**
     * THIS MUST BE PUBLIC in order for interceptors to run.  GRRRR.
     **/
    public void replaceManifestation(Long manifestationId,
                                     String filename,
                                     Integer width,
                                     Integer height) {
        try {
            Session session =
                SessionFactoryUtils.getSession(sessionFactory, false);

            HeavyImageManifestation manifest =
                imfFactory.getHeavyById(manifestationId.longValue());
            //            s_log.info("Got manifestation id " + manifest.getId());

            File srcFile = new File(filename);
            s_log.info("Importing " + srcFile.getPath());
            if ( srcFile.length() > Integer.MAX_VALUE ) {
                s_log.info("File is too big...skipping "
                           + srcFile.getPath());
                throw new RuntimeException("File too big");
            }
            FileInputStream fis = new FileInputStream(srcFile);
            manifest.setData(Hibernate.createBlob(fis));
            manifest.setWidth(width.intValue());
            manifest.setHeight(height.intValue());
            session.saveOrUpdate(manifest);

            s_log.info("ImageManifestation saved: "
                       + manifest.getName()
                       + " (" + manifest.getId() + ")"
                       + "...flushing session and evicting manifestation.");

            synchronized (session) {
                session.flush();
                session.evict(manifest);
            }

            fis.close();
            SessionFactoryUtils.releaseSession(session, sessionFactory);

            s_log.info("Finished importing " + srcFile.getPath());
        } catch (Exception e) {
            s_log.error("Error: " + e.getMessage(), e);
            throw new RuntimeException("Error", e);
        }
    }

    public void replaceManifestations(final String ownerName,
                                      final String rollName,
                                      final String importDir) {
        hibernateTemplate.execute(new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    try {
                        User owner = userRepository.getByScreenName(ownerName);
                        ImageGroup roll =
                            imageGroupRepository
                            .getRollByOwnerAndName(owner, rollName);
                        s_log.info("Processing roll: " + roll.getName());
                        for (ImageFrame frame : roll.getFrames()) {
                            Image image = frame.getImage();
                            for (ImageManifestation mf
                                     : image.getManifestations()) {

                                String filename = importDir + File.separator
                                    + mf.getName();
                                File srcFile = new File(filename);
                                s_log.info("Importing " + srcFile.getPath());
                                if ( srcFile.length() > Integer.MAX_VALUE ) {
                                    s_log.info("File is too big...skipping "
                                               + srcFile.getPath());
                                    throw new RuntimeException("File too big");
                                }

                                // first read the file to get the size
                                FileInputStream fis =
                                    new FileInputStream(srcFile);
                                BufferedImage bi =
                                    ImageIO.read(fis);
                                mf.setHeight(bi.getHeight());
                                mf.setWidth(bi.getWidth());
                                s_log.info("New size is " + mf.getHeight()
                                           + "x" + mf.getWidth());
                                session.saveOrUpdate(mf);
                                fis.close();

                                fis = new FileInputStream(srcFile);
                                HeavyImageManifestation heavyMf =
                                    imfFactory.getHeavyById(mf.getId());
                                heavyMf.setData(Hibernate.createBlob(fis));
                                session.saveOrUpdate(heavyMf);

                                // open the file again to get the size :(

                                s_log.info("ImageManifestation saved: "
                                           + heavyMf.getName()
                                           + " (" + heavyMf.getId() + ")"
                                           + "...flushing session and evicting "
                                           + "manifestation.");

                                synchronized (session) {
                                    session.flush();
                                    session.evict(heavyMf);
                                }

                                // looks like fis has to be open for the flush
                                fis.close();

                            }
                        }
                    } catch (Exception e) {
                        s_log.error("Error replacing manifestations ", e);
                    }
                    return null;
                }
            });
    }

    private static void printUsage() {
        System.out.println("Usage: ReplaceImageManifestation "
                           + "<manifestation-id> <filename> <width> <height>");
    }


    public static final void main(String[] args) {
        if ( args.length != 4 && args.length != 3) {
            printUsage();
            System.exit(1);
        }
        s_log.debug("Before loading context");

        ClassPathXmlApplicationContext appContext =
            new ClassPathXmlApplicationContext(new String[]
                {"applicationContext-global.xml",
                 "applicationContext-user.xml",
                 "applicationContext-imagestore.xml",
                 "applicationContext-imagestore-authorization.xml",
                 "applicationContext-standalone.xml"});

        ReplaceImageManifestation worker =
            (ReplaceImageManifestation) appContext.getBean(REPLACEIM_BEAN);

        s_log.debug("got bean " + worker
                    + "with sf: " + worker.sessionFactory
                    + " and IMFactory: " + worker.imfFactory);

        if (args.length == 3) {
            worker.replaceManifestations(args[0], args[1], args[2]);
            System.exit(0);
        }

        try {
            Long manifestId = new Long(args[0]);
            Integer width = new Integer(args[2]);
            Integer height = new Integer(args[3]);

        System.out.println("Updating manifestation id " + manifestId
                           + " from file " + args[1] + " with size "
                           + width + "x" + height);

        worker.replaceManifestation(manifestId,
                                    args[1],
                                    width,
                                    height);
        } catch (NumberFormatException e) {
            printUsage();
            System.exit(1);
        }
    }
}
