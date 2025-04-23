package server.database.DAO.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.DatabaseConnection;
import server.models.Destination;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DestinationDAOImplTest {
    private DestinationDAOImpl destinationDAO;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        destinationDAO = new DestinationDAOImpl();

        DatabaseConnection mockDbConnection = mock(DatabaseConnection.class);
        when(mockDbConnection.getConnection()).thenReturn(mockConnection);

        Field instanceField = DatabaseConnection.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(instanceField, mockDbConnection);
    }

    @Test
    public void testGetDestinationById() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Paris");
        when(mockResultSet.getString("country")).thenReturn("France");
        when(mockResultSet.getString("description")).thenReturn("City of Light");

        Destination result = destinationDAO.getDestinationById(1);

        assertNotNull(result);
        assertEquals("Paris", result.getName());
        assertEquals("France", result.getCountry());

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    public void testAddDestination() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Destination destination = new Destination(0, "Berlin", "Germany", "Capital city");
        destinationDAO.addDestination(destination);

        verify(mockPreparedStatement).setString(1, destination.getName());
        verify(mockPreparedStatement).setString(2, destination.getCountry());
        verify(mockPreparedStatement).setString(3, destination.getDescription());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testUpdateDestination() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Destination destination = new Destination(1, "Updated Paris", "France", "Updated description");

        destinationDAO.updateDestination(destination);

        verify(mockPreparedStatement).setString(1, destination.getName());
        verify(mockPreparedStatement).setString(2, destination.getCountry());
        verify(mockPreparedStatement).setString(3, destination.getDescription());
        verify(mockPreparedStatement).setInt(4, destination.getId());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testGetAllDestinations() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getString("name")).thenReturn("Paris").thenReturn("Rome");
        when(mockResultSet.getString("country")).thenReturn("France").thenReturn("Italy");
        when(mockResultSet.getString("description")).thenReturn("City of Light").thenReturn("Eternal City");

        List<Destination> result = destinationDAO.getAllDestinations();

        assertEquals(2, result.size());
        assertEquals("Paris", result.get(0).getName());
        assertEquals("Rome", result.get(1).getName());

        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    public void testDeleteDestination() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        destinationDAO.deleteDestination(1);

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeUpdate();
    }
}