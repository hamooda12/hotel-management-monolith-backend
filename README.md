أكيد، هذا README معاد كتابته بشكل احترافي أكثر، بنفس معلومات مشروعك بدون تغيير الفلو، ومبني على التفاصيل الموجودة في الملف الذي أرسلته. 

انسخه مباشرة في `README.md`:

````md
# 🏨 Tourism Hotel Booking System API

A **Spring Boot REST API** for managing a tourism hotel booking platform.

The system supports hotel catalog management, room types, availability checks, bookings, mock payments, notifications, media uploads, authentication, authorization, pagination, filtering, validation, centralized error handling, and OpenAPI documentation.

---

## 📌 Overview

This project is a backend API for a **Tourism Hotel Booking System**.

Guests can browse hotels and room types, check room availability, create bookings, simulate payments, cancel bookings, and read their notifications.

Managers and admins can manage hotels, room types, bookings, and catalog images.

The application is built as a **modular Spring Boot monolith**, organized into separate packages for:

- Hotel catalog
- Room types
- Availability
- Bookings
- Mock payments
- Notifications
- Media storage
- Security

---

## ✨ Features

- 🔐 JWT authentication with access and refresh tokens
- 👥 Role-based access control: `ADMIN`, `MANAGER`, `GUEST`
- 🏨 Hotel CRUD operations
- 🖼️ Hotel image upload
- 🛏️ Room type CRUD operations
- 🖼️ Room type image upload
- 🔎 Hotel and room type filtering
- 📄 Pagination and sorting
- 📅 Availability checking by hotel, room type, date range, and guest count
- 🧾 Booking lifecycle: `PENDING`, `CONFIRMED`, `CANCELLED`
- 💳 Mock payment lifecycle: `INITIATED`, `SUCCESS`, `FAILED`, `REFUNDED`
- 🔔 Notification records for booking and payment events
- 🗄️ MySQL persistence using Spring Data JPA
- 📘 OpenAPI / Swagger UI documentation
- ⚠️ Centralized JSON error responses
- 🌱 Startup seed data for demo users, hotels, and room types

---

## 🧰 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.2 |
| Web | Spring Web MVC |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL |
| Security | Spring Security, JWT |
| Validation | Jakarta Bean Validation |
| Documentation | Springdoc OpenAPI |
| Build Tool | Maven Wrapper |
| Boilerplate Reduction | Lombok |
| Test Dependencies | Spring Boot Test, H2 |

---

## 🏗️ Architecture

The project follows a clean **Controller → Service → Repository** structure.

### Main responsibilities

- **Controllers** expose REST endpoints and validate request bodies.
- **Services** contain business rules and transaction logic.
- **Repositories** handle database access through Spring Data JPA.
- **DTOs** isolate API payloads from JPA entities.
- **Mappers** convert between entities and request/response DTOs.
- **Security filters** validate JWT tokens before protected endpoints.
- **Global exception handling** returns consistent JSON error responses.

---

## 📁 Project Structure

