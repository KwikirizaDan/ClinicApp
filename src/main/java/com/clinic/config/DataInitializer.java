package com.clinic.config;

import com.clinic.entity.*;
import com.clinic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Initializes sample data when the app starts.
 * Great for learning and testing!
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public void run(String... args) {
        // Create sample doctors
        Doctor doc1 = Doctor.builder()
                .firstName("John")
                .lastName("Smith")
                .specialization("Cardiology")
                .email("john.smith@clinic.com")
                .phone("+1234567890")
                .yearsOfExperience(15)
                .build();

        Doctor doc2 = Doctor.builder()
                .firstName("Sarah")
                .lastName("Johnson")
                .specialization("Dermatology")
                .email("sarah.johnson@clinic.com")
                .phone("+1987654321")
                .yearsOfExperience(10)
                .build();

        doctorRepository.save(doc1);
        doctorRepository.save(doc2);

        // Create sample patients
        Patient pat1 = Patient.builder()
                .firstName("Alice")
                .lastName("Williams")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .email("alice.williams@email.com")
                .phone("+1122334455")
                .gender(Patient.Gender.FEMALE)
                .address("123 Main St, City")
                .medicalHistory("No known allergies")
                .build();

        Patient pat2 = Patient.builder()
                .firstName("Bob")
                .lastName("Brown")
                .dateOfBirth(LocalDate.of(1985, 8, 22))
                .email("bob.brown@email.com")
                .phone("+1555666777")
                .gender(Patient.Gender.MALE)
                .address("456 Oak Ave, Town")
                .medicalHistory("Penicillin allergy")
                .build();

        patientRepository.save(pat1);
        patientRepository.save(pat2);

        // Create sample appointment
        Appointment app1 = Appointment.builder()
                .appointmentDateTime(LocalDateTime.now().plusDays(1))
                .reason("Annual heart checkup")
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .notes("Patient reported occasional chest pain")
                .patient(pat1)
                .doctor(doc1)
                .build();

        appointmentRepository.save(app1);

        System.out.println("\n========================================");
        System.out.println("  Sample data loaded successfully!");
        System.out.println("  2 Doctors, 2 Patients, 1 Appointment");
        System.out.println("========================================\n");
    }
}
