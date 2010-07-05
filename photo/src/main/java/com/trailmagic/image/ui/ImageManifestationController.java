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

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.ImageManifestationRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

public class ImageManifestationController extends AbstractController {
    private String controllerPath;
    private ImageManifestationRepository imageManifestationRepository;

    private static Logger log =
        LoggerFactory.getLogger(ImageManifestationController.class);

    public ImageManifestationController(ImageManifestationRepository imageManifestationRepository) {
        super();
        this.imageManifestationRepository = imageManifestationRepository;
    }

    public String getControllerPath() {
        return controllerPath;
    }

    public void setControllerPath(String path) {
        controllerPath = path;
    }

    public ModelAndView handleRequestInternal(HttpServletRequest req,
                                              HttpServletResponse res)
        throws Exception {

        UrlPathHelper pathHelper = new UrlPathHelper();
        String myPath = pathHelper.getLookupPathForRequest(req);
        log.debug("Controller Path: " + controllerPath);
        myPath = myPath.substring(controllerPath.length());
        StringTokenizer pathTokens = new StringTokenizer(myPath, "/");

        Map<String,Object> model = new HashMap<String,Object>();

        String method = pathTokens.nextToken();

        if ( method.equals("by-id") ) {
            HeavyImageManifestation mf =
                imageManifestationRepository.getHeavyById(Long.parseLong(pathTokens.nextToken()));

            java.io.InputStream dataStream = mf.getData().getBinaryStream();
            res.setContentLength((int) mf.getData().length());

            log.debug("Passing manifestation data stream to view (type: " + dataStream.getClass() + ")");
            model.put(InputStreamView.STREAM_KEY, dataStream);
            model.put(InputStreamView.CONTENT_TYPE_KEY, mf.getFormat());
            if (mf.getName() != null) {
                model.put(InputStreamView.CONTENT_DISPOSITION_KEY, "inline; filename=" + mf.getName() + ";");
            }
            return new ModelAndView(new InputStreamView(), model);
        }

        return null;
    }
}
