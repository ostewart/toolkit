package com.trailmagic.user.hibernate;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate.SessionFactoryUtils;

import com.trailmagic.user.*;

public class HibernateUserFactory implements UserFactory {
    private static final String BY_SN_QUERY_NAME = "userByScreenName";
    
    private SessionFactory m_sessionFactory;
        
    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }
        


    public User newInstance() {
        return new User();
    }

    public User getByScreenName(String screenName) throws DataAccessException {
        try {
            // XXX: should we allow creation here?
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query query = session.getNamedQuery(BY_SN_QUERY_NAME);
                /*
                session.createQuery("select from com.trailmagic.user.User " +
                                    "as user " +
                                    "where user.screenName = :screenName");
                */
            query.setString("screenName", screenName);
            
            return (User)query.uniqueResult();
            
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public User getById(long id) {
        try {
            // XXX: should we allow creation here?
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);

            return (User)session.get(User.class, new Long(id));
            
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }
}