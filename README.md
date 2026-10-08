# Clinic App - Spring Boot CRUD API

A complete Clinic Management REST API built with Spring Boot. This project teaches you all the core concepts of Spring Boot development including authentication, authorization, pagination, API documentation, and testing.

## Project Structure

```
ClinicApp/
├── pom.xml                                    # Maven dependencies
├── src/main/resources/
│   ├── application.properties                 # Default config (H2 database)
│   ├── application-mysql.properties           # MySQL config
│   ├── application-postgres.properties        # PostgreSQL config
│   └── application-test.properties            # Test config
└── src/main/java/com/clinic/
    ├── ClinicAppApplication.java              # Main entry point
    ├── config/
    │   ├── DataInitializer.java               # Sample data loader
    │   ├── AdminInitializer.java              # Default admin/doctor users
    │   ├── OpenApiConfig.java                 # Swagger UI configuration
    │   └── LoggingConfig.java                  # Logging setup
    ├── controller/                            # API Layer (REST endpoints)
    │   ├── PatientController.java
    │   ├── DoctorController.java
    │   ├── AppointmentController.java
    │   └── AuthController.java
    ├── service/                               # Business Logic Layer
    │   ├── PatientService.java
    │   ├── DoctorService.java
    │   ├── AppointmentService.java
    │   └── AuthService.java
    ├── repository/                            # Data Access Layer
    │   ├── PatientRepository.java
    │   ├── DoctorRepository.java
    │   ├── AppointmentRepository.java
    │   └── UserRepository.java
    ├── entity/                                # Database Models
    │   ├── Patient.java
    │   ├── Doctor.java
    │   ├── Appointment.java
    │   └── User.java
    ├── dto/                                   # Data Transfer Objects
    │   ├── PatientDTO.java
    │   ├── DoctorDTO.java
    │   ├── AppointmentDTO.java
    │   ├── AuthRequest.java
    │   ├── AuthResponse.java
    │   └── RegisterRequest.java
    ├── security/                              # Security Layer
    │   ├── SecurityConfig.java
    │   ├── JwtUtil.java
    │   └── JwtAuthenticationFilter.java
    └── exception/
        └── GlobalExceptionHandler.java
└── src/test/java/com/clinic/
    ├── ClinicAppApplicationTests.java
    ├── controller/
    │   ├── PatientControllerTest.java
    │   ├── DoctorControllerTest.java
    │   ├── AppointmentControllerTest.java
    │   └── AuthControllerTest.java
    ├── service/
    │   └── PatientServiceTest.java
    └── security/
        └── JwtUtilTest.java
```

## How to Run

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Quick Start (H2 In-Memory Database)
```bash
mvn spring-boot:run
```

### Run with MySQL
```bash
# 1. Create the database in MySQL:
#    CREATE DATABASE clinicdb;

# 2. Update src/main/resources/application-mysql.properties with your credentials

# 3. Run with the mysql profile:
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

### Run with PostgreSQL
```bash
# 1. Create the database:
#    CREATE DATABASE clinicdb;

# 2. Update src/main/resources/application-postgres.properties

# 3. Run with the postgres profile:
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Run Tests
```bash
mvn test
```

## Access Points

| Resource | URL |
|----------|-----|
| API Base | `http://localhost:8080/api` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| H2 Console | `http://localhost:8080/h2-console` |

## Default Users

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | ADMIN |
| `doctor` | `doctor123` | DOCTOR |

## API Endpoints

### Authentication
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login and get JWT | Public |

### Patients
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/patients` | Create patient | Admin, Doctor |
| GET | `/api/patients?page=0&size=10` | List patients (paginated) | Admin, Doctor |
| GET | `/api/patients/{id}` | Get patient by ID | Admin, Doctor |
| PUT | `/api/patients/{id}` | Update patient | Admin, Doctor |
| DELETE | `/api/patients/{id}` | Delete patient | Admin |
| GET | `/api/patients/search?keyword=john` | Search patients | Admin, Doctor |

### Doctors
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/doctors` | Create doctor | Admin |
| GET | `/api/doctors?page=0&size=10` | List doctors (paginated) | Any role |
| GET | `/api/doctors/{id}` | Get doctor by ID | Any role |
| PUT | `/api/doctors/{id}` | Update doctor | Admin |
| DELETE | `/api/doctors/{id}` | Delete doctor | Admin |
| GET | `/api/doctors/specialization/{spec}` | Filter by specialization | Any role |

### Appointments
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/appointments` | Create appointment | Admin, Doctor |
| GET | `/api/appointments?page=0&size=10` | List appointments (paginated) | Any role |
| GET | `/api/appointments/{id}` | Get appointment by ID | Any role |
| PUT | `/api/appointments/{id}` | Update appointment | Admin, Doctor |
| DELETE | `/api/appointments/{id}` | Delete appointment | Admin |
| GET | `/api/appointments/patient/{id}` | Get by patient | Any role |
| GET | `/api/appointments/doctor/{id}` | Get by doctor | Any role |
| PATCH | `/api/appointments/{id}/cancel` | Cancel appointment | Admin, Doctor |
| PATCH | `/api/appointments/{id}/complete` | Complete appointment | Admin, Doctor |

## Key Concepts

### 1. Authentication (JWT)
- User logs in with username/password
- Server returns a JWT token
- Client sends token in `Authorization: Bearer <token>` header
- JwtAuthenticationFilter validates token on every request

### 2. Authorization (Role-Based)
- **ADMIN**: Full access to all endpoints
- **DOCTOR**: Can manage patients and appointments
- **PATIENT**: Read-only access to doctors and appointments

### 3. Pagination
All list endpoints support pagination:
```
GET /api/patients?page=0&size=10&sort=lastName,asc
```
Returns a `Page<T>` object with content, total pages, total elements, etc.

### 4. Swagger UI
Auto-generated API documentation at `/swagger-ui.html`. Includes an "Authorize" button for testing authenticated endpoints.

### 5. Logging
SLF4J with Logback. Different log levels per package:
- `com.clinic` = DEBUG (app internals)
- `org.springframework.web` = INFO
- `org.hibernate.SQL` = DEBUG (SQL queries)

### 6. Database Profiles
- **Default (H2)**: In-memory, no setup needed
- **MySQL**: Production-ready, activate with `--spring.profiles.active=mysql`
- **PostgreSQL**: Production-ready, activate with `--spring.profiles.active=postgres`

## Testing with cURL

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Create Patient (with JWT)
```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "dateOfBirth": "1995-03-20",
    "email": "jane@example.com",
    "phone": "+1234567890",
    "gender": "FEMALE"
  }'
```

### Get Paginated Patients
```bash
curl "http://localhost:8080/api/patients?page=0&size=5&sort=lastName,asc" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```
