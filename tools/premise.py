#!/usr/bin/env python3
"""S0a premise from replay truth (research/REWRITE_DESIGN.md section 3, S0a): bars P1, P2, P3, P4, P6 and descriptive D.

    tools/premise.py [-P N] [--out trips.csv] [--side A|B] <run_dir | replay.bc24 | trips.csv>...

Each replay is dumped with `tools/replay-dump.sh <replay> --track --team <our side>`. Our side comes from the file name
(...__botA.bc24 -> A, ...__botB.bc24 -> B); other names (diag games: team A is the first named build) use --side
(default A). A run_dir contributes run_dir/losses/*.bc24, run_dir/replays/*.bc24 and run_dir/*.bc24. A .csv argument is a
trip CSV written earlier with --out and is read without dumping. -P N runs N dumps at once (default 3; nice 19).
--out FILE writes the pooled per-trip rows (file column + the --track columns) before the report.

The sample is checked, never assumed: an argument that does not exist, or a directory without a replay, is an error (exit
2). Each argument's replay count is printed; when run_dir/results.csv exists, its distinct (opponent, map, bot_side) cells
are compared with the replays found (names opp__map__botX.bc24), and every cell without a replay becomes a kind=missing
row. A replay whose dump fails becomes a kind=error row. Both kinds are written by --out, so a CSV re-read reports the same
shortfall. With any missing or failed replay the report is PARTIAL: bars are printed, routing is not, and the exit is 1.

--track rows (one per finished post-setup trip; see tools/replaydump/ReplayDump.java TRACK_COLS):
  kind=their   the other team carries one of our flags. unseenRounds = carried rounds with none of ours within dist2 20;
               predErr = median Chebyshev error of the 2.4 prediction over ALL those rounds (a HOME belief predicts the
               home tile); destHit = the destination held for most live-track rounds from tKnow on is the zone the carrier
               captured in (blank = no such round); tKnow = rounds after the pickup until one of ours is within dist2 20 of
               the carrier (tKnowBy sight) or of the home tile while the belief was HOME (tKnowBy home: the MISSING trigger);
               reachAll / reachFree = our non-defender, non-carrying robots with t <= 30 by the 2.5 rule at tKnow (free = no
               enemy within dist2 10, or jailed). chaserSum / chaserRounds = the exact meanChasers20.
  kind=game    one per replay: symOk (Sym.best() of our slot 16 at r250 gives their true spawn centres), outcome WON/LOST.
  kind=own     we carry theirs. ownEscorts20 = mean of ours within dist2 20 of our carrier per carried round;
               convoyReachFree = ours within Chebyshev 10 of the carrier at the end of the pickup round, outside dist2 20,
               no enemy within dist2 10 (every own trip, also one that ends in its pickup round).

Bars (pinned before any number was seen; trip length = tripRounds = rounds from pickup to the trip's end):
  P1  their trips >= 25 rounds, CAPTURE, with an unseen carried round: median predErr (true symmetry) <= 4 tiles
  P2  U = their trips >= 25 rounds, meanChasers20 < 0.5, CAPTURE: share with reachFree >= 3 at tKnow (no tKnow = 0):
      >= 0.5 build both doses; 0.25-0.5 build dose A only; < 0.25 close CUT
  P3  their trips >= 25 rounds, CAPTURE: share destHit >= 0.8 (no destination = a miss)
  P4  our trips: (i) median convoyReachFree >= 2 and (ii) on our trips >= 6 rounds, capture rate with ownEscorts20 >= 3
      at least 1.5 x the rate with ownEscorts20 < 1.5; both hold or S3 is closed
  P6  their trips >= 25 rounds, all outcomes: capture rate(meanChasers20 >= 1) / rate(< 0.5) <= 0.6
  D   on U: share with one of ours within dist2 20 at the pickup; of those ducks, the share jailed within 10 rounds;
      reachInFight; symOk (share of games)
Readings fixed here (the design leaves them open; the alternative is printed beside each so a choice is visible):
  - missing values: P2 no tKnow = 0 (design); P3 no destination = 0 (conditional share printed); P1 a trip with unseen
    rounds but no predErr counts as a failure (inf); fully seen trips have no unseen round and are outside P1 (counted).
  - meanChasers20 thresholds use the exact (unrounded) mean, the literal reading and the one --capabilities unopposedCaps
    uses. The B3 reference figures (74%, 38%, P6 0.51) came from --defense's 1-decimal value; U, P2 and P6 are also printed
    under that reading with the number of long trips it moves across 0.5 or 1.
  - ratio bars are compared in integers (no float boundary error); a rate of 0 in the reference group gives ratio inf
    (P6 FAIL, P4(ii) ok); an empty group, or both rates 0, is NO DATA.
Routing: P2 or P6 fails -> CUT closed with no bot code; P1 or P3 fails -> one tracker revision, then re-measure;
P4 fails -> S3 closed. If symOk < 0.9, P1 and P3 are also read under Sym.best() (predErrSym, destHitSym; always printed).
"""
import csv, glob, math, os, random, re, statistics as st, subprocess, sys
from concurrent.futures import ThreadPoolExecutor
from decimal import Decimal
from fractions import Fraction

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DUMP = os.path.join(REPO, 'tools', 'replay-dump.sh')
NAME = re.compile(r'^(.+)__(.+)__bot([AB])\.bc24$')


