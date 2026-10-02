package bot;

import battlecode.common.*;

/** Pure-logic tests for the bot. Plain main, exit code 1 on failure. */
public class BotTest {
    static int fails = 0;
    static void check(boolean ok, String what) { if (!ok) { fails++; System.out.println("FAIL " + what); } }

    // ------------------------------------------------------------------ S0b Track (research/REWRITE_DESIGN.md 2.9)
    /** Fake robot controller: a 64-slot shared array, vision dist2 20 around G.me, walls and spawn zones from the test. */
    static int[] shared = new int[64];
    static boolean badWrite, failReads;
    static GlobalUpgrade[] theirUpg = new GlobalUpgrade[0];
    static java.util.Set<MapLocation> walls = new java.util.HashSet<>();
    static MapLocation[] zoneA = new MapLocation[0], zoneB = new MapLocation[0];
    static int zoneOf(MapLocation l) {
        for (MapLocation c : zoneA) if (Math.max(Math.abs(c.x - l.x), Math.abs(c.y - l.y)) <= 1) return 1;
        for (MapLocation c : zoneB) if (Math.max(Math.abs(c.x - l.x), Math.abs(c.y - l.y)) <= 1) return 2;
        return 0;
    }
    static boolean sensable(MapLocation l) { return G.me != null && l.x >= 0 && l.y >= 0 && l.x < G.W && l.y < G.H && G.me.distanceSquaredTo(l) <= 20; }
    static RobotController fakeRc() {
        return (RobotController) java.lang.reflect.Proxy.newProxyInstance(RobotController.class.getClassLoader(), new Class<?>[]{RobotController.class},
            (proxy, m, args) -> {
                switch (m.getName()) {
                    case "readSharedArray":
                        if (failReads) throw new GameActionException(GameActionExceptionType.INTERNAL_ERROR, "test");
                        return shared[(Integer) args[0]];
                    case "writeSharedArray": {
                        int i = (Integer) args[0], v = (Integer) args[1];
                        if (i < Comms.RT_STAMP || i >= Comms.OWN_C || v < 0 || v > GameConstants.MAX_SHARED_ARRAY_VALUE) badWrite = true;
                        shared[i] = v; return null;
                    }
                    case "canSenseLocation": return sensable((MapLocation) args[0]);
                    case "senseMapInfo": {
                        MapLocation l = (MapLocation) args[0];
                        if (!sensable(l)) throw new GameActionException(GameActionExceptionType.CANT_SENSE_THAT, "test");
                        return new MapInfo(l, !walls.contains(l), walls.contains(l), false, zoneOf(l), false, 0, TrapType.NONE, Team.NEUTRAL);
                    }
                    case "getGlobalUpgrades": return theirUpg;
                    case "getRoundNum": return G.round;
                    case "getLocation": return G.me;
                    case "isSpawned": return G.me != null;
                    case "hashCode": return 1;
                    case "equals": return proxy == args[0];
                    case "toString": return "fakeRc";
                }
                Class<?> r = m.getReturnType();
                return r == boolean.class ? (Object) false : r == int.class ? (Object) 0 : null;
            });
    }
    static int st(int i) { return Track.aState(shared[Comms.TRK_A + i]); }
    static int b0() { return shared[Comms.TRK_B]; }
    static int c0() { return shared[Comms.TRK_C]; }
    /** One robot turn of the S0b hooks, in Duck's order: roundStart, sight per flag in view, homes, negatives, psym. */
    static void trackTurn(int round, MapLocation me, FlagInfo[] inView, RobotInfo[] enemies) {
        G.round = round; G.me = me; Duck.enemies = enemies;
        Track.roundStart();
        if (me == null) return;
        for (FlagInfo f : inView) Track.sight(f);
        Track.homes(); Track.negatives(); Track.psym();
    }
    static final RobotInfo[] NONE = new RobotInfo[0];
    static final FlagInfo[] NOFLAG = new FlagInfo[0];
    static void turn(int round, MapLocation me, FlagInfo... inView) { trackTurn(round, me, inView, NONE); }
    /** A fresh game state for the scripted tests: 30x30, our centres (3,3) (3,15) (10,3), theirs by rotation; slot 16 = ROT. */
    static void resetTrack() {
        java.util.Arrays.fill(shared, 0);
        shared[Comms.SYM] = Sym.ROT;
        theirUpg = new GlobalUpgrade[0];
        walls.clear();
        Track.dstSym = -1; Track.loaded = -1; Track.theyCap = false;
        for (int i = 0; i < 3; i++) { Track.homeAt[i] = null; Track.seen[i] = -1; Track.infAt[i] = -1; Track.pAt[i] = -1; }
        Track.mSeen = null; Track.scanX = -1; Track.scanY = -1; Track.pend = null;
    }

