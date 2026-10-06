#!/usr/bin/env python3
"""Tests for the python tools: the SPRT, the Elo bookkeeping, the scrimmage recorder, the benchmark
selector. Synthetic inputs; runs from tools/unit-tests.sh. Every check names what it protects."""
import math, re, os, shutil, subprocess, sys, tempfile, csv, importlib.util, collections
HERE = os.path.dirname(os.path.abspath(__file__))
fails = 0
def check(ok, what):
    global fails
    if not ok: fails += 1; print('FAIL', what)

# --- sprt.py: the verdict is a pure function of (wins, losses)
def sprt(w, l, extra=()):
    out = subprocess.run([sys.executable, os.path.join(HERE, 'sprt.py'), str(w), str(l), *extra], capture_output=True, text=True).stdout
    return out.strip().split()[-1]
check(sprt(0, 0) == 'CONTINUE', 'sprt: no games is CONTINUE')
check(sprt(60, 20) == 'ACCEPT', 'sprt: 60-20 accepts')
check(sprt(20, 60) == 'REJECT', 'sprt: 20-60 rejects')
check(sprt(8, 8) == 'CONTINUE', 'sprt: 8-8 continues')
# the boundaries are the log-likelihood ratio thresholds log(beta/(1-alpha)) and log((1-beta)/alpha)
llr = lambda w, l, p0=.5, p1=.58: w * math.log(p1 / p0) + l * math.log((1 - p1) / (1 - p0))
hi = math.log(0.95 / 0.05)
n = next(n for n in range(1, 400) if llr(n, 0) >= hi)
check(sprt(n, 0) == 'ACCEPT' and sprt(n - 1, 0) == 'CONTINUE', f'sprt: accept boundary at {n} straight wins')

# --- elolib: ratings move in the right direction and a never-met bot stays at 1500
spec = importlib.util.spec_from_file_location('elolib', os.path.join(HERE, 'elolib.py')); elolib = importlib.util.module_from_spec(spec); spec.loader.exec_module(elolib)
rows = [dict(run='r1', seq=0, teamA='us:g0', teamB='x.bot', map='M', winner='A', rounds='100', reason='', seed='1'),
        dict(run='r1', seq=1, teamA='y.bot', teamB='us:g0', map='M', winner='B', rounds='100', reason='', seed='2'),
        dict(run='r1', seq=2, teamA='us:g0', teamB='x.bot', map='M', winner='B', rounds='100', reason='', seed='3')]
R, SE, games, wins = elolib.fit(rows)
check(games['us:g0'] == 3 and games['x.bot'] == 2 and games['y.bot'] == 1, 'elo: games counted per player')
check(wins['us:g0'] == 2 and wins['x.bot'] == 1, 'elo: wins counted')
check(R['y.bot'] < 1500 < R['us:g0'], 'elo: a loser drops and the winner rises')
check(R['never.met'] == 1500 and SE['never.met'] == float('inf'), 'elo: unmet bot is 1500, unrated')
check(abs(elolib.expected(1500, 1500) - 0.5) < 1e-9 and elolib.expected(1900, 1500) > 0.9, 'elo: expected score')
check(R['x.bot'] > R['y.bot'], 'elo: a bot that took a game off us rates above one that did not')
check(elolib.fit(rows[::-1])[0]['us:g0'] - R['us:g0'] < 1e-6, 'elo: the fit does not depend on play order')
# builds are separate players: a build's easy games do not lift another build
two = rows + [dict(run='r2', seq=i, teamA='us:g1', teamB='z.bot', map='M', winner='A', rounds='100', reason='', seed=str(i)) for i in range(20)]
check(abs(elolib.fit(two)[0]['us:g0'] - R['us:g0']) < 1e-6, 'elo: builds are rated separately')
check(elolib.current_build(two) == 'g1', 'elo: current build is the one of the last game')
# a repeated pairing with the same seed is the same game: counted once; a different seed is a new game
rep = rows + [dict(rows[0], seq=9)]
check(elolib.fit(rep)[2]['us:g0'] == 3, 'elo: a repeated cell (same seed) counts once')
rep2 = rows + [dict(rows[0], seq=9, seed='77')]
check(elolib.fit(rep2)[2]['us:g0'] == 4, 'elo: a different seed is a new game')
# the 2026-09-24 failure: many wins over weak bots must not lift us above a bot that beats us 9 of 10
hist = [dict(run='r', seq=i, teamA='us:g0', teamB='s.bot', map='M', winner='B' if i % 10 else 'A', rounds='1', reason='', seed=str(i)) for i in range(40)]
hist += [dict(run='r', seq=100 + i, teamA='us:g0', teamB=f'w{i}.bot', map='M', winner='A', rounds='1', reason='', seed=str(i)) for i in range(96)]
Rh = elolib.fit(hist)[0]
check(Rh['s.bot'] > Rh['us:g0'], 'elo: easy wins do not lift us above a bot that beats us')
check(0.3 < elolib.field_score(Rh, 'us:g0', ['s.bot', 'w0.bot']) < 0.7, 'elo: field score averages expected scores')

# --- scrim-record.py: reads a gauntlet results.csv into games.csv rows, idempotent per run
with tempfile.TemporaryDirectory() as d:
    run = os.path.join(d, '20260101-000000-scrim-bot'); os.makedirs(run)
    open(os.path.join(run, 'results.csv'), 'w').write('opponent,map,bot_side,winner_side,rounds,bot_result,reason\n'
        'x.bot,Maze,A,A,500,win,HQ destroyed\nx.bot,Soup,B,A,700,loss,HQ destroyed\ny.bot,Maze,B,?,?,unknown,timeout\n')
    env = dict(os.environ, PYTHONPATH=HERE)
    # point elolib at a temp games.csv by copying the module into a fake repo
    fake = os.path.join(d, 'repo', 'tools'); os.makedirs(fake); os.makedirs(os.path.join(d, 'repo', 'progress'))
    for f in ('elolib.py', 'scrim-record.py'): open(os.path.join(fake, f), 'w').write(open(os.path.join(HERE, f)).read())
    r1 = subprocess.run([sys.executable, os.path.join(fake, 'scrim-record.py'), run, '--label', 'g0'], capture_output=True, text=True)
    r2 = subprocess.run([sys.executable, os.path.join(fake, 'scrim-record.py'), run, '--label', 'g0'], capture_output=True, text=True)
    rows = list(csv.DictReader(open(os.path.join(d, 'repo', 'progress', 'games.csv'))))
    check(len(rows) == 2, f'scrim-record: two decided games recorded, unknown skipped ({len(rows)})')
    check(rows[0]['teamA'] == 'us:g0' and rows[0]['winner'] == 'A' and rows[1]['teamA'] == 'x.bot' and rows[1]['winner'] == 'A', 'scrim-record: sides and winners preserved')
    check('already recorded' in r2.stdout, 'scrim-record: second run is a no-op')

# --- bench-select.py: name-only scoring prefers finals over sprints and drops test packages
spec = importlib.util.spec_from_file_location('bsel', os.path.join(HERE, 'bench-select.py')); bsel = importlib.util.module_from_spec(spec); spec.loader.exec_module(bsel)
check(bsel.score('finalbot') > bsel.score('sprintbot') > bsel.score('v1'), 'bench-select: final > sprint > v1')
check(bsel.JUNK.search('testplayer') and bsel.JUNK.search('donothing') and not bsel.JUNK.search('finalbot'), 'bench-select: junk filter')

REPO = os.path.dirname(HERE)

# --- elolib.accepted_builds: accept order is numeric, not lexical
check(elolib.accepted_builds({'us:g_iter10': 1, 'us:g_iter2': 1, 'x.bot': 1, 'us:g_iter0': 1}) == ['us:g_iter0', 'us:g_iter2', 'us:g_iter10'], 'elolib: accepted builds sorted numerically, externals excluded')

# --- replay-dump: integrity on the committed fixture replay (examplefuncsplayer mirror, DefaultSmall, seed 1)
FIX = os.path.join(REPO, 'test', 'fixtures', 'example-DefaultSmall-s1.bc24')
if os.path.exists(FIX) and os.path.exists(os.path.join(REPO, 'engine', 'engine.jar')):
    dump = lambda *a: subprocess.run([os.path.join(HERE, 'replay-dump.sh'), FIX, *a], capture_output=True, text=True).stdout
    def _sections(text):   # one dump with several modes: comm rows 66 fields, --defense 11, --capabilities 93, --track 29, --contact-d0 28
        out = {}
        for ln in text.splitlines():
            if not ln or ln.startswith('#'): continue
            f = next(csv.reader([ln])); out.setdefault(len(f), []).append(f)
        return {n: [dict(zip(v[0], r)) for r in v[1:]] for n, v in out.items()}
    def _pick(sec, column):   # the section whose header has `column` (field counts change when columns are added)
        for rows in sec.values():
            if rows and column in rows[0]: return rows
        return []
    dump('--summary')   # compile once before the parallel dumps
    # OWN: a committed game of our own build (src/bot of 2026-09-30, team A) against examplefuncsplayer, DefaultSmall, won by
    # capture at r746; it carries our shared array (slot 0 = 50 after round 1). The design's fixture test runs on it, so a
    # clean checkout or the VM runs it too (2.11).
    OWN = os.path.join(REPO, 'test', 'fixtures', 'bot-vs-example-DefaultSmall-b1.bc24')
    check(os.path.exists(OWN), f'test fixture present: {OWN}')
    COLT = os.path.join(REPO, 'diag', 'sp5', 'ColtG5.Goob_final__Ambush__botA.bc24')   # extra S0a checks on a band game; local only (diag/ is not committed)
    # --contact-d0 rides along (28 fields, its own section); it must leave every other mode's output unchanged
    _jobs = {'fix': [FIX, '--comm', '1-2000', '--defense', '--capabilities', '--track', '--contact-d0'], 'fixflags': [FIX, '--flags']}
    if os.path.exists(OWN):
        _jobs.update(own=[OWN, '--comm', '1-746', '--defense', '--capabilities', '--track', '--contact-d0'], ownflags=[OWN, '--flags'])
        # the dive plumbing on a replay that predates C.CONTACT: every indicator string counts as a dive turn (prefix ''); B's
        # strings are listed beside, to count them
        _jobs['dive'] = [OWN, '--capabilities', '--dive-note', '', '--logs', '^', '--team', 'B']
        _jobs['rd'] = [OWN, '--recall-d0']   # recall premise check (Gymhgy study L1): checked against --contact-d0's chains
    if os.path.exists(COLT): _jobs['colt'] = [COLT, '--comm', '1-1', '--defense', '--capabilities', '--track', '--team', 'A', '--contact-d0']
    END = os.path.join(REPO, 'diag', 'sp5', 'IvanGeffner.kuma__EndAround__botA.bc24')   # local regression of the drop window (below)
    if os.path.exists(END): _jobs['endlog'] = [END, '--track-log', '--team', 'A']
    _procs = {k: subprocess.Popen([os.path.join(HERE, 'replay-dump.sh'), *a], stdout=subprocess.PIPE, stderr=subprocess.DEVNULL, text=True) for k, a in _jobs.items()}
    _d0dir = tempfile.mkdtemp(); _d0csv = os.path.join(_d0dir, 'd0.csv')
    if os.path.exists(OWN):
        _procs['d0e2e'] = subprocess.Popen([sys.executable, os.path.join(HERE, 'contact-d0.py'), '-P', '1', '--side', 'B', '--out', _d0csv, OWN],
                                           stdout=subprocess.PIPE, stderr=subprocess.DEVNULL, text=True)
    s = dump()
    check('winner B' in s and 'LEVEL_SUM' in s and 'round 2000' in s, 'replay-dump: summary matches the engine result (B by level sum at r2000)')
    import re as _re
    kd = {m.group(1): (int(m.group(2)), int(m.group(3))) for m in _re.finditer(r'^([AB]): .*? deaths=(\d+) kills=(\d+)', s, _re.M)}
    check(kd.get('A', (0, 1))[0] == kd.get('B', (1, 0))[1] and kd['A'][1] == kd['B'][0], 'replay-dump: A deaths = B kills and vice versa')
    rows = list(csv.DictReader(dump('--metrics', '50').splitlines()))
    check(len(rows) == 2 * 40, f'replay-dump: --metrics 50 gives 40 rounds x 2 teams ({len(rows)})')
    check(all(0 <= int(r['alive']) <= 50 for r in rows), 'replay-dump: alive within [0,50]')
    check(all(int(r['max_bc']) <= 25000 for r in rows), 'replay-dump: bytecode within limit')
    for t in 'AB':
        tr = [r for r in rows if r['team'] == t]
        for col in ('deaths', 'kills', 'attacks', 'traps_built', 'moves', 'spawned'):
            check(all(int(a[col]) <= int(b[col]) for a, b in zip(tr, tr[1:])), f'replay-dump: cumulative {col} never decreases ({t})')
    nav = dump('--navstats')
    cov = [float(x) for x in _re.findall(r'coverage=([\d.]+)%', nav)]
    check(len(cov) == 2 and all(0 <= c <= 100 for c in cov), 'replay-dump: coverage in [0,100]')
    m = dump('--map-at', '300').splitlines()
    check(len(m) == 1 + 31 and all(len(l) == 3 + 31 for l in m[1:]), 'replay-dump: board is 31x31 with row labels')
    check(not any(l.startswith('r') and ' digs ' in l for l in s.splitlines()), 'replay-dump: the summary prints no events (actor -1 is not the default --robot)')
    raw = {k: p.communicate()[0] for k, p in _procs.items()}
    outs = {k: _sections(raw[k]) for k in ('fix', 'own', 'colt') if k in raw}
    cap = _pick(outs['fix'], 'enemyCarrierKills')
    check(len(cap) == 2 and all(int(c['captured']) + int(c['carrierDeaths']) <= int(c['pickups']) for c in cap),
          'replay-dump --capabilities: captures + carrier deaths never exceed pickups')
    check(int(cap[0]['enemyCarrierKills']) == int(cap[1]['carrierDeaths']) and int(cap[1]['enemyCarrierKills']) == int(cap[0]['carrierDeaths']),
          'replay-dump --capabilities: one side\'s carrier kills are the other side\'s carrier deaths')
    check(all(0 <= float(c['meanAlive']) <= 50 for c in cap), 'replay-dump --capabilities: mean alive in [0,50]')
    check(all(float(c['chasers20']) >= 0 for c in cap) and cap[0]['enemyCaptured'] == cap[1]['captured'] and cap[1]['enemyCaptured'] == cap[0]['captured'],
          'replay-dump --capabilities: enemyCaptured mirrors the other side; chasers20 >= 0')
    check(all(float(c['escorts20']) >= 0 for c in cap), 'replay-dump --capabilities: escorts20 >= 0')
    check(all(0 <= float(c['stillPost']) <= 100 for c in cap), 'replay-dump --capabilities: stillPost is a percentage')
    check(all(c['exceptions'].isdigit() for c in cap), 'replay-dump --capabilities: exceptions is a count')
    check(all(c['symWrong'] in ('', '0', '1') for c in cap), 'replay-dump --capabilities: symWrong is blank, 0 or 1')
    check(all(c['alertNoThreat'] == '' or int(c['alertNoThreat']) <= int(c['alertWrites']) for c in cap), 'replay-dump --capabilities: alerts without a threat are a subset of alert writes')
    dfn = outs['fix'].get(11, [])
    caps = {c['team']: int(c['captured']) for c in cap}
    check(dfn and all(d['outcome'] in ('DIED', 'DROP', 'CAPTURE') for d in dfn) and
          all(sum(1 for d in dfn if d['carrierTeam'] == t and d['outcome'] == 'CAPTURE') <= caps.get(t, 0) for t in 'AB'),
          'replay-dump --defense: outcomes are DIED/DROP/CAPTURE; capture trips never exceed captures')
    check(all(list(d.keys())[:10] == 'round,carrierTeam,first,fx,fy,defNear20,defNear64,tripRounds,meanChasers20,outcome'.split(',') for d in dfn),
          'replay-dump --defense: the existing columns keep their positions (tools/defense-profile.py reads by position)')
    check(all(int(c['regrabsLate']) <= int(c['regrabs']) and int(c['capturedLate']) <= int(c['captured']) for c in cap),
          'replay-dump --capabilities: late re-grabs/captures are subsets of all re-grabs/captures')
    check(all(int(c['firstGrabs']) + int(c['regrabs']) + int(c['relayPickups']) == int(c['postPickups']) <= int(c['pickups']) for c in cap),
          'replay-dump --capabilities: first grabs + re-grabs + relay pickups = post-setup pickups <= all pickups')
    r = subprocess.run([os.path.join(HERE, 'replay-dump.sh'), FIX, '--nosuchflag'], capture_output=True, text=True)
    check(r.returncode != 0, 'replay-dump: unknown flags are hard errors')
    tg = [l.split(',') for l in dump('--trapgeo').splitlines() if l.startswith('TG,')]
    check(all(len(x) == 12 and x[1] in 'AB' and x[2] in ('STUN', 'EXPLOSIVE', 'WATER') and int(x[3]) > 200
              and (x[8] == '-1' or int(x[9]) == int(x[8]) - int(x[3]) >= 0) for x in tg),
          f'replay-dump --trapgeo: 12 fields, post-setup builds, latency = trigger - build ({len(tg)} traps)')
    capr = {c['team']: c for c in _pick(_sections(dump('--capabilities')), 'enemyCarrierKills')}
    check(all(sum(1 for x in tg if x[1] == t and x[2] == 'STUN' and x[8] != '-1') <= int(capr[t]['stunTrig']) for t in capr),
          'replay-dump: --trapgeo triggered post-setup stuns <= census stunTrig (which also counts setup-built stuns)')
    sc = subprocess.run([os.path.join(HERE, 'stun-check.sh'), FIX], capture_output=True, text=True).stdout
    check(' stuns ' in sc and ' built ' in sc and ' latMedian ' in sc, 'stun-check.sh: one summary line per replay')
    with tempfile.TemporaryDirectory() as cd:   # a run with wins only (no losses/ folder) must still be censused
        os.makedirs(os.path.join(cd, 'run', 'replays'))
        shutil.copy(FIX, os.path.join(cd, 'run', 'replays', 'examplefuncsplayer__DefaultSmall__botA.bc24'))
        cr = subprocess.run([os.path.join(HERE, 'capability-census.sh'), os.path.join(cd, 'out.csv'), os.path.join(cd, 'run')], capture_output=True, text=True)
        rows = open(os.path.join(cd, 'out.csv')).read().splitlines() if os.path.exists(os.path.join(cd, 'out.csv')) else []
        check(cr.returncode == 0 and len(rows) == 3, f'capability-census.sh: a run without losses/ still gives its rows ({len(rows)} lines)')
