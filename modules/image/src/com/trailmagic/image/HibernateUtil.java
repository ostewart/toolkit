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
package com.trailmagic.image;

import org.hibernate.*;
import org.hibernate.cfg.*;

/**
 * Taken from the Hibernate Quickstart.  Assumed to be under GPL.
 **/
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // load the config file from this class's classloader, to
            // avoid multiple classloader issues in Tomcat
            sessionFactory = new Configuration()
                .configure(HibernateUtil.class
                           .getResource("/trailmagic-image.cfg.xml"))
                .buildSessionFactory();
        } catch (HibernateException ex) {
            throw new RuntimeException("Exception building SessionFactory: " + ex.getMessage(), ex);
        }
    }

    public static final ThreadLocal<Session> session =
        new ThreadLocal<Session>();

    public static Session currentSession() throws HibernateException {
        Session s = (Session) session.get();
        // Open a new Session, if this Thread has none yet
        if (s == null) {
            s = sessionFactory.openSession();
            session.set(s);
        }
        return s;
    }

    public static void closeSession() throws HibernateException {
        Session s = (Session) session.get();
        session.set(null);
        if (s != null)
            s.close();
    }
}
