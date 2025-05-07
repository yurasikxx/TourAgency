package server.database.DAO;

import server.models.Tour;

import java.util.List;

public interface TourDAO {
    Tour getTourById(int id);

    Tour getTourByName(String name);

    List<Tour> getAllTours();

    void addTour(Tour tour);

    void updateTour(Tour tour);

    void deleteTour(int id);

    boolean hasToursForDestination(int destinationId);
}