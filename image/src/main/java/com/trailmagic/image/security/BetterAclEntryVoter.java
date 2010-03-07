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
package com.trailmagic.image.security;

import java.lang.reflect.Method;

import org.slf4j.LoggerFactory;
import org.springframework.security.AuthorizationServiceException;
import org.springframework.security.vote.BasicAclEntryVoter;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.Authentication;


public class BetterAclEntryVoter extends BasicAclEntryVoter {
    private static Logger s_log = LoggerFactory.getLogger(BetterAclEntryVoter.class);
    /**
     * This implementation supports both
     * <code>MethodSecurityInterceptor</code> and <code>JoinPoint</code>.
     *
     * @param clazz the secure object
     *
     * @return <code>true</code> if the secure object is
     *         <code>MethodInvocation</code> or <code>JoinPoint</code>,
     *         <code>false</code> otherwise
     */
    public boolean supports(Class clazz) {
        s_log.debug("supports called on " + clazz);
        return (MethodInvocation.class.isAssignableFrom(clazz)
                || JoinPoint.class.isAssignableFrom(clazz));
    }

    protected Object getDomainObjectInstance(Object secureObject) {
        Method method;
        Class[] params;

        s_log.debug("getDomainObjectInstance called on " + secureObject);

        if (secureObject instanceof MethodInvocation) {
            s_log.debug("processing MethodInvocation:" + secureObject);

            MethodInvocation invocation = (MethodInvocation) secureObject;

            // Check if this MethodInvocation provides the required argument
            method = invocation.getMethod();
            params = method.getParameterTypes();

            for (int i = 0; i < params.length; i++) {
                if (getProcessDomainObjectClass()
                    .isAssignableFrom(params[i])) {

                    return invocation.getArguments()[i];
                }
            }

            throw new AuthorizationServiceException("MethodInvocation: "
                + invocation + " did not provide any argument of type: "
                + getProcessDomainObjectClass());

        } else if (secureObject instanceof JoinPoint) {
            s_log.debug("Processing JoinPoint:" + secureObject);
            try {
                JoinPoint joinPoint = (JoinPoint) secureObject;
                CodeSignature signature =
                    (CodeSignature) joinPoint.getStaticPart().getSignature();
                Class targetClass = joinPoint.getTarget().getClass();

                params = signature.getParameterTypes();
                targetClass.getMethod(signature.getName(), params);

                for (int i = 0; i < params.length; i++) {
                    if (getProcessDomainObjectClass()
                        .isAssignableFrom(params[i])) {

                        return joinPoint.getArgs()[i];
                    }
                }
                throw new AuthorizationServiceException("JoinPoint: "
                    + joinPoint + " did not provide any argument of type: "
                    + getProcessDomainObjectClass());

            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Could not obtain target "
                                                   + "method from JoinPoint: "
                                                   + secureObject);
            }
        } else {
            throw new IllegalArgumentException("secure object is neither a"
                                               + "MethodInvocation nor a"
                                               + "JoinPoint: "
                                               + secureObject);
        }
    }

        public int vote(Authentication authentication, Object object,
                    ConfigAttributeDefinition config) {
            s_log.debug("vote called with: " + authentication + "; " + object
                        + "; " + config);

            int result = super.vote(authentication, object, config);
            s_log.debug("super returned " + result);
            return result;
        }

}