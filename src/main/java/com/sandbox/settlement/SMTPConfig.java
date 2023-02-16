package com.sandbox.settlement;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**--------------------------------------------------------------------
 * ■SMTP 환경 설정 ■sangheon
 --------------------------------------------------------------------**/
@Data
@Configuration
@ConfigurationProperties(prefix="smtp")
public class SMTPConfig {
    private String strHost;
    private String strPort;

    /**--------------------------------------------------------------------
     * ■서비스 등록 ■sangheon
     --------------------------------------------------------------------**/
    @Bean
    public JavaMailSender getMailSender() {
        JavaMailSenderImpl objJavaMailSenderImpl = new JavaMailSenderImpl();

        objJavaMailSenderImpl.setJavaMailProperties(getMailProperties());

        return objJavaMailSenderImpl;
    }

    /**--------------------------------------------------------------------
     * ■SMTP 프로퍼티 설정 ■sangheon
     --------------------------------------------------------------------**/
    private Properties getMailProperties() {
        Properties properties = new Properties();

        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.ssl.trust", getStrHost());
        properties.setProperty("mail.smtp.host", getStrHost());
        properties.setProperty("mail.smtp.auth", "false");
        properties.setProperty("mail.smtp.port", getStrPort());
        properties.setProperty("mail.smtp.socketFactory.port", getStrPort());
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        return properties;
    }
}
