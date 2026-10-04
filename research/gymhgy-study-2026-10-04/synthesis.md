# Why g_iter4 loses to Gymhgy.v10official: synthesis of five lenses

Inputs: lens 1 MAPS (census 499 games, 318 replays), lens 2 THEIR OFFENSE (394 replays, 3,553 chains on our flags,
census 536), lens 3 OUR OFFENSE (627 replays, census 993), lens 4 FIGHT AND ECONOMY (census 993, 502 replays), lens 5
SETUP AND FLAGS (census 1,174, 981 replays). The win rate is about 40% in every snapshot (40.3%, 39.9%). Code read:
src/bot/Duck.java, Micro.java, Nav.java, Comms.java, C.java, G.java. History read: research/CRACK-GYMHGY.md,
TRAINING_LOG.md (arm results), tools/arm-intent.txt, tools/delivery-gate.sh, tools/replaydump/ReplayDump.java (census
columns). No games were played and no repository file was changed.

Maps and sides may be chosen for diagnostics against Gymhgy (PROMPTS 178 and the owner's relayed request). Every 5(a)
design below uses chosen cells. The ladder and pre-registered victory reads stay on random scrimmages, because chosen
cells would bias the rating.

## Summary

- **Gymhgy grabs our flags just as often in our wins as in our losses.** What decides the game is how many grabs it
  turns into captures, and nearly all of the extra captures in our losses are unopposed. Its first capture, of our
  nearest flag, comes early in wins and losses alike. The second and third flags decide.
- **Its relay is a slow, dense convoy.** Our ducks are 5-10 tiles away, kept off by its screen and stuck in fight micro.
  Our sighting of the flag goes stale on 46% of the rounds of a chain that ends in a capture. In the code, the fight
  branch of `Duck.turn` never looks at the chase target. A carrier sighting stays usable for only 5 rounds, and seeing
  our flag dropped on the ground does not count as a sighting.
- **Two loss classes have a cause we can see in the code.** Stalemates lost on level sum: at 0-0 we are 9-24 and never
  dig, while the median margin is only 17 levels. Centre-crumb maps: win rate 0.31 against 0.43 elsewhere, and g_iter4
  never picks up crumbs after setup.
- **Some differences are structural: they are the same in wins and losses.** These are the r200 bank (about 4,000 crumbs
  against about 500), setup digs (73 against 0) and kills per attack (+25-30% for Gymhgy). Setup placement is fixed per
  map, so it cannot separate wins from losses within a map.
- **Ranked levers:** (1) farm build XP late in a stalemate, (2) harvest the centre crumbs on the 17 centre-crumb maps,
  (3) a convoy response (fight toward our flag on a track that survives relay drops), (4) earlier CAPTURING, (5) move
  the setup trap budget from deep rings to the dam line.
- **Run order differs from rank.** Levers 2 and 4 need no new code (arms g4crumb and UPGRADE_ORDER=3), so their 5(a)
  runs can go at once while 1 and 3 are built.

## 1. Robust findings (merged, de-duplicated)

| # | finding | strongest evidence | lenses | within map | conf. |
|---|---|---|---|---|---|
| R1 | Map identity matters a little and side not at all. How a map ends is a stable property of the map, so cells should be chosen by loss mode, not by record. | Spread between maps SD ~0.11 (p 0.02); split-half reliability of a map's record 0.36; side p 0.75 (A 98-148, B 103-150); our conversion A 8.2% vs B 7.8%. Reliability of the end mode: r2000 0.85, CAPTURE before r1000 0.70, its captures by r600 0.88. | L1 M1, L3 O10 | - | high |
| R2 | Conversion decides, not grab count. Unopposed captures carry most of the extra captures in losses. | First grabs 8.9 W / 9.1 L (census 8.93 / 9.45). Captures per chain 0.29 vs 0.41: within map -0.17, t -6.1, lower in wins on 48 of 62 maps. unopposedCaps 2.18 vs 1.31: within map -1.0, t -14.6, about 85% of the gap. Its captures by r600 within map -0.54, lower in wins on 40 of 48 maps. | L2 F1/F12, L1 M7, L4 F6 | yes | high |
| R3 | Path distance from its spawns sets which of our flags falls. The nearest falls early in most games; the 2nd and 3rd decide the game. | Captured by r600: -0.125 log-odds per path tile (z -7.6); path absorbs Euclidean distance (z -0.7). Lost by r600, W vs L: 1st flag 72% vs 85% (t -4.8), 2nd 25% vs 51% (t -7.3), 3rd 0% vs 20% (t -8.4). Captures per chain by BFS distance: under 20 tiles 0.50, 36-47 tiles 0.13. Canals: the centre flag fell r255-332 in all games; the far flags decided. | L5 F2/F5, L2 F10 | yes | high |
| R4 | Its relay is a dense, slow convoy. Group size at the grab sets the capture odds; our ducks near the grab barely matter. | Hand-off every 1-2 rounds; flag moves 0.56 tiles/round; 3.2 of its ducks within dist2 2 of each drop vs 0.14 of ours. P(capture) by its ducks within dist2 20 at the grab: 0-2 0.04, 12+ 0.48. A group of the same size converts 1.5-2x more in our losses (12+: 0.54 vs 0.36). | L2 F2/F3, L3 O4 | partly | high |
| R5 | Our ducks are near but kept off. They stay in fight micro with the screen, and our track of the flag goes stale. Chains break only when the carrier dies at local parity. | At t5 after the grab, our ducks that were near: 47% out of the flag's vision with an enemy within dist2 20, 14% stunned, 11% dead (returned chains: 65% still near). Rounds with no sighting for 6+ rounds: 46% in capture chains vs 5% in returned chains. 88% of returns are carrier kills with ours 8.1 vs its 4.0 within dist2 20. Canals loss: 6-9 of ours, all noted "fight", at 6-10 tiles. | L2 F4/F5/F9 | partly | high |
| R6 | Bodies reaching the chain separate wins from losses: respawns that reach it, and its stuns that catch pursuers. | Share of our respawns reaching the chain: 0.37 W / 0.30 L (within map +0.09, t 4.2, higher in wins on 41 of 60 maps). Our pursuers caught by its stuns: 15.3 L / 10.3 W per chain (within map -5.4, t -4.5). | L2 F6/F7 | yes | high |
| R7 | Flags in their spawn zone fall less once path distance is held fixed, probably because respawns keep landing there. | Captured ever: inSpawnZone -0.92 log-odds (z -5.4); holds on both sides. At its grabs, 6.2 of ours stand near zone flags vs 3.4 near relocated flags. Lens 2's raw comparison (57% vs 43%) is confounded. | L5 F3, L2 F11 | yes | medium |
| R8 | The economic gap is structural. It is the same in wins and losses, and setup does not vary within a map. | Bank at r200: Gymhgy 3,900-4,500 (median 3,022), ours 450-600 (median 175), equal in W and L. Setup digs 73 vs 0. Level sum at r200 14.1 vs 5.6. Traps by r200 38.5 vs 2.4, at least 83% of ours within d2 8 of our flags. All setup measures within map: abs(t) 1.2 or less. | L1 M7, L4 F4, L5 F5/F6 | no difference | high |
| R9 | Setup rings protect our flags a little but miss the dam-drop fight. | Each setup trap within d2 8 of a flag: -0.041 log-odds on its capture by r600 (z -4.0). 32 of our 38 setup traps sit at f < 0.3 and fire 8-12% by r250 (41-51% by r400). The 2.3 at f 0.4-0.5 fire 62% by r250. | L4 F4, L5 F6 | - | medium |
| R10 | The dam-drop skirmish (r200-250) is the earliest within-map difference, and it is small. Gymhgy wins it with midline stuns paid from its bank. | Net kills at r250 +0.89 within map (t 2.4; 42 vs 30 maps); win rate 52% vs 40% when the skirmish is level or won. Faded by r300 (+0.58, t 0.9). Gymhgy builds 31 stuns r200-250 at median f 0.55, we build 17 at f 0.25. Downstream correlations are 0.15 or less. | L4 F6/F7, L1 M7 | yes | medium |
| R11 | Centre-crumb maps (at least 9,000 crumbs behind dams, 17 maps) lose early because Gymhgy harvests the field and we do not. | Win 0.31 vs 0.43; loss by early capture 0.45 vs about 0.20 (map permutation p 0.0009). r200-400 crumbs 5,664 vs 11,927; bank at r225 479 vs 10,652. GaltonBoard trace: our ducks leave the strip at r200 (bank 205 -> 130), Gymhgy +10,440, stuns by r300 104 vs 11. In code, C.POST_SETUP_CRUMBS is off. | L1 M2/M4/M5 | between maps | medium |
| R12 | Level-sum losses come from build and heal XP, and the margins are thin. | Level-sum record by score: 0-0 9-24, 1-1 13-14, 2-2 32-15. At 0-0: 422 vs 433 (build -11, heal -19). We dig 0 times in all 57 level-sum replays. Median margin 17; +30 levels flips 20 of 26 losses. Census, 603 losses: 95 LEVEL_SUM at equal captures and 101 MORE_FLAG_CAPTURES one capture short, 33% of losses. | L1 M6, L3 O10 | - | high |
| R13 | Our offense: many grabs, few captures. Carriers die near its base, outnumbered and alone. Its conversion differs between W and L twice as much as ours. | Journeys 18.3 vs 9.2 a game; conversion 7.9% vs 22.0%; re-grab after a carrier death 19.5% vs 44%. 48% of our carrier deaths are within 5 tiles of its flag; 51% with no ally within dist2 8. Within map: ours +6.7 pp in wins (t 3.5), its -14.4 pp (t -6.0). | L3 O1-O6 | yes | high |
| R14 | Carrier speed binds our conversion, though the evidence is confounded by the late-game regime. | In the same games, r1501-1800 vs r1801-2000 (both teams have CAPTURING from r1800): carrier moves 0.445 -> 0.732 a round; our conversion 3.2% -> 13.9%, Gymhgy's 4.2% -> 12.5%. Both bots buy ATK@600, HEAL@1200, CAP@1800 in all 501 surveys. | L3 O5, L4 F5 | no | medium |
| R15 | Where the fight happens is mostly an outcome, not a cause. The split by territory builds after r1000; in full-length games it is how we come back. | Within map, deathsHome -5 (36 vs 40 maps); wins last 292 rounds longer. In 465 games that reach r2000, the split is large (deathsHome 140 vs 242) but only about 15% of it exists by r1000. Comebacks from behind at r1000: enemyDeathsHome +126 (t 2.4). | L4 F1-F3/F9 | yes | high |
| R16 | Gymhgy gets 25-30% more kills per attack and heals more; neither differs between W and L. | Kills per 100 attacks r200-300: 2.3 vs 2.9; W-L 0.13 or less. | L4 F5 | yes | high |
| R17 | Gymhgy's flags sit on the map edge with dug water beside them. Its relocation lengthens our walk, and we raid its three flags about evenly. | Edge 80% vs ours 2%; 2.2 dug tiles within d2 8; its weakest flag 1.9 path tiles farther than ours. Our walks are 4.5 tiles longer; capture rate falls from 18.7% (walk under 20 tiles) to 3.6% (40-49 tiles). | L5 F1, L3 O7 | - | medium |
| R18 | The basics hold: symmetry and timing are not factors. | symWrong 0; symDecidedRound median r2; within map t 0.1. | L5 F7 | yes | high |

### Conflicts between lenses, and corrections to earlier claims

- **C1. Where the game is decided.** research/CRACK-GYMHGY.md (181 games) says "decided by where the fight happens". Lens
  4 (R15) shows the territory split is mostly game length and map mix, and that it builds late. Lenses 1, 2 and 5 put
  the decision in Gymhgy's conversion of our 2nd and 3rd flags (R2, R3). The CRACK doc's headline is superseded.
- **C2. When the gap opens.** Lens 2 says first grabs and the first-capture round are equal (median r344 W vs r348 L)
  and that conversion differs from r400 (chains starting r400-600: 0.15 W / 0.29 L). Lens 4 says its captures before
  r400 already differ (0.63 vs 0.81, t -3.5). Lens 5's order-of-loss table reconciles them: the first flag falls in
  both, and the difference is whether the 2nd and 3rd fall. Lens 4 F10 places its r400 difference in short losses only
  (-0.34 vs -0.07).
- **C3. Value of setup rings.** Lens 5 finds rings protect each flag (-0.041 per trap, z -4.0). Lens 4 finds 85% of the
  setup budget sits where it misses the skirmish. Both can be true. Lens 4's lever L1 has a measurable price under lens
  5's coefficient: about 7 fewer ring traps per flag is about +0.29 log-odds on each flag's capture by r600.
- **C4. Spawn-zone flags against relocation.** Lens 5 (medium) and lens 2 (low) agree that zone flags fall less. But
  relocation was part of g_iter3's promotion (pooled capture delta +0.25, the waffle crack). Lens 5's threshold rule
  (stay unless the move gains at least 5 path tiles) resolves this, but it needs path distance (Duck.nearestEnemy2 is
  Euclidean, which lens 5 shows carries no information once path distance is in the model).
- **C5. Defenders at the grab.** Lens 2 says ours near at the grab barely matter. Lens 5 says zone flags fall less
  because more of ours are near them at the grab. Lens 2's respawn-arrival finding (R6, t 4.2) reconciles them: what
  matters is bodies arriving during the chain, and respawns land in the zone.
- **C6. The opening fight.** Lens 1 says it "barely" decides (kills minus deaths by r300 +1.66, 31 of 53 maps). Lens 4
  calls it the earliest separator (t 2.4 at r250). The magnitudes agree; only the emphasis differs.
- **C7. Lens premises the code contradicts.**
  - Lens 2 L1 assumes the 10-tile alert radius limits recall. In g_iter4, `Duck.carrierTarget` has no radius: once a
    carrier sighting is fresh, every duck that is not fighting chases it (or goes to its destination when the carrier
    is more than 15 tiles away, CHASE_RADIUS2 225), and respawns spawn toward it (`trySpawn`). After the grab, the
    limits are staleness (CARRY_FRESH 5) and the fight branch, not the radius. ALERT_RADIUS2 limits only the alert
    before the grab, and that was refuted twice (g2alert400, and d2alert 12-31).
  - Lens 4 L2 assumes our army holds our half by choice. `fieldTargetFrom` already marches on the nearest known enemy
    flag. The median f of 0.39 is where our army meets Gymhgy's push and fight micro takes over. Its ancestor, forward
    drift (g1drift80), was refuted on the band (11-24, kills -62).
  - Lens 2 L5 (meet the convoy at its destination) partly exists already: carrierTarget sends far ducks to the
    destination for fresh sightings, and DEST_CAMP failed twice against Cyril.
- **C8. Lever 4 against history.** g2up3 (UPGRADE_ORDER 3, g_iter2 base, band) failed delivery: capturedLate 0.33 vs
  0.44, kills -6.6% (-2.9 SE). It was never played against Gymhgy. Lens 3's case comes from the r1800 regime, where both
  teams have CAPTURING.

## 2. Ranked levers

The ranking weighs the ceiling, the probability of delivering, how cleanly a pair can measure the arm, and the
§4 rotation rule. Defense levers against Gymhgy so far: g4z1 failed delivery twice; g4z2 did not deliver at 5(a).
Offense levers: g4relay2 failed delivery; g4pick and g4relay did not deliver at 5(a).

Every 5(a) below uses two fresh seeds, shared by the arm and g_iter4 (e.g. 781001 and 781002), through
`tools/diag-batch.sh <tag> <arm>:Gymhgy.v10official:<map>:<seed>:<side>`, with g_iter4 on the same cells. Wins in a
5(a) are descriptive only (g4relay2's +54% on 8 cells did not hold at 96).

### Lever 1: farm build XP late in a stalemate (new C.LEVEL_FARM, arm g4farm)

- **Mechanism.** Level sum decides ties at r2000 (all 50 robots, jailed included; RULES.md). At 0-0 we trail 422 to
  433, mostly in build (-11) and heal (-19), and never dig. Build XP counts actions (5 / 10 / 15 for levels 1-3), and a
  dig costs 20 crumbs (RULES.md), so a build level costs about 100 crumbs. Most of our ducks finish at a3 b0 h6. Heal
  mastery caps build at 3, so the headroom is about 3 levels a duck (~127 levels a game). Build is never a heal master's
  highest skill, so the jail XP penalty does not touch it.
- **Expected effect.** +30 to +60 level sum at r2000 in games tied late. +30 flips 20 of 26 observed level-sum losses.
  LEVEL_SUM losses are about 10.6% of games, so the ceiling is about +8 points. A realistic estimate is +3-6 points,
  because farming needs idle ducks and crumbs, and some tied games are later decided by a capture. This is the largest
  ceiling of any lever.
- **Clean measurement.** The arm changes nothing before FARM_ROUND. If the farm test puts the round check first,
  paired games are byte-identical up to r1500 (same seed, same code paths; check by diffing the replays at r1499). Every
  later difference in level sum or result is caused by the arm.
- **Signature (new census columns, ReplayDump --capabilities).**
  - `digsLate`: our DIG actions after r1500. Blank unless the game lasted past r1600 with equal captured counts at the
    end of r1600 (the eligible games). g_iter4 has 0 in all 57 level-sum replays.
  - `levelGain1500`: our team level sum (ReplayDump.levelSum) at the final round minus at the end of r1500, blank
    outside eligible games. Companion `levelGapEnd`: ours minus theirs at the final round.
  - Pre-registered gate, with the ratio bar set from the 5(a) base reading so that it means at least +30 levels:
    `fire:digsLate>0>=0.9 rel:levelGain1500>=<base+30 as ratio> nw:enemyCaptured<=1.1 mean:overruns<=0`.
- **5(a) design.** DefaultSmall A, DefaultSmall B, EndAround A, EndAround B, GravitationalWaves A, Fusbol A, Hurricane A.
  Controls: Tunnels A and Mountain A, where we win 0-0 level sums 7-3 and the lever must not lose them. 9 cells x 2
  seeds = 18 pairs (36 games). Read: games identical to r1500; digsLate in every eligible game; level sum at r2000
  against the same-seed g_iter4 (target +30); our fills after r1500 (the self-fill tax that sank b2dig5 and b3dig5);
  enemy captures after r1500 (must not rise).
- **Implementation sketch.**
  - C.java: `LEVEL_FARM=false`, `FARM_ROUND=1500`, `FARM_RESERVE=300` (kept for carrier and combat stuns),
    `FARM_XP=15` (build level 3).
  - Duck.turn: a farm branch after the defender branch (line 76) and before POST_SETUP_CRUMBS (line 79). It runs only
    when no enemy is in view; the fight branch at lines 47-75 has already returned otherwise.
  - Conditions: `G.round >= C.FARM_ROUND` tested first; `rc.getExperience(SkillType.BUILD) < C.FARM_XP`; no fresh alert
    (`alertedFlag() < 0`); `carrierTarget(G.me) == null`; captures level. Our captures = registry slots with EF_STATE 2;
    theirs = bits in `Comms.lostMask()` (FLAG_LOST, which lags by LOST_AFTER 60 rounds).
  - Digging: reuse setupDig's checkerboard loop (lines 330-344: x+y even keeps land diagonally connected; skips spawn
    centres) with the DIG_SITE 2 rule (`behindSpawn`) so digs stay off the lanes. Budget `crumbs >= 20 + FARM_RESERVE`.
    With no diggable neighbour, `Nav.moveTo` a backfield point behind the nearest spawn centre.
  - Nav.fillToward line 96: extend the NO_FILL_OWN skip (`ownDigSignature`) past setup while LEVEL_FARM is on, so our
    ducks do not pay 30 crumbs to refill our own digs.
  - Counter `lf` (farm digs), placed right after `x` in G.endTurn so the 64-character cut keeps it.
  - Optional dose: while farming, spendFloat (line 859) digs instead of building stuns (20 crumbs per XP against 100).
- **Falsifier.** digsLate fires but levelGain1500 rises by less than 15, or enemy captures after r1500 rise. Either
  means the farm is not affordable or distracts the defense.

### Lever 2: harvest the centre crumbs on centre-crumb maps (existing arm g4crumb)

- **Mechanism.** On the 17 maps with at least 9,000 crumbs behind dams (AceOfSpades, Backslash, Battlecode24, BedWars,
  GaltonBoard, HeMustBeFreed, HungerGames, Joker, KingQuacksCastle, MIT, Pancakes, Puzzle, Randy, Snake, StarryNight,
  SteamboatMickey, Waterworld), Gymhgy takes 80-98% of the r200-400 crumbs. It turns them into stuns (GaltonBoard 104
  vs 11 by r300) and early captures. g_iter4 never picks up a crumb after setup. C.POST_SETUP_CRUMBS (Duck.java line
  79) sends ducks with no enemy in view to the nearest visible crumb. C.CRUMB_STEP (Micro.java lines 187 and 225) adds
  +40 to a fight step onto a crumb tile. Extra crumbs are spent through the existing placeCombatTrap and carrierStun.
- **Expected effect.** Our r200-400 harvest on the class from 5,664 toward 11,000+. Early-capture losses there from
  0.45 toward 0.20, win rate from 0.31 toward 0.44: a ceiling of +14 wins in 499 (+3 points). Realistic +1-2 points.
  Lens 1 M7 found the economic lead the same in W and L at r200, so whether crumbs flip games is unproven.
- **Signature.**
  - New column `gathered200to400` = map crumbs our robots collected in r201-400 (gathered400 minus gathered200 as one
    column, because tools/delivery-check.py tests single columns). Companion: survey `crumbs250` (exists).
  - The gate should run on the map class. A random-cell block dilutes the effect: g4crumb against Cyril on random maps
    reached +17% against a +15% bar, INCONCLUSIVE at 96 cells. Tool change: a `MAPS=<file>` option in scrim.sh, whose
    map list is fixed to tools/bc24-maps.txt at line 47, passed through delivery-gate.sh (`DGMAPS`) and tagged with
    DGTAG (rule 6: unit tests).
  - Gate: `DGPOOL=Gymhgy.v10official DGTAG=-gymcrumb DGMAPS=<17 maps> BASE=g_iter4 tools/delivery-gate.sh g4crumb
    'rel:gathered200to400>=1.5 nw:kills>=0.95 mean:overruns<=0'`.
- **5(a) design.** GaltonBoard A, GaltonBoard B, AceOfSpades A, AceOfSpades B, KingQuacksCastle A, HungerGames A,
  StarryNight A. Control: MIT B (we win 5-1 there while taking 40%). 8 cells x 2 seeds = 16 pairs. Read:
  gathered200to400; bank at r225 and r250 (`--metrics`); our stuns built r201-300; on GaltonBoard, whether the ducks at
  the r199 dam line enter the strip at r200-230 (`--map-at 225`). If the strip stays untouched because an enemy is in
  view, build a v2 (below).
- **Implementation sketch.** None for v1: tools/arm-intent.txt already has `g4crumb C.CRUMB_STEP=true` and
  `C.POST_SETUP_CRUMBS=true`. Print `Duck.crumbDetours` in the indicator string (only `cr` = crumbSteps is printed
  now). v2 if 5(a) shows the fight branch pre-empting the harvest: a window from r200 to C.CRUMB_UNTIL (about 260)
  in Duck.turn, ahead of the fight branch. A duck with no enemy within dist2 10 (`Micro.threat(G.me, enemies) == 0`)
  and a crumb on our half within vision (`Micro.ourHalf`) steps to the crumb. A bank floor is not recommended: BUDGET_V1
  (g2crb) delivered the bank against Cyril but only +12% stun output.
- **Falsifier.** gathered200to400 doubles but stuns r201-400 and its captures by r600 on the cells are unchanged.

### Lever 3: convoy response, fighting toward our flag on a track that survives relay drops (arm g4convoy)

- **Mechanism.** Conversion of its chains is the main within-map decider (R2, t -6.1; unopposed captures t -14.6).
  Lens 2 found our ducks near the convoy but stuck fighting the screen, with our track going stale. The code shows why.
  - **Fight branch.** Duck.turn lines 47-75: with an enemy in view, the duck runs `Micro.fight(enemies, allies)`
    without a goal. Micro.fight closes on a carrier only if it is in the duck's own vision (lines 208-209). A duck 6-10
    tiles from our flag fights the screen and ignores the flag.
  - **Track.** `Comms.carried` lasts CARRY_FRESH = 5 rounds. Duck.sense (lines 161-170) refreshes it only when our
    flag is seen carried; seeing our flag dropped off home does nothing, though the relay leaves it on the ground about
    1.7 rounds per hand-off.
  - **Arm: three existing or tiny switches.**
    - C.INTERCEPT (exists): in a fight, a carrier target within INTERCEPT_R2 225 becomes the fight goal (lines 59-64).
    - C.CARRY_PREDICT (exists): a stale sighting predicts the carrier at age/2 tiles toward its nearest spawn. That is
      0.5 tiles a round; the convoy moves 0.56.
    - New C.DROP_TRACK: in Duck.sense, our flag in view, not picked up and off its home writes
      `Comms.reportCarried(i, loc)`.
  - The respawn and destination parts of lens 2 (L3, L5) follow without new code: trySpawn (line 210) and carrierTarget
    (line 531) already consume the track.
- **Expected effect.**
  - Share of chain rounds with one of ours within dist2 20 of the flag: from 0.37 toward the returned-chain level of
    0.89.
  - Its conversion per chain from 0.41 toward the 0.29 seen in our wins. Its captures by r600 -0.3 to -0.5 in losses.
  - Games with no Gymhgy capture by r600 win 56% vs 42% (within map +0.35), so +2-4 points if it delivers.
  - Probability of delivery is lower than for levers 1 and 2. Lens 2 F3 says convoys of 12+ convert 48% whatever is
    near, and the Gymhgy defense area already has two failed deliveries.
- **Signature.** New column `flagContact20`: over post-setup rounds when one of our flags is off its home (carried by an
  enemy, or dropped and not yet returned), the share with at least one of our robots within dist2 20 of the flag at end
  of round. Blank without such rounds. Baseline 0.37 in capture chains, 0.89 in returned chains. Existing fallback:
  `enemyUnseenRounds` (carrier-rounds only, absolute; 80.4 L / 74.4 W). Gate: `DGPOOL=Gymhgy.v10official DGTAG=-gymconv
  BASE=g_iter4 tools/delivery-gate.sh g4convoy 'rel:flagContact20>=1.3 nw:kills>=0.95 mean:overruns<=0'`. Random cells
  are fine because chains occur on every map. Fire evidence comes from counters `ch` (chases/intercepts/camps) and
  `pr` (predictTurns), already in the indicator string.
- **5(a) design.** Canals A, Canals B (traced: far flags decide), KingQuacksCastle A, Joker B, Snake B, DefaultHuge A,
  DefaultHuge B, Valentine A, Foxes B. 9 cells x 2 seeds = 18 pairs. Read: intercepts per game, flagContact20 (from
  `--defense` until the column exists), enemyUnseenRounds, carrier kills on its chains (enemyCarrierKills), its
  captures by r600, our kills and deaths.
- **Implementation sketch.**
  - Duck.sense: put the DROP_TRACK test before the A11(a) `clearCarried` branch (line 167), and let A11(a) clear only
    when the flag is at `Comms.flagHome(i)`. Otherwise CARRY_PREDICT's clear erases the relay track on every hand-off.
  - Dose knob: a new `C.ICPT_THREAT_COST` used by Micro.fight's goal branch (line 222; RUSH_THREAT_COST 150 today, kept
    for rushers). With 150 per threat and -4 per dist2 to the goal, a duck steps toward the flag only when the step adds
    no threat, so it may still stall behind a 12-duck screen. Try 150, then 75.
  - tools/arm-intent.txt: `g4convoy C.INTERCEPT=true`, `C.CARRY_PREDICT=true`, `C.DROP_TRACK=true`.
