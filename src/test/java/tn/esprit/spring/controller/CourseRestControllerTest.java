package tn.esprit.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.controllers.CourseRestController;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.services.ICourseServices;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseRestController.class)
 class CourseRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICourseServices courseServices;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddCourse() throws Exception {
        Course course = new Course();
        course.setNumCourse(1L);
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);
        course.setSupport(Support.SKI);

        Mockito.when(courseServices.addCourse(any())).thenReturn(course);

        mockMvc.perform(post("/course/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numCourse").value(1L));
    }

    @Test
    void testGetAllCourses() throws Exception {
        Course course1 = new Course(1L, TypeCourse.COLLECTIVE_CHILDREN, Support.SKI);
        Course course2 = new Course(2L, TypeCourse.INDIVIDUAL, Support.SNOWBOARD);

        Mockito.when(courseServices.retrieveAllCourses()).thenReturn(List.of(course1, course2));

        mockMvc.perform(get("/course/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testUpdateCourse() throws Exception {
        Course course = new Course(1L, TypeCourse.COLLECTIVE_CHILDREN, Support.SKI);

        Mockito.when(courseServices.updateCourse(any())).thenReturn(course);

        mockMvc.perform(put("/course/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numCourse").value(1L));
    }

    @Test
    void testGetCourseById() throws Exception {
        Course course = new Course(1L, TypeCourse.INDIVIDUAL, Support.SNOWBOARD);

        Mockito.when(courseServices.retrieveCourse(1L)).thenReturn(course);

        mockMvc.perform(get("/course/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numCourse").value(1L));
    }
}
