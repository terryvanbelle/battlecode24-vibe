# Lens 3: our offense against Gymhgy.v10official (g_iter4, existing games only)

## Data and method
- **Replays:** 627 g_iter4 vs Gymhgy filler replays still on the VM (`gauntlet/*-scrim-g_iter4-fill*/{replays,losses}/Gymhgy*`), 241 wins and 386 losses, all 78 maps. Our side is split A 319 / B 308.
- **Census:** the census now holds 993 games (390-603). Its numbers agree with the replay set: our `regrabs/carrierDeaths` is 0.20 against Gymhgy's 0.43, and our `carrierMoves/carrierRounds` is 0.447.
- **Tool:** a scratch copy of ReplayDump (`off/src/offdump/OffDump.java`). It adds `--carrier`, which writes:
  - one row per carrier per round: position, hp, move cooldown, enemies and allies within dist2 20 and dist2 8;
  - every attack and heal on a carrier;
  - every trap triggered within dist2 13 of a carrier;
  - the post-setup grid and the flag homes.

  It ran niced on the VM together with `--flags --defense --track`, two at a time, with output in VM `/tmp/gymoff`. Only text came back. I played no game and edited nothing in the repo.
- **Trip:** one carrier's pickup until its CAPTURE, DROP or DIED.
- **Journey:** one flag's excursion from a first grab until capture or return home. A journey is the unit that compares fairly with Gymhgy's relay, because a relay is many trips in one journey.
- **Walk:** BFS over the r201 terrain from the flag to the carrier team's spawn-zone tiles, 8-connected, through no wall and no water. Where water cuts every path, the walk ignores water.
- **Win/loss comparisons:** made within each map (72 maps hold both a win and a loss: 228 wins, 354 losses), as the mean over maps of (W - L), with the standard error.

## 1. Headline numbers
| per game | us | Gymhgy |
|---|---|---|
| journeys (first grabs) | 18.3 | 9.2 |
| journeys captured | 1.45 (7.9% of 11,368) | 2.03 (22.0% of 5,729) |
| same, wins / losses | 10.9% / 5.5% | 15.5% / 25.7% |
| carriers per captured journey | 1.35 | 10.0 (relay) |
| speed of captured journeys, BFS tiles per round | **0.46** (29 tiles in 68 rounds) | **0.63** (29 tiles in 51) |
| re-grab after a carrier death (before the flag returns) | **19.5%** of 13,107 | **44.1%** of 7,018 |

## 2. Where and how our carriers die (13,107 carrier deaths)
- **Near its flag.** 29.6% die within 3 tiles of its flag home and 48.4% within 5.
  - Of the journeys that fail, 28% end with under 10% of the walk done and 63% with under 25%.
- **Quickly.** The median hold is 9 rounds (mean 17.8) for 8.3 moves. 18.5% die within 2 rounds of the pickup, 36% within 5 and 53% within 10.
- **Outnumbered.**
  - At death there are 8.3 of its ducks within dist2 20 against 3.4 of ours, and 4.2 against 1.2 within dist2 8.
  - Chasers outnumber escorts in 86% of the deaths, and are at least twice the escorts in 71%.
  - 51% of our carriers die with **no ally within dist2 8**; Gymhgy's figure is 24%.
  - A death takes 2.7 distinct attackers and 4.0 hits; our carrier gets 1.7 heals.
- **Traps play a small part.** A stun froze the carrier in 20% of the deaths; explosive traps in about 1%.
- **The grabbing duck is often already hurt.** Over 11,038 first-grab trips, its HP at the first carried round splits as:
  - under 500 HP: 21% of grabs, 2.4% captured;
  - 500-899 HP: 21%, 5.0% captured;
  - 900 HP or more: 51%, 9.3% captured.
- **Two kinds of death.**
  - Near the flag (47% of deaths): 8.8 chasers against 4.0 escorts.
  - Far, after at least 25% of the walk (29%): 7.6 chasers against 2.3 escorts and 0.7 within dist2 8, after 36 rounds. The escorts have fallen away; Gymhgy's far deaths still have 8.9 escorts.