class InputError(Exception):
    pass


def side_of(path, default='A'):
    m = re.search(r'__bot([AB])\.bc24$', path)
    return m[1] if m else default


def expected_cells(run_dir):
    """Distinct (opponent, map, bot_side) cells of run_dir/results.csv, or None without one."""
    p = os.path.join(run_dir, 'results.csv')
    if not os.path.isfile(p): return None
    with open(p) as f: return {(r['opponent'], r['map'], r['bot_side']) for r in csv.DictReader(f)}


def replays(args):
    """-> (replays, csvs, notes, missing rows). Raises InputError for an argument that does not exist or has no replay."""
    out, csvs, notes, missing = [], [], [], []
    for a in args:
        if a.endswith('.csv'):
            if not os.path.isfile(a): raise InputError(f'{a}: no such trip CSV')
            csvs.append(a); continue
        if a.endswith('.bc24'):
            if not os.path.isfile(a): raise InputError(f'{a}: no such replay')
            out.append(a); notes.append(f'input {a}: 1 replay'); continue
        if not os.path.isdir(a): raise InputError(f'{a}: no such run dir, replay or trip CSV')
        found = {}
        for sub in ('losses', 'replays', '.'):
            for p in sorted(glob.glob(os.path.join(a, sub, '*.bc24'))): found.setdefault(os.path.basename(p), p)
        if not found: raise InputError(f'{a}: no .bc24 under losses/, replays/ or the dir itself')
        out += found.values()
        note = f'input {a}: {len(found)} replays'
        cells = expected_cells(a)
        if cells is not None:
            have = {(m[1], m[2], m[3]) for m in map(NAME.match, found) if m}
            gone = sorted(cells - have)
            note += f'; results.csv {len(cells)} distinct (opponent, map, side) cells, {len(cells) - len(gone)} with a replay'
            if gone: note += f', {len(gone)} WITHOUT one'
            missing += [dict(file=os.path.join(a, f'{o}__{m}__bot{s}.bc24'), kind='missing', outcome='no replay for this results.csv cell')
                        for o, m, s in gone]
        notes.append(note)
    return out, csvs, notes, missing


def dump_one(path, side):
    r = subprocess.run(['nice', '-n', '19', DUMP, path, '--track', '--team', side], capture_output=True, text=True)
    rows = list(csv.DictReader(r.stdout.splitlines())) if r.returncode == 0 else []
    if r.returncode != 0 or not any(x.get('kind') == 'game' for x in rows):
        return path, None, (r.stderr.strip()[-300:] or 'no game row in the --track output')
    for x in rows: x['file'] = path
    return path, rows, ''


def collect(files, side, par):
    """-> (trip rows, error rows); a failed dump becomes one kind=error row."""
    if files: subprocess.run([DUMP, '--calc'], input='', capture_output=True, text=True)   # compile once before the parallel dumps
    rows, errs = [], []
    with ThreadPoolExecutor(max_workers=max(1, par)) as ex:
        for path, got, err in ex.map(lambda f: dump_one(f, side_of(f, side)), files):
            if got is None: errs.append(dict(file=path, kind='error', outcome=' '.join(err.split())[:300]))
            else: rows += got
    return rows, errs


