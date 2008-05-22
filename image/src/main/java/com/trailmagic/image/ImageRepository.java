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

public interface ImageRepository {
    public Image getById(long id);
    public Image loadById(long imageId) throws NoSuchImageException;
    public List<Image> getAll();
    public List<Image> getByName(String name);
    public List<Image> getByNameAndGroup(String name, ImageGroup group);
    public void saveNew(Image image);
    public Image save(Image image);
}
