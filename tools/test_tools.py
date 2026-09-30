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

# --- puppet (PROMPTS 59-60): the extractor, the encoding contract, the diff, the guards. Synthetic raw lines.
spec = importlib.util.spec_from_file_location('puppet', os.path.join(HERE, 'puppet.py')); pp = importlib.util.module_from_spec(spec); spec.loader.exec_module(pp)
REPO = os.path.dirname(HERE)
BASE = 'H g_iter13 quals_bot M 40 40 7 0 0\nB 1 1 0 3 3\nB 2 2 0 36 36\n'
def pgame(text): return pp.parse(text.strip().splitlines())
def pex(text, side=2, by='O'): return pp.Extraction(pgame(text), side, by).run()
def pev(ex, i): return [(pp.OPS[o], r, a, x, y) for (o, r, a, x, y) in ex.bots[i].ev]
def praises(fn, needle):
    try: fn(); return False
    except pp.PuppetError as e: return needle in str(e)
# 1. dirt pairing: ground dig/deposit take their D entries in order, a building target uses its tile, actor -1 consumes silently
T1 = BASE + 'B 20 2 6 10 10\nB 21 2 6 12 10\nB 30 2 2 11 11\nB 40 1 6 20 20\nR 1 200 200\nA 20 3 -1\nA -1 4 -1\nA 21 4 30\nA 40 4 -1\nD 9 10 -1\nD 15 15 30\nD 21 21 1\n'
ex = pex(T1)
check(pev(ex, 20) == [('DIG', 1, 0, 9, 10)] and pev(ex, 21) == [('DEPOSIT', 1, 0, 11, 11)] and pev(ex, 40) == [], 'puppet: dirt pairing in order, building tile, actor -1 silent')
check(praises(lambda: pex(T1 + 'D 1 1 1\n'), 'r1'), 'puppet: a dirt count mismatch aborts naming the round')
check(praises(lambda: pex(T1.replace('D 9 10 -1', 'D 9 10 1')), 'r1'), 'puppet: a dig with a positive change aborts')
# 2. MINE pairing
T2 = BASE + 'B 22 2 1 5 5\nR 1 200 200\nA 22 0 -1\nU 5 6 -7\n'
check(pev(pex(T2), 22) == [('MINE', 1, 0, 5, 6)], 'puppet: MINE takes its soup change tile')
check(praises(lambda: pex(T2.replace('U 5 6 -7\n', '')), 'r1'), 'puppet: a MINE without a soup change aborts')
check(praises(lambda: pex(T2.replace('U 5 6 -7', 'U 5 6 7')), 'r1'), 'puppet: a MINE with a positive soup change aborts')
# 3. move classification: pickup entry, follow, regular and death drops (after the carrier moved), own moves, drowning
T3 = BASE + ('B 50 2 7 15 15\nB 51 2 1 16 15\nB 52 2 7 25 25\nB 53 2 1 26 25\nB 54 2 7 13 13\nB 60 1 8 18 18\nB 61 1 1 14 14\nF 24 24\n'
             'R 1 200 200\nA 50 5 51\nM 51 15 15\nA 52 5 53\nM 53 25 25\nA 54 5 61\nM 61 13 13\n'
             'R 2 200 200\nM 50 15 16\nM 51 15 16\nA 52 6 53\nM 53 26 26\n'
             'R 3 200 200\nM 50 15 17\nM 51 15 17\nA 60 8 50\nA 50 6 51\nM 51 15 17\nX 50\n'
             'R 4 200 200\nM 51 15 18\nA 52 5 53\nM 53 25 25\n'
             'R 5 200 200\nA 52 6 53\nM 53 24 24\nX 53\n')
