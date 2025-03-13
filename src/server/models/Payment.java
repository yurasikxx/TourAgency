package server.models;

/**
 * Класс, представляющий сущность "Платеж".
 */
public class Payment {
    private int id;          // Уникальный идентификатор платежа
    private int bookingId;   // Идентификатор бронирования
    private double amount;   // Сумма платежа
    private String paymentDate; // Дата платежа
    private String status;   // Статус платежа (например, "оплачено", "ожидание")

    // Конструкторы
    public Payment() {}

    public Payment(int id, int bookingId, double amount, String paymentDate, String status) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    // Геттеры и сеттеры
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

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", bookingId=" + bookingId +
                ", amount=" + amount +
                ", paymentDate='" + paymentDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}