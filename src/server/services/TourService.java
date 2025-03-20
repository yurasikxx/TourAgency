package server.services;

import server.models.Tour;

import java.util.List;

public interface TourService {
    Tour getTourById(int id);
    List<Tour> getAllTours();
    List<Tour> getToursByDestinationId(int id);
    void addTour(Tour tour);
    void updateTour(Tour tour);
    void deleteTour(int id);
}