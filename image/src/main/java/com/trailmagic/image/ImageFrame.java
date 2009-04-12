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
import org.springframework.security.acl.basic.AclObjectIdentity;
import org.springframework.security.acl.basic.AclObjectIdentityAware;
import org.springframework.security.acl.basic.NamedEntityObjectIdentity;

public class ImageFrame implements Owned, Comparable<ImageFrame>, AclObjectIdentityAware {
    private long m_id;
    private ImageGroup m_imageGroup;
    private int m_position;
    private Image m_image;
    private String m_caption;

    public ImageFrame() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }



    public ImageGroup getImageGroup() {
        return m_imageGroup;
    }

    public void setImageGroup(ImageGroup group) {
        m_imageGroup = group;
    }

    public int getPosition() {
        return m_position;
    }

    public void setPosition(int pos) {
        m_position = pos;
    }

    public Image getImage() {
        return m_image;
    }

    public void setImage(Image image) {
        m_image = image;
    }

    public String getCaption() {
        return m_caption;
    }

    public void setCaption(String caption) {
        m_caption = caption;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ImageFrame) &&
            (this.getImageGroup().equals(((ImageFrame)obj).getImageGroup())) &&
            (this.getImage().equals(((ImageFrame)obj).getImage()));
        /*
        if ( !(obj instanceof ImageFrame) ) {return false;}
        ImageGroup mine = getImageGroup();
        ImageGroup yours = ((ImageFrame)obj).getImageGroup();
        System.err.println("mine: " + mine + ", yours: " + yours);

        if (!(mine.equals(yours))) {
            return false;
        }
        if (!(this.getImage().equals(((ImageFrame)obj).getImage()))) {
            return false;
        }
        return false;
        */
    }


    public int compareTo(ImageFrame other) {
        // XXX: need to add something to this to make it consistent with equals
        return (this.m_position - other.m_position);
    }

    public int hashCode() {
        return m_position;
    }

    /**
     * The frame is always owned by the owner of the image group,
     * since it's really only a link class.  This could cause mayhem if
     * the permissions on an image change to disallow access to the group
     * owner, but it's a tricky situation anyway, and the frame really
     * properly belongs to the group.
     **/
    public User getOwner() {
        return getImageGroup().getOwner();
    }

    public AclObjectIdentity getAclObjectIdentity() {
        return new NamedEntityObjectIdentity(ImageFrame.class.getName(),
                                             Long.toString(getId()));
    }
    
    @Override
    public String toString() {
        return getClass() + "(id=" + m_id
            + "; position=" + m_position
            + "; image=" + m_image
            + ")";
    }
}
