package server.database.DAO.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.DatabaseConnection;
import server.models.User;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserDAOImplTest {
    private UserDAOImpl userDAO;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userDAO = new UserDAOImpl();

        DatabaseConnection mockDbConnection = mock(DatabaseConnection.class);
        when(mockDbConnection.getConnection()).thenReturn(mockConnection);

        Field instanceField = DatabaseConnection.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(instanceField, mockDbConnection);
    }

    @Test
    public void testGetUserById() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("username")).thenReturn("testuser");
        when(mockResultSet.getString("password")).thenReturn("testpass");
        when(mockResultSet.getString("role")).thenReturn("user");
        when(mockResultSet.getDouble("balance")).thenReturn(1000.0);
        when(mockResultSet.getString("full_name")).thenReturn("Иванов Иван Иванович");
        when(mockResultSet.getInt("age")).thenReturn(20);
        when(mockResultSet.getString("email")).thenReturn("ivanov@example.com");
        when(mockResultSet.getString("phone")).thenReturn("+375(29)123-45-67");

        User result = userDAO.getUserById(1);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("user", result.getRole());

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    public void testAddUser() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        User user = new User(0, "testuser", "testpass", "user", 1000.0, "Иванов Иван Иванович", 20, "ivanov@example.com", "+375(29)123-45-67");

        userDAO.addUser(user);

        verify(mockPreparedStatement).setString(1, user.getUsername());
        verify(mockPreparedStatement).setString(2, user.getPassword());
        verify(mockPreparedStatement).setString(3, user.getRole());
        verify(mockPreparedStatement).setDouble(4, user.getBalance());
        verify(mockPreparedStatement).setString(5, user.getFullName());
        verify(mockPreparedStatement).setInt(6, user.getAge());
        verify(mockPreparedStatement).setString(7, user.getEmail());
        verify(mockPreparedStatement).setString(8, user.getPhone());

        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testUpdateUser() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        User user = new User(0, "testuser", "testpass", "user", 1000.0, "Иванов Иван Иванович", 20, "ivanov@example.com", "+375(29)123-45-67");

        userDAO.updateUser(user);

        verify(mockPreparedStatement).setString(1, user.getUsername());
        verify(mockPreparedStatement).setString(2, user.getPassword());
        verify(mockPreparedStatement).setString(3, user.getRole());
        verify(mockPreparedStatement).setDouble(4, user.getBalance());
        verify(mockPreparedStatement).setString(5, user.getFullName());
        verify(mockPreparedStatement).setInt(6, user.getAge());
        verify(mockPreparedStatement).setString(7, user.getEmail());
        verify(mockPreparedStatement).setString(8, user.getPhone());
        verify(mockPreparedStatement).setInt(9, user.getId());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testGetUserByUsername() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("username")).thenReturn("testuser");
        when(mockResultSet.getString("password")).thenReturn("testpass");
        when(mockResultSet.getString("role")).thenReturn("user");
        when(mockResultSet.getDouble("balance")).thenReturn(1000.0);
        when(mockResultSet.getString("full_name")).thenReturn("Иванов Иван Иванович");
        when(mockResultSet.getInt("age")).thenReturn(20);
        when(mockResultSet.getString("email")).thenReturn("ivanov@example.com");
        when(mockResultSet.getString("phone")).thenReturn("+375(29)123-45-67");

        User result = userDAO.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(1000.0, result.getBalance(), 0.001);

        verify(mockPreparedStatement).setString(1, "testuser");
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    public void testUpdateBalance() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        userDAO.updateBalance(1, 1500.0);

        verify(mockPreparedStatement).setDouble(1, 1500.0);
        verify(mockPreparedStatement).setInt(2, 1);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testGetBalance() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getDouble("balance")).thenReturn(1000.0);

        double balance = userDAO.getBalance(1);

        assertEquals(1000.0, balance, 0.001);
        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }
}