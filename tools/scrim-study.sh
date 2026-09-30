#!/usr/bin/env bash
# Mine a block for hypotheses: the games are already paid for; this is where the next candidate comes from.
#   tools/scrim-study.sh gauntlet/<run> [sample]
# Every replay of the block (wins in replays/, losses in losses/): per-team metrics every 50 rounds ->
# <run>/study.tsv (us_/th_ columns from tools/polarity.py STUDY_COLS), navigation summary -> <run>/nav.tsv.
# Replay names are <opponent>__<map>__bot<side>.bc24 (tools/gauntlet.sh). Then tools/scrim-study.py prints medians.
set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUN="${1:?usage: scrim-study.sh <run-dir> [sample]}"; SAMPLE="${2:-0}"
COLS="$(python3 -c "import sys; sys.path.insert(0,'$REPO/tools'); from polarity import STUDY_COLS; print(' '.join(STUDY_COLS))")"
OUT="$RUN/study.tsv"; NAV="$RUN/nav.tsv"
{ printf 'game\topp\tmap\twon\tround'; for s in us th; do for c in $COLS; do printf '\t%s_%s' "$s" "$c"; done; done; printf '\n'; } > "$OUT"
printf 'game\topp\tmap\twon\tside\tus_cov\tth_cov\tus_meanMoves\tth_meanMoves\tus_aba\tth_aba\tus_still\tth_still\n' > "$NAV"
n=0
for f in "$RUN"/losses/*.bc24 "$RUN"/replays/*.bc24; do
  [ -e "$f" ] || continue
  case "$f" in *"/losses/"*) won=0;; *) won=1;; esac
  n=$((n+1)); [ "$SAMPLE" -gt 0 ] && [ "$n" -gt "$SAMPLE" ] && break
  b=$(basename "$f" .bc24); opp=${b%%__*}; rest=${b#*__}; map=${rest%%__*}; side=${b##*bot}
  U=$side; T=$([ "$side" = A ] && echo B || echo A)
  M="$(nice -n 10 "$REPO/tools/replay-dump.sh" "$f" --metrics 50 --navstats 2>/dev/null)" || { echo "  !! skipped $b (replay-dump failed)" >&2; continue; }
  printf '%s\n' "$M" | grep '^nav ' | awk -v g="$b" -v opp="$opp" -v map="$map" -v won="$won" -v U="$U" '
    { t=$2; sub(":","",t); for(i=3;i<=NF;i++){split($i,kv,"="); v=kv[2]; gsub("%","",v); d[t,kv[1]]=v} }
    END { T=(U=="A")?"B":"A"; printf "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n", g,opp,map,won,U,
          d[U,"coverage"],d[T,"coverage"],d[U,"movesPerRobotRound"],d[T,"movesPerRobotRound"],d[U,"oscillationABA"],d[T,"oscillationABA"],d[U,"stillRounds"],d[T,"stillRounds"] }' >> "$NAV"
  printf '%s\n' "$M" | grep -v '^nav \|^bytecode ' | awk -F, -v U="$U" -v T="$T" -v game="$b" -v opp="$opp" -v map="$map" -v won="$won" -v cols="$COLS" '
    BEGIN{nc=split(cols,C," ")}
    NR==1{for(i=1;i<=NF;i++)h[$i]=i; next}
    { row[$1,$2]=$0; if(!($1 in seen)){seen[$1]=1; order[++nr]=$1} }
    END { for(k=1;k<=nr;k++){ r=order[k]; if(!((r,U) in row) || !((r,T) in row)) continue;
            split(row[r,U],u,","); split(row[r,T],t,",");
            printf "%s\t%s\t%s\t%s\t%s", game, opp, map, won, r;
            for(c=1;c<=nc;c++) printf "\t%s", u[h[C[c]]]; for(c=1;c<=nc;c++) printf "\t%s", t[h[C[c]]]; printf "\n" } }' >> "$OUT"
done
echo "wrote $OUT ($(( $(wc -l < "$OUT") - 1 )) rows from $n replays)"
"$REPO/tools/scrim-study.py" "$OUT"
"$REPO/tools/scrim-study.py" "$NAV" --nav
