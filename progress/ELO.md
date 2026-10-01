# Ladder

1429 scrimmages (ours only), 1429 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| arch_rush10 | 1909 +- 133 | 14 of 68 | 110 | 84-26 | 75.6% | 15.0% (vs 13) |
| g_iter1_c2 | 1909 +- 133 | 15 of 68 | 110 | 84-26 | 75.6% | 15.0% (vs 13) |
| a3dig5 | 1909 +- 133 | 16 of 68 | 110 | 84-26 | 75.6% | 15.0% (vs 13) |
| e1aggr | 1883 +- 132 | 17 of 68 | 110 | 83-27 | 74.7% | 13.4% (vs 13) |
| c5bank | 1857 +- 131 | 19 of 68 | 110 | 82-28 | 73.8% | 14.5% (vs 14) |
| a3dig10 | 1857 +- 131 | 20 of 68 | 110 | 82-28 | 73.8% | 14.5% (vs 14) |
| c6pair | 1857 +- 131 | 21 of 68 | 110 | 82-28 | 73.8% | 14.5% (vs 14) |
| a2relay | 1832 +- 129 | 23 of 68 | 110 | 81-29 | 73.0% | 15.3% (vs 15) |
| e2aggr | 1832 +- 129 | 24 of 68 | 110 | 81-29 | 73.0% | 15.3% (vs 15) |
| a2reloc | 1832 +- 129 | 25 of 68 | 110 | 81-29 | 73.0% | 15.3% (vs 15) |
| g_iter1 | 1832 +- 129 | 26 of 68 | 110 | 81-29 | 73.0% | 15.3% (vs 15) |
| arch_rush | 1483 +- 94 | 31 of 68 | 110 | 62-48 | 56.4% | 9.2% (vs 19) |
| g_iter0 | 1418 +- 89 | 34 of 68 | 109 | 56-53 | 51.9% | 10.5% (vs 21) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | NotLLeon.v3 | 2401 | 348 | 26 | 26-0 |  |
| 2 | Strequals.duck0127v5 | 2401 | 348 | 26 | 26-0 |  |
| 3 | andli28.v9_USQuals_angle | 2401 | 348 | 26 | 26-0 |  |
| 4 | andrewgopher.player22 | 2401 | 348 | 26 | 26-0 |  |
| 5 | uravt.Version18Final | 2401 | 348 | 26 | 26-0 |  |
| 6 | Gymhgy.v10official | 2272 | 252 | 26 | 25-1 |  |
| 7 | IvanGeffner.kuma | 2272 | 252 | 26 | 25-1 |  |
| 8 | chenyx512.flagbot_final | 2272 | 252 | 26 | 25-1 |  |
| 9 | jmerle.camel_case_v21_final | 2272 | 252 | 26 | 25-1 |  |
| 10 | CyrilSharma.finalBot | 2193 | 211 | 26 | 24-2 |  |
| 11 | winkelmantanner.waffle | 2193 | 211 | 26 | 24-2 |  |
| 12 | ColtG5.Goob_final | 1941 | 144 | 26 | 18-8 |  |
| 13 | kyleezz.jeeryfix3 | 1941 | 144 | 26 | 18-8 |  |
| 14 | **us:arch_rush10** | 1909 | 133 | 110 | 84-26 |  |
| 15 | **us:g_iter1_c2** | 1909 | 133 | 110 | 84-26 |  |
| 16 | **us:a3dig5** | 1909 | 133 | 110 | 84-26 |  |
| 17 | **us:e1aggr** | 1883 | 132 | 110 | 83-27 |  |
| 18 | hsmalladi.finalbot | 1881 | 140 | 26 | 16-10 |  |
| 19 | **us:c5bank** | 1857 | 131 | 110 | 82-28 |  |
| 20 | **us:a3dig10** | 1857 | 131 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1857 | 131 | 110 | 82-28 |  |
| 22 | SampleProvider.TSPAARKSPRINT1 | 1852 | 139 | 26 | 15-11 |  |
| 23 | **us:a2relay** | 1832 | 129 | 110 | 81-29 |  |
| 24 | **us:e2aggr** | 1832 | 129 | 110 | 81-29 |  |
| 25 | **us:a2reloc** | 1832 | 129 | 110 | 81-29 |  |
| 26 | **us:g_iter1** | 1832 | 129 | 110 | 81-29 |  |
| 27 | quesswho.cretplayer2_3 | 1823 | 139 | 26 | 14-12 |  |
| 28 | dmtrung14.defaultplayer_intlqualifier | 1598 | 160 | 26 | 7-19 |  |
| 29 | clbarrell.duck8 | 1558 | 167 | 26 | 6-20 |  |
| 30 | awu7.ExplosiveBot | 1514 | 176 | 26 | 5-21 |  |
| 31 | **us:arch_rush** | 1483 | 94 | 110 | 62-48 |  |
| 32 | jonters.bling3 | 1464 | 187 | 26 | 4-22 |  |
| 33 | justinottesen.sprint1 | 1464 | 187 | 26 | 4-22 |  |
| 34 | **us:g_iter0** | 1418 | 89 | 109 | 56-53 |  |
| 35 | HugoIngelsson.Bot21 | 1408 | 202 | 26 | 3-23 |  |
| 36 | Metta-AI.bc24scenario | 1340 | 225 | 26 | 2-24 |  |
| 37 | noahzemlin.honeyducklings | 1340 | 225 | 26 | 2-24 |  |
| 38 | sivakovivan.NewHide | 1340 | 225 | 26 | 2-24 |  |
| 39 | JeffLegendPower.v11 | 1252 | 264 | 26 | 1-25 |  |
| 40 | MiloAkerman.v1 | 1252 | 264 | 26 | 1-25 |  |
| 41 | PerishoJ.tx | 1252 | 264 | 26 | 1-25 |  |
| 42 | Peter-Fun.dinoboxer | 1252 | 264 | 26 | 1-25 |  |
| 43 | RyanAspen.v22 | 1252 | 264 | 26 | 1-25 |  |
| 44 | TylerJulian.v9 | 1252 | 264 | 26 | 1-25 |  |
| 45 | aj-chau.cowards | 1252 | 264 | 26 | 1-25 |  |
| 46 | cViper971.ourplayer | 1252 | 264 | 26 | 1-25 |  |
| 47 | dylanzemlin.dangerduck2 | 1252 | 264 | 26 | 1-25 |  |
| 48 | lukerhoads.warrior_2nd_comp | 1252 | 264 | 26 | 1-25 |  |
| 49 | neilhuang007.baseline | 1252 | 264 | 26 | 1-25 |  |
| 50 | polyllc.polyv4 | 1252 | 264 | 26 | 1-25 |  |
| 51 | andrearante12.turtle | 1145 | 357 | 25 | 0-25 |  |
| 52 | AlexYu84.smartPlayer | 1114 | 357 | 26 | 0-26 |  |
| 53 | H4ffliger.keyboardcrusader_v1 | 1114 | 357 | 26 | 0-26 |  |
| 54 | Lithanium.AttackingBot | 1114 | 357 | 26 | 0-26 |  |
| 55 | Rubrasum.version_3 | 1114 | 357 | 26 | 0-26 |  |
| 56 | SriLakshmiPolavarapu.ducks | 1114 | 357 | 26 | 0-26 |  |
| 57 | VarunVejalla.alexander | 1114 | 357 | 26 | 0-26 |  |
| 58 | abdullah8a0.crayBasic | 1114 | 357 | 26 | 0-26 |  |
| 59 | adamseth2.moveBot1 | 1114 | 357 | 26 | 0-26 |  |
| 60 | dylanconklin.Team3 | 1114 | 357 | 26 | 0-26 |  |
| 61 | itswin.MPAttack | 1114 | 357 | 26 | 0-26 |  |
| 62 | joelcrouch.ducks | 1114 | 357 | 26 | 0-26 |  |
| 63 | lcforges.funkyguy3 | 1114 | 357 | 26 | 0-26 |  |
| 64 | qpwoeirut.tournament_sprint1 | 1114 | 357 | 26 | 0-26 |  |
| 65 | reeceyang.v5 | 1114 | 357 | 26 | 0-26 |  |
| 66 | samithShetty.combustiblelemon | 1114 | 357 | 26 | 0-26 |  |
| 67 | sayam-goyal.SimpleBot | 1114 | 357 | 26 | 0-26 |  |
| 68 | tlevietpdx.Sprint2 | 1114 | 357 | 26 | 0-26 |  |
