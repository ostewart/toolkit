package com.trailmagic.image.ui;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import net.sf.hibernate.Session;
import java.util.StringTokenizer;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.SessionFactory;
import java.util.SortedSet;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import com.trailmagic.image.*;
import com.trailmagic.user.*;

public class AlbumController implements Controller {
    private SessionFactory m_sessionFactory;

    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
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
        Session session = SessionFactoryUtils.getSession(m_sessionFactory,
                                                         false);
        
        UrlPathHelper pathHelper = new UrlPathHelper();
        String myPath = pathHelper.getPathWithinServletMapping(req);
        StringTokenizer pathTokens = new StringTokenizer(myPath, "/");
        String appName = pathTokens.nextToken();
        
        Map model = new HashMap();

        // got no args: show users        
        if ( !pathTokens.hasMoreTokens() ) {
            // List users with albums
            model.put("owners", getUsersWithAlbums(session));
            return new ModelAndView("/album-users.jsp", model);
        }

        // process first (owner) arg
        String ownerName = pathTokens.nextToken();
        User owner = getUserByScreenName(session, ownerName);
        model.put("owner", owner);

        // got user arg: show his/her albums
        if ( !pathTokens.hasMoreTokens() ) {
            model.put("albums", getAlbumsForUser(session, ownerName));
            return new ModelAndView("/album-list.jsp", model);
        }

        // process second (album name) arg
        String albumName = pathTokens.nextToken();
        ImageGroup album = getAlbumByOwnerAndName(session, owner, albumName);
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
            return new ModelAndView("/album.jsp", model);
        }

        // process third (frame number) arg
        try {
            long frameId = Long.parseLong(pathTokens.nextToken().trim());
            ImageFrame frame = getImageFrameByAlbumAndImageId(session, album,
                                                              frameId);
            if ( frame == null ) {
                // XXX: pure eeeeeevil
                throw new NumberFormatException("No image found.");
            }
            model.put("frame", frame);
            model.put("image", frame.getImage());

            SortedSet tmpSet = frames.headSet(frame);
            /*
            Iterator iter = tmpSet.iterator();
            iter.next();
            if ( iter.hasNext() ) {
                ImageFrame prevFrame = (ImageFrame)iter.next();
            }
            */

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
            return new ModelAndView("/image-display.jsp", model);
        } catch (NumberFormatException e) {
            throw new JspException("Invalid frame number.");
        }
    }

    private List getUsersWithAlbums(Session session) {
        try {
            Query query =
                /*
                session.createQuery("SELECT user " +
                                    "FROM com.trailmagic.user.User " +
                                    "AS user " +
                                    "inner join " +
                                    "com.trailmagic.image.ImageFrame " +
                                    " AS album WHERE album.type = 'album' ");
                                    //                                    "AND album.owner = user");
                                    */
                session.createQuery("select distinct album.owner " +
                                    "from com.trailmagic.image.ImageGroup " +
                                    "as album inner join album.owner " +
                                    "where album.type = 'album'");

            return query.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    private List getAlbumsForUser(Session session, String screenName) {
        try {
            Query query =
                session.createQuery("select album from com.trailmagic.image.ImageGroup" +
                                    " as album inner join album.owner as owner " +
                                    "where album.type = 'album' " +
                                    "and owner.screenName = :screenName");
            query.setString("screenName", screenName);
            return query.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    private User getUserByScreenName(Session session, String screenName) {
        try {
            Query query =
                session.createQuery("select from com.trailmagic.user.User " +
                                    "as user " +
                                    "where user.screenName = :screenName");
            query.setString("screenName", screenName);

            return (User)query.uniqueResult();

        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }

    }
    /*
select elements(grp.frames) from com.trailmagic.image.ImageGroup grp join grp.frames frame where grp.owner.screenName = 'oliver' AND grp.name = 'test-album' AND frame.image.id = 3
    */
    private ImageFrame getImageFrameByAlbumAndImageId(Session session,
                                                      ImageGroup album,
                                                      long imageId) {

        try {
            /*
            Query query =
                session.createQuery("select elements(grp.frames) " +
                                    "from com.trailmagic.image.ImageGroup grp"+
                                    " join grp.frames frame " +
                                    "where grp = :album " +
                                    "AND frame.image.id = :imageId");
            query.setEntity("album", album);
            query.setLong("imageId", imageId);
            return (ImageFrame)query.uniqueResult();
            */

            
            Collection frames =
                session.filter(album.getFrames(),
                               "where this.image.id = ?",
                               new Long(imageId),
                               new net.sf.hibernate.type.LongType());
            // XXX: is this really the right way to do this?
            Iterator iter = frames.iterator();
            if (iter.hasNext()) {
                return (ImageFrame)iter.next();
            } else {
                return null;
            }
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    private ImageGroup getAlbumByOwnerAndName(Session session,
                                              User owner,
                                              String albumName) {

        try {
            Query query =
                session.createQuery("select from com.trailmagic.image.ImageGroup " +
                                    "AS grp where grp.owner = :owner AND " +
                                    "grp.name = :albumName " +
                                    "AND grp.type = 'album'");
            query.setEntity("owner", owner);
            query.setString("albumName", albumName);
            return (ImageGroup)query.uniqueResult();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }
}
