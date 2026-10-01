package s1stack;

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
 */
public strictfp class Comms {
    public static final int IDX = 0, EF_ID = 1, EF_LOC = 4, EF_STATE = 7, OF_ALERT = 10, OF_LOC = 13, SYM = 16, OF_CARRY = 17, OF_HOME = 20;

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
        if (G.rc.readSharedArray(EF_STATE + s) != 2 && G.rc.readSharedArray(EF_STATE + s) != st) G.rc.writeSharedArray(EF_STATE + s, st);
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

    /** Location of an enemy carrying our flag i, if seen in the last `fresh` rounds; else null. */
    public static MapLocation carried(int i, int fresh) throws GameActionException {
        int r = G.rc.readSharedArray(OF_CARRY + i);
        if (r == 0 || G.round - r > fresh) return null;
        return dec(G.rc.readSharedArray(OF_LOC + i));
    }

    /** Home of our flag i: where it was placed in setup, else its spawn centre. */
    public static MapLocation flagHome(int i) throws GameActionException {
        MapLocation h = dec(G.rc.readSharedArray(OF_HOME + i));
        return h != null ? h : G.spawnCenters[i];
    }

    public static void alertOurFlag(int i) throws GameActionException {
        if (G.rc.readSharedArray(OF_ALERT + i) != G.round) G.rc.writeSharedArray(OF_ALERT + i, G.round);
    }
}
