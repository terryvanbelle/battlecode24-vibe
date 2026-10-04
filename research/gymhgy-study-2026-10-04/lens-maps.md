# Lens 1: maps. Why g_iter4 loses to Gymhgy.v10official, by map

Data: census snapshot of 499 filler games (g_iter4 vs Gymhgy, random maps and sides, 78 maps, 2-10 games a map),
**201-298 (40.3%)**. Replay metrics (`--metrics 25`) for the 318 of those games whose replay still exists on the VM;
`--levels` on all 57 LEVEL_SUM replays; traces named below. Static traits come from the 78 `engine/maps/*.map24` files
(a scratch Java reader, BFS on the map grid). Scratch scripts: `gymstudy/maps/`.

## 1. Map identity matters a little; side does not

| test | result |
|---|---|
| per-map win rate, heterogeneity (map-label permutation) | chi2 101 on 77 df, **p 0.02**; true spread between maps SD ~0.11 around 0.40 |
| split-half reliability of a map's win rate (n~6.4) | **0.36**: a map's record is mostly noise |
| winless maps | 8 seen, 5 expected if every map were 40% (p 0.11) |
| empirical Bayes | prior worth 20 games; Puzzle 0-9 shrinks to 0.28, P(true < 0.25) = 0.38. No map is confidently hopeless |
| side within map (A vs B, permutation) | **p 0.75**: no side effect. Side A 98-148, side B 103-150 |
| reliability of *how* a map ends | reached r2000: 0.85; lost by CAPTURE before r1000: 0.70; its captures by r600: 0.88 |

So map records cannot sort maps into hopeless and flippable. The *loss mode* is a stable map property, so the useful
split is by mode and by trait.

## 2. Static traits: weak predictors, with one strong exception

Rank correlation (game-weighted, map permutation) of 17 static traits with the per-map win rate: only spawn distance
shows up. Closer spawns help us (pathWaterMin r -0.27, p 0.013; the nearest third of maps by straight-line spawn distance 49% vs 35-38%). Area, walls, water
and dam count all have p > 0.1. Symmetry: vertical 43%, rotational 40%, horizontal 35% (not significant).
Flag distances (census flagDistMean, both rows) predict capture *speed* strongly (its captures by r600 r -0.42, ours r -0.32,
both p < 0.003) but not the win rate (r -0.10 / -0.15).

**Centre crumbs (crumbs neither side can reach in setup, i.e. behind dams):** the exception.

| centre crumbs | maps | games | W | lost by CAPTURE < r1000 | r200-400 crumbs us / Gymhgy |
|---|---|---|---|---|---|
| >= 9,000 | 17 | 106 | **0.31** | **0.45** | 5,664 / 11,927 |
| 3,000-9,000 | 21 | 136 | 0.43 | 0.19 | 1,676 / 3,007 |
| < 3,000 | 40 | 257 | 0.42 | 0.22 | 149 / 275 |

Map permutation, >= 9,000 vs the rest: early-capture loss **+24 points (p 0.0009)**, win -11.6 points (p 0.057). The
observed version (Gymhgy out-collects us by > 2,000 in r200-400; 20 maps) gives win -11.7 points (p 0.045). Within the
maps with >= 3,000 centre crumbs, wins have a smaller crumb gap than losses (-434; 21 of 33 maps). Replays: on the >= 9,000 maps Gymhgy
**holds 10,652 crumbs at r225 to our 479**. The opening exchange is even (+1.8 at r250), but we are at -10.3 kills
by r400 and its captures by r600 are 1.68.

## 3. Three kinds of loss, three groups of maps

| group | maps | W-L | losses: early CAP / late CAP / MFC / LVL |
|---|---|---|---|
| centre-crumb (>= 9,000) | 17 | 33-73 (31%) | 48 / 10 / 10 / 5 |
| stalemate (r2000 in >= 2/3 of games) | 22 | 61-87 (41%) | 3 / 8 / 33 / **43** |
| other | 39 | 107-138 (44%) | **80** / 33 / 20 / 5 |
| all | 78 | 201-298 | 131 / 51 / 63 / 53 |

