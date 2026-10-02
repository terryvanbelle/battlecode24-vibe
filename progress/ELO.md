# Ladder

9709 scrimmages (ours only), 9709 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1888 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| arch_rush10 | 1888 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| g_iter1_c2 | 1888 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| e1aggr | 1861 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 15.9% (vs 14) |
| c6pair | 1836 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| a3dig10 | 1836 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c5bank | 1836 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| g_iter1 | 1812 +- 15 | 23 of 71 | 4190 | 1523-2667 | 73.0% | 15.0% (vs 15) |
| e2aggr | 1810 +- 130 | 24 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| a2relay | 1810 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| a2reloc | 1810 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| b1z2b | 1797 +- 26 | 28 of 71 | 1440 | 495-945 | 72.5% | 16.3% (vs 16) |
| b2fs | 1787 +- 32 | 29 of 71 | 960 | 324-636 | 72.2% | 15.7% (vs 16) |
| b1v2 | 1779 +- 24 | 30 of 71 | 1800 | 597-1203 | 71.9% | 15.1% (vs 16) |
| arch_rush | 1449 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.2% (vs 19) |
| g_iter0 | 1383 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2407 | 96 | 440 | 428-12 | 3% (g_iter1 7-199) |
| 2 | jmerle.camel_case_v21_final | 2407 | 96 | 440 | 428-12 | 5% (g_iter1 10-196) |
| 3 | IvanGeffner.kuma | 2382 | 89 | 440 | 426-14 | 4% (g_iter1 8-198) |
| 4 | uravt.Version18Final | 2379 | 348 | 26 | 26-0 |  |
| 5 | NotLLeon.v3 | 2261 | 65 | 440 | 412-28 | 7% (g_iter1 15-191) |
| 6 | andli28.v9_USQuals_angle | 2261 | 65 | 440 | 412-28 | 6% (g_iter1 12-194) |
| 7 | chenyx512.flagbot_final | 2255 | 64 | 440 | 411-29 | 6% (g_iter1 12-194) |
| 8 | Gymhgy.v10official | 2211 | 58 | 440 | 403-37 | 12% (g_iter1 24-182) |
| 9 | andrewgopher.player22 | 2206 | 57 | 440 | 402-38 | 10% (g_iter1 20-186) |
| 10 | hsmalladi.finalbot | 2112 | 46 | 440 | 378-62 | 12% (g_iter1 24-182) |
| 11 | CyrilSharma.finalBot | 2096 | 45 | 440 | 373-67 | 19% (g_iter1 40-166) |
| 12 | winkelmantanner.waffle | 2065 | 42 | 440 | 362-78 | 20% (g_iter1 42-164) |
| 13 | ColtG5.Goob_final | 1909 | 34 | 440 | 288-152 | 38% (g_iter1 79-127) |
| 14 | **us:a3dig5** | 1888 | 132 | 110 | 84-26 |  |
| 15 | **us:arch_rush10** | 1888 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1888 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1882 | 33 | 440 | 272-168 | 41% (g_iter1 84-122) |
| 18 | **us:e1aggr** | 1861 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1846 | 33 | 440 | 250-190 | 39% (g_iter1 81-125) |
| 20 | **us:c6pair** | 1836 | 131 | 110 | 82-28 |  |
| 21 | **us:a3dig10** | 1836 | 131 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1836 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1812 | 15 | 4190 | 1523-2667 |  |
| 24 | **us:e2aggr** | 1810 | 130 | 110 | 81-29 |  |
| 25 | **us:a2relay** | 1810 | 130 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1810 | 130 | 110 | 81-29 |  |
| 27 | SampleProvider.TSPAARKSPRINT1 | 1804 | 33 | 440 | 224-216 | 50% (g_iter1 104-102) |
| 28 | **us:b1z2b** | 1797 | 26 | 1440 | 495-945 |  |
| 29 | **us:b2fs** | 1787 | 32 | 960 | 324-636 |  |
| 30 | **us:b1v2** | 1779 | 24 | 1800 | 597-1203 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1620 | 37 | 440 | 117-323 | 69% (g_iter1 143-63) |
| 32 | clbarrell.duck8 | 1595 | 38 | 440 | 105-335 | 78% (g_iter1 161-45) |
| 33 | jonters.bling3 | 1461 | 49 | 440 | 56-384 | 89% (g_iter1 183-23) |
| 34 | **us:arch_rush** | 1449 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1385 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1383 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1341 | 64 | 440 | 30-410 | 97% (g_iter1 200-6) |
| 38 | Metta-AI.bc24scenario | 1317 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1317 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1317 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1229 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1229 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1229 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1229 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1229 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1229 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1229 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1229 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1229 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1229 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1229 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1229 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1123 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1090 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1090 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1090 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1090 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1090 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1090 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1090 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1090 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1090 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1090 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1090 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1090 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1090 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1090 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1090 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1090 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1090 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1009 | 154 | 440 | 4-436 | 100% (g_iter1 206-0) |
