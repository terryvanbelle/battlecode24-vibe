package g7kiterc;

/** Every tunable constant, each with the measurement (or reason) that set it. */
public strictfp class C {
    public static final boolean DEBUG = false;          // stack traces to stdout; off in all gated games
    public static final int NEAR_MISS_BC = 22500;       // 90% of the 25,000 limit (TRAINING_ALGORITHM hyperparameters)
    public static final int SETUP_ROUNDS = 200;         // RULES.md, engine GameConstants.SETUP_ROUNDS
    public static final int GATHER_ROUND = 150;         // setup: stop crumb hunting and move to the dam front (iteration 0 guess)
    public static final int DEFENDERS_PER_FLAG = 1;     // iteration 0 guess
    public static final int HEAL_HP_BELOW = 1000;       // heal anyone missing HP when not attacking
    public static final int RETREAT_HP = 300;           // iteration 0 guess: below this HP back off when threatened
    public static final int FILL_RESERVE = 0;           // crumbs kept back when filling water to get through
    public static final int TRAP_RESERVE = 200;         // crumbs kept back when placing combat stun traps
    public static final int DEF_TRAP_RESERVE = 100;     // defenders keep this many crumbs after a flag-ring trap
    public static final int RING_POST_RESERVE = 100;    // after setup, the ring reserve (andli28 study lever 5: 53% of our post-setup stuns are
                                                        // ring rebuilds, 2.3 victims each after 47-158 rounds vs a field stun's 5.5 after 2-48;
                                                        // above placeCombatTrap's 300 gate the field stuns get first claim); 100 = g_iter7, arm g7ring 300
    public static final int DAM_TRAP_ROUND = 185;       // setup: start trapping the dam front (dam opens after r200)
    public static final int DAM_TRAP_RESERVE = 600;     // keep this bank for the fight when trapping the dam
    public static final int ADVANCE_MARGIN = 3;         // advance into a held line when allies+1 >= enemies+3 in vision (iteration 1 guess)
    public static final int FLOAT_CRUMBS = 1500;        // above this bank an idle duck spends on traps (self-play floated 13k by r1500)
    public static final int CARRY_FRESH = 5;            // rounds a carrier sighting stays actionable
    public static final int CHASE_RADIUS2 = 225;        // chase a carrier within dist 15; farther ducks intercept at its destination
    public static final boolean RELOCATE_FLAGS = true;   // T2: carry flags in setup to far spots (iteration 2; on with RELOC_V2 in g_iter3)
    public static final boolean RELOC_V2 = true;        // waffle crack: spot far from the NEAREST enemy spawn under every live symmetry (g_iter3)
    public static final int RELOC_R2 = 225;             // RELOC_V2: a flag's spot is within 15 tiles of its spawn centre
    public static final int RELOC_DECIDE = 40;          // RELOC_V2: wait this long for observed symmetry before choosing spots
    public static final int RELOC_DEADLINE = 170;       // drop wherever legal from this round (dam opens after r200)
    public static final int RELOC_STALL = 12;           // turns without progress before giving up the spot (V2: walk to the best tile reached, else home)
    public static final boolean RELOC_STALL_MOVES = false;  // audit BOT3(b): count only movement-ready turns; give up to the best tile reached; arm g4reach
    public static final boolean RELOC_CLIMB = true;     // audit BOT3(a): the carrier climbs over visible passable tiles away from the enemy
                                                        // spawns (no fixed spot that may be unreachable) and drops at a local maximum; arm
                                                        // g5climb2, the incumbent g_iter6 since 2026-10-06
    public static final boolean SPAWN_SAFE = false;     // upper-tier micro study: 59% of our spawns end beside an enemy (theirs 24%), 17% of our
                                                        // deaths come within 10 rounds of a spawn (theirs 2%); respawn away from a zone with
                                                        // SAFE_MIN+ enemies seen near it in the last 2 rounds when another zone has <= 1; arm g7spawn
    public static final int SAFE_MIN = 3;
    public static final boolean HEAL_HOLD = true;       // 2026-10-06 replay study: with an enemy within HOLD_R2 keep the action for a strike
                                                        // (no heal unless the target carries a flag); the upper tier heals under threat 25%
                                                        // of the time to our 45% and holds a ready strike 0.34 to our 0.11; arm g6heal, the
                                                        // incumbent g_iter7 since 2026-10-06
    public static final int HOLD_R2 = 10;
    // C.ENGAGE_HP (andli28 loss study 2026-10-06; arm g7ehp). We die 873 times a game to andli28's 506, and 262 of our deaths
    // come before the robot's next turn after it stepped in to strike. A step-in onto a tile where the enemies able to reach
    // it next turn (within dist2 10) deal our HP or more dies before the next turn 12.8% of the time (1,920 a game), onto any
    // other tile 0.1%. Below ENGAGE_HP, in a plain fight (no goal), a robot refuses an engage or advance tile whose summed
    // enemy hits (each enemy's own hit: their ATTACK upgrade and its attack level) reach its HP, unless an enemy in reach of
    // the tile dies to our strike; the tile scores as kite/hold. RETREAT_HP, strike-first, the carrier and loose-flag branches
    // and HEAL_HOLD are unchanged. 0 = off (g_iter7); arm 700, dose 1000.
    public static final int ENGAGE_HP = 0;
    public static final boolean EHP_HOLD = false;       // review amendment 7 (research/andli28-study-2026-10-06): a robot ENGAGE_HP refused this
    public static final int EHP_HOLD_R2 = 20;           // turn keeps its action (no heal unless the target carries a flag) while an enemy is within
                                                        // EHP_HOLD_R2, so it holds the strike the refusal was for; arm g7ehp2
    // C.KITE_REACH_W (upper-tier study 2026-10-07, research/upper-tier-study-2026-10-07 lever 1; arm g7kite). The kite/hold score
    // counts threats out to dist2 10, and from a tile within dist2 4 of an enemy every adjacent tile is still within dist2 10 of
    // it, so stepping out of reach earns nothing: adjacency, crumbs and the +1 for staying pick the tile. After a strike we stay
    // in reach 0.252 of the time against 0.111 for the upper bots. In the kite/hold branch of a plain fight (goal null, no enemy
    // carrier in view), a robot that cannot strike this turn or is hurt (RETREAT_HP), at HP below KITE_REACH_HP, scores
    // -KITE_REACH_W per enemy within dist2 4 of the tile, counting at most KITE_REACH_CAP of them: it leaves reach whenever a tile
    // out of reach adds no threat (300 beats adjacency 80 + band 50 + crumbs 40 + stay 1) and never pays a threat for it
    // (2 x 300 + 80 + 40 + 1 < 1000; uncapped, 4 enemies in reach cost 1200 and bought an exit with one more threat). The
    // randomized tie-break study: leaving wins below 700 HP, staying earns strikes above. Engage, advance, loose-flag and rush
    // tiles are untouched, and so is every tile while an enemy carrier is in view (review 2026-10-07: a hurt robot skips the
    // carrier branch, so without that gate it kited away from the carrier). 0 = off: play-identical to g_iter7, not
    // byte-identical (the kr counter rides in the indicator string, built every turn, so --metrics max_bc runs above the twin's);
    // arm 300, dose 1200 (min(inRange, 2) x 1200 > 1000: pays one extra threat to leave reach).
    public static final int KITE_REACH_W = 300;
    public static final int KITE_REACH_HP = 700;
    public static final int KITE_REACH_CAP = 2;
    // C.RC_BAND (upper-tier study 2026-10-07 lever 2, as amended by the RC_BAND premise check of 2026-10-07 on the 720 g_iter7
    // control replays; arm g7kiterc = g7kite + RC_BAND). The kite/hold score charges 1000 for one enemy within dist2 10 and pays
    // 50 for dist2 11-20, so a robot whose strike is back next turn never waits at one-step range: it retreats to the band and
    // loses a turn. In a plain fight (goal null, no enemy carrier, no loose flag) a robot that was not action-ready at the start
    // of its turn but is ready next turn (cooldown < 20: a true recharge turn, never a strike turn), not hurt, at HP >= RC_HP,
    // with no enemy within dist2 4 of its start tile (the tie test: leaving reach at HP >= 700 costs strikes, so in-reach turns
    // stay g_iter7's), at most RC_MAX_E enemies and at least RC_MIN_A allies in vision (the enemy count, not the balance, cut both
    // the next-turn hit and the 10-20 round deaths in the quasi-experiment), scores a hold tile (exactly one enemy within dist2
    // 10, none within dist2 4) as a threat-free tile with RC_BAND in place of the band bonus. Every other tile keeps g_iter7's
    // score. 150 beats a band tile without a crumb whatever the adjacency (50 + 8 x 10 + 1 < 150), and one with a crumb unless
    // it has 6+ more adjacent allies; RC_BAND + 8 x 10 + crumb + stay stays below one threat (1000). RC_HP = KITE_REACH_HP, so the
    // two gates split on one HP read (KITE_REACH below, RC_BAND at or above). 0 = off: play-identical to g_iter7 (and src/bot with
    // KITE_REACH_W 300 to g7kite); the rc counter rides in the indicator string, so --metrics max_bc runs above the twin's.
    // RC_SUP (review 2026-10-07, RC_BAND amendments RC4): a hold tile earns the bonus only when at least RC_SUP of the allies this
    // robot sees stand within dist2 10 of the one enemy that threatens it. The vision counts above do not see local support: on
    // the 720 control replays (gated class, band tile blocked so g_iter7 holds, minus free), holds whose threat had 0-1 such
    // allies took a hit before the next turn +0.135 U / +0.126 R (2+: +0.015 / +0.016) and died within 20 rounds +0.026 / +0.020
    // (2+: -0.012 / +0.005) for the same own kills (+0.085 / +0.060 vs +0.078 / +0.071). It drops about a third of the changed
    // turns (U 245 of 700 a game, R 164 of 598). Allies within dist2 10 of the hold tile itself are 0-1 in only 1-2% of them.
    public static final int RC_BAND = 150;
    public static final int RC_HP = 700;
    public static final int RC_MAX_E = 2;
    public static final int RC_MIN_A = 4;
    public static final int RC_SUP = 2;
    public static final boolean TERR_MICRO = false;     // TACTICS T15 (kill reward: +30 only for a killer on enemy territory): in a fight,
                                                        // engage from enemy-territory tiles and give ground into our own territory last; arm g6terr
    public static final int TERR_ENGAGE = 60;           // TERR_MICRO: engage-tile bonus on enemy territory (adjacent allies count 10, a threat 100)
    public static final int TERR_HOLD = 40;             // TERR_MICRO: kite/hold penalty for a tile on our territory (the distance band counts 50)
    public static final int CLIMB_R2 = 400;             // RELOC_CLIMB: stay within 20 tiles of the flag's spawn centre (Gymhgy moves its ~18)
    public static final int CLIMB_SEP2 = 64;            // RELOC_CLIMB: 8+ tiles from our other flags (the engine resets all three under dist2 36)
    public static final boolean RELAY = false;          // T3 offence copy: hand the flag forward to an adjacent ally
    public static final boolean RELAY_THREAT = false;   // RELAY only for a carrier below RELAY_HP or with an enemy in view; arm g4relay2
    public static final int RELAY_HP = 600;
    public static final int ALERT_RADIUS2 = 100;        // ducks within this dist2 of an alerted flag go home to it (iteration 0 value)
    public static final boolean ALERT_FIX = true;       // audit A1: alert = an enemy within ALERT_THREAT_R2 of the flag home; defenders
                                                        // gate on their own flag; responders go to the threat, never onto the flag tile
    public static final int ALERT_THREAT_R2 = 20;
    public static final boolean REG_FIX = true;         // audit A5/A6: our dead carrier's flag is "dropped", not "carried by us" forever;
                                                        // a drop tile expires to the flag's home after the return window
    public static final boolean REACH_FIX = true;       // audit A4: an enemy takes the turn only if within dist2 8 or reachable in 3 moves
    public static final int REACH_BC = 8000;               // A4: the reachability search runs only with this many bytecodes left (else "engage")
    public static final int REACH_BC_STOP = 0;             // A4: the search gives up ("engage") once fewer bytecodes are left (0 = never; arm g2bc2: 13000)
    public static final boolean REACH_FAST = true;      // A4 search at a fraction of the bytecode, same answer (AuditTest compares); g2fast band: 0 of 234 cells differ, so folded into g_iter2 2026-10-03
    public static final boolean NAV_FIX = true;         // audit A7/A9: bug state survives small target moves; one edge flip per call
    public static final int SETUP_DIGS = 0;             // T4 copy dose: checkerboard digs per duck in setup (arm ladder 0/5/10)
    public static final int DIG_RESERVE = 1000;         // setup digging never takes the bank below this
    public static final int DIG_SITE = 0;               // T4 siting: 0 any, 1 wall-hugging (>=3 wall/off-map nbrs), 2 behind our spawn (away from the enemy)
    public static final boolean NO_FILL_OWN = false;   // T4: in setup, never fill a tile matching our dig signature (even, DIG_SITE 2) unless stalled
    public static final boolean REGRAB = false;        // C9/C10: in a fight, a visible enemy flag on the ground within REGRAB_R2 is the movement goal; pick it up before striking
    public static final int REGRAB_R2 = 13;
    public static final boolean REGRAB_HALF = false;   // REGRAB only for loose flags nearer our spawn centres than theirs (a carrier there has a chance)
    public static final boolean CARRY_SAFE = false;    // C9: with enemies in view, the carrier takes the homeward step with the fewest enemies able to reach it
    public static final boolean CARRIER_HEAL = false;  // C9: a hurt allied carrier in heal range is healed before anyone else
    public static final boolean INTERCEPT = false;     // C10a: in a fight, a fresh carrier alert within INTERCEPT_R2 turns the fight goal-directed toward the carrier
    public static final int INTERCEPT_R2 = 225;
    public static final boolean FLAG_LOST = true;       // audit 2026-10-03 BOT1: recognise our captured flags (no alerts, defenders re-home); g_iter4 (2026-10-04)
    public static final int LOST_AFTER = 60;            // FLAG_LOST: rounds a home in view without its flag before the flag counts as gone
    public static final boolean STUN_FRONT = false;     // combat stuns one enemy step from triggering, most enemies in the stun radius (Cyril's geometry); arm g4front
    public static final int FRONT_MIN_VICTIMS = 3, FRONT_RESERVE = 100;   // STUN_FRONT: enemies within dist2 13 of the tile; crumbs kept
    // C.BUILDERS (2026-10-04, Cyril economy: after setup it pays ~54 crumbs a stun to our ~77; build level L cuts trap cost
    // 10/15/20/30/40/50% (engine SkillType) and needs 5L build actions, but level 4 in attack or heal caps build at 3):
    // one field duck in ten builds every combat stun (front placement), digs toward level 6 in setup, and stops attacking
    // and healing before either reaches level 4. Arm g4builder.
    public static final boolean DAM_FIRST = false;      // setup budget to the dam line: no flag rings in setup, dam traps from DAM_TRAP_ROUND; arm g4dam
    public static final int DAM_FIRST_RESERVE = 300;    // DAM_FIRST: crumbs kept for the first fight after the dam
    public static final boolean LEVEL_FARM = false;     // late-game build-XP farm for the level-sum tiebreak (Gymhgy study); arm g4farm
    // g4farm (FARM_XP 15, reserve 300) fired 0-11 digs a game: the late bank sits at 160-260 crumbs (traps spend the
    // rest). g4farm2: the cheapest levels first (level 1 = 5 build actions, ~50 levels for ~250 digs, the passive income
    // of r1500-2000) and no reserve.
    public static final int FARM_ROUND = 1500, FARM_XP = 5, FARM_RESERVE = 0;
    // C.LATE_BANK (TACTICS T17, andli28's end-game level dump; arm g7bank). 59 of g_iter7's 145 losses to andli28 are level-sum
    // losses (median deficit 41). In those games andli28 holds ~1,900 crumbs at r1900 and digs only from r1901 (89 -> 195 digs),
    // while we hold ~250 and never dig after setup. While the flag counts are level (Duck.capturesLevel; needs C.FLAG_LOST):
    // after BANK_R1, builds that would sit and wait (re-arms of a ring holding RING_KEEP+ of our traps, combat stuns with no
    // enemy within BANK_CLOSE_R2, float stuns) need BANK_CAP more crumbs. Close stuns build above the rising bankLine or once
    // per BANK_GAP rounds team-wide (slot Comms.LB_PACE). Alerted flag fights, carrier stuns, thin rings and fills are unchanged.
    // After BANK_R2, a robot with a spare action and no enemy within DIG_HOLD_R2 digs a checkerboard tile toward its next build
    // level: build <= 3, only a level the jail penalty never takes and can finish by r2000, and whole levels committed
    // team-wide (a robot starts only when the bank not owed to the levels in progress, Comms.LB_OWED, covers its level plus
    // DIG_KEEP; it owes the rest until it finishes or stops). A combat stun aimed at water goes beside it. Never combine with
    // LEVEL_FARM, STUN_FRONT or BUILDERS (their branches skip the gate).
    public static final boolean LATE_BANK = false;
    public static final int BANK_R1 = 1400, BANK_R2 = 1900, BANK_CAP = 2000, BANK_PACE100 = 400;
    public static final int BANK_CLOSE_R2 = 8, RING_KEEP = 8, BANK_GAP = 20;
    public static final int DIG_XP_MAX = 15, DIG_KEEP = 300, DIG_ALLIN = 1990, DIG_KEEP_END = 100, DIG_HOLD_R2 = 20, DIG_BC = 3000;
    // C.FINAL_COMPLETE (andli28 loss study 2026-10-06, research/andli28-study-2026-10-06; arm g7fc). 410 of 1,520 games vs
    // andli28 are level-sum losses (median deficit 42). At r1950, 3.9/9.5/16.9/26.2 of our spawned robots are within 1/2/3/4
    // digs of their next build level, and 77.6 of our 241.5 build XP is still stranded in partial levels at r2000.
    // From FC_ROUND, while the flag counts are level (Duck.capturesLevel), robots finish those partial levels by digging,
    // the closest first: a robot at most K digs from its next build level (K = 1 from FC_ROUND, 2 from FC_K2, 3 from FC_K3,
    // 4 from FC_K4) that can still finish it by r2000 digs a dump tile. It starts a level only when the bank not owed to the
    // levels in progress (Comms.LB_OWED) covers the rest of the level plus FC_FLOOR (a carrier stun stays payable; an alert
    // stun needs 100 + TRAP_RESERVE) plus FC_KEEP[digs still needed]. A robot one dig away digs before its fight (it spends a
    // strike); the others dig only with a spare action after the turn (RobotPlayer), so no heal is replaced, and with an enemy
    // within HOLD_R2 only a dig that leaves the action ready next turn. Near an alerted home (Duck.guardFight) nobody digs.
    // placeCombatTrap (except at an alerted home) and spendFloat pause; rings, carrier stuns and fills stay. No bank (g7bank's
    // never formed). Reuses LATE_BANK's dig arithmetic, jail-penalty test, dump tile and owing; never combine with LATE_BANK
    // (they share LB_OWED).
    public static final boolean FINAL_COMPLETE = false;
    public static final int FC_ROUND = 1950, FC_K2 = 1960, FC_K3 = 1975, FC_K4 = 1985, FC_FLOOR = 100;
    public static final int[] FC_KEEP = {0, 0, 20, 60, 100};   // by digs still needed (index 0 unused); simulation: about 0.58
                                                                // levels per dig at a median 436-crumb budget
    public static final boolean BUILDERS = false;
    public static final int BUILDER_DIGS = 30, BUILDER_DIG_RESERVE = 1000, BUILDER_XP_ATK = 74, BUILDER_XP_HEAL = 99;
    // g4builder (reserve 300, non-builders never built combat stuns): setup digs took the bank (Joker 662 at r200 vs 3,857),
    // setup traps fell (14 vs 24, 31 vs 48) and three games ended in early captures. g4builder2: the default dig reserve,
    // and a non-builder keeps the old combat stun when no builder (an ally at build level 3+) is in view.
    public static final int BUILDER_SEEN_LEVEL = 3;
    public static final boolean CRUMB_STEP = true;     // audit BOT4: fight steps prefer a tile with crumbs (Cyril collects 3,572 in r201-400 to our 838); arm g4crumb
    public static final int CRUMB_BONUS = 40;           // CRUMB_STEP: score bonus (engage tiles differ by 100 per threat, kite tiles by 1000)
    public static final boolean STUN_WARY = false;      // T14 neutralization: among reaching tiles, avoid one that may sit beside a fresh enemy stun; arm g4wary2
    public static final int WARY_ROUNDS = 8, WARY_COST = 60;    // STUN_WARY: how long an enemy-adjacent tile stays suspect; penalty (< one threat, 100)
    public static final boolean INIT_FAST = false;      // round 1: spawn centres from a bitset (~2k bytecodes, not ~18k); arm g4init
    public static final boolean ALERT_NEAREST = false;  // audit BOT9: field ducks answer the nearest live fresh alert; respawns split over alerts; arm g4alert
    public static final boolean RELOC_SPREAD = false;   // audit BOT16: a relocation spot scan pauses at 4000 bytecodes left and resumes next turn; arm g4spread
    public static final boolean FILL_STEP = false;      // audit BOT8: step onto the tile just filled in the same turn; arm g4fill
    public static final boolean CARRY_PREDICT = false;  // audit BOT10: chase a stale carrier sighting at its predicted point (age < 60); arm g4pred
    public static final int PREDICT_MARGIN = 5;         // CARRY_PREDICT: rounds of slack past the predicted arrival
    // C.CONTACT (convoy synthesis 2026-10-05; arm g4contact): our flag's latest off-home sighting is kept as a short predicted
    // track in its own slots (Comms.CT, CT_AUX, CT_DIVE; its round is OF_SEEN, so it needs C.FLAG_LOST). In a fight, a capped
    // number of ducks dive at the predicted flag (Micro.dive); out of a fight it is the chase point (carrierTarget). Only for
    // observed grab groups under CT_GROUP_MAX: contact changes conversion for groups 3-11 (0.34-0.56 vs 0.18-0.35), not for
    // 12+ (0.57 vs 0.54; critic R5). g1icpt's goal lived only in the kite branch; g4pred chased out of fights only.
    public static final boolean CONTACT = false;
    public static final int CT_HOLD = 12;               // CONTACT: rounds a track stays live after the last sighting (12 x 0.56 ~ 7 tiles
                                                        // of predicted travel; g4pred's 60 had a median error of ~24 tiles)
    public static final int CT_GROUP_MAX = 12;          // no dive and no chase for an observed group of this many or more (R5 above)
    public static final int CT_DIVE_R2 = 100;           // dive only for a predicted flag within 10 tiles: the screened band, ours at
                                                        // dist2 21-100 on 44% of capture-chain rounds
    public static final int CT_SPEED16 = 9;             // predicted carrier travel 9/16 = 0.56 tiles a round (lens 2 F2: 0.56)
    public static final int CT_LEAD = 2;                // goal this many steps past the predicted point toward its spawn: lead pursuit
                                                        // at 1 tile a round against 0.56, off the trail where its rear-guard stuns sit
    public static final int CT_EDGE = 2;                // divers per chain a round: en20 + CT_EDGE - (age <= 1 ? ou20 : 0); <= 0 means
                                                        // fresh contact already has the numbers
    public static final int CT_MISS = 2, CT_MISS_R2 = 9;   // this many negative sightings (predicted point within CT_MISS_R2 of a robot
                                                        // that does not see the flag, age >= 1) kill a track until the next sighting
    public static final int CT_BC = 6000;               // the dive is chosen only with this many bytecodes left (else g_iter4's fight)
    public static final int CT_SENSE_BC = 15000;        // the sensor skips its ally count below this
    public static final int CT_STEP = 300, CT_THREAT = 250, CT_HIT = 120;   // dive score (g4meet's objective weights, REWRITE_DESIGN
                                                        // 2.7): a tile gained through one extra threat scores +50 and is taken, through
                                                        // two -200 and is not, at every distance; dose ladder CT_THREAT 250, then 140
    public static final boolean STUN_AHEAD = false;     // audit BOT7: build a carrier stun only from ahead of the carrier; arm g4ahead
    public static final boolean PICKUP_AFTER_MOVE = true;  // audit BOT5: pick up a loose enemy flag after the step, before striking; arm g4pick
    public static final boolean DEF_TETHER = false;     // audit BOT2: defenders fight within TETHER_R2 of their home; arm g3tether
    public static final int TETHER_R2 = 20;
    public static final boolean CAMP_SPLIT = false;     // DEST_CAMP: split campers over the enemy spawns within 1.3x the nearest distance (arm g3camp2)
    public static final boolean DEST_CAMP = false;     // C10a: after a carrier slips out of sight, ducks that can beat it there wait at its destination spawn
    public static final boolean ESCORT_CARRIER = false; // C9: in a fight, a visible own carrier within ESCORT_R2 makes its next homeward tile the fight goal
    public static final int ESCORT_R2 = 20;
    public static final boolean HOLD_LINE = false;     // structural: the army holds front points (midpoints spawn centre - its mirror) instead of marching on enemy flags
    public static final int HOLD_UNTIL = 2001;            // round after which the army marches on flags again
    public static final int HOLD_DEPTH_TENTHS = 5;        // front point at this fraction of the way from our spawn centre to its mirror (5 = midline)
    public static final int HOLD_DRIFT = 0;               // fight kite/hold branch: +this for a step toward the field target, -this away (0 off; < the 50 safe-band bonus)
    public static final boolean ESCORT_BEHIND = false; // escort goal one tile behind the carrier (never in its path) instead of its next tile
    public static final int OWN_FILL_STALL = 8;           // turns without progress before an own-signature tile may be filled
    public static final int RUSHERS = 0;                // T1 offence copy: ducks after the defenders that rush flags (arch_rush: 47)
    public static final int RUSH_THREAT_COST = 150;     // rush micro: score cost per enemy threatening a tile (kiting uses 1000)
    public static final int RING_RADIUS2 = 8;           // defenders ring their flag with traps out to this dist2 (iteration 0 value)
    public static final int TRAP_ENEMY_DIST2 = 20;      // combat trap only with an enemy this close (iteration 0: any in vision)
    public static final int EXPLOSIVE_BANK = 100000;    // combat explosive trap above this bank (off by default)
    public static final int ENGAGE_MAX_THREAT = 1;      // engage when weaker only if at most this many enemies threaten the tile
    public static final boolean TRAP_PLACEMENT_V2 = false; // iteration-4 placement (nearest-to-centroid tile); read 18-32 vs the rush partner as an unintended 'inert' control
    public static final boolean WATER_WHEN_WEAK = false; // T7 adoption: combat trap is a water trap when allies+1 < enemies in vision
    public static final boolean MICRO_V2 = false;       // structural swing (iteration 7): smooth tile scoring
    public static final int V2_HURT_HP = 300, V2_THREAT_HURT = 120, V2_THREAT_STRONG = 15, V2_THREAT_WEAK = 45;
    public static final int V2_SUPPORT = 6, V2_REACH = 100, V2_KILL = 60, V2_GOAL = 20;
    public static final boolean FLAG_TILE_STUN = false; // T1 neutralization: keep a stun trap on each home flag tile (rebuilt when triggered)
    public static final boolean POST_SETUP_CRUMBS = true; // T10 adoption: after setup, idle ducks pick up visible crumbs (g_iter1 never does)
    public static final int UPGRADE_ORDER = 0;           // 0 attack>heal>capture (g_iter1), 1 heal first, 2 capture first, 3 attack>capture>heal
    public static final boolean TRAP_TOWARD_NEAREST = false; // combat trap direction: nearest enemy (true) or enemy centroid (g_iter1)
    public static final int ATTACKER_TENTHS = 0;         // specialisation: ducks with idx%10 below this never heal (attack mastery)
    public static final int GROUP_MIN = 0;               // cohesion: push only with this many allies in view, else regroup (0 = off)
    public static final boolean RUSH_FLANK = false;     // T12 adoption: rushers raid the enemy flag farthest from the army's target
    public static final boolean HINT_SWEEP = false;      // block 2: sweep the dist2-100 disc of a broadcast hint instead of idling on it
    public static final boolean BUDGET_V1 = false;      // block 1: no discretionary spending in setup, paced floor after
    public static final int BANK_FLOOR0 = 1500, BANK_PACE = 10, FILL_STALL = 3;
    public static final boolean Z1HOLD = false;          // block 4: converge on our dropped flag (re-grabs 9.8/game vs beaters)
    public static final int Z1_RADIUS2 = 20;
    public static final boolean Z2ESCORT = false;        // block 4 v2: hit a carrier's escorts first (re-grab within 1-5 rounds otherwise)
    public static final int Z2_ESC_R2 = 2;               // escort radius around a carrier (8: a raider can step in and pick up the same turn)
    public static final boolean FILL_SMART = false;     // C2: take a free land step that does not lose distance instead of filling
    public static final boolean CARRIER_STUN = true;    // waffle crack: stun within dist2 8 of an enemy carrying our flag, ahead of it (g_iter3, 2026-10-03)
    public static final boolean ESCORT_TIGHT = false;   // escorts fight only from tiles within ESCORT_TIGHT_R2 of our carrier (Cyril crack)
    public static final int ESCORT_TIGHT_R2 = 8;        // a duck this close can step in and re-grab the same turn
    public static final int ESCORT_FAR_R2 = 0;          // > 0: out of a fight, join our carrier (registry location) within this dist2
    public static final int STUN_ENEMIES_MIN = 3;       // place a stun trap when this many enemies are within vision
    // S0b sensor (research/REWRITE_DESIGN.md 2.4): a shared, predicted track per our flag in slots 23-33 (Track); no consumer
    public static final boolean TRACK = false;          // S0b switch (g1trk): off, javac drops every Track hook and play is g_iter1 exactly
    public static final int HOME_CONFIRM = 8;           // re-stamp a HOME track this old when the flag is seen at home: bounds MISSING's
                                                        // pessimistic departure to ~8 rounds for 3 writes per 8 rounds (design 2.4)
    public static final int TRK_MISS_R2 = 10;           // a negative sighting needs the predicted point this close: half the vision dist2 20,
                                                        // so a carrier a tile or two off the straight-line prediction is still in view
    public static final int TRK_EXPIRE = 10;            // GONE once predicted arrival plus this many rounds has passed: bug-nav detours make
                                                        // real carriers slower than the straight-line prediction, which runs early
    public static final int TRK_INF_HOLD2 = 8;          // no miss on an inferred track (or a presumed re-grab) predicted within this dist2 of L
                                                        // (two moves): the inference has just seen that ground empty, so a miss there counts the
                                                        // same observation again (S0b review: MISSING / re-grab went LOST in 3 rounds, D switched)
    public static final int TRK_PSYM_BC = 2000;         // bytecodes one psym() call may spend scanning (metered: ~190 a new tile, so a one-tile
                                                        // move onto new ground costs 1.1-2.2k and a full new disc ~14k); the rest of a cut scan
                                                        // resumes next turn. Setup turns use ~750 (max 1.6k) and post-setup ones peak near 10.6k,
                                                        // so a turn stays far below the 19k S0b bar
}
