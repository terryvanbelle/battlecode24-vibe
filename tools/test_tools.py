#!/usr/bin/env python3
"""Tests for the python tools: the SPRT, the Elo bookkeeping, the scrimmage recorder, the benchmark
selector. Synthetic inputs; runs from tools/unit-tests.sh. Every check names what it protects."""
import math, os, subprocess, sys, tempfile, csv, importlib.util
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
    cap = list(csv.DictReader(dump('--capabilities').splitlines()))
    check(len(cap) == 2 and all(int(c['captured']) + int(c['carrierDeaths']) <= int(c['pickups']) for c in cap),
          'replay-dump --capabilities: captures + carrier deaths never exceed pickups')
    check(int(cap[0]['enemyCarrierKills']) == int(cap[1]['carrierDeaths']) and int(cap[1]['enemyCarrierKills']) == int(cap[0]['carrierDeaths']),
          'replay-dump --capabilities: one side\'s carrier kills are the other side\'s carrier deaths')
    check(all(0 <= float(c['meanAlive']) <= 50 for c in cap), 'replay-dump --capabilities: mean alive in [0,50]')
    check(all(int(c['firstGrabs']) + int(c['regrabs']) + int(c['relayPickups']) == int(c['postPickups']) <= int(c['pickups']) for c in cap),
          'replay-dump --capabilities: first grabs + re-grabs + relay pickups = post-setup pickups <= all pickups')
    r = subprocess.run([os.path.join(HERE, 'replay-dump.sh'), FIX, '--nosuchflag'], capture_output=True, text=True)
    check(r.returncode != 0, 'replay-dump: unknown flags are hard errors')
else:
    print('test_tools: replay fixture or engine missing, replay-dump checks skipped')

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
    r = subprocess.run([sys.executable, os.path.join(HERE, 'delivery-check.py'), 'zr', 'rel:digs200<=0.7', c, sv, 'run', bc, bs], cwd=d, capture_output=True, text=True)
    check('delivery zr: PASS' in r.stdout and '2 shared cells' in r.stdout, 'delivery-check rel: arm 2.5 vs base 10 on the 2 shared cells passes <=0.7: ' + r.stdout)
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
    check('1 seeds, 3 paired games, gained 1, lost 0, net +1' in out, 'filler-tally: one discordant pair in the candidate\'s favour: ' + out)

print('test_tools: %s' % ('OK' if fails == 0 else f'FAILED {fails}'))
sys.exit(1 if fails else 0)
