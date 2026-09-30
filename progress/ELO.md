# Ladder

989 scrimmages (ours only), 989 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1905 +- 123 | 13 of 64 | 110 | 84-26 | 75.6% | 15.1% (vs 12) |
| arch_rush10 | 1905 +- 123 | 14 of 64 | 110 | 84-26 | 75.6% | 15.1% (vs 12) |
| g_iter1_c2 | 1905 +- 123 | 15 of 64 | 110 | 84-26 | 75.6% | 15.1% (vs 12) |
| a3dig10 | 1861 +- 121 | 18 of 64 | 110 | 82-28 | 73.8% | 16.9% (vs 14) |
| a2relay | 1839 +- 120 | 21 of 64 | 110 | 81-29 | 73.0% | 19.4% (vs 16) |
| a2reloc | 1839 +- 120 | 22 of 64 | 110 | 81-29 | 73.0% | 19.4% (vs 16) |
| g_iter1 | 1839 +- 120 | 23 of 64 | 110 | 81-29 | 73.0% | 19.4% (vs 16) |
| arch_rush | 1519 +- 92 | 29 of 64 | 110 | 62-48 | 56.3% | 14.3% (vs 21) |
| g_iter0 | 1457 +- 88 | 31 of 64 | 109 | 56-53 | 51.8% | 13.1% (vs 22) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | IvanGeffner.kuma | 2328 | 352 | 18 | 18-0 |  |
| 2 | NotLLeon.v3 | 2328 | 352 | 18 | 18-0 |  |
| 3 | Strequals.duck0127v5 | 2328 | 352 | 18 | 18-0 |  |
| 4 | andli28.v9_USQuals_angle | 2328 | 352 | 18 | 18-0 |  |
| 5 | andrewgopher.player22 | 2328 | 352 | 18 | 18-0 |  |
| 6 | uravt.Version18Final | 2328 | 352 | 18 | 18-0 |  |
| 7 | Gymhgy.v10official | 2195 | 258 | 18 | 17-1 |  |
| 8 | chenyx512.flagbot_final | 2195 | 258 | 18 | 17-1 |  |
| 9 | jmerle.camel_case_v21_final | 2195 | 258 | 18 | 17-1 |  |
| 10 | CyrilSharma.finalBot | 2110 | 219 | 18 | 16-2 |  |
| 11 | winkelmantanner.waffle | 2110 | 219 | 18 | 16-2 |  |
| 12 | kyleezz.jeeryfix3 | 1991 | 185 | 18 | 14-4 |  |
| 13 | **us:a3dig5** | 1905 | 123 | 110 | 84-26 |  |
| 14 | **us:arch_rush10** | 1905 | 123 | 110 | 84-26 |  |
| 15 | **us:g_iter1_c2** | 1905 | 123 | 110 | 84-26 |  |
| 16 | SampleProvider.TSPAARKSPRINT1 | 1897 | 172 | 18 | 12-6 |  |
| 17 | hsmalladi.finalbot | 1897 | 172 | 18 | 12-6 |  |
| 18 | **us:a3dig10** | 1861 | 121 | 110 | 82-28 |  |
| 19 | ColtG5.Goob_final | 1854 | 169 | 18 | 11-7 |  |
| 20 | quesswho.cretplayer2_3 | 1854 | 169 | 18 | 11-7 |  |
| 21 | **us:a2relay** | 1839 | 120 | 110 | 81-29 |  |
| 22 | **us:a2reloc** | 1839 | 120 | 110 | 81-29 |  |
| 23 | **us:g_iter1** | 1839 | 120 | 110 | 81-29 |  |
| 24 | dmtrung14.defaultplayer_intlqualifier | 1632 | 181 | 18 | 6-12 |  |
| 25 | awu7.ExplosiveBot | 1526 | 197 | 18 | 4-14 |  |
| 26 | clbarrell.duck8 | 1526 | 197 | 18 | 4-14 |  |
| 27 | jonters.bling3 | 1526 | 197 | 18 | 4-14 |  |
| 28 | justinottesen.sprint1 | 1526 | 197 | 18 | 4-14 |  |
| 29 | **us:arch_rush** | 1519 | 92 | 110 | 62-48 |  |
| 30 | HugoIngelsson.Bot21 | 1464 | 211 | 18 | 3-15 |  |
| 31 | **us:g_iter0** | 1457 | 88 | 109 | 56-53 |  |
| 32 | Metta-AI.bc24scenario | 1391 | 231 | 18 | 2-16 |  |
| 33 | noahzemlin.honeyducklings | 1391 | 231 | 18 | 2-16 |  |
| 34 | sivakovivan.NewHide | 1391 | 231 | 18 | 2-16 |  |
| 35 | JeffLegendPower.v11 | 1299 | 269 | 18 | 1-17 |  |
| 36 | MiloAkerman.v1 | 1299 | 269 | 18 | 1-17 |  |
| 37 | PerishoJ.tx | 1299 | 269 | 18 | 1-17 |  |
| 38 | Peter-Fun.dinoboxer | 1299 | 269 | 18 | 1-17 |  |
| 39 | RyanAspen.v22 | 1299 | 269 | 18 | 1-17 |  |
| 40 | TylerJulian.v9 | 1299 | 269 | 18 | 1-17 |  |
| 41 | aj-chau.cowards | 1299 | 269 | 18 | 1-17 |  |
| 42 | cViper971.ourplayer | 1299 | 269 | 18 | 1-17 |  |
| 43 | dylanzemlin.dangerduck2 | 1299 | 269 | 18 | 1-17 |  |
| 44 | lukerhoads.warrior_2nd_comp | 1299 | 269 | 18 | 1-17 |  |
| 45 | neilhuang007.baseline | 1299 | 269 | 18 | 1-17 |  |
| 46 | polyllc.polyv4 | 1299 | 269 | 18 | 1-17 |  |
| 47 | andrearante12.turtle | 1190 | 361 | 17 | 0-17 |  |
| 48 | AlexYu84.smartPlayer | 1158 | 360 | 18 | 0-18 |  |
| 49 | H4ffliger.keyboardcrusader_v1 | 1158 | 360 | 18 | 0-18 |  |
| 50 | Lithanium.AttackingBot | 1158 | 360 | 18 | 0-18 |  |
| 51 | Rubrasum.version_3 | 1158 | 360 | 18 | 0-18 |  |
| 52 | SriLakshmiPolavarapu.ducks | 1158 | 360 | 18 | 0-18 |  |
| 53 | VarunVejalla.alexander | 1158 | 360 | 18 | 0-18 |  |
| 54 | abdullah8a0.crayBasic | 1158 | 360 | 18 | 0-18 |  |
| 55 | adamseth2.moveBot1 | 1158 | 360 | 18 | 0-18 |  |
| 56 | dylanconklin.Team3 | 1158 | 360 | 18 | 0-18 |  |
| 57 | itswin.MPAttack | 1158 | 360 | 18 | 0-18 |  |
| 58 | joelcrouch.ducks | 1158 | 360 | 18 | 0-18 |  |
| 59 | lcforges.funkyguy3 | 1158 | 360 | 18 | 0-18 |  |
| 60 | qpwoeirut.tournament_sprint1 | 1158 | 360 | 18 | 0-18 |  |
| 61 | reeceyang.v5 | 1158 | 360 | 18 | 0-18 |  |
| 62 | samithShetty.combustiblelemon | 1158 | 360 | 18 | 0-18 |  |
| 63 | sayam-goyal.SimpleBot | 1158 | 360 | 18 | 0-18 |  |
| 64 | tlevietpdx.Sprint2 | 1158 | 360 | 18 | 0-18 |  |
