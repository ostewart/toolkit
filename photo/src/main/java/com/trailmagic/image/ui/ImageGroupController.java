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
import com.trailmagic.image.security.ImageSecurityFactory;
import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.acegisecurity.ui.savedrequest.SavedRequest;
import org.acegisecurity.util.PortResolver;
import org.acegisecurity.util.PortResolverImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.UrlPathHelper;

/**
 * to add a new group type, simply add a new mapping to this controller as
 * "/<type-name>s/**" in the handlerMapping in images-servlet.xml
 **/
public class ImageGroupController implements Controller {
    private static final String LIST_VIEW = "imageGroupList";
    private static final String USERS_VIEW = "imageGroupUsers";
    private static final String IMG_GROUP_VIEW = "imageGroup";
    private static final String IMAGE_DISPLAY_VIEW = "imageDisplay";
    private static final int DIR_TOKENS = 3;

    private static Logger s_log =
        Logger.getLogger(ImageGroupController.class);

    private ImageSecurityFactory imageSecurityFactory;
    private PortResolver portResolver = new PortResolverImpl();
    private ImageGroupRepository imageGroupRepository;
    private UserFactory userFactory;

    @Required
    public void setImageGroupRepository(ImageGroupRepository imageGroupRepository) {
        this.imageGroupRepository = imageGroupRepository;
    }

    @Required
    public void setUserFactory(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    @Required
    public void setImageSecurityFactory(ImageSecurityFactory imageSecurityFactory) {
        this.imageSecurityFactory = imageSecurityFactory;
    }


    public ModelAndView handleRequest(HttpServletRequest req,
                                      HttpServletResponse res)
        throws Exception {

        // make sure caches don't get in the way of selective content
        // to authorized users
        res.setHeader("Cache-control", "private");
        // save the request in case someone clicks the sign in link
        SavedRequest savedRequest =
            new SavedRequest(req, portResolver);
        if (s_log.isDebugEnabled()) {
            s_log.debug("SavedRequest added to Session: " + savedRequest);
        }
        // Store the HTTP request itself. Used by AbstractProcessingFilter
        // for redirection after successful authentication (SEC-29)
        req.getSession().setAttribute(AbstractProcessingFilter
                                      .ACEGI_SAVED_REQUEST_KEY, savedRequest);

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

        UrlPathHelper pathHelper = new UrlPathHelper();
        String myPath = pathHelper.getLookupPathForRequest(req);
        s_log.debug("Lookup path: " +
                    pathHelper.getLookupPathForRequest(req));
        StringTokenizer pathTokens = new StringTokenizer(myPath, "/");

        // if this is a "directory" request, make sure there's a trailing
        // slash
        if (pathTokens.countTokens() <= DIR_TOKENS) {
            if (WebSupport.handleDirectoryUrlRedirect(req, res)) {
                // stop processing if we redirected
                return new ModelAndView();
            }
        }

        String groupTypeString = pathTokens.nextToken();
        ImageGroup.Type groupType;
        Map<String,Object> model = new HashMap<String,Object>();

        // depluralize
        groupTypeString = groupTypeString.substring(0, groupTypeString.length() - 1);
        groupType = ImageGroup.Type.fromString(groupTypeString);
        String groupTypeDisplay = groupTypeString.substring(0, 1).toUpperCase()
            + groupTypeString.substring(1);
        model.put("groupTypeDisplay", groupTypeDisplay);
        model.put("groupType", groupTypeString);

        // got no args: show users
        if ( !pathTokens.hasMoreTokens() ) {
            model.put("owners", imageGroupRepository.getOwnersByType(groupType));
            return new ModelAndView(USERS_VIEW, model);
        }

        // process first (owner) arg
        String ownerName = pathTokens.nextToken();
        User owner = userFactory.getByScreenName(ownerName);
        // XXX: handle this with an exception instead
        if (owner == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND,
                          "No such user");
            return null;
        }
        model.put("owner", owner);

        // got user arg: show his/her groups
        if ( !pathTokens.hasMoreTokens() ) {
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
                    s_log.debug("Access Denied: not including group in "
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

            model.put("numImages", numImages);
            model.put("imageGroups", filteredGroups);
            model.put("previewImages", previewImages);

            return new ModelAndView(LIST_VIEW, model);
        }

        // process second (group name) arg
        String groupName = pathTokens.nextToken();
        ImageGroup group =
            imageGroupRepository.getByOwnerNameAndTypeWithFrames(owner,
                                                                 groupName,
                                                                 groupType);
        if (group == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND,
                          "No such " + groupType);
            return null;
        }
        model.put("imageGroup", group);
        model.put("imageGroupIsPublic",
                  imageSecurityFactory.isPublic(group));

        SortedSet<ImageFrame> frames = group.getFrames();
        s_log.debug("Frames contains " + frames.size() + " items.");
        model.put("frames", frames);
        /*
        // XXX: This should be fixed
        Iterator iter = frames.iterator();
        while (iter.hasNext()) {
            iter.next();
        }*/
        // XXX: end kludge

        // got user and group args: show one group
        if (!pathTokens.hasMoreTokens()) {
            return new ModelAndView(IMG_GROUP_VIEW, model);
        }

        // process third (frame number) arg
        long frameId;
        try {
            frameId = Long.parseLong(pathTokens.nextToken().trim());
        } catch (NumberFormatException e) {
            throw new JspException("Invalid frame number.");
        }

        ImageFrame frame =
            imageGroupRepository.getImageFrameByImageGroupAndImageId(group,
                                                                     frameId);
        if ( frame == null ) {
            // XXX: we could really do better than this
            throw new JspException("No image found with ID: " + frameId);
        }

        model.put("frame", frame);
        model.put("image", frame.getImage());
        model.put("imageIsPublic",
                  imageSecurityFactory.isPublic(frame.getImage()));
        List<ImageGroup> groupsContainingImage =
            imageGroupRepository.getByImage(frame.getImage());
        List<ImageGroup> otherGroups = new ArrayList<ImageGroup>();
        Iterator<ImageGroup> iter = groupsContainingImage.iterator();
        ImageGroup containingGroup;
        while (iter.hasNext()) {
            containingGroup = iter.next();
            if ( !group.equals(containingGroup) ) {
                otherGroups.add(containingGroup);
            }
        }
        model.put("groupsContainingImage", otherGroups);

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
}
