#!/usr/bin/env python3
"""g4contact premise check D0 from replay truth (convoy plan section 6): the four pre-registered routes.

    tools/contact-d0.py [-P N] [--out rows.csv] [--side A|B] <run_dir | replay.bc24 | rows.csv>...

Each replay is dumped with `tools/replay-dump.sh <replay> --contact-d0 --team <our side>`: one row per chain on our flags
(tools/replaydump/ReplayDump.java D0_COLS: the arm's sensor replayed on what our robots could see). Inputs, our side and
the sample checks are premise.py's: our side from the name (...__botA.bc24 -> A, ...__botB.bc24 -> B), else --side
(default A); a run_dir contributes losses/, replays/ and its own *.bc24, and its results.csv cells without a replay become
kind=missing rows; a failed dump becomes a kind=error row; every replay dumped gives one kind=game row. --out writes all
rows, so a re-read CSV reports the same sample. With a missing or failed replay the report is PARTIAL: the reads are
printed, the routes are not, and the exit is 1. -P N runs N dumps at once (default 3; nice 19).

u12 = a chain whose true grab group g0 < 12. Routes (pinned in the plan before any number was seen; the readings fixed
here are marked *; every ratio bar is compared in exact fractions):
  1 prediction  M(k) = median chainPoint error (Chebyshev tiles) pooled over the u12-chain rounds aged 1..k (* pooled over
                the ages, as "over u12-chain rounds aged 1-12" reads; per-age medians printed beside; * a round whose
                chainPoint is null, past the predicted arrival, has no error: counted beside, not scored):
                M(12) <= 3 -> CT_HOLD 12; else M(8) <= 3 -> CT_HOLD 8; else M(4) > 3 -> PARK (the track cannot lead
                divers); else (M(4) <= 3 < M(8)) no pre-registered route
  2 reach       sum elig12 / sum unseenLive over u12 chains: >= 0.25 build as specified; 0.15-0.25 CT_DIVE_R2 144 (the
                same read with elig12r144 printed beside); < 0.15 PARK
  3 group gate  CT_GROUP_MAX = the largest of 12, 10, 8 whose eligible rounds (elig12 / elig10 / elig8, all chains) come
                from true g0 >= 12 chains at a share <= 0.25; none -> keep 12 (the 5(a) leakage read decides)
  4 gate column baseline = mean over games of the per-game noContact10u12 (* the quantity delivery-check's rel: compares;
                the pooled share printed beside); seenT0 share = no-contact u12 chains (noContact10 = 1) with seenT0 = 1:
                baseline >= 0.08 and seenT0 share >= 0.40 -> gate rel:noContact10u12<=0.8; else rel:screened20u12<=0.8
"""
import csv, os, statistics as st, subprocess, sys
from concurrent.futures import ThreadPoolExecutor
from fractions import Fraction as Fr

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import premise   # noqa: E402  (inputs, side_of, InputError)

DUMP = os.path.join(HERE, 'replay-dump.sh')
AGES = range(1, 13)


def dump_one(path, side):
    r = subprocess.run(['nice', '-n', '19', DUMP, path, '--contact-d0', '--team', side], capture_output=True, text=True)
    lines = r.stdout.splitlines()
    if r.returncode != 0 or not lines or not lines[0].startswith('team,grab,'):
        return path, None, ' '.join((r.stderr.strip()[-300:] or 'no --contact-d0 header').split())
    rows = [dict(x, file=path, kind='chain') for x in csv.DictReader(lines)]
    return path, rows + [dict(file=path, kind='game')], ''


def collect(files, side, par):
    if files: subprocess.run([DUMP, '--calc'], input='', capture_output=True, text=True)   # compile once before the parallel dumps
    rows = []
    with ThreadPoolExecutor(max_workers=max(1, par)) as ex:
        for path, got, err in ex.map(lambda f: dump_one(f, premise.side_of(f, side)), files):
            rows += got if got is not None else [dict(file=path, kind='error', outcome=err)]
    return rows


def ival(v): return int(v) if v not in (None, '') else 0


def errs(r, ages):
    return [int(x) for a in ages for x in (r.get(f'err{a}') or '').split(';') if x != '']


def med(v): return st.median(v) if v else None


def fmt(x): return 'n/a' if x is None else f'{float(x):.3f}'


def fmt1(x): return '-' if x is None else f'{float(x):.1f}'


