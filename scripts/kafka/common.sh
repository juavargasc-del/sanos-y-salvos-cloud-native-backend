#!/usr/bin/env bash

COMPOSE_FILE="docker-compose.aws.yml"
ENV_FILE=".env.aws"
KAFKA_SERVICE="kafka"
BOOTSTRAP_SERVER="localhost:9092"

if [[ -t 1 ]]; then
    RED=$'\033[31m'
    GREEN=$'\033[32m'
    YELLOW=$'\033[33m'
    CYAN=$'\033[36m'
    RESET=$'\033[0m'
else
    RED=""
    GREEN=""
    YELLOW=""
    CYAN=""
    RESET=""
fi

kafka_error() {
    printf '%sERROR:%s %s\n' "$RED" "$RESET" "$*" >&2
}

kafka_info() {
    printf '%sINFO:%s %s\n' "$CYAN" "$RESET" "$*"
}

kafka_init() {
    if [[ ! -d ".git" ]]; then
        kafka_error "Ejecute los scripts desde la raiz del repositorio."
        exit 1
    fi

    if ! command -v docker >/dev/null 2>&1; then
        kafka_error "Docker no esta disponible en PATH."
        exit 1
    fi

    if ! docker compose version >/dev/null 2>&1; then
        kafka_error "Docker Compose v2 no esta disponible."
        exit 1
    fi

    if [[ ! -f "$ENV_FILE" ]]; then
        kafka_error "No se encontro .env.aws en la raiz del repositorio."
        exit 1
    fi

    if [[ ! -f "$COMPOSE_FILE" ]]; then
        kafka_error "No se encontro docker-compose.aws.yml en la raiz del repositorio."
        exit 1
    fi

    if [[ -z "$(docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" ps -q "$KAFKA_SERVICE")" ]]; then
        kafka_error "El servicio Kafka no esta iniciado en Docker Compose."
        exit 1
    fi

    local missing
    missing="$(
        docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" \
            exec -T "$KAFKA_SERVICE" sh -c '
missing=""
for command_name in kafka-topics kafka-configs kafka-consumer-groups; do
    if ! command -v "$command_name" >/dev/null 2>&1; then
        missing="${missing:+${missing}, }${command_name}"
    fi
done
printf "%s" "$missing"
'
    )"

    if [[ -n "$missing" ]]; then
        kafka_error "Comandos Kafka no disponibles en la imagen: ${missing//$'\n'/, }"
        exit 1
    fi
}

kafka_exec() {
    docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" \
        exec -T "$KAFKA_SERVICE" "$@"
}
