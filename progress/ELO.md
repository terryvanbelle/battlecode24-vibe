# Ladder

4109 scrimmages (ours only), 4109 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1913 +- 131 | 15 of 69 | 110 | 84-26 | 75.6% | 18.3% (vs 14) |
| arch_rush10 | 1913 +- 131 | 16 of 69 | 110 | 84-26 | 75.6% | 18.3% (vs 14) |
| g_iter1_c2 | 1913 +- 131 | 17 of 69 | 110 | 84-26 | 75.6% | 18.3% (vs 14) |
| e1aggr | 1887 +- 131 | 19 of 69 | 110 | 83-27 | 74.7% | 18.7% (vs 15) |
| c6pair | 1861 +- 130 | 20 of 69 | 110 | 82-28 | 73.8% | 16.9% (vs 15) |
| c5bank | 1861 +- 130 | 21 of 69 | 110 | 82-28 | 73.8% | 16.9% (vs 15) |
| a3dig10 | 1861 +- 130 | 22 of 69 | 110 | 82-28 | 73.8% | 16.9% (vs 15) |
| g_iter1 | 1843 +- 19 | 23 of 69 | 2590 | 961-1629 | 73.2% | 15.6% (vs 15) |
| e2aggr | 1836 +- 129 | 25 of 69 | 110 | 81-29 | 73.0% | 17.3% (vs 16) |
| a2reloc | 1836 +- 129 | 26 of 69 | 110 | 81-29 | 73.0% | 17.3% (vs 16) |
| a2relay | 1836 +- 129 | 27 of 69 | 110 | 81-29 | 73.0% | 17.3% (vs 16) |
| b1v2 | 1764 +- 72 | 28 of 69 | 200 | 61-139 | 70.4% | 12.9% (vs 16) |
| arch_rush | 1473 +- 95 | 31 of 69 | 110 | 62-48 | 56.4% | 5.5% (vs 18) |
| g_iter0 | 1407 +- 89 | 33 of 69 | 109 | 56-53 | 51.9% | 6.1% (vs 19) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2476 | 172 | 160 | 157-3 | 2% (g_iter1 3-123) |
| 2 | uravt.Version18Final | 2405 | 348 | 26 | 26-0 |  |
| 3 | andli28.v9_USQuals_angle | 2403 | 142 | 160 | 155-5 | 4% (g_iter1 5-121) |
| 4 | jmerle.camel_case_v21_final | 2375 | 132 | 160 | 154-6 | 4% (g_iter1 5-121) |
| 5 | IvanGeffner.kuma | 2351 | 124 | 160 | 153-7 | 5% (g_iter1 6-120) |
| 6 | chenyx512.flagbot_final | 2329 | 117 | 160 | 152-8 | 5% (g_iter1 6-120) |
| 7 | NotLLeon.v3 | 2309 | 111 | 160 | 151-9 | 7% (g_iter1 9-117) |
| 8 | andrewgopher.player22 | 2246 | 95 | 160 | 147-13 | 10% (g_iter1 13-113) |
| 9 | Gymhgy.v10official | 2187 | 83 | 160 | 142-18 | 14% (g_iter1 18-108) |
| 10 | hsmalladi.finalbot | 2115 | 72 | 160 | 134-26 | 13% (g_iter1 16-110) |
| 11 | CyrilSharma.finalBot | 2072 | 67 | 160 | 128-32 | 22% (g_iter1 28-98) |
| 12 | winkelmantanner.waffle | 2053 | 65 | 160 | 125-35 | 25% (g_iter1 31-95) |
| 13 | ColtG5.Goob_final | 1930 | 56 | 160 | 102-58 | 40% (g_iter1 50-76) |
| 14 | quesswho.cretplayer2_3 | 1921 | 56 | 160 | 100-60 | 36% (g_iter1 45-81) |
| 15 | **us:a3dig5** | 1913 | 131 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1913 | 131 | 110 | 84-26 |  |
| 17 | **us:g_iter1_c2** | 1913 | 131 | 110 | 84-26 |  |
| 18 | kyleezz.jeeryfix3 | 1889 | 55 | 160 | 93-67 | 44% (g_iter1 55-71) |
| 19 | **us:e1aggr** | 1887 | 131 | 110 | 83-27 |  |
| 20 | **us:c6pair** | 1861 | 130 | 110 | 82-28 |  |
| 21 | **us:c5bank** | 1861 | 130 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1861 | 130 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1843 | 19 | 2590 | 961-1629 |  |
| 24 | SampleProvider.TSPAARKSPRINT1 | 1840 | 54 | 160 | 82-78 | 49% (g_iter1 62-64) |
| 25 | **us:e2aggr** | 1836 | 129 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1836 | 129 | 110 | 81-29 |  |
| 27 | **us:a2relay** | 1836 | 129 | 110 | 81-29 |  |
| 28 | **us:b1v2** | 1764 | 72 | 200 | 61-139 |  |
| 29 | dmtrung14.defaultplayer_intlqualifier | 1706 | 58 | 160 | 53-107 | 66% (g_iter1 83-43) |
| 30 | clbarrell.duck8 | 1623 | 64 | 160 | 38-122 | 77% (g_iter1 97-29) |
| 31 | **us:arch_rush** | 1473 | 95 | 110 | 62-48 |  |
| 32 | jonters.bling3 | 1452 | 88 | 160 | 17-143 | 90% (g_iter1 114-12) |
| 33 | **us:g_iter0** | 1407 | 89 | 109 | 56-53 |  |
| 34 | HugoIngelsson.Bot21 | 1407 | 203 | 26 | 3-23 |  |
| 35 | awu7.ExplosiveBot | 1369 | 105 | 160 | 11-149 | 96% (g_iter1 121-5) |
| 36 | Metta-AI.bc24scenario | 1338 | 226 | 26 | 2-24 |  |
| 37 | noahzemlin.honeyducklings | 1338 | 226 | 26 | 2-24 |  |
| 38 | sivakovivan.NewHide | 1338 | 226 | 26 | 2-24 |  |
| 39 | JeffLegendPower.v11 | 1249 | 265 | 26 | 1-25 |  |
| 40 | MiloAkerman.v1 | 1249 | 265 | 26 | 1-25 |  |
| 41 | PerishoJ.tx | 1249 | 265 | 26 | 1-25 |  |
| 42 | Peter-Fun.dinoboxer | 1249 | 265 | 26 | 1-25 |  |
| 43 | RyanAspen.v22 | 1249 | 265 | 26 | 1-25 |  |
| 44 | TylerJulian.v9 | 1249 | 265 | 26 | 1-25 |  |
| 45 | aj-chau.cowards | 1249 | 265 | 26 | 1-25 |  |
| 46 | cViper971.ourplayer | 1249 | 265 | 26 | 1-25 |  |
| 47 | dylanzemlin.dangerduck2 | 1249 | 265 | 26 | 1-25 |  |
| 48 | lukerhoads.warrior_2nd_comp | 1249 | 265 | 26 | 1-25 |  |
| 49 | neilhuang007.baseline | 1249 | 265 | 26 | 1-25 |  |
| 50 | polyllc.polyv4 | 1249 | 265 | 26 | 1-25 |  |
| 51 | justinottesen.sprint1 | 1202 | 157 | 160 | 4-156 | 100% (g_iter1 126-0) |
| 52 | andrearante12.turtle | 1142 | 358 | 25 | 0-25 |  |
| 53 | AlexYu84.smartPlayer | 1111 | 358 | 26 | 0-26 |  |
| 54 | H4ffliger.keyboardcrusader_v1 | 1111 | 358 | 26 | 0-26 |  |
| 55 | Lithanium.AttackingBot | 1111 | 358 | 26 | 0-26 |  |
| 56 | Rubrasum.version_3 | 1111 | 358 | 26 | 0-26 |  |
| 57 | SriLakshmiPolavarapu.ducks | 1111 | 358 | 26 | 0-26 |  |
| 58 | VarunVejalla.alexander | 1111 | 358 | 26 | 0-26 |  |
| 59 | abdullah8a0.crayBasic | 1111 | 358 | 26 | 0-26 |  |
| 60 | adamseth2.moveBot1 | 1111 | 358 | 26 | 0-26 |  |
| 61 | dylanconklin.Team3 | 1111 | 358 | 26 | 0-26 |  |
| 62 | itswin.MPAttack | 1111 | 358 | 26 | 0-26 |  |
| 63 | joelcrouch.ducks | 1111 | 358 | 26 | 0-26 |  |
| 64 | lcforges.funkyguy3 | 1111 | 358 | 26 | 0-26 |  |
| 65 | qpwoeirut.tournament_sprint1 | 1111 | 358 | 26 | 0-26 |  |
| 66 | reeceyang.v5 | 1111 | 358 | 26 | 0-26 |  |
| 67 | samithShetty.combustiblelemon | 1111 | 358 | 26 | 0-26 |  |
| 68 | sayam-goyal.SimpleBot | 1111 | 358 | 26 | 0-26 |  |
| 69 | tlevietpdx.Sprint2 | 1111 | 358 | 26 | 0-26 |  |