### 3a. Centre-crumb maps: Gymhgy takes and banks the crumbs the dam was hiding
- **GaltonBoard, A, loss r540** (`fill1791136579/losses/...GaltonBoard__botA`): all 37,800 crumbs lie in a strip behind
  a dam. At r199 our ducks stand along that dam on our own half, but at r200 they turn to the centre fight and leave
  the strip untouched (our bank 205 -> 130 by r240). Gymhgy eats its half (+10,440 by r240), builds 104 stuns by r300
  (ours 28), and wins the fight 33 kills to 4 by r300. Its first capture is at r270.
- **AceOfSpades, A, loss r379** (`fill1791135313`): Gymhgy goes 2,035 -> 14,835 crumbs in r200-220 (the walled spade),
  builds 141 stuns by r360 (ours: 21 after setup), and grabs our flags at r239/262/267. Captures come at r295, r299 and r379, the last legs
  with 0 chasers.
- Same pattern (Gymhgy's share of r200-400 crumbs): KingQuacksCastle 95%, StarryNight 98%, GaltonBoard 91%,
  AceOfSpades 88%, HungerGames 80%, EvilGrin 80%. Exceptions: MIT (7-3; we take 40%) and Joker/Snake (we take 44-58% and
  still lose: Snake, A, r604, every one of our 12+ first grabs dies on the long route while its relay captures unopposed).
- Our bot already has the switches, off by default: `C.POST_SETUP_CRUMBS` and `C.CRUMB_STEP` (arm g4crumb, INCONCLUSIVE
  at 96 cells on random maps vs Cyril). Against Gymhgy the effect should concentrate on these 17 maps.

### 3b. Stalemate maps: level sum at 0-0
- LEVEL_SUM games by capture score: **0-0: 9-24**, 1-1: 13-14, 2-2: 32-15. The 0-0 losses are concentrated on a few maps:
  EndAround 1-6, DefaultSmall 0-4, GravitationalWaves 0-2, Fusbol 0-2, Hurricane 1-2. Mountain 3-2 and Tunnels 4-1 are the maps we win.
- 17 of the 0-0 level-sum games have replays (4-13): level sum **422 vs 433**. Split by skill: attack +17, build -11,
  heal -19. **We dig 0 times in all 57 level-sum replays; Gymhgy digs 73 a game in setup** (its level sum at r200 is 13-15
  to our ~6). Trace, **DefaultSmall, A, loss** (`fill1791134708`): 455 vs 496. Ours: 28 ducks at a3b0h6 (9 levels). Its
  healers mostly a3b1-3h6 (10-12 levels).
- The margins are thin. Over the 26 level-sum losses with replays: median 17, +10 levels flips 8, **+30 flips 20**, +50 flips 24.
  Our headroom to build 3 on every duck is ~127 levels a game. Each build level costs 5 builds (5 digs, about 100 crumbs or
  less with the build discount). In level-sum games the margin tracks our kills (r 0.81) and our traps (r 0.70), so these
  are the games with little fighting.

### 3c. Other maps: early relay captures without a crumb windfall
80 early-capture losses on 39 maps (DefaultHuge 1-5, Ambush 1-4, Decision 1-4, Skyline 2-7).
**DefaultHuge, A, loss r354** (`fill1791139295`): Gymhgy enters r200 with 3,955 crumbs to our 165 (we spent ours on
49 traps around our flags in setup). It builds 56 stuns by r300 and wins r200-250 10 kills to 1. It grabs our flags at
r239 (10 defenders near) and r260, hands them off every 1-3 rounds and captures at r269/283/354, the last legs with no
chaser. Across all 318 replays Gymhgy's bank at r200 is ~4,300-4,500 to our ~520-730, **in wins and losses alike**: a
structural difference, not a within-map decider. Within a map the decider is its captures by r600 (lower in wins on
40 of 48 maps, -0.54). The opening exchange is weaker (K-D by r300 +1.7 in wins, 31 of 53 maps).

## 4. Recommended chosen-map diagnostic cells
Side has no measurable effect, so play both sides where the budget allows. Otherwise use the side listed first (more
observed losses).

| loss kind | cells | why |
|---|---|---|
| crumb windfall | **GaltonBoard B, A**; **AceOfSpades B, A**; KingQuacksCastle A; HungerGames A; StarryNight A | 1-17 combined (GaltonBoard and AceOfSpades 0-8 on both sides); Gymhgy takes 80-98% of the r200-400 crumbs; small maps (cheap); early CAP losses |
| crumb control (no harm) | MIT B | we win 5-1 while taking 40% of a 42,800 centre |
| level sum at 0-0 | **DefaultSmall B, A**; **EndAround A, B**; GravitationalWaves A | 1-16 combined, 13 of the losses on LEVEL_SUM; DefaultSmall 31x31 runs fast even to r2000 |
| level-sum control | Tunnels A, Mountain A | we win 0-0 level sums there (7-3); a lever must not lose them |
| early relay capture, no crumbs | **DefaultHuge B, A**; Ambush B; Skyline A | 10 losses, all by CAPTURE, 9 of them by r525; its captures by r600 2.0-3.0 |
| long route, carriers die | Snake B; Foxes B; BigDucksBigPond A | 0-13 on those cells; we grab, our carriers die, it wins by 1 capture |

## 5. Levers
1. **Take and bank the centre crumbs** (g4crumb = `C.POST_SETUP_CRUMBS` + `C.CRUMB_STEP`, possibly with a bank floor
   for r250-400). Fires as: our gathered400 - gathered200 on the >= 9,000 maps rising from ~5,700 toward Gymhgy's ~11,900
   (existing census columns), plus a new `bank250` column (team crumbs at r250; Gymhgy 10,679 vs our 206 there). Ceiling:
   if the 17 maps played like the rest (44%), about +14 wins in 499 (+3 points).
2. **Farm levels in a stalemate** (new switch, e.g. `C.LEVEL_FARM`): after ~r1600 with captures level and no carrier in
   transit, ducks with build < 3 dig (backfield tiles; fill back if it blocks paths), late traps go to the ducks with the
   least build XP, and attack/heal masters heal to raise their capped skills. Fires as: a new `levelSumEnd` / `levelGapEnd`
   column (team level sum at the last round, already in `--metrics`), plus `digsLate` (digs after r1500). Ceiling: +30 levels
   flips 20 of 26 observed level-sum losses; 53 LEVEL_SUM losses in 499, so up to about +8 points.
3. **Setup economy (cross-map)**: Gymhgy digs ~73 times in setup and enters r200 with ~4,400 crumbs; we dig 0 times and
   enter with ~600, having spent the rest on setup traps. `C.SETUP_DIGS` exists (arms a3dig5/10, b2dig5). Fires as: a new
   `bank200` column and level_sum at r200. Diagnostic on DefaultHuge B/A, where the opening is lost 10-1 by r250.

## 6. Not measured
- Replay numbers cover the 318 of 499 games whose replay survived. Losses and wins were pruned alike (123 W / 195 L
  remain: 38.7% against 40.3% in the census).
- No map has more than 10 games; per-cell (map+side) records (<= 6 games) are noise.
- What Gymhgy spends its 10k centre-crumb bank on after r300 was not traced trap by trap (`--trapgeo` would show it).
- How much of the level gap the jail XP penalty causes was not separated from earned XP.
- Chokepoint width and dam geometry beyond counts were not computed.
- Whether taking the crumbs or farming levels actually flips games: only a diagnostic can show that.
