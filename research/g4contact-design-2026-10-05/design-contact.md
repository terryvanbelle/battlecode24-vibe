# Angle A, keep contact: arm g4contact (C.CONTACT)

Design only. No games played, no repository file changed. Sources: research/gymhgy-study-2026-10-04 (lens-their_offense
F2-F9, synthesis lever 3, critic "Lever 3" and "Missing alternatives"), research/CRACK-GYMHGY.md, TRAINING_LOG entries
for g1icpt, g1camp, g1icamp, g4pred, g4alert, g4z1 and g4z2, and the code in src/bot (Duck, Micro, Comms, C, G, Nav,
Track) and tools/replaydump/ReplayDump.java.

## 1. One-paragraph summary

Our ducks keep one shared track of each of our flags during an enemy chain. Every sighting of the flag away from its
home refreshes the track, whether the flag is carried or lying on the ground between hand-offs. Each observer also
records how many enemies stand around the flag. The track lives for up to 12 rounds after the last sighting. During that
time it predicts the flag's position at the convoy's measured speed (0.56 tiles a round) toward the nearest enemy spawn
centre. Divers who reach the predicted point and see nothing record a miss, which kills the track. A duck in fight mode
breaks off to dive at the chain when four conditions hold: the track is live, the flag is 5-10 tiles away (beyond
vision, within dist2 100), the chain's group is under 12, and our ducks near the flag do not already outnumber it. The
dive keeps strike-first and the visible-carrier branch. It replaces the engage, advance and kite choices with one score:
tiles gained toward a point two steps ahead of the flag, minus a bounded cost per threat. Chains of 12+ get exactly
g_iter4's behaviour, because the study found contact does not change their conversion.

## 2. Why contact is lost today (code facts)

| fact | where |
|---|---|
| With any engageable enemy in view, `Duck.turn` calls `Micro.fight(enemies, allies)` with no goal. The chase target (`carrierTarget`) is consulted only in `fieldTarget`, i.e. when no enemy is in view. | Duck.java 47-75, 495-500 |
| `Micro.fight` closes on our flag only if the carrier is in the duck's own vision (score 20000 branch). Otherwise the engage (10000), advance (5000) and kite branches choose the step from the local screen. A goal term exists only in the kite branch (`-th*150 - dist2*4`). | Micro.java 208-223 |
| A carrier sighting is usable for `CARRY_FRESH` = 5 rounds (`Comms.carried`). | C.java, Comms.java 159-163 |
| Seeing our flag on the ground away from home does nothing in g_iter4. Under CARRY_PREDICT or DEST_CAMP it is worse: `clearCarried` wipes the track at every relay drop someone sees (the A11(a) branch). | Duck.java 160-171 |
| Gymhgy drops the flag every ~2 rounds. It lies 1 round (57%), 2 (21%), 3 (17%) or 4 (4.4%) before the re-pick, with 3.2 receivers within dist2 2. The flag is on the ground at end of round in about 24% of chain rounds. | lens 2 F2 |
| On 44% of capture-chain rounds ours are at dist2 21-100 of the flag with none within 20 (the "screened" band). Over r463-475 of the Canals loss, 6-9 of ours were within 10 tiles, all with note `fight`. | lens 2 F4, trace |

## 3. Shared-array slots

Existing slots reused, with their g_iter4 meaning kept:

| slot | content | change under C.CONTACT |
|---|---|---|
| 13..15 `OF_LOC[i]` | location of the latest sighting away from home | also written for a **dropped** sighting (the drop tile) |
| 17..19 `OF_CARRY[i]` | round of the latest sighting | also written for a dropped sighting; cleared (with OF_LOC) **only when the flag is seen on its home tile** |
| 58 `OF_LOST` | captured flags | read: no track for a lost flag |

New: **34..36 `CT[i]`**, one per our flag. These are the `OWN_C` slots, reserved for S3 of the paused rewrite and never
written. Comms' schema comment moves the S3 reservation to "unassigned", and `tools/unit-tests.sh` drops OWN_C from the
deadcode `--allow` list. If S3 ever returns it takes 62-63 plus one AUC slot.

