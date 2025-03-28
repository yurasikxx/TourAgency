package client.models;

public class PaymentModel {
    private int id;
    private int bookingId;
    private int userId;
    private double amount;
    private String paymentDate;
    private String status;
    private String tourName;

    public PaymentModel() {
    }

    public PaymentModel(int id, double amount, String paymentDate, String status) {
        this.id = id;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    public PaymentModel(int id, int bookingId, int userId, double amount,
                        String paymentDate, String status, String tourName) {
        this.id = id;
        this.bookingId = bookingId;
        this.userId = userId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
        this.tourName = tourName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }
}