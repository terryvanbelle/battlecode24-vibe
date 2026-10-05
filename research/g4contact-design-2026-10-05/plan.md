# Arm g4contact (C.CONTACT): a flag track in its own slots, and an in-fight dive capped per chain for observed groups under 12

Synthesis of the convoy designs. The base is g4contact, the winner on both judges (33 and 31). Grafts:

- **From g4meet:**
  - The objective score in place of the dive score (no dist2 term).
  - The track kept in the arm's own slots, never in OF_CARRY.
  - A per-chain diver counter.
  - The offline premise check D0, run before the 5(a).
  - The fire column read from a note, not from indicator dots.
- **From the measurement judge:**
  - A per-chain signature that matches the critic's forward check: `noContact10u12`.
  - The observed group bucket carried in the note, so leakage can be read.
  - Guards on first grabs and stun victims.
- **From g4deny:** `capRate12p` as the control read for 12+ chains, which the arm must leave alone.

Built on g_iter4 (rule 16), paired against g_iter4. No games were played and no repository file was edited.

Every fact below was checked against the code:
- The fight branch has no chase goal (Duck.java 47-75).
- The goal term exists only in the kite branch (Micro.java 219-222).
- CARRY_FRESH is 5.
- A dropped own flag is never reported (Duck.java 162-164).
- A11(a) runs only under DEST_CAMP or CARRY_PREDICT, so it is inert in g_iter4.
- Slots 34-36 (OWN_C) and 62-63 are never written in play.
- OF_SEEN (59-61) is written by trackLost under C.FLAG_LOST, which is on in g_iter4.
- Engine costs (MethodCosts.txt): `writeSharedArray` 75, `readSharedArray` 2, `senseNearbyRobots` 100 flat.
- `senseNearbyRobots(center, r2, team)` excludes the caller and filters to tiles the caller can sense (RobotControllerImpl 253-273).

## 1. Switches

| name | default | role |
|---|---|---|
| `C.CONTACT` | false | **The only behaviour switch.** Sensor, in-fight dive and out-of-fight predicted chase. `static final`, so with it off javac drops every hook (the zero arm is g_iter4). Intent line: `g4contact C.CONTACT=true`. |

Constants beside it in C.java (tunables, not switches):

| constant | value | reason |
|---|---|---|
| `CT_HOLD` | 12 | Rounds a track stays live after the last sighting. 12 x 0.56 is about 7 tiles of predicted travel. g4pred used 60 (median error ~24 tiles). |
| `CT_GROUP_MAX` | 12 | No dive and no chase for an observed group of 12 or more. Critic R5: contact changes conversion only for groups 3-11 (0.34-0.56 vs 0.18-0.35), not for 12+ (0.57 vs 0.54). D0 may lower it to 10 or 8 (section 6). |
| `CT_DIVE_R2` | 100 | Dive only when the predicted flag is within 10 tiles: the screened band, ours at dist2 21-100 on 44% of capture-chain rounds. |
| `CT_SPEED16` | 9 | Prediction at 9/16 = 0.5625 tiles a round (lens 2 F2: 0.56). |
| `CT_LEAD` | 2 | Goal 2 steps past the predicted point toward its spawn: lead pursuit at 1 tile a round against 0.56, off the trail where its rear-guard stuns sit. |
| `CT_EDGE` | 2 | Diver cap per chain per round: `need = en20 + 2 - (age <= 1 ? ou20 : 0)`. `need <= 0` means fresh contact already has the numbers. |
| `CT_MISS`, `CT_MISS_R2` | 2, 9 | Two negative sightings (predicted point within dist2 9 of a robot that does not see the flag, age >= 1) kill the track until the next positive sighting. |
| `CT_BC` | 6000 | chainGoal runs only with this many bytecodes left; otherwise the turn is g_iter4's `Micro.fight`. |
| `CT_SENSE_BC` | 15000 | The sensor skips its ally count below this. |
| `CT_STEP`, `CT_THREAT`, `CT_HIT` | 300, 250, 120 | Dive score: g4meet's objective weights (REWRITE_DESIGN 2.7). A tile gained through one extra threat scores +50 and is taken; through two it scores -200 and is not, at every distance. Dose ladder for `CT_THREAT`: 250, then 140 (two extra threats per tile). |

A unit test pins `!C.CONTACT || C.FLAG_LOST`, because the track's round comes from OF_SEEN.

