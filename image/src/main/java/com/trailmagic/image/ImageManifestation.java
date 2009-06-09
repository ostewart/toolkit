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

/**
 * This class maps the metadata of the manifestation, while its subclass,
 * the <code>HeavyImageManifestation</code> also maps the data.
 */
public class ImageManifestation implements Comparable<ImageManifestation>, Owned {
    private long id;
    @IdentityProxy
    private Image image;
    private int height;
    private int width;
    private String format;
    private boolean original;
    private String name;

    public ImageManifestation() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        // this would be redundant if we added to the other collection
        //        SortedSet mfs = image.getManifestations();
        //        mfs.add(this);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getArea() {
        return height * width;
    }

    public User getOwner() {
        return image.getOwner();
    }

    public int compareTo(ImageManifestation other) {
        int difference = this.getArea() - other.getArea();
        if (difference == 0) {
            return (int) (this.getId() - other.getId());
        } else {
            return difference;
        }
    }

    public boolean equals(Object obj) {
        return obj instanceof ImageManifestation && this.getId() == ((ImageManifestation) obj).getId();

    }

    public int hashCode() {
        return new Long(getId()).hashCode();
    }

    @Override
    public String toString() {
        return getClass() + "(id=" + id
                + "; name=" + name
                + "; height=" + height
                + "; width=" + width
                + "; format=" + format
                + "; original=" + original;

    }
}
