package com.trailmagic.image.ui;

import java.util.Iterator;
import java.util.SortedSet;
import org.apache.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.trailmagic.image.*;
import com.trailmagic.user.*;

public class WebSupport {
    private static Logger s_logger =
        Logger.getLogger(WebSupport.class);

    public static ImageManifestation getDefaultMF(User user, Image image) {
        // default is small (384x256), so find the closest
        return getMFBySize(image, 384*256);
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

    /**
     * If the request URI does not end with a /, redirects to the same URI
     * with a trailing /.  Otherwise, does nothing.
     *
     * @param req the servlet request
     * @param res the servlet response
     **/
    public static void handleDirectoryUrlRedirect(HttpServletRequest req,
                                                  HttpServletResponse res)
    throws IOException {
        String uri = req.getRequestURI();
        // if trailing / already, no work to do; we're done
        if (!uri.endsWith("/")) {
            StringBuffer newLocation = new StringBuffer();
            newLocation.append(uri);
            newLocation.append("/");
            if (req.getQueryString() != null) {
                newLocation.append("?");
                newLocation.append(req.getQueryString());
            }

            res.sendRedirect(newLocation.toString());
        }
    }
}
