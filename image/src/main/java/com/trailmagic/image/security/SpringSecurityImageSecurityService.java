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
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.ImageManifestation;
import com.trailmagic.user.Owned;
import com.trailmagic.user.User;
import org.apache.log4j.Logger;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.acls.AccessControlEntry;
import org.springframework.security.acls.Acl;
import org.springframework.security.acls.MutableAcl;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.NotFoundException;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.objectidentity.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.sid.GrantedAuthoritySid;
import org.springframework.security.acls.sid.PrincipalSid;
import org.springframework.security.acls.sid.Sid;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

@Transactional
public class SpringSecurityImageSecurityService implements ImageSecurityService {
    private MutableAclService aclService;
    private ImageGroupRepository imageGroupRepository;
    private ObjectIdentityRetrievalStrategy identityRetrievalStrategy;
    private static Logger log = Logger.getLogger(SpringSecurityImageSecurityService.class);
    private static final Set<Permission> OWNER_PERMISSIONS = new HashSet<Permission>();
    private static final String ROLE_EVERYONE = "ROLE_EVERYONE";
    private static final GrantedAuthoritySid PUBLIC_SID = new GrantedAuthoritySid(ROLE_EVERYONE);
    private static final Sid[] PUBLIC_SID_ARY = new Sid[]{PUBLIC_SID};

    static {
        OWNER_PERMISSIONS.add(BasePermission.READ);
        OWNER_PERMISSIONS.add(BasePermission.WRITE);
        OWNER_PERMISSIONS.add(BasePermission.CREATE);
        OWNER_PERMISSIONS.add(BasePermission.DELETE);
        OWNER_PERMISSIONS.add(BasePermission.ADMINISTRATION);
    }

    public SpringSecurityImageSecurityService(MutableAclService aclService, ImageGroupRepository imageGroupRepository,
                                              ObjectIdentityRetrievalStrategy identityRetrievalStrategy) {
        this.aclService = aclService;
        this.imageGroupRepository = imageGroupRepository;
        this.identityRetrievalStrategy = identityRetrievalStrategy;
    }

    public void makeFramesPublic(ImageGroup group) {
        log.info("Making all images in " + group.getType() + " public: " + group);

        for (ImageFrame frame : group.getFrames()) {
            log.info("Making image public: " + frame.getImage());
            makePublic(frame.getImage());
        }
    }

    public void makeFramesPrivate(ImageGroup group) {
        log.info("Making all images in " + group.getType() + " private: " + group);
        for (ImageFrame frame : group.getFrames()) {
            log.info("Making image private: " + frame.getImage());
            makePrivate(frame.getImage());
        }
    }

    public void makePublic(Object obj) {
        addReadPermission(obj, ROLE_EVERYONE);
        log.info("Added access to " + obj + " by ROLE_EVERYONE");

        if (obj instanceof Image) {
            Image image = (Image) obj;
            // for now also make the manifestations public
            // this will mess things up if we set e.g. the original
            // to be permissioned differently
            SortedSet<ImageManifestation> manifestations = image.getManifestations();
            for (ImageManifestation mf : manifestations) {
                makePublic(mf);
            }
        }
    }

    public void addOwnerAcl(Image image) {
        addOwnerAclInternal(image, imageGroupRepository.getRollForImage(image));
    }

    public void addOwnerAcl(ImageFrame frame) {
        // image is always parent of frame so that we never
        // have access to a frame without having access to the image
        addOwnerAclInternal(frame, frame.getImage());
    }

    public void addOwnerAcl(ImageGroup group) {
        addOwnerAclInternal(group, null);
    }

    public void addOwnerAcl(ImageManifestation mf) {
        addOwnerAclInternal(mf, mf.getImage());
    }

    private void addOwnerAclInternal(Owned ownedObj, Object parent) {
        final User owner = ownedObj.getOwner();
        final ObjectIdentity identity = identityRetrievalStrategy.getObjectIdentity(ownedObj);
        final MutableAcl acl = aclService.createAcl(identity);
        final PrincipalSid principalSid = new PrincipalSid(owner.getScreenName());
        acl.setOwner(principalSid);

        if (parent != null) {
            final ObjectIdentity parentIdentity = identityRetrievalStrategy.getObjectIdentity(parent);
            if (parentIdentity != null) {
                try {
                    final Acl parentAcl = aclService.readAclById(parentIdentity, new Sid[]{principalSid});
                    acl.setParent(parentAcl);
                } catch (NotFoundException e) {
                    // don't care
                }
            }
        }
        effectPermissions(acl, principalSid, OWNER_PERMISSIONS, false);
    }

    public boolean isPublic(Object obj) {
        log.debug("isPublic called");

        final Acl acl = aclService.readAclById(identityRetrievalStrategy.getObjectIdentity(obj), PUBLIC_SID_ARY);
        return acl != null && acl.isGranted(new Permission[]{BasePermission.READ}, PUBLIC_SID_ARY, false);
    }

    public void addReadPermission(Object obj, String recipientRole) {
        addPermission(obj, recipientRole, BasePermission.READ);
    }

    public void addReadPermission(Object obj, User recipient) {
        addPermission(obj, recipient, BasePermission.READ);
    }

