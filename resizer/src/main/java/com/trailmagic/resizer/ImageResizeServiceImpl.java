package com.trailmagic.resizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ImageResizeServiceImpl implements ImageResizeService {
    private ImageResizer imageResizer;

    @Autowired
    public ImageResizeServiceImpl(ImageResizer imageResizer) {
        this.imageResizer = imageResizer;
    }

    @Override
    public void scheduleResize(Long imageId, Long imageManifestationId) {
        File srcFile = saveImageToFile(imageId, imageManifestationId);
        try {
            imageResizer.resizeImage(srcFile, imageResizer.identify(srcFile), 128);
            imageResizer.resizeImage(srcFile, imageResizer.identify(srcFile), 256);
            imageResizer.resizeImage(srcFile, imageResizer.identify(srcFile), 512);
            imageResizer.resizeImage(srcFile, imageResizer.identify(srcFile), 1024);
            imageResizer.resizeImage(srcFile, imageResizer.identify(srcFile), 2048);
        } catch (ResizeFailedException e) {
            e.printStackTrace();
        }

    }

    private File saveImageToFile(Long imageId, Long imageManifestationId) {
        return null;
    }
}
