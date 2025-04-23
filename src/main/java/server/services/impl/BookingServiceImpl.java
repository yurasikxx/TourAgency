package server.services.impl;

import server.database.DAO.BookingDAO;
import server.models.Booking;
import server.services.BookingService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookingServiceImpl implements BookingService {
    private final BookingDAO bookingDAO;

    public BookingServiceImpl(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    @Override
    public Booking getBookingById(int id) {
        return bookingDAO.getBookingById(id);
    }

    @Override
    public List<Booking> getBookingsByUserId(int userId) {
        return bookingDAO.getBookingsByUserId(userId);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }

    @Override
    public void addBooking(Booking booking) {
        bookingDAO.addBooking(booking);
    }

    @Override
    public void updateBooking(Booking booking) {
        bookingDAO.updateBooking(booking);
    }

    @Override
    public Map<Integer, Long> getTourPopularity() {
        List<Booking> allBookings = bookingDAO.getAllBookings();
        return allBookings.stream()
                .collect(Collectors.groupingBy(
                        Booking::getTourId,
                        Collectors.counting()
                ));
    }

    @Override
    public boolean hasUserBookedTour(int userId, int tourId) {
        String status = bookingDAO.getBookingStatus(userId, tourId);
        return status != null && (status.equals("pending") || status.equals("confirmed"));
    }

    @Override
    public String getBookingStatus(int userId, int tourId) {
        return bookingDAO.getBookingStatus(userId, tourId);
    }

    @Override
    public boolean hasBookingsForTour(int tourId) {
        return bookingDAO.hasBookingsForTour(tourId);
    }
}