# Ladder

8749 scrimmages (ours only), 8749 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1889 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| arch_rush10 | 1889 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| g_iter1_c2 | 1889 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.3% (vs 13) |
| e1aggr | 1863 +- 131 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| c5bank | 1838 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| a3dig10 | 1838 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c6pair | 1838 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| g_iter1 | 1815 +- 16 | 23 of 71 | 3950 | 1441-2509 | 73.1% | 15.2% (vs 15) |
| a2reloc | 1812 +- 130 | 24 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2relay | 1812 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| e2aggr | 1812 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1z2b | 1811 +- 28 | 27 of 71 | 1200 | 422-778 | 72.9% | 14.9% (vs 15) |
| b1v2 | 1782 +- 25 | 29 of 71 | 1560 | 518-1042 | 71.9% | 15.3% (vs 16) |
| b2fs | 1775 +- 37 | 30 of 71 | 720 | 236-484 | 71.7% | 14.9% (vs 16) |
| arch_rush | 1452 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1386 +- 90 | 36 of 71 | 109 | 56-53 | 51.9% | 8.2% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2405 | 100 | 392 | 381-11 | 4% (g_iter1 7-187) |
| 2 | jmerle.camel_case_v21_final | 2391 | 96 | 392 | 380-12 | 5% (g_iter1 10-184) |
| 3 | uravt.Version18Final | 2382 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2365 | 90 | 392 | 378-14 | 4% (g_iter1 8-186) |
| 5 | NotLLeon.v3 | 2279 | 72 | 392 | 369-23 | 7% (g_iter1 13-181) |
| 6 | andli28.v9_USQuals_angle | 2265 | 69 | 392 | 367-25 | 6% (g_iter1 11-183) |
| 7 | chenyx512.flagbot_final | 2238 | 65 | 392 | 363-29 | 6% (g_iter1 12-182) |
| 8 | andrewgopher.player22 | 2226 | 63 | 392 | 361-31 | 10% (g_iter1 19-175) |
| 9 | Gymhgy.v10official | 2220 | 62 | 392 | 360-32 | 12% (g_iter1 23-171) |
| 10 | hsmalladi.finalbot | 2113 | 49 | 392 | 336-56 | 11% (g_iter1 22-172) |
| 11 | CyrilSharma.finalBot | 2092 | 47 | 392 | 330-62 | 20% (g_iter1 39-155) |
| 12 | winkelmantanner.waffle | 2070 | 45 | 392 | 323-69 | 21% (g_iter1 40-154) |
| 13 | ColtG5.Goob_final | 1902 | 36 | 392 | 251-141 | 39% (g_iter1 76-118) |
| 14 | **us:a3dig5** | 1889 | 132 | 110 | 84-26 |  |
| 15 | **us:arch_rush10** | 1889 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1889 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1881 | 35 | 392 | 240-152 | 42% (g_iter1 81-113) |
| 18 | **us:e1aggr** | 1863 | 131 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1847 | 35 | 392 | 221-171 | 39% (g_iter1 76-118) |
| 20 | **us:c5bank** | 1838 | 131 | 110 | 82-28 |  |
| 21 | **us:a3dig10** | 1838 | 131 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1838 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1815 | 16 | 3950 | 1441-2509 |  |
| 24 | **us:a2reloc** | 1812 | 130 | 110 | 81-29 |  |
| 25 | **us:a2relay** | 1812 | 130 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1812 | 130 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1811 | 28 | 1200 | 422-778 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1805 | 35 | 392 | 198-194 | 50% (g_iter1 97-97) |
| 29 | **us:b1v2** | 1782 | 25 | 1560 | 518-1042 |  |
| 30 | **us:b2fs** | 1775 | 37 | 720 | 236-484 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1634 | 39 | 392 | 109-283 | 69% (g_iter1 133-61) |
| 32 | clbarrell.duck8 | 1594 | 41 | 392 | 92-300 | 78% (g_iter1 151-43) |
| 33 | jonters.bling3 | 1476 | 50 | 392 | 53-339 | 89% (g_iter1 172-22) |
| 34 | **us:arch_rush** | 1452 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1388 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1386 | 90 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1346 | 67 | 392 | 27-365 | 97% (g_iter1 189-5) |
| 38 | Metta-AI.bc24scenario | 1319 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1319 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1319 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1231 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1231 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1231 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1231 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1231 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1231 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1231 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1231 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1231 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1231 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1231 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1231 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1125 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1093 | 357 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1093 | 357 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1093 | 357 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1093 | 357 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1093 | 357 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1093 | 357 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1093 | 357 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1093 | 357 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1093 | 357 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1093 | 357 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1093 | 357 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1093 | 357 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1093 | 357 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1093 | 357 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1093 | 357 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1093 | 357 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1093 | 357 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1032 | 154 | 392 | 4-388 | 100% (g_iter1 194-0) |
