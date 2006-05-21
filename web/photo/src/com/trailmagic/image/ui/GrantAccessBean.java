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
package com.trailmagic.image.ui;

import java.util.List;

public class GrantAccessBean {
    private List<String> m_imageIds;
    private int m_mask;
    private String m_recipient;

    public GrantAccessBean() {
        // do nothing
    }

    public List<String> getImageIds() {
        return m_imageIds;
    }

    public void setImageIds(List<String> imageIds) {
        m_imageIds = imageIds;
    }

    public void setMask(int mask) {
        m_mask = mask;
    }

    public int getMask() {
        return m_mask;
    }

    public String getRecipient() {
        return m_recipient;
    }

    public void setRecipient(String recipient) {
        m_recipient = recipient;
    }
}