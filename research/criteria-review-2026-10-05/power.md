# Power of the promotion criteria (REWRITE_EVAL "Per milestone build")

2026-10-05. Question: are the shipping criteria too strict? Method: Monte Carlo of the full procedure, resampling the
per-pair outcomes of the 19 seeded band runs (census-*-seeded.csv on the VM, read-only copies). No games were played and
nothing in the repo was edited.

Rule simulated, computed exactly as tools/eval-paired.py does: (a) all-cell wins net > 0 with exact two-sided sign
p < 0.05, or (b) upper-tier capture delta t >= 2 with all-cell net >= -5. Stage 1 uses 240 pairs; confirmation pools 480
pairs and must still meet (a) or (b). The basics battery is assumed to pass; it can only lower the promotion rates below.

## Answer

**Yes: the criteria are too strict for the size of effect this project produces.**

- **False promotions are rare.** A null arm is promoted 1.3% of the time under the written two-stage rule, and 3.8% from
  a single look at 480 pairs. A -2-point arm (wins and captures both worse) is promoted 0.04%. Neither the OR of (a) and
  (b) nor the second stage pushes the error above 5%. The exact sign test is conservative (1.9% at the null), and
  requiring stage 1 *and* the pooled result to pass cuts the error rate to a third.
- **Real gains are usually missed.** Real arms move capture by about 0.05 per win point. For such an arm, the written
  rule's promotion rate is:

  | Win effect | Capture | Elo | Promoted (written rule) |
  |---|---|---|---|
  | +1 pt | +0.05 | ~+13 | 4% |
  | +2 pt | +0.10 | ~+26 | 12% |
  | +3 pt | +0.15 | ~+39 | 25% |
  | +5 pt | +0.25 | ~+65 | 61% |

  The written rule reaches 50% only at about +4.5 points (about +58 Elo) and 80% only at about +6.5 points.
- **The project's arms sit right where power is lowest.** The band-tested arms other than g1basics average +2.0 win
  points and +0.10 capture. Their spread across arms is no larger than sampling noise. At that size the rule promotes
  12% (written), 21% (as practised) or 28% (one look at 480).
- **g4ship1's miss is the expected outcome.** Its four-seed estimate (+2.5 points, +0.14 capture) matches the
  "+2.5 / +0.125" row below. An arm of that true size would be promoted 17% written, 30% as practised and 38% from one
  look. So a real arm of g4ship1's apparent size fails about two times in three.

## Model and calibration

- **Pairs.** pairs.py pairs each arm's census with its control's census exactly as eval-paired.py does. It reproduces
  every milestone row; for example, g4ship1 stage 1 gives net +9, capture +0.113 +- 0.093, upper +0.100 +- 0.142.
- **Independence.** The four seeds draw different cells: g4ship1's four runs share only 1-5 of 120 (opponent, map, side)
  cells. Pairs are therefore modelled as independent, with exactly half in the upper tier (120 per stage).
- **Per-pair outcome.** Each pair is a gain, a loss or concordant. A gain's capture delta is drawn from the empirical
  "flip" distribution: mean 2.26 in the upper tier, 2.73 in the rest. A loss is drawn from minus that distribution. A
  concordant pair is drawn from the symmetrised empirical concordant distribution (sd 0.92 upper, 0.81 rest; 53-60%
  zeros). The capture effect on concordant pairs is a +-1 shift with the matching probability.
- **Pools.** The pools come from all band arms except g1basics (a large bundle of audit fixes) and g2bc/g2fast (games
  identical to the control).
- **Discordance.** The baseline is 0.18 upper and 0.14 rest (0.16 overall). Observed across arms: 0.16 / 0.13 pooled;
  g4ship1 0.20 / 0.12. The sensitivity runs use 0.11 and 0.21 overall.
- **Validation.** Simulated SEs, all cells: 0.088 at 240 pairs and 0.062 at 480 (observed about 0.09 and 0.06). Upper
  tier: 0.127 and 0.090 (observed 0.12-0.14 and 0.09-0.10).
- **How win and capture effects couple.** Real arms' capture gain is about half from flipped games (about 2.5 captures
  per flip) and half from games whose outcome did not change. The ratio is roughly constant: g2cr 0.054 capture per
  point, g4ship1 0.056, g4pick 0.065, g3lost 0.074. A "realistic arm" here therefore means w points of wins plus
  0.05·w of capture, spread evenly across tiers. Pure-win and pure-capture arms are reported separately.
