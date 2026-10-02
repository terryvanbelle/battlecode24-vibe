# Ladder

9389 scrimmages (ours only), 9389 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1889 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| arch_rush10 | 1889 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| g_iter1_c2 | 1889 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| e1aggr | 1862 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 15.9% (vs 14) |
| a3dig10 | 1837 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.5% (vs 15) |
| c6pair | 1837 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.5% (vs 15) |
| c5bank | 1837 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.5% (vs 15) |
| g_iter1 | 1812 +- 16 | 23 of 71 | 4110 | 1493-2617 | 73.0% | 15.0% (vs 15) |
| a2reloc | 1811 +- 130 | 24 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| a2relay | 1811 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| e2aggr | 1811 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| b1z2b | 1801 +- 27 | 28 of 71 | 1360 | 470-890 | 72.6% | 16.5% (vs 16) |
| b1v2 | 1784 +- 24 | 29 of 71 | 1720 | 575-1145 | 72.0% | 15.4% (vs 16) |
| b2fs | 1783 +- 34 | 30 of 71 | 880 | 294-586 | 72.0% | 15.3% (vs 16) |
| arch_rush | 1450 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1384 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2402 | 96 | 424 | 412-12 | 3% (g_iter1 7-195) |
| 2 | jmerle.camel_case_v21_final | 2402 | 96 | 424 | 412-12 | 5% (g_iter1 10-192) |
| 3 | uravt.Version18Final | 2380 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2377 | 89 | 424 | 410-14 | 4% (g_iter1 8-194) |
| 5 | NotLLeon.v3 | 2256 | 66 | 424 | 396-28 | 7% (g_iter1 15-187) |
| 6 | andli28.v9_USQuals_angle | 2256 | 66 | 424 | 396-28 | 6% (g_iter1 12-190) |
| 7 | chenyx512.flagbot_final | 2250 | 64 | 424 | 395-29 | 6% (g_iter1 12-190) |
| 8 | andrewgopher.player22 | 2226 | 61 | 424 | 391-33 | 9% (g_iter1 19-183) |
| 9 | Gymhgy.v10official | 2210 | 59 | 424 | 388-36 | 12% (g_iter1 24-178) |
| 10 | hsmalladi.finalbot | 2115 | 47 | 424 | 365-59 | 11% (g_iter1 23-179) |
| 11 | CyrilSharma.finalBot | 2096 | 46 | 424 | 359-65 | 20% (g_iter1 40-162) |
| 12 | winkelmantanner.waffle | 2075 | 44 | 424 | 352-72 | 20% (g_iter1 41-161) |
| 13 | ColtG5.Goob_final | 1906 | 35 | 424 | 275-149 | 38% (g_iter1 77-125) |
| 14 | **us:a3dig5** | 1889 | 132 | 110 | 84-26 |  |
| 15 | **us:arch_rush10** | 1889 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1889 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1883 | 34 | 424 | 262-162 | 41% (g_iter1 83-119) |
| 18 | **us:e1aggr** | 1862 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1846 | 33 | 424 | 240-184 | 39% (g_iter1 78-124) |
| 20 | **us:a3dig10** | 1837 | 131 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1837 | 131 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1837 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1812 | 16 | 4110 | 1493-2617 |  |
| 24 | **us:a2reloc** | 1811 | 130 | 110 | 81-29 |  |
| 25 | **us:a2relay** | 1811 | 130 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1811 | 130 | 110 | 81-29 |  |
| 27 | SampleProvider.TSPAARKSPRINT1 | 1803 | 33 | 424 | 214-210 | 50% (g_iter1 101-101) |
| 28 | **us:b1z2b** | 1801 | 27 | 1360 | 470-890 |  |
| 29 | **us:b1v2** | 1784 | 24 | 1720 | 575-1145 |  |
| 30 | **us:b2fs** | 1783 | 34 | 880 | 294-586 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1624 | 37 | 424 | 114-310 | 69% (g_iter1 139-63) |
| 32 | clbarrell.duck8 | 1589 | 39 | 424 | 98-326 | 78% (g_iter1 158-44) |
| 33 | jonters.bling3 | 1469 | 49 | 424 | 56-368 | 89% (g_iter1 179-23) |
| 34 | **us:arch_rush** | 1450 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1386 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1384 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1343 | 65 | 424 | 29-395 | 98% (g_iter1 197-5) |
| 38 | Metta-AI.bc24scenario | 1318 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1318 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1318 | 225 | 26 | 2-24 |  |
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
| 54 | AlexYu84.smartPlayer | 1091 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1091 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1091 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1091 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1091 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1091 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1091 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1091 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1091 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1091 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1091 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1091 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1091 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1091 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1091 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1091 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1091 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1017 | 154 | 424 | 4-420 | 100% (g_iter1 202-0) |
