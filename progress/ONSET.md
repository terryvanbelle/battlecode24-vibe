# Which metric starts predicting the result first

108 games, 80 wins. Noise floor about 0.19; a correlation inside that band is not evidence.

Every metric is oriented so **higher is better for us**, so a positive correlation always means
"this being better goes with winning". `~avg` is the running mean over all rounds so far rather than
the snapshot at that round. **Onset** is the first round where the correlation reaches *+*threshold and holds.
**Anti** is the first round where it reaches *-*threshold and holds: there the metric predicts the result
backwards, which means either the orientation is wrong or something counter-intuitive is happening early.
A metric with an early anti and a late onset is changing sign, not rising early.
See `progress/METRICS.md` for how each quantity is computed.

| onset | anti | metric | peak corr | r100 | r200 | r300 | r400 | r600 | r900 |
|---|---|---|---|---|---|---|---|---|---|
| r50 | - | level_sum (us-them) | +0.58 | +0.37 | +0.40 | +0.58 | +0.50 | +0.51 | +0.44 |
| r50 | - | level_sum (us-them) ~avg | +0.56 | +0.35 | +0.38 | +0.51 | +0.54 | +0.52 | +0.44 |
| r250 | - | alive (us-them) | +0.53 | +0.09 | +0.09 | +0.48 | +0.46 | +0.47 | +0.52 |
| r250 | - | attacks (us-them) | +0.62 | . | . | +0.62 | +0.58 | +0.57 | +0.58 |
| r250 | - | attacks (us-them) ~avg | +0.62 | . | . | +0.61 | +0.58 | +0.58 | +0.58 |
| r250 | - | carrying (us-them) | +0.55 | +0.06 | . | +0.46 | +0.29 | +0.40 | +0.50 |
| r250 | - | deaths (us-them) [inverted] | +0.54 | . | . | +0.54 | +0.48 | +0.47 | +0.45 |
| r250 | - | deaths (us-them) [inverted] ~avg | +0.54 | . | . | +0.54 | +0.50 | +0.47 | +0.43 |
| r250 | - | hp (us-them) | +0.55 | +0.09 | +0.09 | +0.49 | +0.47 | +0.49 | +0.55 |
| r250 | - | kills (us-them) | +0.54 | . | . | +0.54 | +0.48 | +0.47 | +0.45 |
| r250 | - | kills (us-them) ~avg | +0.54 | . | . | +0.54 | +0.50 | +0.47 | +0.43 |
| r250 | - | net (us-them) | +0.54 | . | . | +0.54 | +0.48 | +0.47 | +0.45 |
| r250 | - | net (us-them) ~avg | +0.54 | . | . | +0.54 | +0.50 | +0.47 | +0.43 |
| r250 | - | pickups (us-them) | +0.63 | +0.13 | +0.13 | +0.52 | +0.51 | +0.49 | +0.50 |
| r300 | - | alive (us-them) ~avg | +0.49 | +0.12 | +0.11 | +0.36 | +0.40 | +0.44 | +0.46 |
| r300 | - | captured (us-them) | +0.83 | . | . | +0.34 | +0.50 | +0.55 | +0.70 |
| r300 | - | hp (us-them) ~avg | +0.52 | +0.12 | +0.11 | +0.38 | +0.43 | +0.47 | +0.51 |
| r300 | - | moves (us-them) | +0.44 | +0.23 | +0.12 | +0.31 | +0.39 | +0.43 | +0.44 |
| r300 | - | pickups (us-them) ~avg | +0.68 | +0.13 | +0.13 | +0.34 | +0.47 | +0.51 | +0.52 |
| r350 | - | captured (us-them) ~avg | +0.81 | . | . | +0.28 | +0.46 | +0.53 | +0.61 |
| r350 | - | carrying (us-them) ~avg | +0.51 | +0.07 | +0.08 | +0.28 | +0.37 | +0.44 | +0.51 |
| r350 | - | traps_built (us-them) | +0.61 | -0.19 | -0.21 | +0.24 | +0.33 | +0.49 | +0.59 |
| r350 | - | traps_stun (us-them) | +0.62 | -0.12 | -0.11 | +0.30 | +0.39 | +0.53 | +0.61 |
| r400 | - | moves (us-them) ~avg | +0.39 | +0.23 | +0.19 | +0.23 | +0.30 | +0.36 | +0.39 |
| r500 | - | traps_hit (us-them) | +0.52 | . | . | +0.18 | +0.24 | +0.38 | +0.50 |
| r500 | - | traps_stun (us-them) ~avg | +0.63 | -0.11 | -0.13 | +0.16 | +0.24 | +0.38 | +0.49 |
| r650 | - | traps_built (us-them) ~avg | +0.53 | -0.19 | -0.22 | +0.04 | +0.13 | +0.29 | +0.42 |
| r650 | - | traps_hit (us-them) ~avg | +0.48 | . | . | +0.17 | +0.20 | +0.30 | +0.37 |
| - | r1050 | heals (us-them) | -0.63 | . | . | -0.04 | -0.13 | -0.15 | -0.21 |
| - | r1000 | heals (us-them) ~avg | -0.67 | . | . | -0.02 | -0.09 | -0.12 | -0.24 |
| - | r700 | traps_expl (us-them) | -0.38 | -0.20 | -0.27 | -0.25 | -0.26 | -0.27 | -0.30 |
| - | r400 | traps_expl (us-them) ~avg | -0.43 | -0.19 | -0.23 | -0.27 | -0.31 | -0.33 | -0.42 |
| - | - | traps_water (us-them) | -0.18 | -0.09 | -0.04 | +0.01 | +0.00 | -0.00 | -0.04 |
| - | - | traps_water (us-them) ~avg | -0.21 | -0.10 | -0.08 | -0.04 | -0.03 | -0.01 | -0.05 |

**Reading it.** Earliest onset is the first place to look: temporal precedence is the one causal hint a
correlation can honestly give. Late-onset metrics are usually the scoreboard rather than the cause -- by then
the winner leads on everything. A high correlation earns a diagnostic game, not a code change.
