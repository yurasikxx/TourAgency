package server.database.DAO;

import server.models.Booking;

import java.util.List;

public interface BookingDAO {
    Booking getBookingById(int id);

    List<Booking> getBookingsByUserId(int userId);

    List<Booking> getAllBookings();

    void addBooking(Booking booking);

    void updateBooking(Booking booking);

    void deleteBooking(int id);

    String getBookingStatus(int userId, int tourId);

    boolean hasBookingsForTour(int tourId);
}