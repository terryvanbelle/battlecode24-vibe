# Judge's independent check. Per-pair rows from sim/pairs.json (built with eval-paired.py's load/pairs).
import json, math, sys
import numpy as np
D = json.load(open('/tmp/claude-1000/-home-terryvanbelle-projects-vibe-2024/0c12d742-3a89-49d0-8e9e-654c983bb00e/scratchpad/criteria/sim/pairs.json'))
def arr(keys):
    rows = [r for k in keys for r in D[k]]
    up = np.array([r['up'] for r in rows]); dw = np.array([r['aw'] - r['cw'] for r in rows]); dc = np.array([r['ad'] - r['cd'] for r in rows], float)
    return up, dw, dc
def sign_p(g, l):
    g, l = int(g), int(l)
    n = g + l
    if n == 0: return 1.0
    k = min(g, l); return min(1.0, 2 * sum(math.comb(n, i) for i in range(k + 1)) / 2 ** n)
CRIT = np.full(800, 10**6, int)   # smallest gained count with exact two-sided sign p < 0.05 (and g > l)
for n in range(1, 800):
    tot = 2 ** n; cum = 0; kmax = -1
    for k in range(0, n // 2 + 1):          # k = min(g, l); p = 2 * P(X <= k)
        cum += math.comb(n, k)
        if 2 * cum >= 0.05 * tot: break
        kmax = k
    if kmax >= 0 and n - kmax > kmax: CRIT[n] = n - kmax
def stats(up, dw, dc):   # arrays shaped (N, m)
    m = dc.shape[1]; g = (dw > 0).sum(1); l = (dw < 0).sum(1); net = g - l
    mu = dc.mean(1); se = dc.std(1, ddof=1) / math.sqrt(m); t = np.where(se > 0, mu / np.where(se > 0, se, 1), 0)
    u = up.astype(bool); nu = u.sum(1)
    su = (dc * u).sum(1); mu_u = su / nu
    var_u = ((dc - mu_u[:, None]) ** 2 * u).sum(1) / (nu - 1); se_u = np.sqrt(var_u / nu)
    tu = np.where(se_u > 0, mu_u / np.where(se_u > 0, se_u, 1), 0)
    A = (g > l) & (g >= CRIT[np.minimum(g + l, 799)])
    B = (tu >= 2) & (net >= -5)
    return dict(net=net, t=t, tu=tu, A=A, B=B, mu=mu)
def simulate(N, up, dw, dc, signflip, tilt=0.5, blocks=3, rng=None):
    """Each block: 120 upper + 120 rest pairs drawn with replacement. signflip: random joint sign (null);
    tilt = P(sign +) when signflip (0.5 = exact null)."""
    iu = np.flatnonzero(up == 1); ir = np.flatnonzero(up == 0)
    out = []
    for b in range(blocks):
        idx = np.concatenate([rng.choice(iu, (N, 120)), rng.choice(ir, (N, 120))], 1)
        w, c, u = dw[idx], dc[idx], up[idx]
        if signflip:
            s = np.where(rng.random((N, 240)) < tilt, 1, -1); w = w * s; c = c * s
        out.append((u, w, c))
    looks = []
    for k in range(1, blocks + 1):
        u = np.concatenate([o[0] for o in out[:k]], 1); w = np.concatenate([o[1] for o in out[:k]], 1); c = np.concatenate([o[2] for o in out[:k]], 1)
        looks.append(stats(u, w, c))
    return looks
def rules(L, ca=2.2, cu=2.5):
    l1, l2, l3 = L
    r = {}
    r['R0 written'] = (l1['A'] | l1['B']) & (l2['A'] | l2['B'])
    r['R0 practice'] = (l1['A'] | l1['B'] | (l1['t'] >= 1)) & (l2['A'] | l2['B'])
    r['R0 pooled480'] = l2['A'] | l2['B']
    r['(i)+screen t2.0'] = (l1['t'] >= 0.5) & (l2['t'] >= 2.0) & (l2['net'] >= 0)
    def hyb(ca, cu):
        win = lambda l: ((l['t'] >= ca) | (l['tu'] >= cu)) & (l['net'] >= 0)
        sc = lambda l: np.maximum(l['t'], l['tu'] - (cu - ca))
        e1 = (l1['t'] >= 3.0) & (l1['net'] >= 0); go2 = ~e1 & (sc(l1) >= 0.5)
        e2 = go2 & win(l2); go3 = go2 & ~e2 & (sc(l2) >= 1.0); e3 = go3 & win(l3)
        games = np.where(~go2, 240, np.where(go3, 720, 480))
        return e1 | e2 | e3, games
    for a, b in ((2.2, 2.5), (2.3, 2.6), (2.0, 2.3), (2.4, 2.7)):
        r[f'(iv-h) {a}/{b}'] = hyb(a, b)
    # simple 3-look all-cell only (iv): early 3.0; stop <0.5; look2 promote 2.1 stop <1.0; look3 2.1
    win4 = lambda l, c: (l['t'] >= c) & (l['net'] >= 0)
    e1 = (l1['t'] >= 3.0) & (l1['net'] >= 0); go2 = ~e1 & (l1['t'] >= 0.5); e2 = go2 & win4(l2, 2.1); go3 = go2 & ~e2 & (l2['t'] >= 1.0); e3 = go3 & win4(l3, 2.1)
    r['(iv) 2.1'] = (e1 | e2 | e3, np.where(~go2, 240, np.where(go3, 720, 480)))
    return r
if __name__ == '__main__':
    rng = np.random.default_rng(int(sys.argv[1]) if len(sys.argv) > 1 else 1)
    scen = [('null, g4ship1 pairs sign-flipped', ['g4ship1', 'g4ship1-conf'], True, 0.5),
            ('null, all g4-era arm pairs sign-flipped', ['g4pick', 'g4pick-conf', 'g4bundle', 'g4econ2', 'g4crumb', 'g4gym1', 'g4ship1', 'g4ship1-conf'], True, 0.5),
            ('null, g3lost pairs sign-flipped', ['g3lost', 'g3lost-conf'], True, 0.5),
            ('g4ship1 as observed (bootstrap)', ['g4ship1', 'g4ship1-conf'], False, None),
            ('g4ship1 confirmation seeds only (bootstrap)', ['g4ship1-conf'], False, None),
            ('g4ship1 at half its observed effect (tilt)', ['g4ship1', 'g4ship1-conf'], 'half', None),
            ('g3lost as observed (bootstrap)', ['g3lost', 'g3lost-conf'], False, None),
            ('g4pick as observed (bootstrap)', ['g4pick', 'g4pick-conf'], False, None),
            ('g2cr as observed (bootstrap)', ['g2cr', 'g2cr-conf'], False, None)]
    N = 8000; R = 8
    for name, keys, sf, tilt in scen:
        up, dw, dc = arr(keys)
        acc = {}
        for rep in range(R):
            if sf == 'half':
                # sign-flip with tilt chosen so the expected capture delta is half the observed one
                a = np.abs(dc).mean(); target = dc.mean() / 2; tt = 0.5 + target / (2 * a)
                # use |pair| magnitudes with a tilted sign: orient each pair so that its capture change is >= 0 first
                s0 = np.where(dc != 0, np.sign(dc), np.where(dw != 0, np.sign(dw), 1))
                L = simulate(N, up, dw * s0, dc * s0, True, tt, rng=rng)
            else:
                L = simulate(N, up, dw, dc, sf, tilt if tilt else 0.5, rng=rng)
            for k, v in rules(L).items():
                p, g = v if isinstance(v, tuple) else (v, None)
                acc.setdefault(k, []).append((p.mean(), g.mean() if g is not None else float('nan')))
        mu = dc.mean(); print(f'\n{name}: n={len(dc)} mean cap delta {mu:+.3f}, wins/pair {dw.mean():+.4f}')
        for k, v in acc.items():
            p = np.mean([x[0] for x in v]); g = np.nanmean([x[1] for x in v]) if not all(math.isnan(x[1]) for x in v) else float('nan')
            print(f'  {k:22s} {100*p:5.1f}%' + (f'   arm games {g:4.0f}' if not math.isnan(g) else ''))