- **Procedures.**
  - **Written:** stage 1 meets (a) or (b), then the pooled 480 meets (a) or (b).
  - **Practice:** stage 1 is also extended when it looks promising, operationalised here as net >= +5 and all-cell
    capture t >= 1. g4pick and g4ship1 were extended at that level; g4crumb (+4) and g4bundle were not.
  - **One-shot:** the pooled 480 only.
- **Elo scale.** g_iter4's per-opponent band win rates give a mean p(1-p) of 0.126. One band win point is therefore
  about 13 Elo. The ladder's per-build SE is 33-39 Elo, so the ladder cannot verify a +2-3 point arm either.

## Results

### 1. Realistic arms (win and capture together, the requested +1/+2/+3/+5 points and +0.05/+0.10/+0.15 capture)

| true win effect | capture (all, per pair) | ~Elo | written | practice | one-shot 480 | pooled (a) | pooled (b) |
|---|---|---|---|---|---|---|---|
| -2 pt | -0.100 | -26 | 0.04% | 0.1% | 0.1% | 0.1% | 0.1% |
| -1 pt | -0.050 | -13 | 0.3% | 0.4% | 0.9% | 0.4% | 0.5% |
| 0 (null) | 0 | 0 | **1.3%** | 2.1% | 3.8% | 1.9% | 2.2% |
| +1 pt | +0.050 | +13 | **4.4%** | 7.8% | 12% | 6.4% | 7.5% |
| +2 pt | +0.100 | +26 | **12%** | 21% | 28% | 16% | 19% |
| +2.5 pt | +0.125 | +32 | 17% | 30% | 38% | 24% | 27% |
| +3 pt | +0.150 | +39 | **25%** | 41% | 50% | 33% | 37% |
| +4 pt | +0.200 | +52 | 42% | 64% | 73% | 55% | 58% |
| +5 pt | +0.250 | +65 | **61%** | 83% | 89% | 75% | 77% |
| +7 pt | +0.350 | +91 | 88% | 98% | 99% | 97% | 97% |

Replications: 200k per row for the null and harmful rows, 100k otherwise. Monte Carlo SE is at most 0.2 points.

The stage-1 gate is the largest single cost. At +3 points it halves the promotion rate (25% written vs 50% one-shot).
The informal "extend if promising" practice recovers most of that loss (41%).

### 2. Pure-win and pure-capture arms

| arm | win | capture | written | practice | one-shot | pooled (a) | pooled (b) |
|---|---|---|---|---|---|---|---|
| pure win | +1 pt | +0.025 (flips only) | 3.3% | 5.6% | 9.0% | 6.2% | 4.1% |
| pure win | +2 pt | +0.050 (flips only) | 7.6% | 13% | 20% | 16% | 6.7% |
| pure win | +3 pt | +0.075 (flips only) | 16% | 26% | 37% | 34% | 11% |
| pure win | +5 pt | +0.125 (flips only) | 44% | 61% | 77% | 76% | 23% |
| pure capture | 0 | +0.05 | 3.2% | 5.1% | 8.5% | 1.9% | 7.3% |
| pure capture | 0 | +0.10 | 7.3% | 11% | 18% | 1.9% | 17% |
| pure capture | 0 | +0.15 | 15% | 19% | 32% | 1.8% | 32% |
| pure capture | 0 | +0.20 | 25% | 30% | 48% | 2.0% | 48% |
| pure capture | 0 | +0.25 | 36% | 40% | 61% | 1.9% | 61% |

Criterion (a) alone needs about +4 points to reach 50% at 480 pairs. With roughly 77 discordant pairs it needs net
>= +19; g4ship1 had +12. Criterion (b) sees only the upper half of the data, so the effective sample is 240 pairs.

### 3. Grid: written rule (one-shot in brackets), win effect x total all-cell capture effect

