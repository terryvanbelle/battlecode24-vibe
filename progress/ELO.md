# Ladder

35909 scrimmages (ours only), 35909 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter6 | 1977 +- 40 | 11 of 85 | 480 | 267-213 | 83.4% | 27.3% (vs 10) |
| g_iter5 | 1973 +- 12 | 12 of 85 | 3760 | 1872-1888 | 83.3% | 26.8% (vs 10) |
| g4crumb | 1932 +- 27 | 13 of 85 | 640 | 258-382 | 81.9% | 22.8% (vs 10) |
| g4gym1 | 1932 +- 16 | 14 of 85 | 1880 | 755-1125 | 81.9% | 22.8% (vs 10) |
| g_iter4 | 1929 +- 7 | 15 of 85 | 9040 | 3645-5395 | 81.8% | 22.5% (vs 10) |
| g4econ2 | 1926 +- 56 | 16 of 85 | 160 | 60-100 | 81.7% | 22.2% (vs 10) |
| g3lost | 1914 +- 112 | 17 of 85 | 40 | 15-25 | 81.3% | 21.1% (vs 10) |
| g4pick | 1912 +- 65 | 18 of 85 | 120 | 43-77 | 81.2% | 20.9% (vs 10) |
| g2cr | 1891 +- 107 | 19 of 85 | 40 | 21-19 | 80.5% | 19.1% (vs 10) |
| g_iter3 | 1888 +- 13 | 20 of 85 | 3520 | 1259-2261 | 80.4% | 18.8% (vs 10) |
| g3escrg2 | 1886 +- 44 | 23 of 85 | 280 | 90-190 | 80.3% | 23.9% (vs 12) |
| g_iter2 | 1846 +- 15 | 24 of 85 | 2200 | 1032-1168 | 78.9% | 20.4% (vs 12) |
| g_iter1_c2 | 1756 +- 128 | 26 of 85 | 110 | 84-26 | 75.8% | 16.4% (vs 13) |
| arch_rush10 | 1756 +- 128 | 27 of 85 | 110 | 84-26 | 75.8% | 16.4% (vs 13) |
| a3dig5 | 1756 +- 128 | 28 of 85 | 110 | 84-26 | 75.8% | 16.4% (vs 13) |
| e1aggr | 1732 +- 127 | 30 of 85 | 110 | 83-27 | 74.9% | 17.1% (vs 14) |
| c5bank | 1708 +- 126 | 31 of 85 | 110 | 82-28 | 74.1% | 15.4% (vs 14) |
| a3dig10 | 1708 +- 126 | 32 of 85 | 110 | 82-28 | 74.1% | 15.4% (vs 14) |
| c6pair | 1708 +- 126 | 33 of 85 | 110 | 82-28 | 74.1% | 15.4% (vs 14) |
| a2reloc | 1684 +- 125 | 35 of 85 | 110 | 81-29 | 73.2% | 16.1% (vs 15) |
| e2aggr | 1684 +- 125 | 36 of 85 | 110 | 81-29 | 73.2% | 16.1% (vs 15) |
| a2relay | 1684 +- 125 | 37 of 85 | 110 | 81-29 | 73.2% | 16.1% (vs 15) |
| b1z2b | 1678 +- 24 | 38 of 85 | 1760 | 620-1140 | 73.0% | 15.7% (vs 15) |
| g_iter1 | 1668 +- 10 | 39 of 85 | 6990 | 2485-4505 | 72.6% | 15.1% (vs 15) |
| b2fs | 1663 +- 28 | 40 of 85 | 1280 | 439-841 | 72.4% | 14.8% (vs 15) |
| g1copy | 1661 +- 91 | 41 of 85 | 80 | 28-52 | 72.3% | 14.7% (vs 15) |
| g1sym | 1653 +- 51 | 42 of 85 | 200 | 68-132 | 72.0% | 14.2% (vs 15) |
| b1v2 | 1652 +- 22 | 43 of 85 | 2120 | 711-1409 | 72.0% | 14.1% (vs 15) |
| arch_rush | 1344 +- 94 | 47 of 85 | 110 | 62-48 | 56.7% | 7.1% (vs 18) |
| g_iter0 | 1280 +- 89 | 50 of 85 | 109 | 56-53 | 52.2% | 9.2% (vs 20) |