ex = pex(T3)
check(pev(ex, 50) == [('PICK_OWN', 1, 1, 16, 15), ('MOVE', 2, 0, 15, 16), ('MOVE', 3, 1, 15, 17)], 'puppet: carrier picks, moves, dies (MOVE arg 1, death drop filtered): %s' % pev(ex, 50))
check(pev(ex, 51) == [('PLACE', 3, 0, 15, 17), ('MOVE', 4, 0, 15, 18)], 'puppet: follow entries are not moves; a death-dropped survivor gets PLACE: %s' % pev(ex, 51))
check(pev(ex, 52) == [('PICK_OWN', 1, 1, 26, 25), ('DROP', 2, 0, 26, 26), ('PICK_OWN', 4, 1, 26, 26), ('DROP', 5, 0, 24, 24)], 'puppet: regular drops at the drop entry: %s' % pev(ex, 52))
check(pev(ex, 53) == [('PLACE', 2, 0, 26, 26)], 'puppet: no PLACE (and no DIE) for a unit dropped into water: %s' % pev(ex, 53))
check(pev(ex, 54) == [('PICK_OTHER', 1, 1, 14, 14)] and pev(ex, 60) == [], 'puppet: PICK_OTHER carries the target type and pre-pick tile; O acts are not scripted')
check(praises(lambda: pex(T3 + 'R 6 200 200\nM 51 15 19\nM 51 15 20\n'), 'r6'), 'puppet: two own moves in a round abort')
check(praises(lambda: pex(T3 + 'R 6 200 200\nM 51 17 19\n'), 'r6'), 'puppet: a non-adjacent own move aborts')
# 4. keys: plain newborn, picked in its spawn round, picked at spawn+1 by an older drone, by a younger drone; the HQ
T4 = BASE + ('B 55 2 7 34 37\nB 58 2 7 34 35\nB 56 2 5 38 36\n'
             'R 1 200 200\nS 100 2 1 35 35\nA 2 7 100\nR 2 200 200\nM 100 34 34\n'
             'R 3 200 200\nS 101 2 1 35 37\nA 2 7 101\nA 55 5 101\nM 101 34 37\nR 4 200 200\nA 55 6 101\nM 101 33 37\nR 5 200 200\nM 101 32 37\n'
             'R 6 200 200\nS 102 2 1 35 35\nA 2 7 102\nR 7 200 200\nA 58 5 102\nM 102 34 35\nR 8 200 200\nA 58 6 102\nM 102 33 35\nR 9 200 200\nM 102 32 35\n'
             'R 10 200 200\nS 103 2 1 37 37\nS 104 2 7 38 37\nA 2 7 103\nA 56 7 104\nR 11 200 200\nA 104 5 103\nM 103 38 37\n'
             'R 12 200 200\nA 104 6 103\nM 103 39 38\nR 13 200 200\nM 103 39 39\n')
