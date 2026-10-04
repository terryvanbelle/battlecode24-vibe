#!/usr/bin/env bash
# Sum our robots' last indicator counters (chases/intercepts/camps, escort turns, regrabs, combat traps) per replay,
# on OUR side of each game (replay names end in __bot<SIDE>.bc24; old diag-batch names without it: side A): for
# step-5(a) checks on scrimmage and diag-batch blocks.
#   tools/side-indsum.sh <replay.bc24> [...]
cd "$(dirname "${BASH_SOURCE[0]}")/.."
for f in "$@"; do
  b=$(basename "$f" .bc24); side=${b##*__bot}
  case "$side" in A|B) ;; *) side=A;; esac   # old diag-batch names (<bot>-vs-<opp>-<map>-s<seed>): our bot is team A
  tools/replay-dump.sh "$f" --logs ' rg' --team "$side" 2>/dev/null | python3 -c '
import sys,re
last={}
for l in sys.stdin:
    p=l.split()
    if len(p)>=3: last[p[1]]=l
s=dict(ch=0,ic=0,cp=0,et=0,rg=0,ct=0,pk=0,pr=0,fs=0,an=0,wy=0,cr=0,lf=0)
for l in last.values():
    for k,pat in (("ch",r" ch(\d+)/"),("ic",r" ch\d+/(\d+)/"),("cp",r" ch\d+/\d+/(\d+)"),("et",r" et(\d+)"),("ct",r" ct(\d+)"),("rg",r" rg(\d+)"),("pk",r" pk(\d+)"),("pr",r" pr(\d+)"),("fs",r" fs(\d+)"),("an",r" an(\d+)"),("wy",r" wy(\d+)"),("cr",r" cr(\d+)"),("lf",r" lf(\d+)")):
        m=re.search(pat,l)
        if m: s[k]+=int(m.group(1))   # each counter on its own: a truncated tail no longer drops the whole robot
print(sys.argv[1].ljust(48)," ".join(f"{k}{v}" for k,v in s.items()))
' "$b"
done
