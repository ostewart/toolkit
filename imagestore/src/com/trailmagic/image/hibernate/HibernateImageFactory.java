package com.trailmagic.image.hibernate;

import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import java.util.List;

import com.trailmagic.image.*;

public class HibernateImageFactory implements ImageFactory {
    private static final String ALL_IMAGES_QUERY_NAME = "allImages";
    private static final String IMAGES_BY_NAME_QUERY_NAME = "imagesByName";
    private static final String IMAGES_BY_NAME_GROUP_QUERY_NAME =
        "imagesByNameAndGroup";

    private SessionFactory m_sessionFactory;
        
    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }
        
    public Image newInstance() {
        return new Image();
    }

    public Image getById(long id) {
        try {
            // XXX: should we allow creation here?
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);

            return (Image)session.get(Image.class, new Long(id));
            
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
        
    }

    public List getAll() {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query query = session.getNamedQuery(ALL_IMAGES_QUERY_NAME);
            return query.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public List getByName(String name) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query query = session.getNamedQuery(IMAGES_BY_NAME_QUERY_NAME);
            query.setString("name", name);
            return query.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        } 
    }

    public List getByNameAndGroup(String name, ImageGroup group) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query query =
                session.getNamedQuery(IMAGES_BY_NAME_GROUP_QUERY_NAME);
            query.setString("name", name);
            query.setEntity("group", group);
            return query.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        } 
    }
}
