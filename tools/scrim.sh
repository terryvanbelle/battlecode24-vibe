#!/usr/bin/env bash
# Scrimmage block against external bots under contest rules (PROMPTS 25, 2026-09-18):
# the map and the side are drawn at random for every game, opponents rotate (never the same
# one twice in a row, each at most ceil(N/pool) times per block). This is the only way LADDER games against an
# external bot are played (diagnostics may choose maps and sides since PROMPTS 178: tools/diag-batch.sh).
#   BOT=g_iter5 N=48 tools/scrim.sh             # the band: 8 rated bots nearest g_iter5's rating, either side
#   BOT=g_iter5 POOLSIZE=0 EXPLORE=48 N=96 tools/scrim.sh   # calibration: 48 never-played bots, two games each
#   POOL="a.b c.d" N=12 SEED=7 tools/scrim.sh   # explicit pool; SEED for a reproducible draw
# Maps: tools/bc24-maps.txt (the released corpus). Results: gauntlet/<run>-scrim-<BOT>/ ;
# record them with tools/post-block.sh <run> <build> (appends progress/games.csv, refits the ladder).
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BOT="${BOT:-bot}"; N="${N:-24}"; MAXJOBS="${MAXJOBS:-6}"
# The pool (2026-09-24, PROMPTS 8-17): ratings are the batch Bradley-Terry fit of tools/elolib.py, and the
# pool is centred on BOT's own rating (a candidate with no games yet uses the incumbent, our latest g_iterN).
# tools/roster.txt is used until 40 games exist.
# Refuse rather than fall back silently. The roster fallback is only legitimate before the ladder has
# 40 games; a MISSING games.csv means the history did not reach this machine, and quietly substituting a
# different set of opponents is how every block from 2026-09-17 to 2026-09-20 challenged the wrong bots.
if [ -z "${POOL:-}" ]; then
  if [ ! -f "$REPO/progress/games.csv" ]; then
    echo "!! progress/games.csv is missing: the ladder history did not reach this machine." >&2
    echo "!! Run tools/vm-sync.sh, or set POOL=\"a.b c.d\" to choose the opponents explicitly." >&2
    exit 4
  fi
  if [ "$(grep -c . "$REPO/progress/games.csv")" -ge 40 ]; then
    # Pool modes (2026-09-24, PROMPTS 7-10, owner approved): the default is the graded pool -- the POOLSIZE rated bots
    # nearest to us in rating on either side (tools/elo.py --band) plus EXPLORE never-played bots (POOLSIZE=0 EXPLORE=48
    # is a pure calibration block). CHALLENGE=1 is the old climb pool (bots that beat us at least half the time);
    # POOLMODE=above the old "just above us" pool; POOLMODE=established the fixed most-played field.
    if [ "${CHALLENGE:-0}" = 1 ]; then
      POOL="$(python3 "$REPO/tools/elo.py" --challenge "${POOLSIZE:-20}")"
    elif [ "${POOLMODE:-band}" = established ]; then
      POOL="$(python3 "$REPO/tools/elo.py" --established "${POOLSIZE:-8}")"
    elif [ "${POOLMODE:-band}" = above ]; then
      POOL="$(python3 "$REPO/tools/elo.py" --pool "${POOLSIZE:-8}" --explore "${EXPLORE:-0}" --as "$BOT")"
    else
      POOL="$(python3 "$REPO/tools/elo.py" --band "${POOLSIZE:-8}" --explore "${EXPLORE:-0}" --as "$BOT")"
    fi
  else
    echo "ladder history has under 40 games: using tools/roster.txt" >&2
    POOL="$(tr '\n' ' ' < "$REPO/tools/roster.txt")"
  fi
fi
SEED="${SEED:-$(date +%s%N | cut -c1-13)}"
CELLS="$(mktemp)"
# MAPFILE=<file>: draw maps from this list instead of the corpus. Chosen maps against external bots are for diagnostics and
# delivery blocks only (owner PROMPTS 178); such runs are never recorded in the ladder.
MAPFILE="${MAPFILE:-$REPO/tools/bc24-maps.txt}"
python3 - "$N" "$SEED" "$POOL" "$(tr '\n' ' ' < "$MAPFILE")" > "$CELLS" <<'PY'
import random, sys, math
n = int(sys.argv[1]); seed = int(sys.argv[2]); pool = sys.argv[3].split(); maps = sys.argv[4].split()
random.seed(seed)
rs = random.Random(seed ^ 0x5EED5EED)   # engine seeds from a second stream: the cell sequence for a SEED is unchanged
per = math.ceil(n / len(pool)); counts = {o: 0 for o in pool}; last = None
for _ in range(n):
    cands = [o for o in pool if counts[o] < per and o != last] or [o for o in pool if counts[o] < per]
    o = random.choice(cands); counts[o] += 1; last = o
    # 4th field: the engine seed (audit B2, 2026-10-02: without it every build drew its own seed per cell, so identical
    # code flipped 15-29% of cells and paired tests were mostly engine noise). Maps and sides stay random (ladder games, rule 4).
    print(o, random.choice(maps), random.choice("AB"), rs.randrange(1, 2**31 - 1))
PY
if [ "${DRY:-0}" = 1 ]; then cat "$CELLS"; rm -f "$CELLS"; exit 0; fi   # DRY=1: print the cells (with engine seeds) only
echo "scrim block: bot=$BOT n=$N seed=$SEED pool=[$POOL]"
SCRIM=1 KEEP_ALL=1 CELLS="$CELLS" BOT="$BOT" TAG="scrim-$BOT${RUNTAG:+-$RUNTAG}" MAXJOBS="$MAXJOBS" "$REPO/tools/gauntlet.sh"   # KEEP_ALL: wins are studied too (PROMPTS 43)
rm -f "$CELLS"
