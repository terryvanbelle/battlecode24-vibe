#!/usr/bin/env bash
# Step 5(a) diagnostic games in parallel (meant for the VM queue; the driver plays one game at a time).
#   tools/diag-batch.sh <tag> <bot>:<opponent>:<map>:<seed>[:<side>] [...]      (side A or B, default A: our bot's team)
# The opponent is one of our builds (src/<name>) or, since owner PROMPTS 178, an external bot from the benchmark manifest
# on a chosen map and side; an external bot's output is silenced and these games never enter the ladder (CLAUDE.md
# rule 4). Writes diag/<tag>/<bot>-vs-<opp>-<map>-s<seed>__bot<side>.bc24 and diag/<tag>/summary.txt (--capabilities rows
# + upgrade orders). DRY=1 prints the game commands only.
set -uo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; cd "$REPO"
TAG="${1:?tag}"; shift; [ "${DRY:-0}" = 1 ] || mkdir -p "diag/$TAG"
PAR="${PAR:-8}"   # at most PAR games at once (2026-10-02: 24 at once on 8 vCPUs starved sshd for minutes)
MANIFEST="${MANIFEST:-$HOME/projects/vibe/bc24-benchmarks/manifest.tsv}"
for spec in "$@"; do
  IFS=: read -r bot opp map seed side <<< "$spec"
  side="${side:-A}"
  case "$side" in A|B) ;; *) echo "diag-batch: side must be A or B in $spec" >&2; exit 2;; esac
  ext=0
  if ! [ -d "src/$opp" ]; then
    if [ -f "$MANIFEST" ] && awk -F'\t' -v n="$opp" '$1==n{f=1} END{exit !f}' "$MANIFEST"; then ext=1
    else echo "diag-batch: $opp is neither one of our builds nor in $MANIFEST; refusing" >&2; exit 2; fi
  fi
  out="diag/$TAG/$bot-vs-$opp-$map-s${seed}__bot${side}"   # opponent in the name: two opponents on one map/seed overwrote each other (2026-10-03)
  if [ "$side" = A ]; then ta="$bot"; tb="$opp"; quiet="-Dbc.engine.silence-b=true"; else ta="$opp"; tb="$bot"; quiet="-Dbc.engine.silence-a=true"; fi
  [ "$ext" = 1 ] || quiet=""                            # an external bot's output is theirs: silenced
  if [ "${DRY:-0}" = 1 ]; then echo "tools/run-dev.sh $ta $tb $map $out.bc24 -Dbc.game.seed=$seed $quiet"; continue; fi
  while [ "$(jobs -rp | wc -l)" -ge "$PAR" ]; do sleep 2; done
  tools/run-dev.sh "$ta" "$tb" "$map" "$out.bc24" "-Dbc.game.seed=$seed" $quiet > "$out.out" 2>&1 &
done
[ "${DRY:-0}" = 1 ] && exit 0
wait
{ for f in diag/"$TAG"/*.bc24; do
    echo "== $(basename "$f" .bc24)"
    tools/replay-dump.sh "$f" --capabilities | cut -d, -f1-5,12-15,25,31-34
    tools/replay-dump.sh "$f" --survey | cut -d, -f2,13,14,16,18,24,28 | tail -2
  done; } > "diag/$TAG/summary.txt" 2>&1
echo "diag-batch: $(ls diag/"$TAG"/*.bc24 | wc -l) games -> diag/$TAG/summary.txt"