```
CT[i] bits  [3..0]   en20   enemies within dist2 20 of the flag, carrier included, cap 15
                            (max over the round's observers; decays by 1 per new sighting round, see 4.2)
            [7..4]   ou20   our robots within dist2 20 of the flag (max over the round's observers), cap 15
            [8]      drop   the latest sighting was on the ground away from home
            [10..9]  miss   negative sightings since the latest positive one, cap 3
            [15..11] spare
```

Same-round detection needs no stamp. An observer reads `OF_CARRY[i]` before writing. If it already equals `G.round`,
another robot has observed this round and the counts merge by max.

## 4. Track rules (sensor; `Duck.sense`, new `Duck.contactSight` / `contactHome`)

Active from r204, after the A12 home re-stamp window (r201-203), and only for flags not in `OF_LOST`. **The sensor never
calls `G.rand` or `G.nearest`.** Ties go to the lower x, then the lower y, as in Track.java. So on a shared seed the game
is byte-identical to g_iter4 until our flag is first seen away from home.

### 4.1 Sightings (a new `if` at the top of sense()'s flag loop, before the existing carried branch)

- **Our flag on its home tile** (`!isPickedUp() && loc == Comms.flagHome(i)`): `contactHome(i)`. If `OF_CARRY[i] != 0`,
  call `Comms.clearCarried(i)`. If `CT[i] != 0`, write 0. This is the A11(a) clear, restricted to home.
- **Our flag elsewhere, carried or on the ground**: `contactSight(i, F, dropped)`:
  1. Read `ct = CT[i]` and `r0 = OF_CARRY[i]`. Set `same = (r0 == round)` and `live = (r0 > 203 && round - r0 <= CT_HOLD)`.
  2. Early out (no counting) if `same` and `en20(ct) >= min(15, enemies.length)` and `ou20(ct) >= min(15, allies.length + 1)`.
     In a crowded fight the first observer counts and the later ones skip.
  3. Count `en` = enemies with `F.distanceSquaredTo(e) <= 20`, carrier included. Count `ou` = 1 for the observer itself
     (it sees F, so it is within dist2 20 of it) plus allies within dist2 20 of F. Skip the ally loop when
     `G.bcLeft() < 15000`; then `ou = 1`.
  4. Merge: `en' = same ? max(en20, en) : live ? max(en, en20 - 1) : en`, and `ou' = same ? max(ou20, ou) : ou`. Write
     `CT[i] = en' | ou' << 4 | drop << 8` (miss reset to 0) only if it changed.
  5. `Comms.reportCarried(i, F)` (OF_LOC, OF_CARRY; it writes only on change).

  The decay by 1 per new sighting round turns several partial views (each observer sees only its own vision disc) into a
  lower bound that does not collapse when one edge observer sees 3 of 12. It falls only as fast as the group really
  shrinks under kills.

### 4.2 Prediction (pure, closed form; `Duck.chainPoint`)

```java
// age = round - OF_CARRY[i]; last = OF_LOC[i]; dest = enemy spawn centre nearest last (cached Sym.enemyCenters(),
// deterministic tie-break). A dropped flag lies one round before its re-pick (57% of re-picks come after 1 round).
static MapLocation chainPoint(MapLocation last, MapLocation dest, int age, boolean dropped) {
    if (dest == null) return last;
    int m = ((dropped ? age - 1 : age) * C.CT_SPEED16) >> 4;       // 9/16 = 0.56 tiles a round (lens 2 F2)
    int ex = dest.x - last.x, ey = dest.y - last.y, ax = Math.abs(ex), ay = Math.abs(ey);
    int n = Math.max(ax, ay) - 1;                                    // stop beside the centre
    if (m > n) m = n;
    if (m <= 0) return last;
    if (ax > m) ax = m; if (ay > m) ay = m;
    return new MapLocation(last.x + (ex < 0 ? -ax : ax), last.y + (ey < 0 ? -ay : ay));
}
```

