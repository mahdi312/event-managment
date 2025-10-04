# Event Management Platform - API Collection

## Base URLs
- **User Service**: `http://localhost:8081`
- **Event Service**: `http://localhost:8082`
- **Ticketing Service**: `http://localhost:8083`

## Authentication
First, get a JWT token by logging in:
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "mehdi-jv",
    "password": "admin1"
  }'
```

Use the returned token in the `Authorization: Bearer <token>` header for protected endpoints.

---

## User Service APIs

### 1. User Registration
**POST** `/api/v1/auth/register`
- **Auth Required**: None
- **Description**: Register a new user account

```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "roles": ["USER"]
  }'
```

### 2. User Login
**POST** `/api/v1/auth/login`
- **Auth Required**: None
- **Description**: Authenticate user and get JWT token

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "mehdi-jv",
    "password": "admin1"
  }'
```

### 3. Get All Users
**GET** `/api/v1/users`
- **Auth Required**: ADMIN or EVENT_MANAGER
- **Description**: Get all users (admin only)

```bash
curl -X GET http://localhost:8081/api/v1/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Get User by ID
**GET** `/api/v1/users/{id}`
- **Auth Required**: ADMIN, EVENT_MANAGER, or own profile
- **Description**: Get user details by ID

```bash
curl -X GET http://localhost:8081/api/v1/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Get User by Username
**GET** `/api/v1/users/username/{username}`
- **Auth Required**: None
- **Description**: Get user details by username

```bash
curl -X GET http://localhost:8081/api/v1/users/username/mehdi-jv
```

### 6. Update User
**PUT** `/api/v1/users/{id}`
- **Auth Required**: ADMIN or EVENT_MANAGER
- **Description**: Update user information (admin only)

```bash
curl -X PUT http://localhost:8081/api/v1/users/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "username": "updated-username",
    "email": "updated@example.com",
    "roles": ["ADMIN", "USER"]
  }'
```

### 7. Delete User
**DELETE** `/api/v1/users/{id}`
- **Auth Required**: ADMIN or EVENT_MANAGER
- **Description**: Delete a user (admin only)

```bash
curl -X DELETE http://localhost:8081/api/v1/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Event Service APIs

### 1. Create Event
**POST** `/api/v1/events`
- **Auth Required**: ADMIN or EVENT_MANAGER
- **Description**: Creates a new event

```bash
curl -X POST http://localhost:8082/api/v1/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Spring Boot Workshop",
    "description": "Learn Spring Boot from scratch",
    "startTime": "2024-12-15T10:00:00",
    "endTime": "2024-12-15T17:00:00",
    "location": "Tech Conference Center",
    "totalTickets": 100,
    "ticketPrice": 150.00
  }'
```

### 2. Get All Events
**GET** `/api/v1/events`
- **Auth Required**: None
- **Description**: Retrieves all events

```bash
curl -X GET http://localhost:8082/api/v1/events
```

### 3. Get Event by ID
**GET** `/api/v1/events/{id}`
- **Auth Required**: None
- **Description**: Retrieves event by ID

```bash
curl -X GET http://localhost:8082/api/v1/events/1
```

### 4. Update Event
**PUT** `/api/v1/events/{id}`
- **Auth Required**: ADMIN or EVENT_MANAGER
- **Description**: Updates an existing event

```bash
curl -X PUT http://localhost:8082/api/v1/events/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Updated Spring Boot Workshop",
    "description": "Updated description",
    "startTime": "2024-12-15T10:00:00",
    "endTime": "2024-12-15T18:00:00",
    "location": "Updated Location",
    "totalTickets": 120,
    "ticketPrice": 175.00
  }'
```

### 5. Delete Event
**DELETE** `/api/v1/events/{id}`
- **Auth Required**: ADMIN or EVENT_MANAGER
- **Description**: Deletes an event

```bash
curl -X DELETE http://localhost:8082/api/v1/events/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 6. Decrement Event Tickets
**PUT** `/api/v1/events/{id}/decrement-tickets`
- **Auth Required**: ADMIN or EVENT_MANAGER
- **Description**: Decrements available tickets

```bash
curl -X PUT http://localhost:8082/api/v1/events/1/decrement-tickets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "numberOfTickets": 5
  }'
```

