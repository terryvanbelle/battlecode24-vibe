#!/usr/bin/env python3
"""Basics battery (owner prompts 127, 137: "the basics -- symmetry, movement, combat, economy -- are the foundation;
make sure they are and remain solid"). Reads a build's census (and survey) CSVs and reports every basic against an
absolute bar where one is defined, and against a control build where none is (worse by more than 2 SE = FAIL).

    tools/basics.py <census.csv> [--survey survey.csv] [--base base_census.csv [--base-survey s.csv]] [--name NAME]

Absolute bars (a failure stops work above it, CLAUDE rule 15):
  sym wrong  symWrong == 0                 (the true enemy spawns never eliminated from slot 16)
  sym setup  decided by r201 in >= 95% of games on setup-decidable maps (walls, spawn zones, setup dams)
  sym late   decided by r400 in >= 90% of games on the 15 maps that need a post-setup sighting (audit A3)
  bytecode   overruns == 0                 (turns at the limit, every game)
  exceptions exceptions == 0               (caught exceptions abandon the rest of a turn)
Relative to the base (paired by nothing: means over the block, SE of the difference of means):
  movement   stillPost (post-setup share of robot-rounds standing still)      lower is better
  combat     kills / deaths per game, trapsHit                                 higher is better
  economy    gathered400 (map crumbs by r400); crumbs250 floating (survey)      gathered higher, floating lower
Exit status 1 if any absolute bar or relative check fails."""
import csv, math, sys

# maps whose surviving wrong candidates differ only on their side: one post-setup sighting is needed (audit A3, from
# all 78 engine maps); every other map is decidable by r201 from walls, spawn zones and setup dams.
POST_SETUP_MAPS = {'Asteroids', 'Digging', 'FloodGates', 'Fountain', 'Gauntlet', 'HungerGames', 'MIT', 'MazeRunner', 'Puzzle',
                   'Snake', 'Soccer', 'Tunnels', 'Valentine', 'Waterworld', 'Fusbol'}

def mapname(f):
    b = f.rsplit('/', 1)[-1]
    parts = b.split('__')
    return parts[1] if len(parts) >= 3 else ''

def rows(path):
    if not path: return []
    return [r for r in csv.DictReader(l for l in open(path) if ',' in l and not l.startswith('census:')) if r.get('us') == '1']

def vals(R, k):
    out = []
    for r in R:
        try: out.append(float(r[k]))
        except (KeyError, TypeError, ValueError): pass
    return out

def mean_se(v):
    if not v: return None, None
    m = sum(v) / len(v)
    se = math.sqrt(sum((x - m) ** 2 for x in v) / (len(v) - 1) / len(v)) if len(v) > 1 else float('nan')
    return m, se

def kd(R):
    v = []
    for r in R:
        try:
            k, d = float(r['kills']), float(r['deaths'])
            v.append(k / max(1.0, d))
        except (KeyError, ValueError): pass
    return v

def main(argv):
    a = {'--survey': None, '--base': None, '--base-survey': None, '--name': None}
    args = []
    i = 0
    while i < len(argv):
        if argv[i] in a: a[argv[i]] = argv[i + 1]; i += 2
        else: args.append(argv[i]); i += 1
    C, S = rows(args[0]), rows(a['--survey'])
    BC, BS = rows(a['--base']), rows(a['--base-survey'])
    name = a['--name'] or args[0]
    fails = 0
    print(f'basics {name}: {len(C)} games' + (f' (base {len(BC)} games)' if BC else ''))

    def absolute(label, v, ok, show, column=None):
        nonlocal fails
        if not v:
            if column and C and column not in C[0]:          # never measured: a basic that is not measured fails
                fails += 1; print(f'  {label:11s} NOT MEASURED (census lacks {column}: re-census with the current tools) FAIL')
            else: print(f'  {label:11s} NO DATA')
            return
        good = ok(v)
        if not good: fails += 1
        print(f'  {label:11s} {show(v):40s} {"ok" if good else "FAIL"}')
    # symmetry (audit B1: symOk credits a fixed-order guess): never wrong, and DECIDED by observation in time
    absolute('sym wrong', vals(C, 'symWrong'), lambda v: sum(v) == 0, lambda v: f'symWrong in {int(sum(v))} of {len(v)} games (bar 0)', 'symWrong')
    setup, late = [], []
    for r in (C if C and 'symDecidedRound' in C[0] else []):   # a census made before the column existed: NO DATA
        m = mapname(r.get('file', ''))
        d = r.get('symDecidedRound', '')
        dec = float(d) if d not in ('', None) else float('inf')
        (late if m in POST_SETUP_MAPS else setup).append(dec)
    absolute('sym setup', setup, lambda v: sum(x <= 201 for x in v) / len(v) >= 0.95,
             lambda v: f'decided by r201 in {sum(x <= 201 for x in v)}/{len(v)} setup-decidable games (bar 95%)', 'symDecidedRound')
    absolute('sym late', late, lambda v: sum(x <= 400 for x in v) / len(v) >= 0.90,
             lambda v: f'decided by r400 in {sum(x <= 400 for x in v)}/{len(v)} post-setup maps (bar 90%)')
    v = vals(C, 'symOk')
    if v: print(f'  {"(symOk)":11s} {sum(v)/len(v):.3f} descriptive only: a correct guess scores 1')
    absolute('bytecode', vals(C, 'overruns'), lambda v: sum(v) == 0, lambda v: f'overruns {int(sum(v))} (bar 0); max {max(vals(C, "maxBcK") or [0]):.1f}k', 'overruns')
    absolute('exceptions', vals(C, 'exceptions'), lambda v: sum(v) == 0, lambda v: f'exceptions {int(sum(v))} (bar 0)', 'exceptions')

    def relative(label, va, vb, better, given=None):
        nonlocal fails
        ma, sa = mean_se(va)
        if ma is None: print(f'  {label:11s} NO DATA'); return
        if not vb:
            if (BC if given is None else given):             # a base was given but lacks this column: a basic not measured fails
                fails += 1; print(f'  {label:11s} {ma:10.2f} +- {sa:.2f}  base not measured (re-census the base) FAIL'); return
            print(f'  {label:11s} {ma:10.2f} +- {sa:.2f}  (no base: reported)'); return
        mb, sb = mean_se(vb)
        d = ma - mb; se = math.sqrt((sa or 0) ** 2 + (sb or 0) ** 2)
        worse = (-d if better == 'higher' else d)
        bad = se > 0 and worse > 2 * se
        if bad: fails += 1
        print(f'  {label:11s} {ma:10.2f} vs base {mb:10.2f}  diff {d:+.2f} +- {se:.2f}  {"FAIL (worse by > 2 SE)" if bad else "ok"}')
    relative('stillPost', vals(C, 'stillPost'), vals(BC, 'stillPost'), 'lower')
    relative('kill/death', kd(C), kd(BC), 'higher')
    relative('trapsHit', vals(C, 'trapsHit'), vals(BC, 'trapsHit'), 'higher')
    relative('gathered400', vals(C, 'gathered400'), vals(BC, 'gathered400'), 'higher')
    relative('floating250', vals(S, 'crumbs250'), vals(BS, 'crumbs250'), 'lower', given=BS)
    print(f'basics {name}: {"PASS" if fails == 0 else f"FAIL ({fails})"}')
    return 1 if fails else 0

if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
