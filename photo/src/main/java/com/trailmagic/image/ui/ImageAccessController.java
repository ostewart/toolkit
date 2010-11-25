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
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageAccessController extends SimpleFormController {
    private ImageSecurityService imageSecurityService;
    private ImageRepository imageRepository;
    private ImageGroupRepository imageGroupRepository;
    private RequestCache requestCache;

    private static final String MAKE_PUBLIC_ACTION = "makePublic";
    private static final String MAKE_PRIVATE_ACTION = "makePrivate";
    private static final String MAKE_FRAMES_PUBLIC_ACTION = "makeImagesPublic";
    private static final String MAKE_FRAMES_PRIVATE_ACTION = "makeImagesPrivate";
    private static final String IMAGE_TARGET = "image";
    private static final String IMAGE_GROUP_TARGET = "imageGroup";

    public ImageAccessController(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

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
                imageSecurityService.makeImagesPublic(target);
            } else if (MAKE_FRAMES_PRIVATE_ACTION.equals(bean.getAction())) {
                imageSecurityService.makeImagesPrivate(target);
            } else {
                throw new Exception("invalid action");
            }
        }

        // if all goes well, redirect back to the last page
        final SavedRequest savedRequest = requestCache.getRequest(req, res);
        if (savedRequest != null) {
            res.sendRedirect(savedRequest.getRedirectUrl());
            return null;
        } else {
            return super.onSubmit(req, res, command, errors);
        }
    }
}