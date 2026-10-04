# Lens 4: fight geography and economy, g_iter4 vs Gymhgy.v10official (existing games only)

## Short answer

1. **The headline split is mostly map mix and game length.** The headline is that we kill 200 of its robots on its ground in wins vs 73 in losses.
   - Across all games, within map, our total deaths on our own ground do not differ (−5).
   - In games that go the full 2000 rounds, the split is real and large: our deaths at home 140 vs 242, its deaths on its ground 252 vs 101.
   - Even there, only about 15% of the gap exists by r1000. The fight location drifts apart slowly from about r400, and most of the gap builds in r1000-2000.
2. **The fight sits in our half in every game, wins included.** The causes are the same in wins and losses:
   - Gymhgy banks about 3,900 crumbs through setup. It spends them on 31 stuns in the first 50 rounds after the dam drops, placed at the midline.
   - We spend our setup budget on 38 traps deep in our half. Only 8-12% of those fire by r250.
   - Its robots get 25-30% more kills per attack.
3. **The earliest win/loss difference is the dam-drop skirmish (r200-250).**
   - Net kills at r250: −1.5 in wins vs −2.6 in losses (t 2.4). Games that are level or ahead at r250 win 52% vs 40%.
   - The kill edge is gone by r300. What carries on is position (more of ours in its half at r400, t 3.2) and fewer of its early captures (t 3.5).
4. **Economy follows geography (in full-length games).**
   - A kill pays +30 only on enemy ground. Gymhgy earns about 3,000 more crumbs from kills on our ground in losses; we earn about 4,500 more in wins.
   - Trap counts follow the money: ours +38 and its −25, within map.
   - Upgrades are identical in every game: ATTACK r600, HEALING r1200, CAPTURING r1800, for both teams in all 501 surveyed games.

## Data and method

- **Census:** 993 filler games of g_iter4 vs Gymhgy (390 W / 603 L, 39%) on 78 maps, from `research/fill/fillcensus-g_iter4-*.csv`
  (87 files, read 20:20). That is more than the ~430 in the brief, because the filler kept running.
  - How losses end: 111 by capture before r600, 162 at r600-999, 105 at r1000-1999. 225 go to r2000 (95 LEVEL_SUM, 130 MORE_FLAG_CAPTURES).
- **Replay sample:** 943 replays were still on the VM. I took up to 4 wins and 4 losses per map, census games only.
  - That gives 502 replays (224 W / 278 L) on 74 maps; 73 maps have both a win and a loss.
  - Per replay, on the VM (niced, 2 at a time, text back only): `--metrics 10` to r1000, `--from 1 --to 1000`, `--map-at 250/300/400`.
  - From the event window I kept every death, trap build/trigger, spawn, flag pickup and capture with its tile. Attacks and heals were counted per 10 rounds.
  - A second VM pass ran `--survey` on the same replays (upgrades).
- **Geography:** each tile gets a front coordinate **f = d_ours / (d_ours + d_theirs)**, where d is the distance to the nearest spawn centre of each team.
  - f = 0 is our spawn, f = 1 is its spawn, and f < 0.5 is "our half". This is the census `side` rule, made continuous.
  - Check against the census flood-fill territory (games under r1000): r = 0.94 for our deaths at home and 0.97 for its deaths at home. The f counts run about 20% higher (they include neutral tiles).
- **Statistics:** every W-vs-L number is **within map**: the win mean minus the loss mean on each map, weighted by the harmonic n, then averaged over maps that have both.
  - t = diff / SE over maps. "maps w>l / w<l" counts the sign per map.
  - A window only counts games that lasted through it, so later windows compare long losses with wins.
- **Traces** (fetched to the driver): Mountain, side A, three games.
  - fill1791139587 `replays/` (win, LEVEL_SUM) and `losses/` (MORE_FLAG_CAPTURES): same filler run, map and side.
  - fill1791141550 `losses/` (MORE_FLAG_CAPTURES).
  - Modes: `--metrics 50`, `--map-at 200/240/280`, `--from 200 --to 260`, `--trapgeo`.

## Findings

### 1. The census split is mostly length and map mix; in full-length games it is real but late

| census, within map | all 993 games | per 100 post-setup rounds | 465 games that reached r2000 |
|---|---|---|---|
| our deaths on our ground (`deathsHome`) W / L | 115 / 143, diff **−5** (36 vs 40 maps) | 8.9 / 12.8, −2.1 | 140 / 242, **−76** (10 vs 38 maps) |
| its deaths on its ground (`enemyDeathsHome`) | 202 / 68, +115 | 13.4 / 5.9, +6.2 | 252 / 101, **+111** (36 vs 12) |
| kills / deaths | +171 / +55 | +7.2 / −0.6 | +85 / −53 |
| our traps / its traps | +70 / +35 | +0.3 / −3.4 | **+38 / −25** |
| its stun victims on us (`enemyStunVictims`) | +206 | −7.0 | −93 (13 vs 35) |
| its map crumbs (`gatheredAll`) | −109 | | −252 (11 vs 22) |

