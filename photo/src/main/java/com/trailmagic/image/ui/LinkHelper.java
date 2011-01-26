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

import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageManifestation;
import com.trailmagic.user.User;

import javax.servlet.http.HttpServletRequest;

public class LinkHelper {
    public LinkHelper() {
    }

    public String getImageGroupFrameUrl(HttpServletRequest request, ImageFrame frame) {
        return getImageGroupsRootUrl(request, frame.getImageGroup().getType()) +
                frame.getImage().getOwner().getScreenName() + "/" +
                frame.getImageGroup().getName() + "/" + frame.getImage().getId();
    }

    public String getImageGroupFrameUrl(HttpServletRequest request, ImageGroup imageGroup, Long imageId) {
        return getImageGroupsRootUrl(request, imageGroup.getType()) +
                imageGroup.getOwner().getScreenName() + "/" +
                imageGroup.getName() + "/" + imageId;
    }

    public String getImageGroupUrl(HttpServletRequest request, ImageGroup imageGroup) {
        return getImageGroupsRootUrl(request, imageGroup.getType()) +
                imageGroup.getOwner().getScreenName() + "/" +
                imageGroup.getName() + "/";
    }

    public String getImageGroupsUrl(HttpServletRequest request, ImageGroup.Type groupType, User owner) {
        return getImageGroupsRootUrl(request, groupType) +
                owner.getScreenName() + "/";
    }

    public String getImageGroupsRootUrl(HttpServletRequest request, ImageGroup.Type type) {
        String prettyType;
        switch (type) {
            case ROLL:
                prettyType = "roll";
                break;
            case ALBUM:
                prettyType = "album";
                break;
            default:
                throw new IllegalArgumentException();
        }
        return request.getContextPath() + "/" + prettyType + "s/";
    }

    public String getImageMFUrl(HttpServletRequest request, ImageManifestation imf) {
        return request.getContextPath() + "/mf/by-id/" +
                imf.getId();
    }

}
