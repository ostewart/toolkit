package com.trailmagic.resizer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ImageResizer {
    public File resizeImage(File srcFile, ImageFileInfo dimensionLength, int shortestDimensionLength) throws ResizeFailedException;
    public ImageFileInfo identify(File file);
    public File writeToTempFile(InputStream imageInputStream) throws IOException;
}
