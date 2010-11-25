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
import com.trailmagic.user.UserRepository;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public class HibernateUserRepository implements UserRepository {
    private static final String BY_SN_QUERY_NAME = "userByScreenName";

    private HibernateTemplate hibernateTemplate;

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public User createUser() {
        return new User();
    }

    public User getByScreenName(String screenName) throws NoSuchUserException {
        List results = hibernateTemplate.findByNamedQueryAndNamedParam(BY_SN_QUERY_NAME, "screenName", screenName);
        if (results.size() > 0) {
            return (User) results.get(0);
        } else {
            throw new NoSuchUserException(screenName);
        }
    }

    public User getById(long userId) throws NoSuchUserException {
        try {
            return hibernateTemplate.load(User.class, userId);
        } catch (ObjectRetrievalFailureException e) {
            throw new NoSuchUserException(userId);
        }
    }

    @Transactional(readOnly = false)
    public void save(User user) {
        hibernateTemplate.save(user);
    }
}
