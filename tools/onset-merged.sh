#!/usr/bin/env bash
# The onset table over EVERY recorded block of one build (its study.tsv files merged), not one block at a time:
# one block's 43 reviewable games have a noise floor of 0.30; twenty blocks' 600 have 0.08 (2026-09-24, PROMPTS 25).
#   tools/onset-merged.sh g_iter6            # -> progress/ONSET-merged.md (and the games it covers)
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD="${1:?build label, e.g. g_iter6}"
# only a submission (>= 200 recorded games, the roster's rule) owns progress/ONSET-merged.md: a candidate's two-block
# ladder arm once overwrote the incumbent's 786-game table
N=$(awk -F, -v b="us:$BUILD" 'NR>1 && ($3==b || $4==b)' "$REPO/progress/games.csv" | wc -l)
if [ "$N" -lt 200 ]; then echo "onset-merged: $BUILD has $N games (< 200): not a submission, table unchanged"; exit 0; fi
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
python3 - "$REPO" "$BUILD" "$TMP/study.tsv" <<'PY'
import csv, glob, sys, os
repo, build, out = sys.argv[1:4]
w = None; n = 0; runs = 0
for f in sorted(glob.glob(os.path.join(repo, 'gauntlet', f'*-scrim-{build}', 'study.tsv'))):
    blk = os.path.basename(os.path.dirname(f))[:15]; runs += 1
    for r in csv.DictReader(open(f), delimiter='\t'):
        if w is None: w = csv.DictWriter(open(out, 'w'), fieldnames=list(r.keys()), delimiter='\t'); w.writeheader()
        r['map'] = r['map'] + '#' + blk   # a pairing repeats across blocks: keep each game its own
        w.writerow(r); n += 1
print(f"{runs} blocks, {n} rows")
if n == 0: sys.exit(1)
PY
"$REPO/tools/onset.py" "$TMP" --md "$REPO/progress/ONSET-merged.md" | tail -1
sed -i "1s/.*/# Which metric starts predicting the result first -- every recorded block of $BUILD merged/" "$REPO/progress/ONSET-merged.md"
