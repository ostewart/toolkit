package com.trailmagic.image.ui;

import java.util.ArrayList;
import java.util.Collection;
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
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.UrlPathHelper;
import com.trailmagic.image.security.ImageSecurityFactory;
import com.trailmagic.image.*;
import com.trailmagic.user.*;

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

    private static Logger s_log =
        Logger.getLogger(ImageGroupController.class);

    private ApplicationContext m_appContext;
    private ImageSecurityFactory m_imageSecurityFactory;

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
        String groupType = pathTokens.nextToken();
        Map<String,Object> model = new HashMap<String,Object>();

        // depluralize
        groupType = groupType.substring(0, groupType.length() - 1);
        String groupTypeDisplay = groupType.substring(0, 1).toUpperCase()
            + groupType.substring(1);
        model.put("groupTypeDisplay", groupTypeDisplay);
        model.put("groupType", groupType);

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
                          "No such image group");
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
