# Briefing for the morning discussion (owner prompt 115): the new approach, what's working and what's not

Updated at every task check. Protocol: research/REWRITE_EVAL.md; numbers: progress/REWRITE.md; design:
research/REWRITE_DESIGN.md (when the design panel returns).

## Status
- 2026-10-02 13:45 UTC: design panel running (four angles, three judges, synthesis). Evaluation protocol and tools in
  place (tools/eval-paired.py with all / upper / rest and a descriptive rung slice). g_iter1 control on the
  confirmation seeds queued. No rewrite code yet.
- 14:15 UTC: four designs written, judges scoring. g_iter1 confirmation seed 717171 done, 818181 running.
  Archetype partners can start from builds we already have, each an early, weak copy of one enemy component:
  g1escrg (convoy with re-grabs), arch_rush / arch_rush10 (early raid), g1drift80 (ground push). Rejected as our
  strategy, useful as sparring opponents.
- 14:40 UTC: design done (research/REWRITE_DESIGN.md): persistent per-flag tracks predicted toward their spawn, a capped
  auction of free or jailed responders that cut the predicted path; every other duck plays g_iter1. Stage S0a (premise
  from g_iter1 band replays, no bot code) being implemented by a workflow with three-lens review. g_iter1 control on the
  confirmation seeds finished. Replays for the premise are on the VM (2 pinned runs; 3 more for a descriptive check).

- 16:00 UTC: **focus changed (owner prompt 120)**: one strategy, one goal: an unambiguous win over a higher-ranked bot.
  Target ColtG5.Goob_final (rank 13, Elo 1909; g_iter1 38.6% over 220 games). Plan and pre-registered criterion:
  research/CRACK.md (>= 60% of >= 120 scrims, random maps/sides, and paired net >= +2 SE vs g_iter1). ColtG5 profile:
  it floods our half (25 ducks at r250 vs our 7 in theirs), loses fights 2.5:1, grabs our flags 6.5 times a game and
  converts 1-2 early; games then freeze to r2000 and one flag decides them. Screen 1 queued: existing rush/flank builds
  vs ColtG5 on identical cells. The filler now builds the g_iter1 vs ColtG5 baseline. Band-wide rewrite paused.

- 18:00 UTC: crack screen: rush/flank raids reach ColtG5's flags (pickups x12-30) but never convert: closed. Trip study:
  56% of ColtG5's captured trips walk home with none of ours near; with 1.5-3 chasers its capture rate drops 82% -> 25%.
  Crack = CUT (intercept the carrier walking home). S0a premise on 515 g_iter1-vs-ColtG5 games: all bars pass.
  S0b (sensor) being implemented; then S1 (responders) against ColtG5.

- 00:00 UTC (Oct 3): **basics overhaul** after the owner's symmetry question. (1) Symmetry repaired: decided by
  observation (walls, spawn zones, setup dam, enemy flag ids, scouts), never guessed; 0 overruns; head-to-head vs
  g_iter1 63-57 and band neutral, because g_iter1 barely uses symmetry. (2) A correctness audit (8 agents, 41 of 42
  findings confirmed) found basic defects; the biggest are confirmed in our own replays and fixed behind switches:
  A1 the own-flag alert was on ~all game, switching off defenders and parking ducks on the flag tile for up to 1466
  rounds (fixed: false alerts 305-2324 -> 0-81, parking -> 1-121 rounds); A5/A6 stale enemy-flag registry (up to 1393
  rounds a flag marked "carried by us" -> 15). A2, A4, A7/A9 fixed and unit-tested, diagnostics running. (3) The audit
  also found our paired tests never shared the engine seed: identical code flipped 15-29% of games, so most past
  verdicts were noise-bound. Fixed: g_iter1 vs an exact copy now differs in 0 of 80 paired games. (4) Running: the
  combined build g1basics (all fixes) through its gate and a seeded band test; ColtG5 fillers pair it with g_iter1.