Our record = OUR win rate (our W-L) against the bot by the incumbent (g_iter6), whatever the count; * marks fewer than 30 games (+- 18 points at 95% for 30 games, +- 20 for 24); a bot the incumbent never met shows the most recent of our builds that did; blank if none has.

| rank | player | rating | +- 95% | games | W-L | our record |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2313 | 64 | 688 | 658-30 | 12% (g_iter6 3-21*) |
| 2 | jmerle.camel_case_v21_final | 2296 | 61 | 688 | 655-33 | 17% (g_iter6 4-20*) |
| 3 | IvanGeffner.kuma | 2274 | 58 | 688 | 651-37 | 21% (g_iter6 5-19*) |
| 4 | chenyx512.flagbot_final | 2274 | 58 | 688 | 651-37 | 4% (g_iter6 1-23*) |
| 5 | andli28.v9_USQuals_angle | 2156 | 45 | 688 | 620-68 | 21% (g_iter6 5-19*) |
| 6 | NotLLeon.v3 | 2147 | 44 | 688 | 617-71 | 25% (g_iter6 6-18*) |
| 7 | andrewgopher.player22 | 2107 | 41 | 688 | 602-86 | 25% (g_iter6 6-18*) |
| 8 | hsmalladi.finalbot | 2051 | 37 | 688 | 577-111 | 25% (g_iter6 6-18*) |
| 9 | CyrilSharma.finalBot | 2018 | 9 | 6488 | 4360-2128 | 50% (g_iter6 12-12*) |
| 10 | Gymhgy.v10official | 2001 | 7 | 11408 | 6857-4551 | 50% (g_iter6 12-12*) |
| 11 | **us:g_iter6** | 1977 | 40 | 480 | 267-213 |  |
| 12 | **us:g_iter5** | 1973 | 12 | 3760 | 1872-1888 |  |
| 13 | **us:g4crumb** | 1932 | 27 | 640 | 258-382 |  |
| 14 | **us:g4gym1** | 1932 | 16 | 1880 | 755-1125 |  |
| 15 | **us:g_iter4** | 1929 | 7 | 9040 | 3645-5395 |  |
| 16 | **us:g4econ2** | 1926 | 56 | 160 | 60-100 |  |
| 17 | **us:g3lost** | 1914 | 112 | 40 | 15-25 |  |
| 18 | **us:g4pick** | 1912 | 65 | 120 | 43-77 |  |
| 19 | **us:g2cr** | 1891 | 107 | 40 | 21-19 |  |
| 20 | **us:g_iter3** | 1888 | 13 | 3520 | 1259-2261 |  |
| 21 | uravt.Version18Final | 1888 | 64 | 126 | 61-65 | 62% (g_iter6 15-9*) |
| 22 | winkelmantanner.waffle | 1888 | 14 | 2408 | 1420-988 | 54% (g_iter6 13-11*) |
| 23 | **us:g3escrg2** | 1886 | 44 | 280 | 90-190 |  |
| 24 | **us:g_iter2** | 1846 | 15 | 2200 | 1032-1168 |  |
| 25 | ColtG5.Goob_final | 1770 | 12 | 3648 | 2217-1431 | 92% (g_iter6 22-2*) |
| 26 | **us:g_iter1_c2** | 1756 | 128 | 110 | 84-26 |  |
| 27 | **us:arch_rush10** | 1756 | 128 | 110 | 84-26 |  |
| 28 | **us:a3dig5** | 1756 | 128 | 110 | 84-26 |  |
| 29 | kyleezz.jeeryfix3 | 1754 | 28 | 688 | 365-323 | 75% (g_iter6 18-6*) |
| 30 | **us:e1aggr** | 1732 | 127 | 110 | 83-27 |  |
| 31 | **us:c5bank** | 1708 | 126 | 110 | 82-28 |  |
| 32 | **us:a3dig10** | 1708 | 126 | 110 | 82-28 |  |
| 33 | **us:c6pair** | 1708 | 126 | 110 | 82-28 |  |
| 34 | quesswho.cretplayer2_3 | 1707 | 27 | 688 | 323-365 | 92% (g_iter6 22-2*) |
| 35 | **us:a2reloc** | 1684 | 125 | 110 | 81-29 |  |
| 36 | **us:e2aggr** | 1684 | 125 | 110 | 81-29 |  |
| 37 | **us:a2relay** | 1684 | 125 | 110 | 81-29 |  |
| 38 | **us:b1z2b** | 1678 | 24 | 1760 | 620-1140 |  |
| 39 | **us:g_iter1** | 1668 | 10 | 6990 | 2485-4505 |  |
| 40 | **us:b2fs** | 1663 | 28 | 1280 | 439-841 |  |
| 41 | **us:g1copy** | 1661 | 91 | 80 | 28-52 |  |
| 42 | **us:g1sym** | 1653 | 51 | 200 | 68-132 |  |
| 43 | **us:b1v2** | 1652 | 22 | 2120 | 711-1409 |  |
| 44 | SampleProvider.TSPAARKSPRINT1 | 1639 | 28 | 688 | 263-425 | 96% (g_iter6 23-1*) |
| 45 | dmtrung14.defaultplayer_intlqualifier | 1483 | 33 | 688 | 145-543 | 100% (g_iter6 24-0*) |
| 46 | clbarrell.duck8 | 1460 | 34 | 688 | 131-557 | 96% (g_iter6 23-1*) |
| 47 | **us:arch_rush** | 1344 | 94 | 110 | 62-48 |  |
| 48 | HugoIngelsson.Bot21 | 1319 | 159 | 86 | 5-81 | 96% (g_iter6 23-1*) |
| 49 | jonters.bling3 | 1314 | 45 | 688 | 65-623 | 100% (g_iter6 24-0*) |
| 50 | **us:g_iter0** | 1280 | 89 | 109 | 56-53 |  |
| 51 | Metta-AI.bc24scenario | 1216 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 52 | noahzemlin.honeyducklings | 1216 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 53 | sivakovivan.NewHide | 1216 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 54 | awu7.ExplosiveBot | 1196 | 60 | 628 | 34-594 | 100% (g_iter5 36-0) |
| 55 | JeffLegendPower.v11 | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 56 | MiloAkerman.v1 | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 57 | PerishoJ.tx | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 58 | Peter-Fun.dinoboxer | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 59 | RyanAspen.v22 | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 60 | TylerJulian.v9 | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 61 | aj-chau.cowards | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 62 | cViper971.ourplayer | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 63 | dylanzemlin.dangerduck2 | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 64 | lukerhoads.warrior_2nd_comp | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 65 | neilhuang007.baseline | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 66 | polyllc.polyv4 | 1129 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 67 | andrearante12.turtle | 1025 | 357 | 25 | 0-25 | 100% (g_iter1 2-0*) |
| 68 | AlexYu84.smartPlayer | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 69 | H4ffliger.keyboardcrusader_v1 | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 70 | Lithanium.AttackingBot | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 71 | Rubrasum.version_3 | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 72 | SriLakshmiPolavarapu.ducks | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 73 | VarunVejalla.alexander | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 74 | abdullah8a0.crayBasic | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 75 | adamseth2.moveBot1 | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 76 | dylanconklin.Team3 | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 77 | itswin.MPAttack | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 78 | joelcrouch.ducks | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 79 | lcforges.funkyguy3 | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 80 | qpwoeirut.tournament_sprint1 | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 81 | reeceyang.v5 | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 82 | samithShetty.combustiblelemon | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 83 | sayam-goyal.SimpleBot | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 84 | tlevietpdx.Sprint2 | 992 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 85 | justinottesen.sprint1 | 846 | 153 | 628 | 4-624 | 100% (g_iter5 36-0) |