- **Falsifier.** flagContact20 rises but its captures per chain do not fall. That would mean group size, not presence,
  decides (lens 2 F3), and the line closes for this opponent.

### Lever 4: earlier CAPTURING (C.UPGRADE_ORDER=3, arm g4up3; dose ladder 0 / 3 / 2)

- **Mechanism.** CAPTURING sets carrier move cooldown to 12 instead of 20, and an enemy flag our dying carrier drops
  stays 25 rounds instead of 4 (RULES.md; Comms.expireDrops). Our carriers need about 76 rounds to walk home while its
  response arrives in a median 3 (lens 3 O3). In the same games, our conversion quadrupled when CAPTURING arrived at
  r1800. Both bots buy CAP last, so UPGRADE_ORDER 3 (ATK r600, CAP r1200, HEAL r1800) gives us 600 rounds of speed it
  does not have.
- **Expected effect.** Up to +0.9 captures a game in games past r1300 (lens 3 upper bound). The target is the 101
  MORE_FLAG_CAPTURES losses one capture short and the 95 level-sum losses at equal captures (33% of losses). Price:
  HEAL moves to r1800; g2up3 lost 6.6% of kills on the band, and its capturedLate fell. Expected sign against Gymhgy
  unknown.
- **Clean measurement.** buyUpgrades (Duck.java lines 193-201) is identical until r1200, so paired games are identical
  to r1200.
