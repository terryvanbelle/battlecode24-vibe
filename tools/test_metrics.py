#!/usr/bin/env python3
"""Unit tests for the metrics pipeline (polarity, derived, statlib, and the correlate/onset scripts
end to end on a synthetic block). Run: tools/test_metrics.py  (also from tools/unit-tests.sh)."""
import os, sys, math, subprocess, tempfile, csv
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from polarity import orient, label, POLARITY
from statlib import pointbiserial, noise_floor, running_mean, onset, anti_onset, within_group
from derived import add_derived, DERIVED_COLS

fails = []
def check(name, cond, detail=''):
    if not cond: print(f"  FAIL {name} {detail}"); fails.append(name)
def close(a, b, eps=1e-9): return a is not None and abs(a-b) < eps

# polarity
check("our count is higher-better", orient('miners', 3, 'us') == 3)
check("their count is inverted", orient('miners', 3, 'th') == -3)
check("a gap of a higher-is-better metric keeps its sign", orient('soup', -2, 'gap') == -2)
check("a gap of an inverted metric is flipped", orient('died', 3, 'gap') == -3)
check("HQ buried dirt is inverted", orient('hqBuried', 5, 'us') == -5)
check("an unoriented metric has no gap", orient('hqElev', 3, 'gap') is None and orient('firstHQ', 3, 'us') is None)
check("label marks inversion and gaps", '[inverted]' in label('aba', 'gap') and '(us-them)' in label('soup', 'gap'))
STUDY_COLS = "soup hq hqBuried hqElev worth miners landscapers drones refineries vaporators schools centers netguns spawned spawnCost died drowned shot buriedDeaths mines soupDeps refines digs dirtDeps pickups drops shots moves cov aba bcOver".split()
check("every study column has a polarity entry", all(k in POLARITY for k in STUDY_COLS + DERIVED_COLS), [k for k in STUDY_COLS if k not in POLARITY])

# derived
rows = [{'us_miners': '3', 'us_landscapers': '2', 'us_drones': '1', 'us_hq': '1', 'us_refineries': '1', 'us_vaporators': '0', 'us_schools': '1', 'us_centers': '0', 'us_netguns': '0',
         'th_miners': '4', 'th_landscapers': '0', 'th_drones': '0', 'th_hq': '0', 'th_refineries': '0', 'th_vaporators': '0', 'th_schools': '0', 'th_centers': '0', 'th_netguns': '0'}]
add_derived(rows)
check("units sums mobile robots", rows[0]['us_units'] == 6.0 and rows[0]['th_units'] == 4.0)
check("robots adds the HQ and buildings", rows[0]['us_robots'] == 9.0 and rows[0]['th_robots'] == 4.0)
check("missing columns count as zero", add_derived([{'round': '50'}])[0]['us_robots'] == 0.0)

# statlib
perfect = [(1,1.0),(2,1.0),(3,1.0),(0,0.0),(-1,0.0),(-2,0.0)]
check("separating metric gives a strong positive", pointbiserial(perfect) > 0.85)
check("reversed gives the mirror value", close(pointbiserial([(-x, y) for x, y in perfect]), -pointbiserial(perfect)))
check("constant metric is undefined", pointbiserial([(5, 1.0)]*3 + [(5, 0.0)]*3) is None)
check("too few points is undefined", pointbiserial([(1,1.0),(2,0.0)]) is None)
check("matches a hand-computed value", close(pointbiserial([(1,0.0),(2,0.0),(3,1.0),(4,1.0),(5,0.0),(6,1.0)]), 0.4879500365, 1e-6))
check("noise floor of 48 is about 0.29", close(noise_floor(48), 0.2886751, 1e-5))
check("running mean ignores gaps", close(running_mean([2, None, 4]), 3.0) and running_mean([None]) is None)
rounds = [50,100,150,200,250]
check("first sustained crossing", onset([0.1,0.2,0.35,0.4,0.5], rounds, 0.3) == 150)
check("a lone spike is ignored", onset([0.1,0.9,0.05,0.05,0.05], rounds, 0.3) is None)
check("a negative crossing is an anti-onset, not an onset", onset([-0.1,-0.4,-0.5,-0.5,-0.5], rounds, 0.3) is None and anti_onset([-0.1,-0.4,-0.5,-0.5,-0.5], rounds, 0.3) == 100)
check("Nones are skipped", onset([None,None,0.4,0.45,0.5], rounds, 0.3) == 150)
g = [{'g':'A','v':100,'w':1.0}]*3 + [{'g':'B','v':1,'w':0.0}]*3
check("a pure group effect is erased", all(abs(v) < 1e-9 for v, _ in within_group(g, lambda r: r['v'], lambda r: r['g'], lambda r: r['w'])))

