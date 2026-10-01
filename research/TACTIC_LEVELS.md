# TACTIC_LEVELS.md: elementary vs infrastructure-heavy tactics, and which building blocks to build first

*2026-10-01. This is the synthesis of workflow wf_7819c1e0-5b6, revised after three adversarial critiques (evidence, mechanics, actionability). The Review notes at the end list what they changed. It combines 12 per-tactic decompositions into the C1-C14 vocabulary (research/CAPABILITIES.md) with three independent classification methods, each applied to all 12 tactics:*
- ***PDG**: a prerequisite dependency graph built only from engine rules and code paths. A tactic is scored by the number of below-par root capabilities on its path to a win term.*
- ***XO**: cross-opponent statistics over 55 opponents. It measures how common the tactic is in each strength tier, which tactics occur together, and whether the tactic's link to strength survives once capability metrics are held fixed.*
- ***FRCD**: an experimental protocol (fire, reproduce, convert, depend). It was applied retroactively to the ~25 arms in TRAINING_LOG.md.*

*"Beaters" means the 15 bots that beat g_iter1 in the capability census. Every figure carries a source tag:*
- ***[C]** census, g_iter1 vs the 15 beaters, 444 games;*
- ***[S]** survey.csv, g_iter1 vs the 15 beaters, 60 games over two field blocks;*
- ***[F]** the 30 beater cells of one field block;*
- ***[Band]** 240-game band blocks;*
- ***[X]** cross-opponent. XO's tiers come from the survey: 33 weak bots, and 16 strong bots that include SampleProvider, which we beat 17/32.*

*Figures marked **(s)** were recomputed for this synthesis or its revision from research/census-g_iter1*.csv, progress/survey.csv or gauntlet/\*/nav.tsv. Everything below is observational except the arm and ablation results.*

## Summary

- **Elementary tactics can be told apart from infrastructure-heavy ones, but only by answering three questions in order:**
  1. Is the surveyed signature a policy, or an outcome of who wins the fight? (symptom screen)
  2. Did our copy reproduce the opponents' state? (delivery)
  3. How many below-par root capabilities lie between that state and a win term? (payoff graph)

  §1.2 combines them into one procedure, TL-1. §2 applies it mechanically and records every judgement call.
- **Result of TL-1:**
  - **Elementary: none.**
  - **Intermediate, one root each:**
    - T4 setup digging and T7 water traps. Root: the setup crumb budget.
    - T2 flag relocation. Root: flag pressure (C10b).
  - **Composite:** T3, T5, T10.
  - **Symptoms, retired as adoption targets:** T6, T8, T9.
  - **Undetermined:**
    - T1: its survey signature is an outcome. Our own first pickup is r244 against bots we beat and r362.5 against beaters [S].
    - T11: no beater uses it.
    - T12: no signature.
    - T1 and T12 are composite on the graph if a policy signature confirms them.
- **10 of the 11 tactics that had an adoption arm were never reproduced as the opponents run them.** §2.3 lists them:
  - 4 arms did not reach the opponents' state (T3, T4, T5, T10);
  - 2 reached only the cheap layer (T1, T12);
  - 4 tested a different mechanism (T6, T7, T8, T11);
  - T2 completed its action, but an engine rule blocked the payoff;
  - T9 had no adoption arm.

  So the ~25 neutral readings say little about the tactics themselves.
- **Focusing on basics is the better bet, but for a narrower reason than the first draft gave.**
  - **The strongest argument is shared roots.** The setup crumb budget starves T4, T5, T7 and T10. C11 and flag pressure recur in T1, T2, T3, T5 and T12. Fixing a root unblocks several retries at once.
  - **The cross-opponent R² is weak support.** Capability metrics give 0.77, tactic flags 0.51, and both together 0.77 [X]. That shows the in-game capability metrics sit close to the outcome (capture difference alone gives 0.93). It does not show that capabilities cause strength.
  - **g_iter1 (+246 Elo) is not clean evidence.** It repaired a self-play standoff in a weak baseline and added a T1 neutralization, and attribution between them is open. Three of its five parts ablate to about 0 on the band.
  - **No early (≤ r250) basic has a large win gradient inside beater games.** Win rate by tercile is 0.15 / 0.16 / 0.17 for territory and 0.17 / 0.16 / 0.15 for first flag sighting [C](s). The metrics with steep gradients are whole-game or late measures and partly the outcome: kill share (0.03 / 0.07 / 0.37 [C](s)) and the opponents' r200-r400 income.
- **Build order:**
  0. Phase 0 measurement, no bot change. Split pickups into first grabs, re-grabs and relay re-pickups. Record where carriers die, staging and respawn flow over r199-r260, and still% by cause.
  1. Setup crumb budget with one paced floor (C3b + C13), including fills. This is the cheapest enabler.
  2. The C1 hint-search defect.
  3. C11 presence in the enemy half over r200-r260. The lever is chosen from the Phase 0 decomposition.
  4. Flag-pressure defence (C10). The lever is chosen from Phase 0.
  5. C5 fight-trap volume on the freed bank.
  6. C9 carrier protection, once its status is corrected for setup pickups.

  The first draft's "C3 income after the dam" block is dropped. That gap is the opponents' income; our own gathering does not separate wins from losses.

---

## 1. Can elementary tactics be distinguished from infrastructure-heavy ones?

### 1.1 What each method measures, and why they disagree

| method | "elementary" means | sees well | blind spot |
|---|---|---|---|
| Decompositions | (gives the list of prerequisites, not a level) | the full prerequisite list, with code and census evidence | labels 10 of 12 composite, because it counts every deficit on the path, including inherited ones |
| PDG | no below-par **root** capability on the path from the tactic's effect to CAPTURE / MORE_FLAG_CAPTURES / LEVEL_SUM, and no prerequisite tactic | payoff dependency; separates root deficits from inherited ones | thresholds sit inside sampling noise (the SE of P is 0.064-0.075 at n = 43-59, wider than the old ±0.03 knife-edge band); denominators were unstated (C9 is P 0.30 per pickup but 0.83 per game); deciding the tactic's boundary takes judgement |
| XO | weak bots show it about as often as strong bots | build cost; which tactics occur together | cheap is not the same as valuable; capability metrics are in-game measures close to the result; 35 of 55 opponents have only 2 census games (68 of those 70 are our wins) (s); "strength" pools 13 builds; conditioning on mediators would also erase a real mechanism, so "marker" is now informational only |
| FRCD | our copy fires, reproduces the opponents' state on its own, and behaves the same on any base | whether our arm tested the tactic at all | needs arms; its 1-3 game delivery check would have missed T4's failure; the transfer and interaction steps (D) have never been run |

**Where the methods agree, after the corrections:**
- T10 is composite in all four columns.
- T3 and T5 are composite in three of four.
- T1's XO "elementary" vote is withdrawn. It rested on "our median first pickup is r248", which is the median over all 1539 survey games: every build and every opponent, plus 119 games with no pickup. g_iter1 against the beaters picks up first at r362.5 (46 of 60 games had a pickup) [S](s).

**The disagreements follow three patterns, and each pattern carries information:**

1. **Symptoms (T6, T8, T9).**
   - Step 1(a) fires for all three on our own g_iter1 values [S](s):
     - We heal more than we attack in 19% of games against bots we beat and in 100% against the beaters.
     - defend300 is 5 against bots we beat and 6.5 against beaters; 5 in our wins and 7 in our losses.
   - **T6/T8:** (b) also fires. Within the 30 beater cells, our attack-minus-heal correlates 0.70 with kill share (n = 30, 95% CI about 0.45-0.85) (s).
   - **T9:** (b) does not fire (|rho| ≤ 0.26). But weak bots guard far more often than strong ones (23 of 33 vs 4 of 16, Fisher p ≈ 0.005) [X](s). And defend300 is partly geometry: flags at spawn collect respawning robots for free.
   - Step 1(c) adds nothing for T6. e1aggr's paired shifts over 110 games are all below 2 SE: attacks +29 ± 20, heals +28 ± 16, kills −73 ± 41. The paired medians (+5, +5, and +19 kills) even point the other way.
2. **Cheap to execute, expensive to make pay (T1, T2).**
   - XO: 7 of 33 weak bots raid fast and 8 of 33 move flags.
   - T1's survey signature fails Step 1: our first pickup is r247.5 in our wins and r312 in our losses [S](s). "Fast first pickup" partly measures who wins the clash.
   - T2: our copy completed its action. The payoff is blocked by an engine rule: every uncarried flag is broadcast within dist2 100. Delay must therefore come from defence.
