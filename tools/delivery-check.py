#!/usr/bin/env python3
"""Evaluate delivery checks for tools/delivery-gate.sh (see there for the check syntax)."""
import csv, sys, re, statistics as st
import os, math
arm, checks, cpath, spath, run = sys.argv[1:6]
base_paths = sys.argv[6:8]          # optional: base census, base survey (same cells) for rel: checks
def load(paths):
    rows = {}
    for p in paths:
        for r in csv.DictReader(l for l in open(p) if ',' in l and not l.startswith('census:')):
            if r.get('us') == '1': rows.setdefault(os.path.basename(r['file']), {}).update(r)
    return rows
rows = load((cpath, spath)); base = load(base_paths) if len(base_paths) == 2 else {}
SEEDSEG = re.compile(r'__s\d+(?=__bot[AB]\.bc24$)')
if base and not all(SEEDSEG.search(k) for k in list(rows) + list(base)):   # audit MEAS3: a seedless side pairs on seedless names
    def _unseed(d):
        out = {}
        for k, v in d.items(): out.setdefault(SEEDSEG.sub('', k), {}).update(v)
        return out
    rows, base = _unseed(rows), _unseed(base)
games = list(rows.values())
def vals(col):
    out = []
    for g in games:
        try: out.append(float(g[col]))
        except (KeyError, ValueError, TypeError): pass
    return out
RANK = {'PASS': 0, 'INCONCLUSIVE': 1, 'FAIL': 2}
MIN_PAIRS = int(os.environ.get('MIN_PAIRS', 18)); NW_MAX_DROP = float(os.environ.get('NW_MAX_DROP', 0.20))
worst, lines = 0, []
for c in checks.split():
    m = re.fullmatch(r'(median|mean|fire|rel|nw):(\w+)(>=|<=|>|<)(-?[\d.]+)(?:(>=)([\d.]+))?', c)
    if not m: print('!! bad check', c); sys.exit(2)
    stat, col, op, val = m.group(1), m.group(2), m.group(3), float(m.group(4))
    v = vals(col)
    if not v: lines.append(f'{c}: NO DATA FAIL'); worst = max(worst, 2); continue
    if stat in ('rel', 'nw'):   # paired: arm mean on cells shared with the base vs ratio x base mean on those cells
        # Three-way verdict (audit 2026-10-03 MEAS1: point bars decided 16 of 28 FAILs inside 1 SE). Per pair e = arm - ratio*base;
        # margin = mean(e) on the good side of the bar. rel: PASS when margin >= 1 SE, FAIL when margin <= -2 SE, else
        # INCONCLUSIVE. nw: (guards) FAIL when margin <= -2 SE, INCONCLUSIVE when the worst plausible drop (lower 2-SE bound of
        # arm - base, as a share of the base) exceeds NW_MAX_DROP, else PASS. Fewer than MIN_PAIRS shared cells is INCONCLUSIVE.
        pairs = []
        for k, g in rows.items():
            b = base.get(k)
            try: pairs.append((float(g[col]), float(b[col])))
            except (TypeError, KeyError, ValueError): pass
        if not pairs: lines.append(f'{c}: NO PAIRED DATA (base missing?)'); state = 'FAIL'; worst = max(worst, RANK[state]); continue
        ma = st.mean(a for a, _ in pairs); mb = st.mean(b for _, b in pairs)
        target = val * mb
        e = [(a - val * b) * (1 if op in ('>=', '>') else -1) for a, b in pairs]
        margin = st.mean(e); se = st.stdev(e) / math.sqrt(len(e)) if len(e) > 1 else float('inf')
        if len(pairs) < MIN_PAIRS: state = 'INCONCLUSIVE'; why = f'only {len(pairs)} shared cells (< {MIN_PAIRS})'
        elif stat == 'rel':
            state = 'PASS' if margin >= se else 'FAIL' if margin < -2 * se else 'INCONCLUSIVE'
            why = f'margin {margin / se:+.1f} SE' if se > 0 else 'no variance'
        else:
            # worst plausible drop: the lower 2-SE bound of (arm - base) as a share of the base (2026-10-04: the old rule used
            # 2 SE alone, so a guard whose arm was 39% BETTER (kills 335 vs 241, 26 cells) read INCONCLUSIVE on power)
            d = [a - b for a, b in pairs]
            sed = st.stdev(d) / math.sqrt(len(d)) if len(d) > 1 else float('inf')
            sgn = 1 if op in ('>=', '>') else -1           # the direction in which the arm would be worse
            drop = max(0.0, (2 * sed - sgn * (ma - mb)) / abs(mb)) if mb else float('inf')
            state = 'FAIL' if margin < -2 * se else 'INCONCLUSIVE' if drop > NW_MAX_DROP else 'PASS'
            why = f'worst plausible drop {drop:.0%}'
        lines.append(f'{c}: arm {ma:.2f} vs base {mb:.2f} on {len(pairs)} shared cells (target {op} {target:.2f}; diff {ma - mb:+.2f}, '
                     f'margin {margin:+.2f} +- {se:.2f}; {why}) {state}')
        worst = max(worst, RANK[state]); continue
    elif stat == 'fire':
        x = sum(1 for a in v if (a > val if op == '>' else a >= val if op == '>=' else a < val if op == '<' else a <= val)) / len(v)
        need = float(m.group(6) or 0.9); ok = x >= need; lines.append(f'{c}: fired in {x:.0%} of {len(v)} (need {need:.0%}) {"ok" if ok else "FAIL"}')
    else:
        x = st.median(v) if stat == 'median' else st.mean(v)
        ok = {'>=': x >= val, '<=': x <= val, '>': x > val, '<': x < val}[op]
        lines.append(f'{c}: {stat} {x:.1f} over {len(v)} games {"ok" if ok else "FAIL"}')
    worst = max(worst, 0 if ok else 2)
verdict = ['PASS', 'INCONCLUSIVE', 'FAIL'][worst]
for v in ('PASS', 'INCONCLUSIVE', 'FAIL'):              # one verdict file per arm: drop stale ones
    try: os.remove(f'gauntlet/delivery-{arm}.{v}')
    except OSError: pass
open(f'gauntlet/delivery-{arm}.{verdict}', 'w').write(f'{run}\n' + '\n'.join(lines) + '\n')
print(f'delivery {arm}: {verdict}'); print('\n'.join(lines))
sys.exit(0 if verdict == 'PASS' else 3 if verdict == 'INCONCLUSIVE' else 1)
