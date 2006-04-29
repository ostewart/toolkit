package com.trailmagic.image;

import java.util.List;

public interface ImageFactory {

    public Image newInstance();
    public Image getById(long id);
    public List<Image> getAll();
    public List<Image> getByName(String name);
    public List<Image> getByNameAndGroup(String name, ImageGroup group);
}
