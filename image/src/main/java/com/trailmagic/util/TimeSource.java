package com.trailmagic.util;

import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class TimeSource {
    public Date today() {
        return new Date();
    }

    public Calendar calendar() {
        return Calendar.getInstance();
    }
}
