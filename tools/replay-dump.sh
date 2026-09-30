#!/usr/bin/env bash
# Replay (.bc24) -> text. Compiles tools/replaydump/ReplayDump.java on demand (cached by source
# hash) against the staged engine jar.   tools/replay-dump.sh <replay.bc24> [flags]   (flags: see ReplayDump.java)
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source "$REPO/tools/lib.sh"
CP="$(engine_cp)"
# BENCHMARK.md rule 2: a locked bot's games may not be reviewed. Enforced here, not left to the reader.
# PROMPTS 45 (2026-09-26): games against any bot may be reviewed; the tier check no longer gates the dump.
SHA="$(sha1sum "$REPO/tools/replaydump/ReplayDump.java" | cut -c1-12)"
OUT="$REPO/build/replaydump-$SHA"
if [ ! -f "$OUT/replaydump/ReplayDump.class" ]; then mkdir -p "$OUT"; javac -nowarn -d "$OUT" -cp "$CP" "$REPO/tools/replaydump/ReplayDump.java"; fi
exec java -Xmx"${DUMP_XMX:-512m}" -cp "$OUT:$CP" replaydump.ReplayDump "$@"
