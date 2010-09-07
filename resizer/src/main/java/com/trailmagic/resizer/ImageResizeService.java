package com.trailmagic.resizer;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface ImageResizeService {
    public List<ImageFileInfo> scheduleResize(InputStream srcInputStream) throws ResizeFailedException;
    public List<ImageFileInfo> scheduleResize(File srcFile) throws ResizeFailedException;
    public ImageFileInfo identify(File srcFile);
    public File writeFile(InputStream srcInputStream) throws ResizeFailedException;
}
