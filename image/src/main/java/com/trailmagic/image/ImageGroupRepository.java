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

import com.trailmagic.image.ImageGroup.Type;
import com.trailmagic.user.User;
import java.util.List;

public interface ImageGroupRepository {
    public int ROLL_TYPE = 0;
    public int ALBUM_TYPE = 1;

    public ImageGroup newInstance(int type);
    public ImageGroup getAlbumByOwnerAndName(User owner, String albumName);
    public ImageGroup getRollByOwnerAndName(User owner, String rollName);
    public ImageFrame getImageFrameByImageGroupAndImageId(ImageGroup group,
                                                          long imageId)
        throws NoSuchImageFrameException;
    public ImageFrame getImageFrameByGroupNameAndImageId(String groupName,
                                                         long imageId)
        throws NoSuchImageFrameException;
    public List<ImageFrame> getFramesContainingImage(Image image);
    public List<ImageGroup> getAlbumsByOwnerScreenName(String screenName);
    public List<ImageGroup> getRollsByOwnerScreenName(String screenName);
    public List<User> getAlbumOwners();
    public List<User> getRollOwners();
    public List<User> getOwnersByType(Type groupType);
    public List<ImageGroup> getByOwnerScreenNameAndType(String screenName,
                                                        Type groupType);
    public ImageGroup getByOwnerNameAndTypeWithFrames(User owner, String groupName,
                                                      Type groupType)
        throws NoSuchImageGroupException;
    public List<ImageGroup> getByImage(Image image);
    public ImageGroup getRollForImage(Image image);
    public ImageGroup getById(long id);
    public List<ImageGroup> getAll();
    /** Only need to call this on a new object **/
    public void saveFrame(ImageFrame frame);
    /** Creates a new ImageGroup...call saveGroup() to persist **/
    public ImageGroup createImageGroup(Type type);
    public void saveGroup(ImageGroup newGroup);
    public int getPublicFrameCount(ImageGroup group);
    public int getAccurateFrameCount(ImageGroup group);
}
