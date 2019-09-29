package com.creativityinspire.services;

import com.creativityinspire.Utils.Utility;
import com.creativityinspire.models.Creation;
import com.creativityinspire.repositories.CreationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.*;

@Service
public class CreationServiceImpl implements CreationService {

    private CreationDAO _creationDAO;
    private Utility _utility;

    @Autowired
    public CreationServiceImpl(CreationDAO creationDAO, Utility utility) {
        _creationDAO = creationDAO;
        _utility = utility;
    }

    public List<Creation> creationsOnPageLoad(HttpSession session) {
        List<Creation> _creations;

        // Need to set a session attribute so that we can count the number of views a poem gets based on unique sessions.
        if (session.getAttribute("uniqueSessionId") == null) {
            String time = String.valueOf(_utility.getCurrentTimeStamp());
            Random randomGenerator = new Random();
            String uniqueSessionId = String.valueOf(randomGenerator.nextInt(1000000000)) + " " + time;
            session.setAttribute("uniqueSessionId", uniqueSessionId);
        }

        if (session.getAttribute("username") != null && session.getAttribute("userValue") != null) {
            String username = (String) session.getAttribute("username");
            String userValue = (String) session.getAttribute("userValue");
            System.out.println("inside creation list, username: " + username);
            System.out.println("inside creation list, userValue: " + userValue);
            _creations = getMyCreations(username, userValue);

            // If the user has no personal creations, then show all creations and add an extra creation with no title, poem but with username and uservalue
            // will be used on the front end to save new creations.
            if (_creations.size() == 0) {
                _creations = getAllCreations(session);
                if (_creations.size() > 0) {
                    Creation creation = new Creation("", "", username, userValue, false);
                    _creations.add(creation);
                }
            }
        } else {
            System.out.println("inside creation list no username.");
            _creations = getAllCreations(session);
        }
        return _creations;
    }

    public List<Creation> getAllCreations(HttpSession session) {

        List<Creation> _creations = new ArrayList<Creation>();
        try {
            _creations = _creationDAO.getCreations(session);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return _creations;
    }

    public List<Creation> getMyCreations(String username, String sessionLoggedInUserValue) {

        List<Creation> _creations = new ArrayList<Creation>();
        try {
            _creations = _creationDAO.getMyCreations(username, sessionLoggedInUserValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return _creations;
    }

    public List<Creation> getPopularCreations() {
        List<Creation> _creations = new ArrayList<Creation>();
        try {
            _creations = _creationDAO.getPopularCreations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return _creations;
    }

    public Map<String, String> saveCreation(HttpSession session, String title, String poem, String username, String userValue, boolean isPrivate, String phoneId) {
        Map<String, String> _response = new HashMap<String, String>();

        if (session != null && session.getAttribute("userValue") != null && session.getAttribute("userValue").equals(userValue)) {
            try {
                System.out.println("inside website save creation impl if");
                _response = _creationDAO.saveCreation(title, poem, username, isPrivate, phoneId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if(phoneId != null) {
            try {
                _response = _creationDAO.saveCreation(title, poem, username, isPrivate, phoneId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            _response.put("response", "Server Transaction Error");
        }
        return _response;
    }

    public Map<String, String> updateCreation(HttpSession session, String title, String poem, String username, String userValue, boolean isPrivate, String phoneId) {
        Map<String, String> _response = new HashMap<String, String>();
        if (session != null && session.getAttribute("userValue") != null && session.getAttribute("userValue").equals(userValue)) {
            try {
                _response = _creationDAO.updateCreation(title, poem, username, isPrivate, phoneId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if(phoneId != null) {
            try {
                _response = _creationDAO.updateCreation(title, poem, username, isPrivate, phoneId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            _response.put("response", "Server Transaction Error");
        }
        return _response;
    }

    public Map<String, String> deleteCreation(HttpSession session, String title, String username, String userValue, String phoneId, boolean isPrivate) {
        Map<String, String> _response = new HashMap<String, String>();
        if (session != null && session.getAttribute("userValue") != null && session.getAttribute("userValue").equals(userValue)) {
            try {
                _response = _creationDAO.deleteCreation(title, username, null, isPrivate);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if(phoneId != null) {
            try {
                _response = _creationDAO.deleteCreation(title, username, phoneId, isPrivate);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            _response.put("response", "Server Transaction Error");
        }
        return _response;
    }

    @SuppressWarnings("unchecked")
    public void countReadMoreClick(HttpSession session, String title, String username) {
        String uniqueSessionTitleUsername = session.getAttribute("uniqueSessionId") + " " + title + " " + username;
        List<String> uniqueTitleUsernamesOnSession = new ArrayList<String>();
        int numberOfClicks = getNumberClicksToday(title, username);

        if (numberOfClicks < 5) { // you can only count 5 clicks per day per poem.
            if (session.getAttribute("uniqueSessionTitleUsernames") != null) {
                uniqueTitleUsernamesOnSession = (List<String>) session.getAttribute("uniqueSessionTitleUsernames");
            }
            System.out.println("inside countReadMoreClick, uniqueTitleUsernamesOnSession: " + uniqueTitleUsernamesOnSession);
            if (uniqueTitleUsernamesOnSession.size() == 0) {
                uniqueTitleUsernamesOnSession.add(uniqueSessionTitleUsername);
                session.setAttribute("uniqueSessionTitleUsernames", uniqueTitleUsernamesOnSession);
                try {
                    _creationDAO.countReadMoreClick(title, username);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (!uniqueTitleUsernamesOnSession.contains(uniqueSessionTitleUsername)) {
                System.out.println("inside countReadMoreClick else if, uniqueSessionTitleUsername: " + uniqueSessionTitleUsername);
                uniqueTitleUsernamesOnSession.add(uniqueSessionTitleUsername);
                session.setAttribute("uniqueSessionTitleUsernames", uniqueTitleUsernamesOnSession);
                try {
                    _creationDAO.countReadMoreClick(title, username);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getNumberClicksToday(String title, String username) {
        int numberClicks = 0;
        try {
            numberClicks = _creationDAO.getNumberClicksToday(title, username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numberClicks;
    }
}

