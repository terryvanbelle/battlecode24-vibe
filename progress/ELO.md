# Ladder

4709 scrimmages (ours only), 4709 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1904 +- 132 | 15 of 70 | 110 | 84-26 | 75.6% | 18.1% (vs 14) |
| g_iter1_c2 | 1904 +- 132 | 16 of 70 | 110 | 84-26 | 75.6% | 18.1% (vs 14) |
| arch_rush10 | 1904 +- 132 | 17 of 70 | 110 | 84-26 | 75.6% | 18.1% (vs 14) |
| e1aggr | 1878 +- 131 | 19 of 70 | 110 | 83-27 | 74.7% | 18.5% (vs 15) |
| c6pair | 1853 +- 130 | 20 of 70 | 110 | 82-28 | 73.8% | 16.7% (vs 15) |
| a3dig10 | 1853 +- 130 | 21 of 70 | 110 | 82-28 | 73.8% | 16.7% (vs 15) |
| c5bank | 1853 +- 130 | 22 of 70 | 110 | 82-28 | 73.8% | 16.7% (vs 15) |
| g_iter1 | 1830 +- 19 | 23 of 70 | 2830 | 1040-1790 | 73.1% | 15.2% (vs 15) |
| e2aggr | 1827 +- 129 | 24 of 70 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2reloc | 1827 +- 129 | 25 of 70 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1827 +- 129 | 26 of 70 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1z2b | 1794 +- 90 | 28 of 70 | 120 | 40-80 | 71.8% | 15.0% (vs 16) |
| b1v2 | 1791 +- 48 | 29 of 70 | 440 | 144-296 | 71.7% | 14.9% (vs 16) |
| arch_rush | 1465 +- 95 | 32 of 70 | 110 | 62-48 | 56.5% | 5.6% (vs 18) |
| g_iter0 | 1399 +- 89 | 35 of 70 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2453 | 154 | 190 | 186-4 | 3% (g_iter1 4-134) |
| 2 | uravt.Version18Final | 2397 | 348 | 26 | 26-0 |  |
| 3 | andli28.v9_USQuals_angle | 2393 | 131 | 190 | 184-6 | 4% (g_iter1 6-132) |
| 4 | jmerle.camel_case_v21_final | 2369 | 123 | 190 | 183-7 | 4% (g_iter1 6-132) |
| 5 | IvanGeffner.kuma | 2347 | 116 | 190 | 182-8 | 4% (g_iter1 6-132) |
| 6 | chenyx512.flagbot_final | 2347 | 116 | 190 | 182-8 | 4% (g_iter1 6-132) |
| 7 | NotLLeon.v3 | 2252 | 92 | 190 | 176-14 | 7% (g_iter1 10-128) |
| 8 | andrewgopher.player22 | 2229 | 87 | 190 | 174-16 | 10% (g_iter1 14-124) |
| 9 | Gymhgy.v10official | 2197 | 81 | 190 | 171-19 | 14% (g_iter1 19-119) |
| 10 | hsmalladi.finalbot | 2116 | 68 | 190 | 161-29 | 12% (g_iter1 17-121) |
| 11 | CyrilSharma.finalBot | 2072 | 63 | 190 | 154-36 | 22% (g_iter1 31-107) |
| 12 | winkelmantanner.waffle | 2044 | 60 | 190 | 149-41 | 24% (g_iter1 33-105) |
| 13 | ColtG5.Goob_final | 1925 | 52 | 190 | 123-67 | 38% (g_iter1 53-85) |
| 14 | quesswho.cretplayer2_3 | 1917 | 51 | 190 | 121-69 | 35% (g_iter1 48-90) |
| 15 | **us:a3dig5** | 1904 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1904 | 132 | 110 | 84-26 |  |
| 17 | **us:arch_rush10** | 1904 | 132 | 110 | 84-26 |  |
| 18 | kyleezz.jeeryfix3 | 1879 | 50 | 190 | 111-79 | 43% (g_iter1 59-79) |
| 19 | **us:e1aggr** | 1878 | 131 | 110 | 83-27 |  |
| 20 | **us:c6pair** | 1853 | 130 | 110 | 82-28 |  |
| 21 | **us:a3dig10** | 1853 | 130 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1853 | 130 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1830 | 19 | 2830 | 1040-1790 |  |
| 24 | **us:e2aggr** | 1827 | 129 | 110 | 81-29 |  |
| 25 | **us:a2reloc** | 1827 | 129 | 110 | 81-29 |  |
| 26 | **us:a2relay** | 1827 | 129 | 110 | 81-29 |  |
| 27 | SampleProvider.TSPAARKSPRINT1 | 1826 | 50 | 190 | 97-93 | 49% (g_iter1 67-71) |
| 28 | **us:b1z2b** | 1794 | 90 | 120 | 40-80 |  |
| 29 | **us:b1v2** | 1791 | 48 | 440 | 144-296 |  |
| 30 | dmtrung14.defaultplayer_intlqualifier | 1682 | 54 | 190 | 60-130 | 65% (g_iter1 90-48) |
| 31 | clbarrell.duck8 | 1612 | 59 | 190 | 45-145 | 78% (g_iter1 107-31) |
| 32 | **us:arch_rush** | 1465 | 95 | 110 | 62-48 |  |
| 33 | jonters.bling3 | 1450 | 79 | 190 | 21-169 | 91% (g_iter1 125-13) |
| 34 | HugoIngelsson.Bot21 | 1399 | 203 | 26 | 3-23 |  |
| 35 | **us:g_iter0** | 1399 | 89 | 109 | 56-53 |  |
| 36 | awu7.ExplosiveBot | 1345 | 100 | 190 | 12-178 | 96% (g_iter1 133-5) |
| 37 | Metta-AI.bc24scenario | 1331 | 226 | 26 | 2-24 |  |
| 38 | noahzemlin.honeyducklings | 1331 | 226 | 26 | 2-24 |  |
| 39 | sivakovivan.NewHide | 1331 | 226 | 26 | 2-24 |  |
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
| 52 | justinottesen.sprint1 | 1165 | 156 | 190 | 4-186 | 100% (g_iter1 138-0) |
| 53 | andrearante12.turtle | 1136 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1104 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1104 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1104 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1104 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1104 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1104 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1104 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1104 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1104 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1104 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1104 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1104 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1104 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1104 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1104 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1104 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1104 | 358 | 26 | 0-26 |  |
