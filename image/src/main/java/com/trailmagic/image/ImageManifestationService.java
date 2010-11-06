package com.trailmagic.image;

import com.trailmagic.resizer.ImageFileInfo;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by: oliver on Date: Sep 26, 2010 Time: 7:39:04 PM
 */
public interface ImageManifestationService {
    public void createManifestation(Image image, ImageFileInfo info, boolean original) throws IOException;

}
