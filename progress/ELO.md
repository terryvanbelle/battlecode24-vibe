# Ladder

549 scrimmages (ours only), 549 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1868 +- 109 | 13 of 60 | 110 | 84-26 | 75.6% | 21.6% (vs 12) |
| a2relay | 1816 +- 107 | 18 of 60 | 110 | 81-29 | 73.0% | 23.5% (vs 16) |
| a2reloc | 1816 +- 107 | 19 of 60 | 110 | 81-29 | 73.0% | 23.5% (vs 16) |
| g_iter1 | 1816 +- 107 | 20 of 60 | 110 | 81-29 | 73.0% | 23.5% (vs 16) |
| g_iter0 | 1488 +- 84 | 28 of 60 | 109 | 56-53 | 51.6% | 17.0% (vs 23) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | IvanGeffner.kuma | 2183 | 361 | 10 | 10-0 |  |
| 2 | NotLLeon.v3 | 2183 | 361 | 10 | 10-0 |  |
| 3 | Strequals.duck0127v5 | 2183 | 361 | 10 | 10-0 |  |
| 4 | andli28.v9_USQuals_angle | 2183 | 361 | 10 | 10-0 |  |
| 5 | andrewgopher.player22 | 2183 | 361 | 10 | 10-0 |  |
| 6 | uravt.Version18Final | 2183 | 361 | 10 | 10-0 |  |
| 7 | Gymhgy.v10official | 2040 | 272 | 10 | 9-1 |  |
| 8 | chenyx512.flagbot_final | 2040 | 272 | 10 | 9-1 |  |
| 9 | jmerle.camel_case_v21_final | 2040 | 272 | 10 | 9-1 |  |
| 10 | kyleezz.jeeryfix3 | 2040 | 272 | 10 | 9-1 |  |
| 11 | winkelmantanner.waffle | 2040 | 272 | 10 | 9-1 |  |
| 12 | CyrilSharma.finalBot | 1944 | 238 | 10 | 8-2 |  |
| 13 | **us:g_iter1_c2** | 1868 | 109 | 110 | 84-26 |  |
| 14 | ColtG5.Goob_final | 1866 | 222 | 10 | 7-3 |  |
| 15 | SampleProvider.TSPAARKSPRINT1 | 1866 | 222 | 10 | 7-3 |  |
| 16 | hsmalladi.finalbot | 1866 | 222 | 10 | 7-3 |  |
| 17 | quesswho.cretplayer2_3 | 1866 | 222 | 10 | 7-3 |  |
| 18 | **us:a2relay** | 1816 | 107 | 110 | 81-29 |  |
| 19 | **us:a2reloc** | 1816 | 107 | 110 | 81-29 |  |
| 20 | **us:g_iter1** | 1816 | 107 | 110 | 81-29 |  |
| 21 | dmtrung14.defaultplayer_intlqualifier | 1653 | 221 | 10 | 4-6 |  |
| 22 | awu7.ExplosiveBot | 1577 | 231 | 10 | 3-7 |  |
| 23 | Metta-AI.bc24scenario | 1492 | 248 | 10 | 2-8 |  |
| 24 | clbarrell.duck8 | 1492 | 248 | 10 | 2-8 |  |
| 25 | jonters.bling3 | 1492 | 248 | 10 | 2-8 |  |
| 26 | justinottesen.sprint1 | 1492 | 248 | 10 | 2-8 |  |
| 27 | sivakovivan.NewHide | 1492 | 248 | 10 | 2-8 |  |
| 28 | **us:g_iter0** | 1488 | 84 | 109 | 56-53 |  |
| 29 | HugoIngelsson.Bot21 | 1388 | 282 | 10 | 1-9 |  |
| 30 | JeffLegendPower.v11 | 1388 | 282 | 10 | 1-9 |  |
| 31 | MiloAkerman.v1 | 1388 | 282 | 10 | 1-9 |  |
| 32 | PerishoJ.tx | 1388 | 282 | 10 | 1-9 |  |
| 33 | TylerJulian.v9 | 1388 | 282 | 10 | 1-9 |  |
| 34 | cViper971.ourplayer | 1388 | 282 | 10 | 1-9 |  |
| 35 | lukerhoads.warrior_2nd_comp | 1388 | 282 | 10 | 1-9 |  |
| 36 | polyllc.polyv4 | 1388 | 282 | 10 | 1-9 |  |
| 37 | andrearante12.turtle | 1280 | 372 | 9 | 0-9 |  |
| 38 | AlexYu84.smartPlayer | 1237 | 368 | 10 | 0-10 |  |
| 39 | H4ffliger.keyboardcrusader_v1 | 1237 | 368 | 10 | 0-10 |  |
| 40 | Lithanium.AttackingBot | 1237 | 368 | 10 | 0-10 |  |
| 41 | Peter-Fun.dinoboxer | 1237 | 368 | 10 | 0-10 |  |
| 42 | Rubrasum.version_3 | 1237 | 368 | 10 | 0-10 |  |
| 43 | RyanAspen.v22 | 1237 | 368 | 10 | 0-10 |  |
| 44 | SriLakshmiPolavarapu.ducks | 1237 | 368 | 10 | 0-10 |  |
| 45 | VarunVejalla.alexander | 1237 | 368 | 10 | 0-10 |  |
| 46 | abdullah8a0.crayBasic | 1237 | 368 | 10 | 0-10 |  |
| 47 | adamseth2.moveBot1 | 1237 | 368 | 10 | 0-10 |  |
| 48 | aj-chau.cowards | 1237 | 368 | 10 | 0-10 |  |
| 49 | dylanconklin.Team3 | 1237 | 368 | 10 | 0-10 |  |
| 50 | dylanzemlin.dangerduck2 | 1237 | 368 | 10 | 0-10 |  |
| 51 | itswin.MPAttack | 1237 | 368 | 10 | 0-10 |  |
| 52 | joelcrouch.ducks | 1237 | 368 | 10 | 0-10 |  |
| 53 | lcforges.funkyguy3 | 1237 | 368 | 10 | 0-10 |  |
| 54 | neilhuang007.baseline | 1237 | 368 | 10 | 0-10 |  |
| 55 | noahzemlin.honeyducklings | 1237 | 368 | 10 | 0-10 |  |
| 56 | qpwoeirut.tournament_sprint1 | 1237 | 368 | 10 | 0-10 |  |
| 57 | reeceyang.v5 | 1237 | 368 | 10 | 0-10 |  |
| 58 | samithShetty.combustiblelemon | 1237 | 368 | 10 | 0-10 |  |
| 59 | sayam-goyal.SimpleBot | 1237 | 368 | 10 | 0-10 |  |
| 60 | tlevietpdx.Sprint2 | 1237 | 368 | 10 | 0-10 |  |