- **The response comes fast, the walk is slow.** The median is 3 rounds until the first chaser comes within dist2 20, and 7 rounds until three do. 57% of grabs are seen within 5 rounds and 80% within 20. Walking home at 0.45 tiles per round takes a median 76 rounds.
- **A grab converts only if it stays unopposed.**

  | first-trip mean chasers | journeys | captured |
  |---|---|---|
  | under 0.5 | 1,823 | 30% |
  | 0.5-2 | 3,905 | 5.5% |
  | 2 or more | 5,640 | 2.7% |

- **Even unopposed grabs fail on long walks.** Captured share of unopposed first trips by walk: 24 tiles or less 50% (310), 25-34 tiles 32% (484), 35 or more 21% (982).

## 3. Its flags: relocation and walk length
- Its flag homes sit a mean 5.6 tiles (Chebyshev) from their spawn centres, and 48% are moved 5 tiles or more (1,404 flags).
- The move adds **+4.5 BFS tiles** to our walk on average; 38% of its flags are 5 or more tiles farther from our zone than their centre. The mean walk from its flag home to our zone is 32 tiles.
- Capture rate falls steeply with the walk:

  | walk (tiles) | journeys | captured |
  |---|---|---|
  | under 20 | 1,021 | 18.7% |
  | 20-29 | 3,313 | 9.7% |
  | 30-39 | 3,841 | 6.2% |
  | 40-49 | 2,139 | 3.6% |

- **We do not favour the short flag.** We spread our raids evenly over its three flags: 7.4, 8.1 and 6.6 journeys a game, ordered from shortest to longest walk (468 games). The shortest-walk flag converts best (9.3% against 7.0% and 7.7%).
- Conversion is flat in the number of flags we already hold (8.4%, 7.0%, 8.5%), so there is no "last flag" effect.

## 4. Its offense compared
- **Convoy.** Gymhgy carries inside a dense blob. Its voluntary hand-off legs have 11-12 of its ducks within dist2 20 and 6.5 within dist2 8; its carriers die with 2.5 allies within dist2 8. A dead carrier's flag is re-picked 44% of the time.
- **Escorts decide its conversion; ours barely move.**

  | first-trip escorts within dist2 8 | Gymhgy captured | our captured |
  |---|---|---|
  | under 1 | 2.6% | 5.6% |
  | 1-2 | 7.7% | 9.9% |
  | 2-4 | 18.5% | 9.8% |
  | 4 or more | **38.5%** | 10.6% |

- **Its convoy survives our defenders.** When 5 or more of ours are near its first carrier, it still captures 28.5%. Ours captures 2.6% once 2 of its ducks are near.
- **Its convoy is faster.** The relay moves its flag at 0.63 tiles per round to our 0.46, even though each of its carriers moves less than ours (0.17 to our 0.45 moves per carried round).

## 5. Natural experiment: CAPTURING at r1800
Both bots buy ATK at r600, HEAL at r1200 and CAP at r1800 (survey of 24 sampled games). The comparison below uses the same 190 games, all of which reach r1900.

| our journeys started | carrier moves per round | captured | captured if unopposed | captured if contested | re-grab trips |
|---|---|---|---|---|---|
| r1501-1800 | 0.445 | **3.2%** (864) | 9.4% | 2.1% | 19% |
| r1801-2000 | **0.732** | **13.9%** (346) | 42.2% | 9.6% | 30% |

- **Its defense did not thin after r1800.** Our unopposed share went 16% to 13%, and the chasers at our carriers' deaths went 7.8 to 8.3.
- **Gymhgy gains less from the same upgrade.** Its own rate rose 4.2% to 12.5% (small n).
- **Reading:** carrier speed, plus the 25-round return window, roughly quadruples our conversion. We get CAPTURING 600 rounds after we could.
- **Volume affected.** We start 8.1 journeys a game in r1201-1800 (320 games pass r1300), and they convert at 4.8%.

