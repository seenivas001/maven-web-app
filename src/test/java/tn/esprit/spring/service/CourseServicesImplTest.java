package tn.esprit.spring.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.services.CourseServicesImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class CourseServicesImplTest {

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseServicesImpl courseServices;

    private Course validCourse;

    @BeforeEach
    void setUp() {

        validCourse = new Course();
        validCourse.setNumCourse(1L);
        validCourse.setLevel(2);
        validCourse.setTypeCourse(TypeCourse.COLLECTIVE_ADULT);
        validCourse.setPrice(150.0f);
        validCourse.setTimeSlot(90);


        Course invalidCourse = new Course();
        invalidCourse.setPrice(-50.0f); // Prix négatif
    }

    // ===== TESTS POSITIFS =====

    @Test
    void retrieveAllCourses_withExistingCourses_shouldReturnList() {
        // Arrange
        when(courseRepository.findAll()).thenReturn(Collections.singletonList(validCourse));

        // Act
        List<Course> result = courseServices.retrieveAllCourses();

        // Assert
        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void addCourse_withValidData_shouldReturnSavedCourse() {
        // Arrange
        when(courseRepository.save(any(Course.class))).thenReturn(validCourse);

        // Act
        Course result = courseServices.addCourse(validCourse);

        // Assert
        assertNotNull(result.getNumCourse());
        assertEquals(validCourse.getLevel(), result.getLevel());
        verify(courseRepository, times(1)).save(validCourse);
    }

    @Test
    void updateCourse_withValidId_shouldUpdateCourse() {
        // Arrange
        when(courseRepository.save(any(Course.class))).thenReturn(validCourse);
        validCourse.setLevel(3); // Modification du niveau

        // Act
        Course result = courseServices.updateCourse(validCourse);

        // Assert
        assertEquals(3, result.getLevel());
        verify(courseRepository, times(1)).save(validCourse);
    }

    @Test
    void retrieveCourse_withValidId_shouldReturnCourse() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(validCourse));

        // Act
        Course result = courseServices.retrieveCourse(1L);

        // Assert
        assertNotNull(result);
        assertEquals(validCourse.getNumCourse(), result.getNumCourse());
        verify(courseRepository, times(1)).findById(1L);
    }

    // ===== TESTS NÉGATIFS =====

    @Test
    void retrieveAllCourses_whenNoCourses_shouldReturnEmptyList() {
        // Arrange
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Course> result = courseServices.retrieveAllCourses();

        // Assert
        assertTrue(result.isEmpty());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void retrieveCourse_withInvalidId_shouldReturnNull() {
        // Arrange
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Course result = courseServices.retrieveCourse(99L);

        // Assert
        assertNull(result);
        verify(courseRepository, times(1)).findById(99L);
    }


    // ===== TESTS DE PERFORMANCE =====
    @Test
    void retrieveAllCourses_withLargeDataset_shouldRespondQuickly() {
        // Arrange
        List<Course> largeList = createLargeCourseList();
        when(courseRepository.findAll()).thenReturn(largeList);

        // Act & Assert
        assertTimeoutPreemptively(
                java.time.Duration.ofMillis(500),
                () -> courseServices.retrieveAllCourses()
        );
    }

    // ===== METHODES UTILITAIRES =====
    private List<Course> createLargeCourseList() {
        return Collections.nCopies(1000, validCourse);
    }
}
