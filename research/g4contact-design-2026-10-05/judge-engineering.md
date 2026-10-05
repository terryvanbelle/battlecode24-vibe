# Engineering judge: three convoy designs against Gymhgy.v10official

I read the code (src/bot Duck, Micro, Comms, C, G, Nav, Track, RobotPlayer, test/bot/BotTest.java,
tools/replaydump/ReplayDump.java), RULES.md, TRAINING_ALGORITHM §3, CRACK-GYMHGY.md, the study (lens-their_offense, synthesis
lever 3, critic) and the predecessor entries in TRAINING_LOG.md. I played no games and edited no repository file.

## Scores (1-10; total = sum of 5)

| design | feasibility | expected gain | measurability | novelty vs failed | bytecode safety | total |
|---|---|---|---|---|---|---|
| g4contact (C.CONTACT) | 7 | 5 | 8 | 6 | 7 | **33** |
| g4deny (C.MASS_DENY) | 8 | 3 | 7 | 5 | 9 | 32 |
| g4meet (C.MEET) | 5 | 5 | 6 | 6 | 8 | 30 |

**Best: g4contact**, with four fixes listed below and two grafts from g4meet.

## Shared facts I checked against the code

- **The fight-branch defect is real.**
  - Duck.turn lines 47-75 call `Micro.fight(enemies, allies)` with no goal.
  - Micro.fight's goal term exists only in the final kite/hold `else` (`-th*RUSH_THREAT_COST - dist2*4`). The carrier
    (20000), loose-flag (19000), engage (10000) and advance (5000) branches ignore it.
  - INTERCEPT therefore charged 150 per threat against about 52 per tile (at d=7).
  - CARRY_FRESH is 5.
- **A11(a) is off in g_iter4.** The A11(a) clear (Duck.sense 168-171) runs only under DEST_CAMP or CARRY_PREDICT, both
  false in g_iter4. So g_iter4 ignores a dropped-flag sighting rather than wiping it. The wipe happened only in g4pred.
- **Slots.**
  - 34-36 (OWN_C) and 37-48 (AUC) have never been written.
  - 62-63 are free: 49-61 hold OF_THREAT, EF_DROP, EF_HOME, OF_LOST and OF_SEEN.
  - So the three designs do not collide.
  - BotTest pins the layout (lines 366-374, which require OWN_C+3 == AUC and AUC+12 == 49), and its fake RC flags writes
    at or above slot 34 (line 36). Every design must update those tests as well as the deadcode `--allow` list.
- **A track already exists.** Track.java (C.TRACK, slots 23-33) is a shared track with drop handling, en20, misses and
  prediction. It costs about 700 bytecodes a turn after setup (S0b log) and has never had a consumer.
  - g4meet gives this reason for not reusing it; g4contact should too.
  - Track's destination rule scored destHit 0.761 on the band sample (S0a P3 FAIL), but 0.892 against ColtG5 (PASS).
    g4meet quotes only the failure.
- **Census.**
  - ReplayDump already has the hooks the designs need: the first-grab test at PICKUP_FLAG (line 530), a per-round
    flag/robot loop (dropGuard, ~687), the indicator-string loop (~663) and the CommTable (alertWrites).
  - It does **not** parse indicator dots, so g4meet's `meetDots` needs new schema parsing.
- **Stun timing (RULES.md).** A stun sets cooldown to 40, and cooldowns fall by 10 a turn, so frozen robots act again at
  r+3 or r+4. A flag dropped at r+k can still be picked up through r+k+3, so a stun alone never outlasts the return window.
  Lens 2 says the same.

## g4contact: best

**Holds:**
- The hook points exist (the fight branch after INTERCEPT, carrierTarget, sense's flag loop).
- `reportCarried` writes only on change.
- `G.bcLeft()` is visible inside the package.
- Micro.dive is cheaper than fight: it drops the inRange/minD enemy loop on every tile.
- The +1.3k worst case is additive on a ~22k peak, giving about 23.3k.
- contact20u12 is the critic's recommended signature. It looks at the object the arm acts on (our flag, carried or
  dropped), not their carrier, which was g4z1's mistake. The sizing claims check out: 78% of chains are under 12, and
  those make 52% of its captures.

**Fix before building:**
1. **The dive score breaks its own threat rule.** `-cheb*100 - th*60 - dist2(goal)`: a diagonal step at Chebyshev k cuts
   dist2 by 4k-2. At k >= 6 that is 22 or more, so a step adding two threats scores 100 - 120 + 22 > 0 and is taken.
   The design claims it is not, and k >= 6 is the arm's operating range (goal within 10-12 tiles). Use g4meet's form
   (`od = max(0, cheb-2)`, no dist2 term) or scale dist2 down to at most 1/16.
2. **"12+ chains behave exactly as in g_iter4" is false.** contactSight calls `reportCarried` on every off-home sighting
   in every chain, so g_iter4's fresh-carrier consumers change for all chains:
   - the chase within 15 tiles;
   - the **destination redirect** for ducks beyond 15 tiles, which is DEST_CAMP-like;
   - respawn routing in trySpawn.

   Either gate the OF_CARRY write on en20 < 12, or keep drop sightings in CT (with a round stamp) as g4meet does with
   its own slots.
3. **sense() structure.** A new `if` "before the existing carried branch" would call reportCarried twice for carried
   flags. Make it one branch that replaces the carried branch when CONTACT is on.