```text
src/main/java/com/example/hotalproject
+-- HotelProjectApplication.java
+-- GlobalExceptionHandler.java
+-- OpenApiConfig.java
+-- PagedResponse.java
+-- loadData.java
+-- HotelCatalog
|   +-- availability
|   +-- booking
|   +-- hotel
|   +-- notification
|   +-- payment
|   +-- roomType
|   +-- Utility
+-- media
+-- security
    +-- auth
    +-- refresh
````

---

## 🧩 Domain Model

### Main entities

| Entity         | Description                                                                          |
| -------------- | ------------------------------------------------------------------------------------ |
| `AppUser`      | Authenticated system user with email, password, and role                             |
| `Hotel`        | Hotel catalog item with city, address, manager email, optional image, and room types |
| `RoomType`     | Bookable room category linked to a hotel                                             |
| `Booking`      | Guest reservation for a room type and date range                                     |
| `Payment`      | Mock payment record linked to a booking                                              |
| `Notification` | Saved notification message for a user                                                |
| `RefreshToken` | Refresh token used for JWT rotation and logout                                       |

### Important relationships

* One `Hotel` has many `RoomType` records.
* One `RoomType` belongs to one `Hotel`.
* One `Booking` belongs to one `RoomType`.
* One `Payment` belongs to one `Booking`.

---

## 🔐 Security

The API uses **stateless JWT authentication**.

Protected endpoints require:

```http
Authorization: Bearer <token>
```

### Roles

| Role      | Purpose                                                                    |
| --------- | -------------------------------------------------------------------------- |
| `GUEST`   | Browse public catalog, create own bookings, pay, cancel, and view own data |
| `MANAGER` | Manage hotel/room type data and view manager booking operations            |
| `ADMIN`   | Full privileged access, including delete operations                        |

### Public endpoints

```http
POST /api/auth/**
GET  /swagger-ui/**
GET  /swagger-ui.html
GET  /api-docs/**
GET  /api/hotels/**
GET  /api/room-types/**
GET  /uploads/**
POST /api/availability/check
```

---

## ⚙️ Configuration

Application configuration is stored in:

```text
src/main/resources/application.properties
```

### Current properties

```properties
spring.application.name=rest
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.datasource.url=jdbc:mysql://${MYSQL_HOST}:3306/hotel_watterson
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.api-docs.path=/api-docs

app.security.jwt.secret=${JWT_SECRET}
app.security.jwt.expiration-ms=${JWT_EXPIRATION_MS:86400000}
app.security.jwt.refresh-expiration-ms=${JWT_REFRESH_EXPIRATION_MS:604800000}

app.upload.base-dir=${UPLOAD_BASE_DIR:uploads}

spring.servlet.multipart.max-file-size=${MAX_UPLOAD_FILE_SIZE:5MB}
spring.servlet.multipart.max-request-size=${MAX_UPLOAD_REQUEST_SIZE:5MB}
```

### Required environment variables

| Variable                     | Description                         |
| ---------------------------- | ----------------------------------- |
| `MYSQL_HOST`                 | MySQL host, for example `localhost` |
| `SPRING_DATASOURCE_USERNAME` | MySQL username                      |
| `SPRING_DATASOURCE_PASSWORD` | MySQL password                      |
| `JWT_SECRET`                 | Base64-safe JWT signing secret      |

### Optional environment variables

| Variable                    | Default     |
| --------------------------- | ----------- |
| `JWT_EXPIRATION_MS`         | `86400000`  |
| `JWT_REFRESH_EXPIRATION_MS` | `604800000` |
| `UPLOAD_BASE_DIR`           | `uploads`   |
| `MAX_UPLOAD_FILE_SIZE`      | `5MB`       |
| `MAX_UPLOAD_REQUEST_SIZE`   | `5MB`       |

---

## 🚀 Running Locally

### Prerequisites

* Java 21
* MySQL running locally or remotely
* Database named `hotel_watterson`

### Create the database

```sql
CREATE DATABASE hotel_watterson;
```

### PowerShell example

```powershell
$env:MYSQL_HOST="localhost"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD=""
$env:JWT_SECRET="ZmFrZS1kZW1vLXNlY3JldC1mb3ItY291cnNlLXByb2plY3QtY2hhbmdlLW1lLWFzYXAtMTIzNDU2Nzg5"

.\mvnw.cmd spring-boot:run
```

The API runs on:

```text
http://localhost:8080
```

---

## 🌱 Seed Data

On startup, `loadData` creates demo users when no users exist.

| Email                | Password      | Role      |
| -------------------- | ------------- | --------- |
| `admin@hotel.local`  | `Admin@123`   | `ADMIN`   |
| `manager1@gmail.com` | `Manager@123` | `MANAGER` |
| `guest@hotel.local`  | `Guest@123`   | `GUEST`   |

The seeder also creates demo hotels and room types.

If hotels already exist, it adds room types only for hotels that currently have none.

---

## 📘 API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/api-docs
```

The OpenAPI configuration includes bearer token support, so authenticated endpoints can be tested directly from Swagger UI after logging in.

---

## 🛣️ API Endpoints

---

## 🔑 Authentication

| Method | Endpoint             | Access | Description                                         |
| ------ | -------------------- | ------ | --------------------------------------------------- |
| `POST` | `/api/auth/register` | Public | Register a user                                     |
| `POST` | `/api/auth/login`    | Public | Login and receive tokens                            |
| `POST` | `/api/auth/refresh`  | Public | Rotate refresh token and receive a new access token |
| `POST` | `/api/auth/logout`   | Public | Revoke a refresh token                              |

---

## 🏨 Hotels

| Method   | Endpoint                 | Access         | Description                               |
| -------- | ------------------------ | -------------- | ----------------------------------------- |
| `GET`    | `/api/hotels`            | Public         | Browse hotels with pagination and filters |
| `GET`    | `/api/hotels/{id}`       | Public         | Get hotel details with room types         |
| `POST`   | `/api/hotels`            | Admin, Manager | Create hotel                              |
| `PUT`    | `/api/hotels/{id}`       | Admin, Manager | Update hotel                              |
| `DELETE` | `/api/hotels/{id}`       | Admin          | Delete hotel                              |
| `POST`   | `/api/hotels/{id}/image` | Admin, Manager | Upload or replace hotel image             |

### Hotel filters

```text
city, nameContains, before, after, description, page, size, sort
```

### Allowed hotel sort fields

```text
city, name, description, createdAt, address, id
```

---

## 🛏️ Room Types

| Method   | Endpoint                          | Access         | Description                                   |
| -------- | --------------------------------- | -------------- | --------------------------------------------- |
| `GET`    | `/api/room-types`                 | Public         | Browse room types with pagination and filters |
| `GET`    | `/api/room-types/{id}`            | Public         | Get room type details                         |
| `GET`    | `/api/room-types/hotel/{hotelId}` | Public         | Get all room types for a hotel                |
| `POST`   | `/api/room-types/hotel/{hotelId}` | Admin, Manager | Create room type for a hotel                  |
| `PUT`    | `/api/room-types/{id}`            | Admin, Manager | Update room type                              |
| `DELETE` | `/api/room-types/{id}`            | Admin          | Delete room type                              |
| `POST`   | `/api/room-types/{id}/image`      | Admin, Manager | Upload or replace room type image             |

### Room type filters

```text
amenities, nameContains, minCapacity, maxCapacity, minTotalRooms, maxTotalRooms, minPrice, maxPrice, page, size, sort
```

### Allowed room type sort fields

```text
id, name, capacity, basePrice, totalRooms
```

---

## 📅 Availability

| Method | Endpoint                  | Access | Description                                        |
| ------ | ------------------------- | ------ | -------------------------------------------------- |
| `POST` | `/api/availability/check` | Public | Check availability and calculated price for a stay |

---

## 🧾 Bookings

| Method  | Endpoint                           | Access                | Description                               |
| ------- | ---------------------------------- | --------------------- | ----------------------------------------- |
| `POST`  | `/api/bookings`                    | Authenticated         | Create booking                            |
| `GET`   | `/api/bookings/{id}`               | Owner, Admin, Manager | Get booking by ID                         |
| `GET`   | `/api/bookings`                    | Admin, Manager        | List all bookings                         |
| `GET`   | `/api/bookings/room-types/{id}`    | Admin, Manager        | List bookings for a room type             |
| `PATCH` | `/api/bookings/{bookingId}/cancel` | Owner, Admin, Manager | Cancel booking                            |
| `GET`   | `/api/bookings/guest-history`      | Authenticated         | Get current guest booking history         |
| `GET`   | `/api/bookings/manager-upcoming`   | Admin, Manager        | Get upcoming bookings for current manager |

---

## 💳 Payments

| Method | Endpoint                             | Access                | Description                                     |
| ------ | ------------------------------------ | --------------------- | ----------------------------------------------- |
| `POST` | `/api/payments/intent`               | Owner, Admin, Manager | Create mock payment intent                      |
| `POST` | `/api/payments/{paymentId}/simulate` | Owner, Admin, Manager | Simulate payment result                         |
| `POST` | `/api/payments/{paymentId}/refund`   | Owner, Admin, Manager | Refund successful payment for cancelled booking |
| `GET`  | `/api/payments/{paymentId}`          | Owner, Admin, Manager | Get payment by ID                               |
| `GET`  | `/api/payments`                      | Authenticated         | List visible payments                           |

---

## 🔔 Notifications

| Method | Endpoint             | Access        | Description                        |
| ------ | -------------------- | ------------- | ---------------------------------- |
| `GET`  | `/api/notifications` | Authenticated | Get notifications for current user |

---

## 🖼️ Static Uploads

| Method | Endpoint      | Access | Description                               |
| ------ | ------------- | ------ | ----------------------------------------- |
| `GET`  | `/uploads/**` | Public | Serve uploaded hotel and room type images |

---

## 🧪 Request Examples

---

### 🔑 Login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "guest@hotel.local",
  "password": "Guest@123"
}
```

---

### 🏨 Create Hotel

```http
POST /api/hotels
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "name": "Jerusalem Grand Hotel",
  "city": "Jerusalem",
  "address": "Street 1, Jerusalem",
  "description": "Comfortable stay in Jerusalem",
  "managerEmail": "manager1@gmail.com"
}
```

---

### 🛏️ Create Room Type

```http
POST /api/room-types/hotel/1
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "name": "Deluxe",
  "capacity": 2,
  "basePrice": 120.00,
  "amenities": "WiFi, TV, AC",
  "totalRooms": 10
}
```

---

### 📅 Check Availability

```http
POST /api/availability/check
Content-Type: application/json
```

```json
{
  "hotelId": 1,
  "roomTypeId": 1,
  "checkinDate": "2026-07-01",
  "checkoutDate": "2026-07-05",
  "guests": 2
}
```

---

### 🧾 Create Booking

```http
POST /api/bookings
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "roomTypeId": 1,
  "checkIn": "2026-07-01",
  "checkOut": "2026-07-05",
  "guests": 2
}
```

---

### 💳 Create Payment Intent

```http
POST /api/payments/intent
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "bookingId": 1
}
```

---

### ✅ Simulate Payment Success

```http
POST /api/payments/1/simulate
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "outcome": "SUCCESS"
}
```

---

## 📜 Business Rules

---

## 📅 Availability Rules

* Check-in date cannot be in the past.
* Check-in date must be before check-out date.
* Requested guests must fit the room type capacity.
* Room type must belong to the supplied hotel.
* Available rooms are calculated from total rooms minus active overlapping bookings.
* Friday and Saturday apply a weekend price multiplier.
* June, July, and August apply a seasonal price multiplier.

---

## 🧾 Booking Rules

* New bookings start as `PENDING`.
* Check-out must be after check-in.
* Guest count must be at least `1`.
* Non-privileged users can only create bookings for themselves.
* Managers and admins can create bookings for a supplied guest email.
* Users can only access their own bookings unless they are manager/admin.
* Cancellation is blocked less than 24 hours before check-in.

---

## 💳 Payment Rules

* Payment intent can only be created for a `PENDING` booking.
* Only one payment may exist for a booking.
* Only `INITIATED` payments can be simulated.
* `SUCCESS` confirms the booking.
* `FAILED` leaves the booking pending.
* Refund requires a `SUCCESS` payment and a cancelled booking.

---

## 🔔 Notification Rules

* Booking and payment events create notification records.
* Notifications are stored in the database.
* Authenticated users can retrieve their own notifications.

---

## 🖼️ Uploads

The system supports uploading hotel and room type images.

### Supported upload types

```text
jpeg, jpg, png, webp, gif
```

### Default upload location

```text
uploads/
```

### Returned image URLs

```text
/uploads/hotels/<file>
/uploads/room-types/<file>
```

### Upload request format

Upload endpoints consume:

```http
multipart/form-data
```

Field name:

```text
file
```

Example in Postman:

| Key    | Type | Value          |
| ------ | ---- | -------------- |
| `file` | File | selected image |

---

## ⚠️ Error Handling

Errors are returned as a consistent JSON object.

```json
{
  "timestamp": "2026-01-01T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/bookings"
}
```

### Common status codes

| Status | Meaning                                           |
| ------ | ------------------------------------------------- |
| `400`  | Validation or business rule error                 |
| `401`  | Missing, invalid, or expired credentials          |
| `403`  | Authenticated user does not have permission       |
| `404`  | Requested resource was not found                  |
| `409`  | Conflict with current state or duplicate resource |

---

## 🧪 Testing And Build

### Compile without running tests

```powershell
.\mvnw.cmd -DskipTests compile
```

### Run tests

```powershell
.\mvnw.cmd test
```

### Package the application

```powershell
.\mvnw.cmd clean package
```

### Run the packaged JAR

```powershell
java -jar target/rest-0.0.1-SNAPSHOT.jar
```

---

## 📝 Development Notes

* The application currently uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

This is convenient for development, but database migrations are recommended for production.

* Uploaded files are stored on local disk under:

```text
UPLOAD_BASE_DIR
```

* CORS allows frontend origins:

```text
http://localhost:3000
http://localhost:5173
```

* `loadData` is enabled at startup and seeds demo users, hotels, and room types.
* Swagger UI is the fastest way to explore and test the API during development.

---

## 💳 Payment Notice

This project uses a **mock payment flow** for demonstration purposes only.

No real payment gateway is integrated.

The goal is to demonstrate the backend flow of:

```text
Create booking
→ Create payment intent
→ Simulate payment result
→ Update booking/payment status
→ Create notification record
```

---

## ✅ Project Status

This project is ready as a backend portfolio project and demonstrates core backend development concepts using Spring Boot, including authentication, authorization, REST API design, business rules, persistence, media upload, and API documentation.

```

في رأيي هذا صار مناسب جدًا لـ GitHub و LinkedIn، خصوصًا آخر قسم **Payment Notice** لأنه بيوضح باحتراف إن الدفع mock ومش دفع حقيقي.
```