- 01:15 UTC: **the basics moved the needle.** g1basics vs g_iter1 on exact seeded band pairs: wins 78 -> 111 of 234
  (+33, p < 0.001); upper tier capture difference +0.34 +- 0.12 (+2.8 SE); rung 15 -> 25. Both pre-registered criteria
  met; confirmation seeds running. Against ColtG5: 57.5% wins (g_iter1 35%), paired +18 of 80 (+3.1 SE); the
  "unambiguous victory" needs >= 60% over >= 120 games -- close.

- 02:15 UTC: **confirmed on fresh seeds (+29, p < 0.001; upper tier +3.8 SE) and promoted: g_iter2.** Ladder: 1918, rank
  13 of 74 (g_iter1 1762, rank 28), now above ColtG5. ColtG5: 71/120 = 59.2%, one win short of the 60% bar; final read at
  240 games, pre-registered.

- 05:45 UTC: **ColtG5 declared defeated by the owner (prompt 157); new target winkelmantanner.waffle** (1997, rank 12;
  g_iter2 10-14, every loss by three flags). Its game, from 24 replays (research/CRACK-WAFFLE.md): it loses the fight
  1:2 but relays our flag home by re-grabbing it the round after each carrier dies (87% of drops in our losses, 77% in
  our wins). Our flags near its spawn fall first (< 20 tiles: 88%, median r358; 28-36 tiles: 50%). First crack:
  g2reloc, flags moved in setup far from its nearest spawn under every live symmetry (iteration 2's relocation used
  the broken guess). Re-test of discarded arms on the fixed base (prompt 150), so far: 5 delivery failures, 1 with no
  premise, 1 closed after a second failure (an audit bug, A11(a), fixed on the way); one bytecode fix folded in.

- 07:10 UTC: waffle, first cracks. Relocation (g2reloc) delivered flags 18% farther from its spawns but cut its captures
  only 9% (bar 15%; wins 8 vs 5 of 24, not significant) and a bigger walk bound did not raise the dose: parked as a
  modest lever. Rushers (g2rush10) add no presence in its half: parked. Now on the waffle pool: escort-first targeting
  (breaks the re-grab chain; fires on all 6 diagnostic maps) and a wider alert net (masses on the threatened flag).
  Also today: the cheap reachability search folded into g_iter2 (identical play, less bytecode); g_iter2 vs waffle is
  96-128 (43%) over 224 games, ladder 1915 (rank 14), waffle 1977 directly above.

- 08:30 UTC: **a crack in waffle.** Its relay chain cannot be denied (1-9 escorts beside every drop), but it can be
  slowed: a duck next to a carrier of our flag builds a stun beside it, ahead on its way home, and a frozen carrier
  cannot move. Alone (g2cstun) it won 13 discordant pairs to 1 over two blocks but missed its capture bars; combined
  with flag relocation (g2cr) it passed delivery on a fresh seed: waffle's captures by r600 -20%, carriers caught x4.6,
  wins 12 vs 8 of 22. Now the decisive paired test: g2cr vs g_iter2 against waffle on fresh seeds toward 240 games
  (victory read >= 60%, p < 0.05), plus the band test so nothing regresses elsewhere.

- 10:30 UTC: **g_iter3 promoted; waffle beaten on the ladder.** g2cr confirmed: pooled over 473 seeded band pairs vs
  g_iter2, net +22 (p 0.017) and capture difference +0.25 per game (t 3.8), basics PASS. Against waffle on the filler
  95-65 (59%) where g_iter2 went 73-87 on the same games. Ladder: g_iter3 1977 (rank 12) above waffle 1964. Next
  target: CyrilSharma.finalBot (2020; g_iter3 10-14).

- 10:45 UTC: **unambiguous victory over waffle** (pre-registered read: >= 60% over 240 games, p < 0.05, paired net >= +2
  SE): g_iter3 146-94 (60.8%, p = 0.001) where g_iter2 went 108-132 on the same games. On to CyrilSharma: a setup bank
  (g2crb) did not help there (wins 9 vs 12 of 24); g_iter3 itself went 12-12 on that block.

