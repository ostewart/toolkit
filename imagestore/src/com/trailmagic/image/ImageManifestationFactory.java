package com.trailmagic.image;

import java.util.List;

public interface ImageManifestationFactory {
    public ImageManifestation newInstance();
    public ImageManifestation getById(long id);
    public List getAllForImageId(long imageId);
}

