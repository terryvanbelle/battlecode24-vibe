# Ladder

14709 scrimmages (ours only), 14709 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g1basics | 2075 +- 74 | 10 of 75 | 120 | 94-26 | 84.0% | 27.6% (vs 9) |
| g_iter2 | 1915 +- 27 | 14 of 75 | 840 | 404-436 | 78.5% | 19.0% (vs 12) |
| arch_rush10 | 1832 +- 130 | 16 of 75 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| a3dig5 | 1832 +- 130 | 17 of 75 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| g_iter1_c2 | 1832 +- 130 | 18 of 75 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| e1aggr | 1807 +- 130 | 20 of 75 | 110 | 83-27 | 74.8% | 16.3% (vs 14) |
| c5bank | 1782 +- 129 | 22 of 75 | 110 | 82-28 | 73.9% | 17.0% (vs 15) |
| c6pair | 1782 +- 129 | 23 of 75 | 110 | 82-28 | 73.9% | 17.0% (vs 15) |
| a3dig10 | 1782 +- 129 | 24 of 75 | 110 | 82-28 | 73.9% | 17.0% (vs 15) |
| e2aggr | 1757 +- 128 | 25 of 75 | 110 | 81-29 | 73.1% | 15.3% (vs 15) |
| a2relay | 1757 +- 128 | 26 of 75 | 110 | 81-29 | 73.1% | 15.3% (vs 15) |
| a2reloc | 1757 +- 128 | 27 of 75 | 110 | 81-29 | 73.1% | 15.3% (vs 15) |
| b1z2b | 1749 +- 24 | 28 of 75 | 1760 | 620-1140 | 72.8% | 14.8% (vs 15) |
| g_iter1 | 1749 +- 10 | 29 of 75 | 6990 | 2485-4505 | 72.8% | 14.8% (vs 15) |
| g1copy | 1743 +- 91 | 30 of 75 | 80 | 28-52 | 72.5% | 14.4% (vs 15) |
| g1sym | 1742 +- 51 | 31 of 75 | 200 | 68-132 | 72.5% | 14.4% (vs 15) |
| b2fs | 1735 +- 28 | 32 of 75 | 1280 | 439-841 | 72.3% | 14.0% (vs 15) |
| b1v2 | 1724 +- 22 | 34 of 75 | 2120 | 711-1409 | 71.9% | 15.6% (vs 16) |
| arch_rush | 1405 +- 94 | 37 of 75 | 110 | 62-48 | 56.6% | 6.3% (vs 18) |
| g_iter0 | 1340 +- 89 | 40 of 75 | 109 | 56-53 | 52.0% | 8.8% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2358 | 86 | 532 | 517-15 | 3% (g_iter1 7-217) |
| 2 | jmerle.camel_case_v21_final | 2358 | 86 | 532 | 517-15 | 4% (g_iter1 10-214) |
| 3 | IvanGeffner.kuma | 2327 | 80 | 532 | 514-18 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2325 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2238 | 63 | 532 | 502-30 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2210 | 59 | 532 | 497-35 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2195 | 57 | 532 | 494-38 | 6% (g_iter1 13-211) |
| 8 | Gymhgy.v10official | 2148 | 51 | 532 | 483-49 | 11% (g_iter1 25-199) |
| 9 | andrewgopher.player22 | 2115 | 47 | 532 | 474-58 | 11% (g_iter1 24-200) |
| 10 | **us:g1basics** | 2075 | 74 | 120 | 94-26 |  |
| 11 | hsmalladi.finalbot | 2059 | 42 | 532 | 455-77 | 12% (g_iter1 27-197) |
| 12 | CyrilSharma.finalBot | 2027 | 39 | 532 | 442-90 | 20% (g_iter1 44-180) |
| 13 | winkelmantanner.waffle | 1977 | 29 | 732 | 536-196 | 43% (g_iter2 96-128) |
| 14 | **us:g_iter2** | 1915 | 27 | 840 | 404-436 |  |
| 15 | ColtG5.Goob_final | 1860 | 12 | 3492 | 2199-1293 | 59% (g_iter2 109-75) |
| 16 | **us:arch_rush10** | 1832 | 130 | 110 | 84-26 |  |
| 17 | **us:a3dig5** | 1832 | 130 | 110 | 84-26 |  |
| 18 | **us:g_iter1_c2** | 1832 | 130 | 110 | 84-26 |  |
| 19 | kyleezz.jeeryfix3 | 1830 | 31 | 532 | 327-205 | 41% (g_iter1 91-133) |
| 20 | **us:e1aggr** | 1807 | 130 | 110 | 83-27 |  |
| 21 | quesswho.cretplayer2_3 | 1784 | 30 | 532 | 293-239 | 40% (g_iter1 90-134) |
| 22 | **us:c5bank** | 1782 | 129 | 110 | 82-28 |  |
| 23 | **us:c6pair** | 1782 | 129 | 110 | 82-28 |  |
| 24 | **us:a3dig10** | 1782 | 129 | 110 | 82-28 |  |
| 25 | **us:e2aggr** | 1757 | 128 | 110 | 81-29 |  |
| 26 | **us:a2relay** | 1757 | 128 | 110 | 81-29 |  |
| 27 | **us:a2reloc** | 1757 | 128 | 110 | 81-29 |  |
| 28 | **us:b1z2b** | 1749 | 24 | 1760 | 620-1140 |  |
| 29 | **us:g_iter1** | 1749 | 10 | 6990 | 2485-4505 |  |
| 30 | **us:g1copy** | 1743 | 91 | 80 | 28-52 |  |
| 31 | **us:g1sym** | 1742 | 51 | 200 | 68-132 |  |
| 32 | **us:b2fs** | 1735 | 28 | 1280 | 439-841 |  |
| 33 | SampleProvider.TSPAARKSPRINT1 | 1731 | 30 | 532 | 253-279 | 50% (g_iter1 112-112) |
| 34 | **us:b1v2** | 1724 | 22 | 2120 | 711-1409 |  |
| 35 | dmtrung14.defaultplayer_intlqualifier | 1567 | 34 | 532 | 140-392 | 69% (g_iter1 155-69) |
| 36 | clbarrell.duck8 | 1546 | 35 | 532 | 128-404 | 78% (g_iter1 175-49) |
| 37 | **us:arch_rush** | 1405 | 94 | 110 | 62-48 |  |
| 38 | jonters.bling3 | 1392 | 46 | 532 | 62-470 | 89% (g_iter1 200-24) |
| 39 | HugoIngelsson.Bot21 | 1343 | 201 | 26 | 3-23 |  |
| 40 | **us:g_iter0** | 1340 | 89 | 109 | 56-53 |  |
| 41 | awu7.ExplosiveBot | 1278 | 60 | 532 | 34-498 | 97% (g_iter1 217-7) |
| 42 | Metta-AI.bc24scenario | 1275 | 224 | 26 | 2-24 |  |
| 43 | noahzemlin.honeyducklings | 1275 | 224 | 26 | 2-24 |  |
| 44 | sivakovivan.NewHide | 1275 | 224 | 26 | 2-24 |  |
| 45 | JeffLegendPower.v11 | 1188 | 264 | 26 | 1-25 |  |
| 46 | MiloAkerman.v1 | 1188 | 264 | 26 | 1-25 |  |
| 47 | PerishoJ.tx | 1188 | 264 | 26 | 1-25 |  |
| 48 | Peter-Fun.dinoboxer | 1188 | 264 | 26 | 1-25 |  |
| 49 | RyanAspen.v22 | 1188 | 264 | 26 | 1-25 |  |
| 50 | TylerJulian.v9 | 1188 | 264 | 26 | 1-25 |  |
| 51 | aj-chau.cowards | 1188 | 264 | 26 | 1-25 |  |
| 52 | cViper971.ourplayer | 1188 | 264 | 26 | 1-25 |  |
| 53 | dylanzemlin.dangerduck2 | 1188 | 264 | 26 | 1-25 |  |
| 54 | lukerhoads.warrior_2nd_comp | 1188 | 264 | 26 | 1-25 |  |
| 55 | neilhuang007.baseline | 1188 | 264 | 26 | 1-25 |  |
| 56 | polyllc.polyv4 | 1188 | 264 | 26 | 1-25 |  |
| 57 | andrearante12.turtle | 1082 | 357 | 25 | 0-25 |  |
| 58 | AlexYu84.smartPlayer | 1050 | 357 | 26 | 0-26 |  |
| 59 | H4ffliger.keyboardcrusader_v1 | 1050 | 357 | 26 | 0-26 |  |
| 60 | Lithanium.AttackingBot | 1050 | 357 | 26 | 0-26 |  |
| 61 | Rubrasum.version_3 | 1050 | 357 | 26 | 0-26 |  |
| 62 | SriLakshmiPolavarapu.ducks | 1050 | 357 | 26 | 0-26 |  |
| 63 | VarunVejalla.alexander | 1050 | 357 | 26 | 0-26 |  |
| 64 | abdullah8a0.crayBasic | 1050 | 357 | 26 | 0-26 |  |
| 65 | adamseth2.moveBot1 | 1050 | 357 | 26 | 0-26 |  |
| 66 | dylanconklin.Team3 | 1050 | 357 | 26 | 0-26 |  |
| 67 | itswin.MPAttack | 1050 | 357 | 26 | 0-26 |  |
| 68 | joelcrouch.ducks | 1050 | 357 | 26 | 0-26 |  |
| 69 | lcforges.funkyguy3 | 1050 | 357 | 26 | 0-26 |  |
| 70 | qpwoeirut.tournament_sprint1 | 1050 | 357 | 26 | 0-26 |  |
| 71 | reeceyang.v5 | 1050 | 357 | 26 | 0-26 |  |
| 72 | samithShetty.combustiblelemon | 1050 | 357 | 26 | 0-26 |  |
| 73 | sayam-goyal.SimpleBot | 1050 | 357 | 26 | 0-26 |  |
| 74 | tlevietpdx.Sprint2 | 1050 | 357 | 26 | 0-26 |  |
| 75 | justinottesen.sprint1 | 927 | 153 | 532 | 4-528 | 100% (g_iter1 224-0) |
