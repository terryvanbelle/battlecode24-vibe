#!/usr/bin/env python3
"""Running paired tally of candidate vs control over filler seeds where both played the same cells (owner prompt 73).
   tools/filler-tally.py <control> <candidate>   (reads gauntlet/*-scrim-<bot>-fill<seed>/results.csv on the driver)"""
import csv, glob, math, os, re, sys
REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ctl, cand = sys.argv[1], sys.argv[2]
def runs(bot):
    out = {}
    for d in glob.glob(os.path.join(REPO, 'gauntlet', f'*-scrim-{bot}-fill*')):
        m = re.search(r'-fill(\d+)$', d)
        if m and os.path.exists(os.path.join(d, 'results.csv')): out[m.group(1)] = d
    return out
def res(d):
    return {(r['opponent'], r['map'], r['bot_side']): r['bot_result'] for r in csv.DictReader(open(os.path.join(d, 'results.csv')))}
C, K = runs(ctl), runs(cand)
gained = lost = games = 0; seeds = 0
for s in sorted(set(C) & set(K)):
    a, b = res(C[s]), res(K[s]); seeds += 1
    for cell in set(a) & set(b):
        if a[cell] not in ('win', 'loss') or b[cell] not in ('win', 'loss'): continue
        games += 1
        if a[cell] == 'loss' and b[cell] == 'win': gained += 1
        elif a[cell] == 'win' and b[cell] == 'loss': lost += 1
n = gained + lost
se = math.sqrt(n) if n else 0.0
print(f'filler tally {cand} vs {ctl}: {seeds} seeds, {games} paired games, gained {gained}, lost {lost}, net {gained - lost:+d}'
      + (f' ({(gained - lost) / se:+.1f} SE)' if se else ''))
