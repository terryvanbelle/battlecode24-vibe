# CRACK-GYMHGY.md: the ladder target since 2026-10-04, Gymhgy.v10official (owner prompt 177)

Opened 2026-10-04 while the CyrilSharma line was running dry (research/CRACK-CYRIL.md): the owner allowed widening the
target list if the lines of attack on Cyril run out. Gymhgy is rated 2099 (rank 9, above Cyril's 2053) and is the bot
above us we do best against. Source never read; games only (scrimmages, random maps and sides).

**Baseline.** g_iter4 vs Gymhgy: 11-13 on the band seeds, 78-103 (43%) over 181 filler games on fresh seeds (Cyril: 36%
over 2,184).

**Profile (survey, census, traces).** Moves its flags in setup, digs in setup, relays its carriers, heals more than it
attacks. Its stun output is level with ours (166 triggered a game to our ~170), unlike Cyril's (454). We first see one
of its flags later than Cyril's (median r301 vs r249) but grab its flags far more often (16 first grabs a game vs 4):
its flag defense leaks, and our carriers die on the way home (16.5 a game). Trace, Canals loss (`--defense`): its
offense hands our flag on every ~2 rounds (27 trips: 22 hand-offs, 3 captures, by r271 and r371) with 0.95 of ours within
dist2 20; its defense puts 5.4 chasers on each of our trips (31 trips, 24 first grabs, 29 carriers dead).

**What separates wins from losses (181 filler games, 78-103).** Losses: CAPTURE 57, LEVEL_SUM 27, MORE_FLAG_CAPTURES 19;
wins: CAPTURE 42, LEVEL_SUM 23, MORE_FLAG_CAPTURES 12. In wins we kill 201 of its ducks on its own territory (73 in
losses) and lose 107 of ours on ours (155); kills 458 vs 285 (within-map P = 0.66); its captures by r600 0.91 vs 1.32
(P 0.34), unopposed captures 1.1 vs 2.0, its unseen carrier-rounds 63 vs 77 (P 0.36). Our grabs barely differ (22 vs 20
first grabs). The game is decided by where the fight happens and by its early unwatched captures, not by our offense.

**5(a), 8 shared cells (seed 777050).** g_iter4 3/8, g4z2 (C.Z2ESCORT, hit a carrier's escorts first) 3/8, g4z1
(C.Z1HOLD, converge on our dropped flag and hit enemies in pickup range) 5/8. g4z2 cuts its re-grabs 4.9 -> 2.9 and changes
nothing else. g4z1: our ducks within dist2 20 of its carrier 1.95 -> 2.95, its captures by r600 1.75 -> 1.12, unseen
carrier-rounds 91 -> 69, games 1,313 -> 1,795 rounds (EvilGrin and KingQuacksCastle are no longer lost early). The
voluntary hand-offs put our flag on the ground every two rounds, so the hold fires along the whole relay chain.

**Next.** g4z1 delivery on the Gymhgy pool, pre-registered: `DGPOOL=Gymhgy.v10official DGTAG=-gym BASE=g_iter4
tools/delivery-gate.sh g4z1 'rel:chasers20>=1.25 nw:kills>=0.95 mean:overruns<=0'`. On a pass, Gymhgy becomes the ladder
target (CLAUDE rule 14) and the filler pairs g4z1 with g_iter4 against it.

**Became the target (2026-10-04)** when the Cyril lines ran out (research/CRACK-CYRIL.md, closing note). Results so far:
g4z1 (Z1HOLD) failed delivery twice (chasers20 -18%; dropGuard +18% against a x2 bar): closed. g4z2 (Z2ESCORT) cut its
re-grabs 41% at 5(a) and nothing else. g4pick at 5(a): our re-grabs x5, captures flat. Next: g4relay (our own relay,
RELAY + PICKUP_AFTER_MOVE) 5(a); the filler builds the g_iter4 baseline.
g4relay (RELAY + PICKUP_AFTER_MOVE) slowed the flag (constant hand-offs); g4relay2 (hand over only when hurt or
threatened) looked strong at 5(a) (captures 2.50 vs 1.62 on 8 cells) but failed delivery at 96 cells (captures 1.68 vs
1.65, -2.0 SE): relay line closed. A loss study (five lenses, synthesis, critic) is running; its levers and chosen-map
cells come next (maps and sides may be chosen for diagnostics since PROMPTS 178).

## Loss study (2026-10-04; research/gymhgy-study-2026-10-04/: five lenses, synthesis, critic)

Over 1,174-1,507 games (critic's frozen census: 1,507). What holds after the critic:
- **It grabs as often in our wins as in our losses** (within map +0.22 a game, t 0.5); losses are decided by conversion
  of its chains. "Unopposed captures" is not evidence: 92% of all its captures are unopposed in a relay (the capturing
  leg is 1-2 rounds at its own spawn), in wins and losses alike.
- **The size of its group at the grab predicts the chain** (capture 0.03-0.06 with 0-2 of its robots near, 0.36-0.54
  with 12+; 12+ groups make 48% of its captures), and contact by our robots matters only for groups under 12.
- **The fight-geography split is game length**: the deathsHome gap builds after r1000; the earliest separator is the
  dam-drop skirmish (net kills at r250, within map +0.89, p 0.028), faded by r300.
- **Level sum**: we lose 0-0 level-sum games 28-48 by a median 17 levels, never dig after setup (Gymhgy digs 73 in
  setup), and 35 of 50 ducks end at build 0 while 250-300 trap builds pile XP onto 1-3 ducks at build 6.
- **Centre-crumb maps** (17 maps with >= 9,000 crumbs behind dams) win 0.31-0.32 vs 0.41-0.43 elsewhere (out of sample),
  but our crumb share does not track the win rate within the class (Spearman -0.06): the cause is not the crumbs.
- **Side**: we win 5.3 points more on side B within map against Gymhgy (p 0.029); against Cyril side A is 2 points
  better and the band shows no consistent side effect, so this looks like Gymhgy's own asymmetry, not ours.
- Our carriers die near its flag, outnumbered 8.3 to 3.4 within dist2 20; 51% with no ally within dist2 8; a grabber
  under 500 HP converts 2.4% (9.3% at 900+). Our re-grab rate after a carrier death is 0.20 (its 0.42-0.44).

Corrected lever ranking (critic): (1) **level farm** (C.LEVEL_FARM, arm g4farm: late digs for build XP when the flag
counts are level; ceiling ~7 points, realistic 2-4), with a trap-XP routing dose; (2) the side-A deficit (checked:
opponent-specific); (3) a convoy response with a signature on contact for grab groups under 12; (4) setup budget to
the dam line; (5) centre crumbs (diagnostic only); (6) CAPTURING at r1200 (demoted: g2up3 failed with captures late
falling). g4farm 5(a) (stalemate maps DefaultSmall, EndAround, GravitationalWaves, Fusbol, Hurricane; control Mountain A;
two seeds, both sides where possible, g_iter4 on the same cells) and g4crumb on the centre-crumb maps are queued, plus a
check that Gymhgy is deterministic under a fixed engine seed.

**First delivered levers (2026-10-05).** g4crumb on the centre-crumb maps: chosen-cell 5(a) 7-1 against g_iter4 on 16 shared
cells; map-class delivery PASS (crumbs r201-400 x1.9, +2.6 SE; kills +27%) after a VOID first block (MEAS3 collisions and
the old guard rule). The filler now pairs it with g_iter4 on random maps (the victory read). g4farm2 (level farm, 5 build
XP per duck after r1500): fires (63 digs, +10 levels late), stalemate-class gate running.
