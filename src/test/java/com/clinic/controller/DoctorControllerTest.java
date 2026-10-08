package com.clinic.controller;

import com.clinic.dto.DoctorDTO;
import com.clinic.service.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Doctor Controller Tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorService doctorService;

    private DoctorDTO doctorDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        doctorDTO = DoctorDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .specialization("Cardiology")
                .email("john.smith@clinic.com")
                .phone("+1234567890")
                .yearsOfExperience(15)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("POST /api/doctors - Should create doctor (Admin only)")
    @WithMockUser(roles = "ADMIN")
    void testCreateDoctor() throws Exception {
        when(doctorService.createDoctor(any(DoctorDTO.class))).thenReturn(doctorDTO);

        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.specialization").value("Cardiology"));
    }

    @Test
    @DisplayName("POST /api/doctors - Should return 403 for DOCTOR role")
    @WithMockUser(roles = "DOCTOR")
    void testCreateDoctor_ForbiddenForDoctor() throws Exception {
        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/doctors - Should return all doctors")
    @WithMockUser(roles = "PATIENT")
    void testGetAllDoctors() throws Exception {
        List<DoctorDTO> doctors = Arrays.asList(doctorDTO);
        Page<DoctorDTO> page = new PageImpl<>(doctors, pageable, doctors.size());
        when(doctorService.getAllDoctors(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("John"));
    }

    @Test
    @DisplayName("GET /api/doctors/specialization/Cardiology - Should filter by specialization")
    @WithMockUser(roles = "ADMIN")
    void testGetBySpecialization() throws Exception {
        List<DoctorDTO> doctors = Arrays.asList(doctorDTO);
        Page<DoctorDTO> page = new PageImpl<>(doctors, pageable, doctors.size());
        when(doctorService.getDoctorsBySpecialization(anyString(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/doctors/specialization/Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].specialization").value("Cardiology"));
    }
}
