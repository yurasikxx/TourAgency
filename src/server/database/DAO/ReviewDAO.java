package server.database.DAO;

import server.models.Review;
import server.models.TourRating;

import java.util.List;

public interface ReviewDAO {
    void addReview(Review review);
    List<Review> getReviewsByTourId(int tourId);
    boolean hasUserReviewedTour(int userId, int tourId);
    void updateTourRating(int tourId);
    TourRating getTourRating(int tourId);
}