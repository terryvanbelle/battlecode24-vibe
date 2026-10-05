# Shipping criteria: the history since the seeded era (2026-10-02 23:00 UTC on)

Sources: progress/REWRITE.md, TRAINING_LOG.md (2026-10-02 to 2026-10-05), progress/ELO.md and its git history, and
progress/games.csv. I recomputed every band number from the VM census files (`gauntlet/census-<build>[-conf]-seeded.csv`,
copied read-only to `scratchpad/criteria/census/`) with tools/eval-paired.py's own `load`, `pairs` and `sign_p`
(script: `scratchpad/criteria/per_seed.py`, output in `per_seed.out`). The upper tier here is today's
tools/upper-tier.txt (11 bots, 10 of them in the band). Rows written before 2026-10-04 used 12 bots (waffle included), so
some older upper values below differ from REWRITE.md. Section 6 shows why that matters.

## 1. Every arm that reached a band test (paired against its incumbent, seeded cells)

disc = share of pairs whose result differs. t = delta / SE. "Pooled" means 4 seeds where confirmation seeds ran, else 2.

| arm (base) | what it changes | pairs | wins net (gained-lost, sign p) | disc | all: capture delta (t) | upper: delta (t) | rest: net, delta | basics | verdict |
|---|---|---|---|---|---|---|---|---|---|
| **g1basics** (g_iter1) | audit fixes A1-A9 + observed symmetry | 473 | **+62** (79-17, p<0.001) | 20% | **+0.67 +- 0.07 (9.3)** | +0.40 +- 0.09 (4.7) | +46, +0.94 | PASS | **promoted: g_iter2** |
| g2nonav (g_iter2) | ablation: NAV_FIX off | 234 | -1 (21-22, 1.00) | 18% | +0.04 +- 0.10 (0.5) | +0.14 +- 0.12 | -1, -0.05 | 1 overrun | kept NAV_FIX (neutral) |
| g2bc (g_iter2) | REACH_BC 8000 -> 11000 | 234 | 0 (0-0) | 0% | identical | identical | identical | PASS | no effect |
| g2bc2 (g_iter2) | BFS bail-out at 13000 | 234 | +7 (23-16, 0.34) | 17% | +0.02 +- 0.09 (0.2) | -0.09 +- 0.12 | +7, +0.12 | **FAIL stillPost** | not promoted |
| g2fast (g_iter2) | same BFS, less bytecode | 234 | 0 (0-0) | 0% | identical | identical | identical | PASS | folded in (no play change) |
| **g2cr** (g_iter2) | carrier stun + flag relocation (waffle crack) | 473 | **+22** (50-28, **p 0.017**) | 17% | **+0.25 +- 0.07 (3.8)** | +0.16 +- 0.09 (1.7; 2.0 with the 12-bot tier) | +19, +0.35 | PASS | **promoted: g_iter3** on (a) |
| g3escrg2 (g_iter3) | convoy + re-grab (Cyril) | 234 | +6 (20-14, 0.39) | 15% | +0.18 +- 0.08 (2.1) | +0.28 +- 0.12 (**2.5**; 1.7 with the 12-bot tier) | +1, +0.07 | **FAIL k/d 2.13 vs 3.11** | not promoted |
| **g3lost** (g_iter3) | FLAG_LOST (second audit BOT1) | 473 | +9 (28-19, 0.24) | **10%** | **+0.14 +- 0.045 (3.1)** | **+0.28 +- 0.07 (4.2)** | -5, +0.00 | PASS | **promoted: g_iter4** on (b) |
| g4pick (g_iter4) | PICKUP_AFTER_MOVE (BOT5) | 473 | +6 (35-29, 0.53) | 14% | +0.08 +- 0.06 (1.3) | +0.10 +- 0.09 (1.1) | +4, +0.07 | PASS | parked |
| g4bundle (g_iter4) | FILL_STEP + PICKUP_AFTER_MOVE + RELOC_STALL | 234 | +2 (20-18, 0.87) | 16% | +0.08 +- 0.09 (0.8) | -0.13 +- 0.13 (-1.0) | +7, +0.28 | PASS (k/d -1.6 SE) | parked |
| g4econ2 (g_iter4) | crumbs + builders | 234 | +3 (19-16, 0.74) | 15% | -0.08 +- 0.09 (-0.9) | -0.20 +- 0.12 (-1.6) | +4, +0.03 | PASS | not promoted (**missing from REWRITE.md**) |
| g4crumb (g_iter4) | CRUMB_STEP + POST_SETUP_CRUMBS | 240 | +4 (15-11, 0.56) | 11% | +0.08 +- 0.07 (1.0) | -0.03 +- 0.10 (-0.3) | +5, +0.18 | PASS (k/d up) | parked |
| g4gym1 (g_iter4) | g4crumb + DAM_FIRST | 240 | +1 (16-15, 1.00) | 13% | -0.06 +- 0.09 (-0.7) | -0.15 +- 0.13 (-1.1) | +5, +0.03 | PASS | not promoted |
| **g4ship1** (g_iter4) | g4crumb + PICKUP_AFTER_MOVE | 480 | **+12** (44-32, 0.21) | 16% | **+0.14 +- 0.065 (2.2)** | +0.14 +- 0.10 (1.4) | +7, +0.15 | PASS (k/d level) | **not promoted** |

