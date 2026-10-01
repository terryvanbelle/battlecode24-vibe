#!/usr/bin/env bash
# The VM's standing job runner (owner question 2026-10-01: the VM idled ~29% of its uptime between runs).
# Runs ~/projects/vibe/2024/queue/pending/*.job in name order, one at a time, each as a bash script from the repo
# root with its log in gauntlet/<job>.log; done jobs move to queue/done. When nothing is pending it runs
# queue/filler.job (if present) once per idle period, so the VM never sits empty. One runner only (flock).
#   start (from the driver): tools/vm-run.sh queue 'tools/vm-queue.sh'   (vm-run.sh setsid's it)
#   enqueue: tools/vm-enqueue.sh <name> '<command>'
# Runs from a private copy: vm-sync.sh replaces tools/ under a running script.
if [ -z "${BC24_QREEXEC:-}" ]; then
  _self="/tmp/.vm-queue.$$"; cat "${BASH_SOURCE[0]}" > "$_self" || exit 1
  BC24_QREEXEC="$_self" exec bash "$_self" "$@"
fi
rm -f "$BC24_QREEXEC"
REPO="$HOME/projects/vibe/2024"; Q="$REPO/queue"; mkdir -p "$Q/pending" "$Q/running" "$Q/done" "$REPO/gauntlet"
exec 9>"$Q/.lock"; flock -n 9 || { echo "another queue runner holds $Q/.lock"; exit 0; }
cd "$REPO"
while true; do
  [ -f "$Q/STOP" ] && { echo "$(date -u +%FT%TZ) STOP file present: exiting"; rm -f "$Q/STOP"; exit 0; }
  J="$(ls "$Q/pending/"*.job 2>/dev/null | sort | head -1)"
  if [ -n "$J" ]; then
    N="$(basename "$J" .job)"; mv "$J" "$Q/running/$N.job"
    echo "$(date -u +%FT%TZ) start $N"
    bash "$Q/running/$N.job" > "gauntlet/$N.log" 2>&1 < /dev/null
    echo "$(date -u +%FT%TZ) done $N (exit $?)"; mv "$Q/running/$N.job" "$Q/done/$N.job"
  elif [ -f "$Q/filler.job" ]; then
    N="filler-$(date -u +%Y%m%d-%H%M%S)"; echo "$(date -u +%FT%TZ) idle: $N"
    bash "$Q/filler.job" > "gauntlet/$N.log" 2>&1 < /dev/null
  else
    sleep 15
  fi
done
