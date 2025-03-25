package server.database.DAO.impl;

import server.database.DAO.PaymentDAO;
import server.models.Payment;
import server.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {
    private Connection connection;

    public PaymentDAOImpl() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Payment getPaymentById(int id) {
        String query = "SELECT * FROM Payments WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Payment(
                        resultSet.getInt("id"),
                        resultSet.getInt("booking_id"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("payment_date"),
                        resultSet.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Payment> getPaymentsByBookingId(int bookingId) {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM Payments WHERE booking_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookingId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                payments.add(new Payment(
                        resultSet.getInt("id"),
                        resultSet.getInt("booking_id"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("payment_date"),
                        resultSet.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public List<Payment> getAllPayments() {
        String sql = "SELECT * FROM payments";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<Payment> payments = new ArrayList<>();
            while (resultSet.next()) {
                payments.add(new Payment(
                        resultSet.getInt("id"),
                        resultSet.getInt("booking_id"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("payment_date"),
                        resultSet.getString("status")
                ));
            }
            return payments;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all payments", e);
        }
    }

    @Override
    public void addPayment(Payment payment) {
        String query = "INSERT INTO Payments (booking_id, amount, payment_date, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, payment.getBookingId());
            statement.setDouble(2, payment.getAmount());
            statement.setString(3, payment.getPaymentDate());
            statement.setString(4, payment.getStatus());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePayment(Payment payment) {
        String query = "UPDATE Payments SET booking_id = ?, amount = ?, payment_date = ?, status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, payment.getBookingId());
            statement.setDouble(2, payment.getAmount());
            statement.setString(3, payment.getPaymentDate());
            statement.setString(4, payment.getStatus());
            statement.setInt(5, payment.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePayment(int id) {
        String query = "DELETE FROM Payments WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}