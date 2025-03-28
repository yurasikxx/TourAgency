package server.services.impl;

import server.database.DAO.DestinationDAO;
import server.models.Destination;
import server.services.DestinationService;

import java.util.List;

public class DestinationServiceImpl implements DestinationService {
    private final DestinationDAO destinationDAO;

    public DestinationServiceImpl(DestinationDAO destinationDAO) {
        this.destinationDAO = destinationDAO;
    }

    @Override
    public List<Destination> getAllDestinations() {
        return destinationDAO.getAllDestinations();
    }

    @Override
    public void addDestination(Destination destination) {
        destinationDAO.addDestination(destination);
    }

    @Override
    public void updateDestination(Destination destination) {
        destinationDAO.updateDestination(destination);
    }

    @Override
    public void deleteDestination(int id) {
        destinationDAO.deleteDestination(id);
    }

    @Override
    public boolean exists(int destinationId) {
        return destinationDAO.getDestinationById(destinationId) != null;
    }
}