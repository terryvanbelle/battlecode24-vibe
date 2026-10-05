# Ladder

25989 scrimmages (ours only), 25989 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter4 | 1956 +- 10 | 12 of 82 | 5480 | 2190-3290 | 81.4% | 23.6% (vs 11) |
| g4crumb | 1953 +- 35 | 13 of 82 | 400 | 157-243 | 81.3% | 23.4% (vs 11) |
| g4econ2 | 1951 +- 56 | 14 of 82 | 160 | 60-100 | 81.3% | 23.2% (vs 11) |
| g3lost | 1939 +- 112 | 15 of 82 | 40 | 15-25 | 80.8% | 22.1% (vs 11) |
| g4pick | 1937 +- 65 | 16 of 82 | 120 | 43-77 | 80.8% | 21.9% (vs 11) |
| g_iter3 | 1912 +- 13 | 17 of 82 | 3520 | 1259-2261 | 79.9% | 19.6% (vs 11) |
| g2cr | 1912 +- 107 | 18 of 82 | 40 | 21-19 | 79.9% | 19.6% (vs 11) |
| g3escrg2 | 1912 +- 44 | 19 of 82 | 280 | 90-190 | 79.9% | 19.6% (vs 11) |
| g_iter2 | 1868 +- 15 | 21 of 82 | 2200 | 1032-1168 | 78.4% | 18.5% (vs 12) |
| g_iter1_c2 | 1789 +- 130 | 23 of 82 | 110 | 84-26 | 75.7% | 15.7% (vs 13) |
| a3dig5 | 1789 +- 130 | 24 of 82 | 110 | 84-26 | 75.7% | 15.7% (vs 13) |
| arch_rush10 | 1789 +- 130 | 25 of 82 | 110 | 84-26 | 75.7% | 15.7% (vs 13) |
| e1aggr | 1764 +- 129 | 27 of 82 | 110 | 83-27 | 74.9% | 16.4% (vs 14) |
| c6pair | 1739 +- 128 | 28 of 82 | 110 | 82-28 | 74.0% | 14.8% (vs 14) |
| a3dig10 | 1739 +- 128 | 29 of 82 | 110 | 82-28 | 74.0% | 14.8% (vs 14) |
| c5bank | 1739 +- 128 | 30 of 82 | 110 | 82-28 | 74.0% | 14.8% (vs 14) |
| e2aggr | 1714 +- 127 | 32 of 82 | 110 | 81-29 | 73.1% | 15.6% (vs 15) |
| a2reloc | 1714 +- 127 | 33 of 82 | 110 | 81-29 | 73.1% | 15.6% (vs 15) |
| a2relay | 1714 +- 127 | 34 of 82 | 110 | 81-29 | 73.1% | 15.6% (vs 15) |
| b1z2b | 1700 +- 24 | 35 of 82 | 1760 | 620-1140 | 72.6% | 14.7% (vs 15) |
| g_iter1 | 1693 +- 10 | 36 of 82 | 6990 | 2485-4505 | 72.4% | 14.3% (vs 15) |
| g1copy | 1687 +- 91 | 37 of 82 | 80 | 28-52 | 72.1% | 13.9% (vs 15) |
| b2fs | 1686 +- 28 | 38 of 82 | 1280 | 439-841 | 72.1% | 13.8% (vs 15) |
| g1sym | 1681 +- 51 | 39 of 82 | 200 | 68-132 | 71.9% | 13.6% (vs 15) |
| b1v2 | 1675 +- 22 | 41 of 82 | 2120 | 711-1409 | 71.7% | 15.5% (vs 16) |
| arch_rush | 1367 +- 94 | 44 of 82 | 110 | 62-48 | 56.7% | 6.7% (vs 18) |
| g_iter0 | 1303 +- 89 | 47 of 82 | 109 | 56-53 | 52.1% | 9.2% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2317 | 78 | 580 | 561-19 | 4% (g_iter1 10-214) |
| 2 | Strequals.duck0127v5 | 2308 | 76 | 580 | 560-20 | 3% (g_iter1 7-217) |
| 3 | IvanGeffner.kuma | 2291 | 73 | 580 | 558-22 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2283 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2230 | 63 | 580 | 549-31 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2165 | 54 | 580 | 536-44 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2156 | 53 | 580 | 534-46 | 6% (g_iter1 13-211) |
| 8 | andrewgopher.player22 | 2089 | 45 | 580 | 515-65 | 11% (g_iter1 24-200) |
| 9 | CyrilSharma.finalBot | 2044 | 9 | 6380 | 4300-2080 | 37% (g_iter4 905-1519) |
| 10 | hsmalladi.finalbot | 2036 | 41 | 580 | 496-84 | 12% (g_iter1 27-197) |
| 11 | Gymhgy.v10official | 2030 | 12 | 3580 | 2312-1268 | 40% (g_iter4 1054-1570) |
| 12 | **us:g_iter4** | 1956 | 10 | 5480 | 2190-3290 |  |
| 13 | **us:g4crumb** | 1953 | 35 | 400 | 157-243 |  |
| 14 | **us:g4econ2** | 1951 | 56 | 160 | 60-100 |  |
| 15 | **us:g3lost** | 1939 | 112 | 40 | 15-25 |  |
| 16 | **us:g4pick** | 1937 | 65 | 120 | 43-77 |  |
| 17 | **us:g_iter3** | 1912 | 13 | 3520 | 1259-2261 |  |
| 18 | **us:g2cr** | 1912 | 107 | 40 | 21-19 |  |
| 19 | **us:g3escrg2** | 1912 | 44 | 280 | 90-190 |  |
| 20 | winkelmantanner.waffle | 1909 | 15 | 2300 | 1375-925 | 62% (g_iter3 163-101) |
| 21 | **us:g_iter2** | 1868 | 15 | 2200 | 1032-1168 |  |
| 22 | ColtG5.Goob_final | 1798 | 12 | 3540 | 2205-1335 | 67% (g_iter2 203-101) |
| 23 | **us:g_iter1_c2** | 1789 | 130 | 110 | 84-26 |  |
| 24 | **us:a3dig5** | 1789 | 130 | 110 | 84-26 |  |
| 25 | **us:arch_rush10** | 1789 | 130 | 110 | 84-26 |  |
| 26 | kyleezz.jeeryfix3 | 1780 | 29 | 580 | 342-238 | 41% (g_iter1 91-133) |
| 27 | **us:e1aggr** | 1764 | 129 | 110 | 83-27 |  |
| 28 | **us:c6pair** | 1739 | 128 | 110 | 82-28 |  |
| 29 | **us:a3dig10** | 1739 | 128 | 110 | 82-28 |  |
| 30 | **us:c5bank** | 1739 | 128 | 110 | 82-28 |  |
| 31 | quesswho.cretplayer2_3 | 1736 | 29 | 580 | 307-273 | 40% (g_iter1 90-134) |
| 32 | **us:e2aggr** | 1714 | 127 | 110 | 81-29 |  |
| 33 | **us:a2reloc** | 1714 | 127 | 110 | 81-29 |  |
| 34 | **us:a2relay** | 1714 | 127 | 110 | 81-29 |  |
| 35 | **us:b1z2b** | 1700 | 24 | 1760 | 620-1140 |  |
| 36 | **us:g_iter1** | 1693 | 10 | 6990 | 2485-4505 |  |
| 37 | **us:g1copy** | 1687 | 91 | 80 | 28-52 |  |
| 38 | **us:b2fs** | 1686 | 28 | 1280 | 439-841 |  |
| 39 | **us:g1sym** | 1681 | 51 | 200 | 68-132 |  |
| 40 | SampleProvider.TSPAARKSPRINT1 | 1676 | 29 | 580 | 259-321 | 50% (g_iter1 112-112) |
| 41 | **us:b1v2** | 1675 | 22 | 2120 | 711-1409 |  |
| 42 | dmtrung14.defaultplayer_intlqualifier | 1512 | 33 | 580 | 142-438 | 69% (g_iter1 155-69) |
| 43 | clbarrell.duck8 | 1490 | 34 | 580 | 129-451 | 78% (g_iter1 175-49) |
| 44 | **us:arch_rush** | 1367 | 94 | 110 | 62-48 |  |
| 45 | jonters.bling3 | 1343 | 45 | 580 | 64-516 | 89% (g_iter1 200-24) |
| 46 | HugoIngelsson.Bot21 | 1307 | 200 | 26 | 3-23 |  |
| 47 | **us:g_iter0** | 1303 | 89 | 109 | 56-53 |  |
| 48 | Metta-AI.bc24scenario | 1240 | 223 | 26 | 2-24 |  |
| 49 | noahzemlin.honeyducklings | 1240 | 223 | 26 | 2-24 |  |
| 50 | sivakovivan.NewHide | 1240 | 223 | 26 | 2-24 |  |
| 51 | awu7.ExplosiveBot | 1223 | 60 | 580 | 34-546 | 97% (g_iter1 217-7) |
| 52 | JeffLegendPower.v11 | 1152 | 263 | 26 | 1-25 |  |
| 53 | MiloAkerman.v1 | 1152 | 263 | 26 | 1-25 |  |
| 54 | PerishoJ.tx | 1152 | 263 | 26 | 1-25 |  |
| 55 | Peter-Fun.dinoboxer | 1152 | 263 | 26 | 1-25 |  |
| 56 | RyanAspen.v22 | 1152 | 263 | 26 | 1-25 |  |
| 57 | TylerJulian.v9 | 1152 | 263 | 26 | 1-25 |  |
| 58 | aj-chau.cowards | 1152 | 263 | 26 | 1-25 |  |
| 59 | cViper971.ourplayer | 1152 | 263 | 26 | 1-25 |  |
| 60 | dylanzemlin.dangerduck2 | 1152 | 263 | 26 | 1-25 |  |
| 61 | lukerhoads.warrior_2nd_comp | 1152 | 263 | 26 | 1-25 |  |
| 62 | neilhuang007.baseline | 1152 | 263 | 26 | 1-25 |  |
| 63 | polyllc.polyv4 | 1152 | 263 | 26 | 1-25 |  |
| 64 | andrearante12.turtle | 1048 | 357 | 25 | 0-25 |  |
| 65 | AlexYu84.smartPlayer | 1015 | 357 | 26 | 0-26 |  |
| 66 | H4ffliger.keyboardcrusader_v1 | 1015 | 357 | 26 | 0-26 |  |
| 67 | Lithanium.AttackingBot | 1015 | 357 | 26 | 0-26 |  |
| 68 | Rubrasum.version_3 | 1015 | 357 | 26 | 0-26 |  |
| 69 | SriLakshmiPolavarapu.ducks | 1015 | 357 | 26 | 0-26 |  |
| 70 | VarunVejalla.alexander | 1015 | 357 | 26 | 0-26 |  |
| 71 | abdullah8a0.crayBasic | 1015 | 357 | 26 | 0-26 |  |
| 72 | adamseth2.moveBot1 | 1015 | 357 | 26 | 0-26 |  |
| 73 | dylanconklin.Team3 | 1015 | 357 | 26 | 0-26 |  |
| 74 | itswin.MPAttack | 1015 | 357 | 26 | 0-26 |  |
| 75 | joelcrouch.ducks | 1015 | 357 | 26 | 0-26 |  |
| 76 | lcforges.funkyguy3 | 1015 | 357 | 26 | 0-26 |  |
| 77 | qpwoeirut.tournament_sprint1 | 1015 | 357 | 26 | 0-26 |  |
| 78 | reeceyang.v5 | 1015 | 357 | 26 | 0-26 |  |
| 79 | samithShetty.combustiblelemon | 1015 | 357 | 26 | 0-26 |  |
| 80 | sayam-goyal.SimpleBot | 1015 | 357 | 26 | 0-26 |  |
| 81 | tlevietpdx.Sprint2 | 1015 | 357 | 26 | 0-26 |  |
| 82 | justinottesen.sprint1 | 873 | 153 | 580 | 4-576 | 100% (g_iter1 224-0) |
