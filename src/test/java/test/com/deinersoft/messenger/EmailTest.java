package test.com.deinersoft.messenger;

import com.deinersoft.messenger.Email;
import com.deinersoft.timeteller.ClockForTesting;
import com.deinersoft.timeteller.TimeFormatterNumeric;
import com.deinersoft.timeteller.TimeZone;
import com.sun.mail.imap.IMAPFolder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.mail.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EmailTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void emailInvalidConfigurationFile() throws Exception {
        thrown.expect(IOException.class);
        thrown.expectMessage("xyzzy.properties (No such file or directory)");

        Email eMail = new Email("xyzzy.properties");

        eMail.send(new TimeFormatterNumeric(new ClockForTesting(12,0,0, TimeZone.LOCAL)).formatTime());
    }

    @Test
    public void emailValidConfiguration() throws IOException, MessagingException {
        Email eMail = new Email("configEmail.properties");

        eMail.send(new TimeFormatterNumeric(new ClockForTesting(12,0,0, TimeZone.LOCAL)).formatTime());

        boolean receivedEmail = false;
        for (int readAttempts = 1; (readAttempts <= 5) && (!receivedEmail); readAttempts++ ) {
            receivedEmail = lookForTimeTellerEmail("12:00:00");
        }
        assertThat(receivedEmail, is(true));
    }

    private boolean lookForTimeTellerEmail(String localTimeNowFormatted){
        Properties localProperties = new Properties();
        try {
            InputStream input = new FileInputStream("configEmail.properties");
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
                            msg.setFlag(Flags.Flag.DELETED, true);
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