    /**
     * Adds permission to the image and the frames it's in.
     */
    public void addPermission(Image image, User recipient, Permission permission) {
        addPermission(image, recipient, permission, false);

        // add read permission for all the frames if mask includes READ
        // ADMIN should be transitive from the group
        if (isSet(BasePermission.READ, permission)) {
            for (ImageFrame frame : imageGroupRepository.getFramesContainingImage(image)) {
                addPermission(frame, recipient, BasePermission.READ);
            }
        }
    }

    public void addPermission(Object obj, String recipientRole, Permission permission) {
        final Sid sid = new GrantedAuthoritySid(recipientRole);
        effectPermission(findAcl(obj, sid), sid, permission, false);
    }

    public void addPermissions(Object obj, User recipient, Set<Permission> permissions) {
        final Sid sid = new PrincipalSid(recipient.getScreenName());
        effectPermissions(findAcl(obj, sid), sid, permissions, false);
    }

    public void addPermissions(Object obj, String recipientRole, Set<Permission> permissions) {
        final Sid sid = new GrantedAuthoritySid(recipientRole);
        effectPermissions(findAcl(obj, sid), sid, permissions, false);
    }

    private boolean isSet(Permission targetPermission, Permission permission) {
        return (targetPermission.getMask() & permission.getMask()) == BasePermission.READ.getMask();
    }

    private void addPermission(Object obj, User recipient, Permission permission) {
        addPermission(obj, recipient, permission, true);
    }

    private void addPermission(Object obj, User recipient, Permission permission, boolean additive) {
        final PrincipalSid principalSid = new PrincipalSid(recipient.getScreenName());
        effectPermission(findAcl(obj, principalSid), principalSid, permission, additive);
    }

    public void setPermission(Object obj, User recipient, Permission permission) {
        addPermission(obj, recipient, permission, false);
    }

    public void setPermission(Object obj, String recipientRole, Permission permission) {
        final GrantedAuthoritySid sid = new GrantedAuthoritySid(recipientRole);
        effectPermission(findAcl(obj, sid), sid, permission, false);
    }

    private void effectPermission(MutableAcl acl, Sid recipient, Permission permission, boolean additive) {
        effectPermissions(acl, recipient, Collections.singleton(permission), additive);
    }

    public void effectPermissions(MutableAcl acl, Sid recipient, Set<Permission> newPermissions, boolean additive) {
        Set<Permission> existingPermissions = findExistingPermissions(acl, recipient);

        if (!additive) {
            Set<Permission> permsToRemove = new HashSet<Permission>();
            permsToRemove.addAll(existingPermissions);
            permsToRemove.retainAll(newPermissions);
            for (Permission perm : permsToRemove) {
                acl.deleteAce(indexOf(recipient, perm, acl));
                if (log.isDebugEnabled()) {
                    log.debug("Removed ACE for permission " + perm + ", recipient " + recipient + ", on object " + acl.getObjectIdentity());
                }

            }
        }

        Set<Permission> permsToAdd = new HashSet<Permission>();
        permsToAdd.addAll(newPermissions);
        permsToAdd.removeAll(existingPermissions);
        for (Permission perm : permsToAdd) {
            acl.insertAce(acl.getEntries().length, perm, recipient, true);
            if (log.isDebugEnabled()) {
                log.debug("Added ACE for permission " + perm + ", recipient " + recipient + ", on object " + acl.getObjectIdentity());
            }

        }
        aclService.updateAcl(acl);
    }

    private Set<Permission> findExistingPermissions(MutableAcl acl, Sid recipient) {
        Set<Permission> existingPermissions = new HashSet<Permission>();
        for (AccessControlEntry entry : acl.getEntries()) {
            if (entry.getSid().equals(recipient)) {
                existingPermissions.add(entry.getPermission());
            }
        }
        return existingPermissions;
    }

    private int indexOf(Sid recipient, Permission permission, MutableAcl acl) {
        final AccessControlEntry[] entries = acl.getEntries();
        for (int i = 0; i < entries.length; i++) {
            final AccessControlEntry entry = entries[i];
            if (entry.getSid().equals(recipient) && permission.equals(entry.getPermission())) {
                return i;
            }
        }
        return -1;
    }

    private MutableAcl findAcl(Object obj, Sid sid) {
        final ObjectIdentity identity = identityRetrievalStrategy.getObjectIdentity(obj);
        return (MutableAcl) aclService.readAclById(identity, new Sid[]{sid});
    }

    public void makePrivate(Image image) {
        makePrivate((Object) image);

        // also try to make all the frames private
        List<ImageFrame> frames = imageGroupRepository.getFramesContainingImage(image);
        for (ImageFrame frame : frames) {
            try {
                makePrivate(frame);
            } catch (AccessDeniedException e) {
                log.warn("No admin access to frame " + frame + " for image: " + image);
            }
        }

        // for now also make the manifestations private
        SortedSet<ImageManifestation> manifestations = image.getManifestations();
        for (ImageManifestation mf : manifestations) {
            makePrivate(mf);
        }
    }

    public void makePrivate(Object obj) {
        // TODO: perhaps this should be a blocking permission instead
        final GrantedAuthoritySid sid = new GrantedAuthoritySid(ROLE_EVERYONE);
        effectPermissions(findAcl(obj, sid), sid, Collections.<Permission>emptySet(), false);
        log.info("Removed access to " + obj + " by ROLE_EVERYONE");
    }

}
