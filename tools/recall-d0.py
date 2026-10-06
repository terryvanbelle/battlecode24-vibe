#!/usr/bin/env python3
"""Recall premise check (Gymhgy study lever L1, research/gymhgy-study-2026-10-04/lens-their_offense.md): when a big enemy
group grabs one of our flags, where are our robots and what are they doing?

    tools/recall-d0.py [-P N] [--out rows.csv] [--side A|B] <run_dir | replay.bc24 | rows.csv>...

Each replay is dumped with `tools/replay-dump.sh <replay> --recall-d0 --team <our side>` (ReplayDump RD_COLS: one row per
chain on our flags at t = 0, 10, 20 rounds after the grab). Inputs, our side and the sample checks are premise.py's (run
dirs contribute losses/ and replays/; results.csv cells without a replay are kind=missing rows and make the report
PARTIAL). Rows carry the file and the game result (won = the replay sits in replays/, lost = in losses/).

Routes, pinned on 2026-10-06 before any pooled number was seen. C12 = CAPTURE chains with g0 >= 12, at t = 0:
  R1 free recall   median(free10 + free20) >= 6: enough of ours are free within 20 tiles to answer a wider recall -> build
                   L1 (free ducks within 20 tiles of a big grab go to the flag)
  R2 committed     else median(busy30 + busyFar) >= 15: our army is fighting more than 20 tiles away -> the lever is a
                   disengage-to-flag rule for the army (riskier; its own design)
  R3 local loss    else: our robots are near the flag but outnumbered -> recall is not the lever
Fight route F, pinned on 2026-10-06 after R was read (R3) and before any near-flag number was pooled: on chains with
g0 >= 12 at t = 10, D = mean(kills100 - deaths100) over RETURN chains minus the same over CAPTURE chains:
  F1 the fight decides  D >= 1.5: the local fight near the flag decides big chains -> build the flag-fight stun bank (stuns
                   add local strength without bodies; arm design follows)
  F2 not decisive  else
Printed beside (descriptive): the same means for RETURN 12+ chains and for u12 chains, ours100 at t = 10 (the study's
near100AtGrab10, 8.1 in capture chains vs 22.0 in returned ones on g_iter4), the free robots' notes, and the split by result.
"""
import csv, os, statistics as st, subprocess, sys
from concurrent.futures import ThreadPoolExecutor

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import premise   # noqa: E402

DUMP = os.path.join(HERE, 'replay-dump.sh')
NUM = ['g0', 'T', 't', 'enemy20', 'enemy10', 'ours20', 'ours100', 'alive', 'free10', 'free20', 'free30', 'freeFar',
       'busy10', 'busy20', 'busy30', 'busyFar', 'nFight', 'nTether', 'nChase', 'nIcpt', 'nDefend', 'nThreat', 'nExplore',
       'nGather', 'nOther', 'kills100', 'deaths100', 'stunVict100', 'enStunVict100']


def dump_one(path, side):
    r = subprocess.run(['nice', '-n', '19', DUMP, path, '--recall-d0', '--team', side], capture_output=True, text=True)
    lines = r.stdout.splitlines()
    if r.returncode != 0 or not lines or not lines[0].startswith('team,grab,'):
        return path, None, ' '.join((r.stderr.strip()[-300:] or 'no --recall-d0 header').split())
    won = '1' if os.sep + 'replays' + os.sep in path else '0' if os.sep + 'losses' + os.sep in path else ''
    rows = [dict(x, file=path, won=won, kind='row') for x in csv.DictReader(lines)]
    return path, rows + [dict(file=path, won=won, kind='game')], ''


def collect(files, side, par):
    if files: subprocess.run([DUMP, '--calc'], input='', capture_output=True, text=True)   # compile once
    rows = []
    with ThreadPoolExecutor(max_workers=max(1, par)) as ex:
        for path, got, err in ex.map(lambda f: dump_one(f, premise.side_of(f, side)), files):
            rows += got if got is not None else [dict(file=path, kind='error', outcome=err)]
    return rows


def iv(r, k): return int(r[k]) if r.get(k) not in (None, '') else 0


def mean(v): return sum(v) / len(v) if v else float('nan')


def med(v): return st.median(v) if v else float('nan')


def line(name, rs):
    if not rs: return f'  {name:<26} n 0'
    m = {k: mean([iv(r, k) for r in rs]) for k in NUM}
    return (f'  {name:<26} n {len(rs):>4}  en20 {m["enemy20"]:4.1f} en10 {m["enemy10"]:4.1f} | ours20 {m["ours20"]:4.1f} '
            f'ours100 {m["ours100"]:4.1f} alive {m["alive"]:4.1f} | free <=10 {m["free10"]:3.1f} 11-20 {m["free20"]:3.1f} '
            f'21-30 {m["free30"]:3.1f} >30 {m["freeFar"]:3.1f} | busy <=10 {m["busy10"]:4.1f} 11-20 {m["busy20"]:4.1f} '
            f'21-30 {m["busy30"]:4.1f} >30 {m["busyFar"]:4.1f} | near flag since grab: kills {m["kills100"]:4.1f} deaths {m["deaths100"]:4.1f} '
            f'stunned by us {m["stunVict100"]:4.1f} by them {m["enStunVict100"]:4.1f}')


