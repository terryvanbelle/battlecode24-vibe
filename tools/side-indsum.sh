#!/usr/bin/env bash
# Sum our robots' last indicator counters (chases/intercepts/camps, escort turns, regrabs, combat traps) per replay,
# on OUR side of each game (replay names end in __bot<SIDE>.bc24): for step-5(a) checks on scrimmage blocks vs
# external bots, where diag-batch cannot be used (CLAUDE.md rule 4).
#   tools/side-indsum.sh <replay.bc24> [...]
cd "$(dirname "${BASH_SOURCE[0]}")/.."
for f in "$@"; do
  b=$(basename "$f" .bc24); side=${b##*__bot}
  tools/replay-dump.sh "$f" --logs ' rg' --team "$side" 2>/dev/null | python3 -c '
import sys,re
last={}
for l in sys.stdin:
    p=l.split()
    if len(p)>=3: last[p[1]]=l
s=dict(ch=0,ic=0,cp=0,et=0,rg=0,ct=0)
for l in last.values():
    m=re.search(r" ch(\d+)/(\d+)/(\d+) et(\d+) .* ct(\d+) .* rg(\d+)",l)
    if m:
        for k,i in zip(["ch","ic","cp","et","ct","rg"],range(1,7)): s[k]+=int(m.group(i))
print(sys.argv[1].ljust(48)," ".join(f"{k}{v}" for k,v in s.items()))
' "$b"
done
