package com.trailmagic.image.hibernate;

import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import java.util.List;

import com.trailmagic.image.*;

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
