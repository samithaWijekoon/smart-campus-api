# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W – Client-Server Architectures  
**Technology:** JAX-RS (Jersey 2.41) + Grizzly Embedded Server  
**Base URL:** `http://localhost:8080/api/v1`

---

## Table of Contents

1. [API Design Overview](#api-design-overview)
2. [Project Structure](#project-structure)
3. [Build & Run Instructions](#build--run-instructions)
4. [Sample curl Commands](#sample-curl-commands)
5. [Report: Answers to Coursework Questions](#report-answers-to-coursework-questions)

---

## API Design Overview

This API manages the Smart Campus infrastructure by exposing three primary resources arranged in a logical hierarchy:

```
/api/v1
├── /rooms                        ← Room collection
│   ├── GET    /                  ← List all rooms
│   ├── POST   /                  ← Create a room
│   ├── GET    /{roomId}          ← Get one room
│   └── DELETE /{roomId}          ← Delete a room (if empty)
│
└── /sensors                      ← Sensor collection
    ├── GET    /                  ← List all (optional ?type= filter)
    ├── POST   /                  ← Register a sensor
    ├── GET    /{sensorId}        ← Get one sensor
    ├── DELETE /{sensorId}        ← Delete a sensor
    └── /{sensorId}/readings      ← Sub-resource (reading history)
        ├── GET  /                ← Get all readings for sensor
        └── POST /                ← Add a new reading
```

### Data Models

**Room** – A physical space on campus  
**Sensor** – Hardware device installed in a room (`ACTIVE`, `MAINTENANCE`, `OFFLINE`)  
**SensorReading** – A single timestamped measurement recorded by a sensor

### Error Handling Strategy

Every error returns a structured JSON body — never a raw Java stack trace:

| Exception | HTTP Code | Scenario |
|---|---|---|
| `RoomNotEmptyException` | 409 Conflict | Delete room with sensors |
| `LinkedResourceNotFoundException` | 422 Unprocessable Entity | Sensor references non-existent room |
| `SensorUnavailableException` | 403 Forbidden | POST reading to MAINTENANCE sensor |
| `GlobalExceptionMapper` | 500 Internal Server Error | Any unexpected runtime error |

---

## Project Structure

```
smart-campus-api/
├── pom.xml
└── src/main/java/com/smartcampus/
    ├── Main.java                              ← Grizzly server entry point
    ├── SmartCampusApplication.java            ← JAX-RS @ApplicationPath config
    ├── model/
    │   ├── Room.java
    │   ├── Sensor.java
    │   └── SensorReading.java
    ├── store/
    │   └── DataStore.java                     ← Thread-safe in-memory store (Singleton)
    ├── resource/
    │   ├── DiscoveryResource.java             ← GET /api/v1
    │   ├── RoomResource.java                  ← /api/v1/rooms
    │   ├── SensorResource.java                ← /api/v1/sensors
    │   └── SensorReadingResource.java         ← /api/v1/sensors/{id}/readings
    ├── exception/
    │   ├── RoomNotEmptyException.java
    │   ├── RoomNotEmptyExceptionMapper.java
    │   ├── LinkedResourceNotFoundException.java
    │   ├── LinkedResourceNotFoundExceptionMapper.java
    │   ├── SensorUnavailableException.java
    │   ├── SensorUnavailableExceptionMapper.java
    │   └── GlobalExceptionMapper.java
    └── filter/
        └── ApiLoggingFilter.java              ← Logs all requests and responses
```

---

## Build & Run Instructions

### Prerequisites

- Java JDK 11 or higher
- Apache Maven 3.6+

Verify your environment:
```bash
java -version
mvn -version
```

### Step 1 – Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/smart-campus-api.git
cd smart-campus-api
```

### Step 2 – Build the Project

```bash
mvn clean package
```

This compiles all Java files and bundles everything into a single fat JAR:  
`target/smart-campus-api-1.0-SNAPSHOT.jar`

### Step 3 – Run the Server

```bash
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
```

You will see:
```
=======================================================
  Smart Campus API — Server Started Successfully!
=======================================================
  Base URL   : http://localhost:8080/
  Rooms      : http://localhost:8080/api/v1/rooms
  Sensors    : http://localhost:8080/api/v1/sensors
=======================================================
  Press ENTER to stop the server...
```

### Step 4 – Test the API

Open a new terminal and run any of the curl commands below, or open Postman and import the requests.

To stop the server, press **ENTER** in the server terminal.

---

## Sample curl Commands

### 1. Discovery – GET /api/v1
```bash
curl -X GET http://localhost:8080/api/v1 \
  -H "Accept: application/json"
```
**Expected:** 200 OK with API metadata and resource links.

---

### 2. Create a Room – POST /api/v1/rooms
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id": "ENG-101",
    "name": "Engineering Lab 101",
    "capacity": 40
  }'
```
**Expected:** 201 Created with the new Room JSON.

---

### 3. Get All Rooms – GET /api/v1/rooms
```bash
curl -X GET http://localhost:8080/api/v1/rooms \
  -H "Accept: application/json"
```
**Expected:** 200 OK with a list of all rooms (includes pre-loaded sample rooms).

---

### 4. Get a Specific Room – GET /api/v1/rooms/{roomId}
```bash
curl -X GET http://localhost:8080/api/v1/rooms/LIB-301 \
  -H "Accept: application/json"
```
**Expected:** 200 OK with Room LIB-301 details.

---

### 5. Create a Sensor – POST /api/v1/sensors
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "TEMP-002",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 20.0,
    "roomId": "ENG-101"
  }'
```
**Expected:** 201 Created with the new Sensor JSON.

---

### 6. Get All Sensors Filtered by Type – GET /api/v1/sensors?type=CO2
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2" \
  -H "Accept: application/json"
```
**Expected:** 200 OK with only CO2 sensors.

---

### 7. Post a Sensor Reading – POST /api/v1/sensors/{sensorId}/readings
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{
    "value": 24.7
  }'
```
**Expected:** 201 Created. Also updates TEMP-001's `currentValue` to 24.7.

---

### 8. Get Reading History – GET /api/v1/sensors/{sensorId}/readings
```bash
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Accept: application/json"
```
**Expected:** 200 OK with list of all historical readings for TEMP-001.

---

### 9. Delete Room with Sensors (Error Demo) – DELETE /api/v1/rooms/LIB-301
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301 \
  -H "Accept: application/json"
```
**Expected:** 409 Conflict — room still has sensors TEMP-001 and CO2-001.

---

### 10. Post Reading to MAINTENANCE Sensor (Error Demo)
```bash
curl -X POST http://localhost:8080/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 10.0}'
```
**Expected:** 403 Forbidden — OCC-001 is in MAINTENANCE status.

---

### 11. Create Sensor with Invalid Room (Error Demo)
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "FAKE-999",
    "type": "Temperature",
    "status": "ACTIVE",
    "roomId": "ROOM-DOESNT-EXIST"
  }'
```
**Expected:** 422 Unprocessable Entity — roomId references a non-existent room.

---

### 12. Delete a Sensor, then Delete its (now-empty) Room
```bash
# First delete both sensors from LIB-301
curl -X DELETE http://localhost:8080/api/v1/sensors/TEMP-001
curl -X DELETE http://localhost:8080/api/v1/sensors/CO2-001

# Now the room is empty — deletion succeeds
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```
**Expected:** 204 No Content on the final DELETE.

---

## Report: Answers to Coursework Questions

---

### Part 1.1 – JAX-RS Resource Class Lifecycle

By default, JAX-RS uses **per-request scope**: a brand new instance of each resource class is instantiated for every incoming HTTP request, and discarded once the response is sent. This is the opposite of a singleton.

This architectural decision has a critical implication for data management. If application data (rooms, sensors, readings) were stored as instance fields inside the resource class, that data would be created fresh with each request and then thrown away — nothing would persist between calls. The API would be useless.

To solve this, the project uses a **Singleton `DataStore`** class. The DataStore is instantiated once when the application starts and lives for the entire application lifetime. Every resource class instance — regardless of how many there are or when they are created — calls `DataStore.getInstance()` to access the same shared object.

Additionally, because multiple HTTP requests can arrive simultaneously (the server is multi-threaded), the DataStore uses `ConcurrentHashMap` instead of a regular `HashMap`. A plain `HashMap` is not thread-safe: two threads writing to it concurrently can corrupt its internal structure, causing data loss or `ConcurrentModificationException`. `ConcurrentHashMap` handles concurrent reads and writes safely, eliminating race conditions without requiring manual `synchronized` blocks.

---

### Part 1.2 – HATEOAS and Hypermedia in REST

HATEOAS (Hypermedia As The Engine Of Application State) is the principle that REST API responses should include navigational links, allowing clients to discover and traverse available actions dynamically rather than hardcoding URLs.

Without HATEOAS, a client developer must read documentation to learn that rooms live at `/api/v1/rooms` and sensors at `/api/v1/sensors`. If the server team later renames a path, every client application must be updated manually. This tight coupling between client and server is fragile.

With HATEOAS, the discovery endpoint at `GET /api/v1` returns a `resources` map containing the current URLs for all collections. Clients navigate by following these links. Benefits include:

- **Self-documenting API**: The API describes itself at runtime, reducing reliance on external documentation.
- **Reduced coupling**: Clients are not hardcoded to specific URL structures; they follow links provided by the server.
- **Resilience to change**: URL changes only require updating the server response — clients that follow links automatically adapt.
- **Improved developer experience**: A developer can explore the entire API starting from a single root URL.

---

### Part 2.1 – Returning IDs vs Full Objects in List Responses

When a client calls `GET /api/v1/rooms`, the server can respond with either a list of room IDs or a list of full room objects. The trade-offs are:

**Returning only IDs:**
- *Pro:* Minimal bandwidth — a list of 1,000 room IDs is tiny compared to full objects.
- *Con:* Forces N+1 HTTP calls — the client must make a separate `GET /rooms/{id}` request for every ID to display anything useful. This is extremely inefficient and increases latency significantly.

**Returning full objects (chosen approach):**
- *Pro:* Single round-trip — the client has everything needed to render a complete room list immediately.
- *Pro:* Better client developer experience — no need to write code to hydrate IDs into objects.
- *Con:* Higher per-response payload size, which matters at very large scale.

For this Smart Campus API, full objects are the correct choice. The room model is small, the list size is bounded, and the N+1 problem would make an ID-only approach impractical for any real client application.

---

### Part 2.2 – Idempotency of the DELETE Operation

An operation is **idempotent** if performing it once produces the same server-side state as performing it N times.

In our implementation:

- **First DELETE** on `/api/v1/rooms/ENG-101`: The room exists and has no sensors → it is removed → server returns **204 No Content**.
- **Second DELETE** on the same URL: The room no longer exists → the server returns **404 Not Found**.

The HTTP response codes differ between the first and second calls, but the **server state is identical** after both: the room does not exist. RFC 7231 defines idempotency in terms of server-side state, not response codes. By that definition, DELETE is idempotent — repeated calls cause no additional state changes beyond the first.

This is the industry-standard interpretation. Some APIs choose to return 204 on all subsequent DELETEs to make idempotency more obvious to clients, but returning 404 is more informative and equally valid.

---

### Part 3.1 – @Consumes and Content-Type Mismatch

The `@Consumes(MediaType.APPLICATION_JSON)` annotation on the POST method declares that this endpoint only accepts request bodies in JSON format (`Content-Type: application/json`).

If a client sends a request with `Content-Type: text/plain` or `Content-Type: application/xml`, the JAX-RS runtime performs content negotiation **before** the method is even called:

1. JAX-RS reads the `Content-Type` header from the incoming request.
2. It compares it against the `@Consumes` annotation on all candidate methods.
3. Finding no match, JAX-RS immediately returns **HTTP 415 Unsupported Media Type**.
4. Our resource method code is never executed at all.

This is enforced entirely by the framework — we write no manual content-type checking code. The client receives a clear 415 error indicating the correct media type to use.

---

### Part 3.2 – @QueryParam vs Path-Based Filtering

Comparing `GET /api/v1/sensors?type=CO2` (query parameter) against `GET /api/v1/sensors/type/CO2` (path segment):

**Query parameter approach (used in this project):**
- Semantically correct: the `/sensors` resource is the target; `?type=CO2` is a *modifier* on that collection, not a sub-resource.
- Optional by nature: omitting `?type=` returns all sensors naturally.
- Composable: multiple filters combine naturally — `?type=CO2&status=ACTIVE` is clean and readable.
- Matches REST convention: query parameters are the standard for filtering, searching, and sorting collections.

**Path segment approach:**
- Semantically misleading: `/sensors/type/CO2` implies `type` is a sub-resource (like `/readings` is), but it's just a filter.
- Rigid and non-composable: combining filters becomes ugly (`/sensors/type/CO2/status/ACTIVE`).
- Harder to version and extend.
- Conflicts with existing paths: `/sensors/{sensorId}` and `/sensors/type/CO2` would be ambiguous to the JAX-RS router.

The query parameter approach is the REST standard for filtering and searching collections, as documented in RFC 3986 and widely adopted across industry APIs (GitHub, Stripe, Twitter, etc.).

---

### Part 4.1 – Sub-Resource Locator Pattern Benefits

The sub-resource locator pattern allows a resource class to delegate handling of a URL segment to a separate dedicated class. In this project, `SensorResource` handles `/sensors` and `/sensors/{id}`, and returns a `SensorReadingResource` instance for the `/readings` sub-path.

Benefits over a monolithic single-class approach:

1. **Single Responsibility Principle**: `SensorResource` handles sensor CRUD. `SensorReadingResource` handles reading operations. Neither class needs to know about the other's internals.

2. **Manageability**: A single class containing all sensor AND reading endpoints would grow very large — potentially hundreds of lines — making it difficult to navigate and maintain.

3. **Independent Evolution**: New reading operations (e.g., `DELETE /readings/{id}`, `PUT /readings/{id}`) can be added to `SensorReadingResource` without modifying `SensorResource` at all.

4. **Testability**: Smaller, focused classes are far easier to unit-test in isolation.

5. **Mirrors URL hierarchy in code**: Just as `/sensors/{id}/readings` is nested under `/sensors/{id}` in the URL, `SensorReadingResource` is logically nested under `SensorResource` in the code. The structure is self-documenting.

In large APIs with dozens of resources and hundreds of endpoints, this pattern is essential for keeping the codebase organized.

---

### Part 5.2 – HTTP 422 vs HTTP 404 for Missing References

When a client POSTs a new sensor with a `roomId` that does not exist, the correct response is **422 Unprocessable Entity**, not 404 Not Found.

The distinction:

- **404 Not Found** means the *requested resource* (the endpoint itself) could not be found. The URL `/api/v1/sensors` exists and is reachable — 404 would be incorrect and misleading.

- **422 Unprocessable Entity** means: "I received your request, I understood it, the JSON syntax is valid, but I cannot process it because the *semantic content* is invalid." Specifically, the `roomId` field references an entity that does not exist in the system — a referential integrity violation.

Think of the analogy of submitting a form: 404 would mean the form submission page doesn't exist. 422 means the form page exists and the form was filled out correctly, but you entered an invalid value in one of the fields (a foreign key that doesn't exist).

422 gives the client precise, actionable information: the request body's data is the problem, not the URL. This leads to faster debugging and better API usability.

---

### Part 5.4 – Security Risks of Exposing Stack Traces

Exposing raw Java stack traces in API responses is a significant security vulnerability. An attacker can extract:

1. **Internal package and class names** (e.g., `com.smartcampus.store.DataStore`): Reveals the internal architecture, making it easier to understand the codebase structure and identify attack surfaces.

2. **File names and line numbers** (e.g., `DataStore.java:142`): Pinpoints exactly where code executes, helping attackers craft targeted exploits.

3. **Third-party library names and versions** (e.g., `jersey-server-2.41.jar`): Enables attackers to look up known CVEs (Common Vulnerabilities and Exposures) for those exact library versions.

4. **Internal data values**: Stack traces sometimes include object `toString()` values in exception messages, which may contain sensitive data like configuration values or internal IDs.

5. **Server technology fingerprinting**: Exposes the Java version, framework, and server environment, narrowing the attack surface considerably.

The `GlobalExceptionMapper` in this project defends against all of these by catching every unhandled exception, logging the full details **server-side only** (for developers), and returning a safe, generic message to the client: "An unexpected error occurred." Attackers receive no useful information.

---

### Part 5.5 – JAX-RS Filters for Cross-Cutting Concerns

Cross-cutting concerns are behaviours that apply uniformly across the entire application, regardless of specific business logic — logging, authentication, rate limiting, and CORS headers are classic examples.

Using a JAX-RS filter (`ApiLoggingFilter`) for logging instead of manual `Logger.info()` calls in each resource method offers several advantages:

1. **DRY Principle**: One filter class handles logging for all endpoints. Adding a new endpoint automatically inherits logging — no developer action required.

2. **Separation of Concerns**: Resource methods contain only business logic. The logging infrastructure is completely separate, making both easier to read and maintain.

3. **Guaranteed consistency**: Manual logging relies on developers remembering to add Logger calls. A filter applies to every request without exception — no endpoint can accidentally be left unlogged.

4. **Easy to modify or disable**: Changing the log format, log level, or adding request timing requires modifying only the filter class. Disabling logging entirely just means removing `@Provider` — no resource classes are touched.

5. **Chainable**: Multiple filters can be stacked (e.g., logging + authentication + CORS headers) and ordered using `@Priority`, creating a clean, layered request pipeline — impossible to achieve cleanly with embedded Logger calls.
