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

import java.util.Collection;
import org.acegisecurity.acl.basic.AclObjectIdentity;
import org.acegisecurity.acl.basic.AclObjectIdentityAware;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;

public class ImageCD implements AclObjectIdentityAware {
    private long m_id;
    private int m_number;
    private String m_description;
    private Collection m_images;

    public ImageCD() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public int getNumber() {
        return m_number;
    }

    public void setNumber(int number) {
        m_number = number;
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String desc) {
        m_description = desc;
    }

    public Collection getImages() {
        return m_images;
    }

    public void setImages(Collection images) {
        m_images = images;
    }

    public AclObjectIdentity getAclObjectIdentity() {
        return new NamedEntityObjectIdentity(ImageCD.class.getName(),
                                             Long.toString(getId()));
    }
}
