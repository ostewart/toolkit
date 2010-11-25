package com.trailmagic.image.impl;

import com.trailmagic.image.Image;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by: oliver on Date: Nov 1, 2010 Time: 3:18:31 AM
 */
public interface ImageResizeClient {
    void createOriginalManifestation(Image image, InputStream inputStream) throws IOException;

    @Async
    void createResizedManifestations(Image image, SecurityContext securityContext) throws IOException;
}
