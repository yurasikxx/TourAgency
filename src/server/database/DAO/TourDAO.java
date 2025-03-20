package server.database.DAO;

import server.models.Tour;

import java.util.List;

public interface TourDAO {
    Tour getTourById(int id);
    List<Tour> getAllTours();
    List<Tour> getToursByDestinationId(int id);
    void addTour(Tour tour);
    void updateTour(Tour tour);
    void deleteTour(int id);
}