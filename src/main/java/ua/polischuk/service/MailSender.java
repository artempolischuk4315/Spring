package ua.polischuk.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.polischuk.entity.Test;
import ua.polischuk.entity.User;
import ua.polischuk.repository.TestRepository;
import ua.polischuk.repository.UserRepository;

import java.util.ResourceBundle;

@Slf4j
@Service
public class MailSender {

    private JavaMailSender mailSender;

    private UserRepository userRepository;

    ResourceBundle bundle = ResourceBundle.getBundle("adminAndMail");

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    public MailSender(JavaMailSender mailSender, UserRepository userRepository, TestRepository testRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;

    }


    public void send(String emailTo, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }


    public void sendResult(UserDetails userDetails,  Test test){

        log.info("IN MAIL");
        String email = userDetails.getUsername();
        log.info("EMAIL IS "+email);
        User user =userRepository.findByEmail(email).get();
        log.info(user.getResultsOfTests().toString());
        Integer result = user.getResultsOfTests().get(test);
        log.info("RESULT "+result);
        log.info("TEST "+test.getName());
        send(email, email, bundle.getString("mail.message")
                + test.getName()+ bundle.getString("is")+result.toString());

        log.info("MAIL");
    }
}