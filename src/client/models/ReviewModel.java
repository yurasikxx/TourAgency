package client.models;

public class ReviewModel {
    private int id;
    private int userId;
    private String username;
    private int rating;
    private String comment;
    private String reviewDate;

    public ReviewModel(int id, int userId, String username, int rating, String comment, String reviewDate) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    @Override
    public String toString() {
        return username + " (" + rating + "/5): " + comment;
    }
}