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
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Controller
public class ImageManifestationController {
    private final TransactionTemplate transactionTemplate;
    private ImageManifestationRepository imageManifestationRepository;

    private static Logger log = LoggerFactory.getLogger(ImageManifestationController.class);

    @Autowired
    public ImageManifestationController(ImageManifestationRepository imageManifestationRepository,
                                        PlatformTransactionManager transactionManager) {
        this.imageManifestationRepository = imageManifestationRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @RequestMapping("/mf/by-id/{imageId}")
    @Transactional(readOnly = true)
    public ModelAndView imageById(final HttpServletResponse res, @PathVariable("imageId") final Long imageId) throws Exception {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    HeavyImageManifestation mf = imageManifestationRepository.getHeavyById(imageId);

                    java.io.InputStream dataStream = mf.getData().getBinaryStream();
                    res.setContentLength((int) mf.getData().length());
                    res.setContentType(mf.getFormat());
                    if (mf.getName() != null) {
                        res.setHeader("Content-Disposition", "inline; filename=" + mf.getName() + ";");
                    }

                    OutputStream out = res.getOutputStream();
                    IOUtils.copy(dataStream, out);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return null;
    }
}
