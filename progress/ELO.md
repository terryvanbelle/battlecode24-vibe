# Ladder

8309 scrimmages (ours only), 8309 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1891 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| arch_rush10 | 1891 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| g_iter1_c2 | 1891 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| e1aggr | 1865 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| c6pair | 1839 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c5bank | 1839 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| a3dig10 | 1839 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| g_iter1 | 1818 +- 16 | 23 of 71 | 3830 | 1403-2427 | 73.1% | 15.3% (vs 15) |
| b1z2b | 1816 +- 30 | 24 of 71 | 1080 | 383-697 | 73.1% | 15.2% (vs 15) |
| a2reloc | 1813 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| e2aggr | 1813 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1813 +- 130 | 27 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1v2 | 1784 +- 26 | 29 of 71 | 1480 | 492-988 | 71.9% | 15.4% (vs 16) |
| b2fs | 1781 +- 41 | 30 of 71 | 600 | 199-401 | 71.9% | 15.2% (vs 16) |
| arch_rush | 1453 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1387 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2414 | 104 | 370 | 360-10 | 0% (b1v2 0-74) |
| 2 | jmerle.camel_case_v21_final | 2399 | 100 | 370 | 359-11 | 0% (b1v2 0-74) |
| 3 | uravt.Version18Final | 2383 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2371 | 93 | 370 | 357-13 | 3% (b1v2 2-72) |
| 5 | chenyx512.flagbot_final | 2280 | 73 | 370 | 348-22 | 4% (b1v2 3-71) |
| 6 | NotLLeon.v3 | 2272 | 72 | 370 | 347-23 | 8% (b1v2 6-68) |
| 7 | andli28.v9_USQuals_angle | 2272 | 72 | 370 | 347-23 | 5% (b1v2 4-70) |
| 8 | andrewgopher.player22 | 2224 | 64 | 370 | 340-30 | 7% (b1v2 5-69) |
| 9 | Gymhgy.v10official | 2212 | 62 | 370 | 338-32 | 4% (b1v2 3-71) |
| 10 | hsmalladi.finalbot | 2112 | 50 | 370 | 316-54 | 15% (b1v2 11-63) |
| 11 | CyrilSharma.finalBot | 2097 | 48 | 370 | 312-58 | 12% (b1v2 9-65) |
| 12 | winkelmantanner.waffle | 2070 | 46 | 370 | 304-66 | 15% (b1v2 11-63) |
| 13 | ColtG5.Goob_final | 1902 | 37 | 370 | 235-135 | 31% (b1v2 23-51) |
| 14 | **us:a3dig5** | 1891 | 132 | 110 | 84-26 |  |
| 15 | **us:arch_rush10** | 1891 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1891 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1876 | 36 | 370 | 222-148 | 34% (b1v2 25-49) |
| 18 | **us:e1aggr** | 1865 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1845 | 36 | 370 | 206-164 | 47% (b1v2 35-39) |
| 20 | **us:c6pair** | 1839 | 131 | 110 | 82-28 |  |
| 21 | **us:c5bank** | 1839 | 131 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1839 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1818 | 16 | 3830 | 1403-2427 |  |
| 24 | **us:b1z2b** | 1816 | 30 | 1080 | 383-697 |  |
| 25 | **us:a2reloc** | 1813 | 130 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1813 | 130 | 110 | 81-29 |  |
| 27 | **us:a2relay** | 1813 | 130 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1805 | 36 | 370 | 185-185 | 50% (b1v2 37-37) |
| 29 | **us:b1v2** | 1784 | 26 | 1480 | 492-988 |  |
| 30 | **us:b2fs** | 1781 | 41 | 600 | 199-401 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1638 | 40 | 370 | 103-267 | 70% (b1v2 52-22) |
| 32 | clbarrell.duck8 | 1601 | 42 | 370 | 88-282 | 78% (b1v2 58-16) |
| 33 | jonters.bling3 | 1462 | 54 | 370 | 46-324 | 91% (b1v2 67-7) |
| 34 | **us:arch_rush** | 1453 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1388 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1387 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1353 | 69 | 370 | 26-344 | 91% (b1v2 67-7) |
| 38 | Metta-AI.bc24scenario | 1320 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1320 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1320 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1232 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1232 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1232 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1232 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1232 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1232 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1232 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1232 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1232 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1232 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1232 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1232 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1126 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1093 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1093 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1093 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1093 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1093 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1093 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1093 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1093 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1093 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1093 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1093 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1093 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1093 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1093 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1093 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1093 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1093 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1044 | 154 | 370 | 4-366 | 100% (b1v2 74-0) |
