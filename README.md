# event-managment


### Project Overview: Advanced Event Management Microservice Platform

This project is a microservices-based web application designed to manage the lifecycle of events, from creation to ticket sales and user participation. Built with **Spring Boot**, it showcases an enterprise-level architecture focusing on modularity, scalability, and robust engineering practices.

**Core Idea:** We're breaking down a complex system into smaller, independent services that communicate with each other. This approach makes the system easier to develop, deploy, and scale.

**Key Components & Their Roles:**

1.  **Microservices (User, Event, Ticketing, Notification):** Each service owns a specific business capability and its own data. They expose **REST APIs** for synchronous interactions and use **Apache Kafka** for asynchronous, event-driven communication.
2.  **API Gateway (Spring Cloud Gateway):** The single entry point for all client requests. It handles request routing, load balancing, and crucially, **centralized JWT authentication** before forwarding requests to the appropriate downstream service.
3.  **Eureka Server:** A dedicated server that maintains a registry of all active microservice instances.
4.  **Eureka Client:** The mechanism by which microservices register themselves and discover other services dynamically.
5.  **Database (PostgreSQL):** Each microservice has its own dedicated PostgreSQL database, ensuring data independence and preventing tight coupling between services. We use **Spring Data JPA** for ORM.
6.  **Security (Spring Security & JWT):** We implement **role-based access control (RBAC)**. Users authenticate once to the User Service, receive a **JSON Web Token (JWT)**, which is then used to secure all subsequent API calls.
7.  **Asynchronous Messaging (Apache Kafka):** For non-real-time communication, like sending notifications after a ticket booking, ensuring services remain decoupled and resilient.
8.  **Third-Party Integration:** We simulate interactions with external systems like a **payment provider** (for ticket purchases) and an **email service** (for notifications).
9.  **Deployment (Docker & Kubernetes):** The entire platform is containerized using **Docker** for consistent environments, and we provide basic **Kubernetes manifests** for scalable orchestration.
10.  **Performance:** Incorporates **connection pooling (HikariCP)** and **caching (Caffeine/Redis)** to optimize database access and API response times.
11.  **Quality:** Emphasizes **clean code, API design, robust error handling,** and includes **unit and integration tests** for reliability.

In essence, we're building a modern, cloud-native application that is highly available, maintainable, and designed to handle real-world event management scenarios.

---

## User Service: What it Does

The **User Service** is the identity and access management hub of our platform. Its primary responsibilities are:

*   **User Registration:** Allows new users to create accounts.
*   **User Authentication:** Verifies user credentials (username and password) and, upon successful login, issues a **JSON Web Token (JWT)**.
*   **User Profiles:** Manages user details (like email and username) and their associated **roles** (e.g., `ROLE_USER`, `ROLE_ADMIN`, `ROLE_EVENT_MANAGER`).
*   **JWT Generation & Validation:** It's responsible for creating the JWTs that other services will use to authenticate requests, and it provides the necessary components for other services to validate these tokens.
*   **Role-Based Access Control (RBAC):** It defines and stores the roles that users possess, which are then used by Spring Security across all services to authorize access to specific functionalities.

## Event Service: What it Does

The **Event Service** is the core component for managing all event-related information within the platform. Its primary responsibilities are:

*   **Event Lifecycle Management:** Handles the creation, retrieval, updating, and deletion of event details (e.g., title, description, dates, location, ticket price).
*   **Ticket Inventory Tracking:** Stores and manages the total number of tickets available for each event, as well as the current count of available tickets. It provides endpoints for other services (like the Ticketing Service) to safely decrement or increment these counts.
*   **Event Search & Discovery:** Allows users and other services to search and browse events based on various criteria.
*   **Event Creator Ownership:** Tracks which user created an event, enabling specific authorization rules (e.g., only the creator or an administrator can modify an event).


## API Gateway: What it Does

The **API Gateway** acts as the single, intelligent entry point for all client applications interacting with our microservices platform. Its primary responsibilities are:

*   **Request Routing:** Directs incoming HTTP requests to the correct downstream microservice based on predefined paths (e.g., /api/v1/users/** goes to the User Service, /api/v1/events/** goes to the Event Service).
*   **Centralized Authentication:** It intercepts all incoming requests, validates the JWT provided by the client, ensuring the user is authenticated before the request reaches any microservice.
*   **Authorization Context Propagation:** After validating a JWT, it extracts crucial user information (like userId and roles) and injects them as custom headers into the request. These headers are then forwarded to the downstream microservices, allowing them to perform their own authorization checks without needing to re-validate the JWT.
*   **Load Balancing:** Distributes incoming requests across multiple instances of a microservice, ensuring high availability and efficient resource utilization.
*   **Fallback Mechanism:** Provides graceful degradation by returning a default response if a downstream microservice is unavailable, preventing client applications from crashing.

## Eureka Server: What it Does
The Eureka Server functions as a centralized registry for all microservices in the platform. Its primary responsibilities are:

*   **Service Registration:** It receives registration requests from all microservices, building a comprehensive directory of available service instances and their network locations.
*   **Health Monitoring:** It continuously monitors the health and availability of registered services, quickly identifying and removing unresponsive instances from its registry.

## Eureka Client: What it Does
The Eureka Client is a library embedded within each microservice and the API Gateway. Its primary responsibilities are:

*   **Self-Registration:** Automatically registers the microservice instance with the Eureka Server upon startup, providing its network address and metadata.
*   **Service Discovery:** Queries the Eureka Server to locate other microservice instances by their logical name, enabling dynamic and resilient inter-service communication.
