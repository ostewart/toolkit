package com.trailmagic.image;
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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.intercept.method.aspectj.AspectJAnnotationCallback;
import org.springframework.security.intercept.method.aspectj.AspectJAnnotationSecurityInterceptor;

@Aspect
public class ImageSecurityAspect implements InitializingBean {
    private AspectJAnnotationSecurityInterceptor securityInterceptor;

    @Pointcut(value = "target(g) && call(public java.util.SortedSet<ImageFrame> getFrames())", argNames = "g")
    public void getFrames(ImageGroup g) {
    }

    @Pointcut("!within(ImageSecurityAspect) " +
            "        && !cflow(within(com.trailmagic.image.hibernate.*)) " +
            "        && !cflow(call(* com.trailmagic.image.security.ImageSecurityService.addOwnerAcl(..))) " +
            "        && ((execution(public Image *(..)) && target(ImageGroup)) " +
            "            || (target(ImageGroup) && execution(public java.util.SortedSet<ImageFrame> *(..))) " +
            "            || (target(Image) && execution(public java.util.SortedSet<ImageManifestation> *(..))))")
    public void springAdvised() {
    }

    @Around("springAdvised()")
    public Object around(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
        if (securityInterceptor != null) {
            AspectJAnnotationCallback callback = new AspectJAnnotationCallback() {
                public Object proceedWithObject() throws Throwable {
                    return thisJoinPoint.proceed();
                }
            };
            return securityInterceptor.invoke(thisJoinPoint, callback);
        } else {
            throw new IllegalStateException("null security interceptor");
        }

    }

    public AspectJAnnotationSecurityInterceptor getSecurityInterceptor() {
        return securityInterceptor;
    }

    public void setSecurityInterceptor(AspectJAnnotationSecurityInterceptor securityInterceptor) {
        this.securityInterceptor = securityInterceptor;
    }

    public void afterPropertiesSet() throws Exception {
        if (securityInterceptor == null) {
            throw new IllegalArgumentException("SecurityInterceptor required");
        }
    }
}