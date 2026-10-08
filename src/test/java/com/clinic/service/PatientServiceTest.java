package com.clinic.service;

import com.clinic.dto.PatientDTO;
import com.clinic.entity.Patient;
import com.clinic.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Patient Service Unit Tests.
 *
 * Uses Mockito to mock the repository layer.
 * Tests business logic without needing a database.
 */
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private PatientDTO patientDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("john@example.com")
                .phone("+1234567890")
                .gender(Patient.Gender.MALE)
                .address("123 Main St")
                .build();

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
    @DisplayName("Should create a new patient successfully")
    void testCreatePatient() {
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO result = patientService.createPatient(patientDTO);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john@example.com", result.getEmail());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testCreatePatient_DuplicateEmail() {
        when(patientRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            patientService.createPatient(patientDTO);
        });

        assertEquals("Email already exists: john@example.com", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    // ==================== READ ALL ====================

    @Test
    @DisplayName("Should return all patients with pagination")
    void testGetAllPatients() {
        List<Patient> patients = Arrays.asList(patient);
        Page<Patient> page = new PageImpl<>(patients, pageable, patients.size());
        when(patientRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<PatientDTO> result = patientService.getAllPatients(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John", result.getContent().get(0).getFirstName());
    }

    // ==================== READ ONE ====================

    @Test
    @DisplayName("Should return patient by ID")
    void testGetPatientById() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientDTO result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
    }

    @Test
    @DisplayName("Should throw exception when patient not found")
    void testGetPatientById_NotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            patientService.getPatientById(99L);
        });

        assertEquals("Patient not found with id: 99", exception.getMessage());
    }

    // ==================== UPDATE ====================

    @Test
    @DisplayName("Should update patient successfully")
    void testUpdatePatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO updateDTO = PatientDTO.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .build();

        PatientDTO result = patientService.updatePatient(1L, updateDTO);

        assertNotNull(result);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("Should delete patient successfully")
    void testDeletePatient() {
        when(patientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(1L);

        patientService.deletePatient(1L);

        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent patient")
    void testDeletePatient_NotFound() {
        when(patientRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            patientService.deletePatient(99L);
        });

        assertEquals("Patient not found with id: 99", exception.getMessage());
    }

    // ==================== SEARCH ====================

    @Test
    @DisplayName("Should search patients by name with pagination")
    void testSearchPatients() {
        List<Patient> patients = Arrays.asList(patient);
        Page<Patient> page = new PageImpl<>(patients, pageable, patients.size());
        when(patientRepository.searchByName(anyString(), any(Pageable.class))).thenReturn(page);

        Page<PatientDTO> result = patientService.searchPatients("John", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John", result.getContent().get(0).getFirstName());
    }
}