else:
    print('test_tools: replay fixture or engine missing, replay-dump checks skipped')


# --- S0a instruments (research/REWRITE_DESIGN.md 2.4/2.5/2.11): replay-dump --calc, --comm, --track, new --capabilities
#     columns, and tools/premise.py. Python references of the 2.4 prediction and the 2.5 intercept rule.
def _sgn(v): return (v > 0) - (v < 0)
def _cheb(a, b): return max(abs(a[0] - b[0]), abs(a[1] - b[1]))
def _moves(age, cls): return 0 if age <= 0 else age if cls == 2 else age * 5 // 6 if cls == 1 else age // 2
def _rounds(n, cls): return n if cls == 2 else (6 * n + 4) // 5 if cls == 1 else 2 * n
def _step(l, d, n): return (l[0] + _sgn(d[0] - l[0]) * min(abs(d[0] - l[0]), n), l[1] + _sgn(d[1] - l[1]) * min(abs(d[1] - l[1]), n))
def _pred(l, d, cls, age): return _step(l, d, min(max(0, _cheb(l, d) - 1), _moves(age, cls)))
def _near(cs, p): return min(cs, key=lambda c: ((c[0] - p[0]) ** 2 + (c[1] - p[1]) ** 2, c[0], c[1]))   # ties: lower x, then lower y
from decimal import Decimal as _Dec, ROUND_HALF_UP as _HALF_UP
def _jf(n, d):   # Java's %.3f of n/d (half-up on the double's shortest decimal); '' without a denominator
    return str(_Dec(repr(n / d)).quantize(_Dec('0.001'), rounding=_HALF_UP)) if d else ''
