# TACTICS.md — what opponents beat us with, and our progress on each

One entry per tactic an opponent has used successfully against us. Each entry records the evidence
(which bots, which games, measured how), our **Adoption** progress (building and using our own version of the
tactic, done first) and our **Neutralization** progress (making the tactic stop working against us, tested
against our own adopted copy). Status words:
`none` -> `sparring archetype` -> `in candidate` -> `accepted (g_iterN)` -> `measured on ladder`.

Order of work per tactic: adopt it first and submit it (it pays immediately and gives us a reproducible
user of the tactic); then neutralize it against our own copy. (Owner, PROMPTS 21: the axis is adoption vs.
neutralization, not offence vs. defence; a setup habit or a trap pattern is adopted or neutralized the same way
a raid is.)

| # | tactic | seen from | Adoption (we use it) | Neutralization (it stops working on us) |
|---|---|---|---|---|
| T1 | **Fast flag raid**: a strike group reaches our flags soon after the dam opens and carries them off, even while losing the fight on kills | hsmalladi.finalbot (DefaultSmall: all three by r383 although we out-killed them 38-18); andli28 (Battlefield r297); andrewgopher (Tunnels r363); most sub-r500 losses of g_iter0 | **measured on ladder, not adopted**: all-out rush (arch_rush) 5/32 vs g_iter1 and 1537 on the field; 10-duck rush squad (arch_rush10) 84/110 = control. Kept as the sparring partner (53% vs g_iter1) | **accepted (g_iter1)**: carrier sightings shared, chase within dist 15, intercept at the carrier's destination, respawn toward it, micro closes on carriers (g_iter1 +246 Elo; sub-r600 losses 26 -> 6). Tested vs arch_rush10 and rejected: 2 defenders per flag (9-25), alert radius x4 (12-31), fortress trap ring (16-29) |
| T2 | **Flags relocated in setup** to edges and corners far from their spawn, often behind water | hsmalladi.finalbot (flags to (22,0),(30,0),(30,8) in round 2) | **measured on ladder, not adopted**: a2reloc 81/110 vs control 84/110; vs arch_rush10 20-21 (neutral). Off in src/bot | none yet: we find relocated flags from broadcast hints and sightings; no measurement of how long our raids take to find them |
| T3 | **Flag relay**: the carrier drops the flag for an adjacent ally, who carries on, so the flag moves faster than a carrier's +20 move cooldown allows | hsmalladi.finalbot, andrewgopher (PLACE/PICKUP pairs every 1-10 rounds), IvanGeffner.kuma | **measured on ladder, not adopted**: a2relay 81/110 vs control 84/110; used inside arch_rush10 | partial (g_iter1): chase and intercept target the flag wherever it moves; not measured separately |
| T4 | **Setup digging** for build XP (level-sum tiebreak, cheaper traps) | hsmalladi.finalbot (60 digs by r175), IvanGeffner.kuma (86) | **measured on ladder, not adopted**: 5 digs/duck 84/110, 10 digs/duck 82/110 vs control 84/110 | not needed while it does not decide games; level-sum losses are few |
| T5 | **Bank through setup, spend on traps in the fight**: top bots hold ~2400 crumbs at r250 (we ~240), build 90 traps by r400 (we 57), and we trigger 53 of theirs to their 32 of ours | block study of the g_iter1 control vs the 14 top bots | **in candidate**: c4bank 80/110 was contaminated by an unintended trap-placement change; clean re-run c5bank queued | none yet |
| T6 | **Attack-heavy fighting**: by r400 top bots attack more and heal less than we do (975 vs 807 attacks; 1005 vs 1282 heals) | same block study | **measured on ladder, not adopted**: advance margin 3->1 (e1aggr) 83/110, plus engage into 2 threats (e2aggr) 81/110, vs control 84/110 on the same cells: no dose response. The attack/heal gap looks like a symptom of how the fights go, not a knob | none yet |