- 17:15 UTC: **CyrilSharma not cracked; second audit started.** g_iter3 wins ~30% against it. Five lines came out near
  neutral (setup bank, two defenders, convoy, destination camp, camp split over near spawns). A replay study (five
  lenses, critic) showed its offense decides games (no Cyril capture -> we win 84%), with relays and long unwatched
  carries. Following the new AUDIT_PLAYBOOK (a plateau across several areas is its first trigger), a second correctness
  audit of g_iter3 and the tools is running.

- 00:15 UTC (Oct 4): **second audit paid off: g_iter4 promoted.** 56 findings, all reproduced. The tool fixes came first
  (delivery checks are now three-way: three of the five Cyril "closures" were coin flips inside 1 SE). The first bot fix,
  recognising our captured flags (their defender, alerts and respawns kept serving an empty home in 82% of Cyril games),
  passed delivery and the band test and its confirmation: upper-tier capture difference +0.24 per game (t 3.8). The
  defender tether failed with power and is closed. Next: relocation abandoning ~20% of flags, pickup timing, economy.

## Working
- The audit as a method: five lenses plus adversarial verifiers found defects that ~50 experiments never could.
- Seeded pairing: exact paired tests (0 of 80 discordant for identical code), so small effects are now measurable.
- Basics battery and contract columns (symWrong, symDecidedRound, alertNoThreat, maxParkOnHome, efStale*, exceptions) in
  every census; dead-code and arm-intent checks in the unit tests.
- Measuring before building: the premise tools answered "can it work against ColtG5?" from 515 replays with no bot change.
- The ColtG5 focus gives a sensitive target: one flag decides most games (102/135 of its wins are 1-flag tiebreaks).

## Not working
- Most of yesterday's arms were judged with unseeded pairs: their verdicts were mostly engine noise (re-test candidates).
- Process slips today, each caught: a switch silently left off in a test arm (now an arm-intent test), commits before the
  test suite finished (twice), a mis-paired evaluation from empty run names.
- g1sym on ColtG5 (seeded, 120 pairs): 2 gained, 7 lost (n.s.); the three scouts may cost fights while undecided.
- Raids against ColtG5 (5 builds): flags reached, never brought home.
- Bot symmetry guess wrong in 28% of ColtG5 games and 34% of band games: the sensor must fix this (PSYM).
- Band premise fails P3 (destination 0.76): widening beyond ColtG5 will need the tracker revision.

- Re-testing discarded arms on the fixed base: none of the seven delivered its pre-registered mechanism so far; the
  bugs were real but these ideas were not what held us back.

## Open questions for the owner
- (answered, prompt 168: cracking the target overrides the relative basics checks such as kill/death; the absolute
  bars, which are bugs, still stop an arm.)
- (Superseded by g_iter3, which carries the carrier stun, but the principle stands.) **Carrier stun vs waffle (g2cstun): delivery bar or wins?** Freezing waffle's carriers (stun built beside a carrier of
  our flag) delivered its mechanism in both blocks (carriers caught x3) and won 13 discordant pairs to 1 over two blocks
  (p ~ 0.002), but missed both pre-registered capture bars (waffle captures -7% and -11% by r600; bars -15% / -20%).
  TRAINING_ALGORITHM §3.5 says a failed delivery is never judged on wins, so it is not delivered and the paired filler
  test is not running. Should delivery for a mechanism like this be judged on its own signature (frozen carriers)
  instead of a downstream outcome? Meanwhile: g2cr (carrier stun + relocation) aims at the same capture bar.
- Once g1basics is measured: adopt it as the new base (g_iter2) if it is non-inferior, even without a clear gain,
  because it is correct where g_iter1 is broken? Then re-test the most promising old arms on it with seeded pairs.
- (answered, prompt 120: one strategy, all energy on it; the crack against ColtG5.)
