package bot;

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
 */
public strictfp class Comms {
    public static final int IDX = 0, EF_ID = 1, EF_LOC = 4, EF_STATE = 7, OF_ALERT = 10, OF_LOC = 13;

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

    public static void alertOurFlag(int i) throws GameActionException {
        if (G.rc.readSharedArray(OF_ALERT + i) != G.round) G.rc.writeSharedArray(OF_ALERT + i, G.round);
    }
}
