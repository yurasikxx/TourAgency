package server.services;

import server.models.Destination;

import java.util.List;

public interface DestinationService {
    Destination getDestinationById(int id);
    List<Destination> getAllDestinations();
    void addDestination(Destination destination);
    void updateDestination(Destination destination);
    void deleteDestination(int id);
    boolean exists(int destinationId);
}