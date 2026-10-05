# Shipping rule: alternatives, with simulated power and false-promotion rates

Question (owner): are the promotion criteria in research/REWRITE_EVAL.md ("Per milestone build") too strict?
Scope here: propose alternatives, simulate their power, false-promotion rate and cost on the same parameters as the
current rule, and say how each treats g4ship1. No games were run and nothing in the repo was changed.

## Short answer

- **The false-promotion rate is not too strict.** Under an exact null, the rule as practised (extend to 4 seeds when
  promising, then pooled (a) or (b)) promotes a no-effect arm **2.5%** of the time; as written (it must also pass at
  240) **1.4%**; if every arm got 4 seeds, **3.8%**. That is the usual one-sided 2.5%.
- **Power is too low, because of which numbers are tested.** The rule rejects **66%** of g4ship1-sized gains (+0.14
  capture difference, about +2.4 points of win rate, about +29 Elo) and **80%** of +0.10 gains (about +21 Elo). It
  catches only **17%** of a +0.14 gain that lands in the lower half of the band. Criterion (a) tests wins, which carry
  about 2.6 times less information per pair than captures. Criterion (b) uses only the upper half of the cells. Taking
  the union of the two spends the error budget twice.
- **Use a better test, not a lower bar.** At the same 2.5% false-promotion rate, an all-cell capture test reaches 56%
  power at +0.14. A sequential version that can add seeds 5 and 6 reaches 67%. A hybrid that keeps an upper-tier route
  reaches 66% with even effects, 84% when the gain is in the upper tier and 65% when it is in the lower half. It also
  promotes harmful builds less often than the current rule (0.3% vs 0.7% at -0.05).
  Relaxing the wins test instead (rule ii, alpha 0.10) triples false promotions to 8.2%. It still misses
  capture-type gains: it would have rejected **g3lost**, today's incumbent.
- **g4ship1** (t 2.21 on all-cell captures, wins +12) passes rules (i), (iii), (iv) and the hybrid, and fails (ii)
  (one-sided p 0.103). It sits right on every boundary: the hybrid passes it by 0.01 t. Those rules were chosen after
  its result was seen, so promoting it on the same data would break REWRITE_EVAL's pre-registration ("numbers decided
  here are not moved after a result is seen"). The clean route: pre-register the new rule now and give
  g4ship1 (or g4ship2, which contains it) the rule's third look: seeds 5 and 6, 240 arm games plus a one-time 240-game
  g_iter4 control.

## The parameters (measured, g4ship1 pooled, 480 pairs vs g_iter4)

| quantity | value |
|---|---|
| pairs, upper tier share | 480, 240 upper (10 of the 11 upper-tier bots are in the band) |
| discordant (win changed) | 15.8% (76/480) |
| capture-difference delta SE | all 0.065, upper 0.099, rest 0.084 |
| per-pair correlation, wins change vs capture change | **0.73** |
| wins per pair per unit capture delta | 0.175 (g4ship1, implied by the pairs); across arms 0.14-0.20 (g3lost 0.136, g4pick 0.155, g3escrg2 0.146, g4ship1 0.174, g2cr 0.185, g1basics 0.196) |
| Elo scale (band, uniform-shift model, smoothed per-opponent win rates) | about 12 Elo per point of win rate, so **+0.10 capture delta = about +21 Elo**. Checks against the ladder: g2cr predicted +53 vs +59 measured (1918 to 1977); g3lost about +25-29 vs +31; both have ±35 error |
| what (a) needs at 480 pairs | wins net >= +20 of 76 discordant (48-28). g4ship1 had +12 |
| what (b) needs | upper delta >= +0.20 (2 x 0.10) |
| pairs for 80% power at one-sided 2.5%, effect +0.14 | all-cell captures **~810**; upper-tier captures ~1,880; wins ~2,070 (at +0.10: 1,590 / 3,680 / 4,060) |

