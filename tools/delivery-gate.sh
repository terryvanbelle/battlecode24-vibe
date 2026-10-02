#!/usr/bin/env bash
# Delivery gate (owner prompt 66; research/TACTIC_LEVELS.md TL-1 step 4): before any band test, a 24-game mini-block
# on random band cells must show that the arm actually produces the behaviour it was built for.
#   tools/delivery-gate.sh <arm> "<checks>"        (run on the VM, from the repo root)
# Checks are space-separated `stat:column op value` over OUR rows of the mini-block (census + survey columns):
#   median:crumbs200>=2500   mean:traps200<=6   fire:digs200>0>=0.9   (fire: share of games where column > 0)
#   rel:enemyRegrabs<=0.7   (with BASE=<bot>: arm mean <= 0.7 x base mean on the same cells)
# Writes gauntlet/delivery-<arm>.PASS or .FAIL with the measured values; tools/band-test.sh refuses without PASS.
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; cd "$REPO"
ARM="${1:?arm}"; CHECKS="${2:?checks}"; N="${N:-24}"; SEED="${SEED:-909090}"
rm -f "gauntlet/delivery-$ARM.PASS" "gauntlet/delivery-$ARM.FAIL"
BOT="$ARM" N="$N" SEED="$SEED" MAXJOBS=8 GAME_TIMEOUT=900 CLASSES="build/dg-classes-$ARM" POOL="$(cat tools/band-20261001.txt)" tools/scrim.sh | tail -1
RUN="$(ls -d gauntlet/*-scrim-"$ARM" | tail -1)"
P=7 tools/capability-census.sh "gauntlet/dg-census-$ARM.csv" "$RUN" >/dev/null
MODE=--survey P=7 tools/capability-census.sh "gauntlet/dg-survey-$ARM.csv" "$RUN" >/dev/null
# BASE=<bot>: the stack base this arm is built on; its mini-block on the same seed is played once and cached, and
# rel: checks compare the arm with it cell by cell (2026-10-01: absolute bars against the band-wide control mean were
# too noisy and compared against the wrong build).
BARGS=""
if [ -n "${BASE:-}" ]; then
  BC="gauntlet/dg-census-$BASE-$SEED.csv"; BS="gauntlet/dg-survey-$BASE-$SEED.csv"
  if [ ! -s "$BC" ] || [ ! -s "$BS" ]; then
    BOT="$BASE" N="$N" SEED="$SEED" RUNTAG="dg$SEED" MAXJOBS=8 GAME_TIMEOUT=900 CLASSES="build/dg-classes-$BASE" POOL="$(cat tools/band-20261001.txt)" tools/scrim.sh | tail -1
    BRUN="$(ls -d gauntlet/*-scrim-"$BASE"-dg"$SEED" | tail -1)"
    P=7 tools/capability-census.sh "$BC" "$BRUN" >/dev/null; MODE=--survey P=7 tools/capability-census.sh "$BS" "$BRUN" >/dev/null
  fi
  # a check on a column the cached base census predates (new ReplayDump columns): re-census the cached base run
  for col in $(echo "$CHECKS" | grep -oE ':[A-Za-z0-9]+' | tr -d ':' | sort -u); do
    if ! head -1 "$BC" | tr ',' '\n' | grep -qx "$col" && ! head -1 "$BS" | tr ',' '\n' | grep -qx "$col"; then
      BRUN="$(ls -d gauntlet/*-scrim-"$BASE"-dg"$SEED" | tail -1)"
      echo "delivery-gate: base census lacks $col; re-census $BRUN"
      P=7 tools/capability-census.sh "$BC" "$BRUN" >/dev/null; MODE=--survey P=7 tools/capability-census.sh "$BS" "$BRUN" >/dev/null
      break
    fi
  done
  BARGS="$BC $BS"
fi
python3 tools/delivery-check.py "$ARM" "$CHECKS" "gauntlet/dg-census-$ARM.csv" "gauntlet/dg-survey-$ARM.csv" "$RUN" $BARGS
