# Ladder

10189 scrimmages (ours only), 10189 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1886 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| a3dig5 | 1886 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| arch_rush10 | 1886 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| e1aggr | 1859 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| c5bank | 1834 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.5% (vs 15) |
| a3dig10 | 1834 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.5% (vs 15) |
| c6pair | 1834 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.5% (vs 15) |
| g_iter1 | 1810 +- 15 | 23 of 71 | 4310 | 1566-2744 | 73.1% | 15.0% (vs 15) |
| a2relay | 1808 +- 130 | 24 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| a2reloc | 1808 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| e2aggr | 1808 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| b1z2b | 1805 +- 25 | 27 of 71 | 1560 | 547-1013 | 72.9% | 14.7% (vs 15) |
| b2fs | 1792 +- 30 | 29 of 71 | 1080 | 369-711 | 72.4% | 16.1% (vs 16) |
| b1v2 | 1782 +- 23 | 30 of 71 | 1920 | 643-1277 | 72.1% | 15.4% (vs 16) |
| arch_rush | 1447 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.2% (vs 19) |
| g_iter0 | 1381 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2418 | 96 | 464 | 452-12 | 0% (b2fs 0-54) |
| 2 | Strequals.duck0127v5 | 2404 | 92 | 464 | 451-13 | 2% (b2fs 1-53) |
| 3 | IvanGeffner.kuma | 2392 | 89 | 464 | 450-14 | 2% (b2fs 1-53) |
| 4 | uravt.Version18Final | 2377 | 348 | 26 | 26-0 |  |
| 5 | NotLLeon.v3 | 2260 | 63 | 464 | 434-30 | 6% (b2fs 3-51) |
| 6 | chenyx512.flagbot_final | 2260 | 63 | 464 | 434-30 | 9% (b2fs 5-49) |
| 7 | andli28.v9_USQuals_angle | 2243 | 61 | 464 | 431-33 | 11% (b2fs 6-48) |
| 8 | Gymhgy.v10official | 2222 | 58 | 464 | 427-37 | 7% (b2fs 4-50) |
| 9 | andrewgopher.player22 | 2194 | 54 | 464 | 421-43 | 11% (b2fs 6-48) |
| 10 | hsmalladi.finalbot | 2105 | 44 | 464 | 396-68 | 15% (b2fs 8-46) |
| 11 | CyrilSharma.finalBot | 2088 | 43 | 464 | 390-74 | 15% (b2fs 8-46) |
| 12 | winkelmantanner.waffle | 2059 | 41 | 464 | 379-85 | 17% (b2fs 9-45) |
| 13 | ColtG5.Goob_final | 1908 | 33 | 464 | 302-162 | 30% (b2fs 16-38) |
| 14 | **us:g_iter1_c2** | 1886 | 132 | 110 | 84-26 |  |
| 15 | **us:a3dig5** | 1886 | 132 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1886 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1885 | 33 | 464 | 288-176 | 30% (b2fs 16-38) |
| 18 | **us:e1aggr** | 1859 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1848 | 32 | 464 | 264-200 | 39% (b2fs 21-33) |
| 20 | **us:c5bank** | 1834 | 131 | 110 | 82-28 |  |
| 21 | **us:a3dig10** | 1834 | 131 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1834 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1810 | 15 | 4310 | 1566-2744 |  |
| 24 | **us:a2relay** | 1808 | 130 | 110 | 81-29 |  |
| 25 | **us:a2reloc** | 1808 | 130 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1808 | 130 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1805 | 25 | 1560 | 547-1013 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1801 | 32 | 464 | 233-231 | 41% (b2fs 22-32) |
| 29 | **us:b2fs** | 1792 | 30 | 1080 | 369-711 |  |
| 30 | **us:b1v2** | 1782 | 23 | 1920 | 643-1277 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1619 | 36 | 464 | 122-342 | 83% (b2fs 45-9) |
| 32 | clbarrell.duck8 | 1597 | 37 | 464 | 111-353 | 78% (b2fs 42-12) |
| 33 | jonters.bling3 | 1451 | 49 | 464 | 56-408 | 91% (b2fs 49-5) |
| 34 | **us:arch_rush** | 1447 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1384 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1381 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1333 | 64 | 464 | 30-434 | 98% (b2fs 53-1) |
| 38 | Metta-AI.bc24scenario | 1316 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1316 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1316 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1227 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1227 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1227 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1227 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1227 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1227 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1227 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1227 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1227 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1227 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1227 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1227 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1121 | 358 | 25 | 0-25 |  |
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
| 71 | justinottesen.sprint1 | 1002 | 154 | 464 | 4-460 | 100% (b2fs 54-0) |
