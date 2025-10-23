package com.rating.punctuality.rating_punctuality.config;

import org.apache.commons.mail.SimpleEmail;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailSending {
    public static void sendMail(String emailTo, String message) {
        SimpleEmail email = null;
        try {
            log.info("Начинаем отправку email...");
            
            email = new SimpleEmail();

            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587);
            email.setStartTLSEnabled(true);
            email.setStartTLSRequired(true);
            email.setSSLOnConnect(false);
            email.setSocketTimeout(30000);
            email.setSocketConnectionTimeout(15000);

            email.setAuthentication("", ""); // TODO

            email.setFrom(""); // TODO
            email.addTo(emailTo);
            email.setSubject("Тестовое письмо"); 
            email.setMsg(message);

            log.info("Вызываем отправку...");
            String result = email.send();
            
            log.info("Письмо успешно отправлено! Результат: {}", result);

        } catch (Exception e) {
            log.error("ПОЛНАЯ ОШИБКА при отправке email:", e);
            log.error("Тип ошибки: {}", e.getClass().getName());
            log.error("Сообщение: {}", e.getMessage());
        }
    }
}