keys = {k: b.id for k, b in pex(T4).keys().items()}
check(keys.get((1, 2, 35, 35)) == 100, 'puppet key: a plain newborn is (type, spawn+1, tile)')
check(keys.get((1, 4, 33, 37)) == 101, 'puppet key: picked in its spawn round -> its first release (round, tile)')
check(keys.get((1, 8, 33, 35)) == 102, 'puppet key: picked at spawn+1 by an older drone -> its first release')
check(keys.get((1, 11, 37, 37)) == 103, 'puppet key: picked at spawn+1 by a younger drone keeps (type, spawn+1, tile)')
check(keys.get((0, 1, 36, 36)) == 2, 'puppet key: the HQ is (HQ, 1, tile): %s' % keys)
check(keys.get((5, 1, 38, 36)) == 56 and keys.get((7, 11, 38, 37)) == 104, 'puppet key: robots without events are keyed too (a neighbour must never take their record)')
T4b = BASE + 'F 20 20\nR 1 200 200\nS 110 2 1 35 35\nA 2 7 110\nX 110\n'
check((1, 2, 35, 35) not in pex(T4b).keys(), 'puppet key: a robot dead in its spawn round never takes a turn and has no key')
# 5. SHOOT at the victim's last tile; DIE for a death nothing explains, none for a flood
T5 = BASE + 'B 70 2 8 20 20\nB 71 1 7 21 21\nB 80 2 1 10 30\nB 81 2 1 12 30\nR 1 200 200\nM 71 22 22\nA 70 8 71\nX 71\nX 80\nX 81\nW 12 30\n'
ex = pex(T5)
check(pev(ex, 70) == [('SHOOT', 1, 0, 22, 22)], 'puppet: SHOOT targets the victim\'s last tile')
check(pev(ex, 80) == [('DIE', 1, 0, 10, 30)] and pev(ex, 81) == [], 'puppet: an unexplained death is DIE (move into water, disintegrate); a flood is not')
# a flood death counts only in the suffix of diedIDs (floodfill runs after every turn): 81's tile floods, but 80 died after it
ex = pex(T5.replace('X 80\nX 81\n', 'X 81\nX 80\n'))
check(pev(ex, 81) == [('DIE', 1, 0, 12, 30)] and pev(ex, 80) == [('DIE', 1, 0, 10, 30)], 'puppet: a death on a flooded tile before a non-flood death is not the flood: DIE')
# a burial counts only with the building's dirt spill (-1, DEPOSIT_DIRT, -1) just before the deposit on it
TB = BASE + 'B 21 1 6 12 10\nB 30 2 2 11 11\nB 31 2 2 13 11\nR 1 200 200\nA -1 4 -1\nA 21 4 30\nA 21 4 31\nD 11 11 15\nX 30\nX 31\n'
ex = pex(TB)
check(pev(ex, 30) == [] and pev(ex, 31) == [('DIE', 1, 0, 13, 11)], 'puppet: a deposit on a building explains its death only with its dirt spill: %s %s' % (pev(ex, 30), pev(ex, 31)))
# 6. messages: our auth, every Comms.auth the same, r and r-1 signatures to O, the tail carrier
check(pp.auth([1, 2, 3, 4, 5, 6, 0], 100, 1) == 1669466758, 'puppet: the auth port gives PuppetTest\'s golden value')
import re as _re
_bodies = {}
for d in sorted(os.listdir(os.path.join(REPO, 'src'))):
    f = os.path.join(REPO, 'src', d, 'Comms.java')
    if os.path.exists(f):
        t = open(f).read(); m = _re.search(r'public static int auth\(.*?\n    \}', t, _re.S)
        _bodies[d] = (m.group(0) if m else None, 'static final int SALT = 0x5eed2020;' in t)
check(len(_bodies) > 1 and all(v == _bodies['g_iter13'] for v in _bodies.values()) and _bodies['g_iter13'][1], 'puppet: every src/*/Comms.java auth body and SALT equals g_iter13\'s (the port in puppet.py)')
def omsg(r, *w): return 'T 1 %s %d\n' % (' '.join(map(str, w)), pp.auth(list(w) + [0], r, 0))
T6 = (BASE + 'B 90 2 1 30 5\nB 93 2 7 33 6\nB 94 2 1 32 5\nB 95 2 1 31 5\n'
      'R 1 200 200\n' + omsg(1, 1, 2, 3, 4, 5, 6) + 'T 1 5 5 5 5 5 5 5\n'
      'R 2 200 200\n' + omsg(1, 9, 9, 9, 9, 9, 9) + 'T 1 6 6 6 6 6 6 6\nT 2 7 7 7 7 7 7 7\nA 93 5 94\nM 94 33 6\nX 95\n')
ex = pex(T6)
check(ex.bots[95].tx == [(1, 1, (5,) * 7)], 'puppet: round 1 carrier is the highest execIdx free robot: %s' % ex.bots[95].tx)
check(ex.bots[93].tx == [(2, 1, (6,) * 7), (2, 2, (7,) * 7)] and not ex.bots[94].tx and len(ex.bots[95].tx) == 1,
      'puppet: a held or killed robot never carries (a drone that picks does); signatures of r and r-1 both stay with O')
