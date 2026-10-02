#!/usr/bin/env python3
"""Basics battery (owner prompts 127, 137: "the basics -- symmetry, movement, combat, economy -- are the foundation;
make sure they are and remain solid"). Reads a build's census (and survey) CSVs and reports every basic against an
absolute bar where one is defined, and against a control build where none is (worse by more than 2 SE = FAIL).

    tools/basics.py <census.csv> [--survey survey.csv] [--base base_census.csv [--base-survey s.csv]] [--name NAME]

Absolute bars (a failure stops work above it, CLAUDE rule 15):
  symmetry   mean symOk >= 0.90            (the enemy spawn centres right at r250; g_iter1 measured 0.66-0.72)
  bytecode   overruns == 0                 (turns at the limit, every game)
  exceptions exceptions == 0               (caught exceptions abandon the rest of a turn)
Relative to the base (paired by nothing: means over the block, SE of the difference of means):
  movement   stillPost (post-setup share of robot-rounds standing still)      lower is better
  combat     kills / deaths per game, trapsHit                                 higher is better
  economy    gathered400 (map crumbs by r400); crumbs250 floating (survey)      gathered higher, floating lower
Exit status 1 if any absolute bar or relative check fails."""
import csv, math, sys

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

    def absolute(label, v, ok, show):
        nonlocal fails
        if not v: print(f'  {label:11s} NO DATA'); return
        good = ok(v)
        if not good: fails += 1
        print(f'  {label:11s} {show(v):40s} {"ok" if good else "FAIL"}')
    absolute('symmetry', vals(C, 'symOk'), lambda v: sum(v) / len(v) >= 0.90, lambda v: f'symOk {sum(v)/len(v):.3f} over {len(v)} games (bar >= 0.90)')
    absolute('bytecode', vals(C, 'overruns'), lambda v: sum(v) == 0, lambda v: f'overruns {int(sum(v))} (bar 0); max {max(vals(C, "maxBcK") or [0]):.1f}k')
    absolute('exceptions', vals(C, 'exceptions'), lambda v: sum(v) == 0, lambda v: f'exceptions {int(sum(v))} (bar 0)')

    def relative(label, va, vb, better):
        nonlocal fails
        ma, sa = mean_se(va)
        if ma is None: print(f'  {label:11s} NO DATA'); return
        if not vb:
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
    relative('floating250', vals(S, 'crumbs250'), vals(BS, 'crumbs250'), 'lower')
    print(f'basics {name}: {"PASS" if fails == 0 else f"FAIL ({fails})"}')
    return 1 if fails else 0

if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