- **Signature.** New `carrierSpeedLate`: our carrier moves divided by our carrier-rounds after r1200 (split
  carrierMoves/carrierRounds like kCapturedLate; baseline about 0.45, expected about 0.73). Existing: `capturedLate`
  (captures after r1200; local census 0.11 L / 0.58 W), `regrabsLate`. Gate: `rel:capturedLate>=1.3 nw:kills>=0.95
  mean:overruns<=0` on random Gymhgy cells. If kills fail by design, log the cost (CLAUDE rule 15 exception applies only
  to a passed victory read).
- **5(a) design.** Valentine A, Diagonal B, EndAround B, Backslash A, Rainbow B, Soccer A, Mountain A (two traced
  MORE_FLAG_CAPTURES losses). 7 cells x 2 seeds = 14 pairs. If UPGRADE_ORDER 3 moves capturedLate, also run
  UPGRADE_ORDER 2 (CAP r600) on the same cells. Read: carrierSpeedLate, capturedLate, regrabsLate, kills r1200-1800,
  enemy captures after r1200.
- **Implementation sketch.** None. Add the intent line `g4up3 C.UPGRADE_ORDER=3` and build the carrierSpeedLate
  column.
- **Falsifier.** Carrier speed rises but capturedLate does not, or kills r1200-1800 drop more than 2 SE while capturedLate
  is flat (HEAL at r1200 is load-bearing against this opponent too).

