package server.models;

/**
 * Класс, представляющий сущность "Бронирование".
 */
public class Booking {
    private int id;          // Уникальный идентификатор бронирования
    private int userId;      // Идентификатор пользователя
    private int tourId;      // Идентификатор тура
    private String bookingDate; // Дата бронирования
    private String status;   // Статус бронирования (например, "подтверждено", "отменено")

    // Конструкторы
    public Booking() {}

    public Booking(int id, int userId, int tourId, String bookingDate, String status) {
        this.id = id;
        this.userId = userId;
        this.tourId = tourId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
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
        return "Booking{" +
                "id=" + id +
                ", userId=" + userId +
                ", tourId=" + tourId +
                ", bookingDate='" + bookingDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}