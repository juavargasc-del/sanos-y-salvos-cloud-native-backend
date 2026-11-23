#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/common.sh"

kafka_init

printf '\n%s=== Kafka operacional ===%s\n' "$GREEN" "$RESET"

"$SCRIPT_DIR/list-topics.sh"
"$SCRIPT_DIR/describe-topics.sh"
"$SCRIPT_DIR/describe-topic-configs.sh"
"$SCRIPT_DIR/list-consumer-groups.sh"
"$SCRIPT_DIR/describe-consumer-groups.sh"