## 6. Wins against losses, within map (72 maps)
| metric | W | L | W - L | t | maps W > L |
|---|---|---|---|---|---|
| journeys a game | 21.8 | 16.2 | +5.6 | 4.5 | 55/72 |
| conversion per game | 19.5% | 12.9% | +6.7 pp | 3.5 | 53/71 |
| conversion when unopposed | 49% | 40% | +9.3 pp | 2.8 | |
| re-grab after death | 0.201 | 0.147 | +0.055 | 3.1 | |
| deaths with no ally within dist2 8 | 52.5% | 60.0% | -7.4 pp | -3.7 | |
| escorts at death, dist2 20 | 3.5 | 2.7 | +0.81 | 4.5 | |
| escorts at death, dist2 8 | 1.26 | 0.92 | +0.34 | 3.8 | |
| mean escorts on far-dying trips | | | +0.62 | 3.6 | |
| **Gymhgy's conversion** | 25.8% | 40.2% | **-14.4 pp** | **-6.0** | |

- In losses our carriers walk farther (9.3 moves against 7.2) but more alone: 3.3 escorts against 5.1.
- Our side (A or B) makes no difference: 8.2% against 7.8%.
- **How the losses end** (census, 603 losses):
  - CAPTURE: 378;
  - MORE_FLAG_CAPTURES: 130, of which 101 were one capture short;
  - LEVEL_SUM at equal captures: 95.

  So 196 losses (33%) were within one capture of a draw or better.

## 7. Traces
**YearOfTheDragon, side A, loss** (`gauntlet/20261004-184142-scrim-g_iter4-fill1791139295/losses/Gymhgy.v10official__YearOfTheDragon__botA.bc24`)

- **r212-231, a carrier dies at its base.** Our carrier #11365 grabs flag B52 at (12,0) with none of its ducks within dist2 20.
  - The first chaser arrives at r214, a water trap fires at r216 and a stun at r221 (move cooldown 40).
  - At r227 it has 11 chasers against 8 escorts within dist2 20. At death, r231, 17 against 5, and 9 against 0 within dist2 8.
  - Five attackers land six hits between r227 and r231. The carrier made 7 moves in 19 rounds and died at (13,8), 8 tiles from the flag.
- **Two captures, both unopposed.** B797 at r259-308 (mean 0.6 chasers) and B52 at r359-397 (0 chasers).
- **The third flag never comes home.** B436 was moved 10 tiles in setup, to the corner (36,0), which leaves a BFS walk of 34. We grabbed it 7 times between r377 and r978:
  - every carrier was nearly alone (escorts within dist2 20 averaged 0-5, and 4 of the 7 had nobody within dist2 8 at death);
  - each died 11-22 tiles out.

  Gymhgy captured our third flag at r1029.
- **Its relay in the same game.** Flag A802, r328-397: 10 hand-offs, with 9-16 escorts on each leg.

**Mountain, side A, loss at r1988** (`gauntlet/20261004-190223-scrim-g_iter4-fill1791140536/losses/Gymhgy.v10official__Mountain__botA.bc24`)

54 journeys and 0 captures.

- **The shortest flag is camped.** Flag B1356 was moved from (30,26) to (36,20), a walk of only 15 tiles. We grabbed it 24 times.
  - Its defenders sat on it: 3-9 within dist2 20 at grab time.
  - 21 of our 24 carriers made 0-6 tiles before dying 1-4 tiles from its home, with 5-12 chasers against 0-3 escorts. The other three walked to within 1-3 tiles of our zone and still died.
  - 13 of the 24 grabs were by ducks under 600 HP, for example 97, 164 and 180 HP.
- **r1564-1590, a lone walk ends 2 tiles short.** Carrier #12892 walked 12 tiles west at exactly one tile every two rounds, with one escort from r1572 on.
  - Two single Gymhgy ducks took turns hitting it: #12225 at r1574-1577 and #10749 at r1585-1588. Three heals from two allies did not keep up.
  - It died at (22,23), 2 BFS tiles from our zone, with 7 chasers at the end.
- **r637-642, a grab inside its defense.** Carrier #10404 grabbed with 775 HP and 9 chasers; three attackers killed it within 5 rounds.

## 8. Levers
1. **Buy CAPTURING at r1200 (`C.UPGRADE_ORDER=3`: ATK r600 > CAP r1200 > HEAL r1800).**
   - Evidence: §5. It doubles carrier speed and opens the re-grab window. Over the ~8 journeys a game in r1201-1800, the r1800 jump would be worth up to about +0.9 captures a game in games that pass r1300; that is an upper bound.
   - Risk: HEAL slips to r1800. Capture-first lost 14-33 on an old band in the g_iter1 era, and order 3 was never tested against Gymhgy.
   - Columns: `capturedLate` and `regrabsLate` exist. A simple new `carrierSpeedLate` (carrier moves per carried round after r1200) should go from 0.45 to about 0.73.
   - Chosen-map cells (most games past r1300, 11-21 of our journeys a game after r1200, conversion 0.8-5.5%): **Valentine A, Diagonal B, EndAround B, Backslash A, Rainbow B, Soccer A.**
