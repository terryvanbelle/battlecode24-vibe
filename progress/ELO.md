# Ladder

3669 scrimmages (ours only), 3669 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| arch_rush10 | 1919 +- 131 | 14 of 68 | 110 | 84-26 | 75.6% | 16.1% (vs 13) |
| a3dig5 | 1919 +- 131 | 15 of 68 | 110 | 84-26 | 75.6% | 16.1% (vs 13) |
| g_iter1_c2 | 1919 +- 131 | 16 of 68 | 110 | 84-26 | 75.6% | 16.1% (vs 13) |
| e1aggr | 1893 +- 130 | 19 of 68 | 110 | 83-27 | 74.7% | 18.9% (vs 15) |
| a3dig10 | 1868 +- 130 | 20 of 68 | 110 | 82-28 | 73.8% | 17.1% (vs 15) |
| c5bank | 1868 +- 130 | 21 of 68 | 110 | 82-28 | 73.8% | 17.1% (vs 15) |
| c6pair | 1868 +- 130 | 22 of 68 | 110 | 82-28 | 73.8% | 17.1% (vs 15) |
| g_iter1 | 1853 +- 20 | 24 of 68 | 2350 | 881-1469 | 73.3% | 18.2% (vs 16) |
| e2aggr | 1843 +- 129 | 25 of 68 | 110 | 81-29 | 72.9% | 17.5% (vs 16) |
| a2relay | 1843 +- 129 | 26 of 68 | 110 | 81-29 | 72.9% | 17.5% (vs 16) |
| a2reloc | 1843 +- 129 | 27 of 68 | 110 | 81-29 | 72.9% | 17.5% (vs 16) |
| arch_rush | 1480 +- 95 | 30 of 68 | 110 | 62-48 | 56.4% | 5.4% (vs 18) |
| g_iter0 | 1415 +- 90 | 32 of 68 | 109 | 56-53 | 51.9% | 5.9% (vs 19) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2463 | 173 | 138 | 135-3 | 3% (g_iter1 3-111) |
| 2 | uravt.Version18Final | 2412 | 348 | 26 | 26-0 |  |
| 3 | andli28.v9_USQuals_angle | 2390 | 142 | 138 | 133-5 | 4% (g_iter1 5-109) |
| 4 | chenyx512.flagbot_final | 2390 | 142 | 138 | 133-5 | 4% (g_iter1 4-110) |
| 5 | IvanGeffner.kuma | 2362 | 132 | 138 | 132-6 | 4% (g_iter1 5-109) |
| 6 | jmerle.camel_case_v21_final | 2362 | 132 | 138 | 132-6 | 4% (g_iter1 5-109) |
| 7 | NotLLeon.v3 | 2296 | 112 | 138 | 129-9 | 8% (g_iter1 9-105) |
| 8 | andrewgopher.player22 | 2232 | 96 | 138 | 125-13 | 11% (g_iter1 13-101) |
| 9 | Gymhgy.v10official | 2194 | 88 | 138 | 122-16 | 14% (g_iter1 16-98) |
| 10 | hsmalladi.finalbot | 2115 | 75 | 138 | 114-24 | 12% (g_iter1 14-100) |
| 11 | CyrilSharma.finalBot | 2090 | 72 | 138 | 111-27 | 22% (g_iter1 25-89) |
| 12 | winkelmantanner.waffle | 2067 | 70 | 138 | 108-30 | 25% (g_iter1 29-85) |
| 13 | quesswho.cretplayer2_3 | 1922 | 60 | 138 | 84-54 | 37% (g_iter1 42-72) |
| 14 | **us:arch_rush10** | 1919 | 131 | 110 | 84-26 |  |
| 15 | **us:a3dig5** | 1919 | 131 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1919 | 131 | 110 | 84-26 |  |
| 17 | ColtG5.Goob_final | 1917 | 59 | 138 | 83-55 | 42% (g_iter1 48-66) |
| 18 | kyleezz.jeeryfix3 | 1896 | 59 | 138 | 79-59 | 45% (g_iter1 51-63) |
| 19 | **us:e1aggr** | 1893 | 130 | 110 | 83-27 |  |
| 20 | **us:a3dig10** | 1868 | 130 | 110 | 82-28 |  |
| 21 | **us:c5bank** | 1868 | 130 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1868 | 130 | 110 | 82-28 |  |
| 23 | SampleProvider.TSPAARKSPRINT1 | 1855 | 58 | 138 | 71-67 | 49% (g_iter1 56-58) |
| 24 | **us:g_iter1** | 1853 | 20 | 2350 | 881-1469 |  |
| 25 | **us:e2aggr** | 1843 | 129 | 110 | 81-29 |  |
| 26 | **us:a2relay** | 1843 | 129 | 110 | 81-29 |  |
| 27 | **us:a2reloc** | 1843 | 129 | 110 | 81-29 |  |
| 28 | dmtrung14.defaultplayer_intlqualifier | 1727 | 62 | 138 | 47-91 | 65% (g_iter1 74-40) |
| 29 | clbarrell.duck8 | 1638 | 69 | 138 | 33-105 | 76% (g_iter1 87-27) |
| 30 | **us:arch_rush** | 1480 | 95 | 110 | 62-48 |  |
| 31 | jonters.bling3 | 1469 | 94 | 138 | 15-123 | 90% (g_iter1 103-11) |
| 32 | **us:g_iter0** | 1415 | 90 | 109 | 56-53 |  |
| 33 | HugoIngelsson.Bot21 | 1413 | 204 | 26 | 3-23 |  |
| 34 | awu7.ExplosiveBot | 1372 | 115 | 138 | 9-129 | 96% (g_iter1 110-4) |
| 35 | Metta-AI.bc24scenario | 1344 | 226 | 26 | 2-24 |  |
| 36 | noahzemlin.honeyducklings | 1344 | 226 | 26 | 2-24 |  |
| 37 | sivakovivan.NewHide | 1344 | 226 | 26 | 2-24 |  |
| 38 | JeffLegendPower.v11 | 1255 | 265 | 26 | 1-25 |  |
| 39 | MiloAkerman.v1 | 1255 | 265 | 26 | 1-25 |  |
| 40 | PerishoJ.tx | 1255 | 265 | 26 | 1-25 |  |
| 41 | Peter-Fun.dinoboxer | 1255 | 265 | 26 | 1-25 |  |
| 42 | RyanAspen.v22 | 1255 | 265 | 26 | 1-25 |  |
| 43 | TylerJulian.v9 | 1255 | 265 | 26 | 1-25 |  |
| 44 | aj-chau.cowards | 1255 | 265 | 26 | 1-25 |  |
| 45 | cViper971.ourplayer | 1255 | 265 | 26 | 1-25 |  |
| 46 | dylanzemlin.dangerduck2 | 1255 | 265 | 26 | 1-25 |  |
| 47 | lukerhoads.warrior_2nd_comp | 1255 | 265 | 26 | 1-25 |  |
| 48 | neilhuang007.baseline | 1255 | 265 | 26 | 1-25 |  |
| 49 | polyllc.polyv4 | 1255 | 265 | 26 | 1-25 |  |
| 50 | justinottesen.sprint1 | 1238 | 159 | 138 | 4-134 | 100% (g_iter1 114-0) |
| 51 | andrearante12.turtle | 1148 | 358 | 25 | 0-25 |  |
| 52 | AlexYu84.smartPlayer | 1116 | 358 | 26 | 0-26 |  |
| 53 | H4ffliger.keyboardcrusader_v1 | 1116 | 358 | 26 | 0-26 |  |
| 54 | Lithanium.AttackingBot | 1116 | 358 | 26 | 0-26 |  |
| 55 | Rubrasum.version_3 | 1116 | 358 | 26 | 0-26 |  |
| 56 | SriLakshmiPolavarapu.ducks | 1116 | 358 | 26 | 0-26 |  |
| 57 | VarunVejalla.alexander | 1116 | 358 | 26 | 0-26 |  |
| 58 | abdullah8a0.crayBasic | 1116 | 358 | 26 | 0-26 |  |
| 59 | adamseth2.moveBot1 | 1116 | 358 | 26 | 0-26 |  |
| 60 | dylanconklin.Team3 | 1116 | 358 | 26 | 0-26 |  |
| 61 | itswin.MPAttack | 1116 | 358 | 26 | 0-26 |  |
| 62 | joelcrouch.ducks | 1116 | 358 | 26 | 0-26 |  |
| 63 | lcforges.funkyguy3 | 1116 | 358 | 26 | 0-26 |  |
| 64 | qpwoeirut.tournament_sprint1 | 1116 | 358 | 26 | 0-26 |  |
| 65 | reeceyang.v5 | 1116 | 358 | 26 | 0-26 |  |
| 66 | samithShetty.combustiblelemon | 1116 | 358 | 26 | 0-26 |  |
| 67 | sayam-goyal.SimpleBot | 1116 | 358 | 26 | 0-26 |  |
| 68 | tlevietpdx.Sprint2 | 1116 | 358 | 26 | 0-26 |  |
