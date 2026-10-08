package com.clinic.service;

import com.clinic.dto.PatientDTO;
import com.clinic.entity.Patient;
import com.clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Layer for Patient - Contains all business logic.
 *
 * This layer sits between the Controller (API) and Repository (database).
 * It handles:
 * - Business rules (e.g., email must be unique)
 * - Transactions (all-or-nothing database operations)
 * - Mapping between Entity (database) and DTO (API contract)
 * - Logging for debugging and monitoring
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    // Logger for this class - use it to log debug info, warnings, errors
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    // Repository is injected by Spring (via @RequiredArgsConstructor)
    private final PatientRepository patientRepository;

    // ==================== CREATE ====================

    /**
     * Create a new patient.
     * Business rule: Email must be unique.
     */
    @Transactional
    public PatientDTO createPatient(PatientDTO dto) {
        logger.debug("Creating new patient: {} {}", dto.getFirstName(), dto.getLastName());

        // Business rule: Check for duplicate email
        if (patientRepository.existsByEmail(dto.getEmail())) {
            logger.warn("Duplicate email attempted: {}", dto.getEmail());
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }

        Patient patient = mapToEntity(dto);
        Patient saved = patientRepository.save(patient);
        logger.info("Patient created successfully with ID: {}", saved.getId());
        return mapToDTO(saved);
    }

    // ==================== READ (with Pagination) ====================

    /**
     * Get all patients with pagination.
     * Pageable contains: page number, page size, and sort order.
     */
    @Transactional(readOnly = true)
    public Page<PatientDTO> getAllPatients(Pageable pageable) {
        logger.debug("Fetching patients - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return patientRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    /**
     * Get a single patient by ID.
     */
    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        logger.debug("Fetching patient with ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Patient not found with ID: {}", id);
                    return new RuntimeException("Patient not found with id: " + id);
                });
        return mapToDTO(patient);
    }

    // ==================== UPDATE ====================

    /**
     * Update an existing patient.
     */
    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO dto) {
        logger.debug("Updating patient with ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Update failed - patient not found with ID: {}", id);
                    return new RuntimeException("Patient not found with id: " + id);
                });

        // Update all fields
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setGender(dto.getGender());
        patient.setAddress(dto.getAddress());
        patient.setMedicalHistory(dto.getMedicalHistory());

        Patient updated = patientRepository.save(patient);
        logger.info("Patient updated successfully with ID: {}", updated.getId());
        return mapToDTO(updated);
    }

    // ==================== DELETE ====================

    /**
     * Delete a patient by ID.
     */
    @Transactional
    public void deletePatient(Long id) {
        logger.debug("Deleting patient with ID: {}", id);
        if (!patientRepository.existsById(id)) {
            logger.warn("Delete failed - patient not found with ID: {}", id);
            throw new RuntimeException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
        logger.info("Patient deleted successfully with ID: {}", id);
    }

    // ==================== SEARCH ====================

    /**
     * Search patients by name (first or last) with pagination.
     */
    @Transactional(readOnly = true)
    public Page<PatientDTO> searchPatients(String keyword, Pageable pageable) {
        logger.debug("Searching patients with keyword: '{}', page: {}", keyword, pageable.getPageNumber());
        return patientRepository.searchByName(keyword, pageable)
                .map(this::mapToDTO);
    }

    // ==================== MAPPING METHODS ====================
    // These convert between Entity (database model) and DTO (API model)

    /** Convert Patient entity to PatientDTO */
    private PatientDTO mapToDTO(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .email(patient.getEmail())
                .phone(patient.getPhone())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .medicalHistory(patient.getMedicalHistory())
                .build();
    }

    /** Convert PatientDTO to Patient entity */
    private Patient mapToEntity(PatientDTO dto) {
        return Patient.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .gender(dto.getGender())
                .address(dto.getAddress())
                .medicalHistory(dto.getMedicalHistory())
                .build();
    }
}
