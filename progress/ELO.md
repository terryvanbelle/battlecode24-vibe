# Ladder

11949 scrimmages (ours only), 11949 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1877 +- 131 | 15 of 71 | 110 | 84-26 | 75.6% | 17.9% (vs 14) |
| arch_rush10 | 1877 +- 131 | 16 of 71 | 110 | 84-26 | 75.6% | 17.9% (vs 14) |
| g_iter1_c2 | 1877 +- 131 | 17 of 71 | 110 | 84-26 | 75.6% | 17.9% (vs 14) |
| e1aggr | 1851 +- 131 | 18 of 71 | 110 | 83-27 | 74.8% | 16.1% (vs 14) |
| a3dig10 | 1825 +- 130 | 20 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c6pair | 1825 +- 130 | 21 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c5bank | 1825 +- 130 | 22 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| a2reloc | 1800 +- 129 | 23 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| e2aggr | 1800 +- 129 | 24 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| a2relay | 1800 +- 129 | 25 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| g_iter1 | 1800 +- 12 | 26 of 71 | 5470 | 1976-3494 | 73.0% | 15.1% (vs 15) |
| b1z2b | 1798 +- 23 | 27 of 71 | 1760 | 620-1140 | 72.9% | 15.0% (vs 15) |
| b2fs | 1785 +- 28 | 29 of 71 | 1280 | 439-841 | 72.4% | 16.3% (vs 16) |
| b1v2 | 1774 +- 22 | 30 of 71 | 2120 | 711-1409 | 72.1% | 15.6% (vs 16) |
| arch_rush | 1441 +- 95 | 33 of 71 | 110 | 62-48 | 56.5% | 5.9% (vs 18) |
| g_iter0 | 1375 +- 90 | 36 of 71 | 109 | 56-53 | 52.0% | 8.4% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2423 | 95 | 504 | 492-12 | 5% (g_iter1 10-212) |
| 2 | IvanGeffner.kuma | 2398 | 89 | 504 | 490-14 | 4% (g_iter1 8-214) |
| 3 | Strequals.duck0127v5 | 2398 | 89 | 504 | 490-14 | 3% (g_iter1 7-215) |
| 4 | uravt.Version18Final | 2369 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2266 | 63 | 504 | 474-30 | 6% (g_iter1 13-209) |
| 6 | NotLLeon.v3 | 2249 | 60 | 504 | 471-33 | 8% (g_iter1 17-205) |
| 7 | andli28.v9_USQuals_angle | 2223 | 57 | 504 | 466-38 | 6% (g_iter1 13-209) |
| 8 | Gymhgy.v10official | 2200 | 54 | 504 | 461-43 | 11% (g_iter1 25-197) |
| 9 | andrewgopher.player22 | 2165 | 49 | 504 | 452-52 | 11% (g_iter1 24-198) |
| 10 | hsmalladi.finalbot | 2101 | 43 | 504 | 432-72 | 12% (g_iter1 27-195) |
| 11 | CyrilSharma.finalBot | 2077 | 41 | 504 | 423-81 | 19% (g_iter1 43-179) |
| 12 | winkelmantanner.waffle | 2037 | 38 | 504 | 406-98 | 21% (g_iter1 46-176) |
| 13 | ColtG5.Goob_final | 1903 | 19 | 1464 | 949-515 | 36% (g_iter1 423-759) |
| 14 | kyleezz.jeeryfix3 | 1878 | 31 | 504 | 314-190 | 41% (g_iter1 90-132) |
| 15 | **us:a3dig5** | 1877 | 131 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1877 | 131 | 110 | 84-26 |  |
| 17 | **us:g_iter1_c2** | 1877 | 131 | 110 | 84-26 |  |
| 18 | **us:e1aggr** | 1851 | 131 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1832 | 31 | 504 | 282-222 | 41% (g_iter1 90-132) |
| 20 | **us:a3dig10** | 1825 | 130 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1825 | 130 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1825 | 130 | 110 | 82-28 |  |
| 23 | **us:a2reloc** | 1800 | 129 | 110 | 81-29 |  |
| 24 | **us:e2aggr** | 1800 | 129 | 110 | 81-29 |  |
| 25 | **us:a2relay** | 1800 | 129 | 110 | 81-29 |  |
| 26 | **us:g_iter1** | 1800 | 12 | 5470 | 1976-3494 |  |
| 27 | **us:b1z2b** | 1798 | 23 | 1760 | 620-1140 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1788 | 30 | 504 | 250-254 | 50% (g_iter1 110-112) |
| 29 | **us:b2fs** | 1785 | 28 | 1280 | 439-841 |  |
| 30 | **us:b1v2** | 1774 | 22 | 2120 | 711-1409 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1620 | 34 | 504 | 138-366 | 69% (g_iter1 153-69) |
| 32 | clbarrell.duck8 | 1600 | 35 | 504 | 127-377 | 78% (g_iter1 173-49) |
| 33 | **us:arch_rush** | 1441 | 95 | 110 | 62-48 |  |
| 34 | jonters.bling3 | 1437 | 47 | 504 | 59-445 | 90% (g_iter1 199-23) |
| 35 | HugoIngelsson.Bot21 | 1377 | 202 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1375 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1332 | 60 | 504 | 34-470 | 97% (g_iter1 215-7) |
| 38 | Metta-AI.bc24scenario | 1309 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1309 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1309 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1221 | 264 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1221 | 264 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1221 | 264 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1221 | 264 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1221 | 264 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1221 | 264 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1221 | 264 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1221 | 264 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1221 | 264 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1221 | 264 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1221 | 264 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1221 | 264 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1115 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1083 | 357 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1083 | 357 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1083 | 357 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1083 | 357 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1083 | 357 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1083 | 357 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1083 | 357 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1083 | 357 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1083 | 357 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1083 | 357 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1083 | 357 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1083 | 357 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1083 | 357 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1083 | 357 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1083 | 357 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1083 | 357 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1083 | 357 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 980 | 153 | 504 | 4-500 | 100% (g_iter1 222-0) |
