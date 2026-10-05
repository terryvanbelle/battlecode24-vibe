# Tilted effects on g4ship1's pooled pairs: uniform capture effects, a harmful arm and a wins-for-captures trade arm.
from jsim import *
def sim_tilt(N, up, dw, dc, cap_target, win_target=None, rng=None, blocks=3):
    s0 = np.where(dc != 0, np.sign(dc), np.where(dw != 0, np.sign(dw), 1)); ow, oc = dw * s0, dc * s0
    tt = 0.5 + cap_target / (2 * np.abs(dc).mean())
    disc = (dw != 0).mean()
    iu = np.flatnonzero(up == 1); ir = np.flatnonzero(up == 0); out = []
    for b in range(blocks):
        idx = np.concatenate([rng.choice(iu, (N, 120)), rng.choice(ir, (N, 120))], 1)
        s = np.where(rng.random((N, 240)) < tt, 1, -1); w = ow[idx] * s; c = oc[idx] * s
        if win_target is not None:   # re-draw the sign of every discordant win change to hit the wins target
            pw = 0.5 + win_target / (2 * disc)
            w = np.where(w != 0, np.where(rng.random((N, 240)) < pw, 1, -1), 0)
        out.append((up[idx], w, c))
    L = []
    for k in range(1, blocks + 1):
        L.append(stats(*[np.concatenate([o[i] for o in out[:k]], 1) for i in range(3)]))
    return L
if __name__ == '__main__':
    rng = np.random.default_rng(11)
    up, dw, dc = arr(['g4ship1', 'g4ship1-conf'])
    keep = ['R0 written', 'R0 practice', 'R0 pooled480', '(i)+screen t2.0', '(iv-h) 2.2/2.5', '(iv-h) 2.3/2.6', '(iv) 2.1']
    scen = [('harm: capture -0.05, wins coupled', -0.05, None), ('harm: capture -0.10, wins -2 pt', -0.10, -0.02),
            ('uniform +0.05', 0.05, None), ('uniform +0.10', 0.10, None), ('uniform +0.14', 0.14, None), ('uniform +0.20', 0.20, None),
            ('trade: capture +0.10, wins -2 pt', 0.10, -0.02), ('trade: capture +0.15, wins -2 pt', 0.15, -0.02),
            ('trade: capture +0.15, wins -1 pt', 0.15, -0.01), ('pure capture +0.14, wins 0', 0.14, 0.0)]
    for name, ct, wt in scen:
        acc = {}
        for rep in range(5):
            L = sim_tilt(8000, up, dw, dc, ct, wt, rng)
            for k, v in rules(L).items():
                if k not in keep: continue
                p, g = v if isinstance(v, tuple) else (v, None)
                acc.setdefault(k, []).append((p.mean(), g.mean() if g is not None else np.nan))
        l2 = L[1]; print(f'\n{name}: E[cap delta @480] {l2["mu"].mean():+.3f}  E[net/480] {l2["net"].mean():+.1f}', flush=True)
        for k in keep:
            v = acc[k]; p = np.mean([x[0] for x in v]); g = np.nanmean([x[1] for x in v]) if not np.isnan(v[0][1]) else np.nan
            print(f'  {k:18s} {100*p:5.1f}%' + (f'   arm games {g:4.0f}' if not np.isnan(g) else ''), flush=True)
