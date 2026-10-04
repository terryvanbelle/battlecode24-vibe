package bot;

import battlecode.common.*;

/** Regression tests for the 2026-10-02 correctness audit (research/AUDIT-2026-10-02.md). */
public class AuditTest {
    static int fails = 0;
    static void check(boolean ok, String what) { if (!ok) { fails++; System.out.println("FAIL " + what); } }
    static RobotInfo enemy(int id, int x, int y) { return new RobotInfo(id, Team.B, 1000, new MapLocation(x, y), false, 0, 0, 0); }

    /** RELOC_SPREAD (audit BOT16): the spots computed with the scan paused at every column (one column per call) equal
     *  the spots computed in one go, and the paused run really did take more calls. */
    static boolean spreadSame() {
        int keepRes = Duck.scanReserve, keepBc = G.testBc;
        MapLocation[][] spot = new MapLocation[2][3];
        int[] calls = new int[2];
        G.testBc = 25000;
        for (int run = 0; run < 2; run++) {
            Duck.relocK = 0; Duck.scanK = -1; Duck.scanReserve = run == 0 ? 0 : Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                MapLocation m = null;
                for (int t = 0; t < 200 && m == null; t++) { m = Duck.relocTargetV2(i); calls[run]++; }
                spot[run][i] = m;
            }
        }
        Duck.scanReserve = keepRes; G.testBc = keepBc; Duck.relocK = 0; Duck.scanK = -1;
        for (int i = 0; i < 3; i++) if (spot[0][i] == null || !spot[0][i].equals(spot[1][i])) return false;
        return calls[1] > calls[0] + 6;
    }

    /** RELOC_V2 spots for the current G.spawnCenters / Sym.cands: none nearer the nearest candidate enemy centre than its
     *  spawn centre, (optionally) at least one strictly farther, pairwise 8+ tiles apart, each within RELOC_R2 of its spawn. */
    static boolean relocOk(int[] syms, boolean someFarther) {
        MapLocation[] spot = new MapLocation[3];
        for (int i = 0; i < 3; i++) { MapLocation m = null; for (int t = 0; t < 4 && m == null; t++) m = Duck.relocTargetV2(i); spot[i] = m; }
        boolean ok = true, farther = false;
        for (int i = 0; i < 3; i++) {
            if (spot[i] == null) return false;
            int nearSpot = Integer.MAX_VALUE, nearHome = Integer.MAX_VALUE;
            for (int sym : syms) for (MapLocation c : G.spawnCenters) {
                MapLocation e = Sym.image(c, sym);
                nearSpot = Math.min(nearSpot, spot[i].distanceSquaredTo(e)); nearHome = Math.min(nearHome, G.spawnCenters[i].distanceSquaredTo(e));
            }
            if (nearSpot < nearHome) ok = false;
            if (nearSpot > nearHome) farther = true;
            if (spot[i].distanceSquaredTo(G.spawnCenters[i]) > C.RELOC_R2) ok = false;
            for (int j = 0; j < i; j++) if (spot[i].distanceSquaredTo(spot[j]) < 64) ok = false;
        }
        return ok && (farther || !someFarther);
    }

    public static void main(String[] a) {
        // A1: an alert means an enemy within ALERT_THREAT_R2 (20) of the flag home -- not "an enemy seen by a duck near it"
        MapLocation home = new MapLocation(10, 10);
        check(Comms.threatTo(home, new RobotInfo[]{enemy(1, 17, 11)}) == null, "A1: an enemy at dist2 50 from the home is no threat");
        RobotInfo near = enemy(2, 13, 12);                     // dist2 13
        check(Comms.threatTo(home, new RobotInfo[]{enemy(1, 17, 11), near}) == near, "A1: an enemy at dist2 13 is the threat");
        RobotInfo nearer = enemy(3, 11, 11);                   // dist2 2
        check(Comms.threatTo(home, new RobotInfo[]{near, nearer}) == nearer, "A1: the nearest threatening enemy is recorded");
        check(Comms.threatTo(home, new RobotInfo[]{enemy(4, 14, 12)}) != null, "A1: an enemy at exactly dist2 20 counts");
        check(Comms.threatTo(home, new RobotInfo[]{enemy(5, 14, 13)}) == null, "A1: an enemy at dist2 25 does not");
        check(Comms.threatTo(null, new RobotInfo[]{near}) == null, "A1: no home, no threat");
        check(C.ALERT_THREAT_R2 == 20, "A1: the threat radius is the flag's vision radius (dist2 20)");
        // A5/A6 (C.REG_FIX): registry staleness, through the real Comms code on BotTest's fake controller
        G.W = 40; G.H = 30; G.rc = BotTest.fakeRc(); G.me = null; BotTest.theirUpg = new GlobalUpgrade[0];
        java.util.Arrays.fill(BotTest.shared, 0);
        int id = 5 + 7 * 40; MapLocation home2 = new MapLocation(5, 7), drop = new MapLocation(20, 15);
        try {
            BotTest.shared[Comms.EF_ID] = id + 1; BotTest.shared[Comms.EF_STATE] = 1; BotTest.shared[Comms.EF_LOC] = Comms.enc(drop);
            G.round = 500; Comms.enemyFlagDropped(id);
            check(BotTest.shared[Comms.EF_STATE] == 0 && BotTest.shared[Comms.EF_DROP] == 500, "A5: our dead carrier's flag is dropped, not carried forever");
            G.round = 504; Comms.expireDrops();
            check(BotTest.shared[Comms.EF_LOC] == Comms.enc(drop), "A6: inside the 4-round window the drop tile stays");
            BotTest.shared[Comms.EF_HOME] = Comms.enc(home2);
            G.round = 505; Comms.expireDrops();
            check(BotTest.shared[Comms.EF_LOC] == Comms.enc(home2) && BotTest.shared[Comms.EF_DROP] == 0, "A6: after the window the registry points home");
            BotTest.shared[Comms.EF_HOME] = 0; BotTest.shared[Comms.EF_LOC] = Comms.enc(drop); BotTest.shared[Comms.EF_DROP] = 500;
            Comms.expireDrops();
            check(BotTest.shared[Comms.EF_LOC] == 0, "A6: with no known home the stale tile is cleared (targeting falls back to the hints)");
        } catch (GameActionException e) { check(false, "A5/A6: unexpected " + e); }
        // A11(a): a carry sighting is forgotten once our flag is seen not carried (sense() calls this under C.DEST_CAMP)
        java.util.Arrays.fill(BotTest.shared, 0);
        G.spawnCenters[0] = new MapLocation(5, 7); G.spawnCenters[1] = new MapLocation(5, 20); G.spawnCenters[2] = new MapLocation(12, 25);
        MapLocation[] ec = {new MapLocation(34, 22), new MapLocation(34, 9), new MapLocation(27, 4)};
        try {
            G.round = 100; Comms.reportCarried(0, new MapLocation(15, 12));
            G.round = 110;
            check(Comms.carriedAge(0) == 10 && Duck.campTarget(new MapLocation(20, 15), ec) != null, "A11(a): a young carry sighting sets a camp");
            G.round = 103; Comms.clearCarried(0);
            G.round = 110;
            check(Comms.carriedAge(0) == Integer.MAX_VALUE && Comms.carried(0, 99) == null && Duck.campTarget(new MapLocation(20, 15), ec) == null,
                  "A11(a): after our flag is seen not carried there is no carry and no camp");
        } catch (GameActionException e) { check(false, "A11(a): unexpected " + e); }
        check(C.REG_FIX && C.ALERT_FIX && C.REACH_FIX && C.REACH_FAST && C.NAV_FIX && Sym.OBSERVE && !C.TRACK && C.RELOCATE_FLAGS && C.RELOC_V2 && C.CARRIER_STUN && !C.DEST_CAMP && !C.BUDGET_V1 && !C.ESCORT_TIGHT && C.FLAG_LOST && !C.DEF_TETHER && !C.PICKUP_AFTER_MOVE && !C.RELOC_STALL_MOVES && !C.CARRY_PREDICT && !C.STUN_AHEAD && !C.FILL_STEP && !C.RELOC_SPREAD && !C.ALERT_NEAREST && !C.INIT_FAST && !C.STUN_FRONT && !C.STUN_WARY && !C.CRUMB_STEP && !C.POST_SETUP_CRUMBS && !C.BUILDERS,
              "src/bot plays as the incumbent g_iter4 (g_iter3 + captured flags recognised; the track sensor off)");

        // A2: an enemy flag id is the location index of their spawn centre; one id decides the symmetry (audit example)
        G.W = 59; G.H = 59; Sym.cands = 7; Sym.conflicts = 0; Sym.decidedRound = -1; G.spawns = null;
        G.spawnCenters[0] = new MapLocation(32, 6); G.spawnCenters[1] = new MapLocation(17, 16); G.spawnCenters[2] = new MapLocation(45, 20);
        Sym.observeEnemyCentre(17 + 42 * 59);
        check(Sym.cands == Sym.FY && Sym.conflicts == 0, "A2: flag id (17,42) leaves only FY (" + Sym.cands + ")");

        // A7: only a real change of goal resets bug-following
        MapLocation g0 = new MapLocation(10, 26);
        check(!Nav.resetNeeded(g0, new MapLocation(11, 26), true) && Nav.resetNeeded(g0, new MapLocation(20, 26), true),
              "A7: a one-tile target move keeps the bug state; a new goal resets it");
        check(Nav.resetNeeded(g0, new MapLocation(11, 26), false) && Nav.resetNeeded(null, g0, true), "A7: g_iter1 reset on any move; no target resets");

        // A4: an enemy behind a wall is not engageable; in the open, or close, it is
        G.W = 40; G.H = 30; G.rc = BotTest.fakeRc(); G.me = new MapLocation(10, 10); G.testBc = 25000;
        BotTest.walls.clear();
        for (int y = 0; y < 30; y++) { BotTest.walls.add(new MapLocation(12, y)); BotTest.walls.add(new MapLocation(13, y)); }
        try {
            check(!Micro.engageable(new RobotInfo[]{enemy(9, 14, 12)}), "A4: an enemy behind a 2-wide wall (dist2 20) does not take the turn");
            check(Micro.engageable(new RobotInfo[]{enemy(9, 12, 12)}), "A4: an enemy within dist2 8 always does");
            BotTest.walls.clear();
            check(Micro.engageable(new RobotInfo[]{enemy(9, 14, 12)}), "A4: the same enemy in the open is engageable");
        } catch (GameActionException e) { check(false, "A4: unexpected " + e); }
        // C.REACH_FAST: the cheap search gives the reference search's answer (random walls, enemies in view, map edges)
        java.util.Random rnd = new java.util.Random(4242);
        int agree = 0, yes = 0, trials = 3000;
        try {
            for (int t = 0; t < trials; t++) {
                G.W = 20 + rnd.nextInt(41); G.H = 20 + rnd.nextInt(41);
                G.me = new MapLocation(rnd.nextInt(G.W), rnd.nextInt(G.H));
                BotTest.walls.clear();
                double dens = 0.3 + rnd.nextDouble() * 0.6;
                for (int x = G.me.x - 5; x <= G.me.x + 5; x++) for (int y = G.me.y - 5; y <= G.me.y + 5; y++)
                    if (rnd.nextDouble() < dens && (x != G.me.x || y != G.me.y)) BotTest.walls.add(new MapLocation(x, y));
                RobotInfo[] es = new RobotInfo[1 + (rnd.nextInt(4) == 0 ? rnd.nextInt(15) : rnd.nextInt(3))];
                for (int i = 0; i < es.length; i++) {
                    MapLocation l;
                    do { l = new MapLocation(G.me.x - 4 + rnd.nextInt(9), G.me.y - 4 + rnd.nextInt(9)); }
                    while (G.me.distanceSquaredTo(l) <= 8 || G.me.distanceSquaredTo(l) > 20 || l.x < 0 || l.y < 0 || l.x >= G.W || l.y >= G.H);
                    es[i] = enemy(100 + i, l.x, l.y);
                }
                boolean r = Micro.engageableRef(es), f = Micro.engageableFast(es);
                if (r == f) agree++;
                if (r) yes++;
            }
        } catch (GameActionException e) { check(false, "REACH_FAST: unexpected " + e); }
        check(agree == trials && yes > trials / 10 && yes < trials * 9 / 10,
              "REACH_FAST: fast and reference searches agree (" + agree + "/" + trials + ", " + yes + " engageable)");
        G.testBc = -1;

        // STUN_FRONT: a line of 4 enemies 3 tiles east: a tile 2 from the nearest with all 4 in the stun radius; never adjacent
        // to an enemy; none with 2 enemies; none when no enemy is one step from the trigger radius
        G.W = 40; G.H = 30; G.rc = BotTest.fakeRc(); G.me = new MapLocation(10, 10); BotTest.walls.clear(); BotTest.buildOk = true;
        try {
            Duck.enemies = new RobotInfo[]{enemy(1, 13, 10), enemy(2, 13, 11), enemy(3, 14, 10), enemy(4, 13, 9)};
            MapLocation ft = Duck.frontStunTile();
            int v = 0, adj = 0;
            if (ft != null) for (RobotInfo e : Duck.enemies) { int x = ft.distanceSquaredTo(e.location); if (x <= 13) v++; if (x <= 2) adj++; }
            check(ft != null && ft.x == 11 && v == 4 && adj == 0, "STUN_FRONT: the east-side tile with all 4 enemies in the radius (" + ft + ")");
            Duck.enemies = new RobotInfo[]{enemy(1, 13, 10), enemy(2, 13, 11)};
            check(Duck.frontStunTile() == null, "STUN_FRONT: two enemies are not worth a stun");
            Duck.enemies = new RobotInfo[]{enemy(1, 15, 10), enemy(2, 15, 11), enemy(3, 16, 10)};
            check(Duck.frontStunTile() == null, "STUN_FRONT: enemies 5 tiles away are not one step from the trigger");
        } catch (GameActionException e) { check(false, "STUN_FRONT: unexpected " + e); }
        BotTest.buildOk = false; Duck.enemies = new RobotInfo[0];

        // STUN_WARY: me (10,10), an enemy at (13,10) this round: stepping east puts (12,9..11) within dist2 2, beside the
        // enemy and beside none of ours: risky; west is clear; with an ally at (12,12), (12,11) is covered but (12,9) and (12,10) are not;
        // an enemy seen beside a tile WARY_ROUNDS+1 rounds ago no longer counts
        G.W = 40; G.H = 30; G.round = 500; Micro.enemyNearStamp = null;
        MapLocation wm = new MapLocation(10, 10);
        Micro.waryPrep(new RobotInfo[]{enemy(1, 13, 10)}, new RobotInfo[0], wm);
        check(Micro.waryRisk(new MapLocation(11, 10), wm) == 3 && Micro.waryRisk(new MapLocation(9, 10), wm) == 0,
              "STUN_WARY: a step toward a fresh enemy front is risky (3 suspect tiles), a step away is not");
        Micro.waryPrep(new RobotInfo[]{enemy(1, 13, 10)}, new RobotInfo[]{new RobotInfo(9, Team.A, 1000, new MapLocation(12, 12), false, 0, 0, 0)}, wm);
        check(Micro.waryRisk(new MapLocation(11, 10), wm) == 2, "STUN_WARY: tiles beside an ally are clear ((12,11) covered; (12,9), (12,10) stay suspect)");
        G.round = 500 + C.WARY_ROUNDS + 1;
        Micro.waryPrep(new RobotInfo[0], new RobotInfo[0], wm);
        check(Micro.waryRisk(new MapLocation(11, 10), wm) == 0, "STUN_WARY: an enemy seen beside a tile WARY_ROUNDS+1 rounds ago no longer counts");
        Micro.enemyNearStamp = null;

        // CRUMB_STEP (audit BOT4) is off in the incumbent: with nothing else to choose between, the step ignores the crumb
        // tile; the arm's bonus is checked by its 5(a) counter cr. Here: the fight loop runs and moves (fake controller).
        G.W = 40; G.H = 30; G.rc = BotTest.fakeRc(); G.me = new MapLocation(10, 10); BotTest.moveOk = true; BotTest.lastMove = null;
        BotTest.crumbTiles = new MapLocation[]{new MapLocation(11, 10)};
        try {
            Micro.fight(new RobotInfo[0], new RobotInfo[0]);
            check(C.CRUMB_STEP ? BotTest.lastMove == Direction.EAST : BotTest.lastMove == null,
                  "CRUMB_STEP: " + (C.CRUMB_STEP ? "steps onto the crumb tile" : "off: no reason to move, so no move") + " (" + BotTest.lastMove + ")");
        } catch (GameActionException e) { check(false, "CRUMB_STEP: unexpected " + e); }
        BotTest.moveOk = false; BotTest.crumbTiles = new MapLocation[0];

        // BUILDERS: idx 9, 19, ..., 49 (never a defender 0-2 or scout 3-5); off in the incumbent, so nobody is one
        check(Duck.builderIdx(9) && Duck.builderIdx(19) && Duck.builderIdx(49) && !Duck.builderIdx(10) && !Duck.builderIdx(5) && !Duck.builderIdx(0),
              "BUILDERS: one field duck in ten");
        { int keep = G.idx; G.idx = 9; check(Duck.isBuilder() == C.BUILDERS, "BUILDERS: isBuilder follows the switch"); G.idx = keep; }

        // INIT_FAST: the bitset finds the same spawn centres, in the same order, as the 27x27 loop (random 3x3 zones, map edges)
        java.util.Random ir = new java.util.Random(77);
        int same = 0, itr = 500;
        for (int t = 0; t < itr; t++) {
            G.W = 30 + ir.nextInt(31); G.H = 30 + ir.nextInt(31);
            java.util.LinkedHashSet<MapLocation> sp3 = new java.util.LinkedHashSet<>();
            for (int z = 0; z < 3; z++) {
                int cx = 1 + ir.nextInt(G.W - 2), cy = 1 + ir.nextInt(G.H - 2);
                for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) sp3.add(new MapLocation(cx + dx, cy + dy));
            }
            java.util.List<MapLocation> shuffled = new java.util.ArrayList<>(sp3);
            java.util.Collections.shuffle(shuffled, ir);
            G.spawns = shuffled.toArray(new MapLocation[0]);
            MapLocation[] ref = new MapLocation[3]; int n = 0;
            for (MapLocation sa : G.spawns) {
                int adj = 0;
                for (MapLocation sb : G.spawns) if (sa.isAdjacentTo(sb) && !sa.equals(sb)) adj++;
                if (adj == 8 && n < 3) ref[n++] = sa;
            }
            G.spawnCenters = new MapLocation[3]; Sym.ours = null;
            G.initCentres();
            if (java.util.Arrays.equals(ref, G.spawnCenters)) same++;
        }
        check(same == itr, "INIT_FAST: bitset spawn centres equal the 27x27 loop's (" + same + "/" + itr + ")");
        G.spawnCenters = new MapLocation[3]; Sym.ours = null;

        // RELOC_V2: spots within the walk bound, never closer to the nearest candidate enemy centre than the spawn, 8+ apart
        G.W = 60; G.H = 40; Sym.cands = Sym.ROT | Sym.FX;
        G.spawnCenters[0] = new MapLocation(10, 8); G.spawnCenters[1] = new MapLocation(12, 20); G.spawnCenters[2] = new MapLocation(9, 31);
        Duck.relocK = 0; G.round = 10;
        check(Duck.relocTargetV2(0) == null, "RELOC_V2: undecided before RELOC_DECIDE: no spot yet");
        G.round = C.RELOC_DECIDE;
        check(relocOk(new int[]{Sym.ROT, Sym.FX}, true), "RELOC_V2: three spots, none closer to a candidate enemy centre than its spawn, some farther, 8+ apart, within the walk bound");
        // DefaultMedium (2026-10-03 diag): the greedy version moved flag 2 from 32 to 27 tiles of the nearest enemy spawn
        G.W = 44; G.H = 31; Sym.cands = Sym.FX; Duck.relocK = 0;
        G.spawnCenters[0] = new MapLocation(3, 3); G.spawnCenters[1] = new MapLocation(3, 23); G.spawnCenters[2] = new MapLocation(9, 27);
        check(relocOk(new int[]{Sym.FX}, false), "RELOC_V2: DefaultMedium layout: no flag closer than at its spawn");

        // Whirlpool (2026-10-03 diag): an earlier flag's spot beside another flag's spawn forced that flag 25.6 -> 23.0 tiles
        G.W = 30; G.H = 30; Sym.cands = Sym.FY; Duck.relocK = 0;
        G.spawnCenters[0] = new MapLocation(3, 23); G.spawnCenters[1] = new MapLocation(27, 26); G.spawnCenters[2] = new MapLocation(11, 27);
        check(relocOk(new int[]{Sym.FY}, false), "RELOC_V2: Whirlpool layout: no flag closer than at its spawn");
        check(spreadSame(), "RELOC_SPREAD: Whirlpool spots are the same when every scan pauses after each column");
        G.W = 44; G.H = 31; Sym.cands = Sym.FX | Sym.ROT | Sym.FY;
        G.spawnCenters[0] = new MapLocation(3, 3); G.spawnCenters[1] = new MapLocation(3, 23); G.spawnCenters[2] = new MapLocation(9, 27);
        check(spreadSame(), "RELOC_SPREAD: DefaultMedium spots, all three symmetries live, are the same when every scan pauses");

        // CAMP_SPLIT: near-equidistant spawns are both camp candidates, split by id; a clearly nearest one is the only one
        MapLocation[] sp = {new MapLocation(36, 8), new MapLocation(33, 19), new MapLocation(41, 50)};
        MapLocation sight = new MapLocation(1, 2);
        java.util.Set<MapLocation> got = new java.util.HashSet<>();
        int keepId = G.id;
        for (int rid = 0; rid < 6; rid++) { G.id = rid; got.add(Duck.campDest(sight, sp)); }
        G.id = keepId;
        check(got.size() == 2 && got.contains(sp[0]) && got.contains(sp[1]), "CAMP_SPLIT: Joker-like spawns at 35.5 and 36.2 tiles are both camped (" + got + ")");
        check(Duck.campDest(new MapLocation(40, 45), sp).equals(sp[2]), "CAMP_SPLIT: a clearly nearest spawn is the only camp");

        // FLAG_LOST (audit 2026-10-03 BOT1): alerts skip a lost flag; its defender re-homes to the nearest live home
        G.W = 40; G.H = 30; G.rc = BotTest.fakeRc(); G.me = new MapLocation(5, 5); java.util.Arrays.fill(BotTest.shared, 0);
        try {
            G.round = 500;
            BotTest.shared[Comms.OF_ALERT] = 500; BotTest.shared[Comms.OF_ALERT + 1] = 499;
            check(Duck.alertedFlagSkipping(0) == 0 && Duck.alertedFlagSkipping(1) == 1 && Duck.alertedFlagSkipping(3) == -1,
                  "FLAG_LOST: the freshest alert among flags not lost");
            BotTest.shared[Comms.OF_HOME] = Comms.enc(new MapLocation(3, 3)); BotTest.shared[Comms.OF_HOME + 1] = Comms.enc(new MapLocation(3, 20));
            BotTest.shared[Comms.OF_HOME + 2] = Comms.enc(new MapLocation(10, 27));
            check(Duck.liveHome(0, 1).equals(new MapLocation(3, 20)) && Duck.liveHome(0, 0).equals(new MapLocation(3, 3))
                  && Duck.liveHome(2, 7).equals(new MapLocation(10, 27)), "FLAG_LOST: a lost flag's defender goes to the nearest live home");
            // ALERT_NEAREST (audit BOT9): alerts on flags 0 and 1; a duck near home 1 answers flag 1 though flag 0's is fresher
            G.me = new MapLocation(4, 18);
            check(Duck.alertedFlagSkipping(0) == 0 && Duck.alertNearest(G.me, 0, C.ALERT_RADIUS2) == 1,
                  "ALERT_NEAREST: the nearest fresh alert, not the freshest");
            check(Duck.alertNearest(G.me, 2, C.ALERT_RADIUS2) == -1, "ALERT_NEAREST: a lost flag's alert, or one beyond ALERT_RADIUS2, is not answered");
            BotTest.shared[Comms.OF_ALERT + 1] = 480;
            check(Duck.alertNearest(G.me, 0, C.ALERT_RADIUS2) == -1, "ALERT_NEAREST: a stale alert (20 rounds) is not answered");
            BotTest.shared[Comms.OF_ALERT + 1] = 499; BotTest.shared[Comms.OF_ALERT + 2] = 495;
            int keepIdx = G.idx; java.util.Set<Integer> sp2 = new java.util.HashSet<>();
            for (int q = 0; q < 6; q++) { G.idx = q; sp2.add(Duck.alertSplit(0)); }
            G.idx = keepIdx;
            check(sp2.size() == 3 && Duck.alertSplit(7) == -1, "ALERT_NEAREST: respawns split over all three alerted flags (" + sp2 + ")");
        } catch (GameActionException e) { check(false, "FLAG_LOST: unexpected " + e); }

        // PICKUP_AFTER_MOVE (audit BOT5): a loose enemy flag one step away (dist2 <= 8) is found; a carried one or one 3 away is not
        G.them = Team.B; G.me = new MapLocation(10, 10);
        Duck.flags = new FlagInfo[]{new FlagInfo(new MapLocation(12, 12), Team.B, false, 7), new FlagInfo(new MapLocation(11, 10), Team.B, true, 8)};
        check(new MapLocation(12, 12).equals(Micro.looseFlagNear(G.me)), "PICKUP_AFTER_MOVE: a dropped enemy flag at dist2 8 is one step away");
        Duck.flags = new FlagInfo[]{new FlagInfo(new MapLocation(13, 10), Team.B, false, 7), new FlagInfo(new MapLocation(11, 10), Team.B, true, 8)};
        check(Micro.looseFlagNear(G.me) == null, "PICKUP_AFTER_MOVE: dist2 9 or a carried flag is not");
        Duck.flags = new FlagInfo[0];

        // CARRY_PREDICT (audit BOT10): last seen (10,10) heading to (40,10); at age 20 the carrier is ~10 tiles on, we aim 2 ahead
        MapLocation pr = Duck.predictCarrier(new MapLocation(10, 10), 20, new MapLocation(40, 10));
        check(pr != null && Math.abs(pr.x - 22) <= 1 && pr.y == 10, "CARRY_PREDICT: predicted point (22,10) +- 1 at age 20 (" + pr + ")");
        check(Duck.predictCarrier(new MapLocation(10, 10), 60, new MapLocation(40, 10)) == null
              && Duck.predictCarrier(new MapLocation(10, 10), 2 * 30 + 2 * C.PREDICT_MARGIN + 2, new MapLocation(40, 10)) == null,
              "CARRY_PREDICT: no prediction at age 60 or after the predicted arrival");
        // STUN_AHEAD (audit BOT7): carrier (10,10) heading to (20,10); a builder behind it does not build, one ahead does
        check(!Duck.aheadOf(new MapLocation(8, 10), new MapLocation(10, 10), new MapLocation(20, 10))
              && Duck.aheadOf(new MapLocation(13, 11), new MapLocation(10, 10), new MapLocation(20, 10)), "STUN_AHEAD: behind no, ahead yes");

        System.out.println("AuditTest: " + (fails == 0 ? "OK" : "FAILED " + fails));
        if (fails > 0) System.exit(1);
    }
}
