package g4bundle;

import battlecode.common.*;

/**
 * Shared-array schema (64 slots x 16 bits; writes are visible at once to robots that act later in the round,
 * and every robot can read/write, spawned or not: RULES.md). One purpose per slot; locations are x*64+y+1, 0 = none.
 *   0        index counter: each robot claims its creation-order index on its first turn
 *   1..3     enemy flag id+1 per registry slot
 *   4..6     enemy flag last exact location (0 = unknown)
 *   7..9     enemy flag state: 0 at large, 1 carried by us, 2 captured
 *   10..12   our flag i: round of the latest enemy sighting within 20 of it (0 = never)
 *   13..15   our flag i: current location when seen away from home (0 = at home / unknown)
 *   16       surviving map symmetries as a Sym bitmask (0 = not yet written = all three)
 *   17..19   our flag i: round an enemy was last seen carrying it (location in 13..15)
 *   20..22   our flag i: home (default) location chosen in setup (0 = its spawn centre)
 * Rewrite slots (research/REWRITE_DESIGN.md 2.3; written only under C.TRACK, never read by a g_iter1 code path; rounds
 * are 11 bits since the game ends at r2000):
 *   23       RT_STAMP: [10..0] round of the last round start (Track.roundStart, written last); [11] their CAPTURING as read
 *            by that round's round-start runner (every robot of ours uses it, so all share one return window a round);
 *            [15..12] spare
 *   24       PSYM: [2..0] private symmetry candidates as Sym bits (0 = unwritten = slot 16's mask), always within slot 16
 *   25..27   TRK_A[i] our flag i: [11..0] location of the last positive information (carrier or drop tile; home for
 *            HOME/MISSING); [14..12] Track state 0 HOME, 1 CARRIED, 2 DROPPED, 3 MISSING, 4 LOST, 5 GONE;
 *            [15] destination confirmed by heading
 *   28..30   TRK_B[i]: [10..0] round of that information (HOME: last home confirmation; MISSING: kept at it; DROPPED:
 *            first round seen dropped; CARRIED, MISSING and LOST: the departure, so DROPPED -> LOST rewrites it to the
 *            window end); [12..11] destination j = the image of our spawn centre j under PSYM (3 unknown);
 *            [14..13] speed class 0 = 1/2, 1 = 5/6 (their CAPTURING), 2 = 1/1 (relay); [15] inferred
 *   31..33   TRK_C[i]: [2..0] enemies within dist2 8 of the carrier at the last sighting (cap 7); [6..3] within dist2 20
 *            (cap 15); [8..7] misses 0-3; [12..9] round & 15 of the last counted miss (at most one miss a round);
 *            [15..13] DROPPED: first round seen dropped minus the earliest possible drop round (the previous
 *            information's round), 7 = seven or more or no bound
 *   34..36   OWN_C[s]: reserved for S3 (our carrier of enemy registry slot s reports itself); not written yet
 *   37..48   AUC[o][p] = 37 + 2o + p: reserved for S1/S3 (responder auction); not written yet
 *   49..63   spare
 */
