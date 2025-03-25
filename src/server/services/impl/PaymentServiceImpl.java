package server.services.impl;

import server.models.Payment;
import server.database.DAO.PaymentDAO;
import server.services.PaymentService;

import java.util.Collections;
import java.util.List;

public class PaymentServiceImpl implements PaymentService {
    private PaymentDAO paymentDAO;

    public PaymentServiceImpl(PaymentDAO paymentDAO) {
        this.paymentDAO = paymentDAO;
    }

    @Override
    public Payment getPaymentById(int id) {
        return paymentDAO.getPaymentById(id);
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

    @Override
    public void updatePayment(Payment payment) {
        paymentDAO.updatePayment(payment);
    }

    @Override
    public void deletePayment(int id) {
        paymentDAO.deletePayment(id);
    }
}