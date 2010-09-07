package com.trailmagic.resizer;

import java.io.File;

/**
 * Created by: oliver on Date: Aug 28, 2010 Time: 6:41:03 PM
 */
public class ImageFileInfo {
    private int height;
    private int width;
    private String format;
    private File file;

    public ImageFileInfo(int width, int height, String format) {
        this.height = height;
        this.width = width;
        this.format = format;
    }

    public ImageFileInfo(int width, int height, String format, File file) {
        this.height = height;
        this.width = width;
        this.format = format;
        this.file = file;
    }

    public ImageFileInfo() {
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getFormat() {
        return format;
    }

    public boolean isLandscape() {
        return width > height;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
