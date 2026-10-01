#!/usr/bin/env bash
# Basic-capability census over the replays of one or more run dirs (research/TACTIC_LEVELS.md).
#   tools/capability-census.sh <out.csv> <run-dir>...      (P=parallel dumps, default 3; nice 19)
# One row per team per game from `replay-dump.sh --capabilities`, plus file, opp, us (1 = our side).
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT="${1:?out.csv}"; shift
"$REPO/tools/replay-dump.sh" "$REPO/test/fixtures/example-DefaultSmall-s1.bc24" --capabilities > /dev/null   # compile once
one () {
  f="$1"; b=$(basename "$f" .bc24); opp=${b%%__*}; side=${b##*bot}
  nice -n 19 "$REPO/tools/replay-dump.sh" "$f" --capabilities 2>/dev/null | tail -n +2 | awk -F, -v f="$f" -v o="$opp" -v s="$side" '{print f","o","($1==s?1:0)","$0}'
}
export -f one; export REPO
{ echo "file,opp,us,$("$REPO/tools/replay-dump.sh" "$REPO/test/fixtures/example-DefaultSmall-s1.bc24" --capabilities | head -1)"
  for d in "$@"; do ls "$d"/losses/*.bc24 "$d"/replays/*.bc24 2>/dev/null; done | xargs -P "${P:-3}" -I{} bash -c 'one {}'; } > "$OUT"
echo "census: $(( $(wc -l < "$OUT") - 1 )) rows -> $OUT"
