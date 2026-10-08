package com.clinic.repository;

import com.clinic.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Interface for Patient.
 *
 * Extends JpaRepository which provides:
 * - Basic CRUD: save, findById, findAll, deleteById, count, existsById
 * - Pagination: findAll(Pageable) returns Page<T>
 * - Sorting: findAll(Sort) returns sorted List<T>
 *
 * Spring Data JPA automatically implements methods based on naming convention.
 * For example: findByLastNameIgnoreCase(String) generates:
 *   SELECT * FROM patients WHERE UPPER(last_name) = UPPER(?)
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // ==================== BASIC QUERIES ====================

    /** Find a patient by their unique email address */
    Optional<Patient> findByEmail(String email);

    /** Find all patients with a specific last name (case-insensitive) */
    List<Patient> findByLastNameIgnoreCase(String lastName);

    /** Find all patients whose first name contains the given string */
    List<Patient> findByFirstNameContainingIgnoreCase(String firstName);

    /** Check if a patient with the given email already exists */
    boolean existsByEmail(String email);

    // ==================== PAGINATED QUERIES ====================
    // Pageable allows the client to request specific pages:
    // GET /api/patients?page=0&size=10&sort=lastName,asc

    /** Find all patients with pagination support */
    Page<Patient> findAll(Pageable pageable);

    /** Find patients by last name with pagination */
    Page<Patient> findByLastNameIgnoreCase(String lastName, Pageable pageable);

    /** Find patients by first name with pagination */
    Page<Patient> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    // ==================== CUSTOM JPQL QUERIES ====================

    /**
     * Custom search query - searches both first and last name.
     * JPQL (Java Persistence Query Language) is similar to SQL but uses entity names.
     */
    @Query("SELECT p FROM Patient p WHERE p.lastName LIKE %:keyword% OR p.firstName LIKE %:keyword%")
    List<Patient> searchByName(String keyword);

    /** Paginated version of the search query */
    @Query("SELECT p FROM Patient p WHERE p.lastName LIKE %:keyword% OR p.firstName LIKE %:keyword%")
    Page<Patient> searchByName(String keyword, Pageable pageable);
}
