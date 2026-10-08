package com.clinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Clinic App.
 *
 * @SpringBootApplication is a convenience annotation that combines:
 * - @Configuration: Marks this class as a source of bean definitions
 * - @EnableAutoConfiguration: Tells Spring Boot to auto-configure based on dependencies
 * - @ComponentScan: Scans for components in the com.clinic package
 */
@SpringBootApplication
public class ClinicAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicAppApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Clinic App is running!");
        System.out.println("  API: http://localhost:8080/api");
        System.out.println("  H2 Console: http://localhost:8080/h2-console");
        System.out.println("========================================\n");
    }
}
