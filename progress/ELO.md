# Ladder

30349 scrimmages (ours only), 30349 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g4ship1 | 1994 +- 76 | 12 of 84 | 80 | 39-41 | 83.4% | 29.7% (vs 11) |
| g4crumb | 1941 +- 27 | 13 of 84 | 640 | 258-382 | 81.6% | 24.2% (vs 11) |
| g4gym1 | 1940 +- 16 | 14 of 84 | 1880 | 755-1125 | 81.6% | 24.1% (vs 11) |
| g_iter4 | 1934 +- 8 | 15 of 84 | 7640 | 3028-4612 | 81.3% | 23.5% (vs 11) |
| g4econ2 | 1930 +- 56 | 16 of 84 | 160 | 60-100 | 81.2% | 23.1% (vs 11) |
| g3lost | 1918 +- 112 | 17 of 84 | 40 | 15-25 | 80.8% | 22.0% (vs 11) |
| g4pick | 1916 +- 65 | 18 of 84 | 120 | 43-77 | 80.7% | 21.8% (vs 11) |
| g2cr | 1892 +- 107 | 19 of 84 | 40 | 21-19 | 79.9% | 19.6% (vs 11) |
| g_iter3 | 1890 +- 13 | 20 of 84 | 3520 | 1259-2261 | 79.9% | 19.5% (vs 11) |
| g3escrg2 | 1890 +- 44 | 21 of 84 | 280 | 90-190 | 79.9% | 19.5% (vs 11) |
| g_iter2 | 1847 +- 15 | 23 of 84 | 2200 | 1032-1168 | 78.4% | 18.5% (vs 12) |
| a3dig5 | 1770 +- 130 | 25 of 84 | 110 | 84-26 | 75.8% | 15.8% (vs 13) |
| g_iter1_c2 | 1770 +- 130 | 26 of 84 | 110 | 84-26 | 75.8% | 15.8% (vs 13) |
| arch_rush10 | 1770 +- 130 | 27 of 84 | 110 | 84-26 | 75.8% | 15.8% (vs 13) |
| e1aggr | 1745 +- 129 | 29 of 84 | 110 | 83-27 | 74.9% | 16.6% (vs 14) |
| a3dig10 | 1721 +- 128 | 30 of 84 | 110 | 82-28 | 74.0% | 15.0% (vs 14) |
| c6pair | 1721 +- 128 | 31 of 84 | 110 | 82-28 | 74.0% | 15.0% (vs 14) |
| c5bank | 1721 +- 128 | 32 of 84 | 110 | 82-28 | 74.0% | 15.0% (vs 14) |
| e2aggr | 1697 +- 126 | 34 of 84 | 110 | 81-29 | 73.2% | 15.7% (vs 15) |
| a2reloc | 1697 +- 126 | 35 of 84 | 110 | 81-29 | 73.2% | 15.7% (vs 15) |
| a2relay | 1697 +- 126 | 36 of 84 | 110 | 81-29 | 73.2% | 15.7% (vs 15) |
| b1z2b | 1680 +- 24 | 37 of 84 | 1760 | 620-1140 | 72.6% | 14.7% (vs 15) |
| g_iter1 | 1673 +- 10 | 38 of 84 | 6990 | 2485-4505 | 72.3% | 14.3% (vs 15) |
| g1copy | 1667 +- 91 | 39 of 84 | 80 | 28-52 | 72.1% | 13.9% (vs 15) |
| b2fs | 1666 +- 28 | 40 of 84 | 1280 | 439-841 | 72.0% | 13.9% (vs 15) |
| g1sym | 1661 +- 51 | 41 of 84 | 200 | 68-132 | 71.9% | 13.6% (vs 15) |
| b1v2 | 1654 +- 22 | 43 of 84 | 2120 | 711-1409 | 71.6% | 15.5% (vs 16) |
| arch_rush | 1352 +- 94 | 46 of 84 | 110 | 62-48 | 56.7% | 6.9% (vs 18) |
| g_iter0 | 1287 +- 89 | 49 of 84 | 109 | 56-53 | 52.1% | 9.3% (vs 20) |

