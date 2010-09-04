package com.trailmagic.resizer;

/**
 * Created by: oliver on Date: Aug 28, 2010 Time: 8:37:23 PM
 */
public class ImageSize {
    private int height;
    private int width;

    public ImageSize(int width, int height) {
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
