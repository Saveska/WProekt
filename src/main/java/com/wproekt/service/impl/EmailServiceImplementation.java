package com.wproekt.service.impl;

import com.wproekt.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class EmailServiceImplementation implements EmailService {


    private final JavaMailSender mailSender;
    private TemplateEngine templateEngine; // From Thymeleaf

    public EmailServiceImplementation(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendTokenMail(String to, String token, String host,String username) {


        MimeMessage message = mailSender.createMimeMessage();



        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("mindmapprapp@gmail.com");
            helper.setTo(to);
            helper.setSubject("MindMappr Email Validation");
            //TODO: ne mu dozvoluvaj username da e so /
            String link = host+"/verify/"+username+'/'+token;
            String processedHTMLTemplate = this.constructHTMLTemplate(link);

            helper.setText(processedHTMLTemplate, true);


            mailSender.send(message);
        }
        catch (Exception e){
            System.out.println("GRESKA");
            System.out.println(e.toString());
        }


    }

    private String constructHTMLTemplate(String link) {
        Context context = new Context();
        context.setVariable("link", link);
        return templateEngine.process("mailTemplate.html", context);
    }
}
