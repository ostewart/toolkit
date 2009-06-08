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

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String screenName;
    private String firstName;
    private String lastName;
    private String primaryEmail;
    private String password;

    public User() {
    }

    public User(String screenName) {
        this.screenName = screenName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String sn) {
        screenName = sn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        firstName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        lastName = name;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String email) {
        primaryEmail = email;
    }

    /**
     * MD5 password digest - stored in hex chars
     **/
    public String getPassword() {
        return password;
    }

    /**
     * MD5 password digest
     **/
    public void setPassword(String pass) {
        password = pass;
    }

    public boolean equals(Object obj) {
        return (obj instanceof User) &&
            (this.getScreenName().equals(((User)obj).getScreenName()));

    }

    @Override
    public int hashCode() {
        return screenName.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", screenName='" + screenName + '\'' +
               '}';
    }
}
