package server.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.DAO.BookingDAO;
import server.models.Booking;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BookingServiceImplTest {
    private BookingServiceImpl bookingService;

    @Mock
    private BookingDAO bookingDAO;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bookingService = new BookingServiceImpl(bookingDAO);
    }

    @Test
    public void testGetBookingById() {
        Booking expected = new Booking(1, 1, 1, "2025-01-01", "pending", 1, 1, "Без", "Массаж", 1000);
        when(bookingDAO.getBookingById(1)).thenReturn(expected);

        Booking actual = bookingService.getBookingById(1);
        assertEquals(expected, actual);
        verify(bookingDAO).getBookingById(1);
    }

    @Test
    public void testGetBookingsByUserId() {
        List<Booking> expected = Arrays.asList(
                new Booking(1, 1, 1, "2025-01-01", "pending", 1, 1, "Без", "Массаж", 1000),
                new Booking(2, 1, 2, "2025-01-02", "confirmed", 1, 1, "Без", "Массаж", 1000)
        );
        when(bookingDAO.getBookingsByUserId(1)).thenReturn(expected);

        List<Booking> actual = bookingService.getBookingsByUserId(1);
        assertEquals(2, actual.size());
        verify(bookingDAO).getBookingsByUserId(1);
    }

    @Test
    public void testHasUserBookedTourWhenNotBooked() {
        when(bookingDAO.getBookingStatus(1, 1)).thenReturn(null);
        assertFalse(bookingService.hasUserBookedTour(1, 1));
    }

    @Test
    public void testHasUserBookedTourWhenPending() {
        when(bookingDAO.getBookingStatus(1, 1)).thenReturn("pending");
        assertTrue(bookingService.hasUserBookedTour(1, 1));
    }
}