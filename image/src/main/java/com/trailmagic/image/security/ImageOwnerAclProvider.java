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
package com.trailmagic.image.security;

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.user.Owned;
import com.trailmagic.user.security.OwnerAclProvider;
import org.apache.log4j.Logger;

/**
 * Creates an ACL with full permissions for the User owner of an Image.
 **/
public class ImageOwnerAclProvider extends OwnerAclProvider {
    private ImageGroupRepository imageGroupRepository;
    private static Logger s_log = Logger.getLogger(ImageOwnerAclProvider.class);

    public void setImageGroupRepository(ImageGroupRepository factory) {
        imageGroupRepository = factory;
    }

    /**
     * Determines whether or not a domain object instance is supported by
     * this provider
     *
     * @param domainInstance domain object instance
     * @return <code>true</code> if <code>domainInstance</code> is an
     * <code>Image</code>, <code>false</code> otherwise
     **/
    public boolean supports(Object domainInstance) {
        if (domainInstance == null) {
            return false;
        }

        boolean isSupported =
            Image.class.isAssignableFrom(domainInstance.getClass());

        if (s_log.isDebugEnabled()) {
            s_log.debug("domain instance " + domainInstance
                        + (isSupported ? " is" : " is not") + " supported");
        }
        return isSupported;
    }

    protected Object getParent(Owned ownedObj) {
        Image image = (Image) ownedObj;
        return imageGroupRepository.getRollForImage(image);
    }
}