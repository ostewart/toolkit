package com.trailmagic.image.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate.SessionFactoryUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

import com.trailmagic.image.*;
import com.trailmagic.user.*;

public class ImageManifestationController extends AbstractController {
    private static final String IMF_BEAN = "imageManifestationFactory";
    private SessionFactory m_sessionFactory;
    private String m_controllerPath;

    private static Logger s_logger =
        Logger.getLogger(ImageManifestationController.class);

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
        s_logger.debug("Controller Path: " + m_controllerPath);
        myPath = myPath.substring(m_controllerPath.length());
        StringTokenizer pathTokens = new StringTokenizer(myPath, "/");
        int numTokens = pathTokens.countTokens();

        Map model = new HashMap();

        String method = pathTokens.nextToken();

        ImageManifestationFactory imFactory =
            (ImageManifestationFactory)getApplicationContext()
            .getBean(IMF_BEAN);
        if ( method.equals("by-id") ) {
            HeavyImageManifestation mf =
                imFactory.getHeavyById(Long.parseLong(pathTokens.nextToken()));

            java.io.InputStream dataStream = mf.getData().getBinaryStream();
            s_logger.debug("Passing manifestation data stream to view (type: "
                           + dataStream.getClass() + ")");
            model.put(InputStreamView.STREAM_KEY, dataStream);
            model.put(InputStreamView.CONTENT_TYPE_KEY, mf.getFormat());
            return new ModelAndView(new InputStreamView(), model);
        }

        return null;
    }
}
