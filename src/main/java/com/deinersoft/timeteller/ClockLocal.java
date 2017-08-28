package com.deinersoft.timeteller;

import java.time.LocalDateTime;

public class ClockLocal extends Clock {
    LocalDateTime localDateTime = null;

    public ClockLocal() {
        localDateTime = LocalDateTime.now();
    }

    public int getHour() {
        return localDateTime.getHour();
    }

    public int getMinute() {
        return localDateTime.getMinute();
    }

    public int getSecond() {
        return localDateTime.getSecond();
    }
}
