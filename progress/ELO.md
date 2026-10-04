# Ladder

21069 scrimmages (ours only), 21069 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter4 | 1909 +- 23 | 12 of 80 | 1120 | 482-638 | 80.6% | 21.2% (vs 11) |
| g3lost | 1906 +- 112 | 13 of 80 | 40 | 15-25 | 80.5% | 21.0% (vs 11) |
| g4pick | 1904 +- 65 | 14 of 80 | 120 | 43-77 | 80.4% | 20.8% (vs 11) |
| g2cr | 1886 +- 107 | 15 of 80 | 40 | 21-19 | 79.8% | 19.2% (vs 11) |
| g_iter3 | 1880 +- 13 | 17 of 80 | 3520 | 1259-2261 | 79.6% | 21.3% (vs 12) |
| g3escrg2 | 1878 +- 44 | 18 of 80 | 280 | 90-190 | 79.5% | 21.2% (vs 12) |
| g_iter2 | 1842 +- 15 | 19 of 80 | 2200 | 1032-1168 | 78.3% | 18.1% (vs 12) |
| g_iter1_c2 | 1767 +- 130 | 21 of 80 | 110 | 84-26 | 75.8% | 15.7% (vs 13) |
| arch_rush10 | 1767 +- 130 | 22 of 80 | 110 | 84-26 | 75.8% | 15.7% (vs 13) |
| a3dig5 | 1767 +- 130 | 23 of 80 | 110 | 84-26 | 75.8% | 15.7% (vs 13) |
| e1aggr | 1742 +- 129 | 25 of 80 | 110 | 83-27 | 74.9% | 16.5% (vs 14) |
| a3dig10 | 1718 +- 128 | 26 of 80 | 110 | 82-28 | 74.0% | 14.9% (vs 14) |
| c5bank | 1718 +- 128 | 27 of 80 | 110 | 82-28 | 74.0% | 14.9% (vs 14) |
| c6pair | 1718 +- 128 | 28 of 80 | 110 | 82-28 | 74.0% | 14.9% (vs 14) |
| a2relay | 1693 +- 127 | 30 of 80 | 110 | 81-29 | 73.2% | 15.7% (vs 15) |
| e2aggr | 1693 +- 127 | 31 of 80 | 110 | 81-29 | 73.2% | 15.7% (vs 15) |
| a2reloc | 1693 +- 127 | 32 of 80 | 110 | 81-29 | 73.2% | 15.7% (vs 15) |
| b1z2b | 1676 +- 24 | 33 of 80 | 1760 | 620-1140 | 72.6% | 14.7% (vs 15) |
| g_iter1 | 1669 +- 10 | 34 of 80 | 6990 | 2485-4505 | 72.3% | 14.2% (vs 15) |
| g1copy | 1663 +- 91 | 35 of 80 | 80 | 28-52 | 72.1% | 13.9% (vs 15) |
| b2fs | 1662 +- 28 | 36 of 80 | 1280 | 439-841 | 72.0% | 13.8% (vs 15) |
| g1sym | 1657 +- 51 | 37 of 80 | 200 | 68-132 | 71.8% | 13.5% (vs 15) |
| b1v2 | 1651 +- 22 | 38 of 80 | 2120 | 711-1409 | 71.6% | 13.2% (vs 15) |
| arch_rush | 1348 +- 94 | 42 of 80 | 110 | 62-48 | 56.7% | 6.9% (vs 18) |
| g_iter0 | 1284 +- 89 | 45 of 80 | 109 | 56-53 | 52.1% | 9.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2288 | 78 | 580 | 561-19 | 4% (g_iter1 10-214) |
| 2 | Strequals.duck0127v5 | 2280 | 76 | 580 | 560-20 | 3% (g_iter1 7-217) |
| 3 | IvanGeffner.kuma | 2263 | 73 | 580 | 558-22 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2261 | 347 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2202 | 62 | 580 | 549-31 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2137 | 53 | 580 | 536-44 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2129 | 52 | 580 | 534-46 | 6% (g_iter1 13-211) |
| 8 | andrewgopher.player22 | 2062 | 45 | 580 | 515-65 | 11% (g_iter1 24-200) |
| 9 | Gymhgy.v10official | 2053 | 44 | 580 | 512-68 | 11% (g_iter1 25-199) |
| 10 | CyrilSharma.finalBot | 2010 | 11 | 4460 | 3105-1355 | 36% (g_iter4 240-424) |
| 11 | hsmalladi.finalbot | 2010 | 41 | 580 | 496-84 | 12% (g_iter1 27-197) |
| 12 | **us:g_iter4** | 1909 | 23 | 1120 | 482-638 |  |
| 13 | **us:g3lost** | 1906 | 112 | 40 | 15-25 |  |
| 14 | **us:g4pick** | 1904 | 65 | 120 | 43-77 |  |
| 15 | **us:g2cr** | 1886 | 107 | 40 | 21-19 |  |
| 16 | winkelmantanner.waffle | 1882 | 15 | 2300 | 1375-925 | 62% (g_iter3 163-101) |
| 17 | **us:g_iter3** | 1880 | 13 | 3520 | 1259-2261 |  |
| 18 | **us:g3escrg2** | 1878 | 44 | 280 | 90-190 |  |
| 19 | **us:g_iter2** | 1842 | 15 | 2200 | 1032-1168 |  |
| 20 | ColtG5.Goob_final | 1774 | 12 | 3540 | 2205-1335 | 67% (g_iter2 203-101) |
| 21 | **us:g_iter1_c2** | 1767 | 130 | 110 | 84-26 |  |
| 22 | **us:arch_rush10** | 1767 | 130 | 110 | 84-26 |  |
| 23 | **us:a3dig5** | 1767 | 130 | 110 | 84-26 |  |
| 24 | kyleezz.jeeryfix3 | 1755 | 29 | 580 | 342-238 | 41% (g_iter1 91-133) |
| 25 | **us:e1aggr** | 1742 | 129 | 110 | 83-27 |  |
| 26 | **us:a3dig10** | 1718 | 128 | 110 | 82-28 |  |
| 27 | **us:c5bank** | 1718 | 128 | 110 | 82-28 |  |
| 28 | **us:c6pair** | 1718 | 128 | 110 | 82-28 |  |
| 29 | quesswho.cretplayer2_3 | 1711 | 29 | 580 | 307-273 | 40% (g_iter1 90-134) |
| 30 | **us:a2relay** | 1693 | 127 | 110 | 81-29 |  |
| 31 | **us:e2aggr** | 1693 | 127 | 110 | 81-29 |  |
| 32 | **us:a2reloc** | 1693 | 127 | 110 | 81-29 |  |
| 33 | **us:b1z2b** | 1676 | 24 | 1760 | 620-1140 |  |
| 34 | **us:g_iter1** | 1669 | 10 | 6990 | 2485-4505 |  |
| 35 | **us:g1copy** | 1663 | 91 | 80 | 28-52 |  |
| 36 | **us:b2fs** | 1662 | 28 | 1280 | 439-841 |  |
| 37 | **us:g1sym** | 1657 | 51 | 200 | 68-132 |  |
| 38 | **us:b1v2** | 1651 | 22 | 2120 | 711-1409 |  |
| 39 | SampleProvider.TSPAARKSPRINT1 | 1651 | 29 | 580 | 259-321 | 50% (g_iter1 112-112) |
| 40 | dmtrung14.defaultplayer_intlqualifier | 1488 | 33 | 580 | 142-438 | 69% (g_iter1 155-69) |
| 41 | clbarrell.duck8 | 1466 | 34 | 580 | 129-451 | 78% (g_iter1 175-49) |
| 42 | **us:arch_rush** | 1348 | 94 | 110 | 62-48 |  |
| 43 | jonters.bling3 | 1319 | 45 | 580 | 64-516 | 89% (g_iter1 200-24) |
| 44 | HugoIngelsson.Bot21 | 1289 | 200 | 26 | 3-23 |  |
| 45 | **us:g_iter0** | 1284 | 89 | 109 | 56-53 |  |
| 46 | Metta-AI.bc24scenario | 1222 | 223 | 26 | 2-24 |  |
| 47 | noahzemlin.honeyducklings | 1222 | 223 | 26 | 2-24 |  |
| 48 | sivakovivan.NewHide | 1222 | 223 | 26 | 2-24 |  |
| 49 | awu7.ExplosiveBot | 1199 | 60 | 580 | 34-546 | 97% (g_iter1 217-7) |
| 50 | JeffLegendPower.v11 | 1135 | 263 | 26 | 1-25 |  |
| 51 | MiloAkerman.v1 | 1135 | 263 | 26 | 1-25 |  |
| 52 | PerishoJ.tx | 1135 | 263 | 26 | 1-25 |  |
| 53 | Peter-Fun.dinoboxer | 1135 | 263 | 26 | 1-25 |  |
| 54 | RyanAspen.v22 | 1135 | 263 | 26 | 1-25 |  |
| 55 | TylerJulian.v9 | 1135 | 263 | 26 | 1-25 |  |
| 56 | aj-chau.cowards | 1135 | 263 | 26 | 1-25 |  |
| 57 | cViper971.ourplayer | 1135 | 263 | 26 | 1-25 |  |
| 58 | dylanzemlin.dangerduck2 | 1135 | 263 | 26 | 1-25 |  |
| 59 | lukerhoads.warrior_2nd_comp | 1135 | 263 | 26 | 1-25 |  |
| 60 | neilhuang007.baseline | 1135 | 263 | 26 | 1-25 |  |
| 61 | polyllc.polyv4 | 1135 | 263 | 26 | 1-25 |  |
| 62 | andrearante12.turtle | 1031 | 357 | 25 | 0-25 |  |
| 63 | AlexYu84.smartPlayer | 997 | 357 | 26 | 0-26 |  |
| 64 | H4ffliger.keyboardcrusader_v1 | 997 | 357 | 26 | 0-26 |  |
| 65 | Lithanium.AttackingBot | 997 | 357 | 26 | 0-26 |  |
| 66 | Rubrasum.version_3 | 997 | 357 | 26 | 0-26 |  |
| 67 | SriLakshmiPolavarapu.ducks | 997 | 357 | 26 | 0-26 |  |
| 68 | VarunVejalla.alexander | 997 | 357 | 26 | 0-26 |  |
| 69 | abdullah8a0.crayBasic | 997 | 357 | 26 | 0-26 |  |
| 70 | adamseth2.moveBot1 | 997 | 357 | 26 | 0-26 |  |
| 71 | dylanconklin.Team3 | 997 | 357 | 26 | 0-26 |  |
| 72 | itswin.MPAttack | 997 | 357 | 26 | 0-26 |  |
| 73 | joelcrouch.ducks | 997 | 357 | 26 | 0-26 |  |
| 74 | lcforges.funkyguy3 | 997 | 357 | 26 | 0-26 |  |
| 75 | qpwoeirut.tournament_sprint1 | 997 | 357 | 26 | 0-26 |  |
| 76 | reeceyang.v5 | 997 | 357 | 26 | 0-26 |  |
| 77 | samithShetty.combustiblelemon | 997 | 357 | 26 | 0-26 |  |
| 78 | sayam-goyal.SimpleBot | 997 | 357 | 26 | 0-26 |  |
| 79 | tlevietpdx.Sprint2 | 997 | 357 | 26 | 0-26 |  |
| 80 | justinottesen.sprint1 | 849 | 153 | 580 | 4-576 | 100% (g_iter1 224-0) |
