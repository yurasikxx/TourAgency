package main.server.services;

import main.server.models.Tour;
import main.server.database.DAO.TourDAO;

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