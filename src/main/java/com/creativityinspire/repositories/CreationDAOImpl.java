package com.creativityinspire.repositories;


import com.creativityinspire.models.Creation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;

@Repository
public class CreationDAOImpl implements CreationDAO {

    private DataSource _dataSource;

    @Autowired
    public CreationDAOImpl(DataSource _dataSource) {
        this._dataSource = _dataSource;
    }

    public List<Creation> getCreations(HttpSession session) throws SQLException {

        String sql;
        Connection _connection = null;
        PreparedStatement _preparedStatement = null;
        List<Creation> _creationList = new ArrayList<Creation>();
        String username = String.valueOf(session.getAttribute("username"));

        try {
            _connection = _dataSource.getConnection();

            if (!username.equals("null")) {
                sql = "SELECT * FROM creations WHERE isprivate = false OR isprivate = true AND username = ? ORDER BY datesaved DESC;";
                _preparedStatement = _connection.prepareStatement(sql);
                _preparedStatement.setString(1, username);
            } else {
                sql = "SELECT * FROM creations WHERE isprivate = false ORDER BY datesaved DESC;";
                _preparedStatement = _connection.prepareStatement(sql);
            }
            ResultSet rs = _preparedStatement.executeQuery();

            while (rs.next()) {
                Creation _creation = new Creation(rs.getString("title"), rs.getString("poem"), rs.getString("username"), null, false);
                _creationList.add(_creation);
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
        return _creationList;
    }

    public List<Creation> getMyCreations(String username, String sessionLoggedInUserValue) throws SQLException {

        Connection _connection = null;
        PreparedStatement _preparedStatement = null;
        List<Creation> _creationList = new ArrayList<Creation>();

        try {
            _connection = _dataSource.getConnection();
            String sql = "SELECT * FROM creations WHERE username = ?;";
            _preparedStatement = _connection.prepareStatement(sql);
            _preparedStatement.setString(1, username);
            ResultSet rs = _preparedStatement.executeQuery();

            System.out.println("Creations of username: " + username);

            while (rs.next()) {

                System.out.println(rs.getString("title"));
                System.out.println(rs.getString("poem"));
                System.out.println(rs.getString("username"));
                System.out.println(sessionLoggedInUserValue);
                System.out.println(rs.getBoolean("isprivate"));

                Creation _creation = new Creation(rs.getString("title"), rs.getString("poem"), rs.getString("username"), sessionLoggedInUserValue, rs.getBoolean("isprivate"));
                System.out.println("new title: " + _creation);
                _creationList.add(_creation);
                System.out.println("new title: " + _creation.getTitle());
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
        return _creationList;
    }

    public List<Creation> getPopularCreations() throws SQLException {
        Connection _connection = null;
        PreparedStatement _preparedStatement = null;
        List<Creation> _creationList = new ArrayList<Creation>();

        try {
            _connection = _dataSource.getConnection();
            String sql = "SELECT * FROM creations WHERE isprivate = false AND creation_id in (select creation_id from creation_views GROUP BY creation_id ORDER BY count(creation_id) DESC);";
            _preparedStatement = _connection.prepareStatement(sql);
            ResultSet rs = _preparedStatement.executeQuery();

            while (rs.next()) {
                Creation _creation = new Creation(rs.getString("title"), rs.getString("poem"), rs.getString("username"), null, false);
                _creationList.add(_creation);
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
        return _creationList;
    }

    public Map<String, String> saveCreation(String title, String poem, String username, boolean isPrivate, String phoneId) throws SQLException {

        Connection _connection = null;
        PreparedStatement _preparedStatement = null;
        Map<String, String> _response = new HashMap<String, String>();

        System.out.println("phone id:" + phoneId);

        if (phoneId != null) {
            try {
                _connection = _dataSource.getConnection();
                String sql = "SELECT * FROM creators WHERE phoneid = ?;";
                _preparedStatement = _connection.prepareStatement(sql);
                _preparedStatement.setString(1, phoneId);
                ResultSet rs = _preparedStatement.executeQuery();

                if (rs.next()) {
                    System.out.println("Username from phoneid: " + rs.getString("username"));
                } else {
                    System.out.println("Your personal details are not recognized. Username from phoneid: " + username);
                    _response.put("response", "Your personal details are not recognized. Please login to the App and try again.");

                    _preparedStatement.close();
                    _connection.close();

                    return _response;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                _response.put("response", "Your personal details are not recognized. Please login to the App and try again.");

                if (_preparedStatement != null) {
                    _preparedStatement.close();
                }
                if (_connection != null) {
                    _connection.close();
                }
                return _response;
            }
        }

        try {
            if (_dataSource.getConnection() != null) {
                _connection = _dataSource.getConnection();
            }
            if (poem.equals("")) { // To only save username, title, isprivate, date for a private iPhone creation.
                String sql2 = "INSERT INTO creations_backup(title,poem,username,datesaved,isprivate) VALUES(?,?,?,?,?);";
                assert _connection != null;
                _preparedStatement = _connection.prepareStatement(sql2);
                _preparedStatement.setString(1, title);
                _preparedStatement.setString(2, poem);
                _preparedStatement.setString(3, username);
                _preparedStatement.setTimestamp(4, getCurrentTimeStamp());
                _preparedStatement.setBoolean(5, isPrivate);
                _preparedStatement.executeUpdate();

                System.out.println("Private record is inserted into creations_backup table: " + title + " by " + username);
                _response.put("response", "Server Transaction Success");
            } else {
                String sql = "INSERT INTO creations(title,poem,username,datesaved,isprivate) VALUES(?,?,?,?,?);";
                assert _connection != null;
                _preparedStatement = _connection.prepareStatement(sql);
                _preparedStatement.setString(1, title);
                _preparedStatement.setString(2, poem);
                _preparedStatement.setString(3, username);
                _preparedStatement.setTimestamp(4, getCurrentTimeStamp());
                _preparedStatement.setBoolean(5, isPrivate);
                _preparedStatement.executeUpdate();

                String sql2 = "INSERT INTO creations_backup(title,poem,username,datesaved,isprivate) VALUES(?,?,?,?,?);";
                _preparedStatement = _connection.prepareStatement(sql2);
                _preparedStatement.setString(1, title);
                _preparedStatement.setString(2, poem);
                _preparedStatement.setString(3, username);
                _preparedStatement.setTimestamp(4, getCurrentTimeStamp());
                _preparedStatement.setBoolean(5, isPrivate);
                _preparedStatement.executeUpdate();

                System.out.println("Record is inserted into creations table: " + title + " by " + username);
                _response.put("response", "Server Transaction Success");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            _response.put("response", "Please try again now or later in the day.");
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

    public Map<String, String> updateCreation(String title, String poem, String username, boolean isPrivate, String phoneId) throws SQLException {

        Connection _connection = null;
        PreparedStatement _preparedStatement = null;
        Map<String, String> _response = new HashMap<String, String>();

        System.out.println("phone id:" + phoneId);

        if (phoneId != null) {
            try {
                _connection = _dataSource.getConnection();
                String sql = "SELECT * FROM creators WHERE phoneid = ?;";
                _preparedStatement = _connection.prepareStatement(sql);
                _preparedStatement.setString(1, phoneId);
                ResultSet rs = _preparedStatement.executeQuery();

                if (rs.next()) {
                    System.out.println("Username from phoneid: " + rs.getString("username"));
                } else {
                    System.out.println("Your personal details are not recognized. Username from phoneid: " + username);
                    _response.put("response", "Your personal details are not recognized. Please login to the App and try again.");

                    _preparedStatement.close();
                    _connection.close();
                    return _response;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                _response.put("response", "Your personal details are not recognized. Please login to the App and try again.");

                if (_preparedStatement != null) {
                    _preparedStatement.close();
                }
                if (_connection != null) {
                    _connection.close();
                }
                return _response;
            }
        }

        try {
            if (_dataSource.getConnection() != null) {
                _connection = _dataSource.getConnection();
            }

            if (poem.equals("")) { // Increment the update count for this private creation from iPhone
                String sql2 = "UPDATE creations_backup SET update_count = update_count + 1, datesaved = ? WHERE username = ? AND title = ? AND isprivate = true;";
                assert _connection != null;
                _preparedStatement = _connection.prepareStatement(sql2);
                _preparedStatement.setTimestamp(1, getCurrentTimeStamp());
                _preparedStatement.setString(2, username);
                _preparedStatement.setString(3, title);
                _preparedStatement.executeUpdate();

                System.out.println("Private record is updated - update_count is incremented in creations_backup table: " + title + " by " + username);
                _response.put("response", "Server Transaction Success");
            } else {
                String sql = "UPDATE creations SET update_count = update_count + 1, poem = ?,datesaved = ?, isprivate = ? WHERE username = ? AND title = ?;";
                assert _connection != null;
                _preparedStatement = _connection.prepareStatement(sql);
                _preparedStatement.setString(1, poem);
                _preparedStatement.setTimestamp(2, getCurrentTimeStamp());
                _preparedStatement.setBoolean(3, isPrivate);
                _preparedStatement.setString(4, username);
                _preparedStatement.setString(5, title);
                _preparedStatement.executeUpdate();

                String sql2 = "UPDATE creations_backup SET update_count = update_count + 1, poem = ?,datesaved = ?, isprivate = ? WHERE username = ? AND title = ? AND isprivate = false;";
                _preparedStatement = _connection.prepareStatement(sql2);
                _preparedStatement.setString(1, poem);
                _preparedStatement.setTimestamp(2, getCurrentTimeStamp());
                _preparedStatement.setBoolean(3, isPrivate);
                _preparedStatement.setString(4, username);
                _preparedStatement.setString(5, title);
                _preparedStatement.executeUpdate();

                System.out.println("Record is updated in creations table: " + title + " by " + username);
                _response.put("response", "Server Transaction Success");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            _response.put("response", "Please try again now or later in the day.");
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

    public Map<String, String> deleteCreation(String title, String username, String phoneId, boolean isPrivate) throws SQLException {

        Connection _connection = null;
        PreparedStatement _preparedStatement = null;
        Map<String, String> _response = new HashMap<String, String>();

        System.out.println("phone id:" + phoneId);

        if (phoneId != null) {
            try {
                _connection = _dataSource.getConnection();
                String sql = "SELECT * FROM creators WHERE phoneid = ?;";
                _preparedStatement = _connection.prepareStatement(sql);
                _preparedStatement.setString(1, phoneId);
                ResultSet rs = _preparedStatement.executeQuery();

                if (rs.next()) {
                    System.out.println("Username from phoneid: " + rs.getString("username"));
                } else {
                    System.out.println("Your personal details are not recognized. Username from phoneid: " + username);
                    _response.put("response", "Your personal details are not recognized. Please login to the App and try again.");

                    _preparedStatement.close();
                    _connection.close();
                    return _response;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                _response.put("response", "Your personal details are not recognized. Please login to the App and try again.");

                if (_preparedStatement != null) {
                    _preparedStatement.close();
                }
                if (_connection != null) {
                    _connection.close();
                }
                return _response;
            }
        }

        try {
            if (_dataSource.getConnection() != null) {
                _connection = _dataSource.getConnection();
            }

            if (isPrivate) {
                System.out.println("Private record is deleted - delete_count is incremented in creations_backup table: " + title + " by " + username);
                _response.put("response", "Server Transaction Success");
            } else {
                String sql = "DELETE FROM creations WHERE username = ? AND title = ?;";
                assert _connection != null;
                _preparedStatement = _connection.prepareStatement(sql);
                _preparedStatement.setString(1, username);
                _preparedStatement.setString(2, title);
                _preparedStatement.executeUpdate();

                System.out.println("Record is deleted in creations table: " + title + " by " + username);
                _response.put("response", "Server Transaction Success");
            }
            String sql2 = "UPDATE creations_backup SET delete_count = delete_count + 1, datesaved = ? WHERE username = ? AND title = ? AND isprivate = ?;";
            assert _connection != null;
            _preparedStatement = _connection.prepareStatement(sql2);
            _preparedStatement.setTimestamp(1, getCurrentTimeStamp());
            _preparedStatement.setString(2, username);
            _preparedStatement.setString(3, title);
            _preparedStatement.setBoolean(4, isPrivate);
            _preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            _response.put("response", "Please try again now or later in the day.");
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

    public void countReadMoreClick(String title, String username) throws SQLException {
        Connection _connection = null;
        PreparedStatement _preparedStatement = null;

        try {
            _connection = _dataSource.getConnection();
            String sql = "INSERT into creation_views (creation_id, date_clicked) VALUES ((select creation_id from creations WHERE title = ? AND username = ?),?);";
            _preparedStatement = _connection.prepareStatement(sql);
            _preparedStatement.setString(1, title);
            _preparedStatement.setString(2, username);
            _preparedStatement.setTimestamp(3, getCurrentTimeStamp());
            _preparedStatement.executeUpdate();

            System.out.println("Row added to creation_views table where title is " + title + " by " + username);
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
    }


    public int getNumberClicksToday(String title, String username) throws SQLException {
        Connection _connection = null;
        PreparedStatement _preparedStatement = null;
        int numberClicksToday = 0;

        try {
            _connection = _dataSource.getConnection();
            String sql = "SELECT COUNT(*) FROM creation_views WHERE creation_id = (select creation_id from creations where title = ? AND username = ?) AND date_clicked > now() - interval '24 hours';";
            _preparedStatement = _connection.prepareStatement(sql);
            _preparedStatement.setString(1, title);
            _preparedStatement.setString(2, username);
            ResultSet rs = _preparedStatement.executeQuery();

            if (rs.next()) {
                numberClicksToday = rs.getInt(1);
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
        return numberClicksToday;
    }

    private Timestamp getCurrentTimeStamp() {
        Date _today = new java.util.Date();
        return new Timestamp(_today.getTime());
    }
}
