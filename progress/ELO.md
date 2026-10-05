# Ladder

32949 scrimmages (ours only), 32949 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter5 | 1983 +- 19 | 11 of 84 | 1600 | 820-780 | 83.4% | 27.8% (vs 10) |
| g4crumb | 1941 +- 27 | 12 of 84 | 640 | 258-382 | 81.9% | 23.5% (vs 10) |
| g4gym1 | 1941 +- 16 | 13 of 84 | 1880 | 755-1125 | 81.9% | 23.5% (vs 10) |
| g_iter4 | 1939 +- 8 | 14 of 84 | 8720 | 3530-5190 | 81.9% | 23.4% (vs 10) |
| g4econ2 | 1936 +- 56 | 15 of 84 | 160 | 60-100 | 81.8% | 23.0% (vs 10) |
| g3lost | 1924 +- 112 | 16 of 84 | 40 | 15-25 | 81.3% | 21.9% (vs 10) |
| g4pick | 1922 +- 65 | 17 of 84 | 120 | 43-77 | 81.3% | 21.7% (vs 10) |
| g2cr | 1903 +- 107 | 19 of 84 | 40 | 21-19 | 80.6% | 22.6% (vs 11) |
| g_iter3 | 1898 +- 13 | 21 of 84 | 3520 | 1259-2261 | 80.4% | 24.4% (vs 12) |
| g3escrg2 | 1896 +- 44 | 22 of 84 | 280 | 90-190 | 80.4% | 24.3% (vs 12) |
| g_iter2 | 1858 +- 15 | 23 of 84 | 2200 | 1032-1168 | 79.0% | 20.8% (vs 12) |
| arch_rush10 | 1765 +- 128 | 26 of 84 | 110 | 84-26 | 75.8% | 18.8% (vs 14) |
| g_iter1_c2 | 1765 +- 128 | 27 of 84 | 110 | 84-26 | 75.8% | 18.8% (vs 14) |
| a3dig5 | 1765 +- 128 | 28 of 84 | 110 | 84-26 | 75.8% | 18.8% (vs 14) |
| e1aggr | 1741 +- 127 | 29 of 84 | 110 | 83-27 | 74.9% | 17.0% (vs 14) |
| c5bank | 1717 +- 126 | 31 of 84 | 110 | 82-28 | 74.0% | 17.7% (vs 15) |
| c6pair | 1717 +- 126 | 32 of 84 | 110 | 82-28 | 74.0% | 17.7% (vs 15) |
| a3dig10 | 1717 +- 126 | 33 of 84 | 110 | 82-28 | 74.0% | 17.7% (vs 15) |
| a2relay | 1693 +- 125 | 34 of 84 | 110 | 81-29 | 73.2% | 16.1% (vs 15) |
| e2aggr | 1693 +- 125 | 35 of 84 | 110 | 81-29 | 73.2% | 16.1% (vs 15) |
| a2reloc | 1693 +- 125 | 36 of 84 | 110 | 81-29 | 73.2% | 16.1% (vs 15) |
| b1z2b | 1688 +- 24 | 37 of 84 | 1760 | 620-1140 | 73.0% | 15.7% (vs 15) |
| g_iter1 | 1680 +- 10 | 38 of 84 | 6990 | 2485-4505 | 72.7% | 15.2% (vs 15) |
| b2fs | 1674 +- 28 | 39 of 84 | 1280 | 439-841 | 72.5% | 14.8% (vs 15) |
| g1copy | 1673 +- 91 | 40 of 84 | 80 | 28-52 | 72.4% | 14.8% (vs 15) |
| g1sym | 1666 +- 51 | 41 of 84 | 200 | 68-132 | 72.2% | 14.4% (vs 15) |
| b1v2 | 1663 +- 22 | 42 of 84 | 2120 | 711-1409 | 72.0% | 14.1% (vs 15) |
| arch_rush | 1351 +- 94 | 46 of 84 | 110 | 62-48 | 56.7% | 6.8% (vs 18) |
| g_iter0 | 1286 +- 89 | 49 of 84 | 109 | 56-53 | 52.1% | 9.2% (vs 20) |

