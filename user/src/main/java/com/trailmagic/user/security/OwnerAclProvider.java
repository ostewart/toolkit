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
import com.trailmagic.user.Owned;
import java.lang.reflect.InvocationTargetException;
import org.springframework.security.Authentication;
import org.springframework.security.acl.AclEntry;
import org.springframework.security.acl.AclProvider;
import org.springframework.security.acl.basic.NamedEntityObjectIdentity;
import org.springframework.security.acl.basic.SimpleAclEntry;
import org.apache.log4j.Logger;

/**
 * Creates an ACL with full permissions for the User owner of an
 * Owned object.
 **/
public abstract class OwnerAclProvider implements AclProvider {
    private static Logger s_log = Logger.getLogger(OwnerAclProvider.class);

    /**
     * Returns a single ACL with full permissions for the owner of the
     * <code>Owned</code> passed as the <code>domainInstance</code>
     *
     * @param domainInstance <code>Owned</code> domain object for which
     * an ACL should be returned.
     * @return a single <code>AclEntry</code> contained in an array
     **/
    public AclEntry[] getAcls(Object domainInstance) {
        Owned ownedObj = (Owned) domainInstance;
        User owner = ownedObj.getOwner();
        Object parent = getParent(ownedObj);

        try {
            SimpleAclEntry entry =
                new SimpleAclEntry(owner.getScreenName(),
                                   new NamedEntityObjectIdentity(ownedObj),
                                   new NamedEntityObjectIdentity(parent),
                                   (SimpleAclEntry.ADMINISTRATION
                                    |SimpleAclEntry.READ_WRITE_CREATE_DELETE));

            if (s_log.isDebugEnabled()) {
                s_log.debug("Adding full permissions ACL for owner of: "
                            + ownedObj);
            }
            return new AclEntry[] {entry};
        } catch (IllegalAccessException e) {
            s_log.warn("Illegal access creating object identity", e);
            return new AclEntry[] {};
        } catch (InvocationTargetException e) {
            s_log.warn("Invocation target exception creating object identity",
                       e);
            return new AclEntry[] {};
        }
    }

    public AclEntry[] getAcls(Object domainInstance,
                              Authentication authentication) {
        Owned ownedObj = (Owned) domainInstance;
        User owner = ownedObj.getOwner();
        if (owner.getScreenName().equals(authentication.getName())) {
            return getAcls(domainInstance);
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Owner doesn't match authentication.  "
                            + "Returning no ACLS");
            }
            return new AclEntry[] {};
        }
    }

    abstract protected Object getParent(Owned ownedObj);
}