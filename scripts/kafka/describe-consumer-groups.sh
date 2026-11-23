#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/common.sh"

kafka_init

for group_name in sanosysalvos-coincidencias sanosysalvos-alertas; do
    kafka_info "Estado y offsets de $group_name"
    kafka_exec kafka-consumer-groups \
        --bootstrap-server "$BOOTSTRAP_SERVER" \
        --describe \
        --group "$group_name" || \
        kafka_error "No fue posible describir el grupo $group_name"
done
