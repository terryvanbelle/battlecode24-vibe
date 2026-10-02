# Ladder

9589 scrimmages (ours only), 9589 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| arch_rush10 | 1888 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| a3dig5 | 1888 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| g_iter1_c2 | 1888 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| e1aggr | 1862 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 15.9% (vs 14) |
| a3dig10 | 1836 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c5bank | 1836 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c6pair | 1836 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| g_iter1 | 1813 +- 15 | 23 of 71 | 4150 | 1510-2640 | 73.1% | 15.1% (vs 15) |
| a2reloc | 1811 +- 130 | 24 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| a2relay | 1811 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| e2aggr | 1811 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| b1z2b | 1799 +- 27 | 28 of 71 | 1400 | 483-917 | 72.6% | 16.4% (vs 16) |
| b2fs | 1784 +- 33 | 29 of 71 | 920 | 308-612 | 72.0% | 15.4% (vs 16) |
| b1v2 | 1780 +- 24 | 30 of 71 | 1800 | 597-1203 | 71.9% | 15.1% (vs 16) |
| arch_rush | 1449 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1384 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2406 | 96 | 434 | 422-12 | 0% (b1v2 0-90) |
| 2 | jmerle.camel_case_v21_final | 2406 | 96 | 434 | 422-12 | 0% (b1v2 0-90) |
| 3 | uravt.Version18Final | 2380 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2380 | 89 | 434 | 420-14 | 2% (b1v2 2-88) |
| 5 | NotLLeon.v3 | 2260 | 65 | 434 | 406-28 | 7% (b1v2 6-84) |
| 6 | andli28.v9_USQuals_angle | 2260 | 65 | 434 | 406-28 | 4% (b1v2 4-86) |
| 7 | chenyx512.flagbot_final | 2253 | 64 | 434 | 405-29 | 7% (b1v2 6-84) |
| 8 | andrewgopher.player22 | 2214 | 59 | 434 | 398-36 | 7% (b1v2 6-84) |
| 9 | Gymhgy.v10official | 2209 | 58 | 434 | 397-37 | 4% (b1v2 4-86) |
| 10 | hsmalladi.finalbot | 2113 | 47 | 434 | 373-61 | 14% (b1v2 13-77) |
| 11 | CyrilSharma.finalBot | 2097 | 45 | 434 | 368-66 | 11% (b1v2 10-80) |
| 12 | winkelmantanner.waffle | 2068 | 43 | 434 | 358-76 | 14% (b1v2 13-77) |
| 13 | ColtG5.Goob_final | 1910 | 34 | 434 | 284-150 | 31% (b1v2 28-62) |
| 14 | **us:arch_rush10** | 1888 | 132 | 110 | 84-26 |  |
| 15 | **us:a3dig5** | 1888 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1888 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1882 | 34 | 434 | 268-166 | 34% (b1v2 31-59) |
| 18 | **us:e1aggr** | 1862 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1845 | 33 | 434 | 246-188 | 47% (b1v2 42-48) |
| 20 | **us:a3dig10** | 1836 | 131 | 110 | 82-28 |  |
| 21 | **us:c5bank** | 1836 | 131 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1836 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1813 | 15 | 4150 | 1510-2640 |  |
| 24 | **us:a2reloc** | 1811 | 130 | 110 | 81-29 |  |
| 25 | **us:a2relay** | 1811 | 130 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1811 | 130 | 110 | 81-29 |  |
| 27 | SampleProvider.TSPAARKSPRINT1 | 1805 | 33 | 434 | 221-213 | 50% (b1v2 45-45) |
| 28 | **us:b1z2b** | 1799 | 27 | 1400 | 483-917 |  |
| 29 | **us:b2fs** | 1784 | 33 | 920 | 308-612 |  |
| 30 | **us:b1v2** | 1780 | 24 | 1800 | 597-1203 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1622 | 37 | 434 | 116-318 | 71% (b1v2 64-26) |
| 32 | clbarrell.duck8 | 1594 | 39 | 434 | 103-331 | 78% (b1v2 70-20) |
| 33 | jonters.bling3 | 1464 | 49 | 434 | 56-378 | 90% (b1v2 81-9) |
| 34 | **us:arch_rush** | 1449 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1386 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1384 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1338 | 65 | 434 | 29-405 | 91% (b1v2 82-8) |
| 38 | Metta-AI.bc24scenario | 1318 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1318 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1318 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1229 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1229 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1229 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1229 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1229 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1229 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1229 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1229 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1229 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1229 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1229 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1229 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1123 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1091 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1091 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1091 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1091 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1091 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1091 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1091 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1091 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1091 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1091 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1091 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1091 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1091 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1091 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1091 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1091 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1091 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1012 | 154 | 434 | 4-430 | 100% (b1v2 90-0) |
