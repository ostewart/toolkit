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

import com.trailmagic.user.Group;
import com.trailmagic.user.GroupFactory;
import com.trailmagic.user.User;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class HibernateGroupFactory implements GroupFactory {
    private HibernateTemplate m_hibernateTemplate;

    private static final String BY_NAME_QUERY = "groupByName";
    private static final String FOR_USER_QUERY = "groupsForUser";

    public void setHibernateTemplate(HibernateTemplate template) {
        m_hibernateTemplate = template;
    }

    public Group getById(final long id) {
        return
            (Group)
            m_hibernateTemplate.execute(new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        return session.get(Group.class, new Long(id));
                    }
                });
    }

    public Group getByName(final String groupName) {
        return (Group)
            m_hibernateTemplate.execute(new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        Query qry = session.getNamedQuery(BY_NAME_QUERY);
                        qry.setString("name", groupName);
                        return qry.uniqueResult();
                    }
                });
    }

    public List<Group> getForUser(final User user) {
        return (List<Group>)
            m_hibernateTemplate.execute(new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        Query qry = session.getNamedQuery(FOR_USER_QUERY);
                        qry.setEntity("user", user);
                        return qry.list();
                    }
                });
    }
}