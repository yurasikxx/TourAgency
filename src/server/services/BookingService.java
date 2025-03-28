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

    Map<Integer, Long> getTourPopularity();

    boolean hasUserBookedTour(int userId, int tourId);

    String getBookingStatus(int userId, int tourId);

    boolean hasBookingsForTour(int tourId);
}