3. **Trivial code that marks a strong stack (T4).**
   - XO: 1 of 33 weak bots digs, against 8 of 16 strong ones. Holding capabilities fixed cuts the link to 2% of its raw size. Because that conditioning can erase a real mechanism, this is informational only.
   - PDG and FRCD: there is one starving resource, the setup bank.

### 1.2 Recommended decision procedure, TL-1 (revised)

TL-1 applies to every tactic, existing or new. Steps 0-3 use only data we already have. Step 4 needs a delivery mini-block.

**Step 0. Make the tactic measurable.** Write down:
- **The signature `s_T`.** It must be a policy quantity (what the bot chooses), preferably fixed before contact (≤ r200-r250), and measured for both teams.
- **The mechanism counter `m_T`.** Take it from replays, or from a dedicated counter that replaces a dead one. The indicator string is already full: 63-76 of its 64 characters are used (G.java:40).
- **`s_opp` and `s_ctl`**, on g_iter1's beater games, with the spread of `s_opp` across identical-cell blocks. For example, the opponents' crumbs250 ranges from 1003 to 2032 across our blocks [S](s).
- **The candidate prerequisites.** Only edges from an engine rule (RULES.md) or a code path are allowed.

If there is no policy signature, the label is **undetermined**.

**Step 1. Symptom screen (tactic signatures only).** It uses our own g_iter1 survey values.
- (a) The threshold flag flips between bots we beat (n = 156) and beaters (n = 60), or between wins (165) and losses (55).
- (b) Within beater games, |Spearman| ≥ 0.4 between our own `s_T` and the kill-share difference or the territory difference. Report n and the CI.
- (c) An arm moves `s_T` and its opposite by similar amounts, and both shifts reach 2 SE paired.

Verdicts:
- (a) alone: the signature is outcome-contaminated. Find a pre-contact policy signature. If none exists, the label is **undetermined**.
- (a) plus any of (b), (c) or anti-strength prevalence (weak bots show it more than strong ones, Fisher p < 0.05): **symptom**.

**Capability metrics are exempt from Step 1, with this justification:**
- They are performance measures, so they are expected to differ between wins and losses.
- inEnemy250, firstFlagSight, trapsHit and gathered400 all flip.
- They are therefore used only as deltas against the same-block control, preferring metrics fixed by r250, and their win gradients are never read as causal.

**Step 2. Payoff graph (PDG).**

For each prerequisite node, fix one metric and one denominator (§2.1). Let d be us minus them, oriented so that positive is good. Let P be the share of games with d ≥ 0, with a 95% Wilson interval, and R the oriented ratio of the medians. Statuses:

| code | meaning | how it counts toward b |
|---|---|---|
| − | below par (P < 0.40 and R < 0.85, with the Wilson CI below 0.40), and a root | counts |
| (−) | below par but inherited: P on the games where all measured parents have d ≥ 0 has a CI wholly ≥ 0.40 | does not count |
| u | undecided: the CI straddles 0.40, n < 60, or the metric has a known artefact | counts only toward the upper bound of b |
| 0 | a prerequisite capability that is missing from our code. The tactic's own policy (hand-off rule, moat placement) is not a prerequisite | counts |
| + | par or better | does not count |
| ? | unmeasured | makes the label provisional |

Additional rules:
- A requirement above every beater's observed level counts as below par.
- Report knife edges on R (within ±0.03 of 0.85 or 1.15) as well as on P.
- b is reported as a range: [roots, roots + u].
- K is the set of other tactics the tactic needs.

**Step 3. Marker check (XO): informational only, with no effect on the label.** Restrict it to opponents with 20 or more census games and to capability metrics from before r250. Report the raw and the conditioned coefficients.

**Step 4. Delivery mini-block (FRCD F and R).** Run about 20 random band cells across map types, not 1-3 games: the a3dig5 diagnostic dug 63 tiles by r100, while its field block had a median of 0 digs.
- **F:** `m_T` fires in at least 90% of games.
- **R:** `mean(s_arm − s_ctl) ≥ 0.5 × (s_opp − s_ctl)`, judged against the spread of unrelated arms on identical cells (survey.csv gives this for free).
- Otherwise record one of these outcomes:

| outcome | meaning |
|---|---|
| STARVED(k) | name the k resources of ours that ran short; re-run after freeing them |
| COMPLETED-NO-EFFECT | the action completed, but the signature did not move for an engine reason; closed unless a variant removes that reason |
| DEFECTIVE | the mechanism fires, but overshoots or misbehaves, e.g. the relay's 219 drops per capture |

**Labels:**

| label | rule | action |
|---|---|---|
| symptom | Step 1 verdict | retire; use `s_T` as a manipulation check |
| undetermined | no policy signature, or a contaminated signature with no alternative | build the signature first |
| elementary | not a symptom; upper bound of b = 0; K empty; R = PASS or not yet run | arm now, delivery mini-block first |
| intermediate | K empty; upper bound of b = 1, or STARVED(1) | arm only on a base where that root is at par |
| composite | lower bound of b ≥ 2, or K non-empty, or STARVED(k ≥ 2) | no arm; build the roots, then re-run TL-1 |

When the range of b spans two labels, report both and act on the higher one. Every judgement call (for example, which nodes lie on the payoff path) is written down as an explicit override with its reason.

**Value is a separate question, under one significance rule.** A tactic's value is its band verdict: paired gained-minus-lost over 240 games on identical cells, two seeds.
- About 35-45 discordant pairs per 240 games give 2 SE ≈ 12-13 games.
- This document uses one rule throughout: **significant** at ≥ 2 SE, **lean** at 1-2 SE, **about 0** below 1 SE.
- inEnemy250 has a per-game SD of 9.6 on beater games and 11.6 over all 700 [C](s), so a 240-game block resolves about ±2 robots.
- **Capability metrics resolve in one block; win counts do not.**

**What is measured today, and what is missing.**
- **Measured:**
  - survey columns (crumbs200/250, traps200, digs200, fills200, level200, flagMoveDist, defend300, atk400, heal400, stun400, water400, drops, firstPickup, upgrades);
  - census columns (gathered200/400, firstEnemySide, inEnemy250/300, firstFlagSight, pickups, captured, carrier deaths, rounds and moves, enemyCarrierKills, trapsBuilt, trapsHit, kills, deaths, meanAlive);
  - **C2 navigation**, in gauntlet/\*/nav.tsv (still%, meanMoves, coverage, ABA, both teams);
  - study.tsv: per-50-round attacks, heals, traps_hit, traps by type, level_sum, both teams.
- **Missing:**
  - a split of pickups into first grabs, re-grabs after a carrier death, and re-pickups after a voluntary drop;
  - setup-free pickup and carrier counts (ReplayDump.java:272-275 and :240 include setup own-flag pickups);
  - where carrier deaths happen;
  - staging at r199 and respawn-to-front flow;
  - still% by cause;
  - C3c kill-bounty income on engine flood-fill territory;
  - C7, C12;
  - census rows for the arms.

### 1.3 Is focusing on basics better?

**For it:**
1. **Shared roots.**
   - The setup budget is the named starving resource for T4, T5, T7 and T10.
   - C11 is a root for T1, T3, T5, T10 and T12.
   - Flag pressure is a root for T2, T3 and T12.
2. **Most tactic arms never delivered** (10 of 11), and the resources named in those failures are basics.
3. **Capability metrics resolve within one block** (about ±2 robots); win counts do not (about ±12-13 games).
4. **XO, as weak support only.** The 11 tactic flags add nothing to the capability metrics (leave-one-out R² 0.77 either way; flags alone 0.51) [X]. But this mainly shows that in-game capability metrics are close to the outcome: per-opponent capture difference alone gives 0.93, and census win rate 0.96.

**Against it, or at least tempering it:**
1. **g_iter1 is not clean evidence for basics at the plateau.**
   - Iteration 1 stacked symmetry, trap rings, the advance rule, float spending and carrier chase.
   - It targeted (a) a self-play standoff in g_iter0 and (b) a T1 flag-rush defence. Chase and rings are T1 *neutralizations*, and the symmetry served the carrier intercept (C10), not flag search.
   - The log says "attribution between them is open."
   - On the band, rings (20-19), float traps (19-20) and dam traps (20-16) ablate to about 0.
2. **No early basic has a large win gradient inside beater games.**
   - Territory: 0.15 / 0.16 / 0.17 by tercile, rho 0.06 with winning, Mantel-Haenszel difference +0.08 [0.00, 0.17]. Our wins over beaters are still 11.5 vs 22 [C](s).
   - First flag sighting: flat [C](s).
   - Kill share is steep (0.03 / 0.07 / 0.37, rho 0.43, MH +0.13) [C](s), but it is a whole-game measure.
   - FRCD's model predicts that closing every early deficit raises our win rate against beaters from only 16% to 22%.
