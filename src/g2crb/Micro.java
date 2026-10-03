package g2crb;

import battlecode.common.*;

/**
 * Kite and strike. Attack range dist² 4, attack cooldown 20 (one hit per two turns), move cooldown 10 (a step
 * every turn): hit, then step out of reach; when ready, step in and hit. A tile is "threatened" by an enemy within
 * dist² 10 of it (the enemy can step once and still reach dist² 4).
 */
public strictfp class Micro {
    public static int attacks, heals, kites, engages, advances, escortHits;

    /** A carrier within dist2 36 of an enemy spawn centre is about to score: kill it whatever the escorts. */
    static boolean nearTheirSpawn(MapLocation l) {
        for (MapLocation c : Sym.enemyCenters()) if (c != null && l.distanceSquaredTo(c) <= 36) return true;
        return false;
    }
    public static MapLocation objective;
    public static MapLocation guardFlag;   // z1hold: our dropped flag in view (null otherwise)   // set by Duck each turn: where the army is going (v2 pulls toward it when strong)

    static RobotInfo bestTarget(RobotInfo[] enemies, MapLocation from) {
        RobotInfo best = null; int bs = Integer.MAX_VALUE;
        if (C.Z2ESCORT) {   // block 4 v2: an escorted carrier re-grabs the next round if killed; strip the escorts first
            RobotInfo carrier = null;
            for (RobotInfo e : enemies) if (e.hasFlag) { carrier = e; break; }
            if (carrier != null) {
                int escorts = 0; RobotInfo esc = null; int eh = Integer.MAX_VALUE;
                for (RobotInfo e : enemies) if (!e.hasFlag && e.location.distanceSquaredTo(carrier.location) <= C.Z2_ESC_R2) {
                    escorts++;
                    if (from.distanceSquaredTo(e.location) <= 4 && e.health < eh) { eh = e.health; esc = e; }
                }
                if (escorts > 0 && esc != null && !nearTheirSpawn(carrier.location)) { escortHits++; return esc; }
            }
        }
        for (RobotInfo e : enemies) {
            if (from.distanceSquaredTo(e.location) > 4) continue;
            // flag carriers first, then lowest HP (kill soonest), then highest attack level
            int s = (e.hasFlag ? 0 : 100000) + (guardFlag != null && e.location.distanceSquaredTo(guardFlag) <= 2 ? 0 : 50000)
                    + e.health * 10 - e.attackLevel;   // z1hold: an enemy in pickup range of our dropped flag comes right after carriers
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

    /** Specialist attackers never heal, so their attack XP is never capped by heal mastery (reaching level 4 in one
     *  skill caps the others at 3; ~40 of our 50 ducks reached heal mastery in g_iter1's long games). */
    public static boolean isAttacker() { return G.idx >= 0 && (G.idx % 10) < C.ATTACKER_TENTHS; }

    public static boolean tryHeal(RobotInfo[] allies) throws GameActionException {
        if (!G.rc.isActionReady() || isAttacker()) return false;
        if (C.CARRIER_HEAL) {
            for (RobotInfo a : allies) {
                if (a.hasFlag && a.health < GameConstants.DEFAULT_HEALTH && G.rc.canHeal(a.location)) { G.rc.heal(a.location); heals++; carrierHeals++; return true; }
            }
        }
        RobotInfo best = null; int bh = C.HEAL_HP_BELOW;
        for (RobotInfo a : allies) {
            if (a.health < bh && G.rc.canHeal(a.location)) { bh = a.health; best = a; }
        }
        if (best != null) { G.rc.heal(best.location); heals++; return true; }
        return false;
    }

    /** Target v2: a kill this turn first, then flag carriers, then lowest HP. */
    static RobotInfo bestTargetV2(RobotInfo[] enemies, MapLocation from, int dmg) {
        RobotInfo best = null; int bs = Integer.MAX_VALUE;
        for (RobotInfo e : enemies) {
            if (from.distanceSquaredTo(e.location) > 4) continue;
            int s = (e.health <= dmg ? 0 : 200000) + (e.hasFlag ? 0 : 100000) + e.health * 10 - e.attackLevel;
            if (s < bs) { bs = s; best = e; }
        }
        return best;
    }

    static boolean tryAttackV2(RobotInfo[] enemies) throws GameActionException {
        if (!G.rc.isActionReady()) return false;
        RobotInfo t = bestTargetV2(enemies, G.rc.getLocation(), G.rc.getAttackDamage());
        if (t != null && G.rc.canAttack(t.location)) { G.rc.attack(t.location); attacks++; return true; }
        return false;
    }

    /** Micro v2 (structural swing): one smooth score per tile instead of lexicographic modes.
     *  + reach (can hit next action) with a kill bonus; - each enemy that can reach the tile, weighted by our state;
     *  + allies close by; approach when ready, back off when not. */
    public static boolean fightV2(RobotInfo[] enemies, RobotInfo[] allies, MapLocation obj) throws GameActionException {
        RobotController rc = G.rc;
        boolean actReady = rc.isActionReady();
        if (actReady && tryAttackV2(enemies)) actReady = false;
        if (!rc.isMovementReady()) { if (actReady) tryHeal(allies); return true; }
        MapLocation me = rc.getLocation();
        int dmg = rc.getAttackDamage(), hp = rc.getHealth();
        int nearAllies = 0;
        for (RobotInfo a : allies) if (me.distanceSquaredTo(a.location) <= 20) nearAllies++;
        boolean strong = nearAllies + 1 >= enemies.length + 1;
        boolean hurt = hp < C.V2_HURT_HP;
        int threatW = hurt ? C.V2_THREAT_HURT : strong ? C.V2_THREAT_STRONG : C.V2_THREAT_WEAK;
        RobotInfo carrier = null;
        for (RobotInfo e : enemies) if (e.hasFlag) { carrier = e; break; }
        MapLocation loose = null;                                    // REGRAB: an enemy flag on the ground in reach
        if (C.REGRAB && !hurt) {
            int ld = C.REGRAB_R2 + 1;
            for (FlagInfo f : Duck.flags) {
                if (f.getTeam() != G.them || f.isPickedUp()) continue;
                if (C.REGRAB_HALF && !ourHalf(f.getLocation())) continue;
                int x = me.distanceSquaredTo(f.getLocation());
                if (x < ld) { ld = x; loose = f.getLocation(); }
            }
        }
        Direction best = null; int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < 9; i++) {
            Direction d = i < 8 ? G.DIRS[i] : Direction.CENTER;
            if (d != Direction.CENTER && !rc.canMove(d)) continue;
            MapLocation l = me.add(d);
            int threats = 0, minD = Integer.MAX_VALUE; boolean reach = false, kill = false;
            for (RobotInfo e : enemies) {
                int x = l.distanceSquaredTo(e.location);
                if (x <= 10) threats++;
                if (x <= 4) { reach = true; if (e.health <= dmg) kill = true; }
                if (x < minD) minD = x;
            }
            int support = 0;
            for (RobotInfo a : allies) if (l.distanceSquaredTo(a.location) <= 8) support++;
            int score = -threats * threatW + support * C.V2_SUPPORT;
            if (actReady && reach) score += C.V2_REACH + (kill ? C.V2_KILL : 0);
            if (actReady && !hurt) score -= minD; else score += Math.min(minD, 20);
            if (carrier != null && !hurt) score -= l.distanceSquaredTo(carrier.location) * 4;
            if (obj != null && strong && !hurt) score -= (int) (Math.sqrt(l.distanceSquaredTo(obj)) * C.V2_GOAL);
            if (d == Direction.CENTER) score += 1;
            if (score > bestScore || (score == bestScore && G.rand(2) == 0)) { bestScore = score; best = d; }
        }
        if (best != null && best != Direction.CENTER) { rc.move(best); G.me = rc.getLocation(); }
        if (actReady) { if (!tryAttackV2(enemies)) tryHeal(allies); }
        return true;
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
        if (C.MICRO_V2 && goal == null) return fightV2(enemies, allies, objective);
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
        MapLocation loose = null;                                    // REGRAB: an enemy flag on the ground in reach
        if (C.REGRAB && !hurt) {
            int ld = C.REGRAB_R2 + 1;
            for (FlagInfo f : Duck.flags) {
                if (f.getTeam() != G.them || f.isPickedUp()) continue;
                if (C.REGRAB_HALF && !ourHalf(f.getLocation())) continue;
                int x = me.distanceSquaredTo(f.getLocation());
                if (x < ld) { ld = x; loose = f.getLocation(); }
            }
        }
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
            } else if (loose != null) {
                score = 19000 - l.distanceSquaredTo(loose) * 10 - th;              // REGRAB: reach the loose flag
            } else if (actReady && !hurt && inRange > 0 && (strong || th <= C.ENGAGE_MAX_THREAT)) {
                score = 10000 - th * 100 + adjAllies * 10;               // engage: hit from the safest reaching tile
            } else if (actReady && !hurt && nearAllies + 1 >= nearEnemies + C.ADVANCE_MARGIN) {
                score = 5000 - minD * 20 - th * 50 + adjAllies * 10;       // clear local superiority: close the gap
            } else {
                // kite / hold: out of reach, but stay close enough to strike next turn
                score = goal == null
                        ? -th * 1000 + adjAllies * 10 + (minD >= 11 && minD <= 20 ? 50 : 0) - (hurt ? minD < 20 ? 200 : 0 : 0)
                          + (C.HOLD_DRIFT > 0 && objective != null && !hurt ? Integer.signum(me.distanceSquaredTo(objective) - l.distanceSquaredTo(objective)) * C.HOLD_DRIFT : 0)
                        : -th * C.RUSH_THREAT_COST + adjAllies * 10 - l.distanceSquaredTo(goal) * 4;
            }
            if (d == Direction.CENTER) score += 1;                        // mild preference not to move for nothing
            if (score > bestScore || (score == bestScore && G.rand(2) == 0)) { bestScore = score; best = d; }
        }
        if (best != null && best != Direction.CENTER) {
            rc.move(best); G.me = rc.getLocation();
            if (bestScore >= 9000) engages++; else if (bestScore >= 4000) advances++; else kites++;
        }
        if (loose != null && actReady && rc.canPickupFlag(loose)) { Duck.pickupFlags(); if (rc.hasFlag()) { regrabTries++; return true; } }
        if (actReady) { if (!tryAttack(enemies)) tryHeal(allies); }
        return true;
    }

    public static int regrabTries, carrierHeals;

    /** Audit A4: can we engage any of these enemies soon? Yes if one is within dist2 8, or if a breadth-first search over
     *  passable tiles in view (at most 3 moves) reaches a tile within attack range (dist2 4) of one. Vision ignores walls,
     *  so without this a duck facing an enemy across a wall or water stood still for 100+ rounds (Tunnels). Falls back to
     *  "yes" when bytecode is short. */
    static boolean engageable(RobotInfo[] enemies) throws GameActionException {
        return C.REACH_FAST ? engageableFast(enemies) : engageableRef(enemies);
    }

    static boolean engageableRef(RobotInfo[] enemies) throws GameActionException {
        MapLocation me = G.me;
        for (RobotInfo e : enemies) if (me.distanceSquaredTo(e.location) <= 8) return true;
        if (G.bcLeft() < C.REACH_BC) return true;
        final int R = 3, N = 2 * R + 1;
        boolean[] seen = new boolean[N * N];
        int[] q = new int[N * N], dep = new int[N * N];
        int head = 0, tail = 0;
        q[tail] = R * N + R; dep[tail++] = 0; seen[R * N + R] = true;
        while (head < tail) {
            if (G.bcLeft() < C.REACH_BC_STOP) return true;      // never let the search eat the fight's budget (overrun r1887)
            int c = q[head], d = dep[head++];
            int cx = c % N - R + me.x, cy = c / N - R + me.y;
            MapLocation cl = new MapLocation(cx, cy);
            for (RobotInfo e : enemies) if (cl.distanceSquaredTo(e.location) <= 4) return true;
            if (d == R) continue;
            for (Direction dir : G.DIRS) {
                int nx = cx + dir.dx, ny = cy + dir.dy, gx = nx - me.x + R, gy = ny - me.y + R;
                if (gx < 0 || gy < 0 || gx >= N || gy >= N) continue;
                int k = gy * N + gx;
                if (seen[k]) continue;
                seen[k] = true;
                MapLocation nl = new MapLocation(nx, ny);
                if (!G.rc.onTheMap(nl) || !G.rc.canSenseLocation(nl) || !G.rc.senseMapInfo(nl).isPassable()) continue;
                q[tail] = k; dep[tail++] = d + 1;
            }
        }
        return false;
    }

    static final int[] NEAR_DX = {0, 1, -1, 0, 0, 1, 1, -1, -1, 2, -2, 0, 0}, NEAR_DY = {0, 0, 0, 1, -1, 1, -1, 1, -1, 0, 0, 2, -2};

    /** C.REACH_FAST: engageableRef's answer at a fraction of its bytecode (overrun r1887: the search plus a crowded
     *  fight). Tiles within attack range of an enemy are marked once (13 offsets per enemy) instead of testing every
     *  enemy at every reached tile, and the onTheMap/canSenseLocation calls become a bounds test: the whole 7x7 grid
     *  is inside vision (corner dist2 18 <= 20). */
    static boolean engageableFast(RobotInfo[] enemies) throws GameActionException {
        MapLocation me = G.me;
        for (RobotInfo e : enemies) if (me.distanceSquaredTo(e.location) <= 8) return true;
        if (G.bcLeft() < C.REACH_BC) return true;
        final int R = 3, N = 2 * R + 1;
        int ox = me.x - R, oy = me.y - R;
        boolean[] near = new boolean[N * N];
        for (RobotInfo e : enemies) {
            int ex = e.location.x - ox, ey = e.location.y - oy;
            if (ex < -2 || ey < -2 || ex > N + 1 || ey > N + 1) continue;
            for (int j = 12; j >= 0; j--) {
                int gx = ex + NEAR_DX[j], gy = ey + NEAR_DY[j];
                if (gx >= 0 && gy >= 0 && gx < N && gy < N) near[gy * N + gx] = true;
            }
        }
        boolean[] seen = new boolean[N * N];
        int[] q = new int[N * N], dep = new int[N * N];
        int head = 0, tail = 0, W = G.W, H = G.H;
        q[tail] = R * N + R; dep[tail++] = 0; seen[R * N + R] = true;
        while (head < tail) {
            int c = q[head], d = dep[head++];
            if (near[c]) return true;
            if (d == R) continue;
            int gx0 = c % N, gy0 = c / N;
            for (Direction dir : G.DIRS) {
                int gx = gx0 + dir.dx, gy = gy0 + dir.dy;
                if (gx < 0 || gy < 0 || gx >= N || gy >= N) continue;
                int k = gy * N + gx;
                if (seen[k]) continue;
                seen[k] = true;
                int nx = gx + ox, ny = gy + oy;
                if (nx < 0 || ny < 0 || nx >= W || ny >= H || !G.rc.senseMapInfo(new MapLocation(nx, ny)).isPassable()) continue;
                q[tail] = k; dep[tail++] = d + 1;
            }
        }
        return false;
    }

    /** Nearer our closest spawn centre than any enemy spawn centre (Sym's most likely symmetry). */
    static boolean ourHalf(MapLocation l) {
        int ours = Integer.MAX_VALUE, theirs = Integer.MAX_VALUE;
        for (MapLocation c : G.spawnCenters) if (c != null) ours = Math.min(ours, l.distanceSquaredTo(c));
        for (MapLocation c : Sym.enemyCenters()) if (c != null) theirs = Math.min(theirs, l.distanceSquaredTo(c));
        return ours < theirs;
    }
}
