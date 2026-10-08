package com.clinic.controller;

import com.clinic.dto.AppointmentDTO;
import com.clinic.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Appointment endpoints.
 * Supports pagination for all list endpoints.
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Appointments", description = "Appointment management endpoints")
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;

    // ==================== CREATE ====================

    @PostMapping
    @Operation(summary = "Create a new appointment", description = "Schedules a new appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO dto) {
        logger.info("POST /api/appointments - Creating appointment");
        return new ResponseEntity<>(appointmentService.createAppointment(dto), HttpStatus.CREATED);
    }

    // ==================== READ ALL (Paginated) ====================

    @GetMapping
    @Operation(summary = "Get all appointments", description = "Returns a paginated list of all appointments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointments"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<AppointmentDTO>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        logger.debug("GET /api/appointments - page: {}, size: {}", page, size);
        return ResponseEntity.ok(appointmentService.getAllAppointments(pageable));
    }

    // ==================== READ ONE ====================

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Returns a single appointment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment found"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        logger.debug("GET /api/appointments/{} - Fetching appointment", id);
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    @Operation(summary = "Update an appointment", description = "Updates an existing appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentDTO dto) {
        logger.info("PUT /api/appointments/{} - Updating appointment", id);
        return ResponseEntity.ok(appointmentService.updateAppointment(id, dto));
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an appointment", description = "Deletes an appointment (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Appointment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        logger.info("DELETE /api/appointments/{} - Deleting appointment", id);
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== FILTER BY PATIENT (Paginated) ====================

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get appointments by patient", description = "Returns all appointments for a specific patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointments")
    })
    public ResponseEntity<Page<AppointmentDTO>> getByPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        logger.debug("GET /api/appointments/patient/{} - page: {}", patientId, page);
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId, pageable));
    }

    // ==================== FILTER BY DOCTOR (Paginated) ====================

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get appointments by doctor", description = "Returns all appointments for a specific doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointments")
    })
    public ResponseEntity<Page<AppointmentDTO>> getByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        logger.debug("GET /api/appointments/doctor/{} - page: {}", doctorId, page);
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId, pageable));
    }

    // ==================== CANCEL ====================

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an appointment", description = "Changes appointment status to CANCELLED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment cancelled"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable Long id) {
        logger.info("PATCH /api/appointments/{}/cancel - Cancelling appointment", id);
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    // ==================== COMPLETE ====================

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Complete an appointment", description = "Changes appointment status to COMPLETED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment completed"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<AppointmentDTO> completeAppointment(@PathVariable Long id) {
        logger.info("PATCH /api/appointments/{}/complete - Completing appointment", id);
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }
}
