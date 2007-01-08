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

import java.util.List;

import com.trailmagic.user.User;

public interface ImageFactory {

    public Image newInstance();
    public Image getById(long id);
    public List<Image> getAll();
    public List<Image> getByName(String name);
    public List<Image> getByNameAndGroup(String name, ImageGroup group);
    /** don't use this...use ImageManager.addPhoto(..) **/ 
    public Photo createPhoto();
	public void save(Photo newPhoto);
}