check(ex.stats['msgs O'] == 2 and ex.stats['msgs P'] == 3, 'puppet: message attribution counts')
T6b = BASE + 'R 1 200 200\nT 1 5 5 5 5 5 5 5\n'
check(pex(T6b).bots[2].tx == [(1, 1, (5,) * 7)], 'puppet: the HQ carries round 1')
_cap = pp.TX_CAP[pp.MINER]
check(pp.TX_CAP[pp.HQ] == 69 and _cap == 32 and pp.TX_CAP[pp.VAPORATOR] == 13 and pp.TX_CAP[pp.NETGUN] == 21,
      'puppet: TX_CAP is floor((bytecode limit - 600 reserve - 700) / 270 per message): %s' % pp.TX_CAP)
T6c = BASE + 'B 90 2 1 30 5\nB 93 2 7 33 6\nR 1 200 200\n' + ''.join('T 1 %d 0 0 0 0 0 0\n' % i for i in range(_cap + 5))
ex = pex(T6c)
check([w[0] for _, _, w in ex.bots[90].tx] == list(range(_cap)) and [w[0] for _, _, w in ex.bots[93].tx] == list(range(_cap, _cap + 5)),
      'puppet: over one robot\'s capacity the tail carriers split a round in execution order')
# the chain model: ids from Random(seed) re-created at every construction; the placement that reproduces the recorded
# block decides who carries (round 2: [6,5] needs message 5 before the school's spawn, [5,6] allows both after it)
check([pp.ChainModel(42).id(i) for i in range(3)] == [-1170105035, 234785527, -1360544799], 'puppet chain: the Random port gives Java\'s new Random(42).nextInt() x3')
TC = ('H g_iter13 quals_bot M 40 40 7 0 0\nB 1 1 0 3 3\nB 2 2 0 36 36\nB 90 2 1 30 5\nB 91 2 1 31 5\nB 92 2 4 20 20\nB 93 2 1 33 5\n'
      'R 1 200 200\nT 1 4 4 4 4 4 4 4\nK 1 4 4 4 4 4 4 4\nR 2 200 200\nS 100 2 6 21 21\nA 92 7 100\nT 1 5 5 5 5 5 5 5\nT 1 6 6 6 6 6 6 6\n')
ex = pex(TC + 'K 1 6 6 6 6 6 6 6\nK 1 5 5 5 5 5 5 5\n')
check(ex.bots[91].tx == [(2, 1, (5,) * 7)] and ex.bots[93].tx == [(1, 1, (4,) * 7), (2, 1, (6,) * 7)] and ex.chain_notes['rounds placed exactly'] == 2,
      'puppet chain: a message the block shows drawn before a spawn is carried by a robot acting before its builder: %s %s' % (ex.bots[91].tx, ex.bots[93].tx))
ex = pex(TC + 'K 1 5 5 5 5 5 5 5\nK 1 6 6 6 6 6 6 6\n')
check(ex.bots[93].tx == [(1, 1, (4,) * 7), (2, 1, (5,) * 7), (2, 1, (6,) * 7)] and not ex.bots[91].tx, 'puppet chain: otherwise the latest free robot carries')
ex = pex(TC + 'K 1 9 9 9 9 9 9 9\n')
check(ex.chain_notes['rounds on the tail carriers (block order approximate)'] == 1 and ex.chain_notes['rounds placed exactly'] == 1 and ex.bots[93].tx,
      'puppet chain: from a block the model cannot reproduce on, the tail carriers; the rounds before stay placed')
