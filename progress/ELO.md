# Ladder

34949 scrimmages (ours only), 34949 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter5 | 1982 +- 13 | 11 of 84 | 3280 | 1649-1631 | 83.3% | 27.0% (vs 10) |
| g4crumb | 1943 +- 27 | 12 of 84 | 640 | 258-382 | 82.0% | 23.0% (vs 10) |
| g4gym1 | 1943 +- 16 | 13 of 84 | 1880 | 755-1125 | 81.9% | 23.0% (vs 10) |
| g_iter4 | 1940 +- 7 | 14 of 84 | 9040 | 3645-5395 | 81.8% | 22.8% (vs 10) |
| g4econ2 | 1937 +- 56 | 15 of 84 | 160 | 60-100 | 81.7% | 22.4% (vs 10) |
| g3lost | 1925 +- 112 | 16 of 84 | 40 | 15-25 | 81.3% | 21.4% (vs 10) |
| g4pick | 1923 +- 65 | 17 of 84 | 120 | 43-77 | 81.3% | 21.2% (vs 10) |
| g2cr | 1900 +- 107 | 18 of 84 | 40 | 21-19 | 80.5% | 19.2% (vs 10) |
| g_iter3 | 1898 +- 13 | 19 of 84 | 3520 | 1259-2261 | 80.4% | 19.1% (vs 10) |
| g3escrg2 | 1897 +- 44 | 21 of 84 | 280 | 90-190 | 80.4% | 21.8% (vs 11) |
| g_iter2 | 1856 +- 15 | 23 of 84 | 2200 | 1032-1168 | 79.0% | 20.5% (vs 12) |
| a3dig5 | 1765 +- 128 | 25 of 84 | 110 | 84-26 | 75.8% | 16.4% (vs 13) |
| g_iter1_c2 | 1765 +- 128 | 26 of 84 | 110 | 84-26 | 75.8% | 16.4% (vs 13) |
| arch_rush10 | 1765 +- 128 | 27 of 84 | 110 | 84-26 | 75.8% | 16.4% (vs 13) |
| e1aggr | 1741 +- 127 | 29 of 84 | 110 | 83-27 | 74.9% | 17.0% (vs 14) |
| c5bank | 1716 +- 126 | 31 of 84 | 110 | 82-28 | 74.0% | 17.7% (vs 15) |
| c6pair | 1716 +- 126 | 32 of 84 | 110 | 82-28 | 74.0% | 17.7% (vs 15) |
| a3dig10 | 1716 +- 126 | 33 of 84 | 110 | 82-28 | 74.0% | 17.7% (vs 15) |
| a2relay | 1693 +- 125 | 34 of 84 | 110 | 81-29 | 73.2% | 16.0% (vs 15) |
| a2reloc | 1693 +- 125 | 35 of 84 | 110 | 81-29 | 73.2% | 16.0% (vs 15) |
| e2aggr | 1693 +- 125 | 36 of 84 | 110 | 81-29 | 73.2% | 16.0% (vs 15) |
| b1z2b | 1689 +- 24 | 37 of 84 | 1760 | 620-1140 | 73.0% | 15.8% (vs 15) |
| g_iter1 | 1679 +- 10 | 38 of 84 | 6990 | 2485-4505 | 72.7% | 15.2% (vs 15) |
| b2fs | 1674 +- 28 | 39 of 84 | 1280 | 439-841 | 72.5% | 14.9% (vs 15) |
| g1copy | 1672 +- 91 | 40 of 84 | 80 | 28-52 | 72.4% | 14.7% (vs 15) |
| g1sym | 1665 +- 51 | 41 of 84 | 200 | 68-132 | 72.2% | 14.3% (vs 15) |
| b1v2 | 1663 +- 22 | 42 of 84 | 2120 | 711-1409 | 72.1% | 14.2% (vs 15) |
| arch_rush | 1351 +- 94 | 46 of 84 | 110 | 62-48 | 56.7% | 6.9% (vs 18) |
| g_iter0 | 1287 +- 89 | 49 of 84 | 109 | 56-53 | 52.1% | 9.2% (vs 20) |

