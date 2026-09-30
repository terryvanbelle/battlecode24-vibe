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
    public static final int STUN_ENEMIES_MIN = 3;       // place a stun trap when this many enemies are within vision
}
