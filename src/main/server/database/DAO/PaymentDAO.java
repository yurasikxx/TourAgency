package main.server.database.DAO;

import main.server.models.Payment;

import java.util.List;

public interface PaymentDAO {
    Payment getPaymentById(int id);
    List<Payment> getPaymentsByBookingId(int bookingId);
    void addPayment(Payment payment);
    void updatePayment(Payment payment);
    void deletePayment(int id);
}