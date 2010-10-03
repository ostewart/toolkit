package com.trailmagic.image.impl;

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.Image;
import com.trailmagic.image.ImageManifestationService;
import com.trailmagic.resizer.ImageFileInfo;
import com.trailmagic.resizer.ImageResizeService;
import com.trailmagic.resizer.ResizeFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class ImageManifestationServiceImpl implements ImageManifestationService {
    private ImageResizeService imageResizeService;
    private static Logger log = LoggerFactory.getLogger(ImageManifestationServiceImpl.class);
    private HibernateUtil hibernateUtil;
    private ImageInitializer imageInitializer;

    @Autowired
    public ImageManifestationServiceImpl(ImageResizeService imageResizeService, HibernateUtil hibernateUtil, ImageInitializer imageInitializer) {
        this.imageResizeService = imageResizeService;
        this.hibernateUtil = hibernateUtil;
        this.imageInitializer = imageInitializer;
    }

    @Override
    public void createManifestationsFromOriginal(Image image, InputStream inputStream) throws IOException {
        File srcFile = imageResizeService.writeFile(inputStream);
        ImageFileInfo srcFileInfo = imageResizeService.identify(srcFile);

        try {
            addManifestation(image, srcFileInfo, true);
            scheduleResize(image, srcFile);
        } finally {
            boolean deleted = srcFile.delete();
            if (!deleted) {
                log.warn("Failed to delete temporary image file: " + srcFile.getAbsolutePath());
            }
        }
    }

    private void addManifestation(Image image, ImageFileInfo info, boolean original) throws IOException {
        final HeavyImageManifestation manifestation = new HeavyImageManifestation();
        manifestation.setData(hibernateUtil.toBlob(info.getFile()));
        manifestation.setOriginal(original);
        manifestation.setFormat(info.getFormat());
        manifestation.setHeight(info.getHeight());
        manifestation.setWidth(info.getWidth());
        image.addManifestation(manifestation);
        imageInitializer.saveNewImageManifestation(manifestation, false);
    }

    private void scheduleResize(Image image, File srcFile) throws IOException {
        try {
            List<ImageFileInfo> fileInfos = imageResizeService.scheduleResize(srcFile);
            for (ImageFileInfo info : fileInfos) {
                addManifestation(image, info, false);
                boolean deleted = info.getFile().delete();
                if (!deleted) {
                    log.warn("Could not delete resize temp file " + info.getFile().getAbsolutePath());
                }
            }
        } catch (ResizeFailedException e) {
            log.error("Resize failed on " + image, e);
        }

    }

}