def evaluate(rows):
    """Pooled rows -> (report lines, verdicts {R1, R2, R3, R4, PARTIAL})."""
    L, V = [], {}
    ch = [r for r in rows if r.get('kind') == 'chain']; bad = [r for r in rows if r.get('kind') in ('error', 'missing')]
    games = {r['file'] for r in rows if r.get('kind') == 'game'}
    u12 = [r for r in ch if ival(r['g0']) < 12]; p12 = [r for r in ch if ival(r['g0']) >= 12]
    V['PARTIAL'] = bool(bad)
    L.append(f'replays: {len(games) + sum(r["kind"] == "error" for r in bad)} found, {len(games)} dumped, '
             f'{sum(r["kind"] == "error" for r in bad)} failed; {sum(r["kind"] == "missing" for r in bad)} results.csv cells without a replay')
    for r in bad: L.append(f'  {r["kind"].upper()} {os.path.basename(r["file"])}: {r.get("outcome", "")}')
    L.append(f'chains on our flags: {len(ch)} (u12 {len(u12)}, 12+ {len(p12)}); u12 outcomes '
             + ', '.join(f'{o} {sum(r["outcome"] == o for r in u12)}' for o in ('CAPTURE', 'RETURN', 'OPEN')))
    # route 1: prediction
    per = {a: med([e for r in u12 for e in errs(r, [a])]) for a in AGES}
    M = {k: med([e for r in u12 for e in errs(r, range(1, k + 1))]) for k in (4, 8, 12)}
    n12 = sum(len(errs(r, AGES)) for r in u12)
    L.append(f'R1 prediction: {n12} unseen live u12 rounds with a point (+ {sum(ival(r["noPoint"]) for r in u12)} past the predicted arrival); '
             f'pooled median M(4) {fmt1(M[4])}, M(8) {fmt1(M[8])}, M(12) {fmt1(M[12])}')
    L.append('   per-age medians ' + ' '.join(f'{a}:{fmt1(per[a])}' for a in AGES))
    V['R1'] = ('NO DATA' if M[12] is None else 'CT_HOLD 12' if M[12] <= 3 else 'CT_HOLD 8' if M[8] is not None and M[8] <= 3
               else 'PARK' if M[4] is None or M[4] > 3 else 'NO ROUTE')
    # route 2: reach
    un = sum(ival(r['unseenLive']) for r in u12); el = sum(ival(r['elig12']) for r in u12); el144 = sum(ival(r['elig12r144']) for r in u12)
    reach = Fr(el, un) if un else None
    L.append(f'R2 reach: elig12 / unseenLive over u12 chains = {el}/{un} = {fmt(reach)} (dist2 144: {el144}/{un} = {fmt(Fr(el144, un) if un else None)})')
    V['R2'] = 'NO DATA' if reach is None else 'BUILD' if reach >= Fr(1, 4) else 'CT_DIVE_R2 144' if reach >= Fr(3, 20) else 'PARK'
    # route 3: group gate
    V['R3'] = None
    for g in (12, 10, 8):
        tot = sum(ival(r[f'elig{g}']) for r in ch); big = sum(ival(r[f'elig{g}']) for r in p12)
        s = Fr(big, tot) if tot else None
        L.append(f'R3 CT_GROUP_MAX {g}: eligible rounds from true g0 >= 12 chains {big}/{tot} = {fmt(s)}')
        if V['R3'] is None and s is not None and s <= Fr(1, 4): V['R3'] = f'CT_GROUP_MAX {g}'
    if V['R3'] is None: V['R3'] = 'CT_GROUP_MAX 12 (none qualifies)'
    # route 4: gate column
    judged = [r for r in u12 if r['noContact10'] in ('0', '1')]
    per_game = {}
    for r in judged: per_game.setdefault(r['file'], []).append(r['noContact10'] == '1')
    base = sum((Fr(sum(v), len(v)) for v in per_game.values()), Fr(0)) / len(per_game) if per_game else None
    nc = [r for r in judged if r['noContact10'] == '1']; seen = sum(r['seenT0'] == '1' for r in nc)
    sh = Fr(seen, len(nc)) if nc else None
    L.append(f'R4 gate column: noContact10u12 baseline (mean of {len(per_game)} games) {fmt(base)}, pooled {len(nc)}/{len(judged)} = '
             f'{fmt(Fr(len(nc), len(judged)) if judged else None)}; no-contact u12 chains seen at t0 {seen}/{len(nc)} = {fmt(sh)}')
    V['R4'] = ('NO DATA' if base is None else 'rel:noContact10u12<=0.8' if base >= Fr(2, 25) and sh is not None and sh >= Fr(2, 5)
               else 'rel:screened20u12<=0.8')
    if V['PARTIAL']: L.append('PARTIAL: a replay is missing or failed; the routes are not read')
    else: L.append(f'ROUTES: R1 {V["R1"]}; R2 {V["R2"]}; R3 {V["R3"]}; R4 gate {V["R4"]}')
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
    try: files, csvs, notes, missing = premise.replays(args)
    except premise.InputError as e: print(f'contact-d0: {e}', file=sys.stderr); return 2
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
    print('\n'.join(notes + lines))
    return 1 if V['PARTIAL'] else 0


if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
