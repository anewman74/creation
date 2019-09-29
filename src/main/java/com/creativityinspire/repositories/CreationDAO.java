package com.creativityinspire.repositories;


import com.creativityinspire.models.Creation;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CreationDAO {

    public List<Creation> getCreations(HttpSession session) throws SQLException;

    public List<Creation> getMyCreations(String username, String sessionLoggedInUserValue) throws SQLException;

    public List<Creation> getPopularCreations() throws SQLException;

    public Map<String, String> saveCreation(String title, String poem, String username, boolean isPrivate, String phoneId) throws SQLException;

    public Map<String, String> updateCreation(String title, String poem, String username, boolean isPrivate, String phoneId) throws SQLException;

    public Map<String, String> deleteCreation(String title, String username, String phoneId, boolean isPrivate) throws SQLException;

    public void countReadMoreClick(String title, String username) throws SQLException;

    public int getNumberClicksToday(String title, String username) throws SQLException;
}
