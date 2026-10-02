# Ladder

6709 scrimmages (ours only), 6709 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| arch_rush10 | 1894 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.4% (vs 13) |
| a3dig5 | 1894 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.4% (vs 13) |
| g_iter1_c2 | 1894 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.4% (vs 13) |
| e1aggr | 1868 +- 131 | 18 of 71 | 110 | 83-27 | 74.7% | 16.2% (vs 14) |
| c5bank | 1842 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c6pair | 1842 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| a3dig10 | 1842 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| b1z2b | 1834 +- 37 | 23 of 71 | 680 | 248-432 | 73.6% | 16.2% (vs 15) |
| g_iter1 | 1821 +- 17 | 24 of 71 | 3430 | 1257-2173 | 73.1% | 15.3% (vs 15) |
| a2relay | 1817 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| e2aggr | 1817 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| a2reloc | 1817 +- 130 | 27 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| b1v2 | 1784 +- 30 | 29 of 71 | 1080 | 356-724 | 71.8% | 15.1% (vs 16) |
| b2fs | 1750 +- 72 | 30 of 71 | 200 | 62-138 | 70.6% | 13.1% (vs 16) |
| arch_rush | 1456 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.0% (vs 19) |
| g_iter0 | 1390 +- 89 | 36 of 71 | 109 | 56-53 | 51.9% | 8.2% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2484 | 140 | 290 | 285-5 | 0% (b1v2 0-54) |
| 2 | jmerle.camel_case_v21_final | 2412 | 115 | 290 | 282-8 | 0% (b1v2 0-54) |
| 3 | uravt.Version18Final | 2386 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2332 | 93 | 290 | 277-13 | 4% (b1v2 2-52) |
| 5 | chenyx512.flagbot_final | 2320 | 90 | 290 | 276-14 | 4% (b1v2 2-52) |
| 6 | andli28.v9_USQuals_angle | 2286 | 83 | 290 | 273-17 | 6% (b1v2 3-51) |
| 7 | NotLLeon.v3 | 2257 | 77 | 290 | 270-20 | 7% (b1v2 4-50) |
| 8 | Gymhgy.v10official | 2217 | 70 | 290 | 265-25 | 4% (b1v2 2-52) |
| 9 | andrewgopher.player22 | 2203 | 68 | 290 | 263-27 | 7% (b1v2 4-50) |
| 10 | hsmalladi.finalbot | 2118 | 56 | 290 | 248-42 | 13% (b1v2 7-47) |
| 11 | CyrilSharma.finalBot | 2086 | 53 | 290 | 241-49 | 13% (b1v2 7-47) |
| 12 | winkelmantanner.waffle | 2062 | 51 | 290 | 235-55 | 15% (b1v2 8-46) |
| 13 | ColtG5.Goob_final | 1901 | 41 | 290 | 182-108 | 31% (b1v2 17-37) |
| 14 | **us:arch_rush10** | 1894 | 132 | 110 | 84-26 |  |
| 15 | **us:a3dig5** | 1894 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1894 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1888 | 41 | 290 | 177-113 | 33% (b1v2 18-36) |
| 18 | **us:e1aggr** | 1868 | 131 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1858 | 41 | 290 | 165-125 | 46% (b1v2 25-29) |
| 20 | **us:c5bank** | 1842 | 131 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1842 | 131 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1842 | 131 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1834 | 37 | 680 | 248-432 |  |
| 24 | **us:g_iter1** | 1821 | 17 | 3430 | 1257-2173 |  |
| 25 | **us:a2relay** | 1817 | 130 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1817 | 130 | 110 | 81-29 |  |
| 27 | **us:a2reloc** | 1817 | 130 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1809 | 40 | 290 | 145-145 | 50% (b1v2 27-27) |
| 29 | **us:b1v2** | 1784 | 30 | 1080 | 356-724 |  |
| 30 | **us:b2fs** | 1750 | 72 | 200 | 62-138 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1663 | 44 | 290 | 88-202 | 65% (b1v2 35-19) |
| 32 | clbarrell.duck8 | 1594 | 48 | 290 | 66-224 | 81% (b1v2 44-10) |
| 33 | jonters.bling3 | 1471 | 60 | 290 | 37-253 | 89% (b1v2 48-6) |
| 34 | **us:arch_rush** | 1456 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1391 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1390 | 89 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1333 | 82 | 290 | 18-272 | 91% (b1v2 49-5) |
| 38 | Metta-AI.bc24scenario | 1323 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1323 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1323 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1234 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1234 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1234 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1234 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1234 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1234 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1234 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1234 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1234 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1234 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1234 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1234 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1128 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1096 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1096 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1096 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1096 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1096 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1096 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1096 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1096 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1096 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1096 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1096 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1096 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1096 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1096 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1096 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1096 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1096 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1088 | 155 | 290 | 4-286 | 100% (b1v2 54-0) |
