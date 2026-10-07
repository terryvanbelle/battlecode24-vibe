# Ladder

42749 scrimmages (ours only), 42749 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`), each pair of players counting at most 200 games (owner PROMPTS 191: the target filler plays one opponent thousands of times); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter7 | 2136 +- 25 | 6 of 86 | 4760 | 1852-2908 | 88.6% | 30.7% (vs 5) |
| g_iter6 | 1989 +- 23 | 11 of 86 | 1880 | 1029-851 | 84.0% | 25.6% (vs 9) |
| g_iter5 | 1963 +- 22 | 13 of 86 | 4440 | 2195-2245 | 83.1% | 25.4% (vs 10) |
| g4crumb | 1928 +- 49 | 14 of 86 | 640 | 258-382 | 81.9% | 22.1% (vs 10) |
| g4gym1 | 1927 +- 49 | 15 of 86 | 1880 | 755-1125 | 81.9% | 22.0% (vs 10) |
| g_iter4 | 1913 +- 25 | 16 of 86 | 9040 | 3645-5395 | 81.4% | 20.7% (vs 10) |
| g_iter3 | 1896 +- 28 | 17 of 86 | 3520 | 1259-2261 | 80.9% | 19.2% (vs 10) |
| g4econ2 | 1895 +- 56 | 18 of 86 | 160 | 60-100 | 80.8% | 19.1% (vs 10) |
| g3lost | 1883 +- 111 | 19 of 86 | 40 | 15-25 | 80.4% | 18.2% (vs 10) |
| g4pick | 1881 +- 65 | 20 of 86 | 120 | 43-77 | 80.4% | 18.0% (vs 10) |
| g2cr | 1879 +- 107 | 21 of 86 | 40 | 21-19 | 80.3% | 17.9% (vs 10) |
| g3escrg2 | 1854 +- 52 | 23 of 86 | 280 | 90-190 | 79.5% | 18.8% (vs 11) |
| g_iter2 | 1841 +- 28 | 25 of 86 | 2200 | 1032-1168 | 79.0% | 20.4% (vs 12) |
| g_iter1_c2 | 1748 +- 128 | 27 of 86 | 110 | 84-26 | 75.8% | 16.5% (vs 13) |
| a3dig5 | 1748 +- 128 | 28 of 86 | 110 | 84-26 | 75.8% | 16.5% (vs 13) |
| arch_rush10 | 1748 +- 128 | 29 of 86 | 110 | 84-26 | 75.8% | 16.5% (vs 13) |
| e1aggr | 1723 +- 127 | 31 of 86 | 110 | 83-27 | 74.9% | 17.2% (vs 14) |
| c5bank | 1699 +- 126 | 33 of 86 | 110 | 82-28 | 74.1% | 17.9% (vs 15) |
| a3dig10 | 1699 +- 126 | 34 of 86 | 110 | 82-28 | 74.1% | 17.9% (vs 15) |
| c6pair | 1699 +- 126 | 35 of 86 | 110 | 82-28 | 74.1% | 17.9% (vs 15) |
| e2aggr | 1676 +- 125 | 36 of 86 | 110 | 81-29 | 73.2% | 16.3% (vs 15) |
| a2reloc | 1676 +- 125 | 37 of 86 | 110 | 81-29 | 73.2% | 16.3% (vs 15) |
| a2relay | 1676 +- 125 | 38 of 86 | 110 | 81-29 | 73.2% | 16.3% (vs 15) |
| g_iter1 | 1672 +- 16 | 39 of 86 | 6990 | 2485-4505 | 73.1% | 16.1% (vs 15) |
| b1z2b | 1672 +- 24 | 40 of 86 | 1760 | 620-1140 | 73.1% | 16.0% (vs 15) |
| b2fs | 1657 +- 28 | 41 of 86 | 1280 | 439-841 | 72.5% | 15.1% (vs 15) |
| b1v2 | 1646 +- 22 | 42 of 86 | 2120 | 711-1409 | 72.1% | 14.4% (vs 15) |
| g1copy | 1638 +- 91 | 43 of 86 | 80 | 28-52 | 71.8% | 13.9% (vs 15) |
| g1sym | 1622 +- 51 | 45 of 86 | 200 | 68-132 | 71.2% | 15.2% (vs 16) |
| arch_rush | 1337 +- 94 | 48 of 86 | 110 | 62-48 | 56.8% | 7.1% (vs 18) |
| g_iter0 | 1273 +- 89 | 51 of 86 | 109 | 56-53 | 52.2% | 9.4% (vs 20) |

Our record = OUR win rate (our W-L) against the bot by the incumbent (g_iter7), whatever the count; * marks fewer than 30 games (+- 18 points at 95% for 30 games, +- 20 for 24); a bot the incumbent never met shows the most recent of our builds that did; blank if none has.

| rank | player | rating | +- 95% | games | W-L | our record |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2305 | 52 | 772 | 723-49 | 31% (g_iter7 15-33) |
| 2 | chenyx512.flagbot_final | 2304 | 52 | 772 | 722-50 | 19% (g_iter7 9-39) |
| 3 | jmerle.camel_case_v21_final | 2298 | 51 | 772 | 721-51 | 31% (g_iter7 15-33) |
| 4 | IvanGeffner.kuma | 2279 | 49 | 772 | 716-56 | 29% (g_iter7 14-34) |
| 5 | andli28.v9_USQuals_angle | 2208 | 34 | 4532 | 3286-1246 | 31% (g_iter7 1168-2640) |
| 6 | **us:g_iter7** | 2136 | 25 | 4760 | 1852-2908 |  |
| 7 | NotLLeon.v3 | 2134 | 36 | 812 | 682-130 | 55% (g_iter7 48-40) |
| 8 | andrewgopher.player22 | 2112 | 37 | 772 | 651-121 | 52% (g_iter7 25-23) |
| 9 | hsmalladi.finalbot | 2073 | 35 | 772 | 630-142 | 54% (g_iter7 26-22) |
| 10 | Gymhgy.v10official | 1999 | 19 | 12852 | 7569-5283 | 81% (g_iter7 39-9) |
| 11 | **us:g_iter6** | 1989 | 23 | 1880 | 1029-851 |  |
| 12 | CyrilSharma.finalBot | 1987 | 19 | 6572 | 4389-2183 | 73% (g_iter7 35-13) |
| 13 | **us:g_iter5** | 1963 | 22 | 4440 | 2195-2245 |  |
| 14 | **us:g4crumb** | 1928 | 49 | 640 | 258-382 |  |
| 15 | **us:g4gym1** | 1927 | 49 | 1880 | 755-1125 |  |
| 16 | **us:g_iter4** | 1913 | 25 | 9040 | 3645-5395 |  |
| 17 | **us:g_iter3** | 1896 | 28 | 3520 | 1259-2261 |  |
| 18 | **us:g4econ2** | 1895 | 56 | 160 | 60-100 |  |
| 19 | **us:g3lost** | 1883 | 111 | 40 | 15-25 |  |
| 20 | **us:g4pick** | 1881 | 65 | 120 | 43-77 |  |
| 21 | **us:g2cr** | 1879 | 107 | 40 | 21-19 |  |
| 22 | winkelmantanner.waffle | 1875 | 22 | 2492 | 1434-1058 | 92% (g_iter7 44-4) |
| 23 | **us:g3escrg2** | 1854 | 52 | 280 | 90-190 |  |
| 24 | uravt.Version18Final | 1846 | 53 | 210 | 74-136 | 96% (g_iter7 46-2) |
| 25 | **us:g_iter2** | 1841 | 28 | 2200 | 1032-1168 |  |
| 26 | kyleezz.jeeryfix3 | 1751 | 27 | 772 | 376-396 | 90% (g_iter7 43-5) |
| 27 | **us:g_iter1_c2** | 1748 | 128 | 110 | 84-26 |  |
| 28 | **us:a3dig5** | 1748 | 128 | 110 | 84-26 |  |
| 29 | **us:arch_rush10** | 1748 | 128 | 110 | 84-26 |  |
| 30 | ColtG5.Goob_final | 1738 | 21 | 3732 | 2221-1511 | 96% (g_iter7 46-2) |
| 31 | **us:e1aggr** | 1723 | 127 | 110 | 83-27 |  |
| 32 | quesswho.cretplayer2_3 | 1706 | 27 | 772 | 335-437 | 90% (g_iter7 43-5) |
| 33 | **us:c5bank** | 1699 | 126 | 110 | 82-28 |  |
| 34 | **us:a3dig10** | 1699 | 126 | 110 | 82-28 |  |
| 35 | **us:c6pair** | 1699 | 126 | 110 | 82-28 |  |
| 36 | **us:e2aggr** | 1676 | 125 | 110 | 81-29 |  |
| 37 | **us:a2reloc** | 1676 | 125 | 110 | 81-29 |  |
| 38 | **us:a2relay** | 1676 | 125 | 110 | 81-29 |  |
| 39 | **us:g_iter1** | 1672 | 16 | 6990 | 2485-4505 |  |
| 40 | **us:b1z2b** | 1672 | 24 | 1760 | 620-1140 |  |
| 41 | **us:b2fs** | 1657 | 28 | 1280 | 439-841 |  |
| 42 | **us:b1v2** | 1646 | 22 | 2120 | 711-1409 |  |
| 43 | **us:g1copy** | 1638 | 91 | 80 | 28-52 |  |
| 44 | SampleProvider.TSPAARKSPRINT1 | 1631 | 28 | 772 | 266-506 | 96% (g_iter7 46-2) |
| 45 | **us:g1sym** | 1622 | 51 | 200 | 68-132 |  |
| 46 | dmtrung14.defaultplayer_intlqualifier | 1479 | 33 | 772 | 148-624 | 98% (g_iter7 47-1) |
| 47 | clbarrell.duck8 | 1455 | 34 | 772 | 132-640 | 100% (g_iter7 48-0) |
| 48 | **us:arch_rush** | 1337 | 94 | 110 | 62-48 |  |
| 49 | jonters.bling3 | 1312 | 45 | 772 | 66-706 | 98% (g_iter7 47-1) |
| 50 | HugoIngelsson.Bot21 | 1276 | 154 | 170 | 5-165 | 100% (g_iter7 48-0) |
| 51 | **us:g_iter0** | 1273 | 89 | 109 | 56-53 |  |
| 52 | Metta-AI.bc24scenario | 1210 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 53 | noahzemlin.honeyducklings | 1210 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 54 | sivakovivan.NewHide | 1210 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 55 | awu7.ExplosiveBot | 1198 | 60 | 628 | 34-594 | 100% (g_iter5 36-0) |
| 56 | JeffLegendPower.v11 | 1123 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 57 | MiloAkerman.v1 | 1123 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 58 | PerishoJ.tx | 1123 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 59 | Peter-Fun.dinoboxer | 1123 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 60 | RyanAspen.v22 | 1123 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 61 | TylerJulian.v9 | 1123 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 62 | aj-chau.cowards | 1123 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 63 | cViper971.ourplayer | 1123 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 64 | dylanzemlin.dangerduck2 | 1123 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 65 | lukerhoads.warrior_2nd_comp | 1123 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 66 | neilhuang007.baseline | 1123 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 67 | polyllc.polyv4 | 1123 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 68 | andrearante12.turtle | 1018 | 357 | 25 | 0-25 | 100% (g_iter1 2-0*) |
| 69 | AlexYu84.smartPlayer | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 70 | H4ffliger.keyboardcrusader_v1 | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 71 | Lithanium.AttackingBot | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 72 | Rubrasum.version_3 | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 73 | SriLakshmiPolavarapu.ducks | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 74 | VarunVejalla.alexander | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 75 | abdullah8a0.crayBasic | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 76 | adamseth2.moveBot1 | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 77 | dylanconklin.Team3 | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 78 | itswin.MPAttack | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 79 | joelcrouch.ducks | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 80 | lcforges.funkyguy3 | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 81 | qpwoeirut.tournament_sprint1 | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 82 | reeceyang.v5 | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 83 | samithShetty.combustiblelemon | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 84 | sayam-goyal.SimpleBot | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 85 | tlevietpdx.Sprint2 | 985 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 86 | justinottesen.sprint1 | 852 | 153 | 628 | 4-624 | 100% (g_iter5 36-0) |
