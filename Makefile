# Environment variables from .env file
include .env
export

help:
	@echo "MatchMe Commands:"
	@echo "  make dev            - Run development environment with dockerized PostgreSQL"
	@echo "  make dev-local-db   - Run development with local PostgreSQL"
	@echo "  make dev-down       - Stop development environment"
	@echo "  make prod           - Run production environment"
	@echo "  make prod-down      - Stop production environment"
	@echo "  make test           - Run all tests"
	@echo "  make test-back      - Run backend tests only"
	@echo "  make test-front     - Run frontend tests (needs dev environment running)"
	@echo "  make logs           - View all logs"

dev:
	docker compose up -d db
	cd back-end && ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.datasource.url=jdbc:postgresql://localhost:5432/match -Dspring.datasource.username=${POSTGRES_USER} -Dspring.datasource.password=${POSTGRES_PASSWORD}" &
	cd front-end && pnpm dev

dev-local-db:
	cd back-end && ./mvnw spring-boot:run &
	cd front-end && pnpm dev

dev-down:
	docker compose down db
	pkill -f "spring-boot:run" || true
	pkill -f "pnpm dev" || true

prod:
	docker compose build
	docker compose up -d

prod-down:
	docker compose down

test: test-back test-front

test-back:
	cd back-end && ./mvnw test -B \
		-DSPRING_DATASOURCE_URL=jdbc:h2:mem:testdb \
		-DSPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver \
		-DSPRING_DATASOURCE_USERNAME=sa \
		-DSPRING_DATASOURCE_PASSWORD=password \
		-DSPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.H2Dialect

test-front:
	@echo "Running frontend tests - make sure dev environment is running"
	cd front-end && pnpm install --no-frozen-lockfile
	cd front-end && PLAYWRIGHT_TEST_BASE_URL=http://localhost:3000 pnpm exec playwright test

logs:
	docker compose logs -f