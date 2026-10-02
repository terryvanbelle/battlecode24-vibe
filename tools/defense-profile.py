#!/usr/bin/env python3
"""Aggregate --defense trips over a run's replays, split into their trips on our flags and ours on theirs.
    tools/defense-profile.py <run_dir> [...]     (our side read from the replay name: ...__botA.bc24 -> A)"""
import glob, os, re, subprocess, sys, statistics as st
REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
trips = {'theirs': [], 'ours': []}
for run in sys.argv[1:]:
    for f in glob.glob(os.path.join(run, '**', '*.bc24'), recursive=True):
        m = re.search(r'bot([AB])\.bc24$', f)
        if not m: continue
        us = m[1]
        out = subprocess.run([os.path.join(REPO, 'tools/replay-dump.sh'), f, '--defense'], capture_output=True, text=True).stdout
        for l in out.splitlines():
            if not l or l.startswith('round,'): continue
            p = l.split(',')
            r = dict(round=int(p[0]), first=int(p[2]), near20=int(p[5]), near64=int(p[6]), rounds=int(p[7]), chasers=float(p[8]), out=p[9])
            trips['ours' if p[1] == us else 'theirs'].append(r)
for k, T in trips.items():
    F = [t for t in T if t['first']]
    if not F: continue
    print(f'== {k} trips (carrier = {"enemy, on our flags" if k == "theirs" else "us, on their flags"}): {len(T)} trips, {len(F)} first grabs')
    print(f'   first grabs: defenders within dist2 20 at the pickup: mean {st.mean(t["near20"] for t in F):.2f}; '
          f'share with 0: {sum(t["near20"] == 0 for t in F) / len(F):.0%}; within 64: {st.mean(t["near64"] for t in F):.2f}')
    for lo, hi in ((0, 0), (1, 2), (3, 99)):
        S = [t for t in F if lo <= t['near20'] <= hi]
        if S: print(f'   first grabs with {lo}-{hi} defenders near: {len(S)}, captured {sum(t["out"] == "CAPTURE" for t in S) / len(S):.0%}')
    C = [t for t in T if t['out'] == 'CAPTURE']
    print(f'   all trips captured {len(C) / len(T):.1%}; mean chasers within 20 of the carrier: captured trips '
          f'{st.mean(t["chasers"] for t in C) if C else float("nan"):.2f}, failed {st.mean(t["chasers"] for t in T if t["out"] != "CAPTURE"):.2f}; '
          f'early first grabs (r<=400): {sum(t["round"] <= 400 for t in F)}')
