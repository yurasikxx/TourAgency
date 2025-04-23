package server.database.DAO.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.DatabaseConnection;
import server.models.Payment;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PaymentDAOImplTest {
    private PaymentDAOImpl paymentDAO;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        paymentDAO = new PaymentDAOImpl();

        DatabaseConnection mockDbConnection = mock(DatabaseConnection.class);
        when(mockDbConnection.getConnection()).thenReturn(mockConnection);

        Field instanceField = DatabaseConnection.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(instanceField, mockDbConnection);
    }

    @Test
    public void testGetPaymentById() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getInt("booking_id")).thenReturn(1);
        when(mockResultSet.getDouble("amount")).thenReturn(500.0);
        when(mockResultSet.getString("payment_date")).thenReturn("2025-01-01");
        when(mockResultSet.getString("status")).thenReturn("paid");

        Payment result = paymentDAO.getPaymentById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(500.0, result.getAmount(), 0.001);
        assertEquals("paid", result.getStatus());

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    public void testAddPayment() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Payment payment = new Payment(0, 1, 500.0, "2025-01-01", "paid");

        paymentDAO.addPayment(payment);

        verify(mockPreparedStatement).setInt(1, payment.getBookingId());
        verify(mockPreparedStatement).setDouble(2, payment.getAmount());
        verify(mockPreparedStatement).setString(3, payment.getPaymentDate());
        verify(mockPreparedStatement).setString(4, payment.getStatus());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testGetPaymentsByBookingId() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getInt("booking_id")).thenReturn(1).thenReturn(1);
        when(mockResultSet.getDouble("amount")).thenReturn(500.0).thenReturn(300.0);
        when(mockResultSet.getString("payment_date")).thenReturn("2025-01-01").thenReturn("2025-01-02");
        when(mockResultSet.getString("status")).thenReturn("paid").thenReturn("paid");

        List<Payment> result = paymentDAO.getPaymentsByBookingId(1);

        assertEquals(2, result.size());
        assertEquals(500.0, result.get(0).getAmount(), 0.001);
        assertEquals(300.0, result.get(1).getAmount(), 0.001);

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    public void testUpdatePayment() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Payment payment = new Payment(1, 1, 500.0, "2025-01-01", "refunded");

        paymentDAO.updatePayment(payment);

        verify(mockPreparedStatement).setInt(1, payment.getBookingId());
        verify(mockPreparedStatement).setDouble(2, payment.getAmount());
        verify(mockPreparedStatement).setString(3, payment.getPaymentDate());
        verify(mockPreparedStatement).setString(4, payment.getStatus());
        verify(mockPreparedStatement).setInt(5, payment.getId());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testGetAllPayments() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getInt("booking_id")).thenReturn(1);
        when(mockResultSet.getDouble("amount")).thenReturn(500.0);
        when(mockResultSet.getString("payment_date")).thenReturn("2025-01-01");
        when(mockResultSet.getString("status")).thenReturn("paid");

        List<Payment> result = paymentDAO.getAllPayments();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());

        verify(mockPreparedStatement).executeQuery();
    }
}