package server.models;

public class TourRating {
    private int tourId;
    private double averageRating;
    private int reviewCount;

    public TourRating() {}

    public TourRating(int tourId, double averageRating, int reviewCount) {
        this.tourId = tourId;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    // Геттеры и сеттеры
    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}