#!/usr/bin/env bash
# Sum our robots' indicator counters (chases/intercepts/camps, escort turns, regrabs, combat traps, ..., and the andli28 arms'
# eh, fc, fcF) per replay, on OUR side of each game (replay names end in __bot<SIDE>.bc24; old diag-batch names without it:
# side A): for step-5(a) checks on scrimmage and diag-batch blocks.
#   tools/side-indsum.sh <replay.bc24> [...]
# Each counter only grows, so a robot's count is the largest value seen in any of its strings: the engine cuts a string at 64
# chars, and since arms g7ehp/g7fc put their counters after o/x the later counters (from ch on) are often cut in fights
# (2026-10-06: the old filter ' rg' and "last line per robot" missed those robots).
cd "$(dirname "${BASH_SOURCE[0]}")/.."
for f in "$@"; do
  b=$(basename "$f" .bc24); side=${b##*__bot}
  case "$side" in A|B) ;; *) side=A;; esac   # old diag-batch names (<bot>-vs-<opp>-<map>-s<seed>): our bot is team A
  tools/replay-dump.sh "$f" --logs ' x[0-9]' --team "$side" 2>/dev/null | python3 -c '
import sys,re
PATS=(("ch",r" ch(\d+)/"),("ic",r" ch\d+/(\d+)/"),("cp",r" ch\d+/\d+/(\d+)"),("et",r" et(\d+)"),("rg",r" rg(\d+)"),("ct",r" ct(\d+)"),("pk",r" pk(\d+)"),
      ("pr",r" pr(\d+)"),("fs",r" fs(\d+)"),("an",r" an(\d+)"),("wy",r" wy(\d+)"),("cr",r" cr(\d+)"),("lf",r" lf(\d+)"),("eh",r" eh(\d+)"),
      ("fc",r" fc(\d+)"),("fcF",r" fcF(\d+)"))
best={}
for l in sys.stdin:
    p=l.split()
    if len(p)<3: continue
    r=best.setdefault(p[1],{})
    for k,pat in PATS:
        m=re.search(pat,l)
        if m: r[k]=max(r.get(k,0),int(m.group(1)))
s={k:sum(r.get(k,0) for r in best.values()) for k,_ in PATS}
print(sys.argv[1].ljust(48)," ".join(f"{k}{v}" for k,v in s.items()))
' "$b"
done
