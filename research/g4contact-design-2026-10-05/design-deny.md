# Angle B: deny the big group before the grab (arm g4deny, switch C.MASS_DENY)

Design only. No games played, no repository file changed. Sources: CLAUDE.md, RULES.md, TRAINING_ALGORITHM.md section 3,
research/CRACK-GYMHGY.md, research/gymhgy-study-2026-10-04/ (lens-their_offense F3/F5/F7/F8, Canals traces; lens-setup
section 5; lens-fight; synthesis lever 3; critic "Lever 3" and "Missing alternatives" 3), TRAINING_LOG.md (d2alert,
g2alert400, g2fstun, g1icpt, g4pred, g3camp/g3camp2, g4alert, g4z1, g4z2, g3tether, g4front), src/bot (Duck, Micro,
Comms, C, G, Nav, Sym), test/bot/BotTest.java (slot layout test), tools/replaydump/ReplayDump.java, tools/delivery-check.py.

## 1. What the data says about the window

- The group at the grab decides the chain. Its robots within dist2 20 of our flag at the first grab: 0-2 convert 0.04,
  3-5 0.11, 6-8 0.22, 9-11 0.30, 12+ 0.48 (n 789 of 3,553 chains, about 2.0 a game). 12+ groups make 48% of its
  captures (378 of 795). For 12+ groups, contact by our robots after the grab changes nothing (critic R5: 0.57 vs 0.54).
  The only place a response can still work against them is **before** the grab, by making the group smaller or by stopping
  the grab.
- There is a window. Gymhgy first sees one of our flags at a median r243 and needs a median **25 rounds** to its first grab
  (we need 6; lens 5 section 5). That is a team-level number, not per flag, and our warning time before grabs was never
  measured (lens 2 "Not measured"). The 5(a) below has to measure it.
- Bodies exist but stay elsewhere. At the grab 16.4 of ours are on our half more than 10 tiles from the flag, 17.2 on its
  half, 5.7 jailed (lens 2 F8). Within 10 tiles at the grab: capture chains ours 10.7 vs theirs 17.9; returned chains 8.0 vs
  10.0. Canals loss trace: at r459 about 15 of its robots at our corner flag (2,56) while about 20 of ours stood idle about
  20 tiles away at the captured centre home; grab 13 vs 6; ours20 = 0 from r463 to the capture.
- Why the code misses it. The pre-grab alert (C.ALERT_FIX) fires only once an enemy is within dist2 20 of the home, does
  not look at how many, and pulls only non-fighting ducks within ALERT_RADIUS2 100 (Duck.fieldTarget line 502). Respawns
  follow the freshest alert (Duck.trySpawn line 212). Nothing in the bot counts a group.
- What a stun does at the grab (RULES.md). A stun on the flag tile triggers when an enemy enters any tile within dist2 2,
  which is exactly where a grabber must stand. It fires at the end of that robot's turn and sets move and action cooldown
  of every enemy within dist2 13 to 40, so they cannot move, strike, heal or pick up for about 4 turns. A dropped
  (non-start) flag returns after 4 end-of-round ticks (Gymhgy has no CAPTURING before r1800). So if the frozen carrier dies
  within about 2 rounds, its frozen receivers cannot re-pick before the flag returns. With nobody of ours there the freeze
  is wasted, which is why g2fstun (an unconditional flag-tile stun) failed against the mirror.

## 2. Mechanism

One switch, C.MASS_DENY (off by default), with four parts that all key on one shared "mass" record:

1. **Detect (Duck.sense).** After setup, a duck that sees at least MASS_MIN (8) enemies takes their centroid. If the
   centroid lies within MASS_R2 (100) of the home of a live flag that nobody is carrying, the duck writes that flag, the
   centroid and the count into slots 62-63. The biggest fresh mass keeps the record. MASS_FRESH (8) is how many rounds a
   record stays actionable. A group of 12+ within dist2 20 of a flag puts 8+ in the view of any duck beside it, while a
   front skirmish 15 tiles out does not count.
2. **Respawn (Duck.trySpawn).** A non-defender respawn with no carrier to chase spawns at the zone tile nearest the massed
   flag's home. Order: carrier, then mass, then alert, then field target. Jailed ducks run code every round (RULES.md), so
   the respawn stream goes to the threatened flag as soon as the first duck sees the mass, before the alert fires.
3. **Muster (Duck.fieldTarget).** A non-defender field duck that is not fighting, stands on our half (Micro.ourHalf), and is
   within MASS_RECALL_R2 (400, 20 tiles) of the massed home walks to the muster point. The muster point is the home plus
   two steps toward the mass centroid: between the group and the flag, inside the flag stun's dist2 13. Ducks already
   escorting our own carrier or going to a visible loose enemy flag keep doing that. Ducks on its half (the raiders) are
   never recalled.
