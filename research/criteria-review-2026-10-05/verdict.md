# Verdict: are the shipping criteria too strict?

2026-10-05. Judge's reading of three analyses: power (power.md), history (history.md) and alternatives
(alternatives.md). I checked their numbers against each other, against the census data and against my own simulator
(criteria/judge/). No games were played, nothing in the repo was edited, and nothing was committed.

## Answer

**Yes, in what the criteria can detect. No, in how often they promote a build that does nothing.** The two need
different fixes.

- **The false-promotion rate is about right.** A null arm is promoted 1.2-1.4% of the time under the written rule and
  2.1-2.5% as practised. That is at or below the usual one-sided 2.5%. Do not lower this bar.
- **Power is about half what the same data can give.** As written, the rule promotes a g4ship1-sized gain (+2.5 win
  points, +0.14 capture, about +30 Elo) 17% of the time; as practised, 34%. To promote half the time it needs about
  +4.2 win points (about +52 Elo) as written and +2.9 (about +36 Elo) as practised. The band-tested arms average about
  +2 points.
- **By how much.** At an equal or lower false-promotion rate (2.0% vs 2.4%), the replacement below promotes about
  twice as many real gains of the size this project produces (g4ship1-sized: 34% to 63-65%). The effect it needs for a
  coin-flip pass is about +2.1 win points (about +26 Elo): half what the written rule needs.
- **Why it is weak.** It is not the 2 SE bar. It is which numbers get tested, and when:
  1. **Criterion (a) tests wins.** Per pair, wins carry 2.2-5.6 times less information than the capture difference on
     every arm (g4ship1 2.6x, g3lost 5.6x). At 480 pairs, (a) needs wins net of about +20 (+4.2 points); g4ship1 had +12.
  2. **Criterion (b) reads only the upper half of the band.** It cannot see a gain in the rest tier (g4crumb: rest
     +0.18, upper -0.03), and its verdict flips when one bot joins or leaves the tier (g3escrg2's upper t is 1.7 or 2.5
     on the same games, depending on waffle).
  3. **The all-cell capture delta is used by no criterion.** It is the most precise number the band produces (SE about
     0.06 at 480 pairs).
  4. **The stage-1 gate at 240 pairs halves power** (written vs pooled-only). Practice already gets around it
     informally, by extending arms that look promising.

## How strict, measured

Probability of promotion. The first row uses a sign-flip null. "As observed" means a bootstrap of the arm's own pooled
pairs, i.e. the promotion probability if its true effect equals what was measured. The proposed rule is set out below.

| scenario | current, written | current, as practised | proposed (2.3 / 2.6) |
|---|---|---|---|
| null arm (false promotion) | 1.2% | 2.4% | **2.0%** |
| harmful: capture -0.05 (about -1 pt) | 0.3% | 0.5% | 0.2% |
| harmful: -2 pt, capture -0.10 | 0.0% | 0.0% | 0.0% |
| uniform +0.10 capture (about +1.7 pt, +21 Elo) | 11% | 21% | 36% |
| uniform +0.14 (about +2.4 pt, +29 Elo) | 20% | 38% | 63% |
| uniform +0.20 | 39% | 66% | 91% |
| g4ship1 as observed | 17% | 34% | 65% |
| g4pick as observed | 9% | 18% | 30% |
| g3lost as observed (promoted) | 84% | 92% | 96% |
| g2cr as observed (promoted) | 42% | 72% | 98% |
| trade: capture +0.15, wins -2 pt | 4.9% | 11% | 12% |
| trade: capture +0.15, wins -1 pt | 8.3% | 18% | 25% (see guard below) |
| mean arm games: null arm / g4ship1-sized arm | ~250 / ~295 | ~280 / ~410 | ~360 / ~515 |

Source: judge/jsim.out and judge/jsim2.out (8,000 draws per repetition, 4-8 repetitions per row). They agree with
alternatives.md within 1-3 points on every shared row and with power.md's model on the current rule (17% written, 30%
as practised, 38% pooled-only at g4ship1 size). The g2cr row is a reminder that the written rule would have passed the
waffle crack only 42% of the time under today's 11-bot tier. It passed because waffle was then in the upper tier.

**Elo scale: 12-13 Elo per band win point.** Ladder gap per pooled band win point: g_iter2 13.4 (+175 for +13.1
points), g_iter3 9.7-12.3, g_iter4 16-23.

## Where the analyses disagree, and which number stands

1. **Power of the current rule at g4ship1's size.** History says about 45% (75% with a capture route); power.md and
   alternatives.md say 17% written, 30-34% as practised, 38% pooled-only (55-68% with a capture route). **Ruling: the
   lower figures.** They come from two different simulators that agree, and my own check gives 17% / 34% / 38%.
   History treated the tests as independent and flagged its figures as rough.
