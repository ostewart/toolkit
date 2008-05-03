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
package com.trailmagic.image.util;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.trailmagic.user.User;
import com.trailmagic.user.UserFactory;
import com.trailmagic.image.Image;
import com.trailmagic.image.ImageFactory;
import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupFactory;
import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.ImageManifestation;
import com.trailmagic.image.ImageManifestationFactory;
import java.util.List;
import org.hibernate.Session;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


public class FixDimensions {
    private ImageGroupFactory m_imageGroupFactory;
    private HibernateTemplate m_hibernateTemplate;
    private UserFactory m_userFactory;
    private ImageManifestationFactory m_imageManifestationFactory;
    private static Logger s_log = Logger.getLogger(FixDimensions.class);

    private static final String FIX_DIMENSIONS_BEAN = "fixDimensions";

    public void setHibernateTemplate(HibernateTemplate template) {
        m_hibernateTemplate = template;
    }
    public void setImageGroupFactory(ImageGroupFactory factory) {
        m_imageGroupFactory = factory;
    }
    public void setUserFactory(UserFactory factory) {
        m_userFactory = factory;
    }

    public void setImageManifestationFactory(ImageManifestationFactory factory) {
        m_imageManifestationFactory = factory;
    }

    public void fixDimensions(final String ownerName, final String rollName) {
        m_hibernateTemplate.execute(new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    try {
                    User owner = m_userFactory.getByScreenName(ownerName);
                    ImageGroup roll =
                        m_imageGroupFactory
                        .getRollByOwnerAndName(owner, rollName);

                        for (ImageFrame frame : roll.getFrames()) {
                            Image image = frame.getImage();
                            for (ImageManifestation mf
                                     : image.getManifestations()) {
                                HeavyImageManifestation heavyMf =
                                    m_imageManifestationFactory
                                    .getHeavyById(mf.getId());
                                BufferedImage bi =
                                    ImageIO.read(heavyMf.getData()
                                                 .getBinaryStream());
                                mf.setHeight(bi.getHeight());
                                mf.setWidth(bi.getWidth());
                                session.evict(heavyMf);
                            }
                        }
                    } catch (Exception e) {
                        s_log.error("Error fixing permissions", e);
                    }
                    return null;
                }
            });
    }

    public static final void main(String[] args) {
        ClassPathXmlApplicationContext appContext =
            new ClassPathXmlApplicationContext(new String[]
                {"applicationContext-global.xml",
                 "applicationContext-user.xml",
                 "applicationContext-imagestore.xml",
                 "applicationContext-imagestore-authorization.xml",
                 "applicationContext-standalone.xml"});

        FixDimensions fd =
            (FixDimensions) appContext.getBean(FIX_DIMENSIONS_BEAN);


        fd.fixDimensions(args[0], args[1]);
    }
}