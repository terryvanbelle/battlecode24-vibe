# Critic: synthesis.md (why g_iter4 loses to Gymhgy.v10official)

## What I checked, and how

- **Census.** A frozen copy of the 101 `research/fill/fillcensus-g_iter4-*.csv` files, Gymhgy rows only: **1,507 games
  (595-912, 39.5%), 78 maps**. Copy and scripts: `scratchpad/critic/` (`cl.py`, `side.py`, `centre.py`, `grabs.py`,
  `chains.py`, `contact.py`). The census grew from 1,319 to 1,507 games while I worked, so all figures below use the
  frozen copy. Where a lens quoted an older snapshot, I reran it on the first N games in file order.
- **Replay data.** I reused the lens extracts from the VM rather than pulling new ones. That covers lens 2's per-chain
  JSON (394 replays, 3,505 closed chains) and flag-event logs, lens 4's `FS.pkl` (502 replays) and lens 1's `lvl.pkl`
  (57 LEVEL_SUM replays). I played no games and did not touch the VM queue or the repo.
- **Code and rules.** I read src/bot Duck.java, Micro.java and C.java, and the BC24 engine source
  (`reference/battlecode24/engine`: RobotControllerImpl, InternalRobot, SkillType). History is from TRAINING_LOG.md and
  research/RETEST.md.
- **Statistics.** Within-map W-L comparisons use harmonic weights. "t" uses pooled variance; it is conservative and runs
  lower than the lenses' per-map t. Where that changed a verdict, I also ran a permutation of the game labels within
  each map ("perm p").

## Verdicts on the summary claims

