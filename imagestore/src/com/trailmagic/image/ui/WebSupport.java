package com.trailmagic.image.ui;

import com.trailmagic.image.*;
import com.trailmagic.user.*;

public class WebSupport {
    public static ImageManifestation getDefaultMF(User user, Image image) {
        return (ImageManifestation)image.getManifestations().first();
    }
}
