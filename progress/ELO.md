# Ladder

219 scrimmages (ours only), 219 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1 | 1756 +- 88 | 14 of 57 | 110 | 81-29 | 73.1% | 30.9% (vs 13) |
| g_iter0 | 1510 +- 78 | 24 of 57 | 109 | 56-53 | 51.5% | 19.6% (vs 22) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | CyrilSharma.finalBot | 1896 | 387 | 4 | 4-0 |  |
| 2 | IvanGeffner.kuma | 1896 | 387 | 4 | 4-0 |  |
| 3 | NotLLeon.v3 | 1896 | 387 | 4 | 4-0 |  |
| 4 | SampleProvider.TSPAARKSPRINT1 | 1896 | 387 | 4 | 4-0 |  |
| 5 | Strequals.duck0127v5 | 1896 | 387 | 4 | 4-0 |  |
| 6 | andli28.v9_USQuals_angle | 1896 | 387 | 4 | 4-0 |  |
| 7 | andrewgopher.player22 | 1896 | 387 | 4 | 4-0 |  |
| 8 | chenyx512.flagbot_final | 1896 | 387 | 4 | 4-0 |  |
| 9 | hsmalladi.finalbot | 1896 | 387 | 4 | 4-0 |  |
| 10 | jmerle.camel_case_v21_final | 1896 | 387 | 4 | 4-0 |  |
| 11 | kyleezz.jeeryfix3 | 1896 | 387 | 4 | 4-0 |  |
| 12 | quesswho.cretplayer2_3 | 1896 | 387 | 4 | 4-0 |  |
| 13 | uravt.Version18Final | 1896 | 387 | 4 | 4-0 |  |
| 14 | **us:g_iter1** | 1756 | 88 | 110 | 81-29 |  |
| 15 | ColtG5.Goob_final | 1720 | 312 | 4 | 3-1 |  |
| 16 | Gymhgy.v10official | 1720 | 312 | 4 | 3-1 |  |
| 17 | winkelmantanner.waffle | 1720 | 312 | 4 | 3-1 |  |
| 18 | Metta-AI.bc24scenario | 1585 | 294 | 4 | 2-2 |  |
| 19 | awu7.ExplosiveBot | 1585 | 294 | 4 | 2-2 |  |
| 20 | clbarrell.duck8 | 1585 | 294 | 4 | 2-2 |  |
| 21 | dmtrung14.defaultplayer_intlqualifier | 1585 | 294 | 4 | 2-2 |  |
| 22 | jonters.bling3 | 1585 | 294 | 4 | 2-2 |  |
| 23 | sivakovivan.NewHide | 1585 | 294 | 4 | 2-2 |  |
| 24 | **us:g_iter0** | 1510 | 78 | 109 | 56-53 |  |
| 25 | HugoIngelsson.Bot21 | 1453 | 307 | 4 | 1-3 |  |
| 26 | JeffLegendPower.v11 | 1453 | 307 | 4 | 1-3 |  |
| 27 | MiloAkerman.v1 | 1453 | 307 | 4 | 1-3 |  |
| 28 | PerishoJ.tx | 1453 | 307 | 4 | 1-3 |  |
| 29 | TylerJulian.v9 | 1453 | 307 | 4 | 1-3 |  |
| 30 | cViper971.ourplayer | 1453 | 307 | 4 | 1-3 |  |
| 31 | justinottesen.sprint1 | 1453 | 307 | 4 | 1-3 |  |
| 32 | lukerhoads.warrior_2nd_comp | 1453 | 307 | 4 | 1-3 |  |
| 33 | polyllc.polyv4 | 1453 | 307 | 4 | 1-3 |  |
| 34 | andrearante12.turtle | 1338 | 392 | 3 | 0-3 |  |
| 35 | AlexYu84.smartPlayer | 1284 | 381 | 4 | 0-4 |  |
| 36 | H4ffliger.keyboardcrusader_v1 | 1284 | 381 | 4 | 0-4 |  |
| 37 | Lithanium.AttackingBot | 1284 | 381 | 4 | 0-4 |  |
| 38 | Peter-Fun.dinoboxer | 1284 | 381 | 4 | 0-4 |  |
| 39 | Rubrasum.version_3 | 1284 | 381 | 4 | 0-4 |  |
| 40 | RyanAspen.v22 | 1284 | 381 | 4 | 0-4 |  |
| 41 | SriLakshmiPolavarapu.ducks | 1284 | 381 | 4 | 0-4 |  |
| 42 | VarunVejalla.alexander | 1284 | 381 | 4 | 0-4 |  |
| 43 | abdullah8a0.crayBasic | 1284 | 381 | 4 | 0-4 |  |
| 44 | adamseth2.moveBot1 | 1284 | 381 | 4 | 0-4 |  |
| 45 | aj-chau.cowards | 1284 | 381 | 4 | 0-4 |  |
| 46 | dylanconklin.Team3 | 1284 | 381 | 4 | 0-4 |  |
| 47 | dylanzemlin.dangerduck2 | 1284 | 381 | 4 | 0-4 |  |
| 48 | itswin.MPAttack | 1284 | 381 | 4 | 0-4 |  |
| 49 | joelcrouch.ducks | 1284 | 381 | 4 | 0-4 |  |
| 50 | lcforges.funkyguy3 | 1284 | 381 | 4 | 0-4 |  |
| 51 | neilhuang007.baseline | 1284 | 381 | 4 | 0-4 |  |
| 52 | noahzemlin.honeyducklings | 1284 | 381 | 4 | 0-4 |  |
| 53 | qpwoeirut.tournament_sprint1 | 1284 | 381 | 4 | 0-4 |  |
| 54 | reeceyang.v5 | 1284 | 381 | 4 | 0-4 |  |
| 55 | samithShetty.combustiblelemon | 1284 | 381 | 4 | 0-4 |  |
| 56 | sayam-goyal.SimpleBot | 1284 | 381 | 4 | 0-4 |  |
| 57 | tlevietpdx.Sprint2 | 1284 | 381 | 4 | 0-4 |  |
