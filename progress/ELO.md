# Ladder

5109 scrimmages (ours only), 5109 distinct (a repeated pairing with the same seed replays the same game and counts once), rated by a batch Bradley-Terry fit on the Elo scale (`tools/elolib.py`); each of our builds is its own player. 55 of 55 ladder bots met.

Our builds (rating +- 95%; field score = expected score against every ladder bot, one game each; vs higher = the same against only the ladder bots rated above the build, with their count):

| build | rating | rank | games | record | field score | vs higher |
|---|---|---|---|---|---|---|
| a3dig5 | 1903 +- 132 | 15 of 70 | 110 | 84-26 | 75.6% | 18.1% (vs 14) |
| g_iter1_c2 | 1903 +- 132 | 16 of 70 | 110 | 84-26 | 75.6% | 18.1% (vs 14) |
| arch_rush10 | 1903 +- 132 | 17 of 70 | 110 | 84-26 | 75.6% | 18.1% (vs 14) |
| e1aggr | 1877 +- 131 | 19 of 70 | 110 | 83-27 | 74.7% | 18.4% (vs 15) |
| a3dig10 | 1852 +- 130 | 20 of 70 | 110 | 82-28 | 73.8% | 16.6% (vs 15) |
| c6pair | 1852 +- 130 | 21 of 70 | 110 | 82-28 | 73.8% | 16.6% (vs 15) |
| c5bank | 1852 +- 130 | 22 of 70 | 110 | 82-28 | 73.8% | 16.6% (vs 15) |
| g_iter1 | 1829 +- 18 | 23 of 70 | 2950 | 1084-1866 | 73.1% | 15.1% (vs 15) |
| a2relay | 1826 +- 129 | 24 of 70 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| e2aggr | 1826 +- 129 | 25 of 70 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| a2reloc | 1826 +- 129 | 26 of 70 | 110 | 81-29 | 73.0% | 14.9% (vs 15) |
| b1z2b | 1815 +- 63 | 27 of 70 | 240 | 83-157 | 72.6% | 14.2% (vs 15) |
| b1v2 | 1799 +- 41 | 29 of 70 | 600 | 200-400 | 72.0% | 15.4% (vs 16) |
| arch_rush | 1464 +- 95 | 32 of 70 | 110 | 62-48 | 56.5% | 5.7% (vs 18) |
| g_iter0 | 1399 +- 89 | 35 of 70 | 109 | 56-53 | 51.9% | 8.4% (vs 20) |

Our last run = OUR win rate (our W-L) against the bot, by the most recent of our builds that played it 30+ times (+- 18 points at 95% for 30 games, +- 15 for 42; blank if no build has).

