# Ladder

13229 scrimmages (ours only), 13229 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1857 +- 131 | 14 of 73 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| g_iter1_c2 | 1857 +- 131 | 15 of 73 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| arch_rush10 | 1857 +- 131 | 16 of 73 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| e1aggr | 1831 +- 130 | 18 of 73 | 110 | 83-27 | 74.8% | 16.2% (vs 14) |
| c6pair | 1806 +- 129 | 20 of 73 | 110 | 82-28 | 73.9% | 16.8% (vs 15) |
| a3dig10 | 1806 +- 129 | 21 of 73 | 110 | 82-28 | 73.9% | 16.8% (vs 15) |
| c5bank | 1806 +- 129 | 22 of 73 | 110 | 82-28 | 73.9% | 16.8% (vs 15) |
| g1copy | 1786 +- 151 | 23 of 73 | 40 | 15-25 | 73.2% | 15.5% (vs 15) |
| a2reloc | 1781 +- 128 | 24 of 73 | 110 | 81-29 | 73.0% | 15.2% (vs 15) |
| a2relay | 1781 +- 128 | 25 of 73 | 110 | 81-29 | 73.0% | 15.2% (vs 15) |
| e2aggr | 1781 +- 128 | 26 of 73 | 110 | 81-29 | 73.0% | 15.2% (vs 15) |
| b1z2b | 1777 +- 23 | 27 of 73 | 1760 | 620-1140 | 72.9% | 14.9% (vs 15) |
| g_iter1 | 1776 +- 11 | 28 of 73 | 6590 | 2332-4258 | 72.8% | 14.9% (vs 15) |
| g1sym | 1774 +- 65 | 29 of 73 | 120 | 41-79 | 72.8% | 14.8% (vs 15) |
| b2fs | 1763 +- 28 | 30 of 73 | 1280 | 439-841 | 72.4% | 14.1% (vs 15) |
| b1v2 | 1752 +- 22 | 32 of 73 | 2120 | 711-1409 | 72.0% | 15.6% (vs 16) |
| arch_rush | 1425 +- 95 | 35 of 73 | 110 | 62-48 | 56.6% | 6.1% (vs 18) |
| g_iter0 | 1360 +- 89 | 38 of 73 | 109 | 56-53 | 52.0% | 8.5% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | jmerle.camel_case_v21_final | 2402 | 95 | 508 | 496-12 | 4% (g_iter1 10-214) |
| 2 | Strequals.duck0127v5 | 2377 | 89 | 508 | 494-14 | 3% (g_iter1 7-217) |
| 3 | IvanGeffner.kuma | 2354 | 84 | 508 | 492-16 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2349 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2245 | 63 | 508 | 478-30 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2228 | 60 | 508 | 475-33 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2202 | 57 | 508 | 470-38 | 6% (g_iter1 13-211) |
| 8 | Gymhgy.v10official | 2179 | 54 | 508 | 465-43 | 11% (g_iter1 25-199) |
| 9 | andrewgopher.player22 | 2144 | 49 | 508 | 456-52 | 11% (g_iter1 24-200) |
| 10 | hsmalladi.finalbot | 2080 | 43 | 508 | 436-72 | 12% (g_iter1 27-197) |
| 11 | CyrilSharma.finalBot | 2051 | 41 | 508 | 425-83 | 20% (g_iter1 44-180) |
| 12 | winkelmantanner.waffle | 2012 | 38 | 508 | 408-100 | 21% (g_iter1 47-177) |
| 13 | ColtG5.Goob_final | 1893 | 14 | 2668 | 1771-897 | 34% (g_iter1 764-1500) |
| 14 | **us:a3dig5** | 1857 | 131 | 110 | 84-26 |  |
| 15 | **us:g_iter1_c2** | 1857 | 131 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1857 | 131 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1855 | 31 | 508 | 316-192 | 41% (g_iter1 91-133) |
| 18 | **us:e1aggr** | 1831 | 130 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1812 | 31 | 508 | 286-222 | 40% (g_iter1 90-134) |
| 20 | **us:c6pair** | 1806 | 129 | 110 | 82-28 |  |
| 21 | **us:a3dig10** | 1806 | 129 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1806 | 129 | 110 | 82-28 |  |
| 23 | **us:g1copy** | 1786 | 151 | 40 | 15-25 |  |
| 24 | **us:a2reloc** | 1781 | 128 | 110 | 81-29 |  |
| 25 | **us:a2relay** | 1781 | 128 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1781 | 128 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1777 | 23 | 1760 | 620-1140 |  |
| 28 | **us:g_iter1** | 1776 | 11 | 6590 | 2332-4258 |  |
| 29 | **us:g1sym** | 1774 | 65 | 120 | 41-79 |  |
| 30 | **us:b2fs** | 1763 | 28 | 1280 | 439-841 |  |
| 31 | SampleProvider.TSPAARKSPRINT1 | 1762 | 30 | 508 | 250-258 | 50% (g_iter1 112-112) |
| 32 | **us:b1v2** | 1752 | 22 | 2120 | 711-1409 |  |
| 33 | dmtrung14.defaultplayer_intlqualifier | 1595 | 34 | 508 | 138-370 | 69% (g_iter1 155-69) |
| 34 | clbarrell.duck8 | 1576 | 35 | 508 | 127-381 | 78% (g_iter1 175-49) |
| 35 | **us:arch_rush** | 1425 | 95 | 110 | 62-48 |  |
| 36 | jonters.bling3 | 1420 | 47 | 508 | 61-447 | 89% (g_iter1 200-24) |
| 37 | HugoIngelsson.Bot21 | 1362 | 202 | 26 | 3-23 |  |
| 38 | **us:g_iter0** | 1360 | 89 | 109 | 56-53 |  |
| 39 | awu7.ExplosiveBot | 1309 | 60 | 508 | 34-474 | 97% (g_iter1 217-7) |
| 40 | Metta-AI.bc24scenario | 1294 | 225 | 26 | 2-24 |  |
| 41 | noahzemlin.honeyducklings | 1294 | 225 | 26 | 2-24 |  |
| 42 | sivakovivan.NewHide | 1294 | 225 | 26 | 2-24 |  |
| 43 | JeffLegendPower.v11 | 1206 | 264 | 26 | 1-25 |  |
| 44 | MiloAkerman.v1 | 1206 | 264 | 26 | 1-25 |  |
| 45 | PerishoJ.tx | 1206 | 264 | 26 | 1-25 |  |
| 46 | Peter-Fun.dinoboxer | 1206 | 264 | 26 | 1-25 |  |
| 47 | RyanAspen.v22 | 1206 | 264 | 26 | 1-25 |  |
| 48 | TylerJulian.v9 | 1206 | 264 | 26 | 1-25 |  |
| 49 | aj-chau.cowards | 1206 | 264 | 26 | 1-25 |  |
| 50 | cViper971.ourplayer | 1206 | 264 | 26 | 1-25 |  |
| 51 | dylanzemlin.dangerduck2 | 1206 | 264 | 26 | 1-25 |  |
| 52 | lukerhoads.warrior_2nd_comp | 1206 | 264 | 26 | 1-25 |  |
| 53 | neilhuang007.baseline | 1206 | 264 | 26 | 1-25 |  |
| 54 | polyllc.polyv4 | 1206 | 264 | 26 | 1-25 |  |
| 55 | andrearante12.turtle | 1101 | 357 | 25 | 0-25 |  |
| 56 | AlexYu84.smartPlayer | 1068 | 357 | 26 | 0-26 |  |
| 57 | H4ffliger.keyboardcrusader_v1 | 1068 | 357 | 26 | 0-26 |  |
| 58 | Lithanium.AttackingBot | 1068 | 357 | 26 | 0-26 |  |
| 59 | Rubrasum.version_3 | 1068 | 357 | 26 | 0-26 |  |
| 60 | SriLakshmiPolavarapu.ducks | 1068 | 357 | 26 | 0-26 |  |
| 61 | VarunVejalla.alexander | 1068 | 357 | 26 | 0-26 |  |
| 62 | abdullah8a0.crayBasic | 1068 | 357 | 26 | 0-26 |  |
| 63 | adamseth2.moveBot1 | 1068 | 357 | 26 | 0-26 |  |
| 64 | dylanconklin.Team3 | 1068 | 357 | 26 | 0-26 |  |
| 65 | itswin.MPAttack | 1068 | 357 | 26 | 0-26 |  |
| 66 | joelcrouch.ducks | 1068 | 357 | 26 | 0-26 |  |
| 67 | lcforges.funkyguy3 | 1068 | 357 | 26 | 0-26 |  |
| 68 | qpwoeirut.tournament_sprint1 | 1068 | 357 | 26 | 0-26 |  |
| 69 | reeceyang.v5 | 1068 | 357 | 26 | 0-26 |  |
| 70 | samithShetty.combustiblelemon | 1068 | 357 | 26 | 0-26 |  |
| 71 | sayam-goyal.SimpleBot | 1068 | 357 | 26 | 0-26 |  |
| 72 | tlevietpdx.Sprint2 | 1068 | 357 | 26 | 0-26 |  |
| 73 | justinottesen.sprint1 | 957 | 153 | 508 | 4-504 | 100% (g_iter1 224-0) |