So no statistic reaches 80% power on a +0.14 gain with 480 pairs. Capture difference is the efficient one. Adding
wins to it barely helps: a joint estimator gains about 2% information, because wins are mostly a coarser copy of
captures (correlation 0.73).

## Simulation method (sim/ next to this file)

- Per-pair outcomes (wins change, capture-difference change, tier) are drawn **jointly** from the 480 real
  g4ship1-vs-g_iter4 pairs, so the correlation, the 216 zero-change pairs and the heavy tails are real.
- **Null:** each drawn pair's (wins, capture) change gets a joint random sign. This is the exact paired-randomisation
  null. **Alternatives:** the sign is tilted (P(+) = q per tier) to give a chosen true capture delta, either uniform
  across tiers, upper-only or rest-only. The implied wins effect is 0.175 per capture unit, matching the observed arms.
  **"As observed" rows:** bootstrap of an arm's own pooled pairs (g4ship1, g3lost, g2cr, g4pick), i.e. power if the
  true effect equals what was measured.
- Each experiment draws 120 upper and 120 rest pairs per 2-seed block, up to 3 blocks (240/480/720), and applies
  tools/eval-paired.py's statistics exactly: discordant gained/lost, exact sign test, capture t with the sample SE, and
  the upper-tier t. 40,000 runs per scenario (100,000 for the null); Monte-Carlo error is at most ±0.5 points.
- The per-arm cost counts arm games only. The incumbent's own 4-seed band runs are the control (as now). A 120-game
  seed run takes about 13.5 min on the VM (g4ship1: 14:55 to 15:08), so a 240-pair block is about 27 min.

## The rules compared

- **R0 written:** (a) or (b) at 240, then (a) or (b) on the pooled 480. **R0 practice:** extend to 480 if (a) or (b)
  holds or the all-cell t >= 1 at 240 (this is what happened with g4pick and g4ship1), then pooled (a) or (b).
  **R0 pooled-only:** pooled (a) or (b) with every arm extended.
- **(i)** pooled 480: all-cell capture t >= 2 and wins net >= 0. **(i)+screen:** stop at 240 if t < 0.5.
- **(ii)** pooled 480: exact one-sided sign test p <= 0.10 (wins net >= +14 of ~76).
- **(iii)** pooled 480: Bayesian P(delta > 0) >= 0.95 on the all-cell capture delta, with a skeptical prior N(0, 0.10²).
  The width roughly matches the spread of past arms, excluding the g1basics audit bundle. With a normal prior this
  works out to a t threshold: t >= 1.645·sqrt(1 + SE²/tau²), i.e. **t >= 1.96** at SE 0.065. tau 0.05 gives 2.70,
  tau 0.07 gives 2.24, tau 0.20 gives 1.73. With a point mass on "inert" (30-50% of arms exactly 0, slab N(0, 0.10-0.15)),
  it needs **t >= 2.7-3.2**.
- **(iv)** sequential on the all-cell capture t, with wins net >= 0 at any promotion.
  Look 1 (240): promote if t >= 3.0; stop if t < 0.5. Look 2 (480): promote if t >= 2.1; stop if t < 1.0.
  Look 3 (720, seeds 5-6): promote if t >= 2.1. The boundaries are set by simulation to a 2.5% false-promotion rate.
- **(iv-h) hybrid sequential** (keeps an upper-tier route, the intent of (b)). Same looks as (iv). Promote at look 2 or 3
  if (all-cell t >= 2.2 **or** upper t >= 2.5) and net >= 0. Futility uses max(t_all, t_up - 0.3) < 0.5 at look 1 and
  < 1.0 at look 2. Early promotion at look 1 if t_all >= 3.0. The boundaries are set to a 2.6% false-promotion rate.

## Results

Each cell gives the probability of promotion. Effects are true all-cell capture-difference deltas; Elo is about 210
times the delta.

