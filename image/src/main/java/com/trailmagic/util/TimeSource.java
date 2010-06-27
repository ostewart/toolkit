package com.trailmagic.util;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TimeSource {
    public Date today() {
        return new Date();
    }
}
