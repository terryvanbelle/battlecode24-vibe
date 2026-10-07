# Ladder

45269 scrimmages (ours only), 45269 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`), each pair of players counting at most 200 games (owner PROMPTS 191: the target filler plays one opponent thousands of times); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter7 | 2113 +- 25 | 6 of 88 | 6720 | 2401-4319 | 88.6% | 30.5% (vs 5) |
| g7ehp | 2108 +- 49 | 8 of 88 | 320 | 126-194 | 88.4% | 33.1% (vs 6) |
| g7kite | 2042 +- 52 | 11 of 88 | 240 | 74-166 | 86.3% | 30.6% (vs 8) |
| g_iter6 | 1969 +- 23 | 13 of 88 | 1880 | 1029-851 | 83.9% | 25.6% (vs 9) |
| g_iter5 | 1943 +- 22 | 15 of 88 | 4440 | 2195-2245 | 83.1% | 25.4% (vs 10) |
| g4crumb | 1908 +- 49 | 16 of 88 | 640 | 258-382 | 81.9% | 22.1% (vs 10) |
| g4gym1 | 1907 +- 49 | 17 of 88 | 1880 | 755-1125 | 81.9% | 22.0% (vs 10) |
| g_iter4 | 1893 +- 25 | 18 of 88 | 9040 | 3645-5395 | 81.4% | 20.7% (vs 10) |
| g_iter3 | 1876 +- 28 | 19 of 88 | 3520 | 1259-2261 | 80.8% | 19.3% (vs 10) |
| g4econ2 | 1875 +- 56 | 20 of 88 | 160 | 60-100 | 80.8% | 19.2% (vs 10) |
| g3lost | 1864 +- 111 | 21 of 88 | 40 | 15-25 | 80.4% | 18.3% (vs 10) |
| g4pick | 1861 +- 65 | 22 of 88 | 120 | 43-77 | 80.4% | 18.1% (vs 10) |
| g2cr | 1859 +- 107 | 23 of 88 | 40 | 21-19 | 80.3% | 17.9% (vs 10) |
| g3escrg2 | 1834 +- 52 | 25 of 88 | 280 | 90-190 | 79.4% | 18.8% (vs 11) |
| g_iter2 | 1821 +- 28 | 27 of 88 | 2200 | 1032-1168 | 79.0% | 20.4% (vs 12) |
| g_iter1_c2 | 1730 +- 128 | 29 of 88 | 110 | 84-26 | 75.8% | 16.6% (vs 13) |
| arch_rush10 | 1730 +- 128 | 30 of 88 | 110 | 84-26 | 75.8% | 16.6% (vs 13) |
| a3dig5 | 1730 +- 128 | 31 of 88 | 110 | 84-26 | 75.8% | 16.6% (vs 13) |
| e1aggr | 1705 +- 127 | 33 of 88 | 110 | 83-27 | 75.0% | 17.4% (vs 14) |
| c5bank | 1682 +- 126 | 35 of 88 | 110 | 82-28 | 74.1% | 18.0% (vs 15) |
| a3dig10 | 1682 +- 126 | 36 of 88 | 110 | 82-28 | 74.1% | 18.0% (vs 15) |
| c6pair | 1682 +- 126 | 37 of 88 | 110 | 82-28 | 74.1% | 18.0% (vs 15) |
| e2aggr | 1658 +- 124 | 38 of 88 | 110 | 81-29 | 73.2% | 16.4% (vs 15) |
| a2relay | 1658 +- 124 | 39 of 88 | 110 | 81-29 | 73.2% | 16.4% (vs 15) |
| a2reloc | 1658 +- 124 | 40 of 88 | 110 | 81-29 | 73.2% | 16.4% (vs 15) |
| g_iter1 | 1653 +- 16 | 41 of 88 | 6990 | 2485-4505 | 73.0% | 16.1% (vs 15) |
| b1z2b | 1652 +- 24 | 42 of 88 | 1760 | 620-1140 | 73.0% | 16.0% (vs 15) |
| b2fs | 1638 +- 28 | 43 of 88 | 1280 | 439-841 | 72.5% | 15.1% (vs 15) |
| b1v2 | 1626 +- 22 | 44 of 88 | 2120 | 711-1409 | 72.0% | 14.4% (vs 15) |
| g1copy | 1619 +- 91 | 45 of 88 | 80 | 28-52 | 71.8% | 14.0% (vs 15) |
| g1sym | 1602 +- 51 | 47 of 88 | 200 | 68-132 | 71.1% | 15.2% (vs 16) |
| arch_rush | 1321 +- 94 | 50 of 88 | 110 | 62-48 | 56.8% | 7.2% (vs 18) |
| g_iter0 | 1257 +- 89 | 53 of 88 | 109 | 56-53 | 52.2% | 9.5% (vs 20) |

Our record = OUR win rate (our W-L) against the bot by the incumbent (g_iter7), whatever the count; * marks fewer than 30 games (+- 18 points at 95% for 30 games, +- 20 for 24); a bot the incumbent never met shows the most recent of our builds that did; blank if none has.

| rank | player | rating | +- 95% | games | W-L | our record |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2285 | 52 | 772 | 723-49 | 31% (g_iter7 15-33) |
| 2 | chenyx512.flagbot_final | 2283 | 52 | 772 | 722-50 | 19% (g_iter7 9-39) |
| 3 | jmerle.camel_case_v21_final | 2278 | 51 | 772 | 721-51 | 31% (g_iter7 15-33) |
| 4 | IvanGeffner.kuma | 2258 | 49 | 772 | 716-56 | 29% (g_iter7 14-34) |
| 5 | andli28.v9_USQuals_angle | 2187 | 25 | 7052 | 5057-1995 | 30% (g_iter7 1717-4051) |
| 6 | **us:g_iter7** | 2113 | 25 | 6720 | 2401-4319 |  |
| 7 | NotLLeon.v3 | 2113 | 36 | 812 | 682-130 | 55% (g_iter7 48-40) |
| 8 | **us:g7ehp** | 2108 | 49 | 320 | 126-194 |  |
| 9 | andrewgopher.player22 | 2092 | 37 | 772 | 651-121 | 52% (g_iter7 25-23) |
| 10 | hsmalladi.finalbot | 2053 | 35 | 772 | 630-142 | 54% (g_iter7 26-22) |
| 11 | **us:g7kite** | 2042 | 52 | 240 | 74-166 |  |
| 12 | Gymhgy.v10official | 1979 | 19 | 12852 | 7569-5283 | 81% (g_iter7 39-9) |
| 13 | **us:g_iter6** | 1969 | 23 | 1880 | 1029-851 |  |
| 14 | CyrilSharma.finalBot | 1967 | 19 | 6572 | 4389-2183 | 73% (g_iter7 35-13) |
| 15 | **us:g_iter5** | 1943 | 22 | 4440 | 2195-2245 |  |
| 16 | **us:g4crumb** | 1908 | 49 | 640 | 258-382 |  |
| 17 | **us:g4gym1** | 1907 | 49 | 1880 | 755-1125 |  |
| 18 | **us:g_iter4** | 1893 | 25 | 9040 | 3645-5395 |  |
| 19 | **us:g_iter3** | 1876 | 28 | 3520 | 1259-2261 |  |
| 20 | **us:g4econ2** | 1875 | 56 | 160 | 60-100 |  |
| 21 | **us:g3lost** | 1864 | 111 | 40 | 15-25 |  |
| 22 | **us:g4pick** | 1861 | 65 | 120 | 43-77 |  |
| 23 | **us:g2cr** | 1859 | 107 | 40 | 21-19 |  |
| 24 | winkelmantanner.waffle | 1856 | 22 | 2492 | 1434-1058 | 92% (g_iter7 44-4) |
| 25 | **us:g3escrg2** | 1834 | 52 | 280 | 90-190 |  |
| 26 | uravt.Version18Final | 1826 | 53 | 210 | 74-136 | 96% (g_iter7 46-2) |
| 27 | **us:g_iter2** | 1821 | 28 | 2200 | 1032-1168 |  |
| 28 | kyleezz.jeeryfix3 | 1731 | 27 | 772 | 376-396 | 90% (g_iter7 43-5) |
| 29 | **us:g_iter1_c2** | 1730 | 128 | 110 | 84-26 |  |
| 30 | **us:arch_rush10** | 1730 | 128 | 110 | 84-26 |  |
| 31 | **us:a3dig5** | 1730 | 128 | 110 | 84-26 |  |
| 32 | ColtG5.Goob_final | 1718 | 21 | 3732 | 2221-1511 | 96% (g_iter7 46-2) |
| 33 | **us:e1aggr** | 1705 | 127 | 110 | 83-27 |  |
| 34 | quesswho.cretplayer2_3 | 1686 | 27 | 772 | 335-437 | 90% (g_iter7 43-5) |
| 35 | **us:c5bank** | 1682 | 126 | 110 | 82-28 |  |
| 36 | **us:a3dig10** | 1682 | 126 | 110 | 82-28 |  |
| 37 | **us:c6pair** | 1682 | 126 | 110 | 82-28 |  |
| 38 | **us:e2aggr** | 1658 | 124 | 110 | 81-29 |  |
| 39 | **us:a2relay** | 1658 | 124 | 110 | 81-29 |  |
| 40 | **us:a2reloc** | 1658 | 124 | 110 | 81-29 |  |
| 41 | **us:g_iter1** | 1653 | 16 | 6990 | 2485-4505 |  |
| 42 | **us:b1z2b** | 1652 | 24 | 1760 | 620-1140 |  |
| 43 | **us:b2fs** | 1638 | 28 | 1280 | 439-841 |  |
| 44 | **us:b1v2** | 1626 | 22 | 2120 | 711-1409 |  |
| 45 | **us:g1copy** | 1619 | 91 | 80 | 28-52 |  |
| 46 | SampleProvider.TSPAARKSPRINT1 | 1611 | 28 | 772 | 266-506 | 96% (g_iter7 46-2) |
| 47 | **us:g1sym** | 1602 | 51 | 200 | 68-132 |  |
| 48 | dmtrung14.defaultplayer_intlqualifier | 1459 | 33 | 772 | 148-624 | 98% (g_iter7 47-1) |
| 49 | clbarrell.duck8 | 1436 | 34 | 772 | 132-640 | 100% (g_iter7 48-0) |
| 50 | **us:arch_rush** | 1321 | 94 | 110 | 62-48 |  |
| 51 | jonters.bling3 | 1293 | 45 | 772 | 66-706 | 98% (g_iter7 47-1) |
| 52 | HugoIngelsson.Bot21 | 1259 | 154 | 170 | 5-165 | 100% (g_iter7 48-0) |
| 53 | **us:g_iter0** | 1257 | 89 | 109 | 56-53 |  |
| 54 | Metta-AI.bc24scenario | 1195 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 55 | noahzemlin.honeyducklings | 1195 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 56 | sivakovivan.NewHide | 1195 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 57 | awu7.ExplosiveBot | 1179 | 60 | 628 | 34-594 | 100% (g_iter5 36-0) |
| 58 | JeffLegendPower.v11 | 1108 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 59 | MiloAkerman.v1 | 1108 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 60 | PerishoJ.tx | 1108 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 61 | Peter-Fun.dinoboxer | 1108 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 62 | RyanAspen.v22 | 1108 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 63 | TylerJulian.v9 | 1108 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 64 | aj-chau.cowards | 1108 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 65 | cViper971.ourplayer | 1108 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 66 | dylanzemlin.dangerduck2 | 1108 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 67 | lukerhoads.warrior_2nd_comp | 1108 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 68 | neilhuang007.baseline | 1108 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 69 | polyllc.polyv4 | 1108 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 70 | andrearante12.turtle | 1004 | 357 | 25 | 0-25 | 100% (g_iter1 2-0*) |
| 71 | AlexYu84.smartPlayer | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 72 | H4ffliger.keyboardcrusader_v1 | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 73 | Lithanium.AttackingBot | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 74 | Rubrasum.version_3 | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 75 | SriLakshmiPolavarapu.ducks | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 76 | VarunVejalla.alexander | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 77 | abdullah8a0.crayBasic | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 78 | adamseth2.moveBot1 | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 79 | dylanconklin.Team3 | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 80 | itswin.MPAttack | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 81 | joelcrouch.ducks | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 82 | lcforges.funkyguy3 | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 83 | qpwoeirut.tournament_sprint1 | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 84 | reeceyang.v5 | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 85 | samithShetty.combustiblelemon | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 86 | sayam-goyal.SimpleBot | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 87 | tlevietpdx.Sprint2 | 970 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 88 | justinottesen.sprint1 | 832 | 153 | 628 | 4-624 | 100% (g_iter5 36-0) |
