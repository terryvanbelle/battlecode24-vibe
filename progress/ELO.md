# Ladder

8989 scrimmages (ours only), 8989 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1889 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| g_iter1_c2 | 1889 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| arch_rush10 | 1889 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| e1aggr | 1862 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| a3dig10 | 1837 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c6pair | 1837 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c5bank | 1837 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| g_iter1 | 1814 +- 16 | 23 of 71 | 3990 | 1454-2536 | 73.1% | 15.2% (vs 15) |
| e2aggr | 1811 +- 130 | 24 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1811 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2reloc | 1811 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1z2b | 1806 +- 28 | 27 of 71 | 1280 | 447-833 | 72.8% | 14.7% (vs 15) |
| b1v2 | 1784 +- 25 | 29 of 71 | 1640 | 548-1092 | 72.0% | 15.5% (vs 16) |
| b2fs | 1779 +- 36 | 30 of 71 | 760 | 252-508 | 71.9% | 15.2% (vs 16) |
| arch_rush | 1451 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1386 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2410 | 100 | 404 | 393-11 | 5% (b1z2b 3-61) |
| 2 | jmerle.camel_case_v21_final | 2395 | 96 | 404 | 392-12 | 2% (b1z2b 1-63) |
| 3 | uravt.Version18Final | 2381 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2370 | 90 | 404 | 390-14 | 3% (b1z2b 2-62) |
| 5 | NotLLeon.v3 | 2269 | 69 | 404 | 379-25 | 6% (b1z2b 4-60) |
| 6 | andli28.v9_USQuals_angle | 2262 | 68 | 404 | 378-26 | 11% (b1z2b 7-57) |
| 7 | chenyx512.flagbot_final | 2242 | 65 | 404 | 375-29 | 8% (b1z2b 5-59) |
| 8 | andrewgopher.player22 | 2230 | 63 | 404 | 373-31 | 8% (b1z2b 5-59) |
| 9 | Gymhgy.v10official | 2224 | 62 | 404 | 372-32 | 6% (b1z2b 4-60) |
| 10 | hsmalladi.finalbot | 2111 | 48 | 404 | 346-58 | 9% (b1z2b 6-58) |
| 11 | CyrilSharma.finalBot | 2094 | 46 | 404 | 341-63 | 12% (b1z2b 8-56) |
| 12 | winkelmantanner.waffle | 2072 | 45 | 404 | 334-70 | 19% (b1z2b 12-52) |
| 13 | ColtG5.Goob_final | 1902 | 35 | 404 | 259-145 | 36% (b1z2b 23-41) |
| 14 | **us:a3dig5** | 1889 | 132 | 110 | 84-26 |  |
| 15 | **us:g_iter1_c2** | 1889 | 132 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1889 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1876 | 35 | 404 | 245-159 | 44% (b1z2b 28-36) |
| 18 | **us:e1aggr** | 1862 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1844 | 34 | 404 | 227-177 | 56% (b1z2b 36-28) |
| 20 | **us:a3dig10** | 1837 | 131 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1837 | 131 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1837 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1814 | 16 | 3990 | 1454-2536 |  |
| 24 | **us:e2aggr** | 1811 | 130 | 110 | 81-29 |  |
| 25 | **us:a2relay** | 1811 | 130 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1811 | 130 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1806 | 28 | 1280 | 447-833 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1801 | 34 | 404 | 202-202 | 58% (b1z2b 37-27) |
| 29 | **us:b1v2** | 1784 | 25 | 1640 | 548-1092 |  |
| 30 | **us:b2fs** | 1779 | 36 | 760 | 252-508 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1631 | 38 | 404 | 111-293 | 84% (b1z2b 54-10) |
| 32 | clbarrell.duck8 | 1591 | 40 | 404 | 94-310 | 70% (b1z2b 45-19) |
| 33 | jonters.bling3 | 1477 | 50 | 404 | 55-349 | 77% (b1z2b 49-15) |
| 34 | **us:arch_rush** | 1451 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1387 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1386 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1353 | 65 | 404 | 29-375 | 84% (b1z2b 54-10) |
| 38 | Metta-AI.bc24scenario | 1319 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1319 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1319 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1230 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1230 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1230 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1230 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1230 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1230 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1230 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1230 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1230 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1230 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1230 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1230 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1124 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1092 | 357 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1092 | 357 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1092 | 357 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1092 | 357 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1092 | 357 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1092 | 357 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1092 | 357 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1092 | 357 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1092 | 357 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1092 | 357 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1092 | 357 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1092 | 357 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1092 | 357 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1092 | 357 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1092 | 357 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1092 | 357 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1092 | 357 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1026 | 154 | 404 | 4-400 | 100% (b1z2b 64-0) |