**Timing in the 196 full-length sample games** (98 W / 98 L), using the f measure:

| | by r600 | by r1000 | whole game (census) |
|---|---|---|---|
| its deaths in its half, W / L | 19.5 / 13.2 | 67.0 / 46.7 | 240 / 100 |
| our deaths in our half, W / L | 36.0 / 40.9 | 88.7 / 102.1 | 155 / 225 |

**Time course** (`out-ts-*.txt`, 50-round buckets, within map):
- r200-500: every death-by-half bucket differs by at most 0.6 a bucket.
- From r500, our deaths at home are lower in wins by 0.7-2.2 a bucket (t −1.3 to −3.0).
- From r700, its deaths at home are higher in wins by 1.5-2.2 a bucket (t 1.6-2.7).

**The late gap goes with the capture race but is not just its echo.** In the 78 full-length games where we were behind on captures at r1000:
- 21 were wins (comebacks).
- Those comebacks killed it on its ground 296 vs 108 times (within +126, t 2.4) and died at home 167 vs 235 times (−98, t −2.9).
- In the 85 games level at r1000, the split is +47 / −29.

### 2. Constant handicaps: the same in wins and losses

| r200-300 | ours (W / L) | Gymhgy (W / L) | within-map W−L |
|---|---|---|---|
| crumbs banked at r200 | 465 / 440 | **3,981 / 3,787** | +16 / +2 |
| traps built in setup | 38.6 / 37.1 (f median 0.14) | 2.4 (water traps at f 0.88) | +0.3 |
| stuns built r200-250 | 17.6 / 15.1 | **31.1 / 31.3** | +0.9 (t 1.7) / +0.4 |
| median f of r200-250 builds | 0.25 | 0.54-0.56 | 0.00 / +0.01 |
| median f of r200-250 stun triggers | 0.31 | 0.52-0.53 | 0.00 |
| stuns triggered r200-250 | 12.9 / 12.0 | 12.4 / 12.4 | +0.8 / +0.1 |
| kills per 100 attacks r200-300 / r300-400 | 2.3 / 3.5 | 2.9 / 4.2 | at most ±0.13 for either team |
| heals / attacks r200-250, r400-600 | 1.18, 1.36-1.39 | 1.35, 1.69 | 0.00 |

- **Our setup budget.** About 85% of it sits deep in our half and does not take part in the dam-drop fight (`out-setup.txt`, 488 games of 400+ rounds).

  | where (f) | setup traps a game | fired by r250 | fired by r400 |
  |---|---|---|---|
  | 0-0.15 | 20.5 | 7.7% | 41% |
  | 0.15-0.30 | 12.0 | 11.6% | 51% |
  | 0.30-0.40 | 2.5 | 24% | 52% |
  | 0.40-0.50 (dam line) | 2.3 | 62% | 88% |
  | 0.50+ | 0.7 | 38% | 87% |

- **Gymhgy's stuns fire at the midline. Ours fire only once its robots are deep in our half.**
- **First contact is identical in wins and losses.**
  - First death: r212 at f 0.48-0.49.
  - First big fight (8+ deaths in 20 rounds): r256-257 at f 0.37-0.38, inside our half.
  - At r250 both armies' median robots stand in our half: ours f 0.39-0.40, its f 0.44-0.46.
- **Its trap output does not differ between wins and losses.** That covers its stuns built and triggered r200-300, and its bank at r200. The census per-round gap (its traps 20.7 vs 27.7 per 100 rounds) is an artefact of game length: short games are mostly the r200-300 spend.
- **Upgrades are identical.** In all 501 surveyed games, both teams buy the same upgrades at the same rounds: ATK@600, then HEAL@1200, then CAP@1800, as far as the game lasts (226 games got all three, 62 ATK+HEAL, 141 ATK only, 72 ended before r600).

### 3. The earliest win/loss difference: the dam-drop skirmish, then position