# after the break the queue empties at r3 (3 submitted, 3 minted); a fresh beam (draws since the r2 spawn: 0..2) places r4,
# whose block [8,7] needs message 7 before the school's second spawn
TR = TC + 'K 1 9 9 9 9 9 9 9\nR 3 200 200\nK 1 5 5 5 5 5 5 5\nR 4 200 200\nS 101 2 6 19 21\nA 92 7 101\nT 1 7 7 7 7 7 7 7\nT 1 8 8 8 8 8 8 8\nK 1 8 8 8 8 8 8 8\nK 1 7 7 7 7 7 7 7\n'
ex = pex(TR)
check(pp.ChainModel(7).draws_after(ex.g.rounds, 2) == [0, 1, 2] and ex.chain_notes['chain model restarts (at an empty queue)'] == 1
      and ex.chain_notes['rounds placed exactly'] == 2 and ex.chain_notes['rounds on the tail carriers (block order approximate)'] == 1 and ex.bots[91].tx == [(4, 1, (7,) * 7)],
      'puppet chain: the search restarts at the next empty queue and places the rounds after it: %s %s' % (dict(ex.chain_notes), ex.bots[91].tx))
# the HQ that submitted, then built: (seed 1) block [5,6] needs message 5 before the HQ's spawn and no P robot acts before
# the HQ, so the HQ carries it before its act (the PRE flag on its round), message 6 after the spawn
TP = ('H g_iter13 quals_bot M 40 40 1 0 0\nB 1 1 0 3 3\nB 2 2 0 36 36\nB 93 2 1 33 5\nR 1 200 200\nS 100 2 1 35 35\nA 2 7 100\n'
      'T 1 5 5 5 5 5 5 5\nT 1 6 6 6 6 6 6 6\nK 1 5 5 5 5 5 5 5\nK 1 6 6 6 6 6 6 6\n')
ex = pex(TP)
check(ex.bots[2].tx == [(1 | pp.PRE, 1, (5,) * 7)] and ex.bots[93].tx == [(1, 1, (6,) * 7)] and ex.chain_notes['rounds placed exactly'] == 1,
      'puppet chain: a builder carries a message drawn before its own spawn, flagged PRE: %s %s' % (ex.bots[2].tx, ex.bots[93].tx))
_ex = pp.Extraction(pgame(TP), 2, 'O'); _ex.run()
def _alloc(cs): _ex.msg_rounds[1] = ([True] * len(cs),) + _ex.msg_rounds[1][1:]; return _ex.allocate(1, cs)
check(_alloc((0,) * 66 + (1,) * 35) is not None and _alloc((0,) * 66 + (1,) * 36) is None,
      'puppet chain: a pre builder\'s capacity is shared with its messages after its act (69 per round for the HQ)')
_ex = pp.Extraction(pgame(TP), 2, 'O'); _ex.run()
check(_ex.allocate(1, (0, 0)) is not None and [len(k) for _, _, k in _ex.allocate(1, (0, 0))] == [2], 'puppet chain: the pre builder takes a whole interval')
# 7. encoding: round trip, sorted idx with its sentinel, ASCII with exactly the two keys, the golden file byte for byte
G = {(0, 1, 5, 5): ([(pp.BUILD, 3, 1, 6, 6)], [(1, 1, (1, 2, 3, 4, 5, 6, -7)), (3 | pp.PRE, 2, (7,) * 7)]),
     (1, 4, 6, 6): ([(pp.MOVE, 14, 0, 7, 7), (pp.MINE, 15, 0, 7, 8), (pp.DIE, 20, 0, 7, 7)], []),
     (1, 4, 9, 9): ([(pp.MOVE, 12, 1, 10, 10)], []),
     (7, 30, 10, 12): ([(pp.PICK_OTHER, 40, 6, 11, 12), (pp.DROP, 42, 0, 63, 0), (pp.PLACE, 4095, 0, 63, 63)],
                       [(45, 65535, (65536, -1, 0, 70000, 2147483647, -2147483648, 42))])}
