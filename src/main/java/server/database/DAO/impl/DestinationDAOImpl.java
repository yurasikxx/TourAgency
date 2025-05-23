package server.database.DAO.impl;

import server.database.DAO.DestinationDAO;
import server.models.Destination;
import server.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DestinationDAOImpl implements DestinationDAO {

    @Override
    public Destination getDestinationById(int id) {
        String query = "SELECT * FROM Destinations WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Destination(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("country"),
                        resultSet.getString("description")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Destination> getAllDestinations() {
        List<Destination> destinations = new ArrayList<>();
        String query = "SELECT * FROM Destinations";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                destinations.add(new Destination(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("country"),
                        resultSet.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке направлений: " + e.getMessage());
        }
        return destinations;
    }

    @Override
    public void addDestination(Destination destination) {
        String query = "INSERT INTO Destinations (name, country, description) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, destination.getName());
            statement.setString(2, destination.getCountry());
            statement.setString(3, destination.getDescription());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDestination(Destination destination) {
        String query = "UPDATE Destinations SET name = ?, country = ?, description = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, destination.getName());
            statement.setString(2, destination.getCountry());
            statement.setString(3, destination.getDescription());
            statement.setInt(4, destination.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDestination(int id) {
        String query = "DELETE FROM Destinations WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}