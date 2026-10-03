# Decision-layer rewrite: milestone statistics

Protocol: research/REWRITE_EVAL.md. Paired against g_iter1 on the band cells (SEED 515151 + 616161); from g2cr on, against the incumbent g_iter2. "Upper" = the 11
band bots rated 2050+; capture diff = our captures minus theirs per game.

| build | stages on | all: wins, net (p) | all: capture delta | upper: wins, net | upper: capture delta | rest: net | rest: capture delta | Elo |
|---|---|---|---|---|---|---|---|---|
| g_iter1 (control) | none | 82/234 | -0.88 | 11/128 | -2.04 | 71/106 | +0.53 | 1811 +- 15 |
| B2 b1z2b (reference) | - | +3 (0.76) | +0.08 +- 0.10 | +1 | +0.12 +- 0.11 | +2 | +0.02 +- 0.17 | 1804 +- 25 |
| B3 b2fs (reference) | - | +1 (1.00) | +0.05 +- 0.10 | +4 | +0.17 +- 0.11 | -3 | -0.09 +- 0.18 | 1789 +- 31 |
| **g1basics** (seeded pairs vs the seeded g_iter1 control) | audit fixes A1 A2 A4 A5 A6 A7 A9 + observed symmetry | 78 -> 111, **+33 (p<0.001)** | **+0.66 +- 0.11** | 13 -> 21, +8 | **+0.34 +- 0.12** | +25 | +1.04 +- 0.17 | 1918 +- 35 (rank 13) |
| g1basics confirmation (seeds 717171 + 818181) | same | 78 -> 107, **+29 (p<0.001)** | **+0.68 +- 0.10** | 12 -> 21, +9 (p 0.049) | **+0.46 +- 0.12** | +20 | +0.95 +- 0.16 | promoted: **g_iter2** |
| **g2cr** (seeded pairs vs g_iter2, waffle crack) | carrier stun + flag relocation (C.CARRIER_STUN, RELOCATE_FLAGS + RELOC_V2) | 111 -> 122, +11 (p 0.11) | **+0.33 +- 0.10** | 21 -> 25, +4 | **+0.30 +- 0.13** | +7 | +0.37 +- 0.14 | criterion (b) met |
| g2cr confirmation (seeds 717171 + 818181) | same | 107 -> 118, +11 (p 0.11) | +0.17 +- 0.09 | 21 -> 24, +3 | +0.07 +- 0.13 | +8 | +0.30 +- 0.13 | - |
| **g2cr pooled (4 seeds, 473 pairs)** | same | 218 -> 240, **+22 (p 0.017)** | **+0.25 +- 0.07** | 42 -> 49, +7 | +0.18 +- 0.09 | +15 | +0.33 +- 0.10 | promoted: **g_iter3**, 1977 +- 33 (rank 12) |
| g3escrg2 (vs g_iter3, Cyril crack) | g_iter3 + convoy (ESCORT_CARRIER/BEHIND/TIGHT, ESCORT_FAR_R2 225, REGRAB) | 122 -> 128, +6 (0.39) | +0.18 +- 0.08 | 25 -> 28, +3 | +0.20 +- 0.12 | +3 | +0.14 +- 0.11 | basics FAIL (k/d 2.13 vs 3.11) |
