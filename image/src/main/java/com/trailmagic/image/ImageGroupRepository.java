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

import java.util.List;

public interface ImageGroupRepository {
    public ImageGroup getRollByOwnerAndName(User owner, String rollName);
    public ImageFrame getImageFrameByGroupNameTypeAndImageId(String groupName, ImageGroupType groupType, long imageId)
            throws NoSuchImageFrameException;

    public List<User> getOwnersByType(ImageGroupType groupType);
    public List<ImageGroup> getByOwnerScreenNameAndType(String screenName, ImageGroupType groupType);
    public ImageGroup getByOwnerNameAndTypeWithFrames(User owner, String groupName, ImageGroupType groupType)
            throws NoSuchImageGroupException;
    public List<ImageGroup> getByImage(Image image);
    public ImageGroup getRollForImage(Image image);
    public ImageGroup getById(long id);
    public ImageGroup getByIdWithFrames(long id);
    public ImageGroup loadById(long imageGroupId);
    public List<ImageGroup> getAll();
    public void saveNewGroup(ImageGroup newGroup);
    public ImageGroup saveGroup(ImageGroup imageGroup);
    public int getPublicFrameCount(ImageGroup group);
}
