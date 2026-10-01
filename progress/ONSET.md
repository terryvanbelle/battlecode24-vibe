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
| r50 | - | level_sum (us-them) | +0.58 | +0.40 | +0.40 | +0.49 | +0.55 | +0.55 | +0.45 |
| r50 | - | level_sum (us-them) ~avg | +0.56 | +0.37 | +0.39 | +0.47 | +0.54 | +0.54 | +0.43 |
| r250 | - | alive (us-them) | +0.60 | +0.12 | +0.10 | +0.49 | +0.50 | +0.47 | +0.42 |
| r250 | - | alive (us-them) ~avg | +0.55 | +0.17 | +0.16 | +0.53 | +0.55 | +0.52 | +0.44 |
| r250 | - | attacks (us-them) | +0.63 | . | . | +0.62 | +0.63 | +0.58 | +0.55 |
| r250 | - | attacks (us-them) ~avg | +0.62 | . | . | +0.61 | +0.62 | +0.59 | +0.55 |
| r250 | - | deaths (us-them) [inverted] | +0.54 | . | . | +0.53 | +0.54 | +0.51 | +0.44 |
| r250 | - | deaths (us-them) [inverted] ~avg | +0.53 | . | . | +0.53 | +0.53 | +0.51 | +0.44 |
| r250 | - | hp (us-them) | +0.60 | +0.12 | +0.10 | +0.48 | +0.51 | +0.46 | +0.39 |
| r250 | - | hp (us-them) ~avg | +0.55 | +0.17 | +0.16 | +0.53 | +0.55 | +0.49 | +0.43 |
| r250 | - | kills (us-them) | +0.54 | . | . | +0.53 | +0.54 | +0.51 | +0.44 |
| r250 | - | kills (us-them) ~avg | +0.53 | . | . | +0.53 | +0.53 | +0.51 | +0.44 |
| r250 | - | net (us-them) | +0.54 | . | . | +0.53 | +0.54 | +0.51 | +0.44 |
| r250 | - | net (us-them) ~avg | +0.53 | . | . | +0.53 | +0.53 | +0.51 | +0.44 |
| r300 | - | captured (us-them) | +0.79 | . | . | +0.37 | +0.56 | +0.69 | +0.73 |
| r300 | - | captured (us-them) ~avg | +0.76 | . | . | +0.37 | +0.53 | +0.62 | +0.69 |
| r300 | - | carrying (us-them) | +0.54 | -0.08 | . | +0.49 | +0.44 | +0.54 | +0.42 |
| r300 | - | pickups (us-them) | +0.50 | -0.01 | -0.01 | +0.31 | +0.42 | +0.46 | +0.43 |
| r300 | - | traps_built (us-them) | +0.59 | -0.13 | -0.20 | +0.30 | +0.47 | +0.52 | +0.55 |
| r300 | - | traps_stun (us-them) | +0.64 | -0.03 | -0.04 | +0.39 | +0.54 | +0.58 | +0.60 |
| r350 | - | moves (us-them) | +0.47 | +0.22 | +0.08 | +0.25 | +0.37 | +0.39 | +0.40 |
| r350 | - | traps_stun (us-them) ~avg | +0.62 | -0.05 | -0.04 | +0.23 | +0.39 | +0.48 | +0.56 |
| r400 | - | carrying (us-them) ~avg | +0.40 | -0.06 | -0.04 | +0.16 | +0.32 | +0.40 | +0.37 |
| r400 | - | pickups (us-them) ~avg | +0.47 | -0.01 | -0.01 | +0.15 | +0.31 | +0.37 | +0.36 |
| r400 | - | traps_hit (us-them) | +0.46 | . | . | +0.14 | +0.32 | +0.43 | +0.45 |
| r450 | - | traps_built (us-them) ~avg | +0.54 | -0.14 | -0.17 | +0.07 | +0.26 | +0.39 | +0.49 |
| r500 | - | moves (us-them) ~avg | +0.45 | +0.23 | +0.16 | +0.19 | +0.29 | +0.33 | +0.35 |
| r500 | - | traps_hit (us-them) ~avg | +0.41 | . | . | +0.12 | +0.24 | +0.37 | +0.40 |
| - | r1250 | heals (us-them) | -0.43 | . | . | -0.06 | -0.14 | -0.13 | -0.18 |
| - | r1250 | heals (us-them) ~avg | -0.41 | . | . | -0.07 | -0.13 | -0.13 | -0.20 |
| - | r250 | traps_expl (us-them) | -0.38 | -0.20 | -0.29 | -0.32 | -0.29 | -0.33 | -0.33 |
| - | r400 | traps_expl (us-them) ~avg | -0.43 | -0.19 | -0.24 | -0.30 | -0.30 | -0.33 | -0.37 |
| - | - | traps_water (us-them) | -0.22 | -0.07 | -0.04 | -0.06 | -0.06 | -0.08 | -0.04 |
| - | - | traps_water (us-them) ~avg | -0.21 | -0.09 | -0.07 | -0.07 | -0.07 | -0.09 | -0.05 |

**Reading it.** Earliest onset is the first place to look: temporal precedence is the one causal hint a
correlation can honestly give. Late-onset metrics are usually the scoreboard rather than the cause -- by then
the winner leads on everything. A high correlation earns a diagnostic game, not a code change.
