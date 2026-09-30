# Which metric starts predicting the result first

110 games, 82 wins. Noise floor about 0.19; a correlation inside that band is not evidence.

Every metric is oriented so **higher is better for us**, so a positive correlation always means
"this being better goes with winning". `~avg` is the running mean over all rounds so far rather than
the snapshot at that round. **Onset** is the first round where the correlation reaches *+*threshold and holds.
**Anti** is the first round where it reaches *-*threshold and holds: there the metric predicts the result
backwards, which means either the orientation is wrong or something counter-intuitive is happening early.
A metric with an early anti and a late onset is changing sign, not rising early.
See `progress/METRICS.md` for how each quantity is computed.

| onset | anti | metric | peak corr | r100 | r200 | r300 | r400 | r600 | r900 |
|---|---|---|---|---|---|---|---|---|---|
| r250 | - | alive (us-them) | +0.63 | +0.12 | +0.10 | +0.50 | +0.48 | +0.47 | +0.59 |
| r250 | - | alive (us-them) ~avg | +0.63 | +0.17 | +0.16 | +0.52 | +0.55 | +0.55 | +0.58 |
| r250 | - | attacks (us-them) | +0.68 | . | . | +0.65 | +0.63 | +0.62 | +0.66 |
| r250 | - | attacks (us-them) ~avg | +0.68 | . | . | +0.64 | +0.63 | +0.62 | +0.65 |
| r250 | - | carrying (us-them) | +0.65 | -0.02 | . | +0.54 | +0.51 | +0.57 | +0.26 |
| r250 | - | deaths (us-them) [inverted] | +0.61 | . | . | +0.56 | +0.55 | +0.55 | +0.57 |
| r250 | - | deaths (us-them) [inverted] ~avg | +0.61 | . | . | +0.56 | +0.56 | +0.55 | +0.57 |
| r250 | - | hp (us-them) | +0.64 | +0.12 | +0.10 | +0.54 | +0.49 | +0.49 | +0.56 |
| r250 | - | hp (us-them) ~avg | +0.61 | +0.17 | +0.16 | +0.56 | +0.56 | +0.56 | +0.58 |
| r250 | - | kills (us-them) | +0.61 | . | . | +0.56 | +0.55 | +0.55 | +0.57 |
| r250 | - | kills (us-them) ~avg | +0.61 | . | . | +0.56 | +0.56 | +0.55 | +0.57 |
| r250 | - | net (us-them) | +0.61 | . | . | +0.56 | +0.55 | +0.55 | +0.57 |
| r250 | - | net (us-them) ~avg | +0.61 | . | . | +0.56 | +0.56 | +0.55 | +0.57 |
| r300 | - | captured (us-them) | +0.86 | . | . | +0.33 | +0.53 | +0.64 | +0.77 |
| r300 | - | level_sum (us-them) | +0.67 | +0.22 | +0.23 | +0.51 | +0.57 | +0.56 | +0.54 |
| r300 | - | level_sum (us-them) ~avg | +0.59 | +0.21 | +0.23 | +0.33 | +0.47 | +0.51 | +0.54 |
| r300 | - | pickups (us-them) | +0.70 | +0.03 | +0.03 | +0.46 | +0.53 | +0.61 | +0.70 |
| r300 | - | traps_built (us-them) | +0.75 | -0.19 | -0.26 | +0.31 | +0.48 | +0.53 | +0.68 |
| r300 | - | traps_stun (us-them) | +0.75 | -0.09 | -0.10 | +0.41 | +0.55 | +0.58 | +0.69 |
| r350 | - | captured (us-them) ~avg | +0.76 | . | . | +0.27 | +0.44 | +0.55 | +0.66 |
| r350 | - | carrying (us-them) ~avg | +0.68 | -0.01 | +0.01 | +0.21 | +0.40 | +0.56 | +0.63 |
| r350 | - | moves (us-them) | +0.59 | +0.25 | +0.11 | +0.28 | +0.36 | +0.41 | +0.48 |
| r350 | - | pickups (us-them) ~avg | +0.67 | +0.03 | +0.03 | +0.24 | +0.41 | +0.53 | +0.66 |
| r400 | - | traps_hit (us-them) | +0.65 | . | . | +0.13 | +0.37 | +0.40 | +0.45 |
| r400 | - | traps_stun (us-them) ~avg | +0.74 | -0.10 | -0.11 | +0.20 | +0.36 | +0.44 | +0.58 |
| r500 | - | moves (us-them) ~avg | +0.56 | +0.26 | +0.19 | +0.22 | +0.26 | +0.32 | +0.40 |
| r500 | - | traps_built (us-them) ~avg | +0.74 | -0.19 | -0.23 | +0.03 | +0.21 | +0.33 | +0.58 |
| r500 | - | traps_hit (us-them) ~avg | +0.59 | . | . | +0.10 | +0.26 | +0.28 | +0.33 |
| - | - | heals (us-them) | -0.23 | . | . | -0.03 | -0.09 | -0.14 | -0.11 |
| - | - | heals (us-them) ~avg | -0.26 | . | . | -0.03 | -0.07 | -0.12 | -0.14 |
| - | - | traps_expl (us-them) | -0.29 | -0.22 | -0.29 | -0.25 | -0.24 | -0.21 | -0.12 |
| - | - | traps_expl (us-them) ~avg | -0.30 | -0.22 | -0.26 | -0.29 | -0.29 | -0.26 | -0.17 |
| - | r1900 | traps_water (us-them) | -0.31 | -0.07 | -0.04 | -0.04 | -0.07 | -0.09 | -0.07 |
| - | - | traps_water (us-them) ~avg | -0.29 | -0.09 | -0.08 | -0.07 | -0.08 | -0.10 | -0.04 |

**Reading it.** Earliest onset is the first place to look: temporal precedence is the one causal hint a
correlation can honestly give. Late-onset metrics are usually the scoreboard rather than the cause -- by then
the winner leads on everything. A high correlation earns a diagnostic game, not a code change.