Our record = OUR win rate (our W-L) against the bot by the incumbent (g_iter4), whatever the count; * marks fewer than 30 games (+- 18 points at 95% for 30 games, +- 20 for 24); a bot the incumbent never met shows the most recent of our builds that did; blank if none has.

| rank | player | rating | +- 95% | games | W-L | our record |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2296 | 78 | 580 | 561-19 | 12% (g_iter4 3-21*) |
| 2 | Strequals.duck0127v5 | 2287 | 76 | 580 | 560-20 | 17% (g_iter4 4-20*) |
| 3 | IvanGeffner.kuma | 2271 | 73 | 580 | 558-22 | 8% (g_iter4 2-22*) |
| 4 | uravt.Version18Final | 2265 | 348 | 26 | 26-0 | 0% (g_iter1 0-2*) |
| 5 | chenyx512.flagbot_final | 2209 | 63 | 580 | 549-31 | 4% (g_iter4 1-23*) |
| 6 | NotLLeon.v3 | 2144 | 54 | 580 | 536-44 | 21% (g_iter4 5-19*) |
| 7 | andli28.v9_USQuals_angle | 2136 | 53 | 580 | 534-46 | 21% (g_iter4 5-19*) |
| 8 | andrewgopher.player22 | 2069 | 45 | 580 | 515-65 | 17% (g_iter4 4-20*) |
| 9 | CyrilSharma.finalBot | 2022 | 9 | 6380 | 4300-2080 | 37% (g_iter4 905-1519) |
| 10 | hsmalladi.finalbot | 2016 | 41 | 580 | 496-84 | 17% (g_iter4 4-20*) |
| 11 | Gymhgy.v10official | 2010 | 8 | 7940 | 4939-3001 | 40% (g_iter4 1892-2892) |
| 12 | **us:g4ship1** | 1994 | 76 | 80 | 39-41 |  |
| 13 | **us:g4crumb** | 1941 | 27 | 640 | 258-382 |  |
| 14 | **us:g4gym1** | 1940 | 16 | 1880 | 755-1125 |  |
| 15 | **us:g_iter4** | 1934 | 8 | 7640 | 3028-4612 |  |
| 16 | **us:g4econ2** | 1930 | 56 | 160 | 60-100 |  |
| 17 | **us:g3lost** | 1918 | 112 | 40 | 15-25 |  |
| 18 | **us:g4pick** | 1916 | 65 | 120 | 43-77 |  |
| 19 | **us:g2cr** | 1892 | 107 | 40 | 21-19 |  |
| 20 | **us:g_iter3** | 1890 | 13 | 3520 | 1259-2261 |  |
| 21 | **us:g3escrg2** | 1890 | 44 | 280 | 90-190 |  |
| 22 | winkelmantanner.waffle | 1888 | 15 | 2300 | 1375-925 | 46% (g_iter4 11-13*) |
| 23 | **us:g_iter2** | 1847 | 15 | 2200 | 1032-1168 |  |
| 24 | ColtG5.Goob_final | 1778 | 12 | 3540 | 2205-1335 | 83% (g_iter4 20-4*) |
| 25 | **us:a3dig5** | 1770 | 130 | 110 | 84-26 |  |
| 26 | **us:g_iter1_c2** | 1770 | 130 | 110 | 84-26 |  |
| 27 | **us:arch_rush10** | 1770 | 130 | 110 | 84-26 |  |
| 28 | kyleezz.jeeryfix3 | 1760 | 29 | 580 | 342-238 | 71% (g_iter4 17-7*) |
| 29 | **us:e1aggr** | 1745 | 129 | 110 | 83-27 |  |
| 30 | **us:a3dig10** | 1721 | 128 | 110 | 82-28 |  |
| 31 | **us:c6pair** | 1721 | 128 | 110 | 82-28 |  |
| 32 | **us:c5bank** | 1721 | 128 | 110 | 82-28 |  |
| 33 | quesswho.cretplayer2_3 | 1715 | 29 | 580 | 307-273 | 71% (g_iter4 17-7*) |
| 34 | **us:e2aggr** | 1697 | 126 | 110 | 81-29 |  |
| 35 | **us:a2reloc** | 1697 | 126 | 110 | 81-29 |  |
| 36 | **us:a2relay** | 1697 | 126 | 110 | 81-29 |  |
| 37 | **us:b1z2b** | 1680 | 24 | 1760 | 620-1140 |  |
| 38 | **us:g_iter1** | 1673 | 10 | 6990 | 2485-4505 |  |
| 39 | **us:g1copy** | 1667 | 91 | 80 | 28-52 |  |
| 40 | **us:b2fs** | 1666 | 28 | 1280 | 439-841 |  |
| 41 | **us:g1sym** | 1661 | 51 | 200 | 68-132 |  |
| 42 | SampleProvider.TSPAARKSPRINT1 | 1655 | 29 | 580 | 259-321 | 88% (g_iter4 21-3*) |
| 43 | **us:b1v2** | 1654 | 22 | 2120 | 711-1409 |  |
| 44 | dmtrung14.defaultplayer_intlqualifier | 1492 | 33 | 580 | 142-438 | 96% (g_iter4 23-1*) |
| 45 | clbarrell.duck8 | 1470 | 34 | 580 | 129-451 | 96% (g_iter4 23-1*) |
| 46 | **us:arch_rush** | 1352 | 94 | 110 | 62-48 |  |
| 47 | jonters.bling3 | 1323 | 45 | 580 | 64-516 | 96% (g_iter4 23-1*) |
| 48 | HugoIngelsson.Bot21 | 1292 | 200 | 26 | 3-23 | 100% (g_iter1 2-0*) |
| 49 | **us:g_iter0** | 1287 | 89 | 109 | 56-53 |  |
| 50 | Metta-AI.bc24scenario | 1225 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 51 | noahzemlin.honeyducklings | 1225 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 52 | sivakovivan.NewHide | 1225 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 53 | awu7.ExplosiveBot | 1203 | 60 | 580 | 34-546 | 100% (g_iter4 24-0*) |
| 54 | JeffLegendPower.v11 | 1138 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 55 | MiloAkerman.v1 | 1138 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 56 | PerishoJ.tx | 1138 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 57 | Peter-Fun.dinoboxer | 1138 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 58 | RyanAspen.v22 | 1138 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 59 | TylerJulian.v9 | 1138 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 60 | aj-chau.cowards | 1138 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 61 | cViper971.ourplayer | 1138 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 62 | dylanzemlin.dangerduck2 | 1138 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 63 | lukerhoads.warrior_2nd_comp | 1138 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 64 | neilhuang007.baseline | 1138 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 65 | polyllc.polyv4 | 1138 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 66 | andrearante12.turtle | 1033 | 357 | 25 | 0-25 | 100% (g_iter1 2-0*) |
| 67 | AlexYu84.smartPlayer | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 68 | H4ffliger.keyboardcrusader_v1 | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 69 | Lithanium.AttackingBot | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 70 | Rubrasum.version_3 | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 71 | SriLakshmiPolavarapu.ducks | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 72 | VarunVejalla.alexander | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 73 | abdullah8a0.crayBasic | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 74 | adamseth2.moveBot1 | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 75 | dylanconklin.Team3 | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 76 | itswin.MPAttack | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 77 | joelcrouch.ducks | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 78 | lcforges.funkyguy3 | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 79 | qpwoeirut.tournament_sprint1 | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 80 | reeceyang.v5 | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 81 | samithShetty.combustiblelemon | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 82 | sayam-goyal.SimpleBot | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 83 | tlevietpdx.Sprint2 | 1000 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 84 | justinottesen.sprint1 | 853 | 153 | 580 | 4-576 | 100% (g_iter4 24-0*) |
