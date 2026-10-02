# Ladder

6549 scrimmages (ours only), 6549 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1895 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.4% (vs 13) |
| g_iter1_c2 | 1895 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.4% (vs 13) |
| arch_rush10 | 1895 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.4% (vs 13) |
| e1aggr | 1869 +- 131 | 18 of 71 | 110 | 83-27 | 74.7% | 16.2% (vs 14) |
| c6pair | 1843 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.8% (vs 15) |
| c5bank | 1843 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.8% (vs 15) |
| a3dig10 | 1843 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.8% (vs 15) |
| b1z2b | 1834 +- 38 | 23 of 71 | 640 | 233-407 | 73.5% | 16.2% (vs 15) |
| g_iter1 | 1821 +- 17 | 24 of 71 | 3390 | 1243-2147 | 73.1% | 15.4% (vs 15) |
| a2relay | 1818 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| e2aggr | 1818 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| a2reloc | 1818 +- 130 | 27 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| b1v2 | 1786 +- 31 | 29 of 71 | 1040 | 344-696 | 71.9% | 15.3% (vs 16) |
| b2fs | 1724 +- 81 | 30 of 71 | 160 | 47-113 | 69.6% | 11.7% (vs 16) |
| arch_rush | 1456 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.0% (vs 19) |
| g_iter0 | 1391 +- 89 | 36 of 71 | 109 | 56-53 | 51.9% | 8.2% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2513 | 154 | 282 | 278-4 | 0% (b1v2 0-52) |
| 2 | jmerle.camel_case_v21_final | 2408 | 115 | 282 | 274-8 | 0% (b1v2 0-52) |
| 3 | uravt.Version18Final | 2387 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2328 | 93 | 282 | 269-13 | 4% (b1v2 2-50) |
| 5 | chenyx512.flagbot_final | 2328 | 93 | 282 | 269-13 | 4% (b1v2 2-50) |
| 6 | andli28.v9_USQuals_angle | 2292 | 85 | 282 | 266-16 | 6% (b1v2 3-49) |
| 7 | NotLLeon.v3 | 2262 | 79 | 282 | 263-19 | 8% (b1v2 4-48) |
| 8 | Gymhgy.v10official | 2220 | 71 | 282 | 258-24 | 4% (b1v2 2-50) |
| 9 | andrewgopher.player22 | 2198 | 68 | 282 | 255-27 | 8% (b1v2 4-48) |
| 10 | hsmalladi.finalbot | 2113 | 57 | 282 | 240-42 | 13% (b1v2 7-45) |
| 11 | CyrilSharma.finalBot | 2090 | 54 | 282 | 235-47 | 13% (b1v2 7-45) |
| 12 | winkelmantanner.waffle | 2057 | 51 | 282 | 227-55 | 15% (b1v2 8-44) |
| 13 | ColtG5.Goob_final | 1904 | 42 | 282 | 178-104 | 31% (b1v2 16-36) |
| 14 | **us:a3dig5** | 1895 | 132 | 110 | 84-26 |  |
| 15 | **us:g_iter1_c2** | 1895 | 132 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1895 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1883 | 42 | 282 | 170-112 | 35% (b1v2 18-34) |
| 18 | **us:e1aggr** | 1869 | 131 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1858 | 41 | 282 | 160-122 | 46% (b1v2 24-28) |
| 20 | **us:c6pair** | 1843 | 131 | 110 | 82-28 |  |
| 21 | **us:c5bank** | 1843 | 131 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1843 | 131 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1834 | 38 | 640 | 233-407 |  |
| 24 | **us:g_iter1** | 1821 | 17 | 3390 | 1243-2147 |  |
| 25 | **us:a2relay** | 1818 | 130 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1818 | 130 | 110 | 81-29 |  |
| 27 | **us:a2reloc** | 1818 | 130 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1812 | 41 | 282 | 142-140 | 50% (b1v2 26-26) |
| 29 | **us:b1v2** | 1786 | 31 | 1040 | 344-696 |  |
| 30 | **us:b2fs** | 1724 | 81 | 160 | 47-113 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1668 | 44 | 282 | 87-195 | 65% (b1v2 34-18) |
| 32 | clbarrell.duck8 | 1597 | 49 | 282 | 65-217 | 81% (b1v2 42-10) |
| 33 | jonters.bling3 | 1465 | 62 | 282 | 35-247 | 88% (b1v2 46-6) |
| 34 | **us:arch_rush** | 1456 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1392 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1391 | 89 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1339 | 82 | 282 | 18-264 | 90% (b1v2 47-5) |
| 38 | Metta-AI.bc24scenario | 1324 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1324 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1324 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1235 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1235 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1235 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1235 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1235 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1235 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1235 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1235 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1235 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1235 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1235 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1235 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1129 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1097 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1097 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1097 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1097 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1097 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1097 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1097 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1097 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1097 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1097 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1097 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1097 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1097 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1097 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1097 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1097 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1097 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1092 | 155 | 282 | 4-278 | 100% (b1v2 52-0) |
