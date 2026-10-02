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
