# Ladder

25109 scrimmages (ours only), 25109 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter4 | 1967 +- 10 | 12 of 81 | 5000 | 2011-2989 | 81.5% | 23.8% (vs 11) |
| g4econ2 | 1962 +- 56 | 13 of 81 | 160 | 60-100 | 81.3% | 23.3% (vs 11) |
| g3lost | 1950 +- 112 | 14 of 81 | 40 | 15-25 | 80.9% | 22.2% (vs 11) |
| g4pick | 1949 +- 65 | 15 of 81 | 120 | 43-77 | 80.8% | 22.0% (vs 11) |
| g_iter3 | 1923 +- 13 | 16 of 81 | 3520 | 1259-2261 | 80.0% | 19.7% (vs 11) |
| g3escrg2 | 1923 +- 44 | 17 of 81 | 280 | 90-190 | 80.0% | 19.7% (vs 11) |
| g2cr | 1922 +- 107 | 18 of 81 | 40 | 21-19 | 79.9% | 19.6% (vs 11) |
| g_iter2 | 1878 +- 15 | 20 of 81 | 2200 | 1032-1168 | 78.5% | 18.5% (vs 12) |
| g_iter1_c2 | 1798 +- 131 | 22 of 81 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| a3dig5 | 1798 +- 131 | 23 of 81 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| arch_rush10 | 1798 +- 131 | 24 of 81 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| e1aggr | 1773 +- 130 | 26 of 81 | 110 | 83-27 | 74.9% | 16.4% (vs 14) |
| c6pair | 1748 +- 129 | 27 of 81 | 110 | 82-28 | 74.0% | 14.7% (vs 14) |
| c5bank | 1748 +- 129 | 28 of 81 | 110 | 82-28 | 74.0% | 14.7% (vs 14) |
| a3dig10 | 1748 +- 129 | 29 of 81 | 110 | 82-28 | 74.0% | 14.7% (vs 14) |
| a2relay | 1723 +- 127 | 31 of 81 | 110 | 81-29 | 73.1% | 15.5% (vs 15) |
| a2reloc | 1723 +- 127 | 32 of 81 | 110 | 81-29 | 73.1% | 15.5% (vs 15) |
| e2aggr | 1723 +- 127 | 33 of 81 | 110 | 81-29 | 73.1% | 15.5% (vs 15) |
| b1z2b | 1710 +- 24 | 34 of 81 | 1760 | 620-1140 | 72.7% | 14.7% (vs 15) |
| g_iter1 | 1704 +- 10 | 35 of 81 | 6990 | 2485-4505 | 72.4% | 14.3% (vs 15) |
| g1copy | 1697 +- 91 | 36 of 81 | 80 | 28-52 | 72.2% | 13.9% (vs 15) |
| b2fs | 1696 +- 28 | 37 of 81 | 1280 | 439-841 | 72.1% | 13.8% (vs 15) |
| g1sym | 1692 +- 51 | 38 of 81 | 200 | 68-132 | 72.0% | 13.6% (vs 15) |
| b1v2 | 1685 +- 22 | 40 of 81 | 2120 | 711-1409 | 71.7% | 15.5% (vs 16) |
| arch_rush | 1375 +- 94 | 43 of 81 | 110 | 62-48 | 56.7% | 6.7% (vs 18) |
| g_iter0 | 1310 +- 89 | 46 of 81 | 109 | 56-53 | 52.1% | 9.1% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2327 | 78 | 580 | 561-19 | 4% (g_iter1 10-214) |
| 2 | Strequals.duck0127v5 | 2318 | 76 | 580 | 560-20 | 3% (g_iter1 7-217) |
| 3 | IvanGeffner.kuma | 2302 | 73 | 580 | 558-22 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2292 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2240 | 63 | 580 | 549-31 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2175 | 54 | 580 | 536-44 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2167 | 53 | 580 | 534-46 | 6% (g_iter1 13-211) |
| 8 | andrewgopher.player22 | 2099 | 45 | 580 | 515-65 | 11% (g_iter1 24-200) |
| 9 | CyrilSharma.finalBot | 2055 | 9 | 6380 | 4300-2080 | 37% (g_iter4 905-1519) |
| 10 | hsmalladi.finalbot | 2047 | 41 | 580 | 496-84 | 12% (g_iter1 27-197) |
| 11 | Gymhgy.v10official | 2039 | 14 | 2700 | 1768-932 | 41% (g_iter4 875-1269) |
| 12 | **us:g_iter4** | 1967 | 10 | 5000 | 2011-2989 |  |
| 13 | **us:g4econ2** | 1962 | 56 | 160 | 60-100 |  |
| 14 | **us:g3lost** | 1950 | 112 | 40 | 15-25 |  |
| 15 | **us:g4pick** | 1949 | 65 | 120 | 43-77 |  |
| 16 | **us:g_iter3** | 1923 | 13 | 3520 | 1259-2261 |  |
| 17 | **us:g3escrg2** | 1923 | 44 | 280 | 90-190 |  |
| 18 | **us:g2cr** | 1922 | 107 | 40 | 21-19 |  |
| 19 | winkelmantanner.waffle | 1919 | 15 | 2300 | 1375-925 | 62% (g_iter3 163-101) |
| 20 | **us:g_iter2** | 1878 | 15 | 2200 | 1032-1168 |  |
| 21 | ColtG5.Goob_final | 1809 | 12 | 3540 | 2205-1335 | 67% (g_iter2 203-101) |
| 22 | **us:g_iter1_c2** | 1798 | 131 | 110 | 84-26 |  |
| 23 | **us:a3dig5** | 1798 | 131 | 110 | 84-26 |  |
| 24 | **us:arch_rush10** | 1798 | 131 | 110 | 84-26 |  |
| 25 | kyleezz.jeeryfix3 | 1790 | 29 | 580 | 342-238 | 41% (g_iter1 91-133) |
| 26 | **us:e1aggr** | 1773 | 130 | 110 | 83-27 |  |
| 27 | **us:c6pair** | 1748 | 129 | 110 | 82-28 |  |
| 28 | **us:c5bank** | 1748 | 129 | 110 | 82-28 |  |
| 29 | **us:a3dig10** | 1748 | 129 | 110 | 82-28 |  |
| 30 | quesswho.cretplayer2_3 | 1746 | 29 | 580 | 307-273 | 40% (g_iter1 90-134) |
| 31 | **us:a2relay** | 1723 | 127 | 110 | 81-29 |  |
| 32 | **us:a2reloc** | 1723 | 127 | 110 | 81-29 |  |
| 33 | **us:e2aggr** | 1723 | 127 | 110 | 81-29 |  |
| 34 | **us:b1z2b** | 1710 | 24 | 1760 | 620-1140 |  |
| 35 | **us:g_iter1** | 1704 | 10 | 6990 | 2485-4505 |  |
| 36 | **us:g1copy** | 1697 | 91 | 80 | 28-52 |  |
| 37 | **us:b2fs** | 1696 | 28 | 1280 | 439-841 |  |
| 38 | **us:g1sym** | 1692 | 51 | 200 | 68-132 |  |
| 39 | SampleProvider.TSPAARKSPRINT1 | 1686 | 29 | 580 | 259-321 | 50% (g_iter1 112-112) |
| 40 | **us:b1v2** | 1685 | 22 | 2120 | 711-1409 |  |
| 41 | dmtrung14.defaultplayer_intlqualifier | 1522 | 33 | 580 | 142-438 | 69% (g_iter1 155-69) |
| 42 | clbarrell.duck8 | 1500 | 34 | 580 | 129-451 | 78% (g_iter1 175-49) |
| 43 | **us:arch_rush** | 1375 | 94 | 110 | 62-48 |  |
| 44 | jonters.bling3 | 1353 | 45 | 580 | 64-516 | 89% (g_iter1 200-24) |
| 45 | HugoIngelsson.Bot21 | 1314 | 200 | 26 | 3-23 |  |
| 46 | **us:g_iter0** | 1310 | 89 | 109 | 56-53 |  |
| 47 | Metta-AI.bc24scenario | 1247 | 224 | 26 | 2-24 |  |
| 48 | noahzemlin.honeyducklings | 1247 | 224 | 26 | 2-24 |  |
| 49 | sivakovivan.NewHide | 1247 | 224 | 26 | 2-24 |  |
| 50 | awu7.ExplosiveBot | 1233 | 60 | 580 | 34-546 | 97% (g_iter1 217-7) |
| 51 | JeffLegendPower.v11 | 1160 | 264 | 26 | 1-25 |  |
| 52 | MiloAkerman.v1 | 1160 | 264 | 26 | 1-25 |  |
| 53 | PerishoJ.tx | 1160 | 264 | 26 | 1-25 |  |
| 54 | Peter-Fun.dinoboxer | 1160 | 264 | 26 | 1-25 |  |
| 55 | RyanAspen.v22 | 1160 | 264 | 26 | 1-25 |  |
| 56 | TylerJulian.v9 | 1160 | 264 | 26 | 1-25 |  |
| 57 | aj-chau.cowards | 1160 | 264 | 26 | 1-25 |  |
| 58 | cViper971.ourplayer | 1160 | 264 | 26 | 1-25 |  |
| 59 | dylanzemlin.dangerduck2 | 1160 | 264 | 26 | 1-25 |  |
| 60 | lukerhoads.warrior_2nd_comp | 1160 | 264 | 26 | 1-25 |  |
| 61 | neilhuang007.baseline | 1160 | 264 | 26 | 1-25 |  |
| 62 | polyllc.polyv4 | 1160 | 264 | 26 | 1-25 |  |
| 63 | andrearante12.turtle | 1055 | 357 | 25 | 0-25 |  |
| 64 | AlexYu84.smartPlayer | 1022 | 357 | 26 | 0-26 |  |
| 65 | H4ffliger.keyboardcrusader_v1 | 1022 | 357 | 26 | 0-26 |  |
| 66 | Lithanium.AttackingBot | 1022 | 357 | 26 | 0-26 |  |
| 67 | Rubrasum.version_3 | 1022 | 357 | 26 | 0-26 |  |
| 68 | SriLakshmiPolavarapu.ducks | 1022 | 357 | 26 | 0-26 |  |
| 69 | VarunVejalla.alexander | 1022 | 357 | 26 | 0-26 |  |
| 70 | abdullah8a0.crayBasic | 1022 | 357 | 26 | 0-26 |  |
| 71 | adamseth2.moveBot1 | 1022 | 357 | 26 | 0-26 |  |
| 72 | dylanconklin.Team3 | 1022 | 357 | 26 | 0-26 |  |
| 73 | itswin.MPAttack | 1022 | 357 | 26 | 0-26 |  |
| 74 | joelcrouch.ducks | 1022 | 357 | 26 | 0-26 |  |
| 75 | lcforges.funkyguy3 | 1022 | 357 | 26 | 0-26 |  |
| 76 | qpwoeirut.tournament_sprint1 | 1022 | 357 | 26 | 0-26 |  |
| 77 | reeceyang.v5 | 1022 | 357 | 26 | 0-26 |  |
| 78 | samithShetty.combustiblelemon | 1022 | 357 | 26 | 0-26 |  |
| 79 | sayam-goyal.SimpleBot | 1022 | 357 | 26 | 0-26 |  |
| 80 | tlevietpdx.Sprint2 | 1022 | 357 | 26 | 0-26 |  |
| 81 | justinottesen.sprint1 | 883 | 153 | 580 | 4-576 | 100% (g_iter1 224-0) |
