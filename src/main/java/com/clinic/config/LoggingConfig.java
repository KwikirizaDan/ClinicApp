package com.clinic.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Logging Configuration.
 *
 * SLF4J (Simple Logging Facade for Java) is the logging API.
 * Spring Boot uses Logback as the default implementation.
 *
 * Log Levels (from most verbose to least):
 * TRACE - Most detailed (method entry/exit, variable values)
 * DEBUG - Detailed info for debugging (SQL queries, business logic)
 * INFO  - General information (app started, requests processed)
 * WARN  - Potential issues (deprecated API usage, retries)
 * ERROR - Errors that need attention (exceptions, failures)
 *
 * You can set different levels for different packages:
 * - com.clinic = DEBUG (see your app's internal workings)
 * - org.springframework.web = INFO (less noise from framework)
 * - org.hibernate.SQL = DEBUG (see generated SQL queries)
 */
@Configuration
public class LoggingConfig {

    /**
     * Create a logger for this config class.
     * Use LoggerFactory.getLogger() to create loggers in any class.
     */
    private static final Logger logger = LoggerFactory.getLogger(LoggingConfig.class);

    /**
     * Log a message when the application context loads.
     * This confirms the logging configuration is working.
     */
    @Bean
    public String initLogging() {
        logger.info("========================================");
        logger.info("  Logging initialized successfully");
        logger.info("  Log levels configured in application.properties");
        logger.info("========================================");
        return "loggingInitialized";
    }
}
