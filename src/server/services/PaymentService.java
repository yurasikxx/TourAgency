package server.services;

import server.models.Payment;

import java.util.List;

public interface PaymentService {
    Payment getPaymentById(int id);
    List<Payment> getPaymentsByBookingId(int bookingId);
    void addPayment(Payment payment);
    void updatePayment(Payment payment);
    void deletePayment(int id);
}