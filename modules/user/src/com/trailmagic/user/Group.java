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
}
