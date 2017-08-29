package com.deinersoft.timeteller;

public class TimeFormatter {
    Clock clock;

    int hour;
    int minute;
    int second;

    public TimeFormatter(Clock clock) {
        this.clock = clock;

        hour = clock.getHour();
        minute = clock.getMinute();
        second = clock.getSecond();
    }

    public String formatTime() {
        return "";
    }

}