# Which metric starts predicting the result first

109 games, 56 wins. Noise floor about 0.19; a correlation inside that band is not evidence.

Every metric is oriented so **higher is better for us**, so a positive correlation always means
"this being better goes with winning". `~avg` is the running mean over all rounds so far rather than
the snapshot at that round. **Onset** is the first round where the correlation reaches *+*threshold and holds.
**Anti** is the first round where it reaches *-*threshold and holds: there the metric predicts the result
backwards, which means either the orientation is wrong or something counter-intuitive is happening early.
A metric with an early anti and a late onset is changing sign, not rising early.
See `progress/METRICS.md` for how each quantity is computed.

| onset | anti | metric | peak corr | r100 | r200 | r300 | r400 | r600 | r900 |
|---|---|---|---|---|---|---|---|---|---|
| r100 | - | level_sum (us-them) | +0.66 | +0.33 | +0.33 | +0.20 | +0.23 | +0.42 | +0.51 |
| r100 | - | level_sum (us-them) ~avg | +0.64 | +0.32 | +0.32 | +0.34 | +0.28 | +0.39 | +0.50 |
| r250 | - | carrying (us-them) | +0.51 | -0.06 | . | +0.41 | +0.47 | +0.39 | +0.34 |
| r300 | - | alive (us-them) | +0.47 | +0.01 | +0.06 | +0.31 | +0.34 | +0.38 | +0.36 |
| r300 | - | pickups (us-them) | +0.43 | +0.03 | +0.03 | +0.36 | +0.41 | +0.24 | +0.13 |
| r350 | - | attacks (us-them) | +0.64 | . | . | +0.27 | +0.34 | +0.43 | +0.44 |
| r350 | - | captured (us-them) | +0.69 | . | . | +0.29 | +0.59 | +0.66 | +0.69 |
| r350 | - | captured (us-them) ~avg | +0.65 | . | . | +0.29 | +0.53 | +0.63 | +0.63 |
| r350 | - | deaths (us-them) [inverted] | +0.59 | +0.09 | +0.09 | +0.22 | +0.32 | +0.43 | +0.48 |
| r350 | - | hp (us-them) | +0.49 | +0.01 | +0.06 | +0.30 | +0.31 | +0.36 | +0.33 |
| r350 | - | kills (us-them) | +0.59 | +0.09 | +0.09 | +0.22 | +0.32 | +0.43 | +0.48 |
| r350 | - | net (us-them) | +0.59 | +0.09 | +0.09 | +0.22 | +0.32 | +0.43 | +0.48 |
| r400 | - | carrying (us-them) ~avg | +0.54 | -0.03 | -0.05 | +0.12 | +0.32 | +0.48 | +0.51 |
| r400 | - | pickups (us-them) ~avg | +0.34 | +0.03 | +0.03 | +0.19 | +0.33 | +0.31 | +0.20 |
| r400 | - | traps_stun (us-them) | +0.57 | -0.03 | +0.09 | +0.21 | +0.30 | +0.31 | +0.39 |
| r450 | - | attacks (us-them) ~avg | +0.63 | . | . | +0.22 | +0.28 | +0.39 | +0.42 |
| r450 | - | deaths (us-them) [inverted] ~avg | +0.57 | +0.09 | +0.09 | +0.18 | +0.26 | +0.38 | +0.47 |
| r450 | - | kills (us-them) ~avg | +0.57 | +0.09 | +0.09 | +0.18 | +0.26 | +0.38 | +0.47 |
| r450 | - | net (us-them) ~avg | +0.57 | +0.09 | +0.09 | +0.18 | +0.26 | +0.38 | +0.47 |
| r550 | - | alive (us-them) ~avg | +0.38 | +0.06 | +0.05 | +0.15 | +0.22 | +0.31 | +0.32 |
| r650 | - | hp (us-them) ~avg | +0.41 | +0.06 | +0.05 | +0.15 | +0.21 | +0.28 | +0.30 |
| r750 | - | traps_built (us-them) | +0.51 | -0.23 | -0.15 | +0.11 | +0.25 | +0.23 | +0.34 |
| r850 | - | traps_hit (us-them) | +0.51 | . | . | -0.03 | +0.13 | +0.21 | +0.33 |
| r950 | - | traps_stun (us-them) ~avg | +0.53 | -0.03 | +0.02 | +0.13 | +0.22 | +0.19 | +0.26 |
| r1000 | - | traps_built (us-them) ~avg | +0.47 | -0.22 | -0.21 | -0.11 | +0.07 | +0.06 | +0.20 |
| r1000 | - | traps_hit (us-them) ~avg | +0.46 | . | . | -0.10 | +0.02 | +0.06 | +0.20 |
| - | - | heals (us-them) | +0.18 | . | . | -0.04 | +0.03 | +0.10 | +0.18 |
| - | - | heals (us-them) ~avg | +0.20 | . | . | -0.05 | +0.00 | +0.11 | +0.20 |
| - | - | moves (us-them) | +0.28 | +0.17 | +0.04 | +0.11 | +0.18 | +0.20 | +0.26 |
| - | - | moves (us-them) ~avg | +0.25 | +0.20 | +0.11 | +0.10 | +0.13 | +0.08 | +0.12 |
| - | r950 | traps_expl (us-them) | -0.42 | -0.23 | -0.30 | -0.21 | -0.19 | -0.28 | -0.24 |
| - | r950 | traps_expl (us-them) ~avg | -0.42 | -0.23 | -0.26 | -0.26 | -0.22 | -0.27 | -0.23 |
| - | - | traps_water (us-them) | +0.21 | -0.14 | -0.07 | -0.00 | +0.01 | -0.00 | +0.08 |
| - | - | traps_water (us-them) ~avg | +0.23 | -0.15 | -0.11 | -0.07 | -0.04 | -0.01 | +0.05 |

**Reading it.** Earliest onset is the first place to look: temporal precedence is the one causal hint a
correlation can honestly give. Late-onset metrics are usually the scoreboard rather than the cause -- by then
the winner leads on everything. A high correlation earns a diagnostic game, not a code change.
