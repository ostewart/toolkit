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

import java.util.Date;
import org.acegisecurity.acl.basic.AclObjectIdentity;
import org.acegisecurity.acl.basic.AclObjectIdentityAware;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;


public class Photo extends Image implements AclObjectIdentityAware {
    private Lens m_lens;
    private Camera m_camera;
    private String m_notes;
    private Date m_captureDate;
    private ImageGroup m_roll;

    public Photo(long id) {
        super(id);
    }

    public Photo() {
    }

    public Photo(Image image) {
        super(image);
    }

    public Photo(Photo photo) {
        super(photo);

        setLens(photo.getLens());
        setCamera(photo.getCamera());
        setNotes(photo.getNotes());
        setCaptureDate(photo.getCaptureDate());
        setRoll(photo.getRoll());
    }

    public Lens getLens() {
        return m_lens;
    }

    public void setLens(Lens lens) {
        m_lens = lens;
    }

    public Camera getCamera() {
        return m_camera;
    }

    public void setCamera(Camera camera) {
        m_camera = camera;
    }

    public String getNotes() {
        return m_notes;
    }

    public void setNotes(String notes) {
        m_notes = notes;
    }

    public Date getCaptureDate() {
        return m_captureDate;
    }

    public void setCaptureDate(Date date) {
        m_captureDate = date;
    }

    public ImageGroup getRoll() {
        return m_roll;
    }

    // XXX: assert(m_imageGroup.getType().equals("roll"));
    public void setRoll(ImageGroup group) {
        m_roll = group;
    }

    public AclObjectIdentity getAclObjectIdentity() {
        return new NamedEntityObjectIdentity(Photo.class.getName(),
                                             Long.toString(getId()));
    }
}
