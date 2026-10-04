# Ladder

20509 scrimmages (ours only), 20509 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter4 | 1921 +- 32 | 12 of 79 | 680 | 326-354 | 80.6% | 21.4% (vs 11) |
| g3lost | 1917 +- 112 | 13 of 79 | 40 | 15-25 | 80.5% | 21.0% (vs 11) |
| g2cr | 1896 +- 107 | 14 of 79 | 40 | 21-19 | 79.8% | 19.2% (vs 11) |
| g_iter3 | 1890 +- 13 | 16 of 79 | 3520 | 1259-2261 | 79.6% | 21.3% (vs 12) |
| g3escrg2 | 1889 +- 44 | 17 of 79 | 280 | 90-190 | 79.6% | 21.2% (vs 12) |
| g_iter2 | 1852 +- 15 | 18 of 79 | 2200 | 1032-1168 | 78.3% | 18.1% (vs 12) |
| arch_rush10 | 1777 +- 130 | 20 of 79 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| g_iter1_c2 | 1777 +- 130 | 21 of 79 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| a3dig5 | 1777 +- 130 | 22 of 79 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| e1aggr | 1752 +- 129 | 24 of 79 | 110 | 83-27 | 74.9% | 16.5% (vs 14) |
| c6pair | 1727 +- 128 | 25 of 79 | 110 | 82-28 | 74.0% | 14.8% (vs 14) |
| c5bank | 1727 +- 128 | 26 of 79 | 110 | 82-28 | 74.0% | 14.8% (vs 14) |
| a3dig10 | 1727 +- 128 | 27 of 79 | 110 | 82-28 | 74.0% | 14.8% (vs 14) |
| e2aggr | 1702 +- 127 | 29 of 79 | 110 | 81-29 | 73.1% | 15.6% (vs 15) |
| a2reloc | 1702 +- 127 | 30 of 79 | 110 | 81-29 | 73.1% | 15.6% (vs 15) |
| a2relay | 1702 +- 127 | 31 of 79 | 110 | 81-29 | 73.1% | 15.6% (vs 15) |
| b1z2b | 1687 +- 24 | 32 of 79 | 1760 | 620-1140 | 72.6% | 14.7% (vs 15) |
| g_iter1 | 1680 +- 10 | 33 of 79 | 6990 | 2485-4505 | 72.3% | 14.2% (vs 15) |
| g1copy | 1673 +- 91 | 34 of 79 | 80 | 28-52 | 72.1% | 13.8% (vs 15) |
| b2fs | 1673 +- 28 | 35 of 79 | 1280 | 439-841 | 72.1% | 13.8% (vs 15) |
| g1sym | 1667 +- 51 | 36 of 79 | 200 | 68-132 | 71.9% | 13.5% (vs 15) |
| b1v2 | 1661 +- 22 | 37 of 79 | 2120 | 711-1409 | 71.7% | 13.2% (vs 15) |
| arch_rush | 1356 +- 94 | 41 of 79 | 110 | 62-48 | 56.7% | 6.8% (vs 18) |
| g_iter0 | 1292 +- 89 | 44 of 79 | 109 | 56-53 | 52.1% | 9.2% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2299 | 78 | 580 | 561-19 | 4% (g_iter1 10-214) |
| 2 | Strequals.duck0127v5 | 2290 | 76 | 580 | 560-20 | 3% (g_iter1 7-217) |
| 3 | IvanGeffner.kuma | 2274 | 73 | 580 | 558-22 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2270 | 347 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2213 | 62 | 580 | 549-31 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2148 | 53 | 580 | 536-44 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2140 | 52 | 580 | 534-46 | 6% (g_iter1 13-211) |
| 8 | andrewgopher.player22 | 2073 | 45 | 580 | 515-65 | 11% (g_iter1 24-200) |
| 9 | Gymhgy.v10official | 2064 | 44 | 580 | 512-68 | 11% (g_iter1 25-199) |
| 10 | CyrilSharma.finalBot | 2021 | 12 | 3900 | 2744-1156 | 38% (g_iter4 84-140) |
| 11 | hsmalladi.finalbot | 2020 | 41 | 580 | 496-84 | 12% (g_iter1 27-197) |
| 12 | **us:g_iter4** | 1921 | 32 | 680 | 326-354 |  |
| 13 | **us:g3lost** | 1917 | 112 | 40 | 15-25 |  |
| 14 | **us:g2cr** | 1896 | 107 | 40 | 21-19 |  |
| 15 | winkelmantanner.waffle | 1893 | 15 | 2300 | 1375-925 | 62% (g_iter3 163-101) |
| 16 | **us:g_iter3** | 1890 | 13 | 3520 | 1259-2261 |  |
| 17 | **us:g3escrg2** | 1889 | 44 | 280 | 90-190 |  |
| 18 | **us:g_iter2** | 1852 | 15 | 2200 | 1032-1168 |  |
| 19 | ColtG5.Goob_final | 1784 | 12 | 3540 | 2205-1335 | 67% (g_iter2 203-101) |
| 20 | **us:arch_rush10** | 1777 | 130 | 110 | 84-26 |  |
| 21 | **us:g_iter1_c2** | 1777 | 130 | 110 | 84-26 |  |
| 22 | **us:a3dig5** | 1777 | 130 | 110 | 84-26 |  |
| 23 | kyleezz.jeeryfix3 | 1765 | 29 | 580 | 342-238 | 41% (g_iter1 91-133) |
| 24 | **us:e1aggr** | 1752 | 129 | 110 | 83-27 |  |
| 25 | **us:c6pair** | 1727 | 128 | 110 | 82-28 |  |
| 26 | **us:c5bank** | 1727 | 128 | 110 | 82-28 |  |
| 27 | **us:a3dig10** | 1727 | 128 | 110 | 82-28 |  |
| 28 | quesswho.cretplayer2_3 | 1721 | 29 | 580 | 307-273 | 40% (g_iter1 90-134) |
| 29 | **us:e2aggr** | 1702 | 127 | 110 | 81-29 |  |
| 30 | **us:a2reloc** | 1702 | 127 | 110 | 81-29 |  |
| 31 | **us:a2relay** | 1702 | 127 | 110 | 81-29 |  |
| 32 | **us:b1z2b** | 1687 | 24 | 1760 | 620-1140 |  |
| 33 | **us:g_iter1** | 1680 | 10 | 6990 | 2485-4505 |  |
| 34 | **us:g1copy** | 1673 | 91 | 80 | 28-52 |  |
| 35 | **us:b2fs** | 1673 | 28 | 1280 | 439-841 |  |
| 36 | **us:g1sym** | 1667 | 51 | 200 | 68-132 |  |
| 37 | **us:b1v2** | 1661 | 22 | 2120 | 711-1409 |  |
| 38 | SampleProvider.TSPAARKSPRINT1 | 1661 | 29 | 580 | 259-321 | 50% (g_iter1 112-112) |
| 39 | dmtrung14.defaultplayer_intlqualifier | 1498 | 33 | 580 | 142-438 | 69% (g_iter1 155-69) |
| 40 | clbarrell.duck8 | 1476 | 34 | 580 | 129-451 | 78% (g_iter1 175-49) |
| 41 | **us:arch_rush** | 1356 | 94 | 110 | 62-48 |  |
| 42 | jonters.bling3 | 1329 | 45 | 580 | 64-516 | 89% (g_iter1 200-24) |
| 43 | HugoIngelsson.Bot21 | 1297 | 200 | 26 | 3-23 |  |
| 44 | **us:g_iter0** | 1292 | 89 | 109 | 56-53 |  |
| 45 | Metta-AI.bc24scenario | 1230 | 223 | 26 | 2-24 |  |
| 46 | noahzemlin.honeyducklings | 1230 | 223 | 26 | 2-24 |  |
| 47 | sivakovivan.NewHide | 1230 | 223 | 26 | 2-24 |  |
| 48 | awu7.ExplosiveBot | 1210 | 60 | 580 | 34-546 | 97% (g_iter1 217-7) |
| 49 | JeffLegendPower.v11 | 1142 | 263 | 26 | 1-25 |  |
| 50 | MiloAkerman.v1 | 1142 | 263 | 26 | 1-25 |  |
| 51 | PerishoJ.tx | 1142 | 263 | 26 | 1-25 |  |
| 52 | Peter-Fun.dinoboxer | 1142 | 263 | 26 | 1-25 |  |
| 53 | RyanAspen.v22 | 1142 | 263 | 26 | 1-25 |  |
| 54 | TylerJulian.v9 | 1142 | 263 | 26 | 1-25 |  |
| 55 | aj-chau.cowards | 1142 | 263 | 26 | 1-25 |  |
| 56 | cViper971.ourplayer | 1142 | 263 | 26 | 1-25 |  |
| 57 | dylanzemlin.dangerduck2 | 1142 | 263 | 26 | 1-25 |  |
| 58 | lukerhoads.warrior_2nd_comp | 1142 | 263 | 26 | 1-25 |  |
| 59 | neilhuang007.baseline | 1142 | 263 | 26 | 1-25 |  |
| 60 | polyllc.polyv4 | 1142 | 263 | 26 | 1-25 |  |
| 61 | andrearante12.turtle | 1038 | 357 | 25 | 0-25 |  |
| 62 | AlexYu84.smartPlayer | 1005 | 357 | 26 | 0-26 |  |
| 63 | H4ffliger.keyboardcrusader_v1 | 1005 | 357 | 26 | 0-26 |  |
| 64 | Lithanium.AttackingBot | 1005 | 357 | 26 | 0-26 |  |
| 65 | Rubrasum.version_3 | 1005 | 357 | 26 | 0-26 |  |
| 66 | SriLakshmiPolavarapu.ducks | 1005 | 357 | 26 | 0-26 |  |
| 67 | VarunVejalla.alexander | 1005 | 357 | 26 | 0-26 |  |
| 68 | abdullah8a0.crayBasic | 1005 | 357 | 26 | 0-26 |  |
| 69 | adamseth2.moveBot1 | 1005 | 357 | 26 | 0-26 |  |
| 70 | dylanconklin.Team3 | 1005 | 357 | 26 | 0-26 |  |
| 71 | itswin.MPAttack | 1005 | 357 | 26 | 0-26 |  |
| 72 | joelcrouch.ducks | 1005 | 357 | 26 | 0-26 |  |
| 73 | lcforges.funkyguy3 | 1005 | 357 | 26 | 0-26 |  |
| 74 | qpwoeirut.tournament_sprint1 | 1005 | 357 | 26 | 0-26 |  |
| 75 | reeceyang.v5 | 1005 | 357 | 26 | 0-26 |  |
| 76 | samithShetty.combustiblelemon | 1005 | 357 | 26 | 0-26 |  |
| 77 | sayam-goyal.SimpleBot | 1005 | 357 | 26 | 0-26 |  |
| 78 | tlevietpdx.Sprint2 | 1005 | 357 | 26 | 0-26 |  |
| 79 | justinottesen.sprint1 | 859 | 153 | 580 | 4-576 | 100% (g_iter1 224-0) |
