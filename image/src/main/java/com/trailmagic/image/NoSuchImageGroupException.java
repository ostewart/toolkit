package com.trailmagic.image;

import com.trailmagic.user.User;

public class NoSuchImageGroupException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoSuchImageGroupException(User owner, String groupName,
                                     ImageGroup.Type groupType) {
        super("No image group owned by " + owner + " with the name "
              + groupName + " and of type " + groupType);
    }
    
    public NoSuchImageGroupException(long imageGroupId, Throwable cause) {
        super("ImageGroup not found with id: " + imageGroupId);
    }
    
}
