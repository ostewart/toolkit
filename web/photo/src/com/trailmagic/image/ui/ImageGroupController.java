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
import com.trailmagic.image.ImageGroupFactory;
import com.trailmagic.image.security.ImageSecurityFactory;
import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.UrlPathHelper;

/**
 * to add a new group type, simply add a new mapping to this controller as
 * "/<type-name>s/**" in the handlerMapping in images-servlet.xml
 **/
public class ImageGroupController implements Controller, ApplicationContextAware, InitializingBean {
    private static final String USER_FACTORY_BEAN = "userFactory";
    private static final String IMG_GROUP_FACTORY_BEAN = "imageGroupFactory";
    private static final String LIST_VIEW = "imageGroupList";
    private static final String USERS_VIEW = "imageGroupUsers";
    private static final String IMG_GROUP_VIEW = "imageGroup";
    private static final String IMAGE_DISPLAY_VIEW = "imageDisplay";
    private static final int DIR_TOKENS = 3;

    private static Logger s_log =
        Logger.getLogger(ImageGroupController.class);

    private ApplicationContext m_appContext;
    private ImageSecurityFactory m_imageSecurityFactory;
    private PortResolver m_portResolver = new PortResolverImpl();

    public void setApplicationContext(ApplicationContext appContext)
        throws BeansException {

        m_appContext = appContext;
    }

    public void setImageSecurityFactory(ImageSecurityFactory factory) {
        m_imageSecurityFactory = factory;
    }


    public void afterPropertiesSet() throws Exception {
        Assert.notNull(m_imageSecurityFactory);
        Assert.notNull(m_appContext);
    }

    public ModelAndView handleRequest(HttpServletRequest req,
                                      HttpServletResponse res)
        throws Exception {

        // make sure caches don't get in the way of selective content
        // to authorized users
        res.setHeader("Cache-control", "private");
        // save the request in case someone clicks the sign in link
        SavedRequest savedRequest =
            new SavedRequest(req, m_portResolver);
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
        ImageGroupFactory imgGroupFactory =
            (ImageGroupFactory)m_appContext.getBean(IMG_GROUP_FACTORY_BEAN);
        UserFactory userFactory =
            (UserFactory)m_appContext.getBean(USER_FACTORY_BEAN);

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
            model.put("owners", imgGroupFactory.getOwnersByType(groupType));
            return new ModelAndView(USERS_VIEW, model);
        }

        // process first (owner) arg
        String ownerName = pathTokens.nextToken();
        User owner = userFactory.getByScreenName(ownerName);
        // check for null!
        model.put("owner", owner);

        // got user arg: show his/her groups
        if ( !pathTokens.hasMoreTokens() ) {
            List<ImageGroup> imageGroups =
                imgGroupFactory.getByOwnerScreenNameAndType(ownerName,
                                                            groupType);

            List<ImageGroup> filteredGroups = new ArrayList<ImageGroup>();

            // show a preview image
            Map<ImageGroup,Image> previewImages =
                new HashMap<ImageGroup,Image>();
            Map<ImageGroup,Integer> numImages =
                new HashMap<ImageGroup,Integer>();
            for (ImageGroup group : imageGroups) {
                try {
                    // need to get all the frames in order to get an
                    // accurate count of how many we have access to :(
                    SortedSet<ImageFrame> frames = group.getFrames();
                    if (frames.size() > 0) {
                        // if there are no images that we have permission
                        // to see, we'll get an AccessDeniedException
                        previewImages.put(group,
                                          frames.first().getImage());
                        filteredGroups.add(group);
                        numImages.put(group, frames.size());
                    }
                } catch (AccessDeniedException e) {
                    s_log.debug("Access Denied: not including group in "
                                + "collection: " + group);
                }
            }

            // sort the groups by upload date (descending)
            Collections.sort(filteredGroups, new Comparator<ImageGroup>() {
                    public int compare(ImageGroup g1, ImageGroup g2) {
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
            imgGroupFactory.getByOwnerNameAndType(owner, groupName, groupType);
        if (group == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND,
                          "No such " + groupType);
            return null;
        }
        model.put("imageGroup", group);
        model.put("imageGroupIsPublic",
                  m_imageSecurityFactory.isPublic(group));

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
        try {
            long frameId = Long.parseLong(pathTokens.nextToken().trim());
            ImageFrame frame =
                imgGroupFactory.getImageFrameByImageGroupAndImageId(group,
                                                                    frameId);
            if ( frame == null ) {
                // XXX: pure eeeeeevil
                throw new NumberFormatException("No image found.");
            }
            model.put("frame", frame);
            model.put("image", frame.getImage());
            model.put("imageIsPublic",
                      m_imageSecurityFactory.isPublic(frame.getImage()));
            List<ImageGroup> groupsContainingImage =
                imgGroupFactory.getByImage(frame.getImage());
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
        } catch (NumberFormatException e) {
            throw new JspException("Invalid frame number.");
        }
    }
}
