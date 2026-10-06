# Ladder

37349 scrimmages (ours only), 37349 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter6 | 1999 +- 21 | 11 of 85 | 1560 | 863-697 | 84.0% | 28.8% (vs 10) |
| g_iter5 | 1973 +- 12 | 12 of 85 | 4120 | 2033-2087 | 83.2% | 26.1% (vs 10) |
| g4crumb | 1932 +- 27 | 13 of 85 | 640 | 258-382 | 81.8% | 22.2% (vs 10) |
| g4gym1 | 1932 +- 16 | 14 of 85 | 1880 | 755-1125 | 81.8% | 22.1% (vs 10) |
| g_iter4 | 1929 +- 7 | 15 of 85 | 9040 | 3645-5395 | 81.7% | 21.9% (vs 10) |
| g4econ2 | 1926 +- 56 | 16 of 85 | 160 | 60-100 | 81.6% | 21.6% (vs 10) |
| g3lost | 1914 +- 112 | 17 of 85 | 40 | 15-25 | 81.2% | 20.5% (vs 10) |
| g4pick | 1912 +- 65 | 18 of 85 | 120 | 43-77 | 81.1% | 20.3% (vs 10) |
| g2cr | 1891 +- 107 | 19 of 85 | 40 | 21-19 | 80.4% | 18.5% (vs 10) |
| g_iter3 | 1888 +- 13 | 20 of 85 | 3520 | 1259-2261 | 80.3% | 18.3% (vs 10) |
| g3escrg2 | 1886 +- 44 | 22 of 85 | 280 | 90-190 | 80.2% | 21.1% (vs 11) |
| g_iter2 | 1846 +- 15 | 24 of 85 | 2200 | 1032-1168 | 78.9% | 20.0% (vs 12) |
| arch_rush10 | 1757 +- 129 | 26 of 85 | 110 | 84-26 | 75.8% | 16.3% (vs 13) |
| g_iter1_c2 | 1757 +- 129 | 27 of 85 | 110 | 84-26 | 75.8% | 16.3% (vs 13) |
| a3dig5 | 1757 +- 129 | 28 of 85 | 110 | 84-26 | 75.8% | 16.3% (vs 13) |
| e1aggr | 1733 +- 128 | 30 of 85 | 110 | 83-27 | 74.9% | 16.9% (vs 14) |
| c5bank | 1708 +- 127 | 32 of 85 | 110 | 82-28 | 74.1% | 17.6% (vs 15) |
| a3dig10 | 1708 +- 127 | 33 of 85 | 110 | 82-28 | 74.1% | 17.6% (vs 15) |
| c6pair | 1708 +- 127 | 34 of 85 | 110 | 82-28 | 74.1% | 17.6% (vs 15) |
| a2relay | 1685 +- 125 | 35 of 85 | 110 | 81-29 | 73.2% | 16.0% (vs 15) |
| a2reloc | 1685 +- 125 | 36 of 85 | 110 | 81-29 | 73.2% | 16.0% (vs 15) |
| e2aggr | 1685 +- 125 | 37 of 85 | 110 | 81-29 | 73.2% | 16.0% (vs 15) |
| b1z2b | 1680 +- 24 | 38 of 85 | 1760 | 620-1140 | 73.0% | 15.7% (vs 15) |
| g_iter1 | 1668 +- 10 | 39 of 85 | 6990 | 2485-4505 | 72.6% | 15.0% (vs 15) |
| b2fs | 1665 +- 28 | 40 of 85 | 1280 | 439-841 | 72.5% | 14.8% (vs 15) |
| g1copy | 1661 +- 91 | 41 of 85 | 80 | 28-52 | 72.3% | 14.5% (vs 15) |
| b1v2 | 1654 +- 22 | 42 of 85 | 2120 | 711-1409 | 72.0% | 14.1% (vs 15) |
| g1sym | 1653 +- 51 | 43 of 85 | 200 | 68-132 | 72.0% | 14.0% (vs 15) |
| arch_rush | 1343 +- 94 | 47 of 85 | 110 | 62-48 | 56.7% | 7.0% (vs 18) |
| g_iter0 | 1279 +- 89 | 50 of 85 | 109 | 56-53 | 52.2% | 9.3% (vs 20) |

Our record = OUR win rate (our W-L) against the bot by the incumbent (g_iter6), whatever the count; * marks fewer than 30 games (+- 18 points at 95% for 30 games, +- 20 for 24); a bot the incumbent never met shows the most recent of our builds that did; blank if none has.

