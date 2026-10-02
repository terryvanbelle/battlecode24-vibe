# Ladder

6029 scrimmages (ours only), 6029 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1896 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| a3dig5 | 1896 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| arch_rush10 | 1896 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| e1aggr | 1870 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 16.2% (vs 14) |
| c5bank | 1844 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| a3dig10 | 1844 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c6pair | 1844 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| b1z2b | 1828 +- 43 | 23 of 71 | 520 | 187-333 | 73.3% | 15.6% (vs 15) |
| g_iter1 | 1820 +- 17 | 24 of 71 | 3270 | 1196-2074 | 73.0% | 15.2% (vs 15) |
| e2aggr | 1818 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2reloc | 1818 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1818 +- 130 | 27 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1v2 | 1788 +- 34 | 29 of 71 | 880 | 292-588 | 71.9% | 15.3% (vs 16) |
| b2fs | 1755 +- 156 | 30 of 71 | 40 | 13-27 | 70.7% | 13.3% (vs 16) |
| arch_rush | 1456 +- 95 | 33 of 71 | 110 | 62-48 | 56.5% | 5.7% (vs 18) |
| g_iter0 | 1391 +- 89 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2497 | 154 | 256 | 252-4 | 2% (g_iter1 4-156) |
| 2 | jmerle.camel_case_v21_final | 2413 | 122 | 256 | 249-7 | 4% (g_iter1 6-154) |
| 3 | uravt.Version18Final | 2388 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2340 | 101 | 256 | 245-11 | 4% (g_iter1 7-153) |
| 5 | chenyx512.flagbot_final | 2325 | 97 | 256 | 244-12 | 5% (g_iter1 8-152) |
| 6 | andli28.v9_USQuals_angle | 2299 | 91 | 256 | 242-14 | 6% (g_iter1 9-151) |
| 7 | NotLLeon.v3 | 2287 | 88 | 256 | 241-15 | 7% (g_iter1 11-149) |
| 8 | andrewgopher.player22 | 2218 | 74 | 256 | 234-22 | 10% (g_iter1 16-144) |
| 9 | Gymhgy.v10official | 2202 | 72 | 256 | 232-24 | 13% (g_iter1 21-139) |
| 10 | hsmalladi.finalbot | 2104 | 58 | 256 | 216-40 | 12% (g_iter1 19-141) |
| 11 | CyrilSharma.finalBot | 2084 | 56 | 256 | 212-44 | 21% (g_iter1 34-126) |
| 12 | winkelmantanner.waffle | 2053 | 53 | 256 | 205-51 | 22% (g_iter1 35-125) |
| 13 | ColtG5.Goob_final | 1921 | 45 | 256 | 167-89 | 38% (g_iter1 61-99) |
| 14 | **us:g_iter1_c2** | 1896 | 132 | 110 | 84-26 |  |
| 15 | **us:a3dig5** | 1896 | 132 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1896 | 132 | 110 | 84-26 |  |
| 17 | quesswho.cretplayer2_3 | 1872 | 43 | 256 | 150-106 | 36% (g_iter1 58-102) |
| 18 | **us:e1aggr** | 1870 | 132 | 110 | 83-27 |  |
| 19 | kyleezz.jeeryfix3 | 1866 | 43 | 256 | 148-108 | 42% (g_iter1 68-92) |
| 20 | **us:c5bank** | 1844 | 131 | 110 | 82-28 |  |
| 21 | **us:a3dig10** | 1844 | 131 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1844 | 131 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1828 | 43 | 520 | 187-333 |  |
| 24 | **us:g_iter1** | 1820 | 17 | 3270 | 1196-2074 |  |
| 25 | **us:e2aggr** | 1818 | 130 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1818 | 130 | 110 | 81-29 |  |
| 27 | **us:a2relay** | 1818 | 130 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1811 | 43 | 256 | 128-128 | 49% (g_iter1 79-81) |
| 29 | **us:b1v2** | 1788 | 34 | 880 | 292-588 |  |
| 30 | **us:b2fs** | 1755 | 156 | 40 | 13-27 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1666 | 47 | 256 | 78-178 | 68% (g_iter1 108-52) |
| 32 | clbarrell.duck8 | 1603 | 51 | 256 | 60-196 | 78% (g_iter1 125-35) |
| 33 | **us:arch_rush** | 1456 | 95 | 110 | 62-48 |  |
| 34 | jonters.bling3 | 1455 | 66 | 256 | 30-226 | 90% (g_iter1 144-16) |
| 35 | HugoIngelsson.Bot21 | 1392 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1391 | 89 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1325 | 89 | 256 | 15-241 | 97% (g_iter1 155-5) |
| 38 | Metta-AI.bc24scenario | 1324 | 226 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1324 | 226 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1324 | 226 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1235 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1235 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1235 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1235 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1235 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1235 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1235 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1235 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1235 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1235 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1235 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1235 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1129 | 358 | 25 | 0-25 |  |
| 54 | justinottesen.sprint1 | 1110 | 155 | 256 | 4-252 | 100% (g_iter1 160-0) |
| 55 | AlexYu84.smartPlayer | 1097 | 358 | 26 | 0-26 |  |
| 56 | H4ffliger.keyboardcrusader_v1 | 1097 | 358 | 26 | 0-26 |  |
| 57 | Lithanium.AttackingBot | 1097 | 358 | 26 | 0-26 |  |
| 58 | Rubrasum.version_3 | 1097 | 358 | 26 | 0-26 |  |
| 59 | SriLakshmiPolavarapu.ducks | 1097 | 358 | 26 | 0-26 |  |
| 60 | VarunVejalla.alexander | 1097 | 358 | 26 | 0-26 |  |
| 61 | abdullah8a0.crayBasic | 1097 | 358 | 26 | 0-26 |  |
| 62 | adamseth2.moveBot1 | 1097 | 358 | 26 | 0-26 |  |
| 63 | dylanconklin.Team3 | 1097 | 358 | 26 | 0-26 |  |
| 64 | itswin.MPAttack | 1097 | 358 | 26 | 0-26 |  |
| 65 | joelcrouch.ducks | 1097 | 358 | 26 | 0-26 |  |
| 66 | lcforges.funkyguy3 | 1097 | 358 | 26 | 0-26 |  |
| 67 | qpwoeirut.tournament_sprint1 | 1097 | 358 | 26 | 0-26 |  |
| 68 | reeceyang.v5 | 1097 | 358 | 26 | 0-26 |  |
| 69 | samithShetty.combustiblelemon | 1097 | 358 | 26 | 0-26 |  |
| 70 | sayam-goyal.SimpleBot | 1097 | 358 | 26 | 0-26 |  |
| 71 | tlevietpdx.Sprint2 | 1097 | 358 | 26 | 0-26 |  |
