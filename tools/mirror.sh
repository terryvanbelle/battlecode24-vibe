#!/usr/bin/env bash
# Mirror match under SPRT: our candidate against our incumbent on random maps and random sides.
# Our own builds, so the contest rule on external bots does not apply -- but random maps, because a
# fixed 12-map set answers a different question than the ladder does.
#   BOT=bot REF=g_iter4 N=200 BATCH=16 tools/mirror.sh
#   W0=11 L0=5 ... resumes the record from an interrupted run (its later, partial batch must be voided by hand)
#   PAIRED=1 (the default since 2026-09-25): every cell is also played as REF vs REF on the same seed (tools/paired.sh);
#   the engine is deterministic under a fixed seed, so a pair is concordant wherever the change did not alter the
#   game, and the SPRT counts discordant pairs only (gate 40c: 34-46 unpaired, 74 of 80 pairs concordant, 4-2 on the
#   rest). N counts pairs. PAIRED=0 is the old unpaired mirror.
#   OPP=arch_rush (paired only): both builds play that archetype instead of each other -- BOT vs OPP against REF vs OPP
#   on the same map, side and seed (tools/paired.sh); for a change that only fires against that archetype.
# Plays in batches, runs tools/sprt.py after each, stops at ACCEPT or REJECT. Prints the verdict.
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BOT="${BOT:-bot}"; REF="${REF:-g_iter4}"; N="${N:-200}"; BATCH="${BATCH:-16}"; MAXJOBS="${MAXJOBS:-6}"
SEED="${SEED:-$(date +%s)}"; TAG="${TAG:-mirror-$BOT-vs-$REF}"
# Own class tree by default: a mirror often runs beside a scrimmage block, and a shared
# build/classes means whichever starts second rebuilds the bot the first one is playing.
CLASSES="${CLASSES:-$REPO/build/mirror-classes}"; PAIRED="${PAIRED:-1}"
MAPS="$(tr '\n' ' ' < "$REPO/tools/bc24-maps.txt")"
W="${W0:-0}"; L="${L0:-0}"; i=0; P=0; CW=0; CL=0; FIRED=0   # W0/L0: fold in a batch already played, to resume after an interruption without replaying it
while { [ "$PAIRED" = 1 ] && [ "$P" -lt "$N" ]; } || { [ "$PAIRED" != 1 ] && [ $((W + L)) -lt "$N" ]; }; do
  i=$((i + 1))
  CELLS="$(mktemp)"
  python3 - "$BATCH" "$((SEED + i))" "$REF" "$MAPS" "$PAIRED" > "$CELLS" <<'PY'
import random, sys
n = int(sys.argv[1]); random.seed(int(sys.argv[2])); ref = sys.argv[3]; maps = sys.argv[4].split(); paired = sys.argv[5] == "1"
for _ in range(n):
    m, s, seed = random.choice(maps), random.choice("AB"), random.randrange(1, 2**31)
    print(m, s, seed) if paired else print(ref, m, s, seed)
PY
  if [ "$PAIRED" = 1 ]; then
    OUT=$(BOT="$BOT" REF="$REF" CLASSES="$CLASSES" TAG="$TAG-b$i" MAXJOBS="$MAXJOBS" LOGTAG="${LOGTAG:-@}" SKIP_COMPILE=$([ $i -gt 1 ] && echo 1 || echo 0) "$REPO/tools/paired.sh" "$CELLS" | sed -n 's#^wrote \(.*\)/$#\1#p')
    rm -f "$CELLS"
    read -r bw bl bcw bcl bf <<<"$(awk -F, 'NR>1{ if($4=="win"&&$5=="loss")w++; else if($4=="loss"&&$5=="win")l++; else if($4=="win")cw++; else cl++; if($8>0)f++ } END{print w+0, l+0, cw+0, cl+0, f+0}' "$OUT/results.csv")"
    P=$((P + bw + bl + bcw + bcl)); CW=$((CW + bcw)); CL=$((CL + bcl)); FIRED=$((FIRED + bf))
  else
    # compile once: every batch after the first reuses build/classes (was ~25 s per batch)
    OUT=$(CELLS="$CELLS" CLASSES="$CLASSES" BOT="$BOT" TAG="$TAG-b$i" MAXJOBS="$MAXJOBS" KEEP_ALL="${KEEP_ALL:-0}" SKIP_COMPILE=$([ $i -gt 1 ] && echo 1 || echo 0) "$REPO/tools/gauntlet.sh" | sed -n 's#^wrote \(.*\)/$#\1#p')
    rm -f "$CELLS"
    bw=$(awk -F, '$6=="win"{n++} END{print n+0}' "$OUT/results.csv"); bl=$(awk -F, '$6=="loss"{n++} END{print n+0}' "$OUT/results.csv")
  fi
  W=$((W + bw)); L=$((L + bl))
  VERDICT="$(python3 "$REPO/tools/sprt.py" "$W" "$L")"
  if [ "$PAIRED" = 1 ]; then echo "batch $i: discordant +$bw -$bl (concordant $bcw/$bcl, fired $bf)  ==> $P pairs, $VERDICT"; else echo "batch $i: +$bw -$bl  ==> $VERDICT"; fi
  case "$VERDICT" in *ACCEPT) echo "SPRT_ACCEPT $W-$L"; break;; *REJECT) echo "SPRT_REJECT $W-$L"; break;; esac
done
if [ "$PAIRED" = 1 ]; then
  [ "$P" -lt "$N" ] || echo "SPRT_INCONCLUSIVE $W-$L discordant after $P pairs (concordant $CW-$CL, fired in $FIRED)"
else
  [ $((W + L)) -lt "$N" ] || echo "SPRT_INCONCLUSIVE $W-$L after $N games"
fi
