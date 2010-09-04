package com.trailmagic.resizer;

/**
 * Created by: oliver on Date: Aug 28, 2010 Time: 6:56:47 PM
 */
public class ResizeFailedException extends Exception {
    public ResizeFailedException(Throwable cause) {
        super("Failed to resize image because of lower level problem", cause);
    }
}
