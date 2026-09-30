#!/usr/bin/env bash
# Everything that follows a scrimmage block, in order, in one command (TRAINING_ALGORITHM.md 4.5):
#   tools/post-block.sh <run-id-on-the-VM> <build-label>
# 1 fetch the run; 2 record it into progress/games.csv; 3 refit the ladder ratings (ELO.md, elo.png);
# 4 re-tier the roster in BENCHMARK.md; 5 study the block (study.tsv, nav.tsv; every opponent
# since PROMPTS 45); 6 correlation and onset tables and chart (progress/ONSET.md, onset-ladder.png), and the onset table over
# every block of the build (progress/ONSET-merged.md).
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN="${1:?run id}"; LABEL="${2:?build label, e.g. g_iter1}"
[ -f "$REPO/gauntlet/$RUN/summary.txt" ] || "$REPO/tools/vm-collect.sh" "$RUN" | tail -3
"$REPO/tools/scrim-record.py" "$REPO/gauntlet/$RUN" --label "$LABEL"
"$REPO/tools/elo.py" --quiet
PY="$REPO/tools/.venv/bin/python3"; [ -x "$PY" ] || PY=python3
"$PY" "$REPO/tools/field-score.py" | tail -7 || true   # PROMPTS 28-30: the field-score projection chart, refreshed with every block
"$REPO/tools/bench-roster.py"
"$REPO/tools/scrim-study.sh" "$REPO/gauntlet/$RUN" 2>&1 | grep -v '^  studied'
"$REPO/tools/correlate.py" "$REPO/gauntlet/$RUN" --round 200 || true
"$REPO/tools/onset.py" "$REPO/gauntlet/$RUN" --md "$REPO/progress/ONSET.md" --plot "$REPO/progress/onset-ladder.png" | tail -25 || true
"$REPO/tools/onset-merged.sh" "$LABEL" | tail -1 || true   # the same table over every block of this build (noise floor ~0.07 at 800 games)
head -12 "$REPO/progress/ELO.md"
echo "done: $RUN recorded as us:$LABEL; ladder, roster, study and onset regenerated"