    static void trackTests() {
        // pack/unpack round trips for every rewrite slot
        boolean pa = true, pb = true, pc = true, ps = true;
        for (int x = 0; x < 60; x++) for (int y = 0; y < 60; y++) {
            int l = Comms.enc(new MapLocation(x, y));
            for (int s = 0; s <= 5; s++) for (int cf = 0; cf < 2; cf++) {
                int v = Track.packA(l, s, cf == 1);
                if (v < 0 || v > 65535 || Track.aLoc(v) != l || Track.aState(v) != s || Track.aConf(v) != (cf == 1)) pa = false;
            }
        }
        for (int r = 0; r < 2048; r++) {
            for (int d = 0; d <= 3; d++) for (int c = 0; c <= 2; c++) for (int inf = 0; inf < 2; inf++) {
                int v = Track.packB(r, d, c, inf == 1);
                if (v < 0 || v > 65535 || Track.bRound(v) != r || Track.bDest(v) != d || Track.bCls(v) != c || Track.bInf(v) != (inf == 1)) pb = false;
            }
            for (int cap = 0; cap < 2; cap++) {
                int v = Track.packStamp(r, cap == 1);
                if (v < 0 || v > 65535 || Track.stampRound(v) != r || Track.stampCap(v) != (cap == 1)) ps = false;
            }
        }
        for (int e8 = 0; e8 <= 7; e8++) for (int e20 = 0; e20 <= 15; e20++) for (int m = 0; m <= 3; m++) for (int mr = 0; mr < 16; mr++) for (int lag = 0; lag <= 7; lag++) {
            int v = Track.packC(e8, e20, m, mr, lag);
            if (v < 0 || v > 65535 || Track.cEsc8(v) != e8 || Track.cEn20(v) != e20 || Track.cMiss(v) != m || Track.cMissR(v) != mr || Track.cLag(v) != lag) pc = false;
        }
        int sat = Track.packC(30, 40, 9, 1234, 99);
        check(pa && pb && pc && ps, "track: TRK_A/B/C and RT_STAMP pack/unpack round trips, all within 16 bits");
        check(Track.cEsc8(sat) == 7 && Track.cEn20(sat) == 15 && Track.cMiss(sat) == 3 && Track.cMissR(sat) == (1234 & 15) && Track.cLag(sat) == Track.LAG_UNKNOWN,
                "track: TRK_C counts saturate (7, 15, 3, lag 7) and keep round & 15");
        check(Track.stampRound(Track.packStamp(2000, true)) == 2000 && GameConstants.GAME_MAX_NUMBER_OF_ROUNDS < 2048, "track: 11-bit rounds hold the whole game");

        // predict: monotone toward D, one Chebyshev step at most per move, stops on the zone tile next to the centre
        boolean mono = true, edge = true, between = true;
        int[][] ld = {{5, 5, 40, 30}, {50, 2, 3, 40}, {10, 10, 10, 40}, {30, 30, 2, 30}, {7, 7, 8, 8}, {9, 9, 9, 9}, {0, 59, 59, 0}};
        for (int[] q : ld) for (int cls = 0; cls <= 2; cls++) {
            MapLocation prev = null;
            for (int age = 0; age <= 200; age++) {
                MapLocation p = Track.predict(q[0], q[1], q[2], q[3], cls, age);
                int dD = Track.cheb(p.x, p.y, q[2], q[3]);
                if (prev != null && (dD > Track.cheb(prev.x, prev.y, q[2], q[3]) || Track.cheb(p.x, p.y, prev.x, prev.y) > 1)) mono = false;
                if (p.x < Math.min(q[0], q[2]) || p.x > Math.max(q[0], q[2]) || p.y < Math.min(q[1], q[3]) || p.y > Math.max(q[1], q[3])) between = false;
                prev = p;
            }
            int n0 = Track.cheb(q[0], q[1], q[2], q[3]);
            if (Track.cheb(prev.x, prev.y, q[2], q[3]) != Math.min(1, n0)) edge = false;
        }
        check(mono, "track: predict is monotone toward D (never farther, at most one Chebyshev step a move)");
        check(edge && between, "track: predict stops at the zone edge (Chebyshev 1 from the centre) and stays within the L-D box");
        check(Track.predict(5, 5, 25, 5, Track.SLOW, 10).equals(new MapLocation(10, 5)) && Track.predict(5, 5, 25, 5, Track.CAP, 12).equals(new MapLocation(15, 5))
                && Track.predict(5, 5, 25, 5, Track.RELAY, 7).equals(new MapLocation(12, 5)), "track: speed classes move 1/2, 5/6 and 1 tile a round");
        boolean inv = true;
        for (int cls = 0; cls <= 2; cls++) for (int n = 1; n < 80; n++) {
            int t = Track.roundsFor(n, cls);
            if (Track.movesDone(t, cls) < n || Track.movesDone(t - 1, cls) >= n) inv = false;
        }
        check(inv, "track: roundsFor(n) is the first round with movesDone >= n");
        check(Track.MISS_R * Track.MISS_R <= C.TRK_MISS_R2 && (Track.MISS_R + 1) * (Track.MISS_R + 1) > C.TRK_MISS_R2, "track: MISS_R = floor(sqrt(TRK_MISS_R2))");

        // PSYM offset tables: DISC is the vision disc (dist2 <= 20), NEW[(ddx+1)*3+ddy+1] the tiles a one-tile move brings into view
        java.util.Set<Integer> disc = new java.util.HashSet<>();
        for (int k = 0; k < Track.DISC.length(); k++) { int c = Track.DISC.charAt(k); disc.add(((c >> 4) - 8) * 100 + (c & 15) - 8); }
        boolean tabOk = disc.size() == 69 && Track.DISC.length() == 69;
        for (int dx = -5; dx <= 5; dx++) for (int dy = -5; dy <= 5; dy++) if (disc.contains(dx * 100 + dy) != (dx * dx + dy * dy <= GameConstants.VISION_RADIUS_SQUARED)) tabOk = false;
        for (int ddx = -1; ddx <= 1; ddx++) for (int ddy = -1; ddy <= 1; ddy++) {
            java.util.Set<Integer> got = new java.util.HashSet<>(), want = new java.util.HashSet<>();
            String t = Track.NEW[(ddx + 1) * 3 + ddy + 1];
            for (int k = 0; k < t.length(); k++) { int c = t.charAt(k); got.add(((c >> 4) - 8) * 100 + (c & 15) - 8); }
            for (int o : disc) { int ox = Math.round(o / 100f), oy = o - ox * 100, qx = ox + ddx, qy = oy + ddy; if (qx * qx + qy * qy > GameConstants.VISION_RADIUS_SQUARED) want.add(o); }
            if (!got.equals(want) || got.size() != t.length()) tabOk = false;
        }
        check(tabOk, "track: PSYM offset tables are the vision disc and exactly the tiles each one-tile move brings into view");

        // the state machine on a fake controller: 30x30, our centres (3,3) (3,15) (10,3), theirs by rotation
        RobotController saveRc = G.rc;
        MapLocation[] saveSpawns = G.spawns;
        G.rc = fakeRc(); G.W = 30; G.H = 30; G.id = 4321; G.us = Team.A; G.them = Team.B; G.spawns = null;
        G.spawnCenters = new MapLocation[]{new MapLocation(3, 3), new MapLocation(3, 15), new MapLocation(10, 3)};
        zoneA = G.spawnCenters.clone();
        zoneB = new MapLocation[]{new MapLocation(26, 26), new MapLocation(26, 14), new MapLocation(19, 26)};
        resetTrack();
        G.rngState = 0x1234567; int rng0 = G.rngState; int cands0 = Sym.cands = 7; MapLocation nav0 = Nav.target;
        int exc0 = Track.exc;
        int id0 = 3 + 3 * 30;                                         // flag id = location index of its spawn centre
        FlagInfo atHome = new FlagInfo(new MapLocation(3, 3), Team.A, false, id0);

        trackTurn(150, new MapLocation(4, 4), new FlagInfo[]{atHome}, NONE);
        check(shared[Comms.RT_STAMP] == 0 && shared[Comms.TRK_A] == 0, "track: nothing written in setup");
        trackTurn(201, new MapLocation(4, 4), new FlagInfo[]{atHome}, NONE);
        check(shared[Comms.RT_STAMP] == 201 && st(0) == Track.HOME && Track.bRound(b0()) == 201
                && Track.aLoc(shared[Comms.TRK_A]) == Comms.enc(new MapLocation(3, 3)), "track: r201 home sighting stamps HOME and the round stamp");
        trackTurn(205, new MapLocation(4, 4), new FlagInfo[]{atHome}, NONE);
        check(Track.bRound(b0()) == 201, "track: HOME re-stamp waits HOME_CONFIRM rounds");
        trackTurn(209, new MapLocation(4, 4), new FlagInfo[]{atHome}, NONE);
        check(Track.bRound(b0()) == 209, "track: HOME re-stamped after HOME_CONFIRM rounds");

        int m0 = Track.missings;
        turn(212, new MapLocation(1, 6));                              // home in view, P(212) = (4,4) outside dist2 10
        check(st(0) == Track.MISSING && Track.bInf(b0()) && Track.bRound(b0()) == 209 && Track.missings == m0 + 1
                && Track.bDest(b0()) == 1, "track: home in view and empty -> MISSING, inferred, departure kept at the last confirmation, D nearest home");

        MapLocation cl = new MapLocation(10, 9);
        RobotInfo[] esc = {new RobotInfo(70, Team.B, 1000, cl, true, 0, 0, 0), new RobotInfo(71, Team.B, 1000, new MapLocation(11, 9), false, 0, 0, 0),
                new RobotInfo(72, Team.B, 1000, new MapLocation(10, 11), false, 0, 0, 0), new RobotInfo(73, Team.B, 1000, new MapLocation(13, 12), false, 0, 0, 0)};
        trackTurn(220, new MapLocation(12, 10), new FlagInfo[]{new FlagInfo(cl, Team.A, true, id0)}, esc);
        int cc = c0();
        check(st(0) == Track.CARRIED && !Track.bInf(b0()) && Track.bRound(b0()) == 220 && Track.cEsc8(cc) == 2 && Track.cEn20(cc) == 3
                && Track.bDest(b0()) == 1 && Track.aConf(shared[Comms.TRK_A]), "track: carried sighting -> CARRIED with escorts 2/3 (carrier excluded) and a heading-confirmed D");
        int w0 = Track.writes;
        trackTurn(220, new MapLocation(9, 8), new FlagInfo[]{new FlagInfo(cl, Team.A, true, id0)}, new RobotInfo[]{esc[0]});
        check(Track.writes == w0 && c0() == cc, "track: a second observer of the same sighting in the same round writes nothing (no escort loop)");

        // negative sightings: P(224) = 2 moves from (10,9) toward (26,14) = (12,11)
        MapLocation p224 = Track.predict(10, 9, 26, 14, Track.SLOW, 4);
        turn(224, p224.translate(0, 1));
        int b1 = b0();
        check(Track.cMiss(c0()) == 1 && Track.bDest(b1) == 2 && st(0) == Track.CARRIED, "track: first miss switches D to the other centre nearest L");
        turn(224, p224);
        check(Track.cMiss(c0()) == 1, "track: at most one miss a round");
        turn(225, new MapLocation(28, 1));                             // nobody near P at r225
        MapLocation p226 = Track.predict(10, 9, 19, 26, Track.SLOW, 6);
        turn(226, p226);
        check(Track.cMiss(c0()) == 2 && st(0) == Track.CARRIED, "track: a miss two rounds after the last one counts (round & 15, not a parity bit)");
        MapLocation p228 = Track.predict(10, 9, 19, 26, Track.SLOW, 8);
        turn(228, p228);
        check(Track.cMiss(c0()) == 3 && st(0) == Track.LOST && Track.bRound(b0()) == 220, "track: third miss -> LOST, a CARRIED track keeps its departure round");

        // expiry: arrival (19,26) from (10,9): N = 16 moves = 32 rounds; GONE once round > 220 + 32 + TRK_EXPIRE
        trackTurn(220 + 32 + C.TRK_EXPIRE, null, NOFLAG, NONE);
        check(st(0) == Track.LOST, "track: not expired at predicted arrival + TRK_EXPIRE");
        trackTurn(220 + 33 + C.TRK_EXPIRE, null, NOFLAG, NONE);
        check(st(0) == Track.GONE && Track.stampRound(shared[Comms.RT_STAMP]) == 220 + 33 + C.TRK_EXPIRE, "track: a jailed round-start runner expires the track (GONE) and stamps the round");
        turn(300, new MapLocation(4, 4));
        check(st(0) == Track.GONE, "track: MISSING never fires from GONE (a captured flag raises no phantom track)");

        // drops: seen on the ground away from home -> DROPPED; drop tile in view and empty inside the window -> re-grab
        MapLocation dl = new MapLocation(8, 8);
        FlagInfo onDrop = new FlagInfo(dl, Team.A, false, id0);
        turn(399, new MapLocation(4, 4), atHome);
        turn(400, new MapLocation(9, 9), new FlagInfo(dl, Team.A, true, id0));
        turn(401, new MapLocation(9, 9), onDrop);
        check(st(0) == Track.DROPPED && Track.bRound(b0()) == 401 && Track.cLag(c0()) == 1 && Track.bDest(b0()) != Track.NO_DEST,
                "track: dropped sighting -> DROPPED, first round seen, lag = rounds since the carried sighting");
        turn(402, new MapLocation(9, 9), onDrop);
        check(Track.bRound(b0()) == 401 && Track.cLag(c0()) == 1, "track: DROPPED keeps the first round seen dropped and its lag");
        int dj0 = Track.bDest(b0());
        boolean conf0 = Track.aConf(shared[Comms.TRK_A]);
        turn(403, new MapLocation(9, 9));                              // re-grab inferred with r0 = 402: P(403) = L (age 1, SLOW: 0 moves)
        check(st(0) == Track.CARRIED && Track.bInf(b0()) && Track.bRound(b0()) == 402, "track: empty drop tile inside the window -> CARRIED, inferred, r0 = round - 1");
        check(Track.cMiss(c0()) == 0 && Track.bDest(b0()) == dj0 && Track.aConf(shared[Comms.TRK_A]) == conf0,
                "track: no miss and no D switch in the turn of the inference (P = L, the tile just seen empty)");
        turn(404, new MapLocation(9, 9));
        check(Track.cMiss(c0()) == 0, "track: no miss on an inferred track while P is still within TRK_INF_HOLD2 (two moves) of L");
        turn(405, new MapLocation(3, 4), atHome);
        check(st(0) == Track.HOME && Track.bDest(b0()) == Track.NO_DEST && c0() == 0, "track: a home sighting returns any state to HOME");
        turn(410, new MapLocation(9, 9), onDrop);
        check(Track.cLag(c0()) == 5, "track: HOME -> DROPPED lag = rounds since the home confirmation");
        turn(410 + 4 + 1, new MapLocation(4, 4));
        check(st(0) == Track.CARRIED && Track.bInf(b0()) && Track.bRound(b0()) == 414, "track: empty home after the window -> CARRIED, inferred, r0 = window end");

        // a drop first seen late (S0b review): home confirmed r499, carrier dies unseen r500, flag first seen dropped r502; the
        // engine returns it at the end of r504, so an empty drop tile at r505 is no re-grab
        resetTrack();
        turn(499, new MapLocation(4, 4), atHome);
        turn(502, new MapLocation(9, 9), onDrop);
        check(st(0) == Track.DROPPED && Track.cLag(c0()) == 3, "track: late drop sighting keeps lag 3");
        turn(505, new MapLocation(9, 9));
        check(st(0) == Track.DROPPED && !Track.bInf(b0()), "track: no phantom re-grab from an empty drop tile once the earliest drop's window has passed");
        turn(506, new MapLocation(6, 6));                              // drop tile and home both in view, both empty: re-grabbed
        check(st(0) == Track.CARRIED && Track.bInf(b0()) && Track.bRound(b0()) == 505, "track: drop tile and home both empty -> re-grab inferred");
        resetTrack();
        turn(301, new MapLocation(4, 4), atHome);
        turn(330, new MapLocation(9, 9), onDrop);                      // lag 29: unknown
        turn(331, new MapLocation(9, 9));
        check(st(0) == Track.DROPPED && Track.cLag(c0()) == Track.LAG_UNKNOWN, "track: no drop-tile inference without a bound on the drop round");

        // DROPPED -> LOST (S0b review): the round becomes the window end, so GONE and P run from the presumed departure
        resetTrack();
        theirUpg = new GlobalUpgrade[]{GlobalUpgrade.CAPTURING};
        turn(700, new MapLocation(4, 4), atHome);
        check(Track.theyCap && Track.stampCap(shared[Comms.RT_STAMP]), "track: the round-start runner publishes their CAPTURING in RT_STAMP");
        turn(800, new MapLocation(9, 9), onDrop);
        check(Track.bCls(b0()) == Track.CAP, "track: HOME -> DROPPED with their CAPTURING takes speed class CAP (as HOME -> CARRIED)");
        int djd = Track.bDest(b0());
        MapLocation Dd = Track.dests()[djd];
        for (int r = 825; r <= 832 && st(0) != Track.LOST; r++) { G.round = r; Track.roundStart(); Track.pred(0); turn(r, new MapLocation(Track.px[0] + 1, Track.py[0])); }
        check(st(0) == Track.LOST && Track.bRound(b0()) == 800 + 25, "track: DROPPED -> LOST rewrites the round to the window end (" + Track.bRound(b0()) + ")");
        int dj2 = Track.bDest(b0());
        MapLocation D2 = Track.dests()[dj2];
        int arrive = 825 + Track.roundsFor(Math.max(0, Track.cheb(8, 8, D2.x, D2.y) - 1), Track.CAP) + C.TRK_EXPIRE;
        trackTurn(arrive, null, NOFLAG, NONE);
        check(st(0) == Track.LOST, "track: LOST from a drop is not expired before arrival from the window end");
        trackTurn(arrive + 1, null, NOFLAG, NONE);
        check(st(0) == Track.GONE, "track: ... and expires one round later");

        // their CAPTURING: one belief for the whole team in a round (the runner's), refreshed every round
        resetTrack();
        turn(600, new MapLocation(4, 4), atHome);                      // runner: no CAPTURING yet
        theirUpg = new GlobalUpgrade[]{GlobalUpgrade.CAPTURING};       // they buy it later in r600
        turn(600, new MapLocation(5, 5));                              // a later robot of ours in r600
        check(!Track.theyCap, "track: robots after the runner use the stamp's CAPTURING bit (same window for the whole team)");
        turn(601, new MapLocation(5, 5));
        check(Track.theyCap, "track: their purchase is seen by every robot from the next round");

        // MISSING raised by a duck next to home: no miss that turn, none while P is within TRK_INF_HOLD2 of home
        resetTrack();
        turn(209, new MapLocation(4, 4), atHome);
        turn(212, new MapLocation(4, 4));
        check(st(0) == Track.MISSING && Track.cMiss(c0()) == 0 && Track.bDest(b0()) == 1, "track: MISSING from next to home counts no miss in the same turn (D stays the centre nearest home)");
        turn(213, new MapLocation(4, 5));
        turn(214, new MapLocation(5, 4));
        check(st(0) == Track.MISSING && Track.cMiss(c0()) == 0, "track: MISSING: no miss while P is within two moves of home");
        turn(215, new MapLocation(5, 5));
        check(st(0) == Track.MISSING && Track.cMiss(c0()) == 1, "track: MISSING: misses count once P has left the ground the inference saw");

        // private symmetry: slot 16 all three; every tile seen is checked against our spawn tiles; never the true ROT
        resetTrack();
        shared[Comms.SYM] = 0;
        turn(500, new MapLocation(26, 5));
        check(shared[Comms.PSYM] == (Sym.ROT | Sym.FY), "track: PSYM prunes FX: (26,3) is not their spawn zone but its FX image (3,3) is ours (" + shared[Comms.PSYM] + ")");
        turn(501, new MapLocation(26, 11));
        check(shared[Comms.PSYM] == Sym.ROT && shared[Comms.SYM] == 0, "track: their zone tile (26,13) whose FY image (26,16) is not ours prunes FY; slot 16 never written");
        // walls: a tile and its image never in view together (per-robot memory). slot 16 = {FX, FY}, true FY-free map
        resetTrack();
        shared[Comms.SYM] = Sym.FX | Sym.FY;
        walls.add(new MapLocation(16, 10));                            // FX image (13,10) is not a wall
        turn(600, new MapLocation(20, 10));                            // sees (16,10), not (13,10)
        check(shared[Comms.PSYM] == 0, "track: one side of a wall pair alone prunes nothing");
        int tiles0 = Track.psymTiles;
        turn(601, new MapLocation(20, 10));
        check(Track.psymTiles == tiles0, "track: PSYM scans nothing while the robot has not moved");
        turn(602, new MapLocation(9, 10));                             // a jump: sees (13,10), not (16,10)
        check(shared[Comms.PSYM] == Sym.FY && !sensable(new MapLocation(16, 10)), "track: a remembered wall without a wall at its FX image prunes FX (" + shared[Comms.PSYM] + ")");
        walls.clear();
        // the true symmetry is never pruned: walls symmetric under ROT only, a walk over the whole map ends at ROT
        resetTrack();
        shared[Comms.SYM] = 0;
        for (int x = 0; x < 30; x++) for (int y = 0; y < 30; y++) if (((x * 7 + y * 13) % 11) == 0 || (x == 6 && y > 20)) { walls.add(new MapLocation(x, y)); walls.add(new MapLocation(29 - x, 29 - y)); }
        boolean lostTrue = false;
        int r = 900;
        for (int y = 2; y < 30 && !lostTrue; y += 4) for (int x = 0; x < 30; x++) {
            turn(r++, new MapLocation(y % 8 == 2 ? x : 29 - x, y));
            int pm = shared[Comms.PSYM];
            if (pm != 0 && (pm & Sym.ROT) == 0) lostTrue = true;
        }
        check(!lostTrue && shared[Comms.PSYM] == Sym.ROT, "track: PSYM never loses the true symmetry and decides it on a walk (" + shared[Comms.PSYM] + ")");
        walls.clear();

        // identity and safety (2.9): no RNG draw, no write outside 23-33, Sym/Nav untouched, exceptions caught
        check(G.rngState == rng0, "track: G.rngState unchanged across every Track call");
        check(!badWrite, "track: writes only slots 23-33, values within 16 bits");
        check(Sym.cands == cands0 && Nav.target == nav0, "track: Sym.cands and Nav state untouched");
        check(Track.exc == exc0, "track: no exception in the scripted game (" + (Track.exc - exc0) + ")");
        failReads = true;
        boolean escaped = false;
        try { trackTurn(700, new MapLocation(4, 4), new FlagInfo[]{atHome}, NONE); } catch (Throwable t) { escaped = true; }
        failReads = false;
        check(!escaped && Track.exc > exc0, "track: entry points catch their own exceptions into Track.exc");
        check("HCDMLG-".indexOf(Track.headline()) >= 0, "track: headline letter is a state letter");
        G.rc = saveRc;
        G.spawns = saveSpawns;
    }

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
        int[][] ranges = {{Comms.IDX, 1}, {Comms.EF_ID, 3}, {Comms.EF_LOC, 3}, {Comms.EF_STATE, 3}, {Comms.OF_ALERT, 3}, {Comms.OF_LOC, 3}, {Comms.SYM, 1}, {Comms.OF_CARRY, 3},
                {Comms.OF_HOME, 3}, {Comms.RT_STAMP, 1}, {Comms.PSYM, 1}, {Comms.TRK_A, 3}, {Comms.TRK_B, 3}, {Comms.TRK_C, 3}, {Comms.OWN_C, 3}, {Comms.AUC, Comms.AUC_SLOTS}};
        boolean[] used = new boolean[64]; boolean ok = true;
        for (int[] r : ranges) for (int i = r[0]; i < r[0] + r[1]; i++) { if (i >= 64 || used[i]) ok = false; else used[i] = true; }
        check(ok, "comms: slot ranges disjoint and < 64 (OF_HOME 20-22, rewrite 23-48 included)");
        boolean contiguous = true;
        for (int i = 0; i <= Comms.AUC + Comms.AUC_SLOTS - 1; i++) if (!used[i]) contiguous = false;
        check(contiguous && Comms.RT_STAMP == 23 && Comms.OWN_C + 3 == Comms.AUC && Comms.AUC + Comms.AUC_SLOTS == 49,
                "comms: slots 0-48 all assigned, rewrite slots 23-48 as design 2.3");

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

