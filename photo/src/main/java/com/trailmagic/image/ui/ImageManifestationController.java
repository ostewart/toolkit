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

import com.trailmagic.image.ImageManifestationRepository;
import com.trailmagic.image.StreamWrapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

@Controller
public class ImageManifestationController {
    private ImageManifestationRepository imageManifestationRepository;

    private static Logger log = LoggerFactory.getLogger(ImageManifestationController.class);

    @Autowired
    public ImageManifestationController(ImageManifestationRepository imageManifestationRepository) {
        this.imageManifestationRepository = imageManifestationRepository;
    }

    @RequestMapping("/mf/by-id/{imageId}")
    @Transactional(readOnly = true)
    public ModelAndView imageById(final HttpServletResponse res, @PathVariable("imageId") final Long imageId) throws Exception {
        imageManifestationRepository.streamHeavyById(imageId, new StreamWrapper() {
            @Override
            public void stream(int length, String contentType, String name, InputStream in) throws Exception {
                res.setContentLength(length);
                res.setContentType(contentType);
                if (name != null) {
                    res.setHeader("Content-Disposition", "inline; filename=" + name + ";");
                }
                OutputStream out = res.getOutputStream();
                IOUtils.copy(in, out);
            }
        });
        return null;
    }
}
