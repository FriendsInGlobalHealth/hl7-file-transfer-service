package hl7.file.transfer.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    public static final String EMAIL_SUBJECT = "Erro ao transferir ficheiro HL7 -  %s";
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.list}")
    private String mailList;

    @Override
    public void sendEmail(final String mailMessage, String healthFacility) throws MessagingException, UnsupportedEncodingException {
        // Prepare the evaluation context
        final Context ctx = new Context(new Locale("pt", "BR"));
        ctx.setVariable("message", mailMessage);

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject(String.format(EMAIL_SUBJECT,healthFacility));
        message.setFrom(this.fromEmail,"[DISA_EPTS]");
        String [] mailList=this.mailList.split(",");
        message.setTo(mailList);

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.templateEngine.process("index.html", ctx);
        message.setText(htmlContent, true); // true = isHtml


        // Send mail
        this.mailSender.send(mimeMessage);
    }
}
