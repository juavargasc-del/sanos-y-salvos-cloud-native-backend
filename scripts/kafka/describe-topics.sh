#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/common.sh"

kafka_init

for topic_name in mascotas-eventos mascotas-alertas; do
    kafka_info "Descripcion de $topic_name"
    kafka_exec kafka-topics \
        --bootstrap-server "$BOOTSTRAP_SERVER" \
        --describe \
        --topic "$topic_name"
done
