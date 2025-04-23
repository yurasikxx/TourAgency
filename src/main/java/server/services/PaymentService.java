package server.services;

import server.models.Payment;

import java.util.List;

public interface PaymentService {

    List<Payment> getPaymentsByBookingId(int bookingId);

    List<Payment> getAllPayments();

    void addPayment(Payment payment);

}