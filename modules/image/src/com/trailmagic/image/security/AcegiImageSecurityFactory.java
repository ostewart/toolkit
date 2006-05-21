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
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupFactory;
import com.trailmagic.image.ImageManifestation;
import com.trailmagic.user.Owned;
import com.trailmagic.user.User;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.acl.AclEntry;
import org.acegisecurity.acl.AclManager;
import org.acegisecurity.acl.basic.AclObjectIdentity;
import org.acegisecurity.acl.basic.AclObjectIdentityAware;
import org.acegisecurity.acl.basic.BasicAclEntry;
import org.acegisecurity.acl.basic.BasicAclExtendedDao;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;
import org.acegisecurity.acl.basic.SimpleAclEntry;
import org.apache.log4j.Logger;


public class AcegiImageSecurityFactory implements ImageSecurityFactory {
    private BasicAclExtendedDao m_aclDao;
    private ImageGroupFactory m_imageGroupFactory;
    private AclManager m_aclManager;
    private static Logger s_log = Logger.getLogger(AcegiImageSecurityFactory.class);
    private static final String ROLE_EVERYONE = "ROLE_EVERYONE";
    private static final int OWNER_ACL_MASK =
        (SimpleAclEntry.ADMINISTRATION
         |SimpleAclEntry.READ_WRITE_CREATE_DELETE);

    public AcegiImageSecurityFactory() {
        // do nothing
    }

    public BasicAclExtendedDao getBasicAclExtendedDao() {
        return m_aclDao;
    }

    public void setBasicAclExtendedDao(BasicAclExtendedDao dao) {
        m_aclDao = dao;
    }

    public void setImageGroupFactory(ImageGroupFactory factory) {
        m_imageGroupFactory = factory;
    }

    public void setAclManager(AclManager aclManager) {
        m_aclManager = aclManager;
    }

    public void makePublic(Image image) {
        ImageGroup roll = m_imageGroupFactory.getRollForImage(image);
        makePublic(image, roll);

        // also try to make all the frames public
        List<ImageFrame> frames =
            m_imageGroupFactory.getFramesContainingImage(image);
        for (ImageFrame frame : frames) {
            try {
                makePublic(frame);
            } catch (AccessDeniedException e) {
                s_log.warn("No admin access to frame " + frame
                           + " for image: " + image);
            }
        }
        /*
        try {
            // also try to make the roll frame public
            makePublic(m_imageGroupFactory
                       .getImageFrameByImageGroupAndImageId(roll,
                                                            image.getId()));
        } catch (AccessDeniedException e) {
            s_log.warn("No access to roll frame for image: " + image);
        }
        */
    }

    public void makePublic(ImageFrame frame) {
        // image is always parent of frame so that we never have
        // access to a frame without having access to the image
        makePublic(frame, frame.getImage());
    }

    public void makePublic(ImageGroup group) {
        makePublic(group, null);
    }

    public void makePublic(ImageManifestation mf) {
        makePublic(mf, mf.getImage());
    }

    private void makePublic(Object obj, Object parent) {
        /*
        SimpleAclEntry entry =
            new SimpleAclEntry(ROLE_EVERYONE,
                               getIdentity(obj),
                               (parent == null ? null
                                :getIdentity(parent)),
                               SimpleAclEntry.READ);

        m_aclDao.create(entry);
        */
        addReadPermission(obj, parent, ROLE_EVERYONE);
        s_log.info("Added access to " + obj + " by ROLE_EVERYONE");
    }

    public void addOwnerAcl(Image image) {
        addOwnerAcl(image, m_imageGroupFactory.getRollForImage(image));
    }

    public void addOwnerAcl(ImageFrame frame) {
        // image is always parent of frame so that we never
        // have access to a frame without having access to the image
        addOwnerAcl(frame, frame.getImage());
    }

    public void addOwnerAcl(ImageGroup group) {
        addOwnerAcl(group, null);
    }

    public void addOwnerAcl(ImageManifestation mf) {
        addOwnerAcl(mf, mf.getImage());
    }

    private void addOwnerAcl(Owned ownedObj, Object parent) {
        User owner = ownedObj.getOwner();
        addPermission(ownedObj, parent, owner.getScreenName(), OWNER_ACL_MASK);
    }

    public boolean isPublic(Object obj) {
        s_log.debug("isPublic called");
        AclEntry[] entries = m_aclManager.getAcls(obj);
        for (AclEntry entry : entries) {
            if (!(entry instanceof BasicAclEntry)) {
                s_log.debug("skipping entry: " + entry);
                continue;
            }

            BasicAclEntry basicEntry = (BasicAclEntry) entry;
            if (ROLE_EVERYONE.equals(basicEntry.getRecipient())) {
                boolean result = ((basicEntry.getMask() & SimpleAclEntry.READ)
                                  == SimpleAclEntry.READ);
                s_log.debug(basicEntry + " mask is " + basicEntry.getMask()
                            + "evaluates to: " + result);
                return result;
            }
        }
        return false;
    }

