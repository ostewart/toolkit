package com.trailmagic.image.hibernate;

import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import java.util.List;

import com.trailmagic.user.User;
import com.trailmagic.image.*;

public class HibernateImageGroupFactory implements ImageGroupFactory {
    private static final String ALBUM_BY_OWNER_AND_NAME_QRY =
        "albumByOwnerAndName";
    private static final String IMGFRAME_BY_IMG_GROUP_AND_IMAGE_QRY =
        "imageFrameByImageGroupAndImageId";
    private static final String ALBUMS_BY_OWNER_NAME_QRY =
        "albumsByOwnerScreenName";
    private static final String ALBUM_OWNERS_QRY =
        "albumOwners";
    private static final String ROLLS_BY_OWNER_NAME_QRY =
        "rollsByOwnerScreenName";
    private static final String ROLL_OWNERS_QRY =
        "rollOwners";
    private static final String ROLL_BY_OWNER_AND_NAME_QRY =
        "rollByOwnerAndName";
    private SessionFactory m_sessionFactory;
        
    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }
        
    public ImageGroup newInstance(int type) {
        return new ImageGroup();
    }

    public ImageGroup getAlbumByOwnerAndName(User owner, String name) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ALBUM_BY_OWNER_AND_NAME_QRY);
            qry.setEntity("owner", owner);
            qry.setString("albumName", name);
            return (ImageGroup)qry.uniqueResult();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }


    public ImageGroup getRollByOwnerAndName(User owner, String name) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ROLL_BY_OWNER_AND_NAME_QRY);
            qry.setEntity("owner", owner);
            qry.setString("rollName", name);
            return (ImageGroup)qry.uniqueResult();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public ImageFrame getImageFrameByImageGroupAndImageId(ImageGroup group,
                                                          long imageId) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry =
                session.getNamedQuery(IMGFRAME_BY_IMG_GROUP_AND_IMAGE_QRY);
            qry.setEntity("imageGroup", group);
            qry.setLong("imageId", imageId);
            return (ImageFrame)qry.uniqueResult();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }
    public List getAlbumsByOwnerScreenName(String name) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ALBUMS_BY_OWNER_NAME_QRY);
            qry.setString("screenName", name);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }
    public List getAlbumOwners() {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ALBUM_OWNERS_QRY);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public List getRollsByOwnerScreenName(String name) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ROLLS_BY_OWNER_NAME_QRY);
            qry.setString("screenName", name);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }
    public List getRollOwners() {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ROLL_OWNERS_QRY);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }
}