### Lever 5: setup budget to the dam line (new C.DAM_FIRST, arm g4dam)

- **Mechanism.** Defenders ring their flags in setup whenever the bank exceeds trap cost + DEF_TRAP_RESERVE 100
  (Duck.defend, line 823), so about 38 traps and most of the 4,300 crumbs gathered in setup go to rings. Duck.damTrap
  (line 841) builds stuns on dam-touching tiles from r185 only above a 700-crumb bank, so it rarely fires; the 2.3 traps
  at f 0.4-0.5 are probably these, and they fire 62% by r250. Gymhgy wins the r200-250 skirmish (R10) with about 31
  midline stuns. Change in setup only:
  - Rings need bank >= cost + RING_SETUP_RESERVE (e.g. 1,500), except the defender of the most exposed flag, which keeps
    today's reserve (`relocOrder[0] == homeFlag()`; folds in lens 5's exposure weighting).
  - damTrap uses DAM_TRAP_RESERVE 100 from r180.
- **Expected effect.** Setup traps fired by r250 from about 4.3 to 12+. Skirmishes level or won from 39% toward 50%;
  those games win 52% vs 40%. If causal, +1-3 points (lens 4). Price (C3): about +0.29 log-odds on each unprotected
  flag's capture by r600.
- **Signature.** New `setupFired250`: our traps built at r <= 200 that triggered by end of r250 (extend the --trapgeo
  accounting to setup traps). Companion new `netKills250`. Guard on the price: `nw:enemyCaptured600<=1.1`. Gate:
  `rel:setupFired250>=2.5 nw:enemyCaptured600<=1.1 mean:overruns<=0` on random Gymhgy cells.
