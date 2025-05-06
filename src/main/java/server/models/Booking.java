package server.models;

import java.util.Objects;

public class Booking {
    private int id;
    private int userId;
    private int tourId;
    private String bookingDate;
    private String status;
    private int adults;
    private int children;
    private String mealOption;
    private String additionalServices;
    private double totalPrice;

    public Booking(int id, int userId, int tourId, String bookingDate, String status, int adults, int children, String mealOption, String additionalServices, double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.tourId = tourId;
        this.bookingDate = bookingDate;
        this.status = status;
        this.adults = adults;
        this.children = children;
        this.mealOption = mealOption;
        this.additionalServices = additionalServices;
        this.totalPrice = totalPrice;
    }

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

    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public String getMealOption() {
        return mealOption;
    }

    public void setMealOption(String mealOption) {
        this.mealOption = mealOption;
    }

    public String getAdditionalServices() {
        return additionalServices;
    }

    public void setAdditionalServices(String additionalServices) {
        this.additionalServices = additionalServices;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id == booking.id && userId == booking.userId && tourId == booking.tourId && adults == booking.adults && children == booking.children && Double.compare(totalPrice, booking.totalPrice) == 0 && Objects.equals(bookingDate, booking.bookingDate) && Objects.equals(status, booking.status) && Objects.equals(mealOption, booking.mealOption) && Objects.equals(additionalServices, booking.additionalServices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, tourId, bookingDate, status, adults, children, mealOption, additionalServices, totalPrice);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userId=" + userId +
                ", tourId=" + tourId +
                ", bookingDate='" + bookingDate + '\'' +
                ", status='" + status + '\'' +
                ", adults=" + adults +
                ", children=" + children +
                ", mealOption='" + mealOption + '\'' +
                ", additionalServices='" + additionalServices + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }
}