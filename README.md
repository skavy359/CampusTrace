# Campus Lost & Found Portal

A robust, microservices-based web application built with Spring Boot for a college project. The portal allows students to report, track, and claim lost and found items on campus, managed through an administrative panel.

## Architecture

This project strictly adheres to a Spring Cloud Microservices architecture using the following components:

*   **Eureka Server (`eureka-server`)**: Service Registry for service discovery.
*   **API Gateway (`api-gateway`)**: Spring Cloud Gateway that routes traffic (port 8080) to the underlying microservices. It also serves the frontend UI.
*   **User Service (`user-service`)**: Handles user registration, login, and JWT token generation using Spring Security and BCrypt.
*   **Lost Item Service (`lost-item-service`)**: Manages the CRUD operations for items reported as lost.
*   **Found Item Service (`found-item-service`)**: Manages the CRUD operations for items found around campus.
*   **Claim Service (`claim-service`)**: Handles the submission and administrative approval/rejection of claims for items.
*   **Notification Service (`notification-service`)**: A lightweight service that logs system notifications for users (e.g., claim status updates).

## Technologies Used

*   **Backend:** Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Hibernate, Spring Security, JWT, OpenFeign, Bean Validation, AspectJ (AOP).
*   **Database:** H2 Database (In-Memory for each service).
*   **Frontend:** HTML5, Vanilla JavaScript, CSS3 (Styled with a stark, monochrome "Nothing OS" aesthetic), Thymeleaf.
*   **Routing & Discovery:** Spring Cloud Gateway, Netflix Eureka.

## How to Run the Project

Since this project consists of 7 separate Spring Boot applications, you must start all of them for the application to function correctly. 

### 1. Start the Microservices
In separate terminal windows, navigate to each service directory and run:
```bash
./mvnw spring-boot:run
```
**Start order recommendation:**
1. `eureka-server` (Wait for it to boot)
2. `user-service`, `lost-item-service`, `found-item-service`, `claim-service`, `notification-service`
3. `api-gateway`

### 2. Access the Application
Once all services have successfully registered with Eureka, open your browser and navigate to the API Gateway:
`http://localhost:8080`

### 3. Seed the Database (Testing)
Because this project uses in-memory H2 databases, data is wiped on every restart. You can automatically seed the database with users, items, and claims by running the provided shell script in the root directory:
```bash
bash test_all_services.sh
```

## Creating an Admin Profile
To create an admin profile, simply register a new account on the portal with a username that contains the word **"admin"** (e.g., `admin`, `superadmin`). The system will automatically grant this account the `ADMIN` role, giving you access to the Admin Dashboard to approve or reject claims.

## API Documentation
Please refer to the `API_DOCS.md` file in this repository for a complete list of REST endpoints, required headers, and JSON request bodies.
