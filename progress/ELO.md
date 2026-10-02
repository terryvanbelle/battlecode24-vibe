# Ladder

7629 scrimmages (ours only), 7629 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1893 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| arch_rush10 | 1893 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| g_iter1_c2 | 1893 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| e1aggr | 1867 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| c5bank | 1841 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c6pair | 1841 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| a3dig10 | 1841 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| b1z2b | 1824 +- 32 | 23 of 71 | 920 | 330-590 | 73.3% | 15.5% (vs 15) |
| g_iter1 | 1820 +- 16 | 24 of 71 | 3670 | 1345-2325 | 73.1% | 15.3% (vs 15) |
| a2reloc | 1815 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| e2aggr | 1815 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1815 +- 130 | 27 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1v2 | 1784 +- 28 | 29 of 71 | 1280 | 424-856 | 71.9% | 15.2% (vs 16) |
| b2fs | 1763 +- 48 | 30 of 71 | 440 | 140-300 | 71.1% | 13.9% (vs 16) |
| arch_rush | 1454 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1389 +- 89 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2456 | 122 | 336 | 329-7 | 3% (g_iter1 5-175) |
| 2 | jmerle.camel_case_v21_final | 2400 | 104 | 336 | 326-10 | 4% (g_iter1 8-172) |
| 3 | uravt.Version18Final | 2385 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2356 | 93 | 336 | 323-13 | 4% (g_iter1 8-172) |
| 5 | andli28.v9_USQuals_angle | 2291 | 79 | 336 | 317-19 | 6% (g_iter1 10-170) |
| 6 | chenyx512.flagbot_final | 2273 | 75 | 336 | 315-21 | 6% (g_iter1 11-169) |
| 7 | NotLLeon.v3 | 2265 | 74 | 336 | 314-22 | 7% (g_iter1 13-167) |
| 8 | Gymhgy.v10official | 2228 | 67 | 336 | 309-27 | 12% (g_iter1 22-158) |
| 9 | andrewgopher.player22 | 2221 | 66 | 336 | 308-28 | 10% (g_iter1 18-162) |
| 10 | hsmalladi.finalbot | 2114 | 52 | 336 | 287-49 | 12% (g_iter1 21-159) |
| 11 | CyrilSharma.finalBot | 2098 | 51 | 336 | 283-53 | 21% (g_iter1 37-143) |
| 12 | winkelmantanner.waffle | 2065 | 48 | 336 | 274-62 | 22% (g_iter1 39-141) |
| 13 | ColtG5.Goob_final | 1901 | 39 | 336 | 212-124 | 39% (g_iter1 71-109) |
| 14 | **us:a3dig5** | 1893 | 132 | 110 | 84-26 |  |
| 15 | **us:arch_rush10** | 1893 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1893 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1883 | 38 | 336 | 204-132 | 42% (g_iter1 75-105) |
| 18 | **us:e1aggr** | 1867 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1851 | 38 | 336 | 189-147 | 38% (g_iter1 69-111) |
| 20 | **us:c5bank** | 1841 | 131 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1841 | 131 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1841 | 131 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1824 | 32 | 920 | 330-590 |  |
| 24 | **us:g_iter1** | 1820 | 16 | 3670 | 1345-2325 |  |
| 25 | **us:a2reloc** | 1815 | 130 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1815 | 130 | 110 | 81-29 |  |
| 27 | **us:a2relay** | 1815 | 130 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1813 | 37 | 336 | 171-165 | 50% (g_iter1 90-90) |
| 29 | **us:b1v2** | 1784 | 28 | 1280 | 424-856 |  |
| 30 | **us:b2fs** | 1763 | 48 | 440 | 140-300 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1643 | 42 | 336 | 95-241 | 68% (g_iter1 123-57) |
| 32 | clbarrell.duck8 | 1585 | 45 | 336 | 74-262 | 79% (g_iter1 142-38) |
| 33 | jonters.bling3 | 1479 | 55 | 336 | 45-291 | 89% (g_iter1 160-20) |
| 34 | **us:arch_rush** | 1454 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1390 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1389 | 89 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1341 | 74 | 336 | 22-314 | 97% (g_iter1 175-5) |
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
| 54 | AlexYu84.smartPlayer | 1095 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1095 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1095 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1095 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1095 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1095 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1095 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1095 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1095 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1095 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1095 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1095 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1095 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1095 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1095 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1095 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1095 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1062 | 154 | 336 | 4-332 | 100% (g_iter1 180-0) |
