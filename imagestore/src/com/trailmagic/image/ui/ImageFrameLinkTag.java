package com.trailmagic.image.ui;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import com.trailmagic.user.*;
import com.trailmagic.image.*;

public class ImageFrameLinkTag extends TagSupport {
    private ImageFrame m_frame;
    

    private static final String USER_ATTR = "user";

    public int doStartTag() throws JspException {
        StringBuffer html = new StringBuffer();

        try {
            html.append("<a href=\"");

            //XXX: yeek?
            LinkHelper helper =
                new LinkHelper((HttpServletRequest)pageContext.getRequest());

            html.append(helper.getAlbumFrameUrl(m_frame));
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
