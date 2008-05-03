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

import com.trailmagic.image.util.ImagesParser;
import java.io.ByteArrayInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ImageImportController extends SimpleFormController {
    private ImagesParser imagesParser;

    private static Logger s_logger =
        Logger.getLogger(ImageImportController.class);

    public ImageImportController(ImagesParser imagesParser) {
        super();
        this.imagesParser = imagesParser;
    }

    protected ModelAndView onSubmit(HttpServletRequest req,
                                    HttpServletResponse res,
                                    Object command,
                                    BindException errors)
        throws Exception {
        try {
            s_logger.debug("onSubmit called.");
            ImageImportBean bean = (ImageImportBean)command;

            if ( bean == null ) {
                throw new Exception("null command in ImageImportController");
            }

            byte[] data = bean.getImagesData();
            s_logger.debug("imagesData: " + data);
            ByteArrayInputStream bis = new ByteArrayInputStream(data);

            imagesParser.parse(bis);
        } catch (Exception e) {
            s_logger.warn("Exception in onSubmit", e);
            throw e;
        }
        
        return super.onSubmit(req, res, command, errors);
    }


    protected void initBinder(HttpServletRequest request,
                              ServletRequestDataBinder binder)
        throws ServletException {

        // to actually be able to convert Multipart instance to byte[]
        // we have to register a custom editor (in this case the
        // ByteArrayMultipartEditor)
        binder.registerCustomEditor(byte[].class,
                                    new ByteArrayMultipartFileEditor());
        // now Spring knows how to handle multipart object and convert them

        binder.setRequiredFields(new String[] {"imagesData"});
    }
}