2. **Elo per win point.** History converts at about 7 Elo per point (the slope at a 50% win rate). Power.md uses 13
   and alternatives.md 12. **Ruling: 12-13.** The band's win rates are lopsided and the ladder gaps above confirm the
   scale. History's Elo figures should roughly double:
   - g3lost: about +25 (history said +13);
   - g4ship1: about +30 (history said +17);
   - the realistic crumbs + pick + convoy stack: about +40 to +55 (history said +20 to +30).
3. **Null rate "as practised"** is 2.1% in power.md and 2.5% in alternatives.md. They define "promising" differently;
   my check gives 2.3-2.5%. This does not matter.
4. **Direction of the shared-control bias.** Alternatives.md says g_iter4's control (g3lost's own band games, selected
   because they looked good) biases later arms down by about 0.15-0.2 SE. Power.md says the g4 arms' stage-1 win nets
   were all positive, which hints at an unlucky control.
   - The data lean towards power.md on wins: the six g4 arms' seed 1-2 nets were +9, +2, +3, +4, +1 and +9. Both arms
     that got confirmation seeds fell back on seeds 3-4 (g4pick +9 to -3; g4ship1 +9 to +3).
   - On captures the sign runs the other way: g4ship1 rose from +0.11 to +0.175.
   - **Ruling:** the bias is small and its direction is not settled. The remedy is the same either way: play a fresh
     incumbent control on fresh seeds at each promotion (already planned for the band refresh, PROMPTS 185).
5. **Alternatives.md's hybrid threshold.** Its all-cell threshold of 2.2 sits 0.01 below g4ship1's 2.21, and it was
   chosen from candidates 2.1-2.3 (sim/hybrid.py) with that number known. **Ruling: use 2.3 / 2.6.** Measured:
   - null rate 2.0% (2.2 / 2.5 gives 2.5%; 2.0 / 2.3 gives 3.7-3.9%; 2.4 / 2.7 gives 1.5-1.7%);
   - power about 3 points lower (63% vs 66% at +0.14);
   - g4ship1 is then settled by the rule itself rather than by an exception.
6. **How g2cr was promoted.** History says on (a); alternatives.md says via (b) at 240. Both are true: stage 1 passed
   on (b) with the 12-bot tier, and the pooled 473 passed on (a).

Simulation details:
- **What I reproduced.** Every pooled statistic is reproduced from the census copies with eval-paired.py's own
  functions: g4ship1 net +12 (44-32), t 2.21, upper t 1.39; seeds 3-4 alone t 1.92, net +3; g3lost t 3.10 / 4.20;
  g4pick 1.34 / 1.06; g4crumb 1.04 / -0.32; g2cr 3.81 / 1.69.
- **Lost source.** Alternatives.md's simulator source (sim/sim.py) no longer exists; only its .pyc remains. My check
  is an independent re-implementation.

## The replacement rule (pre-register in REWRITE_EVAL.md before any g4ship2 band result is seen)

**Statistics.** All are taken from tools/eval-paired.py on seeded band pairs against the incumbent:
- `t_all`: the all-cell capture-difference delta divided by its SE;
- `t_up`: the same on the upper tier, with the tier list frozen at the arm's registration;
- `net`: all-cell wins gained minus lost.

**Ship test.** `(t_all >= 2.3 or t_up >= 2.6) and net >= 0`.

| look | pairs (seeds) | ship if | stop (park) if | otherwise |
|---|---|---|---|---|
| 1 | 240 (seeds 1-2) | t_all >= 3.0 and net >= 0 | t_all < 0.5 and t_up < 0.8 | run seeds 3-4 |
| 2 | 480 (seeds 1-4, pooled) | ship test | t_all < 1.0 and t_up < 1.3 | run seeds 5-6 |
| 3 | 720 (seeds 1-6, pooled) | ship test | everything else | - |

**Unchanged:**
- the basics battery, with the absolute bars and the PROMPTS 168 crack exception;
- the regression guard (all-cell net <= -2 SE stops the arm);
- the TRAINING_ALGORITHM §3.5 delivery gate before any band test.

**New controls.** The incumbent's own seeds 5-6 run once per incumbent: 240 games, about 27 min of VM time, reused by
every arm. At each promotion the incumbent gets a fresh control on fresh seeds; the promoted arm's band games are not
reused.

**Properties** (judge/jsim*.out):
- null 2.0%; harmful arms at most 0.2%;
- +0.10 capture 36%; +0.14 63%;
- an upper-only +0.14 gain 84% and a rest-only one 65% (alternatives.md, 2.2 / 2.5 version; about 3 points lower here);
- average cost 360 arm games for a null arm and about 515 for a g4ship1-sized one (now about 280 and 410), at most 720.

Under the track-record priors both power.md and alternatives.md find that a rule of this kind gains about 1.3-2.4
times as much Elo per arm tested, and promotes harmful builds less often.