4. **Captured flags leave live tracks.** For up to 12 rounds the predicted point sits beside its spawn centre, and
   raiders within dist2 100 dive toward its spawn until two misses. End the track once the point reaches the stop tile
   with age past the expected arrival.

**Other notes:**
- The parity gate is evaluated only at age <= 1, and there is no per-chain cap on divers. A whole 10-tile front can
  break off at once. A cheap commit cap (g4meet's counter idea) would bound it.
- The bot gates on the current en20, while the census gates on the grab group. Leakage, i.e. dives on chains the census
  files as 12+, will not show in contact20u12; the 5(a) leakage read covers that.

## g4deny

**Holds:**
- The simplest code, at about 550 bytecodes worst case.
- The muster, respawn and stun hooks exist where stated (fieldTarget after line 510, trySpawn 211-212, after
  carrierStun line 46).
- The bit layout fits: an enc of 3,836 or less needs 12 bits, and 11 round bits are enough.
- massGuard64 comes from true positions, so the BASE pairing works.
- It is the only design aimed at 12+ groups (48% of captures). It is also the critic's untried alternative 3.

**Problems:**
1. **The stun claim is wrong.** "If the frozen carrier dies within ~2 rounds, its frozen receivers cannot re-pick" fails
   for every k (see stun timing above). The stun buys about 3 turns of free hits, not a broken chain.
2. **Musterers kite away by default.** With MASS_FIGHT_GOAL=false, ducks reaching 8+ enemies enter the plain
   Micro.fight. Outnumbered, they kite at 1000 per threat. That is the same fight-branch defect the study names, so the
   goal dose should be on in v1.
3. **The mass record can name an empty home.** `massFlag()` can point at a home whose flag was grabbed and then unseen
   for more than CARRY_FRESH, so it no longer counts as carried. Require a recent home sighting, for example OF_SEEN plus
   the home in view.
4. **The warning window is unmeasured.** The "25 rounds" is the game's first sight to its first grab, a team-level
   number. The lead time per mass is unknown, and it can be measured offline from true positions with no bot code. Make
   that a D0 before building.
5. **The prior is poor.** g2alert400 delivered presence (+51%) and missed the grab bar. d2alert went 12-31. F3 shows that
   more of ours at a 12+ grab goes with higher conversion (0.59 vs 0.42).
6. **The gate mixes in an effect bar.** `rel:enemyAtGrab20<=0.85` is an effect read placed beside the own signature.
   That is defensible, but delivery then depends on an effect.

## g4meet

**Holds:**
- The objective-branch score is the cleanest of the three. One threat costs +50 and two cost -200 at every distance;
  there is no dist2 term.
- The jailed planner is bytecode-safe by construction: it starts only with 9k or more left and stops at 2k.
- The event counter is atomic: robots run in turn and writes are visible immediately.
- The least-detour destination rule and the D0 offline routing (85% / 70%) are good engineering.

**Problems:**
1. **"In between it walks alone" is contradicted.** theirs20 stays near 12 through capture chains and is 13.7 in the
   last 10 rounds (lens 2 F4 and F8). So K = 4-10 meets the whole escort. Gymhgy's own respawn stream (spawn to the front
   on our half) also crosses Q, which sits 6 or more tiles from its zone. "Its rear is thin" is unmeasured.
2. **Complexity.** It adds a new 220-line class with a planner, replan triggers, flips, a fork hedge, jailed bids, a
   counter with thinning, and fixQ. Its parent, REWRITE S1, was paused before it was built. This is the highest bug risk.
3. **sense() hook.** "A new branch before the A11(a) branch" inside the else-if chain never sees carried flags, because
   the first branch takes them. It must be a separate `if`.
4. **Fire column.** `meetDots` is unreadable by ReplayDump today, and `hold()` turns set no note. Count a "meet" note
   instead.
5. **chainMet may saturate.** It is any round, 3 or more ahead, over 10+ round chains. Returned chains, where ours
   swarm the carrier, may already reach it, so g_iter4's base may be high. The only pre-planned fallback covers a base
   under 0.05.
6. **"Ahead" uses a different destination.** It uses the true nearest centre, not the bot's destination.
7. **No guard on our own captures.** It recruits raiders from its half, and d2alert's lesson applies. Add
   nw:captured>=0.9.

## Grafts

1. **From g4meet into g4contact:**
   - The objective-score form (`od=max(0,cheb-2)`, x300 per tile, 250 per threat, +120 hit, no dist2) for Micro.dive.
   - Own slots for drop sightings, so g_iter4's carrierTarget, trySpawn and destination redirect stay unchanged for 12+
     chains.
   - A K-capped commit counter per chain, to bound how many divers break off.
2. **From g4meet: a D0-style offline check before code.** For g4contact, chainPoint's error at age 12 or less, at 9/16
   tiles a round, on the existing --track data for Gymhgy chains under 12.
3. **From g4deny:**
   - The cheap 8+-enemy centroid detector and respawn routing to a threatened flag, as a later separate arm.
   - The census columns enemyAtGrab20, bigGrabs12 and massLead, with massLead computed from true positions now, so
     angle B's lead-time premise is tested offline first.
   - MASS_FIGHT_GOAL's lesson: every recalled duck needs a goal inside the fight branch.
4. **One census pass.** One ReplayDump chain tracker can emit all the chain columns: contact20u12, screened20u12,
   capRateU12, chainMet, enemyAtGrab20, bigGrabs12, massGuard64 and massLead.
