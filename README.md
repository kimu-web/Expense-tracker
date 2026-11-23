# Personal Expense Tracker REST API

A secure, multi-user RESTful backend built with **Java 27**, **Spring Boot**, **Spring Data JPA**, **MySQL**, and **Spring Security**. Authenticated users can register accounts, manage their personal expenses, filter transactions by category, and view aggregated spending analytics.

## Tech Stack
* **Language & Framework:** Java 27, Spring Boot 4.2, Maven
* **Web & Validation:** Spring Web (REST), Jakarta Bean Validation (`@Valid`)
* **Database & ORM:** MySQL 8, Spring Data JPA, Hibernate, HikariCP
* **Security:** Spring Security (HTTP Basic Authentication, BCrypt password hashing)

## Architecture
The application follows a clean, layered architecture:
* `controller/` — REST endpoints (`AuthController`, `ExpenseController`) handling HTTP requests, validation triggers, and user principals.
* `dto/` — Java `record` payloads (`RegisterRequest`, `ExpenseRequest`, `CategorySummary`) decoupling API contracts from database entities.
* `service/` — Business logic (`ExpenseService`, `CustomUserDetailsService`) enforcing user-scoped data ownership.
* `repository/` — Spring Data JPA interfaces (`UserRepository`, `ExpenseRepository`) with derived queries and custom `@Query` JPQL aggregations.
* `entity/` — JPA entities (`User`, `Expense`) mapped with a `@OneToMany` / `@ManyToOne` relational schema.
* `exception/` — Global exception handling (`@RestControllerAdvice`) returning structured JSON error responses (`400 Bad Request`, `404 Not Found`).
* `config/` — Spring Security filter chain and `BCryptPasswordEncoder` configuration.

## Getting Started

### Prerequisites
* Java 17 or newer (built with Java 27)
* MySQL 8+
* Maven

### Database Setup
1. Create the MySQL database:
   ```sql
   CREATE DATABASE expense_tracker_db;
   ```
2. Set your MySQL password via the `DB_PASSWORD` environment variable (or update `src/main/resources/application.properties`):
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/expense_tracker_db
   spring.datasource.username=root
   spring.datasource.password=\${DB_PASSWORD:your_mysql_password}
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Endpoints

### Authentication (Public)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user (`username`, `password`) |

### Expenses & Analytics (Requires HTTP Basic Auth)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/expenses` | Get all expenses for the logged-in user (optional `?category=` filter) |
| `GET` | `/api/expenses/{id}` | Get a single expense by ID (owned by current user) |
| `POST` | `/api/expenses` | Create a new validated expense |
| `PUT` | `/api/expenses/{id}` | Update an existing expense |
| `DELETE` | `/api/expenses/{id}` | Delete an expense |
| `GET` | `/api/expenses/analytics/by-category` | Get total spending grouped by category (JPQL `GROUP BY`) |
| `GET` | `/api/expenses/analytics/total?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` | Get total spending within a date range |

## Example Usage (PowerShell)
```powershell
# Register a user
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -ContentType "application/json" -Body '{"username":"alice","password":"password123"}'

# Create an expense
\$headers = @{ Authorization = "Basic " + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("alice:password123")) }
Invoke-RestMethod -Uri "http://localhost:8080/api/expenses" -Method Post -ContentType "application/json" -Headers \$headers -Body '{"description":"Groceries","amount":2000.00,"category":"Food","date":"2026-11-10"}'
```
