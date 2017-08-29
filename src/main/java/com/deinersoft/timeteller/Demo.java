package com.deinersoft.timeteller;

import com.deinersoft.messenger.Email;

import javax.mail.MessagingException;
import java.io.IOException;

public class Demo {
    public static void main(String [] args) throws MessagingException, IOException{

        Email eMail = new Email("configEmail.properties");

        System.out.println(new TimeFormatterNumeric(new ClockLocal()).formatTime());
        System.out.println(new TimeFormatterNumeric(new ClockUTC()).formatTime());
        System.out.println(new TimeFormatterApproximateWording(new ClockLocal()).formatTime());
        System.out.println(new TimeFormatterApproximateWording(new ClockUTC()).formatTime());

        eMail.send(new TimeFormatterNumeric(new ClockLocal()).formatTime());
        eMail.send(new TimeFormatterNumeric(new ClockUTC()).formatTime());
        eMail.send(new TimeFormatterApproximateWording(new ClockLocal()).formatTime());
        eMail.send(new TimeFormatterApproximateWording(new ClockUTC()).formatTime());

    }

}
