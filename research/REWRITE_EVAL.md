# Evaluation protocol for the decision-layer rewrite (pre-registered 2026-10-02, owner prompt 112)

Written before any rewrite code exists. Numbers decided here are not moved after a result is seen.

## Control and cells
- Control: g_iter1 (src/bot with every switch off plays as g_iter1; identity verified 2026-10-02).
- Cells: the 20-bot band (`tools/band-20261001.txt`), SEED 515151 and 616161, 120 games each (~117 complete).
  Control runs on these cells: `20261001-011402-scrim-g_iter1` (515151) and `20261001-021054-scrim-g_iter1` (616161);
  census `research/census-g_iter1.csv`.
- Tiers: the band holds 11 of the 12 bots rated 2050+ on our ladder (`tools/upper-tier.txt`); they beat us 83-100%.
  Every result is reported for all cells, the upper tier and the rest.

## Measures (tools/eval-paired.py, paired cell by cell)
- Wins: gained / lost on discordant pairs, net, exact two-sided sign-test p.
- Capture difference per game (our captures minus theirs): paired mean delta +- SE. Against the upper tier wins
  barely move (we win ~9%), so this is the sensitive measure there (SE ~0.11 per 128 games).
- Baseline (g_iter1 on the two seeds): all 82/234 wins, capture diff -0.88; upper 11/128, -2.04; rest 71/106, +0.53.
- Reference, earlier builds vs g_iter1 on the same cells: B2 net +3 (upper capture delta +0.12 +- 0.11); B3 net +1
  (+0.17 +- 0.11); b2rg net -12; b3own net -12 (rest capture delta -0.42 +- 0.17).

## Per stage (each stage is a C.java switch, default off)
- Step 5(a): logged diagnostic games show the stage's mechanism firing (its counter) and its diagnostic signature.
- Step 5(b): delivery mini-block, 24 games, SEED 909090, BASE=g_iter1, the stage's metric with a threshold written in
  the design before the run (`tools/delivery-gate.sh`).

## Per milestone build (the stages switched on so far)
- Band test (2 seeds, ~234 paired games) against the control runs above, reported by tier.
- **Improvement** (pre-registered): (a) all-cell wins net >= +2 SE (sign p < 0.05), or (b) upper-tier capture
  difference delta >= +2 SE with all-cell wins net >= -5 (non-inferior).
- **Confirmation** before replacing g_iter1: two more seeds for the milestone and for g_iter1 (power ~480 paired games);
  the pooled result must still meet (a) or (b); then ladder field blocks give its Elo.
- **Regression guard**: all-cell net <= -2 SE stops the milestone; the stage just added is ablated first.
- Every milestone's numbers go into progress/REWRITE.md (a table: build, stages on, all / upper / rest wins and capture
  deltas, Elo when available) and TRAINING_LOG.md.
