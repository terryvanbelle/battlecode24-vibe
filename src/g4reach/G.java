package g4reach;

import battlecode.common.*;

/** Per-robot globals (static = per robot; nothing here is shared between robots). */
public strictfp class G {
    public static RobotController rc;
    public static Team us, them;
    public static int W, H, id, round, idx = -1;
    public static MapLocation me;
    public static MapLocation[] spawns;          // our 27 spawn tiles
    public static MapLocation[] spawnCenters = new MapLocation[3];
    public static int exceptions, overruns, nearMisses, maxBc;
    public static int rngState;
    public static final Direction[] DIRS = {Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST,
            Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST};
    public static String note = "";

    /** Bytecodes left this turn; tests set testBc (outside the engine Clock.getBytecodesLeft() returns 0). */
    public static int testBc = -1;
    static int bcLeft() { return testBc >= 0 ? testBc : Clock.getBytecodesLeft(); }

    public static void init(RobotController r) {
        rc = r; us = rc.getTeam(); them = us.opponent(); W = rc.getMapWidth(); H = rc.getMapHeight(); id = rc.getID();
        rngState = id * 0x9E3779B1 + 12345; if (rngState == 0) rngState = 1;
        spawns = rc.getAllySpawnLocations();
        // spawn centres: a spawn tile whose 8 neighbours are all spawn tiles
        int n = 0;
        for (MapLocation a : spawns) {
            int adj = 0;
            for (MapLocation b : spawns) if (a.isAdjacentTo(b) && !a.equals(b)) adj++;
            if (adj == 8 && n < 3) spawnCenters[n++] = a;
        }
    }

    public static void startTurn() throws GameActionException {
        round = rc.getRoundNum();
        me = rc.isSpawned() ? rc.getLocation() : null;
        note = "";
        if (idx < 0) idx = Comms.claimIndex();
    }

    public static void endTurn() {
        if (C.TRACK) {   // S0b (research/REWRITE_DESIGN.md 2.6): counters first so the 64-char cut never drops them; R/C/E/P are the
                         // responder counters of later stages (no consumer yet: 0). T<headline state><track writes> e<Track exceptions>
                         // M<MISSING starts>
            rc.setIndicatorString("bc" + maxBc / 1000 + "k o" + overruns + " x" + exceptions + " T" + Track.headline() + Track.writes
                    + " e" + Track.exc + " R0 C0 E0 M" + Track.missings + " P0 " + note);
            return;
        }
        rc.setIndicatorString("bc" + maxBc / 1000 + "k o" + overruns + " x" + exceptions + " adv" + Micro.advances + " ch" + Duck.chases + "/" + Duck.intercepts + "/" + Duck.camps + " et" + Duck.escortTurns + " hl" + Duck.holdTurns + " ct" + Duck.combatTraps + " cs" + Duck.safeSteps + "/" + Micro.carrierHeals + " es" + Micro.escortHits + " rg" + Micro.regrabTries + " f" + Nav.fills + "/" + Nav.avoidableFills + "/" + Nav.ownSkips + " " + note);
    }

    /** xorshift; per-robot seeded from the id so identical code on both sides never shares a sequence. */
    public static int rand(int n) {
        int x = rngState; x ^= x << 13; x ^= x >>> 17; x ^= x << 5; rngState = x;
        return ((x % n) + n) % n;
    }

    /** Block 1 (research/TACTIC_LEVELS.md): one round-paced team floor for discretionary spending (setup traps, flag
     *  ring, idle-bank traps). In setup nothing discretionary is spent; after the dam the floor starts at BANK_FLOOR0 and
     *  falls by BANK_PACE per round. Off (floor 0) unless C.BUDGET_V1. */
    public static int bankFloor() {
        if (!C.BUDGET_V1) return 0;
        if (round <= C.SETUP_ROUNDS) return Integer.MAX_VALUE;
        return Math.max(0, C.BANK_FLOOR0 - (round - C.SETUP_ROUNDS) * C.BANK_PACE);
    }

    public static boolean onMap(int x, int y) { return x >= 0 && y >= 0 && x < W && y < H; }

    /** Nearest location in arr to from (null entries skipped); ties broken by the robot's own rng, never array order. */
    public static MapLocation nearest(MapLocation from, MapLocation[] arr) {
        MapLocation best = null; int bd = Integer.MAX_VALUE;
        for (MapLocation m : arr) {
            if (m == null) continue;
            int d = from.distanceSquaredTo(m);
            if (d < bd || (d == bd && rand(2) == 0)) { bd = d; best = m; }
        }
        return best;
    }
}
