package com.trailmagic.image;

public class NoSuchImageFrameException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoSuchImageFrameException(ImageGroup imageGroup, Long imageId) {
        super("No ImageFrame in group: " + imageGroup.getName()
              + " with image id: " + imageId);
    }

    public NoSuchImageFrameException(String groupName, Long imageId) {
        super("No ImageFrame in group: " + groupName
              + " with image id: " + imageId);
    }
}
