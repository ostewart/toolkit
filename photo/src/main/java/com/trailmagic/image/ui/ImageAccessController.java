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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/changePermission")
public class ImageAccessController {
    private ImageSecurityService imageSecurityService;
    private ImageRepository imageRepository;
    private ImageGroupRepository imageGroupRepository;
    private RequestCache requestCache;

    @Autowired
    public ImageAccessController(RequestCache requestCache, ImageSecurityService imageSecurityService,
                                 ImageRepository imageRepository, ImageGroupRepository imageGroupRepository) {
        this.requestCache = requestCache;
        this.imageSecurityService = imageSecurityService;
        this.imageRepository = imageRepository;
        this.imageGroupRepository = imageGroupRepository;
    }

    @RequestMapping(method = RequestMethod.POST, params = {"target=image", "action=makePublic"})
    public String makeImagePublic(@RequestParam("id") Long imageId,
                                HttpServletRequest request, HttpServletResponse response) throws IOException {
        Image target = imageRepository.getById(imageId);
        imageSecurityService.makePublic(target);

        return redirectForSuccess(request, response);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"target=image", "action=makePrivate"})
    public String makeImagePrivate(@RequestParam("id") Long imageId,
                                 HttpServletRequest request, HttpServletResponse response) throws IOException {
        imageSecurityService.makePrivate(imageRepository.getById(imageId));

        return redirectForSuccess(request, response);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"target=imageGroup", "action=makePublic"})
    public String makeImageGroupPublic(@RequestParam("id") Long imageGroupId,
                                     HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageGroup target = imageGroupRepository.getByIdWithFrames(imageGroupId);
        imageSecurityService.makePublic(target);

        return redirectForSuccess(request, response);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"target=imageGroup", "action=makePrivate"})
    public String makeImageGroupPrivate(@RequestParam("id") Long imageGroupId,
                                      HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageGroup target = imageGroupRepository.getByIdWithFrames(imageGroupId);
        imageSecurityService.makePrivate(target);

        return redirectForSuccess(request, response);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"target=imageGroup", "action=makeImagesPublic"})
    public String makeImageGroupImagesPublic(@RequestParam("id") Long imageGroupId,
                                           HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageGroup target = imageGroupRepository.getByIdWithFrames(imageGroupId);
        imageSecurityService.makeImagesPublic(target);

        return redirectForSuccess(request, response);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"target=imageGroup", "action=makeImagesPrivate"})
    public String makeImageGroupImagesPrivate(@RequestParam("id") Long imageGroupId,
                                            HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageGroup target = imageGroupRepository.getByIdWithFrames(imageGroupId);
        imageSecurityService.makeImagesPrivate(target);

        return redirectForSuccess(request, response);
    }

    private String redirectForSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            response.sendRedirect(savedRequest.getRedirectUrl());
            return null;
        }
        return "redirect:";
    }
}