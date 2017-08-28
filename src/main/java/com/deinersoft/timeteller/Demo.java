package com.deinersoft.timeteller;

import com.deinersoft.messenger.Email;

import javax.mail.MessagingException;

public class Demo {
    public static void main(String [] args) {

        Email eMail = new Email();
        TimeFormatter timeTeller = new TimeFormatter();

        System.out.println(timeTeller.formatTime(new ClockLocal(), TimeZone.LOCAL, TimeFormattedAs.NUMERIC));
        System.out.println(timeTeller.formatTime(new ClockUTC(), TimeZone.UTC, TimeFormattedAs.NUMERIC));
        System.out.println(timeTeller.formatTime(new ClockLocal(), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING));
        System.out.println(timeTeller.formatTime(new ClockUTC(), TimeZone.UTC, TimeFormattedAs.APPROXIMATE_WORDING));

        try {
            eMail.send(timeTeller.formatTime(new ClockLocal(), TimeZone.LOCAL, TimeFormattedAs.NUMERIC));
            eMail.send(timeTeller.formatTime(new ClockUTC(), TimeZone.UTC, TimeFormattedAs.NUMERIC));
            eMail.send(timeTeller.formatTime(new ClockLocal(), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING));
            eMail.send(timeTeller.formatTime(new ClockUTC(), TimeZone.UTC, TimeFormattedAs.APPROXIMATE_WORDING));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
