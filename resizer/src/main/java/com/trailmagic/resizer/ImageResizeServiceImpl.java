package com.trailmagic.resizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ImageResizeServiceImpl implements ImageResizeService {
    private ImageResizer imageResizer;

    @Autowired
    public ImageResizeServiceImpl(ImageResizer imageResizer) {
        this.imageResizer = imageResizer;
    }

    @Override
    public List<File> scheduleResize(Long imageId, Long imageManifestationId) {
        File srcFile = saveImageToFile(imageId, imageManifestationId);
        List<File> resultFiles = new ArrayList<File>();
        List<Integer> sizes = Arrays.asList(128, 256, 512, 1024, 2048);
        try {
            for (Integer size : sizes) {
                resultFiles.add(imageResizer.resizeImage(srcFile, imageResizer.identify(srcFile), size));
            }
        } catch (ResizeFailedException e) {
            e.printStackTrace();
        }
        return resultFiles;
    }

    private File saveImageToFile(Long imageId, Long imageManifestationId) {
        return null;
    }
}
