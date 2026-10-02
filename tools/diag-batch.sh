#!/usr/bin/env bash
# Step 5(a) diagnostic games in parallel (meant for the VM queue; the driver plays one game at a time).
#   tools/diag-batch.sh <tag> <bot>:<opponent>:<map>:<seed> [...]
# Opponents must be our own builds (CLAUDE.md rule 4: no chosen map or side against an external bot).
# Writes diag/<tag>/<bot>-<map>-s<seed>.bc24 and diag/<tag>/summary.txt (--capabilities rows + upgrade orders).
set -uo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; cd "$REPO"
TAG="${1:?tag}"; shift; mkdir -p "diag/$TAG"
for spec in "$@"; do
  IFS=: read -r bot opp map seed <<< "$spec"
  if ! [ -d "src/$opp" ]; then echo "diag-batch: $opp is not one of our builds; refusing" >&2; exit 2; fi
  out="diag/$TAG/$bot-$map-s$seed"
  tools/run-dev.sh "$bot" "$opp" "$map" "$out.bc24" "-Dbc.game.seed=$seed" > "$out.out" 2>&1 &
done
wait
{ for f in diag/"$TAG"/*.bc24; do
    echo "== $(basename "$f" .bc24)"
    tools/replay-dump.sh "$f" --capabilities | cut -d, -f1-5,12-15,25,31-34
    tools/replay-dump.sh "$f" --survey | cut -d, -f2,13,14,16,18,24,28 | tail -2
  done; } > "diag/$TAG/summary.txt" 2>&1
echo "diag-batch: $(ls diag/"$TAG"/*.bc24 | wc -l) games -> diag/$TAG/summary.txt"
