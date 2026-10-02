# Ladder

10349 scrimmages (ours only), 10349 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| arch_rush10 | 1885 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 17.7% (vs 14) |
| g_iter1_c2 | 1885 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 17.7% (vs 14) |
| a3dig5 | 1885 +- 132 | 17 of 71 | 110 | 84-26 | 75.6% | 17.7% (vs 14) |
| e1aggr | 1859 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 15.9% (vs 14) |
| a3dig10 | 1833 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.5% (vs 15) |
| c5bank | 1833 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.5% (vs 15) |
| c6pair | 1833 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.5% (vs 15) |
| g_iter1 | 1809 +- 15 | 23 of 71 | 4350 | 1578-2772 | 73.0% | 15.0% (vs 15) |
| a2reloc | 1808 +- 130 | 24 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| e2aggr | 1808 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| a2relay | 1808 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| b1z2b | 1803 +- 25 | 27 of 71 | 1600 | 559-1041 | 72.8% | 14.6% (vs 15) |
| b2fs | 1784 +- 30 | 29 of 71 | 1120 | 377-743 | 72.1% | 15.6% (vs 16) |
| b1v2 | 1782 +- 23 | 30 of 71 | 1960 | 657-1303 | 72.1% | 15.5% (vs 16) |
| arch_rush | 1446 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1381 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2419 | 96 | 472 | 460-12 | 0% (b2fs 0-56) |
| 2 | Strequals.duck0127v5 | 2406 | 92 | 472 | 459-13 | 2% (b2fs 1-55) |
| 3 | IvanGeffner.kuma | 2393 | 89 | 472 | 458-14 | 2% (b2fs 1-55) |
| 4 | uravt.Version18Final | 2377 | 348 | 26 | 26-0 |  |
| 5 | NotLLeon.v3 | 2261 | 63 | 472 | 442-30 | 5% (b2fs 3-53) |
| 6 | chenyx512.flagbot_final | 2261 | 63 | 472 | 442-30 | 9% (b2fs 5-51) |
| 7 | andli28.v9_USQuals_angle | 2233 | 59 | 472 | 437-35 | 12% (b2fs 7-49) |
| 8 | Gymhgy.v10official | 2223 | 58 | 472 | 435-37 | 7% (b2fs 4-52) |
| 9 | andrewgopher.player22 | 2195 | 54 | 472 | 429-43 | 11% (b2fs 6-50) |
| 10 | hsmalladi.finalbot | 2106 | 44 | 472 | 404-68 | 14% (b2fs 8-48) |
| 11 | CyrilSharma.finalBot | 2087 | 43 | 472 | 397-75 | 14% (b2fs 8-48) |
| 12 | winkelmantanner.waffle | 2061 | 41 | 472 | 387-85 | 16% (b2fs 9-47) |
| 13 | ColtG5.Goob_final | 1909 | 33 | 472 | 309-163 | 29% (b2fs 16-40) |
| 14 | kyleezz.jeeryfix3 | 1886 | 32 | 472 | 295-177 | 29% (b2fs 16-40) |
| 15 | **us:arch_rush10** | 1885 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1885 | 132 | 110 | 84-26 |  |
| 17 | **us:a3dig5** | 1885 | 132 | 110 | 84-26 |  |
| 18 | **us:e1aggr** | 1859 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1842 | 32 | 472 | 266-206 | 39% (b2fs 22-34) |
| 20 | **us:a3dig10** | 1833 | 131 | 110 | 82-28 |  |
| 21 | **us:c5bank** | 1833 | 131 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1833 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1809 | 15 | 4350 | 1578-2772 |  |
| 24 | **us:a2reloc** | 1808 | 130 | 110 | 81-29 |  |
| 25 | **us:e2aggr** | 1808 | 130 | 110 | 81-29 |  |
| 26 | **us:a2relay** | 1808 | 130 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1803 | 25 | 1600 | 559-1041 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1797 | 31 | 472 | 236-236 | 39% (b2fs 22-34) |
| 29 | **us:b2fs** | 1784 | 30 | 1120 | 377-743 |  |
| 30 | **us:b1v2** | 1782 | 23 | 1960 | 657-1303 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1621 | 36 | 472 | 126-346 | 82% (b2fs 46-10) |
| 32 | clbarrell.duck8 | 1601 | 37 | 472 | 116-356 | 75% (b2fs 42-14) |
| 33 | jonters.bling3 | 1450 | 48 | 472 | 57-415 | 89% (b2fs 50-6) |
| 34 | **us:arch_rush** | 1446 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1383 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1381 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1328 | 64 | 472 | 30-442 | 98% (b2fs 55-1) |
| 38 | Metta-AI.bc24scenario | 1315 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1315 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1315 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1227 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1227 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1227 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1227 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1227 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1227 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1227 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1227 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1227 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1227 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1227 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1227 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1121 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1088 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1088 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1088 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1088 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1088 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1088 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1088 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1088 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1088 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1088 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1088 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1088 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1088 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1088 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1088 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1088 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1088 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 997 | 154 | 472 | 4-468 | 100% (b2fs 56-0) |
