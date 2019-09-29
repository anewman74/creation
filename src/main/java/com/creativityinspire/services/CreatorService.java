package com.creativityinspire.services;

import java.util.List;
import java.util.Map;

import com.creativityinspire.models.ConnectResponse;
import com.creativityinspire.models.Creator;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public interface CreatorService {

    public List<Creator> getCreators (String email);

    public ConnectResponse signupCreator(String username, String email, String password, String phoneDevice);

    public ConnectResponse loginCreator(String email, String password, String phoneDevice);

    public ConnectResponse logoutCreator();

    public Map<String,String> forgotPassword(String username, String email);

    public Map<String,String> changePassword(HttpSession session, String username, String userValue, String email, String oldPassword, String newPassword);

    public Map<String,String> changeEmail(HttpSession session, String username, String userValue, String oldEmail, String newEmail);

    Map<String,String> contactUs(String username, String email, String subject, String comment);
}