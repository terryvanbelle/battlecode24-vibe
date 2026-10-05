# Angle C: meet the convoy on its way home (arm g4meet, switch C.MEET)

Design only. No games were played, no repository file was changed, no external source or 2024 post-mortem was read.
Sources: CLAUDE.md, RULES.md, TRAINING_ALGORITHM.md section 3, research/CRACK-GYMHGY.md, research/gymhgy-study-2026-10-04/
(lens-their_offense F2-F10, lens-fight sections 1-2, synthesis lever 3 and C7, critic "Lever 3" and "Missing alternatives"),
research/REWRITE_DESIGN.md (S0a premise, 2.4 prediction, 2.5 rendezvous, 2.7 objective branch: designed, never built),
TRAINING_LOG.md (g1icpt, g1camp, g1icamp, g2icamp, g3camp, g3camp2, g4pred, g4alert, g4z1, g4z2, S0a premise numbers),
src/bot (Duck, Micro, Comms, C, G, Nav, Sym, Track, RobotPlayer), test/bot/BotTest.java (slot layout),
tools/replaydump/ReplayDump.java (census and --track columns), tools/delivery-gate.sh.

## 0. Summary

- **Where the convoy is weakest: on its own half, just past the front, before its spawn stream.** Behind the convoy is
  its stun rear guard (15.3 of our pursuers caught per capture chain in losses). At the grab it is at its densest (12+
  in 48% of its captures). The last leg runs through its respawn stream (the closed DEST_CAMP line camped there). In
  between, the convoy walks alone at 0.56 tiles a round. The fight sits on our half in every game (lens 4), 17.2 of our
  ducks are on its half at the grab, and our kills there pay us +30 each.
- **How to pick the destination from the observed track: least implied detour, then hedge at the fork.** For each of
  its three spawn centres E_j, the detour implied by the track so far is e_j = |OL| + |LE_j| - |OE_j|, in Euclidean
  tiles, with O the flag's home and L the latest sighting. The zone with the smallest e_j wins (ties go to the shorter
  route from O). The camp arms picked the straight-line nearest centre to the last sighting instead, and against Cyril they
  went to the wrong one of two nearly equidistant spawns. If the two best zones stay ambiguous and their routes share
  9+ tiles, the meet point goes on the shared segment before the fork, so the group is never split. If the routes split
  early, the heading settles within a few tiles and a replan moves the meet point. If the convoy is overdue at the meet
  point and nobody has seen it, the plan flips to the runner-up zone.
