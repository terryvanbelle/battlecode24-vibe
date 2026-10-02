# Ladder

8509 scrimmages (ours only), 8509 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1890 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| arch_rush10 | 1890 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| a3dig5 | 1890 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.2% (vs 13) |
| e1aggr | 1864 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| c6pair | 1838 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| a3dig10 | 1838 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c5bank | 1838 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| b1z2b | 1816 +- 29 | 23 of 71 | 1160 | 412-748 | 73.1% | 15.2% (vs 15) |
| g_iter1 | 1816 +- 16 | 24 of 71 | 3870 | 1414-2456 | 73.1% | 15.2% (vs 15) |
| e2aggr | 1813 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2reloc | 1813 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1813 +- 130 | 27 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1v2 | 1786 +- 26 | 29 of 71 | 1520 | 508-1012 | 72.0% | 15.5% (vs 16) |
| b2fs | 1778 +- 40 | 30 of 71 | 640 | 211-429 | 71.8% | 15.1% (vs 16) |
| arch_rush | 1452 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1387 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2402 | 100 | 380 | 369-11 | 5% (b1z2b 3-55) |
| 2 | jmerle.camel_case_v21_final | 2388 | 96 | 380 | 368-12 | 2% (b1z2b 1-57) |
| 3 | uravt.Version18Final | 2382 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2375 | 93 | 380 | 367-13 | 3% (b1z2b 2-56) |
| 5 | NotLLeon.v3 | 2276 | 72 | 380 | 357-23 | 5% (b1z2b 3-55) |
| 6 | andli28.v9_USQuals_angle | 2261 | 69 | 380 | 355-25 | 12% (b1z2b 7-51) |
| 7 | chenyx512.flagbot_final | 2254 | 68 | 380 | 354-26 | 9% (b1z2b 5-53) |
| 8 | andrewgopher.player22 | 2228 | 64 | 380 | 350-30 | 9% (b1z2b 5-53) |
| 9 | Gymhgy.v10official | 2216 | 62 | 380 | 348-32 | 7% (b1z2b 4-54) |
| 10 | hsmalladi.finalbot | 2112 | 49 | 380 | 325-55 | 10% (b1z2b 6-52) |
| 11 | CyrilSharma.finalBot | 2102 | 48 | 380 | 322-58 | 12% (b1z2b 7-51) |
| 12 | winkelmantanner.waffle | 2066 | 45 | 380 | 311-69 | 21% (b1z2b 12-46) |
| 13 | ColtG5.Goob_final | 1904 | 36 | 380 | 243-137 | 38% (b1z2b 22-36) |
| 14 | **us:g_iter1_c2** | 1890 | 132 | 110 | 84-26 |  |
| 15 | **us:arch_rush10** | 1890 | 132 | 110 | 84-26 |  |
| 16 | **us:a3dig5** | 1890 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1879 | 36 | 380 | 230-150 | 45% (b1z2b 26-32) |
| 18 | **us:e1aggr** | 1864 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1845 | 35 | 380 | 212-168 | 57% (b1z2b 33-25) |
| 20 | **us:c6pair** | 1838 | 131 | 110 | 82-28 |  |
| 21 | **us:a3dig10** | 1838 | 131 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1838 | 131 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1816 | 29 | 1160 | 412-748 |  |
| 24 | **us:g_iter1** | 1816 | 16 | 3870 | 1414-2456 |  |
| 25 | **us:e2aggr** | 1813 | 130 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1813 | 130 | 110 | 81-29 |  |
| 27 | **us:a2relay** | 1813 | 130 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1802 | 35 | 380 | 189-191 | 59% (b1z2b 34-24) |
| 29 | **us:b1v2** | 1786 | 26 | 1520 | 508-1012 |  |
| 30 | **us:b2fs** | 1778 | 40 | 640 | 211-429 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1635 | 39 | 380 | 105-275 | 84% (b1z2b 49-9) |
| 32 | clbarrell.duck8 | 1601 | 41 | 380 | 91-289 | 69% (b1z2b 40-18) |
| 33 | jonters.bling3 | 1465 | 53 | 380 | 48-332 | 79% (b1z2b 46-12) |
| 34 | **us:arch_rush** | 1452 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1388 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1387 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1354 | 68 | 380 | 27-353 | 84% (b1z2b 49-9) |
| 38 | Metta-AI.bc24scenario | 1320 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1320 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1320 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1231 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1231 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1231 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1231 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1231 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1231 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1231 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1231 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1231 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1231 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1231 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1231 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1125 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1093 | 357 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1093 | 357 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1093 | 357 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1093 | 357 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1093 | 357 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1093 | 357 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1093 | 357 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1093 | 357 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1093 | 357 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1093 | 357 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1093 | 357 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1093 | 357 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1093 | 357 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1093 | 357 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1093 | 357 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1093 | 357 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1093 | 357 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1039 | 154 | 380 | 4-376 | 100% (b1z2b 58-0) |