| # | claim | verdict | evidence |
|---|---|---|---|
| S1 | Gymhgy grabs our flags as often in wins as in losses | **holds** | Per game within map +0.22 (t 0.5). In fixed windows (from lens 2's flag logs): r201-400 −0.31 (t −1.2), r201-600 −0.01. Per post-setup round it grabs 56% more in losses, but only because losses are 300 rounds shorter. |
| S2 | "Nearly all of the extra captures in our losses are unopposed" (R2: unopposedCaps t −14.6, ~85% of the gap) | **wrong as evidence** | 92.4% of **all** its captures count as unopposed: 91.6% in wins, 92.7% in losses. `unopposedCaps` scores the trip of the carrier that captures. In a relay that is a 1-2 round leg at its own spawn. "85% of the gap" is just this base rate. Opposed captures differ by only 0.06 a game. The t −14.6 is the outcome (its captures) restated. |
| S3 | "Its first capture, of our nearest flag, comes early in wins and losses alike" | **wrong** | At least one capture by r600: 70% W vs 83% L (within −0.14, t −5.7; lower in wins on 42 maps, higher on 9). First capture round is later in wins: +159 rounds (lens 4, t 3.6), +177 (lens 5, t 4.8). The first flag captured is the nearest one in only 47-54% of games. What *is* alike is the conversion of chains that start r201-300: 0.40 vs 0.43 (reproduced). |
| S4 | Our sighting is stale on 46% of the rounds of a capture chain | **holds, but it does not separate W from L** | Reproduced at 48% (ours20 = 0 for 6+ rounds). Under the same definition, returned chains are 13-18% (not 5%). Capture chains in wins and losses are identical (0.476 vs 0.484). The number describes capture chains, which are selected on the outcome. |
| S5 | The fight branch ignores the chase target; CARRY_FRESH is 5; a dropped own flag is not a sighting | **holds** | Duck.java 47-75 and 161-170; C.java 19. One addition: Micro.fight applies the goal term only in the kite/hold branch. The engage and "local superiority" branches ignore it, so INTERCEPT never pulls a duck that can strike a screen duck. |
| S6 | 0-0 level-sum games 9-24; we never dig; median margin 17 | **holds** | 9-24 on the first 499; 28-48 on 1,507. Median margin 17, and "+30 levels flips 20 of 26", both reproduce from lvl.pkl. Digs 0 vs 73. |
| S7 | Centre-crumb maps win 0.31 vs 0.43; "g_iter4 never picks up crumbs after setup" | **class effect holds; the crumb part is wrong** | The class effect replicates out of sample. Games 500-1,507: 0.317 vs 0.412. Map permutation on all games: win p **0.073**, early-capture loss p 0.013. But we collect **5,069** map crumbs in r201-400 on these maps, 30% of the total (655 on other maps). The bot does not seek crumbs, but moving onto them collects them. See R11 for the cause. |
| S8 | Bank, setup digs and kills per attack are structural | **holds** | Not recomputed beyond lvl.pkl (digs 0 vs 73). |

## Verdicts on R1-R18

| # | verdict | note |
|---|---|---|
| R1 | **wrong on side, weak on map** | **Side does matter now.** Within-map B−A is +5.3 points (perm p **0.029**, 1,507 games). In games 500+: A 186-335 (35.7%), B 208-279 (42.7%). On side A, Gymhgy grabs more (10.0 vs 8.8 a game, t −3.5) and captures more (t −2.6). The "p 0.75" came from the first 499 games only (A 98-148, B 103-150, reproduced). The map effect is not "a little" either: chi2 250 on 77 df, true SD about 0.16 at 19 games a map (lens 1 had SD 0.11 at 6.4 games a map). |
| R2 | **holds descriptively; it cannot show a mechanism** | Captures per chain 0.295 W / 0.410 L reproduce. A loss *is* its captures, so higher conversion in losses is the outcome. The unopposed part is the base rate (S2). |
| R3 | **path term holds; the W/L table is the outcome restated** | The within-game logit holds (pathDist −0.125, z −7.6). The order-of-loss table counts the k-th capture, not the nearest flag. "3rd flag by r600: 0% in wins" is true by definition (three captures end the game), and "the 2nd and 3rd decide" restates the win condition. The census gives the same table: 70/83, 25/50, 0/19%. |
| R4 | **holds, with an outcome-selected half** | P(capture) by group size reproduces: 0.03/0.06 (0-2) up to 0.36/0.54 (12+). "The same group converts 1.5-2x more in losses" compares chains selected on the game's outcome. That is not a mechanism. |
| R5 | **weak** | Every rate is taken from capture chains. A forward-looking check: among chains still open at t10, a chain with no contact in t1-10 converts more when its grab group is 3-11 (0.34-0.56 vs 0.18-0.35). It makes no difference when the group is 12+ (0.57 vs 0.54), and those groups make **48% of its captures** (378 of 795). |
| R6 | **wrong as a W/L separator** | Respawn arrival per game: +0.08 within map (perm p < 0.002). The gap vanishes within each chain type: capture chains −0.01 (p 0.45), returned chains +0.04 (p 0.22). It is composition: losses simply have more capture chains. Stun victims per capture chain reproduce (−5.4, t −4.5). Per 50 chain-rounds the gap is −4.3 (t −2.4), so part of it is chain length. |
| R7 | **holds (medium)** | Within-game association only (z −5.4 ever, −3.8 by r600). Its cause is untested. |
| R8 | holds | |
| R9 | **weak** | traps8 is −0.041 (z −4.0) only in the "captured by r600" model. In the "captured ever" model it is −0.021 (z −1.9), and per side −0.022 (z −1.4) and −0.021 (z −1.3). Lever 5's price (C3: +0.29 log-odds) takes the larger value, so it is uncertain by a factor of 2. |
| R10 | **holds (small)** | Net kills at r250, within map +0.89; per-map t 2.5; perm p 0.028. Faded by r300 (+0.58). |
| R11 | **class holds; the cause is not supported (between-map confound)** | Within the class, our share of the r201-400 crumbs does not track the win rate across the 17 maps (Spearman −0.06, perm p 0.83). Examples: Randy 2% share, 67% wins; StarryNight 3%, 30%; Snake 46%, 12%; Pancakes 47%, 25%; Puzzle 40%, 13%. Within a map, the crumb gap does not separate W from L (t −0.7, 16 maps), while its captures by r600 do (t −5.1). Flag distance is equal on both groups (30.7 vs 31.4). Spawn paths are shorter on centre maps (15.3 vs 17.6), which lens 1 says helps us, so that cannot explain the deficit either. |
| R12 | holds | LEVEL_SUM losses are now 142 of 1,507 (9.4%); near-miss losses 32.8% (142 + 157 of 912). |
| R13 | **numbers hold; W/L contrasts partly tautological** | Re-grab rate 0.204 vs 0.421 and carrier speed 0.446 reproduce. Lens 3's "journeys +5.6 a game in wins (t 4.5)" is game length: per 1,000 rounds it is 16.3 vs 16.2 (t 0.4). |
| R14 | **weak** | The speed jump is an engine rule (carrier cooldown 20 → 12, so 0.5 → 0.83 moves a round). The conversion jump is confounded with the r1800 regime. The one direct test, g2up3, **FAILED** with capturedLate *falling* (0.33 vs 0.44) and kills −2.9 SE. |
| R15 | holds | Wins are 305 rounds longer within map. |
| R16, R17 | holds | Not recomputed. |
| R18 | holds | symWrong 0, overruns 0, exceptions 0 over all 1,507 games. |

## Lever checks: does the signature measure the mechanism?

- **Lever 1 (g4farm). Holds.**
  - **Mechanism.** It checks out in the engine:
    - dig → `incrementSkill(BUILD)`; fill gives no XP;
    - **a trap build also gives build XP** (RCI line 634; RULES.md leaves this out);
    - the jail penalty hits the highest skill (attack on ties), so a heal master's build is safe;
    - passive income is +10 a round, i.e. 5,000 crumbs over r1500-2000.
  - **Signature.** `digsLate` and `levelGain1500` measure the mechanism directly.
  - **Cells.** The chosen cells reach a tied r2000 in 4-9 of 8-13 games each, enough for a 5(a). Tunnels A is no longer a clean control: its 0-0 level-sum record is 3-3.
  - **Overlooked.** In LEVEL_SUM games we build 250-300 traps, yet **35 of 50 ducks end at build 0**, and the end bank is only about 240. The build XP piles onto 1-3 ducks at b6 (the defenders, in the lvl_dump compositions), where everything past 30 XP is wasted. Routing trap builds and digs to ducks with build < 3 is the cheapest dose.
  - **Unverified.** "Byte-identical to r1500" assumes Gymhgy is deterministic under `bc.game.seed`. Prove it with one same-seed g_iter4 pair first. The new indicator counter changes the replay bytes even when the game state is identical.
  - **Ceiling.** 9.4% × 20/26 ≈ 7 points; realistic 2-4.
- **Lever 2 (g4crumb). Weak.**
  - **Premise.** The mechanism is unsupported (R11).
  - **Signature.** `gathered200to400` only shows the arm fired. The effect read must be its captures by r600 and our stuns r201-400 on the class.
  - **Prior.** INCONCLUSIVE against Cyril at 96 cells.
  - **Expected effect.** Cut to 0-1 point. The 5(a) costs no code and stays worth running as a diagnostic, not as a ranked bet.
- **Lever 3 (g4convoy). Plausible, but the signature as specified is weak.**
  - **Right object.** `flagContact20` watches our flag, not their carrier, so it avoids g4z1's mistake.
  - **Outcome-selected target.** I measure 0.36 in capture chains and 0.73-0.79 in returned chains (not 0.89). The per-game baseline is 0.585, so the ×1.3 bar asks for about 0.76, i.e. returned-chain level. Predecessors moved far less: g1icpt chasers +4%, g4pred +8%.
  - **Its W/L gap is composition.** Per game it is +0.083, but within capture chains W and L are equal (0.356 vs 0.361).
  - **DROP_TRACK.** It can only fire when a duck already sees the flag. Its real job is to stop CARRY_PREDICT's A11(a) clear from wiping the track at every hand-off.
  - **Missing history.** The synthesis does not cite g1icpt (FAIL: an alert is fresh only while someone sees the carrier), g4pred (INCONCLUSIVE: unseen rounds unchanged) or g1icamp (not reproduced). INTERCEPT + CARRY_PREDICT together is new and matches the logged re-open condition ("tracking with prediction").
  - **Better signature.**
    - Primary: the screened share falls, i.e. rounds with ours at dist2 21-100 of the flag but none within 20.
    - Contact counted on chains whose grab group was under 12.
    - Effect read: its conversion of chains with a grab group under 12, the only band where contact predicts the outcome.
- **Lever 4 (g4up3). Weak; demote.**
  - **Prior.** Its only gate failed in the wrong direction: capturedLate fell.
  - **Signature.** `carrierSpeedLate` is an engine identity, so it only shows the arm fired.
  - **Price.** HEAL slips to r1800 while Gymhgy has it from r1200 (heals outnumber attacks 1.2-1.7 to 1 for both teams, lens 4).
- **Lever 5 (g4dam). Plausible, small.**
  - **Signature.** `setupFired250` rises by construction, so it only shows the arm fired. The effect read is net kills at r250 plus `enemyCaptured600`.
  - **Price.** Uncertain by a factor of 2 (R9).
  - **Related history.** c4bank, and g2crb (BUDGET_V1), which delivered the bank against Cyril but only +12% stuns.

## Missing alternatives

1. **The side-A deficit is a basics item (CLAUDE rule 15).**
   - **Size.** Side A trails by 5-7 points, and Gymhgy grabs more on side A.
   - **Diagnosis.** Pair A and B on the same map and seed, and diff the opening (spawn order, turn order, symmetry tie-breaks, relocation).
   - **Design.** Every 5(a) should balance sides.
   - **Choosing sides.** The owner now allows choosing sides against benchmarks, and side B is worth about +5 points against Gymhgy. That would select a better rating rather than improve the bot, so it is the owner's call.
2. **Trap-XP routing** for lever 1 (above).
3. **Deny the 12+ group before the grab.** Group size at the grab is the strongest predictor of conversion, and contact does nothing against 12+ groups. Gymhgy needs a median 25 rounds from first sight to first grab (lens 5), and that is the window. History: ALERT_RADIUS2 400 failed twice (g2alert400, d2alert).
4. **Centre-crumb maps: test layout, not crumbs.** Its captures by r600 run 1.62 there vs 1.21 elsewhere, independent of crumb share. Open fields may let 12+ convoys form.
5. **Map heterogeneity is about 1.5x what R1 says** (SD about 0.16), so per-map records now carry information (19 games a map).

## Corrected lever ranking

1. **Lever 1, level farm (g4farm), with a trap-XP routing dose.** The mechanism is verified, the signature is direct and the measurement is clean.
2. **Side-A deficit diagnosis.** A basics check, cheap; worth about 2.5-3.5 points overall if it is fixable.
3. **Lever 3, convoy response.** It addresses the largest loss mode, but delivery is unlikely. Rebuild the signature: screened share, and contact plus conversion on chains with a grab group under 12.
4. **Lever 5, dam-line budget.** A small, real association (R10); the price is uncertain.
5. **Lever 2, centre crumbs.** Run the free 5(a) as a diagnostic only; the crumb mechanism is unsupported.
6. **Lever 4, CAPTURING at r1200.** The prior gate failed with capturedLate down; the natural experiment is confounded.