- **5(a) design.** Fusbol A, Fusbol B, Hurricane A, Islands B, Mountain A, Mountain B, Swoop A, DefaultHuge A (49 setup
  traps, opening lost 10-1). 8 cells x 2 seeds = 16 pairs. Read: setupFired250, net kills at r250, its robots at f < 0.3
  at r300, enemyCaptured600, traps200.
- **Implementation sketch.**
  - C.java: `DAM_FIRST=false`, `RING_SETUP_RESERVE=1500`.
  - Duck.defend line 823: in setup with DAM_FIRST, use RING_SETUP_RESERVE unless this defender's flag is the most
    exposed.
  - Duck.damTrap line 843: the reserve becomes `C.DAM_FIRST ? 100 : C.DAM_TRAP_RESERVE`. Duck.setup line 321: start
    at r180 under DAM_FIRST (DAM_TRAP_ROUND is 185 today).
- **Falsifier.** setupFired250 triples but net kills at r250 do not improve, or enemyCaptured600 rises (the rings were
  worth more).

### Considered, not ranked

| lever (lens) | reason |
|---|---|
| Wide recall on a big push (L2 L1) | Same premise as ALERT_RADIUS2 400, which failed twice (g2alert400) and was rejected 12-31 (d2alert). The post-grab radius limit does not exist in code (C7). |
| Forward push after the skirmish (L4 L2) | Forward drift g1drift80 refuted on the band (11-24, kills -62). The premise misreads fieldTargetFrom (C7). |
| Re-grab convoy for our carriers (L3) | ESCORT_CARRIER / ESCORT_BEHIND / REGRAB / ESCORT_TIGHT (g3escrg2) delivered against Cyril but no crack. g4pick and g4relay2 raised re-grabs against Gymhgy, not captures. |
| Setup digs (L1 lever 3, L5) | a3dig5 / a3dig10 / b2dig5 / b3dig5: the digs eat the bank and our own Nav fills them. Lever 1 takes the level-sum value late, where the bank is not needed. |
| Reach-aware relocation with a spawn-zone threshold (L5) | Strong flag-level effect (-0.6 log-odds for +5 tiles), but the game-level effect is unmeasured (the first flag falls in 72% of wins). It needs path distance in setup (bytecode). g4reach (stall fix only) moved flags +1%. |
| Healthy grabber; raid by walk length (L3) | At most a few tenths of a point; grab HP is confounded with fight context. |
| Moat digs beside flags (L5) | Low confidence: our wall and water neighbours show no protection (z 0.9). |

