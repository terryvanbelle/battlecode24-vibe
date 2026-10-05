#!/usr/bin/env bash
# One command for the bot and the apparatus: compile src/bot + test/bot against the engine jar and run every
# *Test main (no JUnit), then the python tool tests (synthetic inputs + the committed fixture replay).
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; source "$REPO/tools/lib.sh"
OUT="$REPO/build/tests"; mkdir -p "$OUT"
javac -nowarn -encoding UTF-8 -d "$OUT" -cp "$(engine_cp)" "$REPO"/src/bot/*.java "$REPO"/test/bot/*.java
for t in "$REPO"/test/bot/*Test.java; do java -cp "$OUT:$(engine_cp)" "bot.$(basename "$t" .java)"; done
# C.CONTACT is off in src/bot, so javac drops its hooks there: ContactTest runs again on a copy of src/bot with the switch on,
# which pins the hooks arm g4contact plays (review 2026-10-05: a moved or broken hook passed every test)
CSRC="$REPO/build/tests-contact/src"; COUT="$REPO/build/tests-contact/classes"; rm -rf "$CSRC" "$COUT"; mkdir -p "$CSRC" "$COUT"
for f in "$REPO"/src/bot/*.java; do sed 's/boolean CONTACT = false;/boolean CONTACT = true;/' "$f" > "$CSRC/$(basename "$f")"; done
grep -q 'boolean CONTACT = true;' "$CSRC/C.java" || { echo "unit-tests: could not switch C.CONTACT on in the copy" >&2; exit 1; }
javac -nowarn -encoding UTF-8 -d "$COUT" -cp "$(engine_cp)" "$CSRC"/*.java "$REPO"/test/bot/BotTest.java "$REPO"/test/bot/ContactTest.java
java -cp "$COUT:$(engine_cp)" bot.ContactTest
# no dead code in the bot (owner prompt 127: the symmetry check existed but was never called); reserved slot constants
# for the design's later stages are allowed by name
python3 "$REPO/tools/deadcode.py" "$REPO/src/bot" --allow AUC,AUC_SLOTS | tail -1
if [ "${SKIP_METRIC_TESTS:-0}" != 1 ] && [ -x "$REPO/tools/test_metrics.py" ]; then "$REPO/tools/test_metrics.py" | tail -1; fi
"$REPO/tools/test_tools.py" | tail -3