g2nonav, g2bc, g2bc2, g2fast and g4econ2 have band tests in TRAINING_LOG.md but no row in REWRITE.md, although its
table claims to hold every evaluated build. That breaks rule 8 (nothing stale stays).

Of the 11 arms whose play differed from the base, 3 were promoted. Every promotion so far came from the bug-fix and
crack families. None of the four economy or re-grab arms on g_iter4 has passed.

## 2. Seed by seed (all-cell capture delta / upper delta / wins net; seeds 515151, 616161, 717171, 818181)

| arm | seed 1 | seed 2 | seed 3 | seed 4 | all-cell delta > 0 on every seed? |
|---|---|---|---|---|---|
| g1basics | +0.86 / +0.45 / +19 | +0.46 / +0.29 / +14 | +0.62 / +0.50 / +16 | +0.74 / +0.37 / +13 | 4/4 |
| g2cr | +0.37 / +0.40 / +5 | +0.30 / +0.02 / +6 | +0.17 / +0.02 / +6 | +0.18 / +0.20 / +5 | 4/4 (upper was strong only on seed 1) |
| g3lost | +0.16 / +0.29 / +1 | +0.12 / +0.26 / 0 | +0.14 / +0.33 / +3 | +0.14 / +0.23 / +5 | 4/4, upper 4/4 |
| g4pick | +0.11 / +0.17 / +5 | +0.13 / +0.16 / +4 | +0.03 / +0.10 / -2 | +0.06 / -0.03 / -1 | 4/4 (wins 2/4) |
| **g4ship1** | +0.12 / +0.12 / +5 | +0.10 / +0.08 / +4 | +0.17 / +0.17 / 0 | +0.18 / +0.18 / +3 | **4/4, upper 4/4, wins 3/4 positive and 1 zero** |
| g4crumb | +0.13 / +0.08 / +6 | +0.02 / -0.15 / -2 | - | - | 2/2 (upper 1/2; rest +0.18 on both) |
| g3escrg2 | +0.11 / +0.19 / +5 | +0.24 / +0.38 / +1 | - | - | 2/2, upper 2/2 |
| g4bundle | -0.01 / -0.28 / +2 | +0.16 / +0.02 / 0 | - | - | 1/2 (upper negative on both) |
| g4econ2 | -0.15 / -0.36 / 0 | -0.02 / -0.03 / +3 | - | - | 0/2 |
| g4gym1 | +0.04 / +0.02 / +3 | -0.16 / -0.32 / -2 | - | - | 1/2 |
| g2bc2 | +0.06 / +0.00 / +5 | -0.03 / -0.17 / +2 | - | - | 1/2 |

**Consistently positive but rejected:** g4ship1 (the most consistent arm after g1basics: 12 of 12 per-seed capture
readings positive and no seed with negative wins), g4pick, g4crumb, and g3escrg2 (on the old base, vetoed by basics).

## 3. What the promoted builds bought afterwards

Absolute ratings move between fits (the 2026-10-04 MM convergence fix added about +45 to every rating), so the gaps are
what count. Paired target results are filler pairs on shared engine seeds from games.csv (opponent, map, side, seed).

