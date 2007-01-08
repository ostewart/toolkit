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

import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateTemplate;

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

    public User getByScreenName(String screenName) throws DataAccessException {
        return (User) m_hibernateTemplate
            .findByNamedQueryAndNamedParam(BY_SN_QUERY_NAME,
                                           "screenName",
                                           screenName).get(0);
    }

    public User getById(long id) {
        return (User) m_hibernateTemplate.get(User.class, id);
    }

    public void save(User user) {
        m_hibernateTemplate.save(user);
    }
}
