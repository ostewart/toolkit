package com.trailmagic.image.ui;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import java.util.StringTokenizer;
import java.util.SortedSet;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.BeansException;

import com.trailmagic.image.*;
import com.trailmagic.user.*;

public class AlbumController implements Controller, ApplicationContextAware {
    private static final String USER_FACTORY_BEAN = "userFactory";
    private static final String IMG_GROUP_FACTORY_BEAN = "imageGroupFactory";
    private static final String ALBUM_LIST_VIEW = "albumList";
    private static final String ALBUM_USERS_VIEW = "albumUsers";
    private static final String IMAGE_DISPLAY_VIEW = "imageDisplay";
    private static final String ALBUM_VIEW = "album";
    
    private ApplicationContext m_appContext;
    private String m_controllerPath;

    public String getControllerPath() {
        return m_controllerPath;
    }

    public void setControllerPath(String path) {
        m_controllerPath = path;
    }
        
    public void setApplicationContext(ApplicationContext appContext)
        throws BeansException {

        m_appContext = appContext;
    }
        
    public ModelAndView handleRequest(HttpServletRequest req,
                                      HttpServletResponse res)
        throws Exception {
        /*
         * Model Requirements:
         * user: currently logged in user
         * album: the current album (ImageGroup of type album)
         * frame: the current ImageFrame
         * prev: the previous ImageFrame, or null
         * next: the next ImageFrame, or null
         */
        ImageGroupFactory imgGroupFactory =
            (ImageGroupFactory)m_appContext.getBean(IMG_GROUP_FACTORY_BEAN);
        UserFactory userFactory =
            (UserFactory)m_appContext.getBean(USER_FACTORY_BEAN);
        
        UrlPathHelper pathHelper = new UrlPathHelper();
        String myPath = pathHelper.getLookupPathForRequest(req);
        System.err.println("Path within servlet mapping: " + myPath);
        System.err.println("Lookup path: " +
                           pathHelper.getLookupPathForRequest(req));
        // cut off the controller part of the URL
        myPath = myPath.substring(m_controllerPath.length());
        StringTokenizer pathTokens = new StringTokenizer(myPath, "/");
        //        String appName = pathTokens.nextToken();
        
        Map model = new HashMap();

        // got no args: show users        
        if ( !pathTokens.hasMoreTokens() ) {
            // List users with albums
            model.put("owners", imgGroupFactory.getAlbumOwners());
            return new ModelAndView(ALBUM_USERS_VIEW, model);
        }

        // process first (owner) arg
        String ownerName = pathTokens.nextToken();
        User owner = userFactory.getByScreenName(ownerName);
        // check for null!
        model.put("owner", owner);

        // got user arg: show his/her albums
        if ( !pathTokens.hasMoreTokens() ) {
            model.put("albums",
                      imgGroupFactory.getAlbumsByOwnerScreenName(ownerName));
            return new ModelAndView(ALBUM_LIST_VIEW, model);
        }

        // process second (album name) arg
        String albumName = pathTokens.nextToken();
        ImageGroup album =
            imgGroupFactory.getAlbumByOwnerAndName(owner, albumName);
        model.put("album", album);

        SortedSet frames = album.getFrames();
        model.put("frames", frames);
        /*
        // XXX: This should be fixed
        Iterator iter = frames.iterator();
        while (iter.hasNext()) {
            iter.next();
        }*/
        // XXX: end kludge

        // got user and album args: show one album
        if (!pathTokens.hasMoreTokens()) {
            return new ModelAndView(ALBUM_VIEW, model);
        }

        // process third (frame number) arg
        try {
            long frameId = Long.parseLong(pathTokens.nextToken().trim());
            ImageFrame frame =
                imgGroupFactory.getImageFrameByImageGroupAndImageId(album,
                                                                    frameId);
            if ( frame == null ) {
                // XXX: pure eeeeeevil
                throw new NumberFormatException("No image found.");
            }
            model.put("frame", frame);
            model.put("image", frame.getImage());

            SortedSet tmpSet = frames.headSet(frame);

//             Iterator iter = tmpSet.iterator();
//             iter.next();
//             if ( iter.hasNext() ) {
//                 ImageFrame prevFrame = (ImageFrame)iter.next();
//             }


            if ( !tmpSet.isEmpty() ) {
                ImageFrame prevFrame = (ImageFrame)tmpSet.last();
                model.put("prevFrame", prevFrame);
            }

            tmpSet = frames.tailSet(frame);
            Iterator iter = tmpSet.iterator();
            iter.next();
            if ( iter.hasNext() ) {
                ImageFrame nextFrame = (ImageFrame)iter.next();
                model.put("nextFrame", nextFrame);
            }
        
            // got user, album, and frame number: show that frame
            return new ModelAndView(IMAGE_DISPLAY_VIEW, model);
        } catch (NumberFormatException e) {
            throw new JspException("Invalid frame number.");
        }
    }
}
