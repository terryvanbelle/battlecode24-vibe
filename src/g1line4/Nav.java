package g1line4;

import battlecode.common.*;

/**
 * Greedy step toward the target; on obstruction, bug wall-following with per-robot handedness until strictly
 * closer than when the obstruction began. Water directly in the way is filled when affordable and we are stuck.
 * Every walker has a stall exit: no progress for STALL turns flips handedness and resets.
 */
public strictfp class Nav {
    static MapLocation target;
    static boolean bugging, left;
    static Direction bugDir;
    static int bugStartDist, bestDist, noProgress;
    static final int STALL = 20;
    public static int moves, bugSteps, fills, avoidableFills, ownSkips;

    static void reset(MapLocation t) {
        target = t; bugging = false; bestDist = Integer.MAX_VALUE; noProgress = 0; left = (G.id & 1) == 0;
    }

    static boolean tryMove(Direction d) throws GameActionException {
        if (d != null && G.rc.canMove(d)) { G.rc.move(d); G.me = G.rc.getLocation(); moves++; return true; }
        return false;
    }

    /** One step toward t. Returns true if the robot moved. */
    public static boolean moveTo(MapLocation t) throws GameActionException {
        RobotController rc = G.rc;
        if (t == null || !rc.isMovementReady()) return false;
        if (target == null || !t.equals(target)) reset(t);
        MapLocation me = rc.getLocation();
        if (me.equals(t)) return false;
        int d = me.distanceSquaredTo(t);
        if (d < bestDist) { bestDist = d; noProgress = 0; } else noProgress++;
        if (noProgress > STALL) { left = !left; bugging = false; bestDist = d; noProgress = 0; }
        Direction dir = me.directionTo(t);
        if (!bugging) {
            if (tryMove(dir)) return true;
            Direction l = dir.rotateLeft(), r = dir.rotateRight();
            boolean lf = me.add(l).distanceSquaredTo(t) < d, rf = me.add(r).distanceSquaredTo(t) < d;
            if (left) { if (lf && tryMove(l)) return true; if (rf && tryMove(r)) return true; }
            else { if (rf && tryMove(r)) return true; if (lf && tryMove(l)) return true; }
            if (fillToward(me, dir)) return false;
            bugging = true; bugDir = dir; bugStartDist = d;
        } else if (d < bugStartDist && rc.canMove(dir)) {
            bugging = false;
            return tryMove(dir);
        }
        for (int i = 0; i < 8; i++) {
            if (rc.canMove(bugDir)) {
                rc.move(bugDir); G.me = rc.getLocation(); moves++; bugSteps++;
                bugDir = left ? bugDir.rotateRight().rotateRight() : bugDir.rotateLeft().rotateLeft();
                return true;
            }
            MapLocation n = me.add(bugDir);
            if (!rc.onTheMap(n)) left = !left;     // hit the map edge: follow the other way
            bugDir = left ? bugDir.rotateLeft() : bugDir.rotateRight();
        }
        return false;
    }

    /** Fill water in direction dir (or its neighbours) when stuck behind it. */
    static boolean fillToward(MapLocation me, Direction dir) throws GameActionException {
        RobotController rc = G.rc;
        if (!rc.isActionReady() || rc.getCrumbs() < 30 + C.FILL_RESERVE) return false;
        if (C.BUDGET_V1 && G.round <= C.SETUP_ROUNDS && noProgress < C.FILL_STALL) return false;   // block 1: setup fills only when stalled
        Direction[] ds = {dir, dir.rotateLeft(), dir.rotateRight()};
        boolean fillable = false;
        for (Direction x : ds) if (rc.canFill(me.add(x))) { fillable = true; break; }
        if (!fillable) return false;
        // C2: is there a free land step that does not move us away from the target? Then the fill is avoidable.
        Direction alt = null; int d0 = me.distanceSquaredTo(target);
        for (Direction x : G.DIRS) {
            if (!rc.canMove(x)) continue;
            int d = me.add(x).distanceSquaredTo(target);
            if (d <= d0 && (alt == null || d < me.add(alt).distanceSquaredTo(target))) alt = x;
        }
        if (alt != null) {
            avoidableFills++;
            if (C.FILL_SMART) { if (tryMove(alt)) return true; }
        }
        for (Direction x : ds) {
            MapLocation n = me.add(x);
            if (!rc.canFill(n)) continue;
            if (C.NO_FILL_OWN && G.round <= C.SETUP_ROUNDS && noProgress < C.OWN_FILL_STALL && ownDigSignature(n)) { ownSkips++; continue; }
            rc.fill(n); fills++; return true;
        }
        return false;
    }

    /** The tile matches our setup-dig rule (Duck.setupDig with DIG_SITE 2): even parity and behind our spawn. The
     *  checkerboard keeps every land tile diagonally connected, so bugging around it never needs a fill. */
    static boolean ownDigSignature(MapLocation n) throws GameActionException {
        return ((n.x + n.y) & 1) == 0 && Duck.behindSpawn(n);
    }

    /** Step to the adjacent tile (or stay) minimising distance to t; used for small adjustments. */
    public static boolean stepToward(MapLocation t) throws GameActionException {
        if (!G.rc.isMovementReady()) return false;
        MapLocation me = G.rc.getLocation();
        Direction best = null; int bd = me.distanceSquaredTo(t);
        for (Direction d : G.DIRS) {
            if (!G.rc.canMove(d)) continue;
            int x = me.add(d).distanceSquaredTo(t);
            if (x < bd || (x == bd && best != null && G.rand(2) == 0)) { bd = x; best = d; }
        }
        return best != null && tryMove(best);
    }
}
