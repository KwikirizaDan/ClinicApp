package com.clinic.service;

import com.clinic.dto.AppointmentDTO;
import com.clinic.entity.Appointment;
import com.clinic.entity.Doctor;
import com.clinic.entity.Patient;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.DoctorRepository;
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
 * Service Layer for Appointment - Contains all business logic.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    // ==================== CREATE ====================

    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        logger.debug("Creating appointment for patient ID: {}, doctor ID: {}",
                dto.getPatientId(), dto.getDoctorId());

        // Validate that patient and doctor exist
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appointment = Appointment.builder()
                .appointmentDateTime(dto.getAppointmentDateTime())
                .reason(dto.getReason())
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .notes(dto.getNotes())
                .patient(patient)
                .doctor(doctor)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        logger.info("Appointment created successfully with ID: {}", saved.getId());
        return mapToDTO(saved);
    }

    // ==================== READ (with Pagination) ====================

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getAllAppointments(Pageable pageable) {
        logger.debug("Fetching appointments - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return appointmentRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(Long id) {
        logger.debug("Fetching appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Appointment not found with ID: {}", id);
                    return new RuntimeException("Appointment not found with id: " + id);
                });
        return mapToDTO(appointment);
    }

    // ==================== UPDATE ====================

    @Transactional
    public AppointmentDTO updateAppointment(Long id, AppointmentDTO dto) {
        logger.debug("Updating appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointment.setAppointmentDateTime(dto.getAppointmentDateTime());
        appointment.setReason(dto.getReason());
        appointment.setNotes(dto.getNotes());
        if (dto.getStatus() != null) {
            appointment.setStatus(dto.getStatus());
        }

        return mapToDTO(appointmentRepository.save(appointment));
    }

    // ==================== DELETE ====================

    @Transactional
    public void deleteAppointment(Long id) {
        logger.debug("Deleting appointment with ID: {}", id);
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
        logger.info("Appointment deleted successfully with ID: {}", id);
    }

    // ==================== FILTER (with Pagination) ====================

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getAppointmentsByPatient(Long patientId, Pageable pageable) {
        logger.debug("Fetching appointments for patient ID: {}", patientId);
        return appointmentRepository.findByPatientId(patientId, pageable)
                .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getAppointmentsByDoctor(Long doctorId, Pageable pageable) {
        logger.debug("Fetching appointments for doctor ID: {}", doctorId);
        return appointmentRepository.findByDoctorId(doctorId, pageable)
                .map(this::mapToDTO);
    }

    // ==================== STATUS CHANGES ====================

    @Transactional
    public AppointmentDTO cancelAppointment(Long id) {
        logger.debug("Cancelling appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        return mapToDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDTO completeAppointment(Long id) {
        logger.debug("Completing appointment with ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        return mapToDTO(appointmentRepository.save(appointment));
    }

    // ==================== MAPPING ====================

    private AppointmentDTO mapToDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .appointmentDateTime(appointment.getAppointmentDateTime())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .patientId(appointment.getPatient().getId())
                .doctorId(appointment.getDoctor().getId())
                .build();
    }
}
