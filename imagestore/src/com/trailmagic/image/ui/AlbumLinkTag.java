package com.trailmagic.image.ui;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.http.HttpServletRequest;

import com.trailmagic.user.*;
import com.trailmagic.image.*;

public class AlbumLinkTag extends TagSupport {
    private ImageFrame m_frame = null;
    private User m_owner = null;
    private ImageGroup m_album = null;
    private Long m_image = null;

    public void setOwner(User owner) {
        m_owner = owner;
    }

    public User getOwner() {
        return m_owner;
    }

    public void setAlbum(ImageGroup album) {
        m_album = album;
    }

    public ImageGroup getAlbum() {
        return m_album;
    }

    public void setImage(Long image) {
        m_image = image;
    }

    public Long getImage() {
        return m_image;
    }
    
    public int doStartTag() throws JspException {
        StringBuffer html = new StringBuffer();

        try {
            html.append("<a href=\"");

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
                html.append(helper.getAlbumFrameUrl(m_album, m_image));
            } else if ( m_album != null ) {
                html.append(helper.getAlbumUrl(m_album));
            } else if ( m_owner != null ) {
                html.append(helper.getAlbumsUrl(m_owner));
            } else {
                html.append(helper.getAlbumsRootUrl());
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
