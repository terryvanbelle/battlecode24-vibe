# Ladder

6989 scrimmages (ours only), 6989 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| arch_rush10 | 1893 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.5% (vs 13) |
| a3dig5 | 1893 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.5% (vs 13) |
| g_iter1_c2 | 1893 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.5% (vs 13) |
| e1aggr | 1867 +- 131 | 18 of 71 | 110 | 83-27 | 74.7% | 16.3% (vs 14) |
| a3dig10 | 1842 +- 130 | 20 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c6pair | 1842 +- 130 | 21 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| c5bank | 1842 +- 130 | 22 of 71 | 110 | 82-28 | 73.9% | 16.7% (vs 15) |
| b1z2b | 1833 +- 35 | 23 of 71 | 760 | 277-483 | 73.6% | 16.2% (vs 15) |
| g_iter1 | 1822 +- 17 | 24 of 71 | 3510 | 1291-2219 | 73.2% | 15.5% (vs 15) |
| a2reloc | 1817 +- 129 | 25 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| a2relay | 1817 +- 129 | 26 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| e2aggr | 1817 +- 129 | 27 of 71 | 110 | 81-29 | 73.0% | 15.1% (vs 15) |
| b1v2 | 1780 +- 30 | 29 of 71 | 1120 | 367-753 | 71.7% | 14.9% (vs 16) |
| b2fs | 1752 +- 61 | 30 of 71 | 280 | 87-193 | 70.7% | 13.2% (vs 16) |
| arch_rush | 1455 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1390 +- 89 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2441 | 122 | 304 | 297-7 | 3% (g_iter1 5-167) |
| 2 | uravt.Version18Final | 2386 | 348 | 26 | 26-0 |  |
| 3 | jmerle.camel_case_v21_final | 2384 | 105 | 304 | 294-10 | 5% (g_iter1 8-164) |
| 4 | IvanGeffner.kuma | 2341 | 93 | 304 | 291-13 | 5% (g_iter1 8-164) |
| 5 | andli28.v9_USQuals_angle | 2275 | 79 | 304 | 285-19 | 6% (g_iter1 10-162) |
| 6 | NotLLeon.v3 | 2266 | 77 | 304 | 284-20 | 7% (g_iter1 12-160) |
| 7 | chenyx512.flagbot_final | 2266 | 77 | 304 | 284-20 | 6% (g_iter1 10-162) |
| 8 | Gymhgy.v10official | 2226 | 70 | 304 | 279-25 | 12% (g_iter1 21-151) |
| 9 | andrewgopher.player22 | 2211 | 68 | 304 | 277-27 | 10% (g_iter1 17-155) |
| 10 | hsmalladi.finalbot | 2113 | 55 | 304 | 259-45 | 12% (g_iter1 21-151) |
| 11 | CyrilSharma.finalBot | 2096 | 53 | 304 | 255-49 | 21% (g_iter1 36-136) |
| 12 | winkelmantanner.waffle | 2064 | 50 | 304 | 247-57 | 22% (g_iter1 38-134) |
| 13 | ColtG5.Goob_final | 1904 | 41 | 304 | 192-112 | 40% (g_iter1 68-104) |
| 14 | **us:arch_rush10** | 1893 | 132 | 110 | 84-26 |  |
| 15 | **us:a3dig5** | 1893 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1893 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1882 | 40 | 304 | 183-121 | 42% (g_iter1 73-99) |
| 18 | **us:e1aggr** | 1867 | 131 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1865 | 40 | 304 | 176-128 | 37% (g_iter1 64-108) |
| 20 | **us:a3dig10** | 1842 | 130 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1842 | 130 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1842 | 130 | 110 | 82-28 |  |
| 23 | **us:b1z2b** | 1833 | 35 | 760 | 277-483 |  |
| 24 | **us:g_iter1** | 1822 | 17 | 3510 | 1291-2219 |  |
| 25 | **us:a2reloc** | 1817 | 129 | 110 | 81-29 |  |
| 26 | **us:a2relay** | 1817 | 129 | 110 | 81-29 |  |
| 27 | **us:e2aggr** | 1817 | 129 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1814 | 39 | 304 | 154-150 | 51% (g_iter1 87-85) |
| 29 | **us:b1v2** | 1780 | 30 | 1120 | 367-753 |  |
| 30 | **us:b2fs** | 1752 | 61 | 280 | 87-193 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1657 | 43 | 304 | 90-214 | 69% (g_iter1 118-54) |
| 32 | clbarrell.duck8 | 1593 | 47 | 304 | 69-235 | 78% (g_iter1 135-37) |
| 33 | jonters.bling3 | 1466 | 59 | 304 | 38-266 | 89% (g_iter1 153-19) |
| 34 | **us:arch_rush** | 1455 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1391 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1390 | 89 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1344 | 78 | 304 | 20-284 | 97% (g_iter1 167-5) |
| 38 | Metta-AI.bc24scenario | 1323 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1323 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1323 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1234 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1234 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1234 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1234 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1234 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1234 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1234 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1234 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1234 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1234 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1234 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1234 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1128 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1096 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1096 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1096 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1096 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1096 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1096 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1096 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1096 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1096 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1096 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1096 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1096 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1096 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1096 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1096 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1096 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1096 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1080 | 154 | 304 | 4-300 | 100% (g_iter1 172-0) |