2. **A convoy that re-grabs.**
   - Evidence: §2, §4 and §6. Half our carriers die with nobody within dist2 8; our re-grab rate is 0.20 against its 0.43; wins carry more escorts.
   - Behaviour: when a duck of ours carries, the 2-4 nearest non-defenders hold a tile within dist2 2-8 of it, one behind and beside, so they can re-grab within the 4-round window. They hit any enemy within dist2 4 of the carrier before anything else. The carrier steps only while at least one escort is adjacent once a chaser is in view.
   - Existing switches to start from: `ESCORT_CARRIER` and `ESCORT_BEHIND` (C9). Neither has been tested against Gymhgy.
   - Columns: `regrabs/carrierDeaths` (both exist; target at least 0.35) and a new `esc8` (mean allies within dist2 8 of our carrier per carried round; about 1.9 now, Gymhgy about 4-6).
   - Cells (carriers escape its base and die later, 11-15 far deaths a game, 0.4-1.1 allies within dist2 8 at death): **Valentine A, Foxes B, Backslash A, Battlecode24 B, Soccer A, KingQuacksCastle B.**
3. **Do not give the flag to a hurt duck.** 42% of first grabs are by ducks under 900 HP, which convert 2.4-5.0% against 9.3%.
   - Rule: with an ally of at least 900 HP within dist2 8, a duck under 600 HP heals or steps aside instead of picking up.
   - Column: new `grabHp` (mean HP of our duck at first grabs; 711 among the carriers that die).
   - Caveat: hurt grabs are commoner in wins (within-map -47 HP, t -3.2), because they come with pressing fights, so this is the weakest lever.
4. **Raid by walk length.** Shorter walks convert far better (§3) and we raid evenly.
   - Rule: weight the field target toward the flag with the shortest BFS walk, unless its defenders are camped on it. Mountain's B1356 had 3-9 defenders on it and went 0 for 24.
   - Expected gain: at most about +0.3 captures a game.
   - Column: new `grabRank0` (share of first grabs on the shortest-walk flag; about 0.34 now).
   - Cell: Mountain A, to watch how it handles a camped short flag.
5. **The relay itself is not the lever on our side.** Gymhgy's relay is fast because 6 or more ducks sit within dist2 8. The g4relay and g4relay2 tests failed because we have about 1-2 adjacent allies. Make lever 2 work first.

## 9. Not measured
- **Escort geometry:** only counts within dist2 8 and 20 were logged, not ahead, behind or side.
- **What our escorts do:** whether they attack the chasers or heal; only attacks and heals on the carrier were logged.
- **Counterfactual routes:** whether a different path or spawn target would have avoided the chasers.
- **Its robots' roles:** which are defenders and which are responders (an external bot has no readable roles).
- **CAPTURING's two effects:** speed and the 25-round window are only partly separated (re-grab trips 19% to 30%, speed 0.45 to 0.73).
- **Whether CAP at r1200 would reproduce the r1800 jump:** levels and Gymhgy's own CAP differ then.
- **Terrain changes:** BFS uses the r201 terrain and ignores later digs and fills.
- **Open journeys:** journeys still out at game end are excluded (0.14 a game).
- **Pruned games:** 366 census games have no replay any more.

## Files
- Scratch directory: `/tmp/claude-1000/-home-terryvanbelle-projects-vibe-2024/0c12d742-3a89-49d0-8e9e-654c983bb00e/scratchpad/gymstudy/off/`
  - parser and tables: `parse.py`, `build.py`, `games.pkl`;
  - analyses `a1.py` .. `a9.py` with their outputs `a*.txt`, plus the ad-hoc outputs `a10.txt` .. `a15.txt` (the last summaries were run inline);
  - per-game dumps: `out/*.txt.gz`.
- VM copies: `/tmp/gymoff/`.