4. **Flag stun (Duck.turn, beside carrierStun).** A duck within dist2 2 of the massed home, with action ready and 100
   crumbs, builds a stun **on the home tile** if none is there. canBuild already refuses when an enemy is adjacent or our
   trap is already on the tile. The build costs 5 cooldown, so the duck can still strike that turn, as with
   C.CARRIER_STUN. The stun is built only while a mass is approaching, so it waits for the group and is not spent on a
   lone scout's grab.

How these thin or block the group. The muster and the respawn stream make the fight at the flag happen at parity or
better while the group gathers: kills before the grab send its robots to jail for 25 rounds and a walk back from its
spawns, so the group at the grab shrinks. When the group arrives, the stun freezes the robots around the flag for 4 turns
with our muster in reach. Carriers are struck first (Micro.bestTarget). A frozen carrier dies with its healers frozen, or
a freeze that a non-grabber triggers blocks every pickup for 4 rounds.

Doses (all off or neutral in v1; picked only after the 5(a)):
- MASS_R2 64 / **100** / 196: precision against lead time.
- MASS_RECALL_R2 0 (respawn and stun only) / 225 / **400** / 3600 (all of our half).
- MASS_MIN 6 / **8** / 10.
- MASS_FIGHT_GOAL **false** / true: a muster duck in the fight branch, within MASS_RECALL_R2 and more than dist2 8 from the
  massed home, calls Micro.fight(enemies, allies, home), so the kite branch does not pull it away from the flag. This is
  the lens-2 F5 pattern; switch it on only if the 5(a) shows musterers kited off.
- MASS_HOLD **0** / 200: crumbs that ducks outside the recall radius keep back from combat and float traps while a
  warning is live, so the flag stun and ring can be paid.

## 3. Code plan (src/bot; every change behind C.MASS_DENY, so g_iter4 play is byte-identical with the switch off)

**C.java**
```java
public static final boolean MASS_DENY = false;   // Gymhgy study angle B: 8+ enemies near a flag muster ducks and respawns there
                                                 // before the grab, and a stun goes on the flag tile; arm g4deny
public static final int MASS_MIN = 8, MASS_R2 = 100, MASS_FRESH = 8, MASS_RECALL_R2 = 400, MASS_HOLD = 0;
public static final boolean MASS_FLAG_STUN = true, MASS_FIGHT_GOAL = false;
```

