package tn.esprit.spring.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IInstructorRepository;
import tn.esprit.spring.services.InstructorServicesImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
 class InstructorServicesImplTest {

    @Mock
    private IInstructorRepository instructorRepository;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private InstructorServicesImpl instructorServices;

    @Test
    void testAddInstructor() {
        Instructor instructor = new Instructor();
        instructor.setFirstName("John");

        when(instructorRepository.save(instructor)).thenReturn(instructor);

        Instructor result = instructorServices.addInstructor(instructor);

        assertEquals("John", result.getFirstName());
        verify(instructorRepository).save(instructor);
    }

    @Test
    void testRetrieveAllInstructors() {
        List<Instructor> mockList = List.of(new Instructor(), new Instructor());
        when(instructorRepository.findAll()).thenReturn(mockList);

        List<Instructor> result = instructorServices.retrieveAllInstructors();

        assertEquals(2, result.size());
        verify(instructorRepository).findAll();
    }

    @Test
    void testUpdateInstructor() {
        Instructor instructor = new Instructor();
        instructor.setLastName("Doe");

        when(instructorRepository.save(instructor)).thenReturn(instructor);

        Instructor result = instructorServices.updateInstructor(instructor);

        assertEquals("Doe", result.getLastName());
        verify(instructorRepository).save(instructor);
    }

    @Test
    void testRetrieveInstructorFound() {
        Instructor instructor = new Instructor();
        instructor.setNumInstructor(1L);

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));

        Instructor result = instructorServices.retrieveInstructor(1L);

        assertNotNull(result);
        assertEquals(1L, result.getNumInstructor());
    }

    @Test
    void testRetrieveInstructorNotFound() {
        when(instructorRepository.findById(99L)).thenReturn(Optional.empty());

        Instructor result = instructorServices.retrieveInstructor(99L);

        assertNull(result);
    }

    @Test
    void testAddInstructorAndAssignToCourse() {
        Course course = new Course();
        course.setNumCourse(10L);
        Instructor instructor = new Instructor();
        instructor.setFirstName("Alice");

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(instructorRepository.save(any())).thenReturn(instructor);

        Instructor result = instructorServices.addInstructorAndAssignToCourse(instructor, 10L);

        assertEquals("Alice", result.getFirstName());
        assertEquals(1, result.getCourses().size());
        verify(instructorRepository).save(instructor);
    }
}
