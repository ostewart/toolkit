package com.trailmagic.image.security;

import org.aspectj.lang.NoAspectBoundException;
import org.springframework.security.access.intercept.aspectj.AspectJMethodSecurityInterceptor;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.aspectj.lang.Aspects;
import com.trailmagic.image.ImageSecurityAspect;

public class SecurityTestHelper {
    public SecurityTestHelper() {
    }

    public void disableSecurityInterceptor() {
        try {
            final AspectJMethodSecurityInterceptor interceptor = new AspectJMethodSecurityInterceptor();
            interceptor.setSecurityMetadataSource(new MapBasedMethodSecurityMetadataSource());
            Aspects.aspectOf(ImageSecurityAspect.class).setSecurityInterceptor(interceptor);
        } catch (NoAspectBoundException e) {
            // AspectJ isn't on
        }
    }
}