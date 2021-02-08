package com.vut.fit.gja2020.app.utils;

import com.vut.fit.gja2020.app.beans.SMTP;
import com.vut.fit.gja2020.app.models.SMTPSettings;
import com.vut.fit.gja2020.app.repository.SMTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
public class MailingUtility {

    @Autowired
    SMTPRepository smtpRepository;

    /**
     * Sends email message to specific email address with specific subject and content, using database stored SMTP settings
     *
     * @param to
     * @param subject
     * @param content
     * @throws MessagingException
     */
    public void sendMail(String to, String subject, String content) throws MessagingException {
        SMTPSettings settings = smtpRepository.findByName(SMTP.getSmtpServiceName());

        String from = settings.getFrom();
        String host  = settings.getHost();
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", settings.getPort());
        properties.put("mail.smtp.ssl.enable", settings.isSsl());
        properties.put("mail.smtp.auth", settings.isAuth());

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(settings.getUsername(), settings.getPassword());
            }
        });

        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(session);

        // Set From: header field of the header.
        message.setFrom(new InternetAddress(from));

        // Set To: header field of the header.
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        // Set Subject: header field
        message.setSubject(subject);

        // Now set the actual message
        message.setText(content);

        // Send message
        Transport.send(message);
    }

}
