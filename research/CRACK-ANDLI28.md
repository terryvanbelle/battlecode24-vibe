# CRACK-ANDLI28: the ladder target from 2026-10-06 to the shutdown of 2026-10-07

**Target:** andli28.v9_USQuals_angle, 2174 (rank 5 of 86), the bot just above g_iter7 (2150 +- 29) once the control settled
its rating (CLAUDE rule 14). Starting record: g_iter7 14-34 (29%) over 48 band games.
Final (2026-10-07, shutdown): **not cracked**. andli28 2187 +- 25 (rank 5 of 88), g_iter7 2113 +- 25 (rank 6); g_iter7
1739-4109 against it (30%). The 2174 / 2150 above are the 2026-10-06 starting point.

**Victory read (pre-registered, as for the earlier targets):** a build passes andli28 on the ladder when its rating exceeds
andli28's on the converged fit; the paired filler (candidate vs incumbent against andli28, random maps and sides) reports the
matchup effect.

**What we know already** (g_iter6 control census and the upper-tier micro study, research/upper-tier-micro-2026-10-06):
- It heals under threat 9% of the time (ours 45% before g_iter7) and holds a ready strike 0.41 (ours 0.16): the tempo gap
  g_iter7's heal hold closes.
- It digs in setup (TACTICS measured table), banks crumbs at r250, is stun-heavy and uses water traps; its flags sit 26 tiles
  from our spawns (ours 33 from its).
- g_iter6 won 28% of 36 control games against it; its captures 1.89 a game.

**Next:** the filler builds g_iter7's baseline against andli28; g7spawn (spawn safety) is in its 5(a); a loss study of
g_iter7 against andli28 once ~200 filler games exist.
Status 2026-10-07 (shutdown): all done or stopped. g7spawn's 5(a) fired, then its band delivery FAILED and it was closed;
the loss study ran on 1,520 games (below); the filler was stopped at shutdown (parked as queue/filler.job.stopped on the VM).

## Loss study (2026-10-06, research/andli28-study-2026-10-06/)

g_iter7 vs andli28 over 1,520 filler games (29.5%): the fight trade decides (kill share 0.37 in every phase). Our mid-HP robots
step into reach 40% of the time one step from an enemy (andli28 15%) and our step-in strikes kill half as often; the level-sum
gap is heal XP and jail losses that follow from the fight. Levers being built: FINAL_COMPLETE (finish partial build levels in
the last 50 rounds of a tied game) and ENGAGE_HP (no step-in below ~600 HP unless it kills). Refuted: a late all-in assault.
Status 2026-10-07: both were built and parked (ENGAGE_HP at 700 HP, not ~600). RING_POST_RESERVE (g7ring) failed its 5(a);
FOCUS was refuted by the upper-tier study.

**5(a) results (2026-10-06).** g7ehp (ENGAGE_HP 700): mid-HP step-ins 0.66 -> 0.24 of decisions, deaths -32%, kill share +0.040
(10 of 12 cells), captures level. g7fc (FINAL_COMPLETE): in the 17 games level at r1950, 20 digs a game, level gap +9.5, 4 wins vs
2. Both go to the andli28 deliveries and the band tests.
Outcome: g7fc failed its andli28 delivery (digsLate 3.7 vs bar 4) and its band delivery (1.9 vs 2): closed as a standalone
arm, no band test, a stack candidate. g7ehp passed its andli28 delivery (48 cells) and its band delivery (192 cells), then
stopped at band look 1 (net +2, t_all -1.12). g7ehp2 ended INCONCLUSIVE at its 192-cell band delivery (no band test).

**Matchup read (2026-10-07).** g7ehp (step-in gate) vs g_iter7 against andli28: +24 over 200 paired filler games (+3.2 SE), ~+12
win points. Band-neutral to slightly negative (look 1 capture t -1.12), so not promotable under the shipping rule; the holding
variant g7ehp2 raised enemy captures on the band. The open question is a band-safe step-in gate. The 240-pair read
completed at net +28 (49-21, +3.3 SE).

## 2026-10-07: upper-tier study, kite arms, vertical maps, shutdown

- The level-sum levers failed: g7dig (setup digs) and g7bank (late bank) failed their 5(a); g7ring (ring rebuilds yield
  crumbs to field stuns) failed its 5(a) too.
- **Upper-tier study of g_iter7** (research/upper-tier-study-2026-10-07, 720-game census): contact is twice as lethal for
  us at equal local numbers (2-round hazard 0.082 vs 0.040). Two causes: enemy stun volume (439 triggers a game on us vs our
  200) and tempo while not ready. After a strike our robots stay in enemy reach 0.252 of the time vs the upper tier's 0.111,
  a kite-score defect (TACTICS T19). Target choice and focus fire are at parity.
- **g7kite** (leave reach below 700 HP): 5(a) FIRES, band delivery PASS (48 cells), band pooled 480 pairs t_all 0.32, net +3:
  parked. Against andli28, 240 paired games: net +6 (+0.8 SE), no matchup effect.
- **g7kiterc** (g7kite + C.RC_BAND 150, a supported one-step hold at HP >= 700): diagnostic PASS; its 12-cell 5(a) FIRES on
  every pre-registered bar (8 wins to g_iter7's 6 on those cells). Its band delivery and band test are registered
  (TRAINING_LOG) but were never run.
- **Vertical-symmetry penalty, the top open lead.** Out of sample, on 4,200 g_iter7 filler games after the loss study's
  1,520: win rate vertical 0.187 (1,608 games), rotational 0.383 (1,724), horizontal 0.338 (868). Against the band vertical
  is not worse (0.753 vs 0.702 over 684 control games). On vertical maps against andli28 only, our offence collapses (first
  grabs -2.7 a game, our kills on its territory 119 -> 72). Map table: research/vertical-study-2026-10-07/maps.txt. The
  four-lens study was stopped at shutdown before any lens finished; the mechanism is unidentified.
- **End (shutdown, PROMPTS 192):** not cracked. andli28 2187, g_iter7 2113; g_iter7 1739-4109 against it (30%).