| build | rating at promotion (gap to predecessor; gap to target) | rating now (gap to predecessor; to target) | its target, paired vs predecessor on the same cells | record vs target | next target |
|---|---|---|---|---|---|
| g_iter2 (g1basics) | 1918, rank 13 (+156 over g_iter1; +41 over ColtG5) | 1847, rank 23 (+174; +69) | ColtG5: 280 pairs, **net +82** (102-20) | 203-101 (67%) | waffle 630-834 (43%) |
| g_iter3 (g2cr) | 1977, rank 12 (+57 over g_iter2; +13 over waffle) | 1890, rank 20 (+43; +2) | waffle: 264 pairs, **net +46** (76-30); pre-registered victory read 146-94 = 60.8%, p 0.001 | 163-101 (62%) | Cyril 877-1947 (31%) |
| g_iter4 (g3lost) | 1932, rank 12 (+31 over g_iter3; -100 to Cyril) | 1934, rank 15 (+44; -88) | Cyril: 224 pairs, net +10 (24-14, p 0.14); never cracked | 905-1519 (37%) | Gymhgy 1892-2892 (40%) |

All the paired comparisons of g_iter4 with g_iter3 in games.csv (band plus Cyril): 680 pairs, net +19, p 0.048.

Each incumbent fell in absolute rank after promotion (g_iter3 from rank 12 to 20, g_iter4 from 12 to 15) because the
filler then spent thousands of games on its worst matchup, the next target, and one-dimensional Bradley-Terry cannot
model a matchup that does not follow the ratings. Every gap to the predecessor stayed positive. g_iter4 now rates below
three unpromoted arms (g4ship1 1994 +- 76, g4crumb 1941 +- 27, g4gym1 1940 +- 16), but those ratings come only from
Gymhgy filler games and are not comparable.

## 4. Did a promoted build's gain later fail to show?

None reversed, but two caveats matter:
- **g2cr (g_iter3):** criterion (b) on the first two seeds (upper +0.30, t 2.3) sent it to confirmation. On the
  confirmation seeds the upper tier did not replicate (+0.07 +- 0.13; +0.02 and +0.20 per seed under today's tier). The
  promotion stood on the pooled all-cell wins (criterion (a), p 0.017), and its waffle crack held. The upper-tier gain
  that triggered it was mostly the winner's curse.
- **g3lost (g_iter4):** the upper-tier capture gain replicated on all four seeds, but its wins gain was never
  significant on its own: band +9/473 (p 0.24); Cyril +10/224 (p 0.14); only the combined 680 pairs reach p 0.048. On
  wins, the yardstick that pays at the tournament, the promoted g3lost (+1.9 points, about +13 Elo near 50%) is weaker
  than the rejected g4ship1 (+2.5 points, about +17 Elo).
- g_iter2's ColtG5 victory was declared by the owner (PROMPTS 157) at 58% before the pre-registered 240-game read.
  The data now clear the bar (203-101 = 67% over 304 games; +82/280 paired), so the gain is real.

## 5. g3lost (promoted) against g4ship1 (rejected)

| | g3lost (promoted) | g4ship1 (rejected) |
|---|---|---|
| wins net over ~480 | +9 (p 0.24) | **+12** (p 0.21) |
| all-cell capture delta | +0.140 +- 0.045 (t 3.1) | +0.144 +- 0.065 (t 2.2) |
| upper capture delta | +0.28 +- 0.07 (t 4.2) | +0.14 +- 0.10 (t 1.4) |
| rest | net -5, +0.00 | net +7, +0.15 |
| discordant pairs | 10% (changes play only after a capture) | 16% |
| per-seed capture delta > 0 | 4/4 | 4/4 |

g4ship1 has the same capture effect as g3lost, more wins, and a broader effect, yet it fails both routes:
- **(a)** needs about +20 net at 76-90 discordant pairs (+4.2 win-rate points).
- **(b)** looks only at the upper half, where a broad change carries the higher SE of its higher discordance.

The protocol rewards narrow, low-variance changes aimed at the upper tier. It has no route for an all-cell capture
delta >= 2 SE, which g4ship1 meets (t 2.2) and which g3lost also met (t 3.1).

## 6. Criterion (b) depends on who is in the upper tier

Dropping waffle from the tier (12 of the ~240 band games per two seeds) moves:
- g3escrg2's upper t from 1.7 (not met) to **2.5** (met), on the same games;
- g2cr's pooled upper t from 2.0 to 1.7.

A half-sample test near the 2 SE line flips on tier membership, and the upper slice has never contained uravt (absent
from the band; PROMPTS 184).

## 7. Stacking the consistently positive rejected arms

Implied component effects, from arms paired against the same control cells (all-cell / upper capture delta; wins net
per 480):

| component | source | all | upper | wins net / 480 |
|---|---|---|---|---|
| PICKUP_AFTER_MOVE | g4pick, 4 seeds | +0.08 | +0.10 | +6 |
| crumbs (CRUMB_STEP + POST_SETUP_CRUMBS) | g4crumb, 2 seeds | +0.08 | -0.03 | +8 |
| convoy + re-grab | g3escrg2, 2 seeds, g_iter3 base | +0.18 | +0.28 | +12 |
| dam (DAM_FIRST) | g4gym1 - g4crumb | -0.13 | -0.12 | -6 |
| builders | g4econ2 - g4crumb | -0.16 | -0.17 | -2 |
| FILL_STEP + RELOC_STALL | g4bundle - g4pick (2 seeds) | -0.04 | -0.29 | -14 |

**The additivity check already exists.** g4ship1 = crumbs + pick:
- Predicted from four-seed pick plus two-seed crumbs: +0.157 all, +0.064 upper, about +14 net.
- Measured: +0.144 all, +0.138 upper, +12 net (92% of the predicted all-cell sum).
- On the identical first two seeds the stack gave +0.11 against a +0.195 sum (56%).

So the effects are roughly additive to mildly sub-additive. Noise (SE of a sum about 0.1) cannot separate the two.

**The full stack (crumbs + pick + convoy), if the effects add:**
- All-cell about +0.33 +- 0.13, upper about +0.36 +- 0.17, wins about +26 per 480 (+5.5 points, about +38 Elo).
- That clears both (a) (needs about +20) and (b) (3.5 SE).
- Corrected for the winner's curse seen in two-seed estimates (first-pair to confirmation shrinkage across g2cr, g3lost,
  g4pick and g4ship1 averages about 25%) and for the 56-92% additivity ratio: **+0.20 to +0.28 all-cell, +15 to +22
  wins per 480 (+3 to +4.5 points, +20 to +30 Elo)**. That is about one promotion's worth (g_iter3 +43, g_iter4 +44 on
  the ladder).

