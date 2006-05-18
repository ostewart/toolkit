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
import com.trailmagic.image.ImageFactory;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupFactory;
import com.trailmagic.image.security.ImageSecurityFactory;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.validation.BindException;

public class ImageAccessController extends SimpleFormController {
    private ImageSecurityFactory m_imageSecurityFactory;
    private ImageFactory m_imageFactory;
    private ImageGroupFactory m_imageGroupFactory;
    private static Logger s_log =
        Logger.getLogger(ImageAccessController.class);

    private static final String MAKE_PUBLIC_ACTION = "makePublic";
    private static final String MAKE_PRIVATE_ACTION = "makePrivate";
    private static final String IMAGE_TARGET = "image";
    private static final String IMAGE_GROUP_TARGET = "imageGroup";

    public ImageSecurityFactory getImageSecurityFactory() {
        return m_imageSecurityFactory;
    }

    public void setImageSecurityFactory(ImageSecurityFactory factory) {
        m_imageSecurityFactory = factory;
    }

    public ImageFactory getImageFactory() {
        return m_imageFactory;
    }

    public void setImageFactory(ImageFactory factory) {
        m_imageFactory = factory;
    }

    public ImageGroupFactory getImageGroupFactory() {
        return m_imageGroupFactory;
    }

    public void setImageGroupFactory(ImageGroupFactory factory) {
        m_imageGroupFactory = factory;
    }

    protected void doSubmitAction(Object command) throws Exception {
        ImageAccessBean bean = (ImageAccessBean) command;

        if (bean == null) {
            throw new Exception("null command");
        }

        if (IMAGE_TARGET.equals(bean.getTarget())) {
            Image target = m_imageFactory.getById(bean.getId());

            if (MAKE_PUBLIC_ACTION.equals(bean.getAction())) {
                s_log.info("Making image public" + target.getClass()
                           + "; " + target);
                m_imageSecurityFactory.makePublic(target);
            } else if (MAKE_PRIVATE_ACTION.equals(bean.getAction())) {
                s_log.info("Making image private: " + target.getClass()
                           + "; " + target);
                m_imageSecurityFactory.makePrivate(target);
            } else {
                throw new Exception("Invalid action");
            }
        } else if (IMAGE_GROUP_TARGET.equals(bean.getTarget())) {
            ImageGroup target = m_imageGroupFactory.getById(bean.getId());

            if (MAKE_PUBLIC_ACTION.equals(bean.getAction())) {
                s_log.info("Making " + target.getType() + " public: "
                           + target);
                m_imageSecurityFactory.makePublic(target);
            } else if (MAKE_PRIVATE_ACTION.equals(bean.getAction())) {
                s_log.info("Making " + target.getType() + " private: "
                           + target);
                m_imageSecurityFactory.makePrivate(target);
            } else {
                throw new Exception("invalid action");
            }
        }
    }
}