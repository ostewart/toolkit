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

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.http.HttpServletRequest;

import com.trailmagic.user.*;
import com.trailmagic.image.*;

public class ImageGroupLinkTag extends TagSupport {
    private ImageFrame m_frame = null;
    private User m_owner = null;
    private ImageGroup m_imageGroup = null;
    private Long m_image = null;
    private ImageGroupType m_groupType = null;
    private String m_styleClass = null;

    public void setOwner(User owner) {
        m_owner = owner;
    }

    public User getOwner() {
        return m_owner;
    }

    public void setImageGroup(ImageGroup imageGroup) {
        m_imageGroup = imageGroup;
    }

    public ImageGroup getImageGroup() {
        return m_imageGroup;
    }

    public void setGroupType(String groupType) {
        this.m_groupType = ImageGroupType.fromString(groupType);
    }

    public String getGroupType() {
        return m_groupType.toString();
    }

    public void setImage(Long image) {
        m_image = image;
    }

    public Long getImage() {
        return m_image;
    }

    public void setStyleClass(String styleClass) {
        m_styleClass = styleClass;
    }

    public String getStyleClass() {
        return m_styleClass;
    }

    // getClass is already taken

    public int doStartTag() throws JspException {
        StringBuffer html = new StringBuffer();

        try {
            html.append("<a ");
            if (m_styleClass != null) {
                html.append("class=\"" + m_styleClass + "\" ");
            }
            html.append("href=\"");

            //XXX: yeek?
            WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
            LinkHelper helper = ctx.getBean(LinkHelper.class);

            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            if ( m_image != null ) {
                html.append(helper.getImageGroupFrameUrl(request, m_imageGroup, m_image));
            } else if ( m_imageGroup != null ) {
                html.append(helper.getImageGroupUrl(request, m_imageGroup));
            } else if ( m_owner != null ) {
                html.append(helper.getImageGroupsUrl(request, m_groupType, m_owner));
            } else {
                html.append(helper.getImageGroupsRootUrl(request, m_groupType));
            }
            html.append("\">");
            pageContext.getOut().write(html.toString());
            return EVAL_BODY_INCLUDE;
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().write("</a>");
            return EVAL_PAGE;
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    public void setFrame(ImageFrame frame) {
        m_frame = frame;
    }

    public ImageFrame getFrame() {
        return m_frame;
    }
}
