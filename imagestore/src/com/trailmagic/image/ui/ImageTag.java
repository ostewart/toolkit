package com.trailmagic.image.ui;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import com.trailmagic.user.*;
import com.trailmagic.image.*;

public class ImageTag extends TagSupport {
    private Image m_image;
    private String m_imageVar;

    private static final String USER_ATTR = "user";

    public int doStartTag() throws JspException {
        StringBuffer html = new StringBuffer();
        ImageManifestation mf;
        evaluateExpressions();
        m_image = (Image)pageContext.getAttribute(m_imageVar);
        try {
            mf = WebSupport.getDefaultMF((User)pageContext
                                         .findAttribute(USER_ATTR),
                                         m_image);
            html.append("<img src=\"");

            //XXX: yeek?
            LinkHelper helper =
                new LinkHelper((HttpServletRequest)pageContext.getRequest());

            html.append(helper.getImageMFUrl(mf));
            html.append("\" height=\"");
            html.append(mf.getHeight());
            html.append("\" width=\"");
            html.append(mf.getWidth());
            html.append("\"/>");
            pageContext.getOut().write(html.toString());
            return SKIP_BODY;
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    public void setObj(Image obj) {
        m_image = obj;
    }

    public Image getObj() {
        return m_image;
    }

    public void setImage(String obj) {
        m_imageVar = obj;
    }

    public String getImage() {
        return m_imageVar;
    }

    private void evaluateExpressions() {
        ExpressionEvaluator evaluator = pageContext.getExpressionEvaluator();
        m_image = (Image)evaluator.evaluate(m_imageVar, Image.class,
                                            pageContext.getVariableResolver(),
                                            null);
    }
}
