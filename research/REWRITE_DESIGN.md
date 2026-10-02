# Decision-layer rewrite: shared flag tracks and bounded responders

Pre-registered on 2026-10-02, before any code was written. The base is **g_iter1**: `src/bot` with every switch off plays as g_iter1 (identity verified with g1copy). Evaluation follows `research/REWRITE_EVAL.md` with its bars unchanged. Milestone results go in `progress/REWRITE.md`, and stage results go in `TRAINING_LOG.md`. The owner asked to "try it out, but keep statistics" (prompt 112). Section 5 says which statistics are kept.

**Where this design comes from.** Four designs were each scored by three judges. Combined scores out of 30: Situation Board 24, Objective-Driven Micro (ODM) 23.5, Task-claimed squads 19.5, Tide 12.

- **Core: Situation Board.** Its staging is kept: a premise test computed from replay truth before any bot code, then a sensor-only build scored against truth, then a bounded set of claimants. Every other duck stays byte-for-byte g_iter1.
- **From ODM:**
  - tracks keyed by flag, which survive drops;
  - the responder auction, double-buffered and cleared under a round stamp;
  - the rendezvous time;
  - the objective branch in `Micro.fight`, which replaces the rush-goal path;
  - `holdAct` for convoy re-grabs;
  - archetype sparring partners as the step-5(a) opponents.
- **From task-claimed squads:**
  - respawners bid before they spawn;
  - field ducks bid only when they are not engaged;
  - the number of responders scales with the escorts seen;
  - the symmetry check (`symOk`);
  - execution diagnostics split by sub-mechanism.
- **From Tide:** the `loneDeaths` and `trickleDeaths` instruments, used as guardrails.
- **Dropped:**
  - Tide's wave doctrine, the GUARD/RAID roles and the sector board. Each is a closed direction or has no premise number.
  - Unconditional fire bars and tight outcome guards on 24-game blocks. Both would misclassify an arm that works.
  - Design-specific acceptance bars.

Facts checked against the engine and repo for this document:
- `MethodCosts.txt` gives readSharedArray 2, writeSharedArray 75, setIndicatorDot 0, canSenseLocation 5, senseMapInfo 5 and getSpawnZoneTeamObject 2.
- The 3.0.6 replay schema stores each team's shared array every round (`Round.teamCommunication`, written by `GameMaker$MatchMaker`), plus indicator dots.
- `tools/delivery-check.py` skips blank values, so a column that is blank when its event is absent gives a conditional fire bar with no tool change.
- `Sym.observe` has no callers.
- BotTest's slot-range list omits `OF_HOME` (slots 20-22).
- In `Micro.java`, the rush-goal kite term is `-th*RUSH_THREAT_COST - d2(goal)*4`.

---

## 1. Premise in numbers, and what would falsify it

### 1.1 The binding term

The source is the B3 band census (232 games with trips in `research/trips-b2fs.csv`). Figures marked (r) were recomputed for this document.

| measure | value | meaning |
|---|---|---|
| losses by full capture | 106 of 151 | captures decide the games we lose |
| their trips on our flags | 4,402; 453 captured (10.3%, 1.95 a game) | |
| captured trips that were unopposed (mean of our ducks within dist2 20 of the carrier below 0.5) | 47% | carriers walk home with nobody near |
| trips of 25+ rounds | 573 (13% of trips), holding 298 (66%) of captures | the long walks are the captures |
| capture rate on 25+ round trips, by mean chasers20 (r) | below 0.5: **74%** (132/178); 0.5-1.5: 50% (102/204); 1.5-3: 37% (36/98); 3+: 30% (28/93); 1+: **38%** (102/268) | presence correlates with half the capture rate |
| long unopposed captured trips (r) | 132 (0.57 a game); 61% first grabs; 62% had one of ours within dist2 20 at the pickup | most were **seen leaving**, then forgotten |
| short captured trips, under 20 rounds (r) | 141, of which 97% (137) are not first grabs | chain final legs, reachable only through a track that continues across drops |
| captured trips starting from a re-grab or relay | 64% | the track must be keyed by flag, not by carrier |
| our side | pickups 8.2 vs 20; escorts20 4.85 vs 6.76; re-grabs 1.9 vs 6.8; 29% of our carrier deaths within 3 rounds of pickup, 49% within 6 | convoy deficit (secondary, conditional stage) |
| fights against the beaters | kills 331 vs 294 | not the binding term; used as a guardrail |

**Why nothing so far reached these trips.** These reasons come from the code and the log.
- `CARRY_FRESH` is 5. `carrierTarget` runs only in `fieldTarget` and `trySpawn`. Our ducks have an enemy in view on all but 4.6 of the 36.1 points of post-setup stillness, so `Micro.fight` decides, and it knows a carrier only while the carrier is visible.
- g1icpt fired constantly (chasers20 2.40 vs 2.31) because alerts are fresh only while someone already sees the carrier.
- It also used the rush-goal term. A step toward a goal d tiles away gains about 8d, which outweighs one threat (150) beyond about 19 tiles. The engage (10000) and advance (5000) branches keep their priority over that term, so claimants stay in local fights.
- g1camp acted only outside fights, at the enemy spawn centre.
- g1esc recruited only within dist2 20, which is the radius escorts20 is measured at.

The closed-directions ledger re-opens the flag-pressure family on "shared carrier tracking with prediction, or a dedicated group". This design is both.

### 1.2 Premise statement

Most of their captures are long walks by carriers we saw leave and then forgot, or never saw leave, plus short chain legs from drops near our flags. The premise has four parts:
1. A persistent track per flag, predicted toward the enemy spawn zone, can say where the carrier is and will be.
2. Enough of our ducks are not fighting, or are about to respawn at the trip's start, to reach the predicted path in time.
3. A bounded group of them (3 to 9) can be put there without costing the field fight.
4. Their presence converts: contacted long trips are captured far less often (38% observed with 1+ chaser, against 74% unopposed).

### 1.3 Size of the prize (observational, an upper-bound guide)

- Their trips of 20+ rounds with mean chasers20 below 1.5 number 416, with 241 captures (58%). That is 1.80 trips and 1.04 captures a game.
- Suppose responders reach a share ρ of them and contacted trips convert at the 1.5-3 bucket rate (32.5%). Then their captures fall by about 1.80·ρ·(0.58 − 0.325) = **0.46ρ a game**.
- For ρ = 0.4-0.7 that is −0.18 to −0.32 a game.
- On all cells the capture-difference SE is about 0.10 (the B2 and B3 references), so this would be +1.8 to +3.2 SE.
- On the upper tier the SE is 0.11 per 128 games, and those games hold most of their captures.
- Games that end without a full capture go to the level-sum tiebreak, which B3 won 20 of 24.

