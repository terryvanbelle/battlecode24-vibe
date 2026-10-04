package g4front;

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
    public static final boolean RELAY = false;          // T3 offence copy: hand the flag forward to an adjacent ally
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
    public static final boolean STUN_FRONT = true;     // combat stuns one enemy step from triggering, most enemies in the stun radius (Cyril's geometry); arm g4front
    public static final int FRONT_MIN_VICTIMS = 3, FRONT_RESERVE = 100;   // STUN_FRONT: enemies within dist2 13 of the tile; crumbs kept
    public static final boolean INIT_FAST = false;      // round 1: spawn centres from a bitset (~2k bytecodes, not ~18k); arm g4init
    public static final boolean ALERT_NEAREST = false;  // audit BOT9: field ducks answer the nearest live fresh alert; respawns split over alerts; arm g4alert
    public static final boolean RELOC_SPREAD = false;   // audit BOT16: a relocation spot scan pauses at 4000 bytecodes left and resumes next turn; arm g4spread
    public static final boolean FILL_STEP = false;      // audit BOT8: step onto the tile just filled in the same turn; arm g4fill
    public static final boolean CARRY_PREDICT = false;  // audit BOT10: chase a stale carrier sighting at its predicted point (age < 60); arm g4pred
    public static final int PREDICT_MARGIN = 5;         // CARRY_PREDICT: rounds of slack past the predicted arrival
    public static final boolean STUN_AHEAD = false;     // audit BOT7: build a carrier stun only from ahead of the carrier; arm g4ahead
    public static final boolean PICKUP_AFTER_MOVE = false;  // audit BOT5: pick up a loose enemy flag after the step, before striking; arm g4pick
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
    public static final boolean POST_SETUP_CRUMBS = false; // T10 adoption: after setup, idle ducks pick up visible crumbs (g_iter1 never does)
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
