package com.clinic.controller;

import com.clinic.dto.DoctorDTO;
import com.clinic.service.DoctorService;
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
 * REST Controller for Doctor endpoints.
 * Supports pagination for all list endpoints.
 */
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Doctors", description = "Doctor management endpoints")
public class DoctorController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    private final DoctorService doctorService;

    // ==================== CREATE ====================

    @PostMapping
    @Operation(summary = "Create a new doctor", description = "Creates a new doctor record (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<DoctorDTO> createDoctor(@Valid @RequestBody DoctorDTO dto) {
        logger.info("POST /api/doctors - Creating doctor: {} {}", dto.getFirstName(), dto.getLastName());
        return new ResponseEntity<>(doctorService.createDoctor(dto), HttpStatus.CREATED);
    }

    // ==================== READ ALL (Paginated) ====================

    @GetMapping
    @Operation(summary = "Get all doctors", description = "Returns a paginated list of all doctors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved doctors"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<DoctorDTO>> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        logger.debug("GET /api/doctors - page: {}, size: {}", page, size);
        return ResponseEntity.ok(doctorService.getAllDoctors(pageable));
    }

    // ==================== READ ONE ====================

    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID", description = "Returns a single doctor by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        logger.debug("GET /api/doctors/{} - Fetching doctor", id);
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    @Operation(summary = "Update a doctor", description = "Updates an existing doctor record (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor updated successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorDTO dto) {
        logger.info("PUT /api/doctors/{} - Updating doctor", id);
        return ResponseEntity.ok(doctorService.updateDoctor(id, dto));
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a doctor", description = "Deletes a doctor record (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        logger.info("DELETE /api/doctors/{} - Deleting doctor", id);
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== FILTER BY SPECIALIZATION (Paginated) ====================

    @GetMapping("/specialization/{spec}")
    @Operation(summary = "Get doctors by specialization", description = "Filter doctors by their specialization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved doctors")
    })
    public ResponseEntity<Page<DoctorDTO>> getBySpecialization(
            @PathVariable String spec,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        logger.debug("GET /api/doctors/specialization/{} - page: {}", spec, page);
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(spec, pageable));
    }
}
