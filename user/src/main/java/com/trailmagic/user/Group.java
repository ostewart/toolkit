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
package com.trailmagic.user;

import java.util.Collection;

public class Group {
    private long m_id;
    private String m_name;
    private User m_owner;
    private Collection m_users;

    public Group() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public User getOwner() {
        return m_owner;
    }

    public void setOwner(User owner) {
        m_owner = owner;
    }

    public Collection getUsers() {
        return m_users;
    }

    public void setUsers(Collection users) {
        m_users = users;
    }

    public void addUser(User user) {
        m_users.add(user);
    }

    public void removeUser(User user) {
        m_users.remove(user);
    }
}
