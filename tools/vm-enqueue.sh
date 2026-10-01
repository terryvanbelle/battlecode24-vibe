#!/usr/bin/env bash
# Append a job to the VM's standing queue (tools/vm-queue.sh), after syncing the repo tree.
#   tools/vm-enqueue.sh <name> '<command line, run from the repo root on the VM>'
#   FILLER=1 tools/vm-enqueue.sh x '<cmd>'   # replace the idle filler job instead
# Job files are named <UTC stamp>-<name>.job so they run in submission order; the log is gauntlet/<stamp>-<name>.log.
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; source "$REPO/tools/vm.sh"
NAME="${1:?name}"; CMD="${2:?command}"; ensure_vm
for i in 1 2 3 4 5; do SKIP_BENCH=1 "$REPO/tools/vm-sync.sh" >/dev/null 2>&1 && break; sleep 10; done
STAMP="$(date -u +%Y%m%d-%H%M%S)"
if [ "${FILLER:-0}" = 1 ]; then DEST="queue/filler.job"; else DEST="queue/pending/$STAMP-$NAME.job"; fi
printf '%s\n' "$CMD" | gssh "mkdir -p ~/$REMOTE_REPO/queue/pending && cat > ~/$REMOTE_REPO/$DEST.tmp && mv ~/$REMOTE_REPO/$DEST.tmp ~/$REMOTE_REPO/$DEST"
echo "queued $DEST (log: gauntlet/$( [ "${FILLER:-0}" = 1 ] && echo 'filler-<stamp>' || echo "$STAMP-$NAME").log)"
