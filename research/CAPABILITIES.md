# Basic capabilities (building-block vocabulary for TACTIC_LEVELS.md)

Each is a capability a bot either has at some measurable quality or lacks. Tactics are built from these.

| id | capability | measured by (replay-dump modes) |
|---|---|---|
| C1 | Map knowledge: symmetry, enemy spawn/flag localisation, sharing sightings | firstFlagSight (--capabilities) |
| C2 | Navigation: reach a target efficiently (bug nav, water fill, no stalls) | --navstats moves/robot-round, still%, ABA |
| C3 | Crumb economy: gather map crumbs, bank, pace spending | gathered200/400, crumbs200/250 (--survey) |
| C4 | Unit micro: engage / kite / focus fire / heal | kills vs deaths in even fights, atk/heal volume |
| C5 | Combat traps: place traps where the enemy will step | trapsBuilt, trapsHit, stun400 |
| C6 | Spawn management: where and when units re-enter | meanAlive, spawns |
| C7 | Communication: shared-array schema, fresh shared knowledge | (code inspection) |
| C8 | Flag handling: pickup, carry speed, relay, capture conversion | pickups, captured, carrierMoves/carrierRounds |
| C9 | Carrier protection: escorts keep the carrier alive | carrierDeaths per pickup |
| C10 | Flag defence response: detect raiders, converge, kill carriers | enemyCarrierKills, enemy pickups per game |
| C11 | Territory / tempo: push the army into the enemy half after the dam opens | firstEnemySide, inEnemy250/300 |
| C12 | Force division: independent groups (flank raids, multi-lane) | (replay boards) |
| C13 | Setup planning: flag placement, setup traps, setup crumb sweep | flagMoveDist, traps200, digs200 |
| C14 | Tiebreak management: level sum, crumbs at r2000 | level_sum |

Sub-IDs added 2026-10-01 (research/TACTIC_LEVELS.md §2.1): C3a setup gathering, C3b setup budget (bank at r200),
C3c kill-bounty income, C3d post-dam income; C5v fight-trap volume, C5q trap hit rate; C10a interception per
opportunity (the same paired comparison as C9), C10b pickup prevention; C15 upgrade timing. C11's metric is "robots in
the enemy half by nearest spawn centre", not engine territory (a flood fill from flag starts).
