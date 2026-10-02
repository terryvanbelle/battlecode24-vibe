# Ladder

9149 scrimmages (ours only), 9149 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1888 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| a3dig5 | 1888 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| arch_rush10 | 1888 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| e1aggr | 1862 +- 131 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| c5bank | 1836 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c6pair | 1836 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| a3dig10 | 1836 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| g_iter1 | 1814 +- 16 | 23 of 71 | 4030 | 1469-2561 | 73.1% | 15.2% (vs 15) |
| a2relay | 1811 +- 130 | 24 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2reloc | 1811 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| e2aggr | 1811 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1z2b | 1804 +- 27 | 28 of 71 | 1320 | 459-861 | 72.7% | 16.8% (vs 16) |
| b1v2 | 1784 +- 24 | 29 of 71 | 1680 | 562-1118 | 72.0% | 15.5% (vs 16) |
| b2fs | 1781 +- 35 | 30 of 71 | 800 | 266-534 | 71.9% | 15.3% (vs 16) |
| arch_rush | 1451 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1385 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2413 | 100 | 412 | 401-11 | 5% (b1z2b 3-63) |
| 2 | jmerle.camel_case_v21_final | 2398 | 96 | 412 | 400-12 | 2% (b1z2b 1-65) |
| 3 | uravt.Version18Final | 2380 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2373 | 90 | 412 | 398-14 | 3% (b1z2b 2-64) |
| 5 | NotLLeon.v3 | 2259 | 67 | 412 | 385-27 | 6% (b1z2b 4-62) |
| 6 | andli28.v9_USQuals_angle | 2252 | 66 | 412 | 384-28 | 12% (b1z2b 8-58) |
| 7 | chenyx512.flagbot_final | 2246 | 65 | 412 | 383-29 | 8% (b1z2b 5-61) |
| 8 | andrewgopher.player22 | 2228 | 62 | 412 | 380-32 | 9% (b1z2b 6-60) |
| 9 | Gymhgy.v10official | 2217 | 60 | 412 | 378-34 | 6% (b1z2b 4-62) |
| 10 | hsmalladi.finalbot | 2111 | 48 | 412 | 353-59 | 9% (b1z2b 6-60) |
| 11 | CyrilSharma.finalBot | 2098 | 46 | 412 | 349-63 | 12% (b1z2b 8-58) |
| 12 | winkelmantanner.waffle | 2070 | 44 | 412 | 340-72 | 18% (b1z2b 12-54) |
| 13 | ColtG5.Goob_final | 1903 | 35 | 412 | 265-147 | 36% (b1z2b 24-42) |
| 14 | **us:g_iter1_c2** | 1888 | 132 | 110 | 84-26 |  |
| 15 | **us:a3dig5** | 1888 | 132 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1888 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1880 | 34 | 412 | 252-160 | 42% (b1z2b 28-38) |
| 18 | **us:e1aggr** | 1862 | 131 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1845 | 34 | 412 | 232-180 | 55% (b1z2b 36-30) |
| 20 | **us:c5bank** | 1836 | 131 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1836 | 131 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1836 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1814 | 16 | 4030 | 1469-2561 |  |
| 24 | **us:a2relay** | 1811 | 130 | 110 | 81-29 |  |
| 25 | **us:a2reloc** | 1811 | 130 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1811 | 130 | 110 | 81-29 |  |
| 27 | SampleProvider.TSPAARKSPRINT1 | 1804 | 34 | 412 | 208-204 | 56% (b1z2b 37-29) |
| 28 | **us:b1z2b** | 1804 | 27 | 1320 | 459-861 |  |
| 29 | **us:b1v2** | 1784 | 24 | 1680 | 562-1118 |  |
| 30 | **us:b2fs** | 1781 | 35 | 800 | 266-534 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1626 | 38 | 412 | 111-301 | 85% (b1z2b 56-10) |
| 32 | clbarrell.duck8 | 1592 | 40 | 412 | 96-316 | 70% (b1z2b 46-20) |
| 33 | jonters.bling3 | 1476 | 49 | 412 | 56-356 | 77% (b1z2b 51-15) |
| 34 | **us:arch_rush** | 1451 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1387 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1385 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1349 | 65 | 412 | 29-383 | 85% (b1z2b 56-10) |
| 38 | Metta-AI.bc24scenario | 1318 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1318 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1318 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1230 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1230 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1230 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1230 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1230 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1230 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1230 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1230 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1230 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1230 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1230 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1230 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1124 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1092 | 357 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1092 | 357 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1092 | 357 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1092 | 357 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1092 | 357 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1092 | 357 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1092 | 357 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1092 | 357 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1092 | 357 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1092 | 357 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1092 | 357 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1092 | 357 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1092 | 357 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1092 | 357 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1092 | 357 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1092 | 357 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1092 | 357 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1023 | 154 | 412 | 4-408 | 100% (b1z2b 66-0) |
