# Notes API Technical Challenge

## Table of Contents
1. [Overview](#overview)
2. [Technologies](#technologies)
3. [API Documentation](#api-documentation)
4. [How to Run the Application](#how-to-run-the-application)
5. [Architectural Decision Records (ADR)](#architectural-decision-records-adr)

## Overview
The Notes API is a RESTful web service designed to manage personal notes. It provides endpoints to create, retrieve, update, delete, and search notes with pagination and keyword-based search capabilities.
The API is developed using Spring Boot and implements reactive programming to enable efficient, non-blocking request processing.

## Technologies
- **Java 21**
- **Maven** (build and dependency management)
- **Spring Boot**
- **Project Reactor** (Reactive Programming)
- **Swagger/OpenAPI** (for API documentation)
- **Docker** (containerization and deployment)

## API Documentation
API includes:
- `GET /notes`: Paginated list of notes
- `GET /notes/{id}`: Retrieve a note by ID
- `POST /notes`: Create a new note
- `PUT /notes/{id}`: Update an existing note
- `DELETE /notes/{id}`: Delete a note
- Keyword search via query parameters

You can explore and test all endpoints using:
- [Swagger UI](http://localhost:8080/swagger-ui.html)
- Included Postman collection (postman/collections/notes-api.postman_collection.json)

## How to run the application
The application is fully dockerized for easy deployment. There is also a Makefile available, so in order to execute the application, you need to have [Docker](https://www.docker.com/), [Docker Compose](https://docs.docker.com/compose/) and `make` installed.
1. **Build and start the application with MongoDB:**
``` bash
   make up
```
This will:
- Start a MongoDB container.
- Build the Notes API container and start the application.
- Expose the API on port `8080`.

2. **Stopping the services:**
``` bash
   make down
```
3. **Running the test suite**

There are unit and integration tests for the application. You can run them with:
``` bash
   make test
```

## Architectural Decision Records (ADR)
This repository follows modern architectural and design principles with a focus on scalability, maintainability, and testability.

- **Hexagonal Architecture**: The project follows a hexagonal architecture, divided into domain, application, and infrastructure packages, inspired by [this article by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html).

- **Reactive Stack**: The application is fully reactive for non-blocking, scalable performance.

- **Domain-Driven Design (DDD)**: The application organizes its main business logic following DDD principles to keep the code structure clear and aligned with the domain.

- **Test-Driven Development (TDD)**: Tests were written before the implementation of features to validate expected behavior and guide design decisions.

- **DTO Layer**: All API responses are wrapped in DTOs to decouple domain models from external representation and avoid leaking implementation details.

- **Docker-first Development**: The entire stack can be launched with a single command, which simplifies development and testing.

