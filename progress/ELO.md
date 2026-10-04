# Ladder

19629 scrimmages (ours only), 19629 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g3lost | 1968 +- 109 | 12 of 78 | 40 | 17-23 | 81.9% | 25.0% (vs 11) |
| g2cr | 1907 +- 107 | 13 of 78 | 40 | 21-19 | 79.8% | 19.3% (vs 11) |
| g_iter3 | 1905 +- 13 | 14 of 78 | 3320 | 1194-2126 | 79.7% | 19.1% (vs 11) |
| g3escrg2 | 1904 +- 44 | 15 of 78 | 280 | 90-190 | 79.7% | 19.0% (vs 11) |
| g_iter2 | 1864 +- 15 | 17 of 78 | 2200 | 1032-1168 | 78.4% | 18.2% (vs 12) |
| g_iter1_c2 | 1787 +- 130 | 19 of 78 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| a3dig5 | 1787 +- 130 | 20 of 78 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| arch_rush10 | 1787 +- 130 | 21 of 78 | 110 | 84-26 | 75.7% | 15.6% (vs 13) |
| e1aggr | 1762 +- 129 | 23 of 78 | 110 | 83-27 | 74.9% | 16.4% (vs 14) |
| c5bank | 1737 +- 128 | 24 of 78 | 110 | 82-28 | 74.0% | 14.8% (vs 14) |
| a3dig10 | 1737 +- 128 | 25 of 78 | 110 | 82-28 | 74.0% | 14.8% (vs 14) |
| c6pair | 1737 +- 128 | 26 of 78 | 110 | 82-28 | 74.0% | 14.8% (vs 14) |
| e2aggr | 1713 +- 127 | 28 of 78 | 110 | 81-29 | 73.1% | 15.6% (vs 15) |
| a2relay | 1713 +- 127 | 29 of 78 | 110 | 81-29 | 73.1% | 15.6% (vs 15) |
| a2reloc | 1713 +- 127 | 30 of 78 | 110 | 81-29 | 73.1% | 15.6% (vs 15) |
| b1z2b | 1699 +- 24 | 31 of 78 | 1760 | 620-1140 | 72.6% | 14.7% (vs 15) |
| g_iter1 | 1692 +- 10 | 32 of 78 | 6990 | 2485-4505 | 72.4% | 14.3% (vs 15) |
| g1copy | 1686 +- 91 | 33 of 78 | 80 | 28-52 | 72.1% | 13.9% (vs 15) |
| b2fs | 1685 +- 28 | 34 of 78 | 1280 | 439-841 | 72.1% | 13.9% (vs 15) |
| g1sym | 1680 +- 51 | 35 of 78 | 200 | 68-132 | 72.0% | 13.6% (vs 15) |
| b1v2 | 1673 +- 22 | 37 of 78 | 2120 | 711-1409 | 71.7% | 15.5% (vs 16) |
| arch_rush | 1366 +- 94 | 40 of 78 | 110 | 62-48 | 56.7% | 6.7% (vs 18) |
| g_iter0 | 1301 +- 89 | 43 of 78 | 109 | 56-53 | 52.1% | 9.2% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2317 | 84 | 556 | 540-16 | 3% (g_iter1 7-217) |
| 2 | jmerle.camel_case_v21_final | 2317 | 84 | 556 | 540-16 | 4% (g_iter1 10-214) |
| 3 | uravt.Version18Final | 2281 | 347 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2279 | 76 | 556 | 536-20 | 4% (g_iter1 9-215) |
| 5 | chenyx512.flagbot_final | 2208 | 63 | 556 | 526-30 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2160 | 56 | 556 | 517-39 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2151 | 55 | 556 | 515-41 | 6% (g_iter1 13-211) |
| 8 | Gymhgy.v10official | 2088 | 48 | 556 | 499-57 | 11% (g_iter1 25-199) |
| 9 | andrewgopher.player22 | 2075 | 46 | 556 | 495-61 | 11% (g_iter1 24-200) |
| 10 | CyrilSharma.finalBot | 2036 | 13 | 3476 | 2467-1009 | 42% (g3lost 17-23) |
| 11 | hsmalladi.finalbot | 2020 | 41 | 556 | 476-80 | 12% (g_iter1 27-197) |
| 12 | **us:g3lost** | 1968 | 109 | 40 | 17-23 |  |
| 13 | **us:g2cr** | 1907 | 107 | 40 | 21-19 |  |
| 14 | **us:g_iter3** | 1905 | 13 | 3320 | 1194-2126 |  |
| 15 | **us:g3escrg2** | 1904 | 44 | 280 | 90-190 |  |
| 16 | winkelmantanner.waffle | 1904 | 15 | 2276 | 1362-914 | 62% (g_iter3 163-101) |
| 17 | **us:g_iter2** | 1864 | 15 | 2200 | 1032-1168 |  |
| 18 | ColtG5.Goob_final | 1797 | 12 | 3516 | 2201-1315 | 67% (g_iter2 203-101) |
| 19 | **us:g_iter1_c2** | 1787 | 130 | 110 | 84-26 |  |
| 20 | **us:a3dig5** | 1787 | 130 | 110 | 84-26 |  |
| 21 | **us:arch_rush10** | 1787 | 130 | 110 | 84-26 |  |
| 22 | kyleezz.jeeryfix3 | 1778 | 30 | 556 | 335-221 | 41% (g_iter1 91-133) |
| 23 | **us:e1aggr** | 1762 | 129 | 110 | 83-27 |  |
| 24 | **us:c5bank** | 1737 | 128 | 110 | 82-28 |  |
| 25 | **us:a3dig10** | 1737 | 128 | 110 | 82-28 |  |
| 26 | **us:c6pair** | 1737 | 128 | 110 | 82-28 |  |
| 27 | quesswho.cretplayer2_3 | 1732 | 29 | 556 | 300-256 | 40% (g_iter1 90-134) |
| 28 | **us:e2aggr** | 1713 | 127 | 110 | 81-29 |  |
| 29 | **us:a2relay** | 1713 | 127 | 110 | 81-29 |  |
| 30 | **us:a2reloc** | 1713 | 127 | 110 | 81-29 |  |
| 31 | **us:b1z2b** | 1699 | 24 | 1760 | 620-1140 |  |
| 32 | **us:g_iter1** | 1692 | 10 | 6990 | 2485-4505 |  |
| 33 | **us:g1copy** | 1686 | 91 | 80 | 28-52 |  |
| 34 | **us:b2fs** | 1685 | 28 | 1280 | 439-841 |  |
| 35 | **us:g1sym** | 1680 | 51 | 200 | 68-132 |  |
| 36 | SampleProvider.TSPAARKSPRINT1 | 1675 | 29 | 556 | 256-300 | 50% (g_iter1 112-112) |
| 37 | **us:b1v2** | 1673 | 22 | 2120 | 711-1409 |  |
| 38 | dmtrung14.defaultplayer_intlqualifier | 1512 | 33 | 556 | 141-415 | 69% (g_iter1 155-69) |
| 39 | clbarrell.duck8 | 1489 | 35 | 556 | 128-428 | 78% (g_iter1 175-49) |
| 40 | **us:arch_rush** | 1366 | 94 | 110 | 62-48 |  |
| 41 | jonters.bling3 | 1340 | 46 | 556 | 63-493 | 89% (g_iter1 200-24) |
| 42 | HugoIngelsson.Bot21 | 1305 | 200 | 26 | 3-23 |  |
| 43 | **us:g_iter0** | 1301 | 89 | 109 | 56-53 |  |
| 44 | Metta-AI.bc24scenario | 1239 | 223 | 26 | 2-24 |  |
| 45 | noahzemlin.honeyducklings | 1239 | 223 | 26 | 2-24 |  |
| 46 | sivakovivan.NewHide | 1239 | 223 | 26 | 2-24 |  |
| 47 | awu7.ExplosiveBot | 1224 | 60 | 556 | 34-522 | 97% (g_iter1 217-7) |
| 48 | JeffLegendPower.v11 | 1151 | 263 | 26 | 1-25 |  |
| 49 | MiloAkerman.v1 | 1151 | 263 | 26 | 1-25 |  |
| 50 | PerishoJ.tx | 1151 | 263 | 26 | 1-25 |  |
| 51 | Peter-Fun.dinoboxer | 1151 | 263 | 26 | 1-25 |  |
| 52 | RyanAspen.v22 | 1151 | 263 | 26 | 1-25 |  |
| 53 | TylerJulian.v9 | 1151 | 263 | 26 | 1-25 |  |
| 54 | aj-chau.cowards | 1151 | 263 | 26 | 1-25 |  |
| 55 | cViper971.ourplayer | 1151 | 263 | 26 | 1-25 |  |
| 56 | dylanzemlin.dangerduck2 | 1151 | 263 | 26 | 1-25 |  |
| 57 | lukerhoads.warrior_2nd_comp | 1151 | 263 | 26 | 1-25 |  |
| 58 | neilhuang007.baseline | 1151 | 263 | 26 | 1-25 |  |
| 59 | polyllc.polyv4 | 1151 | 263 | 26 | 1-25 |  |
| 60 | andrearante12.turtle | 1047 | 357 | 25 | 0-25 |  |
| 61 | AlexYu84.smartPlayer | 1014 | 357 | 26 | 0-26 |  |
| 62 | H4ffliger.keyboardcrusader_v1 | 1014 | 357 | 26 | 0-26 |  |
| 63 | Lithanium.AttackingBot | 1014 | 357 | 26 | 0-26 |  |
| 64 | Rubrasum.version_3 | 1014 | 357 | 26 | 0-26 |  |
| 65 | SriLakshmiPolavarapu.ducks | 1014 | 357 | 26 | 0-26 |  |
| 66 | VarunVejalla.alexander | 1014 | 357 | 26 | 0-26 |  |
| 67 | abdullah8a0.crayBasic | 1014 | 357 | 26 | 0-26 |  |
| 68 | adamseth2.moveBot1 | 1014 | 357 | 26 | 0-26 |  |
| 69 | dylanconklin.Team3 | 1014 | 357 | 26 | 0-26 |  |
| 70 | itswin.MPAttack | 1014 | 357 | 26 | 0-26 |  |
| 71 | joelcrouch.ducks | 1014 | 357 | 26 | 0-26 |  |
| 72 | lcforges.funkyguy3 | 1014 | 357 | 26 | 0-26 |  |
| 73 | qpwoeirut.tournament_sprint1 | 1014 | 357 | 26 | 0-26 |  |
| 74 | reeceyang.v5 | 1014 | 357 | 26 | 0-26 |  |
| 75 | samithShetty.combustiblelemon | 1014 | 357 | 26 | 0-26 |  |
| 76 | sayam-goyal.SimpleBot | 1014 | 357 | 26 | 0-26 |  |
| 77 | tlevietpdx.Sprint2 | 1014 | 357 | 26 | 0-26 |  |
| 78 | justinottesen.sprint1 | 873 | 153 | 556 | 4-552 | 100% (g_iter1 224-0) |
