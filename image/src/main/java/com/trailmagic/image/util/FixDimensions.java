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

import com.trailmagic.image.*;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


public class FixDimensions {
    private ImageGroupRepository imageGroupRepository;
    private HibernateTemplate m_hibernateTemplate;
    private UserRepository userRepository;
    private ImageManifestationRepository m_imageManifestationFactory;
    private static Logger s_log = LoggerFactory.getLogger(FixDimensions.class);

    private static final String FIX_DIMENSIONS_BEAN = "fixDimensions";

    public void setHibernateTemplate(HibernateTemplate template) {
        m_hibernateTemplate = template;
    }

    public void setImageGroupRepository(ImageGroupRepository factory) {
        this.imageGroupRepository = factory;
    }

    public void setUserFactory(UserRepository repository) {
        userRepository = repository;
    }

    public void setImageManifestationFactory(ImageManifestationRepository factory) {
        m_imageManifestationFactory = factory;
    }

    public void fixDimensions(final String ownerName, final String rollName) {
        m_hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                try {
                    User owner = userRepository.getByScreenName(ownerName);
                    ImageGroup roll = imageGroupRepository.getRollByOwnerAndName(owner, rollName);

                    for (ImageFrame frame : roll.getFrames()) {
                        Image image = frame.getImage();
                        for (ImageManifestation mf : image.getManifestations()) {
                            HeavyImageManifestation heavyMf = m_imageManifestationFactory.getHeavyById(mf.getId());
                            BufferedImage bi = ImageIO.read(heavyMf.getData().getBinaryStream());
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
                new ClassPathXmlApplicationContext("applicationContext-global.xml",
                        "applicationContext-user.xml",
                        "applicationContext-imagestore.xml",
                        "applicationContext-imagestore-authorization.xml",
                        "applicationContext-standalone.xml");

        FixDimensions fd = (FixDimensions) appContext.getBean(FIX_DIMENSIONS_BEAN);
        fd.fixDimensions(args[0], args[1]);
    }
}