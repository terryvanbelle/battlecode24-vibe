package t8hp500;

import battlecode.common.*;

/**
 * Map symmetry. Maps are symmetric by rotation, horizontal or vertical reflection (spec). Candidates are kept as a
 * bitmask (1 rotation, 2 flip x (mirror across the vertical axis), 4 flip y) and never left empty.
 * Evidence: (1) at start the broadcast hints of the enemy flags lie within dist² 100 of the enemy spawn centres,
 * which are the images of ours; (2) terrain we sense must match terrain at its image.
 */
public strictfp class Sym {
    public static final int ROT = 1, FX = 2, FY = 4;
    public static int cands = 7;

    public static MapLocation image(MapLocation m, int s) {
        switch (s) {
            case ROT: return new MapLocation(G.W - 1 - m.x, G.H - 1 - m.y);
            case FX: return new MapLocation(G.W - 1 - m.x, m.y);
            default: return new MapLocation(m.x, G.H - 1 - m.y);
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

    /** Prune with a sensed tile: a wall must mirror a wall. Cheap: call with a few tiles per turn. */
    public static void observe(MapInfo mi) {
        if (cands == ROT || cands == FX || cands == FY) return;
        MapLocation m = mi.getMapLocation();
        boolean wall = mi.isWall();
        for (int s = 1; s <= 4; s <<= 1) {
            if ((cands & s) == 0) continue;
            MapLocation im = image(m, s);
            if (!G.rc.canSenseLocation(im)) continue;
            try {
                if (G.rc.senseMapInfo(im).isWall() != wall && (cands & ~s) != 0) cands &= ~s;
            } catch (GameActionException e) { /* not sensable */ }
        }
    }
}
