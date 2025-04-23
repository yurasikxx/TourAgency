package server.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.DAO.PaymentDAO;
import server.models.Payment;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PaymentServiceImplTest {
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentDAO paymentDAO;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        paymentService = new PaymentServiceImpl(paymentDAO);
    }

    @Test
    public void testGetPaymentsByBookingId() {
        List<Payment> expected = Arrays.asList(
                new Payment(1, 1, 100.0, "2025-01-01", "paid"),
                new Payment(2, 1, 50.0, "2025-01-02", "refunded")
        );
        when(paymentDAO.getPaymentsByBookingId(1)).thenReturn(expected);

        List<Payment> actual = paymentService.getPaymentsByBookingId(1);
        assertEquals(2, actual.size());
        verify(paymentDAO).getPaymentsByBookingId(1);
    }

    @Test
    public void testAddPayment() {
        Payment payment = new Payment(0, 1, 100.0, "2025-01-01", "paid");
        paymentService.addPayment(payment);
        verify(paymentDAO).addPayment(payment);
    }
}