- **What a few ducks do.** A bounded group (K = 4-10, scaled to the convoy's size at the sighting) commits to a frozen
  meet point Q. Q is on the predicted route where the front coordinate first reaches f >= 0.6, at least 8 rounds ahead
  of the convoy and at least 6 tiles short of its zone. The ducks travel there through fights using an objective
  branch in Micro.fight, which the current fight branch lacks. They wait, put one stun on the route in front of Q, and
  let the convoy walk into them. The existing carrier branch, carrier stun and receiver-first targeting then do the
  killing.
- **Cheap per turn.** The sensor costs only on rounds when someone sees our flag. A committed duck spends about 150
  bytecodes a turn. Planning (up to 6k) runs only at the end of a turn with 9k or more left, which in practice means a
  jailed duck, since jailed ducks run every round with the whole 25k free. Slots: AUC 37-48 (12 slots, reserved for
  the rewrite's responder auction and never written). These do not collide with angle A (OWN_C 34-36) or angle B
  (62-63).
- **Signature: `chainMet`.** It is the share of its chains of 10+ rounds in which, from t5 on, 3 or more of ours stood
  within dist2 20 of our flag on the side toward its destination ("ahead" of it). That is the arm's own behaviour, and
  it is not selected on outcome. The effect read is its conversion per chain, split by grab-group size (< 12 / 12+).

## 1. Which segment of the route to fight on (evidence)

| segment | what the data says | verdict |
|---|---|---|
| grab site, t0-5 | theirs20 at the grab 12+ in 48% of its captures; within dist2 20 our ducks fall 4.3 -> 2.0 by t5; incidental contact does not change conversion for 12+ groups (critic: 0.57 vs 0.54) | its strongest point |
| pursuit from behind | 47% of our ducks near the grab end up out of the flag's vision still fighting the screen, 14-26% are stunned, 11-17% die; its stuns are built at the drop tile and behind it (Canals: (16,34) r268, (18,33) r271, drop tile r275); 15.3 of ours are caught per capture chain in losses vs 10.3 in wins | where we fight today, and it is a trap |
| front-line crossing (our half, f ~0.4) | the fight sits on our half in every game, and our army's median f is 0.39 (lens 4, C7); its army and the convoy merge there; its kills on our ground pay it +30 | crowded; the dive and keep-contact levers live here |
| **its half, past the front (f 0.6-0.75)** | its army is on our half, so its rear is thin; 17.2 of ours are on its half at the grab; our kills while standing on its territory pay +30 (RULES: the attacker's tile counts); no rear-guard stuns ahead of the convoy; convoy speed 0.56 tiles/round against our 1.0 | **meet here** |
| final leg (last ~5 tiles to its zone) | the capture leg is 1-2 rounds at its own spawn (92% of captures count as "unopposed" for that reason); its respawns appear there; DEST_CAMP at the spawn centre failed twice against Cyril (unseen carrier-rounds -15% against a -20% bar, captures equal) | too late, wrong place |

Timing is in our favour. A route of ~30 tiles takes the convoy ~54 rounds (52.6 measured for capture chains), and the
f >= 0.6 point lies ~60% along it, i.e. ~32 rounds after the grab. Counting detours (x1.2), a duck covers 1 tile in 1.2
rounds, so any duck within ~25 tiles of Q can be there first. That is most of the 16.4 ducks on our half beyond 10 tiles
and the 17.2 on its half at the grab (lens 2 F8: 40.6 of ours could have reached the capture tile before the flag did;
3.5 were within dist2 64 at the capture).

## 2. Mechanism

### 2.1 Sensor: our flag off home, carried or dropped (own slots)

- In `Duck.sense()`'s flag loop, after r203 (A12 has written the homes by then), any of our flags in view and off its
  home counts as a sighting: carried, or **lying dropped between hand-offs**. The relay leaves it on the ground for ~1.7
  rounds per hand-off, and today that is not a sighting.
- The first observer of the round writes the location L and the round into the meet slots (2.6), with a size class for
  the convoy. The class comes from enemies within dist2 20 of the flag among `Duck.enemies`: 0 for 0-2, 1 for 3-5, 2 for
  6-11, 3 for 12+. A later observer of the same round rewrites only to raise the class.
- Our flag seen on its home tile clears the four meet slots of that flag.
- Why own slots: it keeps the arm to one mechanism. Writing drops into `OF_LOC`/`OF_CARRY` (the synthesis's DROP_TRACK)
  would also change `carrierTarget` and respawn routing for every uncommitted duck. Why not C.TRACK as the sensor: it adds
  ~700 bytecodes to every robot every turn after setup (S0b measurement), and fight turns already peak near 22k. Its
  destination rule (nearest centre, rejecting centres moved away from) is the one whose offline destHit was 0.76 on the
  band (S0a P3 FAIL).

### 2.2 Destination: which of its three spawn zones

Inputs: O = `Comms.flagHome(i)` (where every chain on flag i starts), L = the latest sighting, and E_0..E_2 = its spawn
centres under the slot-16 symmetry (decided by observation; symWrong 0 over 1,507 Gymhgy games).

1. **Least implied detour.** e_j = |OL| + |LE_j| - |OE_j| >= 0 (Euclidean, by the triangle inequality, using
   `Math.sqrt`; the planner runs off-fight, so 9 square roots are irrelevant). Rank the zones by (e_j, |OE_j|, x, y)
   to get j1 and j2. With no movement yet (|OL| < 3), the prior is the zone nearest the home.
   - **Not Chebyshev** (checked numerically). The set of Chebyshev-shortest paths to a zone due east of O is a wide
     diamond. A convoy walking diagonally toward a zone to the north-east stays inside it for most of the route, so
     both zones read e = 0 and nothing is learned.
   - The Euclidean detour (an ellipse with foci O and E_j) separates them after 3-4 tiles. It also matches how a
     greedy `directionTo` walker or a relay handing forward actually moves. Relay jitter (about +-1 tile per hand-off)
     is absorbed by the ambiguity margin.
2. **Ambiguity.** The pair is ambiguous when e_j2 - e_j1 <= MEET_AMBIG (1.5 tiles) and |OE_j2| <= 1.3 x |OE_j1|, the
   CAMP_SPLIT ratio, which covers Cyril's Joker case (35.5 vs 36.2 tiles).
3. **Fork hedge, scaled to the angle.**
   - n_f = the largest n for which the two predicted routes are still within MEET_FORK (4) tiles:
     cheb(step(L,E_j1,n), step(L,E_j2,n)) <= 4.
   - **Narrow fork** (n_f >= n_min + 4, i.e. 9+ shared tiles): Q goes on the shared segment, at the midpoint of the two
     route points, no later than n_f. A group standing there sees both routes (vision radius ~4.5).
   - **Wide fork** (n_f < n_min + 4): no hedge; the route goes to j1. The routes split within a few tiles, so the next
     sightings settle the heading, and the consistency check (2.3) replans before the convoy can reach Q, which sits
     ~60% along the route.
   - The camp arms split campers over two spawns (CAMP_SPLIT) and halved the group. This rule never splits it.
4. **Negative evidence flips the zone.** A committed duck within 2 tiles of Q, on a round later than
   T_Q + max(8, 0.4 x (T_Q - last sighting round)), with no sighting newer than T_Q - 2, increments `flips` and sets the
   replan bit; the planner then swaps j1 and j2. The slack is generous because the straight-line pace runs early (bug
   detours, hand-off pauses). At flips >= 2 the meet is cleared and the existing behaviour remains.
5. **Worked example** (the camp failure mode; the numbers were checked with a script).
   - Setup: O = (10,40), E1 = (44,40) (34.0 tiles), E2 = (38,18) (35.6), nearly equidistant (ratio 1.05).
   - The convoy heads north-east toward E2 and is seen at L = (13,37). The camp rule (`G.nearest` from the last
     sighting) picks E1 (31.14 vs 31.40 tiles): wrong.
   - Least detour: e1 = 4.24 + 31.14 - 34.00 = 1.39 and e2 = 4.24 + 31.40 - 35.61 = 0.03, so E2: right. Chebyshev
     detour gives 0 and 0 here.
   - The gap of 1.36 is ambiguous, but the two routes are 4 apart already at n = 2, a wide fork, so it commits to E2.
   - If the convoy turns east and is seen at (17,38): e1 = 0.35 < e2 = 0.67. The ranking changes, the observer sets the
     replan bit, and Q moves to the E1 route while the convoy is still more than 15 rounds from it.

### 2.3 Meet point Q (frozen)

```
plan(i):                                            // Meet.plan, ~3k bytecodes, internal stop at 2k left
  L, rs = latest sighting and its round; O = flagHome(i); E = their centres (slot 16); S = our centres
  rank zones (2.2) -> j1, j2, hedge; if flips odd: swap(j1, j2)
  v100 = their CAPTURING ? 85 : MEET_PACE (60)      // tiles per 100 rounds; measured 56 (overestimating is the safe side, 2.4)
  N = cheb(L, E_j1) - MEET_KEEP (6); nmin = ceil(v100 * MEET_LEAD (8) / 100)
  if N < nmin: clear meet i; return                 // too close to its zone: leave it to the carrier branch
  for n = nmin .. N (step 2 when N > 30):
    A = Track.step(L, E_j1, n); P = A
    if hedge: B = Track.step(L, E_j2, n); if cheb(A,B) > MEET_FORK: break; P = mid(A,B)
    last = (n, P)
    if 4*d2(P, nearest S) >= 9*d2(P, nearest E): pick (n, P); break     // front coordinate f >= 0.6, no sqrt
  if nothing picked: use last (the fork point or the keep-out edge); none at all: clear
  T_Q = rs + ceil(n * 100 / v100); K = {4, 5, 8, 10}[size class], capped at MEET_K_MAX
  write A = enc(Q)|j1|hedge (replan bit cleared), B = T_Q|K-3|flips, keep C/D
```

- **Freeze** (Nav resets bug state when a target moves more than dist2 8, so a goal recomputed every turn stalls on
  mazes). Q changes only on a replan. Replans are triggered by:
  - the first sighting off home (no Q yet);
  - a sighting inconsistent with the plan: L past Q (cheb(L,E_j1) < cheb(Q,E_j1)), or a predicted arrival
    rs + cheb(L,Q) x 100 / v100 differing from T_Q by more than MEET_TOL (6) rounds;
  - the ranked destination changing for the new L (the observer does this check in O(3));
  - a flip (2.2 step 4).
- **Q on a wall or water.** The first committed duck that has Q in vision and finds it impassable rewrites Q to the
  nearest passable tile within Chebyshev 2 on the convoy's side (one write). Bug-nav does the rest.
- **Who plans.** Any robot of ours at the end of its turn (RobotPlayer, after `Sym.update`), if a replan bit is set and
  `Clock.getBytecodesLeft() >= MEET_PLAN_BC` (9000). One plan per call. Jailed ducks run every round with the whole
  budget free, so the latency is normally zero rounds. The observer only sets the bit, so a fighting observer never pays
  for planning. Every input is in the shared array, so a jailed planner needs no vision. It reads the symmetry from
  slot 16 directly, because a jailed robot's `Sym.cands` is not synced.

### 2.4 Who goes (bounded, no auction)

- **K by convoy size.** Classes 0/1/2/3 give K = 4/5/8/10 (dose MEET_K_MAX 6 caps that at 4/5/6/6). Groups under 12
  convert 0.04-0.30 and contact helps there. Groups of 12+ convert 0.36-0.54, and only parity can help: the returned
  chains break at ours20 8.1 vs theirs20 4.0 (88% by a carrier kill). The total over simultaneous meets is capped at
  MEET_TOTAL (14).
- **Eligibility.**
  - Field duck: idx >= 3, not a rusher, not carrying, HP >= MEET_MIN_HP (500), no enemy within dist2 10
    (`Micro.threat(G.me, enemies) == 0`, so not engaged), not cooling down (10 rounds after a release).
  - Feasible: tau_d = cheb(me,Q) x 6/5 + 1 <= (T_Q - round) - 2.
  - Room: count_i < K_i and the sum of counts < MEET_TOTAL.
  - With several live meets, a duck takes the one with the smallest tau_d.
- **Jailed bid.** `Meet.jailed()` runs before `trySpawn`. tau_d = jail time left + cheb(S*, Q) x 6/5 + 1, where S* is our
  spawn centre nearest Q and jail time left = 25 - (round - deadSince), with deadSince = the first unspawned round
  after r200 (per robot). A committed jailed duck spawns at the spawn tile nearest Q.
- **Counter.** Slot D's 5-bit count is event-counted: +1 on commit, -1 on release, floored at 0, and zeroed when the
  meet is cleared. Every release path runs code, because all 50 robots run every round, jailed ones included, so the
  count cannot leak a dead duck. Robots run one after another and writes are visible at once, so a read-modify-write
  within one turn is atomic. That removes the round-start runner and the double buffer that S1 needed.
- **Release** (decrement, cool 10): the meet is cleared or flipped twice; HP < RETREAT_HP (300); after a replan,
  tau_d becomes infeasible; or the count is over K + 1 and `(idx*7 + (round>>3)) % count >= K` (the deterministic
  thinning hash of design 2.5).

### 2.5 What the committed ducks do

1. **Travel through fights** (the code defect named in the brief). With an engageable enemy in view, a committed duck
   runs `Micro.fight` with the objective goal Q. The branch is design 2.7's: it sits below the carrier branch (20000) and
   the loose-flag branch (19000) and replaces engage, advance and kite for committed ducks only:
   score = (od0 - od(l)) x MEET_W - th x MEET_T + (actReady && inRange > 0 ? MEET_HIT : 0) + adjAllies x 10, with
   od(l) = max(0, cheb(l,Q) - 2), MEET_W 300, MEET_T 250, MEET_HIT 120. One threatened step forward is taken (+50); two
   are not. Strike-first still happens before the step, so a passing duck hits what is in reach. Today the goal term
   applies only in the kite branch, and the engage (10000) and advance (5000) branches pin INTERCEPT's ducks to the
   screen.
2. **Hold at Q** (out of a fight, within Chebyshev 2 of Q). The duck waits, heals, and draws `setIndicatorDot(Q, 230,
   40, 200 + i)` (cost 0; the census fire column reads it). With crumbs >= 100 + MEET_TRAP_RESERVE (100), a holder within
   dist2 2 of T = Track.step(Q, L, 2) (two route tiles toward the convoy) builds one STUN on T if T has no trap
   (`senseMapInfo(T).getTrapType()`, own traps are visible). It is triggered by any enemy entering within dist2 2 and
   freezes everything of theirs within dist2 13 (cooldowns to 40).
3. **Fight at Q.** In a fight, the same objective branch with od = 0 inside the band means: hold, strike what comes into
   reach, give way to two threats.
4. **Contact.** When the carrier comes into view, the unchanged carrier branch (`20000 - d2 x 10 - th`) takes over and
   C.CARRIER_STUN (on in g_iter4) builds a stun ahead of it. For committed ducks, `Micro.guardFlag` is set to
   `droppedOwnFlag()`, so `bestTarget` hits receivers within dist2 2 of our dropped flag right after carriers. This is
   the targeting half of Z1HOLD, now with bodies already present. A committed duck with our dropped flag in view takes
   the flag tile as its goal (band 1) for that turn. Every tile within dist2 2 of the flag that we stand on is a pickup
   tile a receiver cannot use.

**Engagement arithmetic.**
- Our stun at round r freezes the convoy's head until r+4: from cooldown 40 its members need 4 turns to get below 10.
- K = 6 ducks at 150 damage per hit and one hit per two turns land ~12 hits (1,800; 2,520 after ATTACK at r600) in
  r+1..r+3. That kills the 1,000 HP carrier.
- The flag then lies on the ground. It returns after 4 end-of-round ticks; the frozen receivers can pick it up from r+4.
- So the stun alone does not break the chain. It buys free hits on frozen ducks (they can neither strike nor move),
  and our bodies on the pickup tiles must do the rest. This is why K, not the stun, is the dose.

### 2.6 Shared-array schema (AUC 37-48; per our flag i)

| slot | bits | content |
|---|---|---|
| 37+i MEET_A | [11..0] enc(Q) (0 = no meet), [13..12] j1, [14] hedge, [15] replan | meet point |
| 40+i MEET_B | [10..0] T_Q (predicted convoy round at Q), [13..11] K-3, [15..14] flips | timing and size |
| 43+i MEET_C | [11..0] enc(L) latest sighting, [13..12] size class, [15..14] j2 | sensor |
| 46+i MEET_D | [10..0] round of L, [15..11] committed count | sensor round and counter |

- With C.MEET false, nothing writes these slots (static final; javac drops the hooks).
- BotTest's layout check keeps `{AUC, AUC_SLOTS}`. MEET_A..D are aliases inside that range. The Comms header comment
  says the AUC reservation is taken by MEET; if the S1 auction ever returns, it needs other slots.

## 3. Code plan (every change behind C.MEET; g_iter4 play is byte-identical with it off)

- **C.java**
  - `MEET = false`; `MEET_K_MIN = 4`, `MEET_K_MAX = 10` (dose 6), `MEET_TOTAL = 14`.
  - `MEET_PACE = 60`, `MEET_PACE_CAP = 85`, `MEET_LEAD = 8`, `MEET_KEEP = 6`; `MEET_F_NUM = 9`, `MEET_F_DEN = 4` (f >= 0.6).
  - `MEET_AMBIG10 = 15` (tenths of a tile), `MEET_RATIO_PCT = 130`, `MEET_FORK = 4`, `MEET_TOL = 6`, `MEET_MIN_HP = 500`.
  - `MEET_TRAP_RESERVE = 100`, `MEET_PLAN_BC = 9000`, `MEET_W = 300`, `MEET_T = 250`, `MEET_HIT = 120`.
  - Each constant carries its one-line reason, as the file's convention requires.
- **Comms.java**: `MEET_A = AUC`, `MEET_B = AUC + 3`, `MEET_C = AUC + 6`, `MEET_D = AUC + 9`; pack and unpack helpers;
  header comment updated.
- **Meet.java** (new, ~220 lines). Its public helpers are pure (`detour`, `rank`, `forkN`, `frontOk`, `planQ`) so the
  unit tests can call them.
  - `sight(i, loc)`: sensor and consistency check.
  - `clear(i)`.
  - `plan(i)`, `planPending()`.
  - `update()`: commit, keep or release; sets `Meet.goal` and `Meet.flag`.
  - `jailed()`, `spawnWant()`, `hold()` (dot and prepared stun), `fixQ()` (impassable Q).
  - Counters `mt` (committed turns), `mc` (commits), `mp` (plans), `ms` (prepared stuns), `mf` (flips).
  - It reuses `Track.step` and `Track.cheb` (public, pure) and never calls `G.rand`; ties go to lower x, then lower y.
- **Duck.java hooks**
  1. `turn()` line 28, before `trySpawn()`: `if (C.MEET) Meet.jailed();`. In `trySpawn()` lines 209-212, for
     non-defenders: `if (C.MEET) { MapLocation w = Meet.spawnWant(); if (w != null) want = w; }`.
  2. `sense()` flag loop (lines 160-177): a new branch before the A11(a) branch. Our flag, `G.round > 203`, off home
     (`!f.getLocation().equals(Comms.flagHome(i))`), carried or not: `Meet.sight(i, f.getLocation())`. Our flag on its
     home tile: `Meet.clear(i)` if slot A or D is non-zero. Also clear on the FLAG_LOST bit.
  3. After the second `pickupFlags()` (line 36), before the rusher test: `if (C.MEET) Meet.update();`. After line 38
     (`Micro.guardFlag = ...`): `if (C.MEET && Meet.goal != null) Micro.guardFlag = droppedOwnFlag();`.
  4. Fight branch, after `placeCombatTrap()` (line 48), before ESCORT_CARRIER:
     `if (C.MEET && Meet.goal != null) { Micro.objGoal = Meet.goal; Micro.fight(enemies, allies); Micro.objGoal = null; G.note = "meet"; return; }`.
  5. Before the defender branch (line 76): `if (C.MEET && Meet.goal != null) { Meet.hold(); Micro.tryHeal(allies); return; }`.
     `hold()` calls `Nav.moveTo(goal)` while outside band 2, else waits and builds the prepared stun.
- **Micro.java**: `static MapLocation objGoal`. In `fight()`, a branch after the `loose` branch (line 211):
  `else if (objGoal != null && goal == null) { score = ...as 2.5(1)... }`. `od0` is computed once before the tile loop.
  Nothing else changes.
- **RobotPlayer.java**: after `Sym.update()` (line 19): `if (C.MEET && Clock.getBytecodesLeft() >= C.MEET_PLAN_BC) Meet.planPending();`
  inside the existing try.
- **G.endTurn**: ` mt<turns>/<commits>/<plans>/<stuns>/<flips>` right after `x<exceptions>`, so the 64-character cut
  keeps it.
- **tools/arm-intent.txt**: `g4meet C.MEET=true`; `g4meet6 C.MEET=true`, `g4meet6 C.MEET_K_MAX=6`.
- **Unit tests** (test/bot/AuditTest or BotTest; rule 6):
  - `rank` picks the zone the track heads for over the nearer one (the worked example: E2 at (13,37), E1 at (17,38)),
    and the prior when L = O.
  - Hedge on a narrow fork puts Q within MEET_FORK of both routes; a wide fork does not hedge.
  - `planQ` meets f >= 0.6 and the keep-out, and clears when N < nmin.
  - Counter: K commits admitted from 0 in one round, the (K+1)-th refused, release decrements, clear zeroes.
  - Switch off: a full fake turn writes no slot 37-48 (BotTest's badWrite pattern).
  - Layout test unchanged.
- **ReplayDump (census, --capabilities)**: `chainMet`, `chainMetSmall`, `chainMetBig`, `chainConv`, `chainConvSmall`,
  `chainConvBig`, `meetDots` (section 5). Run tools/unit-tests.sh after the tool change too.

## 4. Bytecode budget (limit 25,000; fight turns peak near 22k)

| piece | who / when | typical | worst |
|---|---|---|---|
| `sight`: enemy count around the flag, 2 writes, consistency check (arrival time, past-Q test, 3-zone re-rank with 9 `Math.sqrt`) | first observer of our flag off home in a round (later observers early-out on D.round); the re-rank is skipped below 3,000 left and left to the next observer | 450 | 750 |
| `clear` on a home sighting | only when a meet slot is non-zero | 0 | 330 |
| `update`, eligible scan | spawned field duck not threatened (never in a heavy fight), per live meet | 40 | 200 (3 meets) |
| `update`, committed: live check; counter write only on commit or release | committed duck | 60 | 160 |
| objective branch in `Micro.fight` | committed duck in a fight | replaces engage/advance/kite (~ +60 for 9 cheb) | +120 |
| `hold`: dot (0), prepared stun (`senseMapInfo` + build) | committed at Q, at most once per meet tile | 50 | 300 |
| `jailed` bid | unspawned duck (25k free) | 80 | 200 |
| `plan` (scan of at most 30 route steps, 6 squared distances each) | end of a turn with >= 9,000 left, one plan per call | 3,000 | 6,000 (stops at 2k left) |

- Fight-turn delta for a committed duck: at most ~+300 (live check plus the objective branch), so a peak of ~22.3k,
  under the 22.5k near-miss line.
- Worst stacked case: a committed duck that is also the round's first observer of a convoy, in a crowded fight, adds
  ~+1.0k, for ~23.0k. Hence the re-rank is skipped below 3,000 left, and the 5(a) bar is maxBcK <= 23.0.
- The planner cannot cause an overrun: it starts only with 9k left and stops itself at 2k.
- The 5(a) bar is 0 overruns and maxBcK <= 23.0.

## 5. Census columns (ReplayDump --capabilities; the other team's chains on this team's flags, like chasers20)

- **Chain** (lens 2): a first grab of one of our flags from its home, through every drop and re-pickup, until CAPTURE
  or RETURN (home again). The flag's tile each round is the carrier's tile or the ground tile. "Ahead": one of our
  robots r with dist2(r, flag) <= 20 and dist2(r, Ec) < dist2(flag, Ec), where Ec is the true enemy spawn centre nearest
  the flag that round.
- **`chainMet` (signature)**: among chains lasting 10+ rounds, the share with at least one round t >= grab + 5 in which
  3 or more of our robots are ahead of the flag. Blank when the game has no such chain. `chainMetSmall` and
  `chainMetBig` give the same split by theirs20 at the grab (< 12 / 12+).
  - Why this column: it is what the arm does (a group in front of the convoy, mid-chain) and what g_iter4 does not (our
    ducks trail 5-10 tiles behind, lens 2 F4). It is not outcome-selected (every chain of 10+ rounds counts; the critic's
    objection to capture-chain rates). It is per chain, not per round, so a meet that lasts 5 rounds of a 50-round
    chain still shows.
- **`meetDots` (fire)**: robot-rounds with a MEET indicator dot (red 230, green 40) of this team. Blank without a
  10+ round chain.
- **Effect reads (reported, not gated)**: `chainConv` = captures / chains, plus the < 12 and 12+ splits; also
  `enemyCaptured600`, `enemyCaptured`, `enemyUnseenRounds`, `enemyStunVictims` (the arm should walk fewer pursuers into
  its rear guard).

## 6. Pre-check D0 (no bot code; existing replays)

The earlier camp arms failed on destination. Against Gymhgy the destination behaviour has never been measured, so
measure it before 5(a):
- Over the g_iter4-vs-Gymhgy replays kept on the VM, take every capture chain, the zone it captured in, and the observed
  track (sightings by our side, as `--track` defines them).
- Compute (a) the straight-line nearest centre to the last sighting (the camp rule), (b) Track's rule (`destHit`, which
  `--track` already prints), and (c) the least-detour rule with the fork hedge, at t5, t10 and t15.
- Also print `predErr` (existing) for Gymhgy's convoys, i.e. how well the straight-line route predicts this opponent.
- Routing (pre-registered): rule (c) at t10 right (or hedged with the captured zone inside the fork) in >= 85% of chains:
  build g4meet as specified. 70-85%: build with MEET_AMBIG10 30, i.e. 3 tiles (hedge more often). < 70%, or median predErr > 4 tiles:
  the straight-line route does not describe Gymhgy's path, so park the angle and log it.
- It costs a scratch reader or a few lines added to the --track tracker, and no games.

## 7. 5(a) diagnostic (chosen cells, PROMPTS 178; g_iter4 on the same cells and seeds)

`tools/diag-batch.sh <tag> g4meet:Gymhgy.v10official:<map>:<seed>:<side>` with seeds 782001 and 782002, and the same
cells for g_iter4:

| cell | why |
|---|---|
| Canals A, Canals B | traced; the far flags (BFS 36) decide every game, so the long chains are the target |
| Valentine A | longest chains (~12 hand-offs) |
| Joker B | 0/5; near-equidistant spawns (Cyril's wrong-spawn case): tests the fork hedge |
| KingQuacksCastle A | 0/7, 3.0 of its captures a game, 2.86 unopposed |
| Foxes B | 0/9 |
| DefaultHuge A | open map, longest routes: the rendezvous at its best |
| Snake B | maze: the straight-line prediction's hardest case (per-map read) |
| Puzzle A | 1/8 |

9 cells x 2 seeds = 18 pairs (sides A 5, B 4; the critic asks for both sides). Read (indicator sums via side-indsum,
`--defense`, `--flags`, dots via `--logs`/positions):
1. Fire: `mt`, `mc`, `mp` > 0 on every cell with a 10+ round chain.
2. Destination: the Q zone (j1, or the fork) against the zone each chain captured in.
3. Arrival: committed ducks within 2 tiles of Q before the convoy's first sighting there (margin in rounds), and the
   committed count at that moment against K.
4. `chainMet` (from --defense positions until the column exists), split by < 12 and 12+.
5. Its conversion per chain, `enemyCaptured600`, and stun victims among our pursuers.
6. Kills, deaths, enemyFirstGrabs (thinner flags?), maxBcK, overruns, exceptions.

Wins are descriptive only.

## 8. Delivery gate (pre-register after 5(a); proposed)

`DGPOOL=Gymhgy.v10official DGTAG=-gymmeet BASE=g_iter4 tools/delivery-gate.sh g4meet 'fire:meetDots>0>=0.9 rel:chainMet>=1.5 nw:kills>=0.95 nw:enemyFirstGrabs<=1.1 mean:overruns<=0'`

- The dose arm g4meet6 gets the same line with DGTAG=-gymmeet6. One attempt is both doses (as S1). If both pass, the one
  with the lower `chainConv` goes forward; within 0.02, the smaller dose.
- If 5(a) shows g_iter4's `chainMet` below 0.05, the ratio is unstable, so the bar is re-registered before the block as
  `mean:chainMet>=<base+0.10>`. That is the only bar change allowed, and it is made before any delivery number.
- On PASS, the filler candidate becomes g4meet: `FILLPOOL=Gymhgy.v10official tools/filler-pair.sh g_iter4 g4meet 40`,
  random maps and sides. That is the victory read. If g4gym1 is promoted first, the arm stacks on it and pairs against
  it.
- **Falsifier.** `chainMet` passes but `chainConvSmall` does not fall (paired, same cells). Then presence ahead does
  not convert even where contact predicts it, which matches lens 2 F3 ("group size decides"), and the convoy-defense
  line closes against this opponent. `chainMetBig` rising with `chainConvBig` flat closes only the mass part (K 8-10).

## 9. Expected effect

- **Signature.** `chainMet` x1.5-2.5. g_iter4's ducks are behind the convoy by construction (63% of capture-chain rounds
  have none of ours in the flag's vision; 44% have ours at 5-10 tiles).
- **Conversion, small groups.** Groups under 12 carry ~52% of its captures (~1.0 a game). At t10, chains in this band
  convert 0.34-0.56 without contact and 0.18-0.35 with it (critic). If the meet reaches half of them and converts like
  contacted chains: about -0.17 captures a game.
- **Conversion, 12+ groups.** Untested. 0 to -0.07 a game if K = 10 plus the stun reaches parity.
- **Total.** -0.15 to -0.25 of its ~1.95 captures a game, in losses mostly. Games with no Gymhgy capture by r600 win
  56% vs 42%.
- **If delivered.** +2 to +3 points against Gymhgy, ceiling ~+5. Probability of delivery about 0.4: the arm targets
  the largest loss mode and has a direct signature, but the Gymhgy defense area already has two failed deliveries.
- **Price, small and bounded in time.** About 4-5 meets a game, ~8 ducks each for ~25-30 rounds, is ~1% of post-setup
  duck-rounds. Ducks come mostly from its half (our raiders, converting 7.9%) and from respawns.

## 10. Risks

1. **Prediction on mazes.** A straight-line route (Track.step) puts Q off the convoy's real path (Snake, MazeRunner).
   Mitigations: the replan on an inconsistent sighting, the per-map read in 5(a), and D0's predErr. Choke and path-aware
   placement is a v2 dose only if 5(a) shows Q off-route on open maps too.
2. **Wrong zone despite the rules.** A wide fork plus a late heading change, or a convoy that turns after passing the
   fork. Flips cap the waste at two re-plans. D0 measures it before any build.
3. **Responders lost on the way or pulled from fights we were winning.** Eligibility requires not threatened and
   HP >= 500. The objective branch refuses two threats. Guard: kills.
4. **Thinner flags lead to more first grabs.** Responders come from the field, never the defenders (idx 0-2).
   Guard: enemyFirstGrabs.
5. **12+ convoys still convert** because K = 10 is not parity after the escorts regroup. The dose pair and the
   `chainConvBig` read show it. The falsifier closes only that part.
6. **Bank too low for the prepared stun** (our late bank is 160-260). The stun is an add-on and the bodies are the
   mechanism. `ms` shows how often it is built.
7. **Counter drift** if an exception skips a decrement. It is bounded by the meet's lifetime (the count is zeroed on
   clear). Exceptions are a basics bar (0).
8. **Bytecode.** A committed duck in a crowded fight reaches ~22.3k. The planner runs only above 9k left and stops at
   2k. The 5(a) bar is maxBcK <= 23.0 with 0 overruns.
9. **Slot reuse.** The AUC range is taken. If the S1 auction is ever revived it needs its own slots (BotTest
   documents this).

## 11. Why this is not a repeat

| predecessor | what it did | why it failed or stalled | g4meet |
|---|---|---|---|
| g1icpt / INTERCEPT | in a fight, the fresh carrier became the goal | the alert is fresh only while someone sees the carrier; the goal term sits in the kite branch only; chasing from behind runs into its rear-guard stuns | the goal is a frozen point ahead, valid through unseen rounds; the objective branch replaces engage/advance/kite for committed ducks |
| g4pred / CARRY_PREDICT | a stale sighting became the predicted current point, chased outside fights | unseen rounds unchanged; A11(a) erased the track at every hand-off; pace 0.5; the chase still trails the convoy | needs only the route, not the current point; drops count as sightings in its own slots; pace 0.6 (0.85 with CAPTURING) |
| g1camp, g1icamp, g2icamp, g3camp, g3camp2 / DEST_CAMP (+CAMP_SPLIT) | wait at the enemy spawn centre nearest the last sighting, every duck that could beat the carrier there | wrong zone among near-equidistant spawns (straight-line nearest); camped in its respawn stream after the decisive leg; outside fights only; CAMP_SPLIT halved the group | least-detour zone from the observed track, a fork hedge that never splits, flips on negative evidence; Q mid-route at f >= 0.6 and >= 6 tiles before the zone; K bounded by convoy size; honoured in fights; respawns spawn toward Q |
| g4alert / ALERT_NEAREST | answer the nearest alert | alerts precede the grab; unseen carrier-rounds -4% | acts after the grab, along the route |
| g4z1 / Z1HOLD | converge on our dropped flag | by the time ducks arrive the relay has re-picked it and walks away from them | the group is already ahead; Z1HOLD's receiver targeting is reused only for ducks that are there |
| g4z2 / Z2ESCORT | hit escorts first | targeting only; cut re-grabs 41%, nothing else | not a targeting change |
| g3tether / DEF_TETHER | defenders fight beside their flag | closed | defenders never commit |
| REWRITE S1 CUT (designed, never built) | frozen intercept point, auction, objective branch | paused before S0b's consumer | keeps the frozen Q and the objective branch; drops the auction for an event counter; new destination rule (S0a P3 FAILED on the old one, 0.76); Q by front coordinate, not earliest feasible point; prepared stun; K from convoy size; aimed at Gymhgy's slow convoy |

Combinability: g4meet uses slots 37-48, angle A (keep contact) uses 34-36, angle B (deny before the grab) uses 62-63.
Each is a separate switch, so a later stack can pair any two against the best single one.