A track is **live** when four conditions hold: `OF_CARRY[i] > 203`, `age <= CT_HOLD` (12, so at most ~7 tiles of
predicted travel), `miss < CT_MISS` (2), and the flag is not lost. A 12-round hold bounds the error that killed g4pred
(median ~24 tiles at age 60) to a few tiles. The study's median last contact is 10 rounds before the capture.

### 4.3 Misses

A consumer (4.4) that computes P for flag i and has `me.dist2(P) <= CT_MISS_R2` (9: P is well inside its vision) records
a miss if it did not see flag i this turn. The miss is written at most once per robot per round:
`CT[i] += 1 << 9`, capped at 3. Two misses kill the track until the next positive sighting resets `miss`. This catches a
flag that went home unseen (4-round return window) or a convoy that turned toward another spawn.

### 4.4 Consumers

**(a) Fight-branch break-off: the main change.** It goes in `Duck.turn`'s fight branch after the ESCORT_CARRIER and
INTERCEPT blocks (both off in this arm), before the default `Micro.fight`:

```java
if (C.CONTACT && rc.getHealth() >= C.RETREAT_HP && G.bcLeft() >= C.CT_BC) {
    MapLocation cg = chainGoal();
    if (cg != null) { Micro.dive(enemies, allies, cg); intercepts++; G.note = "dive"; return; }
}
```

`chainGoal()` loops over flags i = 0..2. Within the dive radius it returns the nearest qualifying goal, else null:

1. Skip lost flags, flags without a live track, and, for a defender, every flag but its own (`homeFlag()`; DEF_TETHER's
   lesson).
2. **Group gate:** skip if `en20 >= CT_GROUP_MAX` (12). The critic's forward check found that contact in t1-10 changes
   conversion only for grab groups 3-11 (0.34-0.56 without contact, 0.18-0.35 with). For 12+ it makes no difference
   (0.57 vs 0.54).
3. **Parity gate:** skip if `age <= 1` and `ou20 >= en20 + CT_EDGE` (2): fresh contact with numbers already exists.
   Returned chains end with 8.1 of ours against 4.0 of theirs.
4. P = `chainPoint(...)`, d = `me.dist2(P)`. Skip if `d > CT_DIVE_R2` (100, the screened band of lens 2 F4).
5. Flag in my own view this turn (sense() records `seenAt[i]` / `seenDropped[i]`):
   - carried: skip. `Micro.fight`'s carrier branch already closes in on it, and the carrier stun fires.
   - dropped within dist2 8: skip. The duck is at the drop and fights the receivers normally.
   - dropped farther away: the goal is the flag tile.
6. Flag not in view and `d <= CT_MISS_R2`: record a miss (4.3) and skip.
7. Goal = `d > 20 ? step(P, dest, CT_LEAD) : P`, where CT_LEAD = 2. Aiming two tiles ahead of the flag gives lead
   pursuit at 1 tile a round against 0.56. It also keeps divers off the trail: the Canals loss shows Gymhgy building its
   stun screen on and behind the drop tile (lens 2 F6).

**(b) `Micro.dive(enemies, allies, goal)`: new, about 30 lines, beside `fight`.**

```java
boolean actReady = rc.isActionReady();
if (actReady && tryAttack(enemies)) actReady = false;              // strike first: an attack never costs a step
if (!rc.isMovementReady()) { if (actReady) tryHeal(allies); return; }
RobotInfo carrier = (first enemy with hasFlag, else null);
for each of the 9 tiles l (canMove or CENTER):
    th = threat(l, enemies);                                       // enemies within dist2 10 of l
    score = carrier != null ? 20000 - l.dist2(carrier) * 10 - th    // same as fight's carrier branch
          : -cheb(l, goal) * C.DIVE_STEP - th * C.DIVE_THREAT - l.dist2(goal) + adjAllies(l) * 10;
    CENTER +1; ties G.rand(2) as in fight
if best makes no Chebyshev progress (wall in the way): Nav.moveTo(goal)   // bug-nav around it
else move best
if (actReady) { if (!tryAttack(enemies)) tryHeal(allies); }
```

