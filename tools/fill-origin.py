#!/usr/bin/env python3
"""Where do a team's fills land? Classifies every fill up to round R by the tile's origin.
    tools/fill-origin.py [--to R] game.bc24 [...]
Origin: 'own' = a tile the filling team dug earlier, 'enemy' = dug by the other team, 'natural' = water from the map
or a water trap. Our side is read from the replay name (...__botA.bc24 -> A); without that, both teams print.
Output CSV: file,team,fills,own,enemy,natural,ownLagMedian (rounds from our dig to our fill of the same tile)."""
import re, subprocess, sys, os, statistics as st
REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
EV = re.compile(r'^r(\d+) ([AB])#-?\d+ (digs|fills) \((\d+),(\d+)\)')

def classify(lines):
    dug = {}; out = {t: [0, 0, 0, 0, []] for t in 'AB'}   # fills, own, enemy, natural, lags
    for ln in lines:
        m = EV.match(ln)
        if not m: continue
        rn, t, what, x, y = int(m[1]), m[2], m[3], int(m[4]), int(m[5])
        if what == 'digs': dug[(x, y)] = (t, rn); continue
        o = out[t]; o[0] += 1
        src = dug.pop((x, y), None)
        if src is None: o[3] += 1
        elif src[0] == t: o[1] += 1; o[4].append(rn - src[1])
        else: o[2] += 1
    return out

def main(argv):
    to = 200
    if argv[:1] == ['--to']: to, argv = int(argv[1]), argv[2:]
    print('file,team,fills,own,enemy,natural,ownLagMedian')
    for f in argv:
        txt = subprocess.run([os.path.join(REPO, 'tools/replay-dump.sh'), f, '--from', '1', '--to', str(to)],
                             capture_output=True, text=True).stdout.splitlines()
        side = re.search(r'bot([AB])\.bc24$', f)
        res = classify(txt)
        for t in ([side[1]] if side else 'AB'):
            n, own, en, nat, lags = res[t]
            print(f'{f},{t},{n},{own},{en},{nat},{st.median(lags) if lags else ""}')

if __name__ == '__main__': main(sys.argv[1:])
