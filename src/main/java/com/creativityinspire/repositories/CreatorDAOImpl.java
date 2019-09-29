package com.creativityinspire.repositories;

import com.creativityinspire.Utils.Utility;
import com.creativityinspire.models.ConnectResponse;
import com.creativityinspire.models.Creation;
import com.creativityinspire.models.Creator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CreatorDAOImpl implements CreatorDAO {

    private DataSource _dataSource;
    private Utility _utility;

    @Autowired
    public CreatorDAOImpl(DataSource _dataSource, Utility utility) {
        this._dataSource = _dataSource;
        _utility = utility;
    }

    public List<Creator> getCreators() throws SQLException {
        Connection _connection = null;
        PreparedStatement _preparedStatement = null;
        List<Creator> _creatorList = new ArrayList<Creator>();

        try {
            _connection = _dataSource.getConnection();
            String sql = "SELECT * FROM creators ORDER BY id DESC;";
            _preparedStatement = _connection.prepareStatement(sql);
            ResultSet rs = _preparedStatement.executeQuery();

            while (rs.next()) {
                Creator creator = new Creator(rs.getString("username"), rs.getString("email"), rs.getString("password"));
                _creatorList.add(creator);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (_preparedStatement != null) {
                _preparedStatement.close();
            }

            if (_connection != null) {
                _connection.close();
            }
        }
        return _creatorList;
    }

    public ConnectResponse signupCreator(String username, String email, String password, String phoneDevice) throws SQLException {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Map<String, String> responseValues = new HashMap<String, String>();
        List<Creation> creationList = new ArrayList<Creation>();
        String phoneId = null;
        String hashValue = _utility.getHashValue(username);

        if (phoneDevice != null) {
            phoneId = hashValue;
            System.out.println("Creator is being inserted in DBUSER table from phone device: " + phoneDevice + ", id: " + phoneId);
        }

        try {
            connection = _dataSource.getConnection();
            String sql = "INSERT INTO creators(username,email,password,phoneid,phonedevice,datesaved) VALUES(?,?,?,?,?,?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, phoneId);
            preparedStatement.setString(5, phoneDevice);
            preparedStatement.setTimestamp(6, _utility.getCurrentTimeStamp());
            preparedStatement.executeUpdate();

            System.out.println("Creator is inserted into DBUSER table: " + username + ", " + email);
            responseValues.put("response", hashValue);
            responseValues.put("username", username);

            if (phoneDevice == null) {
                try {
                    sql = "SELECT * FROM creations WHERE isprivate = false ORDER BY datesaved DESC;";
                    preparedStatement = connection.prepareStatement(sql);
                    ResultSet rs = preparedStatement.executeQuery();

                    while (rs.next()) {
                        Creation _creation = new Creation(rs.getString("title"), rs.getString("poem"), rs.getString("username"), null, false);
                        creationList.add(_creation);
                    }
                } catch (SQLException e) {
                    // don't need to send error to front end as we still could sign up.
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.toString().contains("duplicate key value violates unique constraint")) {
                responseValues.put("error", "This username has already been chosen.  Please try a different name.");
            } else {
                responseValues.put("error", "We were unable to sign you on to our server.  Please try again now or later in the day.");
            }
        } finally {

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
        return new ConnectResponse(responseValues, creationList);
    }

    public ConnectResponse loginCreator(String email, String password, String phoneDevice) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Map<String, String> responseValues = new HashMap<String, String>();
        List<Creation> creationList = new ArrayList<Creation>();
        Creator creator = null;
        String phoneId;

        try {
            connection = _dataSource.getConnection();
            String sql = "SELECT * FROM creators WHERE email = ? AND password = ?;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                creator = new Creator(rs.getString("username"), rs.getString("email"), rs.getString("password"));
            }
            if (creator != null) {
                responseValues.put("response", _utility.getHashValue(creator.getUsername()));
                responseValues.put("username", creator.getUsername());
                System.out.println("Creator is being logged in, username: " + creator.getUsername());

                if (phoneDevice != null) {
                    phoneId = _utility.getHashValue(creator.getUsername());
                    System.out.println("Creator is being logged in from phone device: " + phoneDevice + ", id: " + phoneId);
                    try {
                        String sqlUpdate = "UPDATE creators SET phoneid = ?,phonedevice = ? WHERE email = ? AND password = ?;";
                        preparedStatement = connection.prepareStatement(sqlUpdate);
                        preparedStatement.setString(1, phoneId);
                        preparedStatement.setString(2, phoneDevice);
                        preparedStatement.setString(3, email);
                        preparedStatement.setString(4, password);
                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        responseValues.put("error", "There was a server error.  Please try and login again now or later in the day.");
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Creator is being logged in from website, getting creations");
                    try {
                        sql = "SELECT * FROM creations WHERE isprivate = false OR isprivate = true AND username = ? ORDER BY datesaved DESC;";
                        preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1, creator.getUsername());
                        rs = preparedStatement.executeQuery();

                        while (rs.next()) {
                            Creation _creation = new Creation(rs.getString("title"), rs.getString("poem"), rs.getString("username"), null, rs.getBoolean("isprivate"));
                            creationList.add(_creation);
                        }
                    } catch (SQLException e) {
                        // We don't need to send error to front end as we could still login.
                        e.printStackTrace();
                    }
                }
            } else {
                responseValues.put("error", "Your email or password is incorrect. You can click on the 'Forgot password' link to reset your password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            responseValues.put("error", "Your email or password is incorrect. You can click on the 'Forgot password' link to reset your password.");
        } finally {

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
        return new ConnectResponse(responseValues, creationList);
    }


    @Override
    public ConnectResponse logoutCreator() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Map<String, String> responseValues = new HashMap<String, String>();
        List<Creation> creationList = new ArrayList<Creation>();

        try {
            System.out.println("Getting all public creations after a user logs out.");
            connection = _dataSource.getConnection();
            String sql = "SELECT * FROM creations WHERE isprivate = false ORDER BY datesaved DESC;";
            preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Creation _creation = new Creation(rs.getString("title"), rs.getString("poem"), rs.getString("username"), null, false);
                creationList.add(_creation);
            }
            responseValues.put("response", "Server Transaction Success");
        } catch (SQLException e) {
            e.printStackTrace();
            responseValues.put("error", "Your email or password is incorrect. You can click on the 'Forgot password' link to reset your password.");
        } finally {

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
        return new ConnectResponse(responseValues, creationList);
    }

    public Map<String, String> forgotPassword(String username, String email, String newPassword) throws SQLException {
        Connection _connection = null;
        PreparedStatement _preparedStatement = null;
        Map<String, String> _response = new HashMap<String, String>();
        Creator creator = null;

        try {
            _connection = _dataSource.getConnection();
            String sql = "SELECT * FROM creators WHERE username = ? AND email = ?;";
            _preparedStatement = _connection.prepareStatement(sql);
            _preparedStatement.setString(1, username);
            _preparedStatement.setString(2, email);
            ResultSet rs = _preparedStatement.executeQuery();

            while (rs.next()) {
                creator = new Creator(rs.getString("username"), rs.getString("email"), rs.getString("password"));
            }
            if (creator != null) {
                try {
                    String sqlUpdate = "UPDATE creators SET password = ? WHERE username = ? AND email = ?;";
                    _preparedStatement = _connection.prepareStatement(sqlUpdate);
                    _preparedStatement.setString(1, _utility.getHashPasword(newPassword));
                    _preparedStatement.setString(2, username);
                    _preparedStatement.setString(3, email);
                    _preparedStatement.executeUpdate();
                    _response.put("response", "An email has been sent to your email account. Please follow the instructions.");
                    _response.put("username", creator.getUsername());
                    System.out.println("Forgot password email is present - email: " + email);
                } catch (SQLException e) {
                    _response.put("error", "Server error. Please try again now or later in the day.");
                    e.printStackTrace();
                }
            } else {
                _response.put("error", "The username or email appear incorrect.  Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            _response.put("error", "Server error. Please try again now or later in the day.");
        } finally {
            if (_preparedStatement != null) {
                _preparedStatement.close();
            }

            if (_connection != null) {
                _connection.close();
            }
        }
        return _response;
    }

    public Map<String, String> changePassword(String username, String email, String oldPassword, String
            newPassword) throws SQLException {
        Connection _connection = null;
        PreparedStatement _preparedStatement = null;
        Map<String, String> _response = new HashMap<String, String>();
        Creator creator = null;

        try {
            _connection = _dataSource.getConnection();
            String sql = "SELECT * FROM creators WHERE username = ? AND email = ? AND password = ?;";
            _preparedStatement = _connection.prepareStatement(sql);
            _preparedStatement.setString(1, username);
            _preparedStatement.setString(2, email);
            _preparedStatement.setString(3, _utility.getHashPasword(oldPassword));
            ResultSet rs = _preparedStatement.executeQuery();

            while (rs.next()) {
                creator = new Creator(rs.getString("username"), rs.getString("email"), rs.getString("password"));
            }
            if (creator != null) {
                try {
                    String sqlUpdate = "UPDATE creators SET password = ? WHERE username = ? AND email = ?;";
                    _preparedStatement = _connection.prepareStatement(sqlUpdate);
                    _preparedStatement.setString(1, _utility.getHashPasword(newPassword));
                    _preparedStatement.setString(2, username);
                    _preparedStatement.setString(3, email);
                    _preparedStatement.executeUpdate();
                    _response.put("response", "Your password has been successfully changed.");
                    System.out.println("Change password, email: " + email);
                    System.out.println("Change password, username: " + username);
                } catch (SQLException e) {
                    _response.put("error", "Server error. Please try again now or later in the day.");
                    e.printStackTrace();
                }
            } else {
                _response.put("error", "Server error. Please try again now or later in the day.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            _response.put("error", "Server error. Please try again now or later in the day.");
        } finally {

            if (_preparedStatement != null) {
                _preparedStatement.close();
            }

            if (_connection != null) {
                _connection.close();
            }
        }
        return _response;
    }

    @Override
    public Map<String, String> changeEmail(String username, String oldEmail, String newEmail) throws SQLException {
        Connection _connection = null;
        PreparedStatement _preparedStatement = null;
        Map<String, String> _response = new HashMap<String, String>();
        Creator creator = null;

        try {
            _connection = _dataSource.getConnection();
            String sql = "SELECT * FROM creators WHERE username = ? AND email = ?;";
            _preparedStatement = _connection.prepareStatement(sql);
            _preparedStatement.setString(1, username);
            _preparedStatement.setString(2, oldEmail);
            ResultSet rs = _preparedStatement.executeQuery();

            while (rs.next()) {
                creator = new Creator(rs.getString("username"), rs.getString("email"), rs.getString("password"));
            }
            if (creator != null) {
                try {
                    String sqlUpdate = "UPDATE creators SET email = ? WHERE username = ? AND  email = ?;";
                    _preparedStatement = _connection.prepareStatement(sqlUpdate);
                    _preparedStatement.setString(1, newEmail);
                    _preparedStatement.setString(2, username);
                    _preparedStatement.setString(3, creator.getEmail());
                    _preparedStatement.executeUpdate();
                    _response.put("response", "Your email has been successfully changed.");
                    System.out.println("Changed email, old email: " + oldEmail);
                    System.out.println("New email set to: " + newEmail);
                    System.out.println("Change email, username: " + username);
                } catch (SQLException e) {
                    _response.put("error", "Server error. Please try again now or later in the day.");
                    e.printStackTrace();
                }
            } else {
                _response.put("error", "Server error. Please try again now or later in the day.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            _response.put("error", "Server error. Please try again now or later in the day.");
        } finally {

            if (_preparedStatement != null) {
                _preparedStatement.close();
            }

            if (_connection != null) {
                _connection.close();
            }
        }
        return _response;
    }
}
