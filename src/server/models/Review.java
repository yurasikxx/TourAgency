package server.models;

public class Review {
    private int id;
    private int userId;
    private int tourId;
    private int rating;
    private String comment;
    private String reviewDate;

    public Review() {}

    public Review(int id, int userId, int tourId, int rating, String comment, String reviewDate) {
        this.id = id;
        this.userId = userId;
        this.tourId = tourId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", userId=" + userId +
                ", tourId=" + tourId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", reviewDate='" + reviewDate + '\'' +
                '}';
    }
}