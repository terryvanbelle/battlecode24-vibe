# Ladder

14189 scrimmages (ours only), 14189 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter2 | 1918 +- 35 | 13 of 74 | 600 | 292-308 | 78.2% | 18.1% (vs 12) |
| g_iter1_c2 | 1845 +- 131 | 15 of 74 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| arch_rush10 | 1845 +- 131 | 16 of 74 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| a3dig5 | 1845 +- 131 | 17 of 74 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| e1aggr | 1819 +- 130 | 19 of 74 | 110 | 83-27 | 74.8% | 16.2% (vs 14) |
| a3dig10 | 1794 +- 129 | 21 of 74 | 110 | 82-28 | 73.9% | 16.8% (vs 15) |
| c6pair | 1794 +- 129 | 22 of 74 | 110 | 82-28 | 73.9% | 16.8% (vs 15) |
| c5bank | 1794 +- 129 | 23 of 74 | 110 | 82-28 | 73.9% | 16.8% (vs 15) |
| a2relay | 1769 +- 128 | 24 of 74 | 110 | 81-29 | 73.0% | 15.2% (vs 15) |
| e2aggr | 1769 +- 128 | 25 of 74 | 110 | 81-29 | 73.0% | 15.2% (vs 15) |
| a2reloc | 1769 +- 128 | 26 of 74 | 110 | 81-29 | 73.0% | 15.2% (vs 15) |
| b1z2b | 1763 +- 24 | 27 of 74 | 1760 | 620-1140 | 72.8% | 14.8% (vs 15) |
| g_iter1 | 1762 +- 11 | 28 of 74 | 6830 | 2416-4414 | 72.8% | 14.8% (vs 15) |
| g1sym | 1759 +- 51 | 29 of 74 | 200 | 68-132 | 72.7% | 14.6% (vs 15) |
| g1copy | 1758 +- 91 | 30 of 74 | 80 | 28-52 | 72.7% | 14.5% (vs 15) |
| b2fs | 1749 +- 28 | 31 of 74 | 1280 | 439-841 | 72.3% | 14.0% (vs 15) |
| b1v2 | 1738 +- 22 | 33 of 74 | 2120 | 711-1409 | 71.9% | 15.6% (vs 16) |
| arch_rush | 1415 +- 95 | 36 of 74 | 110 | 62-48 | 56.6% | 6.2% (vs 18) |
| g_iter0 | 1349 +- 89 | 39 of 74 | 109 | 56-53 | 52.0% | 8.7% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2370 | 86 | 532 | 517-15 | 3% (g_iter1 7-217) |
| 2 | jmerle.camel_case_v21_final | 2370 | 86 | 532 | 517-15 | 4% (g_iter1 10-214) |
| 3 | IvanGeffner.kuma | 2340 | 80 | 532 | 514-18 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2337 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2250 | 63 | 532 | 502-30 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2222 | 59 | 532 | 497-35 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2207 | 57 | 532 | 494-38 | 6% (g_iter1 13-211) |
| 8 | Gymhgy.v10official | 2160 | 51 | 532 | 483-49 | 11% (g_iter1 25-199) |
| 9 | andrewgopher.player22 | 2128 | 47 | 532 | 474-58 | 11% (g_iter1 24-200) |
| 10 | hsmalladi.finalbot | 2072 | 42 | 532 | 455-77 | 12% (g_iter1 27-197) |
| 11 | CyrilSharma.finalBot | 2040 | 39 | 532 | 442-90 | 20% (g_iter1 44-180) |
| 12 | winkelmantanner.waffle | 1997 | 37 | 532 | 422-110 | 21% (g_iter1 47-177) |
| 13 | **us:g_iter2** | 1918 | 35 | 600 | 292-308 |  |
| 14 | ColtG5.Goob_final | 1877 | 13 | 3172 | 2068-1104 | 58% (g_iter2 83-61) |
| 15 | **us:g_iter1_c2** | 1845 | 131 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1845 | 131 | 110 | 84-26 |  |
| 17 | **us:a3dig5** | 1845 | 131 | 110 | 84-26 |  |
| 18 | kyleezz.jeeryfix3 | 1843 | 31 | 532 | 327-205 | 41% (g_iter1 91-133) |
| 19 | **us:e1aggr** | 1819 | 130 | 110 | 83-27 |  |
| 20 | quesswho.cretplayer2_3 | 1797 | 30 | 532 | 293-239 | 40% (g_iter1 90-134) |
| 21 | **us:a3dig10** | 1794 | 129 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1794 | 129 | 110 | 82-28 |  |
| 23 | **us:c5bank** | 1794 | 129 | 110 | 82-28 |  |
| 24 | **us:a2relay** | 1769 | 128 | 110 | 81-29 |  |
| 25 | **us:e2aggr** | 1769 | 128 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1769 | 128 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1763 | 24 | 1760 | 620-1140 |  |
| 28 | **us:g_iter1** | 1762 | 11 | 6830 | 2416-4414 |  |
| 29 | **us:g1sym** | 1759 | 51 | 200 | 68-132 |  |
| 30 | **us:g1copy** | 1758 | 91 | 80 | 28-52 |  |
| 31 | **us:b2fs** | 1749 | 28 | 1280 | 439-841 |  |
| 32 | SampleProvider.TSPAARKSPRINT1 | 1744 | 30 | 532 | 253-279 | 50% (g_iter1 112-112) |
| 33 | **us:b1v2** | 1738 | 22 | 2120 | 711-1409 |  |
| 34 | dmtrung14.defaultplayer_intlqualifier | 1580 | 34 | 532 | 140-392 | 69% (g_iter1 155-69) |
| 35 | clbarrell.duck8 | 1559 | 35 | 532 | 128-404 | 78% (g_iter1 175-49) |
| 36 | **us:arch_rush** | 1415 | 95 | 110 | 62-48 |  |
| 37 | jonters.bling3 | 1405 | 46 | 532 | 62-470 | 89% (g_iter1 200-24) |
| 38 | HugoIngelsson.Bot21 | 1352 | 201 | 26 | 3-23 |  |
| 39 | **us:g_iter0** | 1349 | 89 | 109 | 56-53 |  |
| 40 | awu7.ExplosiveBot | 1291 | 60 | 532 | 34-498 | 97% (g_iter1 217-7) |
| 41 | Metta-AI.bc24scenario | 1285 | 224 | 26 | 2-24 |  |
| 42 | noahzemlin.honeyducklings | 1285 | 224 | 26 | 2-24 |  |
| 43 | sivakovivan.NewHide | 1285 | 224 | 26 | 2-24 |  |
| 44 | JeffLegendPower.v11 | 1197 | 264 | 26 | 1-25 |  |
| 45 | MiloAkerman.v1 | 1197 | 264 | 26 | 1-25 |  |
| 46 | PerishoJ.tx | 1197 | 264 | 26 | 1-25 |  |
| 47 | Peter-Fun.dinoboxer | 1197 | 264 | 26 | 1-25 |  |
| 48 | RyanAspen.v22 | 1197 | 264 | 26 | 1-25 |  |
| 49 | TylerJulian.v9 | 1197 | 264 | 26 | 1-25 |  |
| 50 | aj-chau.cowards | 1197 | 264 | 26 | 1-25 |  |
| 51 | cViper971.ourplayer | 1197 | 264 | 26 | 1-25 |  |
| 52 | dylanzemlin.dangerduck2 | 1197 | 264 | 26 | 1-25 |  |
| 53 | lukerhoads.warrior_2nd_comp | 1197 | 264 | 26 | 1-25 |  |
| 54 | neilhuang007.baseline | 1197 | 264 | 26 | 1-25 |  |
| 55 | polyllc.polyv4 | 1197 | 264 | 26 | 1-25 |  |
| 56 | andrearante12.turtle | 1092 | 357 | 25 | 0-25 |  |
| 57 | AlexYu84.smartPlayer | 1059 | 357 | 26 | 0-26 |  |
| 58 | H4ffliger.keyboardcrusader_v1 | 1059 | 357 | 26 | 0-26 |  |
| 59 | Lithanium.AttackingBot | 1059 | 357 | 26 | 0-26 |  |
| 60 | Rubrasum.version_3 | 1059 | 357 | 26 | 0-26 |  |
| 61 | SriLakshmiPolavarapu.ducks | 1059 | 357 | 26 | 0-26 |  |
| 62 | VarunVejalla.alexander | 1059 | 357 | 26 | 0-26 |  |
| 63 | abdullah8a0.crayBasic | 1059 | 357 | 26 | 0-26 |  |
| 64 | adamseth2.moveBot1 | 1059 | 357 | 26 | 0-26 |  |
| 65 | dylanconklin.Team3 | 1059 | 357 | 26 | 0-26 |  |
| 66 | itswin.MPAttack | 1059 | 357 | 26 | 0-26 |  |
| 67 | joelcrouch.ducks | 1059 | 357 | 26 | 0-26 |  |
| 68 | lcforges.funkyguy3 | 1059 | 357 | 26 | 0-26 |  |
| 69 | qpwoeirut.tournament_sprint1 | 1059 | 357 | 26 | 0-26 |  |
| 70 | reeceyang.v5 | 1059 | 357 | 26 | 0-26 |  |
| 71 | samithShetty.combustiblelemon | 1059 | 357 | 26 | 0-26 |  |
| 72 | sayam-goyal.SimpleBot | 1059 | 357 | 26 | 0-26 |  |
| 73 | tlevietpdx.Sprint2 | 1059 | 357 | 26 | 0-26 |  |
| 74 | justinottesen.sprint1 | 940 | 153 | 532 | 4-528 | 100% (g_iter1 224-0) |
