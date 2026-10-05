# Ladder

30709 scrimmages (ours only), 30709 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g4ship1 | 1996 +- 44 | 11 of 84 | 240 | 113-127 | 84.0% | 30.4% (vs 10) |
| g4crumb | 1950 +- 27 | 12 of 84 | 640 | 258-382 | 82.4% | 25.4% (vs 10) |
| g4gym1 | 1949 +- 16 | 13 of 84 | 1880 | 755-1125 | 82.4% | 25.4% (vs 10) |
| g_iter4 | 1944 +- 8 | 14 of 84 | 7840 | 3126-4714 | 82.2% | 24.8% (vs 10) |
| g4econ2 | 1939 +- 56 | 15 of 84 | 160 | 60-100 | 82.0% | 24.3% (vs 10) |
| g3lost | 1927 +- 112 | 16 of 84 | 40 | 15-25 | 81.6% | 23.2% (vs 10) |
| g4pick | 1925 +- 65 | 17 of 84 | 120 | 43-77 | 81.5% | 23.0% (vs 10) |
| g3escrg2 | 1900 +- 44 | 19 of 84 | 280 | 90-190 | 80.6% | 23.0% (vs 11) |
| g_iter3 | 1900 +- 13 | 20 of 84 | 3520 | 1259-2261 | 80.6% | 23.0% (vs 11) |
| g2cr | 1899 +- 107 | 21 of 84 | 40 | 21-19 | 80.6% | 22.9% (vs 11) |
| g_iter2 | 1855 +- 15 | 23 of 84 | 2200 | 1032-1168 | 79.0% | 21.1% (vs 12) |
| g_iter1_c2 | 1763 +- 127 | 26 of 84 | 110 | 84-26 | 75.8% | 19.0% (vs 14) |
| arch_rush10 | 1763 +- 127 | 27 of 84 | 110 | 84-26 | 75.8% | 19.0% (vs 14) |
| a3dig5 | 1763 +- 127 | 28 of 84 | 110 | 84-26 | 75.8% | 19.0% (vs 14) |
| e1aggr | 1739 +- 126 | 29 of 84 | 110 | 83-27 | 74.9% | 17.2% (vs 14) |
| c6pair | 1715 +- 126 | 31 of 84 | 110 | 82-28 | 74.0% | 17.8% (vs 15) |
| a3dig10 | 1715 +- 126 | 32 of 84 | 110 | 82-28 | 74.0% | 17.8% (vs 15) |
| c5bank | 1715 +- 126 | 33 of 84 | 110 | 82-28 | 74.0% | 17.8% (vs 15) |
| a2relay | 1692 +- 124 | 34 of 84 | 110 | 81-29 | 73.2% | 16.2% (vs 15) |
| e2aggr | 1692 +- 124 | 35 of 84 | 110 | 81-29 | 73.2% | 16.2% (vs 15) |
| a2reloc | 1692 +- 124 | 36 of 84 | 110 | 81-29 | 73.2% | 16.2% (vs 15) |
| b1z2b | 1687 +- 24 | 37 of 84 | 1760 | 620-1140 | 73.0% | 15.8% (vs 15) |
| g_iter1 | 1680 +- 10 | 38 of 84 | 6990 | 2485-4505 | 72.7% | 15.4% (vs 15) |
| g1copy | 1674 +- 91 | 39 of 84 | 80 | 28-52 | 72.5% | 15.0% (vs 15) |
| b2fs | 1673 +- 28 | 40 of 84 | 1280 | 439-841 | 72.5% | 14.9% (vs 15) |
| g1sym | 1668 +- 51 | 41 of 84 | 200 | 68-132 | 72.3% | 14.6% (vs 15) |
| b1v2 | 1661 +- 22 | 43 of 84 | 2120 | 711-1409 | 72.0% | 16.5% (vs 16) |
| arch_rush | 1351 +- 94 | 46 of 84 | 110 | 62-48 | 56.7% | 6.8% (vs 18) |
| g_iter0 | 1286 +- 89 | 49 of 84 | 109 | 56-53 | 52.1% | 9.2% (vs 20) |

