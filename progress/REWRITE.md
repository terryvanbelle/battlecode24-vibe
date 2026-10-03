# Decision-layer rewrite: milestone statistics

Protocol: research/REWRITE_EVAL.md. Paired against g_iter1 on the band cells (SEED 515151 + 616161). "Upper" = the 11
band bots rated 2050+; capture diff = our captures minus theirs per game.

| build | stages on | all: wins, net (p) | all: capture delta | upper: wins, net | upper: capture delta | rest: net | rest: capture delta | Elo |
|---|---|---|---|---|---|---|---|---|
| g_iter1 (control) | none | 82/234 | -0.88 | 11/128 | -2.04 | 71/106 | +0.53 | 1811 +- 15 |
| B2 b1z2b (reference) | - | +3 (0.76) | +0.08 +- 0.10 | +1 | +0.12 +- 0.11 | +2 | +0.02 +- 0.17 | 1804 +- 25 |
| B3 b2fs (reference) | - | +1 (1.00) | +0.05 +- 0.10 | +4 | +0.17 +- 0.11 | -3 | -0.09 +- 0.18 | 1789 +- 31 |
| **g1basics** (seeded pairs vs the seeded g_iter1 control) | audit fixes A1 A2 A4 A5 A6 A7 A9 + observed symmetry | 78 -> 111, **+33 (p<0.001)** | **+0.66 +- 0.11** | 13 -> 21, +8 | **+0.34 +- 0.12** | +25 | +1.04 +- 0.17 | pending |
