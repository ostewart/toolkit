/*
 * Copyright (c) 2006 Oliver Stewart.  All Rights Reserved.
 *
 * This file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.trailmagic.image.ui;

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageManifestation;
import com.trailmagic.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;

public class WebSupport {
    private static Logger s_logger =
            LoggerFactory.getLogger(WebSupport.class);

    public static ImageManifestation getDefaultMF(User user, Image image) {
        // default is small (384x256), so find the closest
        return getMFBySize(image, 384 * 256, false);
    }

    public static ImageManifestation getMFByLabel(Image image,
                                                  String label) {
        int target;
        if ("thumbnail".equals(label)) {
            target = 192 * 128;
        } else if ("small".equals(label)) {
            target = 384 * 256;
        } else if ("medium".equals(label)) {
            target = 768 * 512;
        } else if ("large".equals(label)) {
            target = 1536 * 1024;
        } else if ("huge".equals(label)) {
            target = 3072 * 2048;
        } else {
            throw new IllegalArgumentException("Unsupported label: " + label);
        }

        return getMFBySize(image, target, false);
    }

    public static ImageManifestation getMFBySize(Image image, int size, boolean original) {
        if (original) {
            return findOriginal(image);
        }
        SortedSet mfs = image.getManifestations();
        ImageManifestation closest, tmpMF;

        Iterator iter = mfs.iterator();
        if (iter.hasNext()) {
            closest = (ImageManifestation) iter.next();
        } else {
            return null;
        }

        while (iter.hasNext()) {
            tmpMF = (ImageManifestation) iter.next();

            if (tmpMF.isOriginal()) {
                continue;
            }

            // if tmpMF's area is closer to the target size...
            if (Math.abs(size - (tmpMF.area())) < Math.abs(size - (closest.area()))) {
                closest = tmpMF;
            }
        }

        return closest;
    }

    private static ImageManifestation findOriginal(Image image) {
        for (ImageManifestation manifestation : image.getManifestations()) {
            if (manifestation.isOriginal()) {
                return manifestation;
            }
        }
        return null;
    }

    /**
     * If the request URI does not end with a /, redirects to the same URI
     * with a trailing /.  Otherwise, does nothing.
     *
     * @param req the servlet request
     * @param res the servlet response
     */
    public static boolean handleDirectoryUrlRedirect(HttpServletRequest req,
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
            return true;
        }
        return false;
    }

    public static Long extractImageIdFromRequest(HttpServletRequest req)
            throws IllegalArgumentException, NumberFormatException {
        UrlPathHelper pathHelper = new UrlPathHelper();
        String myPath = pathHelper.getLookupPathForRequest(req);
        s_logger.debug("Lookup path: " +
                       pathHelper.getLookupPathForRequest(req));
        StringTokenizer pathTokens = new StringTokenizer(myPath, "/");
        String token = null;
        while (pathTokens.hasMoreTokens()) {
            token = pathTokens.nextToken();
        }

        if (s_logger.isDebugEnabled()) {
            s_logger.debug("Last token is: " + token);
        }

        if (token == null || "".equals(token)) {
            throw new IllegalArgumentException("Invalid request");
        }
        return new Long(token);
    }
}
