# Which metric starts predicting the result first

110 games, 81 wins. Noise floor about 0.19; a correlation inside that band is not evidence.

Every metric is oriented so **higher is better for us**, so a positive correlation always means
"this being better goes with winning". `~avg` is the running mean over all rounds so far rather than
the snapshot at that round. **Onset** is the first round where the correlation reaches *+*threshold and holds.
**Anti** is the first round where it reaches *-*threshold and holds: there the metric predicts the result
backwards, which means either the orientation is wrong or something counter-intuitive is happening early.
A metric with an early anti and a late onset is changing sign, not rising early.
See `progress/METRICS.md` for how each quantity is computed.

| onset | anti | metric | peak corr | r100 | r200 | r300 | r400 | r600 | r900 |
|---|---|---|---|---|---|---|---|---|---|
| r50 | - | level_sum (us-them) | +0.64 | +0.44 | +0.42 | +0.53 | +0.59 | +0.61 | +0.62 |
| r50 | - | level_sum (us-them) ~avg | +0.63 | +0.45 | +0.44 | +0.53 | +0.59 | +0.60 | +0.61 |
| r250 | - | alive (us-them) | +0.67 | +0.12 | +0.10 | +0.47 | +0.59 | +0.53 | +0.57 |
| r250 | - | alive (us-them) ~avg | +0.61 | +0.17 | +0.16 | +0.49 | +0.58 | +0.58 | +0.61 |
| r250 | - | attacks (us-them) | +0.65 | . | . | +0.58 | +0.62 | +0.62 | +0.62 |
| r250 | - | attacks (us-them) ~avg | +0.65 | . | . | +0.57 | +0.60 | +0.62 | +0.61 |
| r250 | - | carrying (us-them) | +0.56 | +0.08 | . | +0.49 | +0.40 | +0.43 | +0.35 |
| r250 | - | deaths (us-them) [inverted] | +0.58 | . | . | +0.53 | +0.58 | +0.56 | +0.58 |
| r250 | - | deaths (us-them) [inverted] ~avg | +0.59 | . | . | +0.51 | +0.56 | +0.56 | +0.57 |
| r250 | - | hp (us-them) | +0.66 | +0.12 | +0.10 | +0.48 | +0.56 | +0.51 | +0.55 |
| r250 | - | hp (us-them) ~avg | +0.60 | +0.17 | +0.16 | +0.51 | +0.59 | +0.59 | +0.59 |
| r250 | - | kills (us-them) | +0.58 | . | . | +0.53 | +0.58 | +0.56 | +0.58 |
| r250 | - | kills (us-them) ~avg | +0.59 | . | . | +0.51 | +0.56 | +0.56 | +0.57 |
| r250 | - | net (us-them) | +0.58 | . | . | +0.53 | +0.58 | +0.56 | +0.58 |
| r250 | - | net (us-them) ~avg | +0.59 | . | . | +0.51 | +0.56 | +0.56 | +0.57 |
| r250 | - | pickups (us-them) | +0.59 | +0.09 | +0.09 | +0.40 | +0.45 | +0.44 | +0.47 |
| r250 | - | pickups (us-them) ~avg | +0.54 | +0.09 | +0.09 | +0.42 | +0.46 | +0.45 | +0.45 |
| r300 | - | traps_stun (us-them) | +0.76 | -0.01 | -0.01 | +0.37 | +0.50 | +0.59 | +0.66 |
| r350 | - | captured (us-them) | +0.87 | . | . | +0.15 | +0.47 | +0.64 | +0.69 |
| r350 | - | captured (us-them) ~avg | +0.75 | . | . | +0.12 | +0.40 | +0.55 | +0.65 |
| r350 | - | carrying (us-them) ~avg | +0.65 | +0.12 | +0.13 | +0.29 | +0.41 | +0.54 | +0.58 |
| r350 | - | moves (us-them) | +0.62 | +0.25 | +0.12 | +0.29 | +0.39 | +0.50 | +0.56 |
| r350 | - | traps_built (us-them) | +0.75 | -0.11 | -0.16 | +0.29 | +0.45 | +0.55 | +0.63 |
| r350 | - | traps_stun (us-them) ~avg | +0.76 | -0.00 | -0.01 | +0.23 | +0.36 | +0.47 | +0.55 |
| r450 | - | moves (us-them) ~avg | +0.55 | +0.25 | +0.20 | +0.23 | +0.29 | +0.43 | +0.50 |
| r450 | - | traps_hit (us-them) | +0.71 | . | . | +0.12 | +0.30 | +0.41 | +0.53 |
| r500 | - | traps_built (us-them) ~avg | +0.74 | -0.13 | -0.16 | +0.09 | +0.26 | +0.41 | +0.51 |
| r550 | - | traps_hit (us-them) ~avg | +0.69 | . | . | +0.13 | +0.22 | +0.33 | +0.44 |
| - | - | heals (us-them) | -0.16 | . | . | +0.07 | +0.03 | -0.06 | +0.01 |
| - | - | heals (us-them) ~avg | -0.14 | . | . | +0.07 | +0.04 | -0.04 | -0.01 |
| - | r250 | traps_expl (us-them) | -0.31 | -0.17 | -0.26 | -0.30 | -0.25 | -0.21 | -0.23 |
| - | - | traps_expl (us-them) ~avg | -0.30 | -0.19 | -0.24 | -0.30 | -0.29 | -0.26 | -0.24 |
| - | - | traps_water (us-them) | -0.20 | -0.08 | -0.04 | -0.04 | -0.05 | -0.08 | -0.06 |
| - | - | traps_water (us-them) ~avg | -0.19 | -0.10 | -0.08 | -0.06 | -0.06 | -0.08 | -0.06 |

**Reading it.** Earliest onset is the first place to look: temporal precedence is the one causal hint a
correlation can honestly give. Late-onset metrics are usually the scoreboard rather than the cause -- by then
the winner leads on everything. A high correlation earns a diagnostic game, not a code change.
