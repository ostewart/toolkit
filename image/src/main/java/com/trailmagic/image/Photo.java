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

public class Photo extends Image {
    private Lens lens;
    private Camera camera;
    private String notes;
    private Date captureDate;
    private ImageGroup roll;

    public Photo(long id) {
        super(id);
    }

    public Photo() {
        super();
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
        return lens;
    }

    public void setLens(Lens lens) {
        this.lens = lens;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCaptureDate() {
        return captureDate;
    }

    public void setCaptureDate(Date date) {
        this.captureDate = date;
    }

    public ImageGroup getRoll() {
        return roll;
    }

    // XXX: assert(m_imageGroup.getType().equals("roll"));
    public void setRoll(ImageGroup group) {
        this.roll = group;
    }
}