| rank | player | rating | +- 95% | games | W-L | our last run |
|---|---|---|---|---|---|---|
| 1 | Strequals.duck0127v5 | 2471 | 154 | 210 | 206-4 | 0% (b1v2 0-30) |
| 2 | uravt.Version18Final | 2395 | 348 | 26 | 26-0 |  |
| 3 | jmerle.camel_case_v21_final | 2386 | 123 | 210 | 203-7 | 0% (b1v2 0-30) |
| 4 | IvanGeffner.kuma | 2365 | 116 | 210 | 202-8 | 3% (b1v2 1-29) |
| 5 | andli28.v9_USQuals_angle | 2365 | 116 | 210 | 202-8 | 3% (b1v2 1-29) |
| 6 | chenyx512.flagbot_final | 2312 | 101 | 210 | 199-11 | 7% (b1v2 2-28) |
| 7 | NotLLeon.v3 | 2271 | 91 | 210 | 196-14 | 7% (b1v2 2-28) |
| 8 | andrewgopher.player22 | 2207 | 78 | 210 | 190-20 | 10% (b1v2 3-27) |
| 9 | Gymhgy.v10official | 2181 | 74 | 210 | 187-23 | 7% (b1v2 2-28) |
| 10 | CyrilSharma.finalBot | 2093 | 62 | 210 | 174-36 | 10% (b1v2 3-27) |
| 11 | hsmalladi.finalbot | 2093 | 62 | 210 | 174-36 | 17% (b1v2 5-25) |
| 12 | winkelmantanner.waffle | 2065 | 59 | 210 | 169-41 | 17% (b1v2 5-25) |
| 13 | ColtG5.Goob_final | 1929 | 49 | 210 | 137-73 | 20% (b1v2 6-24) |
| 14 | quesswho.cretplayer2_3 | 1908 | 49 | 210 | 131-79 | 37% (b1v2 11-19) |
| 15 | **us:a3dig5** | 1903 | 132 | 110 | 84-26 |  |
| 16 | **us:g_iter1_c2** | 1903 | 132 | 110 | 84-26 |  |
| 17 | **us:arch_rush10** | 1903 | 132 | 110 | 84-26 |  |
| 18 | kyleezz.jeeryfix3 | 1887 | 48 | 210 | 125-85 | 37% (b1v2 11-19) |
| 19 | **us:e1aggr** | 1877 | 131 | 110 | 83-27 |  |
| 20 | **us:a3dig10** | 1852 | 130 | 110 | 82-28 |  |
| 21 | **us:c6pair** | 1852 | 130 | 110 | 82-28 |  |
| 22 | **us:c5bank** | 1852 | 130 | 110 | 82-28 |  |
| 23 | **us:g_iter1** | 1829 | 18 | 2950 | 1084-1866 |  |
| 24 | **us:a2relay** | 1826 | 129 | 110 | 81-29 |  |
| 25 | **us:e2aggr** | 1826 | 129 | 110 | 81-29 |  |
| 26 | **us:a2reloc** | 1826 | 129 | 110 | 81-29 |  |
| 27 | **us:b1z2b** | 1815 | 63 | 240 | 83-157 |  |
| 28 | SampleProvider.TSPAARKSPRINT1 | 1813 | 47 | 210 | 103-107 | 60% (b1v2 18-12) |
| 29 | **us:b1v2** | 1799 | 41 | 600 | 200-400 |  |
| 30 | dmtrung14.defaultplayer_intlqualifier | 1678 | 51 | 210 | 65-145 | 77% (b1v2 23-7) |
| 31 | clbarrell.duck8 | 1604 | 57 | 210 | 48-162 | 80% (b1v2 24-6) |
| 32 | **us:arch_rush** | 1464 | 95 | 110 | 62-48 |  |
| 33 | jonters.bling3 | 1449 | 75 | 210 | 23-187 | 90% (b1v2 27-3) |
| 34 | HugoIngelsson.Bot21 | 1399 | 203 | 26 | 3-23 |  |
| 35 | **us:g_iter0** | 1399 | 89 | 109 | 56-53 |  |
| 36 | awu7.ExplosiveBot | 1368 | 90 | 210 | 15-195 | 87% (b1v2 26-4) |
| 37 | Metta-AI.bc24scenario | 1330 | 226 | 26 | 2-24 |  |
| 38 | noahzemlin.honeyducklings | 1330 | 226 | 26 | 2-24 |  |
| 39 | sivakovivan.NewHide | 1330 | 226 | 26 | 2-24 |  |
| 40 | JeffLegendPower.v11 | 1242 | 265 | 26 | 1-25 |  |
| 41 | MiloAkerman.v1 | 1242 | 265 | 26 | 1-25 |  |
| 42 | PerishoJ.tx | 1242 | 265 | 26 | 1-25 |  |
| 43 | Peter-Fun.dinoboxer | 1242 | 265 | 26 | 1-25 |  |
| 44 | RyanAspen.v22 | 1242 | 265 | 26 | 1-25 |  |
| 45 | TylerJulian.v9 | 1242 | 265 | 26 | 1-25 |  |
| 46 | aj-chau.cowards | 1242 | 265 | 26 | 1-25 |  |
| 47 | cViper971.ourplayer | 1242 | 265 | 26 | 1-25 |  |
| 48 | dylanzemlin.dangerduck2 | 1242 | 265 | 26 | 1-25 |  |
| 49 | lukerhoads.warrior_2nd_comp | 1242 | 265 | 26 | 1-25 |  |
| 50 | neilhuang007.baseline | 1242 | 265 | 26 | 1-25 |  |
| 51 | polyllc.polyv4 | 1242 | 265 | 26 | 1-25 |  |
| 52 | justinottesen.sprint1 | 1149 | 156 | 210 | 4-206 | 100% (b1v2 30-0) |
| 53 | andrearante12.turtle | 1135 | 358 | 25 | 0-25 |  |
| 54 | AlexYu84.smartPlayer | 1103 | 358 | 26 | 0-26 |  |
| 55 | H4ffliger.keyboardcrusader_v1 | 1103 | 358 | 26 | 0-26 |  |
| 56 | Lithanium.AttackingBot | 1103 | 358 | 26 | 0-26 |  |
| 57 | Rubrasum.version_3 | 1103 | 358 | 26 | 0-26 |  |
| 58 | SriLakshmiPolavarapu.ducks | 1103 | 358 | 26 | 0-26 |  |
| 59 | VarunVejalla.alexander | 1103 | 358 | 26 | 0-26 |  |
| 60 | abdullah8a0.crayBasic | 1103 | 358 | 26 | 0-26 |  |
| 61 | adamseth2.moveBot1 | 1103 | 358 | 26 | 0-26 |  |
| 62 | dylanconklin.Team3 | 1103 | 358 | 26 | 0-26 |  |
| 63 | itswin.MPAttack | 1103 | 358 | 26 | 0-26 |  |
| 64 | joelcrouch.ducks | 1103 | 358 | 26 | 0-26 |  |
| 65 | lcforges.funkyguy3 | 1103 | 358 | 26 | 0-26 |  |
| 66 | qpwoeirut.tournament_sprint1 | 1103 | 358 | 26 | 0-26 |  |
| 67 | reeceyang.v5 | 1103 | 358 | 26 | 0-26 |  |
| 68 | samithShetty.combustiblelemon | 1103 | 358 | 26 | 0-26 |  |
| 69 | sayam-goyal.SimpleBot | 1103 | 358 | 26 | 0-26 |  |
| 70 | tlevietpdx.Sprint2 | 1103 | 358 | 26 | 0-26 |  |
