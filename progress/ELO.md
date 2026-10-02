# Ladder

7389 scrimmages (ours only), 7389 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| arch_rush10 | 1893 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| g_iter1_c2 | 1893 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| a3dig5 | 1893 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| e1aggr | 1867 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| c5bank | 1841 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c6pair | 1841 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| a3dig10 | 1841 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| b1z2b | 1825 +- 33 | 23 of 71 | 880 | 316-564 | 73.3% | 15.6% (vs 15) |
| g_iter1 | 1821 +- 17 | 24 of 71 | 3590 | 1318-2272 | 73.2% | 15.3% (vs 15) |
| e2aggr | 1816 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1816 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2reloc | 1816 +- 130 | 27 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1v2 | 1785 +- 28 | 29 of 71 | 1240 | 411-829 | 71.9% | 15.2% (vs 16) |
| b2fs | 1769 +- 53 | 30 of 71 | 360 | 116-244 | 71.3% | 14.2% (vs 16) |
| arch_rush | 1454 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1389 +- 89 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2451 | 122 | 324 | 317-7 | 2% (b1z2b 1-43) |
| 2 | jmerle.camel_case_v21_final | 2394 | 104 | 324 | 314-10 | 2% (b1z2b 1-43) |
| 3 | uravt.Version18Final | 2385 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2351 | 93 | 324 | 311-13 | 5% (b1z2b 2-42) |
| 5 | andli28.v9_USQuals_angle | 2285 | 79 | 324 | 305-19 | 11% (b1z2b 5-39) |
| 6 | chenyx512.flagbot_final | 2276 | 77 | 324 | 304-20 | 7% (b1z2b 3-41) |
| 7 | NotLLeon.v3 | 2259 | 74 | 324 | 302-22 | 7% (b1z2b 3-41) |
| 8 | Gymhgy.v10official | 2236 | 70 | 324 | 299-25 | 5% (b1z2b 2-42) |
| 9 | andrewgopher.player22 | 2216 | 66 | 324 | 296-28 | 11% (b1z2b 5-39) |
| 10 | hsmalladi.finalbot | 2116 | 53 | 324 | 277-47 | 14% (b1z2b 6-38) |
| 11 | CyrilSharma.finalBot | 2104 | 52 | 324 | 274-50 | 11% (b1z2b 5-39) |
| 12 | winkelmantanner.waffle | 2063 | 48 | 324 | 263-61 | 25% (b1z2b 11-33) |
| 13 | ColtG5.Goob_final | 1897 | 39 | 324 | 202-122 | 41% (b1z2b 18-26) |
| 14 | **us:arch_rush10** | 1893 | 132 | 110 | 84-26 |  |
| 15 | **us:g_iter1_c2** | 1893 | 132 | 110 | 84-26 |  |
| 16 | **us:a3dig5** | 1893 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1887 | 39 | 324 | 198-126 | 45% (b1z2b 20-24) |
| 18 | **us:e1aggr** | 1867 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1850 | 38 | 324 | 181-143 | 57% (b1z2b 25-19) |
| 20 | **us:c5bank** | 1841 | 131 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1841 | 131 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1841 | 131 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1825 | 33 | 880 | 316-564 |  |
| 24 | **us:g_iter1** | 1821 | 17 | 3590 | 1318-2272 |  |
| 25 | **us:e2aggr** | 1816 | 130 | 110 | 81-29 |  |
| 26 | **us:a2relay** | 1816 | 130 | 110 | 81-29 |  |
| 27 | **us:a2reloc** | 1816 | 130 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1815 | 38 | 324 | 165-159 | 57% (b1z2b 25-19) |
| 29 | **us:b1v2** | 1785 | 28 | 1240 | 411-829 |  |
| 30 | **us:b2fs** | 1769 | 53 | 360 | 116-244 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1651 | 42 | 324 | 94-230 | 86% (b1z2b 38-6) |
| 32 | clbarrell.duck8 | 1582 | 46 | 324 | 70-254 | 70% (b1z2b 31-13) |
| 33 | jonters.bling3 | 1468 | 57 | 324 | 41-283 | 75% (b1z2b 33-11) |
| 34 | **us:arch_rush** | 1454 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1390 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1389 | 89 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1349 | 75 | 324 | 22-302 | 86% (b1z2b 38-6) |
| 38 | Metta-AI.bc24scenario | 1322 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1322 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1322 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1233 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1233 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1233 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1233 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1233 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1233 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1233 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1233 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1233 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1233 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1233 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1233 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1127 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1095 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1095 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1095 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1095 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1095 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1095 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1095 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1095 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1095 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1095 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1095 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1095 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1095 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1095 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1095 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1095 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1095 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1069 | 154 | 324 | 4-320 | 100% (b1z2b 44-0) |
