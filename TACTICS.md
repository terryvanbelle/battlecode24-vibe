# TACTICS.md — what opponents beat us with, and our progress on each

One entry per tactic an opponent has used successfully against us. Each entry records the evidence
(which bots, which games, measured how), our **offensive** progress (building our own version, done
first) and our **defensive** progress (neutralising it, tested against our own copy). Status words:
`none` -> `sparring archetype` -> `in candidate` -> `accepted (g_iterN)` -> `measured on ladder`.

Order of work per tactic: copy the offence first and submit it (it pays immediately and gives us a
reproducible attacker); then build the defence against our own copy.

| # | tactic | seen from | offence | defence |
|---|---|---|---|---|
| T1 | **Flag rush after the dam opens**: a strike force reaches our spawn-held flags ~50 rounds after r200 and takes all three while losing the fight on kills | hsmalladi.finalbot (DefaultSmall: all three taken by r383 although we out-killed them 38 to 18) and most sub-r500 losses in calibration | **sparring archetype** `arch_rush` (2026-09-30, owner prompt 6): everyone but the 3 defenders rushes the nearest enemy flag with pushing micro (threat cost 150 vs 1000 when kiting) and relays carriers; v1 without micro lost 759 deaths to 86; v2 beat g_iter1 on DefaultSmall seed 3 (3 captures by r756). Queued: archetype check vs g_iter1 (16 maps x2) and a field block as an offence arm | in candidate: carrier sightings shared (slots 17..19), chase within dist 15, intercept at its destination spawn centre, respawn toward the carrier, micro closes on carriers |
| T2 | **Flags relocated in setup** to map edges/corners far from spawn, ringed by dug water (moat) | hsmalladi.finalbot (flags to (22,0),(30,0),(30,8) at r2) | none | n/a (a defence of theirs; our offence must find flags that are not at spawn: broadcast hints + sightings already used) |
| T3 | **Flag relay**: carrier drops the flag and an adjacent ally picks it up, repeatedly (sidesteps the carrier's +20 move cooldown) | hsmalladi.finalbot (PLACE/PICKUP pairs every 1-10 rounds) | none | chase/intercept (T1) applies |
| T4 | **Checkerboard digging in own territory during setup** (build XP for the level tiebreak; broken ground for attackers) | hsmalladi.finalbot (60 digs by r175, level sum 12 at r175 vs our 0) | none | none |
