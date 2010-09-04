package com.trailmagic.resizer;

/**
 * Created by: oliver on Date: Aug 28, 2010 Time: 6:41:03 PM
 */
public class ImageFileInfo {
    private int height;
    private int width;
    private String format;

    public ImageFileInfo(int height, int width, String format) {
        this.height = height;
        this.width = width;
        this.format = format;
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
}
