package client.models;

import java.util.Objects;

public class BookingModel {
    private int id;
    private String userName;
    private String tourName;
    private String bookingDate;
    private String status;
    private double price;
    private double amount;
    private int adults;
    private int children;
    private String mealOption;
    private String additionalServices;
    private double totalPrice;
    private double paidAmount;
    private String paymentStatus;
    private String tourStartDate;

    public BookingModel(int id, String userName, String tourName,
                        String bookingDate, String status, double amount) {
        this.id = id;
        this.userName = userName;
        this.tourName = tourName;
        this.bookingDate = bookingDate;
        this.status = status;
        this.amount = amount;
    }

    public BookingModel(int id, String tourName, String bookingDate, String status, double price, int adults, int children, String mealOption, String additionalServices, double totalPrice, double paidAmount, String paymentStatus, String tourStartDate) {
        this.id = id;
        this.tourName = tourName;
        this.bookingDate = bookingDate;
        this.status = status;
        this.price = price;
        this.adults = adults;
        this.children = children;
        this.mealOption = mealOption;
        this.additionalServices = additionalServices;
        this.totalPrice = totalPrice;
        this.paidAmount = paidAmount;
        this.paymentStatus = paymentStatus;
        this.tourStartDate = tourStartDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTourStartDate() {
        return tourStartDate;
    }

    public void setTourStartDate(String tourStartDate) {
        this.tourStartDate = tourStartDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookingModel that = (BookingModel) o;
        return id == that.id && Double.compare(price, that.price) == 0 && Double.compare(amount, that.amount) == 0 && adults == that.adults && children == that.children && Double.compare(totalPrice, that.totalPrice) == 0 && Double.compare(paidAmount, that.paidAmount) == 0 && Objects.equals(userName, that.userName) && Objects.equals(tourName, that.tourName) && Objects.equals(bookingDate, that.bookingDate) && Objects.equals(status, that.status) && Objects.equals(mealOption, that.mealOption) && Objects.equals(additionalServices, that.additionalServices) && Objects.equals(paymentStatus, that.paymentStatus) && Objects.equals(tourStartDate, that.tourStartDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, tourName, bookingDate, status, price, amount, adults, children, mealOption, additionalServices, totalPrice, paidAmount, paymentStatus, tourStartDate);
    }

    @Override
    public String toString() {
        return "BookingModel{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", tourName='" + tourName + '\'' +
                ", bookingDate='" + bookingDate + '\'' +
                ", status='" + status + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", adults=" + adults +
                ", children=" + children +
                ", mealOption='" + mealOption + '\'' +
                ", additionalServices='" + additionalServices + '\'' +
                ", totalPrice=" + totalPrice +
                ", paidAmount=" + paidAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", tourStartDate='" + tourStartDate + '\'' +
                '}';
    }
}