# Ladder

7189 scrimmages (ours only), 7189 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| arch_rush10 | 1894 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| g_iter1_c2 | 1894 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| a3dig5 | 1894 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| e1aggr | 1867 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 16.2% (vs 14) |
| c5bank | 1842 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c6pair | 1842 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| a3dig10 | 1842 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| b1z2b | 1832 +- 34 | 23 of 71 | 800 | 291-509 | 73.5% | 16.1% (vs 15) |
| g_iter1 | 1822 +- 17 | 24 of 71 | 3550 | 1305-2245 | 73.2% | 15.4% (vs 15) |
| a2reloc | 1816 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| e2aggr | 1816 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1816 +- 130 | 27 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1v2 | 1781 +- 29 | 29 of 71 | 1200 | 394-806 | 71.7% | 14.9% (vs 16) |
| b2fs | 1759 +- 57 | 30 of 71 | 320 | 101-219 | 70.9% | 13.7% (vs 16) |
| arch_rush | 1455 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1389 +- 89 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2447 | 122 | 314 | 307-7 | 0% (b1v2 0-60) |
| 2 | jmerle.camel_case_v21_final | 2390 | 105 | 314 | 304-10 | 0% (b1v2 0-60) |
| 3 | uravt.Version18Final | 2386 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2346 | 93 | 314 | 301-13 | 3% (b1v2 2-58) |
| 5 | andli28.v9_USQuals_angle | 2281 | 79 | 314 | 295-19 | 5% (b1v2 3-57) |
| 6 | chenyx512.flagbot_final | 2271 | 77 | 314 | 294-20 | 5% (b1v2 3-57) |
| 7 | NotLLeon.v3 | 2263 | 75 | 314 | 293-21 | 7% (b1v2 4-56) |
| 8 | Gymhgy.v10official | 2231 | 70 | 314 | 289-25 | 3% (b1v2 2-58) |
| 9 | andrewgopher.player22 | 2210 | 66 | 314 | 286-28 | 7% (b1v2 4-56) |
| 10 | hsmalladi.finalbot | 2119 | 54 | 314 | 269-45 | 12% (b1v2 7-53) |
| 11 | CyrilSharma.finalBot | 2098 | 52 | 314 | 264-50 | 12% (b1v2 7-53) |
| 12 | winkelmantanner.waffle | 2067 | 49 | 314 | 256-58 | 13% (b1v2 8-52) |
| 13 | ColtG5.Goob_final | 1902 | 40 | 314 | 198-116 | 32% (b1v2 19-41) |
| 14 | **us:arch_rush10** | 1894 | 132 | 110 | 84-26 |  |
| 15 | **us:g_iter1_c2** | 1894 | 132 | 110 | 84-26 |  |
| 16 | **us:a3dig5** | 1894 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1879 | 39 | 314 | 188-126 | 35% (b1v2 21-39) |
| 18 | **us:e1aggr** | 1867 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1861 | 39 | 314 | 180-134 | 45% (b1v2 27-33) |
| 20 | **us:c5bank** | 1842 | 131 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1842 | 131 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1842 | 131 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1832 | 34 | 800 | 291-509 |  |
| 24 | **us:g_iter1** | 1822 | 17 | 3550 | 1305-2245 |  |
| 25 | **us:a2reloc** | 1816 | 130 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1816 | 130 | 110 | 81-29 |  |
| 27 | **us:a2relay** | 1816 | 130 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1813 | 39 | 314 | 159-155 | 48% (b1v2 29-31) |
| 29 | **us:b1v2** | 1781 | 29 | 1200 | 394-806 |  |
| 30 | **us:b2fs** | 1759 | 57 | 320 | 101-219 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1656 | 42 | 314 | 93-221 | 67% (b1v2 40-20) |
| 32 | clbarrell.duck8 | 1586 | 47 | 314 | 69-245 | 82% (b1v2 49-11) |
| 33 | jonters.bling3 | 1465 | 58 | 314 | 39-275 | 90% (b1v2 54-6) |
| 34 | **us:arch_rush** | 1455 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1391 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1389 | 89 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1346 | 76 | 314 | 21-293 | 92% (b1v2 55-5) |
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
| 71 | justinottesen.sprint1 | 1074 | 154 | 314 | 4-310 | 100% (b1v2 60-0) |
