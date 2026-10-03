# Ladder

17829 scrimmages (ours only), 17829 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter3 | 1930 +- 19 | 12 of 77 | 1560 | 651-909 | 80.0% | 19.7% (vs 11) |
| g2cr | 1927 +- 107 | 13 of 77 | 40 | 21-19 | 79.9% | 19.4% (vs 11) |
| g3escrg2 | 1915 +- 44 | 15 of 77 | 280 | 90-190 | 79.5% | 21.0% (vs 12) |
| g_iter2 | 1884 +- 15 | 16 of 77 | 2200 | 1032-1168 | 78.4% | 18.4% (vs 12) |
| a3dig5 | 1804 +- 131 | 18 of 77 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| g_iter1_c2 | 1804 +- 131 | 19 of 77 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| arch_rush10 | 1804 +- 131 | 20 of 77 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| e1aggr | 1779 +- 130 | 22 of 77 | 110 | 83-27 | 74.8% | 16.3% (vs 14) |
| a3dig10 | 1754 +- 129 | 23 of 77 | 110 | 82-28 | 74.0% | 14.7% (vs 14) |
| c5bank | 1754 +- 129 | 24 of 77 | 110 | 82-28 | 74.0% | 14.7% (vs 14) |
| c6pair | 1754 +- 129 | 25 of 77 | 110 | 82-28 | 74.0% | 14.7% (vs 14) |
| a2reloc | 1729 +- 128 | 27 of 77 | 110 | 81-29 | 73.1% | 15.4% (vs 15) |
| e2aggr | 1729 +- 128 | 28 of 77 | 110 | 81-29 | 73.1% | 15.4% (vs 15) |
| a2relay | 1729 +- 128 | 29 of 77 | 110 | 81-29 | 73.1% | 15.4% (vs 15) |
| b1z2b | 1717 +- 24 | 30 of 77 | 1760 | 620-1140 | 72.7% | 14.7% (vs 15) |
| g_iter1 | 1711 +- 10 | 31 of 77 | 6990 | 2485-4505 | 72.5% | 14.3% (vs 15) |
| g1copy | 1704 +- 91 | 32 of 77 | 80 | 28-52 | 72.2% | 13.9% (vs 15) |
| b2fs | 1703 +- 28 | 33 of 77 | 1280 | 439-841 | 72.2% | 13.9% (vs 15) |
| g1sym | 1699 +- 51 | 34 of 77 | 200 | 68-132 | 72.0% | 13.6% (vs 15) |
| b1v2 | 1692 +- 22 | 36 of 77 | 2120 | 711-1409 | 71.8% | 15.5% (vs 16) |
| arch_rush | 1380 +- 94 | 39 of 77 | 110 | 62-48 | 56.7% | 6.6% (vs 18) |
| g_iter0 | 1315 +- 89 | 42 of 77 | 109 | 56-53 | 52.1% | 9.0% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2337 | 84 | 556 | 540-16 | 3% (g_iter1 7-217) |
| 2 | jmerle.camel_case_v21_final | 2337 | 84 | 556 | 540-16 | 4% (g_iter1 10-214) |
| 3 | IvanGeffner.kuma | 2298 | 76 | 556 | 536-20 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2297 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2227 | 63 | 556 | 526-30 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2179 | 56 | 556 | 517-39 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2170 | 55 | 556 | 515-41 | 6% (g_iter1 13-211) |
| 8 | Gymhgy.v10official | 2107 | 48 | 556 | 499-57 | 11% (g_iter1 25-199) |
| 9 | andrewgopher.player22 | 2094 | 46 | 556 | 495-61 | 11% (g_iter1 24-200) |
| 10 | CyrilSharma.finalBot | 2048 | 19 | 1676 | 1227-449 | 31% (g_iter3 269-595) |
| 11 | hsmalladi.finalbot | 2040 | 41 | 556 | 476-80 | 12% (g_iter1 27-197) |
| 12 | **us:g_iter3** | 1930 | 19 | 1560 | 651-909 |  |
| 13 | **us:g2cr** | 1927 | 107 | 40 | 21-19 |  |
| 14 | winkelmantanner.waffle | 1924 | 15 | 2276 | 1362-914 | 62% (g_iter3 163-101) |
| 15 | **us:g3escrg2** | 1915 | 44 | 280 | 90-190 |  |
| 16 | **us:g_iter2** | 1884 | 15 | 2200 | 1032-1168 |  |
| 17 | ColtG5.Goob_final | 1816 | 12 | 3516 | 2201-1315 | 67% (g_iter2 203-101) |
| 18 | **us:a3dig5** | 1804 | 131 | 110 | 84-26 |  |
| 19 | **us:g_iter1_c2** | 1804 | 131 | 110 | 84-26 |  |
| 20 | **us:arch_rush10** | 1804 | 131 | 110 | 84-26 |  |
| 21 | kyleezz.jeeryfix3 | 1796 | 30 | 556 | 335-221 | 41% (g_iter1 91-133) |
| 22 | **us:e1aggr** | 1779 | 130 | 110 | 83-27 |  |
| 23 | **us:a3dig10** | 1754 | 129 | 110 | 82-28 |  |
| 24 | **us:c5bank** | 1754 | 129 | 110 | 82-28 |  |
| 25 | **us:c6pair** | 1754 | 129 | 110 | 82-28 |  |
| 26 | quesswho.cretplayer2_3 | 1750 | 29 | 556 | 300-256 | 40% (g_iter1 90-134) |
| 27 | **us:a2reloc** | 1729 | 128 | 110 | 81-29 |  |
| 28 | **us:e2aggr** | 1729 | 128 | 110 | 81-29 |  |
| 29 | **us:a2relay** | 1729 | 128 | 110 | 81-29 |  |
| 30 | **us:b1z2b** | 1717 | 24 | 1760 | 620-1140 |  |
| 31 | **us:g_iter1** | 1711 | 10 | 6990 | 2485-4505 |  |
| 32 | **us:g1copy** | 1704 | 91 | 80 | 28-52 |  |
| 33 | **us:b2fs** | 1703 | 28 | 1280 | 439-841 |  |
| 34 | **us:g1sym** | 1699 | 51 | 200 | 68-132 |  |
| 35 | SampleProvider.TSPAARKSPRINT1 | 1694 | 29 | 556 | 256-300 | 50% (g_iter1 112-112) |
| 36 | **us:b1v2** | 1692 | 22 | 2120 | 711-1409 |  |
| 37 | dmtrung14.defaultplayer_intlqualifier | 1530 | 33 | 556 | 141-415 | 69% (g_iter1 155-69) |
| 38 | clbarrell.duck8 | 1508 | 35 | 556 | 128-428 | 78% (g_iter1 175-49) |
| 39 | **us:arch_rush** | 1380 | 94 | 110 | 62-48 |  |
| 40 | jonters.bling3 | 1359 | 46 | 556 | 63-493 | 89% (g_iter1 200-24) |
| 41 | HugoIngelsson.Bot21 | 1319 | 200 | 26 | 3-23 |  |
| 42 | **us:g_iter0** | 1315 | 89 | 109 | 56-53 |  |
| 43 | Metta-AI.bc24scenario | 1252 | 224 | 26 | 2-24 |  |
| 44 | noahzemlin.honeyducklings | 1252 | 224 | 26 | 2-24 |  |
| 45 | sivakovivan.NewHide | 1252 | 224 | 26 | 2-24 |  |
| 46 | awu7.ExplosiveBot | 1242 | 60 | 556 | 34-522 | 97% (g_iter1 217-7) |
| 47 | JeffLegendPower.v11 | 1164 | 264 | 26 | 1-25 |  |
| 48 | MiloAkerman.v1 | 1164 | 264 | 26 | 1-25 |  |
| 49 | PerishoJ.tx | 1164 | 264 | 26 | 1-25 |  |
| 50 | Peter-Fun.dinoboxer | 1164 | 264 | 26 | 1-25 |  |
| 51 | RyanAspen.v22 | 1164 | 264 | 26 | 1-25 |  |
| 52 | TylerJulian.v9 | 1164 | 264 | 26 | 1-25 |  |
| 53 | aj-chau.cowards | 1164 | 264 | 26 | 1-25 |  |
| 54 | cViper971.ourplayer | 1164 | 264 | 26 | 1-25 |  |
| 55 | dylanzemlin.dangerduck2 | 1164 | 264 | 26 | 1-25 |  |
| 56 | lukerhoads.warrior_2nd_comp | 1164 | 264 | 26 | 1-25 |  |
| 57 | neilhuang007.baseline | 1164 | 264 | 26 | 1-25 |  |
| 58 | polyllc.polyv4 | 1164 | 264 | 26 | 1-25 |  |
| 59 | andrearante12.turtle | 1060 | 357 | 25 | 0-25 |  |
| 60 | AlexYu84.smartPlayer | 1027 | 357 | 26 | 0-26 |  |
| 61 | H4ffliger.keyboardcrusader_v1 | 1027 | 357 | 26 | 0-26 |  |
| 62 | Lithanium.AttackingBot | 1027 | 357 | 26 | 0-26 |  |
| 63 | Rubrasum.version_3 | 1027 | 357 | 26 | 0-26 |  |
| 64 | SriLakshmiPolavarapu.ducks | 1027 | 357 | 26 | 0-26 |  |
| 65 | VarunVejalla.alexander | 1027 | 357 | 26 | 0-26 |  |
| 66 | abdullah8a0.crayBasic | 1027 | 357 | 26 | 0-26 |  |
| 67 | adamseth2.moveBot1 | 1027 | 357 | 26 | 0-26 |  |
| 68 | dylanconklin.Team3 | 1027 | 357 | 26 | 0-26 |  |
| 69 | itswin.MPAttack | 1027 | 357 | 26 | 0-26 |  |
| 70 | joelcrouch.ducks | 1027 | 357 | 26 | 0-26 |  |
| 71 | lcforges.funkyguy3 | 1027 | 357 | 26 | 0-26 |  |
| 72 | qpwoeirut.tournament_sprint1 | 1027 | 357 | 26 | 0-26 |  |
| 73 | reeceyang.v5 | 1027 | 357 | 26 | 0-26 |  |
| 74 | samithShetty.combustiblelemon | 1027 | 357 | 26 | 0-26 |  |
| 75 | sayam-goyal.SimpleBot | 1027 | 357 | 26 | 0-26 |  |
| 76 | tlevietpdx.Sprint2 | 1027 | 357 | 26 | 0-26 |  |
| 77 | justinottesen.sprint1 | 891 | 153 | 556 | 4-552 | 100% (g_iter1 224-0) |
