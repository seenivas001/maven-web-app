package tn.esprit.spring.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.repositories.IPisteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import tn.esprit.spring.services.PisteServicesImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class PisteServicesImplTest {

    @Mock
    private IPisteRepository pisteRepository;

    @InjectMocks
    private PisteServicesImpl pisteServices;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRetrieveAllPistes() {
        List<Piste> pistes = Arrays.asList(
                new Piste(1L, "Piste Rouge", Color.RED, 1200, 30, null),
                new Piste(2L, "Piste Verte", Color.GREEN, 900, 15, null)
        );

        when(pisteRepository.findAll()).thenReturn(pistes);

        List<Piste> result = pisteServices.retrieveAllPistes();

        assertEquals(2, result.size());
        verify(pisteRepository).findAll();
    }

    @Test
    void testAddPiste() {
        Piste piste = new Piste(null, "Piste Bleue", Color.BLUE, 1000, 20, null);
        Piste savedPiste = new Piste(3L, "Piste Bleue", Color.BLUE, 1000, 20, null);

        when(pisteRepository.save(piste)).thenReturn(savedPiste);

        Piste result = pisteServices.addPiste(piste);

        assertNotNull(result);
        assertEquals(3L, result.getNumPiste());
        assertEquals("Piste Bleue", result.getNamePiste());
        verify(pisteRepository).save(piste);
    }

    @Test
    void testRemovePiste() {
        Long idToRemove = 5L;

        pisteServices.removePiste(idToRemove);

        verify(pisteRepository).deleteById(idToRemove);
    }

    @Test
    void testRetrievePiste_Found() {
        Piste piste = new Piste(6L, "Piste Noire", Color.BLACK, 1500, 40, null);

        when(pisteRepository.findById(6L)).thenReturn(Optional.of(piste));

        Piste result = pisteServices.retrievePiste(6L);

        assertNotNull(result);
        assertEquals("Piste Noire", result.getNamePiste());
        verify(pisteRepository).findById(6L);
    }

    @Test
    void testRetrievePiste_NotFound() {
        when(pisteRepository.findById(99L)).thenReturn(Optional.empty());

        Piste result = pisteServices.retrievePiste(99L);

        assertNull(result);
        verify(pisteRepository).findById(99L);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}
