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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
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

        Map<String,Object> model = new HashMap<String,Object>();

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
            if (mf.getName() != null) {
                model.put(InputStreamView.CONTENT_DISPOSITION_KEY,
                          "inline; filename=" + mf.getName() + ";");
            }
            return new ModelAndView(new InputStreamView(), model);
        }

        return null;
    }
}
