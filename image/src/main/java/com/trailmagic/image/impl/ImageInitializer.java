package com.trailmagic.image.impl;

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.Image;
import com.trailmagic.image.ImageGroup;

/**
 * Created by: oliver on Date: Nov 6, 2010 Time: 6:34:09 PM
 */
public interface ImageInitializer {
    void saveNewImage(Image image);

    void saveNewImageGroup(ImageGroup imageGroup) throws IllegalStateException;

    void saveNewImageManifestation(HeavyImageManifestation imageManifestation);

    void saveNewImageManifestation(HeavyImageManifestation imageManifestation, boolean clearFromSession);
}
