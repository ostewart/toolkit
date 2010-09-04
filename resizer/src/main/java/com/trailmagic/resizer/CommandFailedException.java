package com.trailmagic.resizer;

import java.io.IOException;

/**
 * Created by: oliver on Date: Sep 3, 2010 Time: 9:55:48 PM
 */
public class CommandFailedException extends RuntimeException {
    public CommandFailedException(String message) {
        super(message);
    }

    public CommandFailedException(String message, Throwable e) {
        super(message, e);
    }
}
