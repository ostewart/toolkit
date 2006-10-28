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

import java.security.Principal;

public final class UserPrincipal implements Principal {
    private String m_screenName;

    public UserPrincipal(String sn) {
        m_screenName = sn;
    }

    // ** java.security.Principal methods
    public boolean equals(Object obj) {
        if ( !obj.getClass().equals(UserPrincipal.class) ) {
            return false;
        }

        return m_screenName.equals(((UserPrincipal)obj).m_screenName);
    }

    public String getName() {
        return m_screenName;
    }

    public String toString() {
        return m_screenName;
    }

    public int hashCode() {
        return m_screenName.hashCode();
    }

}
