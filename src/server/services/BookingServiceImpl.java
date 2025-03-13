package server.services;

import server.models.Booking;
import server.database.DAO.BookingDAO;

import java.util.List;

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
}