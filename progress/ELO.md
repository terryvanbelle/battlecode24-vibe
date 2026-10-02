# Ladder

10789 scrimmages (ours only), 10789 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1883 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| a3dig5 | 1883 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| arch_rush10 | 1883 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| e1aggr | 1857 +- 131 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| a3dig10 | 1831 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c5bank | 1831 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c6pair | 1831 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| g_iter1 | 1807 +- 15 | 23 of 71 | 4470 | 1621-2849 | 73.0% | 15.0% (vs 15) |
| e2aggr | 1806 +- 130 | 24 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1806 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2reloc | 1806 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1z2b | 1804 +- 24 | 27 of 71 | 1720 | 604-1116 | 72.9% | 14.8% (vs 15) |
| b2fs | 1787 +- 29 | 29 of 71 | 1200 | 408-792 | 72.3% | 16.0% (vs 16) |
| b1v2 | 1779 +- 22 | 30 of 71 | 2080 | 696-1384 | 72.0% | 15.5% (vs 16) |
| arch_rush | 1444 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1379 +- 90 | 36 of 71 | 109 | 56-53 | 52.0% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2426 | 96 | 494 | 482-12 | 1% (b1z2b 1-85) |
| 2 | IvanGeffner.kuma | 2400 | 89 | 494 | 480-14 | 2% (b1z2b 2-84) |
| 3 | Strequals.duck0127v5 | 2400 | 89 | 494 | 480-14 | 6% (b1z2b 5-81) |
| 4 | uravt.Version18Final | 2375 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2268 | 63 | 494 | 464-30 | 6% (b1z2b 5-81) |
| 6 | NotLLeon.v3 | 2262 | 62 | 494 | 463-31 | 7% (b1z2b 6-80) |
| 7 | andli28.v9_USQuals_angle | 2240 | 59 | 494 | 459-35 | 13% (b1z2b 11-75) |
| 8 | Gymhgy.v10official | 2211 | 55 | 494 | 453-41 | 7% (b1z2b 6-80) |
| 9 | andrewgopher.player22 | 2182 | 51 | 494 | 446-48 | 10% (b1z2b 9-77) |
| 10 | hsmalladi.finalbot | 2103 | 43 | 494 | 422-72 | 13% (b1z2b 11-75) |
| 11 | CyrilSharma.finalBot | 2087 | 42 | 494 | 416-78 | 13% (b1z2b 11-75) |
| 12 | winkelmantanner.waffle | 2052 | 39 | 494 | 402-92 | 23% (b1z2b 20-66) |
| 13 | ColtG5.Goob_final | 1909 | 32 | 494 | 324-170 | 34% (b1z2b 29-57) |
| 14 | **us:g_iter1_c2** | 1883 | 132 | 110 | 84-26 |  |
| 15 | **us:a3dig5** | 1883 | 132 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1883 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1881 | 32 | 494 | 306-188 | 42% (b1z2b 36-50) |
| 18 | **us:e1aggr** | 1857 | 131 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1839 | 31 | 494 | 277-217 | 51% (b1z2b 44-42) |
| 20 | **us:a3dig10** | 1831 | 131 | 110 | 82-28 |  |
| 21 | **us:c5bank** | 1831 | 131 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1831 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1807 | 15 | 4470 | 1621-2849 |  |
| 24 | **us:e2aggr** | 1806 | 130 | 110 | 81-29 |  |
| 25 | **us:a2relay** | 1806 | 130 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1806 | 130 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1804 | 24 | 1720 | 604-1116 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1796 | 31 | 494 | 247-247 | 58% (b1z2b 50-36) |
| 29 | **us:b2fs** | 1787 | 29 | 1200 | 408-792 |  |
| 30 | **us:b1v2** | 1779 | 22 | 2080 | 696-1384 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1623 | 35 | 494 | 134-360 | 79% (b1z2b 68-18) |
| 32 | clbarrell.duck8 | 1603 | 36 | 494 | 123-371 | 67% (b1z2b 58-28) |
| 33 | jonters.bling3 | 1446 | 47 | 494 | 59-435 | 81% (b1z2b 70-16) |
| 34 | **us:arch_rush** | 1444 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1382 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1379 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1318 | 64 | 494 | 30-464 | 88% (b1z2b 76-10) |
| 38 | Metta-AI.bc24scenario | 1314 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1314 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1314 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1225 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1225 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1225 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1225 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1225 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1225 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1225 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1225 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1225 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1225 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1225 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1225 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1119 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1087 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1087 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1087 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1087 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1087 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1087 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1087 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1087 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1087 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1087 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1087 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1087 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1087 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1087 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1087 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1087 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1087 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 988 | 153 | 494 | 4-490 | 100% (b1z2b 86-0) |
