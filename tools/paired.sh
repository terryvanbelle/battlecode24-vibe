#!/usr/bin/env bash
# Paired replay of mirror cells. Every cell (map, side, seed) is played twice -- BOT against REF and REF against
# itself, the same seed -- so a candidate that changes nothing in a game plays the identical game and the pair is
# concordant; only the discordant pairs (candidate won where the control lost, or the reverse) say anything about
# the change. Fixed seeds make the engine deterministic (2026-09-25: gate 40c's losses replayed identical to the
# control down to the ring height), so a mirror's concordant games are coin flips the SPRT should not count.
#   tools/paired.sh cells.txt            # lines: map side seed   (side = BOT's side; a gate's results.csv gives them)
#   BOT=bot REF=g_iter7 MAXJOBS=6 LOGTAG='@uncork' KEEP_LOGS=0 CLASSES=build/paired-classes TAG=paired-40c
#   OPP=arch_rush: both games of a cell are played against this third build instead (BOT vs OPP, then REF vs OPP, same
#   map, side and seed) -- a paired gate against one of our own archetypes, for a change that only fires against it
#   Puppet cells (inherited from the predecessor project's PROMPTS 59-60; tools/puppet.sh is not in this repo and no puppet
#   was used here): lines `map side seed fixture cutoff`, with OPP=pup_<base>;
#   both games of such a cell run with GAME_CONFIG=<fixture> and -Dbc.testing.pup.cutoff=<cutoff>
# Writes gauntlet/<stamp>-<TAG>/results.csv: map,side,seed,cand,ctrl,cand_rounds,ctrl_rounds,tags
# (cand/ctrl = win|loss from BOT's side; tags = LOGTAG lines the BOT side printed in the candidate game) and prints
# the pair table: concordant wins/losses, discordant each way, and the sign-test p.
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"; source "$REPO/tools/lib.sh"
CELLS="${1:?cells file: map side seed}"
BOT="${BOT:-bot}"; REF="${REF:-g_iter7}"; OPP="${OPP:-}"; MAXJOBS="${MAXJOBS:-6}"; LOGTAG="${LOGTAG:-@uncork}"; KEEP_LOGS="${KEEP_LOGS:-0}"
CLASSES="${CLASSES:-$REPO/build/paired-classes}"; TAG="${TAG:-paired-$BOT-vs-$REF}"
OUT="$REPO/gauntlet/$(date +%Y%m%d-%H%M%S)-$TAG"; mkdir -p "$OUT/games"
[ -d "$CLASSES/$BOT" ] && [ -d "$CLASSES/$REF" ] && { [ -z "${OPP:-}" ] || [ -d "$CLASSES/$OPP" ]; } && [ "${SKIP_COMPILE:-0}" = 1 ] || { rm -rf "$CLASSES"; compile_src "$REPO/src" "$CLASSES" >&2; }
export REPO CLASSES BOT REF OPP LOGTAG KEEP_LOGS OUT
one_cell () {
  local MAP="$1" SIDE="$2" SEED="$3" FIX="${4:-}" CUT="${5:-}" A B LOG R W RC TAGS CLOG CR CW CRC
  source "$REPO/tools/lib.sh"; team_url () { echo "$CLASSES"; }
  if [ -n "$FIX" ]; then export GAME_CONFIG="$FIX" GAME_OPTS="-Dbc.testing.pup.cutoff=${CUT:?cell $MAP $SIDE $SEED: a fixture needs a cutoff}"; fi
  local O="${OPP:-$REF}"
  if [ "$SIDE" = A ]; then A="$BOT"; B="$O"; else A="$O"; B="$BOT"; fi
  local base="$OUT/games/$MAP-$SIDE-$SEED${CUT:+-c$CUT}"
  LOG="$(GAME_SEED="$SEED" run_game "$A" "$B" "$MAP" "$base-cand.bc24" -Dbc.server.robot-player-to-system-out=true 2>&1 || true)"
  TAGS=$(printf '%s\n' "$LOG" | grep -ac "^\[$SIDE:.*$LOGTAG" || true)
  [ "$KEEP_LOGS" = 1 ] && printf '%s\n' "$LOG" > "$base-cand.log"
  read -r _ W RC _ <<<"$(parse_result "$LOG")"; R=$([ "$W" = "$SIDE" ] && echo win || echo loss)
  local CA="$REF" CB="$REF"; [ -n "$OPP" ] && { if [ "$SIDE" = A ]; then CB="$OPP"; else CA="$OPP"; fi; }
  CLOG="$(GAME_SEED="$SEED" run_game "$CA" "$CB" "$MAP" "$base-ctrl.bc24" 2>&1 || true)"
  read -r _ CW CRC _ <<<"$(parse_result "$CLOG")"; CR=$([ "$CW" = "$SIDE" ] && echo win || echo loss)
  [ "$R" = "$CR" ] && [ "$RC" = "$CRC" ] && rm -f "$base-cand.bc24" "$base-ctrl.bc24"   # a concordant pair: nothing to review
  echo "$MAP${CUT:+@c$CUT},$SIDE,$SEED,$R,$CR,$RC,$CRC,$TAGS"
}
export -f one_cell
echo "map,side,seed,cand,ctrl,cand_rounds,ctrl_rounds,tags" > "$OUT/results.csv"
grep -v '^\s*$' "$CELLS" | xargs -P "$MAXJOBS" -L 1 bash -c 'one_cell "$@"' _ >> "$OUT/results.csv"
python3 - "$OUT/results.csv" <<'PY'
import sys, csv, math
rows = list(csv.DictReader(open(sys.argv[1])))
ww = sum(r['cand'] == 'win' and r['ctrl'] == 'win' for r in rows); ll = sum(r['cand'] == 'loss' and r['ctrl'] == 'loss' for r in rows)
wl = sum(r['cand'] == 'win' and r['ctrl'] == 'loss' for r in rows); lw = sum(r['cand'] == 'loss' and r['ctrl'] == 'win' for r in rows)
ident = sum(r['cand_rounds'] == r['ctrl_rounds'] for r in rows); fired = sum(int(r['tags']) > 0 for r in rows)
n = wl + lw; p = sum(math.comb(n, k) for k in range(0, min(wl, lw) + 1)) / 2 ** n * 2 if n else 1.0
print(f"pairs {len(rows)}: concordant win {ww} loss {ll}; discordant cand-win/ctrl-loss {wl}, cand-loss/ctrl-win {lw}; "
      f"sign test p={min(p,1):.2f}; same end round {ident}; mechanism fired in {fired}")
PY
echo "wrote $OUT/"
