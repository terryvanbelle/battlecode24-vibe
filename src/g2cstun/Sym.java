package g2cstun;

import battlecode.common.*;

/**
 * Map symmetry. Maps are symmetric by rotation, horizontal or vertical reflection (spec). Candidates are kept as a
 * bitmask (1 rotation, 2 flip x (mirror across the vertical axis), 4 flip y) and never left empty.
 *
 * g_iter1 used only (1) the enemy-flag broadcast hints at rounds 1-3 (each within dist2 100 of an enemy spawn centre),
 * which often leave 2-3 symmetries alive, and then GUESSED by a fixed order in best(); the terrain check it carried was
 * never called. Measured on replays: wrong enemy centres in 28% of g_iter1-vs-ColtG5 games and 34% of band games
 * (owner prompts 125-128: "determine symmetry 100% accurately after a limited number of observations").
 *
 * With OBSERVE on, symmetry is decided by observation, never by a guess:
 *  (2) geometry at start: the image of one of our spawn centres can never fall inside our own spawn zones;
 *  (3) spawn zones: under the true symmetry the image of each of our spawn centres is THEIR spawn-zone tile; one sight
 *      of that tile confirms or eliminates the symmetry;
 *  (4) memory: each duck remembers every tile it has seen (wall? spawn zone?); walls and spawn zones never change, so a
 *      tile seen now must match its remembered image under the true symmetry.
 * Surviving candidates are published through Comms.syncSym (slot 16, AND-merged by every robot). A contradiction (all
 * candidates eliminated) is counted in `conflicts` and the set is left as it was.
 */