Our record = OUR win rate (our W-L) against the bot by the incumbent (g_iter4), whatever the count; * marks fewer than 30 games (+- 18 points at 95% for 30 games, +- 20 for 24); a bot the incumbent never met shows the most recent of our builds that did; blank if none has.

| rank | player | rating | +- 95% | games | W-L | our record |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2303 | 78 | 580 | 561-19 | 12% (g_iter4 3-21*) |
| 2 | Strequals.duck0127v5 | 2295 | 76 | 580 | 560-20 | 17% (g_iter4 4-20*) |
| 3 | IvanGeffner.kuma | 2278 | 73 | 580 | 558-22 | 8% (g_iter4 2-22*) |
| 4 | chenyx512.flagbot_final | 2216 | 63 | 580 | 549-31 | 4% (g_iter4 1-23*) |
| 5 | NotLLeon.v3 | 2151 | 54 | 580 | 536-44 | 21% (g_iter4 5-19*) |
| 6 | andli28.v9_USQuals_angle | 2143 | 53 | 580 | 534-46 | 21% (g_iter4 5-19*) |
| 7 | andrewgopher.player22 | 2075 | 45 | 580 | 515-65 | 17% (g_iter4 4-20*) |
| 8 | CyrilSharma.finalBot | 2032 | 9 | 6380 | 4300-2080 | 37% (g_iter4 905-1519) |
| 9 | hsmalladi.finalbot | 2023 | 41 | 580 | 496-84 | 17% (g_iter4 4-20*) |
| 10 | Gymhgy.v10official | 2019 | 8 | 8260 | 5113-3147 | 40% (g_iter4 1964-2980) |
| 11 | **us:g4ship1** | 1996 | 44 | 240 | 113-127 |  |
| 12 | **us:g4crumb** | 1950 | 27 | 640 | 258-382 |  |
| 13 | **us:g4gym1** | 1949 | 16 | 1880 | 755-1125 |  |
| 14 | **us:g_iter4** | 1944 | 8 | 7840 | 3126-4714 |  |
| 15 | **us:g4econ2** | 1939 | 56 | 160 | 60-100 |  |
| 16 | **us:g3lost** | 1927 | 112 | 40 | 15-25 |  |
| 17 | **us:g4pick** | 1925 | 65 | 120 | 43-77 |  |
| 18 | uravt.Version18Final | 1920 | 90 | 66 | 40-26 | 65% (g_iter4 26-14) |
| 19 | **us:g3escrg2** | 1900 | 44 | 280 | 90-190 |  |
| 20 | **us:g_iter3** | 1900 | 13 | 3520 | 1259-2261 |  |
| 21 | **us:g2cr** | 1899 | 107 | 40 | 21-19 |  |
| 22 | winkelmantanner.waffle | 1896 | 15 | 2300 | 1375-925 | 46% (g_iter4 11-13*) |
| 23 | **us:g_iter2** | 1855 | 15 | 2200 | 1032-1168 |  |
| 24 | ColtG5.Goob_final | 1785 | 12 | 3540 | 2205-1335 | 83% (g_iter4 20-4*) |
| 25 | kyleezz.jeeryfix3 | 1766 | 29 | 580 | 342-238 | 71% (g_iter4 17-7*) |
| 26 | **us:g_iter1_c2** | 1763 | 127 | 110 | 84-26 |  |
| 27 | **us:arch_rush10** | 1763 | 127 | 110 | 84-26 |  |
| 28 | **us:a3dig5** | 1763 | 127 | 110 | 84-26 |  |
| 29 | **us:e1aggr** | 1739 | 126 | 110 | 83-27 |  |
| 30 | quesswho.cretplayer2_3 | 1722 | 29 | 580 | 307-273 | 71% (g_iter4 17-7*) |
| 31 | **us:c6pair** | 1715 | 126 | 110 | 82-28 |  |
| 32 | **us:a3dig10** | 1715 | 126 | 110 | 82-28 |  |
| 33 | **us:c5bank** | 1715 | 126 | 110 | 82-28 |  |
| 34 | **us:a2relay** | 1692 | 124 | 110 | 81-29 |  |
| 35 | **us:e2aggr** | 1692 | 124 | 110 | 81-29 |  |
| 36 | **us:a2reloc** | 1692 | 124 | 110 | 81-29 |  |
| 37 | **us:b1z2b** | 1687 | 24 | 1760 | 620-1140 |  |
| 38 | **us:g_iter1** | 1680 | 10 | 6990 | 2485-4505 |  |
| 39 | **us:g1copy** | 1674 | 91 | 80 | 28-52 |  |
| 40 | **us:b2fs** | 1673 | 28 | 1280 | 439-841 |  |
| 41 | **us:g1sym** | 1668 | 51 | 200 | 68-132 |  |
| 42 | SampleProvider.TSPAARKSPRINT1 | 1662 | 29 | 580 | 259-321 | 88% (g_iter4 21-3*) |
| 43 | **us:b1v2** | 1661 | 22 | 2120 | 711-1409 |  |
| 44 | dmtrung14.defaultplayer_intlqualifier | 1499 | 33 | 580 | 142-438 | 96% (g_iter4 23-1*) |
| 45 | clbarrell.duck8 | 1476 | 34 | 580 | 129-451 | 96% (g_iter4 23-1*) |
| 46 | **us:arch_rush** | 1351 | 94 | 110 | 62-48 |  |
| 47 | jonters.bling3 | 1329 | 45 | 580 | 64-516 | 96% (g_iter4 23-1*) |
| 48 | HugoIngelsson.Bot21 | 1289 | 200 | 26 | 3-23 | 100% (g_iter1 2-0*) |
| 49 | **us:g_iter0** | 1286 | 89 | 109 | 56-53 |  |
| 50 | Metta-AI.bc24scenario | 1223 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 51 | noahzemlin.honeyducklings | 1223 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 52 | sivakovivan.NewHide | 1223 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 53 | awu7.ExplosiveBot | 1210 | 60 | 580 | 34-546 | 100% (g_iter4 24-0*) |
| 54 | JeffLegendPower.v11 | 1135 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 55 | MiloAkerman.v1 | 1135 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 56 | PerishoJ.tx | 1135 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 57 | Peter-Fun.dinoboxer | 1135 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 58 | RyanAspen.v22 | 1135 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 59 | TylerJulian.v9 | 1135 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 60 | aj-chau.cowards | 1135 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 61 | cViper971.ourplayer | 1135 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 62 | dylanzemlin.dangerduck2 | 1135 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 63 | lukerhoads.warrior_2nd_comp | 1135 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 64 | neilhuang007.baseline | 1135 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 65 | polyllc.polyv4 | 1135 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 66 | andrearante12.turtle | 1031 | 357 | 25 | 0-25 | 100% (g_iter1 2-0*) |
| 67 | AlexYu84.smartPlayer | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 68 | H4ffliger.keyboardcrusader_v1 | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 69 | Lithanium.AttackingBot | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 70 | Rubrasum.version_3 | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 71 | SriLakshmiPolavarapu.ducks | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 72 | VarunVejalla.alexander | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 73 | abdullah8a0.crayBasic | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 74 | adamseth2.moveBot1 | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 75 | dylanconklin.Team3 | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 76 | itswin.MPAttack | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 77 | joelcrouch.ducks | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 78 | lcforges.funkyguy3 | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 79 | qpwoeirut.tournament_sprint1 | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 80 | reeceyang.v5 | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 81 | samithShetty.combustiblelemon | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 82 | sayam-goyal.SimpleBot | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 83 | tlevietpdx.Sprint2 | 998 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 84 | justinottesen.sprint1 | 859 | 153 | 580 | 4-576 | 100% (g_iter4 24-0*) |
