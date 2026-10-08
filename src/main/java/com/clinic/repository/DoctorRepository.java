package com.clinic.repository;

import com.clinic.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Interface for Doctor.
 * Includes pagination support for large datasets.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // ==================== BASIC QUERIES ====================

    /** Find a doctor by their unique email address */
    Optional<Doctor> findByEmail(String email);

    /** Find all doctors with a specific specialization (case-insensitive) */
    List<Doctor> findBySpecializationIgnoreCase(String specialization);

    /** Find all doctors with a specific last name (case-insensitive) */
    List<Doctor> findByLastNameIgnoreCase(String lastName);

    /** Check if a doctor with the given email already exists */
    boolean existsByEmail(String email);

    // ==================== PAGINATED QUERIES ====================

    /** Find all doctors with pagination support */
    Page<Doctor> findAll(Pageable pageable);

    /** Find doctors by specialization with pagination */
    Page<Doctor> findBySpecializationIgnoreCase(String specialization, Pageable pageable);

    /** Find doctors by last name with pagination */
    Page<Doctor> findByLastNameIgnoreCase(String lastName, Pageable pageable);
}
