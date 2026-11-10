#!/usr/bin/env bash
# Am.Yo : installe et démarre PostgreSQL, les tables sont créées par Flyway au lancement du back
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ENV_FILE="$ROOT/backend/.env"

if [ ! -f "$ENV_FILE" ]; then
  cp "$ROOT/backend/.env.example" "$ENV_FILE"
  echo "backend/.env créé depuis .env.example. Renseigne DB_PASSWORD et JWT_SECRET, puis relance."
  exit 1
fi

docker compose --env-file "$ENV_FILE" -f "$ROOT/docker-compose.yml" up -d --wait postgres
echo "PostgreSQL prêt. Lance le back : cd backend && ./mvnw spring-boot:run"
