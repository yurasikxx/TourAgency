package server.database.DAO.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.DatabaseConnection;
import server.models.Booking;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BookingDAOImplTest {
    private BookingDAOImpl bookingDAO;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        bookingDAO = new BookingDAOImpl();

        DatabaseConnection mockDbConnection = mock(DatabaseConnection.class);
        when(mockDbConnection.getConnection()).thenReturn(mockConnection);

        Field instanceField = DatabaseConnection.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(instanceField, mockDbConnection);
    }

    @Test
    public void testGetBookingById() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getInt("tour_id")).thenReturn(1);
        when(mockResultSet.getString("booking_date")).thenReturn("2025-01-01");
        when(mockResultSet.getString("status")).thenReturn("pending");

        Booking result = bookingDAO.getBookingById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("pending", result.getStatus());

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    public void testAddBooking() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Booking booking = new Booking(0, 1, 1, "2025-01-01", "pending", 1, 1, "Без", "Массаж", 1000);

        bookingDAO.addBooking(booking);

        verify(mockPreparedStatement).setInt(1, booking.getUserId());
        verify(mockPreparedStatement).setInt(2, booking.getTourId());
        verify(mockPreparedStatement).setString(3, booking.getBookingDate());
        verify(mockPreparedStatement).setString(4, booking.getStatus());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testGetBookingsByUserId() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getInt("tour_id")).thenReturn(1);
        when(mockResultSet.getString("booking_date")).thenReturn("2025-01-01");
        when(mockResultSet.getString("status")).thenReturn("pending");

        List<Booking> result = bookingDAO.getBookingsByUserId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    public void testUpdateBooking() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Booking booking = new Booking(1, 1, 1, "2025-01-01", "confirmed", 1, 1, "Без", "Массаж", 1000);

        bookingDAO.updateBooking(booking);

        verify(mockPreparedStatement).setInt(1, booking.getUserId());
        verify(mockPreparedStatement).setInt(2, booking.getTourId());
        verify(mockPreparedStatement).setString(3, booking.getBookingDate());
        verify(mockPreparedStatement).setString(4, booking.getStatus());
        verify(mockPreparedStatement).setInt(5, booking.getId());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testDeleteBooking() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        bookingDAO.deleteBooking(1);

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testGetBookingStatus() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("status")).thenReturn("pending");

        String status = bookingDAO.getBookingStatus(1, 1);

        assertEquals("pending", status);
        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).setInt(2, 1);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    public void testHasBookingsForTour() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt(1)).thenReturn(1);

        boolean result = bookingDAO.hasBookingsForTour(1);

        assertTrue(result);
        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }
}