Our record = OUR win rate (our W-L) against the bot by the incumbent (g_iter5), whatever the count; * marks fewer than 30 games (+- 18 points at 95% for 30 games, +- 20 for 24); a bot the incumbent never met shows the most recent of our builds that did; blank if none has.

| rank | player | rating | +- 95% | games | W-L | our record |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2323 | 67 | 664 | 637-27 | 8% (g_iter5 6-66) |
| 2 | jmerle.camel_case_v21_final | 2310 | 65 | 664 | 635-29 | 12% (g_iter5 9-63) |
| 3 | IvanGeffner.kuma | 2292 | 62 | 664 | 632-32 | 14% (g_iter5 10-62) |
| 4 | chenyx512.flagbot_final | 2270 | 59 | 664 | 628-36 | 7% (g_iter5 5-67) |
| 5 | andli28.v9_USQuals_angle | 2162 | 46 | 664 | 601-63 | 19% (g_iter5 14-58) |
| 6 | NotLLeon.v3 | 2156 | 46 | 664 | 599-65 | 26% (g_iter5 19-53) |
| 7 | andrewgopher.player22 | 2113 | 42 | 664 | 584-80 | 19% (g_iter5 14-58) |
| 8 | hsmalladi.finalbot | 2054 | 38 | 664 | 559-105 | 28% (g_iter5 20-52) |
| 9 | CyrilSharma.finalBot | 2029 | 9 | 6464 | 4348-2116 | 42% (g_iter5 30-42) |
| 10 | Gymhgy.v10official | 2012 | 7 | 10904 | 6588-4316 | 46% (g_iter5 886-1026) |
| 11 | **us:g_iter5** | 1982 | 13 | 3280 | 1649-1631 |  |
| 12 | **us:g4crumb** | 1943 | 27 | 640 | 258-382 |  |
| 13 | **us:g4gym1** | 1943 | 16 | 1880 | 755-1125 |  |
| 14 | **us:g_iter4** | 1940 | 7 | 9040 | 3645-5395 |  |
| 15 | **us:g4econ2** | 1937 | 56 | 160 | 60-100 |  |
| 16 | **us:g3lost** | 1925 | 112 | 40 | 15-25 |  |
| 17 | **us:g4pick** | 1923 | 65 | 120 | 43-77 |  |
| 18 | **us:g2cr** | 1900 | 107 | 40 | 21-19 |  |
| 19 | **us:g_iter3** | 1898 | 13 | 3520 | 1259-2261 |  |
| 20 | uravt.Version18Final | 1897 | 71 | 102 | 52-50 | 67% (g_iter5 24-12) |
| 21 | **us:g3escrg2** | 1897 | 44 | 280 | 90-190 |  |
| 22 | winkelmantanner.waffle | 1897 | 15 | 2384 | 1409-975 | 64% (g_iter5 46-26) |
| 23 | **us:g_iter2** | 1856 | 15 | 2200 | 1032-1168 |  |
| 24 | ColtG5.Goob_final | 1782 | 12 | 3624 | 2215-1409 | 88% (g_iter5 63-9) |
| 25 | **us:a3dig5** | 1765 | 128 | 110 | 84-26 |  |
| 26 | **us:g_iter1_c2** | 1765 | 128 | 110 | 84-26 |  |
| 27 | **us:arch_rush10** | 1765 | 128 | 110 | 84-26 |  |
| 28 | kyleezz.jeeryfix3 | 1764 | 28 | 664 | 359-305 | 78% (g_iter5 56-16) |
| 29 | **us:e1aggr** | 1741 | 127 | 110 | 83-27 |  |
| 30 | quesswho.cretplayer2_3 | 1720 | 28 | 664 | 321-343 | 82% (g_iter5 59-13) |
| 31 | **us:c5bank** | 1716 | 126 | 110 | 82-28 |  |
| 32 | **us:c6pair** | 1716 | 126 | 110 | 82-28 |  |
| 33 | **us:a3dig10** | 1716 | 126 | 110 | 82-28 |  |
| 34 | **us:a2relay** | 1693 | 125 | 110 | 81-29 |  |
| 35 | **us:a2reloc** | 1693 | 125 | 110 | 81-29 |  |
| 36 | **us:e2aggr** | 1693 | 125 | 110 | 81-29 |  |
| 37 | **us:b1z2b** | 1689 | 24 | 1760 | 620-1140 |  |
| 38 | **us:g_iter1** | 1679 | 10 | 6990 | 2485-4505 |  |
| 39 | **us:b2fs** | 1674 | 28 | 1280 | 439-841 |  |
| 40 | **us:g1copy** | 1672 | 91 | 80 | 28-52 |  |
| 41 | **us:g1sym** | 1665 | 51 | 200 | 68-132 |  |
| 42 | **us:b1v2** | 1663 | 22 | 2120 | 711-1409 |  |
| 43 | SampleProvider.TSPAARKSPRINT1 | 1652 | 28 | 664 | 262-402 | 96% (g_iter5 69-3) |
| 44 | dmtrung14.defaultplayer_intlqualifier | 1496 | 33 | 664 | 145-519 | 97% (g_iter5 70-2) |
| 45 | clbarrell.duck8 | 1471 | 34 | 664 | 130-534 | 99% (g_iter5 71-1) |
| 46 | **us:arch_rush** | 1351 | 94 | 110 | 62-48 |  |
| 47 | jonters.bling3 | 1327 | 45 | 664 | 65-599 | 99% (g_iter5 71-1) |
| 48 | HugoIngelsson.Bot21 | 1306 | 175 | 62 | 4-58 | 97% (g_iter5 35-1) |
| 49 | **us:g_iter0** | 1287 | 89 | 109 | 56-53 |  |
| 50 | Metta-AI.bc24scenario | 1223 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 51 | noahzemlin.honeyducklings | 1223 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 52 | sivakovivan.NewHide | 1223 | 223 | 26 | 2-24 | 100% (g_iter1 2-0*) |
| 53 | awu7.ExplosiveBot | 1207 | 60 | 628 | 34-594 | 100% (g_iter5 36-0) |
| 54 | JeffLegendPower.v11 | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 55 | MiloAkerman.v1 | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 56 | PerishoJ.tx | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 57 | Peter-Fun.dinoboxer | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 58 | RyanAspen.v22 | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 59 | TylerJulian.v9 | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 60 | aj-chau.cowards | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 61 | cViper971.ourplayer | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 62 | dylanzemlin.dangerduck2 | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 63 | lukerhoads.warrior_2nd_comp | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 64 | neilhuang007.baseline | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 65 | polyllc.polyv4 | 1136 | 263 | 26 | 1-25 | 100% (g_iter1 2-0*) |
| 66 | andrearante12.turtle | 1032 | 357 | 25 | 0-25 | 100% (g_iter1 2-0*) |
| 67 | AlexYu84.smartPlayer | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 68 | H4ffliger.keyboardcrusader_v1 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 69 | Lithanium.AttackingBot | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 70 | Rubrasum.version_3 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 71 | SriLakshmiPolavarapu.ducks | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 72 | VarunVejalla.alexander | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 73 | abdullah8a0.crayBasic | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 74 | adamseth2.moveBot1 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 75 | dylanconklin.Team3 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 76 | itswin.MPAttack | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 77 | joelcrouch.ducks | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 78 | lcforges.funkyguy3 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 79 | qpwoeirut.tournament_sprint1 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 80 | reeceyang.v5 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 81 | samithShetty.combustiblelemon | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 82 | sayam-goyal.SimpleBot | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 83 | tlevietpdx.Sprint2 | 999 | 357 | 26 | 0-26 | 100% (g_iter1 2-0*) |
| 84 | justinottesen.sprint1 | 857 | 153 | 628 | 4-624 | 100% (g_iter5 36-0) |
