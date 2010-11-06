package com.trailmagic.image.impl;

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.Image;
import com.trailmagic.image.ImageManifestationRepository;
import com.trailmagic.image.ImageManifestationService;
import com.trailmagic.resizer.ImageFileInfo;
import com.trailmagic.resizer.ImageResizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

@Service
public class ImageResizeClientImpl implements ImageResizeClient {
    private ImageResizeService imageResizeService;
    private ImageManifestationService imageManifestationService;
    private static Logger log = LoggerFactory.getLogger(ImageResizeClientImpl.class);
    private ImageManifestationRepository imageManifestationRepository;

    @Autowired
    public ImageResizeClientImpl(ImageResizeService imageResizeService, ImageManifestationService imageManifestationService, ImageManifestationRepository imageManifestationRepository) {
        this.imageResizeService = imageResizeService;
        this.imageManifestationService = imageManifestationService;
        this.imageManifestationRepository = imageManifestationRepository;
    }

    @Override
    @Transactional(readOnly = false)
    public void createOriginalManifestation(Image image, InputStream inputStream) throws IOException {
        File srcFile = imageResizeService.writeFile(inputStream);
        ImageFileInfo srcFileInfo = imageResizeService.identify(srcFile);

        try {
            imageManifestationService.createManifestation(image, srcFileInfo, true);
        } finally {
            boolean deleted = srcFile.delete();
            if (!deleted) {
                log.warn("Failed to delete temporary image file: " + srcFile.getAbsolutePath());
            }
        }
    }

    @Async
    @Transactional(readOnly = false)
    @Override
    public void createResizedManifestations(Image image, SecurityContext securityContext) throws IOException {
        SecurityContextHolder.setContext(securityContext);
        try {
            final HeavyImageManifestation original = imageManifestationRepository.findOriginalHeavyForImage(image.getId());
            if (original == null) {
                throw new IllegalStateException("no original image found for image: " + image);
            }

            try {
                File srcFile = imageResizeService.writeFile(original.getData().getBinaryStream());
                try {
                    List<ImageFileInfo> fileInfos = imageResizeService.scheduleResize(srcFile);
                    for (ImageFileInfo info : fileInfos) {
                        imageManifestationService.createManifestation(image, info, false);
                        boolean deleted = info.getFile().delete();
                        if (!deleted) {
                            log.warn("Could not delete resize temp file " + info.getFile().getAbsolutePath());
                        }
                    }
                } finally {
                    boolean deleted = srcFile.delete();
                    if (!deleted) {
                        log.warn("Could not delete image temp file " + srcFile.getAbsolutePath());
                    }
                }
            } catch (SQLException e) {
                log.error("Failed to retrieve data from original manifestation for image: " + image, e);
                throw new IllegalStateException("Failed to retrieve data from original manifestation for image: " + image, e);
            }
        } catch (Throwable t) {
            log.error("Caught exception during resize for image (which may be in an inconsistent state: " + image, t);
        }
    }
}
