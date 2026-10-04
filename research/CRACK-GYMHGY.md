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
