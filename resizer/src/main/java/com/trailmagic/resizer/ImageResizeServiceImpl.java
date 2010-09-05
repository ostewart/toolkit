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
    public List<ImageFileInfo> scheduleResize(Long imageId, Long imageManifestationId) {
        File srcFile = saveImageToFile(imageId, imageManifestationId);
        ImageFileInfo srcFileInfo = imageResizer.identify(srcFile);

        List<ImageFileInfo> resultInfos = new ArrayList<ImageFileInfo>();

        List<Integer> sizes = Arrays.asList(128, 256, 512, 1024, 2048);
        try {
            for (Integer size : sizes) {
                resultInfos.add(resizeAndIdentify(srcFile, srcFileInfo, size));
            }
        } catch (ResizeFailedException e) {
            e.printStackTrace();
        }

        return resultInfos;
    }

    private ImageFileInfo resizeAndIdentify(File srcFile, ImageFileInfo srcFileInfo, Integer size) throws ResizeFailedException {
        File file = imageResizer.resizeImage(srcFile, srcFileInfo, size);
        ImageFileInfo info = imageResizer.identify(file);
        info.setFile(file);
        return info;
    }

    private File saveImageToFile(Long imageId, Long imageManifestationId) {
        return null;
    }
}
