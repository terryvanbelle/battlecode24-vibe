# Which metric starts predicting the result first

112 games, 54 wins. Noise floor about 0.19; a correlation inside that band is not evidence.

Every metric is oriented so **higher is better for us**, so a positive correlation always means
"this being better goes with winning". `~avg` is the running mean over all rounds so far rather than
the snapshot at that round. **Onset** is the first round where the correlation reaches *+*threshold and holds.
**Anti** is the first round where it reaches *-*threshold and holds: there the metric predicts the result
backwards, which means either the orientation is wrong or something counter-intuitive is happening early.
A metric with an early anti and a late onset is changing sign, not rising early.
See `progress/METRICS.md` for how each quantity is computed.

| onset | anti | metric | peak corr | r100 | r200 | r300 | r400 | r600 | r900 |
|---|---|---|---|---|---|---|---|---|---|
| r50 | - | level_sum (us-them) | +0.70 | +0.45 | +0.48 | +0.60 | +0.64 | +0.62 | +0.69 |
| r50 | - | level_sum (us-them) ~avg | +0.75 | +0.43 | +0.46 | +0.56 | +0.66 | +0.64 | +0.69 |
| r250 | - | alive (us-them) | +0.73 | . | . | +0.47 | +0.45 | +0.47 | +0.60 |
| r250 | - | alive (us-them) ~avg | +0.73 | . | . | +0.52 | +0.59 | +0.58 | +0.68 |
| r250 | - | attacks (us-them) | +0.65 | . | . | +0.56 | +0.59 | +0.62 | +0.65 |
| r250 | - | attacks (us-them) ~avg | +0.62 | . | . | +0.55 | +0.58 | +0.60 | +0.62 |
| r250 | - | deaths (us-them) [inverted] | +0.73 | . | . | +0.55 | +0.60 | +0.58 | +0.68 |
| r250 | - | deaths (us-them) [inverted] ~avg | +0.70 | . | . | +0.53 | +0.59 | +0.59 | +0.65 |
| r250 | - | hp (us-them) | +0.71 | . | . | +0.46 | +0.48 | +0.48 | +0.56 |
| r250 | - | hp (us-them) ~avg | +0.67 | . | . | +0.53 | +0.59 | +0.60 | +0.67 |
| r250 | - | kills (us-them) | +0.73 | . | . | +0.55 | +0.60 | +0.58 | +0.68 |
| r250 | - | kills (us-them) ~avg | +0.70 | . | . | +0.53 | +0.59 | +0.59 | +0.65 |
| r250 | - | net (us-them) | +0.73 | . | . | +0.55 | +0.60 | +0.58 | +0.68 |
| r250 | - | net (us-them) ~avg | +0.70 | . | . | +0.53 | +0.59 | +0.59 | +0.65 |
| r300 | - | traps_stun (us-them) | +0.69 | -0.01 | -0.13 | +0.30 | +0.43 | +0.52 | +0.59 |
| r350 | - | traps_built (us-them) | +0.69 | +0.01 | -0.13 | +0.24 | +0.39 | +0.50 | +0.58 |
| r400 | - | captured (us-them) | +0.76 | . | . | +0.14 | +0.40 | +0.63 | +0.60 |
| r400 | - | carrying (us-them) | +0.50 | -0.01 | . | +0.31 | +0.47 | +0.29 | +0.32 |
| r450 | - | captured (us-them) ~avg | +0.62 | . | . | +0.11 | +0.27 | +0.46 | +0.51 |
| r450 | - | carrying (us-them) ~avg | +0.45 | -0.03 | +0.03 | +0.12 | +0.28 | +0.42 | +0.42 |
| r450 | - | pickups (us-them) | +0.41 | -0.02 | -0.02 | +0.07 | +0.29 | +0.28 | +0.23 |
| r450 | - | traps_hit (us-them) | +0.68 | . | . | +0.24 | +0.28 | +0.45 | +0.56 |
| r450 | - | traps_stun (us-them) ~avg | +0.65 | -0.04 | -0.08 | +0.14 | +0.27 | +0.40 | +0.46 |
| r500 | - | moves (us-them) | +0.66 | -0.25 | -0.30 | -0.08 | +0.15 | +0.39 | +0.59 |
| r500 | - | traps_hit (us-them) ~avg | +0.62 | . | . | +0.22 | +0.27 | +0.37 | +0.45 |
| r550 | - | traps_built (us-them) ~avg | +0.64 | -0.02 | -0.07 | +0.07 | +0.20 | +0.35 | +0.44 |
| r750 | - | moves (us-them) ~avg | +0.57 | -0.24 | -0.29 | -0.22 | -0.11 | +0.16 | +0.37 |
| r1050 | - | heals (us-them) | +0.46 | . | . | -0.09 | -0.08 | -0.02 | +0.18 |
| r1250 | - | heals (us-them) ~avg | +0.45 | . | . | -0.09 | -0.07 | -0.02 | +0.12 |
| r1400 | - | pickups (us-them) ~avg | +0.34 | -0.02 | -0.02 | +0.03 | +0.15 | +0.23 | +0.20 |
| - | r250 | traps_expl (us-them) | -0.32 | +0.02 | -0.11 | -0.28 | -0.22 | -0.20 | -0.10 |
| - | - | traps_expl (us-them) ~avg | -0.23 | -0.00 | -0.04 | -0.15 | -0.19 | -0.23 | -0.13 |
| - | r650 | traps_water (us-them) | -0.32 | +0.25 | -0.01 | -0.26 | -0.26 | -0.30 | -0.31 |
| - | r650 | traps_water (us-them) ~avg | -0.30 | +0.24 | +0.06 | -0.22 | -0.24 | -0.27 | -0.29 |

**Reading it.** Earliest onset is the first place to look: temporal precedence is the one causal hint a
correlation can honestly give. Late-onset metrics are usually the scoreboard rather than the cause -- by then
the winner leads on everything. A high correlation earns a diagnostic game, not a code change.
