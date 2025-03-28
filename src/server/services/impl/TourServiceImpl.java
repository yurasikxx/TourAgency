package server.services.impl;

import server.database.DAO.TourDAO;
import server.database.DAO.impl.BookingDAOImpl;
import server.models.Tour;
import server.services.BookingService;
import server.services.TourService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TourServiceImpl implements TourService {
    private final BookingService bookingService = new BookingServiceImpl(new BookingDAOImpl());
    private final TourDAO tourDAO;

    public TourServiceImpl(TourDAO tourDAO) {
        this.tourDAO = tourDAO;
    }

    @Override
    public Tour getTourById(int id) {
        return tourDAO.getTourById(id);
    }

    @Override
    public List<Tour> getAllTours() {
        return tourDAO.getAllTours();
    }

    @Override
    public List<Tour> getToursByDestinationId(int id) {
        return Collections.emptyList();
    }

    @Override
    public void addTour(Tour tour) {
        tourDAO.addTour(tour);
    }

    @Override
    public void updateTour(Tour tour) {
        tourDAO.updateTour(tour);
    }

    @Override
    public void deleteTour(int id) {
        tourDAO.deleteTour(id);
    }

    @Override
    public List<Tour> searchTours(String searchTerm, Double minPrice, Double maxPrice,
                                  String startDate, String endDate, String sortBy) {
        List<Tour> tours = tourDAO.getAllTours();

        if (searchTerm != null && !searchTerm.equals("null") && !searchTerm.isEmpty()) {
            String finalSearchTerm = searchTerm.toLowerCase();
            tours = tours.stream()
                    .filter(t -> t.getName().toLowerCase().contains(finalSearchTerm) ||
                            t.getDescription().toLowerCase().contains(finalSearchTerm))
                    .collect(Collectors.toList());
        }

        if (minPrice != null) {
            tours = tours.stream()
                    .filter(t -> t.getPrice() >= Double.parseDouble(minPrice.toString()))
                    .collect(Collectors.toList());
        }
        if (maxPrice != null) {
            tours = tours.stream()
                    .filter(t -> t.getPrice() <= Double.parseDouble(maxPrice.toString()))
                    .collect(Collectors.toList());
        }

        if (startDate != null && !startDate.equals("null")) {
            tours = tours.stream()
                    .filter(t -> t.getStartDate().compareTo(startDate) >= 0)
                    .collect(Collectors.toList());
        }
        if (endDate != null && !endDate.equals("null")) {
            tours = tours.stream()
                    .filter(t -> t.getEndDate().compareTo(endDate) <= 0)
                    .collect(Collectors.toList());
        }

        // Сортировка (работает независимо от фильтров)
        if (sortBy != null && !sortBy.equals("null")) {
            switch (sortBy) {
                case "price_asc":
                    tours.sort(Comparator.comparing(Tour::getPrice));
                    break;
                case "price_desc":
                    tours.sort(Comparator.comparing(Tour::getPrice).reversed());
                    break;
                case "date_asc":
                    tours.sort(Comparator.comparing(Tour::getStartDate));
                    break;
                case "date_desc":
                    tours.sort(Comparator.comparing(Tour::getStartDate).reversed());
                    break;
                case "popular":
                    Map<Integer, Long> tourPopularity = bookingService.getTourPopularity();
                    tours.sort((t1, t2) ->
                            Long.compare(
                                    tourPopularity.getOrDefault(t2.getId(), 0L),
                                    tourPopularity.getOrDefault(t1.getId(), 0L)
                            ));
                    break;
            }
        }

        return tours;
    }

    @Override
    public List<Tour> getPopularTours(int limit) {
        return searchTours(null, null, null, null, null, "popular").stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasToursForDestination(int destinationId) {
        return tourDAO.hasToursForDestination(destinationId);
    }
}