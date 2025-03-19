package client.models;

public class BookingModel {
    private int id;
    private String tourName;
    private String bookingDate;
    private double price;
    private String status;

    // Конструкторы
    public BookingModel() {}

    public BookingModel(int id, String tourName, String bookingDate, double price, String status) {
        this.id = id;
        this.tourName = tourName;
        this.bookingDate = bookingDate;
        this.price = price;
        this.status = status;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getTourName() {
        return tourName;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    // Сеттеры (если нужны)
    public void setId(int id) {
        this.id = id;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BookingModel{" +
                "id=" + id +
                ", tourName='" + tourName + '\'' +
                ", bookingDate='" + bookingDate + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}