| rule | null (false promotion) | -0.05 (harm) | +0.05 | +0.10 (~21 Elo) | +0.14 (~29 Elo) | +0.20 | +0.25 | upper-only +0.14 | rest-only +0.14 | arm games: null / +0.14 |
|---|---|---|---|---|---|---|---|---|---|---|
| R0 written | 1.4% | 0.4% | 3.9% | 9.7% | 17% | 34% | 50% | 52% | 6.8% | 249 / 294 |
| **R0 practice** | **2.5%** | 0.7% | 7.8% | 20% | **34%** | 61% | 79% | 67% | **17%** | 280 / 408 |
| R0 pooled-only | 3.8% | 1.2% | 10% | 23% | 38% | 63% | 81% | 81% | 19% | 480 / 480 |
| (i) t_all >= 2, net >= 0 | 2.4% | 0.3% | 11% | 32% | 56% | 86% | 97% | 57% | 56% | 480 / 480 |
| (i)+screen | 2.1% | 0.3% | 11% | 31% | 54% | 85% | 96% | 55% | 54% | 315 / 443 |
| (ii) sign, one-sided 0.10 | **8.2%** | **3.1%** | 18% | 33% | 47% | 70% | 84% | 55% | 41% | 480 / 480 |
| (iii) Bayes N(0, 0.10²) | 2.6% | 0.4% | 12% | 34% | 58% | 87% | 97% | 58% | 58% | 480 / 480 |
| (iv) sequential, all-cell | 2.5% | 0.3% | 14% | 41% | **67%** | 92% | 99% | 68% | 67% | 339 / 493 |
| **(iv-h) hybrid sequential** | **2.6%** | 0.3% | 13% | 39% | **66%** | 92% | 99% | **84%** | **65%** | 358 / 506 |

Bootstrap of real arms (promotion probability if the true effect equals what was measured):

| arm as observed (pooled) | R0 practice | (i) | (ii) | (iii) | (iv) | (iv-h) |
|---|---|---|---|---|---|---|
| g4ship1 (+0.14, net +12) | 34% | 58% | 49% | 60% | 69% | 68% |
| g3lost (+0.14, upper-heavy, 86/234 games unchanged) | 92% | 84% | **46%** | 91% | 92% | **96%** |
| g2cr (+0.25, net +22) | 72% | 97% | 88% | 97% | 98% | 98% |
| g4pick (+0.08, net +6) | 18% | 26% | 26% | 28% | 33% | 32% |

What the tables show:
- R0's union of two tests costs false promotions (3.8% with every arm extended) without buying power. Only when the
  whole gain sits in the upper tier does (b) earn its place: there R0 pooled-only reaches 81%, against 57% for (i). The hybrid
  keeps that strength (84%) and drops R0's blind spot in the lower half (65% vs 17%).
- The net >= 0 guard in (i) costs a little power on capture-only arms (g3lost 84% vs 91% for (iii), which has no
  guard). It is still worth keeping: it is the only check that a capture gain is not bought with lost games.
- (ii) is dominated. It reaches (i)'s power at +0.10 only with 3.4 times the false promotions and 10 times the harmful
  promotions, and it misses g3lost-type arms.

### Decision analysis: true effects drawn from a prior (uniform tilt per arm)

| prior | rule | promoted | mean true delta of promoted arms | P(delta < 0 among promoted) | arm games | Elo gained per 1,000 arm games | missed arms with delta >= 0.10 |
|---|---|---|---|---|---|---|---|
| history N(+0.06, 0.08) | R0 practice | 16% | +0.14 | 1.3% | 336 | +14 | 61% |
| | (i) | 25% | +0.14 | 0.7% | 480 | +15 | 40% |
| | (ii) | 26% | +0.12 | 3.9% | 480 | +14 | 49% |
| | (iii) | 26% | +0.14 | 0.7% | 480 | +16 | 39% |
| | (iv) | 29% | +0.14 | 0.6% | 407 | **+21** | 31% |
| | (iv-h) | 28% | +0.14 | 0.8% | 425 | +19 | 32% |
| skeptical N(0, 0.10) | R0 practice | 9% | +0.13 | 3.0% | 301 | +9 | 60% |
| | (ii) | 15% | +0.11 | **8.5%** | 480 | +7 | 49% |
| | (iv) | 16% | +0.13 | 1.4% | 354 | **+12.5** | 32% |
| | (iv-h) | 16% | +0.13 | 1.4% | 370 | +12 | 33% |
| 40% inert + 60% N(+0.05, 0.10) | R0 practice | 12% | +0.15 | 1.3% | 311 | +11 | 55-56% |
| | (iv) / (iv-h) | 18% | +0.14 | 0.5-0.7% | 370-387 | **+15** | 27-28% |

