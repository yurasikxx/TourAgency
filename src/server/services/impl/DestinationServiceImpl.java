package server.services.impl;

import server.models.Destination;
import server.database.DAO.DestinationDAO;
import server.services.DestinationService;

import java.util.List;

public class DestinationServiceImpl implements DestinationService {
    private DestinationDAO destinationDAO;

    public DestinationServiceImpl(DestinationDAO destinationDAO) {
        this.destinationDAO = destinationDAO;
    }

    @Override
    public Destination getDestinationById(int id) {
        return destinationDAO.getDestinationById(id);
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
}