idx, dat = pp.encode(2, 64, 64, (5, 5), G)
P_, w_, h_, hq_, recs = pp.decode(idx, dat)
check((P_, w_, h_, hq_) == (2, 64, 64, (5, 5)) and {k: (v[0], [(r, f, tuple(ws)) for r, f, ws in v[1]]) for k, v in recs.items()} == {k: (v[0], [(r, f, tuple(ws)) for r, f, ws in v[1]]) for k, v in G.items()}, 'puppet: encode/decode round trip')
heads = [idx[4 + 4 * i] for i in range(len(G) + 1)]
check(heads == sorted(heads) and ord(heads[-1]) >> 12 == 15 and (ord(idx[-2]) << 16 | ord(idx[-1])) == len(dat), 'puppet: idx sorted, sentinel type 15 with offset |dat|')
gold = pp.fixture_text(idx, dat, ['golden fixture: the encoding contract of tools/puppet.py and src/puppet/Script.java (tools/test_tools.py regenerates it byte for byte)'])
check(gold == open(os.path.join(REPO, 'test', 'puppet', 'golden.properties'), encoding='ascii').read(), 'puppet: test/puppet/golden.properties reproduced byte for byte')
check(all(ord(c) < 128 for c in gold) and [l.split('=')[0] for l in gold.splitlines() if not l.startswith('#')] == list(pp.FIXTURE_KEYS) and 'cutoff' not in gold.replace('#', '').split('\n', 1)[1],
      'puppet: the fixture is ASCII with exactly bc.testing.pup.{idx,dat}, never a cutoff key')
check(praises(lambda: pp.encode(2, 64, 64, (5, 5), {(1, 4096, 1, 1): ([], [])}), 'out of range'), 'puppet: a key round >= 4096 is refused')
# 8. diff: identical, a P move change, a block-only change, an O act change with nothing earlier
def dgame(mod=None):
    L = ['H g_iter13 quals_bot M 40 40 7 0 0', 'B 1 1 0 3 3', 'B 2 2 0 36 36', 'B 10 1 1 5 5', 'B 20 2 1 30 30']
    for r in range(1, 21):
        L += ['R %d %d %d' % (r, 200 + r, 200 + r), 'M 10 %d 5' % (5 + r % 2), 'M 20 %d 30' % (30 + r % 2), 'A 1 2 -1', 'C 10 %d' % (100 + r), 'K 1 %d 0 0 0 0 0 0' % r]
    L.append('E 2 20')
    return '\n'.join(mod(L) if mod else L) + '\n'
def run_diff(rec, new, extra=()):
    import io, contextlib
    with tempfile.TemporaryDirectory() as d:
        f1, f2 = os.path.join(d, 'a'), os.path.join(d, 'b'); open(f1, 'w').write(rec); open(f2, 'w').write(new)
        buf = io.StringIO()
        with contextlib.redirect_stdout(buf): rc = pp.main(['diff', '--rec', f1, '--new', f2, '--side', 'B', *extra])
    return rc, buf.getvalue()
rc, out = run_diff(dgame(), dgame())
check(rc == 0 and 'identical' in out, 'puppet diff: identical streams exit 0')
rc, out = run_diff(dgame(), dgame(lambda L: [('M 20 29 30' if (l == 'M 20 31 30' and i > L.index('R 17 217 217')) else l) for i, l in enumerate(L)]))
check(rc == 1 and 'r17' in out.split('cause:')[1] and 'puppet' in out, 'puppet diff: a P move changed at r17 is the puppet at r17: ' + out.replace('\n', ' | '))
rc, out = run_diff(dgame(), dgame(lambda L: [('K 1 99 0 0 0 0 0 0' if l == 'K 1 10 0 0 0 0 0 0' else l) for l in L]))
check(rc == 1 and 'block content' in out.split('cause:')[1], 'puppet diff: a block-only change is "block"')
def two_k(L, swap):
    out = []
    for l in L:
        if l == 'K 1 10 0 0 0 0 0 0': out += ['K 1 11 1 1 1 1 1 1', l] if swap else [l, 'K 1 11 1 1 1 1 1 1']
        else: out.append(l)
    return out
