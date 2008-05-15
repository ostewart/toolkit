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
package com.trailmagic.user.hibernate;

import com.trailmagic.user.NoSuchUserException;
import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public class HibernateUserFactory implements UserFactory {
    private static final String BY_SN_QUERY_NAME = "userByScreenName";
    private static final String HASH_ALGORITHM = "MD5";

    private SessionFactory m_sessionFactory;
    private HibernateTemplate m_hibernateTemplate;

    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        m_hibernateTemplate = hibernateTemplate;
    }

    public User createUser() {
        return new User();
    }

    public User getByScreenName(String screenName) throws NoSuchUserException {
        List results = m_hibernateTemplate
            .findByNamedQueryAndNamedParam(BY_SN_QUERY_NAME,
                                           "screenName",
                                           screenName);
        if (results.size() > 0) {
            return (User) results.get(0);
        } else {
            throw new NoSuchUserException(screenName);
        }
    }

    public User getById(long userId) throws NoSuchUserException {
        try {
            return (User) m_hibernateTemplate.load(User.class, userId);
        } catch (ObjectRetrievalFailureException e) {
            throw new NoSuchUserException(userId);
        }
    }

    @Transactional(readOnly=false)
    public void save(User user) {
        m_hibernateTemplate.save(user);
    }
}
