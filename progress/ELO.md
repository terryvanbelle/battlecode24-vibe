# Ladder

879 scrimmages (ours only), 879 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1896 +- 119 | 15 of 63 | 110 | 84-26 | 75.6% | 21.7% (vs 14) |
| g_iter1_c2 | 1896 +- 119 | 16 of 63 | 110 | 84-26 | 75.6% | 21.7% (vs 14) |
| arch_rush10 | 1896 +- 119 | 17 of 63 | 110 | 84-26 | 75.6% | 21.7% (vs 14) |
| a2relay | 1834 +- 116 | 20 of 63 | 110 | 81-29 | 73.0% | 20.7% (vs 16) |
| g_iter1 | 1834 +- 116 | 21 of 63 | 110 | 81-29 | 73.0% | 20.7% (vs 16) |
| a2reloc | 1834 +- 116 | 22 of 63 | 110 | 81-29 | 73.0% | 20.7% (vs 16) |
| arch_rush | 1528 +- 91 | 28 of 63 | 110 | 62-48 | 56.3% | 14.5% (vs 21) |
| g_iter0 | 1467 +- 87 | 30 of 63 | 109 | 56-53 | 51.7% | 13.2% (vs 22) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | IvanGeffner.kuma | 2296 | 354 | 16 | 16-0 |  |
| 2 | NotLLeon.v3 | 2296 | 354 | 16 | 16-0 |  |
| 3 | Strequals.duck0127v5 | 2296 | 354 | 16 | 16-0 |  |
| 4 | andli28.v9_USQuals_angle | 2296 | 354 | 16 | 16-0 |  |
| 5 | andrewgopher.player22 | 2296 | 354 | 16 | 16-0 |  |
| 6 | uravt.Version18Final | 2296 | 354 | 16 | 16-0 |  |
| 7 | Gymhgy.v10official | 2160 | 261 | 16 | 15-1 |  |
| 8 | chenyx512.flagbot_final | 2160 | 261 | 16 | 15-1 |  |
| 9 | jmerle.camel_case_v21_final | 2160 | 261 | 16 | 15-1 |  |
| 10 | CyrilSharma.finalBot | 2074 | 223 | 16 | 14-2 |  |
| 11 | winkelmantanner.waffle | 2074 | 223 | 16 | 14-2 |  |
| 12 | kyleezz.jeeryfix3 | 1949 | 190 | 16 | 12-4 |  |
| 13 | SampleProvider.TSPAARKSPRINT1 | 1897 | 183 | 16 | 11-5 |  |
| 14 | hsmalladi.finalbot | 1897 | 183 | 16 | 11-5 |  |
| 15 | **us:a3dig5** | 1896 | 119 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1896 | 119 | 110 | 84-26 |  |
| 17 | **us:arch_rush10** | 1896 | 119 | 110 | 84-26 |  |
| 18 | ColtG5.Goob_final | 1848 | 180 | 16 | 10-6 |  |
| 19 | quesswho.cretplayer2_3 | 1848 | 180 | 16 | 10-6 |  |
| 20 | **us:a2relay** | 1834 | 116 | 110 | 81-29 |  |
| 21 | **us:g_iter1** | 1834 | 116 | 110 | 81-29 |  |
| 22 | **us:a2reloc** | 1834 | 116 | 110 | 81-29 |  |
| 23 | dmtrung14.defaultplayer_intlqualifier | 1653 | 186 | 16 | 6-10 |  |
| 24 | awu7.ExplosiveBot | 1542 | 201 | 16 | 4-12 |  |
| 25 | clbarrell.duck8 | 1542 | 201 | 16 | 4-12 |  |
| 26 | jonters.bling3 | 1542 | 201 | 16 | 4-12 |  |
| 27 | justinottesen.sprint1 | 1542 | 201 | 16 | 4-12 |  |
| 28 | **us:arch_rush** | 1528 | 91 | 110 | 62-48 |  |
| 29 | HugoIngelsson.Bot21 | 1478 | 213 | 16 | 3-13 |  |
| 30 | **us:g_iter0** | 1467 | 87 | 109 | 56-53 |  |
| 31 | Metta-AI.bc24scenario | 1403 | 233 | 16 | 2-14 |  |
| 32 | noahzemlin.honeyducklings | 1403 | 233 | 16 | 2-14 |  |
| 33 | sivakovivan.NewHide | 1403 | 233 | 16 | 2-14 |  |
| 34 | JeffLegendPower.v11 | 1310 | 270 | 16 | 1-15 |  |
| 35 | MiloAkerman.v1 | 1310 | 270 | 16 | 1-15 |  |
| 36 | PerishoJ.tx | 1310 | 270 | 16 | 1-15 |  |
| 37 | Peter-Fun.dinoboxer | 1310 | 270 | 16 | 1-15 |  |
| 38 | RyanAspen.v22 | 1310 | 270 | 16 | 1-15 |  |
| 39 | TylerJulian.v9 | 1310 | 270 | 16 | 1-15 |  |
| 40 | aj-chau.cowards | 1310 | 270 | 16 | 1-15 |  |
| 41 | cViper971.ourplayer | 1310 | 270 | 16 | 1-15 |  |
| 42 | dylanzemlin.dangerduck2 | 1310 | 270 | 16 | 1-15 |  |
| 43 | lukerhoads.warrior_2nd_comp | 1310 | 270 | 16 | 1-15 |  |
| 44 | neilhuang007.baseline | 1310 | 270 | 16 | 1-15 |  |
| 45 | polyllc.polyv4 | 1310 | 270 | 16 | 1-15 |  |
| 46 | andrearante12.turtle | 1200 | 362 | 15 | 0-15 |  |
| 47 | AlexYu84.smartPlayer | 1168 | 361 | 16 | 0-16 |  |
| 48 | H4ffliger.keyboardcrusader_v1 | 1168 | 361 | 16 | 0-16 |  |
| 49 | Lithanium.AttackingBot | 1168 | 361 | 16 | 0-16 |  |
| 50 | Rubrasum.version_3 | 1168 | 361 | 16 | 0-16 |  |
| 51 | SriLakshmiPolavarapu.ducks | 1168 | 361 | 16 | 0-16 |  |
| 52 | VarunVejalla.alexander | 1168 | 361 | 16 | 0-16 |  |
| 53 | abdullah8a0.crayBasic | 1168 | 361 | 16 | 0-16 |  |
| 54 | adamseth2.moveBot1 | 1168 | 361 | 16 | 0-16 |  |
| 55 | dylanconklin.Team3 | 1168 | 361 | 16 | 0-16 |  |
| 56 | itswin.MPAttack | 1168 | 361 | 16 | 0-16 |  |
| 57 | joelcrouch.ducks | 1168 | 361 | 16 | 0-16 |  |
| 58 | lcforges.funkyguy3 | 1168 | 361 | 16 | 0-16 |  |
| 59 | qpwoeirut.tournament_sprint1 | 1168 | 361 | 16 | 0-16 |  |
| 60 | reeceyang.v5 | 1168 | 361 | 16 | 0-16 |  |
| 61 | samithShetty.combustiblelemon | 1168 | 361 | 16 | 0-16 |  |
| 62 | sayam-goyal.SimpleBot | 1168 | 361 | 16 | 0-16 |  |
| 63 | tlevietpdx.Sprint2 | 1168 | 361 | 16 | 0-16 |  |
