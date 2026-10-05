#!/usr/bin/env bash
# Band test of an arm: 2 seeds x 120 games on the rating band, identical cells to the g_iter1 control, then census and
# survey of its replays. REFUSES unless tools/delivery-gate.sh has written gauntlet/delivery-<arm>.PASS (owner prompt 66).
# Override only with NO_DELIVERY_REASON="<why>" (the reason is written into the run log and must go into TRAINING_LOG).
#   tools/band-test.sh <arm>
#   SEEDS="717171 818181" TAG=-conf tools/band-test.sh <arm>   (confirmation seeds; census-<arm><TAG>.csv)
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; cd "$REPO"
ARM="${1:?arm}"
if [ ! -f "gauntlet/delivery-$ARM.PASS" ]; then
  if [ -z "${NO_DELIVERY_REASON:-}" ]; then
    echo "!! $ARM has no delivery PASS (gauntlet/delivery-$ARM.PASS): run tools/delivery-gate.sh first. Refusing." >&2
    [ -f "gauntlet/delivery-$ARM.FAIL" ] && cat "gauntlet/delivery-$ARM.FAIL" >&2
    exit 5
  fi
  echo "!! band test WITHOUT delivery for $ARM: $NO_DELIVERY_REASON"
fi
# tools/band-seeds.txt: the current incumbent's look 1 / 2 / 3 seeds, one line per look (refreshed at each promotion)
SEEDS="${SEEDS:-$(sed -n 1p tools/band-seeds.txt)}"; TAG="${TAG:-}"; NS="$(echo $SEEDS | wc -w)"
for S in $SEEDS; do BOT="$ARM" N=120 SEED=$S MAXJOBS=8 GAME_TIMEOUT=900 CLASSES="build/bt-classes-$ARM" POOL="$(cat tools/band.txt)" tools/scrim.sh | tail -2; done
RUNS="$(ls -d gauntlet/*-scrim-"$ARM" | tail -"$NS")"
P=7 tools/capability-census.sh "gauntlet/census-$ARM$TAG.csv" $RUNS
MODE=--survey P=7 tools/capability-census.sh "gauntlet/survey-$ARM$TAG.csv" $RUNS
