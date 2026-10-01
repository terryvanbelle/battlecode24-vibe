# Ladder

3709 scrimmages (ours only), 3709 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1919 +- 131 | 14 of 68 | 110 | 84-26 | 75.6% | 16.1% (vs 13) |
| a3dig5 | 1919 +- 131 | 15 of 68 | 110 | 84-26 | 75.6% | 16.1% (vs 13) |
| arch_rush10 | 1919 +- 131 | 16 of 68 | 110 | 84-26 | 75.6% | 16.1% (vs 13) |
| e1aggr | 1893 +- 130 | 18 of 68 | 110 | 83-27 | 74.7% | 16.7% (vs 14) |
| c5bank | 1868 +- 130 | 20 of 68 | 110 | 82-28 | 73.8% | 17.2% (vs 15) |
| a3dig10 | 1868 +- 130 | 21 of 68 | 110 | 82-28 | 73.8% | 17.2% (vs 15) |
| c6pair | 1868 +- 130 | 22 of 68 | 110 | 82-28 | 73.8% | 17.2% (vs 15) |
| g_iter1 | 1852 +- 20 | 24 of 68 | 2390 | 894-1496 | 73.3% | 18.2% (vs 16) |
| a2relay | 1843 +- 129 | 25 of 68 | 110 | 81-29 | 72.9% | 17.5% (vs 16) |
| e2aggr | 1843 +- 129 | 26 of 68 | 110 | 81-29 | 72.9% | 17.5% (vs 16) |
| a2reloc | 1843 +- 129 | 27 of 68 | 110 | 81-29 | 72.9% | 17.5% (vs 16) |
| arch_rush | 1480 +- 95 | 30 of 68 | 110 | 62-48 | 56.4% | 5.4% (vs 18) |
| g_iter0 | 1415 +- 90 | 32 of 68 | 109 | 56-53 | 51.9% | 5.9% (vs 19) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2465 | 173 | 140 | 137-3 | 3% (g_iter1 3-113) |
| 2 | uravt.Version18Final | 2412 | 348 | 26 | 26-0 |  |
| 3 | andli28.v9_USQuals_angle | 2392 | 142 | 140 | 135-5 | 4% (g_iter1 5-111) |
| 4 | chenyx512.flagbot_final | 2392 | 142 | 140 | 135-5 | 3% (g_iter1 4-112) |
| 5 | IvanGeffner.kuma | 2364 | 132 | 140 | 134-6 | 4% (g_iter1 5-111) |
| 6 | jmerle.camel_case_v21_final | 2364 | 132 | 140 | 134-6 | 4% (g_iter1 5-111) |
| 7 | NotLLeon.v3 | 2298 | 112 | 140 | 131-9 | 8% (g_iter1 9-107) |
| 8 | andrewgopher.player22 | 2234 | 96 | 140 | 127-13 | 11% (g_iter1 13-103) |
| 9 | Gymhgy.v10official | 2196 | 88 | 140 | 124-16 | 14% (g_iter1 16-100) |
| 10 | hsmalladi.finalbot | 2117 | 75 | 140 | 116-24 | 12% (g_iter1 14-102) |
| 11 | CyrilSharma.finalBot | 2092 | 72 | 140 | 113-27 | 22% (g_iter1 25-91) |
| 12 | winkelmantanner.waffle | 2062 | 69 | 140 | 109-31 | 26% (g_iter1 30-86) |
| 13 | quesswho.cretplayer2_3 | 1920 | 59 | 140 | 85-55 | 37% (g_iter1 43-73) |
| 14 | **us:g_iter1_c2** | 1919 | 131 | 110 | 84-26 |  |
| 15 | **us:a3dig5** | 1919 | 131 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1919 | 131 | 110 | 84-26 |  |
| 17 | ColtG5.Goob_final | 1915 | 59 | 140 | 84-56 | 42% (g_iter1 49-67) |
| 18 | **us:e1aggr** | 1893 | 130 | 110 | 83-27 |  |
| 19 | kyleezz.jeeryfix3 | 1889 | 58 | 140 | 79-61 | 46% (g_iter1 53-63) |
| 20 | **us:c5bank** | 1868 | 130 | 110 | 82-28 |  |
| 21 | **us:a3dig10** | 1868 | 130 | 110 | 82-28 |  |
| 22 | **us:c6pair** | 1868 | 130 | 110 | 82-28 |  |
| 23 | SampleProvider.TSPAARKSPRINT1 | 1854 | 58 | 140 | 72-68 | 49% (g_iter1 57-59) |
| 24 | **us:g_iter1** | 1852 | 20 | 2390 | 894-1496 |  |
| 25 | **us:a2relay** | 1843 | 129 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1843 | 129 | 110 | 81-29 |  |
| 27 | **us:a2reloc** | 1843 | 129 | 110 | 81-29 |  |
| 28 | dmtrung14.defaultplayer_intlqualifier | 1728 | 62 | 140 | 48-92 | 65% (g_iter1 75-41) |
| 29 | clbarrell.duck8 | 1641 | 68 | 140 | 34-106 | 76% (g_iter1 88-28) |
| 30 | **us:arch_rush** | 1480 | 95 | 110 | 62-48 |  |
| 31 | jonters.bling3 | 1478 | 91 | 140 | 16-124 | 90% (g_iter1 104-12) |
| 32 | **us:g_iter0** | 1415 | 90 | 109 | 56-53 |  |
| 33 | HugoIngelsson.Bot21 | 1412 | 203 | 26 | 3-23 |  |
| 34 | awu7.ExplosiveBot | 1369 | 115 | 140 | 9-131 | 97% (g_iter1 112-4) |
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
| 50 | justinottesen.sprint1 | 1235 | 158 | 140 | 4-136 | 100% (g_iter1 116-0) |
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
