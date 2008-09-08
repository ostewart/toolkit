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
import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.NoSuchImageGroupException;
import com.trailmagic.image.security.ImageSecurityFactory;
import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import com.trailmagic.web.util.ImageRequestInfo;
import com.trailmagic.web.util.WebRequestTools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acegisecurity.AccessDeniedException;
import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * to add a new group type, simply add a new mapping to this controller as
 * "/<type-name>s/**" in the handlerMapping in images-servlet.xml
 **/
public class ImageGroupController implements Controller {
    private static final String LIST_VIEW = "imageGroupList";
    private static final String USERS_VIEW = "imageGroupUsers";
    private static final String IMG_GROUP_VIEW = "imageGroup";
    private static final String IMAGE_DISPLAY_VIEW = "imageDisplay";

    private static Logger log =
        Logger.getLogger(ImageGroupController.class);

    private ImageSecurityFactory imageSecurityFactory;
    private ImageGroupRepository imageGroupRepository;
    private ImageDisplayController imageDisplayController;
    private UserFactory userFactory;
    private WebRequestTools webRequestTools;

    public ImageGroupController(ImageSecurityFactory imageSecurityFactory,
                                ImageGroupRepository imageGroupRepository,
                                ImageDisplayController imageDisplayController,
                                UserFactory userFactory,
                                WebRequestTools webRequestTools) {
        super();
        this.imageSecurityFactory = imageSecurityFactory;
        this.imageGroupRepository = imageGroupRepository;
        this.userFactory = userFactory;
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
            return handleDisplayGroup(model, iri.getImageGroupType(),
                                      iri.getScreenName(), iri.getImageGroupName());
        }
        
//        return handleDisplayFrame(iri.getImageGroupName(), iri.getImageId(),
//                                  model, isEditMode(req), req.getServletPath());
        return imageDisplayController.handleRequest(req, res);
    }
    
    private boolean isEditMode(HttpServletRequest request) throws ServletException {
        String mode = ServletRequestUtils.getStringParameter(request, "mode");
        if ("edit".equals(mode)) {
            return true;
        } else {
            return false;
        }
    }

    private ModelAndView handleDisplayGroup(Map<String, Object> model,
                                            ImageGroup.Type groupType,
                                            String ownerName,
                                            String groupName)
            throws NoSuchImageGroupException {
        User owner = userFactory.getByScreenName(ownerName);
        ImageGroup group =
            imageGroupRepository.getByOwnerNameAndTypeWithFrames(owner,
                                                                 groupName,
                                                                 groupType);
        model.put("imageGroup", group);
        model.put("imageGroupIsPublic",
                  imageSecurityFactory.isPublic(group));

        SortedSet<ImageFrame> frames = group.getFrames();
        log.debug("Frames contains " + frames.size() + " items.");
        model.put("frames", frames);

        return new ModelAndView(IMG_GROUP_VIEW, model);
    }

    private ModelAndView handleDisplayFrame(String groupName,
                                            long frameId,
                                            Map<String, Object> model,
                                            boolean isEditView,
                                            String requestUri) {
        ImageFrame frame =
            imageGroupRepository.getImageFrameByGroupNameAndImageId(groupName,
                                                                    frameId);
        model.put("requestUri", requestUri);
        model.put("isEditView", isEditView);
        model.put("frame", frame);
        model.put("image", frame.getImage());
        model.put("group", frame.getImageGroup());
        model.put("imageIsPublic",
                  imageSecurityFactory.isPublic(frame.getImage()));
        List<ImageGroup> groupsContainingImage =
            imageGroupRepository.getByImage(frame.getImage());
        List<ImageGroup> otherGroups = new ArrayList<ImageGroup>();
        Iterator<ImageGroup> iter = groupsContainingImage.iterator();
        ImageGroup containingGroup;
        while (iter.hasNext()) {
            containingGroup = iter.next();
            if ( !frame.getImageGroup().equals(containingGroup) ) {
                otherGroups.add(containingGroup);
            }
        }
        model.put("groupsContainingImage", otherGroups);

        SortedSet<ImageFrame> frames = frame.getImageGroup().getFrames();
        SortedSet<ImageFrame> tmpSet = frames.headSet(frame);

//             Iterator iter = tmpSet.iterator();
//             iter.next();
//             if ( iter.hasNext() ) {
//                 ImageFrame prevFrame = (ImageFrame)iter.next();
//             }


        if ( !tmpSet.isEmpty() ) {
            ImageFrame prevFrame = tmpSet.last();
            model.put("prevFrame", prevFrame);
        }

        tmpSet = frames.tailSet(frame);
        Iterator<ImageFrame> framesIter = tmpSet.iterator();
        framesIter.next();
        if ( framesIter.hasNext() ) {
            ImageFrame nextFrame = framesIter.next();
            model.put("nextFrame", nextFrame);
        }

        // got user, group, and frame number: show that frame
        return new ModelAndView(IMAGE_DISPLAY_VIEW, model);
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
        Collections.sort(filteredGroups, new Comparator<ImageGroup>() {
                public int compare(ImageGroup g1, ImageGroup g2) {
                    Date g1Date = g1.getUploadDate();
                    Date g2Date = g2.getUploadDate();
                    if (g2Date == null && g1Date == null) {
                        return 0;
                    }
                    
                    if (g2Date == null) {
                        return 1;
                    }
                    if (g2Date == null) {
                        return -1;
                    }
                    
                    return g2.getUploadDate()
                        .compareTo(g1.getUploadDate());
                }
            });

        User owner = userFactory.getByScreenName(ownerName);
        model.put("owner", owner);
        
        model.put("groupType", groupType);
        model.put("numImages", numImages);
        model.put("imageGroups", filteredGroups);
        model.put("previewImages", previewImages);

        return new ModelAndView(LIST_VIEW, model);
    }
}