def num(v):
    try: return float(v)
    except (TypeError, ValueError): return None


def chasers(r):
    """The exact mean chasers20 of a trip (Fraction) from chaserSum/chaserRounds; older CSVs: the printed mean."""
    s, n = r.get('chaserSum'), r.get('chaserRounds')
    if s not in (None, '') and n not in (None, ''):
        return Fraction(int(s), int(n)) if int(n) > 0 else Fraction(0)
    v = r.get('meanChasers20')
    return Fraction(Decimal(v)) if v not in (None, '') else None


def chasers_1dp(r):
    """The 1-decimal reading of --defense and the B3 census: Java's %.1f, which is half-up on the exact mean
    (9/20 -> 0.5, 19/20 -> 1.0)."""
    c = chasers(r)
    return None if c is None else Fraction(math.floor(c * 10 + Fraction(1, 2)), 10)


def share(k, n):
    """k/n with its binomial SE; (None, None) without a denominator."""
    if n == 0: return None, None
    p = k / n
    return p, math.sqrt(p * (1 - p) / n)


def fmt_share(k, n):
    p, se = share(k, n)
    return f'{k}/{n} = n/a' if p is None else f'{k}/{n} = {p:.3f} +- {se:.3f}'


def rank_q(v, q):
    """Nearest-rank quantile (no interpolation, so inf values stay inf)."""
    s = sorted(v); return s[min(len(s) - 1, max(0, math.ceil(q * len(s)) - 1))]


def boot_se_median(v, reps=400, seed=1):
    if len(v) < 2 or any(math.isinf(x) for x in v): return float('nan')
    rnd = random.Random(seed)
    meds = [st.median(rnd.choices(v, k=len(v))) for _ in range(reps)]
    return st.pstdev(meds)


def ratio_ok(k1, n1, k0, n0, num_, den, op):
    """(k1/n1) / (k0/n0) op num_/den, in integers; op '<=' or '>='. Caller guarantees n1, n0 > 0 and not k1 == k0 == 0."""
    lhs, rhs = den * k1 * n0, num_ * n1 * k0
    return lhs <= rhs if op == '<=' else lhs >= rhs


def p2_verdict(k, n):
    if n == 0: return 'NO DATA'
    return 'PASS' if 2 * k >= n else 'DOSE_A' if 4 * k >= n else 'FAIL'


def p6_eval(long_, rd):
    """-> (verdict, line) for P6 under the chasers reading rd."""
    c1 = [r for r in long_ if rd(r) >= 1]; c0 = [r for r in long_ if rd(r) < Fraction(1, 2)]
    k1 = sum(r['outcome'] == 'CAPTURE' for r in c1); k0 = sum(r['outcome'] == 'CAPTURE' for r in c0)
    head = f'capture rate with chasers20 >= 1 {fmt_share(k1, len(c1))} / with < 0.5 {fmt_share(k0, len(c0))}'
    if not c1 or not c0: return 'NO DATA', head + ': NO DATA (an empty group)'
    if k1 == 0 and k0 == 0: return 'NO DATA', head + ': NO DATA (both rates 0, ratio undefined)'
    if k0 == 0: return 'FAIL', head + ' = inf; bar <= 0.6: FAIL'
    r1, r0 = k1 / len(c1), k0 / len(c0); ratio6 = r1 / r0
    se6 = ratio6 * math.sqrt(((1 - r1) / (len(c1) * r1) if r1 > 0 else 0) + (1 - r0) / (len(c0) * r0))
    v = 'PASS' if ratio_ok(k1, len(c1), k0, len(c0), 3, 5, '<=') else 'FAIL'
    return v, head + f' = {ratio6:.2f} +- {se6:.2f}; bar <= 0.6: {v}'


