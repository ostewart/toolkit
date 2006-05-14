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
import com.trailmagic.image.ImageManifestationFactory;
import java.io.File;
import java.io.FileInputStream;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;


/**
 * Replaces an ImageManifestation's image data with data from a file, updating
 * metadata as necessary.
 *
 * @author <a href="mailto:oliver@trailmagic.com">Oliver Stewart</a>
 **/
public class ReplaceImageManifestation {
    private SessionFactory m_sessionFactory;
    private ImageManifestationFactory m_imfFactory;
    private static Logger s_log =
        Logger.getLogger(ReplaceImageManifestation.class);
    private static final String REPLACEIM_BEAN = "replaceImageManifestation";

    /**
     * Gets the session factory to be used to get a session.
     * @return session factory
     **/
    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    /**
     * Sets the session factory to be used to get a session.
     * @return session factory
     **/
    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
        s_log.debug(this + "setSessionFactory called with " + sf + "("
                    + m_sessionFactory + ")" + Thread.currentThread());
    }

    public void setImageManifestationFactory(ImageManifestationFactory factory) {
        m_imfFactory = factory;
        s_log.debug("setImageManifestationFactory called on "
                    + this + " with " + m_imfFactory);
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
                SessionFactoryUtils.getSession(getSessionFactory(), false);

            HeavyImageManifestation manifest =
                m_imfFactory.getHeavyById(manifestationId.longValue());
            s_log.info("Got manifestation id " + manifest.getId());

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
            SessionFactoryUtils.releaseSession(session, getSessionFactory());

            s_log.info("Finished importing " + srcFile.getPath());
        } catch (Exception e) {
            s_log.error("Error: " + e.getMessage(), e);
            throw new RuntimeException("Error", e);
        }
    }

    private static void printUsage() {
        System.out.println("Usage: ReplaceImageManifestation "
                           + "<manifestation-id> <filename> <width> <height>");
    }


    public static final void main(String[] args) {
        if ( args.length != 4 ) {
            printUsage();
            System.exit(1);
        }
        s_log.debug("Before loading context");

        ClassPathXmlApplicationContext appContext =
            new ClassPathXmlApplicationContext(new String[]
                {"applicationContext-standalone.xml"});

        ReplaceImageManifestation worker =
            (ReplaceImageManifestation) appContext.getBean(REPLACEIM_BEAN);

        s_log.debug("got bean " + worker
                    + "with sf: " + worker.getSessionFactory()
                    + " and IMFactory: " + worker.m_imfFactory);

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