| win \ capture | +0.00 | +0.05 | +0.10 | +0.15 | +0.25 |
|---|---|---|---|---|---|
| -2 pt | 0.7% (2.0%) | 1.9% (5.3%) | 4.7% (11%) | 8.8% (19%) | 18% (29%) |
| 0 pt | 1.3% (3.8%) | 3.1% (8.5%) | 7.3% (18%) | 15% (33%) | 36% (61%) |
| +1 pt | 2.7% (7.8%) | 4.5% (12%) | 8.8% (21%) | 17% (37%) | 41% (70%) |
| +2 pt | 6.3% (17%) | 7.8% (20%) | 12% (28%) | 20% (41%) | 45% (75%) |
| +3 pt | 14% (34%) | 15% (35%) | 18% (41%) | 25% (50%) | 48% (79%) |
| +5 pt | 41% (76%) | 42% (76%) | 44% (77%) | 47% (79%) | 61% (89%) |

60k replications per cell.

### 4. False promotion: null, harmful and "trade" arms (current rule)

| arm | written | practice | one-shot |
|---|---|---|---|
| null (0, 0) | 1.3% | 2.1% | 3.8% |
| -1 pt, capture -0.05 | 0.3% | 0.4% | 0.9% |
| -2 pt, capture -0.10 (realistic harm) | 0.04% | 0.1% | 0.1% |
| -2 pt, capture -0.05 (flips only) | 0.2% | 0.3% | 0.6% |
| -2 pt, capture 0 | 0.7% | 0.9% | 2.0% |
| -2 pt, capture +0.05 | 2.0% | 2.5% | 5.3% |
| -2 pt, capture +0.10 | 4.7% | 5.6% | 11% |
| -2 pt, capture +0.15 | 8.7% | 9.7% | 18% |
| -1 pt, capture +0.10 | 6.1% | 8.0% | 15% |
| -1 pt, capture +0.15 | 12% | 15% | 27% |

200k replications per row.

The only real false-promotion risk is an arm that trades wins for captures. Criterion (b)'s non-inferiority margin is a
fixed net >= -5: that is -2.1 points at 240 pairs and -1.0 at 480. A -2-point arm still clears it 32% of the time at 480
pairs and 55% at 240. No observed arm sits in that quadrant: the closest, g2nonav, was -0.4 points and +0.04 capture.

### 5. Where the capture gain lands (same all-cell mean)

| arm | written | practice | one-shot | pooled (b) |
|---|---|---|---|---|
| +2 pt / +0.10 uniform | 12% | 21% | 28% | 19% |
| +2 pt / +0.10, all in upper (upper +0.20, rest 0) | 31% | 40% | 59% | 56% |
| +2 pt / +0.10, all in rest (upper 0, rest +0.20) | 6.3% | 14% | 17% | 2.3% |
| +3 pt / +0.15 uniform | 25% | 41% | 50% | 37% |
| +3 pt / +0.15, all in upper | 62% | 71% | 89% | 88% |
| +3 pt / +0.15, all in rest | 14% | 30% | 34% | 2.3% |

Criterion (b) works well when the gain is concentrated in the upper tier: g3lost was promoted that way, with upper +0.24
and rest +0.01. It is blind to gains in the rest tier: g4crumb had rest +0.18 and upper -0.03.

### 6. Sensitivity to the discordance rate (written / one-shot)

| arm | q = 0.11 | q = 0.16 (base) | q = 0.21 |
|---|---|---|---|
| null | 1.2% / 3.7% | 1.3% / 3.8% | 1.3% / 3.9% |
| +1 pt | 5.3% / 14% | 4.4% / 12% | 4.1% / 11% |
| +2 pt | 15% / 35% | 12% / 28% | 10% / 24% |
| +3 pt | 32% / 62% | 25% / 50% | 21% / 43% |
| +5 pt | 74% / 96% | 61% / 89% | 52% / 82% |

The null rate does not depend on q. Power falls as q rises: more discordant pairs means more noise around the same net.
Arms that change play only after an event have lower q and more identical games (g3lost, g4crumb: q about 0.10), which
helps them.

### 7. Pairs needed (current rule, single look at N pooled pairs, realistic arms)

VM time assumes about 13.5 minutes per 120 arm games, measured from g4ship1's run timestamps. Beyond 480 pairs the
incumbent's control also needs new seeds, but only once per incumbent.

