package com.deinersoft.timeteller;

import com.deinersoft.messenger.Email;

import javax.mail.MessagingException;

public class Demo {
    public static void main(String [] args) {

        Email eMail = new Email();

        System.out.println(new TimeFormatterNumeric(new ClockLocal()).formatTime());
        System.out.println(new TimeFormatterNumeric(new ClockUTC()).formatTime());
        System.out.println(new TimeFormatterApproximateWording(new ClockLocal()).formatTime());
        System.out.println(new TimeFormatterApproximateWording(new ClockUTC()).formatTime());

        try {
            eMail.send(new TimeFormatterNumeric(new ClockLocal()).formatTime());
            eMail.send(new TimeFormatterNumeric(new ClockUTC()).formatTime());
            eMail.send(new TimeFormatterApproximateWording(new ClockLocal()).formatTime());
            eMail.send(new TimeFormatterApproximateWording(new ClockUTC()).formatTime());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
