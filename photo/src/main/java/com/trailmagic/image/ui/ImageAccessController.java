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
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.ImageRepository;
import com.trailmagic.image.security.ImageSecurityService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.acegisecurity.ui.savedrequest.SavedRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ImageAccessController extends SimpleFormController {
    private ImageSecurityService imageSecurityService;
    private ImageRepository imageRepository;
    private ImageGroupRepository imageGroupRepository;

    private static final String MAKE_PUBLIC_ACTION = "makePublic";
    private static final String MAKE_PRIVATE_ACTION = "makePrivate";
    private static final String MAKE_FRAMES_PUBLIC_ACTION = "makeFramesPublic";
    private static final String MAKE_FRAMES_PRIVATE_ACTION = "makeFramesPrivate";
    private static final String IMAGE_TARGET = "image";
    private static final String IMAGE_GROUP_TARGET = "imageGroup";

    public void setImageSecurityService(ImageSecurityService imageSecurityService) {
        this.imageSecurityService = imageSecurityService;
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

        // save the request for later...if this takes a long time, maybe
        // the session could time out
        final HttpSession session = req.getSession(false);
        final SavedRequest savedRequest;
        if (session == null) {
            savedRequest = null;
        } else {
            savedRequest =
                (SavedRequest) session.getAttribute(AbstractProcessingFilter
                                                    .ACEGI_SAVED_REQUEST_KEY);
        }

        if (bean == null) {
            throw new Exception("null command");
        }

        if (IMAGE_TARGET.equals(bean.getTarget())) {
            Image target = imageRepository.getById(bean.getId());

            if (MAKE_PUBLIC_ACTION.equals(bean.getAction())) {
                imageSecurityService.makePublic(target);
            } else if (MAKE_PRIVATE_ACTION.equals(bean.getAction())) {
                imageSecurityService.makePrivate(target);
            } else {
                throw new Exception("Invalid action");
            }
        } else if (IMAGE_GROUP_TARGET.equals(bean.getTarget())) {
            ImageGroup target = imageGroupRepository.getByIdWithFrames(bean.getId());

            if (MAKE_PUBLIC_ACTION.equals(bean.getAction())) {
                imageSecurityService.makePublic(target);
            } else if (MAKE_PRIVATE_ACTION.equals(bean.getAction())) {
                imageSecurityService.makePrivate(target);
            } else if (MAKE_FRAMES_PUBLIC_ACTION.equals(bean.getAction())) {
                imageSecurityService.makeFramesPublic(target);
            } else if (MAKE_FRAMES_PRIVATE_ACTION.equals(bean.getAction())) {
                imageSecurityService.makeFramesPrivate(target);
            } else {
                throw new Exception("invalid action");
            }
        }

        // if all goes well, redirect back to the last page
        if (savedRequest != null) {
            res.sendRedirect(savedRequest.getFullRequestUrl());
            return null;
        } else {
            return super.onSubmit(req, res, command, errors);
        }
    }
}