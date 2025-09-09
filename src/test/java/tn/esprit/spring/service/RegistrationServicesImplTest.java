package tn.esprit.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;
import tn.esprit.spring.services.RegistrationServicesImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class RegistrationServicesImplTest {

    @InjectMocks
    private RegistrationServicesImpl registrationServices;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private ICourseRepository courseRepository;

    private Skier skieur;
    private Course course;
    private Registration registration;

    @BeforeEach
    void setUp() {
        skieur = new Skier();
        skieur.setNumSkier(1L);
        skieur.setDateOfBirth(LocalDate.now().minusYears(15)); // enfant

        course = new Course();
        course.setNumCourse(1L);
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);
        course.setSupport(Support.SKI);

        registration = new Registration();
        registration.setNumWeek(3);
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_whenValidChildRegistration_thenSuccess() {
        // Arrange
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skieur));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(registrationRepository.countRegistrationsByWeekSkierAndCourse(3, 1L, 1L)).thenReturn(0L);
        when(registrationRepository.countByCourseAndNumWeek(course, 3)).thenReturn(2L);
        when(registrationRepository.save(any())).thenReturn(registration);

        // Act
        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        // Assert
        assertNotNull(result);
        verify(registrationRepository).save(any());
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_whenAlreadyRegistered_thenReturnNull() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skieur));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(registrationRepository.countRegistrationsByWeekSkierAndCourse(3, 1L, 1L)).thenReturn(1L);

        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        assertNull(result);
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_whenFullCourse_thenReturnNull() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skieur));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(registrationRepository.countRegistrationsByWeekSkierAndCourse(3, 1L, 1L)).thenReturn(0L);
        when(registrationRepository.countByCourseAndNumWeek(course, 3)).thenReturn(6L); // complet

        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        assertNull(result);
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_whenSkierOrCourseIsNull_thenReturnNull() {
        when(skierRepository.findById(1L)).thenReturn(Optional.empty());
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        assertNull(result);
    }
}
