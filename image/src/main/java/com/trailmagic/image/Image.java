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
import java.util.TreeSet;
import org.springframework.security.acl.basic.AclObjectIdentity;
import org.springframework.security.acl.basic.AclObjectIdentityAware;
import org.springframework.security.acl.basic.NamedEntityObjectIdentity;

public class Image implements Owned, AclObjectIdentityAware {
    private long id;
    private String name;
    private String displayName;
    private String caption;
    private String copyright;
    private String creator;
    private SortedSet<ImageManifestation> manifestations;
    private User owner;
    private ImageCD imageCD;
    private Integer number;

    public Image(long id) {
        super();
        // nothing for now
        this.id = id;
    }

    public Image() {
        this.manifestations = new TreeSet<ImageManifestation>();
    }

    public Image(Image image) {
        setName(image.getName());
        setDisplayName(image.getDisplayName());
        setCaption(image.getCaption());
        setCopyright(image.getCopyright());
        setCreator(image.getCreator());
        setOwner(image.getOwner());
        setImageCD(image.getImageCD());
        setManifestations(image.getManifestations());
        setNumber(image.getNumber());
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public ImageCD getImageCD() {
        return imageCD;
    }

    public void setImageCD(ImageCD cd) {
        this.imageCD = cd;
    }

    public SortedSet<ImageManifestation> getManifestations() {
        return manifestations;
    }

    public void setManifestations(SortedSet<ImageManifestation>
                                  manifestations) {
        this.manifestations = manifestations;
    }

    public void addManifestation(ImageManifestation im) {
        im.setImage(this);
        manifestations.add(im);
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Image) &&
            //            (this.getId() == ((Image)obj).getId());
            (this.getName().equals(((Image)obj).getName())) &&
            (this.getOwner().equals(((Image)obj).getOwner()));
    }

    public AclObjectIdentity getAclObjectIdentity() {
        return new NamedEntityObjectIdentity(Image.class.getName(),
                                             Long.toString(getId()));
    }

    public String toString() {
        return getClass().getName() + ": " + getName();
    }

}
