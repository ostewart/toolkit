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
package com.trailmagic.image;

import org.acegisecurity.intercept.method.aspectj.AspectJSecurityInterceptor;
import org.acegisecurity.intercept.method.aspectj.AspectJCallback;
import org.springframework.beans.factory.InitializingBean;
import org.apache.log4j.Logger;
import java.util.SortedSet;

public aspect ImageSecurityAspect implements InitializingBean {
    private AspectJSecurityInterceptor m_securityInterceptor;
    private Logger m_log =
        Logger.getLogger(com.trailmagic.image.ImageSecurityAspect.class);

    pointcut imageAccess(Image i): target(i) && call(public * *(..));
    pointcut imageGroupAccess(ImageGroup g): target(g) && call(public * *(..));
    pointcut imageFrameAccess(ImageFrame f): target(f) && call(public * *(..));
    pointcut getFrames(ImageGroup g): target(g)
        && call(public SortedSet<ImageFrame> getFrames());

    pointcut framage(ImageGroup g): target(g) && call(public * getFrames(..));

    after (ImageGroup g) returning (SortedSet<ImageFrame> frames): getFrames(g) {
        for (ImageFrame frame: frames) {
            m_log.warn("got frame: " + frame);
        }
    }

    after (ImageGroup g) returning(): framage(g) {
        m_log.warn("major framage");
    }

    before (ImageGroup g): imageGroupAccess(g) {
        m_log.warn("imageGroupAccess advice!!");
    }

    before(Image i): imageAccess(i) {
        m_log.warn("imageAccess advice!!!");
    }

    before(ImageFrame f): imageFrameAccess(f) {
        m_log.warn("imageFrameAccess advice!!");
    }

    before(): call(String com.trailmagic.image.Image.get*()) {
        m_log.warn("about to call getDisplayName(). w00t!");
    }

    public AspectJSecurityInterceptor getSecurityInterceptor() {
        return m_securityInterceptor;
    }

    public void setSecurityInterceptor(AspectJSecurityInterceptor
                                       securityInterceptor) {
        m_securityInterceptor = securityInterceptor;
    }

    public void afterPropertiesSet() throws Exception {
        if (m_securityInterceptor == null) {
            throw new IllegalArgumentException("SecurityInterceptor required");
        }
    }
}