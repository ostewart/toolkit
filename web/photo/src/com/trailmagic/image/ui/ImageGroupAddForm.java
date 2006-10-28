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

import com.trailmagic.image.ImageGroup;
import com.trailmagic.user.UserFactory;
import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/* Steps:
 * 1: Select an album
 * 2: Select roll from which to add photos
 * 3: Select photo from roll
 * 4: Edit positions in album
 */

public class ImageGroupAddForm extends SimpleFormController {
    private SessionFactory m_sessionFactory;
    private UserFactory m_userFactory;

    public ImageGroupAddForm() {
        setCommandClass(ImageGroup.class);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        m_sessionFactory = sessionFactory;
    }

    public void setUserFactory(UserFactory userFactory) {
        m_userFactory = userFactory;
    }


    protected ModelAndView onSubmit(Object command) throws Exception {
        ImageGroup imgGroup = (ImageGroup)command;
        Session session =
            SessionFactoryUtils.getSession(m_sessionFactory, false);

        imgGroup.setSupergroup(null);
        imgGroup.setSubgroups(new ArrayList<ImageGroup>());
        // TODO: implement multi-user
        imgGroup.setOwner(m_userFactory.getByScreenName("oliver"));
        // TODO: maybe implement new roll
        imgGroup.setType("album");
        session.save(imgGroup);

        return new ModelAndView(getSuccessView(), "imageGroup", imgGroup);
    }
}
