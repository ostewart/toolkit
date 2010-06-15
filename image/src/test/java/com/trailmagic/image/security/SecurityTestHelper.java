package com.trailmagic.image.security;

import org.aspectj.lang.NoAspectBoundException;
import org.springframework.security.intercept.method.aspectj.AspectJAnnotationSecurityInterceptor;
import org.springframework.security.intercept.method.MapBasedMethodDefinitionSource;
import org.aspectj.lang.Aspects;
import com.trailmagic.image.ImageSecurityAspect;

public class SecurityTestHelper {
    public SecurityTestHelper() {
    }

    public void disableSecurityInterceptor() {
        try {
            final AspectJAnnotationSecurityInterceptor interceptor = new AspectJAnnotationSecurityInterceptor();
            interceptor.setObjectDefinitionSource(new MapBasedMethodDefinitionSource());
            Aspects.aspectOf(ImageSecurityAspect.class).setSecurityInterceptor(interceptor);
        } catch (NoAspectBoundException e) {
            // AspectJ isn't on
        }
    }
}