3. **Territory raises our flag play, not our win rate.** By tercile it lifts our pickups (1 / 4 / 6 per game) and captures (0.45 / 0.66 / 0.79), but not the win rate.
4. **Basic knob doses failed too** (e1aggr, e2aggr, ct1, ct2), and none was checked against a capability metric.

**Verdict.** Build basics as the roots of the starved tactics.
- Gate each block on its own early metric, measured as a delta against the same-block control.
- Stack the blocks under a tight cumulative non-inferiority rule (§3).
- Judge the stack's win effect with a power-sized test. That is about 8 seeds for the predicted effect; 4 seeds give about 50-60% power.

The supported claim is: "until the roots are at par, tactic arms cannot be tested." The data do not support the claim "basics beat tactics."

---

## 2. Classification of the 12 tactics

### 2.1 Capability status (Step 2 inputs)

| node | metric (denominator) | source | us vs them (median) | P [95% CI] | R | status |
|---|---|---|---|---|---|---|
| C1 flag localisation | firstFlagSight; 0 = never seen, censored late | [C](s) | 272 vs 232.5 | 0.28 [0.24, 0.33] | 0.855 (knife edge) | (−): the gap vanishes at territory parity (254 vs 253) |
| C2 navigation | still% (lower is good) | nav.tsv, 2 × [F](s) | 31.7-32.1 vs 16.9-21.8 | 0.10-0.20 | – | u: below par against beaters, at par against bots we beat (23.4-23.9 vs 23.6-24.5); cause unknown |
| C3a setup gathering | gathered200 | [C](s) | 3200 vs 3000 | 0.95 | 1.07 | + |
| C3b + C13 setup budget | crumbs200, setup traps | [S](s) | 175 vs 2909; traps200 34 vs 0.5 | 0.03 [0.01, 0.11] | 0.06 | − root (one resource, counted once) |
| C3c kill-bounty income | – | – | – | – | – | ? |
| C3d post-dam income | gathered400 − gathered200 | [C](s) | 300 vs 900 | 0.52 [0.48, 0.57] | 0.33 | + by the P rule (39% of games tie at 0); the gap is their income |
| C4 micro | kills | [C](s) | 245.5 vs 292 | 0.57 [0.52, 0.62] | 0.84 | + in medians, but bimodal: 200 vs 258 in our beater losses, 913 vs 345.5 in our wins |
| C5v fight-trap volume | stun + explosive + water by r400, minus traps200 | [S](s) | 24 vs 72.5 | 0.10 [0.05, 0.20] | 0.33 | − (u whether it is a root or inherited from C3b) |
| C5q trap quality | trapHitRate | [C] | 0.86 vs 0.92 | – | 0.93 | + |
| C6 spawn | meanAlive | [C](s) | 44.9 vs 44.6 | 0.57 | 1.01 | + |
| C8 conversion | captures per pickup (games where both picked up) | [C](s), n 328 | 0.074 vs 0.125 (pooled 0.115 vs 0.106) | 0.44 [0.39, 0.49] | 0.59 | u |
| C8a / C10b flag pressure | pickups, ours vs theirs (one paired comparison; it cannot separate our offence from their pressure on us) | [C](s) | 4 vs 17 (means 5.5 vs 21.8) | 0.10 [0.08, 0.13] | 0.24 | − root (persists at parity, P ≤ 0.13, PDG); counts include setup and relay re-pickups |
| C9 carrier protection (= C10a per opportunity, seen from the other side) | carrier deaths per pickup | [C](s), n 328 | 0.89 vs 0.72 (pooled 0.87 vs 0.66) | 0.30 [0.25, 0.35] | 0.81 | u: setup-pickup artefact; inheritance test 17/43 = 0.395 [0.26, 0.54] |
| C11 enemy-half presence | inEnemy250, by nearest spawn centre (not engine territory) | [C](s) | 10 vs 24 | 0.23 [0.20, 0.28] | 0.42 | − root |
| C12 force division | – | – | – | – | – | ? |
| C14 level sum | LEVEL_SUM wins | [C](s) | 32 of our 70 wins over beaters | – | – | + |

**Proposed sub-IDs, to add to CAPABILITIES.md:**
- C3a-d (see the table);
- C5v and C5q;
- C10a interception per opportunity, and C10b pickup prevention;
- C15 upgrade timing.

The first draft's C16 "flag-zone denial" is merged into C10b. "Usage context" and "heal rotation" are parts of the tactics' own policies, not capabilities.

### 2.2 TL-1 derivation

Method column: D = decomposition, then PDG, XO, FRCD. "comp" = composite, "int" = intermediate, "elem" = elementary.

