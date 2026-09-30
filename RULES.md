# RULES.md — Battlecode 2024 "Breadwars", engine-checked digest

Engine: `battlecode/battlecode24` tag **3.0.6** (spec 3.0.6, final 2024 release), built from source by
`tools/build-engine.sh`. Every fact below is tagged with where it was verified:
`[spec]` = specs.md.html 3.0.6, `[E:File:line]` = engine source under
`reference/battlecode24/engine/src/main/battlecode/`, `[game]` = observed in a game we ran.
Where spec and engine disagree, the engine wins and the row says so.

## Win condition and tiebreaks

| fact | source |
|---|---|
| Capture all 3 enemy flags -> immediate win (`CAPTURE`). | [spec] [E:world/TeamInfo.java captureFlag] |
| Round cap 2000. Tiebreak order: flags captured, **sum of all unit levels (all 50 robots, spawned or jailed)**, crumbs banked, then `Math.random()` (NOT seeded: the only nondeterminism). `MORE_FLAGS_PICKED` exists in the enum but is never used. | [E:world/GameWorld.java checkEndOfMatch] [E:world/TeamInfo.java:234 getLevelSum] |
| Starter bot vs itself on DefaultSmall ended at r2000 on level sum. | [game 2026-09-30] |

## Setup phase (rounds 1..200)

| fact | source |
|---|---|
| `isSetupPhase()` = round <= 200. Dam tiles impassable while setup. Attacks illegal during setup. | [E:world/GameWorld.java isPassable,isSetupPhase] [E:RobotControllerImpl assertCanAttack] |
| During setup a robot may pick up **own** flags only; after setup **enemy** flags only. | [E:RobotControllerImpl assertCanPickupFlag] |
| End of round 200: if any two own flags are closer than dist² 36 all flags return to spawn centres; else current spots become the default ("start") locations. A carried flag is dropped first. | [E:GameWorld confirmFlagPlacements] |
| Team territory = flood fill (8-connected, through water, not through walls/dams) from each team's flag start; used for the kill reward. | [E:GameWorld floodFillTeam] |

## Units, spawning, jail

| fact | source |
|---|---|
| 50 robots per team, all created at start, all run code every round **even when unspawned** (free compute and comms for jailed robots). | [spec] [E:GameWorld ctor] |
| Execution order is fixed for the whole game: creation order, alternating A0,B0,A1,B1,... (never re-sorted). | [E:world/ObjectInfo.java eachDynamicBodyByExecOrder] |
| `spawn(loc)` requires own spawn-zone tile, empty, passable, spawn cooldown < 10. No crumb pickup on spawn (only `move` collects). | [E:RobotControllerImpl assertCanSpawn, move] |
| HP 1000. HP <= 0 -> jail: spawn cooldown 250 (25 rounds), highest-level skill loses XP per penalty table, carried flag dropped at death tile. | [E:InternalRobot despawn, jailedPenalty] |
| Heal cannot exceed 1000; cannot heal self; cannot heal a full-HP unit. | [E:RobotControllerImpl assertCanHeal] |

## Actions and cooldowns (cooldown < 10 to act; both counters fall by 10 per turn)

| action | range (dist²) | cost | cooldown | notes / source |
|---|---|---|---|---|
| move | adjacent | – | +10 move (+20 carrying flag; +12 with CAPTURING upgrade) | collects crumbs on the tile; entering own spawn zone with enemy flag = capture [E:RCI move, InternalRobot addMovementCooldownTurns] |
| attack | 4 | – | 20 x (1+attack cd%) | 150 base (+60 with ATTACK upgrade) x (1+dmg%); must target an enemy robot tile; illegal while carrying a flag; +30 crumbs if the **attacker** stands on enemy territory at the kill [E:InternalRobot attack, getDamage] |
| heal | 4 | – | 30 x (1+heal cd%) | 80 base (+50 HEALING upgrade) x (1+heal%) |
| build trap | 2 | trap cost x (1+build%) | 5 x (1+build cd%) | not on/adjacent to an enemy robot; not on own trap; explosive on land or water, others land only; building on an enemy explosive trap triggers it (build fails, crumbs spent) [E:RCI assertCanBuild, build] |
| dig | 2 | 20 x (1+build%) | 20 x (1+build cd%) | not on water/wall/spawn/robot/flag/own trap; gives build XP [E:RCI assertCanDig] |
| fill | 2 | 30 x (1+build%) | 30 x (1+build cd%) | water -> land; **no** build XP [E:RCI fill] |
| pickup flag | 2 | – | +10 action | not the same round the flag was dropped (unless at start loc); if picked up while standing in own spawn zone, it captures immediately [E:RCI pickupFlag] |
| drop flag | 2 | – | +10 action, +10 move | passable tile only |
| buyGlobal | – | 1 upgrade point | **none**; no spawn needed | points at rounds 600, 1200, 1800; ATTACK (+60 dmg), HEALING (+50 heal), CAPTURING (enemy-dropped-flag return 4 -> 25 rounds, carrier move cd 20 -> 12) [E:RCI buyGlobal, GameWorld processBeginningOfRound] |
| shared array | – | – | – | 64 slots x 16 bits, any robot, spawned or not; writes visible immediately to robots later in the same round [E:RCI writeSharedArray] |

Skill levels (XP = count of actions): attack 15/30/45/75/110/150; build 5/10/15/20/25/30; heal 20/40/70/100/140/180.
Reaching level 4 in one skill caps the others at level 3. Effects per level in `common/SkillType.java`.

## Traps (all invisible to the enemy; triggered at the END of the triggering robot's turn)

| trap | cost | trigger | effect | source |
|---|---|---|---|---|
| EXPLOSIVE | 200 | enemy **enters the tile** (trigger radius 0) | 750 to enemies within dist² 4; if an enemy digs/fills/builds on it: 200 within dist² 2 | [E:common/TrapType.java] [E:GameWorld triggerTrap] |
| WATER | 100 | enemy enters a tile within dist² 2 | digs every empty passable non-spawn non-trap land tile within dist² 9 | |
| STUN | 100 | enemy enters a tile within dist² 2 | sets move and action cooldown of enemies within dist² 13 to 40 (≈3-4 turns frozen) | |

## Flags

| fact | source |
|---|---|
| Visible within vision (dist² 20) always, including carried ones. Out of vision: `senseBroadcastFlagLocations()` gives a random point within dist² 100 of each **dropped** (not carried) enemy flag, re-drawn every 100 rounds (world RNG, map-seeded). | [spec] [E:GameWorld updateFlagBroadcastLocations] |
| A dropped (non-start) flag returns to its start location after 4 end-of-round ticks (25 if the carrier team has CAPTURING). | [E:GameWorld processEndOfRound] |
| Carrying a flag: no actions except movement. | [spec] [E:RCI assert*] |

## Map, sensing, economy

| fact | source |
|---|---|
| Maps 30x30..60x60 (engine allows 20..60), origin (0,0), symmetric by rotation or reflection. 78 maps in the engine resources. | [spec] [E:common/GameConstants.java] |
| Vision dist² 20 for every robot, ignores walls. | [E:GameConstants] |
| Start 400 crumbs; +10 per round passive; map crumbs collected by stepping on the tile. | [E:GameWorld runRound] |
| Bytecode 25,000 per turn; exceptions cost 500 bytecodes; indicator string max 64 chars. | [E:GameConstants] |
| Robot IDs >= 10000, from an IDGenerator seeded by the map seed (our `-Dbc.game.seed` patch changes it). | [E:GameWorld ctor] |
