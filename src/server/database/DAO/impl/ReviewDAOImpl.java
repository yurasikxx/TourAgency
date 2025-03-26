package server.database.DAO.impl;

import server.database.DAO.ReviewDAO;
import server.database.DatabaseConnection;
import server.models.Review;
import server.models.TourRating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAOImpl implements ReviewDAO {
    @Override
    public void addReview(Review review) {
        String sql = "INSERT INTO Reviews (user_id, tour_id, rating, comment, review_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, review.getUserId());
            stmt.setInt(2, review.getTourId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.setDate(5, Date.valueOf(review.getReviewDate()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    review.setId(rs.getInt(1));
                }
            }
            updateTourRating(review.getTourId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Review> getReviewsByTourId(int tourId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username FROM Reviews r JOIN Users u ON r.user_id = u.id WHERE r.tour_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tourId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("tour_id"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getDate("review_date").toString()
                );
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    @Override
    public boolean hasUserReviewedTour(int userId, int tourId) {
        String sql = "SELECT COUNT(*) FROM Reviews WHERE user_id = ? AND tour_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, tourId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void updateTourRating(int tourId) {
        String sqlUpdate = "UPDATE TourRatings SET average_rating = ?, review_count = ? WHERE tour_id = ?";
        String sqlInsert = "INSERT INTO TourRatings (tour_id, average_rating, review_count) VALUES (?, ?, ?)";
        String sqlSelect = "SELECT AVG(rating) as avg_rating, COUNT(*) as count FROM Reviews WHERE tour_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Получаем средний рейтинг и количество отзывов
            PreparedStatement selectStmt = conn.prepareStatement(sqlSelect);
            selectStmt.setInt(1, tourId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                double avgRating = rs.getDouble("avg_rating");
                int count = rs.getInt("count");

                // Пытаемся обновить существующую запись
                PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate);
                updateStmt.setDouble(1, avgRating);
                updateStmt.setInt(2, count);
                updateStmt.setInt(3, tourId);

                int rowsAffected = updateStmt.executeUpdate();

                // Если запись не существует, создаем новую
                if (rowsAffected == 0) {
                    PreparedStatement insertStmt = conn.prepareStatement(sqlInsert);
                    insertStmt.setInt(1, tourId);
                    insertStmt.setDouble(2, avgRating);
                    insertStmt.setInt(3, count);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TourRating getTourRating(int tourId) {
        String sql = "SELECT average_rating, review_count FROM TourRatings WHERE tour_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tourId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new TourRating(
                        tourId,
                        rs.getDouble("average_rating"),
                        rs.getInt("review_count")
                );
            } else {
                // Если запись не найдена, возвращаем рейтинг по умолчанию
                return new TourRating(tourId, 0.0, 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new TourRating(tourId, 0.0, 0);
        }
    }
}