### 1.4 Falsifiers (each tied to a stage and a closing rule in §4)

| id | stage | falsified if | reading |
|---|---|---|---|
| F1 | S0a | fewer than 25% of their long unopposed captured trips have 3 or more *free* responders (not engaged, or jailed) able to reach the predicted path in time | nobody is free to act; close CUT with no bot code |
| F2 | S0a | on their 25+ round trips, the capture rate with chasers20 ≥ 1 is more than 0.6 times the rate with chasers20 < 0.5 (B3: 0.51) | presence does not convert, even as a correlation |
| F3 | S0a/S0b | prediction or destination fails offline after one revision, or in-bot trkHit20 stays below 0.5 after 2 calibrations | the information cannot be had from our vision and comms |
| F4 | S1 | execution diagnostics pass (staffing, arrival), but contacted long trips are still captured at 35% or more and the delivery block misses its signature | arriving does not stop the carrier |
| F5 | M1 | delivered CUT moves all-cell `enemyCaptured` by less than 1 SE (delta above −1 SE) | unseen carriers are not the binding term |

---

## 2. Architecture

### 2.1 What is kept, what changes

**Kept byte-for-byte:**
- Nav;
- strike-first and strike/heal-after-move;
- `bestTarget`, `tryHeal`, `placeCombatTrap`, `setup()`, `defend()` and the dam traps;
- `buyUpgrades` (ATTACK first);
- the existing slots 0-22 and every g_iter1 code path.

**What changes, behind new switches:**
1. **What is seen.** A per-flag track lives in new slots, written by every duck and read by every duck.
2. **Who acts.** A small capped set of responders is chosen by intercept time across the whole map. It includes jailed ducks, which bid before they respawn.
3. **How they act.** In `Micro.fight`, an objective branch applies only to committed responders.

Every duck that holds no objective plays g_iter1 exactly.

### 2.2 Roles and per-robot state

| role | who | behaviour |
|---|---|---|
| defender / home witness | idx 0-2 | Unchanged. Never bids. Its view of the home tile drives the HOME re-stamp (every 8 rounds) and MISSING detection. |
| round-start runner | the first of our robots to run each round (normally idx 0, jailed or not) | Clears this round's auction parity, expires tracks, writes `RT_STAMP` last |
| field duck | idx ≥ 3 with `Obj.kind == NONE` | g_iter1 exactly; writes sightings to the track |
| CUT responder | committed to task o ∈ {0,1,2} (our flag o) | Goes to the frozen intercept point Q on the carrier's predicted path, or to the drop tile inside the return window |
| ESC responder (S3) | committed to task o ∈ {3,4,5} (enemy registry slot o−3) | Rings our carrier at 2 tiles and never stands on its homeward tiles; re-grabs in the 3-round window if it is safe |
| jailed bidder | any unspawned duck with idx ≥ 3 | Bids on CUT with ETA = jail time left + path from the spawn zone nearest Q; a committed one spawns at the tile nearest Q |

**Per-robot statics:**
- `Track`: a cache of slots 23-48, the private symmetry mask, the decoded per-flag tracks, and `exc`.
- `Obj`:
  - assignment: `kind` (NONE/CUT/ESC), `o`, `goal` (frozen Q), `band`, `w`, `f0..f2` (ESC forbidden tiles);
  - freeze and release state: `frozenAt` (track round at freeze), `infeasible`, `cool[6]`;
  - respawn: `deadSince` (first unspawned round after r200, for jail time left), `spawnWant`;
  - counters: R (objective-branch turns), C (CUT turns), E (ESC turns), M (MISSING starts), P (escort post-pickups).

### 2.3 Shared-array schema (slots 23-48 used; 49-63 spare)

Conventions:
- Locations use `Comms.enc` = x·64+y+1 (12 bits; at most 3,836 on 60×60).
- Rounds use 11 bits (the game ends at r2000, below 2048).
- Each slot has one purpose.
- Constants go in `Comms` next to the existing ones, and the javadoc schema is extended.

