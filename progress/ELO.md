# Ladder

22869 scrimmages (ours only), 22869 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter4 | 1975 +- 13 | 12 of 80 | 2920 | 1179-1741 | 81.4% | 23.4% (vs 11) |
| g3lost | 1958 +- 112 | 13 of 80 | 40 | 15-25 | 80.8% | 21.8% (vs 11) |
| g4pick | 1956 +- 65 | 14 of 80 | 120 | 43-77 | 80.7% | 21.7% (vs 11) |
| g2cr | 1932 +- 107 | 15 of 80 | 40 | 21-19 | 79.9% | 19.5% (vs 11) |
| g_iter3 | 1931 +- 13 | 16 of 80 | 3520 | 1259-2261 | 79.9% | 19.4% (vs 11) |
| g3escrg2 | 1931 +- 44 | 17 of 80 | 280 | 90-190 | 79.9% | 19.4% (vs 11) |
| g_iter2 | 1889 +- 15 | 19 of 80 | 2200 | 1032-1168 | 78.4% | 18.4% (vs 12) |
| arch_rush10 | 1808 +- 131 | 21 of 80 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| g_iter1_c2 | 1808 +- 131 | 22 of 80 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| a3dig5 | 1808 +- 131 | 23 of 80 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| e1aggr | 1783 +- 130 | 25 of 80 | 110 | 83-27 | 74.8% | 16.3% (vs 14) |
| c6pair | 1757 +- 129 | 26 of 80 | 110 | 82-28 | 74.0% | 14.6% (vs 14) |
| a3dig10 | 1757 +- 129 | 27 of 80 | 110 | 82-28 | 74.0% | 14.6% (vs 14) |
| c5bank | 1757 +- 129 | 28 of 80 | 110 | 82-28 | 74.0% | 14.6% (vs 14) |
| a2relay | 1733 +- 128 | 30 of 80 | 110 | 81-29 | 73.1% | 15.4% (vs 15) |
| e2aggr | 1733 +- 128 | 31 of 80 | 110 | 81-29 | 73.1% | 15.4% (vs 15) |
| a2reloc | 1733 +- 128 | 32 of 80 | 110 | 81-29 | 73.1% | 15.4% (vs 15) |
| b1z2b | 1721 +- 24 | 33 of 80 | 1760 | 620-1140 | 72.7% | 14.7% (vs 15) |
| g_iter1 | 1714 +- 10 | 34 of 80 | 6990 | 2485-4505 | 72.4% | 14.3% (vs 15) |
| g1copy | 1707 +- 91 | 35 of 80 | 80 | 28-52 | 72.2% | 13.8% (vs 15) |
| b2fs | 1707 +- 28 | 36 of 80 | 1280 | 439-841 | 72.2% | 13.8% (vs 15) |
| g1sym | 1702 +- 51 | 37 of 80 | 200 | 68-132 | 72.0% | 13.5% (vs 15) |
| b1v2 | 1696 +- 22 | 39 of 80 | 2120 | 711-1409 | 71.8% | 15.5% (vs 16) |
| arch_rush | 1383 +- 94 | 42 of 80 | 110 | 62-48 | 56.7% | 6.6% (vs 18) |
| g_iter0 | 1318 +- 89 | 45 of 80 | 109 | 56-53 | 52.1% | 9.0% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2337 | 78 | 580 | 561-19 | 4% (g_iter1 10-214) |
| 2 | Strequals.duck0127v5 | 2328 | 76 | 580 | 560-20 | 3% (g_iter1 7-217) |
| 3 | IvanGeffner.kuma | 2312 | 73 | 580 | 558-22 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2301 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2250 | 63 | 580 | 549-31 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2185 | 54 | 580 | 536-44 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2177 | 53 | 580 | 534-46 | 6% (g_iter1 13-211) |
| 8 | andrewgopher.player22 | 2110 | 45 | 580 | 515-65 | 11% (g_iter1 24-200) |
| 9 | CyrilSharma.finalBot | 2063 | 9 | 6060 | 4098-1962 | 37% (g_iter4 847-1417) |
| 10 | Gymhgy.v10official | 2061 | 32 | 780 | 622-158 | 45% (g_iter4 101-123) |
| 11 | hsmalladi.finalbot | 2057 | 41 | 580 | 496-84 | 12% (g_iter1 27-197) |
| 12 | **us:g_iter4** | 1975 | 13 | 2920 | 1179-1741 |  |
| 13 | **us:g3lost** | 1958 | 112 | 40 | 15-25 |  |
| 14 | **us:g4pick** | 1956 | 65 | 120 | 43-77 |  |
| 15 | **us:g2cr** | 1932 | 107 | 40 | 21-19 |  |
| 16 | **us:g_iter3** | 1931 | 13 | 3520 | 1259-2261 |  |
| 17 | **us:g3escrg2** | 1931 | 44 | 280 | 90-190 |  |
| 18 | winkelmantanner.waffle | 1929 | 15 | 2300 | 1375-925 | 62% (g_iter3 163-101) |
| 19 | **us:g_iter2** | 1889 | 15 | 2200 | 1032-1168 |  |
| 20 | ColtG5.Goob_final | 1819 | 12 | 3540 | 2205-1335 | 67% (g_iter2 203-101) |
| 21 | **us:arch_rush10** | 1808 | 131 | 110 | 84-26 |  |
| 22 | **us:g_iter1_c2** | 1808 | 131 | 110 | 84-26 |  |
| 23 | **us:a3dig5** | 1808 | 131 | 110 | 84-26 |  |
| 24 | kyleezz.jeeryfix3 | 1801 | 29 | 580 | 342-238 | 41% (g_iter1 91-133) |
| 25 | **us:e1aggr** | 1783 | 130 | 110 | 83-27 |  |
| 26 | **us:c6pair** | 1757 | 129 | 110 | 82-28 |  |
| 27 | **us:a3dig10** | 1757 | 129 | 110 | 82-28 |  |
| 28 | **us:c5bank** | 1757 | 129 | 110 | 82-28 |  |
| 29 | quesswho.cretplayer2_3 | 1756 | 29 | 580 | 307-273 | 40% (g_iter1 90-134) |
| 30 | **us:a2relay** | 1733 | 128 | 110 | 81-29 |  |
| 31 | **us:e2aggr** | 1733 | 128 | 110 | 81-29 |  |
| 32 | **us:a2reloc** | 1733 | 128 | 110 | 81-29 |  |
| 33 | **us:b1z2b** | 1721 | 24 | 1760 | 620-1140 |  |
| 34 | **us:g_iter1** | 1714 | 10 | 6990 | 2485-4505 |  |
| 35 | **us:g1copy** | 1707 | 91 | 80 | 28-52 |  |
| 36 | **us:b2fs** | 1707 | 28 | 1280 | 439-841 |  |
| 37 | **us:g1sym** | 1702 | 51 | 200 | 68-132 |  |
| 38 | SampleProvider.TSPAARKSPRINT1 | 1696 | 29 | 580 | 259-321 | 50% (g_iter1 112-112) |
| 39 | **us:b1v2** | 1696 | 22 | 2120 | 711-1409 |  |
| 40 | dmtrung14.defaultplayer_intlqualifier | 1533 | 33 | 580 | 142-438 | 69% (g_iter1 155-69) |
| 41 | clbarrell.duck8 | 1511 | 34 | 580 | 129-451 | 78% (g_iter1 175-49) |
| 42 | **us:arch_rush** | 1383 | 94 | 110 | 62-48 |  |
| 43 | jonters.bling3 | 1363 | 45 | 580 | 64-516 | 89% (g_iter1 200-24) |
| 44 | HugoIngelsson.Bot21 | 1322 | 201 | 26 | 3-23 |  |
| 45 | **us:g_iter0** | 1318 | 89 | 109 | 56-53 |  |
| 46 | Metta-AI.bc24scenario | 1255 | 224 | 26 | 2-24 |  |
| 47 | noahzemlin.honeyducklings | 1255 | 224 | 26 | 2-24 |  |
| 48 | sivakovivan.NewHide | 1255 | 224 | 26 | 2-24 |  |
| 49 | awu7.ExplosiveBot | 1244 | 60 | 580 | 34-546 | 97% (g_iter1 217-7) |
| 50 | JeffLegendPower.v11 | 1167 | 264 | 26 | 1-25 |  |
| 51 | MiloAkerman.v1 | 1167 | 264 | 26 | 1-25 |  |
| 52 | PerishoJ.tx | 1167 | 264 | 26 | 1-25 |  |
| 53 | Peter-Fun.dinoboxer | 1167 | 264 | 26 | 1-25 |  |
| 54 | RyanAspen.v22 | 1167 | 264 | 26 | 1-25 |  |
| 55 | TylerJulian.v9 | 1167 | 264 | 26 | 1-25 |  |
| 56 | aj-chau.cowards | 1167 | 264 | 26 | 1-25 |  |
| 57 | cViper971.ourplayer | 1167 | 264 | 26 | 1-25 |  |
| 58 | dylanzemlin.dangerduck2 | 1167 | 264 | 26 | 1-25 |  |
| 59 | lukerhoads.warrior_2nd_comp | 1167 | 264 | 26 | 1-25 |  |
| 60 | neilhuang007.baseline | 1167 | 264 | 26 | 1-25 |  |
| 61 | polyllc.polyv4 | 1167 | 264 | 26 | 1-25 |  |
| 62 | andrearante12.turtle | 1063 | 357 | 25 | 0-25 |  |
| 63 | AlexYu84.smartPlayer | 1030 | 357 | 26 | 0-26 |  |
| 64 | H4ffliger.keyboardcrusader_v1 | 1030 | 357 | 26 | 0-26 |  |
| 65 | Lithanium.AttackingBot | 1030 | 357 | 26 | 0-26 |  |
| 66 | Rubrasum.version_3 | 1030 | 357 | 26 | 0-26 |  |
| 67 | SriLakshmiPolavarapu.ducks | 1030 | 357 | 26 | 0-26 |  |
| 68 | VarunVejalla.alexander | 1030 | 357 | 26 | 0-26 |  |
| 69 | abdullah8a0.crayBasic | 1030 | 357 | 26 | 0-26 |  |
| 70 | adamseth2.moveBot1 | 1030 | 357 | 26 | 0-26 |  |
| 71 | dylanconklin.Team3 | 1030 | 357 | 26 | 0-26 |  |
| 72 | itswin.MPAttack | 1030 | 357 | 26 | 0-26 |  |
| 73 | joelcrouch.ducks | 1030 | 357 | 26 | 0-26 |  |
| 74 | lcforges.funkyguy3 | 1030 | 357 | 26 | 0-26 |  |
| 75 | qpwoeirut.tournament_sprint1 | 1030 | 357 | 26 | 0-26 |  |
| 76 | reeceyang.v5 | 1030 | 357 | 26 | 0-26 |  |
| 77 | samithShetty.combustiblelemon | 1030 | 357 | 26 | 0-26 |  |
| 78 | sayam-goyal.SimpleBot | 1030 | 357 | 26 | 0-26 |  |
| 79 | tlevietpdx.Sprint2 | 1030 | 357 | 26 | 0-26 |  |
| 80 | justinottesen.sprint1 | 893 | 153 | 580 | 4-576 | 100% (g_iter1 224-0) |
