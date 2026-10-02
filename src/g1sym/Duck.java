package g1sym;

import battlecode.common.*;

/** Per-turn behaviour of every duck. Roles by creation index: 0..2 flag defenders, the rest the field army. */
public strictfp class Duck {
    static RobotInfo[] enemies = new RobotInfo[0], allies = new RobotInfo[0];
    static FlagInfo[] flags = new FlagInfo[0];
    static int carriedFlagId = -1;
    static MapLocation exploreTarget;
    static int exploreSince;

    static boolean isDefender() { return G.idx >= 0 && G.idx < 3 * C.DEFENDERS_PER_FLAG; }
    static int homeFlag() { return G.idx % 3; }

    public static void turn() throws GameActionException {
        RobotController rc = G.rc;
        buyUpgrades();
        if (!rc.isSpawned()) { trySpawn(); if (!rc.isSpawned()) return; }
        G.me = rc.getLocation();
        if (G.round <= 3) Sym.fromBroadcasts(rc.senseBroadcastFlagLocations());
        Comms.syncSym();
        sense();
        if (rc.hasFlag()) { carryFlag(); return; }
        if (G.round <= C.SETUP_ROUNDS) { setup(); return; }
        pickupFlags();
        if (rc.hasFlag()) { carryFlag(); return; }
        if (enemies.length > 0) {
            placeCombatTrap();
            Micro.fight(enemies, allies);
            G.note = "fight e" + enemies.length + " a" + allies.length;
            return;
        }
        if (isDefender() && alertedFlag() < 0) { defend(); Micro.tryHeal(allies); return; }
        Micro.tryHeal(allies);
        Nav.moveTo(fieldTarget());
        if (rc.isActionReady()) Micro.tryHeal(allies);
        if (rc.isActionReady() && rc.getCrumbs() > C.FLOAT_CRUMBS) spendFloat();
    }

    static void sense() throws GameActionException {
        RobotController rc = G.rc;
        enemies = rc.senseNearbyRobots(-1, G.them);
        allies = rc.senseNearbyRobots(-1, G.us);
        flags = rc.senseNearbyFlags(-1);
        for (FlagInfo f : flags) {
            if (f.getTeam() == G.us && f.isPickedUp()) {
                int i = Comms.ourFlagIndex(f.getID());
                if (i >= 0) Comms.reportCarried(i, f.getLocation());
            }
            if (f.getTeam() == G.them) Comms.reportEnemyFlag(f);
            else if (enemies.length > 0) {
                for (int i = 0; i < 3; i++) if (G.spawnCenters[i] != null && f.getLocation().distanceSquaredTo(G.spawnCenters[i]) <= 36) Comms.alertOurFlag(i);
            }
        }
        // our flags: alert if enemies are near a home flag
        if (enemies.length > 0) for (int i = 0; i < 3; i++) {
            MapLocation c = G.spawnCenters[i];
            if (c != null && G.me.distanceSquaredTo(c) <= 20) Comms.alertOurFlag(i);
        }
    }

    static void buyUpgrades() throws GameActionException {
        RobotController rc = G.rc;
        if (G.round < 600) return;
        if (rc.canBuyGlobal(GlobalUpgrade.ATTACK)) rc.buyGlobal(GlobalUpgrade.ATTACK);
        else if (rc.canBuyGlobal(GlobalUpgrade.HEALING)) rc.buyGlobal(GlobalUpgrade.HEALING);
        else if (rc.canBuyGlobal(GlobalUpgrade.CAPTURING)) rc.buyGlobal(GlobalUpgrade.CAPTURING);
    }

    /** Spawn in the zone of our home flag (defenders) or the zone nearest the current field target. */
    static void trySpawn() throws GameActionException {
        RobotController rc = G.rc;
        MapLocation want;
        if (isDefender() || G.round <= 5) want = G.spawnCenters[G.idx % 3];
        else {
            int alerted = alertedFlag();
            MapLocation ch = carrierTarget(G.spawnCenters[G.idx % 3]);
            want = ch != null ? ch : alerted >= 0 ? G.spawnCenters[alerted] : fieldTargetFrom(G.spawnCenters[G.idx % 3]);
        }
        if (want == null) want = G.spawns[0];
        // try tiles nearest the wanted point first
        MapLocation best = null; int bd = Integer.MAX_VALUE;
        for (MapLocation s : G.spawns) {
            if (!rc.canSpawn(s)) continue;
            int d = s.distanceSquaredTo(want);
            if (d < bd || (d == bd && G.rand(2) == 0)) { bd = d; best = s; }
        }
        if (best != null) rc.spawn(best);
    }

    static int alertedFlag() throws GameActionException {
        int best = -1, br = 0;
        for (int i = 0; i < 3; i++) {
            int r = G.rc.readSharedArray(Comms.OF_ALERT + i);
            if (r > 0 && G.round - r <= 10 && r > br) { br = r; best = i; }
        }
        return best;
    }

    // ------------------------------------------------------------------ setup phase
    static void setup() throws GameActionException {
        RobotController rc = G.rc;
        if (isDefender()) { defend(); return; }
        if (G.round < C.GATHER_ROUND) {
            MapLocation[] crumbs = rc.senseNearbyCrumbs(-1);
            MapLocation c = G.nearest(G.me, crumbs);
            if (c != null) { Nav.moveTo(c); G.note = "crumb"; return; }
            explore();
            return;
        }
        // gather at the dam facing the enemy
        MapLocation t = fieldTargetFrom(G.me);
        if (!nextToDam()) Nav.moveTo(t);
        else if (G.round >= C.DAM_TRAP_ROUND) damTrap();
        G.note = "gather";
    }

    static boolean nextToDam() throws GameActionException {
        for (Direction d : G.DIRS) {
            MapLocation n = G.me.add(d);
            if (G.rc.onTheMap(n) && G.rc.senseMapInfo(n).isDam()) return true;
        }
        return false;
    }

    static void explore() throws GameActionException {
        if (exploreTarget == null || G.me.distanceSquaredTo(exploreTarget) <= 8 || G.round - exploreSince > 40) {
            exploreTarget = new MapLocation(G.rand(G.W), G.rand(G.H));
            exploreSince = G.round;
        }
        Nav.moveTo(exploreTarget);
        G.note = "explore";
    }

    // ------------------------------------------------------------------ flags
    static void pickupFlags() throws GameActionException {
        RobotController rc = G.rc;
        for (FlagInfo f : flags) {
            if (f.getTeam() != G.them || f.isPickedUp()) continue;
            if (rc.canPickupFlag(f.getLocation())) {
                rc.pickupFlag(f.getLocation());
                carriedFlagId = f.getID();
                Comms.reportEnemyFlag(new FlagInfo(G.me, G.them, true, f.getID()));
                return;
            }
        }
    }

    static void carryFlag() throws GameActionException {
        RobotController rc = G.rc;
        if (G.round <= C.SETUP_ROUNDS) { // our own flag in setup: we never pick those up in iteration 0; drop it
            if (rc.canDropFlag(G.me)) rc.dropFlag(G.me);
            return;
        }
        MapLocation home = G.nearest(G.me, G.spawns);
        G.note = "carry";
        Nav.moveTo(home);
        if (!rc.hasFlag() && carriedFlagId >= 0) { Comms.enemyFlagCaptured(carriedFlagId); carriedFlagId = -1; }
    }

    // ------------------------------------------------------------------ targets
    /** Where the army goes when nothing is in sight. */
    static MapLocation fieldTarget() throws GameActionException {
        // defend a flag under attack if we are its defender or close to it
        int a = alertedFlag();
        MapLocation chase = carrierTarget(G.me);
        if (chase != null) { G.note = "chase"; return chase; }
        if (isDefender()) return defendTarget();
        if (a >= 0 && G.me.distanceSquaredTo(G.spawnCenters[a]) <= 100) return G.spawnCenters[a];
        // a visible dropped enemy flag
        for (FlagInfo f : flags) if (f.getTeam() == G.them && !f.isPickedUp()) return f.getLocation();
        // escort a friendly carrier we can see
        for (RobotInfo r : allies) if (r.hasFlag) return r.location;
        return fieldTargetFrom(G.me);
    }

    static int chases;

    /** An enemy carrying our flag: chase it if close, else wait at the enemy spawn centre it is walking to. */
    static MapLocation carrierTarget(MapLocation from) throws GameActionException {
        MapLocation best = null; int bd = Integer.MAX_VALUE;
        MapLocation[] ec = Sym.enemyCenters();
        for (int i = 0; i < 3; i++) {
            MapLocation c = Comms.carried(i, C.CARRY_FRESH);
            if (c == null) continue;
            MapLocation dest = G.nearest(c, ec);
            int dc = from.distanceSquaredTo(c);
            MapLocation t = (dest != null && dc > C.CHASE_RADIUS2 && from.distanceSquaredTo(dest) < dc) ? dest : c;
            int d = from.distanceSquaredTo(t);
            if (d < bd) { bd = d; best = t; }
        }
        if (best != null) chases++;
        return best;
    }

    static MapLocation fieldTargetFrom(MapLocation from) throws GameActionException {
        RobotController rc = G.rc;
        MapLocation best = null; int bd = Integer.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            if (rc.readSharedArray(Comms.EF_STATE + i) != 0) continue;
            MapLocation l = Comms.dec(rc.readSharedArray(Comms.EF_LOC + i));
            if (l == null) continue;
            if (G.me != null && G.me.distanceSquaredTo(l) <= 4) {       // arrived and it is not here any more
                boolean seen = false;
                for (FlagInfo f : flags) if (f.getID() + 1 == rc.readSharedArray(Comms.EF_ID + i)) seen = true;
                if (!seen) { Comms.clearEnemyFlagLoc(i); continue; }
            }
            int d = from.distanceSquaredTo(l);
            if (d < bd) { bd = d; best = l; }
        }
        if (best != null) return best;
        MapLocation[] bc = rc.senseBroadcastFlagLocations();
        best = G.nearest(from, bc);
        if (best != null) return best;
        return G.nearest(from, Sym.enemyCenters());                    // last resort: enemy spawn centres by symmetry
    }

    // ------------------------------------------------------------------ defence
    static MapLocation defendTarget() {
        return G.spawnCenters[homeFlag()];
    }

    static int ringStep;

    static void defend() throws GameActionException {
        RobotController rc = G.rc;
        MapLocation home = defendTarget();
        if (home == null) return;
        G.note = "defend";
        if (G.me.distanceSquaredTo(home) > 8) { Nav.moveTo(home); return; }
        // ring the flag with traps: stun where dist2 to the flag is 4..8, explosive adjacent to it
        if (rc.isActionReady()) {
            for (Direction d : G.DIRS) {
                MapLocation t = G.me.add(d);
                int dh = t.distanceSquaredTo(home);
                if (dh == 0 || dh > 8) continue;
                TrapType tt = dh <= 2 ? TrapType.EXPLOSIVE : TrapType.STUN;
                if (rc.getCrumbs() >= tt.buildCost + C.DEF_TRAP_RESERVE && rc.canBuild(tt, t)) { rc.build(tt, t); Duck.defTraps++; break; }
            }
        }
        // walk around the flag so every ring tile comes within reach
        MapLocation post = home.add(G.DIRS[ringStep & 7]);
        if (G.me.equals(post) || !rc.canSenseLocation(post) || !rc.sensePassability(post)) ringStep++;
        Nav.moveTo(home.add(G.DIRS[ringStep & 7]));
    }

    static int defTraps, damTraps;

    /** Late setup, standing at the dam: trap the tiles the enemy must step on first. */
    static void damTrap() throws GameActionException {
        RobotController rc = G.rc;
        if (!rc.isActionReady() || rc.getCrumbs() < TrapType.STUN.buildCost + C.DAM_TRAP_RESERVE) return;
        for (Direction d : G.DIRS) {
            MapLocation t = G.me.add(d);
            if (!rc.canBuild(TrapType.STUN, t)) continue;
            boolean touchesDam = false;
            for (Direction e : G.DIRS) {
                MapLocation n = t.add(e);
                if (rc.onTheMap(n) && rc.canSenseLocation(n) && rc.senseMapInfo(n).isDam()) { touchesDam = true; break; }
            }
            if (touchesDam) { rc.build(TrapType.STUN, t); damTraps++; return; }
        }
    }

    static int floatTraps;

    /** Nothing to do and a big bank: a stun trap on an adjacent tile toward the enemy (also build XP for the tiebreak). */
    static void spendFloat() throws GameActionException {
        MapLocation t = fieldTargetFrom(G.me);
        Direction d = t == null ? G.DIRS[G.rand(8)] : G.me.directionTo(t);
        Direction[] ds = {d, d.rotateLeft(), d.rotateRight(), d.rotateLeft().rotateLeft(), d.rotateRight().rotateRight()};
        for (Direction x : ds) {
            MapLocation l = G.me.add(x);
            if (G.rc.canBuild(TrapType.STUN, l)) { G.rc.build(TrapType.STUN, l); floatTraps++; return; }
        }
    }

    static void placeCombatTrap() throws GameActionException {
        RobotController rc = G.rc;
        if (!rc.isActionReady() || enemies.length < C.STUN_ENEMIES_MIN || rc.getCrumbs() < 100 + C.TRAP_RESERVE) return;
        // a stun trap on the tile toward the enemy mass
        int sx = 0, sy = 0;
        for (RobotInfo e : enemies) { sx += e.location.x; sy += e.location.y; }
        MapLocation c = new MapLocation(sx / enemies.length, sy / enemies.length);
        Direction d = G.me.directionTo(c);
        MapLocation t = G.me.add(d);
        if (rc.canBuild(TrapType.STUN, t)) rc.build(TrapType.STUN, t);
    }
}
