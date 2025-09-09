package tn.esprit.spring.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;
import tn.esprit.spring.services.SkierServicesImpl;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class SkierServicesImplTest {

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private IPisteRepository pisteRepository;

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SkierServicesImpl skierServices;

    private Skier skier;
    private Subscription subscription;
    private Piste piste;
    private Course course;
    private Registration registration;

    @BeforeEach
    void setUp() {

        subscription = new Subscription();
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscription.setStartDate(LocalDate.now());


        skier = new Skier();
        skier.setNumSkier(1L);
        skier.setSubscription(subscription);
        skier.setRegistrations(new HashSet<>());
        skier.setPistes(new HashSet<>());

        // Setup a piste
        piste = new Piste();
        piste.setNumPiste(1L);


        course = new Course();
        course.setNumCourse(1L);


        registration = new Registration();
        registration.setSkier(skier);
        registration.setCourse(course);

        // Add registration to skier's registrations
        skier.getRegistrations().add(registration);
    }

    // ======== Positive Tests ========

    @Test
    void retrieveAllSkiers_shouldReturnList() {
        // Arrange
        when(skierRepository.findAll()).thenReturn(Collections.singletonList(skier));

        // Act
        List<Skier> result = skierServices.retrieveAllSkiers();

        // Assert
        assertEquals(1, result.size());
        verify(skierRepository, times(1)).findAll();
    }

    @Test
    void addSkier_AnnualSubscription_shouldSetEndDateOneYearLater() {
        // Arrange
        when(skierRepository.save(any(Skier.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Skier savedSkier = skierServices.addSkier(skier);

        // Assert
        assertNotNull(savedSkier.getSubscription().getEndDate());
        assertEquals(subscription.getStartDate().plusYears(1), savedSkier.getSubscription().getEndDate());
        verify(skierRepository, times(1)).save(skier);
    }

    @Test
    void assignSkierToSubscription_shouldAssignSubscriptionAndSave() {
        // Arrange
        Subscription newSubscription = new Subscription();
        newSubscription.setTypeSub(TypeSubscription.MONTHLY);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(subscriptionRepository.findById(2L)).thenReturn(Optional.of(newSubscription));
        when(skierRepository.save(any(Skier.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Skier updatedSkier = skierServices.assignSkierToSubscription(1L, 2L);

        // Assert
        assertEquals(newSubscription, updatedSkier.getSubscription());
        verify(skierRepository).findById(1L);
        verify(subscriptionRepository).findById(2L);
        verify(skierRepository).save(skier);
    }

    @Test
    void addSkierAndAssignToCourse_shouldSaveSkierAndRegistrations() {
        // Arrange
        when(skierRepository.save(any(Skier.class))).thenReturn(skier);
        when(courseRepository.getById(1L)).thenReturn(course);
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        // Act
        Skier result = skierServices.addSkierAndAssignToCourse(skier, 1L);

        // Assert
        for (Registration reg : result.getRegistrations()) {
            assertEquals(skier, reg.getSkier());
            assertEquals(course, reg.getCourse());
        }
        verify(skierRepository).save(skier);
        verify(courseRepository).getById(1L);
        verify(registrationRepository, times(result.getRegistrations().size())).save(any(Registration.class));
    }

    @Test
    void removeSkier_shouldCallDeleteById() {
        // Act
        skierServices.removeSkier(1L);

        // Assert
        verify(skierRepository).deleteById(1L);
    }

    @Test
    void retrieveSkier_withExistingId_shouldReturnSkier() {
        // Arrange
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));

        // Act
        Skier result = skierServices.retrieveSkier(1L);

        // Assert
        assertEquals(skier, result);
        verify(skierRepository).findById(1L);
    }

    @Test
    void assignSkierToPiste_shouldAddPisteToSkier() {
        // Arrange
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));
        when(skierRepository.save(any(Skier.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Skier result = skierServices.assignSkierToPiste(1L, 1L);

        // Assert
        assertTrue(result.getPistes().contains(piste));
        verify(skierRepository).save(skier);
    }

    @Test
    void retrieveSkiersBySubscriptionType_shouldReturnCorrectList() {
        // Arrange
        List<Skier> skierList = Collections.singletonList(skier);
        when(skierRepository.findBySubscriptionTypeSub(TypeSubscription.ANNUAL)).thenReturn(skierList);

        // Act
        List<Skier> result = skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.ANNUAL);

        // Assert
        assertEquals(skierList, result);
        verify(skierRepository).findBySubscriptionTypeSub(TypeSubscription.ANNUAL);
    }

    @Test
    void assignSkierToPiste_withNullPistes_shouldInitializeAndAdd() {
        // Arrange
        skier.setPistes(null);
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));
        when(skierRepository.save(any(Skier.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Skier result = skierServices.assignSkierToPiste(1L, 1L);

        // Assert
        assertNotNull(result.getPistes());
        assertTrue(result.getPistes().contains(piste));
        verify(skierRepository).save(skier);
    }
}
