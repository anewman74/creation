//package com.creativityinspire.repositories;
//
//import com.creativityinspire.models.Creation;
//import org.easymock.EasyMock;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import javax.servlet.http.HttpSession;
//import javax.sql.DataSource;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.easymock.EasyMock.*;
//import static org.junit.Assert.assertEquals;
//import static org.powermock.api.easymock.PowerMock.mockStatic;
//
//
//@RunWith(PowerMockRunner.class)
//public class CreationDAOImplTest {
//    private String sql;
//
//    @Test
//    public void testGetMyCreations() throws Exception {
//
//        HttpSession httpSession = createNiceMock(HttpSession.class);
//        expect(httpSession.getAttribute("username")).andReturn("testUsername").times(1);
//        replay(httpSession);
//
//        DataSource dataSource = createNiceMock(DataSource.class);
//        Connection connection = createNiceMock(Connection.class);
//        PreparedStatement preparedStatement = createNiceMock(PreparedStatement.class);
//        Statement mockStatement = createNiceMock(Statement.class);
//        ResultSet mockResultSet = createNiceMock(ResultSet.class);
//
//        CreationDAO creationDAO = new CreationDAOImpl(dataSource);
//        sql = "SELECT * FROM creations WHERE username = ?;";
//
//        Creation creation = new Creation("title1", "poem1", "username1", "userValue1", false);
//
//        List<Creation> creations = new ArrayList<Creation>();
//        creations.add(creation);
//
//        expect(dataSource.getConnection()).andReturn(
//                connection);
//        expect(connection.prepareStatement(sql)).andReturn(
//                preparedStatement).anyTimes();
//        expect(preparedStatement.executeQuery()).andReturn(mockResultSet);
//
//        expect(mockResultSet.next()).andReturn(true);
//        expect(mockResultSet.getString("title")).andReturn("title1");
//        expect(mockResultSet.getString("poem")).andReturn("poem1");
//        expect(mockResultSet.getString("username")).andReturn("username1");
//        expect(mockResultSet.getBoolean("isprivate")).andReturn(false);
//        EasyMock.replay(connection, dataSource, preparedStatement, mockStatement,mockResultSet);
//
//        List<Creation> newCreations = creationDAO.getMyCreations("username1", "xxx");
//        assertEquals(newCreations.size(),1);
//        assertEquals(newCreations.get(0).getTitle(), "title1");
//        EasyMock.verify(connection, dataSource, preparedStatement, mockStatement,mockResultSet);
//    }
//}
