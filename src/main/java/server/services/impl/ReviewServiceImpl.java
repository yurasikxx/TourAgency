package server.services.impl;

import server.database.DAO.ReviewDAO;
import server.models.Review;
import server.models.TourRating;
import server.services.ReviewService;

import java.util.List;

public class ReviewServiceImpl implements ReviewService {
    private final ReviewDAO reviewDAO;

    public ReviewServiceImpl(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }

    @Override
    public void addReview(Review review) {
        if (!hasUserReviewedTour(review.getUserId(), review.getTourId())) {
            reviewDAO.addReview(review);
        }
    }

    @Override
    public List<Review> getReviewsByTourId(int tourId) {
        return reviewDAO.getReviewsByTourId(tourId);
    }

    @Override
    public boolean hasUserReviewedTour(int userId, int tourId) {
        return reviewDAO.hasUserReviewedTour(userId, tourId);
    }

    @Override
    public TourRating getTourRating(int tourId) {
        return reviewDAO.getTourRating(tourId);
    }
}