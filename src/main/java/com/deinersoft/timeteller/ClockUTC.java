package com.deinersoft.timeteller;

import java.time.LocalDateTime;

public class ClockUTC extends Clock {
    LocalDateTime localDateTime = null;

    public ClockUTC() {
        localDateTime = LocalDateTime.now(java.time.Clock.systemUTC());
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
