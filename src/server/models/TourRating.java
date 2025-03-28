package server.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TourRating {
    private int tourId;
    private double averageRating;
    private int reviewCount;

    public TourRating() {}

    public TourRating(int tourId, double averageRating, int reviewCount) {
        this.tourId = tourId;
        setAverageRating(averageRating);
        setReviewCount(reviewCount);
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public double getAverageRating() {
        return BigDecimal.valueOf(averageRating)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = BigDecimal.valueOf(averageRating)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        if (reviewCount < 0) {
            throw new IllegalArgumentException("Review count cannot be negative");
        }
        this.reviewCount = reviewCount;
    }

    @Override
    public String toString() {
        return "TourRating{" +
                "tourId=" + tourId +
                ", averageRating=" + getAverageRating() +
                ", reviewCount=" + reviewCount +
                '}';
    }
}