package com.creativityinspire.controllers;

import com.creativityinspire.models.Creation;
import com.creativityinspire.services.CreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class CreationController extends HttpServlet {

    private final CreationService _creationService;

    @Autowired
    public CreationController(CreationService creationService) {
        _creationService = creationService;
    }

    @RequestMapping("/layout")
    public String getCreationLayout() {
        return "creations/layout";
    }

    @RequestMapping("/mobile-layout")
    public String getCreationMobileLayout() {
        return "creations/mobile-layout";
    }

    @RequestMapping(value = "/creationlist.json", method = RequestMethod.GET)
    @ResponseBody
    public List<Creation> creationlist(HttpServletRequest request,
                                       HttpServletResponse response) throws ServletException, IOException {

        // Create a session object if it is not already created.
        HttpSession session = request.getSession(true);
        // Get session creation time.
        Date createTime = new Date(session.getCreationTime());
        System.out.println("on page load, createTime: " + createTime);
        System.out.println("session id: " + session.getId());
        System.out.println("on page load, session user value: " + session.getAttribute("userValue"));
        return _creationService.creationsOnPageLoad(session);
    }

    @RequestMapping(value = "/myCreations", method = RequestMethod.POST)
    @ResponseBody
    public List<Creation> myCreations(@RequestParam(value = "username", required = true) String username
    ) {
        System.out.println("inside my creations - used in Phone Apps."); // need to give phone a value each time they login and they need to use same value always.
        // also need to know how the action is coming from a phone and not from a browser which will have the session check.
        String userValue = "Phone already has value";
        // Note:  _creationService.getMyCreations is called in _creationService.creationsOnPageLoad() to send the userValue to the front end.
        return _creationService.getMyCreations(username, userValue);
    }

    @RequestMapping(value = "/allCreations", method = RequestMethod.GET)
    @ResponseBody
    public List<Creation> allCreations(HttpSession session) {
        System.out.println("show all creations");
        return _creationService.getAllCreations(session);
    }


    @RequestMapping(value = "/popularCreations", method = RequestMethod.GET)
    @ResponseBody
    public List<Creation> popularCreations() {
        System.out.println("show popular creations");
        return _creationService.getPopularCreations();
    }


    @RequestMapping(value = "/saveCreation", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> saveCreation(HttpSession session,
                                            @RequestParam(value = "title", required = true) String title,
                                            @RequestParam(value = "poem", required = true) String poem,
                                            @RequestParam(value = "username", required = true) String username,
                                            @RequestParam(value = "userValue", required = true) String userValue,
                                            @RequestParam(value = "isPrivate", required = true) boolean isPrivate
    ) {
        System.out.println("inside website save creation");
        System.out.println("inside website save creation, session:  " + session);
        System.out.println("inside website save creation, session.getAttribute userValue:  " + session.getAttribute("userValue"));
        System.out.println("creation save, isPrivate: " + isPrivate);
        return _creationService.saveCreation(session, title, poem, username, userValue, isPrivate, null);
    }

    @RequestMapping(value = "/updateCreation", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> updateCreation(HttpSession session,
                                              @RequestParam(value = "title") String title,
                                              @RequestParam(value = "poem") String poem,
                                              @RequestParam(value = "username") String username,
                                              @RequestParam(value = "userValue", required = true) String userValue,
                                              @RequestParam(value = "isPrivate", required = true) boolean isPrivate
    ) {
        System.out.println("inside website update creation");
        System.out.println("creation update, isPrivate: " + isPrivate);
        return _creationService.updateCreation(session, title, poem, username, userValue, isPrivate, null);
    }


    @RequestMapping(value = "/deleteCreation", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> deleteCreation(HttpSession session,
                                              @RequestParam(value = "title", required = true) String title,
                                              @RequestParam(value = "username", required = true) String username,
                                              @RequestParam(value = "userValue", required = true) String userValue
    ) {
        return _creationService.deleteCreation(session, title, username, userValue, null, false);
    }


    @RequestMapping(value = "/countReadMoreClick", method = RequestMethod.POST)
    @ResponseBody
    public void countReadMoreClick(HttpSession session,
                                   @RequestParam(value = "title", required = true) String title,
                                   @RequestParam(value = "username", required = true) String username
    ) {
        System.out.println("count read more clicks");
        _creationService.countReadMoreClick(session, title, username);
    }


    @RequestMapping(value = "/saveCreationFromPhone", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> saveCreationFromPhone(@RequestParam(value = "title", required = true) String title,
                                                     @RequestParam(value = "poem", required = true) String poem,
                                                     @RequestParam(value = "username", required = true) String username,
                                                     @RequestParam(value = "phoneId", required = true) String phoneId
    ) {
        System.out.println("saveCreationFromPhone, phoneID: " + phoneId);
        // By default, creations from the phone are not private (isPrivate false).
        return _creationService.saveCreation(null, title, poem, username, null, false, phoneId);
    }

    @RequestMapping(value = "/updateCreationFromPhone", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> updateCreationFromPhone(@RequestParam(value = "title") String title,
                                                       @RequestParam(value = "poem") String poem,
                                                       @RequestParam(value = "username") String username,
                                                       @RequestParam(value = "phoneId", required = true) String phoneId
    ) {
        System.out.println("updateCreationFromPhone, phoneID: " + phoneId);
        // By default, creations from the phone are not private (isPrivate false).
        return _creationService.updateCreation(null, title, poem, username, null, false, phoneId);
    }

    @RequestMapping(value = "/deleteCreationFromPhone", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> deleteCreationFromPhone(@RequestParam(value = "title", required = true) String title,
                                                       @RequestParam(value = "username", required = true) String username,
                                                       @RequestParam(value = "phoneId", required = true) String phoneId,
                                                       @RequestParam(value = "isPrivate", required = true) boolean isPrivate
    ) {
        return _creationService.deleteCreation(null, title, username, null, phoneId, isPrivate);
    }
}


//this requires http://localhost:8080/creations/myCreations?username=Walt Whitman
//    @RequestMapping(value = "/myCreations", method = RequestMethod.GET)
//    @ResponseBody
//    public List<Creation> personalCreationsGet(@RequestParam(value = "username", required = true) String username
//    ) {
//        System.out.println("inside my creations");
//        return getCreationService().getMyCreations(username);
//    }

//    //  I could use this with a like statement to do a word search like LOVE.
//    @RequestMapping(value = "/myCreations/{username}", method = RequestMethod.POST)
//    @ResponseBody
//    public List<Creation> myCreations(@PathVariable("username") String username) {
//        return getCreationService().getMyCreations(username);
//    }

//    @RequestMapping(value = "/loginCreator", method = RequestMethod.POST)
//    @ResponseBody
//    public List<Creation> loginCreator(HttpServletRequest request,
//                                      HttpServletResponse response, @RequestParam(value="email", required=true) String email,
//                                 @RequestParam(value="password", required=true) String password
//
//    ) {
//        System.out.println("inside login creator");
//
//        myServiceMethodSettingCookie(request, response);
//
//        List<Creator> creator =  getCreatorService().loginCreator(email, password);
//        List<Creation> creations = new ArrayList<Creation>();
//
//        if(creator.size() == 1) {
//            String username = creator.get(0).getUsername();
//            System.out.println("inside login creator get my creations, username: " + username);
//            creations =  getCreationService().getMyCreations(username);
//        }
//        return creations;
//    }

// http://stackoverflow.com/questions/11704069/servletdispatcher-cannot-be-cast-to-javax-servlet-servlet-exception-in-my-spring

//    // service method - put in service class.
//    void myServiceMethodSettingCookie(HttpServletRequest request, HttpServletResponse response) {
//        final String cookieName = "creationSession";
//        final String cookieValue = "my encoded value";  // you could assign it some encoded value
//        final Boolean useSecureCookie = new Boolean(false);
//        final int expiryTime = 60 * 60 * 24;  // 24h in seconds
//        final String cookiePath = "/";
//
//        Cookie cookie = new Cookie(cookieName, cookieValue);
//
//        cookie.setSecure(useSecureCookie.booleanValue());  // determines whether the cookie should only be sent using a secure protocol, such as HTTPS or SSL
//
//        cookie.setMaxAge(expiryTime);  // A negative value means that the cookie is not stored persistently and will be deleted when the Web browser exits. A zero value causes the cookie to be deleted.
//
//        cookie.setPath(cookiePath);  // The cookie is visible to all the pages in the directory you specify, and all the pages in that directory's subdirectories
//
//        response.addCookie(cookie);
//    }