public strictfp class Comms {
    public static final int IDX = 0, EF_ID = 1, EF_LOC = 4, EF_STATE = 7, OF_ALERT = 10, OF_LOC = 13, SYM = 16, OF_CARRY = 17, OF_HOME = 20;
    public static final int RT_STAMP = 23, PSYM = 24, TRK_A = 25, TRK_B = 28, TRK_C = 31, OWN_C = 34, AUC = 37, AUC_SLOTS = 12;
    /** 49..51: OF_THREAT (C.ALERT_FIX, audit A1): where the enemy that raised our flag i's alert stood (enc), so responders go
     *  to the threat, never onto the flag tile. Written only with OF_ALERT. */
    public static final int OF_THREAT = 49;
    /** 52..54 EF_DROP: round enemy flag slot s was dropped by our dying carrier (0 none); 55..57 EF_HOME: its learned home
     *  (enc; first ground sighting with no drop pending). C.REG_FIX, audit A5/A6. */
    public static final int EF_DROP = 52, EF_HOME = 55;
    /** 58 OF_LOST: our flags taken for good, bits 0-2 (C.FLAG_LOST; audit 2026-10-03 BOT1: the engine removes a captured
     *  flag and the bot never noticed). 59..61 OF_SEEN: last round our flag i was in anyone's view, carried or not. */
    public static final int OF_LOST = 58, OF_SEEN = 59;
    public static int lostMask() throws GameActionException { return G.rc.readSharedArray(OF_LOST) & 7; }

    /** A5: our carrier of enemy flag `flagId` died: the flag is on the ground at its last carried location. */
    public static void enemyFlagDropped(int flagId) throws GameActionException {
        int s = enemyFlagSlot(flagId);
        if (s < 0) return;
        if (G.rc.readSharedArray(EF_STATE + s) == 1) G.rc.writeSharedArray(EF_STATE + s, 0);
        G.rc.writeSharedArray(EF_DROP + s, Math.max(1, G.round));
    }

    /** A6: after the return window a dropped flag is home again: point the registry at its home (or clear it, so targeting
     *  falls back to the broadcast hints). Every robot, every turn; cheap (3 slots). */
    public static void expireDrops() throws GameActionException {
        int win = 4;
        for (GlobalUpgrade u : G.rc.getGlobalUpgrades(G.us)) if (u == GlobalUpgrade.CAPTURING) win = 25;
        for (int s = 0; s < 3; s++) {
            int d = G.rc.readSharedArray(EF_DROP + s);
            if (d == 0 || G.round <= d + win) continue;
            G.rc.writeSharedArray(EF_LOC + s, G.rc.readSharedArray(EF_HOME + s));
            G.rc.writeSharedArray(EF_DROP + s, 0);
        }
    }

    /** A6: a remembered ground location that is in view and empty is stale (the flag moved home or was re-grabbed). */
    public static void clearIfEmpty(int s, FlagInfo[] seen) throws GameActionException {
        MapLocation l = dec(G.rc.readSharedArray(EF_LOC + s));
        if (l == null || G.rc.readSharedArray(EF_STATE + s) != 0 || !G.rc.canSenseLocation(l)) return;
        int id = G.rc.readSharedArray(EF_ID + s) - 1;
        for (FlagInfo f : seen) if (f.getID() == id && f.getLocation().equals(l)) return;
        G.rc.writeSharedArray(EF_LOC + s, G.rc.readSharedArray(EF_HOME + s) == G.rc.readSharedArray(EF_LOC + s) ? 0 : G.rc.readSharedArray(EF_HOME + s));
        G.rc.writeSharedArray(EF_DROP + s, 0);
    }

    public static int enc(MapLocation m) { return m == null ? 0 : m.x * 64 + m.y + 1; }
    public static MapLocation dec(int v) { return v == 0 ? null : new MapLocation((v - 1) / 64, (v - 1) % 64); }

    public static int claimIndex() throws GameActionException {
        int v = G.rc.readSharedArray(IDX);
        G.rc.writeSharedArray(IDX, v + 1);
        return v;
    }

    /** Registry slot for an enemy flag id, allocating a free one; -1 if full. */
    public static int enemyFlagSlot(int flagId) throws GameActionException {
        int free = -1;
        for (int i = 0; i < 3; i++) {
            int v = G.rc.readSharedArray(EF_ID + i);
            if (v == flagId + 1) return i;
            if (v == 0 && free < 0) free = i;
        }
        if (free >= 0) G.rc.writeSharedArray(EF_ID + free, flagId + 1);
        return free;
    }

    public static void reportEnemyFlag(FlagInfo f) throws GameActionException {
        int s = enemyFlagSlot(f.getID());
        if (s < 0) return;
        int loc = enc(f.getLocation());
        if (G.rc.readSharedArray(EF_LOC + s) != loc) G.rc.writeSharedArray(EF_LOC + s, loc);
        int st = f.isPickedUp() ? 1 : 0;
        int prev = G.rc.readSharedArray(EF_STATE + s);
        if (prev != 2 && prev != st) G.rc.writeSharedArray(EF_STATE + s, st);
        if (C.REG_FIX && st == 0 && G.round > 200) {     // A6: learn the home from a ground sighting with no drop pending
            if (prev == 1) { if (G.rc.readSharedArray(EF_DROP + s) == 0) G.rc.writeSharedArray(EF_DROP + s, G.round); }
            else if (G.rc.readSharedArray(EF_DROP + s) == 0 && G.rc.readSharedArray(EF_HOME + s) == 0) G.rc.writeSharedArray(EF_HOME + s, loc);
            else if (G.rc.readSharedArray(EF_HOME + s) == loc) G.rc.writeSharedArray(EF_DROP + s, 0);   // seen back home
        }
    }

    public static void enemyFlagCaptured(int flagId) throws GameActionException {
        int s = enemyFlagSlot(flagId);
        if (s >= 0) G.rc.writeSharedArray(EF_STATE + s, 2);
    }

    public static void clearEnemyFlagLoc(int slot) throws GameActionException {
        if (G.rc.readSharedArray(EF_LOC + slot) != 0) G.rc.writeSharedArray(EF_LOC + slot, 0);
    }

    /** Merge our symmetry candidates with the team's and publish the intersection. */
    public static void syncSym() throws GameActionException {
        int v = G.rc.readSharedArray(SYM);
        if (v != 0 && (Sym.cands & v) != 0) Sym.cands &= v;
        if (v != Sym.cands) G.rc.writeSharedArray(SYM, Sym.cands);
    }

    /** Our flag index for a flag id (flag id = location index of its original spawn centre), -1 if unknown. */
    public static int ourFlagIndex(int flagId) {
        for (int i = 0; i < 3; i++) { MapLocation c = G.spawnCenters[i]; if (c != null && c.x + c.y * G.W == flagId) return i; }
        return -1;
    }

    public static void reportCarried(int i, MapLocation at) throws GameActionException {
        int v = enc(at);
        if (G.rc.readSharedArray(OF_LOC + i) != v) G.rc.writeSharedArray(OF_LOC + i, v);
        if (G.rc.readSharedArray(OF_CARRY + i) != G.round) G.rc.writeSharedArray(OF_CARRY + i, G.round);
    }

    /** Audit A11(a): our flag i is in view and not carried, so no enemy carries it now; forget the stale carry. */
    public static void clearCarried(int i) throws GameActionException {
        if (G.rc.readSharedArray(OF_CARRY + i) != 0) { G.rc.writeSharedArray(OF_CARRY + i, 0); G.rc.writeSharedArray(OF_LOC + i, 0); }
    }

    /** Location of an enemy carrying our flag i, if seen in the last `fresh` rounds; else null. */
    public static MapLocation carried(int i, int fresh) throws GameActionException {
        int r = G.rc.readSharedArray(OF_CARRY + i);
        if (r == 0 || G.round - r > fresh) return null;
        return dec(G.rc.readSharedArray(OF_LOC + i));
    }

    /** Rounds since an enemy carrying our flag i was last seen (Integer.MAX_VALUE if never). */
    public static int carriedAge(int i) throws GameActionException {
        int r = G.rc.readSharedArray(OF_CARRY + i);
        return r == 0 ? Integer.MAX_VALUE : G.round - r;
    }

    /** Where an enemy carrying our flag i was last seen (may be stale; see carriedAge). */
    public static MapLocation carriedLast(int i) throws GameActionException {
        return dec(G.rc.readSharedArray(OF_LOC + i));
    }

    /** Home of our flag i: where it was placed in setup, else its spawn centre. */
    public static MapLocation flagHome(int i) throws GameActionException {
        MapLocation h = dec(G.rc.readSharedArray(OF_HOME + i));
        return h != null ? h : G.spawnCenters[i];
    }

    /** Audit A1: the enemy (nearest to the home) within ALERT_THREAT_R2 of our flag home h, or null. Pure. */
    public static RobotInfo threatTo(MapLocation h, RobotInfo[] enemies) {
        if (h == null) return null;
        RobotInfo best = null; int bd = C.ALERT_THREAT_R2 + 1;
        for (RobotInfo e : enemies) { int d = e.location.distanceSquaredTo(h); if (d < bd) { bd = d; best = e; } }
        return best;
    }

    /** Audit A1: raise our flag i's alert only for a real threat, and record where it stood. */
    public static void alertOurFlag(int i, MapLocation threat) throws GameActionException {
        alertOurFlag(i);
        int v = enc(threat);
        if (G.rc.readSharedArray(OF_THREAT + i) != v) G.rc.writeSharedArray(OF_THREAT + i, v);
    }

    public static MapLocation threatAt(int i) throws GameActionException { return dec(G.rc.readSharedArray(OF_THREAT + i)); }

    public static void alertOurFlag(int i) throws GameActionException {
        if (G.rc.readSharedArray(OF_ALERT + i) != G.round) G.rc.writeSharedArray(OF_ALERT + i, G.round);
    }
}
