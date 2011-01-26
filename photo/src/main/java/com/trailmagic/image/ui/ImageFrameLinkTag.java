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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class ImageFrameLinkTag extends TagSupport {
    private ImageFrame frame;
    private String id;


    public int doStartTag() throws JspException {
        StringBuilder html = new StringBuilder();

        try {
            html.append("<a ");
            html.append(formatAttribute("href", imageFrameUrl()));
            if (id != null) {
                html.append(formatAttribute("id", id));
            }
            html.append(">");

            pageContext.getOut().write(html.toString());
            return EVAL_BODY_INCLUDE;
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    private String imageFrameUrl() {
        return JspFunctions.frameUri(pageContext, frame);
    }

    private String formatAttribute(String name, String value) {
        return name + "=\"" + value + "\" ";

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
        this.frame = frame;
    }

    public ImageFrame getFrame() {
        return frame;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
