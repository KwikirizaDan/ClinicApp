package com.clinic.controller;

import com.clinic.dto.PatientDTO;
import com.clinic.entity.Patient;
import com.clinic.service.PatientService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Patient Controller Tests - Tests all CRUD endpoints.
 *
 * Uses @MockBean to mock the service layer (no database needed).
 * Uses @WithMockUser to simulate authenticated requests.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    private PatientDTO patientDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        patientDTO = PatientDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("john@example.com")
                .phone("+1234567890")
                .gender(Patient.Gender.MALE)
                .address("123 Main St")
                .build();

        pageable = PageRequest.of(0, 10);
    }

    // ==================== CREATE ====================

    @Test
    @DisplayName("POST /api/patients - Should create a new patient")
    @WithMockUser(roles = "ADMIN")
    void testCreatePatient() throws Exception {
        when(patientService.createPatient(any(PatientDTO.class))).thenReturn(patientDTO);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(patientService, times(1)).createPatient(any(PatientDTO.class));
    }

    @Test
    @DisplayName("POST /api/patients - Should return 400 for invalid data")
    @WithMockUser(roles = "ADMIN")
    void testCreatePatient_InvalidData() throws Exception {
        PatientDTO invalidPatient = PatientDTO.builder()
                .firstName("")
                .lastName("Doe")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest());
    }

    // ==================== READ ALL ====================

    @Test
    @DisplayName("GET /api/patients - Should return all patients")
    @WithMockUser(roles = "ADMIN")
    void testGetAllPatients() throws Exception {
        List<PatientDTO> patients = Arrays.asList(patientDTO);
        Page<PatientDTO> page = new PageImpl<>(patients, pageable, patients.size());
        when(patientService.getAllPatients(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.content[0].lastName").value("Doe"));

        verify(patientService, times(1)).getAllPatients(any(Pageable.class));
    }

    // ==================== READ ONE ====================

    @Test
    @DisplayName("GET /api/patients/1 - Should return a patient by ID")
    @WithMockUser(roles = "DOCTOR")
    void testGetPatientById() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(patientDTO);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(patientService, times(1)).getPatientById(1L);
    }

    // ==================== UPDATE ====================

    @Test
    @DisplayName("PUT /api/patients/1 - Should update a patient")
    @WithMockUser(roles = "ADMIN")
    void testUpdatePatient() throws Exception {
        PatientDTO updated = PatientDTO.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("jane@example.com")
                .phone("+1234567890")
                .gender(Patient.Gender.FEMALE)
                .build();

        when(patientService.updatePatient(eq(1L), any(PatientDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));

        verify(patientService, times(1)).updatePatient(eq(1L), any(PatientDTO.class));
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("DELETE /api/patients/1 - Should delete a patient")
    @WithMockUser(roles = "ADMIN")
    void testDeletePatient() throws Exception {
        doNothing().when(patientService).deletePatient(1L);

        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());

        verify(patientService, times(1)).deletePatient(1L);
    }

    // ==================== AUTHORIZATION ====================

    @Test
    @DisplayName("GET /api/patients - Should return 403 for PATIENT role")
    @WithMockUser(roles = "PATIENT")
    void testGetAllPatients_ForbiddenForPatient() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/patients/1 - Should return 403 for DOCTOR role")
    @WithMockUser(roles = "DOCTOR")
    void testDeletePatient_ForbiddenForDoctor() throws Exception {
        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/patients - Should return 401 for unauthenticated")
    void testGetAllPatients_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isUnauthorized());
    }
}
