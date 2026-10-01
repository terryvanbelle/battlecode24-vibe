# Ladder

3869 scrimmages (ours only), 3869 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1910 +- 131 | 15 of 69 | 110 | 84-26 | 75.6% | 18.4% (vs 14) |
| g_iter1_c2 | 1910 +- 131 | 16 of 69 | 110 | 84-26 | 75.6% | 18.4% (vs 14) |
| arch_rush10 | 1910 +- 131 | 17 of 69 | 110 | 84-26 | 75.6% | 18.4% (vs 14) |
| e1aggr | 1885 +- 130 | 18 of 69 | 110 | 83-27 | 74.7% | 16.6% (vs 14) |
| c5bank | 1859 +- 130 | 20 of 69 | 110 | 82-28 | 73.8% | 17.1% (vs 15) |
| c6pair | 1859 +- 130 | 21 of 69 | 110 | 82-28 | 73.8% | 17.1% (vs 15) |
| a3dig10 | 1859 +- 130 | 22 of 69 | 110 | 82-28 | 73.8% | 17.1% (vs 15) |
| g_iter1 | 1841 +- 20 | 23 of 69 | 2470 | 919-1551 | 73.2% | 15.9% (vs 15) |
| e2aggr | 1834 +- 129 | 25 of 69 | 110 | 81-29 | 73.0% | 17.6% (vs 16) |
| a2reloc | 1834 +- 129 | 26 of 69 | 110 | 81-29 | 73.0% | 17.6% (vs 16) |
| a2relay | 1834 +- 129 | 27 of 69 | 110 | 81-29 | 73.0% | 17.6% (vs 16) |
| b1v2 | 1803 +- 109 | 28 of 69 | 80 | 27-53 | 71.9% | 15.5% (vs 16) |
| arch_rush | 1472 +- 95 | 31 of 69 | 110 | 62-48 | 56.4% | 5.4% (vs 18) |
| g_iter0 | 1407 +- 90 | 33 of 69 | 109 | 56-53 | 51.9% | 6.0% (vs 19) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2463 | 173 | 148 | 145-3 | 2% (g_iter1 3-117) |
| 2 | uravt.Version18Final | 2403 | 348 | 26 | 26-0 |  |
| 3 | andli28.v9_USQuals_angle | 2390 | 142 | 148 | 143-5 | 4% (g_iter1 5-115) |
| 4 | chenyx512.flagbot_final | 2390 | 142 | 148 | 143-5 | 3% (g_iter1 4-116) |
| 5 | jmerle.camel_case_v21_final | 2362 | 132 | 148 | 142-6 | 4% (g_iter1 5-115) |
| 6 | IvanGeffner.kuma | 2338 | 124 | 148 | 141-7 | 5% (g_iter1 6-114) |
| 7 | NotLLeon.v3 | 2296 | 112 | 148 | 139-9 | 8% (g_iter1 9-111) |
| 8 | andrewgopher.player22 | 2233 | 96 | 148 | 135-13 | 11% (g_iter1 13-107) |
| 9 | Gymhgy.v10official | 2184 | 86 | 148 | 131-17 | 14% (g_iter1 17-103) |
| 10 | hsmalladi.finalbot | 2117 | 75 | 148 | 124-24 | 12% (g_iter1 14-106) |
| 11 | CyrilSharma.finalBot | 2070 | 69 | 148 | 118-30 | 22% (g_iter1 27-93) |
| 12 | winkelmantanner.waffle | 2050 | 67 | 148 | 115-33 | 26% (g_iter1 31-89) |
| 13 | ColtG5.Goob_final | 1915 | 58 | 148 | 91-57 | 41% (g_iter1 49-71) |
| 14 | quesswho.cretplayer2_3 | 1915 | 58 | 148 | 91-57 | 36% (g_iter1 43-77) |
| 15 | **us:a3dig5** | 1910 | 131 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1910 | 131 | 110 | 84-26 |  |
| 17 | **us:arch_rush10** | 1910 | 131 | 110 | 84-26 |  |
| 18 | **us:e1aggr** | 1885 | 130 | 110 | 83-27 |  |
| 19 | kyleezz.jeeryfix3 | 1880 | 57 | 148 | 84-64 | 44% (g_iter1 53-67) |
| 20 | **us:c5bank** | 1859 | 130 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1859 | 130 | 110 | 82-28 |  |
| 22 | **us:a3dig10** | 1859 | 130 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1841 | 20 | 2470 | 919-1551 |  |
| 24 | SampleProvider.TSPAARKSPRINT1 | 1837 | 56 | 148 | 75-73 | 49% (g_iter1 59-61) |
| 25 | **us:e2aggr** | 1834 | 129 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1834 | 129 | 110 | 81-29 |  |
| 27 | **us:a2relay** | 1834 | 129 | 110 | 81-29 |  |
| 28 | **us:b1v2** | 1803 | 109 | 80 | 27-53 |  |
| 29 | dmtrung14.defaultplayer_intlqualifier | 1718 | 60 | 148 | 51-97 | 65% (g_iter1 78-42) |
| 30 | clbarrell.duck8 | 1637 | 66 | 148 | 37-111 | 76% (g_iter1 91-29) |
| 31 | **us:arch_rush** | 1472 | 95 | 110 | 62-48 |  |
| 32 | jonters.bling3 | 1457 | 91 | 148 | 16-132 | 90% (g_iter1 108-12) |
| 33 | **us:g_iter0** | 1407 | 90 | 109 | 56-53 |  |
| 34 | HugoIngelsson.Bot21 | 1406 | 203 | 26 | 3-23 |  |
| 35 | awu7.ExplosiveBot | 1349 | 115 | 148 | 9-139 | 97% (g_iter1 116-4) |
| 36 | Metta-AI.bc24scenario | 1337 | 226 | 26 | 2-24 |  |
| 37 | noahzemlin.honeyducklings | 1337 | 226 | 26 | 2-24 |  |
| 38 | sivakovivan.NewHide | 1337 | 226 | 26 | 2-24 |  |
| 39 | JeffLegendPower.v11 | 1248 | 265 | 26 | 1-25 |  |
| 40 | MiloAkerman.v1 | 1248 | 265 | 26 | 1-25 |  |
| 41 | PerishoJ.tx | 1248 | 265 | 26 | 1-25 |  |
| 42 | Peter-Fun.dinoboxer | 1248 | 265 | 26 | 1-25 |  |
| 43 | RyanAspen.v22 | 1248 | 265 | 26 | 1-25 |  |
| 44 | TylerJulian.v9 | 1248 | 265 | 26 | 1-25 |  |
| 45 | aj-chau.cowards | 1248 | 265 | 26 | 1-25 |  |
| 46 | cViper971.ourplayer | 1248 | 265 | 26 | 1-25 |  |
| 47 | dylanzemlin.dangerduck2 | 1248 | 265 | 26 | 1-25 |  |
| 48 | lukerhoads.warrior_2nd_comp | 1248 | 265 | 26 | 1-25 |  |
| 49 | neilhuang007.baseline | 1248 | 265 | 26 | 1-25 |  |
| 50 | polyllc.polyv4 | 1248 | 265 | 26 | 1-25 |  |
| 51 | justinottesen.sprint1 | 1216 | 158 | 148 | 4-144 | 100% (g_iter1 120-0) |
| 52 | andrearante12.turtle | 1141 | 358 | 25 | 0-25 |  |
| 53 | AlexYu84.smartPlayer | 1110 | 358 | 26 | 0-26 |  |
| 54 | H4ffliger.keyboardcrusader_v1 | 1110 | 358 | 26 | 0-26 |  |
| 55 | Lithanium.AttackingBot | 1110 | 358 | 26 | 0-26 |  |
| 56 | Rubrasum.version_3 | 1110 | 358 | 26 | 0-26 |  |
| 57 | SriLakshmiPolavarapu.ducks | 1110 | 358 | 26 | 0-26 |  |
| 58 | VarunVejalla.alexander | 1110 | 358 | 26 | 0-26 |  |
| 59 | abdullah8a0.crayBasic | 1110 | 358 | 26 | 0-26 |  |
| 60 | adamseth2.moveBot1 | 1110 | 358 | 26 | 0-26 |  |
| 61 | dylanconklin.Team3 | 1110 | 358 | 26 | 0-26 |  |
| 62 | itswin.MPAttack | 1110 | 358 | 26 | 0-26 |  |
| 63 | joelcrouch.ducks | 1110 | 358 | 26 | 0-26 |  |
| 64 | lcforges.funkyguy3 | 1110 | 358 | 26 | 0-26 |  |
| 65 | qpwoeirut.tournament_sprint1 | 1110 | 358 | 26 | 0-26 |  |
| 66 | reeceyang.v5 | 1110 | 358 | 26 | 0-26 |  |
| 67 | samithShetty.combustiblelemon | 1110 | 358 | 26 | 0-26 |  |
| 68 | sayam-goyal.SimpleBot | 1110 | 358 | 26 | 0-26 |  |
| 69 | tlevietpdx.Sprint2 | 1110 | 358 | 26 | 0-26 |  |
