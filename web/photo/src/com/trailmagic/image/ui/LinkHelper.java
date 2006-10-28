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

import javax.servlet.http.HttpServletRequest;

import com.trailmagic.user.*;
import com.trailmagic.image.*;


public class LinkHelper {
    private HttpServletRequest m_request;
    private String m_albumServletPath;
    /*
    public LinkHelper(HttpServletRequest req) {
        m_request = req;
    }
    */
    public LinkHelper() {
    }

    public void setAlbumServletPath(String path) {
        // XXX: trailing slash?
        m_albumServletPath = path;
    }

    public String getAlbumServletPath() {
        return m_albumServletPath;
    }

    public void setRequest(HttpServletRequest req) {
        m_request = req;
    }

    public HttpServletRequest getRequest() {
        return m_request;
    }

    /** 
     * /albums/user/album-name/image-id
     * this means that an image can't be in an album more than once
     * I think I can live with that
     **/
    public String getAlbumFrameUrl(ImageFrame frame) {
        return getAlbumsRootUrl() +
            frame.getImage().getOwner().getScreenName() + "/" +
            frame.getImageGroup().getName() + "/" + frame.getImage().getId();
    }

    public String getImageGroupFrameUrl(ImageFrame frame) {
        return getImageGroupsRootUrl(frame.getImageGroup().getType()) +
            frame.getImage().getOwner().getScreenName() + "/" +
            frame.getImageGroup().getName() + "/" + frame.getImage().getId();
    }

    public String getAlbumFrameUrl(ImageGroup album, Long imageId) {
        return getAlbumsRootUrl() +
            album.getOwner().getScreenName() + "/" +
            album.getName() + "/" + imageId;
    }

    public String getImageGroupFrameUrl(ImageGroup imageGroup, Long imageId) {
        return getImageGroupsRootUrl(imageGroup.getType()) +
            imageGroup.getOwner().getScreenName() + "/" +
            imageGroup.getName() + "/" + imageId;
    }

    public String getAlbumUrl(ImageGroup album) {
        return getAlbumsRootUrl() +
            album.getOwner().getScreenName() + "/" +
            album.getName() + "/";
    }

    public String getImageGroupUrl(ImageGroup imageGroup) {
        return getImageGroupsRootUrl(imageGroup.getType()) +
            imageGroup.getOwner().getScreenName() + "/" +
            imageGroup.getName() + "/";
    }

    public String getAlbumsUrl(User owner) {
        return getAlbumsRootUrl() + owner.getScreenName() + "/";
    }

    public String getImageGroupsUrl(String groupType, User owner) {
        return getImageGroupsRootUrl(groupType) +
            owner.getScreenName() + "/";
    }

    public String getAlbumsRootUrl() {
        return m_request.getContextPath() + m_albumServletPath;
    }

    public String getImageGroupsRootUrl(String groupType) {
        return m_request.getContextPath() + "/" + groupType + "s/";
    }

    public String getImageDisplayUrl(Image image) {
        return m_request.getContextPath() + "/images/by-id/" + image.getId();
    }

    public String getImageMFUrl(ImageManifestation imf) {
        return m_request.getContextPath() + "/mf/by-id/" + 
            imf.getId();
    }

    public String getImageMFUrl(Image image) {
        return m_request.getContextPath() + "/mf/by-id/" + 
            ((ImageManifestation)image.getManifestations().first()).getId();
    }

}
