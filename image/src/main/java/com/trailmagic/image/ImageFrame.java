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

import com.trailmagic.user.Owned;
import com.trailmagic.user.User;
import com.trailmagic.image.security.IdentityProxy;

public class ImageFrame implements Owned, Comparable<ImageFrame> {
    private long id;
    private ImageGroup imageGroup;
    private int position;
    @IdentityProxy
    private Image image;
    private String caption;

    public ImageFrame() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public ImageGroup getImageGroup() {
        return imageGroup;
    }

    public void setImageGroup(ImageGroup group) {
        imageGroup = group;
        if (!group.getFrames().contains(this)) {
            throw new IllegalStateException("ImageFrame added to ImageGroup that doesn't contain it.  Add frame to group first.");
        }

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int pos) {
        position = pos;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean equals(Object obj) {
        return obj instanceof ImageFrame &&
               this.getImageGroup().equals(((ImageFrame)obj).getImageGroup()) &&
               this.getImage().equals(((ImageFrame)obj).getImage());
    }

    public int compareTo(ImageFrame other) {
        return this.comparisonString().compareTo(other.comparisonString());
    }

    private String comparisonString() {
        return image.getName() + image.getOwner() + position;
    }

    public int hashCode() {
        return getImage().hashCode() + getImageGroup().hashCode();
    }

    /**
     * The frame is always owned by the owner of the image group,
     * since it's really only a link class.  This could cause mayhem if
     * the permissions on an image change to disallow access to the group
     * owner, but it's a tricky situation anyway, and the frame really
     * properly belongs to the group.
     **/
    @SuppressWarnings({"JpaAttributeMemberSignatureInspection", "JpaAttributeTypeInspection"})
    public User getOwner() {
        return getImageGroup().getOwner();
    }

    
    @Override
    public String toString() {
        return getClass() + "(id=" + id
            + "; position=" + position
            + "; image=" + image
            + "; imageGroup=" + imageGroup
            + ")";
    }
}
