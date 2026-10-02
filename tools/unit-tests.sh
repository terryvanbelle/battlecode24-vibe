#!/usr/bin/env bash
# One command for the bot and the apparatus: compile src/bot + test/bot against the engine jar and run every
# *Test main (no JUnit), then the python tool tests (synthetic inputs + the committed fixture replay).
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; source "$REPO/tools/lib.sh"
OUT="$REPO/build/tests"; mkdir -p "$OUT"
javac -nowarn -encoding UTF-8 -d "$OUT" -cp "$(engine_cp)" "$REPO"/src/bot/*.java "$REPO"/test/bot/*.java
for t in "$REPO"/test/bot/*Test.java; do java -cp "$OUT:$(engine_cp)" "bot.$(basename "$t" .java)"; done
# no dead code in the bot (owner prompt 127: the symmetry check existed but was never called); reserved slot constants
# for the design's later stages are allowed by name
python3 "$REPO/tools/deadcode.py" "$REPO/src/bot" --allow AUC,AUC_SLOTS,OWN_C | tail -1
if [ "${SKIP_METRIC_TESTS:-0}" != 1 ] && [ -x "$REPO/tools/test_metrics.py" ]; then "$REPO/tools/test_metrics.py" | tail -1; fi
"$REPO/tools/test_tools.py" | tail -3
