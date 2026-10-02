# Ladder

6629 scrimmages (ours only), 6629 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| arch_rush10 | 1894 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.4% (vs 13) |
| a3dig5 | 1894 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.4% (vs 13) |
| g_iter1_c2 | 1894 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.4% (vs 13) |
| e1aggr | 1868 +- 131 | 18 of 71 | 110 | 83-27 | 74.7% | 16.2% (vs 14) |
| c6pair | 1842 +- 130 | 20 of 71 | 110 | 82-28 | 73.9% | 16.8% (vs 15) |
| c5bank | 1842 +- 130 | 21 of 71 | 110 | 82-28 | 73.9% | 16.8% (vs 15) |
| a3dig10 | 1842 +- 130 | 22 of 71 | 110 | 82-28 | 73.9% | 16.8% (vs 15) |
| b1z2b | 1834 +- 37 | 23 of 71 | 680 | 248-432 | 73.6% | 16.2% (vs 15) |
| g_iter1 | 1821 +- 17 | 24 of 71 | 3390 | 1243-2147 | 73.1% | 15.4% (vs 15) |
| a2relay | 1817 +- 129 | 25 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| e2aggr | 1817 +- 129 | 26 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| a2reloc | 1817 +- 129 | 27 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| b1v2 | 1785 +- 31 | 29 of 71 | 1040 | 344-696 | 71.9% | 15.3% (vs 16) |
| b2fs | 1750 +- 72 | 30 of 71 | 200 | 62-138 | 70.6% | 13.2% (vs 16) |
| arch_rush | 1456 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.0% (vs 19) |
| g_iter0 | 1390 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.2% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2482 | 140 | 286 | 281-5 | 3% (b1z2b 1-33) |
| 2 | jmerle.camel_case_v21_final | 2410 | 115 | 286 | 278-8 | 0% (b1z2b 0-34) |
| 3 | uravt.Version18Final | 2386 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2330 | 93 | 286 | 273-13 | 6% (b1z2b 2-32) |
| 5 | chenyx512.flagbot_final | 2330 | 93 | 286 | 273-13 | 3% (b1z2b 1-33) |
| 6 | andli28.v9_USQuals_angle | 2294 | 85 | 286 | 270-16 | 12% (b1z2b 4-30) |
| 7 | NotLLeon.v3 | 2255 | 77 | 286 | 266-20 | 9% (b1z2b 3-31) |
| 8 | Gymhgy.v10official | 2214 | 70 | 286 | 261-25 | 6% (b1z2b 2-32) |
| 9 | andrewgopher.player22 | 2200 | 68 | 286 | 259-27 | 15% (b1z2b 5-29) |
| 10 | hsmalladi.finalbot | 2115 | 56 | 286 | 244-42 | 15% (b1z2b 5-29) |
| 11 | CyrilSharma.finalBot | 2084 | 53 | 286 | 237-49 | 12% (b1z2b 4-30) |
| 12 | winkelmantanner.waffle | 2059 | 51 | 286 | 231-55 | 24% (b1z2b 8-26) |
| 13 | ColtG5.Goob_final | 1902 | 42 | 286 | 180-106 | 41% (b1z2b 14-20) |
| 14 | **us:arch_rush10** | 1894 | 132 | 110 | 84-26 |  |
| 15 | **us:a3dig5** | 1894 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1894 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1884 | 41 | 286 | 173-113 | 47% (b1z2b 16-18) |
| 18 | **us:e1aggr** | 1868 | 131 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1856 | 41 | 286 | 162-124 | 62% (b1z2b 21-13) |
| 20 | **us:c6pair** | 1842 | 130 | 110 | 82-28 |  |
| 21 | **us:c5bank** | 1842 | 130 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1842 | 130 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1834 | 37 | 680 | 248-432 |  |
| 24 | **us:g_iter1** | 1821 | 17 | 3390 | 1243-2147 |  |
| 25 | **us:a2relay** | 1817 | 129 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1817 | 129 | 110 | 81-29 |  |
| 27 | **us:a2reloc** | 1817 | 129 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1812 | 40 | 286 | 144-142 | 56% (b1z2b 19-15) |
| 29 | **us:b1v2** | 1785 | 31 | 1040 | 344-696 |  |
| 30 | **us:b2fs** | 1750 | 72 | 200 | 62-138 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1664 | 44 | 286 | 87-199 | 82% (b1z2b 28-6) |
| 32 | clbarrell.duck8 | 1597 | 48 | 286 | 66-220 | 65% (b1z2b 22-12) |
| 33 | jonters.bling3 | 1468 | 61 | 286 | 36-250 | 79% (b1z2b 27-7) |
| 34 | **us:arch_rush** | 1456 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1391 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1390 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1336 | 82 | 286 | 18-268 | 94% (b1z2b 32-2) |
| 38 | Metta-AI.bc24scenario | 1323 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1323 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1323 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1234 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1234 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1234 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1234 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1234 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1234 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1234 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1234 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1234 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1234 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1234 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1234 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1128 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1096 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1096 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1096 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1096 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1096 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1096 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1096 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1096 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1096 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1096 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1096 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1096 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1096 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1096 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1096 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1096 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1096 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1090 | 155 | 286 | 4-282 | 100% (b1z2b 34-0) |
