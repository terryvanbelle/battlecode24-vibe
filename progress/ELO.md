# Ladder

15989 scrimmages (ours only), 15989 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g1basics | 2059 +- 74 | 10 of 76 | 120 | 94-26 | 84.0% | 27.5% (vs 9) |
| g2cr | 2015 +- 55 | 12 of 76 | 160 | 95-65 | 82.5% | 25.3% (vs 10) |
| g_iter2 | 1902 +- 16 | 15 of 76 | 1960 | 887-1073 | 78.6% | 19.3% (vs 12) |
| arch_rush10 | 1817 +- 130 | 17 of 76 | 110 | 84-26 | 75.7% | 15.7% (vs 13) |
| g_iter1_c2 | 1817 +- 130 | 18 of 76 | 110 | 84-26 | 75.7% | 15.7% (vs 13) |
| a3dig5 | 1817 +- 130 | 19 of 76 | 110 | 84-26 | 75.7% | 15.7% (vs 13) |
| e1aggr | 1792 +- 129 | 21 of 76 | 110 | 83-27 | 74.8% | 16.4% (vs 14) |
| c6pair | 1767 +- 128 | 23 of 76 | 110 | 82-28 | 73.9% | 17.1% (vs 15) |
| a3dig10 | 1767 +- 128 | 24 of 76 | 110 | 82-28 | 73.9% | 17.1% (vs 15) |
| c5bank | 1767 +- 128 | 25 of 76 | 110 | 82-28 | 73.9% | 17.1% (vs 15) |
| e2aggr | 1742 +- 127 | 26 of 76 | 110 | 81-29 | 73.1% | 15.5% (vs 15) |
| a2reloc | 1742 +- 127 | 27 of 76 | 110 | 81-29 | 73.1% | 15.5% (vs 15) |
| a2relay | 1742 +- 127 | 28 of 76 | 110 | 81-29 | 73.1% | 15.5% (vs 15) |
| b1z2b | 1732 +- 23 | 29 of 76 | 1760 | 620-1140 | 72.7% | 14.9% (vs 15) |
| g_iter1 | 1732 +- 10 | 30 of 76 | 6990 | 2485-4505 | 72.7% | 14.9% (vs 15) |
| g1copy | 1727 +- 91 | 31 of 76 | 80 | 28-52 | 72.5% | 14.5% (vs 15) |
| g1sym | 1726 +- 51 | 32 of 76 | 200 | 68-132 | 72.5% | 14.5% (vs 15) |
| b2fs | 1718 +- 28 | 33 of 76 | 1280 | 439-841 | 72.2% | 14.0% (vs 15) |
| b1v2 | 1707 +- 22 | 35 of 76 | 2120 | 711-1409 | 71.8% | 15.6% (vs 16) |
| arch_rush | 1392 +- 94 | 38 of 76 | 110 | 62-48 | 56.6% | 6.4% (vs 18) |
| g_iter0 | 1328 +- 89 | 41 of 76 | 109 | 56-53 | 52.1% | 8.9% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2342 | 86 | 532 | 517-15 | 3% (g_iter1 7-217) |
| 2 | jmerle.camel_case_v21_final | 2342 | 86 | 532 | 517-15 | 4% (g_iter1 10-214) |
| 3 | IvanGeffner.kuma | 2311 | 80 | 532 | 514-18 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2310 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2222 | 63 | 532 | 502-30 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2194 | 59 | 532 | 497-35 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2179 | 57 | 532 | 494-38 | 6% (g_iter1 13-211) |
| 8 | Gymhgy.v10official | 2131 | 51 | 532 | 483-49 | 11% (g_iter1 25-199) |
| 9 | andrewgopher.player22 | 2099 | 47 | 532 | 474-58 | 11% (g_iter1 24-200) |
| 10 | **us:g1basics** | 2059 | 74 | 120 | 94-26 |  |
| 11 | hsmalladi.finalbot | 2043 | 42 | 532 | 455-77 | 12% (g_iter1 27-197) |
| 12 | **us:g2cr** | 2015 | 55 | 160 | 95-65 |  |
| 13 | CyrilSharma.finalBot | 2011 | 39 | 532 | 442-90 | 20% (g_iter1 44-180) |
| 14 | winkelmantanner.waffle | 1954 | 16 | 2012 | 1238-774 | 59% (g2cr 95-65) |
| 15 | **us:g_iter2** | 1902 | 16 | 1960 | 887-1073 |  |
| 16 | ColtG5.Goob_final | 1844 | 12 | 3492 | 2199-1293 | 59% (g_iter2 109-75) |
| 17 | **us:arch_rush10** | 1817 | 130 | 110 | 84-26 |  |
| 18 | **us:g_iter1_c2** | 1817 | 130 | 110 | 84-26 |  |
| 19 | **us:a3dig5** | 1817 | 130 | 110 | 84-26 |  |
| 20 | kyleezz.jeeryfix3 | 1814 | 31 | 532 | 327-205 | 41% (g_iter1 91-133) |
| 21 | **us:e1aggr** | 1792 | 129 | 110 | 83-27 |  |
| 22 | quesswho.cretplayer2_3 | 1767 | 30 | 532 | 293-239 | 40% (g_iter1 90-134) |
| 23 | **us:c6pair** | 1767 | 128 | 110 | 82-28 |  |
| 24 | **us:a3dig10** | 1767 | 128 | 110 | 82-28 |  |
| 25 | **us:c5bank** | 1767 | 128 | 110 | 82-28 |  |
| 26 | **us:e2aggr** | 1742 | 127 | 110 | 81-29 |  |
| 27 | **us:a2reloc** | 1742 | 127 | 110 | 81-29 |  |
| 28 | **us:a2relay** | 1742 | 127 | 110 | 81-29 |  |
| 29 | **us:b1z2b** | 1732 | 23 | 1760 | 620-1140 |  |
| 30 | **us:g_iter1** | 1732 | 10 | 6990 | 2485-4505 |  |
| 31 | **us:g1copy** | 1727 | 91 | 80 | 28-52 |  |
| 32 | **us:g1sym** | 1726 | 51 | 200 | 68-132 |  |
| 33 | **us:b2fs** | 1718 | 28 | 1280 | 439-841 |  |
| 34 | SampleProvider.TSPAARKSPRINT1 | 1714 | 30 | 532 | 253-279 | 50% (g_iter1 112-112) |
| 35 | **us:b1v2** | 1707 | 22 | 2120 | 711-1409 |  |
| 36 | dmtrung14.defaultplayer_intlqualifier | 1550 | 34 | 532 | 140-392 | 69% (g_iter1 155-69) |
| 37 | clbarrell.duck8 | 1529 | 35 | 532 | 128-404 | 78% (g_iter1 175-49) |
| 38 | **us:arch_rush** | 1392 | 94 | 110 | 62-48 |  |
| 39 | jonters.bling3 | 1376 | 46 | 532 | 62-470 | 89% (g_iter1 200-24) |
| 40 | HugoIngelsson.Bot21 | 1331 | 201 | 26 | 3-23 |  |
| 41 | **us:g_iter0** | 1328 | 89 | 109 | 56-53 |  |
| 42 | Metta-AI.bc24scenario | 1264 | 224 | 26 | 2-24 |  |
| 43 | noahzemlin.honeyducklings | 1264 | 224 | 26 | 2-24 |  |
| 44 | sivakovivan.NewHide | 1264 | 224 | 26 | 2-24 |  |
| 45 | awu7.ExplosiveBot | 1262 | 60 | 532 | 34-498 | 97% (g_iter1 217-7) |
| 46 | JeffLegendPower.v11 | 1176 | 264 | 26 | 1-25 |  |
| 47 | MiloAkerman.v1 | 1176 | 264 | 26 | 1-25 |  |
| 48 | PerishoJ.tx | 1176 | 264 | 26 | 1-25 |  |
| 49 | Peter-Fun.dinoboxer | 1176 | 264 | 26 | 1-25 |  |
| 50 | RyanAspen.v22 | 1176 | 264 | 26 | 1-25 |  |
| 51 | TylerJulian.v9 | 1176 | 264 | 26 | 1-25 |  |
| 52 | aj-chau.cowards | 1176 | 264 | 26 | 1-25 |  |
| 53 | cViper971.ourplayer | 1176 | 264 | 26 | 1-25 |  |
| 54 | dylanzemlin.dangerduck2 | 1176 | 264 | 26 | 1-25 |  |
| 55 | lukerhoads.warrior_2nd_comp | 1176 | 264 | 26 | 1-25 |  |
| 56 | neilhuang007.baseline | 1176 | 264 | 26 | 1-25 |  |
| 57 | polyllc.polyv4 | 1176 | 264 | 26 | 1-25 |  |
| 58 | andrearante12.turtle | 1071 | 357 | 25 | 0-25 |  |
| 59 | AlexYu84.smartPlayer | 1038 | 357 | 26 | 0-26 |  |
| 60 | H4ffliger.keyboardcrusader_v1 | 1038 | 357 | 26 | 0-26 |  |
| 61 | Lithanium.AttackingBot | 1038 | 357 | 26 | 0-26 |  |
| 62 | Rubrasum.version_3 | 1038 | 357 | 26 | 0-26 |  |
| 63 | SriLakshmiPolavarapu.ducks | 1038 | 357 | 26 | 0-26 |  |
| 64 | VarunVejalla.alexander | 1038 | 357 | 26 | 0-26 |  |
| 65 | abdullah8a0.crayBasic | 1038 | 357 | 26 | 0-26 |  |
| 66 | adamseth2.moveBot1 | 1038 | 357 | 26 | 0-26 |  |
| 67 | dylanconklin.Team3 | 1038 | 357 | 26 | 0-26 |  |
| 68 | itswin.MPAttack | 1038 | 357 | 26 | 0-26 |  |
| 69 | joelcrouch.ducks | 1038 | 357 | 26 | 0-26 |  |
| 70 | lcforges.funkyguy3 | 1038 | 357 | 26 | 0-26 |  |
| 71 | qpwoeirut.tournament_sprint1 | 1038 | 357 | 26 | 0-26 |  |
| 72 | reeceyang.v5 | 1038 | 357 | 26 | 0-26 |  |
| 73 | samithShetty.combustiblelemon | 1038 | 357 | 26 | 0-26 |  |
| 74 | sayam-goyal.SimpleBot | 1038 | 357 | 26 | 0-26 |  |
| 75 | tlevietpdx.Sprint2 | 1038 | 357 | 26 | 0-26 |  |
| 76 | justinottesen.sprint1 | 911 | 153 | 532 | 4-528 | 100% (g_iter1 224-0) |
