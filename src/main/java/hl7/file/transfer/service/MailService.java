package hl7.file.transfer.service;


import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface MailService {
    void sendEmail(String message, final String healthFacility) throws MessagingException, UnsupportedEncodingException;

}
