#!/usr/bin/env python3
"""Paired evaluation of an arm against a control on identical band cells, split by opponent tier (owner prompt 112).
    tools/eval-paired.py <ctl_census.csv> <arm_census.csv> <ctl_run>,<arm_run> [...] [--tier tools/upper-tier.txt]
                         [--rung tools/next-rung.txt]   (descriptive slice: the bots just above us, owner prompt 113)
Cells are paired by (seed index, replay basename) as in tools/arm-deltas.py. For each slice (all, upper tier, rest):
  wins: control, arm, gained / lost (discordant pairs), net, exact two-sided sign-test p;
  capture difference per game (our captures - theirs): control mean, arm mean, paired delta +- SE (t).
The upper tier is the list of bots rated 2050+ on our ladder (2026-10-02; re-derived 2026-10-04 on the converged fit, audit
MEAS11: waffle, which we now beat ~62%, dropped), which beat us 83-100%."""
import csv, math, os, re, sys
from collections import defaultdict

def load(path):
    """(run, basename) -> {'won': int, 'cap': int, 'ecap': int, 'opp': str} from a census CSV (both team rows)."""
    rows = defaultdict(dict)
    for r in csv.DictReader(l for l in open(path) if ',' in l and not l.startswith('census:')):
        f = r['file']; parts = f.split('/')
        key = (parts[-3] if len(parts) >= 3 else '', os.path.basename(f))
        rows[key]['us' if r.get('us') == '1' else 'them'] = r
    out = {}
    for k, v in rows.items():
        if 'us' not in v: continue
        u = v['us']; t = v.get('them')
        try:
            out[k] = {'won': int(u['won']), 'cap': int(u['captured']), 'opp': u['opp'], 'rounds': u.get('rounds', ''),
                      'ecap': int(t['captured']) if t else int(u.get('enemyCaptured') or 0)}
        except (KeyError, ValueError):
            continue
    return out

def sign_p(g, l):
    n = g + l
    if n == 0: return 1.0
    k = min(g, l)
    p = sum(math.comb(n, i) for i in range(k + 1)) / 2 ** n
    return min(1.0, 2 * p)

SEEDSEG = re.compile(r'__s\d+(?=__bot[AB]\.bc24$)')

def pairs(ctl, arm, runpairs):
    # audit MEAS3: replay names carry the engine seed since 2026-10-04; pair on full names when both runs have them, else on
    # seedless names (older control runs)
    seeded = all(SEEDSEG.search(b) for (_, b) in list(ctl) + list(arm))
    k = (lambda b: b) if seeded else (lambda b: SEEDSEG.sub('', b))
    for cr, ar in runpairs:
        for (run, base), a in arm.items():
            if not run.endswith(ar): continue
            c = next((v for (r2, b2), v in ctl.items() if r2.endswith(cr) and k(b2) == k(base)), None)
            if c is not None: yield c, a

def summarize(name, P):
    n = len(P)
    if n == 0: return f'{name:6s} n=0'
    cw = sum(c['won'] for c, a in P); aw = sum(a['won'] for c, a in P)
    g = sum(1 for c, a in P if a['won'] and not c['won']); l = sum(1 for c, a in P if c['won'] and not a['won'])
    cd = [c['cap'] - c['ecap'] for c, a in P]; ad = [a['cap'] - a['ecap'] for c, a in P]
    d = [x - y for x, y in zip(ad, cd)]
    m = sum(d) / n
    se = math.sqrt(sum((x - m) ** 2 for x in d) / (n - 1) / n) if n > 1 else float('nan')
    t = m / se if se and se > 0 else float('nan')
    same = sum(1 for c, a in P if (c['won'], c['rounds'], c['cap'], c['ecap']) == (a['won'], a['rounds'], a['cap'], a['ecap']))
    return (f'{name:6s} n={n:4d}  wins {cw}->{aw}  gained {g} lost {l} net {g - l:+d} (sign p={sign_p(g, l):.3f})  '
            f'capture diff {sum(cd) / n:+.2f} -> {sum(ad) / n:+.2f}  delta {m:+.2f} +- {se:.2f} (t={t:+.1f})  identical {same}/{n}')

def stats(P):
    """(t of the capture-difference delta, wins net) over pairs P."""
    n = len(P)
    if n < 2: return float('nan'), 0
    d = [(a['cap'] - a['ecap']) - (c['cap'] - c['ecap']) for c, a in P]
    m = sum(d) / n; se = math.sqrt(sum((x - m) ** 2 for x in d) / (n - 1) / n)
    net = sum(1 for c, a in P if a['won'] and not c['won']) - sum(1 for c, a in P if c['won'] and not a['won'])
    return (m / se if se > 0 else float('nan')), net

def ship_decision(look, t_all, t_up, net):
    """Shipping rule (owner PROMPTS 186-188, research/criteria-review-2026-10-05/verdict.md; REWRITE_EVAL.md): ship test
    (t_all >= 2.3 or t_up >= 2.6) and net >= 0, read at look 1 (240 pairs), 2 (480 pooled) or 3 (720 pooled)."""
    test = (t_all >= 2.3 or t_up >= 2.6) and net >= 0
    if look == 1:
        if t_all >= 3.0 and net >= 0: return 'SHIP'
        if t_all < 0.5 and t_up < 0.8: return 'STOP (park)'
        return 'CONTINUE (seeds 3-4)'
    if look == 2:
        if test: return 'SHIP'
        if t_all < 1.0 and t_up < 1.3: return 'STOP (park)'
        return 'CONTINUE (seeds 5-6)'
    return 'SHIP' if test else 'STOP (park)'

def main(argv):
    look = None
    if '--look' in argv:
        i = argv.index('--look'); look = int(argv[i + 1]); argv = argv[:i] + argv[i + 2:]
    tier_file = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'upper-tier.txt')
    rung = None
    if '--rung' in argv:
        i = argv.index('--rung'); rung = {l.strip() for l in open(argv[i + 1]) if l.strip()}; argv = argv[:i] + argv[i + 2:]
    if '--tier' in argv:
        i = argv.index('--tier'); tier_file = argv[i + 1]; argv = argv[:i] + argv[i + 2:]
    upper = {l.strip() for l in open(tier_file) if l.strip()}
    ctl, arm = load(argv[0]), load(argv[1])
    runpairs = [p.split(',') for p in argv[2:]]
    P = list(pairs(ctl, arm, runpairs))
    print(summarize('all', P))
    print(summarize('upper', [p for p in P if p[0]['opp'] in upper]))
    print(summarize('rest', [p for p in P if p[0]['opp'] not in upper]))
    if rung is not None: print(summarize('rung', [p for p in P if p[0]['opp'] in rung]))   # descriptive only
    if look is not None:
        t_all, net = stats(P); t_up, _ = stats([p for p in P if p[0]['opp'] in upper])
        print(f'ship rule, look {look} ({len(P)} pairs): t_all {t_all:+.2f}, t_up {t_up:+.2f}, net {net:+d} -> '
              f'{ship_decision(look, t_all, t_up, net)}')

if __name__ == '__main__':
    main(sys.argv[1:])
