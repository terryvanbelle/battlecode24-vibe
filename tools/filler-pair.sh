#!/usr/bin/env bash
# Idle filler (owner prompt 73): the incumbent and the candidate stack play the SAME fresh band cells (one seed), so
# filler time accumulates the paired, power-sized test the stack needs before it can replace the incumbent.
#   tools/filler-pair.sh <control> <candidate> [N]      (run on the VM by tools/vm-queue.sh as queue/filler.job)
# Runs are tagged fill<seed> (gauntlet/<stamp>-scrim-<bot>-fill<seed>); both get a capability census
# (gauntlet/fillcensus-<bot>-<seed>.csv). tools/collect-fillers.sh (driver) records them in the ladder and tallies pairs.
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; cd "$REPO"
CTL="${1:?control}"; CAND="${2:?candidate}"; N="${3:-40}"; S="$(date +%s)"
for B in "$CTL" "$CAND"; do
  BOT="$B" N="$N" SEED="$S" RUNTAG="fill$S" MAXJOBS=8 GAME_TIMEOUT=900 CLASSES="build/filler-classes" \
    POOL="$(cat tools/band-20261001.txt)" tools/scrim.sh | tail -2
  P=8 tools/capability-census.sh "gauntlet/fillcensus-$B-$S.csv" "$(ls -d gauntlet/*-scrim-"$B"-fill"$S" | tail -1)" | tail -1
done
