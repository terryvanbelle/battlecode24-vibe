# Judge (measurement lens): three convoy designs against Gymhgy.v10official

Scope. Read only: CLAUDE.md, RULES.md, TRAINING_ALGORITHM.md section 3, research/CRACK-GYMHGY.md, the loss study
(lens-their_offense.md, synthesis.md lever 3, critic.md), src/bot/{Duck,Micro,Comms,C,G,RobotPlayer,Track}.java,
tools/replaydump/ReplayDump.java, tools/delivery-gate.sh, tools/delivery-check.py, tools/unit-tests.sh,
test/bot/BotTest.java, and the three design files. No games were played and no repository file was edited.

## Scores (1-10; total = sum of five, max 50)

| design | feasibility | expected gain | measurability | novelty vs failed | bytecode safety | total |
|---|---|---|---|---|---|---|
| A g4contact (C.CONTACT) | 7 | 5 | 6 | 6 | 7 | **31** |
| B g4deny (C.MASS_DENY) | 7 | 4 | 5 | 5 | 7 | 28 |
| C g4meet (C.MEET) | 4 | 4 | 7 | 6 | 6 | 27 |

**Best: A (g4contact)**, after the fixes listed under "Grafts". It repairs the four documented defects directly, and
its signature is taken on the same object it acts on: our flag, carried or dropped. Its measurement still has four
holes, listed below. C has the best signature and the best pre-build check, but its central premise conflicts with
lens 2 and it is the hardest to build. B's primary signature is sound, but it gates delivery on an outcome column that
can be gamed, and its premise is the weakest.

## Code checks that apply to all three designs

- **Confirmed, the defects the brief names:**
  - The fight branch never looks at a chase target unless C.INTERCEPT is on (Duck.java 47-75).
  - The INTERCEPT goal is used only in the kite/hold score (Micro.java 219-222). The carrier, loose-flag, engage and
    advance branches ignore `goal`.
  - CARRY_FRESH = 5 (C.java 19).
  - The sense loop reports our flag only when `isPickedUp()` (Duck.java 162-164), so a dropped flag is never a sighting.
- **The A11(a) clear is not live in g_iter4.** Duck.java 168 runs it only under DEST_CAMP or CARRY_PREDICT, and both
  are off. Calling it "the clear that wipes the track at every drop" describes g4pred, not the incumbent.
- **g_iter4 already sends far ducks to the destination.** carrierTarget (Duck.java 546-568) sends a duck farther than
  CHASE_RADIUS2 to the enemy centre nearest the carrier when that centre is nearer than the carrier (line 561). Any
  design that makes `Comms.carried()` fresh more often also feeds that redirect.
- **delivery-check.py:**
  - A `rel:` check pairs only cells where both arm and base have a numeric value, so blank conditional columns are safe.
  - A `fire:` check counts a 0 as "not fired". A fire column should therefore be blank, not 0, in a game with no
    triggering chain, or games without chains count against the 90% bar.
- **ReplayDump does not parse indicator dots;** it reads only indicator strings (lines 663-668). C's `meetDots` needs
  new parsing code; a note-based column would not.
- **The indicator string already runs past 64 characters when the note is long.** G.java 69-71 builds about 72-80
  characters with a 12-character note such as "fight e12 a8", so the trailing counters (an, wy, cr) are cut. Any
  counter inserted after "x" (B's mw, C's mt) pushes pr/cs/fs off as well. `pr` is the fire evidence A names for its
  carrierTarget extension, and `cr` is g4crumb's.
- **Track.java (S0b, behind C.TRACK) is already a validated shared track of our flags.** Slots 25-33 hold carried and
  dropped states, misses, en20 within dist2 20 of the carrier, a destination and a speed class. It passed three reviews
  and the identity cells. A rebuilds a smaller version of it in slots 34-36. C reuses only `Track.step`/`Track.cheb`.

## A: g4contact (C.CONTACT)

**Claims checked against the code.**
- **Fight-branch placement and the HP and bytecode gates fit the code.** The dive goes after the ESCORT_CARRIER and
  INTERCEPT blocks and before DEF_TETHER and `Micro.fight` (Duck.java 49-72). The gates use RETREAT_HP 300 and `G.bcLeft()`.
- **Slots 34-36 are free in play** (OWN_C, never written). BotTest still references `Comms.OWN_C`: its layout check at
  366-374 and the Track write guard at line 36. So "drop OWN_C from the allow list" also requires editing BotTest, or
  keeping OWN_C as an alias of CT.
- **False: "Chains of 12+ get exactly g_iter4's behaviour", and "games identical until the first dive".**
  `contactSight` calls `Comms.reportCarried(i, F)` for every dropped sighting, with no en20 gate. That makes
  `Comms.carried()` fresh for dropped flags in every chain, 12+ included. trySpawn, fieldTarget's chase and the
  far-duck destination redirect (Duck.java 561) all consume it. The design file itself says "the drop refresh makes
  out-of-fight ducks and respawns go to a dropped flag too". Paired games therefore diverge at the first dropped-flag
  sighting, and the 12+ group is not an untouched control.
