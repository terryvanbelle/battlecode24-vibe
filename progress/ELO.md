# Ladder

33549 scrimmages (ours only), 33549 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g4ship1 | 2003 +- 41 | 10 of 85 | 280 | 142-138 | 84.4% | 29.1% (vs 9) |
| g_iter5 | 1973 +- 19 | 12 of 85 | 1600 | 820-780 | 83.4% | 27.9% (vs 10) |
| g4crumb | 1931 +- 27 | 13 of 85 | 640 | 258-382 | 81.9% | 23.6% (vs 10) |
| g4gym1 | 1931 +- 16 | 14 of 85 | 1880 | 755-1125 | 81.9% | 23.5% (vs 10) |
| g_iter4 | 1928 +- 7 | 15 of 85 | 9040 | 3645-5395 | 81.8% | 23.3% (vs 10) |
| g4econ2 | 1925 +- 56 | 16 of 85 | 160 | 60-100 | 81.7% | 23.0% (vs 10) |
| g3lost | 1913 +- 112 | 17 of 85 | 40 | 15-25 | 81.3% | 21.9% (vs 10) |
| g4pick | 1911 +- 65 | 18 of 85 | 120 | 43-77 | 81.2% | 21.7% (vs 10) |
| g2cr | 1893 +- 107 | 20 of 85 | 40 | 21-19 | 80.6% | 22.6% (vs 11) |
| g_iter3 | 1887 +- 13 | 22 of 85 | 3520 | 1259-2261 | 80.4% | 24.3% (vs 12) |
| g3escrg2 | 1886 +- 44 | 23 of 85 | 280 | 90-190 | 80.3% | 24.2% (vs 12) |
| g_iter2 | 1848 +- 15 | 24 of 85 | 2200 | 1032-1168 | 79.0% | 20.8% (vs 12) |
| g_iter1_c2 | 1756 +- 128 | 27 of 85 | 110 | 84-26 | 75.8% | 18.9% (vs 14) |
| a3dig5 | 1756 +- 128 | 28 of 85 | 110 | 84-26 | 75.8% | 18.9% (vs 14) |
| arch_rush10 | 1756 +- 128 | 29 of 85 | 110 | 84-26 | 75.8% | 18.9% (vs 14) |
| e1aggr | 1732 +- 127 | 30 of 85 | 110 | 83-27 | 74.9% | 17.1% (vs 14) |
| c6pair | 1708 +- 126 | 32 of 85 | 110 | 82-28 | 74.1% | 17.8% (vs 15) |
| c5bank | 1708 +- 126 | 33 of 85 | 110 | 82-28 | 74.1% | 17.8% (vs 15) |
| a3dig10 | 1708 +- 126 | 34 of 85 | 110 | 82-28 | 74.1% | 17.8% (vs 15) |
| a2reloc | 1684 +- 125 | 35 of 85 | 110 | 81-29 | 73.2% | 16.1% (vs 15) |
| a2relay | 1684 +- 125 | 36 of 85 | 110 | 81-29 | 73.2% | 16.1% (vs 15) |
| e2aggr | 1684 +- 125 | 37 of 85 | 110 | 81-29 | 73.2% | 16.1% (vs 15) |
| b1z2b | 1678 +- 24 | 38 of 85 | 1760 | 620-1140 | 73.0% | 15.7% (vs 15) |
| g_iter1 | 1670 +- 10 | 39 of 85 | 6990 | 2485-4505 | 72.7% | 15.2% (vs 15) |
| b2fs | 1664 +- 28 | 40 of 85 | 1280 | 439-841 | 72.4% | 14.8% (vs 15) |
| g1copy | 1663 +- 91 | 41 of 85 | 80 | 28-52 | 72.4% | 14.8% (vs 15) |
| g1sym | 1656 +- 51 | 42 of 85 | 200 | 68-132 | 72.2% | 14.4% (vs 15) |
| b1v2 | 1652 +- 22 | 43 of 85 | 2120 | 711-1409 | 72.0% | 14.1% (vs 15) |
| arch_rush | 1343 +- 94 | 47 of 85 | 110 | 62-48 | 56.7% | 6.9% (vs 18) |
| g_iter0 | 1279 +- 89 | 50 of 85 | 109 | 56-53 | 52.2% | 9.3% (vs 20) |

