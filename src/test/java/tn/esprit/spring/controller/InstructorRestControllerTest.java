package tn.esprit.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.controllers.InstructorRestController;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.services.IInstructorServices;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstructorRestController.class)
class InstructorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IInstructorServices instructorServices;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllInstructors() throws Exception {
        List<Instructor> instructors = List.of(new Instructor(1L, "Ali", "Ben Salah", Support.SKI, TypeCourse.COLLECTIVE_ADULT, null));
        when(instructorServices.retrieveAllInstructors()).thenReturn(instructors);

        mockMvc.perform(get("/instructor/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }


    }
