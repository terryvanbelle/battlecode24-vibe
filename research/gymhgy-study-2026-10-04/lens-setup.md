# Lens 5: setup and flags, g_iter4 vs Gymhgy.v10official

Data: the fill census (1,174 games, 469 wins, 78 maps; side A 220/589, B 249/585). I also ran one niced dump per replay
(`--survey --flags --map-at 200 --defense`) on the 981 Gymhgy replays still on the VM (386 wins). Each was joined to its
census row, and a round-1 board per map gave the original terrain. Scripts and outputs are in this directory (`*.py`,
`out_*.txt`). Per-flag features come from the r200 board: path distance is an 8-connected BFS from the enemy spawn
centres, with walls and water blocked and dams open. The within-game model is a conditional logit (one stratum per game,
3 flags, games where 1 or 2 flags fell).

## 1. Where its flags go (2,943 flags per side)

| r200 flag spot | ours | Gymhgy |
|---|---|---|
| moved from spawn centre (median tiles) | 4.5 | 5.0 |
| on the map boundary (edge = 0) | 2% | **80%** |
| impassable of 8 neighbours (mean) | 0.46 | **4.18** (2.95 wall/out-of-map, 1.03 water) |
| water dug by it among 8 neighbours / within d2 8 | 0 / 0 | 0.78 / 2.2 |
| own traps within d2 8 | **10.7** | 0.8 |
| inside own spawn zone (<= 1.5 tiles from a centre) | 33% | 8% |
| normalised distance from map centre | 0.71 | 0.79 |
| weakest flag's path distance to enemy spawns | 27.0 | 28.9 (farther in 724/981 games) |

Gymhgy carries every flag a few tiles to the map edge and digs about 2 tiles of water next to it. 25% of its 73 setup
digs are within d2 20 of its flags; the rest fall elsewhere, in a checkerboard pattern. Its most exposed (central) flag
goes sideways out of the lane. On Canals, for example, it moved from (15,38) to (0,44) or (4,45).

## 2. Which flags fall (within game, so the map is held fixed)

For our flags, the outcome is "captured by r600", over 662 games:
- each tile of path distance from its spawns: **-0.125** log-odds (z -7.6). Path distance absorbs Euclidean distance
  (pEn -0.154 z -5.6 vs dEn -0.018). The nearest flag is lost by r600 in 58% of games, the middle one in 43%, the
  farthest in 32%. Our nearest flag is the first one grabbed in 419 of 889 games (chance would be 296).
- flag inside our spawn zone: **-0.60** (z -3.8). For "ever captured" it is -0.92 (z -5.4), with the spawn centre
  itself -0.85 and the tiles next to it -1.08. Gymhgy's first grabs at zone flags meet 6.2 of ours within d2 20,
  against 3.4 at relocated flags, because respawns land beside the flag.
- each tile from the map edge: +0.37 (z +7.3). This is **not robust**: side B gives +0.37 (z 4.4) and side A +0.02 (z 0.3).
- each setup trap within d2 8: -0.041 (z -4.0). Ten traps are worth about 3 tiles of path distance. Traps are spread
  flat by exposure (10.2 / 10.1 / 11.8 from the nearest flag to the farthest).

For its flags, the same model gives path distance -0.19 per tile (z -9.8) and dug neighbours -0.18 each (z -2.6). Edge
and wall neighbours add nothing once distance is in the model. Wall or water neighbours do not protect our flags
(nbImp +0.05, z 0.9).

## 3. Our relocation falls short
Our weakest flag stands a mean 5.9 tiles (median 4.3) closer to its spawns than the best spot reachable in setup within
our 15-tile radius. The gap is 5+ tiles in 458 of 981 games, and in 335 of those the flag never left its spawn zone. This
is an upper bound, because it ignores flag spacing and what the bot can sense.

Trace: VM `gauntlet/20261004-190819-scrim-g_iter4-fill1791140889/losses/Gymhgy.v10official__Canals__botB.bc24`,
`--robot 13438`. The defender picks up B635 at (15,20) on r1 and walks to (17,13). It runs along row 13 to (26,13) by
r33, where the closed dams block it. It stalls, walks home, and drops the flag at (16,20) on r54, 18 tiles from Gymhgy's
centre; row 13 would have given about 25.

In all 12 Canals games, wins included, this centre flag was grabbed between r245 and r318 and captured between r269 and
r340:
- Loss (same file): first grab r251 with 7 of ours within d2 20; relayed every 2-5 rounds; captured r291.
- Win (`.../20261004-185652-scrim-g_iter4-fill1791140205/replays/Gymhgy.v10official__Canals__botB.bc24`): first grab
  r253 with **19** of ours within d2 20 and 14 chasers. The carrier died and the flag was re-picked at r255, then
  captured r269.
