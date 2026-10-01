# Ladder

4269 scrimmages (ours only), 4269 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1914 +- 132 | 15 of 69 | 110 | 84-26 | 75.6% | 18.3% (vs 14) |
| arch_rush10 | 1914 +- 132 | 16 of 69 | 110 | 84-26 | 75.6% | 18.3% (vs 14) |
| g_iter1_c2 | 1914 +- 132 | 17 of 69 | 110 | 84-26 | 75.6% | 18.3% (vs 14) |
| e1aggr | 1888 +- 131 | 19 of 69 | 110 | 83-27 | 74.7% | 18.6% (vs 15) |
| c6pair | 1862 +- 131 | 20 of 69 | 110 | 82-28 | 73.8% | 16.7% (vs 15) |
| a3dig10 | 1862 +- 131 | 21 of 69 | 110 | 82-28 | 73.8% | 16.7% (vs 15) |
| c5bank | 1862 +- 131 | 22 of 69 | 110 | 82-28 | 73.8% | 16.7% (vs 15) |
| g_iter1 | 1844 +- 19 | 23 of 69 | 2670 | 989-1681 | 73.2% | 15.5% (vs 15) |
| a2reloc | 1837 +- 130 | 25 of 69 | 110 | 81-29 | 73.0% | 17.2% (vs 16) |
| e2aggr | 1837 +- 130 | 26 of 69 | 110 | 81-29 | 73.0% | 17.2% (vs 16) |
| a2relay | 1837 +- 130 | 27 of 69 | 110 | 81-29 | 73.0% | 17.2% (vs 16) |
| b1v2 | 1775 +- 60 | 28 of 69 | 280 | 87-193 | 70.8% | 13.3% (vs 16) |
| arch_rush | 1472 +- 95 | 31 of 69 | 110 | 62-48 | 56.4% | 5.5% (vs 18) |
| g_iter0 | 1407 +- 89 | 34 of 69 | 109 | 56-53 | 51.9% | 8.3% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2485 | 172 | 168 | 165-3 | 2% (g_iter1 3-127) |
| 2 | andli28.v9_USQuals_angle | 2412 | 142 | 168 | 163-5 | 4% (g_iter1 5-125) |
| 3 | uravt.Version18Final | 2406 | 348 | 26 | 26-0 |  |
| 4 | jmerle.camel_case_v21_final | 2384 | 132 | 168 | 162-6 | 4% (g_iter1 5-125) |
| 5 | IvanGeffner.kuma | 2338 | 117 | 168 | 160-8 | 5% (g_iter1 6-124) |
| 6 | chenyx512.flagbot_final | 2338 | 117 | 168 | 160-8 | 5% (g_iter1 6-124) |
| 7 | NotLLeon.v3 | 2301 | 106 | 168 | 158-10 | 8% (g_iter1 10-120) |
| 8 | andrewgopher.player22 | 2256 | 95 | 168 | 155-13 | 10% (g_iter1 13-117) |
| 9 | Gymhgy.v10official | 2197 | 83 | 168 | 150-18 | 14% (g_iter1 18-112) |
| 10 | hsmalladi.finalbot | 2111 | 70 | 168 | 140-28 | 13% (g_iter1 17-113) |
| 11 | CyrilSharma.finalBot | 2064 | 64 | 168 | 133-35 | 23% (g_iter1 30-100) |
| 12 | winkelmantanner.waffle | 2058 | 64 | 168 | 132-36 | 25% (g_iter1 32-98) |
| 13 | ColtG5.Goob_final | 1930 | 55 | 168 | 107-61 | 40% (g_iter1 52-78) |
| 14 | quesswho.cretplayer2_3 | 1926 | 55 | 168 | 106-62 | 35% (g_iter1 46-84) |
| 15 | **us:a3dig5** | 1914 | 132 | 110 | 84-26 |  |
| 16 | **us:arch_rush10** | 1914 | 132 | 110 | 84-26 |  |
| 17 | **us:g_iter1_c2** | 1914 | 132 | 110 | 84-26 |  |
| 18 | kyleezz.jeeryfix3 | 1899 | 54 | 168 | 100-68 | 43% (g_iter1 56-74) |
| 19 | **us:e1aggr** | 1888 | 131 | 110 | 83-27 |  |
| 20 | **us:c6pair** | 1862 | 131 | 110 | 82-28 |  |
| 21 | **us:a3dig10** | 1862 | 131 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1862 | 131 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1844 | 19 | 2670 | 989-1681 |  |
| 24 | SampleProvider.TSPAARKSPRINT1 | 1840 | 53 | 168 | 86-82 | 49% (g_iter1 64-66) |
| 25 | **us:a2reloc** | 1837 | 130 | 110 | 81-29 |  |
| 26 | **us:e2aggr** | 1837 | 130 | 110 | 81-29 |  |
| 27 | **us:a2relay** | 1837 | 130 | 110 | 81-29 |  |
| 28 | **us:b1v2** | 1775 | 60 | 280 | 87-193 |  |
| 29 | dmtrung14.defaultplayer_intlqualifier | 1709 | 56 | 168 | 56-112 | 65% (g_iter1 85-45) |
| 30 | clbarrell.duck8 | 1619 | 63 | 168 | 39-129 | 77% (g_iter1 100-30) |
| 31 | **us:arch_rush** | 1472 | 95 | 110 | 62-48 |  |
| 32 | jonters.bling3 | 1443 | 87 | 168 | 17-151 | 91% (g_iter1 118-12) |
| 33 | HugoIngelsson.Bot21 | 1407 | 204 | 26 | 3-23 |  |
| 34 | **us:g_iter0** | 1407 | 89 | 109 | 56-53 |  |
| 35 | awu7.ExplosiveBot | 1362 | 105 | 168 | 11-157 | 96% (g_iter1 125-5) |
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
| 51 | justinottesen.sprint1 | 1195 | 157 | 168 | 4-164 | 100% (g_iter1 130-0) |
| 52 | andrearante12.turtle | 1143 | 358 | 25 | 0-25 |  |
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
