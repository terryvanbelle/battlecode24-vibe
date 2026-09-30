#!/usr/bin/env bash
# Compile src/bot + test/bot against the engine jar and run every *Test main. No JUnit needed.
# Also runs the metrics-pipeline tests, so one command checks both the bot and the analysis.
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; source "$REPO/tools/lib.sh"
OUT="$REPO/build/tests"; rm -rf "$OUT"; mkdir -p "$OUT"
javac -nowarn -d "$OUT" -cp "$(engine_cp)" "$REPO"/src/bot/*.java "$REPO"/test/bot/*.java
for t in "$REPO"/test/bot/*Test.java; do java -cp "$OUT:$(engine_cp)" "bot.$(basename "$t" .java)"; done
# the puppet's fixture contract (PROMPTS 59-60): src/puppet decodes test/puppet/golden.properties; our auth's golden value
POUT="$REPO/build/tests-puppet"; rm -rf "$POUT"; mkdir -p "$POUT"
javac -nowarn -d "$POUT" -cp "$(engine_cp)" "$REPO"/src/puppet/*.java "$REPO"/src/g_iter13/*.java "$REPO"/test/puppet/*.java
java -cp "$POUT:$(engine_cp)" puppet.PuppetTest "$REPO/test/puppet/golden.properties"
# metrics/analysis pipeline (user rule, 2026-09-19, PROMPTS 58: run these on every script change)
if [ "${SKIP_METRIC_TESTS:-0}" != 1 ] && [ -x "$REPO/tools/test_metrics.py" ]; then "$REPO/tools/test_metrics.py" | tail -1; fi
# tool tests (python, synthetic inputs)
if [ -x "$REPO/tools/test_tools.py" ]; then "$REPO/tools/test_tools.py" | tail -1; fi
