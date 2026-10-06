# Ladder

39629 scrimmages (ours only), 39629 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter7 | 2122 +- 20 | 7 of 86 | 1640 | 891-749 | 88.2% | 32.2% (vs 6) |
| g_iter6 | 1992 +- 18 | 11 of 86 | 1880 | 1029-851 | 84.1% | 26.0% (vs 9) |
| g_iter5 | 1964 +- 11 | 13 of 86 | 4440 | 2195-2245 | 83.1% | 25.5% (vs 10) |
| g4crumb | 1920 +- 27 | 14 of 86 | 640 | 258-382 | 81.7% | 21.3% (vs 10) |
| g4gym1 | 1919 +- 16 | 15 of 86 | 1880 | 755-1125 | 81.6% | 21.2% (vs 10) |
| g_iter4 | 1917 +- 7 | 16 of 86 | 9040 | 3645-5395 | 81.6% | 21.1% (vs 10) |
| g4econ2 | 1914 +- 56 | 17 of 86 | 160 | 60-100 | 81.5% | 20.7% (vs 10) |
| g3lost | 1902 +- 112 | 18 of 86 | 40 | 15-25 | 81.1% | 19.8% (vs 10) |
| g4pick | 1900 +- 65 | 19 of 86 | 120 | 43-77 | 81.0% | 19.6% (vs 10) |
| g2cr | 1879 +- 107 | 20 of 86 | 40 | 21-19 | 80.3% | 17.8% (vs 10) |
| g_iter3 | 1876 +- 13 | 21 of 86 | 3520 | 1259-2261 | 80.2% | 17.6% (vs 10) |
| g3escrg2 | 1874 +- 44 | 23 of 86 | 280 | 90-190 | 80.1% | 20.4% (vs 11) |
| g_iter2 | 1835 +- 15 | 25 of 86 | 2200 | 1032-1168 | 78.8% | 19.9% (vs 12) |
| arch_rush10 | 1748 +- 129 | 27 of 86 | 110 | 84-26 | 75.8% | 16.4% (vs 13) |
| g_iter1_c2 | 1748 +- 129 | 28 of 86 | 110 | 84-26 | 75.8% | 16.4% (vs 13) |
| a3dig5 | 1748 +- 129 | 29 of 86 | 110 | 84-26 | 75.8% | 16.4% (vs 13) |
| e1aggr | 1723 +- 128 | 31 of 86 | 110 | 83-27 | 74.9% | 17.1% (vs 14) |
| a3dig10 | 1699 +- 126 | 33 of 86 | 110 | 82-28 | 74.1% | 17.7% (vs 15) |
| c5bank | 1699 +- 126 | 34 of 86 | 110 | 82-28 | 74.1% | 17.7% (vs 15) |
| c6pair | 1699 +- 126 | 35 of 86 | 110 | 82-28 | 74.1% | 17.7% (vs 15) |
| a2relay | 1675 +- 125 | 36 of 86 | 110 | 81-29 | 73.2% | 16.1% (vs 15) |
| e2aggr | 1675 +- 125 | 37 of 86 | 110 | 81-29 | 73.2% | 16.1% (vs 15) |
| a2reloc | 1675 +- 125 | 38 of 86 | 110 | 81-29 | 73.2% | 16.1% (vs 15) |
| b1z2b | 1672 +- 24 | 39 of 86 | 1760 | 620-1140 | 73.1% | 15.9% (vs 15) |
| g_iter1 | 1660 +- 10 | 40 of 86 | 6990 | 2485-4505 | 72.6% | 15.1% (vs 15) |
| b2fs | 1657 +- 28 | 41 of 86 | 1280 | 439-841 | 72.5% | 15.0% (vs 15) |
| g1copy | 1653 +- 91 | 42 of 86 | 80 | 28-52 | 72.4% | 14.7% (vs 15) |
| b1v2 | 1646 +- 22 | 43 of 86 | 2120 | 711-1409 | 72.1% | 14.2% (vs 15) |
| g1sym | 1643 +- 51 | 44 of 86 | 200 | 68-132 | 72.0% | 14.1% (vs 15) |
| arch_rush | 1335 +- 94 | 48 of 86 | 110 | 62-48 | 56.8% | 7.1% (vs 18) |
| g_iter0 | 1270 +- 89 | 51 of 86 | 109 | 56-53 | 52.2% | 9.4% (vs 20) |

