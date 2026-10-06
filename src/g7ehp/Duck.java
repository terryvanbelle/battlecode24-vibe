package g7ehp;

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
    /** C.BUILDERS: field ducks idx 9, 19, 29, 39, 49 are the stun builders (one in ten, never a defender or scout). */
    static boolean builderIdx(int idx) { return idx >= 6 && idx % 10 == 9; }
    static boolean isBuilder() { return C.BUILDERS && builderIdx(G.idx); }

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
        if (C.SPAWN_SAFE && G.round > C.SETUP_ROUNDS) reportSpawnZones();
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
        if (C.FINAL_COMPLETE && G.round >= C.FC_ROUND && enemies.length > 0 && fcDig(true)) { fcFightDigs++; fcFightAt = G.round; }   // g7fc: one dig from a level, before the fight
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
            if (C.CONTACT && contactDive()) return;         // g4contact: meet a small chain
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
        if (C.LEVEL_FARM && G.round >= C.FARM_ROUND && farmDig()) return;
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
        if (rc.isActionReady() && rc.getCrumbs() > Math.max(C.FLOAT_CRUMBS, G.bankFloor()) + (C.LATE_BANK && lateOn() ? C.BANK_CAP : 0)) spendFloat();
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
            if (C.CONTACT && f.getTeam() == G.us) contactSight(f);   // g4contact: our flag in view -> its track (slots 34-36, 62)
            if (f.getTeam() == G.us && f.isPickedUp()) {
                int i = Comms.ourFlagIndex(f.getID());
                if (i >= 0) Comms.reportCarried(i, f.getLocation());
            } else if (C.RELOCATE_FLAGS && G.round > C.SETUP_ROUNDS && G.round <= C.SETUP_ROUNDS + 3 && f.getTeam() == G.us) {
                int i = Comms.ourFlagIndex(f.getID());       // audit A12: the engine's r200 placement is the home
                if (i >= 0 && G.rc.readSharedArray(Comms.OF_HOME + i) != Comms.enc(f.getLocation())) G.rc.writeSharedArray(Comms.OF_HOME + i, Comms.enc(f.getLocation()));
            } else if ((C.DEST_CAMP || C.CARRY_PREDICT) && f.getTeam() == G.us) {   // audit A11(a): a camp or a prediction must not outlive the carry
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
            int alerted = C.ALERT_NEAREST ? alertSplit(C.FLAG_LOST ? Comms.lostMask() : 0) : alertedFlag();
            MapLocation ch = carrierTarget(G.spawnCenters[G.idx % 3]);
            want = ch != null ? ch : alerted >= 0 ? Comms.flagHome(alerted) : fieldTargetFrom(G.spawnCenters[G.idx % 3]);
        }
        if (want == null) want = G.spawns[0];
        int avoid = C.SPAWN_SAFE && !isDefender() && G.round > C.SETUP_ROUNDS ? hotZones() : 0;   // zones to skip (bit k)
        // try tiles nearest the wanted point first
        MapLocation best = null; int bd = Integer.MAX_VALUE;
        for (MapLocation s : G.spawns) {
            if (!rc.canSpawn(s)) continue;
            if (avoid != 0 && (avoid >> zoneOf(s) & 1) != 0) continue;
            int d = s.distanceSquaredTo(want);
            if (d < bd || (d == bd && G.rand(2) == 0)) { bd = d; best = s; }
        }
        if (best != null) { rc.spawn(best); if (avoid != 0) safeSpawns++; }
    }



    static int safeSpawns;   // C.SPAWN_SAFE: spawns placed with a hot zone skipped (indicator ss)

    /** C.SPAWN_SAFE: the spawn zone (nearest spawn centre) of a spawn tile. */
    static int zoneOf(MapLocation s) {
        int best = 0, bd = Integer.MAX_VALUE;
        for (int k = 0; k < 3; k++) if (G.spawnCenters[k] != null) { int d = s.distanceSquaredTo(G.spawnCenters[k]); if (d < bd) { bd = d; best = k; } }
        return best;
    }

    /** C.SPAWN_SAFE: bit k set for each zone with SAFE_MIN+ enemies reported in the last 2 rounds, only when some other zone has
     *  at most 1 (else nothing is skipped: every zone is contested and the usual choice stands). */
    static int hotZones() throws GameActionException {
        int hot = 0; boolean calm = false;
        for (int k = 0; k < 3; k++) {
            int v = G.rc.readSharedArray(Comms.SZ + k), n = G.round - (v & 2047) <= 2 ? v >> 11 : 0;
            if (n >= C.SAFE_MIN) hot |= 1 << k; else if (n <= 1) calm = true;
        }
        return calm ? hot : 0;
    }

    /** C.SPAWN_SAFE: a robot within dist2 36 of a spawn centre reports the enemies it sees near that centre this round. */
    static void reportSpawnZones() throws GameActionException {
        for (int k = 0; k < 3; k++) {
            MapLocation c = G.spawnCenters[k];
            if (c == null || G.me.distanceSquaredTo(c) > 36) continue;
            int n = 0;
            for (RobotInfo e : enemies) if (e.location.distanceSquaredTo(c) <= 36) n++;
            n = Math.min(15, n);
            int v = G.rc.readSharedArray(Comms.SZ + k);
            if ((v & 2047) != (G.round & 2047) || (v >> 11) < n) G.rc.writeSharedArray(Comms.SZ + k, (G.round & 2047) | n << 11);
        }
    }

    /** Audit A1: our flag i's alert is fresh (raised within 10 rounds). */
    static boolean alertFresh(int i) throws GameActionException {
        int r = G.rc.readSharedArray(Comms.OF_ALERT + i);
        return r > 0 && G.round - r <= 10;
    }

    static int alertedFlag() throws GameActionException {
        return alertedFlagSkipping(C.FLAG_LOST ? Comms.lostMask() : 0);
    }

    /** C.ALERT_NEAREST (audit 2026-10-03 BOT9: 11% of post-setup rounds have 2+ fresh alerts and the single freshest one
     *  shadowed the rest): the live freshly alerted flag whose home is nearest `from` within maxD2; -1 if none. */
    static int alertNearest(MapLocation from, int lostMask, int maxD2) throws GameActionException {
        int best = -1, bd = maxD2 + 1;
        for (int i = 0; i < 3; i++) {
            if ((lostMask >> i & 1) != 0 || !alertFresh(i)) continue;
            MapLocation h = Comms.flagHome(i);
            if (h == null) continue;
            int d = from.distanceSquaredTo(h);
            if (d < bd) { bd = d; best = i; }
        }
        return best;
    }

    /** C.ALERT_NEAREST: respawns split over every live fresh alert (by robot index), not all to the freshest. */
    static int alertSplit(int lostMask) throws GameActionException {
        int n = 0, a0 = -1, a1 = -1, a2 = -1;
        for (int i = 0; i < 3; i++) {
            if ((lostMask >> i & 1) != 0 || !alertFresh(i)) continue;
            if (n == 0) a0 = i; else if (n == 1) a1 = i; else a2 = i;
            n++;
        }
        if (n == 0) return -1;
        int k = G.idx % n;
        return k == 0 ? a0 : k == 1 ? a1 : a2;
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
        if (C.SETUP_DIGS > 0 || isBuilder()) setupDig();
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
        boolean b = isBuilder();   // C.BUILDERS: a builder digs to build level 6 (30 actions: traps cost half)
        if (digs >= (b ? C.BUILDER_DIGS : C.SETUP_DIGS) || !rc.isActionReady() || rc.getCrumbs() < 20 + (b ? C.BUILDER_DIG_RESERVE : C.DIG_RESERVE)) return;
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

    /** C.LEVEL_FARM (Gymhgy loss study 2026-10-04: level sum decides tied games at r2000; we lose 0-0 level-sum games
     *  28-48 by a median 17 levels, never dig after setup, and 35 of 50 ducks end at build 0). From FARM_ROUND, with the
     *  flag counts level, an idle duck (no enemy in view, no fresh alert, no carrier to chase) below FARM_XP build XP digs
     *  a checkerboard tile on our half away from our flags: one build XP per dig (levels 1-3 at 5/10/15; heal mastery caps
     *  build at 3, and the jail penalty takes the highest skill, never build). True if it dug. */
    static boolean farmDig() throws GameActionException {
        RobotController rc = G.rc;
        if (!rc.isActionReady() || rc.getExperience(SkillType.BUILD) >= C.FARM_XP || rc.getCrumbs() < 20 + C.FARM_RESERVE) return false;
        if (alertedFlag() >= 0 || carrierTarget(G.me) != null || !capturesLevel()) return false;
        for (Direction d : G.DIRS) {
            MapLocation t = G.me.add(d);
            if (((t.x + t.y) & 1) != 0 || !rc.canDig(t) || !Micro.ourHalf(t)) continue;
            boolean nearFlag = false;
            for (int i = 0; i < 3; i++) { MapLocation h = Comms.flagHome(i); if (h != null && h.distanceSquaredTo(t) <= 8) nearFlag = true; }
            if (nearFlag) continue;
            rc.dig(t); farmDigs++; G.note = "farm"; return true;
        }
        return false;
    }

    static int farmDigs;   // LEVEL_FARM digs (indicator lf)

    /** LEVEL_FARM: our captures (registry slots in state 2) equal theirs (our flags marked lost). */
    static boolean capturesLevel() throws GameActionException {
        int ours = 0;
        for (int s = 0; s < 3; s++) if (G.rc.readSharedArray(Comms.EF_STATE + s) == 2) ours++;
        return ours == Integer.bitCount(Comms.lostMask());
    }

    static int lateAt = -1; static boolean lateLevel;
    /** The flag counts are level (capturesLevel), read once per turn; C.LATE_BANK and C.FINAL_COMPLETE. */
    static boolean levelNow() throws GameActionException {
        if (lateAt != G.round) { lateAt = G.round; lateLevel = capturesLevel(); }
        return lateLevel;
    }
    /** C.LATE_BANK: after BANK_R1 with the flag counts level. Switch-free, for the tests. */
    static boolean lateLevel() throws GameActionException { return G.round > C.BANK_R1 && levelNow(); }
    static boolean lateOn() throws GameActionException { return C.LATE_BANK && lateLevel(); }
    /** The rising bank line: 0 up to BANK_R1, BANK_PACE100 a hundred rounds after it, BANK_CAP from BANK_R2. Pure. */
    static int bankLine(int r) { return r <= C.BANK_R1 ? 0 : r >= C.BANK_R2 ? C.BANK_CAP : Math.min(C.BANK_CAP, (r - C.BANK_R1) * C.BANK_PACE100 / 100); }
    static boolean lbPaced;   // this combat build uses the team's paced close-stun slot
    static boolean lateTrapOk() throws GameActionException { lbPaced = false; return !lateOn() || lateTrapAllowed(); }
    /** For a combat stun that the old rule already allows (crumbs >= 100 + TRAP_RESERVE):
     *  - anything builds above the cap;
     *  - class B (an enemy within BANK_CLOSE_R2 of G.me) builds above the line, or on the team pace;
     *  - class A (an alerted flag fight) always builds;
     *  - class C waits. */
    static boolean lateTrapAllowed() throws GameActionException {
        int cr = G.rc.getCrumbs(), base = 100 + C.TRAP_RESERVE;
        if (cr >= base + C.BANK_CAP) return true;
        boolean close = Micro.enemyWithin(G.me, C.BANK_CLOSE_R2);
        if (close && cr >= base + bankLine(G.round)) return true;
        if (guardFight()) return true;
        if (close && G.round - G.rc.readSharedArray(Comms.LB_PACE) >= C.BANK_GAP) { lbPaced = true; return true; }
        return false;
    }
    /** Class A: G.me within ALERT_RADIUS2 of a live home whose alert is fresh (alertFresh: within the last 10 rounds).
     *  C.LATE_BANK's always-build class; C.FINAL_COMPLETE neither digs nor pauses the combat stun there. */
    static boolean guardFight() throws GameActionException {
        int lost = Comms.lostMask();
        for (int i = 0; i < 3; i++) {
            if ((lost >> i & 1) != 0 || !alertFresh(i)) continue;
            MapLocation h = Comms.flagHome(i);
            if (h != null && G.me.distanceSquaredTo(h) <= C.ALERT_RADIUS2) return true;
        }
        return false;
    }
    static void lateBuilt() throws GameActionException { if (lbPaced) { G.rc.writeSharedArray(Comms.LB_PACE, G.round); lbPaced = false; } }
    /** Our traps within RING_RADIUS2 of a home that this robot can sense. Tiles out of view count as empty, which errs toward re-arming. */
    static int ringCount(MapLocation home) throws GameActionException {
        int n = 0;
        for (MapInfo m : G.rc.senseNearbyMapInfos(home, C.RING_RADIUS2)) if (m.getTrapType() != TrapType.NONE) n++;
        return n;
    }
    static int digNeed(int xp) { return 5 - xp % 5; }
    /** Engine: round(20 x (1 + build%)), build% 0/-10/-15/-20/-30/-40 at build 0-5. */
    static int digCost(int lv) { return lv <= 0 ? 20 : lv == 1 ? 18 : lv == 2 ? 17 : lv == 3 ? 16 : lv == 4 ? 14 : 12; }
    /** Engine: round(DIG_COOLDOWN x (1 + build cd%)), build cd 0/-5/-10/-15/-20/-30 at build 0-5. */
    static int digCd(int lv) { return lv <= 3 ? 20 - lv : lv == 4 ? 16 : 14; }
    /** The round of the last of `need` digs, one on each turn the action is ready from this round on (the engine: act below
     *  10, every turn takes 10 off the cooldown, every dig adds `step`). Pure. */
    static int digDoneRound(int round, int cd, int step, int need) {
        for (int k = 1; ; k++) {
            for (; cd >= 10; cd -= 10) round++;
            if (k >= need) return round;
            cd += step;
        }
    }
    static int myOwed;   // C.LATE_BANK: this robot's share of Comms.LB_OWED, the crumbs its dump level in progress still needs
    /** Set this robot's share of Comms.LB_OWED to `want` (one write, only on a change). */
    static void owe(int want) throws GameActionException {
        if (want == myOwed) return;
        int o = G.rc.readSharedArray(Comms.LB_OWED) + want - myOwed;
        G.rc.writeSharedArray(Comms.LB_OWED, Math.max(0, Math.min(GameConstants.MAX_SHARED_ARRAY_VALUE, o)));
        myOwed = want;
    }
    /** Levels are committed team-wide: a robot that owes nothing starts (or resumes) a level only when the bank not owed to
     *  the levels in progress covers the rest of it (`cost`) plus `keep`; a robot already digging one checks the whole bank. Pure. */
    static boolean digAffordable(int crumbs, int owed, int mine, int cost, int keep) { return (mine > 0 ? crumbs : crumbs - owed) >= cost + keep; }
    /** The jail penalty never takes build at level `next` given attack/heal levels (mirrors InternalRobot.jailedPenalty). */
    static boolean digDurable(int next, int atk, int heal) { return next <= atk || next < heal; }
    /** Dump tile:
     *  - checkerboard (x+y even);
     *  - farther than RING_RADIUS2 from every home and from every flag in view;
     *  - all 4 orthogonal neighbours on the map and passable. */
    static boolean digSiteOk(MapLocation t, MapLocation[] homes, FlagInfo[] fl) throws GameActionException {
        if (((t.x + t.y) & 1) != 0) return false;
        for (MapLocation h : homes) if (h != null && h.distanceSquaredTo(t) <= C.RING_RADIUS2) return false;
        for (FlagInfo f : fl) if (f.getLocation().distanceSquaredTo(t) <= C.RING_RADIUS2) return false;
        for (int k = 0; k < 8; k += 2) { MapLocation n = t.add(G.DIRS[k]); if (!G.rc.onTheMap(n) || !G.rc.sensePassability(n)) return false; }
        return true;
    }
    /** C.LATE_BANK stage 2 (after BANK_R2). RobotPlayer calls it, only under the switch, after the whole turn of every robot,
     *  spawned or jailed, so it only uses a spare action. First the robot settles its share of LB_OWED: the rest of its level
     *  while it can still finish that level (spawned, flag counts level, a durable level below DIG_XP_MAX, done by r2000),
     *  else nothing; a jailed robot, a capture or a trap build that completes the level frees the crumbs for the others. */
    static void lateDig() throws GameActionException {
        RobotController rc = G.rc;
        if (G.round <= C.BANK_R2) return;
        int xp = rc.getExperience(SkillType.BUILD), lv = xp / 5, need = digNeed(xp), cost = digCost(lv);
        boolean live = rc.isSpawned() && lateLevel() && xp < C.DIG_XP_MAX && digDurable(lv + 1, rc.getLevel(SkillType.ATTACK), rc.getLevel(SkillType.HEAL))
                && digDoneRound(G.round, rc.getActionCooldownTurns(), digCd(lv), need) <= GameConstants.GAME_MAX_NUMBER_OF_ROUNDS;
        owe(live && myOwed > 0 && xp % 5 != 0 ? need * cost : 0);
        if (!live || rc.hasFlag() || !rc.isActionReady()) return;
        if (!digAffordable(rc.getCrumbs(), rc.readSharedArray(Comms.LB_OWED), myOwed, need * cost, G.round < C.DIG_ALLIN ? C.DIG_KEEP : C.DIG_KEEP_END)) return;
        if (G.bcLeft() < C.DIG_BC || rc.senseNearbyRobots(C.DIG_HOLD_R2, G.them).length > 0) return;
        FlagInfo[] fl = rc.senseNearbyFlags(-1);
        for (FlagInfo f : fl) if (f.getTeam() == G.them) return;     // a pickup keeps the action
        MapLocation me = rc.getLocation();
        MapLocation[] homes = {Comms.flagHome(0), Comms.flagHome(1), Comms.flagHome(2)};
        for (Direction d : G.DIRS) {
            MapLocation t = me.add(d);
            if (((t.x + t.y) & 1) != 0 || !rc.canDig(t) || !digSiteOk(t, homes, fl)) continue;
            rc.dig(t); farmDigs++; G.note = "dump"; owe((need - 1) * cost); return;
        }
    }

    static int fcDigs, fcFightDigs;   // C.FINAL_COMPLETE: digs (indicator fc); of them, made before a fight by the d=1 hook (fcF)
    static int fcFightAt;             // C.FINAL_COMPLETE: the round of this robot's last fight dig (its note; 0 = none)

    /** C.FINAL_COMPLETE: the most digs a level may still need for a robot to dig toward it at round r: 0 before FC_ROUND, then
     *  1, 2 from FC_K2, 3 from FC_K3, 4 from FC_K4 (the closest levels get the bank first). Pure. */
    static int fcK(int r) { return r < C.FC_ROUND ? 0 : r < C.FC_K2 ? 1 : r < C.FC_K3 ? 2 : r < C.FC_K4 ? 3 : 4; }

    /** C.FINAL_COMPLETE: the digs to the next build level of a robot with build XP xp, attack and heal levels atk and heal and
     *  action cooldown cd, when it may dig toward that level at round r; else 0. It may when:
     *  - its build XP still counts: below 30, and below 15 once attack or heal is level 4 (InternalRobot.incrementSkill; 0.1
     *    robots a game are capped, so this is cheap insurance);
     *  - the level needs at most fcK(r) digs;
     *  - the jail penalty never takes build at the new level (digDurable; 3.4% of candidates fail). Build level 4 or more never
     *    passes: only attack 4+ or heal 5+ would take the penalty instead, and either freezes build XP at 15. So only levels
     *    1-3 are dug;
     *  - one dig on each ready turn from cd finishes it by r2000 (digDoneRound). Pure. */
    static int fcNeed(int r, int xp, int atk, int heal, int cd) {
        int need = digNeed(xp), lv = xp / 5;
        if (xp >= (atk >= 4 || heal >= 4 ? 15 : 30) || need > fcK(r) || !digDurable(lv + 1, atk, heal)) return 0;
        return digDoneRound(r, cd, digCd(lv), need) <= GameConstants.GAME_MAX_NUMBER_OF_ROUNDS ? need : 0;
    }

    /** C.FINAL_COMPLETE: from FC_ROUND with the flag counts level (read once a turn). Switch-free, for the tests. The hooks test
     *  the round first (three bytecodes, no call), so before r1950 the arm spends almost exactly the incumbent's bytecode. */
    static boolean fcLevel() throws GameActionException { return G.round >= C.FC_ROUND && levelNow(); }

    /** C.FINAL_COMPLETE dig, both hooks (fight: the hook before the fight, which only a robot one dig from its level uses).
     *  The post-turn hook first settles this robot's share of Comms.LB_OWED: the rest of its level while it may still dig
     *  toward it (spawned, flag counts level, fcNeed), else nothing, so a jailed robot or a capture frees the crumbs for the
     *  others. Then one dig on a dump tile (digSiteOk) when:
     *  - the robot holds no flag and its action is ready;
     *  - it is not near an alerted home (guardFight: within ALERT_RADIUS2 of a live home with a fresh alert), so the
     *    responders keep their strikes and the bank stops draining there (review 2026-10-06: defend() never runs in a fight,
     *    so the dig took the raider's strike);
     *  - post-turn hook only: with an enemy within HOLD_R2 (HEAL_HOLD's held strike), the dig must leave the action ready
     *    next turn (cooldown + dig cooldown below COOLDOWN_LIMIT + COOLDOWNS_PER_TURN = 20; review 2026-10-06: a build-0
     *    dig, or any dig a turn after a strike at attack level 1+, cost the next strike);
     *  - the bank not owed to the levels in progress covers the rest of the level plus FC_FLOOR plus FC_KEEP[digs needed]
     *    (digAffordable: a robot already owing checks the whole bank), so the levels started can be finished;
     *  - no enemy flag and no carried flag is in view (a pickup, a strike on a carrier or a carrier stun keeps the action).
     *  True if it dug. */
    static boolean fcDig(boolean fight) throws GameActionException {
        RobotController rc = G.rc;
        int xp = rc.getExperience(SkillType.BUILD), cost = digCost(xp / 5);
        int need = rc.isSpawned() && fcLevel() ? fcNeed(G.round, xp, rc.getLevel(SkillType.ATTACK), rc.getLevel(SkillType.HEAL), rc.getActionCooldownTurns()) : 0;
        if (!fight) owe(need > 0 && myOwed > 0 && xp % 5 != 0 ? need * cost : 0);
        if (need == 0 || (fight && need > 1) || rc.hasFlag() || !rc.isActionReady() || guardFight()) return false;
        if (!fight && Micro.enemyWithin(rc.getLocation(), C.HOLD_R2)
                && rc.getActionCooldownTurns() + digCd(xp / 5) >= GameConstants.COOLDOWN_LIMIT + GameConstants.COOLDOWNS_PER_TURN) return false;
        if (!digAffordable(rc.getCrumbs(), rc.readSharedArray(Comms.LB_OWED), myOwed, need * cost, C.FC_FLOOR + C.FC_KEEP[need]) || G.bcLeft() < C.DIG_BC) return false;
        FlagInfo[] fl = rc.senseNearbyFlags(-1);
        for (FlagInfo f : fl) if (f.getTeam() == G.them || f.isPickedUp()) return false;
        MapLocation me = rc.getLocation();
        MapLocation[] homes = {Comms.flagHome(0), Comms.flagHome(1), Comms.flagHome(2)};
        for (Direction d : G.DIRS) {
            MapLocation t = me.add(d);
            if (!rc.canDig(t) || !digSiteOk(t, homes, fl)) continue;
            rc.dig(t); fcDigs++; owe((need - 1) * cost); return true;
        }
        return false;
    }

    /** C.FINAL_COMPLETE post-turn hook. RobotPlayer calls it from FC_ROUND, only under the switch, after the whole turn of every
     *  robot, spawned or jailed (after every early return in turn()) and after Sym.update, so it settles the share and digs only
     *  with a spare action (in the rare game whose symmetry is still undecided the observation may leave under DIG_BC). */
    static void finalDig() throws GameActionException {
        if (fcDig(false)) G.note = "fc " + G.note;
        else if (fcFightAt == G.round) G.note = "fcF " + G.note;
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
    static boolean relayWanted(boolean threatOnly, int hp, int enemiesInView) { return !threatOnly || hp < C.RELAY_HP || enemiesInView > 0; }

    static void relay(MapLocation home) throws GameActionException {
        RobotController rc = G.rc;
        if (!rc.isActionReady()) return;
        // C.RELAY_THREAT (g4relay 5(a): handing over whenever an ally stood ahead cut carrier moves 232 vs 290 against
        // Gymhgy, each hand-off costing a round): hand over only a carrier in danger, hurt or with an enemy in view
        if (!relayWanted(C.RELAY_THREAT, rc.getHealth(), enemies.length)) return;
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
        int a = C.ALERT_NEAREST ? alertNearest(G.me, C.FLAG_LOST ? Comms.lostMask() : 0, C.ALERT_RADIUS2) : alertedFlag();
        if (C.ALERT_NEAREST && a >= 0 && a != alertedFlag()) alertOther++;   // answered an alert the freshest one shadowed
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
    static int alertOther;   // ALERT_NEAREST: field turns spent on an alert other than the freshest (indicator an)

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
        if (C.CONTACT) ctLost = Comms.lostMask();
        for (int i = 0; i < 3; i++) {
            if (C.DEF_TETHER && isDefender() && i != homeFlag()) continue;   // BOT2: a defender chases only its own flag
            MapLocation c = Comms.carried(i, C.CARRY_FRESH);
            if (c == null && C.CARRY_PREDICT) {             // audit BOT10: a stale sighting still says where the carrier is going
                int age = Comms.carriedAge(i);
                MapLocation last = Comms.carriedLast(i);
                if (last != null && age != Integer.MAX_VALUE) c = predictCarrier(last, age, G.nearest(last, ec));
                if (c != null) predictTurns++;
            }
            if (C.CONTACT && c == null && ctAdmit(i, ctLost)) {
                MapLocation p = trackGoal(i, from, C.CHASE_RADIUS2);   // g4contact: a stale sighting's track point, no redirect
                if (p != null) { predictTurns++; int d = from.distanceSquaredTo(p); if (d < bd) { bd = d; best = p; } continue; }
            }
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

    static int camps, predictTurns;

    /** C.CARRY_PREDICT (audit BOT10): a carrier last seen `age` rounds ago at `last` has moved about age/2 tiles (move
     *  cooldown 20) toward `dest`; aim two steps beyond that. Null when the sighting is fresh (handled by the caller), too
     *  old (>= 60: the prediction fails from there, median error ~24 tiles), or the carrier should have arrived. */
    static MapLocation predictCarrier(MapLocation last, int age, MapLocation dest) {
        if (dest == null || age >= 60) return null;
        int steps = age / 2, need = Math.max(Math.abs(dest.x - last.x), Math.abs(dest.y - last.y));
        if (steps > need + C.PREDICT_MARGIN) return null;   // arrived (captured) or died on the way
        MapLocation p = last;
        for (int k = 0; k < Math.min(steps + 2, need) && !p.equals(dest); k++) p = p.add(p.directionTo(dest));
        return p;
    }

    // ------------------------------------------------------------------ C.CONTACT (arm g4contact)
    // Our flag i's latest off-home sighting (CT[i], round OF_SEEN[i]) is a track for CT_HOLD rounds. Observed grab groups
    // under CT_GROUP_MAX get an in-fight dive (chainGoal, capped per chain a round through CT_DIVE) and an out-of-fight chase
    // (carrierTarget). Slots 0-33 and 49-61 are never written here, so g_iter4's chase, its redirect and trySpawn are
    // unchanged for every chain the gates do not admit. No G.rand or G.nearest: ties go to the lower x, then the lower y.
    static final int[] ctSeenAt = {-1, -1, -1}, ctMissAt = {-1, -1, -1};   // round this robot saw our flag i / counted its miss
    static final MapLocation[] ctSeenLoc = new MapLocation[3];
    static final boolean[] ctSeenCarried = new boolean[3];
    static MapLocation[] ctEc;                          // Sym.enemyCenters(), cached per round and candidate set (key ctEcAt)
    static int ctEcAt, ctLost;                          // ctLost: carrierTarget's lostMask, read once a call
    static int ctEn, ctNeed;                            // trackGoal's out-values: the chain's en20 and its diver cap

    /** Sensor: our flag f in view (r204+, after the A12 home re-stamp window). At home: the chain is over (CT and its aux
     *  bits cleared). Elsewhere: CT = the tile and the high-water mark of enemies within dist2 20 over the live track; aux =
     *  our robots within dist2 20 this round, misses reset. Runs before this robot's trackLost, so OF_SEEN still holds the
     *  previous sighting's round. Change-only writes; at most two senses (100 bytecodes each, flat). */
    static void contactSight(FlagInfo f) throws GameActionException {
        if (G.round <= C.SETUP_ROUNDS + 3) return;
        RobotController rc = G.rc;
        int i = Comms.ourFlagIndex(f.getID());
        if (i < 0) return;
        MapLocation fl = f.getLocation(); boolean carried = f.isPickedUp();
        ctSeenAt[i] = G.round; ctSeenLoc[i] = fl; ctSeenCarried[i] = carried;
        int ct = rc.readSharedArray(Comms.CT + i), aux = rc.readSharedArray(Comms.CT_AUX);
        if (!carried && fl.equals(Comms.flagHome(i))) { contactHome(i, ct, aux); return; }
        int seen = rc.readSharedArray(Comms.OF_SEEN + i);
        boolean live = ct != 0 && G.round - seen <= C.CT_HOLD, same = live && seen == G.round;
        int loc = Comms.enc(fl), oen = Comms.ctEn(ct), oou = Comms.auxOu(aux, i);
        if (same && Comms.ctLoc(ct) == loc && oen >= Math.min(15, enemies.length) && oou >= Math.min(7, allies.length + 1)) return;   // nothing new
        int en = rc.senseNearbyRobots(fl, 20, G.them).length;                                         // carrier included
        int ou = 1 + (G.bcLeft() >= C.CT_SENSE_BC ? rc.senseNearbyRobots(fl, 20, G.us).length : 0);   // observer included
        int nen = Math.min(15, live ? Math.max(oen, en) : en);   // high-water mark, approximating the grab group (a decay of 1 a
        int nou = Math.min(7, same ? Math.max(oou, ou) : ou);    // round let a real 13-group read 11); ou20: this round only
        int nct = Comms.ctPack(loc, nen);
        if (nct != ct) rc.writeSharedArray(Comms.CT + i, nct);
        int na = Comms.auxSet(aux, i, 0, nou);                   // a positive sighting resets the misses
        if (na != aux) rc.writeSharedArray(Comms.CT_AUX, na);
    }

    /** Our flag i seen at home: its chain is over. */
    static void contactHome(int i, int ct, int aux) throws GameActionException {
        if (ct != 0) G.rc.writeSharedArray(Comms.CT + i, 0);
        int na = Comms.auxSet(aux, i, 0, 0);
        if (na != aux) G.rc.writeSharedArray(Comms.CT_AUX, na);
    }

    /** Where a chain last seen at l `age` rounds ago is now: age * 9/16 tiles toward its spawn centre d, stopping on the
     *  zone tile next to d. Null once the predicted arrival has passed (a capture, or a wrong track). Pure. */
    static MapLocation chainPoint(MapLocation l, MapLocation d, int age) {
        int m = age * C.CT_SPEED16 >> 4, n = Track.cheb(l.x, l.y, d.x, d.y) - 1;
        if (m > n + 1) return null;
        int k = Math.min(m, n);
        return k <= 0 ? l : Track.step(l.x, l.y, d.x, d.y, k);
    }

    /** The enemy spawn centre nearest l by dist2 (ties: lower x, then lower y); null if none. */
    static MapLocation ctDest(MapLocation l) {
        int key = G.round * 8 + Sym.cands;
        if (ctEcAt != key) { ctEc = Sym.enemyCenters(); ctEcAt = key; }
        MapLocation best = null; int bd = Integer.MAX_VALUE;
        for (MapLocation e : ctEc) {
            if (e == null) continue;
            int d = l.distanceSquaredTo(e);
            if (d < bd || (d == bd && (e.x < best.x || (e.x == best.x && e.y < best.y)))) { bd = d; best = e; }
        }
        return best;
    }

    /** Where to meet our flag i's chain from `from` (within maxD2), or null. Gates: a live track (age <= CT_HOLD), an
     *  observed group under CT_GROUP_MAX, fewer than CT_MISS misses, and divers still needed (need = en20 + CT_EDGE, less
     *  ou20 while fresh); then sets ctEn and ctNeed. The flag in my own view: carried -> null (fight's carrier branch and
     *  CARRIER_STUN take it), dropped within dist2 8 -> null (fight the receivers), dropped farther -> its tile. Else the
     *  predicted point, CT_LEAD steps on toward its spawn when farther than dist2 20; a spawned robot within CT_MISS_R2 of
     *  the predicted point that does not see the flag (age >= 1) counts a miss instead. */
    static MapLocation trackGoal(int i, MapLocation from, int maxD2) throws GameActionException { return trackGoal(i, from, maxD2, -1); }

    /** trackGoal with the dive cap: dv >= 0 is CT_DIVE as read this turn, and a chain that already has its `need` divers
     *  this round is null before any geometry (review 2026-10-05: a refused claim paid the whole geometry and then all of
     *  Micro.fight, on the crowded turns where g_iter4 peaks near 22k). */
    static MapLocation trackGoal(int i, MapLocation from, int maxD2, int dv) throws GameActionException {
        RobotController rc = G.rc;
        int ct = rc.readSharedArray(Comms.CT + i);
        if (ct == 0) return null;
        int age = G.round - rc.readSharedArray(Comms.OF_SEEN + i);
        if (age > C.CT_HOLD) return null;
        int en = Comms.ctEn(ct);
        if (en >= C.CT_GROUP_MAX) return null;
        int aux = rc.readSharedArray(Comms.CT_AUX);
        if (Comms.auxMiss(aux, i) >= C.CT_MISS) return null;
        int need = en + C.CT_EDGE - (age <= 1 ? Comms.auxOu(aux, i) : 0);
        if (need <= 0 || (dv >= 0 && Comms.diveCount(dv, i, G.round) >= need)) return null;
        ctEn = en; ctNeed = need;
        if (ctSeenAt[i] == G.round) {
            if (ctSeenCarried[i]) return null;
            return G.me.distanceSquaredTo(ctSeenLoc[i]) <= 8 ? null : ctSeenLoc[i];
        }
        MapLocation l = Comms.dec(Comms.ctLoc(ct)), d = ctDest(l);
        if (d == null) return null;
        MapLocation p = chainPoint(l, d, age);
        if (p == null || from.distanceSquaredTo(p) > maxD2) return null;
        if (G.me != null && age >= 1 && G.me.distanceSquaredTo(p) <= C.CT_MISS_R2) { ctMiss(i, aux); return null; }
        return from.distanceSquaredTo(p) > 20 ? Track.step(p.x, p.y, d.x, d.y, C.CT_LEAD) : p;
    }

    /** A negative sighting of our flag i's track: at most one per robot, round and flag. */
    static void ctMiss(int i, int aux) throws GameActionException {
        if (ctMissAt[i] == G.round) return;
        ctMissAt[i] = G.round;
        int na = Comms.auxSet(aux, i, Comms.auxMiss(aux, i) + 1, Comms.auxOu(aux, i));
        if (na != aux) G.rc.writeSharedArray(Comms.CT_AUX, na);
    }

    /** turn()'s fight-branch hook: dive at chainGoal's chain. Only a healthy duck (hurt ducks keep g_iter4's micro) that can
     *  move this turn (review 2026-10-05: a stunned duck took one of the chain's diver slots every frozen round without
     *  moving; its fight turn strikes and heals exactly as the dive's frozen path would), with CT_BC bytecodes left. */
    static boolean contactDive() throws GameActionException {
        RobotController rc = G.rc;
        if (rc.getHealth() < C.RETREAT_HP || !rc.isMovementReady() || G.bcLeft() < C.CT_BC) return false;
        MapLocation cg = chainGoal();
        if (cg == null) return false;
        Micro.dive(enemies, allies, cg); intercepts++; G.note = "dive" + ctEn;
        return true;
    }

    /** Our flag i's chain is one the track may send ducks to: not a lost flag, and for a defender only its own flag
     *  (DEF_TETHER's lesson). The dive (chainGoal) and the chase (carrierTarget) share it. */
    static boolean ctAdmit(int i, int lost) { return (lost >> i & 1) == 0 && !(isDefender() && i != homeFlag()); }

    /** The fight branch's dive goal: the nearest admitted chain (ctAdmit) within CT_DIVE_R2 whose diver cap still has room
     *  this round, claimed; else null (g_iter4's fight). A full chain is skipped before its geometry. */
    static MapLocation chainGoal() throws GameActionException {
        if (G.round <= C.SETUP_ROUNDS + 3) return null;
        int lost = Comms.lostMask(), dv = G.rc.readSharedArray(Comms.CT_DIVE), bi = -1, bd = Integer.MAX_VALUE, bneed = 0, ben = 0;
        MapLocation best = null;
        for (int i = 0; i < 3; i++) {
            if (!ctAdmit(i, lost)) continue;
            MapLocation p = trackGoal(i, G.me, C.CT_DIVE_R2, dv);
            if (p == null) continue;
            int d = G.me.distanceSquaredTo(p);
            if (d < bd) { bd = d; best = p; bi = i; bneed = ctNeed; ben = ctEn; }
        }
        if (best == null || !claimDive(bi, bneed)) return null;
        ctEn = ben;
        return best;
    }

    /** One diver's claim on our flag i's chain this round; false when `need` divers have claimed it already (chainGoal
     *  skips such a chain first, from the same read). The cap fills in execution order, so the same eligible ducks keep
     *  diving from round to round. */
    static boolean claimDive(int i, int need) throws GameActionException {
        int dv = G.rc.readSharedArray(Comms.CT_DIVE);
        if (Comms.diveCount(dv, i, G.round) >= need) return false;
        G.rc.writeSharedArray(Comms.CT_DIVE, Comms.diveAdd(dv, i, G.round));
        return true;
    }

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
            int x0 = Math.max(1, sx - r);
            if (scanK == k) { x0 = scanX; bx = scanBx; by = scanBy; bs = scanBs; bw = scanBw; }   // resume
            for (int x = x0; x <= Math.min(G.W - 2, sx + r); x += step) {
                if (scanReserve > 0 && x != x0 && G.bcLeft() < scanReserve) {   // audit BOT16: one spot's scan cost up to 23.6k
                    scanK = k; scanX = x; scanBx = bx; scanBy = by; scanBs = bs; scanBw = bw; return null;
                }
                for (int y = Math.max(1, sy - r); y <= Math.min(G.H - 2, sy + r); y += step) {
                    int w = (x - sx) * (x - sx) + (y - sy) * (y - sy);
                    if (w > C.RELOC_R2 || !spotFree(x, y, k, 64)) continue;
                    int sc = nearestEnemy2(x, y);
                    if (sc > bs || (sc == bs && w < bw)) { bs = sc; bx = x; by = y; bw = w; }
                }
            }
            scanK = -1;
            rcx[k] = bx; rcy[k] = by; relocK++;
            if (rcx[i] < 0) return null;
        }
        return new MapLocation(rcx[i], rcy[i]);
    }

    static MapLocation relocBest; static int relocBestScore = Integer.MIN_VALUE;   // RELOC_STALL_MOVES: best tile reached
    static MapLocation[] relocEc = new MapLocation[9];
    static int scanK = -1, scanX, scanBx, scanBy, scanBs, scanBw;   // RELOC_SPREAD: a spot scan paused for bytecode
    static int scanReserve = C.RELOC_SPREAD ? 4000 : 0;              // pause a scan below this many bytecodes left (0: never)
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
        if (C.RELOC_CLIMB) { relocClimb(i); return; }
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

    static int climbStall, climbBest = Integer.MIN_VALUE, climbCands = -1, climbDrops, climbWaits;
    static MapLocation climbBestLoc;
    static boolean climbBack;

    /** C.RELOC_CLIMB (audit BOT3(a): 23% of fixed spots were unreachable and ~20% of flags went back home; Gymhgy's flags sit
     *  35.7 tiles from our spawns, ours 28.9 from its). Holding our flag i after symmetry is decided (or RELOC_DECIDE), on each
     *  movement-ready turn step toward the visible tile farthest from the nearest live enemy spawn centre (climbStep); drop
     *  at a local maximum, at the deadline, or after RELOC_STALL ready turns without a new best, back on the best tile reached. */
    static void relocClimb(int i) throws GameActionException {
        RobotController rc = G.rc;
        if (!Sym.decided() && G.round < C.RELOC_DECIDE) return;
        if (climbCands != Sym.cands) {                       // live enemy centres, refreshed when a symmetry is eliminated
            climbCands = Sym.cands; relocNe = 0;
            for (int sym = Sym.ROT; sym <= Sym.FY; sym <<= 1)
                if ((Sym.cands & sym) != 0) for (MapLocation c : G.spawnCenters) if (c != null) relocEc[relocNe++] = Sym.image(c, sym);
            climbBest = Integer.MIN_VALUE; climbStall = 0;
            int[] ex = new int[3];                           // priority: the most exposed flag first (all robots agree)
            for (int k = 0; k < 3; k++) { relocOrder[k] = k; ex[k] = G.spawnCenters[k] == null ? Integer.MAX_VALUE : nearestEnemy2(G.spawnCenters[k].x, G.spawnCenters[k].y); }
            for (int p = 0; p < 3; p++) for (int q = p + 1; q < 3; q++)
                if (ex[relocOrder[q]] < ex[relocOrder[p]] || (ex[relocOrder[q]] == ex[relocOrder[p]] && relocOrder[q] < relocOrder[p])) { int t = relocOrder[p]; relocOrder[p] = relocOrder[q]; relocOrder[q] = t; }
        }
        int cur = nearestEnemy2(G.me.x, G.me.y);
        boolean drop = G.round >= C.RELOC_DEADLINE;
        if (!drop) {
            if (!rc.isMovementReady()) return;
            if (cur > climbBest) { climbBest = cur; climbBestLoc = G.me; climbStall = 0; } else climbStall++;
            if (climbBack || climbStall >= C.RELOC_STALL) {   // stalled: walk back to the best tile reached, then drop
                climbBack = true;
                if (G.me.distanceSquaredTo(climbBestLoc) > 0 && climbStall < 3 * C.RELOC_STALL) { Nav.moveTo(climbBestLoc); return; }
                drop = true;
            } else {
                MapInfo[] near = rc.senseNearbyMapInfos(GameConstants.VISION_RADIUS_SQUARED);
                MapLocation[] tiles = new MapLocation[near.length];
                for (int k = 0; k < near.length; k++) if (near[k].isPassable() && !near[k].isSpawnZone()) tiles[k] = near[k].getMapLocation();
                MapLocation best = climbStep(G.me, tiles, G.spawnCenters[i], otherFlags(i, true));
                if (best != null) { Nav.moveTo(best); return; }
                // a better tile blocked only by a moving flag of higher priority: wait for it to move on
                if (climbStep(G.me, tiles, G.spawnCenters[i], otherFlags(i, false)) != null) { climbStall = 0; climbWaits++; return; }
                drop = true;                                 // a local maximum
            }
        }
        if (rc.senseLegalStartingFlagPlacement(G.me) && rc.canDropFlag(G.me)) {
            rc.dropFlag(G.me);
            rc.writeSharedArray(Comms.OF_HOME + i, Comms.enc(G.me));
            placed = true; relocs++; climbDrops++;
        } else { flagTarget = G.me; MapLocation alt = legalDropNear(); Nav.moveTo(alt != null ? alt : G.spawnCenters[i]); }
    }

    /** Our other flags a climbing flag i keeps clear of: every placed one (OF_HOME); with live, also each unplaced flag of
     *  higher priority (more exposed, relocOrder) where it is now (a fresh carried position, else its spawn centre). A flag
     *  of lower priority is never in the way: it keeps clear of us (Alien, 2026-10-06: spawn centres 6 tiles apart, so each
     *  flag's start blocked the others and three flags dropped at their spawn centre in round 2). */
    static MapLocation[] otherFlags(int i, boolean live) throws GameActionException {
        MapLocation[] o = new MapLocation[2]; int n = 0;
        boolean higher = true;                               // relocOrder before i = higher priority
        for (int p = 0; p < 3; p++) {
            int j = relocOrder[p];
            if (j == i) { higher = false; continue; }
            MapLocation h = Comms.dec(G.rc.readSharedArray(Comms.OF_HOME + j));
            if (h == null && live && higher) { h = Comms.carried(j, 2); if (h == null) h = G.spawnCenters[j]; }
            o[n++] = h;
        }
        return o;
    }

    /** RELOC_CLIMB's step, pure: among the candidate tiles (null = not passable or a spawn tile) within CLIMB_R2 of the spawn
     *  centre sc and CLIMB_SEP2 of every other flag, the one farthest from the nearest live enemy centre (nearestEnemy2), if
     *  it beats the tile we stand on; ties go to the nearer tile, then the lower x, then the lower y. Null: a local maximum. */
    static MapLocation climbStep(MapLocation me, MapLocation[] tiles, MapLocation sc, MapLocation[] others) {
        MapLocation best = null; int bs = nearestEnemy2(me.x, me.y), bd = Integer.MAX_VALUE;
        for (MapLocation l : tiles) {
            if (l == null || l.equals(me) || l.distanceSquaredTo(sc) > C.CLIMB_R2) continue;
            boolean room = true;
            for (MapLocation o : others) if (o != null && l.distanceSquaredTo(o) < C.CLIMB_SEP2) { room = false; break; }
            if (!room) continue;
            int s = nearestEnemy2(l.x, l.y), d = me.distanceSquaredTo(l);
            if (s > bs || (s == bs && best != null && (d < bd || (d == bd && (l.x < best.x || (l.x == best.x && l.y < best.y)))))) { bs = s; bd = d; best = l; }
        }
        return best;
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
        // C.DAM_FIRST (Gymhgy study: 85% of our setup traps sit in deep rings and 8-12% of them fire by r250, while the
        // dam-drop skirmish r200-250 is the earliest win/loss separator): no rings in setup, the bank goes to the dam line
        if (rc.isActionReady() && !(C.DAM_FIRST && G.round <= C.SETUP_ROUNDS)) {
            int bankHold = 0;   // C.LATE_BANK class C: a calm re-arm of a ring holding RING_KEEP+ of our traps waits for the bank
            if (C.LATE_BANK && lateOn() && rc.getCrumbs() < TrapType.EXPLOSIVE.buildCost + C.DEF_TRAP_RESERVE + C.BANK_CAP && ringCount(home) >= C.RING_KEEP) bankHold = C.BANK_CAP;
            for (Direction d : G.DIRS) {
                MapLocation t = G.me.add(d);
                int dh = t.distanceSquaredTo(home);
                if (dh == 0 || dh > C.RING_RADIUS2) continue;
                TrapType tt = dh <= 2 ? TrapType.EXPLOSIVE : TrapType.STUN;
                if (rc.getCrumbs() >= tt.buildCost + bankHold + Math.max(C.DEF_TRAP_RESERVE, G.bankFloor() == Integer.MAX_VALUE ? Integer.MAX_VALUE - tt.buildCost : G.bankFloor()) && rc.canBuild(tt, t)) { rc.build(tt, t); Duck.defTraps++; break; }
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
        if (!rc.isActionReady() || rc.getCrumbs() < TrapType.STUN.buildCost + (C.DAM_FIRST ? C.DAM_FIRST_RESERVE : C.DAM_TRAP_RESERVE)) return;
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
        if (C.FINAL_COMPLETE && G.round >= C.FC_ROUND && levelNow()) return;   // g7fc: the bank goes to the levels in progress
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
    /** C.STUN_AHEAD: the builder is closer than the carrier to the carrier's destination (it walks toward the trap). */
    static boolean aheadOf(MapLocation builder, MapLocation carrier, MapLocation dest) {
        return dest != null && builder.distanceSquaredTo(dest) < carrier.distanceSquaredTo(dest);
    }

    /** C.STUN_FRONT (2026-10-04, Cyril trap geometry, TwistedTreeline: its 434 stuns were built with our nearest duck at
     *  median dist2 5 and 5.2 of ours within dist2 13, triggered after a median 1 round and froze 6.9 each; half of our
     *  282 had no enemy in vision and waited a median 66 rounds). The buildable tile within reach that is one enemy step
     *  from triggering (an enemy within dist2 8; the trigger is dist2 2) and has the most enemies within the stun radius
     *  (dist2 13), at least FRONT_MIN_VICTIMS; ties to the nearer enemy. Null if none. The build costs 5 cooldown, so the
     *  strike can still follow in the same turn. */
    static MapLocation frontStunTile() throws GameActionException {
        RobotController rc = G.rc;
        if (!rc.isActionReady() || rc.getCrumbs() < TrapType.STUN.buildCost + C.FRONT_RESERVE || enemies.length < C.FRONT_MIN_VICTIMS) return null;
        MapLocation best = null; int bv = C.FRONT_MIN_VICTIMS - 1, bd = Integer.MAX_VALUE;
        for (Direction d : Direction.allDirections()) {
            MapLocation t = G.me.add(d);
            int v = 0, nd = Integer.MAX_VALUE;
            for (RobotInfo e : enemies) { int x = t.distanceSquaredTo(e.location); if (x <= 13) v++; if (x < nd) nd = x; }
            if (nd > 8 || v < bv || (v == bv && nd >= bd) || !rc.canBuild(TrapType.STUN, t)) continue;
            bv = v; bd = nd; best = t;
        }
        return best;
    }

    static void carrierStun() throws GameActionException {
        RobotController rc = G.rc;
        if (!rc.isActionReady() || rc.getCrumbs() < TrapType.STUN.buildCost) return;
        RobotInfo car = null;
        for (RobotInfo e : enemies) if (e.hasFlag) { car = e; break; }
        if (car == null || G.me.distanceSquaredTo(car.location) > 18) return;
        MapLocation dest = G.nearest(car.location, Sym.enemyCenters());
        if (C.STUN_AHEAD && !aheadOf(G.me, car.location, dest)) return;   // audit BOT7: 74% of these stuns sat behind the carrier
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

    /** C.LATE_BANK: a combat trap aimed at water (our dump water; stuns are land-only) goes on the tile beside it, left first.
     *  Any other refusal (our trap there, an enemy beside it, off the map) keeps `t`, so the build fails as in g_iter7. */
    static MapLocation dumpSide(TrapType tt, MapLocation t) throws GameActionException {
        RobotController rc = G.rc;
        if (rc.canBuild(tt, t) || !rc.canSenseLocation(t) || !rc.senseMapInfo(t).isWater()) return t;
        Direction dd = G.me.directionTo(t); MapLocation l = G.me.add(dd.rotateLeft());
        return rc.canBuild(tt, l) ? l : G.me.add(dd.rotateRight());
    }

    static void placeCombatTrap() throws GameActionException {
        RobotController rc = G.rc;
        if (C.FINAL_COMPLETE && G.round >= C.FC_ROUND && levelNow() && !guardFight()) return;   // g7fc: field combat stuns pause; carrier
                                                         // stuns, rings and a stun at an alerted home (guardFight, as g_iter7) stay
        if (C.BUILDERS) {          // the builders build combat stuns (their discount), several a turn while the cooldown allows;
            if (isBuilder()) {     // others only when no builder is in view
                for (int k = 0; k < 3; k++) { MapLocation t = frontStunTile(); if (t == null) break; rc.build(TrapType.STUN, t); combatTraps++; }
                return;
            }
            for (RobotInfo a : allies) if (a.buildLevel >= C.BUILDER_SEEN_LEVEL) return;
        }
        if (C.STUN_FRONT) { MapLocation t = frontStunTile(); if (t != null) { rc.build(TrapType.STUN, t); combatTraps++; } return; }
        if (!rc.isActionReady() || enemies.length < C.STUN_ENEMIES_MIN || rc.getCrumbs() < 100 + C.TRAP_RESERVE) return;
        if (C.LATE_BANK && !lateTrapOk()) return;
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
            if (C.LATE_BANK && G.round > C.BANK_R2 && lateOn()) t = dumpSide(tt, t);
            if (rc.canBuild(tt, t)) { rc.build(tt, t); combatTraps++; if (tt == TrapType.WATER) waterTraps++; if (C.LATE_BANK) lateBuilt(); }
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
        if (best != null && best.distanceSquaredTo(c) < G.me.distanceSquaredTo(c)) { rc.build(tt, best); combatTraps++; if (C.LATE_BANK) lateBuilt(); }
    }
}