public strictfp class Sym {
    public static final int ROT = 1, FX = 2, FY = 4;
    public static final boolean OBSERVE = true;    // observation-based symmetry (on in g_iter2, the incumbent since 2026-10-03)
    public static final int BC_START = 6000, BC_STOP = 2500;   // observation runs after the turn, only with this much left
    public static int cands = 7;
    public static int conflicts, eliminations, equivalents, decidedRound = -1;

    // per-duck terrain memory, one bit per tile (index x + y*W)
    static long[] seen, wall, spawn;
    static long[] setupSeen, dam;       // the dam is a fixed feature until r200: compared only between two setup sightings
    static long[] ours;                 // bitset of our 27 spawn tiles (O(1) membership; nested loops cost ~35k bytecode)

    static boolean isOurs(MapLocation m) {
        if (ours == null) buildOurs();
        if (m.x < 0 || m.y < 0 || m.x >= G.W || m.y >= G.H) return false;
        int i = m.x + m.y * G.W;
        return (ours[i >>> 6] & (1L << (i & 63))) != 0;
    }

    static void buildOurs() {
        ours = new long[(G.W * G.H + 63) >>> 6];
        if (G.spawns != null) for (MapLocation q : G.spawns) { int i = q.x + q.y * G.W; ours[i >>> 6] |= 1L << (i & 63); }
    }
    static boolean geoDone;

    public static MapLocation image(MapLocation m, int s) {
        switch (s) {
            case ROT: return new MapLocation(G.W - 1 - m.x, G.H - 1 - m.y);
            case FX: return new MapLocation(G.W - 1 - m.x, m.y);
            default: return new MapLocation(m.x, G.H - 1 - m.y);
        }
    }

    static int imageIndex(int x, int y, int s) {
        switch (s) {
            case ROT: return (G.W - 1 - x) + (G.H - 1 - y) * G.W;
            case FX: return (G.W - 1 - x) + y * G.W;
            default: return x + (G.H - 1 - y) * G.W;
        }
    }

    /** Enemy spawn centres under the most likely surviving symmetry. */
    public static MapLocation[] enemyCenters() {
        int s = best();
        MapLocation[] r = new MapLocation[3];
        for (int i = 0; i < 3; i++) if (G.spawnCenters[i] != null) r[i] = image(G.spawnCenters[i], s);
        return r;
    }

    public static int best() {
        if ((cands & ROT) != 0) return ROT;
        if ((cands & FX) != 0) return FX;
        if ((cands & FY) != 0) return FY;
        return ROT;
    }

    public static boolean decided() { return cands == ROT || cands == FX || cands == FY; }

    /** Use the enemy flag broadcast hints (all within dist² 100 of an enemy spawn centre at game start). */
    public static void fromBroadcasts(MapLocation[] hints) {
        if (hints == null || hints.length == 0) return;
        int keep = 0;
        for (int s = 1; s <= 4; s <<= 1) {
            if ((cands & s) == 0) continue;
            boolean ok = true;
            for (MapLocation h : hints) {
                int bd = Integer.MAX_VALUE;
                for (int i = 0; i < 3; i++) if (G.spawnCenters[i] != null) bd = Math.min(bd, h.distanceSquaredTo(image(G.spawnCenters[i], s)));
                if (bd > 100) { ok = false; break; }
            }
            if (ok) keep |= s;
        }
        if (keep != 0) cands = keep;
    }

    /** Remove symmetry s; never leaves the set empty (a contradiction is counted instead). */
    static void eliminate(int s) {
        if ((cands & s) == 0) return;
        if ((cands & ~s) == 0) { conflicts++; return; }
        cands &= ~s; eliminations++;
        collapseEquivalent();
        if (decided() && decidedRound < 0) decidedRound = G.round;
    }

    /** (2) Geometry: a symmetry that maps one of our spawn centres into our own spawn zones (dist2 <= 8 of one of our
     *  centres) is impossible. Pure given G.W, G.H and the centres. */
    public static void geometric(MapLocation[] centres) {
        for (int s = 1; s <= 4; s <<= 1) {
            if ((cands & s) == 0) continue;
            boolean bad = false;
            for (MapLocation c : centres) {
                if (c == null) continue;
                MapLocation im = image(c, s);
                for (MapLocation d : centres) if (d != null && im.distanceSquaredTo(d) <= 8) bad = true;
            }
            if (bad) eliminate(s);
        }
        collapseEquivalent();
    }

    /** Candidates that map our spawn tiles onto the same set of tiles predict the same enemy spawn zones: there is
     *  nothing to observe between them. Keep one (the lowest bit) so decided() holds and no scout is sent. */
    public static void collapseEquivalent() {
        if (G.spawns == null) return;
        for (int s = 1; s <= 4; s <<= 1) {
            if ((cands & s) == 0) continue;
            for (int o = s << 1; o <= 4; o <<= 1) {
                if ((cands & o) == 0) continue;
                boolean same = true;
                for (MapLocation p : G.spawns) {
                    if (!isOurs(image(image(p, s), o))) { same = false; break; }   // image(p,s) is an image under o iff its o-image is ours
                }
                if (same) { cands &= ~o; equivalents++; }
            }
        }
        if (decided() && decidedRound < 0) decidedRound = G.round;
    }

    /** (3) One sight of the tile image(c, s) of one of our spawn centres c: it must be their spawn zone under s. */
    public static void observeSpawnImage(int s, boolean theirSpawnZone) {
        if (!theirSpawnZone) eliminate(s);
    }

    /** Audit A2: an enemy flag's id is the location index (x + y*W) of THEIR spawn centre; a symmetry under which none of
     *  our centres maps exactly onto it is impossible. One id decides the symmetry on most maps. */
    public static void observeEnemyCentre(int flagId) {
        if (decided() || flagId < 0) return;
        int cx = flagId % G.W, cy = flagId / G.W;
        for (int s = 1; s <= 4; s <<= 1) {
            if ((cands & s) == 0) continue;
            boolean hit = false;
            for (MapLocation c : G.spawnCenters) if (c != null) { MapLocation im = image(c, s); if (im.x == cx && im.y == cy) hit = true; }
            if (!hit) eliminate(s);
        }
    }

    /** (4) One sensed tile: remember it, and compare it with its remembered image under every surviving symmetry. */
    public static void observeTile(int x, int y, boolean isWall, boolean isSpawn) { observeTile(x, y, isWall, isSpawn, false, false); }

    /** As above; during setup (inSetup) the dam is a fixed feature too: dam(t) must equal dam(image) when both tiles were
     *  seen in setup (the dam vanishes at r200, so a later sighting says nothing about it). */
    public static void observeTile(int x, int y, boolean isWall, boolean isSpawn, boolean isDam, boolean inSetup) {
        int i = x + y * G.W, w = i >>> 6;
        long b = 1L << (i & 63);
        seen[w] |= b;
        if (isWall) wall[w] |= b;
        if (isSpawn) spawn[w] |= b;
        if (inSetup) { setupSeen[w] |= b; if (isDam) dam[w] |= b; }
        for (int s = 1; s <= 4; s <<= 1) {
            if ((cands & s) == 0) continue;
            int j = imageIndex(x, y, s), v = j >>> 6;
            long c = 1L << (j & 63);
            if ((seen[v] & c) == 0) continue;
            if (((wall[v] & c) != 0) != isWall || ((spawn[v] & c) != 0) != isSpawn) { eliminate(s); continue; }
            if (inSetup && (setupSeen[v] & c) != 0 && ((dam[v] & c) != 0) != isDam) eliminate(s);
        }
    }

    static MapLocation scoutT; static int scoutFor = -1;

    /** A tile where the surviving symmetries disagree about THEIR spawn zones (one sight eliminates at least one):
     *  the image of one of our spawn tiles under one candidate that is not an image under another. Nearest to `from`.
     *  Recomputed only when the candidate set changes. Pure given G.spawns, G.W, G.H. */
    public static MapLocation scoutTarget(MapLocation from) {
        if (decided()) return null;
        if (scoutFor == cands && scoutT != null) return scoutT;
        if (scoutT != null && Clock.getBytecodesLeft() < BC_START) return scoutT;   // recompute later
        scoutFor = cands; scoutT = null;
        int bd = Integer.MAX_VALUE;
        for (int s = 1; s <= 4; s <<= 1) {
            if ((cands & s) == 0) continue;
            for (MapLocation p : G.spawns) {
                MapLocation im = image(p, s);
                boolean everywhere = true;                  // is im an image of some spawn tile under every other candidate?
                for (int o = 1; o <= 4 && everywhere; o <<= 1) {
                    if (o == s || (cands & o) == 0) continue;
                    if (!isOurs(image(im, o))) everywhere = false;   // involutions: im = image(q, o) iff q = image(im, o)
                }
                if (everywhere) continue;
                int d = from.distanceSquaredTo(im);
                if (d < bd) { bd = d; scoutT = im; }
            }
        }
        return scoutT;
    }

    /** The scout (one duck, idx SCOUT_IDX): after setup, while undecided, walk to the nearest distinguishing tile. */
    public static final int SCOUT_FIRST = 3, SCOUTS = 3;        // idx 3..5: the first field ducks after the defenders
    public static int scoutTurns;
    public static boolean scout() throws GameActionException {
        if (!OBSERVE || decided() || G.idx < SCOUT_FIRST || G.idx >= SCOUT_FIRST + SCOUTS || G.round <= 200
                || !G.rc.isSpawned() || !G.rc.isMovementReady()) return false;
        MapLocation t = scoutTarget(G.rc.getLocation());
        if (t == null || G.rc.canSenseLocation(t)) return false;  // in sight: update() has observed it this turn
        scoutTurns++;
        return Nav.moveTo(t);
    }

    public static void initMemory() {
        ours = null;
        int n = (G.W * G.H + 63) >>> 6;
        seen = new long[n]; wall = new long[n]; spawn = new long[n]; setupSeen = new long[n]; dam = new long[n];
    }

    /** After the turn's own work, spawned robots only, while undecided and only with BC_START bytecodes left: geometry
     *  once, spawn-zone images, then terrain memory; every loop stops at BC_STOP (verification 3: run before the turn
     *  it overran on Bunkers r201 and Soccer r438). */
    public static void update() throws GameActionException {
        if (!OBSERVE || Clock.getBytecodesLeft() < BC_START) return;
        RobotController rc = G.rc;
        if (seen == null) initMemory();
        if (!geoDone) { geometric(G.spawnCenters); geoDone = true; }
        if (decided()) return;
        for (int i = 0; i < 3; i++) { int v = rc.readSharedArray(Comms.EF_ID + i); if (v > 0) observeEnemyCentre(v - 1); }   // A2
        if (decided() || !rc.isSpawned()) return;
        for (int s = 1; s <= 4; s <<= 1) {                 // every one of our spawn tiles maps onto one of theirs
            if ((cands & s) == 0) continue;
            for (MapLocation p : G.spawns) {
                if (Clock.getBytecodesLeft() < BC_STOP) return;
                MapLocation im = image(p, s);
                if (!rc.canSenseLocation(im)) continue;
                if (rc.senseMapInfo(im).getSpawnZoneTeamObject() != G.them) { observeSpawnImage(s, false); break; }
            }
        }
        if (decided()) return;
        if (Clock.getBytecodesLeft() < BC_STOP + 400) return;
        for (MapInfo mi : rc.senseNearbyMapInfos()) {
            if (Clock.getBytecodesLeft() < BC_STOP) return;  // partial memory is fine: the rest is seen again later
            MapLocation m = mi.getMapLocation();
            int i = m.x + m.y * G.W;
            if ((seen[i >>> 6] & (1L << (i & 63))) != 0) continue;   // each tile once: a pair is compared when its
            observeTile(m.x, m.y, mi.isWall(), mi.isSpawnZone(), mi.isDam(), G.round <= 200);   // second tile first seen (v5:
            if (decided()) return;                                    // rescanning cost 14.8k a turn on Soccer)
        }
    }
}