## 2. Shared-array slots (Comms.java)

| slot | name | bits | writer |
|---|---|---|---|
| 34..36 | `CT[i]` (replaces the never-written `OWN_C` reservation) | [11..0] enc(L), the latest off-home sighting of our flag i, carried (carrier tile) or lying (flag tile); [15..12] en20, the high-water mark over the live track of enemies within dist2 20 of the flag, carrier included, max over observers, cap 15. 0 = no track. Max value 3836 \| 15<<12 = 65,276. | contactSight, contactHome |
| 62 | `CT_AUX` | [1..0], [3..2], [5..4] misses of flags 0/1/2 (cap 3); [8..6], [11..9], [14..12] ou20 of flags 0/1/2, our robots within dist2 20 of the flag in the latest sighting round (observer included, max over that round's observers, cap 7); [15] 0. | contactSight, contactHome, ctMiss |
| 63 | `CT_DIVE` | [3..0] round & 15 of the last claim; [7..4], [11..8], [15..12] divers of flags 0/1/2 this round. A stamp from another round reads as 0. Max 65,535. | claimDive |
| 59..61 | `OF_SEEN[i]` (existing, read only) | Round of the latest sighting by anyone. Invariant: `CT[i] != 0` means the latest sighting was off home at L in round OF_SEEN[i]. A home sighting clears CT in the same turn, before trackLost writes OF_SEEN. | trackLost (unchanged) |

- **No writes to 0-33 or 49-61.** In particular OF_LOC and OF_CARRY are never written, so g_iter4's chase, its destination redirect (Duck.java 561) and trySpawn are unchanged for every chain the gate does not admit.
- **Rewrite reservations:** AUC 37-48 stays reserved for the paused S1. S3 would need new slots.
- **Packing helpers.** Static and pure, for the tests: `ctPack(loc, en)`, `ctLoc(v)`, `ctEn(v)`, `auxMiss(a, i)`, `auxOu(a, i)`, `auxSet(a, i, miss, ou)`, `diveCount(dv, i, round)`, `diveAdd(dv, i, round)`.
- **Schema comment:** rewrite lines 34-36 and "49..63 spare" to cover 49-63.

## 3. Code plan

### src/bot/Duck.java

1. **Per-turn state.**
   - Fields: `ctSeenAt[3] = {-1,-1,-1}`, `ctSeenLoc[3]`, `ctSeenCarried[3]`, `ctMissAt[3] = {-1,-1,-1}`, `ctEc` / `ctEcAt` (a per-round cache of `Sym.enemyCenters()`), and the out-values `ctEn` / `ctNeed`.
   - None of this code calls `G.rand` or `G.nearest`. Ties go to the lower x, then the lower y.

2. **sense(), line 161, right after `if (C.TRACK) Track.sight(f);`.** Add a separate `if`, outside the else-if chain:

   `if (C.CONTACT && G.round > C.SETUP_ROUNDS + 3 && f.getTeam() == G.us) contactSight(f);`

   - The existing carried branch (`reportCarried`) is left untouched, so nothing is reported twice.
   - The start at r204 skips the A12 home re-stamp window (r201-203).

3. **contactSight(f)** (worst case about 500 bytecodes):
   ```java
   int i = Comms.ourFlagIndex(f.getID()); if (i < 0) return;
   MapLocation F = f.getLocation(); boolean carried = f.isPickedUp();
   ctSeenAt[i] = G.round; ctSeenLoc[i] = F; ctSeenCarried[i] = carried;
   int ct = rd(CT+i), aux = rd(CT_AUX);
   if (!carried && F.equals(Comms.flagHome(i))) {            // contactHome: chain over
       if (ct != 0) wr(CT+i, 0); int na = auxSet(aux,i,0,0); if (na != aux) wr(CT_AUX, na); return; }
   int seen = rd(OF_SEEN+i);                                  // read before this robot's trackLost
   boolean live = ct != 0 && G.round - seen <= C.CT_HOLD, same = live && seen == G.round;
   int loc = enc(F), oen = ctEn(ct), oou = auxOu(aux,i);
   if (same && ctLoc(ct) == loc && oen >= min(15, enemies.length) && oou >= min(7, allies.length+1)) return;
   int en = rc.senseNearbyRobots(F, 20, G.them).length;      // 100 flat, carrier included
   int ou = 1 + (G.bcLeft() >= C.CT_SENSE_BC ? rc.senseNearbyRobots(F, 20, G.us).length : 0);
   int nen = min(15, live ? max(oen, en) : en);              // high-water mark: approximates the grab group
   int nou = min(7, same ? max(oou, ou) : ou);                // fresh: this round only
   if (ctPack(loc,nen) != ct) wr(CT+i, ctPack(loc,nen));
   int na = auxSet(aux, i, 0, nou); if (na != aux) wr(CT_AUX, na);   // a positive sighting resets misses
   ```
   - **High-water mark, not design A's decaying max.** A decay of 1 per round let a real 13-group read 11 after two partial views. The census splits chains by grab group, and so does the critic's evidence.

4. **Pure helpers.**
   - `chainPoint(L, D, age)`:
     - `m = age*CT_SPEED16 >> 4`; `n = Track.cheb(L, D) - 1`.
     - `m > n+1` returns **null**: the predicted arrival has passed, meaning a capture or a wrong track (judge fix 4).
     - Otherwise `Track.step(L.x, L.y, D.x, D.y, min(m, n))`, or L when that is <= 0.
   - `ctDest(L)`: the cached enemy centre nearest L by dist2, with the deterministic tie-break.

5. **trackGoal(i, from, maxD2)**, shared by both consumers:
   - **Gates, in order:**
     - `CT[i] != 0`;
     - `age = round - OF_SEEN[i] <= CT_HOLD`;
     - `en20 < CT_GROUP_MAX`;
     - `auxMiss < CT_MISS`;
     - `need = en20 + CT_EDGE - (age <= 1 ? ou20 : 0) > 0`.
     Then set `ctEn` and `ctNeed`.
   - **Flag in my own view this turn** (`ctSeenAt[i] == round`):
     - carried: return null. Micro.fight's carrier branch and CARRIER_STUN handle it.
     - dropped within dist2 8 of me: return null (fight the receivers normally).
     - dropped farther away: return the flag tile.
   - **Otherwise:**
     - `P = chainPoint(L, ctDest(L), age)`; return null if P is null or `from.dist2(P) > maxD2`.
     - If `G.me != null && age >= 1 && G.me.dist2(P) <= CT_MISS_R2`, call `ctMiss(i, aux)` and return null.
     - Return `from.dist2(P) > 20 ? Track.step(P.x, P.y, D.x, D.y, CT_LEAD) : P`.
   - **ctMiss:** at most once per robot per round per flag (`ctMissAt`). Writes `auxSet(aux, i, min(3, miss+1), ou)`.

6. **chainGoal()** (fight branch):
   - Returns null at r <= 203.
   - Skips lost flags (`Comms.lostMask()`), and for defenders every flag but `homeFlag()` (DEF_TETHER's lesson).
   - Keeps the nearest `trackGoal(i, G.me, CT_DIVE_R2)` hit.
   - Then `claimDive(best, need)`:
     - read CT_DIVE; return false if `diveCount >= need`;
     - else write `diveAdd` (75 bytecodes) and return true.
   - The per-round cap is filled in fixed execution order, so the same eligible ducks keep diving from round to round.

7. **turn(): new block between line 64 (end of the INTERCEPT block) and line 65:**
   ```java
   if (C.CONTACT && rc.getHealth() >= C.RETREAT_HP && G.bcLeft() >= C.CT_BC) {
       MapLocation cg = chainGoal();
       if (cg != null) { Micro.dive(enemies, allies, cg); intercepts++; G.note = "dive" + ctEn; return; }
   }
   ```
   - It runs after carrierStun (46) and placeCombatTrap (48), and before DEF_TETHER and `Micro.fight` (72).
   - Hurt ducks (< 300 HP) keep g_iter4's micro.

8. **carrierTarget(from), after the CARRY_PREDICT block (line 557).** This covers out-of-fight ducks (fieldTarget) and respawns (trySpawn):
   ```java
   if (c == null && C.CONTACT && (lost >> i & 1) == 0 && !(isDefender() && i != homeFlag())) {
       MapLocation p = trackGoal(i, from, C.CHASE_RADIUS2);
       if (p != null) { predictTurns++; int d = from.distanceSquaredTo(p); if (d < bd) { bd = d; best = p; } continue; }
   }
   ```
   - There is no destination redirect and no `G.nearest` call on this path.
   - A fresh OF_CARRY sighting still comes first, exactly as in g_iter4.
   - `lost` is read once before the loop, only when CONTACT is on.
   - On jailed robots `G.me == null`, so no miss is written and the lead test uses `from`.

### src/bot/Micro.java: `dive(enemies, allies, goal)`, about 30 lines beside fight()

```java
boolean actReady = rc.isActionReady();
if (actReady && tryAttack(enemies)) actReady = false;          // strike first
if (!rc.isMovementReady()) { if (actReady) tryHeal(allies); return true; }
RobotInfo carrier = first enemy with hasFlag, else null;
int od0 = cheb(me, goal); boolean open = false;
for 9 tiles l (canMove or CENTER):
    th = 0; hit = false; for e: x = l.d2(e); if (x <= 10) { th++; if (x <= 4) hit = true; }   // one enemy loop
    od = cheb(l, goal); if (od < od0) open = true; adj = allies within dist2 2 of l;
    s = carrier != null ? 20000 - l.d2(carrier)*10 - th                                       // as fight's carrier branch
                        : (od0 - od)*C.CT_STEP - th*C.CT_THREAT + (actReady && hit ? C.CT_HIT : 0) + adj*10;
    CENTER +1; ties G.rand(2) as in fight
if (carrier == null && !open && od0 > 1) Nav.moveTo(goal);      // every progress tile blocked: bug around the wall
else if (best != CENTER) move(best);
if (actReady) { if (!tryAttack(enemies)) tryHeal(allies); }
```
- **Score.** Against staying put, a step is taken only when it adds at most one threat per tile gained, at any distance. The judges' counterexample (a diagonal step at Chebyshev 6-8 with two extra threats) now scores 300 - 500 < 0.
- **Cost.** dive is cheaper than fight: one enemy loop per tile instead of two, and no engage/advance/kite scoring.

### Other files

- **Comms.java:** `CT = 34`, `CT_AUX = 62`, `CT_DIVE = 63`; remove `OWN_C`; add the packing helpers; update the schema comment.
- **C.java:** the switch and constants, each with its measurement in the comment.
- **G.endTurn:** no format change. The note `dive<en20>` leads the string (5-6 characters).
  - `ic` (the intercepts slot of `ch a/b/c`) counts dive turns; INTERCEPT is off in this arm.
  - `pr` counts predicted chases.
- **Tooling:**
  - tools/unit-tests.sh: `--allow AUC,AUC_SLOTS` (drop OWN_C).
  - tools/arm-intent.txt: `g4contact C.CONTACT=true`.
  - Dose arm, built only under falsifier F1: `g4contact140 C.CONTACT=true` plus `g4contact140 C.CT_THREAT=140`.
- **test/bot/BotTest.java:**
  - Line 36's Track write guard becomes `i >= Comms.CT`.
  - The layout at lines 366-374 uses `{Comms.CT,3}` and adds OF_THREAT, EF_DROP, EF_HOME, OF_LOST, OF_SEEN, `{Comms.CT_AUX,1}` and `{Comms.CT_DIVE,1}`, so 0-63 are disjoint and all assigned.
  - The contiguity check becomes `Comms.CT + 3 == Comms.AUC`.

## 4. Bytecode budget (limit 25,000; g_iter4 fight turns peak near 22k)

| piece | when | typical | worst |
|---|---|---|---|
| contactSight | spawned robot sees our flag off home, r204+ | 250 (first observer of the round: 2 senses + 1-2 writes); 60 (same-round early out) | 500 (crowd-independent: senseNearbyRobots is flat) |
| contactHome | flag seen at home with CT or aux bits set | 10 | 170 |
| chainGoal, no live track | every fight turn, r204+ | 40 | 60 |
| chainGoal, live tracks | fight turn while a flag has a live u12 track | 350 | 1,100 (3 live tracks + a miss write + the claim write) |
| Micro.dive | replaces Micro.fight on dive turns | 1-2k below fight in crowds | at most fight |
| carrierTarget extension | out-of-fight or jailed turns, live track | 200 | 700 (light turns) |

- **Worst cases:**
  - Stacked fight turn without a dive: +1.6k, about 23.6k.
  - Dive turns: at or below g_iter4's cost.
  - Jailed robots add only the trySpawn path.
- **Guards:**
  - `CT_BC` 6000 before chainGoal.
  - The ally sense is skipped below 15k left.
  - Every write is change-only.
- **Bars (5(a) and delivery):** 0 overruns, 0 exceptions, maxBcK <= 24.0, and near90 turns (>= 22.5k) per game at most twice the twin's.
- **Identity note.** Extra bytecode spent in sense() can flip only `Micro.engageable`'s `REACH_BC` 8000 test, and only on a turn with under ~8.5k left after sense, which is rare. Sym.update returns at once after symmetry is decided.

## 5. Census columns (tools/replaydump/ReplayDump.java --capabilities)

These come from one chain tracker. Each column describes the other team's chains on this team's flags, like chasers20.

**Chain definition:**
- **Start:** a post-setup PICKUP_FLAG passing the existing first-grab test (flagLoc == flagHome, line 530).
- **Flag position each end of round:** the holder's `nowLoc` while carried (the `carrying` entry with this flag id), else `flagLoc`.
- **End:**
  - CAPTURE_FLAG (line 576);
  - or a PLACE_FLAG with `rn > 200 && !wasCarried` (the return home, line 553);
  - or game end (OPEN).
- **g0, the grab group:** the carrier team's robots within dist2 20 of the flag at the end of the grab round, carrier included (lens 2's theirs20).
- **Per-round tally:** in the per-round block after dropGuard (line ~691), for t = rn - grab >= 1:
  - ours20 = our robots within dist2 20 of the flag;
  - ours100 = our robots within dist2 100.

**Columns:**

| column | definition | blank when | role |
|---|---|---|---|
| `noContact10u12` | Among chains with g0 < 12, the share with ours20 = 0 at every end of round t = 1..min(10, T), where T is the chain's last open round. Short chains count over their own window, so early returns do not drain the denominator. | no u12 chain | **gate (own signature)** |
| `contact20u12` | Over u12-chain flag-rounds with t >= 1: the share with ours20 >= 1. | no such flag-round | companion |
| `screened20u12` | The same flag-rounds: the share with ours20 = 0 and ours100 >= 1. | as above | companion; also the fallback gate (section 6) |
| `chainsU12` / `chains12p` | Chains with g0 < 12 and with g0 >= 12. Invariant: their sum is `enemyFirstGrabs`. | no chain | reads |
| `capRateU12` / `capRate12p` | Captures divided by closed chains (CAPTURE or RETURN; OPEN excluded) in each band. | no closed chain in the band | effect read / control |
| `diveTurns` | Our post-setup robot-turns whose indicator string starts with `dive` (indicator loop, line 663). | no u12 chain, so fire: skips the game | **fire** |
| `diveLeak12` / `diveNoChain` | Per dive turn, take the open chain on our flags whose flag lies nearest the diver within dist2 144 (end-of-round positions; divers are queued in the indicator loop and resolved in the per-round block after g0 is set). Count the share whose chain has g0 >= 12, and the share with no chain within dist2 144. | no dive turn | leakage |

**Tool tests (test_tools.py):**
- Extend `NEW_CAP` with the new columns.
- Check that each new column is blank or numeric, and that each share lies in [0,1].
- Check `chainsU12 + chains12p == enemyFirstGrabs`, `contact20u12 + screened20u12 <= 1`, and diveTurns 0 or blank on the fixture replays.
- Add a `--calc` query `X g0 outcome o20_1 o100_1 ... o20_k o100_k` that feeds a scripted chain to the pure ChainTally class. Compare it with a python reference covering:
  - a u12 contact chain;
  - a u12 screened chain;
  - a u12 chain returned at t5 with no contact;
  - a 12+ chain;
  - an OPEN chain.

## 6. D0: offline premise check (no games; before the 5(a), while the bot code is written)

**Tool:** a new `ReplayDump --contact-d0 [--team A|B]`, built on the S0a vision helpers. It applies the bot's exact sensor rules to what our robots could see:
- observers = our alive robots within dist2 20 of the flag;
- observed en = the max over observers of enemies within dist2 20 of both the flag and that observer;
- the high-water mark over the live track;
- age since the last sighting;
- chainPoint toward the true nearest enemy centre;
- eligible round = live track, observed en < CT_GROUP_MAX, one of ours within dist2 100 of P and none within dist2 20 of the flag.

It writes one row per chain (g0, outcome, observed en max, sighting at t0, noContact10, eligible rounds, unseen rounds, Chebyshev prediction error by age 1-12). Input: the newest 300+ g_iter4-vs-Gymhgy filler replays on the VM. The census columns of section 5 run on the same replays and give the baselines.

**Routes, pre-registered:**

1. **Prediction.**
   - Median chainPoint error over u12-chain rounds aged 1-12 is 3 tiles or less: `CT_HOLD` 12.
   - 3 or less only up to age 8: `CT_HOLD` 8.
   - Over 3 by age 4: park the arm (the track cannot lead divers).
2. **Reach.** Eligible rounds divided by unseen live-track rounds of u12 chains:
   - 0.25 or more: build as specified;
   - 0.15-0.25: `CT_DIVE_R2` 144;
   - under 0.15: park (our bodies are not in the band for small groups; the motivating Canals trace was a 13-robot grab).
3. **Group gate calibration.** `CT_GROUP_MAX` is the largest of {12, 10, 8} for which the share of eligible rounds belonging to true g0 >= 12 chains is 0.25 or less. If none qualifies, keep 12; the 5(a) leakage read decides.
4. **Gate column.**
   - Gate on `noContact10u12` if both hold:
     - its baseline is 0.08 or more;
     - 40% or more of the no-contact u12 chains had one of our robots within dist2 20 at t0, so a track exists and the arm can act.
   - Otherwise the gate check becomes `rel:screened20u12<=0.8`.
   - This switch is decided from D0 alone, before any arm data.

## 7. Delivery check (pre-registered; TRAINING_ALGORITHM section 3 step 5(b))

```
DGPOOL=Gymhgy.v10official DGTAG=-gymcontact BASE=g_iter4 tools/delivery-gate.sh g4contact 'fire:diveTurns>0>=0.9 rel:noContact10u12<=0.8 nw:kills>=0.95 nw:enemyFirstGrabs<=1.1 nw:enemyStunVictims<=1.2 mean:overruns<=0'
```

- **Reads beside the gate (not gated):**
  - contact20u12 (expect x1.15 or more);
  - screened20u12 (expect 0.8x or less);
  - diveLeak12 <= 0.3, diveNoChain <= 0.2;
  - capRate12p flat (within +-0.05);
  - maxBcK <= 24.0;
  - enemyCaptured, enemyCaptured600.
- **Ordering:** no delivery block before the 5(a) shows divers converging (CLAUDE rules 5 and 13).
- **After a PASS:**
  - The filler candidate becomes g4contact (rule 14): `FILLPOOL=Gymhgy.v10official tools/filler-pair.sh g_iter4 g4contact 40`, random maps and sides.
  - The victory read is the paired net wins plus capRateU12 vs g_iter4 over 240 or more pairs.
  - Stack on g4gym1 only after g4gym1's own victory read, and re-run the 5(a) on the stack.

## 8. Chosen-cell 5(a) against Gymhgy.v10official

**Setup:**
- 8 map-side cells x 2 seeds (781001, 781002), 16 arm games, sides balanced 4 A / 4 B.
- g_iter4 twins on the same seeds. Gymhgy and g_iter4 are deterministic under a fixed seed (TRAINING_LOG), so the pairs are exact.
- Twins already exist for GaltonBoard A, GaltonBoard B and KingQuacksCastle A (g4crumb 5(a)); replay them if pruned. The other 10 twins are played.

```
tools/vm-enqueue.sh g4contact-5a 'tools/diag-batch.sh g4contact-5a \
 g4contact:Gymhgy.v10official:GaltonBoard:781001:A      g4contact:Gymhgy.v10official:GaltonBoard:781002:A \
 g4contact:Gymhgy.v10official:GaltonBoard:781001:B      g4contact:Gymhgy.v10official:GaltonBoard:781002:B \
 g4contact:Gymhgy.v10official:KingQuacksCastle:781001:A g4contact:Gymhgy.v10official:KingQuacksCastle:781002:A \
 g4contact:Gymhgy.v10official:Puzzle:781001:A           g4contact:Gymhgy.v10official:Puzzle:781002:A \
 g4contact:Gymhgy.v10official:Joker:781001:B            g4contact:Gymhgy.v10official:Joker:781002:B \
 g4contact:Gymhgy.v10official:Foxes:781001:B            g4contact:Gymhgy.v10official:Foxes:781002:B \
 g4contact:Gymhgy.v10official:DefaultHuge:781001:B      g4contact:Gymhgy.v10official:DefaultHuge:781002:B \
 g4contact:Gymhgy.v10official:Canals:781001:A           g4contact:Gymhgy.v10official:Canals:781002:A'
```
Then the same specs with g_iter4 for the missing twins (Puzzle A, Joker B, Foxes B, DefaultHuge B, Canals A).

**Why these cells (census records W/total):**
- GaltonBoard A and B: 0/5 each; an open centre.
- KingQuacksCastle A: 0/7; 3.0 Gymhgy captures a game.
- Puzzle A: 1/8.
- Joker B: 0/5.
- Foxes B: 0/9.
- DefaultHuge B: 0/4; the longest open routes, which stress the prediction.
- Canals A: the **negative control**. Its centre-flag grab is 12+ (13 in the trace), so no dive should fire on that chain; the far flags decide.

**Reads:**
1. **Fire:** diveTurns > 0 (and `ic`) in every game with a u12 chain; `pr` > 0.
2. **Identity:**
   - Diff `--metrics 1` of each pair to find the first differing round.
   - It must be at or after the earlier of our first `dive` note (`--logs '^dive' --team <side>`) and our first `pr` increment. An earlier divergence is a leak, and it is fixed before delivery.
3. **Convergence trace:**
   - At the first dive of each pair, trace the diver with `--robot <id>` for 10 rounds.
   - Its distance to the flag should fall from 5-10 tiles to dist2 20 or less within about 6 rounds.
   - Over that chain's next 10 rounds, compare ours20 with the twin's same chain (`--defense`).
4. **Per game against the twin:** noContact10u12, contact20u12, screened20u12, chainsU12, capRateU12, capRate12p, diveLeak12, diveNoChain.
5. **Price (descriptive; 16 cells cannot judge it):** kills, deaths, enemyStunVictims, enemyFirstGrabs, enemyCaptured600, enemyCaptured.
6. **Basics:** overruns 0, exceptions 0, maxBcK <= 24.0, symWrong 0.

**Passes to delivery when all of these hold:**
- dives fire in 14 or more of the games with a u12 chain;
- at least one traced convergence on 6 or more maps;
- pooled diveLeak12 <= 0.3 and diveNoChain <= 0.2;
- no identity leak;
- the basics hold.

A leakage miss recalibrates `CT_GROUP_MAX` (or sets `CT_MISS` to 1) once, and the 5(a) is repeated on the same cells.

## 9. Unit tests (test/bot/AuditTest.java unless noted; fake RC from BotTest)

1. **Packing.**
   - `ctPack`/`ctLoc`/`ctEn`, the `auxSet` fields and `diveAdd`/`diveCount` round-trip.
   - All values are <= 65,535.
   - A stamp from another round reads 0.
2. **chainPoint.**
   - Age 0 returns L; age 16 gives 9 tiles toward a far D.
   - Each axis is capped; the point stops at cheb - 1.
   - Null once `m > n+1`.
3. **contactSight (fake RC; a write guard flags any write outside {34, 35, 36, 62, 63}).**
   - First sighting: CT written, ou20 written, misses reset.
   - Same round, same tile, a smaller view: no write (count the writes).
   - Same round, more enemies: max.
   - New round, live track, fewer enemies: the high-water mark is kept.
   - After `CT_HOLD`: a fresh count.
   - Home sighting: CT 0 and that flag's aux bits 0.
   - r <= 203: no writes.
   - OF_LOC and OF_CARRY (13-19) are never touched.
   - `G.rngState` is unchanged.
4. **trackGoal / chainGoal gates.**
   - Each of these returns null: lost flag; en20 >= 12; misses >= 2; age > 12; `need <= 0` with fresh ou20; a defender on another flag; P beyond dist2 100.
   - P within dist2 9 and unseen at age >= 1: one miss write, and none on a second call in the same round.
   - Carried flag in view: null. Dropped at dist2 <= 8: null. Dropped farther: the flag tile.
   - Lead: `step(P, D, 2)` when dist2 > 20.
5. **claimDive.**
   - With need 3, three claims succeed in a round and the fourth fails.
   - The next round resets.
   - The counts for different flags are independent.
6. **Micro.dive (moveOk fake).**
   - A step gaining 1 with +1 threat beats CENTER.
   - +2 threats loses, including a diagonal at Chebyshev 8 (the judges' counterexample).
   - A visible carrier uses the carrier branch.
   - All progress tiles walled: Nav.moveTo is used.
7. **carrierTarget extension.**
   - A fresh OF_CARRY wins.
   - A u12 live track within dist2 225 returns P with no redirect.
   - A 12+ track returns null (g_iter4 path).
   - A jailed caller (`G.me == null`) writes no miss.
8. **Static checks (BotTest):** `!C.CONTACT || C.FLAG_LOST`; the slot layout of section 3.
9. **Suite:** tools/unit-tests.sh green, including deadcode (OWN_C removed from the allow list) and arm-intent (g4contact line).
10. **Zero arm:** one same-seed game, src/bot (CONTACT=false) vs g_iter4 against Gymhgy on one cell, gives identical replays.

## 10. Expected effect and falsifiers

**Expected effect:**
- Chains with a grab group under 12 make about 52% of Gymhgy's captures, roughly 1.0 a game.
- If the dive cuts noContact10u12 by 20% or more, and contact roughly halves conversion for groups 3-11 (critic R5), then its captures fall by 0.1-0.25 a game. That is about +1 to +3 points against Gymhgy, using the synthesis anchor that games with no capture by r600 win 56% vs 42%.
- Ceiling about 4 points: 12+ chains are untouched by design.

**Falsifiers (pre-registered):**

| | trigger | meaning | action |
|---|---|---|---|
| F1 | Delivery: fire ok, but noContact10u12 FAILs (2 SE short of 0.8x) | divers do not reach the chain | One dose retry, g4contact140 (CT_THREAT 140, two threats a tile). If that FAILs too, close the dive line against Gymhgy. |
| F2 | The kills or enemyStunVictims guard FAILs | divers feed its stun screen | Close at this dose; no stronger dose. |
| F3 | Paired filler, 240+ pairs vs g_iter4: capRateU12 ratio above 0.9, or the paired difference within 1 SE of 0, while noContact10u12 delivered | presence does not break small chains; the critic's R5 causal reading is falsified | Close the convoy-contact line. What remains against 12+ groups is the pre-grab line (g4deny's muster), after its own offline massLead check. |
| F4 | capRate12p moves by more than 0.05 absolute, or diveLeak12 > 0.3 in delivery | the group gate is blind | Recalibrate `CT_GROUP_MAX` once from D0, else close. |

## 11. Why this is not a repeat

| predecessor | what failed | what this arm does instead |
|---|---|---|
| g1icpt (INTERCEPT) | Fresh only while seen (5 rounds); goal only in the kite branch; 150 per threat vs ~52 per tile | Drops refresh a 12-round predicted track; the dive replaces every movement branch but the visible carrier; one threat per tile is allowed, two are not. |
| g4pred (CARRY_PREDICT) | Consumed out of fights only; 0.5 tiles a round; age 60 | In-fight consumer; 0.56; age 12 plus misses. |
| DEST_CAMP (g1icamp, g3camp, g3camp2) | Camped at the destination | Nobody goes to a destination; the destination only steers a prediction of 7 tiles or less. |
| g4z1 (Z1HOLD) | Held a tile | Recruits from 5-10 tiles and follows the flag; holds no tile. |
| g4z2 (Z2ESCORT) | Targeting only | Targeting is unchanged. |
| g3tether (DEF_TETHER) | Defenders tethered to flags | Defenders dive only for their own flag. |
| g4alert (ALERT_NEAREST) | Before the grab | After the grab. |

New relative to all of them: the group gate puts divers only where contact predicts conversion, and the per-chain cap keeps whole fights from breaking off.

## 12. Residual risks

1. **Partial group counts:** en20 is a lower bound, so some dives enter 12+ convoys. D0 calibrates the gate; diveLeak12 measures it in every game.
2. **Dive cap order:** the cap fills by execution order, not by distance, so a farther duck may take a slot from a nearer one.
3. **Stamp aliasing:** the 4-bit CT_DIVE stamp can alias every 16 rounds. That blocks divers for at most one round.
4. **Unseen return:** a flag that went home unseen keeps a dead track for up to 12 rounds, bounded by misses.
5. **Causality:** contact may not be causal; within capture chains it is equal in wins and losses. F3 is the test.
6. **Indicator cut:** a long `fight` note can push `pr`/`cr` past the 64-character cut. The gate reads only the note prefix.
