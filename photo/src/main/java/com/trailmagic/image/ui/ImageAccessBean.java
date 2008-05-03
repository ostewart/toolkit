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

public class ImageAccessBean {
    private long m_id;
    private String m_action;
    private String m_target;

    public ImageAccessBean() {
        // do nothing
    }

    public String getTarget() {
        return m_target;
    }

    public void setTarget(String target) {
        m_target = target;
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public String getAction() {
        return m_action;
    }

    public void setAction(String action) {
        m_action = action;
    }
}