- What separated the two games was the (2,2) corner flag: it held in the win (raids at r349-400 and r508-541 all died)
  and fell at r599 in the loss.

## 4. Setup differences between wins and losses (within map, 74 maps, 374 W / 562 L dumped games)
None of the setup measures separates wins from losses within a map: |t| <= 1.2 for crumbs200, traps200, digs200,
fills200, flagMoveDist, level200 and crumbs250 (both teams), for every flag-geometry aggregate (min/mean path distance,
shortfall, edge, flags in zone, traps near flags, Gymhgy's dug tiles), for symDecidedRound (+0.9 rounds, t 0.1) and for
firstFlagSight (ours -13, t -1.3; its +3, t 0.6). The one exception is them.defend300 (+0.6, t 2.0). Setup is close to
fixed per map and side (our flags vary by 1-2 tiles within a map).

What separates wins from losses comes after setup:

| our flags lost by r600 | wins | losses | within-map t |
|---|---|---|---|
| 1st | 72% | 85% | -4.8 |
| 2nd | 25% | 51% | -7.3 |
| 3rd | 0% | 20% | -8.4 |

## 5. Setup economy and timing

| | ours | Gymhgy |
|---|---|---|
| crumbs at r200 (mean / median) | 458 / 175 | 3,913 / 3,022 |
| crumbs at r250 (median) | 185 | 345 |
| traps built by r200 | 38.5 | 2.4 |
| traps built r201-400 | 34 | 80 |
| traps by r400 | 72 | 82 |
| digs in setup | 0 | 73 |
| level sum at r200 | 5.6 | 14.1 |

At least 83% of our setup traps sit within d2 8 of our flags. Gymhgy first sees one of our flags at median r243 against
our r279; it is first in 885 of 1,151 games. It then needs a median 25 rounds to make its first grab, where we need 6.
Symmetry is decided at median r2; 203 of 1,174 games decide after r200; there were 0 wrong decisions. Symmetry is not a lever.

## Levers
1. **One placement rule: reach-aware relocation with a spawn-zone threshold.**
   - Choose the spot by path distance over terrain reachable in setup, with dams counted as closed.
   - When the walk stalls, drop at the best tile reached instead of going home. The existing g4reach arm
     (C.RELOC_STALL_MOVES) covers this fallback.
   - Leave a flag in its spawn zone unless moving it gains at least ~5 path tiles. This is the break-even point:
     0.60 / 0.125 = 4.8 tiles for early capture, 0.92 / 0.16 = 5.7 for any capture.
   - Signature: new census columns weakFlagPath (path distance of our nearest flag at r200), weakFlagShort (best
     reachable minus actual) and flagsInZone, plus the existing defNearAtGrab20 and enemyCaptured600.
   - Cells: Canals A+B, QueenOfHearts A, Skyline A, Waterworld A, WheresMyWater A+B, StarryNight A, KingQuacksCastle A.
2. **Keep flags at spawn.**
   - Test it as C.RELOCATE_FLAGS=false, or as the rule-1 threshold, on maps where relocation takes the weak flag out of
     its zone and it falls.
   - Cells: GaltonBoard A+B (0/12 wins, weak flag lost by r600 in 12/12), HungerGames A (0/9), AceOfSpades B (0/5),
     DefaultMedium B (0/6), Checkered B, Swoop B, CH3353C4K3F4CT0RY B.
   - Signature: flagsInZone up, defNearAtGrab20 up, weak-flag capture round later.
3. **Setup traps by exposure.** Put most setup traps on the flag nearest by path distance (-0.041 per trap). New
   column: weakFlagTraps8. Same cells as lever 1. Expect a small effect.
4. **Low confidence: moat digs beside flags**, which work for Gymhgy (-0.18 per dug neighbour). For our flags,
   impassable neighbours show no protection.
5. **Not measurable here: bank crumbs through setup** and build traps in contact, as Gymhgy does. A within-map test is
   impossible because both bots' setup spending is fixed. It needs an arm.

## Not measured / caveats
- These are within-game associations, not causal effects. Lanes may confound the spawn-zone and edge terms.
- The edge term differs by side.
- The r200 board hides traps and terrain under robots, so traps8 and dug counts are floors.
- bestReach ignores flag spacing and the bot's limited map knowledge.
- firstFlagSight is per team, not per flag.
- Setup traps' triggers cannot be separated from post-setup traps (--trapgeo covers post-setup traps only).
- 193 census games had no replay.
- The game-level gain of any lever is unmeasured. The flag-level effects are large, but the first flag falls in 72% of
  wins too.
