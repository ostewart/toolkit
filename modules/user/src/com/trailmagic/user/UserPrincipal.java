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
