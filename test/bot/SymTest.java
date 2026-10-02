package bot;

import battlecode.common.*;
import java.util.Random;

/**
 * Symmetry must be decided by observation, never guessed (owner prompts 125-128). On random maps that are symmetric
 * under a known transformation, simulated observations (vision disks of dist2 20 at random spots, the order a duck
 * would see them) are fed through the real pruning code: the true symmetry must never be eliminated, the set must
 * never empty, and a bounded number of observations must decide it.
 */
public class SymTest {
    static int fails = 0;
    static void check(boolean ok, String what) { if (!ok) { fails++; System.out.println("FAIL " + what); } }

    /** Random map of size w x h symmetric under s: random walls on the whole map, then forced to match their image. */
    static boolean[][] map(Random r, int w, int h, int s, double density) {
        G.W = w; G.H = h;
        boolean[][] wall = new boolean[w][h];
        for (int x = 0; x < w; x++) for (int y = 0; y < h; y++) {
            MapLocation m = new MapLocation(x, y), im = Sym.image(m, s);
            int a = x + y * w, b = im.x + im.y * w;
            if (a <= b) { boolean v = r.nextDouble() < density; wall[x][y] = v; wall[im.x][im.y] = v; }
        }
        return wall;
    }

    static void reset() { Sym.cands = 7; Sym.conflicts = 0; Sym.eliminations = 0; Sym.decidedRound = -1; Sym.initMemory(); }

    public static void main(String[] a) {
        Random r = new Random(20261002);
        int[] syms = {Sym.ROT, Sym.FX, Sym.FY};
        int worst = 0, trials = 0, undecided = 0;
        for (int t = 0; t < 300; t++) {
            int s = syms[t % 3], w = 30 + r.nextInt(31), h = 30 + r.nextInt(31);
            boolean[][] wall = map(r, w, h, s, 0.05 + 0.25 * r.nextDouble());
            reset();
            // spawn zones: one 3x3 zone of ours in a random spot, its image is theirs
            // a valid map: their zone (the image of ours) never overlaps or touches ours (Chebyshev distance >= 3)
            int cx, cy; MapLocation their;
            do {
                cx = 1 + r.nextInt(w - 2); cy = 1 + r.nextInt(h - 2);
                their = Sym.image(new MapLocation(cx, cy), s);
            } while (Math.max(Math.abs(their.x - cx), Math.abs(their.y - cy)) < 3);
            G.spawnCenters[0] = new MapLocation(cx, cy); G.spawnCenters[1] = null; G.spawnCenters[2] = null;
            boolean[][] spawn = new boolean[w][h];
            for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) {
                spawn[cx + dx][cy + dy] = true;
                spawn[their.x + dx][their.y + dy] = true;
            }
            Sym.geometric(G.spawnCenters);
            check((Sym.cands & s) != 0, "geometry never removes the true symmetry (trial " + t + ")");
            // observations: a duck walks to random spots and senses the dist2-20 disk there
            int obs = 0;
            for (int k = 0; k < 400 && !Sym.decided(); k++) {
                int px = r.nextInt(w), py = r.nextInt(h);
                for (int dx = -4; dx <= 4; dx++) for (int dy = -4; dy <= 4; dy++) {
                    int x = px + dx, y = py + dy;
                    if (dx * dx + dy * dy > 20 || x < 0 || y < 0 || x >= w || y >= h) continue;
                    Sym.observeTile(x, y, wall[x][y], spawn[x][y]);
                }
                // the spawn-zone check fires when a candidate's image of our centre is in view
                for (int c = 1; c <= 4; c <<= 1) {
                    if ((Sym.cands & c) == 0) continue;
                    MapLocation im = Sym.image(G.spawnCenters[0], c);
                    if ((im.x - px) * (im.x - px) + (im.y - py) * (im.y - py) <= 20)
                        Sym.observeSpawnImage(c, spawn[im.x][im.y] && !(Math.abs(im.x - cx) <= 1 && Math.abs(im.y - cy) <= 1));
                }
                obs++;
                check((Sym.cands & s) != 0 && Sym.cands != 0, "true symmetry never eliminated, set never empty (trial " + t + ")");
            }
            check(Sym.conflicts == 0, "no contradictions on a consistent map (trial " + t + ")");
            trials++;
            if (!Sym.decided()) {
                // undecided is acceptable only if the map is symmetric under every survivor (equivalent symmetries)
                boolean equiv = true;
                for (int c = 1; c <= 4; c <<= 1) if ((Sym.cands & c) != 0)
                    for (int x = 0; x < w && equiv; x++) for (int y = 0; y < h && equiv; y++) {
                        MapLocation im = Sym.image(new MapLocation(x, y), c);
                        if (wall[x][y] != wall[im.x][im.y]) equiv = false;
                    }
                if (!equiv) undecided++;
            } else worst = Math.max(worst, obs);
        }
        check(undecided == 0, "every map decided or left with only equivalent symmetries (" + undecided + " not)");
        check(worst <= 60, "decided within 60 vision disks on every map (worst " + worst + ")");

