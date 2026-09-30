package bot;

import battlecode.common.*;

/** Pure-logic tests for the bot. Plain main, exit code 1 on failure. */
public class BotTest {
    static int fails = 0;
    static void check(boolean ok, String what) { if (!ok) { fails++; System.out.println("FAIL " + what); } }

    public static void main(String[] a) {
        // Comms location encoding: round trip over the whole legal map range, 0 means none, fits 16 bits
        boolean rt = true;
        for (int x = 0; x < 60; x++) for (int y = 0; y < 60; y++) {
            MapLocation m = new MapLocation(x, y); int v = Comms.enc(m);
            if (v <= 0 || v > 65535 || !Comms.dec(v).equals(m)) rt = false;
        }
        check(rt, "comms: enc/dec round trip on 60x60, never 0, fits 16 bits");
        check(Comms.dec(0) == null && Comms.enc(null) == 0, "comms: 0 is none");
        // slot layout: no two purposes overlap and all fit in 64
        int[][] ranges = {{Comms.IDX, 1}, {Comms.EF_ID, 3}, {Comms.EF_LOC, 3}, {Comms.EF_STATE, 3}, {Comms.OF_ALERT, 3}, {Comms.OF_LOC, 3}};
        boolean[] used = new boolean[64]; boolean ok = true;
        for (int[] r : ranges) for (int i = r[0]; i < r[0] + r[1]; i++) { if (i >= 64 || used[i]) ok = false; else used[i] = true; }
        check(ok, "comms: slot ranges disjoint and < 64");

        // rng: in range, both parities reachable, differs between ids
        G.id = 12345; G.rngState = 12345 * 0x9E3779B1 + 12345;
        boolean inRange = true, saw0 = false, saw1 = false;
        for (int i = 0; i < 1000; i++) { int r = G.rand(7); if (r < 0 || r >= 7) inRange = false; int b = G.rand(2); if (b == 0) saw0 = true; else saw1 = true; }
        check(inRange && saw0 && saw1, "rng: in range and not stuck");

        // micro threat: an enemy can step once and hit from dist2 4, so dist2 <= 10 threatens and 13 does not
        MapLocation o = new MapLocation(10, 10);
        RobotInfo[] e10 = {new RobotInfo(1, Team.B, 1000, new MapLocation(13, 11), false, 0, 0, 0)};
        RobotInfo[] e13 = {new RobotInfo(1, Team.B, 1000, new MapLocation(13, 12), false, 0, 0, 0)};
        check(Micro.threat(o, e10) == 1 && Micro.threat(o, e13) == 0, "micro: threat radius dist2 10 (step + attack reach)");
        // targeting: a flag carrier first, then the lowest HP within reach; out-of-reach never chosen
        RobotInfo far = new RobotInfo(2, Team.B, 10, new MapLocation(13, 10), false, 0, 0, 0);
        RobotInfo low = new RobotInfo(3, Team.B, 200, new MapLocation(11, 10), false, 0, 0, 0);
        RobotInfo carrier = new RobotInfo(4, Team.B, 900, new MapLocation(10, 12), true, 0, 0, 0);
        check(Micro.bestTarget(new RobotInfo[]{far, low}, o) == low, "micro: out-of-reach low-HP enemy not chosen");
        check(Micro.bestTarget(new RobotInfo[]{far, low, carrier}, o) == carrier, "micro: flag carrier first");

        // constants: the near-miss bar sits below the limit; gather happens inside setup
        check(C.NEAR_MISS_BC < GameConstants.BYTECODE_LIMIT, "const: near-miss below the limit");
        check(C.GATHER_ROUND < C.SETUP_ROUNDS && C.SETUP_ROUNDS == GameConstants.SETUP_ROUNDS, "const: gather inside setup; setup length matches engine");

        System.out.println("BotTest: " + (fails == 0 ? "OK" : "FAILED " + fails));
        if (fails > 0) System.exit(1);
    }
}
