package com.clinic.config;

import com.clinic.entity.User;
import com.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Creates a default admin user on startup.
 * Username: admin
 * Password: admin123
 */
@Component
@Order(1)  // Run before DataInitializer
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@clinic.com")
                    .roles(Set.of(User.Role.ROLE_ADMIN))
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            System.out.println("Default admin user created (admin / admin123)");
        }

        if (!userRepository.existsByUsername("doctor")) {
            User doctor = User.builder()
                    .username("doctor")
                    .password(passwordEncoder.encode("doctor123"))
                    .email("doctor@clinic.com")
                    .roles(Set.of(User.Role.ROLE_DOCTOR))
                    .enabled(true)
                    .build();
            userRepository.save(doctor);
            System.out.println("Default doctor user created (doctor / doctor123)");
        }
    }
}
