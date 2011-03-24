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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebSupport {
    public static ImageManifestation getDefaultMF(Image image) {
        // default is small (384x256), so find the closest
        return image.manifestationClosestTo(384 * 256);
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
}
