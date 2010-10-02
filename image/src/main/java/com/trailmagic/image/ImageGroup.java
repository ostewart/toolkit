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

import com.trailmagic.image.security.AccessControlled;
import com.trailmagic.user.Owned;
import com.trailmagic.user.User;

import java.util.Collection;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

public class ImageGroup implements Owned, AccessControlled {
    public static final String DEFAULT_ROLL_NAME = "uploads";

    public ImageGroup(String name, User owner, Type type) {
        this.name = name;
        this.owner = owner;
        this.type = type;
    }

    public enum Type {
        ROLL("roll", "Roll", "Rolls"), ALBUM("album", "Album", "Albums");
        private String typeName;
        private String displayName;
        private String pluralDisplayName;

        private Type(String typeName, String displayName, String pluralDisplayName) {
            this.typeName = typeName;
            this.displayName = displayName;
            this.pluralDisplayName = pluralDisplayName;
        }

        public String toString() {
            return this.typeName;
        }

        public String getDisplayString() {
            return displayName;
        }

        @SuppressWarnings({"UnusedDeclaration"})
        public String getPluralDisplayString() {
            return pluralDisplayName;
        }

        public static Type fromString(String typeString) {
            for (Type type : Type.values()) {
                if (type.typeName.equalsIgnoreCase(typeString)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid type string");
        }
    }

    private long id;
    private Type type;
    private String name;
    private String displayName;
    private String description;
    private Date uploadDate;
    private SortedSet<ImageFrame> frames = new TreeSet<ImageFrame>();
    private Collection<ImageGroup> subgroups;
    private ImageGroup supergroup;
    private User owner;
    private Image previewImage;

    protected ImageGroup() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * URL worthy name *
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * name to be displayed to the user *
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        displayName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        description = desc;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @SuppressWarnings({"JpaAttributeMemberSignatureInspection"})
    public String getTypeDisplay() {
        return type.getDisplayString();
    }

    public void addFrame(ImageFrame frame) {
        frames.add(frame);
        frame.setImageGroup(this);
    }

    public SortedSet<ImageFrame> getFrames() {
        return frames;
    }

    public void setFrames(SortedSet<ImageFrame> frames) {
        this.frames = frames;
    }

    @SuppressWarnings({"JpaAttributeMemberSignatureInspection"})
    public int getNextFrameNumber() {
        ImageFrame lastFrame = frames.last();
        return lastFrame.getPosition() + 1;
    }

    public ImageGroup getSupergroup() {
        return supergroup;
    }

    public void setSupergroup(ImageGroup group) {
        supergroup = group;
    }

    public void addSubgroup(ImageGroup group) {
        group.setSupergroup(this);
        subgroups.add(group);
    }

    public Collection<ImageGroup> getSubgroups() {
        return subgroups;
    }

    public void setSubgroups(Collection<ImageGroup> subgroups) {
        this.subgroups = subgroups;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ImageGroup) &&
               (this.getName().equals(((ImageGroup) obj).getName())) &&
               (this.getOwner().equals(((ImageGroup) obj).getOwner())) &&
               (this.getType().equals(((ImageGroup) obj).getType()));
    }

    @Override
    public int hashCode() {
        return getName().hashCode() + getOwner().hashCode() + getType().hashCode();
    }

    public Image getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(Image previewImage) {
        this.previewImage = previewImage;
    }

    @Override
    public String toString() {
        return "ImageGroup(id=" + id
               + "; type=" + type
               + "; name=" + name
               + "; owner=" + owner
               + ")";
    }
}
