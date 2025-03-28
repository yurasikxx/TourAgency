package server.services.impl;

import server.database.DAO.PaymentDAO;
import server.models.Payment;
import server.services.PaymentService;

import java.util.List;

public class PaymentServiceImpl implements PaymentService {
    private final PaymentDAO paymentDAO;

    public PaymentServiceImpl(PaymentDAO paymentDAO) {
        this.paymentDAO = paymentDAO;
    }

    @Override
    public List<Payment> getPaymentsByBookingId(int bookingId) {
        return paymentDAO.getPaymentsByBookingId(bookingId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }

    @Override
    public void addPayment(Payment payment) {
        paymentDAO.addPayment(payment);
    }

}