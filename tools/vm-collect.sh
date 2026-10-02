#!/usr/bin/env bash
# tools/vm-collect.sh <run-id>   -- pull gauntlet/<run-id>/ (results, summary, logs) into local gauntlet/
# Replays (*.bc24) stay on the VM unless REPLAYS=1 (2026-10-02: local copies filled the driver disk twice).
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; source "$REPO/tools/vm.sh"; ensure_vm
RUN="${1:?run id}"; mkdir -p "$REPO/gauntlet"
EXCL="--exclude=*.bc24"; [ "${REPLAYS:-0}" = 1 ] && EXCL=""
gssh "cd ~/$REMOTE_REPO/gauntlet && tar $EXCL -czf - $RUN" | tar -C "$REPO/gauntlet" -xzf -
ls "$REPO/gauntlet/$RUN"; cat "$REPO/gauntlet/$RUN/summary.txt" 2>/dev/null || echo "(no summary yet: run still in flight)"
