package com.clinic.service;

import com.clinic.dto.DoctorDTO;
import com.clinic.entity.Doctor;
import com.clinic.repository.DoctorRepository;
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
 * Service Layer for Doctor - Contains all business logic.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;

    // ==================== CREATE ====================

    @Transactional
    public DoctorDTO createDoctor(DoctorDTO dto) {
        logger.debug("Creating new doctor: {} {}", dto.getFirstName(), dto.getLastName());

        if (doctorRepository.existsByEmail(dto.getEmail())) {
            logger.warn("Duplicate email attempted: {}", dto.getEmail());
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }

        Doctor doctor = mapToEntity(dto);
        Doctor saved = doctorRepository.save(doctor);
        logger.info("Doctor created successfully with ID: {}", saved.getId());
        return mapToDTO(saved);
    }

    // ==================== READ (with Pagination) ====================

    @Transactional(readOnly = true)
    public Page<DoctorDTO> getAllDoctors(Pageable pageable) {
        logger.debug("Fetching doctors - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return doctorRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public DoctorDTO getDoctorById(Long id) {
        logger.debug("Fetching doctor with ID: {}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Doctor not found with ID: {}", id);
                    return new RuntimeException("Doctor not found with id: " + id);
                });
        return mapToDTO(doctor);
    }

    // ==================== UPDATE ====================

    @Transactional
    public DoctorDTO updateDoctor(Long id, DoctorDTO dto) {
        logger.debug("Updating doctor with ID: {}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Update failed - doctor not found with ID: {}", id);
                    return new RuntimeException("Doctor not found with id: " + id);
                });

        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setYearsOfExperience(dto.getYearsOfExperience());

        return mapToDTO(doctorRepository.save(doctor));
    }

    // ==================== DELETE ====================

    @Transactional
    public void deleteDoctor(Long id) {
        logger.debug("Deleting doctor with ID: {}", id);
        if (!doctorRepository.existsById(id)) {
            logger.warn("Delete failed - doctor not found with ID: {}", id);
            throw new RuntimeException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
        logger.info("Doctor deleted successfully with ID: {}", id);
    }

    // ==================== FILTER ====================

    @Transactional(readOnly = true)
    public Page<DoctorDTO> getDoctorsBySpecialization(String specialization, Pageable pageable) {
        logger.debug("Fetching doctors by specialization: '{}'", specialization);
        return doctorRepository.findBySpecializationIgnoreCase(specialization, pageable)
                .map(this::mapToDTO);
    }

    // ==================== MAPPING ====================

    private DoctorDTO mapToDTO(Doctor doctor) {
        return DoctorDTO.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .specialization(doctor.getSpecialization())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .yearsOfExperience(doctor.getYearsOfExperience())
                .build();
    }

    private Doctor mapToEntity(DoctorDTO dto) {
        return Doctor.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .specialization(dto.getSpecialization())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .yearsOfExperience(dto.getYearsOfExperience())
                .build();
    }
}