**Comms.java**: slots 62-63 (the only spare ones; 23-48 are the rewrite's reserved range, which BotTest pins).
- `MASS_A = 62`: [10..0] round of the latest mass sighting, [12..11] our flag index. `MASS_B = 63`: [11..0]
  enc(centroid) (60x60 maximum 3,836 < 4,096), [15..12] enemies counted, capped at 15. Both stay at or below 65,535.
- `reportMass(int i, MapLocation c, int n)`: read A and B. Return without writing if the record is fresh
  (age <= MASS_FRESH) for another flag with a larger count, or if it was already written this round for the same flag
  with a count at least as large. Otherwise write A = round | i << 11 and B = enc(c) | min(15, n) << 12.
- `massFlag()`: i if A != 0 and round - (A & 2047) <= MASS_FRESH and flag i is not in lostMask, else -1.
  `massAt()`: dec(B & 4095).
- Update the slot header comment.

**Duck.java**
- `sense()`, after the alert block (lines 179-189), before the REG_FIX line (190):
  `if (C.MASS_DENY && G.round > C.SETUP_ROUNDS && enemies.length >= C.MASS_MIN) massSense();`
- `massSense()`:
  1. Pre-check: skip if slot 62 was written this round and enemies.length <= its count. That bounds the loop to a few
     ducks a round in a melee.
  2. Centroid: sum x and y over `enemies`.
  3. Nearest live flag home within MASS_R2. Skip lost flags (Comms.lostMask) and flags with a fresh carry sighting
     (Comms.carried(i, CARRY_FRESH) != null); the chase owns a grabbed flag.
  4. Write with Comms.reportMass. Count massSightings.
- `turn()`, after the carrierStun line (line 46): `if (C.MASS_DENY && C.MASS_FLAG_STUN) massFlagStun();`.
  `massFlagStun()` builds when all of these hold: action ready, crumbs >= STUN.buildCost (MASS_HOLD never applies here: this is
  the use it saves for), m = massFlag() >= 0, G.me within dist2 2 of flagHome(m), and canBuild(STUN, home). It then calls
  rc.build(STUN, home) and counts massStuns.
- `trySpawn()`, non-defender branch (line 211-212): if ch == null and massFlag() m >= 0, want = flagHome(m) and
  massSpawns++. Otherwise leave the line unchanged.
- `fieldTarget()`, after "escort a friendly carrier we can see" (line 510), before ESCORT_FAR_R2 (line 511):
  `if (C.MASS_DENY && !isDefender()) { MapLocation mt = musterTarget(); if (mt != null) { G.note = "muster"; musterTurns++; return mt; } }`
- `musterTarget()`:
  1. Null if massFlag() < 0, if G.me is beyond MASS_RECALL_R2 of the home, or if it is not Micro.ourHalf(G.me).
  2. With c = massAt(): return the home if c is null or within dist2 8 of the home.
  3. Otherwise p = home.add(d).add(d) with d = home.directionTo(c). Return p if it is on the map, else the home.
- MASS_FIGHT_GOAL (dose): in the fight branch, before the plain Micro.fight at line 72, the same muster test with
  Micro.fight(enemies, allies, home).
- MASS_HOLD (dose): in placeCombatTrap's crumb test (line 967) and before spendFloat (line 98), add MASS_HOLD when a
  warning is live and G.me is beyond MASS_RECALL_R2 of the massed home.

**G.endTurn**: put `" mw" + Duck.massSightings + "/" + Duck.musterTurns + "/" + Duck.massStuns` right after `" x" + exceptions`.
The note plus o, x and mw stay within the first 64 characters (MEAS5).

**tools/arm-intent.txt**: `g4deny C.MASS_DENY=true`.

**Unit tests (rule 6)**
- BotTest slot layout: add {Comms.MASS_A, 2} to the disjoint-ranges check.
- reportMass precedence: a stale record is overwritten; a fresh record for another flag with a bigger count is kept; a
  later round for the same flag overwrites; the bit fields round-trip; every value is in 0..65535.
- massSense: skips with fewer than MASS_MIN enemies, with the centroid beyond MASS_R2, for a lost flag, and for a carried
  flag.
- musterTarget: null when stale, beyond the radius or on its half; home + 2 toward the centroid; home when the point is
  off the map.
- Switch off: a full fake turn writes neither slot 62 nor 63.
- Dead-code check and arm-intent check.

**Census (tools/replaydump/ReplayDump.java, --capabilities; appended columns; header comment updated)**
- `massWarns`: post-setup rounds in which this team's slot 62 round field equals the round, read from the stored
  CommTable as alertWrites is. Blank without a CommTable. Meaningful for our builds only (fire column).
- `massGuard64`, **the own signature**: over post-setup flag-rounds where one of this team's flags lies at its home with
  8+ robots of the other team within dist2 100 of that home (end of round, true positions), the mean number of this
  team's robots within dist2 64 of that home. Blank without such flag-rounds. It is computed from positions, so g_iter4
  gets a value too, and the rel: pairing works.
- `enemyAtGrab20`, **the deny read**: the mean, over the other team's first grabs of this team's flags (pickup at home),
  of the grabbing team's robots within dist2 20 of the flag, grabber included. Count it beside n20 at the PICKUP_FLAG trip
  start (line 538), where defNearAtGrab20 is counted. Blank without grabs.
- `bigGrabs12`: those first grabs with 12 or more (per game; lens 2 baseline about 2.0).
- `massLead`: diagnostic. The mean, over the other team's first grabs, of the rounds from the first slot-62 write of the
  warning run live at the grab to the grab. A run is writes with gaps of 8 rounds or less; it counts if its last write is
  within 8 rounds before the grab and its slot-63 centroid lies within dist2 196 of the grabbed flag's home. Blank if no
  grab had one.

## 4. Bytecode budget (25,000 per turn; fight turns already peak near 22k)

| part | when it runs | cost |
|---|---|---|
| massSense pre-check | turns with 8+ enemies in view | 1 shared read, ~20 bytecodes |
| massSense full | the first few ducks a round that see a new or bigger mass | centroid ~15 per enemy (300 for 20), 3 flags x ~40, reportMass ~50; at most ~500 |
| massFlagStun | every post-setup turn | 1 read when no warning (~25); ~80 with the distance and canBuild test; build only near the home |
| trySpawn | jailed robots only | 2 reads (~30) |
| musterTarget | non-fight turns only | 2 reads, flagHome, Micro.ourHalf with Sym.enemyCenters (~250) |

Worst case added on a fight turn is about 550, against about 3,000 of headroom. The 5(a) reads maxBcK and the near-miss
turns, and overruns must stay 0. If a fight turn ever nears the limit, gate massSense on `G.bcLeft() > 6000`. It runs
early in the turn, so this would almost never bite.

## 5. Signature and delivery gate (pre-registered)

**Own signature: `massGuard64`.** It measures the behaviour the code produces: our bodies at a flag while a group masses
on it, before the grab. g_iter4's baseline is unknown. The 5(a) reads it by re-censusing g_iter4's replays on the same
cells, and the gate takes it from its own BASE run (synthesis section 3.9).

**Deny read: `enemyAtGrab20`, gated beside it**, because the angle's claim is denial and the critic's main warning is that
contact rises while nothing else does. It is a mean over about 9 grabs a game, so it has far more power than a 12+
indicator. Lens 2 bins suggest a mean around 7; the BASE run fixes it.

Delivery mini-block, pre-registered (random Gymhgy maps and sides, the gate's own BASE run on the same cells):

```
DGPOOL=Gymhgy.v10official DGTAG=-gymdeny BASE=g_iter4 tools/delivery-gate.sh g4deny \
  'fire:massWarns>0>=0.9 rel:massGuard64>=1.3 rel:enemyAtGrab20<=0.85 nw:captured>=0.9 nw:enemyCaptured<=1.1 nw:kills>=0.95 mean:overruns<=0'
```

- `fire`: the warning fires in at least 90% of games.
- `massGuard64`: our presence at a massed flag rises at least 30%.
- `enemyAtGrab20`: the grab group shrinks at least 15%.
- `captured`: the price on our offense, since recalled ducks stop raiding. It is the lesson of d2alert ("bodies pulled
  home cost more than they save").
- `enemyCaptured` and `kills` are the usual guards.

**Reads, not bars:** bigGrabs12, massLead, chasers20, enemyFirstGrabs, enemyCaptured600, trickleDeaths (respawns into the
mass), enemyStunVictims (musterers walking into its stuns), inEnemy300 (offense position), stunTrig.

**Falsifiers**
- massGuard64 passes but enemyAtGrab20 and bigGrabs12 stay flat at 96 cells. Bodies arrive but do not thin the group.
  Lens 2 F3 then holds before the grab too, and angle B closes for Gymhgy.
- Median massLead under 5 rounds. The warning is too late to muster from 20 tiles. Allow one retry at MASS_R2 196, then
  close.
- captured falls more than 10% with enemyCaptured flat. The recall costs more than it saves. Try MASS_RECALL_R2 225 once,
  then close.

## 6. 5(a) diagnostic (rule 5: the mechanism must be seen firing before any gate)

Use chosen cells against Gymhgy (PROMPTS 178), with g_iter4 on the same cells and seeds. Seeds 781001/781002 reuse
g_iter4's existing g4crumb-diagnostic games on GaltonBoard A/B and KingQuacksCastle A, if their replays are still on the
VM. The remaining cells need g_iter4 played. That gives 11 cells x 2 seeds = 22 pairs, 5 on side A and 6 on side B.

| cell (tools/diag-batch.sh g4deny:Gymhgy.v10official:MAP:SEED:SIDE) | why |
|---|---|
| Canals A, Canals B | traced: the corner-flag mass with ~20 of ours idle 20 tiles away; the far flags decide |
| KingQuacksCastle A | 0/7, 3.0 Gymhgy captures a game, 2.86 unopposed |
| GaltonBoard A, GaltonBoard B | 0/12, weak flag lost by r600 in 12/12; open centre where 12+ convoys can form (critic alt 4) |
| Joker B, Snake B, Foxes B, DefaultHuge B | 0/5, 0/5, 0/9, 0/4 |
| Puzzle A | 1/8 |
| Valentine A | the longest chains (~12 hand-offs), dense convoy |

What to read, per game against its g_iter4 twin:
1. Fire: `--logs muster` and the mw counters (sightings, muster turns, flag stuns). Flag stuns built and triggered
   (`--trapgeo` rows on home tiles).
2. Lead time: massLead per first grab. This is the number the study could not measure. A median of 10 rounds or more is
   needed for the muster to matter. Also precision: the share of warning runs followed by a first grab of that flag
   within 40 rounds.
3. massGuard64 against the twin, and enemyAtGrab20 and bigGrabs12.
4. At grabs where a flag stun fired on the grab round: was the carrier killed within 4 rounds, and did the flag go home
   (a RETURN chain)?
5. Price: our captures and inEnemy300, our kills and deaths, trickleDeaths, enemyStunVictims.
6. Canals corner flag (both sides): do the idle ducks at the centre home muster before the grab, and how many of ours
   stand within dist2 64 at the grab, against g_iter4's 6?
7. Basics: overruns 0, exceptions 0, maxBcK, symWrong 0.

## 7. Expected effect

- Behaviour: massGuard64 +40-70% if the lead time is 10 rounds or more (the 16.4 idle ducks on our half plus the respawn
  stream). Only the respawn and stun parts work if it is shorter.
- Deny: enemyAtGrab20 -10 to -20%; bigGrabs12 from about 2.0 to 1.4-1.6 a game.
- Outcome: if a third of 12+ grabs fall to 6-11 (0.48 to about 0.26) and the flag stun turns some big grabs into RETURNs,
  its captures drop 0.15-0.3 a game from about 2.0. Games with no Gymhgy capture by r600 win 56% vs 42% (lens 4). That is
  about +1 to +4 points against Gymhgy, provided our own captures fall by less than 10%.
- Probability of delivery: below even. The Gymhgy defense area has two failed deliveries (g4z1 twice), the presence
  predecessors moved little, and the 12+ finding may hold before the grab as well. The 5(a) lead-time read is the cheap
  early exit.

## 8. Why this is not a repeat

| predecessor | what it did | difference here |
|---|---|---|
| g1icpt / INTERCEPT, g4pred / CARRY_PREDICT | post-grab: chase a fresh or predicted carrier sighting | acts before the grab, while the flag is at home; needs no carrier sighting |
| g1icamp, g3camp, g3camp2 / DEST_CAMP | post-grab: wait at its spawn | defends the flag itself, before the chain exists |
| g4alert / ALERT_NEAREST | which of several alerts to answer | keys on group size, which no arm has used |
| g4z1 / Z1HOLD, g4z2 / Z2ESCORT | post-grab: our dropped flag, escort-first targeting | pre-grab |
| g3tether / DEF_TETHER | ties the one defender to its flag | brings the field army and the respawns to the flag; the defender is unchanged |
| d2alert, g2alert400 (ALERT_RADIUS2 400) | wider recall for every alert. Rejected 12-31 vs the rush partner; g2alert400 delivered presence (+51% chasers vs the mirror) but missed the first-grab bar (4.88 vs bar 4.71), and did nothing vs waffle | recall only for a group of 8+ near the flag (the 0.30-0.48 conversion band), never for 1-2 raiders (0.04); only ducks on our half; muster between group and flag, not at the threat robot; plus respawn routing and the flag stun; against the opponent whose 12+ convoy is documented |
| g2fstun / FLAG_TILE_STUN | an unconditional flag-tile stun, rebuilt; FAIL vs the mirror (carrier kills 11.5 vs 12.4) | built only while a mass approaches, with a muster in reach to kill the frozen carrier before its frozen receivers can re-pick inside the 4-round return window |
| g4dam / DAM_FIRST (delivered) | setup budget to the dam line, no setup rings | complements it: the flag stun is the ring's one tile that matters, paid only when a group comes |

## 9. Risks

1. **Contact may not matter before the grab either** (lens 2 F3: 12+ convert 0.42 with no ours near and 0.59 with 10+).
   Gymhgy gets 25-30% more kills per attack, heals more, and screens with stuns, so even fights at the flag are still
   lost. The enemyAtGrab20 bar is there to catch this.
2. **The lead time may be short.** If the group comes into our view only a few rounds before the grab, the muster
   arrives after it and the arm shrinks to respawns, the stun, and an earlier chase.
3. **False alarms and the offense price.** Gymhgy fights in our half (about 23-25 of its robots there at r400), so
   warnings near flags can hold our army home and cut the r400 presence in its half (51% vs 40% win predictor).
   Mitigations: only ducks on our half are recalled, the warning lasts 8 rounds, and the nw:captured guard and the
   inEnemy300 read watch the price.
4. **Respawning into a mass** that sits on a spawn-zone flag adds trickle deaths. Lens 5 R7, though, says zone flags fall
   less because respawns land beside them.
5. **Its stun screen** catches musterers on the way in. Read enemyStunVictims; STUN_WARY is a separate arm.
6. **A scout spends the flag stun** before the group arrives. The cost is 100 crumbs. The duck rebuilds it while the
   warning is live and no enemy is adjacent to the home.
7. **One record for two masses.** The bigger one wins; the other flag keeps g_iter4's alert response.
8. **Bytecode.** Bounded at about 550 on a fight turn (section 4). Overruns void the arm.
9. **Base drift.** The arm is built on g_iter4 (rule 16). If g4gym1 is promoted, re-run the 5(a) on it. DAM_FIRST
   removes the setup rings, so the flag stun matters more there.
