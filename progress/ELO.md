# Ladder

16469 scrimmages (ours only), 16469 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| g_iter3 | 1977 +- 33 | 12 of 75 | 640 | 338-302 | 80.8% | 22.1% (vs 11) |
| g_iter2 | 1920 +- 16 | 14 of 75 | 2080 | 981-1099 | 78.9% | 19.5% (vs 12) |
| arch_rush10 | 1826 +- 131 | 16 of 75 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| g_iter1_c2 | 1826 +- 131 | 17 of 75 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| a3dig5 | 1826 +- 131 | 18 of 75 | 110 | 84-26 | 75.7% | 15.5% (vs 13) |
| e1aggr | 1800 +- 130 | 20 of 75 | 110 | 83-27 | 74.8% | 16.2% (vs 14) |
| c6pair | 1775 +- 129 | 22 of 75 | 110 | 82-28 | 73.9% | 16.9% (vs 15) |
| c5bank | 1775 +- 129 | 23 of 75 | 110 | 82-28 | 73.9% | 16.9% (vs 15) |
| a3dig10 | 1775 +- 129 | 24 of 75 | 110 | 82-28 | 73.9% | 16.9% (vs 15) |
| e2aggr | 1750 +- 128 | 25 of 75 | 110 | 81-29 | 73.1% | 15.3% (vs 15) |
| a2reloc | 1750 +- 128 | 26 of 75 | 110 | 81-29 | 73.1% | 15.3% (vs 15) |
| a2relay | 1750 +- 128 | 27 of 75 | 110 | 81-29 | 73.1% | 15.3% (vs 15) |
| b1z2b | 1741 +- 24 | 28 of 75 | 1760 | 620-1140 | 72.8% | 14.7% (vs 15) |
| g_iter1 | 1736 +- 10 | 29 of 75 | 6990 | 2485-4505 | 72.6% | 14.4% (vs 15) |
| g1copy | 1729 +- 91 | 30 of 75 | 80 | 28-52 | 72.3% | 14.0% (vs 15) |
| b2fs | 1727 +- 28 | 31 of 75 | 1280 | 439-841 | 72.3% | 13.9% (vs 15) |
| g1sym | 1725 +- 51 | 32 of 75 | 200 | 68-132 | 72.2% | 13.8% (vs 15) |
| b1v2 | 1716 +- 22 | 34 of 75 | 2120 | 711-1409 | 71.9% | 15.5% (vs 16) |
| arch_rush | 1398 +- 94 | 37 of 75 | 110 | 62-48 | 56.6% | 6.4% (vs 18) |
| g_iter0 | 1333 +- 89 | 40 of 75 | 109 | 56-53 | 52.0% | 8.8% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2365 | 84 | 556 | 540-16 | 3% (g_iter1 7-217) |
| 2 | jmerle.camel_case_v21_final | 2365 | 84 | 556 | 540-16 | 4% (g_iter1 10-214) |
| 3 | IvanGeffner.kuma | 2327 | 76 | 556 | 536-20 | 4% (g_iter1 9-215) |
| 4 | uravt.Version18Final | 2318 | 348 | 26 | 26-0 |  |
| 5 | chenyx512.flagbot_final | 2255 | 63 | 556 | 526-30 | 6% (g_iter1 13-211) |
| 6 | NotLLeon.v3 | 2207 | 56 | 556 | 517-39 | 8% (g_iter1 17-207) |
| 7 | andli28.v9_USQuals_angle | 2198 | 55 | 556 | 515-41 | 6% (g_iter1 13-211) |
| 8 | Gymhgy.v10official | 2135 | 48 | 556 | 499-57 | 11% (g_iter1 25-199) |
| 9 | andrewgopher.player22 | 2122 | 46 | 556 | 495-61 | 11% (g_iter1 24-200) |
| 10 | hsmalladi.finalbot | 2067 | 42 | 556 | 476-80 | 12% (g_iter1 27-197) |
| 11 | CyrilSharma.finalBot | 2020 | 38 | 556 | 456-100 | 20% (g_iter1 44-180) |
| 12 | **us:g_iter3** | 1977 | 33 | 640 | 338-302 |  |
| 13 | winkelmantanner.waffle | 1964 | 16 | 2036 | 1248-788 | 59% (g_iter3 109-75) |
| 14 | **us:g_iter2** | 1920 | 16 | 2080 | 981-1099 |  |
| 15 | ColtG5.Goob_final | 1843 | 12 | 3516 | 2201-1315 | 67% (g_iter2 203-101) |
| 16 | **us:arch_rush10** | 1826 | 131 | 110 | 84-26 |  |
| 17 | **us:g_iter1_c2** | 1826 | 131 | 110 | 84-26 |  |
| 18 | **us:a3dig5** | 1826 | 131 | 110 | 84-26 |  |
| 19 | kyleezz.jeeryfix3 | 1822 | 30 | 556 | 335-221 | 41% (g_iter1 91-133) |
| 20 | **us:e1aggr** | 1800 | 130 | 110 | 83-27 |  |
| 21 | quesswho.cretplayer2_3 | 1776 | 29 | 556 | 300-256 | 40% (g_iter1 90-134) |
| 22 | **us:c6pair** | 1775 | 129 | 110 | 82-28 |  |
| 23 | **us:c5bank** | 1775 | 129 | 110 | 82-28 |  |
| 24 | **us:a3dig10** | 1775 | 129 | 110 | 82-28 |  |
| 25 | **us:e2aggr** | 1750 | 128 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1750 | 128 | 110 | 81-29 |  |
| 27 | **us:a2relay** | 1750 | 128 | 110 | 81-29 |  |
| 28 | **us:b1z2b** | 1741 | 24 | 1760 | 620-1140 |  |
| 29 | **us:g_iter1** | 1736 | 10 | 6990 | 2485-4505 |  |
| 30 | **us:g1copy** | 1729 | 91 | 80 | 28-52 |  |
| 31 | **us:b2fs** | 1727 | 28 | 1280 | 439-841 |  |
| 32 | **us:g1sym** | 1725 | 51 | 200 | 68-132 |  |
| 33 | SampleProvider.TSPAARKSPRINT1 | 1719 | 29 | 556 | 256-300 | 50% (g_iter1 112-112) |
| 34 | **us:b1v2** | 1716 | 22 | 2120 | 711-1409 |  |
| 35 | dmtrung14.defaultplayer_intlqualifier | 1556 | 34 | 556 | 141-415 | 69% (g_iter1 155-69) |
| 36 | clbarrell.duck8 | 1533 | 35 | 556 | 128-428 | 78% (g_iter1 175-49) |
| 37 | **us:arch_rush** | 1398 | 94 | 110 | 62-48 |  |
| 38 | jonters.bling3 | 1384 | 46 | 556 | 63-493 | 89% (g_iter1 200-24) |
| 39 | HugoIngelsson.Bot21 | 1337 | 201 | 26 | 3-23 |  |
| 40 | **us:g_iter0** | 1333 | 89 | 109 | 56-53 |  |
| 41 | Metta-AI.bc24scenario | 1269 | 224 | 26 | 2-24 |  |
| 42 | noahzemlin.honeyducklings | 1269 | 224 | 26 | 2-24 |  |
| 43 | sivakovivan.NewHide | 1269 | 224 | 26 | 2-24 |  |
| 44 | awu7.ExplosiveBot | 1267 | 60 | 556 | 34-522 | 97% (g_iter1 217-7) |
| 45 | JeffLegendPower.v11 | 1182 | 264 | 26 | 1-25 |  |
| 46 | MiloAkerman.v1 | 1182 | 264 | 26 | 1-25 |  |
| 47 | PerishoJ.tx | 1182 | 264 | 26 | 1-25 |  |
| 48 | Peter-Fun.dinoboxer | 1182 | 264 | 26 | 1-25 |  |
| 49 | RyanAspen.v22 | 1182 | 264 | 26 | 1-25 |  |
| 50 | TylerJulian.v9 | 1182 | 264 | 26 | 1-25 |  |
| 51 | aj-chau.cowards | 1182 | 264 | 26 | 1-25 |  |
| 52 | cViper971.ourplayer | 1182 | 264 | 26 | 1-25 |  |
| 53 | dylanzemlin.dangerduck2 | 1182 | 264 | 26 | 1-25 |  |
| 54 | lukerhoads.warrior_2nd_comp | 1182 | 264 | 26 | 1-25 |  |
| 55 | neilhuang007.baseline | 1182 | 264 | 26 | 1-25 |  |
| 56 | polyllc.polyv4 | 1182 | 264 | 26 | 1-25 |  |
| 57 | andrearante12.turtle | 1077 | 357 | 25 | 0-25 |  |
| 58 | AlexYu84.smartPlayer | 1044 | 357 | 26 | 0-26 |  |
| 59 | H4ffliger.keyboardcrusader_v1 | 1044 | 357 | 26 | 0-26 |  |
| 60 | Lithanium.AttackingBot | 1044 | 357 | 26 | 0-26 |  |
| 61 | Rubrasum.version_3 | 1044 | 357 | 26 | 0-26 |  |
| 62 | SriLakshmiPolavarapu.ducks | 1044 | 357 | 26 | 0-26 |  |
| 63 | VarunVejalla.alexander | 1044 | 357 | 26 | 0-26 |  |
| 64 | abdullah8a0.crayBasic | 1044 | 357 | 26 | 0-26 |  |
| 65 | adamseth2.moveBot1 | 1044 | 357 | 26 | 0-26 |  |
| 66 | dylanconklin.Team3 | 1044 | 357 | 26 | 0-26 |  |
| 67 | itswin.MPAttack | 1044 | 357 | 26 | 0-26 |  |
| 68 | joelcrouch.ducks | 1044 | 357 | 26 | 0-26 |  |
| 69 | lcforges.funkyguy3 | 1044 | 357 | 26 | 0-26 |  |
| 70 | qpwoeirut.tournament_sprint1 | 1044 | 357 | 26 | 0-26 |  |
| 71 | reeceyang.v5 | 1044 | 357 | 26 | 0-26 |  |
| 72 | samithShetty.combustiblelemon | 1044 | 357 | 26 | 0-26 |  |
| 73 | sayam-goyal.SimpleBot | 1044 | 357 | 26 | 0-26 |  |
| 74 | tlevietpdx.Sprint2 | 1044 | 357 | 26 | 0-26 |  |
| 75 | justinottesen.sprint1 | 916 | 153 | 556 | 4-552 | 100% (g_iter1 224-0) |
