# Ladder

10989 scrimmages (ours only), 10989 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1881 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 17.9% (vs 14) |
| g_iter1_c2 | 1881 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 17.9% (vs 14) |
| arch_rush10 | 1881 +- 132 | 17 of 71 | 110 | 84-26 | 75.6% | 17.9% (vs 14) |
| e1aggr | 1855 +- 131 | 18 of 71 | 110 | 83-27 | 74.7% | 16.1% (vs 14) |
| c5bank | 1829 +- 130 | 20 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c6pair | 1829 +- 130 | 21 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| a3dig10 | 1829 +- 130 | 22 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| g_iter1 | 1806 +- 15 | 23 of 71 | 4510 | 1639-2871 | 73.1% | 15.2% (vs 15) |
| e2aggr | 1804 +- 129 | 24 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| a2reloc | 1804 +- 129 | 25 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| a2relay | 1804 +- 129 | 26 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| b1z2b | 1803 +- 23 | 27 of 71 | 1760 | 620-1140 | 72.9% | 15.0% (vs 15) |
| b2fs | 1789 +- 28 | 29 of 71 | 1280 | 439-841 | 72.5% | 16.4% (vs 16) |
| b1v2 | 1778 +- 22 | 30 of 71 | 2120 | 711-1409 | 72.1% | 15.7% (vs 16) |
| arch_rush | 1444 +- 95 | 33 of 71 | 110 | 62-48 | 56.5% | 5.8% (vs 18) |
| g_iter0 | 1378 +- 90 | 36 of 71 | 109 | 56-53 | 52.0% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2428 | 96 | 504 | 492-12 | 0% (b2fs 0-64) |
| 2 | IvanGeffner.kuma | 2403 | 89 | 504 | 490-14 | 2% (b2fs 1-63) |
| 3 | Strequals.duck0127v5 | 2403 | 89 | 504 | 490-14 | 2% (b2fs 1-63) |
| 4 | uravt.Version18Final | 2373 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2271 | 63 | 504 | 474-30 | 8% (b2fs 5-59) |
| 6 | NotLLeon.v3 | 2254 | 60 | 504 | 471-33 | 5% (b2fs 3-61) |
| 7 | andli28.v9_USQuals_angle | 2228 | 57 | 504 | 466-38 | 12% (b2fs 8-56) |
| 8 | Gymhgy.v10official | 2206 | 54 | 504 | 461-43 | 8% (b2fs 5-59) |
| 9 | andrewgopher.player22 | 2170 | 49 | 504 | 452-52 | 14% (b2fs 9-55) |
| 10 | hsmalladi.finalbot | 2106 | 43 | 504 | 432-72 | 14% (b2fs 9-55) |
| 11 | CyrilSharma.finalBot | 2082 | 41 | 504 | 423-81 | 16% (b2fs 10-54) |
| 12 | winkelmantanner.waffle | 2042 | 38 | 504 | 406-98 | 20% (b2fs 13-51) |
| 13 | ColtG5.Goob_final | 1901 | 32 | 504 | 326-178 | 31% (b2fs 20-44) |
| 14 | kyleezz.jeeryfix3 | 1883 | 31 | 504 | 314-190 | 33% (b2fs 21-43) |
| 15 | **us:a3dig5** | 1881 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1881 | 132 | 110 | 84-26 |  |
| 17 | **us:arch_rush10** | 1881 | 132 | 110 | 84-26 |  |
| 18 | **us:e1aggr** | 1855 | 131 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1837 | 31 | 504 | 282-222 | 38% (b2fs 24-40) |
| 20 | **us:c5bank** | 1829 | 130 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1829 | 130 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1829 | 130 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1806 | 15 | 4510 | 1639-2871 |  |
| 24 | **us:e2aggr** | 1804 | 129 | 110 | 81-29 |  |
| 25 | **us:a2reloc** | 1804 | 129 | 110 | 81-29 |  |
| 26 | **us:a2relay** | 1804 | 129 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1803 | 23 | 1760 | 620-1140 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1793 | 30 | 504 | 250-254 | 44% (b2fs 28-36) |
| 29 | **us:b2fs** | 1789 | 28 | 1280 | 439-841 |  |
| 30 | **us:b1v2** | 1778 | 22 | 2120 | 711-1409 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1625 | 34 | 504 | 138-366 | 78% (b2fs 50-14) |
| 32 | clbarrell.duck8 | 1605 | 35 | 504 | 127-377 | 75% (b2fs 48-16) |
| 33 | **us:arch_rush** | 1444 | 95 | 110 | 62-48 |  |
| 34 | jonters.bling3 | 1442 | 47 | 504 | 59-445 | 91% (b2fs 58-6) |
| 35 | HugoIngelsson.Bot21 | 1381 | 202 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1378 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1337 | 60 | 504 | 34-470 | 97% (b2fs 62-2) |
| 38 | Metta-AI.bc24scenario | 1313 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1313 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1313 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1224 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1224 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1224 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1224 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1224 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1224 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1224 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1224 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1224 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1224 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1224 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1224 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1118 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1086 | 357 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1086 | 357 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1086 | 357 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1086 | 357 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1086 | 357 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1086 | 357 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1086 | 357 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1086 | 357 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1086 | 357 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1086 | 357 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1086 | 357 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1086 | 357 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1086 | 357 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1086 | 357 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1086 | 357 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1086 | 357 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1086 | 357 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 984 | 153 | 504 | 4-500 | 100% (b2fs 64-0) |
