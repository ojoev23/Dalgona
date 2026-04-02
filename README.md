# Dalgona - Self-Service Umbrella Rental

Dalgona is a full-stack umbrella rental platform inspired by station-based bike sharing systems.
Users can register, authenticate, rent an umbrella from a station, return it to another station, and track order history.

## Functionality

### Core user flow
- Register and log in with JWT-based authentication.
- Browse stations and view available capacity/inventory state.
- Rent an umbrella from a station.
- Return an umbrella to a station with free slots.
- View active rentals and historical orders.
- View and update account profile data.

### Admin capabilities
- Manage accounts.
- Manage stations.
- Manage umbrellas.
- View and manage all orders.

### Inventory model
- Station capacity is modeled with **station slots** (`StationSlot`) rather than a simple umbrella set/count.
- Occupancy is derived from whether each slot has an umbrella assigned.
- Rental flow removes an umbrella from an occupied slot and marks it in use.
- Return flow places umbrella into an empty slot and closes the active order.

## Tech Stack

### Backend
- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA (Hibernate)
- Spring Security
- JWT (`io.jsonwebtoken`)
- PostgreSQL (dev/runtime)
- H2 (tests)
- Gradle Kotlin DSL
- JUnit + Mockito + Spring Security Test

### Frontend
- React 19
- Vite 7
- React Router
- Axios
- React Hook Form
- ESLint

### DevOps / Runtime
- Docker + Docker Compose
- Multi-container setup: `postgres`, `backend`, `frontend`

## Project Structure

```text
src/main/java/com/joeolapurath/dalgona/
  controller/      # REST APIs
  service/         # business logic
  repository/      # JPA repositories
  model/           # entities (Station, StationSlot, Umbrella, Order, Account)
  dto/             # request/response DTOs
  security/        # JWT utility + request filter

frontend/src/
  services/api.js  # axios client + token handling (localStorage key: jwt_token)
  components/      # route guards and shared UI
  *.jsx            # pages (Login, Register, Stations, Orders, Account, Admin)
```

## Security and Access
- Public endpoints:
  - `/api/auth/**`
  - `/error`
  - `GET /api/stations/**`
- All other endpoints require a valid Bearer JWT.
- JWT principal is account email, then services resolve the `Account` from email.

## Local Development

### Prerequisites
- Java 21
- Node.js + npm
- Docker (optional, for containerized run)
- PostgreSQL (if running backend locally without Docker)

### 1) Run backend
```bash
./gradlew bootRun
```

Backend default: `http://localhost:8080`

### 2) Run frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend default: `http://localhost:5173`

`/api` requests are proxied by Vite to backend (`API_URL` override supported).

### 3) Run backend + frontend together (from `frontend/`)
```bash
npm run dev:all
```

### 4) Run with Docker Compose
```bash
docker compose up --build
```

## Testing

Run backend tests:
```bash
./gradlew test
```

Notes:
- Tests use H2 with `create-drop`.
- Controller tests include CSRF tokens in write requests for consistency with test style.

## Configuration

Backend app properties are in `src/main/resources/application.properties`.
Important values include:
- `spring.datasource.url` (uses `DB_HOST`, default `localhost`)
- `spring.jpa.hibernate.ddl-auto=update` for local dev
- `dalgona.jwt.secret`
- bootstrap admin credentials properties

## API Surface (high level)
- Auth: `/api/auth/login`, `/api/auth/register`
- Stations: `/api/stations`
- Umbrella actions: `/api/umbrellas/rent`, `/api/umbrellas/return`
- Orders: `/api/orders`, `/api/orders/active`, `/api/orders/{id}`
- Account: `/api/account/me`
- Admin: `/api/admin/*`

## Known Conventions
- Frontend stores JWT in `localStorage` key: `jwt_token`.
- Frontend API helpers return raw Axios promises; pages use `res.data` directly.
- Runtime/service errors often use exact message strings (tests assert these verbatim).

## Current Status
This project currently supports end-to-end umbrella rental flows across backend and frontend, including station-slot inventory tracking, account management, and admin operations.