The "history" prior comes from REML on the 11 behaviour-changing arms since seeding, excluding g1basics:
capture mu +0.09, tau 0.06; wins mu +0.020 per pair, tau about 0. It is softened to N(+0.06, 0.08) because arms
judged against one incumbent share its control games, so their errors are correlated (see caveat 3). Under every
prior, the sequential rules give about 1.25-1.45 times R0's Elo per game and promote fewer harmful builds.

## Past arms under each rule (today's 11-bot upper tier)

| arm | data (t_all / t_up / wins net) | R0 (actual) | (i) | (ii) | (iii) P(delta>0) | (iv) | (iv-h) |
|---|---|---|---|---|---|---|---|
| g1basics | 240: 6.20 / 3.08 / +33; 473: 9.28 / 4.67 / +62 | promoted | yes | yes | 1.000 | promote at 240 | promote at 240 |
| g2cr | 240: 3.45 / **1.60** / +11; 473: 3.81 / 1.69 / +22 (p 0.017) | promoted via (b) at 240; **with today's tier (b) fails at 240** (t_up was 2.3 only while waffle was in the tier) | yes | yes (p 0.008) | 0.999 | promote at 240 | promote at 240 |
| g3lost | 240: 2.15 / 2.97 / +1; 473: 3.10 / 4.20 / +9 | promoted via (b) | yes | **no (p 0.121)** | 0.998 | promote at 480 | promote at 480 |
| g4pick | 240: 1.39 / 1.36 / +9; 473: 1.34 / 1.06 / +6 | parked | no | no (0.266) | 0.873 | extend to 720 | extend to 720 |
| **g4ship1** | 240: 1.21 / 0.71 / +9; 480: **2.21** / 1.39 / +12 (44-32) | not promoted | **yes** | **no (0.103)** | **0.968** | **promote at 480** (2.21 >= 2.1) | **promote at 480 by 0.01** (2.21 >= 2.20) |
| g3escrg2 | 240: 2.14 / 2.47 / +6 | basics FAIL | - | - | - | continue, then basics veto | continue, then basics veto |
| g4crumb | 240: 1.04 / -0.32 / +4 | parked at 240 | needs seeds 3-4 | - | - | continue | continue |
| g4bundle | 240: 0.82 / -0.96 / +2 | parked at 240 | needs seeds 3-4 | - | - | continue | continue |
| g2nonav | 240: 0.45 / 1.17 / -1 | rejected | - | - | - | stop | continue (upper route) |
| g2bc2, g4econ2, g4gym1 | 240: 0.20 / -0.95 / -0.66 all-cell t | rejected | - | - | - | stop | stop |

On the data in hand, none of the alternatives promotes anything the current rule rejected, except g4ship1. All of them promote both builds
the current rule promoted, except (ii), which loses g3lost. g4pick stays unpromoted under every rule.

For g4ship1 with the skeptical prior: posterior capture delta +0.10 ± 0.055 (about +21 Elo), P(delta > 0) = 0.968,
P(delta > +0.05) = 0.83. With a point-mass prior (30-50% of arms inert), P(delta > 0) is only 0.73-0.85.

## Caveats

1. **Pre-registration.** REWRITE_EVAL fixed its bars before any result. Picking a rule that g4ship1 happens to pass
   and then promoting it on the same data would be choosing the rule after seeing the result, and g4ship1 sits
   on every boundary. Adopt the new rule for future arms, and resolve g4ship1 through the rule's own look 3 (seeds 5-6).
