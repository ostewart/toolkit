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
import com.trailmagic.image.ImageManifestation;
import com.trailmagic.user.User;
import org.springframework.security.acls.Permission;

import java.util.Set;

public interface ImageSecurityService {
    public void addOwnerAcl(Image image);
    public void addOwnerAcl(ImageGroup group);
    public void makePublic(AccessControlled obj);
    public void makePrivate(AccessControlled obj);
    public void makeImagesPublic(ImageGroup group);
    public void makeImagesPrivate(ImageGroup group);
    public void addReadPermission(Object obj, User recipient);
    public void addReadPermission(Object obj, String recipientRole);
    public void addPermission(Object obj, String recipientRole, Permission permission);
    public void addPermissions(Object obj, User recipient, Set<Permission> permissions);
    public boolean isPublic(Object obj);
    public boolean isReadableByUser(Object target, User recipient);
    public boolean isAvailableToUser(Object target, User recipient, Permission permission);
    public boolean isReadableByRole(Object target, String role);
    public boolean isAvailableToRole(Object target, String role, Permission permission);
}