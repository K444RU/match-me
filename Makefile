include .env
export

help:
	@echo "  make prod           - Run production environment"
	@echo "  make prod-down      - Stop production environment"

prod:
	docker compose build
	docker compose up -d

prod-down:
	docker compose down
