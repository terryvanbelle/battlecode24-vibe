# Evaluation protocol for the decision-layer rewrite (pre-registered 2026-10-02, owner prompt 112)

Written before any rewrite code exists. Numbers decided here are not moved after a result is seen.

## Control and cells
- Control: g_iter1 (src/bot with every switch off plays as g_iter1; identity verified 2026-10-02).
- Cells: the 20-bot band (`tools/band-20261001.txt`), SEED 515151 and 616161, 120 games each (~117 complete).
  Control runs on these cells: `20261001-011402-scrim-g_iter1` (515151) and `20261001-021054-scrim-g_iter1` (616161);
  census `research/census-g_iter1.csv`.
- Tiers: the band holds the bots rated 2050+ on our ladder (`tools/upper-tier.txt`: 12 on 2026-10-02; 11 since 2026-10-04,
  re-derived on the converged fit, audit MEAS11, which dropped waffle); they beat us 83-100%.
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
- **Shipping rule since 2026-10-05** (owner PROMPTS 186-188; research/criteria-review-2026-10-05/verdict.md). Statistics
  from `tools/eval-paired.py ... --look N`: t_all = all-cell capture-difference delta / SE; t_up = the same on the upper
  tier (tier list frozen when the arm is registered); net = all-cell wins gained - lost.
  **Ship test:** (t_all >= 2.3 or t_up >= 2.6) and net >= 0.

  | look | pairs (seeds) | ship if | stop (park) if | otherwise |
  |---|---|---|---|---|
  | 1 | 240 (seeds 1-2: 515151, 616161) | t_all >= 3.0 and net >= 0 | t_all < 0.5 and t_up < 0.8 | run seeds 3-4 |
  | 2 | 480 (+ 717171, 818181, pooled) | ship test | t_all < 1.0 and t_up < 1.3 | run seeds 5-6 |
  | 3 | 720 (+ 727272, 838383, pooled) | ship test | everything else | - |

  The incumbent's control on seeds 5-6 is played once per incumbent and reused by every arm. At each promotion the new
  incumbent gets a fresh control on fresh seeds (with the band refresh, PROMPTS 185). Simulated: a null arm ships 2.0%,
  a harmful one <= 0.2%, a g4ship1-sized gain (+2.5 win points, +0.14 capture) 63-65% (the old rule 17% as written).
- **Old rule (superseded 2026-10-05):** (a) all-cell wins net >= +2 SE (sign p < 0.05), or (b) upper-tier capture
  difference delta >= +2 SE with all-cell wins net >= -5 (non-inferior); confirmation on two more seeds, pooled.
- **Regression guard**: all-cell net <= -2 SE stops the milestone; the stage just added is ablated first.
- Every milestone's numbers go into progress/REWRITE.md (a table: build, stages on, all / upper / rest wins and capture
  deltas, Elo when available) and TRAINING_LOG.md.

## Curriculum (owner prompt 113: in a tournament, enemy systems arrive gradually; in practice they arrive mature)
Added 2026-10-02 as process, not as decision bars (the bars above are unchanged).
- **Rung slice** (descriptive): `tools/eval-paired.py ... --rung tools/next-rung.txt` reports the bots just above us
  (1880-2110: ColtG5, kyleezz, winkelmantanner, CyrilSharma, hsmalladi). g_iter1 wins ~28% there (17/60 on the two seeds),
  so those cells flip often: they show progress the top bots hide.
- **Archetype sparring partners**: from what we SEE in field games (never their code), build one-component opponents
  (e.g. a convoy raid with re-grab chains, an early raid at r220-270, a ground-taking push) at a weak, early strength,
  and develop each rewrite stage against the matching archetype before the band. They replace self-play vs g_iter1 as
  the step 5(a) opponent where self-play has misled (g1drift: ground x2 vs g_iter1, none vs the band).
- **Order of opponents for a stage**: its archetype (does the counter work at all?) -> the rung (does it carry over to
  real bots that do it imperfectly?) -> the band (the decision).
