package main.server.database.DAO;

import main.server.models.Destination;

import java.util.List;

public interface DestinationDAO {
    Destination getDestinationById(int id);
    List<Destination> getAllDestinations();
    void addDestination(Destination destination);
    void updateDestination(Destination destination);
    void deleteDestination(int id);
}