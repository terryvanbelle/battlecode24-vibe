# Ladder

13629 scrimmages (ours only), 13629 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g1basics | 1925 +- 76 | 13 of 74 | 80 | 46-34 | 78.4% | 18.8% (vs 12) |
| g_iter1_c2 | 1846 +- 130 | 15 of 74 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| arch_rush10 | 1846 +- 130 | 16 of 74 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| a3dig5 | 1846 +- 130 | 17 of 74 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| e1aggr | 1821 +- 130 | 19 of 74 | 110 | 83-27 | 74.8% | 16.3% (vs 14) |
| a3dig10 | 1796 +- 129 | 21 of 74 | 110 | 82-28 | 73.9% | 16.9% (vs 15) |
| c6pair | 1796 +- 129 | 22 of 74 | 110 | 82-28 | 73.9% | 16.9% (vs 15) |
| c5bank | 1796 +- 129 | 23 of 74 | 110 | 82-28 | 73.9% | 16.9% (vs 15) |
| a2relay | 1771 +- 128 | 24 of 74 | 110 | 81-29 | 73.0% | 15.3% (vs 15) |
| a2reloc | 1771 +- 128 | 25 of 74 | 110 | 81-29 | 73.0% | 15.3% (vs 15) |
| e2aggr | 1771 +- 128 | 26 of 74 | 110 | 81-29 | 73.0% | 15.3% (vs 15) |
| b1z2b | 1765 +- 23 | 27 of 74 | 1760 | 620-1140 | 72.8% | 14.9% (vs 15) |
| g_iter1 | 1765 +- 11 | 28 of 74 | 6790 | 2404-4386 | 72.8% | 14.9% (vs 15) |
| g1sym | 1762 +- 51 | 29 of 74 | 200 | 68-132 | 72.7% | 14.7% (vs 15) |
| g1copy | 1761 +- 91 | 30 of 74 | 80 | 28-52 | 72.7% | 14.7% (vs 15) |
| b2fs | 1751 +- 28 | 31 of 74 | 1280 | 439-841 | 72.3% | 14.1% (vs 15) |
| b1v2 | 1740 +- 22 | 33 of 74 | 2120 | 711-1409 | 71.9% | 15.6% (vs 16) |
| arch_rush | 1417 +- 95 | 36 of 74 | 110 | 62-48 | 56.6% | 6.1% (vs 18) |
| g_iter0 | 1352 +- 89 | 39 of 74 | 109 | 56-53 | 52.0% | 8.6% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2391 | 95 | 508 | 496-12 | 4% (g_iter1 10-214) |
| 2 | Strequals.duck0127v5 | 2365 | 89 | 508 | 494-14 | 3% (g_iter1 7-217) |
| 3 | IvanGeffner.kuma | 2343 | 84 | 508 | 492-16 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2339 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2234 | 63 | 508 | 478-30 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2217 | 60 | 508 | 475-33 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2191 | 57 | 508 | 470-38 | 6% (g_iter1 13-211) |
| 8 | Gymhgy.v10official | 2168 | 54 | 508 | 465-43 | 11% (g_iter1 25-199) |
| 9 | andrewgopher.player22 | 2132 | 49 | 508 | 456-52 | 11% (g_iter1 24-200) |
| 10 | hsmalladi.finalbot | 2069 | 43 | 508 | 436-72 | 12% (g_iter1 27-197) |
| 11 | CyrilSharma.finalBot | 2040 | 41 | 508 | 425-83 | 20% (g_iter1 44-180) |
| 12 | winkelmantanner.waffle | 2001 | 38 | 508 | 408-100 | 21% (g_iter1 47-177) |
| 13 | **us:g1basics** | 1925 | 76 | 80 | 46-34 |  |
| 14 | ColtG5.Goob_final | 1880 | 13 | 3068 | 2013-1055 | 57% (g1basics 46-34) |
| 15 | **us:g_iter1_c2** | 1846 | 130 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1846 | 130 | 110 | 84-26 |  |
| 17 | **us:a3dig5** | 1846 | 130 | 110 | 84-26 |  |
| 18 | kyleezz.jeeryfix3 | 1843 | 31 | 508 | 316-192 | 41% (g_iter1 91-133) |
| 19 | **us:e1aggr** | 1821 | 130 | 110 | 83-27 |  |
| 20 | quesswho.cretplayer2_3 | 1801 | 31 | 508 | 286-222 | 40% (g_iter1 90-134) |
| 21 | **us:a3dig10** | 1796 | 129 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1796 | 129 | 110 | 82-28 |  |
| 23 | **us:c5bank** | 1796 | 129 | 110 | 82-28 |  |
| 24 | **us:a2relay** | 1771 | 128 | 110 | 81-29 |  |
| 25 | **us:a2reloc** | 1771 | 128 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1771 | 128 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1765 | 23 | 1760 | 620-1140 |  |
| 28 | **us:g_iter1** | 1765 | 11 | 6790 | 2404-4386 |  |
| 29 | **us:g1sym** | 1762 | 51 | 200 | 68-132 |  |
| 30 | **us:g1copy** | 1761 | 91 | 80 | 28-52 |  |
| 31 | **us:b2fs** | 1751 | 28 | 1280 | 439-841 |  |
| 32 | SampleProvider.TSPAARKSPRINT1 | 1751 | 30 | 508 | 250-258 | 50% (g_iter1 112-112) |
| 33 | **us:b1v2** | 1740 | 22 | 2120 | 711-1409 |  |
| 34 | dmtrung14.defaultplayer_intlqualifier | 1584 | 34 | 508 | 138-370 | 69% (g_iter1 155-69) |
| 35 | clbarrell.duck8 | 1565 | 35 | 508 | 127-381 | 78% (g_iter1 175-49) |
| 36 | **us:arch_rush** | 1417 | 95 | 110 | 62-48 |  |
| 37 | jonters.bling3 | 1409 | 47 | 508 | 61-447 | 89% (g_iter1 200-24) |
| 38 | HugoIngelsson.Bot21 | 1354 | 201 | 26 | 3-23 |  |
| 39 | **us:g_iter0** | 1352 | 89 | 109 | 56-53 |  |
| 40 | awu7.ExplosiveBot | 1297 | 60 | 508 | 34-474 | 97% (g_iter1 217-7) |
| 41 | Metta-AI.bc24scenario | 1287 | 224 | 26 | 2-24 |  |
| 42 | noahzemlin.honeyducklings | 1287 | 224 | 26 | 2-24 |  |
| 43 | sivakovivan.NewHide | 1287 | 224 | 26 | 2-24 |  |
| 44 | JeffLegendPower.v11 | 1199 | 264 | 26 | 1-25 |  |
| 45 | MiloAkerman.v1 | 1199 | 264 | 26 | 1-25 |  |
| 46 | PerishoJ.tx | 1199 | 264 | 26 | 1-25 |  |
| 47 | Peter-Fun.dinoboxer | 1199 | 264 | 26 | 1-25 |  |
| 48 | RyanAspen.v22 | 1199 | 264 | 26 | 1-25 |  |
| 49 | TylerJulian.v9 | 1199 | 264 | 26 | 1-25 |  |
| 50 | aj-chau.cowards | 1199 | 264 | 26 | 1-25 |  |
| 51 | cViper971.ourplayer | 1199 | 264 | 26 | 1-25 |  |
| 52 | dylanzemlin.dangerduck2 | 1199 | 264 | 26 | 1-25 |  |
| 53 | lukerhoads.warrior_2nd_comp | 1199 | 264 | 26 | 1-25 |  |
| 54 | neilhuang007.baseline | 1199 | 264 | 26 | 1-25 |  |
| 55 | polyllc.polyv4 | 1199 | 264 | 26 | 1-25 |  |
| 56 | andrearante12.turtle | 1093 | 357 | 25 | 0-25 |  |
| 57 | AlexYu84.smartPlayer | 1061 | 357 | 26 | 0-26 |  |
| 58 | H4ffliger.keyboardcrusader_v1 | 1061 | 357 | 26 | 0-26 |  |
| 59 | Lithanium.AttackingBot | 1061 | 357 | 26 | 0-26 |  |
| 60 | Rubrasum.version_3 | 1061 | 357 | 26 | 0-26 |  |
| 61 | SriLakshmiPolavarapu.ducks | 1061 | 357 | 26 | 0-26 |  |
| 62 | VarunVejalla.alexander | 1061 | 357 | 26 | 0-26 |  |
| 63 | abdullah8a0.crayBasic | 1061 | 357 | 26 | 0-26 |  |
| 64 | adamseth2.moveBot1 | 1061 | 357 | 26 | 0-26 |  |
| 65 | dylanconklin.Team3 | 1061 | 357 | 26 | 0-26 |  |
| 66 | itswin.MPAttack | 1061 | 357 | 26 | 0-26 |  |
| 67 | joelcrouch.ducks | 1061 | 357 | 26 | 0-26 |  |
| 68 | lcforges.funkyguy3 | 1061 | 357 | 26 | 0-26 |  |
| 69 | qpwoeirut.tournament_sprint1 | 1061 | 357 | 26 | 0-26 |  |
| 70 | reeceyang.v5 | 1061 | 357 | 26 | 0-26 |  |
| 71 | samithShetty.combustiblelemon | 1061 | 357 | 26 | 0-26 |  |
| 72 | sayam-goyal.SimpleBot | 1061 | 357 | 26 | 0-26 |  |
| 73 | tlevietpdx.Sprint2 | 1061 | 357 | 26 | 0-26 |  |
| 74 | justinottesen.sprint1 | 946 | 153 | 508 | 4-504 | 100% (g_iter1 224-0) |