    public void addReadPermission(Object obj, Object parent,
                                  Object recipient) {
        addPermission(obj, parent, recipient, SimpleAclEntry.READ);
    }

    /**
     * Adds permission to the image and the frames it's in.
     **/
    public void addPermission(Image image, Object recipient, int mask) {
        ImageGroup parent = m_imageGroupFactory.getRollForImage(image);

        addPermission(image, parent, recipient, mask);

        // add read permission for all the frames if mask includes READ
        // ADMIN should be transitive from the group
        if ((SimpleAclEntry.READ & mask) == SimpleAclEntry.READ) {
            for (ImageFrame frame
                     : m_imageGroupFactory.getFramesContainingImage(image)) {
                addPermission(frame, image, recipient, SimpleAclEntry.READ);
            }
        }
    }

    /**
     * recipient should be a string, not a user
     **/
    public void addPermission(Object obj, Object parent, Object recipient,
                              int mask) {
        AclObjectIdentity objIdentity = getIdentity(obj);
        AclObjectIdentity parentIdentity = getIdentity(parent);

        BasicAclEntry existingEntry =
            findExistingEntry(objIdentity, recipient);
        if (existingEntry != null) {
            int oldPerm = existingEntry.getMask();
            m_aclDao.changeMask(objIdentity, recipient,
                                oldPerm | mask);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Added permission " + mask + " on "
                           + objIdentity + " for recipient: "
                           + recipient);
            }
        } else {
            createPermission(objIdentity, parentIdentity, recipient, mask);

        }
    }

    public void setPermission(Object obj, Object parent,
                               Object recipient,
                               int mask) {
        AclObjectIdentity objIdentity = getIdentity(obj);
        AclObjectIdentity parentIdentity = getIdentity(parent);

        BasicAclEntry existingEntry =
            findExistingEntry(objIdentity, recipient);
        if (existingEntry != null) {
            int oldPerm = existingEntry.getMask();
            m_aclDao.changeMask(objIdentity, recipient,
                                mask);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Set permission " + mask + " on "
                           + objIdentity + " for recipient: "
                           + recipient);
            }
        } else {
            createPermission(objIdentity, parentIdentity, recipient, mask);

        }
    }

    private BasicAclEntry findExistingEntry(AclObjectIdentity objIdentity,
                                            Object recipient) {
        BasicAclEntry[] entries = m_aclDao.getAcls(objIdentity);
        BasicAclEntry existingEntry = null;

        // search for existing entries
        if (entries != null) {
            for (BasicAclEntry entry : entries) {
                if (entry.getRecipient().equals(recipient)) {
                    existingEntry = entry;
                    break;
                }
            }
        }
        return existingEntry;
    }

    private void createPermission(AclObjectIdentity objIdentity,
                               AclObjectIdentity parentIdentity,
                               Object recipient,
                               int mask) {
            SimpleAclEntry entry =
                new SimpleAclEntry(recipient,
                                   objIdentity,
                                   parentIdentity,
                                   mask);
            m_aclDao.create(entry);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Created permission " + mask + " on "
                            + objIdentity + " for recipient: "
                            + recipient);
            }
    }

    public void makePrivate(Image image) {
        ImageGroup roll = m_imageGroupFactory.getRollForImage(image);
        makePrivate(image, roll);

        // also try to make all the frames private
        List<ImageFrame> frames =
            m_imageGroupFactory.getFramesContainingImage(image);
        for (ImageFrame frame : frames) {
            try {
                makePrivate(frame);
            } catch (AccessDeniedException e) {
                s_log.warn("No admin access to frame " + frame
                           + " for image: " + image);
            }
        }
    }

    public void makePrivate(ImageFrame frame) {
        makePrivate(frame, frame.getImage());
    }

    public void makePrivate(ImageGroup group) {
        makePrivate(group, null);
    }

    public void makePrivate(ImageManifestation mf) {
        makePrivate(mf, mf.getImage());
    }

    public void makePrivate(Object obj, Object parent) {
        // instead of deleting the acl entry, set it to nothing so
        // we're sure not to inherit any positive permissions
        setPermission(obj, parent, ROLE_EVERYONE, SimpleAclEntry.NOTHING);
        s_log.info("Removed access to " + obj + " by ROLE_EVERYONE");
    }

    private AclObjectIdentity getIdentity(Object obj) {
        try {
            if (obj == null) {
                return null;
            } else if (obj instanceof AclObjectIdentityAware) {
                return ((AclObjectIdentityAware) obj).getAclObjectIdentity();
            } else {
                return new NamedEntityObjectIdentity(obj);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error creating ACL identity", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error creating ACL identity", e);
        }
    }
}