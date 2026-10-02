# Ladder

5829 scrimmages (ours only), 5829 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1904 +- 133 | 14 of 70 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| g_iter1_c2 | 1904 +- 133 | 15 of 70 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| arch_rush10 | 1904 +- 133 | 16 of 70 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| e1aggr | 1878 +- 132 | 19 of 70 | 110 | 83-27 | 74.7% | 18.3% (vs 15) |
| c6pair | 1852 +- 131 | 20 of 70 | 110 | 82-28 | 73.8% | 16.5% (vs 15) |
| a3dig10 | 1852 +- 131 | 21 of 70 | 110 | 82-28 | 73.8% | 16.5% (vs 15) |
| c5bank | 1852 +- 131 | 22 of 70 | 110 | 82-28 | 73.8% | 16.5% (vs 15) |
| b1z2b | 1841 +- 45 | 23 of 70 | 480 | 174-306 | 73.5% | 15.8% (vs 15) |
| g_iter1 | 1829 +- 18 | 24 of 70 | 3190 | 1166-2024 | 73.0% | 15.0% (vs 15) |
| e2aggr | 1827 +- 130 | 25 of 70 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| a2reloc | 1827 +- 130 | 26 of 70 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| a2relay | 1827 +- 130 | 27 of 70 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| b1v2 | 1801 +- 34 | 29 of 70 | 840 | 281-559 | 72.1% | 15.5% (vs 16) |
| arch_rush | 1463 +- 95 | 32 of 70 | 110 | 62-48 | 56.5% | 5.7% (vs 18) |
| g_iter0 | 1397 +- 89 | 35 of 70 | 109 | 56-53 | 51.9% | 8.2% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2500 | 154 | 246 | 242-4 | 0% (b1v2 0-42) |
| 2 | jmerle.camel_case_v21_final | 2416 | 122 | 246 | 239-7 | 0% (b1v2 0-42) |
| 3 | uravt.Version18Final | 2396 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2343 | 101 | 246 | 235-11 | 5% (b1v2 2-40) |
| 5 | andli28.v9_USQuals_angle | 2328 | 97 | 246 | 234-12 | 5% (b1v2 2-40) |
| 6 | chenyx512.flagbot_final | 2328 | 97 | 246 | 234-12 | 5% (b1v2 2-40) |
| 7 | NotLLeon.v3 | 2290 | 88 | 246 | 231-15 | 5% (b1v2 2-40) |
| 8 | andrewgopher.player22 | 2238 | 78 | 246 | 226-20 | 7% (b1v2 3-39) |
| 9 | Gymhgy.v10official | 2213 | 73 | 246 | 223-23 | 5% (b1v2 2-40) |
| 10 | hsmalladi.finalbot | 2116 | 60 | 246 | 208-38 | 14% (b1v2 6-36) |
| 11 | CyrilSharma.finalBot | 2091 | 57 | 246 | 203-43 | 14% (b1v2 6-36) |
| 12 | winkelmantanner.waffle | 2059 | 54 | 246 | 196-50 | 17% (b1v2 7-35) |
| 13 | ColtG5.Goob_final | 1924 | 45 | 246 | 158-88 | 29% (b1v2 12-30) |
| 14 | **us:a3dig5** | 1904 | 133 | 110 | 84-26 |  |
| 15 | **us:g_iter1_c2** | 1904 | 133 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1904 | 133 | 110 | 84-26 |  |
| 17 | quesswho.cretplayer2_3 | 1894 | 44 | 246 | 148-98 | 43% (b1v2 18-24) |
| 18 | kyleezz.jeeryfix3 | 1879 | 44 | 246 | 143-103 | 40% (b1v2 17-25) |
| 19 | **us:e1aggr** | 1878 | 132 | 110 | 83-27 |  |
| 20 | **us:c6pair** | 1852 | 131 | 110 | 82-28 |  |
| 21 | **us:a3dig10** | 1852 | 131 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1852 | 131 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1841 | 45 | 480 | 174-306 |  |
| 24 | **us:g_iter1** | 1829 | 18 | 3190 | 1166-2024 |  |
| 25 | **us:e2aggr** | 1827 | 130 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1827 | 130 | 110 | 81-29 |  |
| 27 | **us:a2relay** | 1827 | 130 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1819 | 44 | 246 | 122-124 | 50% (b1v2 21-21) |
| 29 | **us:b1v2** | 1801 | 34 | 840 | 281-559 |  |
| 30 | dmtrung14.defaultplayer_intlqualifier | 1669 | 48 | 246 | 73-173 | 71% (b1v2 30-12) |
| 31 | clbarrell.duck8 | 1610 | 52 | 246 | 57-189 | 79% (b1v2 33-9) |
| 32 | **us:arch_rush** | 1463 | 95 | 110 | 62-48 |  |
| 33 | jonters.bling3 | 1459 | 69 | 246 | 28-218 | 90% (b1v2 38-4) |
| 34 | HugoIngelsson.Bot21 | 1398 | 203 | 26 | 3-23 |  |
| 35 | **us:g_iter0** | 1397 | 89 | 109 | 56-53 |  |
| 36 | awu7.ExplosiveBot | 1342 | 90 | 246 | 15-231 | 90% (b1v2 38-4) |
| 37 | Metta-AI.bc24scenario | 1330 | 226 | 26 | 2-24 |  |
| 38 | noahzemlin.honeyducklings | 1330 | 226 | 26 | 2-24 |  |
| 39 | sivakovivan.NewHide | 1330 | 226 | 26 | 2-24 |  |
| 40 | JeffLegendPower.v11 | 1241 | 265 | 26 | 1-25 |  |
| 41 | MiloAkerman.v1 | 1241 | 265 | 26 | 1-25 |  |
| 42 | PerishoJ.tx | 1241 | 265 | 26 | 1-25 |  |
| 43 | Peter-Fun.dinoboxer | 1241 | 265 | 26 | 1-25 |  |
| 44 | RyanAspen.v22 | 1241 | 265 | 26 | 1-25 |  |
| 45 | TylerJulian.v9 | 1241 | 265 | 26 | 1-25 |  |
| 46 | aj-chau.cowards | 1241 | 265 | 26 | 1-25 |  |
| 47 | cViper971.ourplayer | 1241 | 265 | 26 | 1-25 |  |
| 48 | dylanzemlin.dangerduck2 | 1241 | 265 | 26 | 1-25 |  |
| 49 | lukerhoads.warrior_2nd_comp | 1241 | 265 | 26 | 1-25 |  |
| 50 | neilhuang007.baseline | 1241 | 265 | 26 | 1-25 |  |
| 51 | polyllc.polyv4 | 1241 | 265 | 26 | 1-25 |  |
| 52 | andrearante12.turtle | 1135 | 358 | 25 | 0-25 |  |
| 53 | justinottesen.sprint1 | 1126 | 155 | 246 | 4-242 | 100% (b1v2 42-0) |
| 54 | AlexYu84.smartPlayer | 1103 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1103 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1103 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1103 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1103 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1103 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1103 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1103 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1103 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1103 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1103 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1103 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1103 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1103 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1103 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1103 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1103 | 358 | 26 | 0-26 |  |
