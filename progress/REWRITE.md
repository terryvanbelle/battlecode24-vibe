# Band paired tests: milestone statistics

Started for the decision-layer rewrite (paused 2026-10-02); the table now records every band paired test. Protocol:
research/REWRITE_EVAL.md. The first rows are paired against g_iter1 on the band cells (SEED 515151 + 616161); from g2cr on,
each row is paired against the incumbent of its time (g_iter2 to g_iter7), on the seeds the row names if any, on
band-20261005 from g5climb2 on. "Upper" = tools/upper-tier.txt at the time: the 11 band bots rated 2050+ through
g4bundle (re-derived on 2026-10-04); the 10 bots above g_iter5 for the g5climb2 and g6heal rows; the 5 above g_iter7 for
the g7 rows. Capture diff = our captures minus theirs per game.

| build | stages on | all: wins, net (p) | all: capture delta | upper: wins, net | upper: capture delta | rest: net | rest: capture delta | Elo |
|---|---|---|---|---|---|---|---|---|
| g_iter1 (control) | none | 82/234 | -0.88 | 11/128 | -2.04 | 71/106 | +0.53 | 1811 +- 15 |
| B2 b1z2b (reference) | - | +3 (0.76) | +0.08 +- 0.10 | +1 | +0.12 +- 0.11 | +2 | +0.02 +- 0.17 | 1804 +- 25 |
| B3 b2fs (reference) | - | +1 (1.00) | +0.05 +- 0.10 | +4 | +0.17 +- 0.11 | -3 | -0.09 +- 0.18 | 1789 +- 31 |
| **g1basics** (seeded pairs vs the seeded g_iter1 control) | audit fixes A1 A2 A4 A5 A6 A7 A9 + observed symmetry | 78 -> 111, **+33 (p<0.001)** | **+0.66 +- 0.11** | 13 -> 21, +8 | **+0.34 +- 0.12** | +25 | +1.04 +- 0.17 | 1918 +- 35 (rank 13) |
| g1basics confirmation (seeds 717171 + 818181) | same | 78 -> 107, **+29 (p<0.001)** | **+0.68 +- 0.10** | 12 -> 21, +9 (p 0.049) | **+0.46 +- 0.12** | +20 | +0.95 +- 0.16 | promoted: **g_iter2** |
| g2nonav (vs g_iter2; ablation of NAV_FIX) | g_iter2 with C.NAV_FIX off | net -1 (21-22) | +0.04 +- 0.09 | +2 | +0.17 +- 0.12 | - | - | neutral: NAV_FIX kept; 1 overrun in the block |
| g2bc (vs g_iter2) | g_iter2 + REACH_BC 11000 | identical (0 of 234 discordant) | 0 | 0 | 0 | 0 | 0 | no effect |
| g2bc2 (vs g_iter2) | g_iter2 + BFS bail-out at 13000 | net +7 (23-16, p 0.34) | +0.02 +- 0.09 | - | - | - | - | basics FAIL (stillPost): not promoted |
| g2fast (vs g_iter2) | g_iter2 + C.REACH_FAST | identical (0 of 234 discordant) | 0 | 0 | 0 | 0 | 0 | same decisions, cheaper bytecode: kept in later builds |
| **g2cr** (seeded pairs vs g_iter2, waffle crack) | carrier stun + flag relocation (C.CARRIER_STUN, RELOCATE_FLAGS + RELOC_V2) | 111 -> 122, +11 (p 0.11) | **+0.33 +- 0.10** | 21 -> 25, +4 | **+0.30 +- 0.13** | +7 | +0.37 +- 0.14 | criterion (b) met |
| g2cr confirmation (seeds 717171 + 818181) | same | 107 -> 118, +11 (p 0.11) | +0.17 +- 0.09 | 21 -> 24, +3 | +0.07 +- 0.13 | +8 | +0.30 +- 0.13 | - |
| **g2cr pooled (4 seeds, 473 pairs)** | same | 218 -> 240, **+22 (p 0.017)** | **+0.25 +- 0.07** | 42 -> 49, +7 | +0.18 +- 0.09 | +15 | +0.33 +- 0.10 | promoted: **g_iter3**, 1977 +- 33 (rank 12) |
| g3escrg2 (vs g_iter3, Cyril crack) | g_iter3 + convoy (ESCORT_CARRIER/BEHIND/TIGHT, ESCORT_FAR_R2 225, REGRAB) | 122 -> 128, +6 (0.39) | +0.18 +- 0.08 | 25 -> 28, +3 | +0.20 +- 0.12 | +3 | +0.14 +- 0.11 | basics FAIL (k/d 2.13 vs 3.11) |
| **g3lost** (vs g_iter3; second-audit fix BOT1) | g_iter3 + C.FLAG_LOST (captured own flags recognised) | 122 -> 123, +1 (1.00) | **+0.14 +- 0.06** | 25 -> 27, +2 | **+0.22 +- 0.09** | -1 | +0.04 +- 0.09 | criterion (b) met; basics PASS |
| g3lost confirmation (seeds 717171 + 818181) | same | 118 -> 126, +8 (0.17) | +0.14 +- 0.06 | 24 -> 33, +9 (p 0.035) | +0.27 +- 0.09 | -1 | -0.01 +- 0.08 | - |
| **g3lost pooled (4 seeds, 473 pairs)** | same | 240 -> 249, +9 (0.24) | **+0.14 +- 0.05** | 49 -> 60, +11 | **+0.24 +- 0.06** | -2 | +0.01 +- 0.06 | promoted: **g_iter4**, 1932 +- 39 (rank 12) |
| g4pick (vs g_iter4; second-audit BOT5) | g_iter4 + C.PICKUP_AFTER_MOVE | 123 -> 132, +9 (0.12) | +0.12 +- 0.09 | 27 -> 33, +6 | +0.16 +- 0.12 | +3 | +0.08 +- 0.13 | no criterion; basics PASS |
| g4pick pooled (4 seeds, 473 pairs) | same | 249 -> 255, +6 (0.53) | +0.08 +- 0.06 | 60 -> 65, +5 | +0.11 +- 0.09 | +1 | +0.05 +- 0.09 | not met: parked |
| g4crumb (vs g_iter4; Gymhgy study) | g_iter4 + C.CRUMB_STEP + C.POST_SETUP_CRUMBS | 126 -> 130, +4 (0.56) | +0.07 +- 0.07 | 24 -> 23, -1 | -0.03 +- 0.10 | +5 | +0.18 +- 0.10 | no criterion; basics PASS |
| g4econ2 (vs g_iter4; Cyril stun economy) | g4crumb-like crumbs + builders added (C.BUILDERS, SEEN_LEVEL 99) | 123 -> 126, +3 (0.74) | -0.08 +- 0.09 | 23 -> 22, -1 | -0.20 +- 0.12 | +4 | +0.03 +- 0.12 | no criterion; basics PASS (kills -17%) |
| g4gym1 (vs g_iter4) | g4crumb + C.DAM_FIRST | 126 -> 127, +1 (1.00) | -0.06 +- 0.09 | 24 -> 20, -4 | -0.15 +- 0.13 | +5 | +0.03 +- 0.12 | no criterion; basics PASS |
| **g4ship1** (vs g_iter4; owner PROMPTS 180 stack) | g4crumb + C.PICKUP_AFTER_MOVE | 126 -> 135, +9 (0.19) | +0.11 +- 0.09 | 24 -> 28, +4 | +0.10 +- 0.14 | +5 | +0.12 +- 0.12 | no criterion yet; basics PASS (k/d level) |
| g4ship1 confirmation (seeds 717171 + 818181) | same | 127 -> 130, +3 (0.75) | +0.17 +- 0.09 | 26 -> 27, +1 | +0.17 +- 0.14 | +2 | +0.17 +- 0.12 | - |
| **g4ship1 pooled (4 seeds, 480 pairs)** | same | 253 -> 265, +12 (0.21) | **+0.14 +- 0.06** | 50 -> 55, +5 | +0.14 +- 0.10 | +7 | +0.15 +- 0.08 | not met under the old rule; look 3 under the new rule (PROMPTS 188) |
| g4ship1 look 3 (seeds 727272 + 838383, vs a fresh g_iter4 control) | same | 130 -> 140, +10 (0.10) | +0.23 +- 0.08 | 22 -> 30, +8 | +0.21 +- 0.13 | +2 | +0.24 +- 0.10 | - |
| **g4ship1 pooled (6 seeds, 720 pairs)** | same | 382 -> 405, **+23 (0.033)** | **+0.17 +- 0.05 (t 3.25)** | 72 -> 85, +13 | +0.16 +- 0.08 (t 1.98) | +10 | +0.18 +- 0.07 | **SHIP: promoted g_iter5**, 1983 +- 19 (rank 11) |
| g4bundle (vs g_iter4; FILL_STEP + PICKUP_AFTER_MOVE + RELOC_STALL_MOVES) | second-audit correctness fixes combined | 123 -> 125, +2 (0.87) | +0.08 +- 0.09 | 23 -> 18, -5 (upper tier = 11 bots since 2026-10-04) | -0.13 +- 0.13 | +7 | +0.28 +- 0.13 | no criterion; basics PASS (k/d -1.6 SE); parked |
| **g5climb2** look 1 (vs g_iter5 on band-20261005, seeds 525252 + 626262) | C.RELOC_CLIMB (audit BOT3(a): climb to the farthest visible tile, no fixed spot) | 124 -> 134, +10 (0.21) | +0.22 +- 0.10 (t 2.20) | 20 -> 29, +9 | +0.29 +- 0.16 (t 1.86) | +1 | +0.14 +- 0.12 | look 1: continue; basics PASS (k/d 2.85 vs 2.19) |
| g5climb2 look 2 (seeds 737373 + 848484) | same | 123 -> 133, +10 (0.14) | +0.24 +- 0.09 (t 2.7) | 19 -> 31, +12 | +0.36 +- 0.12 (t 3.1) | -2 | +0.12 +- 0.13 | basics PASS |
| **g5climb2 pooled (4 seeds, 480 pairs)** | same | 247 -> 267, **+20 (0.045)** | **+0.23 +- 0.07 (t 3.44)** | 39 -> 60, +21 | **+0.33 +- 0.10 (t 3.34)** | -1 | +0.13 +- 0.09 | **SHIP (look 2): promoted g_iter6**, 1977 +- 40 (rank 11) |
| **g6heal** look 1 (vs g_iter6 on band-20261005, seeds 282828 + 393939) | C.HEAL_HOLD (no heal with an enemy within dist2 10 unless the target carries a flag; TACTICS T16) | 136 -> 164, **+28 (<0.001)** | **+0.57 +- 0.09 (t 6.46)** | 31 -> 50, +19 | **+0.72 +- 0.14 (t 5.31)** | +9 | +0.42 +- 0.11 (t 3.8) | **SHIP (look 1): promoted g_iter7**; basics PASS (k/d 2.18 vs 2.59, -0.9 SE; stillPost -2.9) |
| g7ehp look 1 (vs g_iter7 on band-20261005, seeds 313131 + 424242; upper tier = 5 bots) | C.ENGAGE_HP 700 (no lethal step-in below 700 HP unless the strike kills) | 177 -> 179, +2 (0.86) | -0.10 +- 0.09 (t -1.12) | 20 -> 20, +0 | -0.08 +- 0.17 (t -0.48) | +2 | -0.10 +- 0.10 | STOP (park); basics: stillPost +2.3 (FAIL by design: held robots wait), k/d 2.60 vs 2.12 |
| **g7kite** look 1 (vs g_iter7, seeds 313131 + 424242; upper = 5 bots) | C.KITE_REACH_W 300 (leave enemy reach below 700 HP when not ready or hurt) | 177 -> 183, +6 (0.36) | +0.17 +- 0.08 (t 2.03) | 20 -> 19, -1 | +0.12 +- 0.17 (t 0.67) | +7 | +0.18 +- 0.09 (t 2.0) | look 1: continue; basics PASS |
| g7kite look 2 (seeds 535353 + 646464) | same | 171 -> 168, -3 | -0.13 +- 0.09 (t -1.5) | 16 -> 17, +1 | -0.02 +- 0.20 | -4 | -0.17 +- 0.09 | pooled 480: t_all 0.32, t_up 0.38, net +3 -> STOP (park) |
