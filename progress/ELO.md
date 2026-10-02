# Ladder

10029 scrimmages (ours only), 10029 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1886 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| a3dig5 | 1886 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| arch_rush10 | 1886 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| e1aggr | 1860 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| c5bank | 1834 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.5% (vs 15) |
| a3dig10 | 1834 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.5% (vs 15) |
| c6pair | 1834 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.5% (vs 15) |
| g_iter1 | 1811 +- 15 | 23 of 71 | 4270 | 1554-2716 | 73.1% | 15.1% (vs 15) |
| e2aggr | 1809 +- 130 | 24 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| a2relay | 1809 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| a2reloc | 1809 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| b1z2b | 1804 +- 25 | 27 of 71 | 1520 | 531-989 | 72.8% | 14.6% (vs 15) |
| b2fs | 1789 +- 31 | 29 of 71 | 1040 | 353-687 | 72.3% | 15.8% (vs 16) |
| b1v2 | 1783 +- 23 | 30 of 71 | 1880 | 630-1250 | 72.1% | 15.5% (vs 16) |
| arch_rush | 1447 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.2% (vs 19) |
| g_iter0 | 1382 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2415 | 96 | 456 | 444-12 | 0% (b2fs 0-52) |
| 2 | Strequals.duck0127v5 | 2402 | 92 | 456 | 443-13 | 2% (b2fs 1-51) |
| 3 | IvanGeffner.kuma | 2389 | 89 | 456 | 442-14 | 2% (b2fs 1-51) |
| 4 | uravt.Version18Final | 2378 | 348 | 26 | 26-0 |  |
| 5 | NotLLeon.v3 | 2263 | 64 | 456 | 427-29 | 6% (b2fs 3-49) |
| 6 | chenyx512.flagbot_final | 2257 | 63 | 456 | 426-30 | 10% (b2fs 5-47) |
| 7 | andli28.v9_USQuals_angle | 2251 | 62 | 456 | 425-31 | 10% (b2fs 5-47) |
| 8 | Gymhgy.v10official | 2219 | 58 | 456 | 419-37 | 8% (b2fs 4-48) |
| 9 | andrewgopher.player22 | 2209 | 56 | 456 | 417-39 | 10% (b2fs 5-47) |
| 10 | hsmalladi.finalbot | 2102 | 45 | 456 | 388-68 | 15% (b2fs 8-44) |
| 11 | CyrilSharma.finalBot | 2084 | 43 | 456 | 382-74 | 15% (b2fs 8-44) |
| 12 | winkelmantanner.waffle | 2058 | 41 | 456 | 372-84 | 17% (b2fs 9-43) |
| 13 | ColtG5.Goob_final | 1908 | 33 | 456 | 297-159 | 29% (b2fs 15-37) |
| 14 | **us:g_iter1_c2** | 1886 | 132 | 110 | 84-26 |  |
| 15 | **us:a3dig5** | 1886 | 132 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1886 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1884 | 33 | 456 | 282-174 | 29% (b2fs 15-37) |
| 18 | **us:e1aggr** | 1860 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1850 | 32 | 456 | 261-195 | 38% (b2fs 20-32) |
| 20 | **us:c5bank** | 1834 | 131 | 110 | 82-28 |  |
| 21 | **us:a3dig10** | 1834 | 131 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1834 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1811 | 15 | 4270 | 1554-2716 |  |
| 24 | **us:e2aggr** | 1809 | 130 | 110 | 81-29 |  |
| 25 | **us:a2relay** | 1809 | 130 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1809 | 130 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1804 | 25 | 1520 | 531-989 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1801 | 32 | 456 | 229-227 | 38% (b2fs 20-32) |
| 29 | **us:b2fs** | 1789 | 31 | 1040 | 353-687 |  |
| 30 | **us:b1v2** | 1783 | 23 | 1880 | 630-1250 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1617 | 36 | 456 | 119-337 | 85% (b2fs 44-8) |
| 32 | clbarrell.duck8 | 1597 | 38 | 456 | 109-347 | 77% (b2fs 40-12) |
| 33 | jonters.bling3 | 1455 | 49 | 456 | 56-400 | 90% (b2fs 47-5) |
| 34 | **us:arch_rush** | 1447 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1384 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1382 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1336 | 64 | 456 | 30-426 | 98% (b2fs 51-1) |
| 38 | Metta-AI.bc24scenario | 1316 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1316 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1316 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1228 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1228 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1228 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1228 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1228 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1228 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1228 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1228 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1228 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1228 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1228 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1228 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1122 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1089 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1089 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1089 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1089 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1089 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1089 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1089 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1089 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1089 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1089 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1089 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1089 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1089 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1089 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1089 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1089 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1089 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1005 | 154 | 456 | 4-452 | 100% (b2fs 52-0) |
