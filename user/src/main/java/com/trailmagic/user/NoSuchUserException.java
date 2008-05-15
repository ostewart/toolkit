package com.trailmagic.user;

public class NoSuchUserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoSuchUserException(Long userId) {
        super("No user with id: " + userId);
    }
    
    public NoSuchUserException(String screenName) {
        super("No user with screen name: " + screenName);
    }
}
