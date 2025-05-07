package server.services;

import server.models.Tour;

import java.util.List;

public interface TourService {
    Tour getTourById(int id);

    Tour getTourByName(String name);

    List<Tour> getAllTours();

    List<Tour> getToursByDestinationId(int id);

    void addTour(Tour tour);

    void updateTour(Tour tour);

    void deleteTour(int id);

    List<Tour> searchTours(String searchTerm, Double minPrice, Double maxPrice,
                           String startDate, String endDate, String sortBy);

    List<Tour> getPopularTours(int limit);

    boolean hasToursForDestination(int destinationId);
}