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

public interface ImageSecurityService {
    public void makePublic(Image image);
    public void makePublic(ImageFrame frame);
    public void makePublic(ImageGroup group);
    public void makeFramesPublic(ImageGroup group);
    public void makePublic(ImageManifestation mf);
    public void makePrivate(Image image);
    public void makePrivate(ImageFrame frame);
    public void makePrivate(ImageGroup group);
    public void makeFramesPrivate(ImageGroup group);
    public void makePrivate(ImageManifestation mf);
    public void addOwnerAcl(Image image);
    public void addOwnerAcl(ImageFrame frame);
    public void addOwnerAcl(ImageGroup group);
    public void addOwnerAcl(ImageManifestation mf);
    public void addReadPermission(Object obj, Object parent,
                                  Object recipient);
    public void addPermission(Image image, Object recipient, int mask);
    public void addPermission(Object obj, Object parent,
                              Object recipient, int mask);
    public void setPermission(Object obj, Object parent,
                              Object recipient, int mask);
    public boolean isPublic(Object obj);
}