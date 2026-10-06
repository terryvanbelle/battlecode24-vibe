#!/usr/bin/env bash
# Standing replay prune for the VM (2026-10-02: replays filled the 49 GB disk). When the disk holding gauntlet/ is
# above THRESH percent, delete *.bc24 older than AGE minutes from scrim runs whose results.csv exists, except runs of
# the builds in tools/keep-replays.txt and the delivery-gate base runs (*-dg<seed>), which the gate re-censuses when a
# new census column appears. Filler runs of a kept build keep the newest KEEP_FILLS (default 25, ~1,000 games, ~3 GB) per
# build (2026-10-06: the g_iter5-vs-Gymhgy replays were pruned an hour after they ran, before a premise check could read
# them). Results, summaries, logs and census files are never touched.
#   THRESH=80 AGE=60 tools/vm-prune.sh       (called at the start of every filler; REPO= overrides for tests)
set -uo pipefail
REPO="${REPO:-$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)}"; G="$REPO/gauntlet"
THRESH="${THRESH:-80}"; AGE="${AGE:-60}"
use="$(df -P "$G" | awk 'NR==2 {gsub("%", "", $5); print $5}')"
[ "$use" -gt "$THRESH" ] || { echo "vm-prune: disk ${use}% <= ${THRESH}%: nothing to do"; exit 0; }
keep="$(cat "$REPO/tools/keep-replays.txt" 2>/dev/null)"
# the newest KEEP_FILLS filler runs of each kept build (run names start with a UTC stamp, so name order is age order)
fresh="$(for k in $keep; do ls -d "$G"/*-scrim-"$k"-fill[0-9]* 2>/dev/null | sort | tail -n "${KEEP_FILLS:-25}"; done)"
n=0
for d in "$G"/*/; do
  d="${d%/}"; b="$(basename "$d")"
  case "$b" in *-dg[0-9]*) continue;; *scrim-*) ;; *) continue;; esac
  [ -f "$d/results.csv" ] || continue
  label="$(echo "$b" | sed -E 's/^[0-9]{8}-[0-9]{6}-scrim-//; s/-fill[0-9]+$//')"
  if echo "$keep" | grep -qx "$label" && ! echo "$b" | grep -q -- '-fill'; then continue; fi
  if echo "$fresh" | grep -qx -- "$d"; then continue; fi
  # step-5(a) diagnostic runs (RUNTAG diag-*) are compared with later arms on the same cells: keep them DIAG_AGE minutes
  # (2026-10-04: g_iter4's Gymhgy diagnostic was pruned an hour after it ran, before g4pick could be paired with it)
  a="$AGE"; case "$b" in *-diag-*) a="${DIAG_AGE:-1440}";; esac
  c=$(find "$d" -name '*.bc24' -type f -mmin +"$a" | wc -l)
  [ "$c" -gt 0 ] || continue
  find "$d" -name '*.bc24' -type f -mmin +"$a" -delete; n=$((n + c))
done
echo "vm-prune: disk was ${use}%; deleted $n replays; now $(df -P "$G" | awk 'NR==2 {print $5}')"
