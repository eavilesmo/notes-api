PROJECT_NAME=notes-api
DOCKER_COMPOSE=docker compose

.PHONY: build up down test help

build:
	$(DOCKER_COMPOSE) build

up:
	@echo "Starting $(PROJECT_NAME)..."
	@$(DOCKER_COMPOSE) up -d
	@sleep 3
	@if [ "$$(docker inspect -f '{{.State.Running}}' notes-api 2>/dev/null)" = "true" ]; then \
		echo "$(PROJECT_NAME) is up and running!"; \
	else \
		echo "Failed to start $(PROJECT_NAME)."; \
	fi

down:
	@echo "Stopping $(PROJECT_NAME)..."
	@$(DOCKER_COMPOSE) down
	@sleep 2
	@if [ "$$(docker ps -q -f name=notes-api)" = "" ]; then \
		echo "$(PROJECT_NAME) has been stopped successfully."; \
	else \
		echo "Something went wrong stopping $(PROJECT_NAME)."; \
	fi

test:
	$(DOCKER_COMPOSE) run --rm test-runner

help:
	@echo "Usage:"
	@echo "  make build     - Build the Docker images"
	@echo "  make up        - Start the app and MongoDB"
	@echo "  make down      - Stop the app and MongoDB"
	@echo "  make test      - Run unit and integration tests"
