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
package com.trailmagic.user.security;

import com.trailmagic.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ToolkitUserDetails implements UserDetails {
    private User user;
    private Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

    public ToolkitUserDetails(User realUser) {
        this(realUser, Collections.<GrantedAuthority>emptyList());
    }

    public ToolkitUserDetails(User realUser, Collection<GrantedAuthority> grantedAuthorities) {
        this.user = realUser;
        this.grantedAuthorities.addAll(grantedAuthorities);
    }

    /**
     * Returns the toolkit <code>User</code> object.
     *
     * @return <code>User</code> object backing this <code>UserDetails</code>.
     */
    public User getRealUser() {
        return this.user;
    }

    /**
     * Indicates whether the user's account has expired. An expired account
     * cannot be authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie
     *         non-expired), <code>false</code> if no longer valid (ie
     *         expired)
     */
    public boolean isAccountNonExpired() {
        // TODO: add to User object
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot
     * be authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code>
     *         otherwise
     */
    public boolean isAccountNonLocked() {
        // TODO: add to User object
        return true;
    }

    /**
     * Returns the authorities granted to the user. Cannot return
     * <code>null</code>.
     *
     * @return the authorities (never <code>null</code>)
     */
    public Collection<GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie
     *         non-expired), <code>false</code> if no longer valid (ie
     *         expired)
     */
    public boolean isCredentialsNonExpired() {
        // TODO: add to User object
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user
     * cannot be authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code>
     *         otherwise
     */
    public boolean isEnabled() {
        // TODO: add to User object
        return true;
    }

    /**
     * Returns the password used to authenticate the user. Cannot return
     * <code>null</code>.
     *
     * @return the password (never <code>null</code>)
     */
    public String getPassword() {
        return this.user.getPassword();
    }

    /**
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     *
     * @return the username (never <code>null</code>)
     */
    public String getUsername() {
        return this.user.getScreenName();
    }

    public String toString() {
        return "ToolkitUserDetails {username: " + getUsername() + "}";
    }
}