def notes(rs):
    tot = {k: sum(iv(r, k) for r in rs) for k in NUM if k.startswith('n')}
    s = sum(tot.values())
    return '  free-within-30 notes: ' + ', '.join(f'{k[1:].lower()} {v / s:.0%}' for k, v in sorted(tot.items(), key=lambda x: -x[1]) if v) if s else ''


def evaluate(rows):
    L, V = [], {}
    rs = [r for r in rows if r.get('kind') == 'row']; bad = [r for r in rows if r.get('kind') in ('error', 'missing')]
    games = [r for r in rows if r.get('kind') == 'game']
    V['PARTIAL'] = bool(bad)
    L.append(f'replays: {len(games)} dumped ({sum(g["won"] == "1" for g in games)} won, {sum(g["won"] == "0" for g in games)} lost), '
             f'{sum(r["kind"] == "error" for r in bad)} failed, {sum(r["kind"] == "missing" for r in bad)} cells without a replay')
    for r in bad[:10]: L.append(f'  {r["kind"].upper()} {os.path.basename(r["file"])}: {r.get("outcome", "")}')
    t0 = [r for r in rs if iv(r, 't') == 0]
    big = lambda r: iv(r, 'g0') >= 12
    L.append(f'chains on our flags: {len(t0)} (12+ {sum(big(r) for r in t0)}, u12 {sum(not big(r) for r in t0)})')
    for t in (0, 10, 20):
        L.append(f't = {t}:')
        at = [r for r in rs if iv(r, 't') == t]
        for name, f in (('CAPTURE 12+', lambda r: big(r) and r['outcome'] == 'CAPTURE'), ('RETURN 12+', lambda r: big(r) and r['outcome'] == 'RETURN'),
                        ('CAPTURE u12', lambda r: not big(r) and r['outcome'] == 'CAPTURE'), ('RETURN u12', lambda r: not big(r) and r['outcome'] == 'RETURN')):
            L.append(line(name, [r for r in at if f(r)]))
    c12 = [r for r in t0 if big(r) and r['outcome'] == 'CAPTURE']
    L.append('by result, CAPTURE 12+ at t = 0:')
    for w, n in (('1', 'in our wins'), ('0', 'in our losses')): L.append(line(n, [r for r in c12 if r['won'] == w]))
    early = [r for r in c12 if iv(r, 'grab') < 1000]
    L.append(line('grabbed before r1000', early))
    L.append(notes(c12))
    fr = med([iv(r, 'free10') + iv(r, 'free20') for r in c12]); far = med([iv(r, 'busy30') + iv(r, 'busyFar') for r in c12])
    L.append(f'C12 (CAPTURE 12+, t = 0, n {len(c12)}): median free within 20 tiles {fr}, median busy beyond 20 tiles {far}')
    V['ROUTE'] = ('NO DATA' if not c12 else 'R1 free recall (build L1)' if fr >= 6 else 'R2 committed (disengage-to-flag design)' if far >= 15
                  else 'R3 local loss (recall is not the lever)')
    t10 = [r for r in rs if iv(r, 't') == 10 and big(r)]
    nk = lambda o: [iv(r, 'kills100') - iv(r, 'deaths100') for r in t10 if r['outcome'] == o]
    ret, cap = nk('RETURN'), nk('CAPTURE')
    D = mean(ret) - mean(cap) if ret and cap else None
    if D is not None:
        sv = lambda o: mean([iv(r, 'stunVict100') for r in t10 if r['outcome'] == o])
        L.append(f'F (12+ chains, t = 10): net kills near the flag RETURN {mean(ret):+.2f} (n {len(ret)}) vs CAPTURE {mean(cap):+.2f} (n {len(cap)}): '
                 f'D = {D:+.2f}; stunned by us RETURN {sv("RETURN"):.1f} vs CAPTURE {sv("CAPTURE"):.1f}')
    V['FIGHT'] = 'NO DATA' if D is None else 'F1 the fight decides (build the flag-fight stun bank)' if D >= 1.5 else 'F2 not decisive'
    if V['PARTIAL']: L.append('PARTIAL: a replay is missing or failed; the routes are not read')
    else: L.append(f'ROUTE: {V["ROUTE"]}'); L.append(f'FIGHT ROUTE: {V["FIGHT"]}')
    return L, V


def main(argv):
    par, out, side, args = 3, None, 'A', []
    it = iter(argv)
    for a in it:
        if a == '-P': par = int(next(it))
        elif a == '--out': out = next(it)
        elif a == '--side': side = next(it)
        else: args.append(a)
    if not args: print(__doc__, file=sys.stderr); return 2
    try: files, csvs, nts, missing = premise.replays(args)
    except premise.InputError as e: print(f'recall-d0: {e}', file=sys.stderr); return 2
    rows = list(missing)
    for c in csvs:
        with open(c) as f: rows += list(csv.DictReader(f))
    rows += collect(files, side, par)
    if out:
        keys = []
        for r in rows: keys += [k for k in r if k not in keys]
        with open(out, 'w', newline='') as f:
            w = csv.DictWriter(f, fieldnames=keys); w.writeheader(); w.writerows(rows)
    lines, V = evaluate(rows)
    print('\n'.join(nts + lines))
    return 1 if V['PARTIAL'] else 0


if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
