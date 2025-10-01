# event-managment


### Project Overview: Advanced Event Management Microservice Platform

This project is a microservices-based web application designed to manage the lifecycle of events, from creation to ticket sales and user participation. Built with **Spring Boot**, it showcases an enterprise-level architecture focusing on modularity, scalability, and robust engineering practices.

**Core Idea:** We're breaking down a complex system into smaller, independent services that communicate with each other. This approach makes the system easier to develop, deploy, and scale.

**Key Components & Their Roles:**

1.  **Microservices (User, Event, Ticketing, Notification):** Each service owns a specific business capability and its own data. They expose **REST APIs** for synchronous interactions and use **Apache Kafka** for asynchronous, event-driven communication.
2.  **API Gateway (Spring Cloud Gateway):** The single entry point for all client requests. It handles request routing, load balancing, and crucially, **centralized JWT authentication** before forwarding requests to the appropriate downstream service.
3.  **Database (PostgreSQL):** Each microservice has its own dedicated PostgreSQL database, ensuring data independence and preventing tight coupling between services. We use **Spring Data JPA** for ORM.
4.  **Security (Spring Security & JWT):** We implement **role-based access control (RBAC)**. Users authenticate once to the User Service, receive a **JSON Web Token (JWT)**, which is then used to secure all subsequent API calls.
5.  **Asynchronous Messaging (Apache Kafka):** For non-real-time communication, like sending notifications after a ticket booking, ensuring services remain decoupled and resilient.
6.  **Third-Party Integration:** We simulate interactions with external systems like a **payment provider** (for ticket purchases) and an **email service** (for notifications).
7.  **Deployment (Docker & Kubernetes):** The entire platform is containerized using **Docker** for consistent environments, and we provide basic **Kubernetes manifests** for scalable orchestration.
8.  **Performance:** Incorporates **connection pooling (HikariCP)** and **caching (Caffeine/Redis)** to optimize database access and API response times.
9.  **Quality:** Emphasizes **clean code, API design, robust error handling,** and includes **unit and integration tests** for reliability.

In essence, we're building a modern, cloud-native application that is highly available, maintainable, and designed to handle real-world event management scenarios.

---

### User Service: What it Does

The **User Service** is the identity and access management hub of our platform. Its primary responsibilities are:

*   **User Registration:** Allows new users to create accounts.
*   **User Authentication:** Verifies user credentials (username and password) and, upon successful login, issues a **JSON Web Token (JWT)**.
*   **User Profiles:** Manages user details (like email and username) and their associated **roles** (e.g., `ROLE_USER`, `ROLE_ADMIN`, `ROLE_EVENT_MANAGER`).
*   **JWT Generation & Validation:** It's responsible for creating the JWTs that other services will use to authenticate requests, and it provides the necessary components for other services to validate these tokens.
*   **Role-Based Access Control (RBAC):** It defines and stores the roles that users possess, which are then used by Spring Security across all services to authorize access to specific functionalities.
