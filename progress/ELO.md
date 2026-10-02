# Ladder

11229 scrimmages (ours only), 11229 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1880 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 17.9% (vs 14) |
| a3dig5 | 1880 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 17.9% (vs 14) |
| arch_rush10 | 1880 +- 132 | 17 of 71 | 110 | 84-26 | 75.6% | 17.9% (vs 14) |
| e1aggr | 1854 +- 131 | 18 of 71 | 110 | 83-27 | 74.7% | 16.1% (vs 14) |
| c6pair | 1828 +- 130 | 20 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| a3dig10 | 1828 +- 130 | 21 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c5bank | 1828 +- 130 | 22 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| g_iter1 | 1804 +- 14 | 23 of 71 | 4750 | 1721-3029 | 73.0% | 15.1% (vs 15) |
| a2reloc | 1803 +- 129 | 24 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| e2aggr | 1803 +- 129 | 25 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| a2relay | 1803 +- 129 | 26 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| b1z2b | 1802 +- 23 | 27 of 71 | 1760 | 620-1140 | 72.9% | 15.0% (vs 15) |
| b2fs | 1788 +- 28 | 29 of 71 | 1280 | 439-841 | 72.5% | 16.3% (vs 16) |
| b1v2 | 1777 +- 22 | 30 of 71 | 2120 | 711-1409 | 72.1% | 15.6% (vs 16) |
| arch_rush | 1443 +- 95 | 33 of 71 | 110 | 62-48 | 56.5% | 5.8% (vs 18) |
| g_iter0 | 1378 +- 90 | 36 of 71 | 109 | 56-53 | 52.0% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2427 | 95 | 504 | 492-12 | 5% (g_iter1 10-212) |
| 2 | IvanGeffner.kuma | 2401 | 89 | 504 | 490-14 | 4% (g_iter1 8-214) |
| 3 | Strequals.duck0127v5 | 2401 | 89 | 504 | 490-14 | 3% (g_iter1 7-215) |
| 4 | uravt.Version18Final | 2372 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2270 | 63 | 504 | 474-30 | 6% (g_iter1 13-209) |
| 6 | NotLLeon.v3 | 2253 | 60 | 504 | 471-33 | 8% (g_iter1 17-205) |
| 7 | andli28.v9_USQuals_angle | 2227 | 57 | 504 | 466-38 | 6% (g_iter1 13-209) |
| 8 | Gymhgy.v10official | 2204 | 54 | 504 | 461-43 | 11% (g_iter1 25-197) |
| 9 | andrewgopher.player22 | 2168 | 49 | 504 | 452-52 | 11% (g_iter1 24-198) |
| 10 | hsmalladi.finalbot | 2105 | 43 | 504 | 432-72 | 12% (g_iter1 27-195) |
| 11 | CyrilSharma.finalBot | 2081 | 41 | 504 | 423-81 | 19% (g_iter1 43-179) |
| 12 | winkelmantanner.waffle | 2041 | 38 | 504 | 406-98 | 21% (g_iter1 46-176) |
| 13 | ColtG5.Goob_final | 1905 | 26 | 744 | 484-260 | 36% (g_iter1 168-294) |
| 14 | kyleezz.jeeryfix3 | 1882 | 31 | 504 | 314-190 | 41% (g_iter1 90-132) |
| 15 | **us:g_iter1_c2** | 1880 | 132 | 110 | 84-26 |  |
| 16 | **us:a3dig5** | 1880 | 132 | 110 | 84-26 |  |
| 17 | **us:arch_rush10** | 1880 | 132 | 110 | 84-26 |  |
| 18 | **us:e1aggr** | 1854 | 131 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1836 | 31 | 504 | 282-222 | 41% (g_iter1 90-132) |
| 20 | **us:c6pair** | 1828 | 130 | 110 | 82-28 |  |
| 21 | **us:a3dig10** | 1828 | 130 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1828 | 130 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1804 | 14 | 4750 | 1721-3029 |  |
| 24 | **us:a2reloc** | 1803 | 129 | 110 | 81-29 |  |
| 25 | **us:e2aggr** | 1803 | 129 | 110 | 81-29 |  |
| 26 | **us:a2relay** | 1803 | 129 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1802 | 23 | 1760 | 620-1140 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1791 | 30 | 504 | 250-254 | 50% (g_iter1 110-112) |
| 29 | **us:b2fs** | 1788 | 28 | 1280 | 439-841 |  |
| 30 | **us:b1v2** | 1777 | 22 | 2120 | 711-1409 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1623 | 34 | 504 | 138-366 | 69% (g_iter1 153-69) |
| 32 | clbarrell.duck8 | 1604 | 35 | 504 | 127-377 | 78% (g_iter1 173-49) |
| 33 | **us:arch_rush** | 1443 | 95 | 110 | 62-48 |  |
| 34 | jonters.bling3 | 1440 | 47 | 504 | 59-445 | 90% (g_iter1 199-23) |
| 35 | HugoIngelsson.Bot21 | 1380 | 202 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1378 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1336 | 60 | 504 | 34-470 | 97% (g_iter1 215-7) |
| 38 | Metta-AI.bc24scenario | 1312 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1312 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1312 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1223 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1223 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1223 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1223 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1223 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1223 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1223 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1223 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1223 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1223 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1223 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1223 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1118 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1085 | 357 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1085 | 357 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1085 | 357 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1085 | 357 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1085 | 357 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1085 | 357 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1085 | 357 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1085 | 357 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1085 | 357 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1085 | 357 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1085 | 357 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1085 | 357 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1085 | 357 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1085 | 357 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1085 | 357 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1085 | 357 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1085 | 357 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 983 | 153 | 504 | 4-500 | 100% (g_iter1 222-0) |