Our record = OUR win rate (our W-L) against the bot by the incumbent (g_iter5), whatever the count; * marks fewer than 30 games (+- 18 points at 95% for 30 games, +- 20 for 24); a bot the incumbent never met shows the most recent of our builds that did; blank if none has.

| rank | player | rating | +- 95% | games | W-L | our record |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2302 | 70 | 628 | 604-24 | 8% (g_iter5 3-33) |
| 2 | IvanGeffner.kuma | 2288 | 68 | 628 | 602-26 | 11% (g_iter5 4-32) |
| 3 | jmerle.camel_case_v21_final | 2288 | 68 | 628 | 602-26 | 17% (g_iter5 6-30) |
| 4 | chenyx512.flagbot_final | 2239 | 60 | 628 | 594-34 | 8% (g_iter5 3-33) |
| 5 | NotLLeon.v3 | 2140 | 48 | 628 | 571-57 | 31% (g_iter5 11-25) |
| 6 | andli28.v9_USQuals_angle | 2140 | 48 | 628 | 571-57 | 22% (g_iter5 8-28) |
| 7 | andrewgopher.player22 | 2087 | 43 | 628 | 554-74 | 22% (g_iter5 8-28) |
| 8 | hsmalladi.finalbot | 2041 | 40 | 628 | 536-92 | 19% (g_iter5 7-29) |
| 9 | CyrilSharma.finalBot | 2018 | 9 | 6428 | 4326-2102 | 44% (g_iter5 16-20) |
| 10 | **us:g4ship1** | 2003 | 41 | 280 | 142-138 |  |
| 11 | Gymhgy.v10official | 2001 | 7 | 10188 | 6182-4006 | 47% (g_iter5 434-482) |
| 12 | **us:g_iter5** | 1973 | 19 | 1600 | 820-780 |  |
| 13 | **us:g4crumb** | 1931 | 27 | 640 | 258-382 |  |
| 14 | **us:g4gym1** | 1931 | 16 | 1880 | 755-1125 |  |
| 15 | **us:g_iter4** | 1928 | 7 | 9040 | 3645-5395 |  |
| 16 | **us:g4econ2** | 1925 | 56 | 160 | 60-100 |  |
| 17 | **us:g3lost** | 1913 | 112 | 40 | 15-25 |  |
| 18 | **us:g4pick** | 1911 | 65 | 120 | 43-77 |  |
| 19 | uravt.Version18Final | 1907 | 90 | 66 | 40-26 | 65% (g_iter4 26-14) |
| 20 | **us:g2cr** | 1893 | 107 | 40 | 21-19 |  |
| 21 | winkelmantanner.waffle | 1889 | 15 | 2348 | 1400-948 | 53% (g_iter5 19-17) |
| 22 | **us:g_iter3** | 1887 | 13 | 3520 | 1259-2261 |  |
| 23 | **us:g3escrg2** | 1886 | 44 | 280 | 90-190 |  |
| 24 | **us:g_iter2** | 1848 | 15 | 2200 | 1032-1168 |  |
| 25 | ColtG5.Goob_final | 1773 | 12 | 3588 | 2209-1379 | 92% (g_iter5 33-3) |
| 26 | kyleezz.jeeryfix3 | 1756 | 28 | 628 | 353-275 | 72% (g_iter5 26-10) |
| 27 | **us:g_iter1_c2** | 1756 | 128 | 110 | 84-26 |  |
| 28 | **us:a3dig5** | 1756 | 128 | 110 | 84-26 |  |
| 29 | **us:arch_rush10** | 1756 | 128 | 110 | 84-26 |  |
| 30 | **us:e1aggr** | 1732 | 127 | 110 | 83-27 |  |
| 31 | quesswho.cretplayer2_3 | 1708 | 28 | 628 | 313-315 | 86% (g_iter5 31-5) |
| 32 | **us:c6pair** | 1708 | 126 | 110 | 82-28 |  |
| 33 | **us:c5bank** | 1708 | 126 | 110 | 82-28 |  |
| 34 | **us:a3dig10** | 1708 | 126 | 110 | 82-28 |  |
| 35 | **us:a2reloc** | 1684 | 125 | 110 | 81-29 |  |
| 36 | **us:a2relay** | 1684 | 125 | 110 | 81-29 |  |
| 37 | **us:e2aggr** | 1684 | 125 | 110 | 81-29 |  |
| 38 | **us:b1z2b** | 1678 | 24 | 1760 | 620-1140 |  |
| 39 | **us:g_iter1** | 1670 | 10 | 6990 | 2485-4505 |  |
| 40 | **us:b2fs** | 1664 | 28 | 1280 | 439-841 |  |
| 41 | **us:g1copy** | 1663 | 91 | 80 | 28-52 |  |
| 42 | **us:g1sym** | 1656 | 51 | 200 | 68-132 |  |
| 43 | **us:b1v2** | 1652 | 22 | 2120 | 711-1409 |  |
| 44 | SampleProvider.TSPAARKSPRINT1 | 1646 | 28 | 628 | 261-367 | 94% (g_iter5 34-2) |
| 45 | dmtrung14.defaultplayer_intlqualifier | 1489 | 33 | 628 | 145-483 | 94% (g_iter5 34-2) |
| 46 | clbarrell.duck8 | 1464 | 34 | 628 | 130-498 | 97% (g_iter5 35-1) |
| 47 | **us:arch_rush** | 1343 | 94 | 110 | 62-48 |  |
| 48 | jonters.bling3 | 1316 | 45 | 628 | 64-564 | 100% (g_iter5 36-0) |
| 49 | HugoIngelsson.Bot21 | 1282 | 200 | 26 | 3-23 | 100% (g_iter1 2-0*) |
| 50 | **us:g_iter0** | 1279 | 89 | 109 | 56-53 |  |
| 51 | Metta-AI.bc24scenario | 1216 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 52 | noahzemlin.honeyducklings | 1216 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 53 | sivakovivan.NewHide | 1216 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 54 | awu7.ExplosiveBot | 1197 | 60 | 628 | 34-594 | 100% (g_iter5 36-0) |
| 55 | JeffLegendPower.v11 | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 56 | MiloAkerman.v1 | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 57 | PerishoJ.tx | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 58 | Peter-Fun.dinoboxer | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 59 | RyanAspen.v22 | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 60 | TylerJulian.v9 | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 61 | aj-chau.cowards | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 62 | cViper971.ourplayer | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 63 | dylanzemlin.dangerduck2 | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 64 | lukerhoads.warrior_2nd_comp | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 65 | neilhuang007.baseline | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 66 | polyllc.polyv4 | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 67 | andrearante12.turtle | 1024 | 357 | 25 | 0-25 | 100% (g_iter1 2-0*) |
| 68 | AlexYu84.smartPlayer | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 69 | H4ffliger.keyboardcrusader_v1 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 70 | Lithanium.AttackingBot | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 71 | Rubrasum.version_3 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 72 | SriLakshmiPolavarapu.ducks | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 73 | VarunVejalla.alexander | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 74 | abdullah8a0.crayBasic | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 75 | adamseth2.moveBot1 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 76 | dylanconklin.Team3 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 77 | itswin.MPAttack | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 78 | joelcrouch.ducks | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 79 | lcforges.funkyguy3 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 80 | qpwoeirut.tournament_sprint1 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 81 | reeceyang.v5 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 82 | samithShetty.combustiblelemon | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 83 | sayam-goyal.SimpleBot | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 84 | tlevietpdx.Sprint2 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 85 | justinottesen.sprint1 | 847 | 153 | 628 | 4-624 | 100% (g_iter5 36-0) |
