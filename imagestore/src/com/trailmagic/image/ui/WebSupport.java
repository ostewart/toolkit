package com.trailmagic.image.ui;

import java.util.SortedSet;

import com.trailmagic.image.*;
import com.trailmagic.user.*;

public class WebSupport {
    public static ImageManifestation getDefaultMF(User user, Image image) {
        // XXX: need to handle null
        SortedSet mfs = image.getManifestations();
        return (ImageManifestation)mfs.first();
    }
}
