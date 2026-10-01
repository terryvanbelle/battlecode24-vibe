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
games = list(rows.values())
def vals(col):
    out = []
    for g in games:
        try: out.append(float(g[col]))
        except (KeyError, ValueError, TypeError): pass
    return out
ok_all, lines = True, []
for c in checks.split():
    m = re.fullmatch(r'(median|mean|fire|rel):(\w+)(>=|<=|>|<)(-?[\d.]+)(?:(>=)([\d.]+))?', c)
    if not m: print('!! bad check', c); sys.exit(2)
    stat, col, op, val = m.group(1), m.group(2), m.group(3), float(m.group(4))
    v = vals(col)
    if not v: lines.append(f'{c}: NO DATA'); ok_all = False; continue
    if stat == 'rel':   # paired: arm mean on cells shared with the base <= / >= ratio x base mean on those cells
        pairs = []
        for k, g in rows.items():
            b = base.get(k)
            try: pairs.append((float(g[col]), float(b[col])))
            except (TypeError, KeyError, ValueError): pass
        if not pairs: lines.append(f'{c}: NO PAIRED DATA (base missing?)'); ok_all = False; continue
        ma = st.mean(a for a, _ in pairs); mb = st.mean(b for _, b in pairs)
        d = [a - b for a, b in pairs]; se = st.stdev(d) / math.sqrt(len(d)) if len(d) > 1 else float('nan')
        target = val * mb
        ok = {'>=': ma >= target, '<=': ma <= target, '>': ma > target, '<': ma < target}[op]
        lines.append(f'{c}: arm {ma:.2f} vs base {mb:.2f} on {len(pairs)} shared cells (target {op} {target:.2f}; diff {ma - mb:+.2f} +- {se:.2f}) {"ok" if ok else "FAIL"}')
    elif stat == 'fire':
        x = sum(1 for a in v if (a > val if op == '>' else a >= val if op == '>=' else a < val if op == '<' else a <= val)) / len(v)
        need = float(m.group(6) or 0.9); ok = x >= need; lines.append(f'{c}: fired in {x:.0%} of {len(v)} (need {need:.0%}) {"ok" if ok else "FAIL"}')
    else:
        x = st.median(v) if stat == 'median' else st.mean(v)
        ok = {'>=': x >= val, '<=': x <= val, '>': x > val, '<': x < val}[op]
        lines.append(f'{c}: {stat} {x:.1f} over {len(v)} games {"ok" if ok else "FAIL"}')
    ok_all &= ok
verdict = 'PASS' if ok_all else 'FAIL'
open(f'gauntlet/delivery-{arm}.{verdict}', 'w').write(f'{run}\n' + '\n'.join(lines) + '\n')
print(f'delivery {arm}: {verdict}'); print('\n'.join(lines))
