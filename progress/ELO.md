# Ladder

9909 scrimmages (ours only), 9909 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| arch_rush10 | 1886 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| g_iter1_c2 | 1886 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| a3dig5 | 1886 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| e1aggr | 1860 +- 131 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| a3dig10 | 1835 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c5bank | 1835 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c6pair | 1835 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| g_iter1 | 1812 +- 15 | 23 of 71 | 4270 | 1554-2716 | 73.1% | 15.1% (vs 15) |
| a2relay | 1809 +- 130 | 24 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| e2aggr | 1809 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| a2reloc | 1809 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| b1z2b | 1798 +- 26 | 28 of 71 | 1480 | 511-969 | 72.6% | 16.4% (vs 16) |
| b2fs | 1790 +- 32 | 29 of 71 | 1000 | 340-660 | 72.3% | 15.9% (vs 16) |
| b1v2 | 1782 +- 23 | 30 of 71 | 1840 | 615-1225 | 72.0% | 15.4% (vs 16) |
| arch_rush | 1448 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1382 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2412 | 96 | 450 | 438-12 | 5% (g_iter1 10-200) |
| 2 | Strequals.duck0127v5 | 2399 | 92 | 450 | 437-13 | 3% (g_iter1 7-203) |
| 3 | IvanGeffner.kuma | 2387 | 89 | 450 | 436-14 | 4% (g_iter1 8-202) |
| 4 | uravt.Version18Final | 2378 | 348 | 26 | 26-0 |  |
| 5 | NotLLeon.v3 | 2260 | 64 | 450 | 421-29 | 7% (g_iter1 15-195) |
| 6 | chenyx512.flagbot_final | 2254 | 63 | 450 | 420-30 | 6% (g_iter1 13-197) |
| 7 | andli28.v9_USQuals_angle | 2248 | 62 | 450 | 419-31 | 6% (g_iter1 12-198) |
| 8 | Gymhgy.v10official | 2216 | 58 | 450 | 413-37 | 11% (g_iter1 24-186) |
| 9 | andrewgopher.player22 | 2206 | 56 | 450 | 411-39 | 10% (g_iter1 21-189) |
| 10 | hsmalladi.finalbot | 2101 | 45 | 450 | 383-67 | 12% (g_iter1 26-184) |
| 11 | CyrilSharma.finalBot | 2090 | 44 | 450 | 379-71 | 20% (g_iter1 41-169) |
| 12 | winkelmantanner.waffle | 2057 | 41 | 450 | 367-83 | 20% (g_iter1 43-167) |
| 13 | ColtG5.Goob_final | 1911 | 34 | 450 | 295-155 | 39% (g_iter1 81-129) |
| 14 | **us:arch_rush10** | 1886 | 132 | 110 | 84-26 |  |
| 15 | **us:g_iter1_c2** | 1886 | 132 | 110 | 84-26 |  |
| 16 | **us:a3dig5** | 1886 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1886 | 33 | 450 | 280-170 | 41% (g_iter1 86-124) |
| 18 | **us:e1aggr** | 1860 | 131 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1847 | 32 | 450 | 256-194 | 39% (g_iter1 82-128) |
| 20 | **us:a3dig10** | 1835 | 131 | 110 | 82-28 |  |
| 21 | **us:c5bank** | 1835 | 131 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1835 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1812 | 15 | 4270 | 1554-2716 |  |
| 24 | **us:a2relay** | 1809 | 130 | 110 | 81-29 |  |
| 25 | **us:e2aggr** | 1809 | 130 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1809 | 130 | 110 | 81-29 |  |
| 27 | SampleProvider.TSPAARKSPRINT1 | 1804 | 32 | 450 | 228-222 | 50% (g_iter1 106-104) |
| 28 | **us:b1z2b** | 1798 | 26 | 1480 | 511-969 |  |
| 29 | **us:b2fs** | 1790 | 32 | 1000 | 340-660 |  |
| 30 | **us:b1v2** | 1782 | 23 | 1840 | 615-1225 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1620 | 37 | 450 | 119-331 | 70% (g_iter1 146-64) |
| 32 | clbarrell.duck8 | 1597 | 38 | 450 | 108-342 | 78% (g_iter1 164-46) |
| 33 | jonters.bling3 | 1457 | 49 | 450 | 56-394 | 89% (g_iter1 187-23) |
| 34 | **us:arch_rush** | 1448 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1385 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1382 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1338 | 64 | 450 | 30-420 | 97% (g_iter1 204-6) |
| 38 | Metta-AI.bc24scenario | 1317 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1317 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1317 | 225 | 26 | 2-24 |  |
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
| 54 | AlexYu84.smartPlayer | 1090 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1090 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1090 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1090 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1090 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1090 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1090 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1090 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1090 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1090 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1090 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1090 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1090 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1090 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1090 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1090 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1090 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1007 | 154 | 450 | 4-446 | 100% (g_iter1 210-0) |
