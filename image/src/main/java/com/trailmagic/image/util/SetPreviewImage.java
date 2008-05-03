package com.trailmagic.image.util;

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageGroup;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SetPreviewImage {
    public void setPreviewImage(ImageGroup group, Image image);
    public void setDefaultPreviewImage(ImageGroup group);
    public void setAllDefault();
}
