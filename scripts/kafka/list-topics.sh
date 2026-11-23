#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/common.sh"

kafka_init
kafka_info "Topicos disponibles"
kafka_exec kafka-topics \
    --bootstrap-server "$BOOTSTRAP_SERVER" \
    --list
