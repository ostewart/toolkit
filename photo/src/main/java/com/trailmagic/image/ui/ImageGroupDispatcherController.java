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
import com.trailmagic.image.ImageGroupType;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.web.util.WebRequestTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ImageGroupDispatcherController {
    private static final String LIST_VIEW = "imageGroupList";
    private static final String USERS_VIEW = "imageGroupUsers";

    private static Logger log =
            LoggerFactory.getLogger(ImageGroupDispatcherController.class);

    private ImageGroupRepository imageGroupRepository;
    private UserRepository userRepository;
    private WebRequestTools webRequestTools;

    @Autowired
    public ImageGroupDispatcherController(ImageGroupRepository imageGroupRepository,
                                          UserRepository userRepository,
                                          WebRequestTools webRequestTools) {
        super();
        this.imageGroupRepository = imageGroupRepository;
        this.userRepository = userRepository;
        this.webRequestTools = webRequestTools;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ImageGroupType.class, new ImageGroupTypeUrlComponentPropertyEditor());
    }

    @RequestMapping("/albums")
    public ModelAndView handleAlbumsRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
        return preHandleListUsers(req, res, ImageGroupType.ALBUM);
    }

    @RequestMapping("/rolls")
    public ModelAndView handleRollsRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
        return preHandleListUsers(req, res, ImageGroupType.ROLL);
    }

    private ModelAndView preHandleListUsers(HttpServletRequest req, HttpServletResponse res, final ImageGroupType groupType) throws IOException {
        if (webRequestTools.preHandlingFails(req, res, true)) return null;

        Map<String, Object> model = initialModel(req, groupType);


        return handleListUsers(groupType, model);
    }

    private Map<String, Object> initialModel(HttpServletRequest req, ImageGroupType groupType) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("thisRequestUrl", webRequestTools.getFullRequestUrl(req));
        model.put("groupType", groupType);
        return model;
    }

    @RequestMapping("/{groupType}/{screenName}")
    public ModelAndView handleGroupList(HttpServletRequest request, HttpServletResponse response,
                                        @PathVariable("groupType") ImageGroupType groupType,
                                        @PathVariable("screenName") String screenName) throws IOException {
        if (webRequestTools.preHandlingFails(request, response, true)) return null;

        return handleGroupList(groupType, screenName, initialModel(request, groupType));
    }

    private ModelAndView handleListUsers(ImageGroupType groupType,
                                         Map<String, Object> model) {
        model.put("owners", imageGroupRepository.getOwnersByType(groupType));
        return new ModelAndView(USERS_VIEW, model);
    }

    private ModelAndView handleGroupList(ImageGroupType groupType,
                                         String ownerName,
                                         Map<String, Object> model) {
        List<ImageGroup> imageGroups =
                imageGroupRepository.getByOwnerScreenNameAndType(ownerName,
                                                                 groupType);

        List<ImageGroup> filteredGroups = new ArrayList<ImageGroup>();

        // show a preview image
        Map<ImageGroup, Image> previewImages =
                new HashMap<ImageGroup, Image>();
        Map<ImageGroup, Integer> numImages =
                new HashMap<ImageGroup, Integer>();
        for (ImageGroup group : imageGroups) {
            try {
                filteredGroups.add(group);
                previewImages.put(group, group.getPreviewImage());
                numImages.put(group,
                              imageGroupRepository
                                      .getPublicFrameCount(group));
            } catch (AccessDeniedException e) {
                log.debug("Access Denied: not including group in "
                          + "collection: " + group);
            }
        }

        // sort the groups by upload date (descending)
        Collections.sort(filteredGroups, new ImageGroupUploadDateDescendingComparator());

        User owner = userRepository.getByScreenName(ownerName);
        model.put("owner", owner);

        model.put("groupType", groupType);
        model.put("numImages", numImages);
        model.put("imageGroups", filteredGroups);
        model.put("previewImages", previewImages);

        return new ModelAndView(LIST_VIEW, model);
    }

    private static class ImageGroupUploadDateDescendingComparator implements Comparator<ImageGroup> {
        public int compare(ImageGroup g1, ImageGroup g2) {
            Date g1Date = g1.getUploadDate();
            Date g2Date = g2.getUploadDate();
            if (g2Date == null && g1Date == null) {
                return 0;
            }

            if (g2Date == null) {
                return 1;
            }
            if (g1Date == null) {
                return -1;
            }

            return g2.getUploadDate()
                    .compareTo(g1.getUploadDate());
        }
    }
}
