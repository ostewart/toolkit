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

import java.util.Iterator;
import java.util.SortedSet;

public class ImageFrame implements Owned, Comparable<ImageFrame> {
    private long id;
    private ImageGroup imageGroup;
    private int position;
    @IdentityProxy
    private Image image;
    private String caption;

    protected ImageFrame() {
        // for hibernate only, always need image for security check
    }

    public ImageFrame(Image image) {
        this.image = image;
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
//        if (!group.getFrames().contains(this)) {
//            throw new IllegalStateException("setImageGroup called on ImageFrame with ImageGroup that doesn't contain it.  Add frame to group first.");
//        }

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

    @SuppressWarnings({"RedundantIfStatement"})
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageFrame)) return false;

        ImageFrame frame = (ImageFrame) o;

        if (position != frame.position) return false;
        if (image != null ? !image.equals(frame.image) : frame.image != null) return false;
        if (imageGroup != null ? !imageGroup.equals(frame.imageGroup) : frame.imageGroup != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = imageGroup != null ? imageGroup.hashCode() : 0;
        result = 31 * result + position;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }

    public int compareTo(ImageFrame other) {
        int positionDifference = Integer.valueOf(position).compareTo(other.position);
        if (imageGroup == null || imageGroup == other.imageGroup || imageGroup.equals(other.imageGroup)) {
            return positionDifference;
        } else {
            return imageGroup.getName().compareTo(other.getImageGroup().getName()) + positionDifference;
        }
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

    public ImageFrame previous() {
        final SortedSet<ImageFrame> headSet = imageGroup.getFrames().headSet(this);

        if ( !headSet.isEmpty() ) {
            return headSet.last();
        }
        return null;
    }

    public ImageFrame next() {
        Iterator<ImageFrame> framesIter = imageGroup.getFrames().tailSet(this).iterator();
        framesIter.next();
        if ( framesIter.hasNext() ) {
            return framesIter.next();
        }
        return null;
    }

}
