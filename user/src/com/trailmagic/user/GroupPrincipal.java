package com.trailmagic.user;

import java.security.Principal;

public final class GroupPrincipal implements Principal {
    private String m_name;

    public GroupPrincipal(String name) {
        m_name = name;
    }

    // ** java.security.Principal methods
    public boolean equals(Object obj) {
        if ( !obj.getClass().equals(GroupPrincipal.class) ) {
            return false;
        }

        return m_name.equals(((GroupPrincipal)obj).m_name);
    }

    public String getName() {
        return m_name;
    }

    public String toString() {
        return m_name;
    }

    public int hashCode() {
        return m_name.hashCode();
    }
}
