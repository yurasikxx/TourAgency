package server.database.DAO;

import server.models.Destination;
import server.models.Payment;

import java.util.List;

public interface PaymentDAO {
    Payment getPaymentById(int id);
    List<Payment> getPaymentsByBookingId(int bookingId);
    List<Payment> getAllPayments();
    void addPayment(Payment payment);
    void updatePayment(Payment payment);
    void deletePayment(int id);
}