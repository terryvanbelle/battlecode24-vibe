package z0inert;

import battlecode.common.*;

/**
 * Kite and strike. Attack range dist² 4, attack cooldown 20 (one hit per two turns), move cooldown 10 (a step
 * every turn): hit, then step out of reach; when ready, step in and hit. A tile is "threatened" by an enemy within
 * dist² 10 of it (the enemy can step once and still reach dist² 4).
 */
public strictfp class Micro {
    public static int attacks, heals, kites, engages, advances;

    static RobotInfo bestTarget(RobotInfo[] enemies, MapLocation from) {
        RobotInfo best = null; int bs = Integer.MAX_VALUE;
        for (RobotInfo e : enemies) {
            if (from.distanceSquaredTo(e.location) > 4) continue;
            // flag carriers first, then lowest HP (kill soonest), then highest attack level
            int s = (e.hasFlag ? 0 : 100000) + e.health * 10 - e.attackLevel;
            if (s < bs) { bs = s; best = e; }
        }
        return best;
    }

    public static boolean tryAttack(RobotInfo[] enemies) throws GameActionException {
        if (!G.rc.isActionReady()) return false;
        RobotInfo t = bestTarget(enemies, G.rc.getLocation());
        if (t != null && G.rc.canAttack(t.location)) { G.rc.attack(t.location); attacks++; return true; }
        return false;
    }

    public static boolean tryHeal(RobotInfo[] allies) throws GameActionException {
        if (!G.rc.isActionReady()) return false;
        RobotInfo best = null; int bh = C.HEAL_HP_BELOW;
        for (RobotInfo a : allies) {
            if (a.health < bh && G.rc.canHeal(a.location)) { bh = a.health; best = a; }
        }
        if (best != null) { G.rc.heal(best.location); heals++; return true; }
        return false;
    }

    static int threat(MapLocation l, RobotInfo[] enemies) {
        int t = 0;
        for (RobotInfo e : enemies) if (l.distanceSquaredTo(e.location) <= 10) t++;
        return t;
    }

    /** Full combat turn when enemies are visible. Returns true if it took over movement. */
    public static boolean fight(RobotInfo[] enemies, RobotInfo[] allies) throws GameActionException { return fight(enemies, allies, null); }

    /** goal != null: a pushing fight (rush): same strike logic, but when not striking, trade some safety for progress. */
    public static boolean fight(RobotInfo[] enemies, RobotInfo[] allies, MapLocation goal) throws GameActionException {
        RobotController rc = G.rc;
        MapLocation me = rc.getLocation();
        boolean actReady = rc.isActionReady();
        // 1. strike first if something is in reach
        if (actReady && tryAttack(enemies)) actReady = false;
        if (!rc.isMovementReady()) { if (actReady) tryHeal(allies); return true; }

        int nearAllies = 0;
        for (RobotInfo a : allies) if (me.distanceSquaredTo(a.location) <= 20) nearAllies++;
        int nearEnemies = enemies.length;
        boolean strong = nearAllies + 1 >= nearEnemies;
        boolean hurt = rc.getHealth() < C.RETREAT_HP;

        RobotInfo carrier = null;
        for (RobotInfo e : enemies) if (e.hasFlag) { carrier = e; break; }
        Direction best = null; int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < 9; i++) {
            Direction d = i < 8 ? G.DIRS[i] : Direction.CENTER;
            if (d != Direction.CENTER && !rc.canMove(d)) continue;
            MapLocation l = me.add(d);
            int th = threat(l, enemies);
            int inRange = 0, minD = Integer.MAX_VALUE;
            for (RobotInfo e : enemies) {
                int x = l.distanceSquaredTo(e.location);
                if (x <= 4) inRange++;
                if (x < minD) minD = x;
            }
            int adjAllies = 0;
            for (RobotInfo a : allies) if (l.distanceSquaredTo(a.location) <= 2) adjAllies++;
            int score;
            if (carrier != null && !hurt) {
                score = 20000 - l.distanceSquaredTo(carrier.location) * 10 - th;   // a flag carrier: close in regardless
            } else if (actReady && !hurt && inRange > 0 && (strong || th <= 1)) {
                score = 10000 - th * 100 + adjAllies * 10;               // engage: hit from the safest reaching tile
            } else if (actReady && !hurt && nearAllies + 1 >= nearEnemies + C.ADVANCE_MARGIN) {
                score = 5000 - minD * 20 - th * 50 + adjAllies * 10;       // clear local superiority: close the gap
            } else {
                // kite / hold: out of reach, but stay close enough to strike next turn
                score = goal == null
                        ? -th * 1000 + adjAllies * 10 + (minD >= 11 && minD <= 20 ? 50 : 0) - (hurt ? minD < 20 ? 200 : 0 : 0)
                        : -th * C.RUSH_THREAT_COST + adjAllies * 10 - l.distanceSquaredTo(goal) * 4;
            }
            if (d == Direction.CENTER) score += 1;                        // mild preference not to move for nothing
            if (score > bestScore || (score == bestScore && G.rand(2) == 0)) { bestScore = score; best = d; }
        }
        if (best != null && best != Direction.CENTER) {
            rc.move(best); G.me = rc.getLocation();
            if (bestScore >= 9000) engages++; else if (bestScore >= 4000) advances++; else kites++;
        }
        if (actReady) { if (!tryAttack(enemies)) tryHeal(allies); }
        return true;
    }
}