        // symmetry: images are involutions; broadcasts near the rotational images select rotation only
        G.W = 40; G.H = 30;
        boolean inv = true;
        for (int sy = 1; sy <= 4; sy <<= 1) for (int x = 0; x < 40; x += 7) for (int y = 0; y < 30; y += 5) {
            MapLocation m = new MapLocation(x, y); if (!Sym.image(Sym.image(m, sy), sy).equals(m)) inv = false; }
        check(inv, "sym: every image is an involution");
        G.spawnCenters = new MapLocation[]{new MapLocation(3, 3), new MapLocation(3, 15), new MapLocation(10, 3)};
        Sym.cands = 7;
        MapLocation[] hints = new MapLocation[3];
        for (int i = 0; i < 3; i++) hints[i] = Sym.image(G.spawnCenters[i], Sym.ROT).translate(2, -3);
        Sym.fromBroadcasts(hints);
        check((Sym.cands & Sym.ROT) != 0 && (Sym.cands & Sym.FY) == 0, "sym: rotation-consistent hints keep rotation and prune flip-y (" + Sym.cands + ")");
        Sym.cands = 7;
        for (int i = 0; i < 3; i++) hints[i] = Sym.image(G.spawnCenters[i], Sym.FX);
        Sym.fromBroadcasts(hints);
        check((Sym.cands & Sym.FX) != 0 && Sym.cands != 0, "sym: true symmetry is never pruned, set never empty");
        Sym.cands = Sym.FY;
        check(Sym.enemyCenters()[0].equals(new MapLocation(3, 26)), "sym: enemy centre under flip-y");

