# CAPABILITY_CENSUS.md — basic capabilities, us vs opponents

Status 2026-10-07 (shutdown): a dated g_iter1 snapshot (2026-10-01). "Us" below is g_iter1, the incumbent until
2026-10-03, not g_iter7. Only 5 bots rate above g_iter7, which beats ColtG5 46-2, waffle 44-4, Cyril 35-13 and Gymhgy
39-9. g_iter7's census is in gauntlet/census-g_iter7-L1..L3.csv (tools/capability-summary.py).

700 games from census-g_iter1.csv, census-g_iter1-field.csv; 15 opponents beat us here (ColtG5.Goob_final, CyrilSharma.finalBot, Gymhgy.v10official, IvanGeffner.kuma, NotLLeon.v3, Strequals.duck0127v5, andli28.v9_USQuals_angle, andrewgopher.player22, chenyx512.flagbot_final, hsmalladi.finalbot, jmerle.camel_case_v21_final, kyleezz.jeeryfix3, quesswho.cretplayer2_3, uravt.Version18Final, winkelmantanner.waffle). Medians; "n" = games where the metric is defined. Regenerate with `tools/capability-summary.py`. Definitions: `tools/replaydump/ReplayDump.java --capabilities`, research/CAPABILITIES.md.

## vs bots that beat us (444 games)

| metric | us | them | n |
|---|---|---|---|
| gathered200 | 3200.00 | 3000.00 | 444 |
| gathered400 | 3900.00 | 4950.00 | 444 |
| firstEnemySide | 202.00 | 202.00 | 442 |
| inEnemy250 | 10.00 | 24.00 | 444 |
| inEnemy300 | 10.00 | 22.00 | 444 |
| firstFlagSight | 271.00 | 232.00 | 426 |
| pickups | 4.00 | 17.00 | 444 |
| captured | 0.00 | 3.00 | 444 |
| captureRate | 0.06 | 0.13 | 334 |
| carrierSpeed | 0.47 | 0.45 | 333 |
| carrierDeaths | 3.00 | 11.00 | 444 |
| enemyCarrierKills | 11.00 | 3.00 | 444 |
| trapsBuilt | 187.50 | 239.00 | 444 |
| trapsHit | 149.50 | 221.00 | 444 |
| trapHitRate | 0.86 | 0.92 | 444 |
| kills | 245.50 | 292.00 | 444 |
| deaths | 292.00 | 245.50 | 444 |
| meanAlive | 44.90 | 44.60 | 444 |
| rounds | 1345.00 | 1345.00 | 444 |

## vs bots we beat (256 games)

| metric | us | them | n |
|---|---|---|---|
| gathered200 | 3000.00 | 3000.00 | 256 |
| gathered400 | 4200.00 | 4450.00 | 256 |
| firstEnemySide | 202.00 | 202.00 | 244 |
| inEnemy250 | 19.00 | 11.00 | 256 |
| inEnemy300 | 21.00 | 11.00 | 256 |
| firstFlagSight | 228.50 | 231.50 | 232 |
| pickups | 9.00 | 8.00 | 256 |
| captured | 3.00 | 0.00 | 256 |
| captureRate | 0.25 | 0.00 | 222 |
| carrierSpeed | 0.48 | 0.37 | 221 |
| carrierDeaths | 6.00 | 5.50 | 256 |
| enemyCarrierKills | 5.50 | 6.00 | 256 |
| trapsBuilt | 239.00 | 79.50 | 256 |
| trapsHit | 178.00 | 70.00 | 256 |
| trapHitRate | 0.75 | 0.88 | 248 |
| kills | 535.00 | 73.50 | 256 |
| deaths | 73.50 | 535.00 | 256 |
| meanAlive | 48.15 | 38.20 | 256 |
| rounds | 1155.50 | 1155.50 | 256 |

## our losses (all) (414 games)

| metric | us | them | n |
|---|---|---|---|
| gathered200 | 3200.00 | 3200.00 | 414 |
| gathered400 | 4000.00 | 5300.00 | 414 |
| firstEnemySide | 202.00 | 202.00 | 412 |
| inEnemy250 | 11.00 | 24.00 | 414 |
| inEnemy300 | 11.00 | 23.00 | 414 |
| firstFlagSight | 264.00 | 233.00 | 396 |
| pickups | 3.00 | 17.00 | 414 |
| captured | 0.00 | 3.00 | 414 |
| captureRate | 0.00 | 0.14 | 306 |
| carrierSpeed | 0.46 | 0.44 | 305 |
| carrierDeaths | 3.00 | 11.00 | 414 |
| enemyCarrierKills | 11.00 | 3.00 | 414 |
| trapsBuilt | 168.00 | 221.50 | 414 |
| trapsHit | 136.00 | 201.00 | 414 |
| trapHitRate | 0.87 | 0.90 | 414 |
| kills | 212.00 | 220.00 | 414 |
| deaths | 220.00 | 212.00 | 414 |
| meanAlive | 44.90 | 44.80 | 414 |
| rounds | 1098.00 | 1098.00 | 414 |

## our wins (all) (286 games)

| metric | us | them | n |
|---|---|---|---|
| gathered200 | 3000.00 | 2900.00 | 286 |
| gathered400 | 4150.00 | 4100.00 | 286 |
| firstEnemySide | 202.00 | 202.00 | 274 |
| inEnemy250 | 18.00 | 13.00 | 286 |
| inEnemy300 | 18.00 | 11.00 | 286 |
| firstFlagSight | 235.50 | 231.00 | 260 |
| pickups | 9.00 | 8.00 | 286 |
| captured | 3.00 | 0.00 | 286 |
| captureRate | 0.27 | 0.00 | 246 |
| carrierSpeed | 0.48 | 0.39 | 245 |
| carrierDeaths | 6.00 | 6.00 | 286 |
| enemyCarrierKills | 6.00 | 6.00 | 286 |
| trapsBuilt | 253.50 | 100.00 | 286 |
| trapsHit | 191.00 | 87.00 | 286 |
| trapHitRate | 0.76 | 0.92 | 278 |
| kills | 618.50 | 117.50 | 286 |
| deaths | 117.50 | 618.50 | 286 |
| meanAlive | 47.60 | 37.30 | 286 |
| rounds | 1425.50 | 1425.50 | 286 |

