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
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.web.util.ImageRequestInfo;
import com.trailmagic.web.util.WebRequestTools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.AccessDeniedException;
import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * to add a new group type, simply add a new mapping to this controller as
 * "/<type-name>s/**" in the handlerMapping in images-servlet.xml
 **/
public class ImageGroupDispatcherController implements Controller {
    private static final String LIST_VIEW = "imageGroupList";
    private static final String USERS_VIEW = "imageGroupUsers";

    private static Logger log =
        Logger.getLogger(ImageGroupDispatcherController.class);

    private ImageGroupRepository imageGroupRepository;
    private ImageDisplayController imageDisplayController;
    private ImageGroupDisplayController imageGroupDisplayController;
    private UserRepository userRepository;
    private WebRequestTools webRequestTools;

    public ImageGroupDispatcherController(ImageGroupDisplayController imageGroupDisplayController,
                                          ImageGroupRepository imageGroupRepository,
                                          ImageDisplayController imageDisplayController,
                                          UserRepository userRepository,
                                          WebRequestTools webRequestTools) {
        super();
        this.imageGroupDisplayController = imageGroupDisplayController;
        this.imageGroupRepository = imageGroupRepository;
        this.userRepository = userRepository;
        this.webRequestTools = webRequestTools;
        this.imageDisplayController = imageDisplayController;
    }

    public ModelAndView handleRequest(HttpServletRequest req,
                                      HttpServletResponse res)
        throws Exception {

        // make sure caches don't get in the way of selective content
        // to authorized users
        res.setHeader("Cache-control", "private");
        // save the request in case someone clicks the sign in link
        webRequestTools.saveCurrentRequest(req);
        
        /*
         * Model Requirements:
         * user: currently logged in user
         * imageGroup: the current ImageGroup
         * frame: the current ImageFrame
         * prev: the previous ImageFrame, or null
         * next: the next ImageFrame, or null
         *
         * the url must be based at / within the context, as we're using
         * the first element as the group type (could get around this with
         * a skipTokens arg
         */

        ImageRequestInfo iri = webRequestTools.getImageRequestInfo(req);
        
        // redirect if necessary unless we have an image (not a directory url)
        if (iri.getImageId() == null) {
            if (WebSupport.handleDirectoryUrlRedirect(req, res)) {
                // stop processing if we redirected
                return null;
            }
        }
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("thisRequestUrl", webRequestTools.getFullRequestUrl(req));
        
        model.put("groupType", iri.getImageGroupType());

        
        // dispatch according to /[groupType]/[screenName]/[imageGroupName]/[imageId]
        if (iri.getScreenName() == null) {
            return handleListUsers(iri.getImageGroupType(), model);   
        }

        if (iri.getImageGroupName() == null) {
            return handleGroupList(iri.getImageGroupType(), iri.getScreenName(), model);
        }

        // got user and group args: show one group
        if (iri.getImageId() == null) {
//            return handleDisplayGroup(model, iri.getImageGroupType(),
//                                      iri.getScreenName(), iri.getImageGroupName());
            return imageGroupDisplayController.handleDisplayGroup(req, new ModelMap(model));
        }
        
//        return handleDisplayFrame(iri.getImageGroupName(), iri.getImageId(),
//                                  model, isEditMode(req), req.getServletPath());
        return imageDisplayController.handleRequest(req, res);
    }
    
    private ModelAndView handleListUsers(ImageGroup.Type groupType,
                                         Map<String, Object> model) {
        model.put("owners", imageGroupRepository.getOwnersByType(groupType));
        return new ModelAndView(USERS_VIEW, model);
    }

    private ModelAndView handleGroupList(ImageGroup.Type groupType,
                                         String ownerName,
                                         Map<String, Object> model) {
        List<ImageGroup> imageGroups =
            imageGroupRepository.getByOwnerScreenNameAndType(ownerName,
                                                             groupType);

        List<ImageGroup> filteredGroups = new ArrayList<ImageGroup>();

        // show a preview image
        Map<ImageGroup,Image> previewImages =
            new HashMap<ImageGroup,Image>();
        Map<ImageGroup,Integer> numImages =
            new HashMap<ImageGroup,Integer>();
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
