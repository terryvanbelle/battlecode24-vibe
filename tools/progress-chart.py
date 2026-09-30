#!/usr/bin/env python3
"""progress/progress.png: rating (± 95%) and field score of every accepted build g_iterN, in accept order.
Reads progress/games.csv through tools/elolib.py (the same fit as ELO.md). Run by tools/post-block.sh."""
import os, sys
_venv = os.path.join(os.path.dirname(os.path.abspath(__file__)), '.venv', 'bin', 'python')
if os.path.exists(_venv) and '.venv' not in sys.prefix: os.execv(_venv, [_venv] + sys.argv)
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__))); import elolib
import matplotlib; matplotlib.use('Agg'); import matplotlib.pyplot as plt
REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

def main():
    rows = elolib.load()
    R, SE, games, wins = elolib.fit(rows)
    bs = elolib.accepted_builds(R)
    if not bs: print('progress-chart: no builds'); return
    field = elolib.ladder_bots()
    fs = [100 * elolib.field_score(R, b, field) for b in bs]
    x = range(len(bs)); lab = [b[3:] for b in bs]
    fig, (a1, a2) = plt.subplots(2, 1, figsize=(8, 6), sharex=True)
    a1.errorbar(x, [R[b] for b in bs], yerr=[1.96 * SE[b] for b in bs], fmt='o-', color='#c0392b', capsize=4)
    a1.set_ylabel('ladder rating (BT, Elo scale)'); a1.grid(alpha=.3)
    a1.set_title(f'Accepted builds on the ladder ({len(rows)} scrimmages vs {len(field)} external bots)')
    a2.plot(x, fs, 'o-', color='#2471a3'); a2.set_ylabel('expected field score %'); a2.set_ylim(0, 100); a2.grid(alpha=.3)
    a2.set_xticks(list(x)); a2.set_xticklabels(lab)
    for i, v in enumerate(fs): a2.annotate(f'{v:.0f}%', (i, v), textcoords='offset points', xytext=(0, 6), ha='center', fontsize=8)
    fig.tight_layout(); out = os.path.join(REPO, 'progress', 'progress.png'); fig.savefig(out, dpi=110)
    print('wrote', out)

if __name__ == '__main__': main()
