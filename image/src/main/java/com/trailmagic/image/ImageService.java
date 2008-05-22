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

public interface ImageService {
    public void saveNewImage(Image image);
    public void saveNewImageGroup(ImageGroup imageGroup);
    public void saveNewImageFrame(ImageFrame imageFrame);
    public void saveNewImageManifestation(HeavyImageManifestation imageManifestation);
    public ImageFrame addImageToGroup(Image image, ImageGroup group, int position);
    public void makeImageGroupPublic(ImageGroup group);
    public void makeImageGroupPublic(String ownerName,
                                     ImageGroup.Type type,
                                     String imageGroupName)
        throws NoSuchImageGroupException;
    public void setImageGroupPreview(long imageGroupId, long imageId)
        throws NoSuchImageGroupException;
}
