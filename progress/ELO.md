# Ladder

10589 scrimmages (ours only), 10589 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1884 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| g_iter1_c2 | 1884 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| arch_rush10 | 1884 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| e1aggr | 1858 +- 131 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| a3dig10 | 1832 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c6pair | 1832 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c5bank | 1832 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| g_iter1 | 1808 +- 15 | 23 of 71 | 4430 | 1607-2823 | 73.0% | 15.1% (vs 15) |
| e2aggr | 1807 +- 130 | 24 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1807 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2reloc | 1807 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1z2b | 1803 +- 24 | 27 of 71 | 1640 | 574-1066 | 72.8% | 14.7% (vs 15) |
| b2fs | 1786 +- 29 | 29 of 71 | 1160 | 393-767 | 72.3% | 15.9% (vs 16) |
| b1v2 | 1780 +- 22 | 30 of 71 | 2040 | 683-1357 | 72.1% | 15.5% (vs 16) |
| arch_rush | 1445 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1380 +- 90 | 36 of 71 | 109 | 56-53 | 52.0% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2422 | 96 | 484 | 472-12 | 0% (b1v2 0-102) |
| 2 | IvanGeffner.kuma | 2397 | 89 | 484 | 470-14 | 2% (b1v2 2-100) |
| 3 | Strequals.duck0127v5 | 2397 | 89 | 484 | 470-14 | 1% (b1v2 1-101) |
| 4 | uravt.Version18Final | 2376 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2265 | 63 | 484 | 454-30 | 6% (b1v2 6-96) |
| 6 | NotLLeon.v3 | 2259 | 62 | 484 | 453-31 | 6% (b1v2 6-96) |
| 7 | andli28.v9_USQuals_angle | 2237 | 59 | 484 | 449-35 | 5% (b1v2 5-97) |
| 8 | Gymhgy.v10official | 2222 | 57 | 484 | 446-38 | 5% (b1v2 5-97) |
| 9 | andrewgopher.player22 | 2186 | 52 | 484 | 438-46 | 8% (b1v2 8-94) |
| 10 | hsmalladi.finalbot | 2102 | 44 | 484 | 413-71 | 15% (b1v2 15-87) |
| 11 | CyrilSharma.finalBot | 2083 | 42 | 484 | 406-78 | 14% (b1v2 14-88) |
| 12 | winkelmantanner.waffle | 2055 | 40 | 484 | 395-89 | 15% (b1v2 15-87) |
| 13 | ColtG5.Goob_final | 1911 | 33 | 484 | 319-165 | 30% (b1v2 31-71) |
| 14 | **us:a3dig5** | 1884 | 132 | 110 | 84-26 |  |
| 15 | **us:g_iter1_c2** | 1884 | 132 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1884 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1880 | 32 | 484 | 299-185 | 34% (b1v2 35-67) |
| 18 | **us:e1aggr** | 1858 | 131 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1838 | 31 | 484 | 271-213 | 48% (b1v2 49-53) |
| 20 | **us:a3dig10** | 1832 | 131 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1832 | 131 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1832 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1808 | 15 | 4430 | 1607-2823 |  |
| 24 | **us:e2aggr** | 1807 | 130 | 110 | 81-29 |  |
| 25 | **us:a2relay** | 1807 | 130 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1807 | 130 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1803 | 24 | 1640 | 574-1066 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1798 | 31 | 484 | 243-241 | 51% (b1v2 52-50) |
| 29 | **us:b2fs** | 1786 | 29 | 1160 | 393-767 |  |
| 30 | **us:b1v2** | 1780 | 22 | 2040 | 683-1357 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1625 | 35 | 484 | 132-352 | 73% (b1v2 74-28) |
| 32 | clbarrell.duck8 | 1602 | 36 | 484 | 120-364 | 75% (b1v2 77-25) |
| 33 | jonters.bling3 | 1451 | 47 | 484 | 59-425 | 90% (b1v2 92-10) |
| 34 | **us:arch_rush** | 1445 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1382 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1380 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1322 | 64 | 484 | 30-454 | 92% (b1v2 94-8) |
| 38 | Metta-AI.bc24scenario | 1314 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1314 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1314 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1226 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1226 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1226 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1226 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1226 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1226 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1226 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1226 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1226 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1226 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1226 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1226 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1120 | 358 | 25 | 0-25 |  |
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
| 71 | justinottesen.sprint1 | 992 | 153 | 484 | 4-480 | 100% (b1v2 102-0) |
