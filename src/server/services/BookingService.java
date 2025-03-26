package server.services;

import server.models.Booking;

import java.util.List;
import java.util.Map;

public interface BookingService {
    Booking getBookingById(int id);
    List<Booking> getBookingsByUserId(int userId);
    List<Booking> getAllBookings();
    void addBooking(Booking booking);
    void updateBooking(Booking booking);
    void deleteBooking(int id);
    Map<Integer, Long> getTourPopularity();
}