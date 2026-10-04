# Ladder

20109 scrimmages (ours only), 20109 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter4 | 1932 +- 39 | 12 of 78 | 520 | 269-251 | 80.7% | 21.5% (vs 11) |
| g2cr | 1906 +- 107 | 13 of 78 | 40 | 21-19 | 79.8% | 19.2% (vs 11) |
| g_iter3 | 1901 +- 13 | 15 of 78 | 3320 | 1194-2126 | 79.6% | 21.3% (vs 12) |
| g3escrg2 | 1899 +- 44 | 16 of 78 | 280 | 90-190 | 79.6% | 21.2% (vs 12) |
| g_iter2 | 1862 +- 15 | 17 of 78 | 2200 | 1032-1168 | 78.3% | 18.1% (vs 12) |
| a3dig5 | 1786 +- 130 | 19 of 78 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| arch_rush10 | 1786 +- 130 | 20 of 78 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| g_iter1_c2 | 1786 +- 130 | 21 of 78 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| e1aggr | 1760 +- 129 | 23 of 78 | 110 | 83-27 | 74.9% | 16.4% (vs 14) |
| c6pair | 1736 +- 128 | 24 of 78 | 110 | 82-28 | 74.0% | 14.8% (vs 14) |
| c5bank | 1736 +- 128 | 25 of 78 | 110 | 82-28 | 74.0% | 14.8% (vs 14) |
| a3dig10 | 1736 +- 128 | 26 of 78 | 110 | 82-28 | 74.0% | 14.8% (vs 14) |
| e2aggr | 1711 +- 127 | 28 of 78 | 110 | 81-29 | 73.1% | 15.5% (vs 15) |
| a2reloc | 1711 +- 127 | 29 of 78 | 110 | 81-29 | 73.1% | 15.5% (vs 15) |
| a2relay | 1711 +- 127 | 30 of 78 | 110 | 81-29 | 73.1% | 15.5% (vs 15) |
| b1z2b | 1697 +- 24 | 31 of 78 | 1760 | 620-1140 | 72.6% | 14.7% (vs 15) |
| g_iter1 | 1690 +- 10 | 32 of 78 | 6990 | 2485-4505 | 72.4% | 14.2% (vs 15) |
| g1copy | 1683 +- 91 | 33 of 78 | 80 | 28-52 | 72.1% | 13.8% (vs 15) |
| b2fs | 1683 +- 28 | 34 of 78 | 1280 | 439-841 | 72.1% | 13.8% (vs 15) |
| g1sym | 1677 +- 51 | 35 of 78 | 200 | 68-132 | 71.9% | 13.5% (vs 15) |
| b1v2 | 1671 +- 22 | 36 of 78 | 2120 | 711-1409 | 71.7% | 13.2% (vs 15) |
| arch_rush | 1364 +- 94 | 40 of 78 | 110 | 62-48 | 56.7% | 6.8% (vs 18) |
| g_iter0 | 1300 +- 89 | 43 of 78 | 109 | 56-53 | 52.1% | 9.2% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2309 | 78 | 580 | 561-19 | 4% (g_iter1 10-214) |
| 2 | Strequals.duck0127v5 | 2300 | 76 | 580 | 560-20 | 3% (g_iter1 7-217) |
| 3 | IvanGeffner.kuma | 2284 | 73 | 580 | 558-22 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2279 | 347 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2223 | 62 | 580 | 549-31 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2158 | 53 | 580 | 536-44 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2150 | 52 | 580 | 534-46 | 6% (g_iter1 13-211) |
| 8 | andrewgopher.player22 | 2083 | 45 | 580 | 515-65 | 11% (g_iter1 24-200) |
| 9 | Gymhgy.v10official | 2074 | 44 | 580 | 512-68 | 11% (g_iter1 25-199) |
| 10 | CyrilSharma.finalBot | 2032 | 13 | 3500 | 2481-1019 | 42% (g_iter4 27-37) |
| 11 | hsmalladi.finalbot | 2030 | 41 | 580 | 496-84 | 12% (g_iter1 27-197) |
| 12 | **us:g_iter4** | 1932 | 39 | 520 | 269-251 |  |
| 13 | **us:g2cr** | 1906 | 107 | 40 | 21-19 |  |
| 14 | winkelmantanner.waffle | 1903 | 15 | 2300 | 1375-925 | 62% (g_iter3 163-101) |
| 15 | **us:g_iter3** | 1901 | 13 | 3320 | 1194-2126 |  |
| 16 | **us:g3escrg2** | 1899 | 44 | 280 | 90-190 |  |
| 17 | **us:g_iter2** | 1862 | 15 | 2200 | 1032-1168 |  |
| 18 | ColtG5.Goob_final | 1794 | 12 | 3540 | 2205-1335 | 67% (g_iter2 203-101) |
| 19 | **us:a3dig5** | 1786 | 130 | 110 | 84-26 |  |
| 20 | **us:arch_rush10** | 1786 | 130 | 110 | 84-26 |  |
| 21 | **us:g_iter1_c2** | 1786 | 130 | 110 | 84-26 |  |
| 22 | kyleezz.jeeryfix3 | 1775 | 29 | 580 | 342-238 | 41% (g_iter1 91-133) |
| 23 | **us:e1aggr** | 1760 | 129 | 110 | 83-27 |  |
| 24 | **us:c6pair** | 1736 | 128 | 110 | 82-28 |  |
| 25 | **us:c5bank** | 1736 | 128 | 110 | 82-28 |  |
| 26 | **us:a3dig10** | 1736 | 128 | 110 | 82-28 |  |
| 27 | quesswho.cretplayer2_3 | 1731 | 29 | 580 | 307-273 | 40% (g_iter1 90-134) |
| 28 | **us:e2aggr** | 1711 | 127 | 110 | 81-29 |  |
| 29 | **us:a2reloc** | 1711 | 127 | 110 | 81-29 |  |
| 30 | **us:a2relay** | 1711 | 127 | 110 | 81-29 |  |
| 31 | **us:b1z2b** | 1697 | 24 | 1760 | 620-1140 |  |
| 32 | **us:g_iter1** | 1690 | 10 | 6990 | 2485-4505 |  |
| 33 | **us:g1copy** | 1683 | 91 | 80 | 28-52 |  |
| 34 | **us:b2fs** | 1683 | 28 | 1280 | 439-841 |  |
| 35 | **us:g1sym** | 1677 | 51 | 200 | 68-132 |  |
| 36 | **us:b1v2** | 1671 | 22 | 2120 | 711-1409 |  |
| 37 | SampleProvider.TSPAARKSPRINT1 | 1671 | 29 | 580 | 259-321 | 50% (g_iter1 112-112) |
| 38 | dmtrung14.defaultplayer_intlqualifier | 1508 | 33 | 580 | 142-438 | 69% (g_iter1 155-69) |
| 39 | clbarrell.duck8 | 1486 | 34 | 580 | 129-451 | 78% (g_iter1 175-49) |
| 40 | **us:arch_rush** | 1364 | 94 | 110 | 62-48 |  |
| 41 | jonters.bling3 | 1339 | 45 | 580 | 64-516 | 89% (g_iter1 200-24) |
| 42 | HugoIngelsson.Bot21 | 1304 | 200 | 26 | 3-23 |  |
| 43 | **us:g_iter0** | 1300 | 89 | 109 | 56-53 |  |
| 44 | Metta-AI.bc24scenario | 1237 | 223 | 26 | 2-24 |  |
| 45 | noahzemlin.honeyducklings | 1237 | 223 | 26 | 2-24 |  |
| 46 | sivakovivan.NewHide | 1237 | 223 | 26 | 2-24 |  |
| 47 | awu7.ExplosiveBot | 1219 | 60 | 580 | 34-546 | 97% (g_iter1 217-7) |
| 48 | JeffLegendPower.v11 | 1150 | 263 | 26 | 1-25 |  |
| 49 | MiloAkerman.v1 | 1150 | 263 | 26 | 1-25 |  |
| 50 | PerishoJ.tx | 1150 | 263 | 26 | 1-25 |  |
| 51 | Peter-Fun.dinoboxer | 1150 | 263 | 26 | 1-25 |  |
| 52 | RyanAspen.v22 | 1150 | 263 | 26 | 1-25 |  |
| 53 | TylerJulian.v9 | 1150 | 263 | 26 | 1-25 |  |
| 54 | aj-chau.cowards | 1150 | 263 | 26 | 1-25 |  |
| 55 | cViper971.ourplayer | 1150 | 263 | 26 | 1-25 |  |
| 56 | dylanzemlin.dangerduck2 | 1150 | 263 | 26 | 1-25 |  |
| 57 | lukerhoads.warrior_2nd_comp | 1150 | 263 | 26 | 1-25 |  |
| 58 | neilhuang007.baseline | 1150 | 263 | 26 | 1-25 |  |
| 59 | polyllc.polyv4 | 1150 | 263 | 26 | 1-25 |  |
| 60 | andrearante12.turtle | 1045 | 357 | 25 | 0-25 |  |
| 61 | AlexYu84.smartPlayer | 1012 | 357 | 26 | 0-26 |  |
| 62 | H4ffliger.keyboardcrusader_v1 | 1012 | 357 | 26 | 0-26 |  |
| 63 | Lithanium.AttackingBot | 1012 | 357 | 26 | 0-26 |  |
| 64 | Rubrasum.version_3 | 1012 | 357 | 26 | 0-26 |  |
| 65 | SriLakshmiPolavarapu.ducks | 1012 | 357 | 26 | 0-26 |  |
| 66 | VarunVejalla.alexander | 1012 | 357 | 26 | 0-26 |  |
| 67 | abdullah8a0.crayBasic | 1012 | 357 | 26 | 0-26 |  |
| 68 | adamseth2.moveBot1 | 1012 | 357 | 26 | 0-26 |  |
| 69 | dylanconklin.Team3 | 1012 | 357 | 26 | 0-26 |  |
| 70 | itswin.MPAttack | 1012 | 357 | 26 | 0-26 |  |
| 71 | joelcrouch.ducks | 1012 | 357 | 26 | 0-26 |  |
| 72 | lcforges.funkyguy3 | 1012 | 357 | 26 | 0-26 |  |
| 73 | qpwoeirut.tournament_sprint1 | 1012 | 357 | 26 | 0-26 |  |
| 74 | reeceyang.v5 | 1012 | 357 | 26 | 0-26 |  |
| 75 | samithShetty.combustiblelemon | 1012 | 357 | 26 | 0-26 |  |
| 76 | sayam-goyal.SimpleBot | 1012 | 357 | 26 | 0-26 |  |
| 77 | tlevietpdx.Sprint2 | 1012 | 357 | 26 | 0-26 |  |
| 78 | justinottesen.sprint1 | 869 | 153 | 580 | 4-576 | 100% (g_iter1 224-0) |
