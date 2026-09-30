#!/usr/bin/env python3
"""Summarise a study.tsv (tools/scrim-study.sh): medians per round, us vs them, wins vs losses; or nav.tsv (--nav)."""
import csv, sys, os, statistics as st, collections
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from derived import add_derived
NAV = '--nav' in sys.argv
rows = list(csv.DictReader(open(sys.argv[1]), delimiter='\t'))
def num(x):
    try: return float(x)
    except (TypeError, ValueError): return None
def med(rs, k):
    v = [num(r.get(k)) for r in rs]; v = [x for x in v if x is not None]
    return st.median(v) if v else float('nan')
if NAV:
    print(f"\n== movement ({len(rows)} games)")
    print(f"{'':16s} {'coverage%':>10s} {'moves/rr':>9s} {'aba%':>7s} {'still%':>7s}")
    for who, p in (('us (median)', 'us_'), ('them (median)', 'th_')):
        print(f"{who:16s} {med(rows,p+'cov'):10.1f} {med(rows,p+'meanMoves'):9.3f} {med(rows,p+'aba'):7.1f} {med(rows,p+'still'):7.1f}")
    sys.exit(0)
add_derived(rows)
cols = ['alive', 'hp', 'crumbs', 'captured', 'kills', 'deaths', 'net', 'attacks', 'heals', 'traps_built', 'traps_hit', 'level_sum', 'moves']
for rnd in ('200', '400', '600', '1000', '1500'):
    rs = [r for r in rows if r['round'] == rnd]
    if not rs: continue
    w = sum(1 for r in rs if r['won'] == '1')
    print(f"\n== r{rnd}  ({len(rs)} games still running, {w} won)")
    print(f"{'':14s}" + "".join(f"{c[:9]:>10s}" for c in cols))
    for who, p in (('us (median)', 'us_'), ('them (median)', 'th_')):
        print(f"{who:14s}" + "".join(f"{med(rs, p + c):10.1f}" for c in cols))
    for grp, name in ((lambda r: r['won'] == '1', 'wins: us'), (lambda r: r['won'] == '0', 'losses: us')):
        sub = [r for r in rs if grp(r)]
        if sub: print(f"{name:14s}" + "".join(f"{med(sub, 'us_' + c):10.1f}" for c in cols))
