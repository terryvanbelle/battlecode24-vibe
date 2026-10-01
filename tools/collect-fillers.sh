#!/usr/bin/env bash
# Driver side of the idle filler (owner prompt 73): fetch every finished filler run from the VM that is not yet in
# progress/games.csv, record it in the ladder under its bot's label, refit the ratings and charts, and print the
# running paired tally of candidate vs control over all filler seeds where both played.
#   tools/collect-fillers.sh [control candidate[,candidate2...]]     (defaults g_iter1 b1v2)
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; cd "$REPO"; source tools/vm.sh; ensure_vm
CTL="${1:-g_iter1}"; CAND="${2:-b1v2}"
RUNS="$(gssh "cd ~/$REMOTE_REPO/gauntlet && for d in *-scrim-*-fill*; do [ -f \$d/summary.txt ] && echo \$d; done" 2>/dev/null || true)"
new=0
for r in $RUNS; do
  if grep -q "^$r," progress/games.csv 2>/dev/null; then continue; fi
  mkdir -p "gauntlet/$r"
  gssh "cat ~/$REMOTE_REPO/gauntlet/$r/results.csv" > "gauntlet/$r/results.csv"
  gssh "cat ~/$REMOTE_REPO/gauntlet/$r/summary.txt" > "gauntlet/$r/summary.txt"
  label="$(echo "$r" | sed -E 's/^[0-9]{8}-[0-9]{6}-scrim-//; s/-fill[0-9]+$//')"
  tools/scrim-record.py "gauntlet/$r" --label "$label" | tail -1; new=$((new + 1))
  s="$(echo "$r" | sed -E 's/.*-fill([0-9]+)$/\1/')"
  gssh "cat ~/$REMOTE_REPO/gauntlet/fillcensus-$label-$s.csv" > "research/fill/fillcensus-$label-$s.csv" 2>/dev/null || true
done
if [ "$new" -gt 0 ]; then tools/elo.py --quiet; tools/.venv/bin/python3 tools/field-score.py >/dev/null 2>&1 || true; tools/progress-chart.py >/dev/null || true; fi
for c in ${CAND//,/ }; do python3 tools/filler-tally.py "$CTL" "$c"; done
echo "collect-fillers: $new new filler run(s) recorded"
