# Clinic App - How to Run

## Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java | 17+ | https://adoptium.net/ |
| Maven | 3.6+ | https://maven.apache.org/download.cgi |
| PostgreSQL | 16+ | https://www.postgresql.org/download/ |

---

## Quick Start (3 Steps)

### 1. Create the Database

Open a terminal and run:

```bash
psql -U postgres -c "CREATE DATABASE clinicdb;"
```

Or use pgAdmin 4 → Right-click "Databases" → Create → Database → name: `clinicdb`

### 2. Update Password (if needed)

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.password=your_postgres_password
```

### 3. Run the App

```bash
cd C:\Users\DAN\Desktop\ClinicApp
mvn spring-boot:run
```

---

## Access Points

| Resource | URL |
|----------|-----|
| API Base | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

---

## Default Login Credentials

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | ADMIN (full access) |
| `doctor` | `doctor123` | DOCTOR (manage patients & appointments) |

---

## Testing the API

### 1. Login to get JWT token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "admin",
  "role": "ROLE_ADMIN"
}
```

### 2. Use the token

```bash
# Get all patients (paginated)
curl http://localhost:8080/api/patients?page=0&size=10 \
  -H "Authorization: Bearer YOUR_TOKEN"

# Create a patient
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "dateOfBirth": "1995-03-20",
    "email": "jane@example.com",
    "phone": "+1234567890",
    "gender": "FEMALE"
  }'
```

---

## Using Swagger UI (Easiest Way)

1. Open http://localhost:8080/swagger-ui.html
2. Click **Authorize** button
3. Enter: `Bearer YOUR_TOKEN` (paste token from login)
4. Now you can test all endpoints directly from the browser!

---

## Running Tests

```bash
mvn test
```

---

## Database Configuration

### Default (PostgreSQL)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clinicdb
spring.datasource.username=postgres
spring.datasource.password=root
```

### Switch to H2 (In-Memory)
Replace the datasource config with:
```properties
spring.datasource.url=jdbc:h2:mem:clinicdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

### Switch to MySQL
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/clinicdb
spring.datasource.username=root
spring.datasource.password=your_password
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `database "clinicdb" does not exist` | Run: `psql -U postgres -c "CREATE DATABASE clinicdb;"` |
| `Connection refused` | Make sure PostgreSQL is running |
| `Port 8080 already in use` | Change port: `server.port=8081` |
| `Lombok errors` | Install Lombok plugin in VS Code/IntelliJ |
| `mvn not recognized` | Add Maven bin folder to PATH |

---

## Project Structure

```
ClinicApp/
├── src/main/java/com/clinic/
│   ├── controller/    # REST API endpoints
│   ├── service/       # Business logic
│   ├── repository/    # Database access
│   ├── entity/        # Database models
│   ├── dto/           # Data transfer objects
│   ├── security/      # JWT auth & authorization
│   └── config/        # App configuration
├── src/main/resources/
│   └── application.properties
└── src/test/java/     # Unit & integration tests
```