### 7. Increment Event Tickets
**PUT** `/api/v1/events/{id}/increment-tickets`
- **Auth Required**: ADMIN or EVENT_MANAGER
- **Description**: Increments available tickets

```bash
curl -X PUT http://localhost:8082/api/v1/events/1/increment-tickets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "numberOfTickets": 3
  }'
```

---

## Ticketing Service APIs

### 1. Book Tickets
**POST** `/api/v1/bookings`
- **Auth Required**: USER
- **Description**: Books tickets for an event

```bash
curl -X POST http://localhost:8083/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "eventId": 1,
    "numberOfTickets": 2
  }'
```

### 2. Cancel Booking
**PUT** `/api/v1/bookings/{id}/cancel`
- **Auth Required**: USER (own booking) or ADMIN
- **Description**: Cancels a booking and refunds tickets

```bash
curl -X PUT http://localhost:8083/api/v1/bookings/1/cancel \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Get My Bookings
**GET** `/api/v1/bookings/my-bookings`
- **Auth Required**: USER
- **Description**: Gets all bookings for the authenticated user

```bash
curl -X GET http://localhost:8083/api/v1/bookings/my-bookings \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Get Booking by ID
**GET** `/api/v1/bookings/{id}`
- **Auth Required**: USER (own booking) or ADMIN
- **Description**: Gets booking details by ID

```bash
curl -X GET http://localhost:8083/api/v1/bookings/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```


---

## API Summary

### User Service (7 endpoints)
- **POST** `/api/v1/auth/register` - Register new user
- **POST** `/api/v1/auth/login` - User login
- **GET** `/api/v1/users` - Get all users (ADMIN/EVENT_MANAGER)
- **GET** `/api/v1/users/{id}` - Get user by ID
- **GET** `/api/v1/users/username/{username}` - Get user by username
- **PUT** `/api/v1/users/{id}` - Update user (ADMIN/EVENT_MANAGER)
- **DELETE** `/api/v1/users/{id}` - Delete user (ADMIN/EVENT_MANAGER)

### Event Service (7 endpoints)
- **POST** `/api/v1/events` - Create event (ADMIN/EVENT_MANAGER)
- **GET** `/api/v1/events` - Get all events (public)
- **GET** `/api/v1/events/{id}` - Get event by ID (public)
- **PUT** `/api/v1/events/{id}` - Update event (ADMIN/EVENT_MANAGER)
- **DELETE** `/api/v1/events/{id}` - Delete event (ADMIN/EVENT_MANAGER)
- **PUT** `/api/v1/events/{id}/decrement-tickets` - Decrement tickets (ADMIN/EVENT_MANAGER)
- **PUT** `/api/v1/events/{id}/increment-tickets` - Increment tickets (ADMIN/EVENT_MANAGER)

### Ticketing Service (4 endpoints)
- **POST** `/api/v1/bookings` - Book tickets (USER)
- **PUT** `/api/v1/bookings/{id}/cancel` - Cancel booking (USER/ADMIN)
- **GET** `/api/v1/bookings/my-bookings` - Get user's bookings (USER)
- **GET** `/api/v1/bookings/{id}` - Get booking by ID (USER/ADMIN)

## Test Users

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| mehdi-jv | admin1 | ADMIN | Full access to all operations |
| rasoul-nb | event-mng2 | EVENT_MANAGER | Can manage events |
| mahdi-mst | user123 | USER | Can book tickets |

---

## Common Response Formats

### Success Response
```json
{
  "id": 1,
  "title": "Event Title",
  "description": "Event Description",
  "startTime": "2024-12-15T10:00:00",
  "endTime": "2024-12-15T17:00:00",
  "location": "Event Location",
  "totalTickets": 100,
  "availableTickets": 95,
  "ticketPrice": 150.00,
  "createdByUserId": 1,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### Error Response
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2024-01-01T00:00:00",
  "errors": {
    "title": "Title cannot be empty"
  }
}
```

---

## Notes

1. **JWT Token**: Replace `YOUR_JWT_TOKEN` with the actual token from login response
2. **Ports**: Ensure services are running on correct ports (8081, 8082, 8083)
3. **DateTime Format**: Use ISO 8601 format for dates (`YYYY-MM-DDTHH:mm:ss`)
4. **Validation**: All request bodies are validated - check error responses for validation details
5. **Authorization**: Some endpoints require specific roles - use appropriate test users
