package com.trailmagic.image.ui;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Query;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.HibernateException;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.trailmagic.image.*;
import com.trailmagic.user.*;

public class ImageController extends AbstractController {
    private SessionFactory m_sessionFactory;
    private String m_controllerPath;

    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }

    public String getControllerPath() {
        return m_controllerPath;
    }

    public void setControllerPath(String path) {
        m_controllerPath = path;
    }
        
    public ModelAndView handleRequestInternal(HttpServletRequest req,
                                              HttpServletResponse res)
        throws Exception {

        Session session =
            SessionFactoryUtils.getSession(m_sessionFactory, false);
        UrlPathHelper pathHelper = new UrlPathHelper();
        String myPath = pathHelper.getPathWithinServletMapping(req);
        System.err.println("Controller Path: " + m_controllerPath);
        myPath = myPath.substring(m_controllerPath.length());
        StringTokenizer pathTokens = new StringTokenizer(myPath, "/");
        int numTokens = pathTokens.countTokens();
        
        Map model = new HashMap();

        // got no args: show random images
        if ( !pathTokens.hasMoreTokens() ) {
            // List users with albums
            model.put("images", getAllImages(session));
            return new ModelAndView("/image-users.jsp", model);
        }

        String selector = pathTokens.nextToken();
        System.err.println("Selector: " + selector);
        if ( "by-id".equals(selector.trim()) ) {
            long imageId = Long.parseLong(pathTokens.nextToken());
            model.put("image", getImageById(session, imageId));
            return new ModelAndView("/image-display.jsp", model);
        }
        // redirect to the top if it's an invalid request
        // I guess this should really be a 404
        System.err.println("Redirect: " + req.getContextPath() +
                           req.getServletPath() + getControllerPath());
        res.sendRedirect(req.getContextPath() + req.getServletPath() +
                         getControllerPath());
        return null;
    }

    public List getAllImages(Session session) {
        try {
            Query query =
                session.createQuery("select from com.trailmagic.image.Image");
            return query.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public Image getImageById(Session session, long imageId) {
        try {
            Query query =
                session.createQuery("select from com.trailmagic.image.Image " +
                                    "img where img.id = :imageId");
            query.setLong("imageId", imageId);
            return (Image)query.uniqueResult();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }
}