def _icpt(l, d, cls, age, me=None, jail=-1, centres=()):
    nN = _cheb(l, d) - 1; s = max(1, nN // 12); n = _moves(age, cls)
    while n <= nN:
        q = _step(l, d, n); tc = _rounds(n, cls) - age
        m = me if jail < 0 else _near(centres, q)
        td = _cheb(m, q) * 6 // 5 + 1 + max(0, jail)
        if td <= tc: return td if td <= 30 else -1
        n += s
    return -1
if os.path.exists(os.path.join(REPO, 'engine', 'engine.jar')):
    import random as _random
    rnd = _random.Random(7); qs, want = [], []
    for _ in range(300):
        l = (rnd.randrange(60), rnd.randrange(60)); d = (rnd.randrange(60), rnd.randrange(60)); cls = rnd.randrange(3); age = rnd.randrange(-2, 90)
        me = (rnd.randrange(60), rnd.randrange(60)); jl = rnd.randrange(26)
        cs = [(rnd.randrange(60), rnd.randrange(60)) for _ in range(3)]
        if rnd.random() < 0.3: cs[1] = (2 * l[0] - cs[0][0], cs[0][1])   # a mirrored pair: equal distances from points on x = l.x
        qs += [f'P {l[0]} {l[1]} {d[0]} {d[1]} {cls} {age}', f'T {l[0]} {l[1]} {d[0]} {d[1]} {cls} {age} {me[0]} {me[1]}',
               f'J {l[0]} {l[1]} {d[0]} {d[1]} {cls} {age} {jl} ' + ' '.join(f'{c[0]} {c[1]}' for c in cs)]
        want += ['%d %d' % _pred(l, d, cls, age), str(_icpt(l, d, cls, age, me)), str(_icpt(l, d, cls, age, None, jl, cs))]
    # hand cases: a duck ahead on the path meets the carrier there (t 8); a duck behind gets the catch-up point (16); a jailed
    # duck at the same spawn centre with 3 rounds left misses the n=4 point (11 > 8) and takes the next one (n=6, t 8)
    hand = [('T 10 10 40 10 0 0 20 10', '8'), ('T 10 10 40 10 0 0 5 10', '16'), ('J 10 10 40 10 0 0 3 20 10', '8'),
            ('T 10 10 40 10 2 0 0 10', '-1'), ('T 10 10 40 10 0 0 59 59', '-1'), ('W 5 5 4 8 5', '3'), ('W 5 5 2 8 5', '-1'),
            ('P 10 10 40 10 0 10', '15 10'), ('P 10 10 40 10 1 12', '20 10'), ('P 10 10 40 10 2 12', '22 10'), ('P 10 10 40 10 0 500', '39 10'),
            # tie-free nearest centre (2.9 nearestDet): equal dist2 goes to the lower x, then the lower y, in either array order
            # (DefaultLarge (55,3)/(55,27) tie for a point on y = 15; the true and the Sym.best() lists differ in order there)
            ('N 0 0 5 0 3 4', '3 4'), ('N 0 0 3 4 5 0', '3 4'), ('N 0 5 3 10 3 0', '3 0'), ('N 0 5 3 0 3 10', '3 0'), ('N 1 1 9 9 2 2', '2 2'),
            # the end-of-round drop window: dropped (first seen) at r1060 -> on the ground at the end of r1060..r1063, home at r1064
            ('D 1060 4 1060', '1'), ('D 1060 4 1063', '1'), ('D 1060 4 1064', '0'), ('D 1060 25 1084', '1'), ('D 1060 25 1085', '0')]
    qs += [q for q, _ in hand]; want += [w for _, w in hand]
    got = subprocess.run([os.path.join(HERE, 'replay-dump.sh'), '--calc'], input='\n'.join(qs) + '\n', capture_output=True, text=True).stdout.split('\n')
    bad = [(q, w, g) for q, w, g in zip(qs, want, got) if w != g]
    check(len(got) >= len(qs) and not bad, f'replay-dump --calc: 2.4 prediction and 2.5 intercept match the python reference ({len(bad)} mismatches, e.g. {bad[:3]})')
    # properties of the prediction: monotone along the line to D, never past the zone edge (Chebyshev 1 from the centre)
    mono = True
    for _ in range(200):
        l = (rnd.randrange(60), rnd.randrange(60)); d = (rnd.randrange(60), rnd.randrange(60)); cls = rnd.randrange(3)
        ps = [_pred(l, d, cls, a) for a in range(0, 130)]
        mono &= all(_cheb(l, p) <= _cheb(l, q) and _cheb(q, d) <= _cheb(p, d) for p, q in zip(ps, ps[1:]))
        mono &= all(_cheb(p, d) >= min(1, _cheb(l, d)) for p in ps) and (_cheb(l, d) <= 1 or _cheb(ps[-1], d) == 1)
    check(mono, 'track prediction: monotone toward D and stops on the zone edge (Chebyshev 1 from the centre)')
    check(_icpt((10, 10), (40, 10), 0, 0, (20, 10)) <= _rounds(4, 0) and _icpt((10, 10), (40, 10), 0, 0, (5, 10)) == 16,
          'intercept rule: a duck ahead meets the carrier no later than it arrives; a duck behind catches up at t=16')
    # g4contact (convoy plan 3.4): chainPoint = step(L, D, min(m, cheb - 1)) with m = age * 9 >> 4; null once m > cheb (the
    # predicted arrival has passed). Hand cases: age 0 is L; age 16 is 9 tiles toward a far D; each axis is capped; L next
    # to D stays at L until m reaches 2 (age 4: null); on a 10-tile line the point stops at cheb - 1 until m = 11 (age 20)
    def _cpoint(l, d, age):
        m, n = age * 9 >> 4, _cheb(l, d) - 1
        if m > n + 1: return '-'
        s = min(m, n)
        return '%d %d' % (l if s <= 0 else _step(l, d, s))
    cq = [('C 10 10 40 10 0', '10 10'), ('C 10 10 40 10 16', '19 10'), ('C 0 0 3 20 16', '3 9'), ('C 10 10 11 11 2', '10 10'),
          ('C 10 10 11 11 4', '-'), ('C 10 10 20 10 18', '19 10'), ('C 10 10 20 10 20', '-'), ('C 5 5 5 5 0', '5 5')]
    check(all(_cpoint(tuple(map(int, q.split()[1:3])), tuple(map(int, q.split()[3:5])), int(q.split()[5])) == w for q, w in cq),
          'chainPoint python reference: the hand cases')
    for _ in range(300):
        l = (rnd.randrange(60), rnd.randrange(60)); d = (rnd.randrange(60), rnd.randrange(60)); age = rnd.randrange(0, 70)
        cq.append((f'C {l[0]} {l[1]} {d[0]} {d[1]} {age}', _cpoint(l, d, age)))
    got = subprocess.run([os.path.join(HERE, 'replay-dump.sh'), '--calc'], input='\n'.join(q for q, _ in cq) + '\n', capture_output=True, text=True).stdout.split('\n')
    bad = [(q, w, g) for (q, w), g in zip(cq, got) if w != g]
    check(len(got) >= len(cq) and not bad, f'replay-dump --calc C: chainPoint matches the python reference ({len(bad)} mismatches, e.g. {bad[:3]})')
    # the census tally (convoy plan section 5): scripted chains and dive turns into one running ChainTally, each query
    # printing the cumulative CHAIN_COLS; a python reference recomputes them from scratch after each query
    def _chain_cols(chains, dives):
        u = [c for c in chains if c[0] < 12]; p = [c for c in chains if c[0] >= 12]
        judged = [c for c in u if c[2]]; fr = [o for c in u for o in c[2]]   # T = 0 chains have no window to judge
        closed = lambda cs: [c for c in cs if c[1] != 'OPEN']; caps = lambda cs: sum(c[1] == 'CAPTURE' for c in cs)
        return ','.join([_jf(sum(all(o[0] == 0 for o in c[2][:10]) for c in judged), len(judged)),
                         _jf(sum(o[0] >= 1 for o in fr), len(fr)), _jf(sum(o[0] == 0 and o[1] >= 1 for o in fr), len(fr)),
                         str(len(u)) if chains else '', str(len(p)) if chains else '', _jf(caps(u), len(closed(u))), _jf(caps(p), len(closed(p))),
                         str(len(dives)) if u else '', _jf(sum(g >= 12 for g in dives), len(dives)), _jf(sum(g < 0 for g in dives), len(dives))])
    script = [('V', -1),                                                                     # a dive before any chain: diveTurns blank
              ('X', (5, 'CAPTURE', [(0, 2), (1, 3), (2, 4)] + [(1, 1)] * 9)),               # u12, contact from t2
              ('X', (8, 'RETURN', [(0, 1), (0, 3)] * 6)),                                    # u12, screened throughout
              ('X', (3, 'RETURN', [(0, 0)] * 4)),                                           # u12, returned at t5, no contact
              ('X', (14, 'CAPTURE', [(3, 5)] * 8)),                                         # 12+: outside the u12 columns
              ('X', (11, 'OPEN', [(0, 0)] * 10 + [(2, 2)] * 4)),                            # u12 OPEN, contact only after t10
              ('X', (2, 'CAPTURE', [])),                                                    # T = 0: counted, not judged
              ('X', (12, 'RETURN', [(0, 0)] * 3)),                                          # g0 = 12 is 12+
              ('V', 5), ('V', 14), ('V', -1), ('V', 12), ('V', 0)]
    lines, chains_, dives_, want = [], [], [], []
    for kind, a in script:
        if kind == 'V': dives_.append(a); lines.append(f'V {a}')
        else: chains_.append(a); lines.append(f'X {a[0]} {a[1]} ' + ' '.join(f'{x} {y}' for x, y in a[2]))
        want.append(_chain_cols(chains_, dives_))
    got = subprocess.run([os.path.join(HERE, 'replay-dump.sh'), '--calc'], input='\n'.join(lines) + '\n', capture_output=True, text=True).stdout.split('\n')
    bad = [(q, w, g) for q, w, g in zip(lines, want, got) if w != g]
    check(len(got) >= len(lines) and not bad, f'replay-dump --calc X/V: the chain census tally matches the python reference ({len(bad)} mismatches, e.g. {bad[:2]})')
    check(want[0] == ',,,,,,,,0.000,1.000' and want[-1] == '0.750,0.357,0.310,5,2,0.500,0.500,6,0.333,0.333',
          f'chain tally reference by hand: blanks before a chain; 3 of the 4 judged u12 chains (T >= 1) without contact in t1..10, '
          f'contact 15/42 and screened 13/42 u12 flag-rounds, captures 2/4 closed u12 (OPEN out) and 1/2 closed 12+, dives 2/6 leak, 2/6 none ({want[-1]!r})')

if 'outs' in globals():   # the combined dumps started in the replay-dump block above
    from fractions import Fraction as _Fr
    import math as _m
    def _exact(r): return _Fr(int(r['chaserSum']), int(r['chaserRounds'])) if int(r['chaserRounds']) > 0 else _Fr(0)
    def _stored(text):
        m = _re.search(r'^# commStored (\d+)/(\d+)$', text, _re.M)
        return (int(m[1]), int(m[2])) if m else None
    fx = outs['fix']
    comm = fx.get(66, [])
    check(len(comm) == 4000 and {r['team'] for r in comm} == {'A', 'B'}, f'replay-dump --comm: 64 slots for both teams in all 2000 rounds ({len(comm)} rows)')
    check(all(r[f's{k}'] == '0' for r in comm for k in range(1, 64)), 'replay-dump --comm: examplefuncsplayer writes slot 0 only')
    s0 = {(r['round'], r['team']): r['s0'] for r in comm}
    check(s0.get(('1', 'A')) == '0' and s0.get(('250', 'A')) == '3' and s0.get(('250', 'B')) == '1' and s0.get(('2000', 'A')) == '16',
          'replay-dump --comm: the fixture\'s slot-0 values are stable (r1 0, r250 A3/B1, r2000 A16)')
    # per-round recording (2.11): every Round carries a full CommTable, so no printed value is a carried-forward one
    check(_stored(raw['fix']) == (2000, 2000), f'replay-dump --comm: every one of the fixture\'s 2000 rounds stores the shared array ({_stored(raw["fix"])})')
    if 'own' in outs:
        oc = outs['own'].get(66, [])
        check(any(r['team'] == 'A' and r['round'] == '1' and r['s0'] == '50' for r in oc), 'replay-dump --comm (committed fixture of our build): slot 0 = 50 after round 1 (design 2.11)')
        check(_stored(raw['own']) == (746, 746), f'replay-dump --comm (our build): all 746 rounds store the shared array ({_stored(raw["own"])})')
    # the return window under the end-of-round model: a dropped flag that nobody picks up is back home exactly 4 rounds later
    gaps = []
    for k in ('fixflags', 'ownflags'):
        last = {}
        for m in _re.finditer(r'^r(\d+) (PICKUP|PLACE|CAPTURE)\s+flag=(\w+)', raw.get(k, ''), _re.M):
            rn, ev, f = int(m[1]), m[2], m[3]
            if rn <= 200: continue
            if ev == 'PLACE' and last.get(f, ('', 0))[0] == 'DROP': gaps.append(rn - last[f][1]); last[f] = ('HOME', rn)
            else: last[f] = ('DROP' if ev == 'PLACE' else ev, rn)
    check(len(gaps) >= 2 and set(gaps) == {4}, f'replay truth: a dropped flag left alone is home at the end of round drop+4 (inside-window = rn < r0+4; gaps {gaps})')
    # the chain census's boundaries (convoy plan section 5) from --flags alone: a post-setup pickup of a flag lying on its home
    # tile starts a chain; CAPTURE ends it, so does a carrier-less PLACE (the reset home) and so does a first grab of the flag
    # while its chain is open (a carrier dropped it on its own home tile and it was re-grabbed before the reset: RETURN at the
    # re-grab round). Each chain as (team, grab, flag, outcome, end round of a RETURN); D0 rows end at grab + T + 1
    def _flag_chains(text):
        home, at, held, open_, team_, done = {}, {}, set(), {}, {}, []
        for m in _re.finditer(r'^r(\d+) (PICKUP|PLACE|CAPTURE)\s+flag=([AB])(\d+) actor=\S+ loc=\((\d+),(\d+)\)', text, _re.M):
            rn, ev, ft, f, loc = int(m[1]), m[2], m[3], int(m[4]), (int(m[5]), int(m[6]))
            def end(how):
                if f in open_: done.append((ft, open_.pop(f), f, how, rn if how == 'RETURN' else None))
            if ev == 'PICKUP':
                if rn > 200 and at.get(f) is not None and at.get(f) == home.get(f): end('RETURN'); open_[f] = rn; team_[f] = ft
                held.add(f); at[f] = None
            elif ev == 'PLACE':
                if rn == 200 or (rn > 200 and f not in held):
                    home[f] = loc
                    if rn > 200: end('RETURN')
                held.discard(f); at[f] = loc
            else: end('CAPTURE'); held.discard(f); at[f] = None
        return sorted(done + [(team_[f], g, f, 'OPEN', None) for f, g in open_.items()])
    for k, fk in (('fix', 'fixflags'), ('own', 'ownflags')):
        if k not in outs or fk not in raw: continue
        want = _flag_chains(raw[fk])
        got = sorted((r['team'], int(r['grab']), int(r['flag']), r['outcome'], int(r['grab']) + int(r['T']) + 1 if r['outcome'] == 'RETURN' else None)
                     for r in _pick(outs[k], 'enObsMax'))
        check(want and got == want, f'replay-dump --contact-d0 ({k}): chains start, end and score as the --flags reference (CAPTURE; RETURN on a reset '
              f'or on a re-grab from the home tile; OPEN) ({got} vs {want})')
        if k == 'own':   # B399 dropped on its home tile at r290 and re-grabbed at r294, before its r294 reset
            check(('B', 290, 399, 'RETURN', 294) in got and ('B', 294, 399, 'RETURN', 362) in got,
                  f'replay-dump --contact-d0 (our build): the r290 chain on B399 ends RETURN at its r294 re-grab, which opens the next chain')
    OLD_CAP = ('team,name,won,rounds,wintype,gathered200,gathered400,firstEnemySide,inEnemy250,inEnemy300,firstFlagSight,pickups,captured,carrierDeaths,'
               'carrierRounds,carrierMoves,enemyCarrierKills,trapsBuilt,trapsHit,kills,deaths,meanAlive,postPickups,firstGrabs,regrabs,relayPickups,'
               'carrierDeathDist,damStage199,enemyRegrabs,enemyFirstGrabs,regrabsLate,capturedLate,chasers20,enemyCaptured,escorts20,stillPost').split(',')
    NEW_CAP = ['enemyUnseenRounds', 'unopposedCaps', 'longTrips25', 'longCaps25', 'longCapRate', 'loneDeaths', 'trickleDeaths', 'symOk', 'psymOk', 'maxBcK', 'overruns', 'exceptions', 'symDecidedRound', 'symWrong', 'alertWrites', 'alertNoThreat', 'maxParkOnHome', 'efStaleCarry', 'efStaleLoc', 'flagDistMin', 'flagDistMean', 'carrierStunBuilds', 'carrierStunned', 'captured600', 'enemyCaptured600', 'defNearAtGrab20', 'capturedHomeRounds', 'stunTrig', 'stunVictims', 'enemyStunTrig', 'enemyStunVictims', 'stunVictimsEsc', 'enemyStunVictimsEsc', 'stunVictimsFast', 'enemyStunVictimsFast', 'deathsHome', 'enemyDeathsHome', 'gatheredAll', 'dropGuard', 'digsLate', 'levelGain1500', 'gathered201to400', 'stunTrig250', 'kills250', 'deaths250', 'levelGain1200', 'levelGapEnd',
               'noContact10u12', 'contact20u12', 'screened20u12', 'chainsU12', 'chains12p', 'capRateU12', 'capRate12p', 'diveTurns', 'diveLeak12', 'diveNoChain', 'flagSpreadMin', 'flagSpreadMax', 'carrierDeathsSpawn', 'paidKillShare', 'homeDeathShare', 'healThreat10', 'readyHeld20', 'spawnNear20', 'spawnDeath10', 'bank1900']
    CHAIN_SHARES = ('noContact10u12', 'contact20u12', 'screened20u12', 'capRateU12', 'capRate12p', 'diveLeak12', 'diveNoChain')
    D0_COLS = ('team,grab,flag,g0,outcome,T,seenT0,noContact10,enObsMax,liveRounds,unseenLive,noPoint,elig12,elig10,elig8,elig12r144,'
               + ','.join(f'err{a}' for a in range(1, 13))).split(',')
    _iv = lambda v: int(v) if v != '' else 0
    def num_or_blank(v):
        try: return v == '' or float(v) >= 0
        except ValueError: return False
    for name, o in outs.items():
        cap = _pick(o, 'enemyCarrierKills'); trk = o.get(29, []); dfn = o.get(11, []); cm = o.get(66, []); d0r = _pick(o, 'enObsMax')
        check(len(cap) == 2 and list(cap[0].keys()) == OLD_CAP + NEW_CAP, f'replay-dump --capabilities ({name}): existing columns kept in order, S0a columns appended')
        SIGNED = {'levelGain1500', 'levelGain1200', 'levelGapEnd'}   # differences may be negative
        check(all(num_or_blank(c[k]) or (k in SIGNED and re.fullmatch(r'-\d+', c[k] or '')) for c in cap for k in NEW_CAP),
              f'replay-dump --capabilities ({name}): new columns blank or numeric (signed where a difference)')
        check(all(c['longCapRate'] == '' or abs(float(c['longCapRate']) - int(c['longCaps25']) / int(c['longTrips25'])) < 1e-3 for c in cap),
              f'replay-dump --capabilities ({name}): longCapRate = longCaps25 / longTrips25')
        check(all((c['longTrips25'] == '') == (c['longCaps25'] == '') == (c['longCapRate'] == '') and c['longTrips25'] != '0' for c in cap),
              f'replay-dump --capabilities ({name}): longTrips25, longCaps25, longCapRate are all blank without a 25+ round trip (2.11), never 0')
        check(all(int(c['captured600']) <= int(c['captured']) and capd0[c['team']]['enemyCaptured600'] == capd0['B' if c['team'] == 'A' else 'A']['captured600'] for c in cap) if (capd0 := {c['team']: c for c in cap}) else True,
              f'replay-dump --capabilities ({name}): captured600 <= captured; enemyCaptured600 is the other row\'s captured600')
        check(all((c['paidKillShare'] == '' or abs(float(c['paidKillShare']) - int(c['enemyDeathsHome']) / int(c['kills'])) < 1e-3)
                  and (c['homeDeathShare'] == '' or abs(float(c['homeDeathShare']) - int(c['deathsHome']) / int(c['deaths'])) < 1e-3) for c in cap),
              f'replay-dump --capabilities ({name}): paidKillShare = enemyDeathsHome / kills, homeDeathShare = deathsHome / deaths')
        check(all(c[k] == '' or 0 <= float(c[k]) <= 1 for c in cap for k in ('healThreat10', 'readyHeld20', 'spawnNear20', 'spawnDeath10')) and any(c['readyHeld20'] not in ('', '0.000') for c in cap),
              f'replay-dump --capabilities ({name}): healThreat10, readyHeld20, spawnNear20, spawnDeath10 are shares, and some robot holds a ready strike near an enemy')
        check(all(0 <= int(c['carrierDeathsSpawn']) <= int(c['carrierDeaths']) for c in cap),
              f'replay-dump --capabilities ({name}): carrierDeathsSpawn is a subset of carrierDeaths')
        check(all(c['flagSpreadMin'] != '' and 0 < float(c['flagSpreadMin']) <= float(c['flagSpreadMax']) for c in cap),
              f'replay-dump --capabilities ({name}): flagSpreadMin (r200, nearest two own flags) > 0 and <= flagSpreadMax')
        check(all(c['flagDistMin'] != '' and 0 < float(c['flagDistMin']) <= float(c['flagDistMean']) for c in cap),
              f'replay-dump --capabilities ({name}): flagDistMin (r200 own flag to nearest enemy spawn centre) > 0 and <= flagDistMean')
        ours = [c for c in cap if name != 'colt' or c['team'] == 'A']   # an external bot may use slot 23 for its own purposes
        check(all(c['psymOk'] == '' for c in ours) and all(float(c['maxBcK']) <= 25.0 and int(c['overruns']) >= 0 for c in cap),
              f'replay-dump --capabilities ({name}): psymOk blank without the tracker slots; maxBcK in thousands, overruns a count')
        # g4contact chain census (convoy plan section 5)
        check(all(c[k] == '' or 0 <= float(c[k]) <= 1 for c in cap for k in CHAIN_SHARES), f'replay-dump --capabilities ({name}): chain-census shares in [0,1]')
        check(all(_iv(c['chainsU12']) + _iv(c['chains12p']) == int(c['enemyFirstGrabs']) and (c['chainsU12'] == '') == (c['chains12p'] == '') == (c['enemyFirstGrabs'] == '0')
                  for c in cap), f'replay-dump --capabilities ({name}): chainsU12 + chains12p = enemyFirstGrabs, both blank only without a grab')
        check(all(c['contact20u12'] == '' or float(c['contact20u12']) + float(c['screened20u12']) <= 1.001 for c in cap),
              f'replay-dump --capabilities ({name}): contact20u12 + screened20u12 <= 1 (disjoint flag-rounds; 3-decimal rounding)')
        check(all(c['diveTurns'] in ('', '0') and (c['diveTurns'] == '') == (_iv(c['chainsU12']) == 0) and c['diveLeak12'] == c['diveNoChain'] == '' for c in cap),
              f'replay-dump --capabilities ({name}): no dive note before C.CONTACT: diveTurns 0 (blank without a u12 chain), leak shares blank')
        capd = {c['team']: c for c in cap}
        check(d0r and list(d0r[0].keys()) == D0_COLS, f'replay-dump --contact-d0 ({name}): the D0_COLS header')
        for us in sorted({c['team'] for c in cap}) if name != 'colt' else 'A':   # --contact-d0 rows only for the tracked team
            rs = [r for r in d0r if r['team'] == us]; c = capd[us]; them = 'B' if us == 'A' else 'A'
            u = [r for r in rs if int(r['g0']) < 12]; p = [r for r in rs if int(r['g0']) >= 12]
            judged = [r for r in u if r['noContact10'] != '']
            closed = lambda cs: [r for r in cs if r['outcome'] != 'OPEN']; caps_ = lambda cs: sum(r['outcome'] == 'CAPTURE' for r in cs)
            check(len(rs) == int(c['enemyFirstGrabs']) and len(u) == _iv(c['chainsU12']) and caps_(rs) == int(c['enemyCaptured'])
                  and c['capRateU12'] == _jf(caps_(u), len(closed(u))) and c['capRate12p'] == _jf(caps_(p), len(closed(p)))
                  and c['noContact10u12'] == _jf(sum(r['noContact10'] == '1' for r in judged), len(judged)),
                  f'replay-dump ({name} {us}): one --contact-d0 row per chain; its g0 bands, outcomes (every capture ends a chain) and noContact10 '
                  f'give the census\'s chainsU12, capRateU12, capRate12p and noContact10u12')
            firsts = sorted(int(d['round']) for d in dfn if d['carrierTeam'] == them and d['first'] == '1')
            grabs = sorted(int(r['grab']) for r in rs)
            check(not (collections.Counter(firsts) - collections.Counter(grabs)) and len(grabs) - len(firsts) <= sum(r['outcome'] == 'OPEN' for r in rs),
                  f'replay-dump ({name} {us}): every finished --defense first-grab trip starts a chain (the others are OPEN at the end)')
            check(all(r['outcome'] in ('CAPTURE', 'RETURN', 'OPEN') and r['seenT0'] in ('0', '1') and (r['noContact10'] == '') == (r['T'] == '0')
                      and (r['enObsMax'] == '' or 0 <= int(r['enObsMax']) <= 15)
                      and int(r['elig8']) <= int(r['elig10']) <= int(r['elig12']) <= int(r['elig12r144']) <= int(r['unseenLive']) <= int(r['liveRounds']) <= int(r['T'])
                      and int(r['noPoint']) + sum(len(r[f'err{a}'].split(';')) for a in range(1, 13) if r[f'err{a}']) == int(r['unseenLive'])
                      and all(int(x) >= 0 for a in range(1, 13) for x in r[f'err{a}'].split(';') if x) for r in rs),
                  f'replay-dump --contact-d0 ({name} {us}): eligible <= unseen live <= live <= T; every unseen live round has an error or no point')
        for us in sorted({r['team'] for r in trk}) if name == 'colt' else 'AB':   # the diag run tracks A only (--team A)
            them = 'B' if us == 'A' else 'A'
            th = [r for r in trk if r['team'] == us and r['kind'] == 'their']; ow = [r for r in trk if r['team'] == us and r['kind'] == 'own']
            key = lambda rows, k='round': sorted((int(r[k]), r['outcome'], int(r['tripRounds'])) for r in rows)
            check(key(th) == key([d for d in dfn if d['carrierTeam'] == them]) and key(ow) == key([d for d in dfn if d['carrierTeam'] == us]),
                  f'replay-dump --track ({name} {us}): every trip is a --defense trip with the same start round, outcome and length')
            check(sorted((r['round'], r['tKnow']) for r in th) == sorted((d['round'], d['tKnow']) for d in dfn if d['carrierTeam'] == them),
                  f'replay-dump --defense ({name} {us}): tKnow equals --track\'s on every trip of theirs')
            check(all(int(r['reachFree']) <= int(r['reachAll']) for r in th if r['reachAll']), f'replay-dump --track ({name} {us}): reachFree <= reachAll')
            check(all(int(r['predN']) <= int(r['unseenRounds']) <= int(r['tripRounds']) for r in th), f'replay-dump --track ({name} {us}): predN <= unseenRounds <= tripRounds')
            check(all((r['predErr'] == '') == (r['predErrSym'] == '') == (int(r['unseenRounds']) == 0) for r in th),
                  f'replay-dump --track ({name} {us}): predErr scores every unseen round (HOME belief = home tile), blank only for a fully seen trip')
            check(all(r['destHit'] in ('', '0', '1') and (r['destHit'] == '' or r['outcome'] == 'CAPTURE') for r in th) and all(r['tKnowLive'] in ('', '0', '1') for r in th),
                  f'replay-dump --track ({name} {us}): destHit only on captures; tKnowLive is 0/1')
            check(all((r['tKnow'] == '') == (r['tKnowBy'] == '') and r['tKnowBy'] in ('', 'sight', 'home') and (r['tKnow'] == '' or
                      (r['tKnowLive'] == '1' and r['tKnowState'] == {'sight': 'CARRIED', 'home': 'MISSING'}[r['tKnowBy']])) for r in th),
                  f'replay-dump --track ({name} {us}): tKnow lands on a live belief: CARRIED by a sighting, MISSING by the home tile')
            check(all(r['meanChasers20'] == f'{float(_exact(r)):.4f}' for r in th + ow), f'replay-dump --track ({name} {us}): meanChasers20 = chaserSum / chaserRounds')
            half_up = lambda q: _Fr(_m.floor(q * 10 + _Fr(1, 2)), 10)
            dch = sorted((d['round'], _Fr(d['meanChasers20'])) for d in dfn if d['carrierTeam'] == them)
            check(dch == sorted((r['round'], half_up(_exact(r))) for r in th), f'replay-dump ({name} {us}): --defense meanChasers20 is the half-up 1-decimal of the exact mean')
            eu = capd[us]['enemyUnseenRounds']
            check(sum(int(r['unseenRounds']) for r in th) <= (int(eu) if eu else 0), f'replay-dump --track ({name} {us}): unseen rounds within enemyUnseenRounds')
            unop = sum(1 for r in th if r['outcome'] == 'CAPTURE' and _exact(r) < _Fr(1, 2))
            check(capd[us]['unopposedCaps'] == ('' if not th else str(unop)), f'replay-dump --capabilities ({name} {us}): unopposedCaps = unopposed captured trips of --track (exact mean)')
            check(all(r['symOk'] == capd[us]['symOk'] for r in th + ow), f'replay-dump ({name} {us}): symOk agrees between --track and --capabilities')
            check(all(r['convoyReachFree'] != '' for r in ow), f'replay-dump --track ({name} {us}): every own trip has convoyReachFree, also one that ends in its pickup round')
            check(all(num_or_blank(r[k]) for r in th + ow for k in ('predErr', 'reachInFight', 'ownEscorts20', 'convoyReachFree', 'escorts8', 'chaserSum', 'chaserRounds')),
                  f'replay-dump --track ({name} {us}): numeric columns blank or numeric')
            s16 = {r['s16'] for r in cm if r['team'] == us and int(r['round']) > 200}
            if capd[us]['symOk'] == '1' and len(s16) == 1 and len({r['round'] for r in cm}) > 200:
                check(all(r['predErr'] == r['predErrSym'] and r['destHit'] == r['destHitSym'] for r in th),
                      f'replay-dump --track ({name} {us}): with Sym.best() giving the true centres (slot 16 constant), both beliefs score alike')
    if 'dive' in raw:   # --dive-note '': every post-setup indicator string of our 2026-09-30 build (A) and of examplefuncsplayer (B)
        dl = raw['dive'].splitlines()
        logs_b = sum(1 for l in dl if (m := _re.match(r'r(\d+) B#', l)) and int(m[1]) > 200)
        dc = {c['team']: c for c in csv.DictReader(l for l in dl if not _re.match(r'r\d+ B#', l))}
        a_, b_ = dc.get('A', {}), dc.get('B', {})
        check(logs_b > 0 and b_.get('diveTurns') == str(logs_b) and b_.get('chainsU12', '') not in ('', '0')
              and float(b_['diveLeak12']) + float(b_['diveNoChain']) <= 1.001,
              f'replay-dump --dive-note: diveTurns counts the post-setup strings with the prefix ({b_.get("diveTurns")} vs --logs {logs_b}); leak + no-chain shares <= 1')
        check(a_.get('chainsU12') == '' and a_.get('diveTurns') == '' and a_.get('diveLeak12') == '0.000' and a_.get('diveNoChain') == '1.000',
              f'replay-dump --dive-note: on a side whose flags were never grabbed, diveTurns is blank and every dive turn has no chain ({a_.get("diveNoChain")})')
    if 'd0e2e' in raw:   # tools/contact-d0.py end to end on our build's fixture (our side B: the side whose flags were grabbed)
        e2e = raw['d0e2e']
        check('chains on our flags: 5 (u12 3, 12+ 2)' in e2e and 'ROUTES: R1 ' in e2e and 'replays: 1 found, 1 dumped, 0 failed' in e2e,
              'contact-d0.py: dumps a replay with --contact-d0 and reads the routes: ' + e2e[-600:])
        r = subprocess.run([sys.executable, os.path.join(HERE, 'contact-d0.py'), _d0csv], capture_output=True, text=True)
        check(r.returncode == 0 and r.stdout.splitlines() == e2e.splitlines()[1:], 'contact-d0.py: re-reading its --out CSV gives the same report: ' + r.stdout[-300:])
    shutil.rmtree(_d0dir, ignore_errors=True)
    if 'rd' in raw and 'own' in outs:   # --recall-d0: one t = 0 row per --contact-d0 chain, t = 10 / 20 rows while the chain is open
        RD_COLS = ('team,grab,flag,g0,outcome,T,t,enemy20,enemy10,ours20,ours100,alive,free10,free20,free30,freeFar,busy10,busy20,busy30,busyFar,'
                   'nFight,nTether,nChase,nIcpt,nDefend,nThreat,nExplore,nGather,nOther,kills100,deaths100,stunVict100,enStunVict100').split(',')
        rr = list(csv.DictReader(raw['rd'].splitlines()))
        check(rr and list(rr[0].keys()) == RD_COLS, 'replay-dump --recall-d0: the RD_COLS header')
        key = lambda r: (r['team'], r['grab'], r['flag'], r['g0'], r['outcome'], r['T'])
        check(sorted(key(r) for r in rr if r['t'] == '0') == sorted(key(r) for r in _pick(outs['own'], 'enObsMax')),
              'replay-dump --recall-d0: its t = 0 rows are --contact-d0\'s chains (team, grab, flag, g0, outcome, T)')
        check(all((r['t'] in ('10', '20')) <= (int(r['T']) >= int(r['t'])) for r in rr) and all(
                  any(q['t'] == t and key(q) == key(r) for q in rr) for r in rr if r['t'] == '0' for t in ('10', '20') if int(r['T']) >= int(t)),
              'replay-dump --recall-d0: a t = 10 / 20 row exactly when the chain is open T >= t rounds')
        B = ('free10', 'free20', 'free30', 'freeFar', 'busy10', 'busy20', 'busy30', 'busyFar'); N = [c for c in RD_COLS if c.startswith('n')]
        check(all(sum(int(r[c]) for c in B) == int(r['alive']) and int(r['ours20']) <= int(r['ours100']) <= int(r['alive'])
                  and int(r['enemy20']) <= int(r['enemy10']) and sum(int(r[c]) for c in N) == int(r['free10']) + int(r['free20']) + int(r['free30'])
                  for r in rr), 'replay-dump --recall-d0: the eight bands sum to alive; ours20 <= ours100 <= alive; enemy20 <= enemy10; notes cover the free robots within 30')
        K = ('kills100', 'deaths100', 'stunVict100', 'enStunVict100')
        check(all(all(int(q[k]) >= int(r[k]) for k in K) for r in rr for q in rr if key(q) == key(r) and int(q['t']) > int(r['t'])),
              'replay-dump --recall-d0: the near-flag kills, deaths and stun victims accumulate from t = 0 to 10 to 20')
        check(sum(int(r['stunVict100']) + int(r['enStunVict100']) for r in rr) > 0 and sum(int(r['kills100']) for r in rr) > 0,
              'replay-dump --recall-d0: stun victims and kills are counted without --capabilities (2026-10-06: the stun hook sat in the census-only block)')
        _rd = importlib.util.spec_from_file_location('rd0', os.path.join(HERE, 'recall-d0.py')); rd0 = importlib.util.module_from_spec(_rd); _rd.loader.exec_module(rd0)
        def _route(free, far):
            row = dict(kind='row', t='0', g0='14', outcome='CAPTURE', grab='300', won='0', free10=str(free), free20='0', busy30=str(far), busyFar='0')
            return rd0.evaluate([row, dict(kind='game', won='0', file='x')])[1]['ROUTE'][:2]
        check((_route(6, 0), _route(5, 15), _route(5, 14)) == ('R1', 'R2', 'R3'), 'recall-d0.py: routes R1 at 6 free within 20, R2 at 15 busy beyond 20, else R3')
        def _fight(d):
            mk = lambda o, k: dict(kind='row', t='10', g0='13', outcome=o, grab='300', won='0', kills100=str(k), deaths100='0')
            return rd0.evaluate([mk('RETURN', d), mk('CAPTURE', 0), dict(kind='game', won='0', file='x')])[1]['FIGHT'][:2]
        check((_fight(2), _fight(1)) == ('F1', 'F2'), 'recall-d0.py: fight route F1 when returned big chains out-kill captured ones by >= 1.5 near the flag')
    if 'own' in outs:
        ot = outs['own'].get(29, []); od = outs['own'].get(11, [])
        z = [r for r in ot if r['team'] == 'A' and r['kind'] == 'own' and r['tripRounds'] == '0']
        check(z and all(r['convoyReachFree'] != '' and r['outcome'] == 'DIED' for r in z), 'replay-dump --track (our build): the r290 trip that dies in its pickup round has a convoyReachFree')
        r382 = [r for r in ot if r['team'] == 'B' and r['kind'] == 'their' and r['round'] == '382']
        d382 = [d for d in od if d['round'] == '382']
        check(r382 and r382[0]['chaserSum'] == '25' and r382[0]['chaserRounds'] == '54' and r382[0]['meanChasers20'] == '0.4630' and d382 and d382[0]['meanChasers20'] == '0.5',
              'replay-dump: a 25/54 = 0.463 chaser mean is below 0.5 in --track while --defense\'s 1 decimal prints 0.5 (the two readings differ)')
    if 'colt' in outs:
        cc = outs['colt'].get(66, [])
        check(any(r['team'] == 'A' and r['round'] == '1' and r['s0'] == '50' for r in cc), 'replay-dump --comm: our bot\'s slot 0 counts 50 robots after round 1')
        check(any(r['team'] == 'A' and r['round'] == '1' and 1 <= int(r['s16']) <= 7 for r in cc), 'replay-dump --comm: our bot\'s slot 16 holds a symmetry mask')
        ct = outs['colt'].get(29, [])
        check(ct and all(r['team'] == 'A' for r in ct), 'replay-dump --track --team A: only the tracked team\'s rows')
        th = [r for r in ct if r['team'] == 'A' and r['kind'] == 'their']
        check(sum(r['outcome'] == 'CAPTURE' for r in th) == 3 and all(r['tKnow'] != '' for r in th if r['outcome'] == 'CAPTURE'),
              'replay-dump --track (local diag game): the opponent\'s 3 captures are tracked trips with a tKnow')
    # local regression (diag/ is not committed): EndAround's 4 drops whose flag went home at r0+4 raised phantom re-grabs
    if 'endlog' in raw:
        lg = raw['endlog']
        prev, phantom = {}, 0
        for m in _re.finditer(r'^# r(\d+) A flag=(\d+) (\w+)(\*?) .* truth=(\S+)( carried)?', lg, _re.M):
            if prev.get(m[2]) == 'DROPPED' and m[3] == 'CARRIED' and m[4] == '*' and not m[6]: phantom += 1
            prev[m[2]] = m[3]
        check(phantom == 0, f'replay-dump --track-log (local EndAround): no unseen re-grab inferred for a flag that is not carried ({phantom})')

# premise.py: the S0a bars on a hand-made trip CSV
spec = importlib.util.spec_from_file_location('premise', os.path.join(HERE, 'premise.py')); premise = importlib.util.module_from_spec(spec); spec.loader.exec_module(premise)
def _trip(kind, rounds, outcome, ch, **kw):
    r = dict(file=kw.pop('file', 'g1.bc24'), team='A', kind=kind, round='300', flag='1', first='1', outcome=outcome, tripRounds=str(rounds), meanChasers20=str(ch),
             defNear20='0', defDied10='0', unseenRounds='0', predN='0', predErr='', predErrSym='', destHit='', destHitSym='', tKnow='', tKnowBy='', tKnowState='',
             tKnowLive='', reachAll='', reachFree='', reachInFight='', escorts8='', ownEscorts20='', convoyReachFree='', symOk='1')
    r.update({k: str(v) for k, v in kw.items()}); return r
rows = [  # long captured: predErr 2,3,5,6 -> median 4.0 (pass); destHit 1,1,1,0 -> 0.75 (fail); U = the three with chasers < 0.5
    _trip('their', 30, 'CAPTURE', 0.2, predErr=2, unseenRounds=9, destHit=1, tKnow=0, tKnowBy='sight', tKnowLive=1, reachAll=5, reachFree=3, reachInFight=0.4, defNear20=2, defDied10=2),
    _trip('their', 40, 'CAPTURE', 0.4, predErr=3, unseenRounds=9, destHit=1, tKnow=2, tKnowBy='home', tKnowLive=1, reachAll=4, reachFree=2, reachInFight=0.5, defNear20=1, defDied10=0),
    _trip('their', 26, 'CAPTURE', 0.0, predErr=5, unseenRounds=26, destHit=1),                       # never known: counts 0 in P2
    _trip('their', 50, 'CAPTURE', 1.5, predErr=6, unseenRounds=9, destHit=0, tKnow=0, tKnowLive=1, reachAll=9, reachFree=9, file='g2.bc24', symOk=0),
    _trip('their', 25, 'DIED', 1.2), _trip('their', 25, 'DROP', 2.0), _trip('their', 60, 'DIED', 3.0), _trip('their', 30, 'DIED', 0.1),
    _trip('their', 10, 'CAPTURE', 0.0),                                                              # short: outside every bar
    _trip('own', 8, 'CAPTURE', 1, ownEscorts20=4, convoyReachFree=1), _trip('own', 9, 'DIED', 1, ownEscorts20=3.5, convoyReachFree=2),
    _trip('own', 7, 'DIED', 1, ownEscorts20=1.0, convoyReachFree=3), _trip('own', 12, 'DIED', 1, ownEscorts20=0.5, convoyReachFree=2),
    _trip('own', 3, 'CAPTURE', 1, ownEscorts20=0.0, convoyReachFree=0),
    _trip('game', '', 'LOST', '', file='g3.bc24', symOk=0)]                                           # a game without trips still counts for symOk
lines, V = premise.evaluate(rows)
txt = '\n'.join(lines)
check(V == {'P1': 'PASS', 'P2': 'DOSE_A', 'P3': 'FAIL', 'P4': 'PASS', 'P6': 'PASS', 'PARTIAL': False}, f'premise: verdicts on the hand-made trips {V}')
check(lines[0] == 'replays: 1 found, 1 dumped, 0 failed', 'premise: the first line counts replays found, dumped and failed: ' + lines[0])
check('median predErr (true symmetry) = 4.0' in txt and '4/4 long captured' in txt, 'premise P1: median of per-trip predErr over long captured trips')
check('1/3 = 0.333 +- 0.272' in txt and 'by sighting 1 (1 with reachFree >= 3), by the home tile 1 (0)' in txt,
      'premise P2: share of U with reachFree >= 3, no tKnow counted as 0, binomial SE; split by how tKnow came')
check('3/4 = 0.750 +- 0.217' in txt, 'premise P3: destHit share with binomial SE')
check('median convoyReachFree at pickup = 2.0 over 5' in txt and '1/2 = 0.500' in txt and '0/2 = 0.000' in txt and 'ratio inf' in txt,
      'premise P4: convoy median over our trips; escort split on our trips >= 6 rounds')
check('1/4 = 0.250' in txt and '3/4 = 0.750' in txt and '= 0.33 +-' in txt, 'premise P6: capture-rate ratio chasers20 >= 1 vs < 0.5 on long trips')
check('2/3 = 0.667' in txt and 'jailed within 10 rounds: 2/3' in txt and '"died") 1' in txt and 'symOk: 1/3' in txt and 'games 3' in txt,
      'premise D: seen at pickup, witnesses jailed within 10 rounds, symOk per game')
check('ROUTING: CUT: build dose A only' in txt and 'one tracker revision' in txt, 'premise: routing lines follow the failed bars')
with tempfile.TemporaryDirectory() as d:
    p = os.path.join(d, 'trips.csv')
    with open(p, 'w', newline='') as f:
        w = csv.DictWriter(f, fieldnames=list(rows[0].keys())); w.writeheader(); w.writerows(rows)
    r = subprocess.run([sys.executable, os.path.join(HERE, 'premise.py'), p], capture_output=True, text=True)
    check(r.returncode == 0 and 'P2 share of U' in r.stdout and 'ROUTING:' in r.stdout, 'premise.py reads a trip CSV written by --out: ' + r.stderr[-200:])
check(premise.side_of('x/opp__Map__botB.bc24') == 'B' and premise.side_of('diag/b2rg/b2rg-Tunnels.bc24') == 'A', 'premise: our side from the replay name, else A')

def _ev(rows): l, v = premise.evaluate(rows); return '\n'.join(l), v
_long = lambda n, outcome, ch, **kw: [_trip('their', 30, outcome, ch, **kw) for _ in range(n)]
# P1: a trip with unseen rounds but no predErr is a failure, not missing data; a fully seen trip is outside the subset
t, v = _ev(_long(4, 'CAPTURE', 0.2, predErr=2, unseenRounds=9) + _long(1, 'CAPTURE', 0.2, unseenRounds=30) + _long(1, 'CAPTURE', 0.2, unseenRounds=0))
check('over 5/6 long captured' in t and '1 fully seen; 1 without predErr counted as failures' in t and v['P1'] == 'PASS',
      'premise P1: a long capture with unseen rounds and no predErr counts as a failure (inf); fully seen trips are counted out')
t, v = _ev(_long(2, 'CAPTURE', 0.2, predErr=2, unseenRounds=9) + _long(3, 'CAPTURE', 0.2, unseenRounds=30))
check(v['P1'] == 'FAIL' and 'predErr (true symmetry) = inf' in t, 'premise P1: unscored trips can fail the bar (median inf)')
# P3: no destination is a miss (7 hits, 3 never had one -> 7/10 FAIL), the conditional share printed beside it
t, v = _ev(_long(7, 'CAPTURE', 0.2, destHit=1, destHitSym=1) + _long(3, 'CAPTURE', 0.2))
check(v['P3'] == 'FAIL' and '7/10 = 0.700' in t and 'among trips with one 7/7 = 1.000' in t and 'destHitSym 7/10' in t,
      'premise P3: the pinned subset is every long captured trip; a blank destHit counts as 0 (and for destHitSym)')
t, v = _ev(_long(4, 'CAPTURE', 0.2, destHit=1) + _long(1, 'CAPTURE', 0.2, destHit=0))
check(v['P3'] == 'PASS', 'premise P3: 4/5 = 0.8 meets the bar exactly')
# P4 (ii): an empty escort group is NO DATA and closes nothing
own_ = [_trip('own', 8, 'CAPTURE', 1, ownEscorts20=4, convoyReachFree=3)]
t, v = _ev(own_ + _long(1, 'CAPTURE', 0.2))
check(v['P4'] == 'NO DATA' and 'S3 closed' not in t and 'NO DATA (an empty group)' in t, 'premise P4(ii): an empty group is NO DATA, never "S3 closed"')
t, v = _ev(own_ + [_trip('own', 8, 'DIED', 1, ownEscorts20=0.5, convoyReachFree=1)] + _long(1, 'CAPTURE', 0.2))
check(v['P4'] == 'PASS' and 'ratio inf' in t, 'premise P4(ii): rate 0 in the low-escort group is ratio inf (ok)')
t, v = _ev([_trip('own', 8, 'DIED', 1, ownEscorts20=4, convoyReachFree=0), _trip('own', 8, 'DIED', 1, ownEscorts20=1, convoyReachFree=0)])
check(v['P4'] == 'FAIL' and 'S3 closed' in t, 'premise P4: (i) failing closes S3 even when (ii) has no data (both rates 0)')
# P6: rate 0 among the low-chaser trips is ratio inf -> FAIL (CUT closed); both rates 0 or an empty group is NO DATA
t, v = _ev(_long(4, 'DIED', 0.1) + _long(1, 'CAPTURE', 2.0) + _long(1, 'DIED', 2.0))
check(v['P6'] == 'FAIL' and '= inf; bar <= 0.6: FAIL' in t and 'CUT closed' in t, 'premise P6: rate(< 0.5) = 0 with rate(>= 1) > 0 is FAIL, routing closes CUT')
t, v = _ev(_long(4, 'DIED', 0.1) + _long(2, 'DIED', 2.0))
check(v['P6'] == 'NO DATA' and 'both rates 0' in t, 'premise P6: both rates 0 is NO DATA')
# ratio bars at the exact boundary: (1/5)/(1/3) = 0.6 passes P6; (3/5)/(2/5) = 1.5 holds P4(ii) (float division gets both wrong)
t, v = _ev(_long(1, 'CAPTURE', 1.5) + _long(4, 'DIED', 1.5) + _long(1, 'CAPTURE', 0.1) + _long(2, 'DIED', 0.1)
           + [_trip('own', 8, o, 1, ownEscorts20=4, convoyReachFree=3) for o in ['CAPTURE'] * 3 + ['DIED'] * 2]
           + [_trip('own', 8, o, 1, ownEscorts20=1, convoyReachFree=3) for o in ['CAPTURE'] * 2 + ['DIED'] * 3])
check(v['P6'] == 'PASS' and v['P4'] == 'PASS' and 'ratio 1.50; bar >= 1.5: ok' in t and '= 0.60 +-' in t,
      f'premise: a ratio exactly on the bar meets it (integer comparison; P6 {v["P6"]}, P4 {v["P4"]})')
# chasers: the exact mean decides (99/199 = 0.4975 is below 0.5 although 2 decimals print 0.50); the 1-decimal reading is shown beside
t, v = _ev([_trip('their', 120, 'CAPTURE', '0.4975', chaserSum=99, chaserRounds=199, reachFree=4, tKnow=0, tKnowBy='sight')] + _long(1, 'CAPTURE', 0.2, reachFree=0))
check('U (captured, meanChasers20 < 0.5 unrounded): 2' in t and '1/2 = 0.500' in t and 'moves 1 of 2 long trips' in t and 'U 1; P2 0/1' in t
      and 'VERDICT DIFFERS on P2' in t, 'premise: U uses the exact chaser mean; the B3 1-decimal reading and its verdict are printed beside it')
# PARTIAL: a failed dump or a cell without a replay suppresses routing, also when the trip CSV is read back
t, v = _ev(rows + [dict(file='x__M__botA.bc24', kind='error', outcome='GZIP EOF')])
check(v['PARTIAL'] and t.startswith('replays: 2 found, 1 dumped, 1 failed') and 'FAILED x__M__botA.bc24' in t and 'ROUTING' not in t and 'PARTIAL:' in t,
      'premise: a failed dump is counted on the first line and the bars are marked PARTIAL with no routing')
with tempfile.TemporaryDirectory() as d:
    P = [sys.executable, os.path.join(HERE, 'premise.py')]
    r = subprocess.run(P + [os.path.join(d, 'no-such-run')], capture_output=True, text=True)
    check(r.returncode == 2 and 'no such run dir' in r.stderr, 'premise.py: a run dir that does not exist is an error (exit 2)')
    os.makedirs(os.path.join(d, 'empty'))
    r = subprocess.run(P + [os.path.join(d, 'empty')], capture_output=True, text=True)
    check(r.returncode == 2 and 'no .bc24' in r.stderr, 'premise.py: a run dir without replays is an error (exit 2)')
    run = os.path.join(d, 'run'); os.makedirs(os.path.join(run, 'losses'))
    with open(os.path.join(run, 'results.csv'), 'w') as f:
        f.write('opponent,map,bot_side,winner_side,rounds,bot_result,reason,seed\nopp,M1,A,B,2000,loss,x,1\nopp,M1,A,B,2000,loss,x,2\nopp,M2,B,B,2000,win,x,3\n')
    with open(FIX if os.path.exists(FIX) else __file__, 'rb') as f: head = f.read(5000)
    with open(os.path.join(run, 'losses', 'opp__M1__botA.bc24'), 'wb') as f: f.write(head)            # truncated: the dump fails
    out_csv = os.path.join(d, 'trips.csv')
    r = subprocess.run(P + ['--out', out_csv, run], capture_output=True, text=True)
    check(r.returncode == 1 and 'input ' + run + ': 1 replays; results.csv 2 distinct (opponent, map, side) cells, 1 with a replay, 1 WITHOUT one' in r.stdout
          and 'replays: 1 found, 0 dumped, 1 failed; 1 results.csv cells without a replay' in r.stdout and 'PARTIAL' in r.stdout and 'ROUTING' not in r.stdout,
          'premise.py: per-argument replay count against results.csv cells; failed and missing replays make the report PARTIAL (exit 1): ' + r.stdout[-400:])
    kinds = [x['kind'] for x in csv.DictReader(open(out_csv))] if os.path.exists(out_csv) else []
    check(sorted(kinds) == ['error', 'missing'], f'premise.py --out: the failed replay and the missing cell are rows of the trip CSV ({kinds})')
    r = subprocess.run(P + [out_csv], capture_output=True, text=True)
    check(r.returncode == 1 and 'PARTIAL' in r.stdout and 'ROUTING' not in r.stdout, 'premise.py: re-reading that CSV reports the same shortfall (exit 1)')

# contact-d0.py: the D0 routes (convoy plan section 6) on hand-made chain rows
spec = importlib.util.spec_from_file_location('cd0', os.path.join(HERE, 'contact-d0.py')); cd0 = importlib.util.module_from_spec(spec); spec.loader.exec_module(cd0)
def _d0(file, g0, nc='0', seen='0', unseen=0, e12=0, e10=0, e8=0, e144=0, errs=None, outcome='CAPTURE', T=12, noPoint=0):
    r = dict(file=file, kind='chain', team='A', grab='300', flag='1', g0=str(g0), outcome=outcome, T=str(T), seenT0=seen, noContact10=nc, enObsMax='5',
             liveRounds=str(unseen), unseenLive=str(unseen), noPoint=str(noPoint), elig12=str(e12), elig10=str(e10), elig8=str(e8), elig12r144=str(e144))
    for a in range(1, 13): r[f'err{a}'] = ';'.join(map(str, (errs or {}).get(a, [])))
    return r
_g = lambda *fs: [dict(file=f, kind='game') for f in fs]
# A: errors 1-3 at every age (M(12) 2.0 <= 3); reach exactly 1/4; 12+ share of elig12 exactly 1/4; baseline mean(1/2, 0) = 1/4
#    with 1/1 no-contact chain seen at t0 -> every bar met at its boundary
rowsA = _g('g1', 'g2') + [_d0('g1', 5, nc='1', seen='1', unseen=8, e12=3, e10=3, e8=3, e144=4, errs={a: [1, 2, 3] for a in range(1, 13)}),
                          _d0('g1', 6, nc='0', unseen=4, e12=0, errs={1: [2]}), _d0('g2', 9, nc='0', unseen=0), _d0('g2', 7, nc='', T=0),
                          _d0('g2', 13, e12=1, e10=0, e8=0)]
t, v = (lambda lv: ('\n'.join(lv[0]), lv[1]))(cd0.evaluate(rowsA))
check(v == {'PARTIAL': False, 'R1': 'CT_HOLD 12', 'R2': 'BUILD', 'R3': 'CT_GROUP_MAX 12', 'R4': 'rel:noContact10u12<=0.8'}
      and 'elig12 / unseenLive over u12 chains = 3/12 = 0.250' in t and 'mean of 2 games) 0.250, pooled 1/3' in t and 'chains on our flags: 5 (u12 4, 12+ 1)' in t,
      f'contact-d0: bars met exactly on their boundaries (fractions, not floats): {v}\n{t}')
# B: per-age errors 2 up to age 8, 9 after (M(8) 2 <= 3 < M(12)); reach 3/15 = 0.2; 12+ share 1/2 at 12, 1/4 at 10; seen at t0 1/4
rowsB = _g('g1') + [_d0('g1', 5, nc='1', seen='1', unseen=15, e12=3, e10=3, e8=1, e144=6, errs={**{a: [2] for a in range(1, 9)}, **{a: [9, 9, 9] for a in range(9, 13)}})]
rowsB += [_d0('g1', 4, nc='1') for _ in range(3)] + [_d0('g1', 14, e12=3, e10=1, e8=0)]
t, v = (lambda lv: ('\n'.join(lv[0]), lv[1]))(cd0.evaluate(rowsB))
check(v['R1'] == 'CT_HOLD 8' and v['R2'] == 'CT_DIVE_R2 144' and v['R3'] == 'CT_GROUP_MAX 10' and v['R4'] == 'rel:screened20u12<=0.8'
      and 'seen at t0 1/4 = 0.250' in t and 'CT_GROUP_MAX 12: eligible rounds from true g0 >= 12 chains 3/6 = 0.500' in t,
      f'contact-d0: CT_HOLD 8, CT_DIVE_R2 144, the largest qualifying CT_GROUP_MAX, the screened fallback: {v}')
# C: M(4) 4 > 3 -> PARK; reach 1/10 -> PARK; no threshold qualifies -> keep 12; baseline 0 -> screened
rowsC = _g('g1') + [_d0('g1', 3, unseen=10, e12=1, e10=1, e8=1, errs={a: [4] for a in range(1, 13)}), _d0('g1', 15, e12=2, e10=2, e8=2)]
v = cd0.evaluate(rowsC)[1]
check(v['R1'] == 'PARK' and v['R2'] == 'PARK' and v['R3'] == 'CT_GROUP_MAX 12 (none qualifies)' and v['R4'] == 'rel:screened20u12<=0.8',
      f'contact-d0: the park routes and the keep-12 fallback: {v}')
# D: M(4) 3 <= 3 < M(8): no pre-registered route; a failed replay makes the report PARTIAL with no routes
rowsD = _g('g1') + [_d0('g1', 3, unseen=8, errs={**{a: [3] for a in range(1, 5)}, **{a: [9] for a in range(5, 13)}})]
l, v = cd0.evaluate(rowsD + [dict(file='x__M__botA.bc24', kind='error', outcome='GZIP EOF')])
check(v['R1'] == 'NO ROUTE' and v['PARTIAL'] and not any(x.startswith('ROUTES') for x in l) and l[0].startswith('replays: 2 found, 1 dumped, 1 failed'),
      f'contact-d0: M(4) <= 3 < M(8) has no pre-registered route; a failed dump is PARTIAL without routes: {v} {l[:2]}')
r = subprocess.run([sys.executable, os.path.join(HERE, 'contact-d0.py'), '/no/such/run'], capture_output=True, text=True)
check(r.returncode == 2 and 'no such run dir' in r.stderr, 'contact-d0.py: an input that does not exist is an error (exit 2)')

# --- delivery gate: the checker passes/fails correctly; band-test.sh refuses without a PASS file
with tempfile.TemporaryDirectory() as d:
    c = os.path.join(d, 'c.csv'); sv = os.path.join(d, 's.csv')
    open(c, 'w').write('file,opp,us,team,crumbs200\nf1,o,1,A,3000\nf1,o,0,B,10\nf2,o,1,A,2000\nf3,o,1,B,4000\n')
    open(sv, 'w').write('file,opp,us,team,digs200\nf1,o,1,A,5\nf2,o,1,A,0\nf3,o,1,B,7\n')
    os.makedirs(os.path.join(d, 'gauntlet'))
    r = subprocess.run([sys.executable, os.path.join(HERE, 'delivery-check.py'), 'zz', 'median:crumbs200>=2500 fire:digs200>0>=0.6', c, sv, 'run'], cwd=d, capture_output=True, text=True)
    check('delivery zz: PASS' in r.stdout and os.path.exists(os.path.join(d, 'gauntlet', 'delivery-zz.PASS')), 'delivery-check: median and fire checks pass on our rows only: ' + r.stdout + r.stderr)
    r = subprocess.run([sys.executable, os.path.join(HERE, 'delivery-check.py'), 'zy', 'fire:digs200>0>=0.9', c, sv, 'run'], cwd=d, capture_output=True, text=True)
    check('delivery zy: FAIL' in r.stdout and os.path.exists(os.path.join(d, 'gauntlet', 'delivery-zy.FAIL')), 'delivery-check: a 67% fire rate fails a 90% bar')
    bc = os.path.join(d, 'bc.csv'); bs = os.path.join(d, 'bs.csv')
    open(bc, 'w').write('file,opp,us,team,crumbs200\nx/f1,o,1,A,10\nx/f2,o,1,A,10\n'); open(bs, 'w').write('file,opp,us,team,digs200\nx/f1,o,1,A,10\nx/f2,o,1,A,10\n')
    env2 = dict(os.environ, MIN_PAIRS='2')
    r = subprocess.run([sys.executable, os.path.join(HERE, 'delivery-check.py'), 'zr', 'rel:digs200<=0.7', c, sv, 'run', bc, bs], cwd=d, capture_output=True, text=True, env=env2)
    check('delivery zr: PASS' in r.stdout and '2 shared cells' in r.stdout, 'delivery-check rel: arm 2.5 vs base 10 on the 2 shared cells passes <=0.7: ' + r.stdout)
    open(bs, 'w').write('file,opp,us,team,digs200\nx/f1,o,1,A,7\nx/f2,o,1,A,0\n')   # arm 5 and 0 vs base 7 and 0: diff -1.0 +- 1.0
    r = subprocess.run([sys.executable, os.path.join(HERE, 'delivery-check.py'), 'zn', 'nw:digs200>=1.0', c, sv, 'run', bc, bs], cwd=d, capture_output=True, text=True, env=env2)
    check('delivery zn: INCONCLUSIVE' in r.stdout and 'worst plausible drop 86%' in r.stdout, 'delivery-check nw: a guard whose worst plausible drop exceeds 20% is INCONCLUSIVE: ' + r.stdout)
    r = subprocess.run([sys.executable, os.path.join(HERE, 'delivery-check.py'), 'zq', 'rel:digs200>=1.0', c, sv, 'run', bc, bs], cwd=d, capture_output=True, text=True, env=env2)
    check('delivery zq: INCONCLUSIVE' in r.stdout and r.returncode == 3, 'delivery-check rel: a miss inside 2 SE is INCONCLUSIVE, exit 3 (audit 2026-10-03 MEAS1): ' + r.stdout)
    # three-way verdicts on 20 synthetic pairs (base 10 each; arm = base * ratio + alternating noise of +-1)
    def block(name, ratio, n=20):
        cc = os.path.join(d, name + '_c.csv'); cs = os.path.join(d, name + '_s.csv')
        open(cc, 'w').write('file,opp,us,team,kills\n' + ''.join(f'g{i},o,1,A,{10 * ratio + (1 if i % 2 else -1)}\n' for i in range(n)))
        open(cs, 'w').write('file,opp,us,team,x\n' + ''.join(f'g{i},o,1,A,0\n' for i in range(n)))
        return cc, cs
    bcc = os.path.join(d, 'b3_c.csv'); bcs = os.path.join(d, 'b3_s.csv')
    open(bcc, 'w').write('file,opp,us,team,kills\n' + ''.join(f'g{i},o,1,A,10\n' for i in range(20)))
    open(bcs, 'w').write('file,opp,us,team,x\n' + ''.join(f'g{i},o,1,A,0\n' for i in range(20)))
    for name, ratio, want in (('at', 1.2, 'INCONCLUSIVE'), ('over', 1.2 + 0.034, 'PASS'), ('short', 1.2 - 0.07, 'FAIL')):
        cc, cs = block(name, ratio)
        r = subprocess.run([sys.executable, os.path.join(HERE, 'delivery-check.py'), name, 'rel:kills>=1.2', cc, cs, 'run', bcc, bcs], cwd=d, capture_output=True, text=True)
        check(f'delivery {name}: {want}' in r.stdout, f'delivery-check three-way: {name} -> {want}: ' + r.stdout)
    # nw guard (2026-10-04): an arm 39% better but noisy (+-8 on a base of 10) passes; the old 2-SE-only rule read it INCONCLUSIVE
    cc = os.path.join(d, 'nwb_c.csv'); cs = os.path.join(d, 'nwb_s.csv')
    open(cc, 'w').write('file,opp,us,team,kills\n' + ''.join(f'g{i},o,1,A,{13.9 + (8 if i % 2 else -8)}\n' for i in range(20)))
    open(cs, 'w').write('file,opp,us,team,x\n' + ''.join(f'g{i},o,1,A,0\n' for i in range(20)))
    r = subprocess.run([sys.executable, os.path.join(HERE, 'delivery-check.py'), 'nwb', 'nw:kills>=0.95', cc, cs, 'run', bcc, bcs], cwd=d, capture_output=True, text=True)
    check('delivery nwb: PASS' in r.stdout, 'delivery-check nw: a clearly better arm passes its guard despite noise: ' + r.stdout)
    cc = os.path.join(d, 'nwz_c.csv')
    open(cc, 'w').write('file,opp,us,team,kills\n' + ''.join(f'g{i},o,1,A,{10 + (8 if i % 2 else -8)}\n' for i in range(20)))
    r = subprocess.run([sys.executable, os.path.join(HERE, 'delivery-check.py'), 'nwz', 'nw:kills>=0.95', cc, cs, 'run', bcc, bcs], cwd=d, capture_output=True, text=True)
    check('delivery nwz: INCONCLUSIVE' in r.stdout, 'delivery-check nw: an equal arm with the same noise stays INCONCLUSIVE: ' + r.stdout)
    # audit MEAS3: seeded replay names. Two seeds of one cell are two pairs when both sides carry seeds; against a seedless
    # base (an old cache) the arm pairs on seedless names, as before
    sa = os.path.join(d, 'sa_c.csv'); ss = os.path.join(d, 'sa_s.csv'); sb = os.path.join(d, 'sb_c.csv'); sbs = os.path.join(d, 'sb_s.csv')
    names = [f'o__M{i % 10}__s{100 + i}__botA.bc24' for i in range(20)]
    open(sa, 'w').write('file,opp,us,team,kills\n' + ''.join(f'r/{n},o,1,A,12\n' for n in names))
    open(ss, 'w').write('file,opp,us,team,x\n' + ''.join(f'r/{n},o,1,A,0\n' for n in names))
    open(sb, 'w').write('file,opp,us,team,kills\n' + ''.join(f'q/{n},o,1,A,10\n' for n in names))
    open(sbs, 'w').write('file,opp,us,team,x\n' + ''.join(f'q/{n},o,1,A,0\n' for n in names))
    r = subprocess.run([sys.executable, os.path.join(HERE, 'delivery-check.py'), 'seeded', 'rel:kills>=1.1', sa, ss, 'run', sb, sbs], cwd=d, capture_output=True, text=True)
    check('20 shared cells' in r.stdout, 'delivery-check: seeded names keep both seeds of a repeated cell (20 pairs): ' + r.stdout)
    open(sb, 'w').write('file,opp,us,team,kills\n' + ''.join(f'q/o__M{i},o,1,A,10\n'.replace(f'o__M{i},', f'o__M{i}__botA.bc24,') for i in range(10)))
    open(sbs, 'w').write('file,opp,us,team,x\n' + ''.join(f'q/o__M{i}__botA.bc24,o,1,A,0\n' for i in range(10)))
    r = subprocess.run([sys.executable, os.path.join(HERE, 'delivery-check.py'), 'mixed', 'rel:kills>=1.1', sa, ss, 'run', sb, sbs], cwd=d, capture_output=True, text=True, env=dict(os.environ, MIN_PAIRS='2'))
    check('10 shared cells' in r.stdout, 'delivery-check: a seedless base pairs the seeded arm on seedless names: ' + r.stdout)
    cc, cs = block('few', 1.5, n=10)
    r = subprocess.run([sys.executable, os.path.join(HERE, 'delivery-check.py'), 'few', 'rel:kills>=1.2', cc, cs, 'run', bcc, bcs], cwd=d, capture_output=True, text=True)
    check('delivery few: INCONCLUSIVE' in r.stdout and 'only 10 shared cells' in r.stdout, 'delivery-check: fewer than 18 shared cells is INCONCLUSIVE: ' + r.stdout)
r = subprocess.run(['bash', os.path.join(HERE, 'band-test.sh'), 'no_such_arm_xyz'], capture_output=True, text=True)
check(r.returncode == 5 and 'Refusing' in r.stderr, 'band-test.sh refuses an arm without a delivery PASS')

# --- filler-tally: pairs cells of control and candidate runs on the same filler seed
with tempfile.TemporaryDirectory() as d:
    fake = os.path.join(d, 'tools'); os.makedirs(fake); open(os.path.join(fake, 'filler-tally.py'), 'w').write(open(os.path.join(HERE, 'filler-tally.py')).read())
    hdr = 'opponent,map,bot_side,winner_side,rounds,bot_result,reason,seed\n'
    for bot, res in (('ctl', ['win', 'loss', 'loss']), ('cand', ['win', 'win', 'loss'])):
        rd = os.path.join(d, 'gauntlet', f'20260101-000000-scrim-{bot}-fill77'); os.makedirs(rd)
        open(os.path.join(rd, 'results.csv'), 'w').write(hdr + ''.join(f'o{i},M,A,A,9,{r},x,1\n' for i, r in enumerate(res)))
    out = subprocess.run([sys.executable, os.path.join(fake, 'filler-tally.py'), 'ctl', 'cand'], capture_output=True, text=True).stdout
    check('1 seeds (1 on shared engine seeds, 0 legacy), 3 paired games, gained 1, lost 0, net +1' in out, 'filler-tally: one discordant pair in the candidate\'s favour: ' + out)
    # legacy: the same cells on different engine seeds pair on the cell only, and are reported as legacy
    rd = os.path.join(d, 'gauntlet', '20260101-000000-scrim-cand-fill77')
    open(os.path.join(rd, 'results.csv'), 'w').write(hdr + ''.join(f'o{i},M,A,A,9,{r},x,{5 + i}\n' for i, r in enumerate(['win', 'win', 'loss'])))
    out = subprocess.run([sys.executable, os.path.join(fake, 'filler-tally.py'), 'ctl', 'cand'], capture_output=True, text=True).stdout
    check('(0 on shared engine seeds, 1 legacy), 3 paired games' in out, 'filler-tally: unseeded runs fall back to cells and say so: ' + out)
# scrim cells carry the engine seed, and the same SEED gives identical cells for any bot (audit B2)
_env = dict(os.environ, DRY='1', N='12', SEED='4242', POOL='a.x b.y c.z')
c1 = subprocess.run(['bash', os.path.join(HERE, 'scrim.sh')], env=dict(_env, BOT='g_iter1'), capture_output=True, text=True).stdout.split('\n')
c2 = subprocess.run(['bash', os.path.join(HERE, 'scrim.sh')], env=dict(_env, BOT='g1sym'), capture_output=True, text=True).stdout.split('\n')
c1 = [l for l in c1 if l.strip()]; c2 = [l for l in c2 if l.strip()]
check(len(c1) == 12 and c1 == c2 and all(len(l.split()) == 4 and l.split()[3].isdigit() for l in c1),
      'scrim: 12 cells with a 4th engine-seed field, identical for two bots on one SEED: %r' % (c1[:2],))

# fill-origin: own/enemy/natural classification and dig->fill lag
spec = importlib.util.spec_from_file_location('fo', os.path.join(os.path.dirname(os.path.abspath(__file__)), 'fill-origin.py'))
fo = importlib.util.module_from_spec(spec); spec.loader.exec_module(fo)
r = fo.classify(['r3 A#10 digs (1,1)', 'r5 B#11 digs (2,2)', 'r9 A#12 fills (1,1)', 'r10 A#12 fills (2,2)',
                 'r11 A#12 fills (3,3)', 'r12 A#12 fills (1,1)', 'r12 B#-1 fills (9,9)'])
check(r['A'][:4] == [4, 1, 1, 2] and r['A'][4] == [6], 'fill-origin: A fills own/enemy/natural: %r' % (r['A'],))
check(r['B'][:4] == [1, 0, 0, 1], 'fill-origin: B one natural fill: %r' % (r['B'],))

# diag-batch (PROMPTS 178): an external opponent from the manifest on a chosen side; side B swaps the teams; the
# external bot is silenced; replay names carry __bot<side>; unknown opponents and bad sides are refused
with tempfile.TemporaryDirectory() as td:
    man = os.path.join(td, 'manifest.tsv'); open(man, 'w').write('Ext.bot\text\t/x\n')
    env = dict(os.environ, DRY='1', MANIFEST=man)
    r = subprocess.run([os.path.join(HERE, 'diag-batch.sh'), 't', 'g_iter4:Ext.bot:Ambush:7:B', 'g_iter4:g_iter3:Ambush:7'], capture_output=True, text=True, env=env)
    lines = r.stdout.strip().splitlines()
    check(r.returncode == 0 and len(lines) == 2
          and lines[0].startswith('tools/run-dev.sh Ext.bot g_iter4 Ambush diag/t/g_iter4-vs-Ext.bot-Ambush-s7__botB.bc24') and 'silence-a=true' in lines[0]
          and lines[1].startswith('tools/run-dev.sh g_iter4 g_iter3 Ambush diag/t/g_iter4-vs-g_iter3-Ambush-s7__botA.bc24') and 'silence' not in lines[1],
          'diag-batch: external opponent on a chosen side (swapped, silenced), own build unchanged: %r %s' % (lines, r.stderr))
    r1 = subprocess.run([os.path.join(HERE, 'diag-batch.sh'), 't', 'g_iter4:No.such:Ambush:7'], capture_output=True, text=True, env=env)
    r2 = subprocess.run([os.path.join(HERE, 'diag-batch.sh'), 't', 'g_iter4:g_iter3:Ambush:7:C'], capture_output=True, text=True, env=env)
    check(r1.returncode == 2 and r2.returncode == 2, 'diag-batch: unknown opponent and bad side refused')

# scrim.sh MAPFILE (PROMPTS 178): cells are drawn only from the given map list; the same SEED gives the same cells
with tempfile.TemporaryDirectory() as td:
    mf = os.path.join(td, 'maps.txt'); open(mf, 'w').write('GaltonBoard\nMIT\n')
    env = dict(os.environ, DRY='1', POOL='Gymhgy.v10official', N='8', SEED='5', MAPFILE=mf)
    a = subprocess.run([os.path.join(HERE, 'scrim.sh')], capture_output=True, text=True, env=env).stdout.split()
    b = subprocess.run([os.path.join(HERE, 'scrim.sh')], capture_output=True, text=True, env=env).stdout.split()
    lines = [l for l in subprocess.run([os.path.join(HERE, 'scrim.sh')], capture_output=True, text=True, env=env).stdout.splitlines() if l.strip()]
    check(len(lines) == 8 and all(l.split()[1] in ('GaltonBoard', 'MIT') for l in lines) and a == b,
          'scrim.sh MAPFILE: 8 cells, all on the listed maps, reproducible: %r' % lines[:3])

# vm-prune: deletes old replays of censused arm/filler runs; keeps stack builds, gate bases, unfinished runs, non-replays
with tempfile.TemporaryDirectory() as td:
    os.makedirs(os.path.join(td, 'tools'))
    open(os.path.join(td, 'tools', 'keep-replays.txt'), 'w').write('g_iter1\n')
    runs = {'20261001-000000-scrim-x': True, '20261001-000001-scrim-g_iter1': True, '20261001-000002-scrim-y-dg909090': True,
            '20261001-000003-scrim-z': False, '20261001-000004-scrim-g_iter1-fill123': True,
            '20261001-000005-scrim-g_iter1-diag-gym': True, '20261001-000006-scrim-w-diag-old': True,
            '20261001-000007-scrim-g_iter1-fill124': True, '20261001-000008-scrim-x-fill125': True}
    old = 1e9
    recent = __import__('time').time() - 3 * 3600   # 3 hours old: past AGE, inside DIAG_AGE (1440 min)
    for r, done in runs.items():
        d = os.path.join(td, 'gauntlet', r, 'losses'); os.makedirs(d)
        f = os.path.join(d, 'a__m__botA.bc24'); open(f, 'w').write('x')
        t = recent if r.endswith('diag-gym') else old
        os.utime(f, (t, t))
        if done: open(os.path.join(td, 'gauntlet', r, 'results.csv'), 'w').write('h\n')
    out = subprocess.run(['bash', os.path.join(HERE, 'vm-prune.sh')], env=dict(os.environ, REPO=td, THRESH='-1', AGE='60', KEEP_FILLS='1'),
                         capture_output=True, text=True).stdout
    left = {r: os.path.exists(os.path.join(td, 'gauntlet', r, 'losses', 'a__m__botA.bc24')) for r in runs}
    check(left == {'20261001-000000-scrim-x': False, '20261001-000001-scrim-g_iter1': True, '20261001-000002-scrim-y-dg909090': True,
                   '20261001-000003-scrim-z': True, '20261001-000004-scrim-g_iter1-fill123': False,
                   '20261001-000005-scrim-g_iter1-diag-gym': True, '20261001-000006-scrim-w-diag-old': False,
                   '20261001-000007-scrim-g_iter1-fill124': True, '20261001-000008-scrim-x-fill125': False}
          and all(os.path.exists(os.path.join(td, 'gauntlet', r, 'results.csv')) for r, d in runs.items() if d),
          'vm-prune: prunes arm and filler replays only; keeps stack, gate bases, unfinished runs, recent diagnostics, the newest KEEP_FILLS filler runs of a kept build, and results: %r %s' % (left, out))

# eval-paired: pairing, tier split, sign test, capture difference
spec = importlib.util.spec_from_file_location('evp', os.path.join(HERE, 'eval-paired.py')); evp = importlib.util.module_from_spec(spec); spec.loader.exec_module(evp)
check(abs(evp.sign_p(0, 6) - 0.03125) < 1e-9 and evp.sign_p(3, 3) == 1.0, 'eval-paired: exact sign test')
# shipping rule (PROMPTS 186-188): looks 1-3 with their ship / stop / continue boundaries
sd = evp.ship_decision
check(sd(1, 3.1, 0, 1) == 'SHIP' and sd(1, 3.1, 0, -1).startswith('CONTINUE') and sd(1, 0.4, 0.7, 5).startswith('STOP')
      and sd(1, 1.2, 0, 9).startswith('CONTINUE'), 'eval-paired ship rule look 1: ship at t_all >= 3 with net >= 0, stop below 0.5 / 0.8')
check(sd(2, 2.21, 1.39, 12).startswith('CONTINUE') and sd(2, 2.3, 0, 0) == 'SHIP' and sd(2, 0.5, 2.6, 0) == 'SHIP'
      and sd(2, 2.5, 0, -1).startswith('CONTINUE') and sd(2, 0.9, 1.2, 4).startswith('STOP'),
      'eval-paired ship rule look 2: (t_all >= 2.3 or t_up >= 2.6) and net >= 0; g4ship1 (2.21, 1.39, +12) continues')
check(sd(3, 2.3, 0, 0) == 'SHIP' and sd(3, 2.29, 2.59, 10).startswith('STOP'), 'eval-paired ship rule look 3: ship test or park')
P0 = [({'won': 0, 'cap': 1, 'ecap': 1}, {'won': 1, 'cap': 2, 'ecap': 1}), ({'won': 1, 'cap': 2, 'ecap': 0}, {'won': 1, 'cap': 2, 'ecap': 1}),
      ({'won': 0, 'cap': 0, 'ecap': 2}, {'won': 0, 'cap': 1, 'ecap': 2})]
t0, n0 = evp.stats(P0)
check(n0 == 1 and abs(t0 - (1 / 3) / ((((2 / 3) ** 2 * 2 + (4 / 3) ** 2) / 2 / 3) ** 0.5)) < 1e-9, f'eval-paired stats: capture-delta t and wins net ({t0}, {n0})')
with tempfile.TemporaryDirectory() as td:
    hdr = 'file,opp,us,team,name,won,captured\n'
    def census(path, rows):
        with open(path, 'w') as f:
            f.write(hdr)
            for run, base, opp, won, cap, ecap in rows:
                fp = f'gauntlet/{run}/losses/{base}'
                f.write(f'{fp},{opp},1,A,x,{won},{cap}\n{fp},{opp},0,B,{opp},{1 - won},{ecap}\n')
    census(os.path.join(td, 'c.csv'), [('r1-ctl', 'a.bc24', 'top.bot', 0, 0, 3), ('r1-ctl', 'b.bc24', 'low.bot', 1, 2, 0)])
    census(os.path.join(td, 'a.csv'), [('r1-arm', 'a.bc24', 'top.bot', 1, 1, 0), ('r1-arm', 'b.bc24', 'low.bot', 1, 3, 0)])
    open(os.path.join(td, 'tier.txt'), 'w').write('top.bot\n')
    out = subprocess.run([sys.executable, os.path.join(HERE, 'eval-paired.py'), os.path.join(td, 'c.csv'), os.path.join(td, 'a.csv'),
                          'r1-ctl,r1-arm', '--tier', os.path.join(td, 'tier.txt')], capture_output=True, text=True).stdout
    lines = out.splitlines()
    check(len(lines) == 3 and 'n=   2' in lines[0] and 'gained 1 lost 0' in lines[0] and 'delta +2.50' in lines[0]
          and 'n=   1' in lines[1] and 'delta +4.00' in lines[1] and 'delta +1.00' in lines[2],
          'eval-paired: pairs cells, splits tiers, capture difference deltas: ' + out)

# deadcode: an unreferenced method or constant in a bot package is reported (owner prompt 127: Sym.observe was never called)
spec = importlib.util.spec_from_file_location('dcode', os.path.join(HERE, 'deadcode.py')); dcode = importlib.util.module_from_spec(spec); spec.loader.exec_module(dcode)
with tempfile.TemporaryDirectory() as td:
    open(os.path.join(td, 'A.java'), 'w').write("""package p;
public class A {
    public static final int USED = 1, UNUSED = 2;
    public static void run(Object rc) { helper(USED); }
    static void helper(int x) { /* calls nothing: observe(x) in a comment does not count */ }
    static void observe(int x) { }
    static String s = "observe(1) in a string does not count";
}
""")
    dead = dcode.scan(td)
    check(dead == ['method observe (A.java)', 'constant UNUSED (A.java)'], 'deadcode: finds the uncalled method and unread constant only: %r' % (dead,))

# arm intent: every switch an arm is meant to carry is really set in its source (a silent sed miss ran g1sym with it off)
REPO_ROOT = os.path.dirname(HERE)
for line in open(os.path.join(HERE, 'arm-intent.txt')):
    line = line.strip()
    if not line or line.startswith('#'): continue
    arm, spec = line.split(); fc, val = spec.split('='); fname, const = fc.split('.')
    src = open(os.path.join(REPO_ROOT, 'src', arm, fname + '.java')).read()
    m = re.search(r'static final \w+\s+(?:\w+\s*=\s*[^,;]+,\s*)*' + const + r'\s*=\s*([^,;]+)', src)
    check(m is not None and m.group(1).strip() == val, 'arm intent: %s %s.%s should be %s, found %r' % (arm, fname, const, val, m.group(1).strip() if m else None))

# basics battery: absolute bars (symmetry, overruns, exceptions) and relative checks against a base
with tempfile.TemporaryDirectory() as td:
    hdr = 'file,us,symOk,symWrong,symDecidedRound,overruns,maxBcK,exceptions,stillPost,kills,deaths,trapsHit,gathered400\n'
    def cen(path, rows):
        with open(path, 'w') as f:
            f.write(hdr)
            for i, r in enumerate(rows): f.write(f'g{i},1,' + ','.join(str(x) for x in r) + '\n')
    good = [(1, 0, 15.0, 0, 30, 400, 300, 200, 6000)] * 10
    cen(os.path.join(td, 'base.csv'), [(0, 0, '', 0, 15.0, 0, 30 + i % 3, 400 + i, 300, 200, 6000 + i) for i in range(10)])
    cen(os.path.join(td, 'ok.csv'), [(1, 0, 150, 0, 15.0, 0, 30 + i % 3, 400 + i, 300, 200, 6000 + i) for i in range(10)])
    cen(os.path.join(td, 'bad.csv'), [(1, 1 if i < 2 else 0, 150 if i < 7 else '', 1 if i == 0 else 0, 25.0, 0, 30 + i % 3, 200 + i, 300, 200, 6000 + i) for i in range(10)])
    run = lambda c: subprocess.run([sys.executable, os.path.join(HERE, 'basics.py'), os.path.join(td, c), '--base', os.path.join(td, 'base.csv')], capture_output=True, text=True)
    r_ok, r_bad = run('ok.csv'), run('bad.csv')
    check(r_ok.returncode == 0 and 'PASS' in r_ok.stdout, 'basics: a clean block passes: ' + r_ok.stdout)
    check(r_bad.returncode == 1 and 'symWrong in 2 of 10' in r_bad.stdout and 'decided by r201 in 7/10' in r_bad.stdout
          and 'overruns 1' in r_bad.stdout and r_bad.stdout.count('FAIL') >= 5,
          'basics: a wrong symmetry, an undecided symmetry, an overrun and a kill/death collapse fail: ' + r_bad.stdout)
    with open(os.path.join(td, 'base_nostill.csv'), 'w') as f:   # audit 2026-10-03 MEAS12: a base lacking a column is not a pass
        f.write('file,us,symOk,symWrong,symDecidedRound,overruns,maxBcK,exceptions,kills,deaths,trapsHit,gathered400\n')
        for i in range(10): f.write(f'g{i},1,0,0,,0,15.0,0,{30 + i % 3},{400 + i},300,{6000 + i}\n')
    r_nb = subprocess.run([sys.executable, os.path.join(HERE, 'basics.py'), os.path.join(td, 'ok.csv'), '--base', os.path.join(td, 'base_nostill.csv')], capture_output=True, text=True)
    check(r_nb.returncode == 1 and 'base not measured' in r_nb.stdout, 'basics: a base census without a column fails that check: ' + r_nb.stdout)

# Elo fit converges (audit 2026-10-03 MEAS11): a synthetic ladder recovers its true rating gaps; a capped fit warns
import elolib, contextlib, io, random as _r
_r.seed(7); true = {f'p{i}': 1500 + 60 * i for i in range(12)}; rows = []
names = list(true)
for k in range(6000):
    a, b = _r.sample(names, 2)
    rows.append(dict(run=f'r{k}', seq=str(k), teamA=a, teamB=b, map='m', winner='A' if _r.random() < elolib.expected(true[a], true[b]) else 'B', rounds='1', reason='x', seed=str(k)))
R, SE, g, W = elolib.fit(rows)
gaps = [(R[f'p{i}'] - R['p0']) - 60 * i for i in range(12)]
check(max(abs(x) for x in gaps) < 60, 'elo fit: synthetic ladder rating gaps recovered within 60 (worst %.0f)' % max(abs(x) for x in gaps))
err = io.StringIO()
with contextlib.redirect_stderr(err): elolib.fit(rows, iters=3)
check('not converged' in err.getvalue(), 'elo fit: a capped fit warns on stderr')

print('test_tools: %s' % ('OK' if fails == 0 else f'FAILED {fails}'))
sys.exit(1 if fails else 0)
