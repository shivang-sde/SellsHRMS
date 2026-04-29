package com.sellspark.SellsHRMS.notification.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class SystemNotificationConfig {

    /**
     * System mail sender for platform-level notifications
     * Configure via application.properties:
     * notification.system.smtp-host=...
     * notification.system.smtp-port=...
     * etc.
     */

    @Bean(name = "systemMailSender")
    public JavaMailSender systemMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setJavaMailProperties(systemMailProperties());
        return mailSender;
    }

    private Properties systemMailProperties() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "10000");
        return props;
    }
}
