package server.services.impl;

import server.database.DAO.BookingDAO;
import server.models.Booking;
import server.services.BookingService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookingServiceImpl implements BookingService {
    private BookingDAO bookingDAO;

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
    public void deleteBooking(int id) {
        bookingDAO.deleteBooking(id);
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
}