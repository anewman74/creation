package com.creativityinspire.controllers;


import com.creativityinspire.models.ConnectResponse;
import com.creativityinspire.models.Creator;
import com.creativityinspire.services.CreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class CreatorController {

    private final CreatorService _creatorService;

    @Autowired
    public CreatorController(CreatorService creatorService) {
        _creatorService = creatorService;
    }

    @RequestMapping(value = "/creator", method = RequestMethod.GET)
    @ResponseBody
    public List<Creator> creator(@RequestParam(value = "email", required = true) String email) {
        return _creatorService.getCreators(email);
    }

    @RequestMapping(value = "/signupCreatorFromPhone", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> signupCreatorFromPhone(@RequestParam(value = "username", required = true) String username,
                                                      @RequestParam(value = "email", required = true) String email,
                                                      @RequestParam(value = "password", required = true) String password,
                                                      @RequestParam(value = "phoneDevice", required = true) String phoneDevice
    ) {
        System.out.println("inside signupCreatorFromPhone, username: " + username + ", phone device: " + phoneDevice);
        ConnectResponse connectResponse = _creatorService.signupCreator(username, email, password, phoneDevice);
        return connectResponse.getResponseValues();
    }


    @RequestMapping(value = "/loginCreatorFromPhone", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> loginCreator(@RequestParam(value = "email", required = true) String email,
                                            @RequestParam(value = "password", required = true) String password,
                                            @RequestParam(value = "phoneDevice", required = true) String phoneDevice
    ) {
        System.out.println("inside login creator from phone, email: " + email + ", phone device: " + phoneDevice);
        ConnectResponse connectResponse = _creatorService.loginCreator(email, password, phoneDevice);
        return connectResponse.getResponseValues();
    }


    /* Methods for website */
    @RequestMapping(value = "/signupCreator", method = RequestMethod.POST)
    @ResponseBody
    public ConnectResponse signupCreator(HttpSession session,
                                         @RequestParam(value = "username", required = true) String username,
                                         @RequestParam(value = "email", required = true) String email,
                                         @RequestParam(value = "password", required = true) String password
    ) {
        System.out.println("inside signup creator");
        ConnectResponse connectResponse = _creatorService.signupCreator(username, email, password, null);
        Map<String, String> responseMap = connectResponse.getResponseValues();

        if (responseMap.get("error") == null) {
            System.out.println("inside signup creator get my creations, username: " + username);
            System.out.println("inside signup creator get my creations, response: " + responseMap.get("response"));
            session.setAttribute("username", username);
            session.setAttribute("userValue", responseMap.get("response"));
        }
        return connectResponse;
    }


    @RequestMapping(value = "/loginCreator", method = RequestMethod.POST)
    @ResponseBody
    public ConnectResponse loginCreator(HttpSession session,
                                        @RequestParam(value = "email", required = true) String email,
                                        @RequestParam(value = "password", required = true) String password

    ) {
        System.out.println("inside login creator");
        ConnectResponse connectResponse = _creatorService.loginCreator(email, password, null);
        Map<String, String> responseMap = connectResponse.getResponseValues();
        if (responseMap.get("error") == null) {
            System.out.println("inside login creator, username: " + responseMap.get("username"));
            System.out.println("inside login creator, response (userValue): " + responseMap.get("response"));
            session.setAttribute("username", responseMap.get("username"));
            session.setAttribute("userValue", responseMap.get("response"));

            System.out.println("inside website login creator, session id:  " + session.getId());
            System.out.println("inside website login creator, session.getAttribute userValue:  " + session.getAttribute("userValue"));
        }
        return connectResponse;
    }


    @RequestMapping(value = "/logoutCreator", method = RequestMethod.POST)
    @ResponseBody
    public ConnectResponse logoutCreator(HttpSession session) {
        ConnectResponse connectResponse = _creatorService.logoutCreator();
        System.out.println("inside logout creator, username: " + session.getAttribute("username"));
        session.setAttribute("username", null);
        session.setAttribute("userValue", null);
        System.out.println("inside logout creator, username: " + session.getAttribute("username"));
        System.out.println("inside logout creator, userValue: " + session.getAttribute("userValue"));
        return connectResponse;
    }


    @RequestMapping(value = "/contactUs", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> contactUs(@RequestParam(value = "username", required = true) String username,
                                         @RequestParam(value = "email", required = true) String email,
                                         @RequestParam(value = "subject", required = true) String subject,
                                         @RequestParam(value = "comment", required = true) String comment

    ) {
        System.out.println("inside contact us");
        Map<String, String> _contactUsResponse;
        _contactUsResponse = _creatorService.contactUs(username, email, subject, comment);
        if (_contactUsResponse.get("error") == null) {
            System.out.println("inside contact us, response: " + _contactUsResponse.get("response"));
        }
        return _contactUsResponse;
    }


    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> forgotPassword(HttpSession session,
                                              @RequestParam(value = "username", required = true) String username,
                                              @RequestParam(value = "email", required = true) String email

    ) {
        System.out.println("inside forgot password");
        Map<String, String> _forgotPasswordResponse;
        _forgotPasswordResponse = _creatorService.forgotPassword(username, email);
        if (_forgotPasswordResponse.get("error") == null) {
            System.out.println("inside forgot password, response: " + _forgotPasswordResponse.get("response"));
        }
        return _forgotPasswordResponse;
    }


    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> changePassword(HttpSession session,
                                              @RequestParam(value = "username", required = true) String username,
                                              @RequestParam(value = "userValue", required = true) String userValue,
                                              @RequestParam(value = "email", required = true) String email,
                                              @RequestParam(value = "oldPassword", required = true) String oldPassword,
                                              @RequestParam(value = "newPassword", required = true) String newPassword

    ) {
        System.out.println("inside change password");
        Map<String, String> _changePasswordResponse;
        _changePasswordResponse = _creatorService.changePassword(session, username, userValue, email, oldPassword, newPassword);
        if (_changePasswordResponse.get("error") == null) {
            System.out.println("inside change password, response: " + _changePasswordResponse.get("response"));
        }
        return _changePasswordResponse;
    }


    @RequestMapping(value = "/changeEmail", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> changeEmail(HttpSession session,
                                           @RequestParam(value = "username", required = true) String username,
                                           @RequestParam(value = "userValue", required = true) String userValue,
                                           @RequestParam(value = "oldEmail", required = true) String oldEmail,
                                           @RequestParam(value = "newEmail", required = true) String newEmail

    ) {
        System.out.println("inside change email");
        Map<String, String> _changeEmailResponse;
        _changeEmailResponse = _creatorService.changeEmail(session, username, userValue, oldEmail, newEmail);
        if (_changeEmailResponse.get("error") == null) {
            System.out.println("inside change password, response: " + _changeEmailResponse.get("response"));
        }
        return _changeEmailResponse;
    }
}