def evaluate(rows):
    """Pooled trips -> (report lines, verdicts). verdicts: P1/P3/P6 'PASS'|'FAIL'|'NO DATA'; P2 'PASS'|'DOSE_A'|'FAIL'|'NO DATA';
    P4 'PASS'|'FAIL'|'NO DATA'; plus 'PARTIAL': True when a replay failed or a results.csv cell had no replay."""
    L, V = [], {}
    errs = [r for r in rows if r.get('kind') == 'error']; miss = [r for r in rows if r.get('kind') == 'missing']
    dumped = sorted({r.get('file', '') for r in rows if r.get('kind') == 'game'})
    L.append(f'replays: {len(dumped) + len(errs)} found, {len(dumped)} dumped, {len(errs)} failed'
             + (f'; {len(miss)} results.csv cells without a replay' if miss else ''))
    for r in errs: L.append(f'   FAILED {r.get("file", "")}: {r.get("outcome", "")}')
    for r in miss[:10]: L.append(f'   MISSING {r.get("file", "")}')
    if len(miss) > 10: L.append(f'   ... and {len(miss) - 10} more missing cells')
    their = [r for r in rows if r.get('kind') == 'their']
    own = [r for r in rows if r.get('kind') == 'own']
    files = sorted({r['file'] for r in rows if r.get('kind') in ('their', 'own', 'game') and 'file' in r})
    L.append(f'games {len(files)}; their trips {len(their)}, our trips {len(own)}')
    long_ = [r for r in their if num(r['tripRounds']) is not None and num(r['tripRounds']) >= 25]
    s25c = [r for r in long_ if r['outcome'] == 'CAPTURE']
    U = [r for r in s25c if chasers(r) < Fraction(1, 2)]
    L.append(f'their trips >= 25 rounds: {len(long_)}; captured: {len(s25c)}; U (captured, meanChasers20 < 0.5 unrounded): {len(U)}')

    # P1: median of per-trip predErr over long captured trips with an unseen carried round (blank predErr there = failure)
    def p1(col):
        sub = [r for r in s25c if (num(r['unseenRounds']) or 0) > 0]
        v = [num(r[col]) if num(r[col]) is not None else math.inf for r in sub]
        return v, len(s25c) - len(sub), sum(1 for r in sub if num(r[col]) is None)
    pe, nseen, nblank = p1('predErr')
    if pe:
        m = st.median(pe); V['P1'] = 'PASS' if m <= 4 else 'FAIL'
        L.append(f'P1 median predErr (true symmetry) = {m:.1f} tiles (IQR {rank_q(pe, .25):.1f}-{rank_q(pe, .75):.1f}, bootstrap SE '
                 f'{boot_se_median(pe):.2f}) over {len(pe)}/{len(s25c)} long captured trips with an unseen carried round '
                 f'({nseen} fully seen; {nblank} without predErr counted as failures); bar <= 4: {V["P1"]}')
    else:
        V['P1'] = 'NO DATA'; L.append(f'P1 no long captured trip with an unseen carried round (of {len(s25c)}): NO DATA')
    pes, _, nbs = p1('predErrSym')
    if pes: L.append(f'   P1 under Sym.best(): median predErrSym = {st.median(pes):.1f} over {len(pes)} trips ({nbs} without a value counted as failures)')

    # P2: share of U with reachFree >= 3 at tKnow (no tKnow = 0)
    k2 = sum(1 for r in U if (num(r['reachFree']) or 0) >= 3)
    V['P2'] = p2_verdict(k2, len(U))
    kn = sum(1 for r in U if num(r['tKnow']) is not None)
    by = {b: sum(1 for r in U if r.get('tKnowBy') == b) for b in ('sight', 'home')}
    kby = {b: sum(1 for r in U if r.get('tKnowBy') == b and (num(r['reachFree']) or 0) >= 3) for b in ('sight', 'home')}
    L.append(f'P2 share of U with reachFree >= 3 at tKnow: {fmt_share(k2, len(U))}; bar >= 0.5 both doses, 0.25-0.5 dose A only, '
             f'< 0.25 close CUT: {V["P2"]}  (U with tKnow {kn}: by sighting {by["sight"]} ({kby["sight"]} with reachFree >= 3), '
             f'by the home tile {by["home"]} ({kby["home"]}))')

    # P3: destHit on long captured trips; no destination = a miss (the conditional share is printed beside it)
    def p3(col):
        k = sum(1 for r in s25c if r[col] == '1'); nd = sum(1 for r in s25c if r[col] in ('0', '1'))
        return k, nd
    k3, nd3 = p3('destHit')
    V['P3'] = 'NO DATA' if not s25c else 'PASS' if 5 * k3 >= 4 * len(s25c) else 'FAIL'
    L.append(f'P3 destHit on long captured trips: {fmt_share(k3, len(s25c))} (no destination = a miss: {len(s25c) - nd3} trips; '
             f'among trips with one {fmt_share(k3, nd3)}); bar >= 0.8: {V["P3"]}')
    if s25c:
        k3s, nd3s = p3('destHitSym')
        L.append(f'   P3 under Sym.best(): destHitSym {fmt_share(k3s, len(s25c))} (among trips with one {fmt_share(k3s, nd3s)})')

    # P4: convoy reach at pickup; escort conversion on our trips >= 6 rounds
    cv = [num(r['convoyReachFree']) for r in own if num(r['convoyReachFree']) is not None]
    p4i = st.median(cv) if cv else None
    ok_i = None if p4i is None else p4i >= 2
    o6 = [r for r in own if num(r['tripRounds']) >= 6 and num(r['ownEscorts20']) is not None]
    hi = [r for r in o6 if num(r['ownEscorts20']) >= 3]; lo = [r for r in o6 if num(r['ownEscorts20']) < 1.5]
    khi = sum(r['outcome'] == 'CAPTURE' for r in hi); klo = sum(r['outcome'] == 'CAPTURE' for r in lo)
    if not hi or not lo: ok_ii, ratio, why = None, None, 'an empty group'
    elif khi == 0 and klo == 0: ok_ii, ratio, why = None, None, 'both rates 0'
    elif klo == 0: ok_ii, ratio, why = True, math.inf, ''
    else: ok_ii, ratio, why = ratio_ok(khi, len(hi), klo, len(lo), 3, 2, '>='), (khi / len(hi)) / (klo / len(lo)), ''
    V['P4'] = 'FAIL' if False in (ok_i, ok_ii) else 'PASS' if ok_i and ok_ii else 'NO DATA'
    word = lambda ok: 'NO DATA' if ok is None else 'ok' if ok else 'fail'
    L.append(f'P4 (i) median convoyReachFree at pickup = {"n/a" if p4i is None else f"{p4i:.1f}"} over {len(cv)} of our {len(own)} trips; '
             f'bar >= 2: {word(ok_i)}')
    L.append(f'P4 (ii) our trips >= 6 rounds: capture rate with ownEscorts20 >= 3 {fmt_share(khi, len(hi))} vs < 1.5 {fmt_share(klo, len(lo))}; '
             f'ratio {"n/a" if ratio is None else "inf" if math.isinf(ratio) else f"{ratio:.2f}"}; bar >= 1.5: {word(ok_ii)}'
             f'{f" ({why})" if why else ""}  -> P4 {V["P4"]}')

    # P6: conversion on long trips, all outcomes
    V['P6'], line6 = p6_eval(long_, chasers)
    L.append('P6 their trips >= 25 rounds: ' + line6)

    # the 1-decimal reading of the B3 census, beside the unrounded one
    moved = sum(1 for r in long_ if (chasers(r) < Fraction(1, 2)) != (chasers_1dp(r) < Fraction(1, 2))
                or (chasers(r) >= 1) != (chasers_1dp(r) >= 1))
    U1 = [r for r in s25c if chasers_1dp(r) < Fraction(1, 2)]
    k21 = sum(1 for r in U1 if (num(r['reachFree']) or 0) >= 3)
    v61, line61 = p6_eval(long_, chasers_1dp)
    diff = [b for b, a, c in (('P2', V['P2'], p2_verdict(k21, len(U1))), ('P6', V['P6'], v61)) if a != c]
    L.append(f'   1-decimal chasers20 reading (--defense, B3 census): moves {moved} of {len(long_)} long trips across 0.5 or 1; '
             f'U {len(U1)}; P2 {fmt_share(k21, len(U1))} {p2_verdict(k21, len(U1))}; P6 {line61.split(": ")[-1]}'
             + (f'; VERDICT DIFFERS on {",".join(diff)}' if diff else '; same verdicts'))

    # D: descriptives on U
    seen = [r for r in U if (num(r['defNear20']) or 0) >= 1]
    ducks = sum(int(num(r['defNear20'])) for r in seen); died = sum(int(num(r['defDied10']) or 0) for r in seen)
    alldied = sum(1 for r in seen if int(num(r['defDied10']) or 0) >= int(num(r['defNear20'])))
    rif = [num(r['reachInFight']) for r in U if num(r['reachInFight']) is not None]
    sym = {}   # per game: the kind=game row, else any row of that game
    for r in sorted((r for r in rows if r.get('kind') in ('their', 'own', 'game')), key=lambda r: r.get('kind') == 'game'):
        if r.get('symOk') in ('0', '1'): sym[r.get('file', '')] = r['symOk']
    ks = list(sym.values()).count('1')
    L.append(f'D  U seen at pickup (one of ours within dist2 20): {fmt_share(len(seen), len(U))}; of those {ducks} ducks, jailed within 10 rounds: '
             f'{fmt_share(died, ducks)}; trips where every witness died ("died") {alldied}, others ("forgot") {len(seen) - alldied}')
    L.append(f'D  reachInFight on U (mean over {len(rif)} trips with a reacher): {st.mean(rif):.2f}' if rif else 'D  reachInFight on U: n/a')
    psym, _ = share(ks, len(sym))
    L.append(f'D  symOk: {fmt_share(ks, len(sym))} games' + (' (< 0.9: read P1/P3 under Sym.best() too; psymOk >= 0.95 becomes an S0b bar)'
                                                         if psym is not None and psym < 0.9 else ''))

    V['PARTIAL'] = bool(errs or miss)
    if V['PARTIAL']:
        L.append(f'PARTIAL: {len(errs)} failed dumps, {len(miss)} cells without a replay; the bars above are on part of the sample, '
                 'no routing is read from them')
        return L, V
    bars = {k: v for k, v in V.items() if k != 'PARTIAL'}
    route = []
    if bars['P2'] == 'FAIL' or bars['P6'] == 'FAIL': route.append('CUT closed with no bot code (P2/P6)')
    elif bars['P2'] == 'DOSE_A': route.append('CUT: build dose A only (P2 in 0.25-0.5)')
    if bars['P1'] == 'FAIL' or bars['P3'] == 'FAIL': route.append('one tracker revision (velocity from two sightings, heading-led destination), then re-measure P1/P3')
    if bars['P4'] == 'FAIL': route.append('S3 closed (P4)')
    if any(v == 'NO DATA' for v in bars.values()): route.append('NO DATA on ' + ','.join(k for k, v in bars.items() if v == 'NO DATA'))
    L.append('ROUTING: ' + ('; '.join(route) if route else 'all bars pass: S0b next'))
    return L, V


