# Ladder

8069 scrimmages (ours only), 8069 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| arch_rush10 | 1892 +- 132 | 14 of 71 | 110 | 84-26 | 75.6% | 15.1% (vs 13) |
| g_iter1_c2 | 1892 +- 132 | 15 of 71 | 110 | 84-26 | 75.6% | 15.1% (vs 13) |
| a3dig5 | 1892 +- 132 | 16 of 71 | 110 | 84-26 | 75.6% | 15.1% (vs 13) |
| e1aggr | 1866 +- 132 | 18 of 71 | 110 | 83-27 | 74.7% | 16.0% (vs 14) |
| c5bank | 1840 +- 131 | 20 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| c6pair | 1840 +- 131 | 21 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| a3dig10 | 1840 +- 131 | 22 of 71 | 110 | 82-28 | 73.9% | 16.6% (vs 15) |
| g_iter1 | 1819 +- 16 | 23 of 71 | 3750 | 1374-2376 | 73.1% | 15.3% (vs 15) |
| b1z2b | 1819 +- 31 | 24 of 71 | 1040 | 370-670 | 73.1% | 15.3% (vs 15) |
| a2relay | 1814 +- 130 | 25 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| a2reloc | 1814 +- 130 | 26 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| e2aggr | 1814 +- 130 | 27 of 71 | 110 | 81-29 | 73.0% | 15.0% (vs 15) |
| b1v2 | 1782 +- 27 | 29 of 71 | 1400 | 463-937 | 71.9% | 15.1% (vs 16) |
| b2fs | 1772 +- 43 | 30 of 71 | 560 | 182-378 | 71.5% | 14.6% (vs 16) |
| arch_rush | 1453 +- 95 | 34 of 71 | 110 | 62-48 | 56.5% | 8.1% (vs 19) |
| g_iter0 | 1388 +- 89 | 36 of 71 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2409 | 104 | 358 | 348-10 | 4% (b1z2b 2-50) |
| 2 | jmerle.camel_case_v21_final | 2409 | 104 | 358 | 348-10 | 2% (b1z2b 1-51) |
| 3 | uravt.Version18Final | 2384 | 348 | 26 | 26-0 |  |
| 4 | IvanGeffner.kuma | 2366 | 93 | 358 | 345-13 | 4% (b1z2b 2-50) |
| 5 | andli28.v9_USQuals_angle | 2283 | 75 | 358 | 337-21 | 12% (b1z2b 6-46) |
| 6 | chenyx512.flagbot_final | 2275 | 73 | 358 | 336-22 | 8% (b1z2b 4-48) |
| 7 | NotLLeon.v3 | 2267 | 72 | 358 | 335-23 | 6% (b1z2b 3-49) |
| 8 | Gymhgy.v10official | 2225 | 65 | 358 | 329-29 | 6% (b1z2b 3-49) |
| 9 | andrewgopher.player22 | 2219 | 64 | 358 | 328-30 | 10% (b1z2b 5-47) |
| 10 | hsmalladi.finalbot | 2117 | 51 | 358 | 307-51 | 12% (b1z2b 6-46) |
| 11 | CyrilSharma.finalBot | 2095 | 49 | 358 | 301-57 | 13% (b1z2b 7-45) |
| 12 | winkelmantanner.waffle | 2074 | 47 | 358 | 295-63 | 21% (b1z2b 11-41) |
| 13 | ColtG5.Goob_final | 1906 | 38 | 358 | 229-129 | 37% (b1z2b 19-33) |
| 14 | **us:arch_rush10** | 1892 | 132 | 110 | 84-26 |  |
| 15 | **us:g_iter1_c2** | 1892 | 132 | 110 | 84-26 |  |
| 16 | **us:a3dig5** | 1892 | 132 | 110 | 84-26 |  |
| 17 | kyleezz.jeeryfix3 | 1877 | 37 | 358 | 215-143 | 46% (b1z2b 24-28) |
| 18 | **us:e1aggr** | 1866 | 132 | 110 | 83-27 |  |
| 19 | quesswho.cretplayer2_3 | 1847 | 36 | 358 | 200-158 | 56% (b1z2b 29-23) |
| 20 | **us:c5bank** | 1840 | 131 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1840 | 131 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1840 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1819 | 16 | 3750 | 1374-2376 |  |
| 24 | **us:b1z2b** | 1819 | 31 | 1040 | 370-670 |  |
| 25 | **us:a2relay** | 1814 | 130 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1814 | 130 | 110 | 81-29 |  |
| 27 | **us:e2aggr** | 1814 | 130 | 110 | 81-29 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1809 | 36 | 358 | 181-177 | 58% (b1z2b 30-22) |
| 29 | **us:b1v2** | 1782 | 27 | 1400 | 463-937 |  |
| 30 | **us:b2fs** | 1772 | 43 | 560 | 182-378 |  |
| 31 | dmtrung14.defaultplayer_intlqualifier | 1644 | 40 | 358 | 102-256 | 83% (b1z2b 43-9) |
| 32 | clbarrell.duck8 | 1589 | 43 | 358 | 81-277 | 73% (b1z2b 38-14) |
| 33 | jonters.bling3 | 1469 | 54 | 358 | 46-312 | 77% (b1z2b 40-12) |
| 34 | **us:arch_rush** | 1453 | 95 | 110 | 62-48 |  |
| 35 | HugoIngelsson.Bot21 | 1389 | 203 | 26 | 3-23 |  |
| 36 | **us:g_iter0** | 1388 | 89 | 109 | 56-53 |  |
| 37 | awu7.ExplosiveBot | 1344 | 71 | 358 | 24-334 | 87% (b1z2b 45-7) |
| 38 | Metta-AI.bc24scenario | 1321 | 225 | 26 | 2-24 |  |
| 39 | noahzemlin.honeyducklings | 1321 | 225 | 26 | 2-24 |  |
| 40 | sivakovivan.NewHide | 1321 | 225 | 26 | 2-24 |  |
| 41 | JeffLegendPower.v11 | 1232 | 265 | 26 | 1-25 |  |
| 42 | MiloAkerman.v1 | 1232 | 265 | 26 | 1-25 |  |
| 43 | PerishoJ.tx | 1232 | 265 | 26 | 1-25 |  |
| 44 | Peter-Fun.dinoboxer | 1232 | 265 | 26 | 1-25 |  |
| 45 | RyanAspen.v22 | 1232 | 265 | 26 | 1-25 |  |
| 46 | TylerJulian.v9 | 1232 | 265 | 26 | 1-25 |  |
| 47 | aj-chau.cowards | 1232 | 265 | 26 | 1-25 |  |
| 48 | cViper971.ourplayer | 1232 | 265 | 26 | 1-25 |  |
| 49 | dylanzemlin.dangerduck2 | 1232 | 265 | 26 | 1-25 |  |
| 50 | lukerhoads.warrior_2nd_comp | 1232 | 265 | 26 | 1-25 |  |
| 51 | neilhuang007.baseline | 1232 | 265 | 26 | 1-25 |  |
| 52 | polyllc.polyv4 | 1232 | 265 | 26 | 1-25 |  |
| 53 | andrearante12.turtle | 1126 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1094 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1094 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1094 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1094 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1094 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1094 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1094 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1094 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1094 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1094 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1094 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1094 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1094 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1094 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1094 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1094 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1094 | 358 | 26 | 0-26 |  |
| 71 | justinottesen.sprint1 | 1050 | 154 | 358 | 4-354 | 100% (b1z2b 52-0) |
