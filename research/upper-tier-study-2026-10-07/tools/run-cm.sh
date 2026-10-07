#!/usr/bin/env bash
# The upper-tier study's analyser (Cm.java, 2026-10-07) on a diag-batch, delivery or gauntlet run: one CSV of K/R/Q/T/D/G rows per
# replay, named by a census-style tag <run>__<opponent>__<map>__s<seed>__bot<side>, which t6.py, r3.py and agg3.py parse (their
# tier split takes the opponent from the tag and tools/upper-tier.txt). Kept for the g7kite registration's reads (review 2026-10-07).
#   research/upper-tier-study-2026-10-07/tools/run-cm.sh <out-dir> <replay.bc24> [...]
# Our side is the replay name's __bot<side> (diag-batch and gauntlet names; A without it). Arm and twin go to separate out dirs,
# e.g. for a diag-batch block:
#   T=research/upper-tier-study-2026-10-07/tools
#   $T/run-cm.sh ~/cm/arm diag/<tag>/g7kite-vs-*.bc24; $T/run-cm.sh ~/cm/twin diag/<tag>/g_iter7-vs-*.bc24
#   python3 $T/t6.py proj ~/cm/arm     T rows per decision (side 0 = us): eD20 = enemies within dist2 20 of the deciding robot
#                                      dead within 20 rounds, k20 its kills within 20 rounds, d3/d10/d20 its own death, str2 ...
#   python3 $T/r3.py ~/cm/arm          the randomized tie-break test (R rows)
#   python3 $T/agg3.py ~/cm/arm g      G rows: stand hits on recharging victims / hits and per game, step-in hits, us and them
# Dumps run on the VM (CLAUDE.md rule 1), CM_JOBS at a time (default 3; the study's limit).
set -euo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"; REPO="$(cd "$HERE/../../.." && pwd)"
source "$REPO/tools/lib.sh"
OUT="${1:?usage: run-cm.sh <out-dir> <replay.bc24> [...]}"; shift; mkdir -p "$OUT"
CP="$(engine_cp)"; CLS="$REPO/build/cm-$(sha1sum "$HERE/Cm.java" | cut -c1-12)"
if [ ! -f "$CLS/cm/Cm.class" ]; then mkdir -p "$CLS"; javac -nowarn -d "$CLS" -cp "$CP" "$HERE/Cm.java"; fi
export CP CLS OUT
for f in "$@"; do
  b="$(basename "$f" .bc24)"; side=A
  if [[ "$b" =~ __bot([AB])$ ]]; then side="${BASH_REMATCH[1]}"; b="${b%__bot?}"; fi
  if [[ "$b" =~ ^([^-]+)-vs-(.+)-([^-]+)-s([0-9]+)$ ]]; then   # diag-batch: <bot>-vs-<opp>-<map>-s<seed>
    tag="${BASH_REMATCH[1]}__${BASH_REMATCH[2]}__${BASH_REMATCH[3]}__s${BASH_REMATCH[4]}"
  else tag="$(basename "$(dirname "$(dirname "$f")")")__$b"; fi   # gauntlet: <run>/{replays,losses}/<opp>__<map>__s<seed>
  printf '%s %s %s__bot%s\n' "$f" "$side" "$tag" "$side"
done | xargs -P "${CM_JOBS:-3}" -L 1 bash -c 'java -Xmx700m -cp "$CLS:$CP" cm.Cm "$0" "$1" "$2" > "$OUT/$2.csv"'
echo "run-cm: $(ls "$OUT" | grep -c '\.csv$') CSVs in $OUT"
