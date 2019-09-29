package com.creativityinspire.services;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;

import com.creativityinspire.models.Creation;
import org.springframework.stereotype.Service;

@Service
public interface CreationService {

    public List<Creation> creationsOnPageLoad(HttpSession session);

    public List<Creation> getAllCreations(HttpSession session);

    public List<Creation> getMyCreations(String username, String sessionLoggedInUserValue);

    public List<Creation> getPopularCreations();

    public Map<String, String> saveCreation(HttpSession session, String title, String poem, String username, String userValue, boolean isPrivate, String phoneId);

    public Map<String, String> updateCreation(HttpSession session, String title, String poem, String username, String userValue, boolean isPrivate, String phoneId);

    public Map<String, String> deleteCreation(HttpSession session, String title, String username, String userValue, String phoneId, boolean isPrivate);

    public void countReadMoreClick(HttpSession session, String title, String username);

    public int getNumberClicksToday(String title, String username);
}