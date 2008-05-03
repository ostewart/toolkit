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


public class User {
    private long m_id;
    private String m_screenName;
    private String m_firstName;
    private String m_lastName;
    private String m_primaryEmail;
    private String m_password;

    public User() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public String getScreenName() {
        return m_screenName;
    }

    public void setScreenName(String sn) {
        m_screenName = sn;
    }

    public String getFirstName() {
        return m_firstName;
    }

    public void setFirstName(String name) {
        m_firstName = name;
    }

    public String getLastName() {
        return m_lastName;
    }

    public void setLastName(String name) {
        m_lastName = name;
    }

    public String getPrimaryEmail() {
        return m_primaryEmail;
    }

    public void setPrimaryEmail(String email) {
        m_primaryEmail = email;
    }

    /**
     * MD5 password digest - stored in hex chars
     **/
    public String getPassword() {
        return m_password;
    }

    /**
     * MD5 password digest
     **/
    public void setPassword(String pass) {
        m_password = pass;
    }

    public boolean equals(Object obj) {
        return (obj instanceof User) &&
            (this.getScreenName().equals(((User)obj).getScreenName()));

    }
}
