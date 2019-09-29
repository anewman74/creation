package com.creativityinspire.repositories;

import com.creativityinspire.models.ConnectResponse;
import com.creativityinspire.models.Creator;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CreatorDAO {

    public List<Creator> getCreators() throws SQLException;

    public ConnectResponse signupCreator(String username, String email, String password, String phoneDevice) throws SQLException;

    public ConnectResponse loginCreator(String email, String password, String phoneDevice) throws SQLException;

    ConnectResponse logoutCreator() throws SQLException;

    public Map<String,String> forgotPassword(String username, String email, String newPassword) throws SQLException;

    Map<String,String> changePassword(String username, String email, String oldPassword, String newPassword) throws SQLException;

    Map<String,String> changeEmail(String username, String oldEmail, String newEmail) throws SQLException;
}
