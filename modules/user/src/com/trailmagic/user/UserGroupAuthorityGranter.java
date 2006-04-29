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
import java.util.HashSet;
import java.util.Set;
import org.acegisecurity.providers.jaas.AuthorityGranter;
import org.apache.log4j.Logger;

/**
 * Grants authorities based on the User's roles and the roles of the groups
 * to which the User belongs.
 **/
public class UserGroupAuthorityGranter implements AuthorityGranter {
    private static Logger s_log =
        Logger.getLogger(UserGroupAuthorityGranter.class);

    /**
     * The grant method is called for each principal returned from the
     * LoginContext subject. If the AuthorityGranter wishes to grant any
     * authorities, it should return a java.util.Set containing the role names
     * it wishes to grant, such as ROLE_USER. If the AuthrityGranter does not
     * wish to grant any authorities it should return null. <br>
     * The set may contain any object as all objects in the returned set will be
     * passed to the JaasGrantedAuthority constructor using toString().
     *
     * @param principal One of the principals from the
     *        LoginContext.getSubect().getPrincipals() method.
     *
     * @return A java.util.Set of role names to grant, or null meaning no
     *         roles should be granted for the principal.
     */
    @SuppressWarnings("unchecked")
    public Set grant(Principal principal) {
        s_log.debug("Processing principal: " + principal);
        // unimplemented
        HashSet principals = new HashSet();
        if (principal instanceof UserPrincipal) {
            principals.add(principal);
            principals.add("ROLE_USER");
            s_log.debug("added " + principal + " and ROLE_USER");
        } else if (principal instanceof GroupPrincipal) {
            String roleName = "ROLE_" + principal.getName().toUpperCase();
            principals.add(roleName);
            s_log.debug("added " + roleName);
        }
        return principals;
    }
}