| N pairs | arm VM time | null | +1 pt | +2 pt | +3 pt | null, rule + (c) | +2 pt, rule + (c) | +3 pt, rule + (c) |
|---|---|---|---|---|---|---|---|---|
| 480 | ~0.9 h | 3.9% | 12% | 28% | 50% | 5.0% | 41% | 70% |
| 720 | ~1.4 h | 3.9% | 15% | 38% | 66% | 4.9% | 54% | 85% |
| 960 | ~1.8 h | 3.8% | 17% | 46% | 78% | 4.9% | 65% | 93% |
| 1440 | ~2.7 h | 3.9% | 23% | 62% | 91% | 5.0% | 81% | 99% |
| 1920 | ~3.6 h | 4.0% | 29% | 74% | 97% | 4.9% | 90% | 100% |

Under the current rule, 80% power at +3 points needs about 1,000 pairs, and at +2 points about 2,200. Adding (c) (all-cell
capture >= 2 SE, net >= -5) reaches 80% at +3 points with about 600 pairs and at +2 points with about 1,400.

### 8. Alternative rules on the same data

Columns are true effects: win points / capture. "Futility look" means stage 1 stops only when net < 0 and all-cell
capture t < 0; otherwise the pooled 480 decides. That look stops 36% of null arms and 72% of -2-point arms early, and
only 3% of +3-point arms.

| rule | null | -2/-0.10 | -2 pt/+0.15 trade | +1/+0.05 | +2/+0.10 | +3/+0.15 | +5/+0.25 | 0/+0.10 |
|---|---|---|---|---|---|---|---|---|
| current (a) or (b), written | 1.3% | 0.0% | 8.7% | 4.4% | 12% | 25% | 61% | 7.3% |
| current, one-shot 480 | 3.8% | 0.1% | 18% | 12% | 28% | 50% | 89% | 18% |
| current, futility look then 480 | 3.7% | 0.2% | 19% | 12% | 28% | 50% | 89% | 18% |
| + (c) all-cell capture >= 2 SE with net >= -5, written | 1.8% | 0.0% | 19% | 7.1% | 20% | 41% | 82% | 17% |
| + (c), one-shot 480 | 4.9% | 0.2% | 30% | 17% | 41% | 70% | 98% | 36% |
| + (c), futility look then 480 | 4.7% | 0.2% | 30% | 17% | 41% | 69% | 98% | 35% |
| one-sided 5% versions of (a), (b), written | 3.2% | 0.1% | 13% | 9.4% | 22% | 38% | 74% | 14% |
| one-sided 5% versions of (a), (b), one-shot | 7.7% | 0.4% | 23% | 20% | 41% | 65% | 94% | 28% |
| only: all-cell capture >= 2 SE and net >= 0, written | 0.7% | 0.0% | 8.3% | 4.3% | 15% | 34% | 78% | 12% |
| only: all-cell capture >= 2 SE and net >= 0, one-shot | 2.3% | 0.0% | 14% | 12% | 34% | 65% | 97% | 28% |
| only: all-cell capture >= 1.645 SE and net >= 0, one-shot | 5.0% | 0.1% | 15% | 20% | 47% | 76% | 99% | 37% |
| only: all-cell capture >= 1.645 SE and net >= 0, futility look then 480 | 4.8% | 0.1% | 15% | 20% | 47% | 76% | 99% | 37% |

Applied to past arms, adding (c) changes one decision: g4ship1, whose pooled all-cell t was 2.2 with net +12, would have
been promoted. g2cr and g3lost pass either way; every other pooled arm misses (c) as well. g3escrg2's stage 1 (t 2.1)
would also have met (c), but it failed basics. The capture-only rule with net >= 0 promotes g4ship1 too. The one-sided
variant does not: (a) one-sided p is 0.10 and upper t is 1.4.

### 9. Expected value per arm tested (13 Elo per point; realistic coupling)

| prior on true effect | rule | P(promote) | Elo gained per arm tested | P(promote and harmful) |
|---|---|---|---|---|
| track record N(+2, 1.5) | current, written | 17% | +7.4 | 0.1% |
| | current, practice | 27% | +11.3 | 0.1% |
| | current, one-shot | 33% | +13.3 | 0.2% |
| | + (c), one-shot | 44% | +17.1 | 0.2% |
| | capture >= 1.645 SE and net >= 0, one-shot | 48% | +18.4 | 0.2% |
| sceptical N(0, 2) | current, written | 6.2% | +2.2 | 0.2% |
| | + (c), one-shot | 18% | +5.4 | 0.7% |
| | capture >= 1.645 SE and net >= 0, one-shot | 19% | +5.9 | 0.6% |