- **The dive score does not do what it claims.** "Gains a tile through one extra threat but not two" holds only on
  straight approaches. The raw `-dist2(goal)` tie-break is worth +22 to +38 on diagonal steps at Chebyshev 6-8, so
  100 + 30 - 120 > 0 and the step through two threats is taken. Scale the term (for example dist2 / 8) or cap it below 20.
- **The motivating trace is a case the arm excludes.** In the Canals trace (flag 1678), 6-9 of ours stood 6-10 tiles
  away in fight mode against 14-20 of theirs, a 13-robot grab. The arm skips that chain by design. The 44% "screened"
  and 63% "no contact" figures are pooled over all capture chains, and nobody has measured them for grab groups under 12.

**Measurement.**
- **Good:**
  - `contact20u12` is computed from true positions, so g_iter4 gets a value and `rel:` works.
  - It watches the flag, carried or dropped, so it avoids g4z1's mismatch of acting on the dropped flag while gating
    on ducks near the carrier.
  - It conditions on the only band where contact predicts conversion (critic R5).
  - The leakage read (dive turns with true theirs20 >= 12) is the right check on a partial en20.
- **Hole 1: population mismatch.** The arm decides on the live, observed en20: a lower bound that decays. The column
  splits chains by the true grab group. Divers that enter chains with a grab group of 12+ are invisible to the
  numerator, and u12 chains that grow past 12 dilute it.
- **Hole 2: length weighting.** A share of flag-rounds is weighted by chain length. Returned chains run 0.73-0.79
  contact and capture chains 0.36. If the dive shortens chains, the share moves through composition, which is the
  critic's objection to flagContact20 coming back.
- **Hole 3: ceiling.** The per-game flagContact20 baseline is 0.585, and u12 chains probably sit higher. A x1.15 bar may
  ask for near returned-chain levels, about 0.75-0.8. The low-baseline complement, `screened20u12` or a no-contact
  share, is where a relative bar has room. The critic named the screened share as the primary column.
- **Hole 4: no guard for the named falsifier.** The gate has no `enemyStunVictims` guard and no `enemyFirstGrabs`
  guard, yet defenders may dive for their own flag.

**Bytecode.** chainGoal sits behind CT_BC 6000, and Micro.dive costs less than Micro.fight. contactSight (<= 600) runs
in sense() with no guard on the crowded turns near the flag. Worst case is about 22.6k when no dive follows; acceptable.

## B: g4deny (C.MASS_DENY)

**Claims checked.**
- **Correct:**
  - The line anchors: sense 179-189/190, turn 46, trySpawn 211-212, fieldTarget 510/511, placeCombatTrap 967.
  - The bit packing: an 11-bit round, enc <= 3,836 in 12 bits, a 4-bit count.
  - Slots 62-63 are unused. They are not "the only spare slots", though: 23-48 are reserved but never written in play.
- **Overread: "median 25 rounds from first sight to first grab".** That is a per-game number: its first sight of any
  flag (median r243) to its first grab of the game (lens-setup line 91). It says nothing about how long before a grab
  *we* can see an 8+ mass forming at a given flag, and that lead time is unmeasured (lens 2, "Not measured").
- **Overread: "16.4 of our ducks sit idle on our half".** Lens 2 F8 says they are on our half more than 10 tiles from
  the flag; it does not say they are idle.
- **The premise works against the arm.** In the 12+ row, conversion is 0.42 with none of ours near and 0.59 with 10+ of
  ours near (F3). Our bodies at the grab have not shrunk groups or conversion. Gymhgy also gets 25-30% more kills per
  attack (R16).
- **The stun timing is marginal.** Receivers frozen by the flag-tile stun can still pick up on round 4 (lens 2; RULES:
  "about 3-4 turns"). And killing a 1000-HP carrier within 2 rounds needs about 7 hits.

**Measurement.**
- **Good:** `massGuard64` measures the muster's own output, bodies near a massed home, from true positions, so the base
  gets a value.
- **Hole 1: dilution.** The denominator is 8+ enemies within dist2 100 of the home, a disc of about 314 tiles. The
  trigger needs 8+ enemies inside one duck's vision, about 61 tiles. Many denominator rounds can never fire.
- **Hole 2: an outcome column is gated.** `rel:enemyAtGrab20<=0.85` is an effect, and as a mean per grab it can be
  gamed. If the muster pulls ducks off the other flags, extra small grabs there lower the mean and produce a false
  PASS. `bigGrabs12` per game, or theirs20 at grabs that had a live warning, is the clean deny read, and it belongs in
  the effect reads, not in the delivery gate.
- **Hole 3: two parts go ungated.** The flag-stun part, which carries most of the claimed effect, and the respawn part
  appear in no gated column.

## C: g4meet (C.MEET)

**Claims checked.**
- **Correct:**
  - `Track.step(lx,ly,dx,dy,n)` and `Track.cheb` exist.
  - The packing of slots 37-48 fits in 16 bits per slot.
  - The objective branch slots in after the loose-flag branch (Micro.java 210-211).
  - The planner runs in RobotPlayer only with 9k or more bytecodes left.
  - The jailed robots run trySpawn every round.
