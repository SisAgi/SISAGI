package com.agibank.sisagi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("sandbox.smtp.mailtrap.io"); // Replace with your SMTP host
        mailSender.setPort(2525); // Replace with your SMTP port (e.g., 587 for TLS, 465 for SSL)
        mailSender.setUsername("00d3457a43d624"); // Replace with your email
        mailSender.setPassword("84220ba943fdf4"); // Replace with your email password or app password

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "false"); // Or "mail.smtp.ssl.enable" for SSL
        props.put("mail.debug", "true"); // Enable for debugging mail issues

        return mailSender;
    }
}