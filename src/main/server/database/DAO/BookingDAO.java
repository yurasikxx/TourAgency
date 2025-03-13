package main.server.database.DAO;

import main.server.models.Booking;

import java.util.List;

public interface BookingDAO {
    Booking getBookingById(int id);
    List<Booking> getBookingsByUserId(int userId);
    void addBooking(Booking booking);
    void updateBooking(Booking booking);
    void deleteBooking(int id);
}