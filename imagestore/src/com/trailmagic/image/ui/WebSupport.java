package com.trailmagic.image.ui;

import java.util.SortedSet;
import java.util.Iterator;

import com.trailmagic.image.*;
import com.trailmagic.user.*;

public class WebSupport {
    public static ImageManifestation getDefaultMF(User user, Image image) {
        // default is medium (768x512), so find the closest
        return getMFBySize(image, 768*512);
    }

    public static ImageManifestation getMFByLabel(Image image,
                                                  String label) {
        int target;
        if ( "thumbnail".equals(label) ) {
            target = 192*128;
        } else if ( "small".equals(label) ) {
            target = 384*256;
        } else if ( "medium".equals(label) ) {
            target = 768*512;
        } else if ( "large".equals(label) ) {
            target = 1536*1024;
        } else if ( "huge".equals(label) ) {
            target = 3072*2048;
        } else {
            throw new IllegalArgumentException("Unsupported label: " + label);
        }

        return getMFBySize(image, target);
    }

    public static ImageManifestation getMFBySize(Image image,
                                                 int size) {
        SortedSet mfs = image.getManifestations();
        ImageManifestation closest, tmpMF;

        Iterator iter = mfs.iterator();
        if ( iter.hasNext() ) {
            closest = (ImageManifestation)iter.next();
        } else {
            return null;
        }

        while (iter.hasNext()) {
            tmpMF = (ImageManifestation)iter.next();

            // if tmpMF's area is closer to the target size...
            if ( Math.abs(size - (tmpMF.getHeight() * tmpMF.getWidth())) <
                 Math.abs(size -
                          (closest.getHeight() * closest.getWidth())) ) {
                closest = tmpMF;
            }
        }

        return closest;
    }
}