| rank | player | rating | +- 95% | games | W-L | our record |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2321 | 60 | 724 | 690-34 | 12% (g_iter6 7-53) |
| 2 | jmerle.camel_case_v21_final | 2310 | 59 | 724 | 688-36 | 12% (g_iter6 7-53) |
| 3 | chenyx512.flagbot_final | 2286 | 55 | 724 | 683-41 | 8% (g_iter6 5-55) |
| 4 | IvanGeffner.kuma | 2281 | 55 | 724 | 682-42 | 17% (g_iter6 10-50) |
| 5 | andli28.v9_USQuals_angle | 2159 | 42 | 724 | 646-78 | 25% (g_iter6 15-45) |
| 6 | NotLLeon.v3 | 2149 | 41 | 724 | 642-82 | 28% (g_iter6 17-43) |
| 7 | andrewgopher.player22 | 2115 | 39 | 724 | 628-96 | 27% (g_iter6 16-44) |
| 8 | hsmalladi.finalbot | 2073 | 36 | 724 | 608-116 | 18% (g_iter6 11-49) |
| 9 | CyrilSharma.finalBot | 2018 | 9 | 6524 | 4376-2148 | 53% (g_iter6 32-28) |
| 10 | Gymhgy.v10official | 2001 | 6 | 12164 | 7248-4916 | 51% (g_iter6 216-204) |
| 11 | **us:g_iter6** | 1999 | 21 | 1560 | 863-697 |  |
| 12 | **us:g_iter5** | 1973 | 12 | 4120 | 2033-2087 |  |
| 13 | **us:g4crumb** | 1932 | 27 | 640 | 258-382 |  |
| 14 | **us:g4gym1** | 1932 | 16 | 1880 | 755-1125 |  |
| 15 | **us:g_iter4** | 1929 | 7 | 9040 | 3645-5395 |  |
| 16 | **us:g4econ2** | 1926 | 56 | 160 | 60-100 |  |
| 17 | **us:g3lost** | 1914 | 112 | 40 | 15-25 |  |
| 18 | **us:g4pick** | 1912 | 65 | 120 | 43-77 |  |
| 19 | **us:g2cr** | 1891 | 107 | 40 | 21-19 |  |
| 20 | **us:g_iter3** | 1888 | 13 | 3520 | 1259-2261 |  |
| 21 | winkelmantanner.waffle | 1887 | 14 | 2444 | 1430-1014 | 65% (g_iter6 39-21) |
| 22 | **us:g3escrg2** | 1886 | 44 | 280 | 90-190 |  |
| 23 | uravt.Version18Final | 1884 | 56 | 162 | 72-90 | 67% (g_iter6 40-20) |
| 24 | **us:g_iter2** | 1846 | 15 | 2200 | 1032-1168 |  |
| 25 | ColtG5.Goob_final | 1770 | 12 | 3684 | 2219-1465 | 93% (g_iter6 56-4) |
| 26 | **us:arch_rush10** | 1757 | 129 | 110 | 84-26 |  |
| 27 | **us:g_iter1_c2** | 1757 | 129 | 110 | 84-26 |  |
| 28 | **us:a3dig5** | 1757 | 129 | 110 | 84-26 |  |
| 29 | kyleezz.jeeryfix3 | 1754 | 27 | 724 | 371-353 | 80% (g_iter6 48-12) |
| 30 | **us:e1aggr** | 1733 | 128 | 110 | 83-27 |  |
| 31 | quesswho.cretplayer2_3 | 1710 | 27 | 724 | 330-394 | 85% (g_iter6 51-9) |
| 32 | **us:c5bank** | 1708 | 127 | 110 | 82-28 |  |
| 33 | **us:a3dig10** | 1708 | 127 | 110 | 82-28 |  |
| 34 | **us:c6pair** | 1708 | 127 | 110 | 82-28 |  |
| 35 | **us:a2relay** | 1685 | 125 | 110 | 81-29 |  |
| 36 | **us:a2reloc** | 1685 | 125 | 110 | 81-29 |  |
| 37 | **us:e2aggr** | 1685 | 125 | 110 | 81-29 |  |
| 38 | **us:b1z2b** | 1680 | 24 | 1760 | 620-1140 |  |
| 39 | **us:g_iter1** | 1668 | 10 | 6990 | 2485-4505 |  |
| 40 | **us:b2fs** | 1665 | 28 | 1280 | 439-841 |  |
| 41 | **us:g1copy** | 1661 | 91 | 80 | 28-52 |  |
| 42 | **us:b1v2** | 1654 | 22 | 2120 | 711-1409 |  |
| 43 | **us:g1sym** | 1653 | 51 | 200 | 68-132 |  |
| 44 | SampleProvider.TSPAARKSPRINT1 | 1637 | 28 | 724 | 264-460 | 97% (g_iter6 58-2) |
| 45 | dmtrung14.defaultplayer_intlqualifier | 1484 | 32 | 724 | 147-577 | 97% (g_iter6 58-2) |
| 46 | clbarrell.duck8 | 1460 | 34 | 724 | 132-592 | 97% (g_iter6 58-2) |
| 47 | **us:arch_rush** | 1343 | 94 | 110 | 62-48 |  |
| 48 | jonters.bling3 | 1313 | 45 | 724 | 65-659 | 100% (g_iter6 60-0) |
| 49 | HugoIngelsson.Bot21 | 1297 | 156 | 122 | 5-117 | 98% (g_iter6 59-1) |
| 50 | **us:g_iter0** | 1279 | 89 | 109 | 56-53 |  |
| 51 | Metta-AI.bc24scenario | 1216 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 52 | noahzemlin.honeyducklings | 1216 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 53 | sivakovivan.NewHide | 1216 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 54 | awu7.ExplosiveBot | 1197 | 60 | 628 | 34-594 | 100% (g_iter5 36-0) |
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
| 68 | AlexYu84.smartPlayer | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 69 | H4ffliger.keyboardcrusader_v1 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 70 | Lithanium.AttackingBot | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 71 | Rubrasum.version_3 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 72 | SriLakshmiPolavarapu.ducks | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 73 | VarunVejalla.alexander | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 74 | abdullah8a0.crayBasic | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 75 | adamseth2.moveBot1 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 76 | dylanconklin.Team3 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 77 | itswin.MPAttack | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 78 | joelcrouch.ducks | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 79 | lcforges.funkyguy3 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 80 | qpwoeirut.tournament_sprint1 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 81 | reeceyang.v5 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 82 | samithShetty.combustiblelemon | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 83 | sayam-goyal.SimpleBot | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 84 | tlevietpdx.Sprint2 | 991 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 85 | justinottesen.sprint1 | 847 | 153 | 628 | 4-624 | 100% (g_iter5 36-0) |