## 3. What remains unmeasured and would change the ranking

1. **Idle ducks and crumbs in late stalemates** (lever 1). In the 57 level-sum replays: the bank at r1500, and the share
   of duck-rounds after r1500 with no enemy in view (`--navstats` stillPostNoEnemy). If our ducks fight most of those
   rounds, or the bank is near zero, lever 1 fires rarely and drops below levers 2 and 3. Also unmeasured: g_iter4's
   own level gain from r1500 to r2000 (the base of levelGain1500), and how much of the gap is jail penalty rather than
   XP earned.
2. **Whether harvested crumbs change the r250-400 fight** (lever 2). Lens 1 M7 found the economic lead the same in W
   and L at r200; only the GaltonBoard and AceOfSpades 5(a) can show stuns and captures moving. What Gymhgy spends its
   10k centre bank on (`--trapgeo`) is also untraced.
3. **Whether committing ducks to a 12+ convoy wins the local fight** (lever 3). Lens 2 F3 says group size decides. If
   contact rises and conversion does not, the defense line against Gymhgy closes.
4. **CAPTURING at r1200 against r1800, and the price of delaying HEAL against Gymhgy** (lever 4). Lens 3's jump is
   confounded with both teams' late CAPTURING and high levels.
5. **Firing time of setup traps.** --trapgeo covers post-setup traps only, and the census does not separate setup from
   post-setup traps. This sets both the benefit and the price of lever 5.
