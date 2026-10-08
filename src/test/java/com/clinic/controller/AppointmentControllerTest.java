package com.clinic.controller;

import com.clinic.dto.AppointmentDTO;
import com.clinic.entity.Appointment;
import com.clinic.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Appointment Controller Tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppointmentService appointmentService;

    private AppointmentDTO appointmentDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        appointmentDTO = AppointmentDTO.builder()
                .id(1L)
                .appointmentDateTime(LocalDateTime.now().plusDays(1))
                .reason("Checkup")
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .patientId(1L)
                .doctorId(1L)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("POST /api/appointments - Should create appointment")
    @WithMockUser(roles = "DOCTOR")
    void testCreateAppointment() throws Exception {
        when(appointmentService.createAppointment(any(AppointmentDTO.class))).thenReturn(appointmentDTO);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reason").value("Checkup"));
    }

    @Test
    @DisplayName("GET /api/appointments - Should return all appointments")
    @WithMockUser(roles = "ADMIN")
    void testGetAllAppointments() throws Exception {
        List<AppointmentDTO> appointments = Arrays.asList(appointmentDTO);
        Page<AppointmentDTO> page = new PageImpl<>(appointments, pageable, appointments.size());
        when(appointmentService.getAllAppointments(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reason").value("Checkup"));
    }

    @Test
    @DisplayName("PATCH /api/appointments/1/cancel - Should cancel appointment")
    @WithMockUser(roles = "ADMIN")
    void testCancelAppointment() throws Exception {
        AppointmentDTO cancelled = AppointmentDTO.builder()
                .id(1L)
                .status(Appointment.AppointmentStatus.CANCELLED)
                .build();
        when(appointmentService.cancelAppointment(1L)).thenReturn(cancelled);

        mockMvc.perform(patch("/api/appointments/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("PATCH /api/appointments/1/complete - Should complete appointment")
    @WithMockUser(roles = "DOCTOR")
    void testCompleteAppointment() throws Exception {
        AppointmentDTO completed = AppointmentDTO.builder()
                .id(1L)
                .status(Appointment.AppointmentStatus.COMPLETED)
                .build();
        when(appointmentService.completeAppointment(1L)).thenReturn(completed);

        mockMvc.perform(patch("/api/appointments/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("DELETE /api/appointments/1 - Should return 403 for DOCTOR role")
    @WithMockUser(roles = "DOCTOR")
    void testDeleteAppointment_ForbiddenForDoctor() throws Exception {
        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isForbidden());
    }
}
