package client.models;

public class BookingModel {
    private int id;
    private String userName;
    private String tourName;
    private String bookingDate;
    private String status;
    private double price;
    private double amount;

    public BookingModel() {
    }

    public BookingModel(int id, String tourName, String bookingDate, double price, String status) {
        this.id = id;
        this.tourName = tourName;
        this.bookingDate = bookingDate;
        this.price = price;
        this.status = status;
    }

    public BookingModel(int id, String userName, String tourName,
                        String bookingDate, String status, double amount) {
        this.id = id;
        this.userName = userName;
        this.tourName = tourName;
        this.bookingDate = bookingDate;
        this.status = status;
        this.amount = amount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

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