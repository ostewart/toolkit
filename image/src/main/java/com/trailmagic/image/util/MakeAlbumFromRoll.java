/*
 * Copyright (c) 2005 Oliver Stewart.  All Rights Reserved.
 */
package com.trailmagic.image.util;

import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupFactory;
import com.trailmagic.user.UserFactory;
import java.util.Iterator;
import java.util.SortedSet;
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
 * Run like: ant run -Drun.class=com.trailmagic.image.util.MakeAlbumFromRoll -Drun.args="oliver descrie"
 **/
public class MakeAlbumFromRoll implements ApplicationContextAware {
    private Session m_session;
    private SessionFactory m_sessionFactory;
    private Transaction m_transaction;
    private ApplicationContext m_appContext;

    private static final String GROUP_FACTORY_BEAN = "imageGroupFactory";
    private static final String USER_FACTORY_BEAN = "userFactory";
    private static final String SESS_FACTORY_BEAN = "sessionFactory";
    private static final String MAFR_BEAN = "makeAlbumFromRoll";

    private static Logger s_logger = Logger.getLogger(MakeAlbumFromRoll.class);

    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        m_appContext = applicationContext;
    }

    public void doStuff(String userName, String rollName, String albumName) {
        m_session = SessionFactoryUtils.getSession(m_sessionFactory, false);
        try {
            m_transaction = m_session.beginTransaction();

            ImageGroupFactory gf =
                (ImageGroupFactory)m_appContext.getBean(GROUP_FACTORY_BEAN);
            UserFactory uf =
                (UserFactory)m_appContext.getBean(USER_FACTORY_BEAN);

            ImageGroup roll =
                gf.getRollByOwnerAndName(uf.getByScreenName(userName),
                                         rollName);

            ImageGroup album = new ImageGroup();
            album.setName(albumName);
            album.setType(ImageGroup.Type.ALBUM);
            album.setDisplayName(roll.getDisplayName());
            album.setDescription(roll.getDescription());
            album.setOwner(roll.getOwner());
            album.setSupergroup(roll.getSupergroup());
            // XXX: not copying subgroups
            // this is probably a leaf group
            m_session.save(album);

            SortedSet frames = roll.getFrames();
            Iterator iter = frames.iterator();
            int frameNum;
            ImageFrame oldFrame, newFrame;
            while (iter.hasNext()) {
                oldFrame = (ImageFrame)iter.next();
                newFrame = new ImageFrame();
                newFrame.setImage(oldFrame.getImage());
                newFrame.setPosition(oldFrame.getPosition());
                newFrame.setCaption(oldFrame.getCaption());
                newFrame.setImageGroup(album);
                m_session.save(newFrame);
                s_logger.debug("Saved frame " + newFrame.getPosition());
            }
            m_session.save(album);
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
        System.out.println("Usage: MakeAlbumFromRoll <user> <roll-name> "
                           + "<album-name>");
    }

    public static final void main(String[] args) {
        if ( args.length != 3 ) {
            printUsage();
            System.exit(1);
        }

        ClassPathXmlApplicationContext appContext =
            new ClassPathXmlApplicationContext(new String[]
                {"applicationContext-standalone.xml"});
        MakeAlbumFromRoll worker =
            (MakeAlbumFromRoll)appContext.getBean(MAFR_BEAN);

        worker.doStuff(args[0], args[1], args[2]);
    }
}