With DIVE_STEP 100 and DIVE_THREAT 60, a step that gains a tile is taken when it adds at most one threat. Two extra
threats (120) outweigh the tile gained (100). For comparison, the kite branch charges 1000 a threat and INTERCEPT's goal
fight charged 150 a threat against about 52 per tile, so it stalled behind any screen.

**(c) `carrierTarget` (out-of-fight ducks and respawns via `trySpawn`).** When `Comms.carried(i, CARRY_FRESH)` is null
and C.CONTACT is set, a live track with `en20 < 12` gives `c = chainPoint(...)`, counted in `predictTurns` (`pr`). This
applies only when `from` is within CHASE_RADIUS2 of c. A predicted point never redirects a far duck to the destination
(the DEST_CAMP failure). Fresh sightings, and every 12+ chain, behave as in g_iter4. The drop refresh (4.1) makes
out-of-fight ducks and respawns go to a dropped flag too.

### 4.5 Indicator and counters (no format change)

- `ch<chases>/<intercepts>/<camps>`: the arm increments `intercepts` on dive turns (INTERCEPT is off in this arm;
  arm-intent enforces it), so `side-indsum.sh`'s `ic` counts dives.
- `pr`: predicted-track turns in carrierTarget.
- Note `dive`: the first token of the indicator string, read by the new census column `diveTurns`.

## 5. Bytecode budget

| work | when | cost |
|---|---|---|
| contactSight count loops (enemies + allies, about 12 bytecode each) and 2-3 slot ops | robot sees our flag away from home; skipped by the same-round early out in crowds | <= 600 (ally loop dropped below 15k left) |
| chainGoal: 3 x (lost bit, 2 reads) | every fight turn after r203 | ~90 |
| chainGoal with live chains: `Sym.enemyCenters()` cached once a turn (~150), nearest centre, chainPoint, lead | fight turn near a live chain | ~300 a chain, <= 700 |
| Micro.dive | replaces Micro.fight on dive turns | <= fight's cost (one threat loop per tile, no inRange/minD/engage loops) |
| carrierTarget extension | out-of-fight turns, live chain | ~200 |

Worst case +1.3k on a fight turn, typical +100-300. Guards:
- chainGoal runs only with `G.bcLeft() >= CT_BC` (6000). Below that the turn falls back to `Micro.fight`, exactly as in
  g_iter4.
- The ally count is skipped below 15k left.

g_iter4's fight turns peak near 22k, so the remaining headroom is about 3k above the worst case. Jailed robots do no new
work. Bar: 0 overruns in the 5(a) and the delivery block; maxBcK at most +1.0 over g_iter4 on the same cells.

## 6. Signature census column (ReplayDump `--capabilities`, new)

**`contact20u12`**, the gate column, is a ratio of two counts:
- Denominator: post-setup end-of-round flag-rounds in which one of our flags is in an enemy chain whose grab group was
  under 12. A chain starts at an enemy first grab of the flag from its home (the existing `kFirstGrabs` test in
  PICKUP_FLAG). It runs through drops and re-pickups until CAPTURE_FLAG, or until the flag lies on its home tile again
  (PLACE_FLAG return). The grab group is the number of the carrier team's robots within dist2 20 of the flag at the end
  of the grab round, the carrier included, as in lens 2's theirs20.
- Numerator: those flag-rounds with at least one of our robots within dist2 20 of the flag. The flag's position is the
  holder's `nowLoc` while carried, else `flagLoc`.
- Blank when the game has no such flag-round.

Companions (same pass, not gated):
- `screened20u12`: none of ours within 20 and at least one within dist2 100. This is the critic's primary; it must fall.
- `capRateU12`: captures divided by chains with a grab group under 12 (blank without one). This is the effect read.
- `chainsU12`: the count of those chains.
- `diveTurns`: our robot-turns whose indicator note is `dive`.

Hook: the per-round block where `dropGuard` is computed (ReplayDump ~684-691) plus chain start and stop at the
PICKUP_FLAG, PLACE_FLAG and CAPTURE_FLAG cases (~526-580). Unit test: a synthetic replay fixture with one small-group
chain, one 12+ chain, a contact round, a screened round and a returned chain. `tools/unit-tests.sh` must stay green.

