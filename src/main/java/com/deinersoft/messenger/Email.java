package com.deinersoft.messenger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Email {
    String eMailConfigurationFile;

    public Email(String eMailConfigurationFile) {
        this.eMailConfigurationFile = eMailConfigurationFile;
    }

    public void send(String formattedTime) throws MessagingException, IOException {
        Properties localConfiguration = getLocalConfiguration();
        Session eMailSession = getEmailSession(localConfiguration);
        Message message = new MimeMessage(eMailSession);
        message.setFrom(new InternetAddress(localConfiguration.getProperty("email.sender")));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(localConfiguration.getProperty("email.recipient")));
        message.setSubject(localConfiguration.getProperty("email.subject"));
        message.setText(localConfiguration.getProperty("email.message") + " " + formattedTime);

        Transport.send(message);
    }

    private Session getEmailSession(Properties localConfiguration) {
        Properties systemConfiguration = getSystemConfiguration(localConfiguration);
        return Session.getInstance(systemConfiguration,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(localConfiguration.getProperty("smtp.username.to.use"), localConfiguration.getProperty("smtp.password.to.use"));
                    }
                });
    }

    private Properties getLocalConfiguration() throws IOException {
        Properties localConfiguration = new Properties();

        InputStream input = new FileInputStream(eMailConfigurationFile);
        localConfiguration.load(input);

        return localConfiguration;
    }

    private Properties getSystemConfiguration(Properties localConfiguration) {
        Properties systemProperties = System.getProperties();

        systemProperties.put("mail.smtp.auth", localConfiguration.getProperty("smtp.authentication.enabled"));
        systemProperties.put("mail.smtp.starttls.enable", localConfiguration.getProperty("smtp.starttls.enabled"));
        systemProperties.put("mail.smtp.host", localConfiguration.getProperty("smtp.host.to.use"));
        systemProperties.put("mail.smtp.port", localConfiguration.getProperty("smtp.port.to.use"));

        return systemProperties;
    }

}