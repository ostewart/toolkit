package com.trailmagic.resizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    @Override
    public List<ImageFileInfo> scheduleResize(InputStream srcInputStream) throws ResizeFailedException {
        File srcFile = writeFile(srcInputStream);
        List<ImageFileInfo> resultInfos = scheduleResize(srcFile);

        srcFile.delete();
        return resultInfos;
    }

    public List<ImageFileInfo> scheduleResize(File srcFile) {
        ImageFileInfo srcFileInfo = imageResizer.identify(srcFile);

        List<ImageFileInfo> resultInfos = new ArrayList<ImageFileInfo>();

        for (Integer size : Arrays.asList(128, 256, 512, 1024, 2048)) {
            if (size <= srcFileInfo.getWidth()) {
                resultInfos.add(resizeAndIdentify(srcFile, srcFileInfo, size));
            }
        }
        return resultInfos;
    }

    @Override
    public ImageFileInfo identify(File srcFile) {
        return imageResizer.identify(srcFile);
    }


    public File writeFile(InputStream srcInputStream) throws ResizeFailedException {
        try {
            return imageResizer.writeToTempFile(srcInputStream);
        } catch (IOException e) {
            throw new ResizeFailedException("Could not write src to temp file", e);
        }
    }

    private ImageFileInfo resizeAndIdentify(File srcFile, ImageFileInfo srcFileInfo, Integer size) throws ResizeFailedException {
        File file = imageResizer.resizeImage(srcFile, srcFileInfo, size);
        ImageFileInfo info = imageResizer.identify(file);
        info.setFile(file);
        return info;
    }
}