2. **Capture difference stands in for wins.** It is a proxy, but a well-tested one here: the per-pair correlation is
   0.73, wins move 0.14-0.20 per capture unit in every past arm, and the ladder agrees within error. The proxy
   overstates arms whose extra captures come in games already decided (g3lost: 0.136). The wins net >= 0 guard
   blocks arms that trade lost games for captures.
3. **The control is shared and was selected.** g_iter4's control runs are g3lost's own band games, chosen because they
   looked good. Later arms' deltas are therefore biased down by about half of g3lost's overestimate: roughly -0.01 all-cell
   and -0.02 upper, i.e. 0.15-0.2 SE. So today's process is slightly stricter than its nominal rate. Every arm judged
   against one incumbent also shares that control's luck, so their errors are correlated. Fix: play a fresh incumbent
   control on new seeds at each promotion (already planned with the band refresh, PROMPTS 185).
4. **Criterion (b) depends on the tier list.** g2cr met (b) at 240 only while waffle was in the tier, and adding uravt
   will move it again. An all-cell primary test does not depend on the tier list.
5. **Several arms face the same incumbent.** About six so far against g_iter4. At 2.5% each, six truly null arms in a
   row produce a false promotion about 14% of the time. That is an argument against (ii)'s 8%, not for tightening:
   past arms average slightly positive.
6. **Model limits.** The tilt keeps g4ship1's pair distribution, which has heavy variance (16/240 games identical).
   Arms that change fewer games (g3lost: 86/234 identical) have smaller SEs and more power under every rule. Seeds draw
   new maps, so pairs are treated as independent; inference covers this fixed set of opponents. The basics battery and
   the regression guard are unchanged and apply the same way to every rule.

## Recommendation

1. **Adopt (iv-h) for future arms, pre-registered.** Ship on all-cell capture t >= 2.2 or upper t >= 2.5, with wins
   net >= 0, at 480 or 720 pairs. Futility stops at 240 and 480; early promotion at 240 only if t_all >= 3.0; basics
   battery and regression guard unchanged. Same false-promotion rate as now (2.6% vs 2.5%), about twice the power on
   +0.10 to +0.14 gains, the upper-tier sensitivity kept, the lower-half blind spot removed, and fewer harmful
   promotions (0.3% vs 0.7% at -0.05). Cost: about 360 arm games for a null arm (now 280) and about 500 for a borderline one (now 410).
   The worst case is 720, plus 240 incumbent games once per incumbent for seeds 5-6.
   Simplest alternative: (i)+screen (2.1%, 54% at +0.14, 315-443 games).
2. **Do not loosen the wins test (ii).**
3. **Treat (iii) as a way to report, not a separate rule.** With a normal prior it is (i) under another name (t >= 1.96),
   and its strictness depends entirely on tau. Report the posterior next to the verdict.
4. **g4ship1:** pre-register look 3 now. Seeds 5-6 for g4ship1 plus a g_iter4 control on the same seeds (about 55 min of
   VM); promote if the pooled 720 pairs meet t_all >= 2.2 or t_up >= 2.5 with net >= 0. Alternatively, let g4ship2
   (g4ship1 plus the farm) carry it under the new rule.

## Files

- sim/extract.py: per-pair rows for every seeded band comparison, built with tools/eval-paired.py's own `load`/`pairs`.
  It reproduces the REWRITE.md numbers, e.g. g4ship1 126 to 135, +9 (p 0.188), +0.11 ± 0.09. Output: sim/pairs.json.
- sim/sim.py (simulator and rules), sim/power.py → power.out/power.json, sim/hybrid.py and hybrid_power.py → hybrid_power.out,
  sim/decision.py and decision2.py → decision.out (prior analysis). Census copies (read-only from the VM) are in census/.
- Python with numpy/scipy in venv/ (scratchpad only).
