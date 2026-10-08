package com.clinic.controller;

import com.clinic.dto.PatientDTO;
import com.clinic.service.PatientService;
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
 * REST Controller for Patient endpoints.
 *
 * This is the API layer - it handles HTTP requests and responses.
 * It delegates all business logic to the Service layer.
 *
 * Pagination is supported via query parameters:
 * GET /api/patients?page=0&size=10&sort=lastName,asc
 *
 * - page: zero-based page number (default: 0)
 * - size: number of items per page (default: 10)
 * - sort: field to sort by, optionally with ,asc or ,desc
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Patients", description = "Patient management endpoints")
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    // ==================== CREATE ====================

    /**
     * POST /api/patients - Create a new patient.
     * Requires ADMIN or DOCTOR role.
     */
    @PostMapping
    @Operation(summary = "Create a new patient", description = "Creates a new patient record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO dto) {
        logger.info("POST /api/patients - Creating patient: {} {}", dto.getFirstName(), dto.getLastName());
        PatientDTO created = patientService.createPatient(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ==================== READ ALL (Paginated) ====================

    /**
     * GET /api/patients - Get all patients with pagination.
     * Query params: page, size, sort
     * Example: GET /api/patients?page=0&size=5&sort=lastName,asc
     */
    @GetMapping
    @Operation(summary = "Get all patients", description = "Returns a paginated list of all patients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved patients"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<PatientDTO>> getAllPatients(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field (e.g., lastName,asc or firstName,desc)")
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        // Parse sort parameters: "lastName,asc" -> Sort by lastName ascending
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        logger.debug("GET /api/patients - page: {}, size: {}, sort: {}", page, size, sort[0]);
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    // ==================== READ ONE ====================

    /**
     * GET /api/patients/{id} - Get a single patient by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Returns a single patient by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<PatientDTO> getPatientById(
            @Parameter(description = "Patient ID") @PathVariable Long id) {
        logger.debug("GET /api/patients/{} - Fetching patient", id);
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    // ==================== UPDATE ====================

    /**
     * PUT /api/patients/{id} - Update an existing patient.
     * Requires ADMIN or DOCTOR role.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a patient", description = "Updates an existing patient record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<PatientDTO> updatePatient(
            @Parameter(description = "Patient ID") @PathVariable Long id,
            @Valid @RequestBody PatientDTO dto) {
        logger.info("PUT /api/patients/{} - Updating patient", id);
        return ResponseEntity.ok(patientService.updatePatient(id, dto));
    }

    // ==================== DELETE ====================

    /**
     * DELETE /api/patients/{id} - Delete a patient.
     * Requires ADMIN role only.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient", description = "Deletes a patient record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "Patient ID") @PathVariable Long id) {
        logger.info("DELETE /api/patients/{} - Deleting patient", id);
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== SEARCH (Paginated) ====================

    /**
     * GET /api/patients/search?keyword=john&page=0&size=10
     * Search patients by first or last name with pagination.
     */
    @GetMapping("/search")
    @Operation(summary = "Search patients", description = "Search patients by name with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<PatientDTO>> searchPatients(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        logger.debug("GET /api/patients/search - keyword: '{}', page: {}", keyword, page);
        return ResponseEntity.ok(patientService.searchPatients(keyword, pageable));
    }
}
