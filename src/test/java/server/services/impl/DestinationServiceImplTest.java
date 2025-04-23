package server.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.DAO.DestinationDAO;
import server.models.Destination;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DestinationServiceImplTest {
    private DestinationServiceImpl destinationService;

    @Mock
    private DestinationDAO destinationDAO;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        destinationService = new DestinationServiceImpl(destinationDAO);
    }

    @Test
    public void testGetAllDestinations() {
        List<Destination> expected = Arrays.asList(
                new Destination(1, "Paris", "France", "City of Light"),
                new Destination(2, "Rome", "Italy", "Eternal City")
        );
        when(destinationDAO.getAllDestinations()).thenReturn(expected);

        List<Destination> actual = destinationService.getAllDestinations();
        assertEquals(2, actual.size());
        verify(destinationDAO).getAllDestinations();
    }

    @Test
    public void testAddDestination() {
        Destination destination = new Destination(0, "Berlin", "Germany", "Capital city");
        destinationService.addDestination(destination);
        verify(destinationDAO).addDestination(destination);
    }

    @Test
    public void testExistsWhenDestinationExists() {
        when(destinationDAO.getDestinationById(1)).thenReturn(
                new Destination(1, "Paris", "France", "Description")
        );
        assertTrue(destinationService.exists(1));
    }

    @Test
    public void testExistsWhenDestinationNotExists() {
        when(destinationDAO.getDestinationById(1)).thenReturn(null);
        assertFalse(destinationService.exists(1));
    }
}