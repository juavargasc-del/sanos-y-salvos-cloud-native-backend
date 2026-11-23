#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/common.sh"

kafka_init
kafka_info "Grupos de consumidores disponibles"
kafka_exec kafka-consumer-groups \
    --bootstrap-server "$BOOTSTRAP_SERVER" \
    --list
