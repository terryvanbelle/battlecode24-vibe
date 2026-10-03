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
def res(d, with_seed):
    """Cell -> result. Seeded runs (audit B2: scrim.sh gives every cell its engine seed) are keyed by (opp, map, side,
    seed), so only games on the same engine seed pair; legacy runs drew a seed per build and pair on the cell only."""
    out = {}
    for r in csv.DictReader(open(os.path.join(d, 'results.csv'))):
        k = (r['opponent'], r['map'], r['bot_side']) + ((r.get('seed', ''),) if with_seed else ())
        out[k] = (r['bot_result'], r.get('rounds', ''))
    return out
C, K = runs(ctl), runs(cand)
gained = lost = games = same = 0; seeds = 0; legacy = 0
for s in sorted(set(C) & set(K)):
    a, b = res(C[s], True), res(K[s], True)
    if not set(a) & set(b):                       # no shared engine seeds: a legacy (unseeded) pair of runs
        a, b = res(C[s], False), res(K[s], False); legacy += 1
    seeds += 1
    for cell in set(a) & set(b):
        if a[cell][0] not in ('win', 'loss') or b[cell][0] not in ('win', 'loss'): continue
        games += 1
        if a[cell] == b[cell]: same += 1                # same result and length: very likely the identical game
        if a[cell][0] == 'loss' and b[cell][0] == 'win': gained += 1
        elif a[cell][0] == 'win' and b[cell][0] == 'loss': lost += 1
n = gained + lost
se = math.sqrt(n) if n else 0.0
print(f'filler tally {cand} vs {ctl}: {seeds} seeds ({seeds - legacy} on shared engine seeds, {legacy} legacy), {games} paired games, '
      f'gained {gained}, lost {lost}, net {gained - lost:+d}' + (f' ({(gained - lost) / se:+.1f} SE)' if se else '')
      + (f'; identical games {same}/{games}, discordant {n}/{games} (audit 2026-10-03 MEAS2: a seeded pair is exact only'
         f' for code that changes nothing)' if games else ''))
