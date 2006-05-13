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
package com.trailmagic.image.security;

import com.trailmagic.user.Owned;
import com.trailmagic.user.User;
import java.lang.reflect.InvocationTargetException;
import org.acegisecurity.acl.basic.AclObjectIdentity;
import org.acegisecurity.acl.basic.BasicAclExtendedDao;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;
import org.acegisecurity.acl.basic.SimpleAclEntry;


public class ImageSecurityFactory {
    private BasicAclExtendedDao m_aclDao;
    private static final String ROLE_EVERYONE = "ROLE_EVERYONE";

    public ImageSecurityFactory() {
        // do nothing
    }

    public BasicAclExtendedDao getBasicAclExtendedDao() {
        return m_aclDao;
    }

    public void setBasicAclExtendedDao(BasicAclExtendedDao dao) {
        m_aclDao = dao;
    }


    public void makePublic(Object obj, Object parent) {
        SimpleAclEntry entry =
            new SimpleAclEntry(ROLE_EVERYONE,
                               getIdentity(obj),
                               (parent == null ? null
                                :getIdentity(parent)),
                               SimpleAclEntry.READ);

        m_aclDao.create(entry);
    }

    public void addOwnerAcl(Owned ownedObj, Object parent) {
        User owner = ownedObj.getOwner();

        SimpleAclEntry entry =
            new SimpleAclEntry(owner.getScreenName(),
                               getIdentity(ownedObj),
                               getIdentity(parent),
                               (SimpleAclEntry.ADMINISTRATION
                                |SimpleAclEntry.READ_WRITE_CREATE_DELETE));
        m_aclDao.create(entry);
    }

    public void makePrivate(Object obj, Object parent) {
        m_aclDao.delete(getIdentity(obj), ROLE_EVERYONE);
    }

    private AclObjectIdentity getIdentity(Object obj) {
        try {
            return new NamedEntityObjectIdentity(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error creating ACL identity", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error creating ACL identity", e);
        }
    }
}