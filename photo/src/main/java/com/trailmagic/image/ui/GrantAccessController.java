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
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

public class GrantAccessController extends SimpleFormController {
    private ImageSecurityService imageSecurityService;
    private ImageRepository imageRepository;
    private ImageGroupRepository imageGroupRepository;
    private UserRepository userRepository;

    private static Logger s_log =
            LoggerFactory.getLogger(GrantAccessController.class);

    private static final String GRANT_ACTION = "grant";
    private static final String MAKE_PUBLIC_ACTION = "makePublic";

    public void setImageSecurityService(ImageSecurityService imageSecurityService) {
        this.imageSecurityService = imageSecurityService;
    }

    public void setImageRepository(ImageRepository factory) {
        this.imageRepository = factory;
    }

    public void setImageGroupRepository(ImageGroupRepository factory) {
        this.imageGroupRepository = factory;
    }

    public void setUserFactory(UserRepository repository) {
        this.userRepository = repository;
    }

    protected ModelAndView showForm(HttpServletRequest req,
                                    HttpServletResponse res,
                                    BindException errors) throws Exception {
        String groupId = req.getParameter("groupId");
        ImageGroup group = imageGroupRepository.getByIdWithFrames(Long.parseLong(groupId));

        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("imageGroupIsPublic", imageSecurityService.isPublic(group));
        model.put("imageGroup", group);
        model.put("groupType", group.getType());
        model.put("groupTypeDisplay", group.getTypeDisplay());
        model.put("owner", group.getOwner());
        model.put("frames", group.getFrames());
        return new ModelAndView(getFormView(), model);
    }

    protected void doSubmitAction(Object command) throws Exception {
        GrantAccessBean bean = (GrantAccessBean) command;

        if (bean == null) {
            throw new Exception("null command");
        }

        List<String> imageIds = bean.getImageIds();
        for (String stringId : imageIds) {
            Long id = Long.parseLong(stringId);
            Image image = imageRepository.getById(id);
            User recipient = userRepository.getByScreenName(bean.getRecipient());
            if (GRANT_ACTION.equals(bean.getAction())) {
                s_log.info("Adding permission " + bean.getMask() + " for " + recipient.getScreenName() + " to Image: " + image);
                throw new UnsupportedOperationException("Need to refactor for new security mask stuff");
//                imageSecurityService.addPermission(image, recipient, bean.getMask());
            } else if (MAKE_PUBLIC_ACTION.equals(bean.getAction())) {
                s_log.info("Making Image public: " + image);
                imageSecurityService.makePublic(image);
            } else {
                throw new IllegalArgumentException("Unknown action");
            }
        }
    }
}