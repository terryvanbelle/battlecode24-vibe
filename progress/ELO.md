# Ladder

7829 scrimmages (ours only), 7829 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1893 +- 133 | 14 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| arch_rush10 | 1893 +- 133 | 15 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| a3dig5 | 1893 +- 133 | 16 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| e1aggr | 1867 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| c5bank | 1841 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c6pair | 1841 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| a3dig10 | 1841 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| b1z2b | 1825 +- 32 | 23 of 71 | 960 | 345-615 | 73.3% | 15.6% (vs 15) |
| g_iter1 | 1820 +- 16 | 24 of 71 | 3710 | 1358-2352 | 73.1% | 15.2% (vs 15) |
| a2reloc | 1815 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1815 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| e2aggr | 1815 +- 130 | 27 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1v2 | 1784 +- 27 | 29 of 71 | 1360 | 450-910 | 71.9% | 15.1% (vs 16) |
| b2fs | 1767 +- 46 | 30 of 71 | 480 | 154-326 | 71.3% | 14.1% (vs 16) |
| arch_rush | 1454 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1388 +- 89 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2440 | 115 | 346 | 338-8 | 0% (b1v2 0-68) |
| 2 | jmerle.camel_case_v21_final | 2404 | 104 | 346 | 336-10 | 0% (b1v2 0-68) |
| 3 | uravt.Version18Final | 2385 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2361 | 93 | 346 | 333-13 | 3% (b1v2 2-66) |
| 5 | andli28.v9_USQuals_angle | 2278 | 75 | 346 | 325-21 | 4% (b1v2 3-65) |
| 6 | chenyx512.flagbot_final | 2270 | 74 | 346 | 324-22 | 4% (b1v2 3-65) |
| 7 | NotLLeon.v3 | 2262 | 72 | 346 | 323-23 | 9% (b1v2 6-62) |
| 8 | Gymhgy.v10official | 2226 | 66 | 346 | 318-28 | 3% (b1v2 2-66) |
| 9 | andrewgopher.player22 | 2220 | 65 | 346 | 317-29 | 7% (b1v2 5-63) |
| 10 | hsmalladi.finalbot | 2119 | 52 | 346 | 297-49 | 12% (b1v2 8-60) |
| 11 | CyrilSharma.finalBot | 2104 | 51 | 346 | 293-53 | 12% (b1v2 8-60) |
| 12 | winkelmantanner.waffle | 2068 | 47 | 346 | 283-63 | 15% (b1v2 10-58) |
| 13 | ColtG5.Goob_final | 1902 | 38 | 346 | 219-127 | 34% (b1v2 23-45) |
| 14 | **us:g_iter1_c2** | 1893 | 133 | 110 | 84-26 |  |
| 15 | **us:arch_rush10** | 1893 | 133 | 110 | 84-26 |  |
| 16 | **us:a3dig5** | 1893 | 133 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1883 | 38 | 346 | 210-136 | 34% (b1v2 23-45) |
| 18 | **us:e1aggr** | 1867 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1851 | 37 | 346 | 195-151 | 46% (b1v2 31-37) |
| 20 | **us:c5bank** | 1841 | 131 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1841 | 131 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1841 | 131 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1825 | 32 | 960 | 345-615 |  |
| 24 | **us:g_iter1** | 1820 | 16 | 3710 | 1358-2352 |  |
| 25 | **us:a2reloc** | 1815 | 130 | 110 | 81-29 |  |
| 26 | **us:a2relay** | 1815 | 130 | 110 | 81-29 |  |
| 27 | **us:e2aggr** | 1815 | 130 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1813 | 37 | 346 | 176-170 | 49% (b1v2 33-35) |
| 29 | **us:b1v2** | 1784 | 27 | 1360 | 450-910 |  |
| 30 | **us:b2fs** | 1767 | 46 | 480 | 154-326 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1638 | 41 | 346 | 96-250 | 69% (b1v2 47-21) |
| 32 | clbarrell.duck8 | 1592 | 44 | 346 | 79-267 | 79% (b1v2 54-14) |
| 33 | jonters.bling3 | 1472 | 55 | 346 | 45-301 | 90% (b1v2 61-7) |
| 34 | **us:arch_rush** | 1454 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1390 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1388 | 89 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1336 | 74 | 346 | 22-324 | 93% (b1v2 63-5) |
| 38 | Metta-AI.bc24scenario | 1322 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1322 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1322 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1233 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1233 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1233 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1233 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1233 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1233 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1233 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1233 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1233 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1233 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1233 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1233 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1127 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1094 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1094 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1094 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1094 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1094 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1094 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1094 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1094 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1094 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1094 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1094 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1094 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1094 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1094 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1094 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1094 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1094 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1056 | 154 | 346 | 4-342 | 100% (b1v2 68-0) |
