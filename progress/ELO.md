# Ladder

44429 scrimmages (ours only), 44429 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`), each pair of players counting at most 200 games (owner PROMPTS 191: the target filler plays one opponent thousands of times); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter7 | 2124 +- 25 | 6 of 87 | 6120 | 2236-3884 | 88.6% | 30.5% (vs 5) |
| g7ehp | 2119 +- 49 | 8 of 87 | 320 | 126-194 | 88.4% | 33.2% (vs 6) |
| g_iter6 | 1979 +- 23 | 12 of 87 | 1880 | 1029-851 | 83.9% | 25.6% (vs 9) |
| g_iter5 | 1952 +- 22 | 14 of 87 | 4440 | 2195-2245 | 83.1% | 25.4% (vs 10) |
| g4crumb | 1918 +- 49 | 15 of 87 | 640 | 258-382 | 81.9% | 22.1% (vs 10) |
| g4gym1 | 1917 +- 49 | 16 of 87 | 1880 | 755-1125 | 81.9% | 22.0% (vs 10) |
| g_iter4 | 1902 +- 25 | 17 of 87 | 9040 | 3645-5395 | 81.4% | 20.7% (vs 10) |
| g_iter3 | 1885 +- 28 | 18 of 87 | 3520 | 1259-2261 | 80.9% | 19.2% (vs 10) |
| g4econ2 | 1884 +- 56 | 19 of 87 | 160 | 60-100 | 80.8% | 19.2% (vs 10) |
| g3lost | 1873 +- 111 | 20 of 87 | 40 | 15-25 | 80.4% | 18.2% (vs 10) |
| g4pick | 1871 +- 65 | 21 of 87 | 120 | 43-77 | 80.4% | 18.0% (vs 10) |
| g2cr | 1869 +- 107 | 22 of 87 | 40 | 21-19 | 80.3% | 17.9% (vs 10) |
| g3escrg2 | 1844 +- 52 | 24 of 87 | 280 | 90-190 | 79.5% | 18.8% (vs 11) |
| g_iter2 | 1830 +- 28 | 26 of 87 | 2200 | 1032-1168 | 79.0% | 20.4% (vs 12) |
| g_iter1_c2 | 1739 +- 128 | 28 of 87 | 110 | 84-26 | 75.8% | 16.6% (vs 13) |
| arch_rush10 | 1739 +- 128 | 29 of 87 | 110 | 84-26 | 75.8% | 16.6% (vs 13) |
| a3dig5 | 1739 +- 128 | 30 of 87 | 110 | 84-26 | 75.8% | 16.6% (vs 13) |
| e1aggr | 1714 +- 127 | 32 of 87 | 110 | 83-27 | 75.0% | 17.3% (vs 14) |
| c5bank | 1690 +- 126 | 34 of 87 | 110 | 82-28 | 74.1% | 18.0% (vs 15) |
| a3dig10 | 1690 +- 126 | 35 of 87 | 110 | 82-28 | 74.1% | 18.0% (vs 15) |
| c6pair | 1690 +- 126 | 36 of 87 | 110 | 82-28 | 74.1% | 18.0% (vs 15) |
| e2aggr | 1667 +- 125 | 37 of 87 | 110 | 81-29 | 73.2% | 16.4% (vs 15) |
| a2relay | 1667 +- 125 | 38 of 87 | 110 | 81-29 | 73.2% | 16.4% (vs 15) |
| a2reloc | 1667 +- 125 | 39 of 87 | 110 | 81-29 | 73.2% | 16.4% (vs 15) |
| g_iter1 | 1662 +- 16 | 40 of 87 | 6990 | 2485-4505 | 73.1% | 16.1% (vs 15) |
| b1z2b | 1662 +- 24 | 41 of 87 | 1760 | 620-1140 | 73.0% | 16.0% (vs 15) |
| b2fs | 1647 +- 28 | 42 of 87 | 1280 | 439-841 | 72.5% | 15.1% (vs 15) |
| b1v2 | 1636 +- 22 | 43 of 87 | 2120 | 711-1409 | 72.1% | 14.4% (vs 15) |
| g1copy | 1628 +- 91 | 44 of 87 | 80 | 28-52 | 71.8% | 14.0% (vs 15) |
| g1sym | 1612 +- 51 | 46 of 87 | 200 | 68-132 | 71.1% | 15.2% (vs 16) |
| arch_rush | 1329 +- 94 | 49 of 87 | 110 | 62-48 | 56.8% | 7.2% (vs 18) |
| g_iter0 | 1265 +- 89 | 52 of 87 | 109 | 56-53 | 52.2% | 9.5% (vs 20) |

Our record = OUR win rate (our W-L) against the bot by the incumbent (g_iter7), whatever the count; * marks fewer than 30 games (+- 18 points at 95% for 30 games, +- 20 for 24); a bot the incumbent never met shows the most recent of our builds that did; blank if none has.

| rank | player | rating | +- 95% | games | W-L | our record |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2294 | 52 | 772 | 723-49 | 31% (g_iter7 15-33) |
| 2 | chenyx512.flagbot_final | 2293 | 52 | 772 | 722-50 | 19% (g_iter7 9-39) |
| 3 | jmerle.camel_case_v21_final | 2288 | 51 | 772 | 721-51 | 31% (g_iter7 15-33) |
| 4 | IvanGeffner.kuma | 2268 | 49 | 772 | 716-56 | 29% (g_iter7 14-34) |
| 5 | andli28.v9_USQuals_angle | 2197 | 28 | 6212 | 4456-1756 | 30% (g_iter7 1552-3616) |
| 6 | **us:g_iter7** | 2124 | 25 | 6120 | 2236-3884 |  |
| 7 | NotLLeon.v3 | 2123 | 36 | 812 | 682-130 | 55% (g_iter7 48-40) |
| 8 | **us:g7ehp** | 2119 | 49 | 320 | 126-194 |  |
| 9 | andrewgopher.player22 | 2102 | 37 | 772 | 651-121 | 52% (g_iter7 25-23) |
| 10 | hsmalladi.finalbot | 2062 | 35 | 772 | 630-142 | 54% (g_iter7 26-22) |
| 11 | Gymhgy.v10official | 1989 | 19 | 12852 | 7569-5283 | 81% (g_iter7 39-9) |
| 12 | **us:g_iter6** | 1979 | 23 | 1880 | 1029-851 |  |
| 13 | CyrilSharma.finalBot | 1977 | 19 | 6572 | 4389-2183 | 73% (g_iter7 35-13) |
| 14 | **us:g_iter5** | 1952 | 22 | 4440 | 2195-2245 |  |
| 15 | **us:g4crumb** | 1918 | 49 | 640 | 258-382 |  |
| 16 | **us:g4gym1** | 1917 | 49 | 1880 | 755-1125 |  |
| 17 | **us:g_iter4** | 1902 | 25 | 9040 | 3645-5395 |  |
| 18 | **us:g_iter3** | 1885 | 28 | 3520 | 1259-2261 |  |
| 19 | **us:g4econ2** | 1884 | 56 | 160 | 60-100 |  |
| 20 | **us:g3lost** | 1873 | 111 | 40 | 15-25 |  |
| 21 | **us:g4pick** | 1871 | 65 | 120 | 43-77 |  |
| 22 | **us:g2cr** | 1869 | 107 | 40 | 21-19 |  |
| 23 | winkelmantanner.waffle | 1865 | 22 | 2492 | 1434-1058 | 92% (g_iter7 44-4) |
| 24 | **us:g3escrg2** | 1844 | 52 | 280 | 90-190 |  |
| 25 | uravt.Version18Final | 1835 | 53 | 210 | 74-136 | 96% (g_iter7 46-2) |
| 26 | **us:g_iter2** | 1830 | 28 | 2200 | 1032-1168 |  |
| 27 | kyleezz.jeeryfix3 | 1741 | 27 | 772 | 376-396 | 90% (g_iter7 43-5) |
| 28 | **us:g_iter1_c2** | 1739 | 128 | 110 | 84-26 |  |
| 29 | **us:arch_rush10** | 1739 | 128 | 110 | 84-26 |  |
| 30 | **us:a3dig5** | 1739 | 128 | 110 | 84-26 |  |
| 31 | ColtG5.Goob_final | 1728 | 21 | 3732 | 2221-1511 | 96% (g_iter7 46-2) |
| 32 | **us:e1aggr** | 1714 | 127 | 110 | 83-27 |  |
| 33 | quesswho.cretplayer2_3 | 1696 | 27 | 772 | 335-437 | 90% (g_iter7 43-5) |
| 34 | **us:c5bank** | 1690 | 126 | 110 | 82-28 |  |
| 35 | **us:a3dig10** | 1690 | 126 | 110 | 82-28 |  |
| 36 | **us:c6pair** | 1690 | 126 | 110 | 82-28 |  |
| 37 | **us:e2aggr** | 1667 | 125 | 110 | 81-29 |  |
| 38 | **us:a2relay** | 1667 | 125 | 110 | 81-29 |  |
| 39 | **us:a2reloc** | 1667 | 125 | 110 | 81-29 |  |
| 40 | **us:g_iter1** | 1662 | 16 | 6990 | 2485-4505 |  |
| 41 | **us:b1z2b** | 1662 | 24 | 1760 | 620-1140 |  |
| 42 | **us:b2fs** | 1647 | 28 | 1280 | 439-841 |  |
| 43 | **us:b1v2** | 1636 | 22 | 2120 | 711-1409 |  |
| 44 | **us:g1copy** | 1628 | 91 | 80 | 28-52 |  |
| 45 | SampleProvider.TSPAARKSPRINT1 | 1621 | 28 | 772 | 266-506 | 96% (g_iter7 46-2) |
| 46 | **us:g1sym** | 1612 | 51 | 200 | 68-132 |  |
| 47 | dmtrung14.defaultplayer_intlqualifier | 1469 | 33 | 772 | 148-624 | 98% (g_iter7 47-1) |
| 48 | clbarrell.duck8 | 1445 | 34 | 772 | 132-640 | 100% (g_iter7 48-0) |
| 49 | **us:arch_rush** | 1329 | 94 | 110 | 62-48 |  |
| 50 | jonters.bling3 | 1302 | 45 | 772 | 66-706 | 98% (g_iter7 47-1) |
| 51 | HugoIngelsson.Bot21 | 1268 | 154 | 170 | 5-165 | 100% (g_iter7 48-0) |
| 52 | **us:g_iter0** | 1265 | 89 | 109 | 56-53 |  |
| 53 | Metta-AI.bc24scenario | 1202 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 54 | noahzemlin.honeyducklings | 1202 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 55 | sivakovivan.NewHide | 1202 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 56 | awu7.ExplosiveBot | 1188 | 60 | 628 | 34-594 | 100% (g_iter5 36-0) |
| 57 | JeffLegendPower.v11 | 1115 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 58 | MiloAkerman.v1 | 1115 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 59 | PerishoJ.tx | 1115 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 60 | Peter-Fun.dinoboxer | 1115 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 61 | RyanAspen.v22 | 1115 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 62 | TylerJulian.v9 | 1115 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 63 | aj-chau.cowards | 1115 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 64 | cViper971.ourplayer | 1115 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 65 | dylanzemlin.dangerduck2 | 1115 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 66 | lukerhoads.warrior_2nd_comp | 1115 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 67 | neilhuang007.baseline | 1115 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 68 | polyllc.polyv4 | 1115 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 69 | andrearante12.turtle | 1011 | 357 | 25 | 0-25 | 100% (g_iter1 2-0*) |
| 70 | AlexYu84.smartPlayer | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 71 | H4ffliger.keyboardcrusader_v1 | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 72 | Lithanium.AttackingBot | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 73 | Rubrasum.version_3 | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 74 | SriLakshmiPolavarapu.ducks | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 75 | VarunVejalla.alexander | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 76 | abdullah8a0.crayBasic | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 77 | adamseth2.moveBot1 | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 78 | dylanconklin.Team3 | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 79 | itswin.MPAttack | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 80 | joelcrouch.ducks | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 81 | lcforges.funkyguy3 | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 82 | qpwoeirut.tournament_sprint1 | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 83 | reeceyang.v5 | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 84 | samithShetty.combustiblelemon | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 85 | sayam-goyal.SimpleBot | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 86 | tlevietpdx.Sprint2 | 978 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 87 | justinottesen.sprint1 | 842 | 153 | 628 | 4-624 | 100% (g_iter5 36-0) |
