package com.clinic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Patient Entity - Represents a patient in the clinic.
 *
 * This maps to the "patients" table in the database.
 * JPA (Java Persistence API) handles the ORM (Object-Relational Mapping).
 */
@Entity
@Table(name = "patients")
@Data                    // Lombok: generates getters, setters, toString, equals, hashCode
@NoArgsConstructor       // Lombok: generates no-args constructor
@AllArgsConstructor      // Lombok: generates all-args constructor
@Builder                 // Lombok: generates builder pattern
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;

    private String medicalHistory;

    // One-to-Many: One patient can have many appointments
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Appointment> appointments = new ArrayList<>();

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