        G.W = 40; G.spawnCenters = new MapLocation[]{new MapLocation(3, 3), new MapLocation(3, 15), new MapLocation(10, 3)};
        check(Comms.ourFlagIndex(3 + 15 * 40) == 1 && Comms.ourFlagIndex(7) == -1, "comms: flag id -> our flag index via spawn centre location index");
        // block-1 bank floor: 0 when off; with C.BUDGET_V1 it would be "no discretionary spend in setup" then paced down
        G.round = 150; check(C.BUDGET_V1 || G.bankFloor() == 0, "bank floor: 0 when BUDGET_V1 is off");
        check(C.BANK_FLOOR0 - (C.SETUP_ROUNDS + 1000 - C.SETUP_ROUNDS) * C.BANK_PACE <= 0, "bank floor: pacing reaches 0 well before the round cap");

        // constants: the near-miss bar sits below the limit; gather happens inside setup
        check(C.NEAR_MISS_BC < GameConstants.BYTECODE_LIMIT, "const: near-miss below the limit");
        check(C.GATHER_ROUND < C.SETUP_ROUNDS && C.SETUP_ROUNDS == GameConstants.SETUP_ROUNDS, "const: gather inside setup; setup length matches engine");

        trackTests();

        System.out.println("BotTest: " + (fails == 0 ? "OK" : "FAILED " + fails));
        if (fails > 0) System.exit(1);
    }
}