## 7. Pre-registration (TRAINING_ALGORITHM §3 step 3)

- **Mechanism:** contact with our flag during chains under 12 converts screened rounds into contact rounds. Contact lets
  divers kill carriers and receivers and build carrier stuns ahead of the carrier, which breaks chains where it matters.
- **Decision counters:** note `dive` / `ic` (dive turns), `pr` (predicted-track turns), census `diveTurns`.
- **Reachability at observed values:** about 78% of its chains have a grab group under 12 (lens 2: 789 of 3,553 are
  12+). It makes 8.9-9.1 first grabs a game. Ours sit at 5-10 tiles in the screened band on 44% of capture-chain rounds.
- **Price:** divers leave their local fight and walk through up to one extra threat per tile gained. False dives are
  bounded by 12 rounds and 2 misses.
- **Zero arm:** C.CONTACT=false compiles to g_iter4 (static final; javac drops the hooks). This is the byte-identical
  control.
- **Dose ladder:** DIVE_THREAT 60 (primary). If delivery fails on contact with dives firing, also try 35. CT_HOLD 12; if
  `pr` fires but misses dominate, try 8.

### 7.1 Step 5(a), chosen cells against Gymhgy (PROMPTS 178), sides balanced (critic)

g4contact and g_iter4 on the same engine seeds, 2 seeds per cell (20 pairs):
- GaltonBoard A and B (0/5 each)
- Canals A and B (the control, where the far flags decide)
- DefaultHuge A and B
- KingQuacksCastle A (0/7)
- Joker B (0/5)
- Foxes B (0/9)
- Puzzle A (1/8)

**Read (mechanism firing):**
- `ic` > 0 and `diveTurns` > 0 in every game with a chain under 12.
- At the first dive of each pair (same seed, games identical until then), a `--robot` trace of a diver: note `dive`,
  distance to the flag falling from 6-10 tiles to 4.5 or less within about 6 rounds, and ours20 over the next 10 rounds
  of that chain against g_iter4's same chain (`--defense`).
- `contact20u12` and `screened20u12` per game.
- Leakage, measured from replay truth: the share of dive turns where theirs20 at the flag is 12 or more, and the share
  with no flag within 10 tiles (false track).
- Basics: overruns 0, exceptions 0, maxBcK.

**No delivery block** before this trace shows divers converging (CLAUDE rule 5).

### 7.2 Step 5(b), delivery (the mechanism's own column)

`DGPOOL=Gymhgy.v10official DGTAG=-gymcontact BASE=g_iter4 tools/delivery-gate.sh g4contact
'fire:diveTurns>0>=0.9 rel:contact20u12>=1.15 nw:kills>=0.95 mean:overruns<=0'`

The diagnostic read beside it, not gated: `screened20u12` at or below 0.8x base.

### 7.3 After delivery

The filler pairs g4contact with g_iter4 against Gymhgy on random maps and sides. Effect read: `capRateU12` and its
captures per game (`enemyCaptured`, `enemyCaptured600`). Stacking on g4gym1 comes only after its own victory read.

- **Falsifier:** `contact20u12` rises but `capRateU12` does not fall. That means presence does not break small chains,
  and the defensive-contact line against Gymhgy closes. A second falsifier: kills fall or `enemyStunVictims` jumps,
  meaning divers feed its screen.

## 8. Expected effect