| # | tactic | `s_T` and Step 1 (our value: vs bots we beat / vs beaters; wins / losses) [S](s) | Step 1 verdict | roots in b | K | F / R | b | **label** | D / PDG / XO / FRCD |
|---|---|---|---|---|---|---|---|---|---|
| T1 | Fast flag raid | first pickup ≤ r260: 244 / 362.5; 247.5 / 312 (flips) | (a) fires; (b) does not (rho −0.02 with territory, −0.27 with kill share, n = 30); no policy signature exists | C11 −, C12 0; u: C9, C10b (a raid leaves home thinner) | – | F pass; R cheap layer only | 2-4 | **undetermined** (composite on the graph) | comp / comp / ~~elem~~ withdrawn / comp |
| T2 | Flags relocated in setup | flagMoveDist ≥ 6: 0 / 0; 0 / 0 | passes | C10b −: flags are broadcast anyway, so any delay must come from defence away from spawn | – | F pass; R COMPLETED-NO-EFFECT (enemy first pickup 269 vs 268.5) | 1 | **intermediate** | comp / int / elem / elem |
| T3 | Flag relay | drops ≥ 3: 0 / 0; 0 / 0 | passes | C11 −, flag pressure −, path-distance navigation 0; u: C9 | – | F pass; R DEFECTIVE (219 drops per capture vs the opponents' 3-7) | 3-4 | **composite** | comp / comp / comp / int |
| T4 | Setup digging for XP | digs200 ≥ 20: 0 / 0; 0 / 0 | passes | setup budget − | – (T5 removed; see the notes) | F pass (diagnostic); R STARVED(1): median 0 digs | 1 | **intermediate** | comp / int / comp (marker, informational) / int |
| T5 | Bank through setup, trap in the fight | crumbs250 ≥ 1000 with traps200 ≤ 6: never crossed (220-250 throughout) | passes. Its stun400 ≥ 60 half flips (65 / 50.5), so that half is a delivery check only | setup budget −, C11 −; u: C5v | – | R STARVED(≥ 2): bank about 1065 at r200 but 277 / 50 at r250 | 2-3 | **composite** | comp / int / comp / comp |
| T6 | Attack-heavy fighting | heal > attack share: 0.19 / 1.00; 0.24 / 0.95 | (a) + (b) (rho 0.70 with kill share) | – | – | no significant shift | – | **symptom** | comp / int / int / comp |
| T7 | Water traps | water400 ≥ 3: 0 / 0; 0 / 0 | passes | setup budget −: an additive water trap needs crumbs; at today's bank it can only replace a stun | – | F pass; R wrong usage (volume combat moat in place of a stun; beaters place 3-4) | 1 | **intermediate** | int / elem / elem / elem |
| T8 | Heal-heavy sustain | as T6 | (a) + (b) | – | – | knob was an aggression dose | – | **symptom** | comp / elem / elem / comp |
| T9 | Flag guards | defend300 ≥ 6: 5 / 6.5; 5 / 7 | (a) + anti-strength prevalence (23/33 weak vs 4/16 strong, p ≈ 0.005) | – | – | no adoption arm | – | **symptom** | comp / int / elem (anti-strength) / int |
| T10 | Centre crumb windfall, banked | bank at r250 on windfall maps: never crossed | passes. Windfall share correlates 0.52 with territory (FRCD, pooled), so it is a delivery check only | setup budget −, C11 −; u: C5v | {T5} | R not reproduced | ≥ 2 | **composite** | comp / comp / comp / comp |
| T11 | Attack specialisation | attack-level-4 ducks (`--levels`): no beater uses it, so `s_opp` is undefined | – | – | – | tested "never heal", not an XP-gated specialist | – | **undetermined** | int / int / undetermined / int |
| T12 | Flank raid | none (C12 unmeasured) | – | C11 −, C12 0, flag pressure − | {T1, T3} | cheap layer only | ≥ 2 | **undetermined** (composite on the graph) | comp / comp / comp / comp |

**Notes and explicit overrides:**
- **Knife edges no longer decide any label.**
  - T1 stays composite on the graph whatever C9's status, because C11 and C12 remain. The first draft's note "drops to intermediate if C9 is inherited" was wrong.
  - T5 stays composite whatever C5v's status, because the setup budget and C11 remain. The first draft's note ran backwards: making C5v a root raises b.
  - The inheritance tests for C9 (17/43) and C5v (P 0.41, n = 59) both have Wilson CIs that straddle 0.40, so both are u.
- **T2 (judgement call):** C10b is placed on T2's payoff path, following PDG. XO and FRCD say elementary because execution is cheap, but the rule counts payoff roots. The first draft's override evidence is withdrawn: defend300 10.0 → 5.5 and enemy pickups 603 → 744. Both sit inside the spread of unrelated arms on the same 30 cells: 516-744 enemy pickups and defend300 4.0-12.0 (s). The fall in defend300 is also geometry, since respawns appear only in spawn zones. So the prerequisite is "a home defence that replaces respawn proximity."
- **T4:** the first draft listed T5 as a prerequisite, which made K non-empty. The real dependency is the setup budget, a capability, so T5 is dropped from T4's list.
- **T7:** PDG's "elementary" referred to the replacement form that t7water tested (b = 0). The adoption target is the additive form, which needs crumbs. T7 sits exactly on the XO prevalence threshold (5 of 33 weak bots); that is informational.
- **Missing from the repo:** the decompositions list 3 to 13 prerequisites per tactic, but the decomposition JSON, the PDG edges and the XO/FRCD models are not in the repo. §4 Phase 0 commits them, so the labels above can be recomputed with `tools/tl1.py`.

### 2.3 Why each Adoption test was neutral

| # | category | most likely reason |
|---|---|---|
| T1 | cheap layer only | On the 30 beater cells our first pickup moved from r359 to r282 (arch_rush10) and to r258 (arch_rush). But captures per pickup fell 0.17 → 0.11 → 0.04, and their captures rose 67 → 72 → 78. No SE is available on these 30-cell subsets. Raiders acted alone, skipped combat traps and carried straight to spawn. Both arms also carried the relay (confound). |
| T2 | completed, engine-blocked | Broadcast hints within dist2 100 point raiders at every uncarried flag, so the enemy's first pickup did not move (269 vs 268.5). Early capture losses fell from 6 before r500 to 0, a defensive gain, but our captures stayed at a median of 0. Net 81 vs 84 on 110 cells; 20-21 against arch_rush10. |
| T3 | not reproduced (defective) | The relay was always on: median 192 drops per game, 219 drops per capture against the opponents' 3-7, and 600-1800 drops per game on mazy maps. Captures fell (203 vs 241), and three-flag CAPTURE wins fell 67 → 43. **Likely back-and-forth defect:** `relay()` drops onto a tile next to the dropper (Duck.java:235-243), and next round `pickupFlags` runs for every duck not carrying a flag. So the dropper can re-take its own flag whenever it acts before the intended taker. "Closer to home" is also measured in straight-line distance, so flags shuttle on mazy maps. Confirm with same-robot drop/pickup pairs (`--flags`). |
| T4 | not delivered (starved) | DIG_RESERVE 1000 against an r200 bank of 175 gave a median of 0 digs; 67-73% of beater cells had none [S](s). On dug games against the same-cell control, paired medians were: fills +11 / +10.5 (means +21); stun400 −7.5 / −9 (means −13); kills +4.5 / +21 (means −85 / −41). The sign of the kill effect depends on the statistic. The level-sum ceiling is about 5 games per 240. |
| T5 | not reproduced (starved) | Bank about 1065 at r200 on beater cells (c4bank 1052, c5bank 1065, c6pair 1067), spent by r250: 277 (c5bank), 50 (c6pair) [S](s), against the opponents' 1485-1856 on the same cells. Trap parity was not reached: c6pair triggered 140.5 of their traps vs their 177 of ours on the 15 beaters (167 vs 169 only with SampleProvider included). Wins against the same-cell control: c6pair 5/32 vs 9/32 (−4); c5bank 6/32. |
| T6 | different mechanism | The aggression knobs moved attacks and heals by similar amounts, and none of the shifts reached 2 SE (e1aggr: +29 ± 20 / +28 ± 16). Our profile flips with fight state: attack 1108 / heal 686 against bots we beat, and 890 / 1260 against the beaters [S]. |
| T7 | different usage | The arm copied the volume combat moat of SampleProvider (0-33, median 18.5) and of bots we beat (4-34). The beaters that use water place a fixed 3-4: uravt 3 in 28/28 games, andli28 3, quesswho 3.5, hsmalladi 4 [S](s). It replaced a stun instead of adding one. 43-33 over 4 band seeds: +10 on 76 discordant, 1.15 SE: lean positive, not significant. |
| T8 | different mechanism | Against the beaters g_iter1 already heals more than it attacks in 100% of games (1260 vs 890) [S](s). The "we do it: no" flag came from all-game, all-build medians. The RETREAT_HP knob was an aggression dose (0: 85, 150: 83, 300: 84, 500: 82, 700: 80). |
| T9 | no adoption arm | d1def2 was a rush defence (a neutralization). Weak bots show the signature (23 of 33) far more than strong ones (4 of 16). Our own split is fragile: defend300 is 10.0 in losses (n = 50) and 4.0 in wins (n = 10) for g_iter1 against the 15 [S](s); over all builds it reverses. |
| T10 | not reproduced | The detour fires only with no enemy in view: Duck.java:30 returns into the fight branch before :39. FLOAT_CRUMBS 1500 spends extra crumbs at once, so no bank was ever held. Windfall maps are 34% of band games. Windfall share pays only when we are ahead on territory (win rate 0.27 → 0.70). |
| T11 | different mechanism | The arm tested "never heal", not an XP-gated specialist. Mastery comes at about r1040-1350, while 46% of beater losses end before r1000 [C](s). No beater is known to use the tactic; the source bot, jonters.bling3, loses to us 86% of the time. |
| T12 | cheap layer only | No launch trigger, no choice of a lightly held flag, no separate route. Respawned raiders re-enter from the spawn tile nearest the army's target (trySpawn, Duck.java:134), not from the flank, and the defective relay stayed on. No dose was positive: fl6 19-21 and fl10 20-23 (2 seeds each, neutral); fl15 2-16 (1 seed). |

---

## 3. Building blocks in build order

**Shared test definitions.**
- **Delivery mini-block:** about 20 random band cells across map types. The mechanism must fire in ≥ 90% of games, and the signature must close ≥ 50% of the gap.
- **Mirror screen:** for any micro change, a paired mirror SPRT against g_iter1 (TRAINING_ALGORITHM §5). Micro regressions show up within 48 pairs.
- **BT (band test):** 2 seeds × 120 games on the 20-bot band (14 of the 15 beaters plus 6 others; uravt is not in it). Identical cells, paired gained-minus-lost, plus `--capabilities` / scrim-study on the band replays.
- **Advance a block to the stack** when both hold:
  - its own metric moves ≥ 2 SE in the intended direction against the same-block control;
  - its wins are non-inferior with a **fixed margin, net ≥ −5 per 240 games**. A true-zero arm fails this about 20% of the time, so one re-run is allowed.
- **Cumulative tally:** the stack keeps a running paired tally against g_iter1 over all its blocks. If the tally falls below −2 SE, stop and ablate the last addition.
- **Replace the incumbent** only with a stacked base and a power-sized test. The FRCD prediction (16% → 22% on beater games) is about +10 games per 240 on a band that is 70% beaters. At about 8 seeds (960 games, about 160 discordant, 2 SE ≈ 25) the power is about 0.85. At 4 seeds it is about 50-60%.
- **Guardrails, on every arm:**
  - kills vs deaths and kill share;
  - enemy carriers killed per enemy pickup;
  - their captures per game (a guardrail only, never a ranking metric);
  - LEVEL_SUM wins;
  - heal400;
  - still%;
  - firstEnemySide.

### 1. Setup crumb budget and one paced floor (C3b + C13): the cheapest enabler [Phase 1]
- **Gap.**
  - Bank at r200: 175 vs 2909. At r250: 250 vs 1910 [S](s).
  - We spend the setup crumbs on 34 setup traps and 33 fills (medians) [S](s); they build 0.5 traps.
  - Setup dam traps (20-16), the flag ring (20-19) and float traps (19-20) all ablate to about 0 (< 1 SE).
  - The bank arms reached about 1065 at r200 and lost it by r250 (273, 277, 50 on beater cells (s)); c4bank also carried the contaminated trap placement.
  - Fills are a named spender: c4bank's diagnostic made 99 fills (about 3000 crumbs), and c6pair's made 77 (about 2300) above the 500 reserve.
- **Not a lever on its own:** c5bank and c6pair each read 82 vs 84 on 110 field cells.
- **Its value:**
  - It is the shared root of T4, T5, T7 and T10.
  - It lets combat traps fire at all: placeCombatTrap needs 300 crumbs (TRAP_RESERVE 200 + 100).
  - For the bots we copy, bank and stun volume go together (per-game r 0.56 in beater games (s)).
- **First step.** Write one round-paced team floor: a single function that every spender calls (setup traps, flag ring, dam, float, combat traps, and Nav.fillToward at Nav.java:66). Turn setup and float traps off.
  - Fills get a paced allowance with a stall exemption: a duck blocked by water from its target for about 3 turns may fill. Otherwise ducks would stall on water maps and arrive late at the dam front.
- **Signature gate (delivery mini-block, then BT):**
  - traps200 ≤ 6;
  - fills200 not above the control;
  - crumbs200 ≥ 2500;
  - crumbs250 ≥ 1000;
  - fight-phase traps over r200-r400 ≥ 50 (ours 24, theirs 72.5 [S](s));
  - still%, firstEnemySide and inEnemy250 not worse;
  - water-heavy maps checked separately.
- **Confirming test:** BT as non-inferiority; the expected effect is +0-3 games. If it holds, it becomes base B1 for the T4 and T7 retries and for block 5.

### 2. C1 hint-search defect [Phase 1]
- **Gap.**
  - firstFlagSight is 272 vs 232.5, counting the 18 never-sighted games as late. All 18 had 0 pickups and were losses.
  - It is at par against bots we beat (228.5 vs 231.5) and vanishes at territory parity, so it is inherited (R 0.855 is a knife edge).
  - Its link to our pickups is modest: rho −0.15 excluding the never-sighted games (n = 426), −0.23 censored (s).
  - Win rate is flat by tercile: 0.17 / 0.16 / 0.15 (s).
- **First step.**
  - **The defect:** ducks walk to the broadcast hint (fieldTargetFrom, Duck.java:311-313). The hint can be up to 10 tiles from the flag, while vision reaches about 4.5 tiles, and the ducks then idle there until the hint is redrawn every 100 rounds.
  - **The fix:** sweep the dist2-100 disc around the hint, and clear a hint once its disc has been covered.
  - **Moved out of this block:** `Sym.observe` (Sym.java:56) is never called. That is real, but symmetry is only the last resort in fieldTargetFrom (Duck.java:314), reached when every remaining enemy flag is carried by us. Its real users are carrierTarget's intercept point (Duck.java:281-287) and relocTarget (switched off), so the fix belongs to block 4.
- **Metric:** firstFlagSight delta against the same-block control of −20 rounds or better, with pickups not lower. There is no absolute exit target, because the gap is inherited from C11.
- **Confirming test:** BT as non-inferiority.

### 3. C11 presence in the enemy half, r200-r260 [Phase 2, after the Phase 0 decomposition]
- **Gap.**
  - 10 vs 24 robots in the enemy half at r250 (P 0.23 [0.20, 0.28]); 10 vs 22 at r300 [C].
  - Both sides first cross at r202, so the gap opens in the first 50 rounds of contact.
  - The metric assigns tiles by nearest spawn centre (ReplayDump `side[]`). The engine's territory, which decides the +30 kill bounty, is a flood fill from flag starts, checked at the attacker's tile.
- **Why it ranks:**
  - It is a root for T1, T3, T5, T10 and T12.
  - Across opponents, Spearman with strength is +0.69 [X], the same as kill share's.
  - By tercile it lifts our pickups (1 / 4 / 6) and captures (0.45 / 0.66 / 0.79).
- **Caveat.**
  - Within beater games the win rate is flat (0.15 / 0.16 / 0.17 (s)); MH +0.08 [0.00, 0.17]. Our wins over the beaters are still 11.5 vs 22 (s).
  - The first draft's "hub" correlations (stun400 0.58, attack-minus-heal 0.41) were pooled over 110 field games. Within the 30 beater cells they are 0.1-0.2 (s), so they are dropped.
  - This is infrastructure for flag play, not a standalone win lever.
- **First step: decompose the gap (Phase 0)** into four parts:
  1. **Staging:** our robots near the dam front at r199. 3 per flag are defenders, and ducks gather only from r150.
  2. **Clash:** kills and robots per half over r200-r260.
  3. **Respawn flow:** jailed robots and rounds from respawn to the front.
  4. **Idle:** still% split by cause (kite holds, the defender ring, idling at a hint, stuck behind water with fewer than 30 crumbs).

  C2 is below par against beaters (still% 31.7-32.1 vs 16.9-21.8, P 0.1-0.2 (s)) and may sit upstream of C11.
- **Then pick the lever with the largest component.**
  - **If the clash dominates,** test two separate pre-registered arms, or a 2×2:
    - **k1heal.** In `tryHeal`, skip heals of allies above about 500 HP while an enemy is within dist2 18 and the duck is in the kite branch.
      - Rationale: a heal costs cooldown 30, an attack 20, so a heal on a kiting turn delays the next strike.
      - It differs from the closed heal cuts (sp3/sp5/sp7 removed healers outright) because it applies only during contact.
    - **k1space.** Only when the duck is action-ready *now* and not locally superior.
      - Every tile at dist2 5-10 is threatened by definition (Micro.java:8). Turn order alternates, and our micro has no estimate of enemy readiness.
      - Pre-register how it differs from MICRO_V2, which rescored every tile and approached whenever ready (Micro.java:97) and lost 1-25 and 1-26. k1space changes one term in the kite branch only.
    - **Diagnostics come from replays,** which record cooldowns and positions (ReplayDump.java:243-245), not from indicator strings: ready turns spent in the kite band, heals with an enemy within dist2 20, and hits taken within 2 rounds of entering the band.
    - **Screen:** mirror SPRT against g_iter1 and against arch_rush10, then BT.
  - **If staging or respawn dominates:** a gather-at-the-front rule by r190, or front-biased respawn.
  - Any push must state how it differs from e1aggr, e2aggr, gr4, gr8 and arch_rush.
- **Metrics:**
  - inEnemy250 delta ≥ +2 against the same-block control (about 2 SE);
  - r200-r260 kills and robots per half;
  - their r200-r400 income, as a downstream check.

  atk400 and heal400 are symptom metrics, used only as manipulation checks. The baseline is g_iter1 against the 15: 890 / 1260 [S](s).
- **Confirming test:** BT with the capability gate.

### 4. Flag-pressure defence (C10b pickup prevention; C10a interception per opportunity) [Phase 2, after Phase 0]
- **Gap.**
  - They pick up our flags 21.8 times a game to our 5.5 (means; medians 17 vs 4) [C].
  - **Contamination:** the counts include setup own-flag pickups (6 beaters relocate, 3 pickups each) and relay re-pickups (Gymhgy 19.7, kuma 16.6, NotLLeon 8.0 voluntary drops per game; the beater mean is 2.9 plus 1.2 setup pickups).
  - **Still a robust root:** P 0.10 [0.08, 0.13], and it persists at parity of the other capabilities (PDG).
- **C10a is not a strength.**
  - Per game we kill 11 of their carriers to their 3, but that count reflects exposure.
  - Per opportunity we are behind: carrier deaths per pickup are 0.66 for theirs vs 0.87 for ours (pooled; the same comparison as C9).
  - So C10 comes off the Protect list. "Enemy carriers killed per enemy pickup" becomes a guardrail.
- **Their captures are the scoreboard.** 361 of 374 beater losses end on CAPTURE or MORE_FLAG_CAPTURES (s), so their captures are not used to rank this block or to judge it. 32 of our 70 wins over beaters come at r2000 on LEVEL_SUM (s).
- **Re-grab mechanics (RULES.md):**
  - A dropped flag can be picked up again from the next round.
  - It returns home after **4** end-of-round ticks, or after **25** if the carrier's team has CAPTURING.
  - Beaters buy CAPTURING at r1800 in 129 of the 420 beater survey games and at r1200 in 24 (s). So late in long games, where our LEVEL_SUM wins are decided, the window is 25 rounds.
- **Where our carrier kills happen.**
  - Ducks farther than dist2 225 from an enemy carrier wait at the enemy spawn centre it is heading for (Duck.java:283-289). Many kills probably happen next to their spawn zone.
  - There, a re-grab made from a spawn tile captures immediately, and their jailed robots respawn there.
- **Closed directions (TRAINING_ALGORITHM §4: leave an area after 3 rejects):**
  - Body-based: d1def2 9-25 and d2alert 12-31, both significant.
  - Trap-based: d3fort 16-29 (about 1.9 SE) and n1flagstun 19-23 (neutral; rejected for no gain).
- **First step (Phase 0).**
  - Split post-setup pickups into three kinds: first grabs, re-grabs within the per-game return window after a carrier death, and re-pickups after a voluntary drop.
  - Record each carrier death's distance to the carrier team's spawn zone, and whether the re-grab came from a spawn tile.
  - Record the rounds from the first raider within dist2 36 of a flag to its pickup.
- **Decision rule:**
  1. **If carrier deaths cluster at their spawn zone:** the lever is earlier interception. Tune CHASE_RADIUS2 and the intercept point, and fix `Sym.observe`, which that intercept point depends on.
  2. **Else, if re-grabs are ≥ 30% of first grabs:** arm **z1hold**, a Micro.fight term that pulls ducks to our own dropped flag for the whole return time. The flag is already in `flags` when visible. Comms.carried keeps the carrier position for CARRY_FRESH = 5 rounds, which covers the 4-round window but not the 25-round one.
     - It has no trap: a stun there would repeat n1flagstun, and the engine forbids building next to an enemy robot, which a raider in pickup range blocks.
     - It needs no new Comms slot.
     - Screen it with a mirror against arch_rush10, then BT.
  3. **Otherwise:** no arm; write the re-open condition into the ledger.
- **Metrics:** first grabs against us per game, re-grabs per game, rounds from the first raider to the pickup, and the share of beater losses that end before r1000 (46% (s)).

### 5. C5 fight-trap volume on the freed bank [Phase 2, needs block 1]
- **Gap.**
  - The deficit is early, not inherited from the result: stun400 is 50.5 vs 72, and fight traps over r200-r400 are 24 vs 72.5 [S](s). The first draft's −4.6 was a cross-opponent figure.
  - Quality is near par: trapsHit 149.5 vs 221, hit rate 0.86 vs 0.92 [C].
- **Volume knobs at today's bank of about 250 were neutral or worse:** ct1 20-22, ct2 16-18, ct3 15-23. The combat-trap ablation leans load-bearing (16-26, 1.5 SE).
- **First step.** On B1's bank, re-run combat stuns from 1-2 enemies. This doubles as the T5 retry. Place traps ahead of the advancing line once block 3 lands.
- **Metrics:** fight traps over r200-r400 ≥ 50; trapsHit by r400 (study.tsv `us/th_traps_hit`); hit rate ≥ 0.86.

### 6. C9 carrier protection [Phase 3]
- **Gap.**
  - Carrier deaths per pickup: 0.87 vs 0.66 pooled; P 0.30 [0.25, 0.35]. Carrier rounds per pickup: 24.5 vs 17.7.
  - **Partly an artefact:** both counts include setup own-flag pickups. On the 30 joined beater games, their deaths per pickup are 0.70 overall and 0.75 after setup, and 0.54 vs 0.65 in games where they relocate.
  - The status stays u until it is recomputed on rounds after r200.
- **Code.** `carryFlag` (Duck.java:246) steps by `Nav.moveTo` toward the nearest spawn tile, with no threat term. Escorting is the last priority in `fieldTarget`.
- **First step.** Add a threat-aware carry step: ducks within dist2 20 of our carrier use its next tile as their fight goal.
  - A shared homeward distance field is not feasible: the shared array is 64 × 16 bits with 23 slots in use, there is no terrain memory, and jailed robots cannot sense.
  - Instead, specify a per-robot field built from tiles that robot has seen, with a bytecode budget (25k per turn). It is a prerequisite for this block and for T3.
- **Metrics:** setup-corrected carrier deaths per pickup ≤ 0.70; captures per pickup at or above the control as pickups rise. The payoff is capped while we make about 4-5 pickups a game.

### 7. C7 communication: built on demand
23 of 64 Comms slots are used. Add slots only as other blocks need them:
- the carrier's position and the escort target (block 6);
- the army front or centroid (block 3, if its lever needs one);
- a budget slot (block 1), if the floor needs one.

Block 4 no longer needs a slot. Before adding indicator counters, drop the dead ones: rl, ry, dg, rs, wt, fs and cd while their arms are off.

**Protect: guardrails, not targets.**
- **C4 micro.** Even in medians (P 0.57), but bimodal: 200 vs 258 in our beater losses, 913 vs 345.5 in our wins (s). MICRO_V2 lost 1-25.
- **Healing volume:** sp7 lost 12-30 (2.8 SE).
- **ATTACK upgrade timing (C15):** capture-first lost 14-33.
- **Combat stun traps:** leaning load-bearing (16-26, 1.5 SE).
- **C14 level sum:** 32 of our 70 wins over beaters.
- **C6:** meanAlive 44.9 vs 44.6.

**Not now.**
- **C12 force division.** No dose was positive:
  - fl6 19-21 and fl10 20-23 (2 seeds each, neutral);
  - fl15 2-16 (1 seed);
  - arch_rush10 84/110 (equal to the control);
  - the all-out rush lost 5/32 head to head.

  gr8 (4-18) is a cohesion dose, the opposite of force division, and is no longer cited here. C12 depends on C11.
- **Our own post-dam crumb sweep (the first draft's block 3).**
  - The r200-r400 gap is at par under TL-1 (P 0.52, median 0, 39% exact ties), and its tercile gradient depends on how the ties are split.
  - Our own gathering does not separate wins from losses: median 200 in wins vs 300 in losses, means 1364 vs 1285.
  - Theirs does: 250 vs 1050, means 2027 vs 3415.
  - Per opponent, our own wins-minus-losses gathering is negative for 9 of 13 [C](s).
  - Their income follows from territory and kills (map crumbs they reach; bounties earned on our territory). It is now a downstream metric of blocks 3 and 4. The windfall share (0.26) remains the T10 delivery check.
- **Flag relocation and digging beyond block 1.**

---

## 4. Building-block roadmap

### Phase 0: instrument and correct (no bot change)
1. **Fix and extend the readers.**
   - **Corrections in `--capabilities`:**
     - count pickups, carrier rounds and carrier moves only after r200;
     - treat firstFlagSight = 0 as censored.
   - **Additions to `--capabilities`:**
     - pickups split into first grabs, re-grabs (window 4 or 25 rounds, depending on the `upgrades` column) and voluntary re-pickups;
     - each carrier death's distance to the carrier team's spawn zone, and re-grabs made from spawn tiles;
     - robots near the dam front at r199;
     - jailed robots and respawn-to-front rounds over r200-r260;
     - kills and robots per half over r200-r260;
     - heals made with an enemy within dist2 20;
     - ready duck-turns in the kite band;
     - kill-bounty crumbs on engine flood-fill territory;
     - same-robot drop/pickup pairs.
   - **Reuse instead of adding new columns:**
     - study.tsv already has traps_hit, level_sum, water traps by round, and attacks/heals per 50 rounds;
     - nav.tsv already has still%; split it by cause.
2. **Run the census on the kept arm replays.**
   - Field-block arm replays are local (a2reloc to c6pair, e1aggr, e2aggr, arch_rush10). They have 30 beater cells each, which resolves inEnemy250 to only about ±3-4 robots.
   - Band-arm replays (fl6, fl10, t7water, t8hp0) are not local. Confirm whether the VM kept them; vm-collect pulls results, summary, losses and logs.
3. **Trace T7 usage.**
   - Start with study.tsv water traps by round (setup vs fight).
   - Then trace replays for andli28, hsmalladi, quesswho, uravt (2 census games, not in the band) and SampleProvider (the volume user).
   - Drop Gymhgy: its median is 2, and its water count matches its setup traps.
4. **Commit the TL-1 inputs.**
   - research/tactic-levels/{nodes.csv, edges.csv, decompositions.json}, plus `tools/tl1.py`, which computes statuses and labels from the census CSVs.
   - Write the closed-directions ledger and the functional-area map that TRAINING_LOG lines 4-5 promise. Map each building block to the arms it must differ from, with re-open conditions.
5. **Apply the documentation corrections** in §5.3.

### Phase 1: cheap enablers, stacked into base B1
- **Building blocks:** 1 (budget and floor, fills included) and 2 (hint search).
- **Procedure:** delivery mini-block → BT with the metric gate (a delta against the same-block control) → add to the stack → running tally against g_iter1.
- **Exit criteria:**
  - block 1's signature gate is met;
  - the firstFlagSight delta is −20 rounds or better;
  - no guardrail is lost;
  - the stack tally is ≥ 0 within 1 SE.

  If a criterion fails: fix the arm once and re-run its mini-block. Otherwise drop it from the stack and record it in the ledger.
- **Retries on B1:**
  - **T4 at a stated per-duck dose.** Delivery check: digs in ≥ 90% of games, level200 rising, fills200 not rising, and no duck reaching build level 4 before heal (build level 4 caps heal and attack at 3; check with `--levels`).
  - **T7 as an additive arm,** if the usage trace supports a fixed moat per flag or per carrier.
- **T11 is not scheduled.** It has no beater source, and its blocker ("games end before mastery") is an outcome, not a capability.

### Phase 2: territory, flag pressure and fight traps (base B2)
- **Building blocks:** 3 (the C11 lever chosen in Phase 0), 4 (the C10 lever chosen in Phase 0) and 5 (fight-trap volume on B1's bank).
- **Exit criteria:**
  - an inEnemy250 delta of +2 or more per step;
  - first grabs against us down;
  - fight traps over r200-r400 ≥ 50;
  - guardrails held.
- **Retries:**
  - **T5:** bank ≥ 1000 at r250, plus at least 50 fight traps over r200-r400.
  - **T10:** judged on windfall maps. Signature: the bank is held at r250, and our windfall share rises from 0.26 to at least 0.40.

### Phase 3: carrier protection and routing (base B3)
- **Building block:** 6 (C9, with a per-robot homeward field).
- **Retries:**
  - **T3 as a sparing hand-off.** The dropper must not re-pick its flag, and "closer to home" uses path distance. A delivery check must show 3-10 drops per capture.
  - **T2 with terrain-aware placement,** only after block 4 shows a home defence that works away from spawn, i.e. one that replaces respawn proximity.

### Phase 4: force division
- **Building blocks:**
  - C7 slots for enemy disposition;
  - a C12 squad with a target, a rendezvous and a launch trigger;
  - a policy signature for T1 and T12: raiders committed to a flag by r210, regardless of the clash.
- **Retry T1 and T12** only when all of these hold on band beater games:
  - inEnemy250 ≥ 18;
  - setup-corrected carrier deaths per pickup ≤ 0.70;
  - first grabs against us down by a third.

### Retire
Retire T6, T8 and T9 as adoption targets. Keep atk400, heal400 and defend300 as manipulation checks.

### Stop doing
- **Building an adoption arm without a TL-1 entry and a delivery mini-block.** 10 of the 11 arms never reproduced their tactic.
- **Judging arms on win count alone,** especially on 110-cell field blocks, where about 80 cells are near-certain wins.
- **More home-defence arms without a stated difference.** Body-based defence (d1def2, d2alert) and trap defences at the flag (d3fort, n1flagstun) are closed.
- **Knob doses without a capability gate:** RETREAT_HP, trap-count triggers at today's bank, aggression margins.
- **Detaching squads before C11 is fixed.**
- **Leaving `C.RELAY` on inside other arms.** arch_rush10 and the fl* arms carried the likely-defective relay, so their verdicts are confounded.
- **Taking a baseline or a "we do it" flag from medians over all games or all builds.** That produced the T8 mislabel and the T1 "r248".
- **Citing pooled figures as beater evidence:** "18 vs 13 in our wins", "fights even (212-220)", and pooled field correlations.
- **Reading whole-game win gradients as causal,** or per-game counts without their denominator ("11 vs 3 carrier kills").

---

## 5. Proposed changes to TACTICS.md and to the training loop

### 5.1 TACTICS.md

Keep the main Adoption / Neutralization table. Add a companion table, "Levels and prerequisites", with one row per tactic. The rows are generated by `tools/tl1.py`.

| # | level (TL-1) | Step 1 verdict | D / PDG / XO / FRCD | signature `s_T`: beaters vs us [source, n] | prerequisites (status) | b (range), K | our copy delivered? (F / R) | retry when |
|---|---|---|---|---|---|---|---|---|
| T4 | intermediate | passes | comp / int / comp (marker, informational) / int | digs200 30-90 for the 8 diggers vs our 0; level200 5.5-18 vs our 3.5-9.5 [S, 4 games each](s) | setup budget −, C14 + | 1, – | F pass (diagnostic); R STARVED(1): median 0 digs | B1 reached (crumbs200 ≥ 2500); dose kept below build level 4 |
| T7 | intermediate | passes | int / elem / elem / elem | water400: uravt 3 (28/28), andli28 3, quesswho 3.5, hsmalladi 4; SampleProvider is a volume user (0-33); ours 0 [S](s) | setup budget −, trap placement + | 1, – | F pass; R wrong usage (volume moat replacing a stun) | the usage trace is done and B1 is reached; retry as an additive arm |

Also:
- In the survey block, compute "we do it" on g_iter1's beater games only.
- Add a "symptom?" column to the survey block, filled by the Step 1(a) flip test.
- Add a "level" cell that `tools/tactics-survey.py` leaves untouched.

### 5.2 Training loop

1. **No adoption arm without a TL-1 entry** (`tools/tl1.py` output). Only elementary tactics get an arm directly. Intermediate tactics get one only on a base where their root is at par.
2. **Pre-register every arm.** Record:
   - `m_T`, taken from replays or from a counter that replaces a dead one;
   - `s_T` and its target, at least 50% of the gap;
   - the capability metric it should move, and the expected delta;
   - the guardrails;
   - the closed arms it must differ from, taken from the ledger.
3. **Delivery mini-block before any BT** (about 20 band cells). F ≥ 90%; R ≥ 50% of the gap, judged against the spread of unrelated arms on identical cells. A STARVED arm is re-run after its resource is freed.
4. **Run the census (or scrim-study) on every block.** Append the rows to a per-arm CSV. Every verdict table shows the wins together with the capability deltas and their SE.
5. **Two-tier acceptance.**
   - A block advances to the stack on its metric (≥ 2 SE against the same-block control) plus non-inferiority, net ≥ −5 per 240.
   - The stack keeps a running paired tally against g_iter1.
   - A base replaces the incumbent only after a power-sized test (about 8 seeds).
6. **One significance rule:** ≥ 2 SE significant, 1-2 SE lean, below 1 SE about 0.
7. **Re-classify after every new base** with `tools/tl1.py`. Retry a tactic only when its "retry when" condition is met.
8. **Log template per arm:** tactic or capability; TL-1 label; pre-registered metrics; delivery result; capability deltas; guardrails; wins; verdict and the reason for it.

### 5.3 Corrections to existing docs
- **TRAINING_LOG and the census headline.**
  - "Flips to 18 vs 13 in our wins" holds over all wins. In wins over the beaters it is 11.5 vs 22 (s).
  - "Fights roughly even (212-220 in our losses)" covers all 414 losses. In the 374 beater losses it is 200 vs 258 (s).
- **TACTICS T1 survey line.** "We do it" for the fast raid holds only against bots we beat. Against the beaters, g_iter1's first pickup is r362.5 [S](s).
- **TACTICS T8.** "Our heals trail our attacks by r400 (796 vs 974)" is a median over all games and builds. g_iter1 against the 15 heals 1260 and attacks 890 [S](s).
- **TACTICS T11.** "Deaths reset attack XP" overstates the rule. The engine docks XP from the highest-level skill per a penalty table, which for a non-healer is attack (RULES.md:33 gives no numbers). Add the table to RULES.md. The first draft's "5-12 XP" is withdrawn as unsourced.
- **TACTICS T5.** "The combat-trap rule does not spend the bank" is wrong. The arms held about 1065 at r200 and were down to 277 (c5bank) and 50 (c6pair) by r250 on beater cells (s). Fills also drained the bank.
- **TACTICS T7.** The water users that beat us place 3-4, except SampleProvider. Gymhgy is below the threshold.
- **TACTICS T10.** "We already collect the centre" came from a mirror diagnostic. Against the real windfall bots on HungerGames we gather 2100-2600 vs their 19600-20100.
- **TRAINING_LOG.** "arch_rush keeps the relay, which fed its captures" is not supported: arch_rush10 had 198 captures vs the control's 241. The closed-directions ledger and the functional-area map promised in lines 4-5 do not exist yet.
- **CAPABILITIES.md.**
  - Add the sub-IDs from §2.1, each with one metric and one denominator.
  - Rename C11's metric to "robots in the enemy half (nearest spawn centre)".
  - Note that C9 and C10a per opportunity are the same paired comparison.
- **ReplayDump.** cPickups (lines 272-275) and kCarrierRounds (line 240) count setup own-flag pickups.

---

## Uncertainty and limitations
- **Mostly observational.** Every capability status rests on one build (g_iter1), a fixed map mix and 444 beater games. The survey-based statuses rest on 60 games, and the within-beater correlations on 30.
- **Whole-game metrics partly measure the outcome.** Kill share, pickups, traps and carrier deaths are whole-game metrics, and even r250-r400 metrics carry some reverse causation. In-game capability metrics also dominate the cross-opponent R² because they sit close to the result.
- **Thresholds are conventions.** The TL-1 cut-offs are not derived. Wilson intervals now mark undecided statuses (C2, C5v-inheritance, C8, C9), but the 0.40 / 0.85 lines themselves are arbitrary. Two labels rest on explicit judgement calls: C10b on T2's path, and the additive form as T7's target.
- **Measurement artefacts.** Setup own-flag pickups and relay re-pickups contaminate C8, C9 and C10 until Phase 0 lands.
- **Unmeasured mechanisms.** The re-grab mechanism, carrier-death locations, the kite-band hypothesis and the staging/respawn split are hypotheses built from engine rules and code reading. Phase 0 exists to test them before any arm is built.
- **Small predicted effects.** The predicted win effects of single blocks are below the band's ±12-13 game resolution. The case for basics rests on shared roots, metric gates and stacking, not on any controlled test of a basics-first programme.
- **Status is relative to the median beater,** not to the top of the field.

## Review notes

**What the critiques changed (by critique and item number):**
- **Classification (3.1, 3.2, 3.3, 1.19).**
  - TL-1 was rewritten so that the labels follow from recorded columns: Wilson intervals, an "undecided" status, "0" counted as a root, "?" making a label provisional, b given as a range, and knife edges on R.
  - New labels: T1, T11 and T12 are undetermined; T7 is intermediate; no tactic is elementary.
  - The T1 and T5 knife-edge notes were corrected.
- **Signature screen (1.3, 2.6).** T1's "r248" was replaced by r362.5 against beaters, and XO's "elementary" vote for T1 was withdrawn.
- **Block 3 (1.1, 3.4).** The C3 income block was dropped as a standalone block: the gap is the opponents' income, at par by P, with tie-sensitive terciles.
- **firstFlagSight (1.2).** The 0 sentinel is now censored, and the C1 correlations and terciles were recomputed.
- **C5 (1.4).** C5 volume is now an early deficit (24 vs 72.5 fight traps), promoted to block 5.
- **Enemy captures (1.5).** They are now a guardrail only, not a ranking metric.
- **C10 and C9 (1.6, 2.7, 3.6).**
  - C10 was split. C10a is per opportunity, below par, and the same comparison as C9.
  - The setup-pickup and relay artefacts are flagged, and C9 is set to u.
- **C4 and the "no single basic" claim (1.7).** C4's beater-loss figures were corrected. "Fights even" was removed, and "no single basic" became "no early basic."
- **Step 1 on capabilities (1.8).** Capabilities stay exempt from Step 1, with the justification in §1.2. Pre-r250 metrics and control-delta gates were adopted.
- **Pooled correlations (1.9).** The pooled "hub" correlations were dropped; within beater cells they are 0.1-0.2.
- **XO (1.10, 3.20).** XO is reframed as closeness to the outcome. "Marker" no longer affects labels, and Step 3 is restricted to opponents with 20 or more games and to pre-r250 metrics.
- **g_iter1 (1.11, 2.5).** g_iter1 is described accurately (five parts, rings included, attribution open), and argument #1 for basics is weakened.
- **Populations (1.12, 2.16, 3.9).** One population with source tags; baselines re-anchored on g_iter1 against the 15 (890 / 1260). Symptom metrics are no longer exit criteria.
- **Single figures (1.13, 1.14, 1.16, 1.17, 1.24, 1.25, 3.19).**
  - c6pair's result is now reported against the control (−4).
  - T9's guard numbers were corrected to 10.0 vs 4.0 (n = 50/10) and called fragile.
  - e1aggr's shifts are not significant, so T6 now rests on (a) + (b).
  - T4's side effects are given as paired medians.
  - The T7 ranges and the trace list were corrected.
  - The bank figures were corrected to beater-cell values.
- **T2 (1.15, 2.14, 3.11).** The defend300 override evidence was dropped, and the geometry is explained.
- **Bank and stun (1.18).** The "r 0.03" claim was replaced: in beater games the opponents' bank and stun volume correlate at 0.56.
- **Acceptance design (1.20, 3.12).** Fixed −5 margin, cumulative tally, power-sized final test (about 8 seeds).
- **C12 evidence (1.21).** gr8 was removed from the C12 evidence; the neutral arms and seed counts are shown.
- **Significance (1.22).** One significance rule. Combat traps are now "lean load-bearing (1.5 SE)".
- **The "9 of 12" count (1.23).** Replaced by the traced "10 of 11", with the list in §2.3.
- **Re-grab window (1.26, 2.1).** 4 rounds before CAPTURING, 25 after; set per game from `upgrades`.
- **Carrier-kill location (2.2).** Recorded in Phase 0, with earlier interception as the alternative lever.
- **Closures and z1deny (2.3, 3.7).** Two closures are reclassified as trap-based. z1deny became z1hold: no trap, no new Comms slot, a 30% threshold and a fallback.
- **Sym.observe (2.4).** Moved to block 4; block 2's metric rests on the hint-idling defect alone.
- **Engine territory (2.8).** C11's metric was renamed, and C3c will use engine flood-fill territory.
- **k1ready (2.9, 3.8, 3.16).** k1ready was split into k1heal and k1space, distinguished from MICRO_V2, given a mirror screen first, and placed behind a Phase 0 decomposition (staging, clash, respawn, idle).
- **Instrumentation (2.10).** The indicator string is full; diagnostics now come from replays, and dead counters are dropped first.
- **Homeward field (2.11).** Now per-robot, with a bytecode budget.
- **T3 (2.12).** The relay is named as a likely back-and-forth defect, and the retry conditions were set.
- **T12 (2.13).** Reworded as a spawn-placement problem.
- **T11 XP (2.15).** The XP correction was softened; the penalty table goes into RULES.md.
- **Fills and the floor (2.17, 3.13).** Fills are a named spender, with a paced allowance, a stall exemption and nav guardrails.
- **T4 dose (2.18).** The build-level-4 cap was added to T4's delivery check.
- **C2 (3.5).** C2's status was set from nav.tsv as a candidate root.
- **Delivery check (3.10).** Replaced by a delivery mini-block.
- **Reproducibility and order (3.14, 3.15).** A commit plan for the TL-1 inputs and the ledger; one build-ordered list; T11 removed from Phase 1.
- **Phase 0 inputs (3.17).** Redundant reader additions were replaced by study.tsv and nav.tsv, and band-replay availability is flagged.
- **T7 trace (3.18).** Corrected.

**Where a critique was partly wrong (draft kept, with the reason):**
- **2.17:** c6pair's 560 crumbs at r200 is one diagnostic game. On the 30 beater cells its median is 1067.5 (s), so "about 1065 at r200" stays; the point about fills was adopted.
- **3.2:** C10a counts as "above par" only per game. Per opportunity it is P 0.30, as critique 1.6 says, so the split was adopted with that status.
- **3.1, on T4:** T4 is not relabelled composite. T5 was dropped from its prerequisites, because the dependency is the setup budget, not the T5 tactic.
- **3.11 / 1.15, on T2:** the evidence critique was accepted, but T2 stays intermediate on PDG's C10b root rather than reverting to the majority vote. The rule counts payoff roots, not execution cost.
- **1.8:** capability metrics stay exempt from the symptom screen, because performance measures are expected to differ between wins and losses. The fix is control-delta gates and pre-r250 metrics.
- **Small numeric differences that do not change any conclusion:**
  - g_iter1's first pickup against the 15 beaters is r362.5 over the 46 games with a pickup (critique 3.1 had r332).
  - Beater-game defend300 is 10.0 in losses vs 4.0 in wins (critique 3.19 had 8 vs 4).
  - Our own per-game bank-stun correlation is 0.38 over g_iter1's 220 survey games (critique 1.18 had 0.08).
  - g_iter1's median heal400 against the beaters is 1260 (critique 3.9 had 1223).
