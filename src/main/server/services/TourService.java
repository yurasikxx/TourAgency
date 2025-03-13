package main.server.services;

import main.server.models.Tour;

import java.util.List;

public interface TourService {
    Tour getTourById(int id);
    List<Tour> getAllTours();
    void addTour(Tour tour);
    void updateTour(Tour tour);
    void deleteTour(int id);
}