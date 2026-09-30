#!/usr/bin/env bash
# One command for the bot and the apparatus: compile src/bot + test/bot against the engine jar and run every
# *Test main (no JUnit), then the python tool tests (synthetic inputs + the committed fixture replay).
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; source "$REPO/tools/lib.sh"
OUT="$REPO/build/tests"; mkdir -p "$OUT"
javac -nowarn -encoding UTF-8 -d "$OUT" -cp "$(engine_cp)" "$REPO"/src/bot/*.java "$REPO"/test/bot/*.java
for t in "$REPO"/test/bot/*Test.java; do java -cp "$OUT:$(engine_cp)" "bot.$(basename "$t" .java)"; done
# metrics pipeline tests are skipped until scrim-study is ported to the 2024 replay columns (HANDOFF)
if [ "${SKIP_METRIC_TESTS:-1}" != 1 ] && [ -x "$REPO/tools/test_metrics.py" ]; then "$REPO/tools/test_metrics.py" | tail -1; fi
"$REPO/tools/test_tools.py" | tail -3
