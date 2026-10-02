# Ladder

6309 scrimmages (ours only), 6309 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1897 +- 133 | 14 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| a3dig5 | 1897 +- 133 | 15 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| arch_rush10 | 1897 +- 133 | 16 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| e1aggr | 1871 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 16.1% (vs 14) |
| a3dig10 | 1845 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c5bank | 1845 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c6pair | 1845 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| b1z2b | 1840 +- 40 | 23 of 71 | 600 | 220-380 | 73.7% | 16.3% (vs 15) |
| g_iter1 | 1823 +- 17 | 24 of 71 | 3310 | 1213-2097 | 73.1% | 15.2% (vs 15) |
| a2reloc | 1819 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1819 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| e2aggr | 1819 +- 130 | 27 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1v2 | 1791 +- 32 | 29 of 71 | 960 | 320-640 | 72.0% | 15.3% (vs 16) |
| b2fs | 1719 +- 95 | 30 of 71 | 120 | 35-85 | 69.4% | 11.3% (vs 16) |
| arch_rush | 1456 +- 95 | 33 of 71 | 110 | 62-48 | 56.5% | 5.8% (vs 18) |
| g_iter0 | 1391 +- 89 | 36 of 71 | 109 | 56-53 | 51.9% | 8.4% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2508 | 154 | 270 | 266-4 | 0% (b1z2b 0-30) |
| 2 | jmerle.camel_case_v21_final | 2424 | 122 | 270 | 263-7 | 0% (b1z2b 0-30) |
| 3 | uravt.Version18Final | 2389 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2337 | 97 | 270 | 258-12 | 3% (b1z2b 1-29) |
| 5 | chenyx512.flagbot_final | 2337 | 97 | 270 | 258-12 | 3% (b1z2b 1-29) |
| 6 | NotLLeon.v3 | 2287 | 85 | 270 | 254-16 | 10% (b1z2b 3-27) |
| 7 | andli28.v9_USQuals_angle | 2287 | 85 | 270 | 254-16 | 13% (b1z2b 4-26) |
| 8 | Gymhgy.v10official | 2215 | 72 | 270 | 246-24 | 3% (b1z2b 1-29) |
| 9 | andrewgopher.player22 | 2200 | 69 | 270 | 244-26 | 17% (b1z2b 5-25) |
| 10 | hsmalladi.finalbot | 2117 | 58 | 270 | 230-40 | 13% (b1z2b 4-26) |
| 11 | CyrilSharma.finalBot | 2097 | 56 | 270 | 226-44 | 7% (b1z2b 2-28) |
| 12 | winkelmantanner.waffle | 2054 | 52 | 270 | 216-54 | 27% (b1z2b 8-22) |
| 13 | ColtG5.Goob_final | 1920 | 43 | 270 | 175-95 | 37% (b1z2b 11-19) |
| 14 | **us:g_iter1_c2** | 1897 | 133 | 110 | 84-26 |  |
| 15 | **us:a3dig5** | 1897 | 133 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1897 | 133 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1873 | 42 | 270 | 158-112 | 53% (b1z2b 16-14) |
| 18 | **us:e1aggr** | 1871 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1865 | 42 | 270 | 155-115 | 60% (b1z2b 18-12) |
| 20 | **us:a3dig10** | 1845 | 131 | 110 | 82-28 |  |
| 21 | **us:c5bank** | 1845 | 131 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1845 | 131 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1840 | 40 | 600 | 220-380 |  |
| 24 | **us:g_iter1** | 1823 | 17 | 3310 | 1213-2097 |  |
| 25 | **us:a2reloc** | 1819 | 130 | 110 | 81-29 |  |
| 26 | **us:a2relay** | 1819 | 130 | 110 | 81-29 |  |
| 27 | **us:e2aggr** | 1819 | 130 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1818 | 42 | 270 | 137-133 | 57% (b1z2b 17-13) |
| 29 | **us:b1v2** | 1791 | 32 | 960 | 320-640 |  |
| 30 | **us:b2fs** | 1719 | 95 | 120 | 35-85 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1664 | 46 | 270 | 81-189 | 83% (b1z2b 25-5) |
| 32 | clbarrell.duck8 | 1592 | 50 | 270 | 60-210 | 70% (b1z2b 21-9) |
| 33 | **us:arch_rush** | 1456 | 95 | 110 | 62-48 |  |
| 34 | jonters.bling3 | 1446 | 66 | 270 | 30-240 | 83% (b1z2b 25-5) |
| 35 | HugoIngelsson.Bot21 | 1393 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1391 | 89 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1339 | 84 | 270 | 17-253 | 93% (b1z2b 28-2) |
| 38 | Metta-AI.bc24scenario | 1324 | 226 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1324 | 226 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1324 | 226 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1236 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1236 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1236 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1236 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1236 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1236 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1236 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1236 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1236 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1236 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1236 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1236 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1129 | 358 | 25 | 0-25 |  |
| 54 | justinottesen.sprint1 | 1102 | 155 | 270 | 4-266 | 100% (b1z2b 30-0) |
| 55 | AlexYu84.smartPlayer | 1097 | 358 | 26 | 0-26 |  |
| 56 | H4ffliger.keyboardcrusader_v1 | 1097 | 358 | 26 | 0-26 |  |
| 57 | Lithanium.AttackingBot | 1097 | 358 | 26 | 0-26 |  |
| 58 | Rubrasum.version_3 | 1097 | 358 | 26 | 0-26 |  |
| 59 | SriLakshmiPolavarapu.ducks | 1097 | 358 | 26 | 0-26 |  |
| 60 | VarunVejalla.alexander | 1097 | 358 | 26 | 0-26 |  |
| 61 | abdullah8a0.crayBasic | 1097 | 358 | 26 | 0-26 |  |
| 62 | adamseth2.moveBot1 | 1097 | 358 | 26 | 0-26 |  |
| 63 | dylanconklin.Team3 | 1097 | 358 | 26 | 0-26 |  |
| 64 | itswin.MPAttack | 1097 | 358 | 26 | 0-26 |  |
| 65 | joelcrouch.ducks | 1097 | 358 | 26 | 0-26 |  |
| 66 | lcforges.funkyguy3 | 1097 | 358 | 26 | 0-26 |  |
| 67 | qpwoeirut.tournament_sprint1 | 1097 | 358 | 26 | 0-26 |  |
| 68 | reeceyang.v5 | 1097 | 358 | 26 | 0-26 |  |
| 69 | samithShetty.combustiblelemon | 1097 | 358 | 26 | 0-26 |  |
| 70 | sayam-goyal.SimpleBot | 1097 | 358 | 26 | 0-26 |  |
| 71 | tlevietpdx.Sprint2 | 1097 | 358 | 26 | 0-26 |  |
