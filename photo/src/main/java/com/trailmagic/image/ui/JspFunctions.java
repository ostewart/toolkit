package com.trailmagic.image.ui;

import com.trailmagic.image.ImageFrame;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class JspFunctions {
    public static String frameUri(PageContext pageContext, ImageFrame frame) {
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
        LinkHelper helper = (LinkHelper) ctx.getBean("linkHelper");

        return helper.getImageGroupFrameUrl((HttpServletRequest) pageContext.getRequest(), frame);
    }
}