# end to end: correlate.py and onset.py on a synthetic block
TOOLS = os.path.dirname(os.path.abspath(__file__))
def write_study(d):
    hdr = ['game','opp','map','won','round'] + [s+'_'+c for s in ('us','th') for c in STUDY_COLS] + ['water','flooded','pollution']
    games = [('a.bot',1), ('a.bot',0), ('b.bot',1), ('b.bot',0), ('c.bot',1), ('c.bot',0), ('d.bot',1), ('d.bot',0)]
    with open(os.path.join(d, 'study.tsv'), 'w') as f:
        f.write('\t'.join(hdr) + '\n')
        for gi, (opp, won) in enumerate(games):
            for k, rnd in enumerate((100, 200, 300, 400)):
                def vals(win):
                    v = dict.fromkeys(STUDY_COLS, 0)
                    v.update(soup=100*(k+1)*(1+win), hq=1, worth=500*(k+1)*(1+win), miners=4+2*k*win, landscapers=2*k*win, spawned=5+3*k*win, spawnCost=350+150*k*win, mines=30*(k+1)*(1+win), moves=60*(k+1), cov=40*(k+1), aba=2)
                    return [v[c] for c in STUDY_COLS]
                f.write('\t'.join(str(x) for x in [f'g{gi}', opp, f'map{gi}', won, rnd] + vals(won) + vals(1-won) + [0.5*(k+1), 30, 10]) + '\n')
def run(script, *args):
    return subprocess.run([sys.executable, os.path.join(TOOLS, script)] + list(args), capture_output=True, text=True)
with tempfile.TemporaryDirectory() as d:
    write_study(d)
    c = run('correlate.py', d)
    check("correlate.py exits cleanly", c.returncode == 0, c.stderr.strip()[-300:])
    check("correlate.py reports the derived robots metric and a gap row", 'robots' in c.stdout and '(us-them)' in c.stdout)
    o = run('onset.py', d)
    check("onset.py exits cleanly", o.returncode == 0, o.stderr.strip()[-300:])
    check("onset.py reports the anti column and an onset", 'anti' in o.stdout and 'r100' in o.stdout)
    check("no traceback reached stdout", 'Traceback' not in c.stdout + o.stdout)
    check("default ranking is gap metrics only", all(('(us-them)' in ln or not ln.strip() or ln.startswith(('correlation','every','metric','-','Onset','temporal','diagnostic','wrote'))) for ln in o.stdout.splitlines()))
    s = run('scrim-study.py', os.path.join(d, 'study.tsv'))
    check("scrim-study.py exits cleanly", s.returncode == 0 and 'r200' in s.stdout, s.stderr.strip()[-300:])

# integrity checks on live data, if any block has been studied
REPO = os.path.dirname(TOOLS)
import glob
for sp in glob.glob(os.path.join(REPO, 'gauntlet', '*', 'study.tsv'))[:3]:
    rows = list(csv.DictReader(open(sp), delimiter='\t'))
    if not rows: continue
    check(f"{os.path.basename(os.path.dirname(sp))}: won is 0/1", set(r['won'] for r in rows) <= {'0','1'})
    check("every us_ column has a th_ twin", all('th_'+k[3:] in rows[0] for k in rows[0] if k.startswith('us_')))
    check("coverage is per mille in [0,1000]", all(0 <= float(r['us_cov']) <= 1000 for r in rows if r['us_cov']))
    # a block can draw the same opponent, map and side twice; the two games then share one study key and their rows
    # interleave (2026-09-26: EmaPajic__FourLakeLand__botB in a r1s11 block, 24 duplicated rounds) -- such a key is a
    # collision, not a decrease, and is skipped
    groups = [[r for r in rows if r['game'] == gm] for gm in {r['game'] for r in rows}]
    groups = [g for g in groups if len({r['round'] for r in g}) == len(g)]
    check("cumulative mines never decrease within a game", all(all(float(g[i]['us_mines']) >= float(g[i-1]['us_mines']) for i in range(1, len(g)))
          for g in groups))
    check("rounds are the 50-step grid", set(int(r['round']) for r in rows) <= set(range(50, 1250, 50)))

print(("test_metrics: FAILED " + ", ".join(fails)) if fails else "test_metrics: OK")
sys.exit(1 if fails else 0)
