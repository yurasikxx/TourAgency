package server.database.DAO.impl;

import server.database.DAO.BookingDAO;
import server.models.Booking;
import server.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOImpl implements BookingDAO {

    @Override
    public Booking getBookingById(int id) {
        String query = "SELECT * FROM Bookings WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Booking(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("tour_id"),
                        resultSet.getString("booking_date"),
                        resultSet.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM Bookings WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                bookings.add(new Booking(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("tour_id"),
                        resultSet.getString("booking_date"),
                        resultSet.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    @Override
    public List<Booking> getAllBookings() {
        String sql = "SELECT * FROM bookings";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<Booking> bookings = new ArrayList<>();
            while (resultSet.next()) {
                bookings.add(new Booking(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("tour_id"),
                        resultSet.getString("booking_date"),
                        resultSet.getString("status")
                ));
            }
            return bookings;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all bookings", e);
        }
    }

    @Override
    public void addBooking(Booking booking) {
        String query = "INSERT INTO Bookings (user_id, tour_id, booking_date, status) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, booking.getUserId());
            statement.setInt(2, booking.getTourId());
            statement.setString(3, booking.getBookingDate());
            statement.setString(4, booking.getStatus());
            statement.executeUpdate();
            System.out.println("Бронирование успешно добавлено.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateBooking(Booking booking) {
        String query = "UPDATE Bookings SET user_id = ?, tour_id = ?, booking_date = ?, status = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, booking.getUserId());
            statement.setInt(2, booking.getTourId());
            statement.setString(3, booking.getBookingDate());
            statement.setString(4, booking.getStatus());
            statement.setInt(5, booking.getId());
            statement.executeUpdate();
            System.out.println("Бронирование успешно обновлено.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteBooking(int id) {
        String query = "DELETE FROM Bookings WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Бронирование успешно удалено.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasUserBookedTour(int userId, int tourId) {
        String sql = "SELECT status FROM Bookings WHERE user_id = ? AND tour_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, tourId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Возвращает true, если есть любая запись (независимо от статуса)
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getBookingStatus(int userId, int tourId) {
        String sql = "SELECT status FROM Bookings WHERE user_id = ? AND tour_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, tourId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasBookingsForTour(int tourId) {
        String query = "SELECT COUNT(*) FROM Bookings WHERE tour_id = ? AND status != 'cancelled'";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, tourId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}