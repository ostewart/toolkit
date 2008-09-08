package com.trailmagic.web.util;

/**
 * Indicates an unrecognized or otherwise invalid request URL.
 * @author oliver
 *
 */
public class MalformedUrlException extends Exception {
    private static final long serialVersionUID = 1L;

    public MalformedUrlException() {
        super();
    }
    
    public MalformedUrlException(String message) {
        super(message);
    }
    
    public MalformedUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
