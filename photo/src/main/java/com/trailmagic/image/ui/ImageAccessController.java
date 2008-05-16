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
import com.trailmagic.image.ImageRepository;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.security.ImageSecurityFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.acegisecurity.ui.savedrequest.SavedRequest;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ImageAccessController extends SimpleFormController {
    private ImageSecurityFactory imageSecurityFactory;
    private ImageRepository imageRepository;
    private ImageGroupRepository imageGroupRepository;
    private static Logger s_log =
        Logger.getLogger(ImageAccessController.class);

    private static final String MAKE_PUBLIC_ACTION = "makePublic";
    private static final String MAKE_PRIVATE_ACTION = "makePrivate";
    private static final String IMAGE_TARGET = "image";
    private static final String IMAGE_GROUP_TARGET = "imageGroup";

    public void setImageSecurityFactory(ImageSecurityFactory factory) {
        this.imageSecurityFactory = factory;
    }

    public void setImageRepository(ImageRepository repository) {
        this.imageRepository = repository;
    }

    public void setImageGroupRepository(ImageGroupRepository imageGroupRepository) {
        this.imageGroupRepository = imageGroupRepository;
    }

    protected ModelAndView onSubmit(HttpServletRequest req,
                                    HttpServletResponse res,
                                    Object command,
                                    BindException errors) throws Exception {
        ImageAccessBean bean = (ImageAccessBean) command;

        if (bean == null) {
            throw new Exception("null command");
        }

        if (IMAGE_TARGET.equals(bean.getTarget())) {
            Image target = imageRepository.getById(bean.getId());

            if (MAKE_PUBLIC_ACTION.equals(bean.getAction())) {
                s_log.info("Making image public" + target.getClass()
                           + "; " + target);
                imageSecurityFactory.makePublic(target);
            } else if (MAKE_PRIVATE_ACTION.equals(bean.getAction())) {
                s_log.info("Making image private: " + target.getClass()
                           + "; " + target);
                imageSecurityFactory.makePrivate(target);
            } else {
                throw new Exception("Invalid action");
            }
        } else if (IMAGE_GROUP_TARGET.equals(bean.getTarget())) {
            ImageGroup target = imageGroupRepository.getById(bean.getId());

            if (MAKE_PUBLIC_ACTION.equals(bean.getAction())) {
                s_log.info("Making " + target.getType() + " public: "
                           + target);
                imageSecurityFactory.makePublic(target);
            } else if (MAKE_PRIVATE_ACTION.equals(bean.getAction())) {
                s_log.info("Making " + target.getType() + " private: "
                           + target);
                imageSecurityFactory.makePrivate(target);
            } else {
                throw new Exception("invalid action");
            }
        }

        // if all goes well, redirect back to the last page
        HttpSession session = req.getSession(false);
        SavedRequest savedRequest =
            (SavedRequest) session.getAttribute(AbstractProcessingFilter
                                                .ACEGI_SAVED_REQUEST_KEY);
        if (savedRequest != null) {
            res.sendRedirect(savedRequest.getFullRequestUrl());
            return null;
        } else {
            return super.onSubmit(req, res, command, errors);
        }
    }
}