| measure | W | L | within-map diff | t | maps w>l / w<l |
|---|---|---|---|---|---|
| net kills at r250 | −1.52 | −2.56 | +0.89 | 2.4 | 42 / 30 |
| our deaths in our half r200-250 | 4.55 | 5.08 | −0.57 | −2.0 | 26 / 45 |
| first big fight, its deaths − ours | −0.04 | −0.82 | +0.86 | 2.2 | 41 / 30 |
| net kills at r300 / r350 | −2.7 / −3.2 | −3.6 / −3.6 | +0.6 / +0.1 | 0.9 / 0.1 | |
| median f of r250-300 deaths | 0.37 | 0.34 | +0.03 | 2.2 | 46 / 27 |
| its robots deep in our half (f < 0.3) at r300 | 13.9 | 15.9 | −1.7 | −1.8 | 28 / 44 |
| our robots in its half at r400 | 18.0 | 15.4 | **+2.5** | **3.2** | 50 / 21 |
| its robots in our half at r400 | 22.9 | 24.7 | −1.9 | −1.8 | 26 / 46 |
| its captures before r400 | 0.63 | 0.81 | −0.20 | −3.5 | 15 / 34 |
| its first capture | r655 | r544 | +159 | 3.6 | 47 / 26 |
| its level sum at r600 | 222 | 224 | −4.9 | −2.6 | 24 / 46 |

**Win rate, conditional, within map** (`out-cond.txt`):

| condition | win rate | within-map diff |
|---|---|---|
| net kills at r250 ≥ 0 (vs < 0) | 52% (100/194) vs 40% (124/308) | +0.20, t 3.1 |
| ...in full-length games | 58% vs 46% | +0.37, t 2.9, only 11 maps |
| first big fight won (vs lost) | 49% vs 41% | +0.15, t 2.5 |
| 17+ of ours in its half at r400 | 51% vs 40% | +0.19, t 3.2 |
| no Gymhgy capture by r600 | 56% vs 42% | +0.35, t 4.8 |

**What goes with the skirmish result** (within-map correlation with net kills at r250, n 501):
- Its stuns built r200-250: −0.26. Our stuns triggered r200-250: +0.23.
- Its robots in our half at r250: −0.28. Ours in its half at r250: +0.20.
- No correlation: our r200 bank (0.00), our setup trap count (+0.05), side (+0.04), crumbs gathered by r200 (0.00).
- The chain is loose. The skirmish barely predicts the r300 intrusion (r −0.12), our r400 presence in its half (+0.15) or its captures by r600 (−0.07). Each link is real but explains little.
- Short losses (before r1000) and long losses differ from wins by about the same amount at r250-300.

**Trace: Mountain side A** (fill1791139587 win vs fill1791141550 loss; `--from 200 --to 260`, `--map-at`, `--trapgeo`).

| | win | loss |
|---|---|---|
| stun triggers in r201-260 | ours 17 (at x 22-28: the centre wall line and its side), its 7 | its 22 (at x 21-27, mostly on our side of the wall), ours 7 (deep, x 19-22) |
| deaths in r201-260 | its 11, ours 5 | ours 7, its 6 |
| our robots across the centre (x > 25) at r240 / r280 | 16 / 21 | 4 / 8 |
| its robots on our side at r240 / r280 | 5 / 13 | 18 / 29 (11 of them past x < 20) |
| its stuns built r200-300 (`--trapgeo`) | 26 (plus 24 explosives) | 59, freezing 342 of ours (5.8 per stun) |
| kills by r300 | 17-8 | 8-18 |

- The other Mountain loss (fill1791139587 `losses/`) looks the same as the loss column: its 52 stuns r200-300 froze 231 of ours, and kills were 5-17 by r300.
- Both Mountain losses were decided by MORE_FLAG_CAPTURES at r2000. On Mountain, the census full-length deaths-at-home gap is −172 (ours) and +111 (its).

### 4. Economy loop

- A kill pays +30 only when the killer stands on enemy ground.
- In full-length games, from census within-map diffs:
  - Gymhgy's kill income is about 30 × 76 ≈ 2,300 crumbs higher in losses (raw 30 × 102 ≈ 3,000).
  - Ours is about 30 × 111 ≈ 3,300 higher in wins (raw 4,500).
  - Trap counts move to match: ours +38, its −25.
  - It also collects about 250 more map crumbs in losses (`gatheredAll`, 11 vs 22 maps).
- Early (r200-300) the loop is negligible: one extra kill on our ground is 30 crumbs.
- The r300 bank is a weak signal. Its bank at r300 is 1,134 vs 1,991 in full-length games (−417, t −2.0), but only −206 (t −1.3) over all games.

## Levers

### L1. Dam-line battery: put the setup budget where the first fight happens

