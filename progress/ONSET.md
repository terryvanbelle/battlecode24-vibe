# Which metric starts predicting the result first

110 games, 84 wins. Noise floor about 0.19; a correlation inside that band is not evidence.

Every metric is oriented so **higher is better for us**, so a positive correlation always means
"this being better goes with winning". `~avg` is the running mean over all rounds so far rather than
the snapshot at that round. **Onset** is the first round where the correlation reaches *+*threshold and holds.
**Anti** is the first round where it reaches *-*threshold and holds: there the metric predicts the result
backwards, which means either the orientation is wrong or something counter-intuitive is happening early.
A metric with an early anti and a late onset is changing sign, not rising early.
See `progress/METRICS.md` for how each quantity is computed.

| onset | anti | metric | peak corr | r100 | r200 | r300 | r400 | r600 | r900 |
|---|---|---|---|---|---|---|---|---|---|
| r50 | - | level_sum (us-them) | +0.63 | +0.45 | +0.46 | +0.58 | +0.59 | +0.58 | +0.55 |
| r50 | - | level_sum (us-them) ~avg | +0.63 | +0.43 | +0.45 | +0.55 | +0.60 | +0.58 | +0.59 |
| r250 | - | alive (us-them) | +0.57 | +0.12 | +0.09 | +0.56 | +0.48 | +0.48 | +0.52 |
| r250 | - | alive (us-them) ~avg | +0.58 | +0.16 | +0.15 | +0.54 | +0.55 | +0.56 | +0.54 |
| r250 | - | attacks (us-them) | +0.61 | . | . | +0.60 | +0.59 | +0.58 | +0.53 |
| r250 | - | attacks (us-them) ~avg | +0.61 | . | . | +0.60 | +0.59 | +0.57 | +0.53 |
| r250 | - | deaths (us-them) [inverted] | +0.58 | . | . | +0.57 | +0.55 | +0.54 | +0.52 |
| r250 | - | deaths (us-them) [inverted] ~avg | +0.57 | . | . | +0.56 | +0.56 | +0.54 | +0.53 |
| r250 | - | hp (us-them) | +0.58 | +0.12 | +0.09 | +0.56 | +0.49 | +0.53 | +0.52 |
| r250 | - | hp (us-them) ~avg | +0.59 | +0.16 | +0.15 | +0.54 | +0.56 | +0.58 | +0.55 |
| r250 | - | kills (us-them) | +0.58 | . | . | +0.57 | +0.55 | +0.54 | +0.52 |
| r250 | - | kills (us-them) ~avg | +0.57 | . | . | +0.56 | +0.56 | +0.54 | +0.53 |
| r250 | - | net (us-them) | +0.58 | . | . | +0.57 | +0.55 | +0.54 | +0.52 |
| r250 | - | net (us-them) ~avg | +0.57 | . | . | +0.56 | +0.56 | +0.54 | +0.53 |
| r300 | - | captured (us-them) | +0.71 | . | . | +0.34 | +0.45 | +0.64 | +0.68 |
| r300 | - | carrying (us-them) | +0.48 | -0.01 | . | +0.41 | +0.38 | +0.26 | +0.38 |
| r300 | - | pickups (us-them) ~avg | +0.44 | +0.06 | +0.06 | +0.30 | +0.32 | +0.34 | +0.36 |
| r300 | - | traps_built (us-them) | +0.55 | -0.12 | -0.09 | +0.31 | +0.41 | +0.44 | +0.47 |
| r300 | - | traps_stun (us-them) | +0.58 | -0.02 | +0.03 | +0.36 | +0.44 | +0.47 | +0.50 |
| r350 | - | captured (us-them) ~avg | +0.65 | . | . | +0.29 | +0.40 | +0.50 | +0.58 |
| r350 | - | moves (us-them) | +0.50 | +0.18 | +0.10 | +0.27 | +0.38 | +0.44 | +0.47 |
| r350 | - | pickups (us-them) | +0.45 | +0.06 | +0.06 | +0.30 | +0.32 | +0.36 | +0.37 |
| r350 | - | traps_hit (us-them) | +0.47 | . | . | +0.26 | +0.34 | +0.37 | +0.37 |
| r350 | - | traps_hit (us-them) ~avg | +0.40 | . | . | +0.25 | +0.28 | +0.32 | +0.34 |
| r400 | - | carrying (us-them) ~avg | +0.51 | +0.02 | +0.04 | +0.18 | +0.32 | +0.42 | +0.50 |
| r400 | - | traps_stun (us-them) ~avg | +0.50 | -0.01 | +0.00 | +0.23 | +0.30 | +0.39 | +0.43 |
| r500 | - | moves (us-them) ~avg | +0.45 | +0.18 | +0.15 | +0.20 | +0.27 | +0.38 | +0.42 |
| r550 | - | traps_built (us-them) ~avg | +0.46 | -0.11 | -0.11 | +0.12 | +0.22 | +0.33 | +0.39 |
| - | - | heals (us-them) | +0.11 | . | . | +0.06 | -0.03 | -0.04 | +0.09 |
| - | - | heals (us-them) ~avg | -0.11 | . | . | +0.05 | -0.02 | -0.03 | +0.08 |
| - | - | traps_expl (us-them) | -0.25 | -0.19 | -0.25 | -0.22 | -0.13 | -0.18 | -0.17 |
| - | - | traps_expl (us-them) ~avg | -0.24 | -0.17 | -0.21 | -0.24 | -0.22 | -0.22 | -0.19 |
| - | - | traps_water (us-them) | -0.11 | -0.07 | -0.05 | -0.01 | -0.01 | -0.03 | -0.02 |
| - | - | traps_water (us-them) ~avg | -0.10 | -0.09 | -0.08 | -0.05 | -0.03 | -0.02 | +0.00 |

**Reading it.** Earliest onset is the first place to look: temporal precedence is the one causal hint a
correlation can honestly give. Late-onset metrics are usually the scoreboard rather than the cause -- by then
the winner leads on everything. A high correlation earns a diagnostic game, not a code change.
