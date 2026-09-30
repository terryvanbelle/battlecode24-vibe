# Ladder

659 scrimmages (ours only), 659 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter1_c2 | 1892 +- 111 | 17 of 61 | 110 | 84-26 | 75.6% | 27.9% (vs 16) |
| g_iter1 | 1837 +- 110 | 18 of 61 | 110 | 81-29 | 73.0% | 22.5% (vs 16) |
| a2relay | 1837 +- 110 | 19 of 61 | 110 | 81-29 | 73.0% | 22.5% (vs 16) |
| a2reloc | 1837 +- 110 | 20 of 61 | 110 | 81-29 | 73.0% | 22.5% (vs 16) |
| arch_rush | 1546 +- 91 | 26 of 61 | 110 | 62-48 | 56.2% | 14.2% (vs 21) |
| g_iter0 | 1485 +- 87 | 28 of 61 | 109 | 56-53 | 51.7% | 12.7% (vs 22) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | IvanGeffner.kuma | 2212 | 360 | 12 | 12-0 |  |
| 2 | NotLLeon.v3 | 2212 | 360 | 12 | 12-0 |  |
| 3 | Strequals.duck0127v5 | 2212 | 360 | 12 | 12-0 |  |
| 4 | andli28.v9_USQuals_angle | 2212 | 360 | 12 | 12-0 |  |
| 5 | andrewgopher.player22 | 2212 | 360 | 12 | 12-0 |  |
| 6 | uravt.Version18Final | 2212 | 360 | 12 | 12-0 |  |
| 7 | Gymhgy.v10official | 2070 | 270 | 12 | 11-1 |  |
| 8 | chenyx512.flagbot_final | 2070 | 270 | 12 | 11-1 |  |
| 9 | jmerle.camel_case_v21_final | 2070 | 270 | 12 | 11-1 |  |
| 10 | kyleezz.jeeryfix3 | 2070 | 270 | 12 | 11-1 |  |
| 11 | winkelmantanner.waffle | 2070 | 270 | 12 | 11-1 |  |
| 12 | CyrilSharma.finalBot | 1977 | 234 | 12 | 10-2 |  |
| 13 | ColtG5.Goob_final | 1901 | 217 | 12 | 9-3 |  |
| 14 | SampleProvider.TSPAARKSPRINT1 | 1901 | 217 | 12 | 9-3 |  |
| 15 | hsmalladi.finalbot | 1901 | 217 | 12 | 9-3 |  |
| 16 | quesswho.cretplayer2_3 | 1901 | 217 | 12 | 9-3 |  |
| 17 | **us:g_iter1_c2** | 1892 | 111 | 110 | 84-26 |  |
| 18 | **us:g_iter1** | 1837 | 110 | 110 | 81-29 |  |
| 19 | **us:a2relay** | 1837 | 110 | 110 | 81-29 |  |
| 20 | **us:a2reloc** | 1837 | 110 | 110 | 81-29 |  |
| 21 | dmtrung14.defaultplayer_intlqualifier | 1709 | 204 | 12 | 6-6 |  |
| 22 | awu7.ExplosiveBot | 1581 | 212 | 12 | 4-8 |  |
| 23 | clbarrell.duck8 | 1581 | 212 | 12 | 4-8 |  |
| 24 | jonters.bling3 | 1581 | 212 | 12 | 4-8 |  |
| 25 | justinottesen.sprint1 | 1581 | 212 | 12 | 4-8 |  |
| 26 | **us:arch_rush** | 1546 | 91 | 110 | 62-48 |  |
| 27 | HugoIngelsson.Bot21 | 1511 | 221 | 12 | 3-9 |  |
| 28 | **us:g_iter0** | 1485 | 87 | 109 | 56-53 |  |
| 29 | Metta-AI.bc24scenario | 1432 | 239 | 12 | 2-10 |  |
| 30 | noahzemlin.honeyducklings | 1432 | 239 | 12 | 2-10 |  |
| 31 | sivakovivan.NewHide | 1432 | 239 | 12 | 2-10 |  |
| 32 | JeffLegendPower.v11 | 1335 | 274 | 12 | 1-11 |  |
| 33 | MiloAkerman.v1 | 1335 | 274 | 12 | 1-11 |  |
| 34 | PerishoJ.tx | 1335 | 274 | 12 | 1-11 |  |
| 35 | TylerJulian.v9 | 1335 | 274 | 12 | 1-11 |  |
| 36 | aj-chau.cowards | 1335 | 274 | 12 | 1-11 |  |
| 37 | cViper971.ourplayer | 1335 | 274 | 12 | 1-11 |  |
| 38 | dylanzemlin.dangerduck2 | 1335 | 274 | 12 | 1-11 |  |
| 39 | lukerhoads.warrior_2nd_comp | 1335 | 274 | 12 | 1-11 |  |
| 40 | neilhuang007.baseline | 1335 | 274 | 12 | 1-11 |  |
| 41 | polyllc.polyv4 | 1335 | 274 | 12 | 1-11 |  |
| 42 | andrearante12.turtle | 1223 | 365 | 11 | 0-11 |  |
| 43 | AlexYu84.smartPlayer | 1190 | 363 | 12 | 0-12 |  |
| 44 | H4ffliger.keyboardcrusader_v1 | 1190 | 363 | 12 | 0-12 |  |
| 45 | Lithanium.AttackingBot | 1190 | 363 | 12 | 0-12 |  |
| 46 | Peter-Fun.dinoboxer | 1190 | 363 | 12 | 0-12 |  |
| 47 | Rubrasum.version_3 | 1190 | 363 | 12 | 0-12 |  |
| 48 | RyanAspen.v22 | 1190 | 363 | 12 | 0-12 |  |
| 49 | SriLakshmiPolavarapu.ducks | 1190 | 363 | 12 | 0-12 |  |
| 50 | VarunVejalla.alexander | 1190 | 363 | 12 | 0-12 |  |
| 51 | abdullah8a0.crayBasic | 1190 | 363 | 12 | 0-12 |  |
| 52 | adamseth2.moveBot1 | 1190 | 363 | 12 | 0-12 |  |
| 53 | dylanconklin.Team3 | 1190 | 363 | 12 | 0-12 |  |
| 54 | itswin.MPAttack | 1190 | 363 | 12 | 0-12 |  |
| 55 | joelcrouch.ducks | 1190 | 363 | 12 | 0-12 |  |
| 56 | lcforges.funkyguy3 | 1190 | 363 | 12 | 0-12 |  |
| 57 | qpwoeirut.tournament_sprint1 | 1190 | 363 | 12 | 0-12 |  |
| 58 | reeceyang.v5 | 1190 | 363 | 12 | 0-12 |  |
| 59 | samithShetty.combustiblelemon | 1190 | 363 | 12 | 0-12 |  |
| 60 | sayam-goyal.SimpleBot | 1190 | 363 | 12 | 0-12 |  |
| 61 | tlevietpdx.Sprint2 | 1190 | 363 | 12 | 0-12 |  |
