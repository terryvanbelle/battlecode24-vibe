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

## Working
- Measuring before building: the premise tools answered "can it work against ColtG5?" from 515 replays with no bot change.
- The ColtG5 focus gives a sensitive target: one flag decides most games (102/135 of its wins are 1-flag tiebreaks).

## Not working
- Raids against ColtG5 (5 builds): flags reached, never brought home.
- Bot symmetry guess wrong in 28% of ColtG5 games and 34% of band games: the sensor must fix this (PSYM).
- Band premise fails P3 (destination 0.76): widening beyond ColtG5 will need the tracker revision.

## Open questions for the owner
- (answered, prompt 120: one strategy, all energy on it; the crack against ColtG5.)
