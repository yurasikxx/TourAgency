package server.database.DAO.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.DatabaseConnection;
import server.models.Tour;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TourDAOImplTest {
    private TourDAOImpl tourDAO;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        tourDAO = new TourDAOImpl();

        DatabaseConnection mockDbConnection = mock(DatabaseConnection.class);
        when(mockDbConnection.getConnection()).thenReturn(mockConnection);

        Field instanceField = DatabaseConnection.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(instanceField, mockDbConnection);
    }

    @Test
    public void testGetTourById() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Test Tour");
        when(mockResultSet.getString("description")).thenReturn("Test Description");
        when(mockResultSet.getDouble("price")).thenReturn(500.0);
        when(mockResultSet.getString("start_date")).thenReturn("2025-01-01");
        when(mockResultSet.getString("end_date")).thenReturn("2025-01-07");
        when(mockResultSet.getInt("destination_id")).thenReturn(1);

        Tour result = tourDAO.getTourById(1);

        assertNotNull(result);
        assertEquals("Test Tour", result.getName());
        assertEquals(500.0, result.getPrice(), 0.001);

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    public void testAddTour() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Tour tour = new Tour(0, "Test Tour", "Test Description", 500.0,
                "2025-01-01", "2025-01-07", 1);

        tourDAO.addTour(tour);

        verify(mockPreparedStatement).setString(1, tour.getName());
        verify(mockPreparedStatement).setString(2, tour.getDescription());
        verify(mockPreparedStatement).setDouble(3, tour.getPrice());
        verify(mockPreparedStatement).setString(4, tour.getStartDate());
        verify(mockPreparedStatement).setString(5, tour.getEndDate());
        verify(mockPreparedStatement).setInt(6, tour.getDestinationId());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testUpdateTour() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Tour tour = new Tour(1, "Updated Tour", "Updated Description", 600.0,
                "2025-01-01", "2025-01-07", 1);

        tourDAO.updateTour(tour);

        verify(mockPreparedStatement).setString(1, tour.getName());
        verify(mockPreparedStatement).setString(2, tour.getDescription());
        verify(mockPreparedStatement).setDouble(3, tour.getPrice());
        verify(mockPreparedStatement).setString(4, tour.getStartDate());
        verify(mockPreparedStatement).setString(5, tour.getEndDate());
        verify(mockPreparedStatement).setInt(6, tour.getDestinationId());
        verify(mockPreparedStatement).setInt(7, tour.getId());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testHasToursForDestination() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt(1)).thenReturn(1);

        boolean result = tourDAO.hasToursForDestination(1);

        assertTrue(result);
        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }
}