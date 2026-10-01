# Ladder

4509 scrimmages (ours only), 4509 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1904 +- 131 | 15 of 70 | 110 | 84-26 | 75.6% | 18.2% (vs 14) |
| a3dig5 | 1904 +- 131 | 16 of 70 | 110 | 84-26 | 75.6% | 18.2% (vs 14) |
| arch_rush10 | 1904 +- 131 | 17 of 70 | 110 | 84-26 | 75.6% | 18.2% (vs 14) |
| e1aggr | 1878 +- 131 | 19 of 70 | 110 | 83-27 | 74.7% | 18.6% (vs 15) |
| c5bank | 1852 +- 130 | 20 of 70 | 110 | 82-28 | 73.8% | 16.8% (vs 15) |
| c6pair | 1852 +- 130 | 21 of 70 | 110 | 82-28 | 73.8% | 16.8% (vs 15) |
| a3dig10 | 1852 +- 130 | 22 of 70 | 110 | 82-28 | 73.8% | 16.8% (vs 15) |
| b1z2b | 1835 +- 150 | 23 of 70 | 40 | 15-25 | 73.2% | 15.6% (vs 15) |
| g_iter1 | 1832 +- 19 | 24 of 70 | 2750 | 1017-1733 | 73.2% | 15.4% (vs 15) |
| a2reloc | 1827 +- 129 | 25 of 70 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| e2aggr | 1827 +- 129 | 26 of 70 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| a2relay | 1827 +- 129 | 27 of 70 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| b1v2 | 1802 +- 50 | 29 of 70 | 400 | 134-266 | 72.1% | 15.6% (vs 16) |
| arch_rush | 1464 +- 95 | 32 of 70 | 110 | 62-48 | 56.5% | 5.5% (vs 18) |
| g_iter0 | 1398 +- 89 | 35 of 70 | 109 | 56-53 | 51.9% | 8.4% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2448 | 154 | 180 | 176-4 | 3% (g_iter1 4-130) |
| 2 | uravt.Version18Final | 2396 | 348 | 26 | 26-0 |  |
| 3 | andli28.v9_USQuals_angle | 2387 | 131 | 180 | 174-6 | 4% (g_iter1 6-128) |
| 4 | jmerle.camel_case_v21_final | 2387 | 131 | 180 | 174-6 | 4% (g_iter1 5-129) |
| 5 | IvanGeffner.kuma | 2341 | 117 | 180 | 172-8 | 4% (g_iter1 6-128) |
| 6 | chenyx512.flagbot_final | 2341 | 117 | 180 | 172-8 | 4% (g_iter1 6-128) |
| 7 | NotLLeon.v3 | 2259 | 95 | 180 | 167-13 | 7% (g_iter1 10-124) |
| 8 | andrewgopher.player22 | 2234 | 89 | 180 | 165-15 | 10% (g_iter1 14-120) |
| 9 | Gymhgy.v10official | 2191 | 81 | 180 | 161-19 | 14% (g_iter1 19-115) |
| 10 | hsmalladi.finalbot | 2109 | 68 | 180 | 151-29 | 13% (g_iter1 17-117) |
| 11 | CyrilSharma.finalBot | 2070 | 64 | 180 | 145-35 | 22% (g_iter1 30-104) |
| 12 | winkelmantanner.waffle | 2041 | 61 | 180 | 140-40 | 25% (g_iter1 33-101) |
| 13 | ColtG5.Goob_final | 1923 | 53 | 180 | 115-65 | 40% (g_iter1 53-81) |
| 14 | quesswho.cretplayer2_3 | 1915 | 53 | 180 | 113-67 | 35% (g_iter1 47-87) |
| 15 | **us:g_iter1_c2** | 1904 | 131 | 110 | 84-26 |  |
| 16 | **us:a3dig5** | 1904 | 131 | 110 | 84-26 |  |
| 17 | **us:arch_rush10** | 1904 | 131 | 110 | 84-26 |  |
| 18 | kyleezz.jeeryfix3 | 1882 | 52 | 180 | 105-75 | 43% (g_iter1 58-76) |
| 19 | **us:e1aggr** | 1878 | 131 | 110 | 83-27 |  |
| 20 | **us:c5bank** | 1852 | 130 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1852 | 130 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1852 | 130 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1835 | 150 | 40 | 15-25 |  |
| 24 | **us:g_iter1** | 1832 | 19 | 2750 | 1017-1733 |  |
| 25 | **us:a2reloc** | 1827 | 129 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1827 | 129 | 110 | 81-29 |  |
| 27 | **us:a2relay** | 1827 | 129 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1827 | 51 | 180 | 91-89 | 49% (g_iter1 66-68) |
| 29 | **us:b1v2** | 1802 | 50 | 400 | 134-266 |  |
| 30 | dmtrung14.defaultplayer_intlqualifier | 1691 | 55 | 180 | 58-122 | 65% (g_iter1 87-47) |
| 31 | clbarrell.duck8 | 1617 | 60 | 180 | 43-137 | 77% (g_iter1 103-31) |
| 32 | **us:arch_rush** | 1464 | 95 | 110 | 62-48 |  |
| 33 | jonters.bling3 | 1423 | 87 | 180 | 17-163 | 91% (g_iter1 122-12) |
| 34 | HugoIngelsson.Bot21 | 1399 | 203 | 26 | 3-23 |  |
| 35 | **us:g_iter0** | 1398 | 89 | 109 | 56-53 |  |
| 36 | awu7.ExplosiveBot | 1342 | 104 | 180 | 11-169 | 96% (g_iter1 129-5) |
| 37 | Metta-AI.bc24scenario | 1330 | 226 | 26 | 2-24 |  |
| 38 | noahzemlin.honeyducklings | 1330 | 226 | 26 | 2-24 |  |
| 39 | sivakovivan.NewHide | 1330 | 226 | 26 | 2-24 |  |
| 40 | JeffLegendPower.v11 | 1242 | 265 | 26 | 1-25 |  |
| 41 | MiloAkerman.v1 | 1242 | 265 | 26 | 1-25 |  |
| 42 | PerishoJ.tx | 1242 | 265 | 26 | 1-25 |  |
| 43 | Peter-Fun.dinoboxer | 1242 | 265 | 26 | 1-25 |  |
| 44 | RyanAspen.v22 | 1242 | 265 | 26 | 1-25 |  |
| 45 | TylerJulian.v9 | 1242 | 265 | 26 | 1-25 |  |
| 46 | aj-chau.cowards | 1242 | 265 | 26 | 1-25 |  |
| 47 | cViper971.ourplayer | 1242 | 265 | 26 | 1-25 |  |
| 48 | dylanzemlin.dangerduck2 | 1242 | 265 | 26 | 1-25 |  |
| 49 | lukerhoads.warrior_2nd_comp | 1242 | 265 | 26 | 1-25 |  |
| 50 | neilhuang007.baseline | 1242 | 265 | 26 | 1-25 |  |
| 51 | polyllc.polyv4 | 1242 | 265 | 26 | 1-25 |  |
| 52 | justinottesen.sprint1 | 1177 | 157 | 180 | 4-176 | 100% (g_iter1 134-0) |
| 53 | andrearante12.turtle | 1135 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1103 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1103 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1103 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1103 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1103 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1103 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1103 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1103 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1103 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1103 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1103 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1103 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1103 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1103 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1103 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1103 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1103 | 358 | 26 | 0-26 |  |
