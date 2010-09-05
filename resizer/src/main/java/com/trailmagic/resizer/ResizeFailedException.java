package com.trailmagic.resizer;

import java.io.IOException;

/**
 * Created by: oliver on Date: Aug 28, 2010 Time: 6:56:47 PM
 */
public class ResizeFailedException extends Exception {
    public ResizeFailedException(Throwable cause) {
        super("Failed to resize image because of lower level problem", cause);
    }

    public ResizeFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
