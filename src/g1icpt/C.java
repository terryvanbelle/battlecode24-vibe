package g1icpt;

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
    public static final boolean RELOCATE_FLAGS = false;  // T2 offence copy: carry flags in setup to far spots (iteration 2)
    public static final int RELOC_DEADLINE = 170;       // drop wherever legal from this round (dam opens after r200)
    public static final int RELOC_STALL = 12;           // turns without progress before dropping where we stand
    public static final boolean RELAY = false;          // T3 offence copy: hand the flag forward to an adjacent ally
    public static final int ALERT_RADIUS2 = 100;        // ducks within this dist2 of an alerted flag go home to it (iteration 0 value)
    public static final int SETUP_DIGS = 0;             // T4 copy dose: checkerboard digs per duck in setup (arm ladder 0/5/10)
    public static final int DIG_RESERVE = 1000;         // setup digging never takes the bank below this
    public static final int DIG_SITE = 0;               // T4 siting: 0 any, 1 wall-hugging (>=3 wall/off-map nbrs), 2 behind our spawn (away from the enemy)
    public static final boolean NO_FILL_OWN = false;   // T4: in setup, never fill a tile matching our dig signature (even, DIG_SITE 2) unless stalled
    public static final boolean REGRAB = false;        // C9/C10: in a fight, a visible enemy flag on the ground within REGRAB_R2 is the movement goal; pick it up before striking
    public static final int REGRAB_R2 = 13;
    public static final boolean REGRAB_HALF = false;   // REGRAB only for loose flags nearer our spawn centres than theirs (a carrier there has a chance)
    public static final boolean CARRY_SAFE = false;    // C9: with enemies in view, the carrier takes the homeward step with the fewest enemies able to reach it
    public static final boolean CARRIER_HEAL = false;  // C9: a hurt allied carrier in heal range is healed before anyone else
    public static final boolean INTERCEPT = true;      // C10a: in a fight, a fresh carrier alert within INTERCEPT_R2 turns the fight goal-directed toward the carrier
    public static final int INTERCEPT_R2 = 225;
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
    public static final int STUN_ENEMIES_MIN = 3;       // place a stun trap when this many enemies are within vision
}
