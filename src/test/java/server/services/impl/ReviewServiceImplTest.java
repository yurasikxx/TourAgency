package server.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.DAO.ReviewDAO;
import server.models.Review;
import server.models.TourRating;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ReviewServiceImplTest {
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewDAO reviewDAO;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reviewService = new ReviewServiceImpl(reviewDAO);
    }

    @Test
    public void testAddReviewWhenNotReviewed() {
        Review review = new Review(0, 1, 1, 5, "Great tour!", "2025-01-01");
        when(reviewDAO.hasUserReviewedTour(1, 1)).thenReturn(false);

        reviewService.addReview(review);
        verify(reviewDAO).addReview(review);
    }

    @Test
    public void testAddReviewWhenAlreadyReviewed() {
        Review review = new Review(0, 1, 1, 5, "Great tour!", "2025-01-01");
        when(reviewDAO.hasUserReviewedTour(1, 1)).thenReturn(true);

        reviewService.addReview(review);
        verify(reviewDAO, never()).addReview(review);
    }

    @Test
    public void testGetReviewsByTourId() {
        List<Review> expected = Arrays.asList(
                new Review(1, 1, 1, 5, "Excellent", "2025-01-01"),
                new Review(2, 2, 1, 4, "Very good", "2025-01-02")
        );
        when(reviewDAO.getReviewsByTourId(1)).thenReturn(expected);

        List<Review> actual = reviewService.getReviewsByTourId(1);
        assertEquals(2, actual.size());
        verify(reviewDAO).getReviewsByTourId(1);
    }

    @Test
    public void testGetTourRating() {
        TourRating expected = new TourRating(1, 4.5, 10);
        when(reviewDAO.getTourRating(1)).thenReturn(expected);

        TourRating actual = reviewService.getTourRating(1);
        assertEquals(expected, actual);
        verify(reviewDAO).getTourRating(1);
    }
}