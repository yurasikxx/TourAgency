package server.database.DAO.impl;

import server.database.DAO.TourDAO;
import server.models.Tour;
import server.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TourDAOImpl implements TourDAO {
    private Connection connection;

    public TourDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Tour getTourById(int id) {
        String query = "SELECT * FROM Tours WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Tour(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getDouble("price"),
                        resultSet.getString("start_date"),
                        resultSet.getString("end_date"),
                        resultSet.getInt("destination_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Tour> getAllTours() {
        List<Tour> tours = new ArrayList<>();
        String query = "SELECT * FROM Tours";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                tours.add(new Tour(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getDouble("price"),
                        resultSet.getString("start_date"),
                        resultSet.getString("end_date"),
                        resultSet.getInt("destination_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tours;
    }

    @Override
    public void addTour(Tour tour) {
        String query = "INSERT INTO Tours (name, description, price, start_date, end_date, destination_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tour.getName());
            statement.setString(2, tour.getDescription());
            statement.setDouble(3, tour.getPrice());
            statement.setString(4, tour.getStartDate());
            statement.setString(5, tour.getEndDate());
            statement.setInt(6, tour.getDestinationId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTour(Tour tour) {
        String query = "UPDATE Tours SET name = ?, description = ?, price = ?, start_date = ?, end_date = ?, destination_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tour.getName());
            statement.setString(2, tour.getDescription());
            statement.setDouble(3, tour.getPrice());
            statement.setString(4, tour.getStartDate());
            statement.setString(5, tour.getEndDate());
            statement.setInt(6, tour.getDestinationId());
            statement.setInt(7, tour.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTour(int id) {
        String query = "DELETE FROM Tours WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}