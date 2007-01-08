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
import java.util.SortedSet;
import org.acegisecurity.acl.basic.AclObjectIdentity;
import org.acegisecurity.acl.basic.AclObjectIdentityAware;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;

/**
 * This class maps the metadata of the manifestation, while its subclass,
 * the <code>HeavyImageManifestation</code> also maps the data.
 **/
public class ImageManifestation implements Comparable, Owned,
                                           AclObjectIdentityAware {
    private long m_id;
    private Image m_image;
    private int m_height;
    private int m_width;
    private String m_format;
    private boolean m_original;
    private String m_name;

    public ImageManifestation() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public Image getImage() {
        return m_image;
    }

    public void setImage(Image image) {
        m_image = image;
        // this would be redundant if we added to the other collection
        //        SortedSet mfs = image.getManifestations();
        //        mfs.add(this);
    }

    public int getHeight() {
        return m_height;
    }

    public void setHeight(int height) {
        m_height = height;
    }

    public int getWidth() {
        return m_width;
    }

    public void setWidth(int width) {
        m_width = width;
    }

    public String getFormat() {
        return m_format;
    }

    public void setFormat(String format) {
        m_format = format;
    }

    public boolean isOriginal() {
        return m_original;
    }

    public void setOriginal(boolean original) {
        m_original = original;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public int getArea() {
        return m_height * m_width;
    }

    public User getOwner() {
        return m_image.getOwner();
    }

    public int compareTo(Object obj) throws ClassCastException {
        ImageManifestation other = (ImageManifestation) obj;

        int difference = this.getArea() - other.getArea();
        if (difference == 0) {
            return (int)(this.getId() - other.getId());
        } else {
            return difference;
        }
    }

    public boolean equals(Object obj) {
        if ( !(obj instanceof ImageManifestation) ) {
            return false;
        }

        return this.getId() == ((ImageManifestation)obj).getId();
    }

    public int hashCode() {
        return new Long(getId()).hashCode();
    }

    public AclObjectIdentity getAclObjectIdentity() {
        return new NamedEntityObjectIdentity(ImageManifestation.class
                                             .getName(),
                                             Long.toString(getId()));
    }
}
