# The crack: one unambiguous win over a higher-ranked bot (owner prompt 120, 2026-10-02)

"Pick a strategy and focus all your energies on it. Your immediate goal is to achieve a single, unambiguous victory over
a higher-ranked opponent on the ladder. If you can achieve that, then you can try it more broadly."

## Target: ColtG5.Goob_final (rank 13, Elo 1909; g_iter1 1811, rank 23)
Chosen from g_iter1's record against every bot rated above it (progress/games.csv, all recorded games):
| opponent | Elo | g_iter1 W-L | win % | their wins by full capture |
|---|---|---|---|---|
| ColtG5.Goob_final | 1909 | 85-135 | 38.6 +- 3.3 | 33 of 135 |
| kyleezz.jeeryfix3 | 1881 | 90-130 | 40.9 +- 3.3 | 76 of 130 |
| quesswho.cretplayer2_3 | 1839 | 88-132 | 40.0 +- 3.3 | 105 of 132 |
| winkelmantanner.waffle and above | 2052+ | 20% or less | | |
Why ColtG5: the highest-ranked of the reachable three, and its wins over us are close games: 102 of 135 are the
"captured more flags" tiebreak at r2000 (76% of our losses end at r2000). One flag decides them: one capture fewer for
them, or one more for us, flips the game. Our 85 wins are 49 level-sum tiebreaks, 20 full captures, 16 more-flags.

## Victory criterion (pre-registered; never moved after a number is seen)
A build B achieves the unambiguous victory when, in scrims against ColtG5 with random maps and sides (CLAUDE rule 4):
1. B wins >= 60% of at least 120 games (exact two-sided binomial p < 0.05 against 50%; 72/120 gives p = 0.035), and
2. on the same cells, paired against g_iter1, B's net (gained - lost) is >= +2 SE (sign test p < 0.05).
Both are computed by tools/eval-paired.py-style pairing on identical cells; the ladder Elo of B is reported too.
Then widen: the rung (kyleezz, quesswho, winkelmantanner, CyrilSharma, hsmalladi), then the band with REWRITE_EVAL's bars.

## Method
1. Baseline: g_iter1 vs ColtG5 accumulates in the idle filler (FILLPOOL=ColtG5.Goob_final), 40 games a seed, replays kept
   for study (random maps and sides).
2. Study what decides these games from the replays (allowed: games, never code): when and how each capture happens
   (--defense / --track trips, first grabs vs chains), who leads level sum at r2000, which maps we never win, what they do
   that the band's top bots also do. Pick the narrowest crack with the largest share of flippable games.
3. Arms aimed at that crack only, each through step 5 (diagnostic, then a delivery mini-block against ColtG5), then the
   victory test. Everything else (band-wide rewrite stages) waits unless it is the crack.

## Study log
- 2026-10-02, 128 g_iter1 games vs ColtG5 (VM replays; us / them per game): pickups 2.57 / 11.33 (first grabs 1.68 /
  6.52, re-grabs 0.89 / 4.81); captured 0.65 / 1.51; inEnemy250 7.4 / 25.2; firstFlagSight r340 / r224; kills 994 / 391;
  meanAlive 44.8 / 36.7; gathered400 5007 / 7742. ColtG5 pours its army into our half from the dam drop, loses the
  fights 2.5:1, but grabs our flags again and again and converts 1-2, usually by r250-580; then the game freezes and
  it wins the more-flags tiebreak at r2000. Every tied game we won on level sum. Its own half is thin.
- Candidate cracks: (A) offence: a counter-raid while their army is in our half (+1 capture flips 0-1 / 1-2 losses into
  level-sum wins); (B) defence: stop the one early capture. A is untested against this opponent (rush and flank squads
  were closed band-wide, which says little about an all-in aggressor).
- Screen 1 (not a gate; chooses the crack): g_iter1, arch_rush10, arch_rush20, fl6, fl10, fl15 vs ColtG5, 40 games
  each, SEED 777001 (identical random cells), census per run.
- Screen 1 result (37 identical cells each vs ColtG5; wins / captures ours-theirs / our pickups): g_iter1 14 / 0.68-1.65 /
  2.1; arch_rush10 12 / 0.65-1.68 / 43.4; arch_rush20 12 / 0.81-1.65 / 63.5; fl6 12 / 0.46-1.62; fl10 12 / 0.57-1.57;
  fl15 13 / 0.45-1.47. Every raid build reaches their flags (pickups x12-30) and none converts: carriers die on the way
  home; their respawners appear on top of their flags. **Crack A (counter-raid) closed** for ColtG5.
- Their trips on our flags (165 g_iter1 games vs ColtG5, --defense; research/crack/colt-trips-g_iter1.csv): 1953 trips,
  254 captured (13%). Captured trips: 41% first grabs, median 38 rounds carried, **56% with on average under 0.5 of our
  ducks within dist2 20 of the carrier**; at the pickup a median of 4 of ours are within dist2 20 (they see it). Trips of
  15+ rounds by mean chasers: < 0.5: 145, 82% captured; 0.5-1.5: 120, 48%; 1.5-3: 60, 25%; 3+: 37, 32%.
  Our ducks see the grab, go back to fighting ColtG5's flood, and the carrier walks home alone.
- **Crack chosen: B, stop the carrier that walks home**, i.e. the CUT stage of research/REWRITE_DESIGN.md (shared,
  predicted flag track; a few committed responders on the predicted path), aimed at ColtG5. Stages as designed (S0a
  premise, S0b sensor, S1 CUT), with these changes for the crack: the premise and the delivery blocks use ColtG5 games
  (scrims, random maps and sides); the decision is this file's victory criterion, not the band bars.