| slot | name | bits | writer | clearing |
|---|---|---|---|---|
| 23 | `RT_STAMP` | [10..0] round of the last round start; [15..11] spare | round-start runner, written **last** (if it throws, the next robot redoes the round start) | every round |
| 24 | `PSYM` | [2..0] private symmetry candidates (ROT 1, FX 2, FY 4; 0 = unwritten, read as slot 16's mask); [15..3] spare | any duck that prunes | never empty; always ⊆ slot 16 |
| 25-27 | `TRK_A[i]` (our flag i) | [11..0] location of the last positive information (carrier or drop tile; home for HOME/MISSING); [14..12] state: 0 HOME, 1 CARRIED, 2 DROPPED, 3 MISSING, 4 LOST, 5 GONE; [15] destination confirmed by heading | any observer, only on change | state machine (§2.4) |
| 28-30 | `TRK_B[i]` | [10..0] round of that information (HOME: last home confirmation; MISSING: kept at the last confirmation, the pessimistic departure; DROPPED: first round seen dropped); [12..11] destination index 0-2 (3 unknown); [14..13] speed class 0 = 1/2, 1 = 5/6 (they have CAPTURING), 2 = 1/1 (observed relay speed); [15] inferred (no direct sighting since the track began) | same | with `TRK_A` |
| 31-33 | `TRK_C[i]` | [2..0] enemies within dist2 8 of the carrier at the last sighting (cap 7); [6..3] enemies within dist2 20 (cap 15); [8..7] misses 0-3; [9] miss parity (round & 1 of the last counted miss); [15..10] spare | observer | reset on a positive sighting |
| 34-36 | `OWN_C[s]` (S3; our carrier of enemy registry slot s) | [10..0] round our carrier last reported; [12..11] index of our spawn centre it heads to; [15..13] enemies in its view (cap 7). Location comes from the existing `EF_LOC[s]`, which `sense()` refreshes from the carried flag | our carrier, every turn | stale when round − [10..0] > 1 |
| 37-48 | `AUC[o][p]` = 37 + 2o + p; o 0-2 CUT(our flag o), 3-5 ESC(registry o−3); p = round & 1 | [3..0] committed; [7..4] candidates with t ≤ 6; [11..8] 6 < t ≤ 14; [15..12] 14 < t ≤ 30 (each saturating at 15) | each committed or bidding duck: one read-modify-write | the round-start runner zeroes the non-zero `AUC[*][round&1]` before anyone bids; readers use parity (round−1)&1, which is complete. An 11-bit stamp means no aliasing, and a dead member drops out in one round |
| 49-63 | spare | 15 slots | | |

### 2.4 Track semantics (`Track`, the sensor)

The track runs after setup (r > 200) and hooks into `Duck.sense()`'s existing flag loop for our flags. Writes happen only when a value changes.

**State changes:**
- **CARRIED** (flag i seen with `isPickedUp()`):
  - Writes the location, the round, `esc8` and `en20`, and clears misses.
  - For a sighting at least 4 rounds after the previous one, it sets the speed class: 2 if the observed Chebyshev speed is 0.9 or more, else 1 if `rc.getGlobalUpgrades(them)` contains CAPTURING (cached every 50 rounds), else 0.
  - Destination: the enemy centre nearest the sighting, rejecting a centre the carrier has moved away from since the previous sighting (sets bit 15).
- **DROPPED** (flag i seen on the ground away from home): records the first round seen dropped.
  - The return window is 4 rounds, or 25 if *they* have CAPTURING (RULES.md).
  - After the window, a home confirmation means HOME. If the home tile is sensed empty, the flag was re-grabbed unseen: state CARRIED, inferred, L = drop tile, r0 = window end. If nobody senses home, the presumed re-grab is predicted the same way with no write.
  - A duck that senses the drop tile inside the window and sees no flag there (re-grabbed) writes CARRIED, inferred, r0 = round − 1.
- **HOME** (flag i seen at its home, `Comms.flagHome(i)`): sets HOME. In HOME, a duck that sees the flag re-stamps the round when it is 8 or more rounds old (normally the defender).
- **MISSING**: a duck that can sense home i while the state is HOME and does not see flag i writes MISSING, inferred, with the location at home and the round kept as is. Counter M. MISSING never fires from GONE, so a captured flag cannot raise a phantom track.
- **Negative sightings**: if a duck has the predicted point within dist2 10 and does not see flag i, misses increase by 1, at most once per round (parity bit).
  - At 1 miss, the destination switches to the other enemy centre most consistent with L.
  - At 3 misses, the state becomes LOST.
- **GONE**: predicted arrival plus 10 rounds has passed. The round-start runner sets it.
- LOST and GONE return to CARRIED, DROPPED or HOME only on a direct sighting.

**Prediction.** Greedy 8-way steps from L toward D stop at the zone edge:
- `step(L, D, n)` = (Lx + sgn(Dx−Lx)·min(|Dx−Lx|, n), Ly + sgn(Dy−Ly)·min(|Dy−Ly|, n))
- `movesDone(age, class)` = age/2, age·5/6 or age; total N = cheb(L, D) − 1
- P(now) = step(L, D, min(N, movesDone(now − r0)))

Overestimating speed (bug-nav detours slow the real carrier) is the safe side, because responders arrive early and wait on the path. Uncertainty is not stored: S0b measures error against age.

**Private symmetry.**
- `PSYM` starts from slot 16 (read-only) and is pruned in two ways:
  - wall checks, `Sym.observe`-style on a few sensed tiles per turn;
  - spawn-zone checks: for each surviving s and each of our centres c, if image(c, s) is sensable, `senseMapInfo(...).getSpawnZoneTeamObject() == them` confirms s, and anything else prunes it.
- It never writes `Sym.cands` or slot 16, so g_iter1's `Sym` (used by the field and by `nearTheirSpawn`) is untouched.
- Since PSYM ⊆ slot 16, the two can disagree only where PSYM has pruned what `Sym.best()` still prefers. `symOk` (g_iter1's `Sym`) and `psymOk` measure both.

### 2.5 Responders: auction, size, goals (`Obj`)

`Obj.plan()` runs once per turn for every spawned duck with idx ≥ 3 that is not carrying, after setup and after the second `pickupFlags()`. `Obj.jailed()` runs for unspawned ducks.

1. **Live tasks.**
   - CUT o: `TRK` state CARRIED or MISSING, or DROPPED (inside the window, or presumed re-grab after it).
   - ESC o (S3): `EF_STATE[s] == 1` and `OWN_C[s]` fresh. Also, for 3 rounds after the last report, if `EF_LOC[s]` shows the dropped flag; this re-grab window is for already-committed escorts only.
2. **Size K.**
   - CUT: K = clamp(3 + esc8, `CUT_K_MIN`, `CUT_K_MAX`). esc8 is the escort count at the last sighting; when it is unknown (MISSING or inferred), K = `CUT_K_MIN` + 1.
   - ESC: K = clamp(2 + enemies in the carrier's view, 2, `ESC_K_MAX`).
   - Global caps: Σ committed over CUT tasks (from the previous round's slots) ≤ `CUT_TOTAL`; the same for ESC.
3. **Eligibility to bid** (from the squads design):
   - spawned: HP ≥ `OBJ_MIN_HP` (500), **no enemy within dist2 10** (not engaged), round ≥ `cool[o]`;
   - jailed: always eligible for CUT.
   - Each duck bids on one task only, the one minimising t + (ESC ? 4 : 0), so CUT has priority.
4. **Own time t** (CUT, carried):
   - For n from movesDone(now − r0) to N in steps of max(1, N/12):
     - Q_n = step(L, D, n);
     - carrier time τc = ⌈n/v⌉ − (now − r0);
     - my time τd = cheb(me, Q_n)·6/5 + 1. When jailed, me is the spawn centre nearest Q_n, and the jail time left (25 − (round − deadSince)) is added.
   - The first n with τd ≤ τc gives t = τd. None means infeasible: no bid.
   - CUT, dropped inside the window: t = cheb(me, L), and the duck is a candidate only if t ≤ the window left.
   - Caps: t ≤ 30 for CUT, t ≤ 14 for ESC.
5. **Auction.** H = `AUC[o][(round−1)&1]`; cap = K − committed(H). Buckets are b0 (t ≤ 6), b1 (t ≤ 14) and b2 (t ≤ 30). `before` is the number of candidates in better buckets, and `inB` is the number in my bucket.
   - **First-round rule:** if H has no candidates, or inB = 0 (I was not counted last round), I only bid this turn. This removes the round-1 stampede and the churn on 1-3 round trips (failed trips last a median of 3-4 rounds).
   - Otherwise I commit iff cap > before and `hash·inB < 16·(cap − before)`, where hash = (idx·7 + o·5 + (round>>4)) & 15. The hash is deterministic and never the RNG; it thins the boundary bucket.
   - A duck that does not commit adds itself to bucket b of `AUC[o][round&1]`. Committed ducks add to the committed count every turn (the heartbeat).
6. **Rendezvous** (from ODM). T_rv is the bound (6, 14 or 30) of the first bucket where committed + cumulative candidates ≥ K, and 30 if none.
   - **Goal Q** = the first Q_n with τd ≤ τc and τc ≥ T_rv, kept outside dist2 36 of D (`OBJ_KEEPOUT2`) unless the duck is urgent. If none qualifies, the first feasible Q_n.
   - Responders committed in the same window therefore head for nearly the same point ahead of the carrier, instead of trickling in one at a time.
7. **Freeze** (from Situation Board). Q is kept until one of these happens:
   - the track's round or state changes;
   - Q becomes infeasible;
   - the carrier is predicted past Q.

   `Nav.moveTo` resets bug-nav state on any change of target, so a goal recomputed every turn would stall on mazes.
8. **Urgency.** w = `OBJ_W_URGENT` (600) when the predicted time left to D is 10 rounds or less, else `OBJ_W` (300).
9. **Release** (sets `cool[o]` = round + 10):
   - the task dies;
   - HP < `RETREAT_HP`;
   - jailed (a jailed duck may then bid again as a respawner);
   - infeasible for 3 recomputations;
   - within dist2 36 of D with no carrier in view and not urgent;
   - ESC: farther than dist2 100 from the carrier for 5 rounds.
10. **ESC goal (S3).**
    - C = `EF_LOC[s]`, H = our centre from `OWN_C[s]`; goal C with band 2, re-frozen when the carrier is 2 or more tiles from the frozen goal.
    - Forbidden tiles: f0 = C + dir(C→H) and its rotateLeft and rotateRight. Escorts trail and flank and never block (the g1esc lesson).
    - In the re-grab window: goal = the dropped flag, band 0.
    - A re-grab is allowed only if allies within dist2 20, counting me, are at least enemies within dist2 20 + 1 (the b2rg lesson).
11. `Obj.dist(l)` = max(0, cheb(l, goal) − band); `Obj.blocks(l)` = l ∈ {f0, f1, f2}.

### 2.6 Per-turn control flow (`Duck.turn`; additions marked +)

```text
turn():
+ if C.TRACK: Track.roundStart()            // every robot, jailed included; first of ours each round does the clearing
  buyUpgrades()
  if !spawned:
+   if C.TRACK: Obj.jailed()                // release objective; deadSince; CUT bid if CUT_K_MAX>0; sets spawnWant
    trySpawn()                              // + want = Obj.spawnWant when set, else g_iter1 logic
    if !spawned: return
  G.me...; Sym.fromBroadcasts (r<=3); Comms.syncSym()
  sense()                                   // + Track.sight(f) inside the flag loop; then Track.homes(), Track.negatives(), Track.psym()
  if hasFlag: + Obj.reportOwnCarrier() (ESC_K_MAX>0); carryFlag(); return
  if setup: setup(); return
  pickupFlags(); if hasFlag: carryFlag(); return
+ if CUT_K_MAX>0 || ESC_K_MAX>0: Obj.plan() // bytecode guard: < 7000 left -> keep last goal
  rusher / Z1HOLD paths (off, unchanged)
  if enemies: placeCombatTrap(); Micro.fight(enemies, allies); return     // objective folded in for committed ducks
+ if Obj.kind != NONE: if Obj.dist(me)>0 Nav.moveTo(Obj.goal); Micro.tryHeal(allies); return
  ... g_iter1 unchanged (defend, crumbs off, fieldTarget, spendFloat)
endTurn: under C.TRACK indicator = "bc%dk o%d x%d T%c%d e%d R%d C%d E%d M%d P%d " + note   // counters first (64-char cut)
         responders draw Q as an indicator dot (cost 0): rgb (220+o, min(t,255), idx)
```

### 2.7 How Micro.fight receives the team objective

The signature does not change and no call site is added. `Obj.plan()` has already set `Obj.kind/goal/band/w/f*` for this turn, and they are cleared at the start of every plan and on jail, so nothing leaks across turns. The objective is one tile-scoring branch:
- **under** the visible-carrier branch (20000) and the loose-flag branch (19000);
- **in place of** engage, advance and kite;
- **for committed ducks only.**

```java
// Micro.fight(enemies, allies, goal): additions marked OBJ; every other line is g_iter1
boolean obj = goal == null && Obj.kind != Obj.NONE;                        // OBJ (plan never commits a hurt duck)
boolean holdAct = obj && Obj.kind == Obj.ESC && Obj.looseNear(me);         // OBJ S3: dropped enemy flag within dist2 8
if (actReady && !holdAct && tryAttack(enemies)) actReady = false;          // strike first: unchanged when !holdAct
if (!rc.isMovementReady()) { if (actReady && !holdAct) tryHeal(allies); return true; }
int od0 = obj ? Obj.dist(me) : 0;                                           // OBJ
... per tile, as today: th, inRange, minD, adjAllies ...
if (carrier != null && !hurt)  score = 20000 - l.distanceSquaredTo(carrier.location) * 10 - th;   // unchanged
else if (loose != null)        score = 19000 - ...;                                               // unchanged (REGRAB off)
else if (obj)                  score = (od0 - Obj.dist(l)) * Obj.w - th * C.OBJ_T                  // OBJ branch
                                      + (actReady && inRange > 0 ? C.OBJ_HIT : 0) + adjAllies * 10
                                      - (Obj.blocks(l) ? C.OBJ_BLOCK : 0);
else if (actReady && !hurt && inRange > 0 && ...) engage ... // g_iter1 branches unchanged
...
if (obj) Obj.respTurns++;
if (holdAct && rc.canPickupFlag(Obj.goal) && Obj.regrabSafe()) { Duck.pickupFlags(); if (rc.hasFlag()) { Obj.postPickups++; return true; } }
if (actReady) { if (!tryAttack(enemies)) tryHeal(allies); }                 // unchanged
```

**Weights.** These are fixed and pre-registered; the dose is K, not the weights.

| constant | value | reason |
|---|---|---|
| `OBJ_W` | 300 | per Chebyshev tile of progress (±1 per step), so the pull does not grow with distance, unlike the rush term d2×4 |
| `OBJ_T` | 250 | per threatening enemy. One threatened step forward is taken (+50); two are not (−200) |
| `OBJ_W_URGENT` | 600 | near their spawn (10 rounds or less to D), take two threats but not three |
| `OBJ_HIT` | 120 | a reaching tile is a bonus, never a reason to stop travelling (120 − 250 < 0) |
| `OBJ_BLOCK` | 2000 | escorts never stand on the carrier's next tiles |

- At its goal, a responder holds against one threat and gives way to two.
- When the carrier comes into view, the unchanged carrier branch takes over, and the sighting refreshes the track.
- `bestTarget`, carrier-first targeting and strike-first all stay unchanged. The one exception is ESC `holdAct`, which keeps the action for a pickup that the post-move `canPickupFlag` needs. Without it, an escort fighting next to a dropped flag could never re-grab it.

### 2.8 Bytecode budget

The limit is 25,000; the measured peak is about 15.1k and the near-miss line 22.5k. Costs use the engine's MethodCosts.

| piece | who / when | typical | worst |
|---|---|---|---|
| `roundStart`: read the stamp; zero up to 6 non-zero parity slots; expire 3 tracks; write the stamp last | first of ours each round | 250 | 900 |
| `Track` load: 26 reads (23-48) plus decode | every robot after r200 | 200 | 250 |
| sightings in `sense` | spawned, our flag in view | 50 | 300 (2 writes) |
| home checks (3 × canSenseLocation), confirmation write at most every 8 rounds, MISSING | spawned | 60 | 210 |
| negative sightings | spawned, per live track | 60 | 135 |
| PSYM pruning (at most 4 tiles plus spawn-zone checks) | spawned while PSYM has more than one candidate | 0 | 250 |
| `plan`, no live task | eligible duck | 40 | 60 |
| `plan`, bidding on a live CUT (predict, scan of at most 15 steps, bucket read-modify-write) | eligible or jailed duck | 650 | 750 × 3 |
| `plan`, committed (freeze check, heartbeat; re-freeze scan) | responder | 200 | 800 |
| Micro objective branch (9 tiles) | responder in a fight | 300 | 350 |
| carrier self-report (S3) | our carrier | 90 | 90 |
| dots, indicator | | 0 | 0 |
| **added: no live task / one live task / worst** | | **~600 / ~1.3k** | **~3.9k, so a peak of about 19k or less** |

- Guard: `plan()` keeps the last goal when `Clock.getBytecodesLeft() < 7000`. Tracker writes run before it.
- S0b must show 0 overruns and a maximum of 19k or less on every identity cell and in the delivery block.

### 2.9 Identity and safety rules (required for every S0b check)

1. Every hook sits behind `static final` switches, so with `C.TRACK = false` none of the new code runs.
2. `Track` and `Obj`:
   - never call `G.rand` or `G.nearest` (they use the tie-free `Obj.nearestDet`);
   - never write slots 0-22, `Sym.cands`, Nav state or Micro state;
   - with `CUT_K_MAX = ESC_K_MAX = 0` they never commit, and `spawnWant` stays null.
3. Every entry point catches its own exceptions into `Track.exc` (indicator `e`). `RobotPlayer` catches only at the top level, so a stray exception would otherwise drop the rest of g_iter1's turn.
4. `BotTest` additions:
   - `G.rngState` is unchanged across every `Track` and `Obj` call;
   - slot ranges include `OF_HOME` 20-22 and 23-48;
   - pack/unpack round trips for every slot;
   - `predict` is monotone and stops at the zone edge;
   - intercept: a duck ahead on the path gets t ≤ τc, and a duck behind gets the catch-up point;
   - auction thinning over synthetic histograms gives K ± 1;
   - the first-round rule commits nobody;
   - parity clearing never wipes the previous round.

### 2.10 Code changes by file

| file | change | stage |
|---|---|---|
| new `src/bot/Track.java` (~220 lines) | slots, `roundStart`, `sight`, `homes`, `negatives`, `psym`, `predict`, `expire`, `exc` | S0b |
| new `src/bot/Obj.java` (~260 lines) | `plan`, `jailed`, auction, `cut(i)`, `esc(s)`, freeze/release, `dist`, `blocks`, `looseNear`, `regrabSafe`, `reportOwnCarrier`, `nearestDet`, counters, dots | S1/S3 |
| `Comms.java` | constants for 23-48; schema javadoc | S0b |
| `C.java` | `TRACK`, `CUT_K_MIN/MAX`, `CUT_TOTAL`, `ESC_K_MAX`, `OBJ_*` (§2.7), `OBJ_MIN_HP` 500, `OBJ_TMAX_CUT` 30, `OBJ_TMAX_ESC` 14, `OBJ_URGENT` 10, `OBJ_COOL` 10, `OBJ_KEEPOUT2` 36, `OBJ_FREE_R2` 10, `HOME_CONFIRM` 8, `TRK_MISS_R2` 10, `TRK_EXPIRE` 10, `OBJ_BC_GUARD` 7000; each with its reason; all off | S0b-S3 |
| `Duck.java` | hooks in `turn`, `sense`, `trySpawn` (`spawnWant`), `carryFlag` (self-report) | S0b-S3 |
| `Micro.java` | the `obj`/`holdAct` lines of §2.7 | S1/S3 |
| `G.java` | indicator variant under `C.TRACK` | S0b |
| `test/bot/BotTest.java` | §2.9 | S0b |
| `tools/replaydump/ReplayDump.java` | §2.11 | S0a |
| `tools/test_tools.py` | synthetic-replay tests for every new column and mode | S0a |

### 2.11 Instruments (all built in S0a; scored from replay truth)

- **Read the stored shared array.** `Round.teamCommunication` gives our 64 slots every round.
  - A fixture test confirms the per-round recording: slot 0 = 50 after round 1. If the table turns out to be written only on change, the last value is carried forward.
  - Track and auction beliefs are scored directly against truth, with no stdout and no dots needed.
  - Dots carry only each responder's Q.
- **`--track`** (offline tracker on g_iter1 replays): for every post-setup trip on our flags, the §2.4 rules are replayed on what our robots could see, under the true symmetry and under `Sym.best()` (slot 16). Per-trip output:
  - `unseenRounds`, `predErr` (median Chebyshev error over unseen rounds), `destHit`;
  - `tKnow`: the first trip round where one of ours has the carrier, or the home tile with the flag absent, within dist2 20;
  - reach counts at `tKnow`:
    - `reachAll`: our robots with t ≤ 30 by the §2.5 rule;
    - `reachFree`: those not within dist2 10 of an enemy, plus jailed robots;
    - `reachInFight`: the share of reachers that were in a fight;
  - `escorts8`.

  On our trips it outputs `ownEscorts20` and `convoyReachFree` (our robots within Chebyshev 10 of our carrier, outside dist2 20, with no enemy within dist2 10, at pickup).
- **`--defense`** per-trip columns gain `hunters20` (committed responders within dist2 20 of the carrier, from the indicator tag) and `tKnow`.
- **`--capabilities`** new columns. A column is **blank when its event is absent**; `delivery-check.py` skips blanks, so fire bars are conditional.
  - Outcome and premise columns, also computable on g_iter1:
    - `enemyUnseenRounds`, `unopposedCaps`;
    - `longTrips25`, `longCaps25`, `longCapRate` (blank without a 25+ round trip);
    - `loneDeaths` (at most 1 of ours within dist2 20 of the death), `trickleDeaths` (death within 30 rounds of spawn with at most 2 of ours near);
    - `symOk` (whether `Sym.best()` at r250 gives the true enemy centres).
  - Tracker columns (blank without slots 23-48): `psymOk`, `trkLat`, `trkHit20`, `trkFalse`, `trkDest`, `trkExc`.
  - Auction and responder columns:
    - `cutFire` (blank if they had no trip of 10+ rounds);
    - `cutStaff`, `cutCommitErr`;
    - `cutReach` (blank if no CUT episode on a 25+ round trip);
    - `cutSpread`;
    - `respDeaths`, `respLoneDeaths`, `respKills`;
    - `escFire` (blank if we had no trip of 3+ rounds), `escTurns`, `escRegrabs`.
  - Bytecode columns: `maxBcK`, `overruns`.
- Every rewrite build is added to `tools/keep-replays.txt`, so new columns can be back-filled.

---

## 3. Staged plan

Process rules for every stage:
- Each stage is a C.java switch, default off. `tools/unit-tests.sh` runs after every change.
- Step-5(a) games use our own builds only (`tools/diag-batch.sh`) and go through the VM queue (`tools/vm-enqueue.sh`).
- Step 5(b) is `BASE=g_iter1 tools/delivery-gate.sh <arm> "<checks>"`: 24 band cells, SEED 909090, paired `rel:` checks against the cached g_iter1 base on the same cells. Columns the base lacks are re-censused automatically.
- No band test runs before a PASS (CLAUDE rule 13).
- Each stage's bars are copied into TRAINING_LOG before its run.

**Step-5(a) cell set.** Per REWRITE_EVAL's curriculum, the opponents are archetypes, not mirrors:
- opponents: `src/g1escrg` (convoy with re-grab chains, 13-27 pickups a game) and `src/arch_rush10` (fast raid);
- cells: DefaultLarge s4/s5, DefaultMedium s4/s5, Tunnels s4/s5 and Battlecode24 s4;
- each arm game is paired with g_iter1 against the same opponent on the same cell;
- plus 4 cells against the g_iter1 mirror as a regression look.

**Outcome guards in delivery are loose (about 1.5 SE at n = 24)**: `rel:kills>=0.8`, `rel:enemyCaptured<=1.25`. Outcomes and the premise are judged at the band test.

### S0a: premise from truth (no bot code)

- **Scope:** §2.11 instruments and tests. Re-census the kept g_iter1 band replays (`20261001-011402`, `20261001-021054`; about 234 games) and back-fill the cached dg909090 base.
- **Pre-registered premise bars:**

| id | subset (pinned) | measure | bar |
|---|---|---|---|
| P1 | their trips ≥ 25 rounds, outcome CAPTURE; unseen carried rounds | median `predErr` (true symmetry) | ≤ 4 tiles |
| P2 | their trips ≥ 25 rounds, mean chasers20 < 0.5, outcome CAPTURE ("U") | share with `reachFree` ≥ 3 at `tKnow` (no `tKnow` = 0) | ≥ 0.5 build both doses; 0.25-0.5 build dose A only; < 0.25 close CUT |
| P3 | their trips ≥ 25 rounds, CAPTURE | `destHit` | ≥ 0.8 |
| P4 | our post-setup trips | (i) median `convoyReachFree` at pickup ≥ 2; and (ii) on our trips ≥ 6 rounds, capture rate with `ownEscorts20` ≥ 3 at least 1.5 times the rate with < 1.5 | both hold, or S3 is closed |
| P6 | their trips ≥ 25 rounds, all outcomes | capture rate(chasers20 ≥ 1) / rate(chasers20 < 0.5) | ≤ 0.6 (B3: 0.51) |
| D | U | descriptive: share with one of ours within dist2 20 at the pickup, and of those ducks, the share jailed within 10 rounds ("forgot" vs "died"); `reachInFight`; `symOk` | reported |

- If `symOk` < 0.9, P1 and P3 are also reported under `Sym.best()`, and `psymOk` ≥ 0.95 becomes an S0b bar (it is in the S0b checks regardless).
- **Signature and delivery:** the tests pass, the columns are back-filled on the base, and the premise numbers are logged.
- **Routing:**
  - P2 or P6 fails: CUT is closed with no bot code.
  - P1 or P3 fails: one tracker revision (velocity from two sightings, heading-led destination), then re-measure.
  - P4 fails: S3 is closed.
- **Risk:** none to the bot. The likely bad outcome is a premise failure, which is a cheap, early answer.

### S0b: sensor only (`g1trk`: `C.TRACK = true`, `CUT_K_MAX = ESC_K_MAX = 0`)

- **Scope:** `Track.java`, the `Duck`, `G` and `Comms` hooks, and the §2.9 tests. No consumer exists.
- **5(a) signature:**
  1. **Identity.** g1trk against the g_iter1 mirror on DefaultMedium, DefaultLarge, Tunnels and Battlecode24 s4: every census and survey value equals the g_iter1-vs-g_iter1 games, with 0 overruns, max ≤ 19k and `trkExc` = 0.
  2. **Firing.** On the 7 g1escrg cells, the track states change on every cell with an enemy pickup, MISSING fires where a pickup went unseen, and the slot-24 symmetry decodes correctly.
- **5(b) delivery** (absolute bars, belief against truth):
  - `median:trkLat<=6 mean:trkHit20>=0.5 mean:trkFalse<=0.05 mean:trkDest>=0.8 mean:psymOk>=0.95 mean:trkExc<=0 mean:overruns<=0`
  - The block's `rel:` deltas of won, kills and enemyCaptured against the g_iter1 base are recorded as **the lineage's noise floor** (an inert change, TRAINING_ALGORITHM §5). They are not a bar, because external bots need not be deterministic.
- **Execution vs premise:** any failure here is a sensor execution failure (prediction, destination or clearing), separable from any consumer. `trkHit20` is reported per map, because maze maps are the expected weak point.
- **Risk:** low. The main risks are straight-line misses on mazes and a wrong destination when only one zone is plausible.

### S1: CUT responders (`g1cut6`: K 3-6, `CUT_TOTAL` 12; `g1cut9`: K 4-9, `CUT_TOTAL` 18; one attempt = both doses)

- **Scope:** CUT in `Obj` (auction, jailed bids and `spawnWant`, frozen Q, rendezvous, release), the `Micro` objective branch, the out-of-fight goal and the dots.
- **5(a) signature** (the 14 archetype cells plus 4 mirror cells, paired against g_iter1). These bars are pre-registered:
  - e1, staffing: in ≥ 80% of their trips of 10+ rounds, at least `CUT_K_MIN` committed within 4 rounds of the track going live;
  - e2, control: committed count within [K−1, K+2] in ≥ 80% of live track-rounds after the first 4;
  - e3, arrival: `cutReach` ≥ 0.5, and responders' median Chebyshev distance to the true carrier at the end of each unseen stretch (`cutSpread`) ≤ 5;
  - conversion, pooled over the batch (a premise reading): contacted 25+ round trips captured ≤ 35% (B3 unopposed: 74%), and contacted carriers killed within 10 rounds of contact ≥ 50%;
  - price, reported: kills and deaths against the paired games, `respLoneDeaths`/`respDeaths`, chasers20 and `enemyUnseenRounds`.
- **Reading:**
  - e1/e2 fail: auction execution.
  - e3 fails while e1/e2 pass: prediction or nav execution. Fix once and re-diagnose.
  - e1-e3 pass but conversion fails: premise failure (F4).
- **5(b) delivery**, per dose:
  - `fire:cutFire>0>=0.9 mean:cutReach>=0.5 rel:chasers20>=1.25 rel:enemyUnseenRounds<=0.75 rel:kills>=0.8 rel:enemyCaptured<=1.25 rel:enemyFirstGrabs<=1.25`
  - Readout reported but not gated: their 25+ round trip capture rate, contacted vs uncontacted, pooled, with binomial SE.
  - If both doses pass, the one with the lower `rel:enemyCaptured` goes forward; within 0.1, the smaller dose.
  - On PASS the filler candidate becomes that build (CLAUDE rule 14).
- **Risks:**
  - Responders lose the fights they leave (kills guard, `respLoneDeaths`).
  - Packs of 6.76 escorts beat K responders (the K-9 dose tests this).
  - Prediction error on mazes (e3 per map).
  - Thinner flags lead to more first grabs (guard).

### M1: band test of CUT

- `tools/band-test.sh`, 2 seeds (515151 + 616161), paired against the existing g_iter1 control runs, with `tools/eval-paired.py --tier tools/upper-tier.txt --rung tools/next-rung.txt` and `arm-deltas.py --beaters <upper tier>`.

**Decision**, pre-registered and applied in order:

| result | action |
|---|---|
| REWRITE_EVAL (a) all-cell net ≥ +2 SE, or (b) upper-tier capture-difference delta ≥ +2 SE with all-cell net ≥ −5 | confirmation seeds 717171 + 818181 (the g_iter1 control is already queued); if the pooled result still meets (a) or (b), CUT replaces g_iter1, followed by ladder field blocks and the TACTICS update |
| all-cell net ≤ −2 SE (regression guard) | CUT closed (ablating it gives g1trk, which is g_iter1 in play) |
| bars not met, all-cell `enemyCaptured` delta ≤ −2 SE | CUT is kept as the provisional base for S3 (it works but does not yet convert to wins) |
| bars not met, `enemyCaptured` delta in (−2, −1] SE | S2: one tuning arm |
| `enemyCaptured` delta > −1 SE | **F5**: CUT closed as a premise failure; S3 proceeds on g1trk if P4 held |

### S2: one tuning arm (conditional on M1, budget 1)

- The choice is pre-registered and read from the M1 band census:
  - (ii) if band `cutReach` < 0.5 (responders do not arrive): **respawner priority**. Jailed bids get t − 4 and `OBJ_TMAX_CUT` 40 for jailed ducks.
  - (i) else if `respDeaths` near the carrier exceed `respKills` of the carrier and its escorts (they arrive and lose): **more bodies**, with K_MIN +2, K_MAX +3 and `CUT_TOTAL` +6.
  - Otherwise there is no tuning arm, and CUT closes by the M1 row.
- **5(a)/5(b):** S1's signature and checks, with bars unchanged. Then one band run with M1's decision table.
- **Risk:** this is the last CUT attempt. A failure closes CUT.

### S3: ESC convoy (conditional on P4; `g1cv4`: `ESC_K_MAX` 4, `g1cv6`: 6, one attempt)

- **Base:** the M1 build if it was accepted or provisional, else g1trk.
- **Scope:** `OWN_C` self-report, ESC objectives, ring and forbidden tiles, the re-grab window, `holdAct` and `regrabSafe`.
- **5(a) signature:** 7 g1escrg cells plus 4 g_iter1-mirror cells, paired. This allows a direct comparison with g1escrg (escorts20 6.34 against 5.55 for the mirror).
  - E fires on 2 or more ducks per trip of ours lasting 6+ rounds.
  - escorts20 within rounds 4-6 after pickup is at least the mirror's + 1.5.
  - Escort-on-forbidden-tile rounds ≤ 2%.
  - A committed escort re-grabs within 2 rounds in ≥ 50% of our carrier deaths with one escort within dist2 8 and the safety rule met.
- **Reading:**
  - Escorts do not arrive: execution failure.
  - escorts20 rises but carrier deaths per pickup and our trip capture rate do not move: premise failure. This is the b2rgc reading: our carriers die inside their spawn regardless.
- **5(b):** `fire:escFire>0>=0.9 rel:escorts20>=1.25 rel:regrabs>=1.5 rel:kills>=0.8 rel:captured>=0.9 rel:enemyCaptured<=1.25`
- **Risks:** re-grabs become dead carriers (b2rg: −37 kills; mitigated by K ≤ 6 and the safety rule), and the quick deaths (29% within 3 rounds) are out of reach.

### M2: band test (CUT + ESC, or ESC alone)

- Same protocol and REWRITE_EVAL bars as M1, plus confirmation on a pass.
- If the bars are not met and our all-cell `captured` delta is below +1 SE, ESC is closed as a premise failure.
- The regression guard ablates ESC and re-reads the M1 numbers.

---

## 4. Stage budget and closing criterion (fixed now; never moved after a number is seen)

**Budget.** Attempts are the binding caps. VM time does not bind: a 120-game band seed takes about 14 minutes.

| stage | cap |
|---|---|
| S0a | 1 tool iteration, plus 1 tracker revision if P1 or P3 fails |
| S0b | at most 2 calibration iterations (each: identity cells plus 1 delivery block) |
| S1 | at most 2 attempts (a dose pair is 1 attempt, so up to 4 delivery blocks) |
| M1 | 1 band run, plus confirmation on a pass |
| S2 | at most 1 arm: 1 delivery block and 1 band run |
| S3 | 1 attempt (a dose pair: 2 delivery blocks) |
| M2 | 1 band run, plus confirmation on a pass |
| **total** | **10 delivery blocks or fewer, 3 band runs or fewer, 1 confirmation pair or fewer, 9 arms or fewer** |

**Closing criteria.** The programme closes on whichever comes first:
- **C1 (S0a premise):**
  - P2 or P6 fails: CUT closed.
  - P1 or P3 still fails after one revision: CUT closed.
  - P4 fails: ESC closed.
  - Both closed: the programme closes. Tracker instruments and columns are kept as tools.
- **C2 (S0b sensor):** the delivery bars still fail after 2 calibrations. F3: the programme closes.
- **C3 (stage delivery):** 2 failed attempts close the stage. The ledger records execution or premise according to which 5(a) diagnostics passed.
- **C4 (M1 premise):** all-cell `enemyCaptured` delta above −1 SE, or still short after the S2 arm. CUT closed (F5).
- **C5 (regression):** all-cell net ≤ −2 SE after ablating the last stage. The programme closes.
- **C6 (budget):** any cap in the table is reached without a milestone meeting REWRITE_EVAL (a) or (b) plus confirmation.
- **C7 (record):** every closure gets a ledger entry with its numbers and a numeric re-open condition, for example "re-open CUT if a tracker reaches predErr ≤ 4 with reachFree ≥ 3 on 50% of unopposed trips".

---

## 5. What to measure overall

**Primary measures** (REWRITE_EVAL, unchanged; `tools/eval-paired.py`):
- Every milestone is paired cell by cell against the g_iter1 control on identical band cells (515151 + 616161; confirmation 717171 + 818181).
- **Wins:** gained, lost, net and exact sign-test p.
- **Capture difference per game** (ours minus theirs): paired delta ± SE.
- Both are reported **separately for the 11 strong band bots (`tools/upper-tier.txt`) and the other 9**, plus all cells, with the rung slice as a descriptive extra.
- Baseline: all 82/234 wins (−0.88); upper 11/128 (−2.04); rest 71/106 (+0.53).

**Split by side:**
- Their captures and our captures per game, delta ± SE, by tier. CUT targets theirs and ESC targets ours, so the split shows which stage moved the difference.
- Trip-level conversion pooled over the band games: their capture rate on 25+ round trips, contacted vs uncontacted, with binomial SE, against the base's low-chaser rate.

**Mechanism and price** (`arm-deltas.py`, all cells and `--beaters` upper tier, ± SE):
- chasers20, enemyUnseenRounds, unopposedCaps, longCapRate;
- cutReach, cutSpread, respDeaths, respLoneDeaths;
- escorts20, regrabs, carrier deaths per pickup;
- kills, deaths, loneDeaths, trickleDeaths;
- inEnemy300, gathered400, stillPost, maxBcK, overruns.

**Running evidence:**
- The filler's paired tally against g_iter1 (`tools/collect-fillers.sh`) is reported at every task check. It is not decisive.
- After acceptance, ladder field blocks alternate with g_iter1 control blocks and give Elo ± SE.

**Records:**
- A `progress/REWRITE.md` row per milestone (build, stages on, all/upper/rest wins and capture deltas, Elo).
- A `TRAINING_LOG.md` entry per stage: pre-registered bars before the run, then values ± SE, the execution/premise reading and the decision.
- TACTICS.md after every ladder run: T1 and T12 neutralization (carriers walking home), T3 (relay chains, via track continuity and the relay speed class), and convoy adoption.

---

## 6. Excluded, and why this is not a closed direction

| element | status | reason |
|---|---|---|
| Tide waves (mass, push, rally, global margin 1, lull crumbs) | excluded | Fights against the beaters are at parity. Its parts are hold-line (0/7), drift (11-24, kills −62), cohesion (20-23, 4-18), the aggression knobs and the crumb detour. Its `loneDeaths`/`trickleDeaths` instruments are kept. |
| GUARD, threat routing, sector board | excluded | Bodies and traps at the flag were refuted four ways (9-25, 12-31, 16-29, 19-23), and g_iter1's `trySpawn` already routes respawners to an alerted flag. Re-open only with a measured deficit-routing premise number. |
| RAID / pre-formed squads | excluded | Cousin of the flank squad (10-26). The quick deaths it targets are a premise S3 tests first. |
| LEAN / drift for non-responders | excluded | Drift refuted. |
| fixing `Sym` for the field | not part of this programme | `symOk` is measured. If it is below 0.9, a separate single-switch arm is logged as a candidate. |

**Differences from the closed flag-pressure arms:**
- **Persistent, predicted, flag-keyed track** instead of 5-round fresh alerts (g1icpt, g1camp).
- **Map-wide selection of free bodies**, jailed ones included, instead of local rules inside a fight.
- **A scale-free objective branch** instead of the rush term, which overruns threats at range and is pinned by engage.
- **Escorts recruited beyond the measurement radius** through the carrier's self-report (g1esc recruited inside dist2 20).
- **Bounded K with sticky commitment and a 1-round decay**, instead of everyone within range (Z1HOLD).

**Main risks across the programme:**
- Responders pulled from winning fights: mitigated by the not-engaged eligibility, K caps and the kills guard.
- Straight-line prediction on mazes: per-map `trkHit20`/`predErr`.
- Self-play misleading the diagnosis: archetype opponents in 5(a), delivery on band cells, judgement on the upper tier.
- Proxies that do not convert: conversion is checked offline (P6), in 5(a) and in the band readout, and the closing rule C4 rests on captures, not on chasers20.