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
    private String m_groupType = null;
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
        m_groupType = groupType;
    }

    public String getGroupType() {
        return m_groupType;
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
            WebApplicationContext ctx =
                WebApplicationContextUtils
                .getRequiredWebApplicationContext(pageContext
                                                  .getServletContext());
            LinkHelper helper =
                (LinkHelper)ctx.getBean("linkHelper");
            // XXX: check for null bean
            // XXX: evil cast?
            helper.setRequest((HttpServletRequest)pageContext.getRequest());

            if ( m_image != null ) {
                html.append(helper.getImageGroupFrameUrl(m_imageGroup, m_image));
            } else if ( m_imageGroup != null ) {
                html.append(helper.getImageGroupUrl(m_imageGroup));
            } else if ( m_owner != null ) {
                html.append(helper.getImageGroupsUrl(m_groupType, m_owner));
            } else {
                html.append(helper.getImageGroupsRootUrl(m_groupType));
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
