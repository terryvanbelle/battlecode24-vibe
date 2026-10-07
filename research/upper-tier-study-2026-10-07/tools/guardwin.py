#!/usr/bin/env python3
# g7kiterc 5(a) guards (d) and falsifier over each cell's COMMON WINDOW (RC_BAND amendments 2026-10-07, A1): rounds 201..E, with
# E the last --metrics 50 row both games of the cell reached (= 50 x floor(min(arm rounds, twin rounds) / 50)). Per game-round
# reads mix the guards with game length (g7kite vs its g_iter7 twin on these cells: deaths per game-round 1.127x, windowed
# 1.004x; enemyStunTrig per game-round 1.098x, windowed 1.030x), so the windows are summed over the cells and compared.
#   deaths, heals    our team's cumulative columns in the --metrics 50 rows: row E minus row 200
#   enemyStunTrig    their stun traps triggered in r201..E, counted from tools/replay-dump.sh <replay> --from 201 --to E
#                    ('r<round> TRAP <their side> STUN at (x,y) triggered' lines; over r201..2000 the count equals the
#                    --capabilities column enemyStunTrig in all 24 kite5a games, checked 2026-10-07)
# Usage (on the VM, from the repo; the replays sit at the paths in metrics.csv's first column, 3 dumps at a time):
#   python3 research/upper-tier-study-2026-10-07/tools/guardwin.py <arm metrics.csv> <twin metrics.csv> <arm bot> <twin bot>
#   e.g. ... guardwin.py diag/kiterc5a/metrics.csv diag/kite5a/metrics.csv g7kiterc g7kite
# A metrics.csv holds `tools/replay-dump.sh <f> --metrics 50 | sed "s|^|$f,|"` for each replay (the 5(a) block jobs' format).
# Prints one row per cell, then arm/twin ratios pooled over the U half, the R half and all cells, with a jackknife SE over cells.
# Cells pair on the name after '-vs-' (<opponent>-<map>-s<seed>__bot<side>); our team is <side>; the tier split takes the
# opponent from tools/upper-tier.txt.
import csv, os, re, subprocess, sys
from concurrent.futures import ThreadPoolExecutor
HERE = os.path.dirname(os.path.abspath(__file__)); REPO = os.path.abspath(os.path.join(HERE, '../../..'))
UP = set(l.strip() for l in open(os.path.join(REPO, 'tools', 'upper-tier.txt')) if l.strip())
NAME = re.compile(r'^(?P<bot>[^-]+)-vs-(?P<cell>(?P<opp>[^-]+)-[^-]+-s\d+__bot(?P<side>[AB]))\.bc24$')

def rows(path, bot):   # {cell: (replay, side, opp, {round: row})} for `bot`'s games, our side only
    out, hdr = {}, None
    for r in csv.reader(open(path)):
        if len(r) > 2 and r[1] == 'round': hdr = r[1:]; continue
        m = NAME.match(os.path.basename(r[0]))
        if not m or m.group('bot') != bot: continue
        d = dict(zip(hdr, r[1:]))
        if d['team'] != m.group('side'): continue
        out.setdefault(m.group('cell'), (r[0], m.group('side'), m.group('opp'), {}))[3][int(d['round'])] = d
    return out

def stuns(replay, side, end):   # their stun traps triggered in r201..end
    them = 'B' if side == 'A' else 'A'
    p = subprocess.run([os.path.join(REPO, 'tools', 'replay-dump.sh'), replay, '--from', '201', '--to', str(end)],
                       cwd=REPO, capture_output=True, text=True, check=True)
    return len(re.findall(rf'^r\d+ TRAP {them} STUN at \(-?\d+,-?\d+\) triggered$', p.stdout, re.M))

def main():
    if len(sys.argv) != 5: sys.exit('usage: guardwin.py <arm metrics.csv> <twin metrics.csv> <arm bot> <twin bot>')
    A, T = rows(sys.argv[1], sys.argv[3]), rows(sys.argv[2], sys.argv[4])
    cells = sorted(set(A) & set(T))
    missing = sorted(set(A) ^ set(T))
    if missing: print('unpaired cells (left out):', ', '.join(missing))
    jobs, res = [], {}
    for c in cells:
        end = min(max(A[c][3]), max(T[c][3]))
        res[c] = {'end': end, 'tier': 'U' if A[c][2] in UP else 'R'}
        for k, g in (('a', A[c]), ('t', T[c])):
            if end <= 200 or 200 not in g[3]:
                res[c].update({'d' + k: 0, 'h' + k: 0, 's' + k: 0}); continue
            res[c]['d' + k] = int(g[3][end]['deaths']) - int(g[3][200]['deaths'])
            res[c]['h' + k] = int(g[3][end]['heals']) - int(g[3][200]['heals'])
            jobs.append((c, k, g[0], g[1], end))
    with ThreadPoolExecutor(3) as ex:
        for (c, k, *_), n in zip(jobs, ex.map(lambda j: stuns(*j[2:]), jobs)): res[c]['s' + k] = n
    print('cell,tier,windowEnd,deathsTwin,deathsArm,healsTwin,healsArm,enemyStunTrigTwin,enemyStunTrigArm')
    for c in cells:
        r = res[c]; print(f"{c},{r['tier']},{r['end']},{r['dt']},{r['da']},{r['ht']},{r['ha']},{r['st']},{r['sa']}")
    def ratio(sel, v):
        a = sum(res[c][v + 'a'] for c in sel); t = sum(res[c][v + 't'] for c in sel)
        return a / t if t else float('nan')
    for half, sel in (('U', [c for c in cells if res[c]['tier'] == 'U']), ('R', [c for c in cells if res[c]['tier'] == 'R']), ('all', cells)):
        s = f'{half:3s} cells {len(sel):2d}'
        for v, lbl in (('d', 'deaths'), ('h', 'heals'), ('s', 'enemyStunTrig')):
            full = ratio(sel, v); n = len(sel)
            jk = [ratio([x for x in sel if x != c], v) for c in sel] if n > 2 else []
            se = ((n - 1) / n * sum((x - sum(jk) / n) ** 2 for x in jk)) ** 0.5 if jk else float('nan')
            s += f' | {lbl} {full:.3f} (se {se:.3f})'
        print(s)

if __name__ == '__main__':
    main()
