package com.creativityinspire.services;

import com.creativityinspire.Utils.Utility;
import com.creativityinspire.models.ConnectResponse;
import com.creativityinspire.models.Creator;
import com.creativityinspire.repositories.CreatorDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class CreatorServiceImpl implements CreatorService {

    private CreatorDAO _creatorDAO;
    private Utility _utility;

    @Autowired
    public CreatorServiceImpl(CreatorDAO creatorDAO, Utility utility) {
        _creatorDAO = creatorDAO;
        _utility = utility;
    }

    public List<Creator> getCreators(String email) {

        List<Creator> _creators = null;
        try {
            _creators = _creatorDAO.getCreators();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return _creators;
    }

    public ConnectResponse signupCreator(String username, String email, String password, String phoneDevice) {

        ConnectResponse connectResponse = new ConnectResponse();
        try {
            connectResponse = _creatorDAO.signupCreator(username, email, _utility.getHashPasword(password), phoneDevice);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connectResponse;
    }

    public ConnectResponse loginCreator(String email, String password, String phoneDevice) {
        ConnectResponse connectResponse = new ConnectResponse();
        try {
            connectResponse = _creatorDAO.loginCreator(email, _utility.getHashPasword(password), phoneDevice);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connectResponse;
    }

    @Override
    public ConnectResponse logoutCreator() {
        ConnectResponse connectResponse = new ConnectResponse();
        try {
            connectResponse = _creatorDAO.logoutCreator();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connectResponse;
    }


    public Map<String, String> forgotPassword(String username, String email) {
        Map<String, String> _forgotPasswordResponse = new HashMap<String, String>();
        try {
            String newPassword =_utility.getNewRandomPassword();
            _forgotPasswordResponse = _creatorDAO.forgotPassword(username, email, newPassword);
            if (_forgotPasswordResponse.get("username") != null) {
                String emailMessage = sendEmail(email, _forgotPasswordResponse.get("username"), newPassword);
                if (emailMessage.equals("Error sending email. Please try again now or later in the day.")) {
                    _forgotPasswordResponse.put("error", emailMessage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return _forgotPasswordResponse;
    }

    private String sendEmail(final String email, final String username, final String newPassword) {

        final String host_email = "psmith83884@gmail.com";
        final String host_password = "popppp46";
        String emailMessage;

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(host_email, host_password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(host_email));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject("Creativity Inspire Message");
            message.setText("Dear " + username + ","
                    + "\n\n Log in using this password: " + newPassword +
                    "\n\nCopy and paste it in the password field of the Log In form." +
                    "\n\nOnce signed in, go to settings and reset your password." +
                            "\n\nEnjoy creating!");

            Transport.send(message);

            System.out.println("Email was sent.");
            emailMessage =  "Email was sent";

        } catch (MessagingException e) {
            emailMessage = "Error sending email. Please try again now or later in the day.";
            e.printStackTrace();
        }
        return emailMessage;
    }

    @Override
    public Map<String, String> changePassword(HttpSession session, String username, String userValue, String email, String oldPassword, String newPassword) {
        Map<String, String> _response = new HashMap<String, String>();
        if (session != null && session.getAttribute("userValue") != null && session.getAttribute("userValue").equals(userValue)) {
            try {
                _response = _creatorDAO.changePassword(username, email, oldPassword, newPassword);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            _response.put("response", "Server Transaction Error");
        }
        return _response;
    }

    @Override
    public Map<String, String> changeEmail(HttpSession session, String username, String userValue, String oldEmail, String newEmail) {
        Map<String, String> _response = new HashMap<String, String>();
        if (session != null && session.getAttribute("userValue") != null && session.getAttribute("userValue").equals(userValue)) {
            try {
                _response = _creatorDAO.changeEmail(username, oldEmail, newEmail);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            _response.put("response", "Server Transaction Error");
        }
        return _response;
    }

    @Override
    public Map<String, String> contactUs(String username, String email, String subject, String comment) {
        Map<String, String> _response = new HashMap<String, String>();
        final String host_email = "psmith83884@gmail.com";
        final String host_password = "popppp46";
        String title = "Contact Us: " + subject;

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(host_email, host_password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(host_email));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("psmith8393@yahoo.com"));
            message.setSubject(title);
            message.setText(
                    comment +
                    "\n\nFrom " + username + "." +
                    "\n\nemail: " + email + ".");

            Transport.send(message);

            System.out.println("Email was sent.");
            _response.put("response", "We have received your message. Thank you.");

        } catch (MessagingException e) {
            _response.put("error", "There was a server error. Please try again now or later in the day.");
            e.printStackTrace();
        }
        return _response;
    }
}
