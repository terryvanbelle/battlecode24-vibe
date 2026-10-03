# Ladder

16829 scrimmages (ours only), 16829 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter3 | 1959 +- 27 | 12 of 76 | 840 | 424-416 | 80.5% | 21.2% (vs 11) |
| g2cr | 1944 +- 107 | 13 of 76 | 40 | 21-19 | 80.0% | 19.9% (vs 11) |
| g_iter2 | 1901 +- 15 | 15 of 76 | 2200 | 1032-1168 | 78.5% | 18.7% (vs 12) |
| g_iter1_c2 | 1816 +- 131 | 17 of 76 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| arch_rush10 | 1816 +- 131 | 18 of 76 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| a3dig5 | 1816 +- 131 | 19 of 76 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| e1aggr | 1791 +- 130 | 21 of 76 | 110 | 83-27 | 74.8% | 16.2% (vs 14) |
| c5bank | 1766 +- 129 | 22 of 76 | 110 | 82-28 | 73.9% | 14.6% (vs 14) |
| c6pair | 1766 +- 129 | 23 of 76 | 110 | 82-28 | 73.9% | 14.6% (vs 14) |
| a3dig10 | 1766 +- 129 | 24 of 76 | 110 | 82-28 | 73.9% | 14.6% (vs 14) |
| a2reloc | 1741 +- 128 | 26 of 76 | 110 | 81-29 | 73.1% | 15.3% (vs 15) |
| a2relay | 1741 +- 128 | 27 of 76 | 110 | 81-29 | 73.1% | 15.3% (vs 15) |
| e2aggr | 1741 +- 128 | 28 of 76 | 110 | 81-29 | 73.1% | 15.3% (vs 15) |
| b1z2b | 1731 +- 24 | 29 of 76 | 1760 | 620-1140 | 72.7% | 14.7% (vs 15) |
| g_iter1 | 1725 +- 10 | 30 of 76 | 6990 | 2485-4505 | 72.5% | 14.4% (vs 15) |
| g1copy | 1718 +- 91 | 31 of 76 | 80 | 28-52 | 72.2% | 13.9% (vs 15) |
| b2fs | 1717 +- 28 | 32 of 76 | 1280 | 439-841 | 72.2% | 13.9% (vs 15) |
| g1sym | 1713 +- 51 | 33 of 76 | 200 | 68-132 | 72.1% | 13.7% (vs 15) |
| b1v2 | 1706 +- 22 | 35 of 76 | 2120 | 711-1409 | 71.8% | 15.5% (vs 16) |
| arch_rush | 1390 +- 94 | 38 of 76 | 110 | 62-48 | 56.6% | 6.5% (vs 18) |
| g_iter0 | 1325 +- 89 | 41 of 76 | 109 | 56-53 | 52.1% | 8.9% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2352 | 84 | 556 | 540-16 | 3% (g_iter1 7-217) |
| 2 | jmerle.camel_case_v21_final | 2352 | 84 | 556 | 540-16 | 4% (g_iter1 10-214) |
| 3 | IvanGeffner.kuma | 2314 | 76 | 556 | 536-20 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2309 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2243 | 63 | 556 | 526-30 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2195 | 56 | 556 | 517-39 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2185 | 55 | 556 | 515-41 | 6% (g_iter1 13-211) |
| 8 | Gymhgy.v10official | 2123 | 48 | 556 | 499-57 | 11% (g_iter1 25-199) |
| 9 | andrewgopher.player22 | 2109 | 46 | 556 | 495-61 | 11% (g_iter1 24-200) |
| 10 | hsmalladi.finalbot | 2055 | 41 | 556 | 476-80 | 12% (g_iter1 27-197) |
| 11 | CyrilSharma.finalBot | 2040 | 34 | 676 | 544-132 | 29% (g_iter3 42-102) |
| 12 | **us:g_iter3** | 1959 | 27 | 840 | 424-416 |  |
| 13 | **us:g2cr** | 1944 | 107 | 40 | 21-19 |  |
| 14 | winkelmantanner.waffle | 1942 | 15 | 2276 | 1362-914 | 62% (g_iter3 163-101) |
| 15 | **us:g_iter2** | 1901 | 15 | 2200 | 1032-1168 |  |
| 16 | ColtG5.Goob_final | 1831 | 12 | 3516 | 2201-1315 | 67% (g_iter2 203-101) |
| 17 | **us:g_iter1_c2** | 1816 | 131 | 110 | 84-26 |  |
| 18 | **us:arch_rush10** | 1816 | 131 | 110 | 84-26 |  |
| 19 | **us:a3dig5** | 1816 | 131 | 110 | 84-26 |  |
| 20 | kyleezz.jeeryfix3 | 1811 | 30 | 556 | 335-221 | 41% (g_iter1 91-133) |
| 21 | **us:e1aggr** | 1791 | 130 | 110 | 83-27 |  |
| 22 | **us:c5bank** | 1766 | 129 | 110 | 82-28 |  |
| 23 | **us:c6pair** | 1766 | 129 | 110 | 82-28 |  |
| 24 | **us:a3dig10** | 1766 | 129 | 110 | 82-28 |  |
| 25 | quesswho.cretplayer2_3 | 1765 | 29 | 556 | 300-256 | 40% (g_iter1 90-134) |
| 26 | **us:a2reloc** | 1741 | 128 | 110 | 81-29 |  |
| 27 | **us:a2relay** | 1741 | 128 | 110 | 81-29 |  |
| 28 | **us:e2aggr** | 1741 | 128 | 110 | 81-29 |  |
| 29 | **us:b1z2b** | 1731 | 24 | 1760 | 620-1140 |  |
| 30 | **us:g_iter1** | 1725 | 10 | 6990 | 2485-4505 |  |
| 31 | **us:g1copy** | 1718 | 91 | 80 | 28-52 |  |
| 32 | **us:b2fs** | 1717 | 28 | 1280 | 439-841 |  |
| 33 | **us:g1sym** | 1713 | 51 | 200 | 68-132 |  |
| 34 | SampleProvider.TSPAARKSPRINT1 | 1708 | 29 | 556 | 256-300 | 50% (g_iter1 112-112) |
| 35 | **us:b1v2** | 1706 | 22 | 2120 | 711-1409 |  |
| 36 | dmtrung14.defaultplayer_intlqualifier | 1544 | 33 | 556 | 141-415 | 69% (g_iter1 155-69) |
| 37 | clbarrell.duck8 | 1522 | 35 | 556 | 128-428 | 78% (g_iter1 175-49) |
| 38 | **us:arch_rush** | 1390 | 94 | 110 | 62-48 |  |
| 39 | jonters.bling3 | 1373 | 46 | 556 | 63-493 | 89% (g_iter1 200-24) |
| 40 | HugoIngelsson.Bot21 | 1329 | 201 | 26 | 3-23 |  |
| 41 | **us:g_iter0** | 1325 | 89 | 109 | 56-53 |  |
| 42 | Metta-AI.bc24scenario | 1262 | 224 | 26 | 2-24 |  |
| 43 | noahzemlin.honeyducklings | 1262 | 224 | 26 | 2-24 |  |
| 44 | sivakovivan.NewHide | 1262 | 224 | 26 | 2-24 |  |
| 45 | awu7.ExplosiveBot | 1256 | 60 | 556 | 34-522 | 97% (g_iter1 217-7) |
| 46 | JeffLegendPower.v11 | 1174 | 264 | 26 | 1-25 |  |
| 47 | MiloAkerman.v1 | 1174 | 264 | 26 | 1-25 |  |
| 48 | PerishoJ.tx | 1174 | 264 | 26 | 1-25 |  |
| 49 | Peter-Fun.dinoboxer | 1174 | 264 | 26 | 1-25 |  |
| 50 | RyanAspen.v22 | 1174 | 264 | 26 | 1-25 |  |
| 51 | TylerJulian.v9 | 1174 | 264 | 26 | 1-25 |  |
| 52 | aj-chau.cowards | 1174 | 264 | 26 | 1-25 |  |
| 53 | cViper971.ourplayer | 1174 | 264 | 26 | 1-25 |  |
| 54 | dylanzemlin.dangerduck2 | 1174 | 264 | 26 | 1-25 |  |
| 55 | lukerhoads.warrior_2nd_comp | 1174 | 264 | 26 | 1-25 |  |
| 56 | neilhuang007.baseline | 1174 | 264 | 26 | 1-25 |  |
| 57 | polyllc.polyv4 | 1174 | 264 | 26 | 1-25 |  |
| 58 | andrearante12.turtle | 1069 | 357 | 25 | 0-25 |  |
| 59 | AlexYu84.smartPlayer | 1036 | 357 | 26 | 0-26 |  |
| 60 | H4ffliger.keyboardcrusader_v1 | 1036 | 357 | 26 | 0-26 |  |
| 61 | Lithanium.AttackingBot | 1036 | 357 | 26 | 0-26 |  |
| 62 | Rubrasum.version_3 | 1036 | 357 | 26 | 0-26 |  |
| 63 | SriLakshmiPolavarapu.ducks | 1036 | 357 | 26 | 0-26 |  |
| 64 | VarunVejalla.alexander | 1036 | 357 | 26 | 0-26 |  |
| 65 | abdullah8a0.crayBasic | 1036 | 357 | 26 | 0-26 |  |
| 66 | adamseth2.moveBot1 | 1036 | 357 | 26 | 0-26 |  |
| 67 | dylanconklin.Team3 | 1036 | 357 | 26 | 0-26 |  |
| 68 | itswin.MPAttack | 1036 | 357 | 26 | 0-26 |  |
| 69 | joelcrouch.ducks | 1036 | 357 | 26 | 0-26 |  |
| 70 | lcforges.funkyguy3 | 1036 | 357 | 26 | 0-26 |  |
| 71 | qpwoeirut.tournament_sprint1 | 1036 | 357 | 26 | 0-26 |  |
| 72 | reeceyang.v5 | 1036 | 357 | 26 | 0-26 |  |
| 73 | samithShetty.combustiblelemon | 1036 | 357 | 26 | 0-26 |  |
| 74 | sayam-goyal.SimpleBot | 1036 | 357 | 26 | 0-26 |  |
| 75 | tlevietpdx.Sprint2 | 1036 | 357 | 26 | 0-26 |  |
| 76 | justinottesen.sprint1 | 905 | 153 | 556 | 4-552 | 100% (g_iter1 224-0) |
