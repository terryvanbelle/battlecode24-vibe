#!/usr/bin/env bash
# Push what a run needs to battlecode-dev (tar over ssh; the VM has no rsync).
#   tools/vm-sync.sh          # repo tree (src tools test + engine if missing) and, once, JDK 8 and benchmark classes
#   FULL=1 tools/vm-sync.sh   # re-push engine and benchmark classes too
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; source "$REPO/tools/vm.sh"; ensure_vm
gssh "mkdir -p ~/$REMOTE_REPO ~/projects/vibe/bc24-benchmarks ~/jdk"
if ! gssh "test -x ~/jdk/jdk8u504-b01/bin/java"; then
  echo "pushing JDK 8 ..."; tar -C "$HOME/jdk" -czf - jdk8u504-b01 | gssh "tar -C ~/jdk -xzf -"
fi
if [ "${SKIP_BENCH:-0}" != 1 ] && { [ "${FULL:-0}" = 1 ] || ! gssh "test -f ~/projects/vibe/bc24-benchmarks/manifest.tsv"; }; then
  echo "pushing benchmark classes + manifest ..."
  tar -C "$HOME/projects/vibe/bc24-benchmarks" -czf - _classes manifest.tsv | gssh "tar -C ~/projects/vibe/bc24-benchmarks -xzf -"
fi
if [ "${FULL:-0}" = 1 ] || ! gssh "test -f ~/$REMOTE_REPO/engine/engine.jar"; then
  echo "pushing engine ..."; tar -C "$REPO" -czf - engine | gssh "tar -C ~/$REMOTE_REPO -xzf -"
fi
echo "pushing repo tree ..."
# progress/ travels too: tools/scrim.sh picks its pool with tools/elo.py, which reads
# progress/games.csv, and silently falls back to the fixed 8-bot tools/roster.txt when that file has
# fewer than 40 rows. It was never synced, so every ladder block since 2026-09-17 took the fallback
# and the "challenge the bots just above us" rule never actually ran on the VM (found 2026-09-20).
# Unpack into a staging directory and swap each tree in with a rename: the old `rm -rf src tools ... && tar -x` left a
# window with no tools/ at all, and a gate running beside the sync lost ten cells of a batch to it (gate81, 2026-09-26:
# "tools/lib.sh: No such file or directory"). A running script keeps its old inode; a rename has no missing window.
# Puppet fixtures (build/puppets/*.properties, PROMPTS 59-60) travel too: they are derived from replays the VM prunes.
# Only the new or changed ones (by sha1; 1-2 MB each), so a sync that changes no fixture costs one sha1sum on the VM.
REMOTE_PUPS="$(gssh "cd ~/$REMOTE_REPO 2>/dev/null && sha1sum build/puppets/*.properties 2>/dev/null" || true)"
PUPS=""
for F in $(cd "$REPO" && ls build/puppets/*.properties 2>/dev/null || true); do
  printf '%s\n' "$REMOTE_PUPS" | grep -qxF "$(cd "$REPO" && sha1sum "$F")" || PUPS="$PUPS $F"
done
[ -z "$PUPS" ] || echo "pushing puppet fixtures:$PUPS"
tar -C "$REPO" --exclude='tools/.venv' --exclude='__pycache__' -czf - src tools test progress BENCHMARK.md | gssh "cd ~/$REMOTE_REPO && rm -rf .sync.new .sync.old && mkdir -p .sync.new .sync.old && tar -C .sync.new -xzf - && for d in src tools test progress; do [ -e \$d ] && mv \$d .sync.old/\$d; mv .sync.new/\$d \$d; done && mv .sync.new/BENCHMARK.md BENCHMARK.md && mkdir -p build/puppets && if [ -d .sync.new/build/puppets ]; then cp -f .sync.new/build/puppets/*.properties build/puppets/; fi && rm -rf .sync.new .sync.old && mkdir -p gauntlet matches build"
echo "synced to $USER_NAME@$IP:~/$REMOTE_REPO"