**Protection against harmful builds.**
- Any build that is worse on both wins and captures is promoted at most 0.2% of the time.
- The remaining exposure is an arm that trades wins for captures. The `net >= 0` guard covers it about as well as
  current practice at -2 points (12% vs 11%). Current criterion (b) is weaker here: its net >= -5 margin is only -1
  point at 480 pairs.
- A mild -1 point / +0.15 trade arm ships 25% of the time, against 18% under current practice. GUARD_RESULT
- No past arm has sat in the trade quadrant. On every arm, wins moved 0.14-0.20 per capture unit.

**Not recommended:**
- Loosening the wins test (alternatives.md rule ii, one-sided p 0.10). It triples false promotions (8.2%), raises
  harmful ones tenfold, and would have rejected g3lost.
- Moving to a 5% bar. The gain comes from testing the right statistic, not from accepting more error, and about six
  arms share each incumbent's control.

## How the rule treats past arms (today's 11-bot tier)

| arm | look 1 (240) | look 2 (480) | under the proposed rule | actual |
|---|---|---|---|---|
| g1basics | t_all 6.2, net +33 | - | ship at look 1 | promoted (g_iter2) |
| g2cr | t_all 3.45, net +11 | - | ship at look 1 | promoted (g_iter3) |
| g3lost | t_all 2.15, t_up 2.97 | t_all 3.10, t_up 4.20, net +9 | ship at look 2 | promoted (g_iter4) |
| g3escrg2 | t_all 2.14, t_up 2.47 | - | continue, then the basics veto (k/d) | basics FAIL |
| **g4pick** | t_all 1.39, net +9 | t_all 1.34, t_up 1.06, net +6 | **not shipped; goes to look 3** (seeds 5-6). About 12% to ship from here if its observed effect is real. | parked |
| **g4crumb** | t_all 1.04, t_up -0.32 | not run | **continue to seeds 3-4** instead of parking. The question is now moot, because g4ship1 contains it. | parked |
| **g4ship1** | t_all 1.21, net +9 | **t_all 2.21, t_up 1.39, net +12** | **not shipped at look 2** (2.21 < 2.3); continues to look 3 | not promoted |
| g4bundle | t_all 0.82 | not run | continue to seeds 3-4 | parked |
| g2nonav (ablation) | t_all 0.45, t_up 1.17 | not run | continue (upper route) | kept as neutral |
| g2bc2, g4econ2, g4gym1 | t_all 0.2 / -0.9 / -0.7 | - | stop at look 1 | rejected |

**On the data in hand, the rule changes no promotion.** It keeps all three, adds none, and sends g4ship1 (and g4pick
and g4crumb, which g4ship1 contains) to more seeds instead of parking them.

## g4ship1

**Do not promote it on the 480 pairs already seen.** Every proposed rule was chosen after its result, it sits on every
boundary, and its seed 1-2 games are partly selected: its two components were picked on those seeds against the same
control. Its clean read is seeds 3-4 alone: capture +0.175 +- 0.09 (t 1.92), net +3.

**Run its look 3 under the new rule:**
- seeds 5-6 for g4ship1 plus the g_iter4 control on the same seeds, about 55 min of VM time;
- ship if the pooled 720 pairs pass the ship test, otherwise park;
- the g_iter4 seeds 5-6 control is reused by every later arm's look 3.

From its current position the chance to ship is about 76% if its observed effect is real, about 47% at half that
size, and about 19% if it is truly null. A pass therefore adds real evidence; it is not automatic.

g4ship2's band delivery reads INCONCLUSIVE (VM file, 17:53) and no g4ship2 band census exists, so g4ship1's look 3 is
the direct route. If g4ship2 later reaches the band, judge it under the new rule.

## Housekeeping found on the way (rule 8; not fixed here)

- REWRITE.md has no rows for the g2nonav, g2bc, g2bc2, g2fast and g4econ2 band tests.
- games.csv holds g4crumb vs g_iter4 against Gymhgy at 640 pairs, net +30 (p 0.029). TRAINING_LOG closed it at 560
  pairs, +20.
- g3escrg2 vs Cyril is 280 pairs, +14 (logged at 240, +8).
- Older upper-tier values in REWRITE.md use the 12-bot tier. uravt leaves the tier at the next re-derivation, which
  is another reason to freeze the tier list per arm.

## Files

- Judge's simulator (independent re-implementation, census copies only), in criteria/judge/:
  - jsim.py and jsim.out: nulls, as-observed bootstraps, threshold sensitivity;
  - jsim2.py and jsim2.out: tilted, harmful and trade arms;
  - jsim3.py and jsim3.out: wins-guard variants.
- Analyses: criteria/power.md, criteria/history.md, criteria/alternatives.md.
