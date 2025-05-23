# Notes API Technical Challenge

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [Technologies](#technologies)
4. [How to run the application](#how-to-run-the-application)
    - [Running with Docker](#running-with-docker)
5. [Architectural Decision Records](#architectural-decision-records-adr)

## Overview
The Notes API is a RESTful web service designed to manage personal notes. It provides endpoints to create, retrieve, update, delete, and search notes with pagination and keyword-based search capabilities.
The API is developed using Spring Boot and implements reactive programming to enable efficient, non-blocking request processing.

## Features
- Create new notes
- Retrieve a specific note by its ID
- Retrieve all notes with pagination
- Search notes by keyword with pagination
- Update an existing note
- Delete a note by ID

## Technologies
- **Java 21**
- **Maven** (build and dependency management)
- **Spring Boot**
- **Project Reactor** (Reactive Programming)
- **Jakarta Validation** (for request validation)
- **Swagger/OpenAPI** (for API documentation)
- **Docker** (containerization and deployment)

## API Documentation
You can explore and test all endpoints using:
- Swagger UI at `http://localhost:8080/webjars/swagger-ui.html`
- Included Postman collection (postman/notes-api.postman_collection.json)

## How to run the application
### Running with Docker
The application is fully dockerized for easy deployment. You need to have [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) installed.
1. **Build and start the application with MongoDB:**
``` bash
   docker-compose up --build
```
This will:
- Start a MongoDB container.
- Build the Notes API container and start the application.
- Expose the API on port `8080`.

2. **Stopping the services:**
``` bash
   docker-compose down
```

You can then interact with the API (default on port `8080`) using an API client such as **Postman** or with `curl`.

## Architectural Decision Records (ADR)
This repository follows modern architectural and design principles with a focus on scalability, maintainability, and testability.

- **Hexagonal Architecture**: The project follows a hexagonal architecture, divided into domain, application, and infrastructure packages, inspired by [this article by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html).

- **Reactive Stack**: The application is fully reactive for non-blocking, scalable performance.

- **Domain-Driven Design (DDD)**: The application organizes its main business logic following DDD principles to keep the code structure clear and aligned with the domain.

- **Test-Driven Development (TDD)**: Tests were written before the implementation of features to validate expected behavior and guide design decisions.

- **DTO Layer**: All API responses are wrapped in DTOs to decouple domain models from external representation and avoid leaking implementation details.

- **Docker-first Development**: The entire stack can be launched with a single `docker-compose up --build` command, which simplifies development and testing.

