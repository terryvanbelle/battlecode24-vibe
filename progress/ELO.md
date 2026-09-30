# Ladder

769 scrimmages (ours only), 769 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| arch_rush10 | 1888 +- 114 | 16 of 62 | 110 | 84-26 | 75.6% | 25.3% (vs 15) |
| g_iter1_c2 | 1888 +- 114 | 17 of 62 | 110 | 84-26 | 75.6% | 25.3% (vs 15) |
| a2relay | 1830 +- 112 | 19 of 62 | 110 | 81-29 | 73.0% | 21.9% (vs 16) |
| a2reloc | 1830 +- 112 | 20 of 62 | 110 | 81-29 | 73.0% | 21.9% (vs 16) |
| g_iter1 | 1830 +- 112 | 21 of 62 | 110 | 81-29 | 73.0% | 21.9% (vs 16) |
| arch_rush | 1537 +- 91 | 27 of 62 | 110 | 62-48 | 56.3% | 14.7% (vs 21) |
| g_iter0 | 1477 +- 87 | 29 of 62 | 109 | 56-53 | 51.7% | 13.2% (vs 22) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | IvanGeffner.kuma | 2253 | 356 | 14 | 14-0 |  |
| 2 | NotLLeon.v3 | 2253 | 356 | 14 | 14-0 |  |
| 3 | Strequals.duck0127v5 | 2253 | 356 | 14 | 14-0 |  |
| 4 | andli28.v9_USQuals_angle | 2253 | 356 | 14 | 14-0 |  |
| 5 | andrewgopher.player22 | 2253 | 356 | 14 | 14-0 |  |
| 6 | uravt.Version18Final | 2253 | 356 | 14 | 14-0 |  |
| 7 | Gymhgy.v10official | 2115 | 265 | 14 | 13-1 |  |
| 8 | chenyx512.flagbot_final | 2115 | 265 | 14 | 13-1 |  |
| 9 | jmerle.camel_case_v21_final | 2115 | 265 | 14 | 13-1 |  |
| 10 | CyrilSharma.finalBot | 2026 | 228 | 14 | 12-2 |  |
| 11 | winkelmantanner.waffle | 2026 | 228 | 14 | 12-2 |  |
| 12 | kyleezz.jeeryfix3 | 1955 | 208 | 14 | 11-3 |  |
| 13 | SampleProvider.TSPAARKSPRINT1 | 1894 | 197 | 14 | 10-4 |  |
| 14 | hsmalladi.finalbot | 1894 | 197 | 14 | 10-4 |  |
| 15 | quesswho.cretplayer2_3 | 1894 | 197 | 14 | 10-4 |  |
| 16 | **us:arch_rush10** | 1888 | 114 | 110 | 84-26 |  |
| 17 | **us:g_iter1_c2** | 1888 | 114 | 110 | 84-26 |  |
| 18 | ColtG5.Goob_final | 1838 | 192 | 14 | 9-5 |  |
| 19 | **us:a2relay** | 1830 | 112 | 110 | 81-29 |  |
| 20 | **us:a2reloc** | 1830 | 112 | 110 | 81-29 |  |
| 21 | **us:g_iter1** | 1830 | 112 | 110 | 81-29 |  |
| 22 | dmtrung14.defaultplayer_intlqualifier | 1675 | 193 | 14 | 6-8 |  |
| 23 | awu7.ExplosiveBot | 1557 | 205 | 14 | 4-10 |  |
| 24 | clbarrell.duck8 | 1557 | 205 | 14 | 4-10 |  |
| 25 | jonters.bling3 | 1557 | 205 | 14 | 4-10 |  |
| 26 | justinottesen.sprint1 | 1557 | 205 | 14 | 4-10 |  |
| 27 | **us:arch_rush** | 1537 | 91 | 110 | 62-48 |  |
| 28 | HugoIngelsson.Bot21 | 1491 | 216 | 14 | 3-11 |  |
| 29 | **us:g_iter0** | 1477 | 87 | 109 | 56-53 |  |
| 30 | Metta-AI.bc24scenario | 1415 | 235 | 14 | 2-12 |  |
| 31 | noahzemlin.honeyducklings | 1415 | 235 | 14 | 2-12 |  |
| 32 | sivakovivan.NewHide | 1415 | 235 | 14 | 2-12 |  |
| 33 | JeffLegendPower.v11 | 1321 | 271 | 14 | 1-13 |  |
| 34 | MiloAkerman.v1 | 1321 | 271 | 14 | 1-13 |  |
| 35 | PerishoJ.tx | 1321 | 271 | 14 | 1-13 |  |
| 36 | Peter-Fun.dinoboxer | 1321 | 271 | 14 | 1-13 |  |
| 37 | RyanAspen.v22 | 1321 | 271 | 14 | 1-13 |  |
| 38 | TylerJulian.v9 | 1321 | 271 | 14 | 1-13 |  |
| 39 | aj-chau.cowards | 1321 | 271 | 14 | 1-13 |  |
| 40 | cViper971.ourplayer | 1321 | 271 | 14 | 1-13 |  |
| 41 | dylanzemlin.dangerduck2 | 1321 | 271 | 14 | 1-13 |  |
| 42 | lukerhoads.warrior_2nd_comp | 1321 | 271 | 14 | 1-13 |  |
| 43 | neilhuang007.baseline | 1321 | 271 | 14 | 1-13 |  |
| 44 | polyllc.polyv4 | 1321 | 271 | 14 | 1-13 |  |
| 45 | andrearante12.turtle | 1210 | 363 | 13 | 0-13 |  |
| 46 | AlexYu84.smartPlayer | 1177 | 361 | 14 | 0-14 |  |
| 47 | H4ffliger.keyboardcrusader_v1 | 1177 | 361 | 14 | 0-14 |  |
| 48 | Lithanium.AttackingBot | 1177 | 361 | 14 | 0-14 |  |
| 49 | Rubrasum.version_3 | 1177 | 361 | 14 | 0-14 |  |
| 50 | SriLakshmiPolavarapu.ducks | 1177 | 361 | 14 | 0-14 |  |
| 51 | VarunVejalla.alexander | 1177 | 361 | 14 | 0-14 |  |
| 52 | abdullah8a0.crayBasic | 1177 | 361 | 14 | 0-14 |  |
| 53 | adamseth2.moveBot1 | 1177 | 361 | 14 | 0-14 |  |
| 54 | dylanconklin.Team3 | 1177 | 361 | 14 | 0-14 |  |
| 55 | itswin.MPAttack | 1177 | 361 | 14 | 0-14 |  |
| 56 | joelcrouch.ducks | 1177 | 361 | 14 | 0-14 |  |
| 57 | lcforges.funkyguy3 | 1177 | 361 | 14 | 0-14 |  |
| 58 | qpwoeirut.tournament_sprint1 | 1177 | 361 | 14 | 0-14 |  |
| 59 | reeceyang.v5 | 1177 | 361 | 14 | 0-14 |  |
| 60 | samithShetty.combustiblelemon | 1177 | 361 | 14 | 0-14 |  |
| 61 | sayam-goyal.SimpleBot | 1177 | 361 | 14 | 0-14 |  |
| 62 | tlevietpdx.Sprint2 | 1177 | 361 | 14 | 0-14 |  |