- **Problem.** About 32 of our 38 setup traps sit at f < 0.3, and only 8-12% of those fire by r250. The 2.3 we put at the dam line fire 62% by r250.
- **Gymhgy's version.** It wins the first 50 rounds with about 31 midline stuns, paid for with a 3,900-crumb bank.
- **What to change.** Move a large share of the setup traps (or hold the crumbs and spend them in r200-230) onto our side of the dam line, on the tiles its first wave crosses at r201-215. Keep a smaller ring on each flag: deep traps fire 41-51% by r400, when raids reach the flags (lens 2), so they still have a job.
- **Expected effect.** Turn some of the r200-250 skirmishes we now lose. Level-or-ahead-at-r250 games win 52% vs 40%, so moving the share of level-or-ahead games from 39% to about 50% would add 1-3 points of win rate if the link is causal. The effect is modest.
- **Census columns to add:**
  - `setupFired250`: our setup traps triggered by r250. Now about 4.3 a game; target 12+.
  - `stunTrig250`: our stun triggers r201-250. Now 12-13.
  - `netKills250`: kills − deaths at r250. Now −1.5 in wins, −2.6 in losses.
  - The `--trapgeo` trigger f of r200-250 stuns should move from 0.31 to about 0.45.
- **Chosen-map cells.** Use maps where the r250 net-kill gap between wins and losses is largest and the record is mixed: Fusbol (A 3/5, B 5/6), Hurricane (3/3, 4/4), Islands (3/3, 4/4), Randy (A 5/5), Mountain (A 6/6, B 3/3) and Swoop (A 7/5, B 3/6). Run both sides of each, with a g_iter4 control on the same seeds.

### L2. Take the fight into its half after the skirmish

- **Evidence.** In wins, more of ours stand in its half at r400 (+2.5, t 3.2; the best single position predictor, 51% vs 40%).
  - From r400, Gymhgy dies on its own ground more in wins. That pays us the +30 kill reward and denies it the same.
  - In full-length games, the r400-600 window already shows our deaths at home −5.1 (t −3.6) and its deaths at home +3.3 (t 3.4).
  - In comebacks from behind on captures, the late push is almost the whole difference.
- **What to change.** When no flag of ours is threatened (no enemy carrier, no group of 6+ deep in our half), put the army's rally point at f 0.5-0.6 (the midline or its side) instead of our half. Pair it with lens 2's raid response, because raids of 12+ convert about 48%. TACTICS T6 ("advance margin") was neutral on the field. Gymhgy, which fights from our half by default, is a different case and is untested.
- **Census columns:**
  - `inEnemy300` (exists) and a new `inEnemy400`. Now 15.4 in losses / 18.0 in wins; target 20+.
  - A new `enemyDeathsHome600`: its deaths on its ground by r600. Now about 11-13 in r400-600.
  - A new `deathF600`: median front coordinate of r400-600 deaths. Now 0.43 / 0.46; target 0.50+.
  - `deathsHome` per round (exists).
- **Chosen-map cells.** Use full-length maps with mixed results and a large gap: Mountain (A 6/6, B 3/3), Fusbol (A 3/5, B 5/6; full-length gap −96 / +179), Alligator (B 5/6; −129 / +226), Hockey (A 4/5, B 2/4; −216 / +465), OceanFloor (A 4/4, B 6/5) and Funnel (A 5/4). Run both sides, and judge at r600 and r1000, not only at the result.

### L3 (measurement only). Deep-intruder count

- Gymhgy robots at f < 0.3 at r300: 13.9 in wins vs 15.9 in losses (−1.7, t −1.8). Games with 12 or fewer win 49% vs 41%.
- This is the bridge to lens 2's raid groups. A census column `enemyDeep300` would show whether L1 or L2 keeps its raid group out.

## Not measured

- **Positions between snapshots.** Positions exist only at r250, r300 and r400. Where the armies stand after r400, and the ~85% of the full-length gap that builds after r1000, are known only as census totals. Events were read to r1000 only.
- **Stun victims per window.** Only trigger counts per window; victims are whole-game census numbers.
- **Kill attribution.** Attack vs trap kills are not separated, so "kills per attack" includes trap kills.
- **Exact crumb income.** The build-level discount is unknown. "Income" is approximate, and map crumbs per window were not read (only census `gathered200/400/All`).
- **Coverage.** 5 maps have no surviving replay pair. The traces are three games on one map and side (Mountain A).
- **Causality.** Every number here is observational. The skirmish, position and capture links are each modest, and a chosen-map test of L1 and L2 is the way to find out whether any of them is causal.

Scratch: tables in `fight/final-all.txt`, `out-*.txt`; feature pickle `fight/FS.pkl`; raw extraction `ext-0.out`, `ext2-*.out`, `fight/survey.out`.
