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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ImageManifestationController {
    private ImageManifestationRepository imageManifestationRepository;

    private static Logger log = LoggerFactory.getLogger(ImageManifestationController.class);

    @Autowired
    public ImageManifestationController(ImageManifestationRepository imageManifestationRepository) {
        this.imageManifestationRepository = imageManifestationRepository;
    }

    @RequestMapping("/mf/by-id/{imageId}")
    public ModelAndView imageById(HttpServletResponse res, @PathVariable("imageId") Long imageId) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        HeavyImageManifestation mf = imageManifestationRepository.getHeavyById(imageId);

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
}
