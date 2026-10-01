#!/usr/bin/env python3
"""Summarise a capability census (tools/capability-census.sh) into research/CAPABILITY_CENSUS.md.
   tools/capability-summary.py <census.csv> [<census.csv> ...]
For each basic-capability metric: medians for us and for the opponent, split by opponents that beat us (our win
rate <= 50% in these games) and the rest, and in our wins vs our losses. Derived per-game rates are added."""
import csv, sys, os, statistics as st, collections
REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
rows = []
for p in sys.argv[1:]:
    rows += [r for r in csv.DictReader(l for l in open(p) if not l.startswith('census:') and ',' in l) if r.get('team') in ('A', 'B')]
games = collections.defaultdict(dict)
for r in rows: games[r['file']]['us' if r['us'] == '1' else 'th'] = r
games = {f: g for f, g in games.items() if 'us' in g and 'th' in g}
def f(x):
    try: return float(x)
    except (TypeError, ValueError): return None
def derived(r):
    d = dict(r)
    pk = f(r['pickups']) or 0
    d['captureRate'] = (f(r['captured']) / pk) if pk else None
    cr = f(r['carrierRounds']) or 0
    d['carrierSpeed'] = (f(r['carrierMoves']) / cr) if cr else None
    d['trapHitRate'] = (f(r['trapsHit']) / f(r['trapsBuilt'])) if f(r['trapsBuilt']) else None
    for k in ('firstEnemySide', 'firstFlagSight'):
        if f(r[k]) == 0: d[k] = None          # never happened
    return d
for g in games.values(): g['us'], g['th'] = derived(g['us']), derived(g['th'])
wr = collections.defaultdict(list)
for g in games.values(): wr[g['us']['opp']].append(g['us']['won'] == '1')
beaters = {o for o, v in wr.items() if sum(v) / len(v) <= 0.5}
METRICS = ['gathered200', 'gathered400', 'firstEnemySide', 'inEnemy250', 'inEnemy300', 'firstFlagSight', 'pickups',
           'captured', 'captureRate', 'carrierSpeed', 'carrierDeaths', 'enemyCarrierKills', 'trapsBuilt', 'trapsHit',
           'trapHitRate', 'kills', 'deaths', 'meanAlive', 'rounds']
def med(sel, side, k):
    v = [f(g[side][k]) for g in sel if f(g[side][k]) is not None]
    return (st.median(v), len(v)) if v else (float('nan'), 0)
groups = [('vs bots that beat us', [g for g in games.values() if g['us']['opp'] in beaters]),
          ('vs bots we beat', [g for g in games.values() if g['us']['opp'] not in beaters]),
          ('our losses (all)', [g for g in games.values() if g['us']['won'] == '0']),
          ('our wins (all)', [g for g in games.values() if g['us']['won'] == '1'])]
out = ['# CAPABILITY_CENSUS.md — basic capabilities, us vs opponents', '',
       f'{len(games)} games from {", ".join(os.path.basename(p) for p in sys.argv[1:])}; {len(beaters)} opponents beat us here '
       f'({", ".join(sorted(beaters))}). Medians; "n" = games where the metric is defined. Regenerate with '
       '`tools/capability-summary.py`. Definitions: `tools/replaydump/ReplayDump.java --capabilities`, research/CAPABILITIES.md.', '']
for name, sel in groups:
    out += [f'## {name} ({len(sel)} games)', '', '| metric | us | them | n |', '|---|---|---|---|']
    for k in METRICS:
        (mu, n1), (mt, n2) = med(sel, 'us', k), med(sel, 'th', k)
        out.append(f'| {k} | {mu:.2f} | {mt:.2f} | {min(n1, n2)} |')
    out.append('')
open(os.path.join(REPO, 'research', 'CAPABILITY_CENSUS.md'), 'w').write('\n'.join(out) + '\n')
print('\n'.join(out[:30]))
