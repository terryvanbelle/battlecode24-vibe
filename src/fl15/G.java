package fl15;

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
        rc.setIndicatorString("bc" + maxBc / 1000 + "k o" + overruns + " x" + exceptions + " adv" + Micro.advances + " ft" + Duck.floatTraps + " ch" + Duck.chases + " rl" + Duck.relocs + " ry" + Duck.relays + " dg" + Duck.digs + " rs" + Duck.rushTurns + " ct" + Duck.combatTraps + " wt" + Duck.waterTraps + " fs" + Duck.flagTileTraps + " cd" + Duck.crumbDetours + " " + note);
    }

    /** xorshift; per-robot seeded from the id so identical code on both sides never shares a sequence. */
    public static int rand(int n) {
        int x = rngState; x ^= x << 13; x ^= x >>> 17; x ^= x << 5; rngState = x;
        return ((x % n) + n) % n;
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