        // the guess is gone: two candidates consistent with hints but only one consistent with an observed wall pair
        G.W = 40; G.H = 30; reset(); Sym.cands = Sym.ROT | Sym.FX;
        Sym.observeTile(3, 4, true, false);                 // a wall at (3,4)
        Sym.observeTile(36, 25, true, false);               // ROT image of (3,4) is (36,25): a wall, consistent
        Sym.observeTile(36, 4, false, false);               // FX image of (3,4) is (36,4): open, so FX is impossible
        check(Sym.cands == Sym.ROT && Sym.decided(), "a wall without its FX image eliminates FX (" + Sym.cands + ")");
        // a contradiction never empties the set
        reset(); Sym.cands = Sym.FY;
        Sym.observeSpawnImage(Sym.FY, false);
        check(Sym.cands == Sym.FY && Sym.conflicts == 1, "eliminating the last candidate is counted, not applied");
        // geometry: a symmetry that maps our centre onto itself is impossible
        G.W = 41; G.H = 30; reset();
        G.spawnCenters[0] = new MapLocation(20, 5); G.spawnCenters[1] = null; G.spawnCenters[2] = null;
        Sym.geometric(G.spawnCenters);
        check((Sym.cands & Sym.FX) == 0, "a centre on the FX axis rules FX out (" + Sym.cands + ")");

        // Soccer-like (53x30, terrain symmetric under ROT and FX alike): the two candidates differ only in the middle
        // zone, one row, deep on their side. The scout target must distinguish them, and one look there must decide it.
        G.W = 53; G.H = 30; reset(); Sym.cands = Sym.ROT | Sym.FX;
        MapLocation[] centres = {new MapLocation(11, 26), new MapLocation(6, 14), new MapLocation(11, 3)};
        G.spawnCenters[0] = centres[0]; G.spawnCenters[1] = centres[1]; G.spawnCenters[2] = centres[2];
        G.spawns = new MapLocation[27];
        int k = 0;
        for (MapLocation c : centres) for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) G.spawns[k++] = c.translate(dx, dy);
        Sym.geometric(G.spawnCenters);
        check(Sym.cands == (Sym.ROT | Sym.FX), "Soccer-like: geometry alone cannot separate ROT and FX (" + Sym.cands + ")");
        MapLocation t = Sym.scoutTarget(new MapLocation(26, 14));
        boolean underFX = false, underROT = false;
        for (MapLocation q : G.spawns) { if (Sym.image(q, Sym.FX).equals(t)) underFX = true; if (Sym.image(q, Sym.ROT).equals(t)) underROT = true; }
        check(t != null && underFX != underROT, "Soccer-like: the scout target is a tile the candidates disagree on (" + t + ")");
        // truth FX: our side seen (spawn tiles are spawn, others not), then the scout sees t
        for (int x = 0; x < 26; x++) for (int y = 0; y < 30; y++) {
            boolean sp = false; for (MapLocation q : G.spawns) if (q.x == x && q.y == y) sp = true;
            Sym.observeTile(x, y, false, sp);
        }
        boolean tSpawnUnderTruth = underFX;
        Sym.observeTile(t.x, t.y, false, tSpawnUnderTruth);
        if (!tSpawnUnderTruth) Sym.observeSpawnImage(Sym.ROT, false);
        check(Sym.cands == Sym.FX, "Soccer-like: one look at the scout target decides FX (" + Sym.cands + ")");
        check(Sym.scoutTarget(new MapLocation(26, 14)) == null, "decided: no scout target");

        // equivalent candidates (DefaultLarge-like: ROT and FX map our spawn tiles onto the same tiles) count as decided
        G.W = 59; G.H = 31; reset(); Sym.cands = Sym.ROT | Sym.FX;
        MapLocation[] dl = {new MapLocation(3, 3), new MapLocation(3, 27), new MapLocation(7, 15)};
        G.spawns = new MapLocation[27]; k = 0;
        for (MapLocation c : dl) for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) G.spawns[k++] = c.translate(dx, dy);
        G.spawnCenters[0] = dl[0]; G.spawnCenters[1] = dl[1]; G.spawnCenters[2] = dl[2];
        Sym.geometric(G.spawnCenters);
        check(Sym.decided() && Sym.scoutTarget(new MapLocation(20, 15)) == null, "equivalent ROT/FX on a DefaultLarge-like map count as decided (" + Sym.cands + ")");

        System.out.println("SymTest: " + (fails == 0 ? "OK" : "FAILED " + fails) + " (" + trials + " random maps, worst " + worst + " disks)");
        if (fails > 0) System.exit(1);
    }
}
