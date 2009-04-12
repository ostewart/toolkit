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
import com.trailmagic.image.ImageFrame;
import org.springframework.security.acl.AclProvider;
import org.springframework.security.acl.AclManager;
import org.springframework.security.Authentication;
import org.springframework.security.acl.AclEntry;
import org.springframework.security.acl.basic.NamedEntityObjectIdentity;
import org.springframework.security.acl.basic.SimpleAclEntry;
import org.springframework.security.acl.basic.BasicAclEntry;
import org.apache.log4j.Logger;
import java.util.Collection;
import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;


public class ImageFrameImageAclProvider implements AclProvider {
    private AclManager m_aclManager;
    private static Logger s_log =
        Logger.getLogger(ImageFrameImageAclProvider.class);

    public ImageFrameImageAclProvider() {
        // do nothing
    }

    public void setAclManager(AclManager aclManager) {
        m_aclManager = aclManager;
    }

    public AclManager getAclManager() {
        return m_aclManager;
    }

    /**
     * Returns the set of ACLs that apply to the <code>Image</code>
     * referenced by this frame, translated to apply to the frame.
     * In other words, the image permissions are automatically carried
     * over to the frame.
     *
     * @param domainInstance <code>ImageFrame</code> domain object for which
     * an ACL should be returned.
     * @return <code>AclEntry</code> objects contained in an array
     **/
    public AclEntry[] getAcls(Object domainInstance) {
        ImageFrame frame = (ImageFrame) domainInstance;
        Image image = frame.getImage();
        AclEntry[] entries = m_aclManager.getAcls(image);
        Collection<AclEntry> newEntries = new ArrayList<AclEntry>();

        for (AclEntry entry : entries) {
            if (entry instanceof BasicAclEntry) {
                try {
                    newEntries.add(constructAclEntry(frame, image,
                                                     (BasicAclEntry) entry));
                } catch (IllegalAccessException e) {
                    s_log.warn("Illegal access creating object identity", e);
                } catch (InvocationTargetException e) {
                    s_log.warn("Invocation target exception creating object "
                               + "identity",
                               e);
                }
            }
        }
        return newEntries.toArray(new AclEntry[0]);
    }

    /**
     * entries must be BasicAclEntry, identities must be
     *  NamedEntityObjectIdentity

     * transfer the permissions of the image to the imageframe by
     * copying the
     **/
    public AclEntry[] getAcls(Object domainInstance,
                              Authentication authentication) {
        ImageFrame frame = (ImageFrame) domainInstance;
        Image image = frame.getImage();
        AclEntry[] entries = m_aclManager.getAcls(image, authentication);
        Collection<AclEntry> newEntries = new ArrayList<AclEntry>();

        for (AclEntry entry : entries) {
            if (entry instanceof BasicAclEntry) {
                try {
                    newEntries.add(constructAclEntry(frame, image,
                                                     (BasicAclEntry) entry));
                } catch (IllegalAccessException e) {
                    s_log.warn("Illegal access creating object identity", e);
                } catch (InvocationTargetException e) {
                    s_log.warn("Invocation target exception creating object "
                               + "identity",
                               e);
                }
            }
        }
        return newEntries.toArray(new AclEntry[0]);
    }

    private AclEntry constructAclEntry(ImageFrame frame,
                                       Image image,
                                       BasicAclEntry entry)
        throws IllegalAccessException, InvocationTargetException {

        NamedEntityObjectIdentity frameIdentity =
            new NamedEntityObjectIdentity(frame);
        NamedEntityObjectIdentity imageIdentity =
            new NamedEntityObjectIdentity(image);
        return new SimpleAclEntry(entry.getRecipient(),
                                  frameIdentity,
                                  imageIdentity,
                                  entry.getMask());
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
       boolean isSupported =
            ImageFrame.class.isAssignableFrom(domainInstance.getClass());

        if (s_log.isDebugEnabled()) {
            s_log.debug("domain instance " + domainInstance
                        + (isSupported ? " is" : " is not") + " supported");
        }
        return isSupported;
    }
}