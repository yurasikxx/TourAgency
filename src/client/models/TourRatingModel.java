package client.models;

public class TourRatingModel {
    private double averageRating;
    private int reviewCount;

    public TourRatingModel(double averageRating, int reviewCount) {
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
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

    @Override
    public String toString() {
        return "TourRatingModel{" +
                "averageRating=" + averageRating +
                ", reviewCount=" + reviewCount +
                '}';
    }
}