6. **Reachable relocation gain.** The weakest flag is 5.9 tiles short of the best reachable spot, but that is an upper
   bound that ignores spacing and sensing. If a +5 path-tile move is reachable in most games, reach-aware relocation
   overtakes lever 5. The causal source of the spawn-zone effect (respawns, or lanes and spawn choice) is also open.
7. **Army positions between r400 and r1000, and fights after r1000.** About 85% of the full-length territory gap builds
   after r1000 (lens 4). A mid-game lever could appear there.
8. **Our escorts' geometry and actions** (lens 3 logged counts only). If escorts never strike chasers, a cheap escort
   fix could exist despite the history.
9. **One snapshot.** The lenses used census snapshots of 499, 536, 993 and 1,174 games and replay sets of 318-981.
   Recompute the baselines behind each pre-registered bar on one snapshot, or take them from the gate's own BASE run.

## 4. Procedure notes

- **Map-class delivery.** A lever aimed at one class of maps (lever 2, and optionally lever 1 on the stalemate class:
  Alligator, Backslash, BreadPudding, DefaultLarge, DefaultSmall, Diagonal, EndAround, FloodGates, Funnel, Fusbol, Gated,
  GravitationalWaves, Hockey, Hurricane, MazeRunner, Mountain, ORV, QuestionableChess, Racetrack, Rainbow, Tunnels,
  Valentine, YearOfTheDragon, by the gymstudy/maps/classes.py rule: r2000 in at least two thirds of games) needs a
  delivery block drawn from that class. Random sides; never posted to the ladder.
- **Identity-until-round check.** For levers 1 and 4, diff arm and g_iter4 replays at r1499 or r1199 in 5(a). Any
  earlier divergence means the switch changes something before it should (for example, a bytecode-dependent branch such
  as Micro.engageable's REACH_BC test).
- **Run order.** Levers 2 and 4 now (no code: g4crumb exists; g4up3 is one intent line), the census columns next
  (digsLate, levelGain1500, levelGapEnd, gathered200to400, flagContact20, carrierSpeedLate, setupFired250, netKills250),
  then build levers 1 and 3. Every arm passes tools/unit-tests.sh, a 5(a) showing the mechanism firing, and its
  delivery gate before any band test (CLAUDE rules 5, 6, 13).
