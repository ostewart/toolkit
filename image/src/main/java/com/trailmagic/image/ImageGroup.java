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
import java.util.Collection;
import java.util.Date;
import java.util.SortedSet;
import org.acegisecurity.acl.basic.AclObjectIdentity;
import org.acegisecurity.acl.basic.AclObjectIdentityAware;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;

public class ImageGroup implements Owned, AclObjectIdentityAware {
    public enum Type {
        ROLL("roll"), ALBUM("album");
        private String typeName;
        private static final String ROLL_DISPLAY = "Roll";
        private static final String ALBUM_DISPLAY = "Album";
        private static final String ROLL_DISPLAY_PLURAL = "Rolls";
        private static final String ALBUM_DISPLAY_PLURAL = "Albums";
        
        private Type(String typeName) {
            this.typeName = typeName;
        }
        public String toString() {
            return this.typeName;
        }
    
        public String getDisplayString() {
            switch (this) {
            case ROLL:
                return ROLL_DISPLAY;
            case ALBUM:
                return ALBUM_DISPLAY;
            default:
                throw new IllegalStateException("Unhandled type");    
            }
        }
    
        public String getPluralDisplayString() {
            switch (this) {
            case ROLL:
                return ROLL_DISPLAY_PLURAL;
            case ALBUM:
                return ALBUM_DISPLAY_PLURAL;
            default:
                throw new IllegalStateException("Unhandled type");    
            }
        }
        
        public static Type fromString(String typeString) {
            if ("album".equalsIgnoreCase(typeString)) {
                return ALBUM;
            } else if ("roll".equalsIgnoreCase(typeString)) {
                return ROLL;
            } else {
                throw new IllegalArgumentException("Invalid type string");
            }
        }
    }

    private long m_id;
    private Type m_type;
    private String m_name;
    private String m_displayName;
    private String m_description;
    private Date m_uploadDate;
    private SortedSet<ImageFrame> m_frames;
    private Collection<ImageGroup> m_subgroups;
    private ImageGroup m_supergroup;
    private User m_owner;
    private Image m_previewImage;

    public static final String ROLL_TYPE = "roll";
    public static final String ALBUM_TYPE = "album";

    public ImageGroup() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    /** URL worthy name **/
    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    /** name to be displayed to the user **/
    public String getDisplayName() {
        return m_displayName;
    }

    public void setDisplayName(String name) {
        m_displayName = name;
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String desc) {
        m_description = desc;
    }

    public Date getUploadDate() {
        return m_uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        m_uploadDate = uploadDate;
    }

    public Type getType() {
        return m_type;
    }

    public void setType(Type type) {
        m_type = type;
    }
    
    public String getTypeDisplay() {
    	switch (m_type) {
    	case ROLL:
    		return "Roll";
    	case ALBUM:
    		return "Album";
    	default:
    	    throw new IllegalStateException("Unknown type");
    	}
    }

    public void addFrame(ImageFrame frame) {
        m_frames.add(frame);
    }

    public SortedSet<ImageFrame> getFrames() {
        return m_frames;
    }

    public void setFrames(SortedSet<ImageFrame> frames) {
        m_frames = frames;
    }

    public int getNextFrameNumber() {
        ImageFrame lastFrame = m_frames.last();
        return lastFrame.getPosition() + 1;
    }

    public ImageGroup getSupergroup() {
        return m_supergroup;
    }

    public void setSupergroup(ImageGroup group) {
        m_supergroup = group;
    }

    public void addSubgroup(ImageGroup group) {
        group.setSupergroup(this);
        m_subgroups.add(group);
    }

    public Collection<ImageGroup> getSubgroups() {
        return m_subgroups;
    }

    public void setSubgroups(Collection<ImageGroup> subgroups) {
        m_subgroups = subgroups;
    }

    public User getOwner() {
        return m_owner;
    }

    public void setOwner(User owner) {
        m_owner = owner;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ImageGroup) &&
            (this.getName().equals(((ImageGroup)obj).getName())) &&
            (this.getOwner().equals(((ImageGroup)obj).getOwner())) &&
            (this.getType().equals(((ImageGroup)obj).getType()));
    }

    public AclObjectIdentity getAclObjectIdentity() {
        return new NamedEntityObjectIdentity(ImageGroup.class.getName(),
                                             Long.toString(getId()));
    }

    public Image getPreviewImage() {
        return m_previewImage;
    }

    public void setPreviewImage(Image previewImage) {
        m_previewImage = previewImage;
    }
    
    @Override
    public String toString() {
        return "ImageGroup(id=" + m_id
            + "type=" + m_type
            + "; name=" + m_name
            + "; owner=" + m_owner
            + ")";
    }
}
