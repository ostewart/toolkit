package com.trailmagic.image;

public interface ImageFactory {

    public Image newInstance();
    public Image getById(long id);
}
