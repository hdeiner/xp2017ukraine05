package com.deinersoft.timeteller;

public class TimeFormatterNumeric extends TimeFormatter {

    public TimeFormatterNumeric(Clock clock) {
        super(clock);
    }

    public String formatTime() {
        String formattedTime = String.format("%02d:%02d:%02d", hour, minute, second);
        if (clock.timeZone == TimeZone.UTC) {
            formattedTime += "Z";
        }
        return formattedTime;
    }
}
