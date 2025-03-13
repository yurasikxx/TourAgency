package client.models;

/**
 * Модель бронирования для клиентской части.
 */
public class BookingModel {
    private int id;
    private String tourName;
    private String bookingDate;
    private String status;

    // Конструкторы
    public BookingModel() {}

    public BookingModel(int id, String tourName, String bookingDate, String status) {
        this.id = id;
        this.tourName = tourName;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
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
                ", status='" + status + '\'' +
                '}';
    }
}