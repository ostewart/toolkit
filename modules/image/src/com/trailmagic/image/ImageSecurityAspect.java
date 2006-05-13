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

    pointcut getFrames(ImageGroup g): target(g)
        && call(public SortedSet<ImageFrame> getFrames());

    //    pointcut springAdvised(): !within(ImageSecurityAspect)
        //        && execution(public * *(..))
        //        && (target(Image) || target(ImageFrame));

    pointcut springAdvised(): !within(ImageSecurityAspect)
        && (execution(public * getImage(..))
            && target(ImageFrame))
        || (target(ImageGroup)
            && execution(public SortedSet<ImageFrame> *(..)))
        || (target(Image)
            && execution(public SortedSet<ImageManifestation> *(..)));

    Object around(): springAdvised() {
        if (m_securityInterceptor != null) {
            AspectJCallback callback = new AspectJCallback() {
                    public Object proceedWithObject() {
                        return proceed();
                    }
                };
            return m_securityInterceptor.invoke(thisJoinPoint, callback);
        } else {
            throw new IllegalStateException("null security interceptor");
        }
    }

//     after (ImageGroup g) returning (SortedSet<ImageFrame> frames): getFrames(g) {
//         for (ImageFrame frame: frames) {
//             m_log.debug("got frame: " + frame);
//         }
//     }

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