package com.trailmagic.image.ui;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.trailmagic.image.*;
import com.trailmagic.user.*;

public class ImageController extends AbstractController {
    private static final String IMAGE_FACTORY_BEAN = "imageFactory";
    private static final String IMAGE_USERS_VIEW = "imageUsers";
    private static final String IMAGE_DISPLAY_VIEW = "imageDisplay";
    private String m_controllerPath;

    public String getControllerPath() {
        return m_controllerPath;
    }

    public void setControllerPath(String path) {
        m_controllerPath = path;
    }

    public ModelAndView handleRequestInternal(HttpServletRequest req,
                                              HttpServletResponse res)
        throws Exception {

        UrlPathHelper pathHelper = new UrlPathHelper();
        String myPath = pathHelper.getLookupPathForRequest(req);
        System.err.println("Path within servlet mapping: " + myPath);
        System.err.println("Lookup path: " +
                           pathHelper.getLookupPathForRequest(req));
        System.err.println("Path within application: " +
                           pathHelper.getPathWithinApplication(req));
        System.err.println("Controller Path: " + m_controllerPath);
        myPath = myPath.substring(m_controllerPath.length());
        StringTokenizer pathTokens = new StringTokenizer(myPath, "/");
        int numTokens = pathTokens.countTokens();

        Map<String,Object> model =
            new HashMap<String,Object>();
        ImageFactory imageFactory =
            (ImageFactory)getApplicationContext().getBean(IMAGE_FACTORY_BEAN);

        // got no args: show random images
        if ( !pathTokens.hasMoreTokens() ) {
            // List users with albums
            model.put("images", imageFactory.getAll());
            return new ModelAndView(IMAGE_USERS_VIEW, model);
        }

        String selector = pathTokens.nextToken();
        System.err.println("Selector: " + selector);
        if ( "by-id".equals(selector.trim()) ) {
            long imageId = Long.parseLong(pathTokens.nextToken());
            model.put("image", imageFactory.getById(imageId));
            return new ModelAndView(IMAGE_DISPLAY_VIEW, model);
        }
        // redirect to the top if it's an invalid request
        // I guess this should really be a 404
        System.err.println("Redirect: " + req.getContextPath() +
                           req.getServletPath() + getControllerPath());
        res.sendRedirect(req.getContextPath() + req.getServletPath() +
                         getControllerPath());
        return null;
    }
    /*
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
    */
}
