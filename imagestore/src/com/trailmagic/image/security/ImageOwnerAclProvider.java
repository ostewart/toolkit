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

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupFactory;
import com.trailmagic.user.User;
import java.lang.reflect.InvocationTargetException;
import org.acegisecurity.Authentication;
import org.acegisecurity.acl.AclEntry;
import org.acegisecurity.acl.AclProvider;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;
import org.acegisecurity.acl.basic.SimpleAclEntry;
import org.apache.log4j.Logger;

/**
 * Creates an ACL with full permissions for the User owner of an Image.
 **/
public class ImageOwnerAclProvider implements AclProvider {
    private ImageGroupFactory m_imageGroupFactory;
    private static Logger s_log = Logger.getLogger(ImageOwnerAclProvider.class);

    public void setImageGroupFactory(ImageGroupFactory factory) {
        m_imageGroupFactory = factory;
    }

    public ImageGroupFactory getImageGroupFactory() {
        return m_imageGroupFactory;
    }

    /**
     * Returns a single ACL with full permissions for the owner of the
     * <code>Image</code> passed as the <code>domainInstance</code>
     *
     * @param domainInstance <code>Image</code> domain object for which
     * an ACL should be returned.
     * @return a single <code>AclEntry</code> contained in an array
     **/
    public AclEntry[] getAcls(Object domainInstance) {
        Image image = (Image) domainInstance;
        User owner = image.getOwner();
        ImageGroup roll = m_imageGroupFactory.getRollForImage(image);

        try {
            SimpleAclEntry entry =
                new SimpleAclEntry(owner.getScreenName(),
                                   new NamedEntityObjectIdentity(image),
                                   new NamedEntityObjectIdentity(roll),
                                   (SimpleAclEntry.ADMINISTRATION
                                    |SimpleAclEntry.READ_WRITE_CREATE_DELETE));

            if (s_log.isDebugEnabled()) {
                s_log.debug("Adding full permissions ACL for owner of image: "
                            + image.getName());
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
        // TODO: actually give specific results
        return getAcls(domainInstance);
    }

    /**
     * Determines whether or not a domain object instance is supported by
     * this provider
     *
     * @param domainInstance domain object instance
     * @return <code>true</code> if <code>domainInstance</code> is an
     * <code>Image</code>, <code>false</code> otherwise
     **/
    public boolean supports(Object domainInstance) {
        if (domainInstance == null) {
            return false;
        }

        return Image.class.isAssignableFrom(domainInstance.getClass());
    }
}