Caveats:
- The convoy was measured on g_iter3, before FLAG_LOST.
- Its re-grab mechanism overlaps PICKUP_AFTER_MOVE: both multiply re-grabs, and against Gymhgy and the band "more
  re-grabs, not more captures" recurs.
- Its kill/death cost (2.13 vs 3.11) would fail the basics battery unless a crack read passes (rule 15, PROMPTS 168).
- On today's evidence, the part that is actually available is g4ship1 itself.

## 8. Target-filler evidence for the rejected arms (games.csv, paired vs the incumbent on the same cells)

| arm | target | pairs | net (gained-lost, p) | logged close |
|---|---|---|---|---|
| g4crumb | Gymhgy | **640** | **+30 (103-73, p 0.029)** | log closed it at 560 pairs, +20 (+1.6 SE); the last 80 pairs (+10) were never logged |
| g4ship1 | Gymhgy | 80 | +10 (18-8, p 0.076) | running |
| g4gym1 | Gymhgy | 1880 | +12 (362-350, p 0.68) | it peaked at +31 after 400 pairs, then regressed: a warning against early reads |
| g3escrg2 | Cyril | 280 | +14 (48-34, p 0.15) | log closed it at 240, +8 |
| g4econ2 | Cyril | 160 | +2 | log: 120 pairs, +1 |
| g4pick | Cyril | 120 | -2 | - |

## 9. What this history says about strictness

- **False-positive rate is about 3%.** The rule passes a null arm about 3% of the time (simulation; each route is
  roughly a one-sided 2.5% test).
- **Power is low for the effect sizes we actually produce.** A g4ship1-sized true effect (+2.5 win points, +0.14
  capture) passes (a) or (b) only about 45% of the time at 480 pairs. A pick-sized effect passes about 22%. Adding an
  all-cell capture route (>= 2 SE with wins net >= -5) would raise g4ship1-like power to about 75% and the null rate to
  at most about 5%. These are rough figures from a simulation that treats the three tests as independent; they are
  positively correlated, so the real gain in power and in false positives is smaller.
- **Rejected arms are biased to the positive side.** On g_iter4, the band-tested arms' pooled all-cell deltas are
  +0.08, +0.08, -0.08, +0.08, -0.06, +0.14 (mean +0.04): four of six are positive. The "rest" half is positive in all
  six, and the upper tier is negative in four. Economy and re-grab changes help against the weaker half and cost
  against the top, and only the top half counts under (b).
- **Confirmation shrinks first-pair estimates by about 25%** (g2cr upper 0.30 -> 0.07). The two-stage design guards
  against that, and it worked as intended for g2cr.
