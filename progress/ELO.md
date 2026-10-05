# Ladder

28629 scrimmages (ours only), 28629 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g4crumb | 1954 +- 27 | 12 of 83 | 640 | 258-382 | 81.7% | 24.5% (vs 11) |
| g4gym1 | 1949 +- 21 | 13 of 83 | 1080 | 427-653 | 81.5% | 24.0% (vs 11) |
| g_iter4 | 1943 +- 9 | 14 of 83 | 6800 | 2679-4121 | 81.3% | 23.4% (vs 11) |
| g4econ2 | 1939 +- 56 | 15 of 83 | 160 | 60-100 | 81.2% | 23.0% (vs 11) |
| g3lost | 1928 +- 112 | 16 of 83 | 40 | 15-25 | 80.8% | 21.9% (vs 11) |
| g4pick | 1926 +- 65 | 17 of 83 | 120 | 43-77 | 80.7% | 21.7% (vs 11) |
| g2cr | 1902 +- 107 | 18 of 83 | 40 | 21-19 | 79.9% | 19.6% (vs 11) |
| g_iter3 | 1900 +- 13 | 19 of 83 | 3520 | 1259-2261 | 79.9% | 19.5% (vs 11) |
| g3escrg2 | 1900 +- 44 | 20 of 83 | 280 | 90-190 | 79.9% | 19.5% (vs 11) |
| g_iter2 | 1858 +- 15 | 22 of 83 | 2200 | 1032-1168 | 78.4% | 18.4% (vs 12) |
| g_iter1_c2 | 1780 +- 130 | 24 of 83 | 110 | 84-26 | 75.8% | 15.7% (vs 13) |
| arch_rush10 | 1780 +- 130 | 25 of 83 | 110 | 84-26 | 75.8% | 15.7% (vs 13) |
| a3dig5 | 1780 +- 130 | 26 of 83 | 110 | 84-26 | 75.8% | 15.7% (vs 13) |
| e1aggr | 1755 +- 129 | 28 of 83 | 110 | 83-27 | 74.9% | 16.5% (vs 14) |
| a3dig10 | 1730 +- 128 | 29 of 83 | 110 | 82-28 | 74.0% | 14.9% (vs 14) |
| c5bank | 1730 +- 128 | 30 of 83 | 110 | 82-28 | 74.0% | 14.9% (vs 14) |
| c6pair | 1730 +- 128 | 31 of 83 | 110 | 82-28 | 74.0% | 14.9% (vs 14) |
| a2relay | 1706 +- 127 | 33 of 83 | 110 | 81-29 | 73.2% | 15.7% (vs 15) |
| e2aggr | 1706 +- 127 | 34 of 83 | 110 | 81-29 | 73.2% | 15.7% (vs 15) |
| a2reloc | 1706 +- 127 | 35 of 83 | 110 | 81-29 | 73.2% | 15.7% (vs 15) |
| b1z2b | 1690 +- 24 | 36 of 83 | 1760 | 620-1140 | 72.6% | 14.7% (vs 15) |
| g_iter1 | 1683 +- 10 | 37 of 83 | 6990 | 2485-4505 | 72.3% | 14.3% (vs 15) |
| g1copy | 1677 +- 91 | 38 of 83 | 80 | 28-52 | 72.1% | 13.9% (vs 15) |
| b2fs | 1676 +- 28 | 39 of 83 | 1280 | 439-841 | 72.1% | 13.8% (vs 15) |
| g1sym | 1671 +- 51 | 40 of 83 | 200 | 68-132 | 71.9% | 13.6% (vs 15) |
| b1v2 | 1665 +- 22 | 42 of 83 | 2120 | 711-1409 | 71.7% | 15.5% (vs 16) |
| arch_rush | 1359 +- 94 | 45 of 83 | 110 | 62-48 | 56.7% | 6.8% (vs 18) |
| g_iter0 | 1295 +- 89 | 48 of 83 | 109 | 56-53 | 52.1% | 9.2% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2306 | 78 | 580 | 561-19 | 4% (g_iter1 10-214) |
| 2 | Strequals.duck0127v5 | 2297 | 76 | 580 | 560-20 | 3% (g_iter1 7-217) |
| 3 | IvanGeffner.kuma | 2281 | 73 | 580 | 558-22 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2274 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2219 | 63 | 580 | 549-31 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2155 | 54 | 580 | 536-44 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2146 | 53 | 580 | 534-46 | 6% (g_iter1 13-211) |
| 8 | andrewgopher.player22 | 2079 | 45 | 580 | 515-65 | 11% (g_iter1 24-200) |
| 9 | CyrilSharma.finalBot | 2032 | 9 | 6380 | 4300-2080 | 37% (g_iter4 905-1519) |
| 10 | hsmalladi.finalbot | 2026 | 41 | 580 | 496-84 | 12% (g_iter1 27-197) |
| 11 | Gymhgy.v10official | 2023 | 9 | 6220 | 3935-2285 | 39% (g_iter4 1543-2401) |
| 12 | **us:g4crumb** | 1954 | 27 | 640 | 258-382 |  |
| 13 | **us:g4gym1** | 1949 | 21 | 1080 | 427-653 |  |
| 14 | **us:g_iter4** | 1943 | 9 | 6800 | 2679-4121 |  |
| 15 | **us:g4econ2** | 1939 | 56 | 160 | 60-100 |  |
| 16 | **us:g3lost** | 1928 | 112 | 40 | 15-25 |  |
| 17 | **us:g4pick** | 1926 | 65 | 120 | 43-77 |  |
| 18 | **us:g2cr** | 1902 | 107 | 40 | 21-19 |  |
| 19 | **us:g_iter3** | 1900 | 13 | 3520 | 1259-2261 |  |
| 20 | **us:g3escrg2** | 1900 | 44 | 280 | 90-190 |  |
| 21 | winkelmantanner.waffle | 1898 | 15 | 2300 | 1375-925 | 62% (g_iter3 163-101) |
| 22 | **us:g_iter2** | 1858 | 15 | 2200 | 1032-1168 |  |
| 23 | ColtG5.Goob_final | 1788 | 12 | 3540 | 2205-1335 | 67% (g_iter2 203-101) |
| 24 | **us:g_iter1_c2** | 1780 | 130 | 110 | 84-26 |  |
| 25 | **us:arch_rush10** | 1780 | 130 | 110 | 84-26 |  |
| 26 | **us:a3dig5** | 1780 | 130 | 110 | 84-26 |  |
| 27 | kyleezz.jeeryfix3 | 1770 | 29 | 580 | 342-238 | 41% (g_iter1 91-133) |
| 28 | **us:e1aggr** | 1755 | 129 | 110 | 83-27 |  |
| 29 | **us:a3dig10** | 1730 | 128 | 110 | 82-28 |  |
| 30 | **us:c5bank** | 1730 | 128 | 110 | 82-28 |  |
| 31 | **us:c6pair** | 1730 | 128 | 110 | 82-28 |  |
| 32 | quesswho.cretplayer2_3 | 1725 | 29 | 580 | 307-273 | 40% (g_iter1 90-134) |
| 33 | **us:a2relay** | 1706 | 127 | 110 | 81-29 |  |
| 34 | **us:e2aggr** | 1706 | 127 | 110 | 81-29 |  |
| 35 | **us:a2reloc** | 1706 | 127 | 110 | 81-29 |  |
| 36 | **us:b1z2b** | 1690 | 24 | 1760 | 620-1140 |  |
| 37 | **us:g_iter1** | 1683 | 10 | 6990 | 2485-4505 |  |
| 38 | **us:g1copy** | 1677 | 91 | 80 | 28-52 |  |
| 39 | **us:b2fs** | 1676 | 28 | 1280 | 439-841 |  |
| 40 | **us:g1sym** | 1671 | 51 | 200 | 68-132 |  |
| 41 | SampleProvider.TSPAARKSPRINT1 | 1665 | 29 | 580 | 259-321 | 50% (g_iter1 112-112) |
| 42 | **us:b1v2** | 1665 | 22 | 2120 | 711-1409 |  |
| 43 | dmtrung14.defaultplayer_intlqualifier | 1502 | 33 | 580 | 142-438 | 69% (g_iter1 155-69) |
| 44 | clbarrell.duck8 | 1480 | 34 | 580 | 129-451 | 78% (g_iter1 175-49) |
| 45 | **us:arch_rush** | 1359 | 94 | 110 | 62-48 |  |
| 46 | jonters.bling3 | 1333 | 45 | 580 | 64-516 | 89% (g_iter1 200-24) |
| 47 | HugoIngelsson.Bot21 | 1299 | 200 | 26 | 3-23 |  |
| 48 | **us:g_iter0** | 1295 | 89 | 109 | 56-53 |  |
| 49 | Metta-AI.bc24scenario | 1233 | 223 | 26 | 2-24 |  |
| 50 | noahzemlin.honeyducklings | 1233 | 223 | 26 | 2-24 |  |
| 51 | sivakovivan.NewHide | 1233 | 223 | 26 | 2-24 |  |
| 52 | awu7.ExplosiveBot | 1213 | 60 | 580 | 34-546 | 97% (g_iter1 217-7) |
| 53 | JeffLegendPower.v11 | 1145 | 263 | 26 | 1-25 |  |
| 54 | MiloAkerman.v1 | 1145 | 263 | 26 | 1-25 |  |
| 55 | PerishoJ.tx | 1145 | 263 | 26 | 1-25 |  |
| 56 | Peter-Fun.dinoboxer | 1145 | 263 | 26 | 1-25 |  |
| 57 | RyanAspen.v22 | 1145 | 263 | 26 | 1-25 |  |
| 58 | TylerJulian.v9 | 1145 | 263 | 26 | 1-25 |  |
| 59 | aj-chau.cowards | 1145 | 263 | 26 | 1-25 |  |
| 60 | cViper971.ourplayer | 1145 | 263 | 26 | 1-25 |  |
| 61 | dylanzemlin.dangerduck2 | 1145 | 263 | 26 | 1-25 |  |
| 62 | lukerhoads.warrior_2nd_comp | 1145 | 263 | 26 | 1-25 |  |
| 63 | neilhuang007.baseline | 1145 | 263 | 26 | 1-25 |  |
| 64 | polyllc.polyv4 | 1145 | 263 | 26 | 1-25 |  |
| 65 | andrearante12.turtle | 1041 | 357 | 25 | 0-25 |  |
| 66 | AlexYu84.smartPlayer | 1008 | 357 | 26 | 0-26 |  |
| 67 | H4ffliger.keyboardcrusader_v1 | 1008 | 357 | 26 | 0-26 |  |
| 68 | Lithanium.AttackingBot | 1008 | 357 | 26 | 0-26 |  |
| 69 | Rubrasum.version_3 | 1008 | 357 | 26 | 0-26 |  |
| 70 | SriLakshmiPolavarapu.ducks | 1008 | 357 | 26 | 0-26 |  |
| 71 | VarunVejalla.alexander | 1008 | 357 | 26 | 0-26 |  |
| 72 | abdullah8a0.crayBasic | 1008 | 357 | 26 | 0-26 |  |
| 73 | adamseth2.moveBot1 | 1008 | 357 | 26 | 0-26 |  |
| 74 | dylanconklin.Team3 | 1008 | 357 | 26 | 0-26 |  |
| 75 | itswin.MPAttack | 1008 | 357 | 26 | 0-26 |  |
| 76 | joelcrouch.ducks | 1008 | 357 | 26 | 0-26 |  |
| 77 | lcforges.funkyguy3 | 1008 | 357 | 26 | 0-26 |  |
| 78 | qpwoeirut.tournament_sprint1 | 1008 | 357 | 26 | 0-26 |  |
| 79 | reeceyang.v5 | 1008 | 357 | 26 | 0-26 |  |
| 80 | samithShetty.combustiblelemon | 1008 | 357 | 26 | 0-26 |  |
| 81 | sayam-goyal.SimpleBot | 1008 | 357 | 26 | 0-26 |  |
| 82 | tlevietpdx.Sprint2 | 1008 | 357 | 26 | 0-26 |  |
| 83 | justinottesen.sprint1 | 863 | 153 | 580 | 4-576 | 100% (g_iter1 224-0) |
