package com.creativityinspire.services;

import com.creativityinspire.models.Creation;
import com.creativityinspire.repositories.CreationDAO;
import junit.framework.AssertionFailedError;
import org.junit.Test;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;


public class CreationServiceImplTest {

    @Test
    public void testCreationsOnPageLoadWithMyCreationOneObject() throws Exception {
        HttpSession httpSession = createNiceMock(HttpSession.class);
        expect(httpSession.getAttribute("username")).andReturn("testUsername").times(2);
        expect(httpSession.getAttribute("userValue")).andReturn("testUservalue").times(2);
        expect(httpSession.getAttribute("uniqueSessionId")).andReturn("1234567");

        CreationDAO creationDAO = createNiceMock(CreationDAO.class);
        CreationServiceImpl creationService = new CreationServiceImpl(creationDAO,null);

        Creation creation1 = new Creation("title1", "poem1", "username1", "uservalue1", false);
        List<Creation> creationList = new ArrayList<Creation>();
        creationList.add(creation1);
        expect(creationDAO.getMyCreations("testUsername", "testUservalue")).andReturn(creationList);
        replay(httpSession, creationDAO);
        List<Creation> newCreations = creationService.creationsOnPageLoad(httpSession);
        assertEquals(newCreations.size(), 1);
        assertEquals(newCreations.get(0).getTitle(), "title1");
        verify(httpSession, creationDAO);
    }

    @Test
    public void testCreationsOnPageLoadWithMyCreationNoObjectAllCreationsOneObject() throws Exception {
        HttpSession httpSession = createNiceMock(HttpSession.class);
        expect(httpSession.getAttribute("username")).andReturn("testUsername").times(2);
        expect(httpSession.getAttribute("userValue")).andReturn("testUservalue").times(2);
        expect(httpSession.getAttribute("uniqueSessionId")).andReturn("1234567");

        CreationDAO creationDAO = createNiceMock(CreationDAO.class);
        CreationServiceImpl creationService = new CreationServiceImpl(creationDAO,null);

        Creation creation1 = new Creation("title1", "poem1", "username1", "uservalue1", false);
        List<Creation> creationList = new ArrayList<Creation>();
        expect(creationDAO.getMyCreations("testUsername", "testUservalue")).andReturn(creationList);
        List<Creation> creationList2 = new ArrayList<Creation>();
        creationList2.add(creation1);
        expect(creationDAO.getCreations(httpSession)).andReturn(creationList2);
        replay(httpSession, creationDAO);
        List<Creation> newCreations = creationService.creationsOnPageLoad(httpSession);
        assertEquals(newCreations.size(), 2);
        assertEquals(newCreations.get(0).getTitle(), "title1");
        assertEquals(newCreations.get(1).getTitle(), "");
        verify(httpSession, creationDAO);
    }

    @Test
    public void testCreationsOnPageLoadWithMyCreationNoObjectAllCreationsNoObject() throws Exception {
        HttpSession httpSession = createNiceMock(HttpSession.class);
        expect(httpSession.getAttribute("username")).andReturn("testUsername").times(2);
        expect(httpSession.getAttribute("userValue")).andReturn("testUservalue").times(2);
        expect(httpSession.getAttribute("uniqueSessionId")).andReturn("1234567");

        CreationDAO creationDAO = createNiceMock(CreationDAO.class);
        CreationServiceImpl creationService = new CreationServiceImpl(creationDAO,null);

        List<Creation> creationList = new ArrayList<Creation>();
        expect(creationDAO.getMyCreations("testUsername", "testUservalue")).andReturn(creationList);
        List<Creation> creationList2 = new ArrayList<Creation>();
        expect(creationDAO.getCreations(httpSession)).andReturn(creationList2);
        replay(httpSession, creationDAO);
        List<Creation> newCreations = creationService.creationsOnPageLoad(httpSession);
        assertEquals(newCreations.size(), 0);
        verify(httpSession, creationDAO);
    }

    @Test
    public void testCountReadMoreClickWhereUniqueSessionTitleUsernameDoesNotAlreadyExist() throws Exception {
        HttpSession httpSession = createNiceMock(HttpSession.class);
        expect(httpSession.getAttribute("uniqueSessionId")).andReturn("1234567");
        List<String> sessionUsernames = new ArrayList<String>();
        sessionUsernames.add("xxx");
        expect(httpSession.getAttribute("uniqueSessionTitleUsernames")).andReturn(sessionUsernames).times(2);

        CreationDAO creationDAO = createNiceMock(CreationDAO.class);
        CreationServiceImpl creationService = new CreationServiceImpl(creationDAO,null);

        String title = "A test poem";
        String username = "differentUsernameAgain";
        expect(creationDAO.getNumberClicksToday(title, username)).andReturn(1).times(1);
        creationDAO.countReadMoreClick(title, username);
        expectLastCall().once();

        replay(httpSession, creationDAO);
        creationService.countReadMoreClick(httpSession, title,username);
        verify(httpSession, creationDAO);
    }

    @Test
    public void testCountReadMoreClickWhereUniqueSessionTitleUsernameAlreadyExists() throws Exception {
        HttpSession httpSession = createNiceMock(HttpSession.class);
        expect(httpSession.getAttribute("uniqueSessionId")).andReturn("1234567");
        List<String> sessionUsernames = new ArrayList<String>();
        sessionUsernames.add("1234567 A test poem differentUsernameAgain");
        expect(httpSession.getAttribute("uniqueSessionTitleUsernames")).andReturn(sessionUsernames).times(2);

        CreationDAO creationDAO = createNiceMock(CreationDAO.class);
        CreationServiceImpl creationService = new CreationServiceImpl(creationDAO,null);

        String title = "A test poem";
        String username = "differentUsernameAgain";
        expect(creationDAO.getNumberClicksToday(title, username)).andReturn(1).times(1);
        creationDAO.countReadMoreClick(title, username);
        expectLastCall().andThrow(new AssertionFailedError()).anyTimes();

        replay(httpSession, creationDAO);
        creationService.countReadMoreClick(httpSession, title,username);
        verify(httpSession, creationDAO);
    }
}
