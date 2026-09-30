# Which metric starts predicting the result first

110 games, 62 wins. Noise floor about 0.19; a correlation inside that band is not evidence.

Every metric is oriented so **higher is better for us**, so a positive correlation always means
"this being better goes with winning". `~avg` is the running mean over all rounds so far rather than
the snapshot at that round. **Onset** is the first round where the correlation reaches *+*threshold and holds.
**Anti** is the first round where it reaches *-*threshold and holds: there the metric predicts the result
backwards, which means either the orientation is wrong or something counter-intuitive is happening early.
A metric with an early anti and a late onset is changing sign, not rising early.
See `progress/METRICS.md` for how each quantity is computed.

| onset | anti | metric | peak corr | r100 | r200 | r300 | r400 | r600 | r900 |
|---|---|---|---|---|---|---|---|---|---|
| r50 | - | level_sum (us-them) | +0.62 | +0.39 | +0.36 | +0.52 | +0.60 | +0.60 | +0.57 |
| r50 | - | level_sum (us-them) ~avg | +0.63 | +0.39 | +0.38 | +0.50 | +0.59 | +0.60 | +0.61 |
| r50 | - | moves (us-them) | +0.57 | +0.32 | +0.26 | +0.44 | +0.51 | +0.54 | +0.52 |
| r50 | - | moves (us-them) ~avg | +0.54 | +0.32 | +0.30 | +0.37 | +0.43 | +0.49 | +0.51 |
| r250 | - | alive (us-them) | +0.57 | +0.18 | +0.15 | +0.43 | +0.57 | +0.45 | +0.49 |
| r250 | - | alive (us-them) ~avg | +0.62 | +0.25 | +0.24 | +0.52 | +0.60 | +0.60 | +0.57 |
| r250 | - | attacks (us-them) | +0.59 | . | . | +0.56 | +0.59 | +0.56 | +0.53 |
| r250 | - | attacks (us-them) ~avg | +0.60 | . | . | +0.55 | +0.59 | +0.57 | +0.55 |
| r250 | - | carrying (us-them) | +0.55 | +0.02 | . | +0.53 | +0.42 | +0.24 | +0.27 |
| r250 | - | deaths (us-them) [inverted] | +0.61 | . | . | +0.56 | +0.61 | +0.58 | +0.54 |
| r250 | - | deaths (us-them) [inverted] ~avg | +0.61 | . | . | +0.57 | +0.61 | +0.60 | +0.57 |
| r250 | - | heals (us-them) | +0.34 | . | . | +0.27 | +0.22 | +0.22 | +0.24 |
| r250 | - | heals (us-them) ~avg | +0.34 | . | . | +0.30 | +0.24 | +0.23 | +0.24 |
| r250 | - | hp (us-them) | +0.55 | +0.18 | +0.15 | +0.42 | +0.53 | +0.44 | +0.48 |
| r250 | - | hp (us-them) ~avg | +0.59 | +0.25 | +0.24 | +0.50 | +0.56 | +0.57 | +0.54 |
| r250 | - | kills (us-them) | +0.61 | . | . | +0.56 | +0.61 | +0.58 | +0.54 |
| r250 | - | kills (us-them) ~avg | +0.61 | . | . | +0.57 | +0.61 | +0.60 | +0.57 |
| r250 | - | net (us-them) | +0.61 | . | . | +0.56 | +0.61 | +0.58 | +0.54 |
| r250 | - | net (us-them) ~avg | +0.61 | . | . | +0.57 | +0.61 | +0.60 | +0.57 |
| r250 | - | pickups (us-them) | +0.53 | +0.01 | +0.01 | +0.50 | +0.51 | +0.45 | +0.41 |
| r250 | - | traps_built (us-them) | +0.45 | +0.00 | -0.06 | +0.43 | +0.45 | +0.35 | +0.24 |
| r250 | - | traps_stun (us-them) | +0.48 | +0.13 | +0.08 | +0.48 | +0.48 | +0.37 | +0.29 |
| r300 | - | pickups (us-them) ~avg | +0.52 | +0.01 | +0.01 | +0.47 | +0.52 | +0.48 | +0.43 |
| r300 | - | traps_stun (us-them) ~avg | +0.44 | +0.14 | +0.11 | +0.36 | +0.44 | +0.41 | +0.32 |
| r350 | - | captured (us-them) | +0.78 | . | . | +0.14 | +0.46 | +0.66 | +0.67 |
| r350 | - | captured (us-them) ~avg | +0.75 | . | . | +0.12 | +0.43 | +0.60 | +0.63 |
| r350 | - | carrying (us-them) ~avg | +0.50 | +0.02 | +0.01 | +0.23 | +0.42 | +0.47 | +0.44 |
| r400 | - | traps_built (us-them) ~avg | +0.38 | +0.02 | -0.04 | +0.20 | +0.34 | +0.37 | +0.27 |
| - | r200 | traps_expl (us-them) | -0.40 | -0.22 | -0.31 | -0.29 | -0.30 | -0.24 | -0.33 |
| - | r250 | traps_expl (us-them) ~avg | -0.43 | -0.20 | -0.27 | -0.31 | -0.33 | -0.27 | -0.31 |
| - | - | traps_hit (us-them) | +0.28 | . | . | +0.14 | +0.20 | +0.21 | +0.13 |
| - | - | traps_hit (us-them) ~avg | +0.28 | . | . | +0.19 | +0.20 | +0.20 | +0.10 |
| - | - | traps_water (us-them) | +0.21 | +0.10 | +0.14 | +0.16 | +0.17 | +0.19 | +0.16 |
| - | - | traps_water (us-them) ~avg | +0.24 | +0.09 | +0.11 | +0.14 | +0.17 | +0.20 | +0.19 |

**Reading it.** Earliest onset is the first place to look: temporal precedence is the one causal hint a
correlation can honestly give. Late-onset metrics are usually the scoreboard rather than the cause -- by then
the winner leads on everything. A high correlation earns a diagnostic game, not a code change.