Chains under 12 make about 52% of its captures, roughly 1.0 a game (2.36 in losses, 1.34 in wins overall). If the dive
lifts `contact20u12` by 15-30%, and contact roughly halves conversion for groups 3-11 (critic's forward check), then:
- Conversion of chains under 12 falls by about 10-25%.
- Its captures fall by about 0.1-0.25 a game.
- Win rate rises by about +1-3 points, using the synthesis anchor that games with no capture by r600 win 56% vs 42%.
- Ceiling about 4 points, because 12+ chains (48% of captures) are untouched by design.

## 9. Risks

1. **Partial group counts:** each observer sees only its own disc, so `en20` is a lower bound and some dives may enter
   12+ convoys. The decaying max mitigates this, and the 5(a) measures the leakage.
2. **Abandoned fights:** pulling fighters out of winning local fights. Mitigated by the 10-tile radius, the parity gate
   and the hurt exclusion. The kills guard checks it.
3. **Stun screen:** its rear-guard stuns (15.3 of ours caught per capture chain in losses) may catch divers. The lead
   goal avoids the trail but not every stun.
4. **False tracks:** a flag that went home unseen gives false dives for up to 12 rounds. Home sightings and 2 misses
   bound this.
5. **Contact may not be causal:** within capture chains, contact is equal in wins and losses (critic). Only the
   forward-looking t1-10 check supports a causal role, and only for groups 3-11.
6. **Bytecode:** a worst case of +1.3k on fight turns, guarded by `CT_BC`.
7. **Slots:** the `OWN_C` reservation (34-36) is repurposed. This must be documented in Comms' schema and the deadcode
   allow list.

## 10. Why this is not a repeat

| predecessor | why it failed | what this arm does differently |
|---|---|---|
| g1icpt / INTERCEPT | Its alert was fresh only while someone saw the carrier (5 rounds). Its goal applied only in the kite branch, so engage and advance overrode it. It was built on g_iter1. | Drop sightings refresh the track, and the track lives 12 rounds with prediction and misses. The dive replaces every movement branch except the visible carrier. A group gate sends ducks only where contact matters. A lead goal is used. |
| g4pred / CARRY_PREDICT | Its prediction was consumed only out of fights (carrierTarget), and our ducks near chains are in fights. Its A11(a) clear erased the track at every relay drop someone saw. Its speed was 0.5. | The prediction feeds the in-fight dive. The track is cleared only at home. Speed is 0.56. Age is capped at 12 instead of 60. |
| g1icamp / g3camp / g3camp2 (DEST_CAMP) | Campers went to the wrong spawn, then the arm was neutral. | Never goes to the destination. The destination only steers a prediction of at most ~7 tiles. Far ducks are not redirected. |
| g4alert (ALERT_NEAREST) | It handled pre-grab alerts at homes. | This arm acts after the grab, on the moving chain. |
| g4z1 (Z1HOLD) | It recruited only ducks already within dist2 20 of a dropped flag and held them there while the chain moved on. | Recruits from 5-10 tiles (the screened band), follows the carried or dropped flag, holds no tile. |
| g4z2 (Z2ESCORT) | Target choice only. | Targeting unchanged; movement only. |
| g3tether (DEF_TETHER) | Defenders were tethered to flags. | Defenders dive only for their own flag; nobody is tethered. |

## 11. Files a builder touches

- src/bot/C.java: `CONTACT=false`; `CT_HOLD=12`, `CT_GROUP_MAX=12`, `CT_DIVE_R2=100`, `CT_SPEED16=9`, `CT_LEAD=2`,
  `CT_EDGE=2`, `CT_MISS=2`, `CT_MISS_R2=9`, `CT_BC=6000`, `DIVE_STEP=100`, `DIVE_THREAT=60`.
- src/bot/Comms.java: schema comment; `CT = 34`; drop the `OWN_C` reservation.
- src/bot/Duck.java:
  - sense(): new block at the top of the flag loop, plus `seenAt[]` / `seenDropped[]`.
  - New methods: `contactSight`, `contactHome`, `chainGoal`, `chainPoint`, `nearestCentreDet`, and a per-turn
    enemy-centre cache.
  - The fight-branch hook.
  - carrierTarget: the extension.
- src/bot/Micro.java: `dive()`.
- tools/arm-intent.txt: `g4contact C.CONTACT=true`.
- tools/unit-tests.sh: drop OWN_C from the allow list.
- tools/replaydump/ReplayDump.java: the five columns plus the header.
- test/bot/AuditTest.java:
  - chainPoint (drop lag, stop beside the centre, cap).
  - Merge rules (same-round max, decay, home clear, miss reset).
  - Group and parity gates; defender restriction.
  - Dive score (gains a tile through one threat, not two).
  - Sensor path never calls G.rand.
