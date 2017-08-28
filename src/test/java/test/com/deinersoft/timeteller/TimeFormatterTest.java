package test.com.deinersoft.timeteller;

import com.deinersoft.messenger.*;
import com.deinersoft.timeteller.*;
import com.sun.mail.imap.IMAPFolder;
import org.junit.Before;
import org.junit.Test;

import javax.mail.*;
import javax.mail.Flags.Flag;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class TimeFormatterTest {

    private TimeFormatter timeTeller;

    @Before
    public void initialize(){
         timeTeller = new TimeFormatter();
    }

    @Test
    public void localTimeCurrent(){
        assertThat(timeTeller.formatTime(new ClockLocal(), TimeZone.LOCAL, TimeFormattedAs.NUMERIC), is(getFormattedTime(LocalDateTime.now())));
    }

    @Test
    public void zuluTimeCurrent(){
        assertThat(timeTeller.formatTime(new ClockUTC(), TimeZone.UTC, TimeFormattedAs.NUMERIC), is(getFormattedTime(LocalDateTime.now(Clock.systemUTC()))+"Z"));
    }

    @Test
    public void zuluTimeInWords000005(){
        assertThat(timeTeller.formatTime(new ClockForTesting(0,0,5), TimeZone.UTC, TimeFormattedAs.APPROXIMATE_WORDING), is("twelve at night Zulu"));
    }

    @Test
    public void localTimeInWords000005(){
        assertThat(timeTeller.formatTime(new ClockForTesting(0,0,5), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("twelve at night"));
    }

    @Test
    public void localTimeInWords090239(){
        assertThat(timeTeller.formatTime(new ClockForTesting(9,2,39), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("almost ten after nine in the morning"));
    }

    @Test
    public void localTimeInWords090949(){
        assertThat(timeTeller.formatTime(new ClockForTesting(9,9,49), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("ten after nine in the morning"));
    }

    @Test
    public void localTimeInWords091702(){
        assertThat(timeTeller.formatTime(new ClockForTesting(9,17,2), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("a quarter after nine in the morning"));
    }

    @Test
    public void localTimeInWords091902(){
        assertThat(timeTeller.formatTime(new ClockForTesting(9,19,2), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("twenty after nine in the morning"));
    }

    @Test
    public void localTimeInWords092312(){
        assertThat(timeTeller.formatTime(new ClockForTesting(9,23,12), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("almost half past nine in the morning"));
    }

    @Test
    public void localTimeInWords093112(){
        assertThat(timeTeller.formatTime(new ClockForTesting(9,31,12), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("half past nine in the morning"));
    }

    @Test
    public void localTimeInWords093623(){
        assertThat(timeTeller.formatTime(new ClockForTesting(9,36,23), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("almost twenty before ten in the morning"));
    }

    @Test
    public void localTimeInWords093823(){
        assertThat(timeTeller.formatTime(new ClockForTesting(9,38,23), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("twenty before ten in the morning"));
    }

    @Test
    public void localTimeInWords094145(){
        assertThat(timeTeller.formatTime(new ClockForTesting(9,43,45), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("a quarter of ten in the morning"));
    }

    @Test
    public void localTimeInWords094945(){
        assertThat(timeTeller.formatTime(new ClockForTesting(9,49,45), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("ten of ten in the morning"));
    }

    @Test
    public void localTimeInWords095801(){
        assertThat(timeTeller.formatTime(new ClockForTesting(9,53,1), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("almost ten in the morning"));
    }

    @Test
    public void localTimeInWords120105(){
        assertThat(timeTeller.formatTime(new ClockForTesting(12,1,5), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), is("twelve in the afternoon"));
    }

    @Test
    public void localTimeInWordsCurrent(){
        assertThat(timeTeller.formatTime(new ClockLocal(), TimeZone.LOCAL, TimeFormattedAs.APPROXIMATE_WORDING), matchesPattern("^(\\s|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|twenty|almost|a|quarter|half|of|past|after|before|at|night|in|the|morning|afternoon|evening|night)+$"));
    }

    @Test
    public void zuluTimeInWordsCurrent(){
        assertThat(timeTeller.formatTime(new ClockUTC(), TimeZone.UTC, TimeFormattedAs.APPROXIMATE_WORDING), matchesPattern("^(\\s|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|twenty|almost|a|quarter|half|of|past|after|before|at|night|in|the|morning|afternoon|evening|night)+Zulu$"));
    }

    @Test
    public void emailForLocalTimeNoon(){
        Email eMail = new Email();

        try {
            eMail.send(timeTeller.formatTime(new ClockForTesting(12, 0, 0), TimeZone.LOCAL, TimeFormattedAs.NUMERIC));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        boolean receivedEmail = false;
        for (int readAttempts = 1; (readAttempts <= 5) && (!receivedEmail); readAttempts++ ) {
            receivedEmail = lookForTimeTellerEmail("12:00:00");
        }
        assertThat(receivedEmail, is(true));
    }

    private String getFormattedTime(LocalDateTime clock){
        int localHour = clock.getHour();
        int localMinute = clock.getMinute();
        int localSecond = clock.getSecond();
        return String.format("%02d:%02d:%02d", localHour, localMinute, localSecond);
    }

    private boolean lookForTimeTellerEmail(String localTimeNowFormatted){
        Properties localProperties = new Properties();
        try {
            InputStream input = new FileInputStream("config.properties");
            localProperties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean receivedEmail = false;
        IMAPFolder folder = null;
        Store store = null;
        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect(localProperties.getProperty("imap.host.to.use"),localProperties.getProperty("imap.username.to.use"), localProperties.getProperty("imap.password.to.use"));

            folder = (IMAPFolder) store.getFolder("inbox");
            if(!folder.isOpen()) {
                folder.open(Folder.READ_WRITE);
                Message[] messages = folder.getMessages();
                for (Message msg : messages) {
                    if (msg.getSubject().equals(localProperties.getProperty("email.subject"))) {
                        if (((String) msg.getContent()).contains(localTimeNowFormatted)) {
                            receivedEmail = true;
                            msg.setFlag(Flag.DELETED, true);
                        }
                    }
                }
            }
        }
        catch (Exception e) { }
        finally {
            try {
                if (folder != null && folder.isOpen()) folder.close(true);
                if (store != null) store.close();
            }
            catch (Exception e) { }
        }

        if (!receivedEmail) {
            try { TimeUnit.SECONDS.sleep(1); }
            catch(InterruptedException e){ }
        }

        return receivedEmail;
    }
}