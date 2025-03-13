package server.services;

import server.models.Booking;

import java.util.List;

public interface BookingService {
    Booking getBookingById(int id);
    List<Booking> getBookingsByUserId(int userId);
    void addBooking(Booking booking);
    void updateBooking(Booking booking);
    void deleteBooking(int id);
}