Under every prior tried, each loosening shown raises the expected gain, and the chance of promoting a harmful arm stays
at or below 1.2% (the worst case is the one-sided one-shot rule under the sceptical prior). Under the track-record prior the written rule captures about 40% of the Elo that the loosest rule above
captures.

## What makes the procedure strict, largest effect first

1. **The stage-1 gate.** Requiring (a) or (b) on the first 240 pairs, then again on the pooled 480, halves power at
   +2 to +5 points. It also drives the null rate down to 1.3%. The practice of extending "promising" arms already
   undoes part of this, but informally.
2. **Criterion (b) reads only the upper half.** Its effective sample is 240 pairs at confirmation, and it ignores
   capture gains in the rest tier. The all-cell capture delta (SE 0.06 at 480) is the most precise statistic the band
   produces, yet no criterion uses it.
3. **The exact two-sided sign test** needs about +4 points to reach 50% at 480 pairs. Its realised null rate is 1.9%,
   not 2.5%.
4. **480 pairs is small for 2-3 point effects.** Even an ideal rule needs 1,000-1,500 pairs for 80% power at +2 points.

## Options that keep false promotion under 5%

All of these are visible in section 8; none is a decision for this report.

- **Make stage 1 a futility look only, and decide on the pooled 480.** Stop when net < 0 and capture t < 0. Null 3.7%;
  +3 points 25% to 50%; +2 points 12% to 28%. This keeps the early stop for bad arms.
- **Add (c), all-cell capture >= 2 SE with net >= -5, on the pooled 480.** Null 4.9%; +2 points 41%; +3 points 70%.
  - Cost: the -2-point / +0.15-capture trade arm is promoted 30% (18% under the current one-shot).
  - Tightening (c)'s margin to net >= 0 gives the "capture >= 1.645 SE and net >= 0" rule: null 5.0%, trade arm 15%,
    +3 points 76%.
- **Extend near misses to 960 pairs instead of stopping.** Current rule at 960: null 3.8%; +2 points 46%; +3 points 78%.
  This costs about 1 hour more VM time per arm, plus new control seeds once per incumbent.

## Caveats

- **Pairs are modelled as independent within the fixed band opponent mix.** Opponent-level heterogeneity in an arm's
  effect is not modelled; it matters for carrying band results over to the ladder, not for the error rates on the band.
- **All arms against one incumbent reuse the same control games.** Each arm's false-promotion rate is as computed, but
  arms tested against the same control can pass or fail together. The g4 arms' stage-1 nets were all positive
  (+1 to +9), which may partly be luck in the g_iter4 control draws.
- **g4ship1's own pooled estimate is partly selected.** It stacks two arms chosen on their seed 515151/616161 results,
  and its stage-1 games share those seeds and control games. The confirmation seeds alone (net +3, capture +0.17 +- 0.09)
  are the unbiased read. Its true size is plausibly +1 to +2.5 points and +0.10 to +0.17 capture. At that size the rule
  promotes it 5-17% written, 8-30% as practised.
- **The "practice" procedure is my reading of when arms were extended.** It is not a written rule.
- **The basics battery is not modelled.** It vetoed g3escrg2, and it can only lower every promotion rate here.
- **The track-record prior is approximate.** It averages 10 band arms after g1basics (method of moments: the spread of
  true win effects is no larger than sampling noise). Arms reach the band only after a delivery gate, so the prior
  describes delivered arms.

## Files and reproduction (scratchpad, criteria/power-sim/)

- `pairs.py` writes `pairs.json` from the census copies in `criteria/census/`.
- `power.py` holds the simulator and the decision rules.
- `run_all.py` produces `results_battery.json` (sections 1-6, 8, 9) and `run_all.log`.
- `run_n.py` produces `results_n.json` (section 7).
- `run_futility.py` produces `results_futility.json`.
- `tables.py` and `summarize.py` print the tables and the expected-value figures.
- Run them with `criteria/venv/bin/python` (numpy).
