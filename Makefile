# Simple Makefile for Black and Pink Inventory Management

.PHONY: help build run clean test docker-wine docker-native docker-dev installer

# Default target
help:
	@echo "Black and Pink Inventory Management - Build Commands"
	@echo ""
	@echo "Development Commands:"
	@echo "  make build         Build the application"
	@echo "  make run           Run the application"
	@echo "  make test          Run tests"
	@echo "  make clean         Clean build artifacts"
	@echo ""
	@echo "Docker Windows Installer Commands:"
	@echo "  make installer     Build Windows installer using Docker (Wine)"
	@echo "  make docker-wine   Build Windows installer using Wine"
	@echo "  make docker-native Build Windows installer using native Windows"
	@echo "  make docker-dev    Build application in development container"
	@echo ""
	@echo "Utility Commands:"
	@echo "  make docker-clean  Clean Docker images and containers"
	@echo "  make full-clean    Clean everything (build + Docker)"

# Local development
build:
	./gradlew build

run:
	./gradlew run

test:
	./gradlew test

clean:
	./gradlew clean
	rm -rf dist/

# Docker-based Windows installer (default to Wine approach)
installer:
	./build-windows-installer.sh --wine

docker-wine:
	./build-windows-installer.sh --wine

docker-native:
	./build-windows-installer.sh --native

docker-dev:
	./build-windows-installer.sh --dev

# Utility targets
docker-clean:
	docker-compose down --remove-orphans || true
	docker system prune -f || true

full-clean: clean docker-clean
	./build-windows-installer.sh --clean
