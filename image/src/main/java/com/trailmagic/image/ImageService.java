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
package com.trailmagic.image;

import com.trailmagic.user.User;

import java.io.InputStream;
import java.io.IOException;

public interface ImageService {
    public Photo createImage(InputStream inputStream) throws IllegalStateException, IOException;
    public Photo createImage(ImageMetadata imageMetadata, InputStream inputStream) throws IllegalStateException, IOException;
    public Photo createImage(ImageMetadata imageData) throws IllegalStateException;
    public ImageFrame addImageToGroup(Image image, ImageGroup group, int position);
    public ImageFrame addImageToGroup(Image image, ImageGroup group);
    public void makeImageGroupAndImagesPublic(ImageGroup group);
    public void makeImageGroupAndImagesPublic(String ownerName, ImageGroup.Type type, String imageGroupName) throws NoSuchImageGroupException;
    public void setImageGroupPreview(long imageGroupId, long imageId) throws NoSuchImageGroupException;

    public ImageGroup findNamedOrDefaultRoll(String rollName, User owner);
}
