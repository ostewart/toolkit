package com.trailmagic.image;

public class NoSuchImageException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoSuchImageException(Exception e) {
        super(e);
    }
    
    public NoSuchImageException(long imageId, Throwable e) {
        super("Image not found with id: " + imageId, e);
    }
    
    public NoSuchImageException(long imageId) {
        super("Image not found with id: " + imageId);
    }
}