rc, out = run_diff(dgame(lambda L: two_k(L, False)), dgame(lambda L: two_k(L, True)))
check(rc == 1 and 'r10 block order' in out, 'puppet diff: the same block minted in another order is "block order" (getBlock shows the order)')
rc, out = run_diff(dgame(), dgame(lambda L: [l for i, l in enumerate(L) if not (l == 'A 1 2 -1' and L[i - 3] == 'R 12 212 212')]))
check(rc == 2 and 'UNEXPLAINED' in out, 'puppet diff: an O act change with nothing earlier exits 2')
rc, out = run_diff(dgame(), dgame(lambda L: [('M 20 29 30' if (l == 'M 20 31 30' and i > L.index('R 17 217 217')) else l) for i, l in enumerate(L)]), ['--cutoff', '15'])
check(rc == 1 and 'after cutoff' in out, 'puppet diff: a divergence at or after the cutoff is "after cutoff"')
# 9. guards
_peek = [os.path.join(dp, f) for dp, _, fs in os.walk(os.path.join(REPO, 'src')) for f in fs
         if f.endswith('.java') and os.path.basename(dp) != 'puppet' and 'getProperty' in open(os.path.join(dp, f)).read()]
check(not _peek, 'puppet guard: no src/ file outside src/puppet calls System.getProperty (a candidate never peeks at a script): %s' % _peek)
_pups = [d for d in os.listdir(os.path.join(REPO, 'src')) if d.startswith('pup_')]
check(_pups and all(os.path.isdir(os.path.join(REPO, 'src', d[4:])) for d in _pups), 'puppet guard: every src/pup_<b> has its src/<b>')
with tempfile.TemporaryDirectory() as d:
    run = os.path.join(d, '20260101-000000-scrim-bot'); os.makedirs(run)
    open(os.path.join(run, 'results.csv'), 'w').write('opponent,map,bot_side,winner_side,rounds,bot_result,reason\npup_g_iter13,Maze,A,A,500,win,HQ destroyed\n')
    fake = os.path.join(d, 'repo', 'tools'); os.makedirs(fake); os.makedirs(os.path.join(d, 'repo', 'progress'))
    for f in ('elolib.py', 'scrim-record.py'): open(os.path.join(fake, f), 'w').write(open(os.path.join(HERE, f)).read())
    r1 = subprocess.run([sys.executable, os.path.join(fake, 'scrim-record.py'), run, '--label', 'g0'], capture_output=True, text=True)
    r2 = subprocess.run([sys.executable, os.path.join(fake, 'scrim-record.py'), run, '--label', 'pup_g0'], capture_output=True, text=True)
    check(r1.returncode != 0 and r2.returncode != 0 and not os.path.exists(os.path.join(d, 'repo', 'progress', 'games.csv')), 'puppet guard: scrim-record refuses a pup_ run')
with tempfile.TemporaryDirectory() as d:
    os.makedirs(os.path.join(d, 'bin')); mark = os.path.join(d, 'ran')
    open(os.path.join(d, 'bin', 'java'), 'w').write('#!/bin/sh\ntouch %s\n' % mark); os.chmod(os.path.join(d, 'bin', 'java'), 0o755)
    sh = 'source %s/tools/lib.sh; team_url () { echo /tmp; }; run_game pup_x g_iter13 M %s/r.bc24' % (REPO, d)
    r = subprocess.run(['bash', '-c', sh], env=dict(os.environ, JAVA_HOME=d), capture_output=True, text=True)
    check(r.returncode != 0 and not os.path.exists(mark), 'puppet guard: run_game refuses a pup_ team without GAME_CONFIG before java starts')
    cfg = os.path.join(d, 'f.properties'); open(cfg, 'w').write('# x\n')
    r = subprocess.run(['bash', '-c', sh], env=dict(os.environ, JAVA_HOME=d, GAME_CONFIG=cfg), capture_output=True, text=True)
    check(os.path.exists(mark), 'puppet guard: with GAME_CONFIG the game starts (the stub java ran)')

print('test_tools: %s' % ('OK' if fails == 0 else f'FAILED {fails}'))
sys.exit(1 if fails else 0)