Our record = OUR win rate (our W-L) against the bot by the incumbent (g_iter7), whatever the count; * marks fewer than 30 games (+- 18 points at 95% for 30 games, +- 20 for 24); a bot the incumbent never met shows the most recent of our builds that did; blank if none has.

| rank | player | rating | +- 95% | games | W-L | our record |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2300 | 52 | 772 | 723-49 | 31% (g_iter7 15-33) |
| 2 | chenyx512.flagbot_final | 2296 | 51 | 772 | 722-50 | 19% (g_iter7 9-39) |
| 3 | jmerle.camel_case_v21_final | 2292 | 51 | 772 | 721-51 | 31% (g_iter7 15-33) |
| 4 | IvanGeffner.kuma | 2273 | 49 | 772 | 716-56 | 29% (g_iter7 14-34) |
| 5 | andli28.v9_USQuals_angle | 2237 | 24 | 1412 | 1127-285 | 30% (g_iter7 207-481) |
| 6 | NotLLeon.v3 | 2128 | 35 | 812 | 682-130 | 55% (g_iter7 48-40) |
| 7 | **us:g_iter7** | 2122 | 20 | 1640 | 891-749 |  |
| 8 | andrewgopher.player22 | 2106 | 36 | 772 | 651-121 | 52% (g_iter7 25-23) |
| 9 | hsmalladi.finalbot | 2067 | 34 | 772 | 630-142 | 54% (g_iter7 26-22) |
| 10 | CyrilSharma.finalBot | 2006 | 9 | 6572 | 4389-2183 | 73% (g_iter7 35-13) |
| 11 | **us:g_iter6** | 1992 | 18 | 1880 | 1029-851 |  |
| 12 | Gymhgy.v10official | 1989 | 6 | 12852 | 7569-5283 | 81% (g_iter7 39-9) |
| 13 | **us:g_iter5** | 1964 | 11 | 4440 | 2195-2245 |  |
| 14 | **us:g4crumb** | 1920 | 27 | 640 | 258-382 |  |
| 15 | **us:g4gym1** | 1919 | 16 | 1880 | 755-1125 |  |
| 16 | **us:g_iter4** | 1917 | 7 | 9040 | 3645-5395 |  |
| 17 | **us:g4econ2** | 1914 | 56 | 160 | 60-100 |  |
| 18 | **us:g3lost** | 1902 | 112 | 40 | 15-25 |  |
| 19 | **us:g4pick** | 1900 | 65 | 120 | 43-77 |  |
| 20 | **us:g2cr** | 1879 | 107 | 40 | 21-19 |  |
| 21 | **us:g_iter3** | 1876 | 13 | 3520 | 1259-2261 |  |
| 22 | winkelmantanner.waffle | 1875 | 14 | 2492 | 1434-1058 | 92% (g_iter7 44-4) |
| 23 | **us:g3escrg2** | 1874 | 44 | 280 | 90-190 |  |
| 24 | uravt.Version18Final | 1846 | 53 | 210 | 74-136 | 96% (g_iter7 46-2) |
| 25 | **us:g_iter2** | 1835 | 15 | 2200 | 1032-1168 |  |
| 26 | ColtG5.Goob_final | 1760 | 12 | 3732 | 2221-1511 | 96% (g_iter7 46-2) |
| 27 | **us:arch_rush10** | 1748 | 129 | 110 | 84-26 |  |
| 28 | **us:g_iter1_c2** | 1748 | 129 | 110 | 84-26 |  |
| 29 | **us:a3dig5** | 1748 | 129 | 110 | 84-26 |  |
| 30 | kyleezz.jeeryfix3 | 1746 | 27 | 772 | 376-396 | 90% (g_iter7 43-5) |
| 31 | **us:e1aggr** | 1723 | 128 | 110 | 83-27 |  |
| 32 | quesswho.cretplayer2_3 | 1702 | 27 | 772 | 335-437 | 90% (g_iter7 43-5) |
| 33 | **us:a3dig10** | 1699 | 126 | 110 | 82-28 |  |
| 34 | **us:c5bank** | 1699 | 126 | 110 | 82-28 |  |
| 35 | **us:c6pair** | 1699 | 126 | 110 | 82-28 |  |
| 36 | **us:a2relay** | 1675 | 125 | 110 | 81-29 |  |
| 37 | **us:e2aggr** | 1675 | 125 | 110 | 81-29 |  |
| 38 | **us:a2reloc** | 1675 | 125 | 110 | 81-29 |  |
| 39 | **us:b1z2b** | 1672 | 24 | 1760 | 620-1140 |  |
| 40 | **us:g_iter1** | 1660 | 10 | 6990 | 2485-4505 |  |
| 41 | **us:b2fs** | 1657 | 28 | 1280 | 439-841 |  |
| 42 | **us:g1copy** | 1653 | 91 | 80 | 28-52 |  |
| 43 | **us:b1v2** | 1646 | 22 | 2120 | 711-1409 |  |
| 44 | **us:g1sym** | 1643 | 51 | 200 | 68-132 |  |
| 45 | SampleProvider.TSPAARKSPRINT1 | 1627 | 27 | 772 | 266-506 | 96% (g_iter7 46-2) |
| 46 | dmtrung14.defaultplayer_intlqualifier | 1476 | 32 | 772 | 148-624 | 98% (g_iter7 47-1) |
| 47 | clbarrell.duck8 | 1450 | 34 | 772 | 132-640 | 100% (g_iter7 48-0) |
| 48 | **us:arch_rush** | 1335 | 94 | 110 | 62-48 |  |
| 49 | jonters.bling3 | 1307 | 44 | 772 | 66-706 | 98% (g_iter7 47-1) |
| 50 | HugoIngelsson.Bot21 | 1275 | 154 | 170 | 5-165 | 100% (g_iter7 48-0) |
| 51 | **us:g_iter0** | 1270 | 89 | 109 | 56-53 |  |
| 52 | Metta-AI.bc24scenario | 1208 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 53 | noahzemlin.honeyducklings | 1208 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 54 | sivakovivan.NewHide | 1208 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 55 | awu7.ExplosiveBot | 1189 | 60 | 628 | 34-594 | 100% (g_iter5 36-0) |
| 56 | JeffLegendPower.v11 | 1121 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 57 | MiloAkerman.v1 | 1121 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 58 | PerishoJ.tx | 1121 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 59 | Peter-Fun.dinoboxer | 1121 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 60 | RyanAspen.v22 | 1121 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 61 | TylerJulian.v9 | 1121 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 62 | aj-chau.cowards | 1121 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 63 | cViper971.ourplayer | 1121 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 64 | dylanzemlin.dangerduck2 | 1121 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 65 | lukerhoads.warrior_2nd_comp | 1121 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 66 | neilhuang007.baseline | 1121 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 67 | polyllc.polyv4 | 1121 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 68 | andrearante12.turtle | 1017 | 357 | 25 | 0-25 | 100% (g_iter1 2-0*) |
| 69 | AlexYu84.smartPlayer | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 70 | H4ffliger.keyboardcrusader_v1 | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 71 | Lithanium.AttackingBot | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 72 | Rubrasum.version_3 | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 73 | SriLakshmiPolavarapu.ducks | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 74 | VarunVejalla.alexander | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 75 | abdullah8a0.crayBasic | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 76 | adamseth2.moveBot1 | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 77 | dylanconklin.Team3 | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 78 | itswin.MPAttack | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 79 | joelcrouch.ducks | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 80 | lcforges.funkyguy3 | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 81 | qpwoeirut.tournament_sprint1 | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 82 | reeceyang.v5 | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 83 | samithShetty.combustiblelemon | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 84 | sayam-goyal.SimpleBot | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 85 | tlevietpdx.Sprint2 | 984 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 86 | justinottesen.sprint1 | 839 | 153 | 628 | 4-624 | 100% (g_iter5 36-0) |
