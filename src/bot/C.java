package bot;

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
    public static final boolean RELOCATE_FLAGS = true;  // T2 offence copy: carry flags in setup to far spots (iteration 2)
    public static final int RELOC_DEADLINE = 170;       // drop wherever legal from this round (dam opens after r200)
    public static final int RELOC_STALL = 12;           // turns without progress before dropping where we stand
    public static final int STUN_ENEMIES_MIN = 3;       // place a stun trap when this many enemies are within vision
}
