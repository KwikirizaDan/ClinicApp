package com.clinic.repository;

import com.clinic.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository Interface for Appointment.
 * Includes pagination support for large datasets.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ==================== BASIC QUERIES ====================

    /** Find all appointments for a specific patient */
    List<Appointment> findByPatientId(Long patientId);

    /** Find all appointments for a specific doctor */
    List<Appointment> findByDoctorId(Long doctorId);

    /** Find all appointments with a specific status */
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);

    /** Find all appointments within a date range */
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);

    /** Check if a patient already has an appointment at a specific time */
    boolean existsByPatientIdAndAppointmentDateTime(Long patientId, LocalDateTime dateTime);

    // ==================== PAGINATED QUERIES ====================

    /** Find all appointments with pagination support */
    Page<Appointment> findAll(Pageable pageable);

    /** Find appointments by patient with pagination */
    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);

    /** Find appointments by doctor with pagination */
    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);

    /** Find appointments by status with pagination */
    Page<Appointment> findByStatus(Appointment.AppointmentStatus status, Pageable pageable);

    /** Find appointments within a date range with pagination */
    Page<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
