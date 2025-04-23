package server.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.DAO.TourDAO;
import server.models.Tour;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TourServiceImplTest {
    private TourServiceImpl tourService;

    @Mock
    private TourDAO tourDAO;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        tourService = new TourServiceImpl(tourDAO);
    }

    @Test
    public void testSearchToursByName() {
        List<Tour> tours = Arrays.asList(
                new Tour(1, "Paris Adventure", "Description", 1000.0, "2025-06-01", "2025-06-07", 1),
                new Tour(2, "Rome Discovery", "Description", 1200.0, "2025-07-01", "2025-07-07", 2)
        );
        when(tourDAO.getAllTours()).thenReturn(tours);

        List<Tour> result = tourService.searchTours("paris", null, null, null, null, null);
        assertEquals(1, result.size());
        assertEquals("Paris Adventure", result.get(0).getName());
    }

    @Test
    public void testSearchToursByPriceRange() {
        List<Tour> tours = Arrays.asList(
                new Tour(1, "Tour 1", "Desc", 800.0, "2025-01-01", "2025-01-07", 1),
                new Tour(2, "Tour 2", "Desc", 1000.0, "2025-02-01", "2025-02-07", 1),
                new Tour(3, "Tour 3", "Desc", 1200.0, "2025-03-01", "2025-03-07", 1)
        );
        when(tourDAO.getAllTours()).thenReturn(tours);

        List<Tour> result = tourService.searchTours(null, 900.0, 1100.0, null, null, null);
        assertEquals(1, result.size());
        assertEquals("Tour 2", result.get(0).getName());
    }

    @Test
    public void testSearchToursByDateRange() {
        List<Tour> tours = Arrays.asList(
                new Tour(1, "Tour 1", "Desc", 1000.0, "2025-06-01", "2025-06-07", 1),
                new Tour(2, "Tour 2", "Desc", 1000.0, "2025-07-01", "2025-07-07", 1),
                new Tour(3, "Tour 3", "Desc", 1000.0, "2025-08-01", "2025-08-07", 1)
        );
        when(tourDAO.getAllTours()).thenReturn(tours);

        List<Tour> result = tourService.searchTours(null, null, null, "2025-06-15", "2025-07-15", null);
        assertEquals(1, result.size());
        assertEquals("Tour 2", result.get(0).getName());
    }

    @Test
    public void testSortToursByPriceAsc() {
        List<Tour> tours = Arrays.asList(
                new Tour(1, "Tour 1", "Desc", 1200.0, "2025-01-01", "2025-01-07", 1),
                new Tour(2, "Tour 2", "Desc", 800.0, "2025-02-01", "2025-02-07", 1),
                new Tour(3, "Tour 3", "Desc", 1000.0, "2025-03-01", "2025-03-07", 1)
        );
        when(tourDAO.getAllTours()).thenReturn(tours);

        List<Tour> result = tourService.searchTours(null, null, null, null, null, "price_asc");
        assertEquals(3, result.size());
        assertEquals("Tour 2", result.get(0).getName());
        assertEquals("Tour 3", result.get(1).getName());
        assertEquals("Tour 1", result.get(2).getName());
    }
}