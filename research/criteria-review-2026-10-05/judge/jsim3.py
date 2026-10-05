# Wins-guard variants for the 3-look rule (all-cell t >= 2.3 or upper t >= 2.6). Guard is applied at every ship decision.
from jsim2 import *   # reuses sim_tilt, arr, stats, simulate (jsim2's own scenario loop is guarded below)
def disc_of(L):  # wins z = net / sqrt(discordant); stats() does not keep g+l, so recompute from net and A? keep it simple: add here
    return None
def hyb_guard(L, guard, ca=2.3, cu=2.6):
    l1, l2, l3 = L
    win = lambda l: ((l['t'] >= ca) | (l['tu'] >= cu)) & guard(l)
    sc = lambda l: np.maximum(l['t'], l['tu'] - (cu - ca))
    e1 = (l1['t'] >= 3.0) & guard(l1); go2 = ~e1 & (sc(l1) >= 0.5)
    e2 = go2 & win(l2); go3 = go2 & ~e2 & (sc(l2) >= 1.0); e3 = go3 & win(l3)
    return e1 | e2 | e3
GUARDS = {'net >= 0': lambda l: l['net'] >= 0,
          'wins z >= 0.5': lambda l: l['net'] >= 0.5 * np.sqrt(np.maximum(l['disc'], 1)),
          'wins z >= 1.0': lambda l: l['net'] >= 1.0 * np.sqrt(np.maximum(l['disc'], 1))}
_stats = stats
def stats_d(up, dw, dc):
    s = _stats(up, dw, dc); s['disc'] = (dw != 0).sum(1); return s
import jsim; jsim.stats = stats_d
import jsim2; jsim2.stats = stats_d
if __name__ == '__main__':
    rng = np.random.default_rng(23)
    up, dw, dc = arr(['g4ship1', 'g4ship1-conf'])
    scen = [('null (sign-flip)', 0.0, None), ('uniform +0.10', 0.10, None), ('uniform +0.14', 0.14, None),
            ('pure capture +0.14, wins 0', 0.14, 0.0), ('trade +0.15 / -1 pt', 0.15, -0.01), ('trade +0.15 / -2 pt', 0.15, -0.02)]
    for name, ct, wt in scen:
        acc = {k: [] for k in GUARDS}
        for rep in range(4):
            L = jsim2.sim_tilt(8000, up, dw, dc, ct, wt, rng)
            for k, g in GUARDS.items(): acc[k].append(hyb_guard(L, g).mean())
        print(name, '  '.join(f'{k}: {100*np.mean(v):.1f}%' for k, v in acc.items()), flush=True)
    for name, keys in (('g4ship1 as observed', ['g4ship1', 'g4ship1-conf']), ('g3lost as observed', ['g3lost', 'g3lost-conf']),
                       ('g2cr as observed', ['g2cr', 'g2cr-conf']), ('g4pick as observed', ['g4pick', 'g4pick-conf'])):
        u2, w2, c2 = arr(keys); acc = {k: [] for k in GUARDS}
        for rep in range(4):
            L = jsim.simulate(8000, u2, w2, c2, False, rng=rng)
            for k, g in GUARDS.items(): acc[k].append(hyb_guard(L, g).mean())
        print(name, '  '.join(f'{k}: {100*np.mean(v):.1f}%' for k, v in acc.items()), flush=True)