Our record = OUR win rate (our W-L) against the bot by the incumbent (g_iter5), whatever the count; * marks fewer than 30 games (+- 18 points at 95% for 30 games, +- 20 for 24); a bot the incumbent never met shows the most recent of our builds that did; blank if none has.

| rank | player | rating | +- 95% | games | W-L | our record |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2312 | 70 | 628 | 604-24 | 8% (g_iter5 3-33) |
| 2 | IvanGeffner.kuma | 2298 | 68 | 628 | 602-26 | 11% (g_iter5 4-32) |
| 3 | jmerle.camel_case_v21_final | 2298 | 68 | 628 | 602-26 | 17% (g_iter5 6-30) |
| 4 | chenyx512.flagbot_final | 2249 | 60 | 628 | 594-34 | 8% (g_iter5 3-33) |
| 5 | NotLLeon.v3 | 2150 | 48 | 628 | 571-57 | 31% (g_iter5 11-25) |
| 6 | andli28.v9_USQuals_angle | 2150 | 48 | 628 | 571-57 | 22% (g_iter5 8-28) |
| 7 | andrewgopher.player22 | 2097 | 43 | 628 | 554-74 | 22% (g_iter5 8-28) |
| 8 | hsmalladi.finalbot | 2051 | 40 | 628 | 536-92 | 19% (g_iter5 7-29) |
| 9 | CyrilSharma.finalBot | 2028 | 9 | 6428 | 4326-2102 | 44% (g_iter5 16-20) |
| 10 | Gymhgy.v10official | 2010 | 7 | 9588 | 5839-3749 | 47% (g_iter5 434-482) |
| 11 | **us:g_iter5** | 1983 | 19 | 1600 | 820-780 |  |
| 12 | **us:g4crumb** | 1941 | 27 | 640 | 258-382 |  |
| 13 | **us:g4gym1** | 1941 | 16 | 1880 | 755-1125 |  |
| 14 | **us:g_iter4** | 1939 | 8 | 8720 | 3530-5190 |  |
| 15 | **us:g4econ2** | 1936 | 56 | 160 | 60-100 |  |
| 16 | **us:g3lost** | 1924 | 112 | 40 | 15-25 |  |
| 17 | **us:g4pick** | 1922 | 65 | 120 | 43-77 |  |
| 18 | uravt.Version18Final | 1917 | 90 | 66 | 40-26 | 65% (g_iter4 26-14) |
| 19 | **us:g2cr** | 1903 | 107 | 40 | 21-19 |  |
| 20 | winkelmantanner.waffle | 1900 | 15 | 2348 | 1400-948 | 53% (g_iter5 19-17) |
| 21 | **us:g_iter3** | 1898 | 13 | 3520 | 1259-2261 |  |
| 22 | **us:g3escrg2** | 1896 | 44 | 280 | 90-190 |  |
| 23 | **us:g_iter2** | 1858 | 15 | 2200 | 1032-1168 |  |
| 24 | ColtG5.Goob_final | 1783 | 12 | 3588 | 2209-1379 | 92% (g_iter5 33-3) |
| 25 | kyleezz.jeeryfix3 | 1766 | 28 | 628 | 353-275 | 72% (g_iter5 26-10) |
| 26 | **us:arch_rush10** | 1765 | 128 | 110 | 84-26 |  |
| 27 | **us:g_iter1_c2** | 1765 | 128 | 110 | 84-26 |  |
| 28 | **us:a3dig5** | 1765 | 128 | 110 | 84-26 |  |
| 29 | **us:e1aggr** | 1741 | 127 | 110 | 83-27 |  |
| 30 | quesswho.cretplayer2_3 | 1718 | 28 | 628 | 313-315 | 86% (g_iter5 31-5) |
| 31 | **us:c5bank** | 1717 | 126 | 110 | 82-28 |  |
| 32 | **us:c6pair** | 1717 | 126 | 110 | 82-28 |  |
| 33 | **us:a3dig10** | 1717 | 126 | 110 | 82-28 |  |
| 34 | **us:a2relay** | 1693 | 125 | 110 | 81-29 |  |
| 35 | **us:e2aggr** | 1693 | 125 | 110 | 81-29 |  |
| 36 | **us:a2reloc** | 1693 | 125 | 110 | 81-29 |  |
| 37 | **us:b1z2b** | 1688 | 24 | 1760 | 620-1140 |  |
| 38 | **us:g_iter1** | 1680 | 10 | 6990 | 2485-4505 |  |
| 39 | **us:b2fs** | 1674 | 28 | 1280 | 439-841 |  |
| 40 | **us:g1copy** | 1673 | 91 | 80 | 28-52 |  |
| 41 | **us:g1sym** | 1666 | 51 | 200 | 68-132 |  |
| 42 | **us:b1v2** | 1663 | 22 | 2120 | 711-1409 |  |
| 43 | SampleProvider.TSPAARKSPRINT1 | 1656 | 28 | 628 | 261-367 | 94% (g_iter5 34-2) |
| 44 | dmtrung14.defaultplayer_intlqualifier | 1499 | 33 | 628 | 145-483 | 94% (g_iter5 34-2) |
| 45 | clbarrell.duck8 | 1474 | 34 | 628 | 130-498 | 97% (g_iter5 35-1) |
| 46 | **us:arch_rush** | 1351 | 94 | 110 | 62-48 |  |
| 47 | jonters.bling3 | 1326 | 45 | 628 | 64-564 | 100% (g_iter5 36-0) |
| 48 | HugoIngelsson.Bot21 | 1290 | 200 | 26 | 3-23 | 100% (g_iter1 2-0*) |
| 49 | **us:g_iter0** | 1286 | 89 | 109 | 56-53 |  |
| 50 | Metta-AI.bc24scenario | 1223 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 51 | noahzemlin.honeyducklings | 1223 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 52 | sivakovivan.NewHide | 1223 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 53 | awu7.ExplosiveBot | 1207 | 60 | 628 | 34-594 | 100% (g_iter5 36-0) |
| 54 | JeffLegendPower.v11 | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 55 | MiloAkerman.v1 | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 56 | PerishoJ.tx | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 57 | Peter-Fun.dinoboxer | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 58 | RyanAspen.v22 | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 59 | TylerJulian.v9 | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 60 | aj-chau.cowards | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 61 | cViper971.ourplayer | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 62 | dylanzemlin.dangerduck2 | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 63 | lukerhoads.warrior_2nd_comp | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 64 | neilhuang007.baseline | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 65 | polyllc.polyv4 | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 66 | andrearante12.turtle | 1032 | 357 | 25 | 0-25 | 100% (g_iter1 2-0*) |
| 67 | AlexYu84.smartPlayer | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 68 | H4ffliger.keyboardcrusader_v1 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 69 | Lithanium.AttackingBot | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 70 | Rubrasum.version_3 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 71 | SriLakshmiPolavarapu.ducks | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 72 | VarunVejalla.alexander | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 73 | abdullah8a0.crayBasic | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 74 | adamseth2.moveBot1 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 75 | dylanconklin.Team3 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 76 | itswin.MPAttack | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 77 | joelcrouch.ducks | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 78 | lcforges.funkyguy3 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 79 | qpwoeirut.tournament_sprint1 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 80 | reeceyang.v5 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 81 | samithShetty.combustiblelemon | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 82 | sayam-goyal.SimpleBot | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 83 | tlevietpdx.Sprint2 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 84 | justinottesen.sprint1 | 857 | 153 | 628 | 4-624 | 100% (g_iter5 36-0) |
