package b2rgc;

import battlecode.common.*;

/** Per-turn behaviour of every duck. Roles by creation index: 0..2 flag defenders, the rest the field army. */
public strictfp class Duck {
    static RobotInfo[] enemies = new RobotInfo[0], allies = new RobotInfo[0];
    static FlagInfo[] flags = new FlagInfo[0];
    static int carriedFlagId = -1;
    static MapLocation exploreTarget;
    static int exploreSince;

    static boolean isDefender() { return G.idx >= 0 && G.idx < 3 * C.DEFENDERS_PER_FLAG; }
    static boolean isRusher() { return G.idx >= 3 * C.DEFENDERS_PER_FLAG && G.idx < 3 * C.DEFENDERS_PER_FLAG + C.RUSHERS; }
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
        if (isRusher()) { rush(); return; }
        Micro.guardFlag = C.Z1HOLD ? droppedOwnFlag() : null;
        if (Micro.guardFlag != null && !isRusher()) {        // block 4 (z1hold): hold our dropped flag through its return window
            holdTurns++;
            G.note = "hold";
            if (enemies.length > 0) { Micro.fight(enemies, allies, Micro.guardFlag); }
            else { Nav.moveTo(Micro.guardFlag); Micro.tryHeal(allies); }
            return;
        }
        if (enemies.length > 0) {
            placeCombatTrap();
            if (C.MICRO_V2) Micro.objective = fieldTarget();
            Micro.fight(enemies, allies);
            G.note = "fight e" + enemies.length + " a" + allies.length;
            return;
        }
        if (isDefender() && alertedFlag() < 0) { defend(); Micro.tryHeal(allies); return; }
        Micro.tryHeal(allies);
        if (C.POST_SETUP_CRUMBS) {                         // T10 adoption: take the crumbs the dam was hiding
            MapLocation c = G.nearest(G.me, rc.senseNearbyCrumbs(-1));
            if (c != null) { Nav.moveTo(c); crumbDetours++; if (rc.isActionReady()) Micro.tryHeal(allies); return; }
        }
        MapLocation ft = fieldTarget();
        if (C.GROUP_MIN > 0 && !"chase".equals(G.note) && allies.length < C.GROUP_MIN && allies.length > 0) {
            // cohesion: too few friends in view to push; close on the visible group instead of trickling forward
            int sx = 0, sy = 0;
            for (RobotInfo a : allies) { sx += a.location.x; sy += a.location.y; }
            ft = new MapLocation(sx / allies.length, sy / allies.length);
            regroups++;
        }
        Nav.moveTo(ft);
        if (rc.isActionReady()) Micro.tryHeal(allies);
        if (rc.isActionReady() && rc.getCrumbs() > Math.max(C.FLOAT_CRUMBS, G.bankFloor())) spendFloat();
    }

    static int rushTurns;

    /** T1 offence copy: go straight for the nearest enemy flag; strike whatever is in reach on the way; never kite.
     *  Escort a friendly carrier in view (it feeds the relay). */
    static void rush() throws GameActionException {
        RobotController rc = G.rc;
        rushTurns++;
        G.note = "rush";
        MapLocation t = null;
        for (RobotInfo r : allies) if (r.hasFlag) { t = r.location; break; }
        if (t == null) for (FlagInfo f : flags) if (f.getTeam() == G.them && !f.isPickedUp()) { t = f.getLocation(); break; }
        if (t == null) t = C.RUSH_FLANK ? flankTarget() : fieldTargetFrom(G.me);
        if (enemies.length > 0) { Micro.fight(enemies, allies, t); return; }
        Nav.moveTo(t);
        Micro.tryHeal(allies);
    }

    /** T12 adoption: the enemy flag farthest from where our main army is heading (the army takes the flag nearest our
     *  spawn centroid), so the squad raids a flag the defenders are not fighting over. */
    static MapLocation flankTarget() throws GameActionException {
        RobotController rc = G.rc;
        MapLocation[] cands = new MapLocation[6]; int n = 0;
        for (int i = 0; i < 3; i++) {
            if (rc.readSharedArray(Comms.EF_STATE + i) != 0) continue;
            MapLocation l = Comms.dec(rc.readSharedArray(Comms.EF_LOC + i));
            if (l != null) cands[n++] = l;
        }
        for (MapLocation b : rc.senseBroadcastFlagLocations()) if (n < 6) cands[n++] = b;
        if (n == 0) return fieldTargetFrom(G.me);
        int sx = 0, sy = 0, k = 0;
        for (MapLocation c : G.spawnCenters) if (c != null) { sx += c.x; sy += c.y; k++; }
        MapLocation home = new MapLocation(sx / Math.max(1, k), sy / Math.max(1, k));
        MapLocation army = null; int ad = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) { int d = home.distanceSquaredTo(cands[i]); if (d < ad) { ad = d; army = cands[i]; } }
        MapLocation best = army; int bd = -1;
        for (int i = 0; i < n; i++) { int d = army.distanceSquaredTo(cands[i]); if (d > bd) { bd = d; best = cands[i]; } }
        return best;
    }

    static int holdTurns;

    /** Our flag lying on the ground away from its home (a carrier of theirs died): within Z1_RADIUS2, else null. */
    static MapLocation droppedOwnFlag() throws GameActionException {
        if (G.round <= C.SETUP_ROUNDS) return null;
        for (FlagInfo f : flags) {
            if (f.getTeam() != G.us || f.isPickedUp()) continue;
            int i = Comms.ourFlagIndex(f.getID());
            MapLocation home = i >= 0 ? Comms.flagHome(i) : null;
            if (home != null && f.getLocation().equals(home)) continue;
            if (G.me.distanceSquaredTo(f.getLocation()) <= C.Z1_RADIUS2) return f.getLocation();
        }
        return null;
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
                for (int i = 0; i < 3; i++) { MapLocation h = Comms.flagHome(i); if (h != null && f.getLocation().distanceSquaredTo(h) <= 36) Comms.alertOurFlag(i); }
            }
        }
        // our flags: alert if enemies are near a home flag
        if (enemies.length > 0) for (int i = 0; i < 3; i++) {
            MapLocation c = Comms.flagHome(i);
            if (c != null && G.me.distanceSquaredTo(c) <= 20) Comms.alertOurFlag(i);
        }
    }

    static void buyUpgrades() throws GameActionException {
        RobotController rc = G.rc;
        if (G.round < 600) return;
        GlobalUpgrade[] order = C.UPGRADE_ORDER == 1 ? new GlobalUpgrade[]{GlobalUpgrade.HEALING, GlobalUpgrade.ATTACK, GlobalUpgrade.CAPTURING}
                : C.UPGRADE_ORDER == 2 ? new GlobalUpgrade[]{GlobalUpgrade.CAPTURING, GlobalUpgrade.ATTACK, GlobalUpgrade.HEALING}
                : new GlobalUpgrade[]{GlobalUpgrade.ATTACK, GlobalUpgrade.HEALING, GlobalUpgrade.CAPTURING};
        for (GlobalUpgrade u : order) if (rc.canBuyGlobal(u)) { rc.buyGlobal(u); return; }
    }

    /** Spawn in the zone of our home flag (defenders) or the zone nearest the current field target. */
    static void trySpawn() throws GameActionException {
        RobotController rc = G.rc;
        MapLocation want;
        if (isDefender() || G.round <= 5) want = G.round <= 5 ? G.spawnCenters[G.idx % 3] : Comms.flagHome(homeFlag());
        else {
            int alerted = alertedFlag();
            MapLocation ch = carrierTarget(G.spawnCenters[G.idx % 3]);
            want = ch != null ? ch : alerted >= 0 ? Comms.flagHome(alerted) : fieldTargetFrom(G.spawnCenters[G.idx % 3]);
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
        if (isDefender()) { if (C.RELOCATE_FLAGS && !placed) relocateFlag(); else defend(); return; }
        if (C.SETUP_DIGS > 0) setupDig();
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
        else if (G.round >= C.DAM_TRAP_ROUND && !C.BUDGET_V1) damTrap();
        G.note = "gather";
    }

    static int digs;

    /** T4 offence copy: dig a checkerboard (x+y even) in our territory during setup for build XP (level sum,
     *  cheaper traps later). Parity keeps every land tile diagonally connected; spawn zones, flags and tiles next
     *  to the dam are skipped by the engine or by us. Dose: C.SETUP_DIGS per duck. */
    static void setupDig() throws GameActionException {
        RobotController rc = G.rc;
        if (digs >= C.SETUP_DIGS || !rc.isActionReady() || rc.getCrumbs() < 20 + C.DIG_RESERVE) return;
        for (Direction d : G.DIRS) {
            MapLocation t = G.me.add(d);
            if (((t.x + t.y) & 1) != 0 || !rc.canDig(t)) continue;
            boolean nearFlag = false;
            for (int i = 0; i < 3; i++) { MapLocation h = G.spawnCenters[i]; if (h != null && h.distanceSquaredTo(t) <= 8) nearFlag = true; }
            if (nearFlag) continue;
            if (C.DIG_SITE == 1 && !wallHugging(t)) continue;
            if (C.DIG_SITE == 2 && !behindSpawn(t)) continue;
            rc.dig(t); digs++; return;
        }
    }

    /** DIG_SITE 1: a tile with at least 3 of its 8 neighbours wall or off the map lies off the walking lanes. */
    static boolean wallHugging(MapLocation t) throws GameActionException {
        int n = 0;
        for (Direction d : G.DIRS) {
            MapLocation x = t.add(d);
            if (!G.rc.onTheMap(x)) n++;
            else if (G.rc.canSenseLocation(x) && G.rc.senseMapInfo(x).isWall()) n++;
        }
        return n >= 3;
    }

    /** DIG_SITE 2: farther from the enemy (believed flag) than our nearest spawn centre, i.e. behind the line our
     *  ducks walk from spawn to the dam. */
    static boolean behindSpawn(MapLocation t) throws GameActionException {
        MapLocation s = G.nearest(G.me, G.spawnCenters);
        if (s == null) return false;
        MapLocation e = fieldTargetFrom(s);
        return e != null && t.distanceSquaredTo(e) > s.distanceSquaredTo(e);
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

    static int relays;

    /** T3 offence copy: hand the flag forward to an ally (the carrier moves at +20 cooldown; a relay keeps it moving). */
    static void relay(MapLocation home) throws GameActionException {
        RobotController rc = G.rc;
        if (!rc.isActionReady()) return;
        MapLocation me = rc.getLocation();
        int dMe = me.distanceSquaredTo(home);
        RobotInfo[] near = rc.senseNearbyRobots(8, G.us);
        if (near.length == 0) return;
        MapLocation best = null; int bd = dMe;
        for (Direction d : G.DIRS) {
            MapLocation t = me.add(d);
            int dt = t.distanceSquaredTo(home);
            if (dt >= bd || !rc.canDropFlag(t)) continue;
            boolean taker = false;
            for (RobotInfo a : near) if (!a.hasFlag && a.location.distanceSquaredTo(t) <= 2 && !a.location.equals(t)) { taker = true; break; }
            if (taker) { bd = dt; best = t; }
        }
        if (best != null) { rc.dropFlag(best); relays++; carriedFlagId = -1; }
    }

    static void carryFlag() throws GameActionException {
        RobotController rc = G.rc;
        if (G.round <= C.SETUP_ROUNDS) {    // our own flag in setup: only a relocating defender carries one
            if (isDefender() && C.RELOCATE_FLAGS) { relocateFlag(); return; }
            if (rc.canDropFlag(G.me)) rc.dropFlag(G.me);
            return;
        }
        MapLocation home = G.nearest(G.me, G.spawns);
        G.note = "carry";
        if (!(C.CARRY_SAFE && enemies.length > 0 && safeCarryStep(home))) Nav.moveTo(home);
        if (!rc.hasFlag() && carriedFlagId >= 0) { Comms.enemyFlagCaptured(carriedFlagId); carriedFlagId = -1; return; }
        if (C.RELAY && rc.hasFlag()) relay(home);
    }

    static int safeSteps;

    /** CARRY_SAFE: among the steps that bring the carrier closer to home, the one with the fewest enemies within
     *  dist2 10 (able to strike next turn), ties to the nearer tile. Only when it is safer than the plain homeward step. */
    static boolean safeCarryStep(MapLocation home) throws GameActionException {
        RobotController rc = G.rc;
        if (!rc.isMovementReady()) return false;
        int d0 = G.me.distanceSquaredTo(home);
        Direction best = null; int bs = Integer.MAX_VALUE, direct = Integer.MAX_VALUE;
        Direction straight = G.me.directionTo(home);
        for (Direction d : G.DIRS) {
            if (!rc.canMove(d)) continue;
            MapLocation l = G.me.add(d);
            int dh = l.distanceSquaredTo(home);
            if (dh >= d0) continue;
            int sc = Micro.threat(l, enemies) * 100000 + dh;
            if (d == straight) direct = sc;
            if (sc < bs) { bs = sc; best = d; }
        }
        if (best == null || best == G.me.directionTo(home) || bs / 100000 >= direct / 100000) return false;
        rc.move(best); G.me = rc.getLocation(); safeSteps++;
        return true;
    }

    // ------------------------------------------------------------------ targets
    /** Where the army goes when nothing is in sight. */
    static MapLocation fieldTarget() throws GameActionException {
        // defend a flag under attack if we are its defender or close to it
        int a = alertedFlag();
        MapLocation chase = carrierTarget(G.me);
        if (chase != null) { G.note = "chase"; return chase; }
        if (isDefender()) return defendTarget();
        if (a >= 0 && G.me.distanceSquaredTo(Comms.flagHome(a)) <= C.ALERT_RADIUS2) return Comms.flagHome(a);
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

    static MapLocation hintAnchor, sweepPt;
    static int hintSweeps;

    /** Block 2 (research/TACTIC_LEVELS.md): a broadcast hint lies anywhere within dist2 100 of the flag, but vision is
     *  dist2 20, so walking to the hint and stopping leaves ducks idle beside it for up to 100 rounds. Sweep the disc
     *  instead: a fresh random point within dist2 100 of the hint each time the current one is reached. */
    static MapLocation sweepHint(MapLocation hint) {
        if (hintAnchor == null || !hint.equals(hintAnchor)) { hintAnchor = hint; sweepPt = hint; }
        if (G.me.distanceSquaredTo(sweepPt) <= 8) {
            for (int k = 0; k < 6; k++) {
                int dx = G.rand(21) - 10, dy = G.rand(21) - 10;
                if (dx * dx + dy * dy > 100) continue;
                int x = hint.x + dx, y = hint.y + dy;
                if (!G.onMap(x, y)) continue;
                sweepPt = new MapLocation(x, y); hintSweeps++; break;
            }
        }
        return sweepPt;
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
        if (best != null && C.HINT_SWEEP && G.me != null && from.equals(G.me)) return sweepHint(best);
        if (best != null) return best;
        return G.nearest(from, Sym.enemyCenters());                    // last resort: enemy spawn centres by symmetry
    }

    // ------------------------------------------------------------------ defence
    static MapLocation defendTarget() throws GameActionException {
        return Comms.flagHome(homeFlag());
    }

    static boolean placed;
    static MapLocation flagTarget;
    static int relocStall;

    /** Target spots for our three flags: far from the enemy, 8+ apart (engine needs > 6). Deterministic, so all agree. */
    static int[] rcx = new int[3], rcy = new int[3];
    static int relocK = 0, rex = -1, rey;

    /** Target spots for our flags, one per call (bytecode): far from the enemy, 8+ apart (engine needs > 6).
     *  Deterministic, so every defender computes the same list. Returns null until spot i is ready. */
    static MapLocation relocTarget(int i) {
        if (rex < 0) {
            MapLocation[] ec = Sym.enemyCenters();
            int ex = 0, ey = 0, n = 0;
            for (MapLocation e : ec) if (e != null) { ex += e.x; ey += e.y; n++; }
            rex = ex / Math.max(1, n); rey = ey / Math.max(1, n);
            return null;
        }
        if (relocK <= i) {
            int k = relocK;
            int step = Math.max(3, Math.max(G.W, G.H) / 9);           // at most ~100 grid points per call
            int bx = -1, by = -1, bs = Integer.MIN_VALUE;
            int sx = G.spawnCenters[k].x, sy = G.spawnCenters[k].y;
            for (int x = 2; x < G.W - 2; x += step) for (int y = 2; y < G.H - 2; y += step) {
                boolean ok = true;
                for (int j = 0; j < k; j++) { int dx = rcx[j] - x, dy = rcy[j] - y; if (dx * dx + dy * dy < 64) { ok = false; break; } }
                if (!ok) continue;
                int dex = x - rex, dey = y - rey, dsx = x - sx, dsy = y - sy;
                int sc = dex * dex + dey * dey - (dsx * dsx + dsy * dsy) / 2;   // far from the enemy, not far from our spawn
                if (sc > bs) { bs = sc; bx = x; by = y; }
            }
            rcx[k] = bx; rcy[k] = by; relocK++;
            if (relocK <= i) return null;
        }
        return rcx[i] < 0 ? G.spawnCenters[i] : new MapLocation(rcx[i], rcy[i]);
    }

    static int relocs;

    static void relocateFlag() throws GameActionException {
        RobotController rc = G.rc;
        int i = homeFlag();
        G.note = "reloc";
        if (!rc.hasFlag()) {
            MapLocation c = G.spawnCenters[i];
            if (G.round > 5 && G.me.distanceSquaredTo(c) > 2 && !rc.canSenseLocation(c)) { placed = true; return; }   // lost it: give up
            if (rc.canPickupFlag(c)) { rc.pickupFlag(c); flagTarget = null; relocStall = 0; return; }   // target next turn (bytecode)
            if (G.me.distanceSquaredTo(c) > 2) { Nav.moveTo(c); return; }
            if (G.round > 20) placed = true;       // cannot pick it up: leave it at spawn
            return;
        }
        if (flagTarget == null) { flagTarget = relocTarget(i); return; }   // one spot per turn (bytecode)
        boolean there = G.me.distanceSquaredTo(flagTarget) <= 2;
        int before = G.me.distanceSquaredTo(flagTarget);
        if (!there && G.round < C.RELOC_DEADLINE) {
            Nav.moveTo(flagTarget);
            if (G.rc.getLocation().distanceSquaredTo(flagTarget) >= before) relocStall++; else relocStall = 0;
            if (relocStall < C.RELOC_STALL) return;
        }
        // drop here if the engine will accept it as a default location
        if (rc.senseLegalStartingFlagPlacement(G.me) && rc.canDropFlag(G.me)) {
            rc.dropFlag(G.me);
            rc.writeSharedArray(Comms.OF_HOME + i, Comms.enc(G.me));
            placed = true; relocs++;
        } else Nav.moveTo(G.spawnCenters[i]);   // illegal spot: walk back toward spawn and try again
    }

    static int ringStep;

    static void defend() throws GameActionException {
        RobotController rc = G.rc;
        MapLocation home = defendTarget();
        if (home == null) return;
        G.note = "defend";
        if (G.me.distanceSquaredTo(home) > C.RING_RADIUS2) { Nav.moveTo(home); return; }
        // T1 neutralization: a stun trap ON the flag tile fires when a raider steps within dist2 2 of it, which is
        // exactly where a pickup must be made from; the raider's action cooldown goes to 40 (about 3 turns)
        if (C.FLAG_TILE_STUN && rc.isActionReady() && rc.getCrumbs() >= TrapType.STUN.buildCost && rc.canBuild(TrapType.STUN, home)) {
            rc.build(TrapType.STUN, home); flagTileTraps++;
        }
        // ring the flag with traps: stun where dist2 to the flag is 4..8, explosive adjacent to it
        if (rc.isActionReady()) {
            for (Direction d : G.DIRS) {
                MapLocation t = G.me.add(d);
                int dh = t.distanceSquaredTo(home);
                if (dh == 0 || dh > C.RING_RADIUS2) continue;
                TrapType tt = dh <= 2 ? TrapType.EXPLOSIVE : TrapType.STUN;
                if (rc.getCrumbs() >= tt.buildCost + Math.max(C.DEF_TRAP_RESERVE, G.bankFloor() == Integer.MAX_VALUE ? Integer.MAX_VALUE - tt.buildCost : G.bankFloor()) && rc.canBuild(tt, t)) { rc.build(tt, t); Duck.defTraps++; break; }
            }
        }
        // walk around the flag so every ring tile comes within reach
        MapLocation post = ringPost(home);
        if (G.me.equals(post) || !rc.canSenseLocation(post) || !rc.sensePassability(post)) ringStep++;
        Nav.moveTo(ringPost(home));
    }

    /** Posts around the flag: the 8 neighbours; with a ring wider than dist2 8, alternate laps two tiles out. */
    static MapLocation ringPost(MapLocation home) {
        Direction d = G.DIRS[ringStep & 7];
        return (C.RING_RADIUS2 > 8 && (ringStep & 8) != 0) ? home.add(d).add(d) : home.add(d);
    }

    static int defTraps, damTraps, flagTileTraps, crumbDetours, regroups;

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

    static int combatTraps, waterTraps;

    /** Combat traps: when the enemy is close enough to walk onto them next turn, put one on the adjacent tile that lies
     *  most toward the enemy mass (the engine forbids tiles next to an enemy). Explosive when the bank is large and the
     *  enemy is clumped (750 to all within dist2 4), else stun (freezes all within dist2 13). */
    static void placeCombatTrap() throws GameActionException {
        RobotController rc = G.rc;
        if (!rc.isActionReady() || enemies.length < C.STUN_ENEMIES_MIN || rc.getCrumbs() < 100 + C.TRAP_RESERVE) return;
        if (!C.TRAP_PLACEMENT_V2) {                       // g_iter1's placement, byte-for-byte behaviour
            int sx = 0, sy = 0;
            for (RobotInfo e : enemies) { sx += e.location.x; sy += e.location.y; }
            MapLocation c = new MapLocation(sx / enemies.length, sy / enemies.length);
            if (C.TRAP_TOWARD_NEAREST) {                   // aim at the enemy about to step in, not the group's centre
                RobotInfo n = null; int nd = Integer.MAX_VALUE;
                for (RobotInfo e : enemies) { int d = G.me.distanceSquaredTo(e.location); if (d < nd) { nd = d; n = e; } }
                c = n.location;
            }
            MapLocation t = G.me.add(G.me.directionTo(c));
            TrapType tt = TrapType.STUN;
            if (C.WATER_WHEN_WEAK && allies.length + 1 < enemies.length) tt = TrapType.WATER;   // T7 adoption: moat their approach when outnumbered
            if (rc.canBuild(tt, t)) { rc.build(tt, t); combatTraps++; if (tt == TrapType.WATER) waterTraps++; }
            return;
        }
        int sx = 0, sy = 0, close = 0;
        for (RobotInfo e : enemies) { sx += e.location.x; sy += e.location.y; if (G.me.distanceSquaredTo(e.location) <= C.TRAP_ENEMY_DIST2) close++; }
        if (close == 0) return;
        MapLocation c = new MapLocation(sx / enemies.length, sy / enemies.length);
        TrapType tt = (rc.getCrumbs() >= C.EXPLOSIVE_BANK && enemies.length >= 4) ? TrapType.EXPLOSIVE : TrapType.STUN;
        MapLocation best = null; int bd = Integer.MAX_VALUE;
        for (Direction d : G.DIRS) {
            MapLocation t = G.me.add(d);
            if (!rc.canBuild(tt, t)) continue;
            int x = t.distanceSquaredTo(c);
            if (x < bd) { bd = x; best = t; }
        }
        if (best != null && best.distanceSquaredTo(c) < G.me.distanceSquaredTo(c)) { rc.build(tt, best); combatTraps++; }
    }
}
