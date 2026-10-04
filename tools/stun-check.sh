#!/usr/bin/env bash
# Stun summary for our side of each replay (step 5(a) checks of trap arms): census stun columns plus the median
# build-to-trigger latency of our post-setup stuns and how many we built with an enemy within dist2 8 (--trapgeo).
#   tools/stun-check.sh <replay.bc24> [...]
cd "$(dirname "${BASH_SOURCE[0]}")/.."
for f in "$@"; do
  b=$(basename "$f" .bc24); side=${b##*__bot}
  case "$side" in A|B) ;; *) side=A;; esac   # diag-batch names: our bot is team A
  cap=$(tools/replay-dump.sh "$f" --capabilities | awk -F, -v s="$side" 'NR==1{for(i=1;i<=NF;i++) c[$i]=i} NR>1 && $1==s {print "won", $c["won"], "cap", $c["captured"]"-"$c["enemyCaptured"], "k/d", $c["kills"]"/"$c["deaths"], "stuns", $c["stunTrig"], "victims", $c["stunVictims"], "theirStuns", $c["enemyStunTrig"], "ourVictims", $c["enemyStunVictims"]}')
  geo=$(tools/replay-dump.sh "$f" --trapgeo | python3 -c '
import sys
s=sys.argv[1]; n=near=0; lat=[]
for l in sys.stdin:
    p=l.strip().split(",")
    if len(p) < 12 or p[0] != "TG" or p[1] != s or p[2] != "STUN": continue
    n+=1; near+= 0 <= int(p[4]) <= 8
    if p[8] != "-1": lat.append(int(p[9]))
lat.sort()
print(f"built {n} near8 {near} latMedian {lat[(len(lat)-1)//2] if lat else chr(45)}")' "$side")
  echo "$b $cap $geo"
done