- **Implementation hazard: the plan could silently replay g4z1.** "After line 38: Micro.guardFlag = droppedOwnFlag()
  when committed" lands right before Duck.java 39 (`if (Micro.guardFlag != null ...)`), which is Z1HOLD's hold branch.
  Committed ducks with our dropped flag within dist2 20 would run the closed g4z1 behaviour and return before the meet
  branch. The assignment has to go after line 45.
- **The core premise conflicts with lens 2.** "In between, the convoy walks alone at 0.56 tiles a round" does not hold:
  - F4: theirs20 stays near 12 through capture chains.
  - F8: 13.7 within dist2 20 over the last 10 rounds.
  - F2: 3.2 receivers within dist2 2 at every drop.
  A meet of K 4-10 faces a dense convoy, not a lone carrier.
- **Price.** The meet group is drawn largely from our raiders on its half, and presence there at r400 predicts wins
  (B's own risk 3 cites 51% vs 40%).
- **Build risk.** It is the largest build of the three: Meet.java at about 220 lines, a planner, an event counter,
  jailed bids, flips, a hedge, a prepared stun, dot parsing and the D0 tool.

**Measurement (the best of the three).**
- **Good:**
  - `chainMet` measures the arm's distinctive geometry: 3+ of ours ahead of the flag within dist2 20 at t >= grab+5.
    g_iter4 trails by construction.
  - It is per chain, not outcome-selected, split by grab group, and computed from true positions.
  - D0 measures destination and route accuracy offline on existing replays before any code. That is the right kind of
    pre-registered kill switch, aimed at the exact way DEST_CAMP failed.
  - The guard `nw:enemyFirstGrabs` watches the price.
  - Paired games stay identical until the first commit: the sensor writes only its own slots and calls no G.rand.
- **Hole 1: chance-prone.** An "ever" indicator on long chains (52-round capture chains) can be met by chance. It
  depends on the chain-length mix through the 10-round filter.
- **Hole 2: unknown baseline.** Returned chains run ours20 3.4-4.2, and 17.2 of ours are on its half, so the baseline
  may well not be near 0. Set it offline first, not from 5(a).
- **Hole 3:** `meetDots` needs new parsing; a note such as "meet" would reuse the existing string loop.

## Grafts onto A (in order of value)

1. **From C: keep the arm free of side effects until it acts.**
   - Keep the track in its own slots (CT, or Track's TRK_*) and do not call `reportCarried` on dropped sightings.
   - Or put the drop refresh behind its own sub-switch, as a separate dose.
   - This restores "12+ chains = g_iter4" and a clean point where paired games diverge, which the 5(a) read
     ("games identical until the first dive") depends on.
2. **From C: an offline pre-check before building (D0-style; no games).** From lens 2's PosDump extracts (every robot's
   position on every off-home round) and its per-chain JSON, compute for chains with a grab group under 12:
   - the screened share (the premise, unmeasured for u12);
   - contact20 and the no-contact-in-t1-10 share (the baselines for the bar);
   - chainPoint's error at 9/16 tiles a round for ages up to 12.
   Pre-register these routes: build only if u12 chains are screened on 25%+ of rounds and the median prediction error
   is 3 tiles or less.
3. **From C and the critic: a signature counted per chain, not per round.** Gate on `noContact10u12`: the share of u12
   chains still open at t10 with none of ours within dist2 20 during t1-10, with a bar <= 0.8x base. That is exactly the
   critic's forward-check quantity (the only evidence that contact matters), it has a low baseline, and it is immune to
   chain-length composition. Keep `contact20u12` as a companion.
4. **Align the decision population.** Put the diver's observed en20 bucket in the note ("dive7") so the census can
   measure leakage (dive turns with true theirs20 >= 12) in every delivery game, not only in 5(a). Make `diveTurns`
   blank in games with no u12 chain.
5. **From C's event counter: cap the divers per chain** (for example en20 + 2) so a whole local fight does not break off
   at once; that cap is what protects `nw:kills`.
6. **Guards from B and C.** Add `nw:enemyFirstGrabs<=1.1` (defenders dive) and `nw:enemyStunVictims<=1.15` (A's own
   falsifier) to the gate line.
7. **Fix the dive score.** Scale or cap the dist2 tie-break so that two extra threats always outweigh one tile.
8. **From B: watch the 12+ chains A leaves alone.** Report `bigGrabs12` and enemyAtGrab20 (both should stay flat under
   A). Keep the mass-gated flag-tile stun plus muster as the follow-on arm for 12+ chains, but first measure massLead
   offline. Lens 2 kept positions only every 10 rounds before grabs, so that needs a new per-round pre-grab extract
   from the VM replays.
9. **Housekeeping.**
   - Either reuse Track's sensor (sight, negatives, misses, en20) with psym skipped, or justify the new CT slot by
     bytecode.
   - Edit BotTest's OWN_C references together with the deadcode allow list.
   - Place any new indicator counter so it does not push `pr`/`cr` past the 64-character cut.
