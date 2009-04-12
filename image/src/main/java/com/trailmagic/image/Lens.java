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

import org.springframework.security.acl.basic.AclObjectIdentity;
import org.springframework.security.acl.basic.AclObjectIdentityAware;
import org.springframework.security.acl.basic.NamedEntityObjectIdentity;

public class Lens implements AclObjectIdentityAware {
    private long m_id;
    private String m_name;
    private String m_manufacturer;
    private int m_focalLength;
    private int m_minAperature;
    private int m_maxAperature;

    public Lens(long id) {
        m_id = id;
    }

    public Lens() {
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getManufacturer() {
        return m_manufacturer;
    }

    public void setManufacturer(String mfr) {
        m_manufacturer = mfr;
    }

    public int getFocalLength() {
        return m_focalLength;
    }

    public void setFocalLength(int length) {
        m_focalLength = length;
    }

    public int getMinAperature() {
        return m_minAperature;
    }

    public void setMinAperature(int aperature) {
        m_minAperature = aperature;
    }

    public int getMaxAperature() {
        return m_maxAperature;
    }

    public void setMaxAperature(int aperature) {
        m_maxAperature = aperature;
    }

    public AclObjectIdentity getAclObjectIdentity() {
        return new NamedEntityObjectIdentity(Lens.class.getName(),
                                             Long.toString(getId()));
    }
}
