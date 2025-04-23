package server.services;

import server.models.Review;
import server.models.TourRating;

import java.util.List;

public interface ReviewService {
    void addReview(Review review);

    List<Review> getReviewsByTourId(int tourId);

    boolean hasUserReviewedTour(int userId, int tourId);

    TourRating getTourRating(int tourId);
}