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
| T1 | **Fast flag raid**: a strike group reaches our flags soon after the dam opens and carries them off, even while losing the fight on kills | hsmalladi.finalbot (DefaultSmall: all three by r383 although we out-killed them 38-18); andli28 (Battlefield r297); andrewgopher (Tunnels r363); most sub-r500 losses of g_iter0 | **measured on ladder, not adopted**: all-out rush (arch_rush) 5/32 vs g_iter1 and 1537 on the field; 10-duck rush squad (arch_rush10) 84/110 = control. Kept as the sparring partner (53% vs g_iter1) | **accepted (g_iter1)**: carrier sightings shared, chase within dist 15, intercept at the carrier's destination, respawn toward it, micro closes on carriers (g_iter1 +246 Elo; sub-r600 losses 26 -> 6). Tested vs arch_rush10 and rejected: 2 defenders per flag (9-25), alert radius x4 (12-31), fortress trap ring (16-29). **In candidate**: a stun trap on the flag tile itself (it fires at the moment of the grab: pickup range = stun trigger range), gate queued. Survey: top bots pick up our flags 17 times a game to our 3 |
| T2 | **Flags relocated in setup** to edges and corners far from their spawn, often behind water | hsmalladi.finalbot (flags to (22,0),(30,0),(30,8) in round 2) | **measured on ladder, not adopted**: a2reloc 81/110 vs control 84/110; vs arch_rush10 20-21 (neutral). Off in src/bot | none yet: we find relocated flags from broadcast hints and sightings; no measurement of how long our raids take to find them |
| T3 | **Flag relay**: the carrier drops the flag for an adjacent ally, who carries on, so the flag moves faster than a carrier's +20 move cooldown allows | hsmalladi.finalbot, andrewgopher (PLACE/PICKUP pairs every 1-10 rounds), IvanGeffner.kuma | **measured on ladder, not adopted**: a2relay 81/110 vs control 84/110; used inside arch_rush10 | partial (g_iter1): chase and intercept target the flag wherever it moves; not measured separately |
| T4 | **Setup digging** for build XP (level-sum tiebreak, cheaper traps) | hsmalladi.finalbot (60 digs by r175), IvanGeffner.kuma (86) | **measured on ladder, not adopted**: 5 digs/duck 84/110, 10 digs/duck 82/110 vs control 84/110 | not needed while it does not decide games; level-sum losses are few |
| T5 | **Bank through setup, trap in the fight**: bots that beat us build ~0 traps in setup (0-6; we 13-53), hold 1000-6650 crumbs at r250 (we ~250), then out-trap us in fights (65-144 stun by r400); 11 of the 16 bots that beat us bank, none of them traps in setup | survey (SURVEY.md: kuma, NotLLeon, Strequals, andli28, andrewgopher, CyrilSharma, jmerle ...) | **in candidate**: banking alone (c5bank: no dam traps, flag rings only above a 1000 bank, fills keep 500) 82/110 vs control 84: neutral, because the combat-trap rule does not spend the bank. Jointly necessary pair c6pair (bank + combat stun from 1 visible enemy, no reserve) 82/110: neutral too (4 gained, 6 lost); on water-heavy maps setup fills still drain the bank | none yet |
| T6 | **Attack-heavy fighting**: by r400 top bots attack more and heal less than we do (975 vs 807 attacks; 1005 vs 1282 heals) | same block study | **measured on ladder, not adopted**: advance margin 3->1 (e1aggr) 83/110, plus engage into 2 threats (e2aggr) 81/110, vs control 84/110 on the same cells: no dose response. The attack/heal gap looks like a symptom of how the fights go, not a knob | none yet |
| T10 | **Centre crumb windfall, banked**: sweep the crumbs the dam was hiding in the first ~25 rounds after it drops and bank them (NotLLeon: 2590 -> 17950 crumbs r200-r225 on HungerGames), then out-trap the opponent for the rest of the fight (148 stun by r350 vs our 47; we hit 130) | NotLLeon.v3 (traced); survey bank at r250: NotLLeon 5775, uravt 6650, andli28 4158, CyrilSharma 3946 vs our ~250 | **in candidate**: idle ducks detour to visible crumbs after setup (t10crumbs, band queued). We already collect the centre by walking over it but spend it at once | none yet |
| T7 | **Water traps** (dig a radius-3 moat under an attacker's feet) | survey: 11 opponents, 5 that beat us (andli28, hsmalladi, uravt, SampleProvider, quesswho) | **measured on ladder, not adopted**: water trap instead of stun when outnumbered (t7water) 85/110 vs control 84 on the field; on the rating band over 4 seeds 175/480 vs 165/480 (43 gained, 33 lost; p ~ 0.24 with the field block): lean positive, not significant; kept off, candidate to stack | none yet |
| T8 | **Sustain fighting (heal-heavy)**: by r400 they heal more than they attack | survey: 31 opponents, 10 of the 16 that beat us (kuma, NotLLeon, Strequals, CyrilSharma, Gymhgy ...) | **in candidate**: retreat-to-heal threshold dose: 500 -> 82/110, 700 -> 80/110 vs 300 -> 84 (monotone, wrong way for 'sustain'); 150 -> 83, 0 (never retreat) -> 85: flat at or below 300, worse above it. Never-retreat on the rating band: 82/240 vs control 84/240 (17 gained, 19 lost): closed. Our heals trail our attacks by r400 (796 vs 974) | none yet |
| T9 | **Flag guards**: 6+ robots within dist2 20 of their flags at r300 | survey: 29 opponents, 3 that beat us (ColtG5, CyrilSharma, NotLLeon) | **measured, not adopted**: 2 defenders per flag lost 9-25 to the rush partner | n/a |

<!-- SURVEY:start (regenerated by tools/tactics-survey.py after every ladder block; edit the table above, not this) -->
## Measured: which opponents use which tactic

From 1539 games against 55 opponents. "Beat us" = opponents we win at most half the games against (16). "We do it" = our own median meets the same threshold. Details per opponent: `progress/SURVEY.md`.

| tactic | opponents showing it | of which beat us | we do it | examples among those that beat us |
|---|---|---|---|---|
| moves flags in setup | 15 | 6 | no | Gymhgy.v10official, SampleProvider.TSPAARKSPRINT1, chenyx512.flagbot_final, hsmalladi.finalbot, quesswho.cretplayer2_3, uravt.Version18Final |
| digs in setup (>=20) | 10 | 8 | no | CyrilSharma.finalBot, Gymhgy.v10official, IvanGeffner.kuma, Strequals.duck0127v5, andli28.v9_USQuals_angle, chenyx512.flagbot_final |
| traps in setup (>=15) | 18 | 0 | yes |  |
| banks crumbs at r250 (>=1000) | 25 | 11 | no | CyrilSharma.finalBot, IvanGeffner.kuma, NotLLeon.v3, Strequals.duck0127v5, andli28.v9_USQuals_angle, andrewgopher.player22 |
| explosive-heavy (>=10 by r400) | 23 | 0 | yes |  |
| water traps (>=3 by r400) | 11 | 5 | no | SampleProvider.TSPAARKSPRINT1, andli28.v9_USQuals_angle, hsmalladi.finalbot, quesswho.cretplayer2_3, uravt.Version18Final |
| stun-heavy (>=60 by r400) | 13 | 10 | yes | CyrilSharma.finalBot, IvanGeffner.kuma, NotLLeon.v3, Strequals.duck0127v5, andli28.v9_USQuals_angle, andrewgopher.player22 |
| fast raid (first pickup <= r260) | 18 | 6 | yes | ColtG5.Goob_final, jmerle.camel_case_v21_final, kyleezz.jeeryfix3, quesswho.cretplayer2_3, uravt.Version18Final, winkelmantanner.waffle |
| relays carriers (drops >= 3) | 7 | 4 | no | Gymhgy.v10official, IvanGeffner.kuma, NotLLeon.v3, hsmalladi.finalbot |
| guards flags (>=6 near at r300) | 30 | 4 | no | ColtG5.Goob_final, CyrilSharma.finalBot, NotLLeon.v3, kyleezz.jeeryfix3 |
| heals more than it attacks by r400 | 32 | 11 | no | CyrilSharma.finalBot, Gymhgy.v10official, IvanGeffner.kuma, NotLLeon.v3, SampleProvider.TSPAARKSPRINT1, Strequals.duck0127v5 |
<!-- SURVEY:end -->
