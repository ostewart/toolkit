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
import org.springframework.security.access.intercept.aspectj.AspectJMethodSecurityInterceptor;

@Aspect
public class ImageSecurityAspect implements InitializingBean {
    private AspectJMethodSecurityInterceptor securityInterceptor;

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
            /** Hack to get around the AroundClosure not getting defined unless we call proceed here **/
            new Runnable(){
                @Override
                public void run() {
                    try {
                        thisJoinPoint.proceed();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            };
            return securityInterceptor.invoke(thisJoinPoint);
        } else {
            throw new IllegalStateException("null security interceptor");
        }

    }

    public void setSecurityInterceptor(AspectJMethodSecurityInterceptor securityInterceptor) {
        this.securityInterceptor = securityInterceptor;
    }

    public void afterPropertiesSet() throws Exception {
        if (securityInterceptor == null) {
            throw new IllegalArgumentException("SecurityInterceptor required");
        }
    }
}