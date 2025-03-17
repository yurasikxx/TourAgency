package client.models;

/**
 * Модель платежа для клиентской части.
 */
public class PaymentModel {
    private int id;
    private double amount;
    private String paymentDate;
    private String status;

    // Конструкторы
    public PaymentModel() {}

    public PaymentModel(int id, double amount, String paymentDate, String status) {
        this.id = id;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    // Метод преобразования серверной модели в клиентскую
    public static PaymentModel fromServerModel(server.models.Payment serverPayment) {
        return new PaymentModel(
                serverPayment.getId(),
                serverPayment.getAmount(),
                serverPayment.getPaymentDate(),
                serverPayment.getStatus()
        );
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return "PaymentModel{" +
                "id=" + id +
                ", amount=" + amount +
                ", paymentDate='" + paymentDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}