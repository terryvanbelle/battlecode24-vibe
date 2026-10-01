#!/usr/bin/env bash
# Delivery gate (owner prompt 66; research/TACTIC_LEVELS.md TL-1 step 4): before any band test, a 24-game mini-block
# on random band cells must show that the arm actually produces the behaviour it was built for.
#   tools/delivery-gate.sh <arm> "<checks>"        (run on the VM, from the repo root)
# Checks are space-separated `stat:column op value` over OUR rows of the mini-block (census + survey columns):
#   median:crumbs200>=2500   mean:traps200<=6   fire:digs200>0>=0.9   (fire: share of games where column > 0)
# Writes gauntlet/delivery-<arm>.PASS or .FAIL with the measured values; tools/band-test.sh refuses without PASS.
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; cd "$REPO"
ARM="${1:?arm}"; CHECKS="${2:?checks}"; N="${N:-24}"; SEED="${SEED:-909090}"
rm -f "gauntlet/delivery-$ARM.PASS" "gauntlet/delivery-$ARM.FAIL"
BOT="$ARM" N="$N" SEED="$SEED" MAXJOBS=8 GAME_TIMEOUT=900 CLASSES="build/dg-classes-$ARM" POOL="$(cat tools/band-20261001.txt)" tools/scrim.sh | tail -1
RUN="$(ls -d gauntlet/*-scrim-"$ARM" | tail -1)"
P=7 tools/capability-census.sh "gauntlet/dg-census-$ARM.csv" "$RUN" >/dev/null
MODE=--survey P=7 tools/capability-census.sh "gauntlet/dg-survey-$ARM.csv" "$RUN" >/dev/null
python3 tools/delivery-check.py "$ARM" "$CHECKS" "gauntlet/dg-census-$ARM.csv" "gauntlet/dg-survey-$ARM.csv" "$RUN"
