package g4econ;

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
        if (C.TRACK) Track.roundStart();                   // S0b: every robot, jailed included; the first of ours each round expires tracks
        if (C.REG_FIX) {                                   // audit A5: jailed while holding a carried id = our carrier died
            if (carriedFlagId >= 0 && !rc.isSpawned()) { Comms.enemyFlagDropped(carriedFlagId); carriedFlagId = -1; }
            if (G.round > 200) Comms.expireDrops();         // audit A6
        }
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
        if (C.CARRIER_STUN && enemies.length > 0) carrierStun();
        if (enemies.length > 0 && (!C.REACH_FIX || Micro.engageable(enemies))) {   // A4: unreachable enemies do not freeze us
            placeCombatTrap();
            if (C.ESCORT_CARRIER) {                         // C9: travel with our carrier so a death is re-grabbed at once
                for (RobotInfo a : allies) {
                    if (!a.hasFlag || G.me.distanceSquaredTo(a.location) > C.ESCORT_R2) continue;
                    MapLocation home = G.nearest(a.location, G.spawns);
                    MapLocation goal = home == null ? a.location
                            : C.ESCORT_BEHIND ? a.location.subtract(a.location.directionTo(home)) : a.location.add(a.location.directionTo(home));
                    if (C.ESCORT_TIGHT) { Micro.escortAnchor = a.location; Micro.anchorR2 = C.ESCORT_TIGHT_R2; }   // g3escrg's escorts fought and fell behind
                    Micro.fight(enemies, allies, goal); Micro.escortAnchor = null; escortTurns++; G.note = "escort"; return;
                }
            }
            if (C.INTERCEPT) {                              // C10a: do not let a carrier walk past a fight
                MapLocation ct = carrierTarget(G.me);
                if (ct != null && G.me.distanceSquaredTo(ct) <= C.INTERCEPT_R2) {
                    Micro.fight(enemies, allies, ct); intercepts++; G.note = "icpt"; return;
                }
            }
            if (C.MICRO_V2 || C.HOLD_DRIFT > 0) Micro.objective = fieldTarget();
            if (C.DEF_TETHER && isDefender() && !carryingOwn(homeFlag())) {   // audit BOT2: fight beside the flag, not elsewhere
                MapLocation home = defendTarget();
                Micro.escortAnchor = home; Micro.anchorR2 = C.TETHER_R2;
                Micro.fight(enemies, allies, home); Micro.escortAnchor = null; tetherTurns++;
                G.note = "tether e" + enemies.length; return;
            }
            Micro.fight(enemies, allies);
            G.note = "fight e" + enemies.length + " a" + allies.length;
            return;
        }
        if (isDefender() && (C.ALERT_FIX ? !alertFresh(homeFlag()) : alertedFlag() < 0)) { defend(); Micro.tryHeal(allies); return; }
        boolean flagStep = C.PICKUP_AFTER_MOVE && rc.isMovementReady() && Micro.looseFlagNear(G.me) != null;
        if (!flagStep) Micro.tryHeal(allies);                // BOT5: keep the action for the pickup after the step
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
        if (C.PICKUP_AFTER_MOVE && rc.isActionReady() && !rc.hasFlag()) {   // BOT5: pickup is legal after a move
            G.me = rc.getLocation(); pickupFlags();
            if (rc.hasFlag()) { Micro.afterMovePickups++; G.note = "pickstep"; return; }
        }
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
            if (C.TRACK) Track.sight(f);                   // S0b: our flag in view -> its shared track (slots 25-33)
            if (f.getTeam() == G.us && f.isPickedUp()) {
                int i = Comms.ourFlagIndex(f.getID());
                if (i >= 0) Comms.reportCarried(i, f.getLocation());
            } else if (C.RELOCATE_FLAGS && G.round > C.SETUP_ROUNDS && G.round <= C.SETUP_ROUNDS + 3 && f.getTeam() == G.us) {
                int i = Comms.ourFlagIndex(f.getID());       // audit A12: the engine's r200 placement is the home
                if (i >= 0 && G.rc.readSharedArray(Comms.OF_HOME + i) != Comms.enc(f.getLocation())) G.rc.writeSharedArray(Comms.OF_HOME + i, Comms.enc(f.getLocation()));
            } else if (C.DEST_CAMP && f.getTeam() == G.us) {   // audit A11(a): a camp must not outlive the carry
                int i = Comms.ourFlagIndex(f.getID());
                if (i >= 0) Comms.clearCarried(i);
            }
            if (f.getTeam() == G.them) Comms.reportEnemyFlag(f);
            if (Sym.OBSERVE && f.getTeam() == G.them) Sym.observeEnemyCentre(f.getID());   // audit A2: a flag id is its spawn centre
            else if (!C.ALERT_FIX && enemies.length > 0) {
                for (int i = 0; i < 3; i++) { MapLocation h = Comms.flagHome(i); if (h != null && f.getLocation().distanceSquaredTo(h) <= 36) Comms.alertOurFlag(i); }
            }
        }
        if (C.FLAG_LOST && G.round > C.SETUP_ROUNDS) trackLost();
        if (C.ALERT_FIX) {                                  // audit A1: an alert means an enemy within ALERT_THREAT_R2 of the home
            int lost = C.FLAG_LOST ? Comms.lostMask() : 0;
            if (enemies.length > 0) for (int i = 0; i < 3; i++) {
                if ((lost >> i & 1) != 0) continue;         // BOT1: no alerts for a flag that is gone
                RobotInfo t = Comms.threatTo(Comms.flagHome(i), enemies);
                if (t != null) { Comms.alertOurFlag(i, t.location); }
            }
        } else if (enemies.length > 0) for (int i = 0; i < 3; i++) {   // g_iter1: observer near a home sees any enemy
            MapLocation c = Comms.flagHome(i);
            if (c != null && G.me.distanceSquaredTo(c) <= 20) Comms.alertOurFlag(i);
        }
        if (C.REG_FIX && G.round > 200) for (int s = 0; s < 3; s++) Comms.clearIfEmpty(s, flags);   // audit A6
        if (C.TRACK) { Track.homes(); Track.negatives(); Track.psym(); }   // S0b: empty home/drop tile, negative sightings, private symmetry
    }

    static void buyUpgrades() throws GameActionException {
        RobotController rc = G.rc;
        if (G.round < 600) return;
        GlobalUpgrade[] order = C.UPGRADE_ORDER == 1 ? new GlobalUpgrade[]{GlobalUpgrade.HEALING, GlobalUpgrade.ATTACK, GlobalUpgrade.CAPTURING}
                : C.UPGRADE_ORDER == 2 ? new GlobalUpgrade[]{GlobalUpgrade.CAPTURING, GlobalUpgrade.ATTACK, GlobalUpgrade.HEALING}
                : C.UPGRADE_ORDER == 3 ? new GlobalUpgrade[]{GlobalUpgrade.ATTACK, GlobalUpgrade.CAPTURING, GlobalUpgrade.HEALING}
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



    /** Audit A1: our flag i's alert is fresh (raised within 10 rounds). */
    static boolean alertFresh(int i) throws GameActionException {
        int r = G.rc.readSharedArray(Comms.OF_ALERT + i);
        return r > 0 && G.round - r <= 10;
    }

    static int alertedFlag() throws GameActionException {
        return alertedFlagSkipping(C.FLAG_LOST ? Comms.lostMask() : 0);
    }

    /** The freshest alerted flag (within 10 rounds) among those not in lostMask; -1 if none. */
    static int alertedFlagSkipping(int lostMask) throws GameActionException {
        int best = -1, br = 0;
        for (int i = 0; i < 3; i++) {
            if ((lostMask >> i & 1) != 0) continue;
            int r = G.rc.readSharedArray(Comms.OF_ALERT + i);
            if (r > 0 && G.round - r <= 10 && r > br) { br = r; best = i; }
        }
        return best;
    }

    /** C.FLAG_LOST (audit 2026-10-03 BOT1): a flag seen anywhere refreshes OF_SEEN and clears its lost bit; a home in
     *  view with its flag nowhere in sight for C.LOST_AFTER rounds sets it. A long unseen carry can set it too, and the
     *  flag's return home clears it again. */
    static void trackLost() throws GameActionException {
        RobotController rc = G.rc;
        int lost = Comms.lostMask(), nl = lost;
        for (int i = 0; i < 3; i++) {
            boolean seen = false;
            for (FlagInfo f : flags) if (f.getTeam() == G.us && Comms.ourFlagIndex(f.getID()) == i) { seen = true; break; }
            if (seen) {
                if (rc.readSharedArray(Comms.OF_SEEN + i) != G.round) rc.writeSharedArray(Comms.OF_SEEN + i, G.round);
                nl &= ~(1 << i);
            } else if ((lost >> i & 1) == 0) {
                MapLocation h = Comms.flagHome(i);
                if (h != null && rc.canSenseLocation(h) && G.round - rc.readSharedArray(Comms.OF_SEEN + i) > C.LOST_AFTER) nl |= 1 << i;
            }
        }
        if (nl != lost) rc.writeSharedArray(Comms.OF_LOST, nl);
    }

    /** The home to defend for flag i: its own while it exists, else the nearest live home (lostMask bit set). */
    static MapLocation liveHome(int i, int lostMask) throws GameActionException {
        if ((lostMask >> i & 1) == 0) return Comms.flagHome(i);
        MapLocation mine = Comms.flagHome(i), best = null; int bd = Integer.MAX_VALUE;
        for (int j = 0; j < 3; j++) {
            if ((lostMask >> j & 1) != 0) continue;
            MapLocation h = Comms.flagHome(j); int d = mine.distanceSquaredTo(h);
            if (d < bd) { bd = d; best = h; }
        }
        return best != null ? best : mine;
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
                if (C.REG_FIX && !rc.hasFlag()) { Comms.enemyFlagCaptured(carriedFlagId); carriedFlagId = -1; return; }   // A5: picked up inside our zone = captured
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
        if (a >= 0 && G.me.distanceSquaredTo(Comms.flagHome(a)) <= C.ALERT_RADIUS2) {
            if (!C.ALERT_FIX) return Comms.flagHome(a);
            MapLocation th = Comms.threatAt(a);                // audit A1: to the threat, never onto the flag tile
            if (th != null) { G.note = "threat"; return th; }
        }
        // a visible dropped enemy flag
        for (FlagInfo f : flags) if (f.getTeam() == G.them && !f.isPickedUp()) return f.getLocation();
        // escort a friendly carrier we can see
        for (RobotInfo r : allies) if (r.hasFlag) return r.location;
        if (C.ESCORT_FAR_R2 > 0) for (int s = 0; s < 3; s++) {   // a friendly carrier beyond vision, from the registry
            if (G.rc.readSharedArray(Comms.EF_STATE + s) != 1) continue;
            MapLocation l = Comms.dec(G.rc.readSharedArray(Comms.EF_LOC + s));
            if (l != null && G.me.distanceSquaredTo(l) <= C.ESCORT_FAR_R2) { G.note = "escortfar"; return l; }
        }
        if (C.HOLD_LINE && G.round < C.HOLD_UNTIL) { MapLocation fp = frontPoint(); if (fp != null) { holdTurns++; return fp; } }
        return fieldTargetFrom(G.me);
    }

    /** HOLD_LINE: front point i = HOLD_DEPTH_TENTHS/10 of the way from our spawn centre i to its mirror image; ducks
     *  split evenly over the three by id. */
    static MapLocation frontPoint() {
        MapLocation[] ec = Sym.enemyCenters();
        for (int k = 0; k < 3; k++) {
            int i = (G.idx + k) % 3;
            MapLocation s = G.spawnCenters[i], e = ec[i];
            if (s == null || e == null) continue;
            return new MapLocation(s.x + (e.x - s.x) * C.HOLD_DEPTH_TENTHS / 10, s.y + (e.y - s.y) * C.HOLD_DEPTH_TENTHS / 10);
        }
        return null;
    }

    static int chases, intercepts, escortTurns;

    /** An enemy carrying our flag: chase it if close, else wait at the enemy spawn centre it is walking to. */
    static int tetherTurns;

    /** An enemy in view carries our flag i (its FlagInfo is picked up). DEF_TETHER: a defender may leave its ring only for
     *  its own flag's carrier. */
    static boolean carryingOwn(int i) {
        for (FlagInfo f : flags) if (f.getTeam() == G.us && f.isPickedUp() && Comms.ourFlagIndex(f.getID()) == i) return true;
        return false;
    }

    static MapLocation carrierTarget(MapLocation from) throws GameActionException {
        MapLocation best = null; int bd = Integer.MAX_VALUE;
        MapLocation[] ec = Sym.enemyCenters();
        for (int i = 0; i < 3; i++) {
            if (C.DEF_TETHER && isDefender() && i != homeFlag()) continue;   // BOT2: a defender chases only its own flag
            MapLocation c = Comms.carried(i, C.CARRY_FRESH);
            if (c == null) continue;
            MapLocation dest = G.nearest(c, ec);
            int dc = from.distanceSquaredTo(c);
            MapLocation t = (dest != null && dc > C.CHASE_RADIUS2 && from.distanceSquaredTo(dest) < dc) ? dest : c;
            int d = from.distanceSquaredTo(t);
            if (d < bd) { bd = d; best = t; }
        }
        if (best == null && C.DEST_CAMP) best = campTarget(from, ec);
        if (best != null) chases++;
        return best;
    }

    static int camps;

    /** DEST_CAMP: a carrier last seen `age` rounds ago at c heads for the enemy spawn centre nearest c; it needs about
     *  2 rounds a tile (move cd 20). A duck that can reach that centre first (1 tile a round) waits there, while the
     *  sighting is young enough that the carrier may still be on its way (age <= 2*dist + 10). */
    static MapLocation campTarget(MapLocation from, MapLocation[] ec) throws GameActionException {
        MapLocation best = null; int bd = Integer.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            int age = Comms.carriedAge(i);
            if (age <= C.CARRY_FRESH || age == Integer.MAX_VALUE) continue;
            MapLocation c = Comms.carriedLast(i);
            if (c == null) continue;
            MapLocation dest = C.CAMP_SPLIT ? campDest(c, ec) : G.nearest(c, ec);
            if (dest == null) continue;
            int left = 2 * (int) Math.sqrt(c.distanceSquaredTo(dest)) - age;   // carrier rounds still needed, roughly
            if (left + 10 < 0) continue;                                      // it has arrived or died by now
            int mine = (int) Math.sqrt(from.distanceSquaredTo(dest));
            if (mine > left + 4) continue;                                    // we cannot get there first
            int d = from.distanceSquaredTo(dest);
            if (d < bd) { bd = d; best = dest; }
        }
        if (best != null) camps++;
        return best;
    }

    /** C.CAMP_SPLIT (Cyril crack): a carrier heads for one of its spawns, not always the straight-line nearest (Joker: it
     *  captured at a spawn 36.2 tiles away rather than the one at 35.5). Every enemy spawn centre within 1.3x the nearest
     *  distance of the last sighting is a candidate; campers split over them by robot id. */
    static MapLocation campDest(MapLocation c, MapLocation[] ec) {
        int near = Integer.MAX_VALUE;
        for (MapLocation e : ec) if (e != null) near = Math.min(near, c.distanceSquaredTo(e));
        if (near == Integer.MAX_VALUE) return null;
        MapLocation[] cand = new MapLocation[ec.length]; int n = 0;
        for (MapLocation e : ec) if (e != null && c.distanceSquaredTo(e) * 100 <= near * 169) cand[n++] = e;
        for (int a = 0; a < n; a++) for (int b = a + 1; b < n; b++)     // stable order: nearest first
            if (c.distanceSquaredTo(cand[b]) < c.distanceSquaredTo(cand[a])) { MapLocation t = cand[a]; cand[a] = cand[b]; cand[b] = t; }
        return cand[G.id % n];
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
        return C.FLAG_LOST ? liveHome(homeFlag(), Comms.lostMask()) : Comms.flagHome(homeFlag());
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
        if (C.RELOC_V2) return relocTargetV2(i);
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

    /** RELOC_V2 (2026-10-03, waffle crack: our flags within 20 tiles of its spawns fell 88% of the time, median r358;
     *  28-36 tiles 50%). Waits for observed symmetry (or C.RELOC_DECIDE). Then, most exposed flag first, each flag takes
     *  the spot within C.RELOC_R2 of its spawn centre (the centre itself included, so no flag ends up closer) that
     *  maximises the distance to the NEAREST enemy spawn centre under every symmetry still possible, 8+ tiles from the
     *  spots already taken. (Iteration 2 used one guessed symmetry's centroid; a greedy version without the walk bound
     *  moved a corner flag first and pushed another toward the centre, DefaultMedium 32 -> 27 tiles.) */
    static MapLocation relocTargetV2(int i) {
        if (!Sym.decided() && G.round < C.RELOC_DECIDE) return null;
        if (relocK == 0) {                                   // live enemy centres, and the order: most exposed first
            relocNe = 0;
            for (int sym = Sym.ROT; sym <= Sym.FY; sym <<= 1)
                if ((Sym.cands & sym) != 0) for (MapLocation c : G.spawnCenters) if (c != null) relocEc[relocNe++] = Sym.image(c, sym);
            int[] ex = new int[3];
            for (int k = 0; k < 3; k++) { relocOrder[k] = k; ex[k] = G.spawnCenters[k] == null ? Integer.MAX_VALUE : nearestEnemy2(G.spawnCenters[k].x, G.spawnCenters[k].y); }
            for (int p = 0; p < 3; p++) for (int q = p + 1; q < 3; q++)
                if (ex[relocOrder[q]] < ex[relocOrder[p]]) { int t = relocOrder[p]; relocOrder[p] = relocOrder[q]; relocOrder[q] = t; }
            for (int k = 0; k < 3; k++) rcx[k] = -1;
        }
        while (relocK < 3 && rcx[i] < 0) {                   // one flag per call (bytecode)
            int k = relocOrder[relocK];
            MapLocation sc0 = G.spawnCenters[k];
            int r = (int) Math.sqrt(C.RELOC_R2), sx = sc0.x, sy = sc0.y, step = Math.max(relocNe > 3 ? 4 : 3, r / 5);   // <= ~85 points x centres
            int bx = sx, by = sy, bs = spotFree(sx, sy, k, 36) ? nearestEnemy2(sx, sy) : Integer.MIN_VALUE, bw = 0;
            for (int x = Math.max(1, sx - r); x <= Math.min(G.W - 2, sx + r); x += step)
                for (int y = Math.max(1, sy - r); y <= Math.min(G.H - 2, sy + r); y += step) {
                    int w = (x - sx) * (x - sx) + (y - sy) * (y - sy);
                    if (w > C.RELOC_R2 || !spotFree(x, y, k, 64)) continue;
                    int sc = nearestEnemy2(x, y);
                    if (sc > bs || (sc == bs && w < bw)) { bs = sc; bx = x; by = y; bw = w; }
                }
            rcx[k] = bx; rcy[k] = by; relocK++;
            if (rcx[i] < 0) return null;
        }
        return new MapLocation(rcx[i], rcy[i]);
    }

    static MapLocation relocBest; static int relocBestScore = Integer.MIN_VALUE;   // RELOC_STALL_MOVES: best tile reached
    static MapLocation[] relocEc = new MapLocation[9];
    static int relocNe;
    static int[] relocOrder = new int[3];

    /** Squared distance from (x, y) to the nearest live enemy spawn centre (RELOC_V2). */
    static int nearestEnemy2(int x, int y) {
        int near = Integer.MAX_VALUE;
        for (int j = 0; j < relocNe; j++) { int dx = relocEc[j].x - x, dy = relocEc[j].y - y; near = Math.min(near, dx * dx + dy * dy); }
        return near;
    }

    /** A spot for flag k: at least min2 from every spot already taken (the engine resets all flags if two are within
     *  dist2 36), and a moved spot also 8+ tiles from the spawn centre of every flag not yet placed, so each later flag
     *  can still stay where it is (Whirlpool: an earlier flag took a spot beside another's spawn and forced it closer). */
    static boolean spotFree(int x, int y, int k, int min2) {
        for (int j = 0; j < 3; j++) {
            if (rcx[j] >= 0) { int dx = rcx[j] - x, dy = rcy[j] - y; if (dx * dx + dy * dy < min2) return false; }
            else if (j != k && min2 > 36 && G.spawnCenters[j] != null) {
                int dx = G.spawnCenters[j].x - x, dy = G.spawnCenters[j].y - y; if (dx * dx + dy * dy < 64) return false;
            }
        }
        return true;
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
            boolean ready = rc.isMovementReady();
            Nav.moveTo(flagTarget);
            MapLocation now = G.rc.getLocation();
            if (C.RELOC_STALL_MOVES) {      // audit BOT3(b): a carrier moves every second turn; count only turns it could move
                if (ready) { if (now.distanceSquaredTo(flagTarget) >= before) relocStall++; else relocStall = 0; }
                if (relocNe > 0) { int sc = nearestEnemy2(now.x, now.y); if (sc > relocBestScore) { relocBestScore = sc; relocBest = now; } }
            } else if (now.distanceSquaredTo(flagTarget) >= before) relocStall++; else relocStall = 0;
            if (C.RELOC_V2 && relocStall >= C.RELOC_STALL && !flagTarget.equals(G.spawnCenters[i])) {   // cannot get there:
                MapLocation home = G.spawnCenters[i];
                boolean useBest = C.RELOC_STALL_MOVES && relocBest != null && !flagTarget.equals(relocBest)
                        && relocBestScore > nearestEnemy2(home.x, home.y);
                flagTarget = useBest ? relocBest : home; relocStall = 0; return;   // BOT3: the best tile reached, else home
            }
            if (relocStall < C.RELOC_STALL) return;
        }
        // drop here if the engine will accept it as a default location
        if (rc.senseLegalStartingFlagPlacement(G.me) && rc.canDropFlag(G.me)) {
            rc.dropFlag(G.me);
            rc.writeSharedArray(Comms.OF_HOME + i, Comms.enc(G.me));
            placed = true; relocs++;
        } else if (C.RELOC_V2) {                // illegal here (another flag placed within dist2 36): walk to a legal tile in
            MapLocation alt = legalDropNear();   // view; spawn may be the illegal area (Tunnels: still carrying at r200, the
            Nav.moveTo(alt != null ? alt : G.spawnCenters[i]);   // engine's unchecked drop reset all three flags)
        } else Nav.moveTo(G.spawnCenters[i]);   // illegal spot: walk back toward spawn and try again
    }

    /** RELOC_V2: the legal starting-flag tile within dist2 13 nearest the target (else farthest from our spawn centres). */
    static MapLocation legalDropNear() throws GameActionException {
        MapLocation best = null; int bs = Integer.MAX_VALUE;
        for (MapInfo m : G.rc.senseNearbyMapInfos(13)) {
            MapLocation l = m.getMapLocation();
            if (!m.isPassable() || !G.rc.senseLegalStartingFlagPlacement(l)) continue;
            int sc = flagTarget != null ? l.distanceSquaredTo(flagTarget) : G.me.distanceSquaredTo(l);
            if (sc < bs) { bs = sc; best = l; }
        }
        return best;
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
    static int carrierStuns;

    /** C.CARRIER_STUN (2026-10-03, waffle crack): a stun within dist2 8 of an enemy carrying our flag catches it (stun
     *  radius dist2 13) whoever triggers it, and a frozen carrier cannot move for ~4 turns, so a relay chain slows (vs
     *  waffle our stuns caught 7.6 carriers a game in wins, 4.7 in losses; almost none were built for it). Built ahead of
     *  the carrier on its way home; the build leaves the action cooldown under 10, so the duck can still attack. */
    static void carrierStun() throws GameActionException {
        RobotController rc = G.rc;
        if (!rc.isActionReady() || rc.getCrumbs() < TrapType.STUN.buildCost) return;
        RobotInfo car = null;
        for (RobotInfo e : enemies) if (e.hasFlag) { car = e; break; }
        if (car == null || G.me.distanceSquaredTo(car.location) > 18) return;
        MapLocation dest = G.nearest(car.location, Sym.enemyCenters());
        Direction h = dest == null ? Direction.CENTER : car.location.directionTo(dest);
        MapLocation ahead = car.location.add(h).add(h);
        MapLocation best = null; int bd = Integer.MAX_VALUE;
        for (Direction d : Direction.allDirections()) {
            MapLocation t = G.me.add(d);
            if (t.distanceSquaredTo(car.location) > 8 || !rc.canBuild(TrapType.STUN, t)) continue;
            int x = t.distanceSquaredTo(ahead);
            if (x < bd) { bd = x; best = t; }
        }
        if (best != null) { rc.build(TrapType.STUN, best); carrierStuns++; }
    }

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
