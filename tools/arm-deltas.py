#!/usr/bin/env python3
"""Paired capability deltas of an arm against the control on identical band cells (research/TACTIC_LEVELS.md gates).
   tools/arm-deltas.py <ctl.csv> <arm.csv> <ctl-run-s1>,<arm-run-s1> [<ctl-run-s2>,<arm-run-s2> ...] [--beaters a,b,...]
Rows are census/survey CSVs (tools/capability-census.sh). Cells are paired by (seed index, replay basename).
For each numeric column: mean of our (arm - ctl) delta, its SE, n; also on beater cells only."""
import csv, re, sys, os, math, statistics as st
argv = sys.argv[1:]; beaters = set()
if '--beaters' in argv:
    i = argv.index('--beaters'); beaters = set(argv[i + 1].split(',')); argv = argv[:i] + argv[i + 2:]
args = argv
ctl_csv, arm_csv, pairs = args[0], args[1], [p.split(',') for p in args[2:]]
def load(p):
    rows = {}
    for r in csv.DictReader(l for l in open(p) if ',' in l and not l.startswith('census:')):
        if r.get('us') != '1': continue
        rows[(r['file'].split('/')[-3] if r['file'].count('/') >= 2 else '', re.sub(r'__s\d+(?=__bot[AB]\.bc24$)', '', os.path.basename(r['file'])))] = r
    return rows
C, A = load(ctl_csv), load(arm_csv)
def num(x):
    try: return float(x)
    except (TypeError, ValueError): return None
cols = None; deltas = {}; bdeltas = {}
for (crun, arun) in pairs:
    for (run, base), a in A.items():
        if not run.endswith(arun): continue
        c = next((v for (r2, b2), v in C.items() if r2.endswith(crun) and b2 == base), None)
        if c is None: continue
        cols = cols or [k for k in a if k not in ('file', 'opp', 'us', 'team', 'name', 'wintype', 'upgrades')]
        opp = base.split('__')[0]
        for k in cols:
            x, y = num(a.get(k)), num(c.get(k))
            if k in ('firstFlagSight', 'firstEnemySide') and (x == 0 or y == 0): continue   # never happened: censored
            if x is None or y is None: continue
            deltas.setdefault(k, []).append(x - y)
            if opp in beaters: bdeltas.setdefault(k, []).append(x - y)
def line(k, v):
    if not v: return f'{k:16s}   -'
    m = st.mean(v); se = st.stdev(v) / math.sqrt(len(v)) if len(v) > 1 else float('nan')
    return f'{k:16s} {m:+9.2f} +- {se:6.2f}  ({m / se:+5.1f} SE, n={len(v)})' if se and se > 0 else f'{k:16s} {m:+9.2f}  n={len(v)}'
print('all cells');      [print('  ' + line(k, deltas.get(k, []))) for k in (cols or [])]
if beaters: print('beater cells'); [print('  ' + line(k, bdeltas.get(k, []))) for k in (cols or [])]
