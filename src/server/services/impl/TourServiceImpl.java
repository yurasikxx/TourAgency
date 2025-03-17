package server.services.impl;

import server.models.Tour;
import server.database.DAO.TourDAO;
import server.services.TourService;

import java.util.List;

public class TourServiceImpl implements TourService {
    private TourDAO tourDAO;

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
}