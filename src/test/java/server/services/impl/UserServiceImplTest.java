package server.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.DAO.UserDAO;
import server.models.User;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    private UserServiceImpl userService;

    @Mock
    private UserDAO userDAO;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserServiceImpl(userDAO);
    }

    @Test
    public void testAuthenticateSuccess() {
        User user = new User(1, "user1", "pass123", "user", 1000.0);
        when(userDAO.getUserByUsername("user1")).thenReturn(user);

        User result = userService.authenticate("user1", "pass123");
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    public void testAuthenticateWrongPassword() {
        User user = new User(1, "user1", "pass123", "user", 1000.0);
        when(userDAO.getUserByUsername("user1")).thenReturn(user);

        User result = userService.authenticate("user1", "wrongpass");
        assertNull(result);
    }

    @Test
    public void testAuthenticateUserNotFound() {
        when(userDAO.getUserByUsername("user1")).thenReturn(null);

        User result = userService.authenticate("user1", "pass123");
        assertNull(result);
    }

    @Test
    public void testRegisterUser() {
        User user = new User(0, "newuser", "pass123", "user", 0.0);
        userService.register(user);
        verify(userDAO).addUser(user);
    }

    @Test
    public void testUpdateBalance() {
        userService.updateBalance(1, 1500.0);
        verify(userDAO).updateBalance(1, 1500.0);
    }
}