def main(argv):
    par, out, side, args = 3, None, 'A', []
    i = 0
    while i < len(argv):
        a = argv[i]
        if a == '-P': par = int(argv[i + 1]); i += 2
        elif a == '--out': out = argv[i + 1]; i += 2
        elif a == '--side': side = argv[i + 1]; i += 2
        elif a in ('-h', '--help'): print(__doc__); return 0
        else: args.append(a); i += 1
    if not args: print(__doc__); return 2
    try: files, csvs, notes, missing = replays(args)
    except InputError as e: print(f'!! {e}', file=sys.stderr); return 2
    rows, errs = collect(files, side, par)
    rows += errs + missing
    for c in csvs:
        with open(c) as f: rows += list(csv.DictReader(f))
    for e in errs: print('!! dump failed:', e['file'], e['outcome'], file=sys.stderr)
    for n in notes: print(n)
    if out:
        cols = ['file']
        for r in rows:
            for k in r:
                if k not in cols: cols.append(k)
        with open(out, 'w', newline='') as f:
            w = csv.DictWriter(f, fieldnames=cols, restval=''); w.writeheader(); w.writerows(rows)
        print(f'trips: {len(rows)} rows -> {out}')
    lines, V = evaluate(rows)
    print('\n'.join(lines))
    return 1 if V.get('PARTIAL') else 0


if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
