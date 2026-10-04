# CRACK-GYMHGY.md: scouting Gymhgy.v10official (owner prompt 177)

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

**5(a), 8 shared cells (seed 777050).** g_iter4 3/8, g4z2 (C.Z2ESCORT, hit a carrier's escorts first) 3/8, g4z1
(C.Z1HOLD, converge on our dropped flag and hit enemies in pickup range) 5/8. g4z2 cuts its re-grabs 4.9 -> 2.9 and changes
nothing else. g4z1: our ducks within dist2 20 of its carrier 1.95 -> 2.95, its captures by r600 1.75 -> 1.12, unseen
carrier-rounds 91 -> 69, games 1,313 -> 1,795 rounds (EvilGrin and KingQuacksCastle are no longer lost early). The
voluntary hand-offs put our flag on the ground every two rounds, so the hold fires along the whole relay chain.

**Next.** g4z1 delivery on the Gymhgy pool, pre-registered: `DGPOOL=Gymhgy.v10official DGTAG=-gym BASE=g_iter4
tools/delivery-gate.sh g4z1 'rel:chasers20>=1.25 nw:kills>=0.95 mean:overruns<=0'`. On a pass, Gymhgy becomes the ladder
target (CLAUDE rule 14) and the filler pairs g4z1 with g_iter4 against it.
