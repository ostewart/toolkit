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
import com.trailmagic.user.Owned;
import com.trailmagic.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
public class SpringSecurityImageSecurityService implements ImageSecurityService {
    private MutableAclService aclService;
    private ImageGroupRepository imageGroupRepository;
    private ObjectIdentityRetrievalStrategy identityRetrievalStrategy;
    private static Logger log = LoggerFactory.getLogger(SpringSecurityImageSecurityService.class);
    private static final Set<Permission> OWNER_PERMISSIONS = new HashSet<Permission>();
    private static final String ROLE_EVERYONE = "ROLE_EVERYONE";
    private static final GrantedAuthoritySid PUBLIC_SID = new GrantedAuthoritySid(ROLE_EVERYONE);

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

    public void makeImagesPublic(ImageGroup group) {
        log.info("Making all images in " + group.getType() + " public: " + group);

        for (ImageFrame frame : group.getFrames()) {
            log.info("Making image public: " + frame.getImage());
            makePublic(frame.getImage());
        }
    }

    public void makeImagesPrivate(ImageGroup group) {
        log.info("Making all images in " + group.getType() + " private: " + group);
        for (ImageFrame frame : group.getFrames()) {
            log.info("Making image private: " + frame.getImage());
            makePrivate(frame.getImage());
        }
    }

    @Secured("ACL_OBJECT_ADMIN")
    public void makePublic(AccessControlled obj) {
        addReadPermission(obj, ROLE_EVERYONE);
        log.info("Added access to " + obj + " by ROLE_EVERYONE");
    }

    @Secured("ROLE_USER")
    public void addOwnerAcl(Image image) {
        addOwnerAclInternal(image, imageGroupRepository.getRollForImage(image));
    }

    @Secured("ROLE_USER")
    public void addOwnerAcl(ImageGroup group) {
        addOwnerAclInternal(group, null);
    }

    private void addOwnerAclInternal(Owned ownedObj, Object parent) {
        final User owner = ownedObj.getOwner();
        final ObjectIdentity identity = identityRetrievalStrategy.getObjectIdentity(ownedObj);
        final MutableAcl acl = aclService.createAcl(identity);
        final Sid ownerSid = sidForUser(owner);
        acl.setOwner(ownerSid);
        aclService.updateAcl(acl);

        if (parent != null) {
            final ObjectIdentity parentIdentity = identityRetrievalStrategy.getObjectIdentity(parent);
            if (parentIdentity != null) {
                try {
                    final Acl parentAcl = aclService.readAclById(parentIdentity, Arrays.asList(ownerSid));
                    acl.setParent(parentAcl);
                } catch (NotFoundException e) {
                    // don't care
                }
            }
        }
        effectPermissions(acl, ownerSid, OWNER_PERMISSIONS, false);
    }

    @Transactional(readOnly = true)
    public boolean isPublic(Object obj) {
        return isGranted(obj, PUBLIC_SID, BasePermission.READ);
    }

    @Transactional(readOnly = true)
    public boolean isReadableByUser(Object target, User recipient) {
        return isGranted(target, recipient, BasePermission.READ);
    }

    @Transactional(readOnly = true)
    public boolean isAvailableToUser(Object target, User recipient, Permission permission) {
        return isGranted(target, recipient, permission);
    }

    @Transactional(readOnly = true)
    public boolean isReadableByRole(Object target, String role) {
        return isGranted(target, role, BasePermission.READ);
    }

    @Transactional(readOnly = true)
    public boolean isAvailableToRole(Object target, String role, Permission permission) {
        return isGranted(target, role, permission);
    }

    private boolean isGranted(Object target, User recipient, Permission permission) {
        return isGranted(target, sidForUser(recipient), permission);
    }

    private boolean isGranted(Object target, String recipientRole, Permission permission) {
        return isGranted(target, sidForRole(recipientRole), permission);
    }

    private boolean isGranted(Object target, Sid recipient, Permission permission) {
        List<Sid> sids = Arrays.asList(recipient);
        try {
            final Acl acl = aclService.readAclById(identityRetrievalStrategy.getObjectIdentity(target), sids);
            return acl != null && acl.isGranted(Arrays.asList(permission), sids, false);
        } catch (NotFoundException e) {
            return false;
        }
    }

    @Secured("ACL_OBJECT_ADMIN")
    public void addReadPermission(Object obj, String recipientRole) {
        addPermission(obj, recipientRole, BasePermission.READ);
    }

    @Secured("ACL_OBJECT_ADMIN")    
    public void addReadPermission(Object obj, User recipient) {
        addPermission(obj, recipient, BasePermission.READ);
    }

    public void addPermission(Object obj, String recipientRole, Permission permission) {
        final Sid sid = sidForRole(recipientRole);
        effectPermission(findAcl(obj, sid), sid, permission, false);
    }

    private Sid sidForRole(String recipientRole) {
        return new GrantedAuthoritySid(recipientRole);
    }

    public void addPermissions(Object obj, User recipient, Set<Permission> permissions) {
        final Sid sid = sidForUser(recipient);
        effectPermissions(findAcl(obj, sid), sid, permissions, false);
    }

    private Sid sidForUser(User recipient) {
        return new PrincipalSid(recipient.getScreenName());
    }

    private void addPermission(Object obj, User recipient, Permission permission) {
        addPermission(obj, recipient, permission, true);
    }

    private void addPermission(Object obj, User recipient, Permission permission, boolean additive) {
        final Sid principalSid = sidForUser(recipient);
        effectPermission(findAcl(obj, principalSid), principalSid, permission, additive);
    }

    private void effectPermission(MutableAcl acl, Sid recipient, Permission permission, boolean additive) {
        effectPermissions(acl, recipient, Collections.singleton(permission), additive);
    }

    public void effectPermissions(MutableAcl acl, Sid recipient, Set<Permission> newPermissions, boolean additive) {
        Set<Permission> existingPermissions = findExistingPermissions(acl, recipient);

        if (!additive) {
            Set<Permission> permsToRemove = new HashSet<Permission>();
            permsToRemove.addAll(existingPermissions);
            permsToRemove.removeAll(newPermissions);
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
            acl.insertAce(acl.getEntries().size(), perm, recipient, true);
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
        final List<AccessControlEntry> entries = acl.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            final AccessControlEntry entry = entries.get(i);
            if (entry.getSid().equals(recipient) && permission.equals(entry.getPermission())) {
                return i;
            }
        }
        return -1;
    }

    private MutableAcl findAcl(Object obj, Sid sid) {
        final ObjectIdentity identity = identityRetrievalStrategy.getObjectIdentity(obj);
        return (MutableAcl) aclService.readAclById(identity, Arrays.asList(sid));
    }

    @Secured("ACL_OBJECT_ADMIN")
    public void makePrivate(AccessControlled obj) {
        final Sid sid = sidForRole(ROLE_EVERYONE);
        effectPermissions(findAcl(obj, sid), sid, Collections.<Permission>emptySet(), false);
        log.info("Removed access to " + obj + " by ROLE_EVERYONE");
    }

}
