package com.trailmagic.image.ui;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
/*
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
*/
import com.trailmagic.user.*;
import com.trailmagic.image.*;

public class ImageTag extends TagSupport {
    private Image m_image;
    private String m_sizeLabel;

    private static final String USER_ATTR = "user";

    public int doStartTag() throws JspException {
        StringBuffer html = new StringBuffer();
        ImageManifestation mf;

        try {
            mf = WebSupport.getDefaultMF((User)pageContext
                                         .findAttribute(USER_ATTR),
                                         m_image);
            if ( mf != null ) {
                html.append("<img src=\"");
                
                //XXX: yeek?
                /*
                LinkHelper helper =
                    new LinkHelper((HttpServletRequest)pageContext.getRequest());
                */
                WebApplicationContext ctx =
                    WebApplicationContextUtils
                    .getRequiredWebApplicationContext(pageContext
                                                      .getServletContext());
                LinkHelper helper =
                    (LinkHelper)ctx.getBean("linkHelper");
                // XXX: check for null bean
                // XXX: evil cast?
                helper.setRequest((HttpServletRequest)
                                  pageContext.getRequest());
                
                html.append(helper.getImageMFUrl(mf));
                html.append("\" height=\"");
                html.append(mf.getHeight());
                html.append("\" width=\"");
                html.append(mf.getWidth());
                html.append("\"/>");
            } else {
                html.append("<p>No manifestations found for the specified " +
                            "image.</p>");
            }
            pageContext.getOut().write(html.toString());
            return SKIP_BODY;
        } catch (IOException e) {
            throw new JspException(e);
        }
    }
    public void setImage(Image image) {
        m_image = image;
    }

    public Image getImage() {
        return m_image;
    }

    public void setSizeLabel(String label) {
        m_sizeLabel = label;
    }

    public String getSizeLabel() {
        return m_sizeLabel;
    }
}
