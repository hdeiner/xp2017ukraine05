package com.deinersoft.timeteller;

public class TimeFormatter {

    public static final int SECONDS_IN_A_HALF_MINUTE = 30;
    public static final int HOURS_IN_A_QUARTER_OF_A_DAY = 6;
    public static final int MINUTE_TO_START_FUZZY_WORDING = 3;
    public static final int MINUTE_TO_START_FUZZING_INTO_NEXT_HOUR = 35;

    public String formatTime(Clock clock, TimeZone whichTimeZone, TimeFormattedAs typeOfFormatting) {
        String formattedTime = "";

        switch (typeOfFormatting) {
            case NUMERIC:
                formattedTime = formatTimeNumeric(clock.getHour(), clock.getMinute(), clock.getSecond());
                if (whichTimeZone == TimeZone.UTC) {
                    formattedTime += "Z";
                }
                break;
            case APPROXIMATE_WORDING:
                formattedTime = formatTimeApproximateWording(clock.getHour(), clock.getMinute(), clock.getSecond());
                if (whichTimeZone == TimeZone.UTC) {
                    formattedTime += " Zulu";
                }
                break;
        }

        return formattedTime;
    }

    private String formatTimeNumeric(int hour, int minute, int second){
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    private String formatTimeApproximateWording(int hour, int minute, int second){
        String formattedTime = "";

        String[] namesOfTheHours = {"twelve", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven"};
        String[] fuzzyTimeWords = {"", "almost ten after", "ten after", "a quarter after", "twenty after", "almost half past", "half past", "almost twenty before", "twenty before", "a quarter of", "ten of", "almost"};
        String[] quadrantOfTheDay = {"at night", "in the morning", "in the afternoon", "in the evening"};

        if (second >= SECONDS_IN_A_HALF_MINUTE) minute++;

        if (minute >= MINUTE_TO_START_FUZZY_WORDING) {
            formattedTime += fuzzyTimeWords[(minute+2)/5] + " ";
        }

        if (minute < MINUTE_TO_START_FUZZING_INTO_NEXT_HOUR) {
            formattedTime += namesOfTheHours[hour % namesOfTheHours.length];
        }  else {
            formattedTime += namesOfTheHours[(hour+1) % namesOfTheHours.length];
        }

        formattedTime += " " + quadrantOfTheDay[hour/HOURS_IN_A_QUARTER_OF_A_DAY];

        return formattedTime;
    }

}