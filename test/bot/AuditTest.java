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
        check(C.REG_FIX && C.ALERT_FIX && C.REACH_FIX && C.REACH_FAST && C.NAV_FIX && Sym.OBSERVE && !C.TRACK && C.RELOCATE_FLAGS && C.RELOC_V2 && C.CARRIER_STUN && !C.DEST_CAMP && !C.BUDGET_V1 && !C.ESCORT_TIGHT && C.FLAG_LOST && !C.DEF_TETHER && !C.PICKUP_AFTER_MOVE && !C.RELOC_STALL_MOVES && !C.CARRY_PREDICT && !C.STUN_AHEAD && !C.FILL_STEP && !C.RELOC_SPREAD && !C.ALERT_NEAREST && !C.INIT_FAST && !C.STUN_FRONT && !C.STUN_WARY && !C.CRUMB_STEP && !C.POST_SETUP_CRUMBS && !C.BUILDERS && !C.RELAY && !C.RELAY_THREAT && !C.LEVEL_FARM && !C.DAM_FIRST && !C.CONTACT,
              "src/bot plays as the incumbent g_iter4 (g_iter3 + captured flags recognised; the track sensor and the contact dive off)");

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

        // RELAY_THREAT: a healthy carrier with no enemy in view keeps the flag; hurt or threatened it hands over; off: always
        check(!Duck.relayWanted(true, 1000, 0) && Duck.relayWanted(true, C.RELAY_HP - 1, 0) && Duck.relayWanted(true, 1000, 2)
              && Duck.relayWanted(false, 1000, 0), "RELAY_THREAT: hand over only in danger");

        // LEVEL_FARM: captures level when our captured registry slots equal our lost flags
        G.rc = BotTest.fakeRc(); java.util.Arrays.fill(BotTest.shared, 0);
        try {
            check(Duck.capturesLevel(), "LEVEL_FARM: 0-0 is level");
            BotTest.shared[Comms.EF_STATE] = 2;
            check(!Duck.capturesLevel(), "LEVEL_FARM: 1-0 is not level");
            BotTest.shared[Comms.OF_LOST] = 4;
            check(Duck.capturesLevel(), "LEVEL_FARM: 1-1 is level");
        } catch (GameActionException e) { check(false, "LEVEL_FARM: unexpected " + e); }
        java.util.Arrays.fill(BotTest.shared, 0);

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

        contactTests();

        System.out.println("AuditTest: " + (fails == 0 ? "OK" : "FAILED " + fails));
        if (fails > 0) System.exit(1);
    }

    /** C.CONTACT: our flag i's track in the shared array: CT[i] at l with en20 `en`, last seen `age` rounds ago; CT_AUX = aux. */
    static void ctTrack(int i, MapLocation l, int en, int age, int aux) {
        BotTest.shared[Comms.CT + i] = Comms.ctPack(Comms.enc(l), en); BotTest.shared[Comms.OF_SEEN + i] = G.round - age; BotTest.shared[Comms.CT_AUX] = aux;
    }

    static RobotInfo ally(int id, int x, int y) { return new RobotInfo(id, Team.A, 1000, new MapLocation(x, y), false, 0, 0, 0); }

    /** C.CONTACT (arm g4contact; off in the incumbent, so these call the code directly): packing, chainPoint, the sensor,
     *  the gates, the dive claim, Micro.dive and the carrierTarget extension, on BotTest's fake controller. */
    static void contactTests() {
        check(!C.CONTACT || C.FLAG_LOST, "CONTACT: the track's round is OF_SEEN, which trackLost writes only under C.FLAG_LOST");

        // 1. packing: CT, CT_AUX and CT_DIVE round trips, all within 16 bits; a dive stamp from another round reads 0
        boolean pk = true;
        for (int x = 0; x < 60; x++) for (int y = 0; y < 60; y++) for (int en = 0; en <= 15; en++) {
            int l = Comms.enc(new MapLocation(x, y)), v = Comms.ctPack(l, en);
            if (v <= 0 || v > GameConstants.MAX_SHARED_ARRAY_VALUE || Comms.ctLoc(v) != l || Comms.ctEn(v) != en) pk = false;
        }
        check(pk && Comms.ctEn(Comms.ctPack(Comms.enc(new MapLocation(59, 59)), 40)) == 15, "CONTACT: CT pack/unpack round trip, en20 saturates at 15, within 16 bits");
        boolean ax = true;
        for (int m0 = 0; m0 <= 3; m0++) for (int o0 = 0; o0 <= 7; o0++) for (int m1 = 0; m1 <= 3; m1++) for (int o1 = 0; o1 <= 7; o1++)
            for (int m2 = 0; m2 <= 3; m2++) for (int o2 = 0; o2 <= 7; o2 += 7) {
                int a = Comms.auxSet(Comms.auxSet(Comms.auxSet(0, 0, m0, o0), 1, m1, o1), 2, m2, o2);
                if (a < 0 || a > 0x7FFF || Comms.auxMiss(a, 0) != m0 || Comms.auxOu(a, 0) != o0 || Comms.auxMiss(a, 1) != m1 || Comms.auxOu(a, 1) != o1
                        || Comms.auxMiss(a, 2) != m2 || Comms.auxOu(a, 2) != o2) ax = false;
                int b = Comms.auxSet(a, 1, 0, 0);
                if (Comms.auxMiss(b, 1) != 0 || Comms.auxOu(b, 1) != 0 || Comms.auxMiss(b, 0) != m0 || Comms.auxOu(b, 2) != o2) ax = false;
            }
        int sat = Comms.auxSet(0, 2, 9, 99);
        check(ax && Comms.auxMiss(sat, 2) == 3 && Comms.auxOu(sat, 2) == 7, "CONTACT: CT_AUX fields per flag round-trip, independent, capped (3, 7), bit 15 clear");
        boolean dv = true;
        int full = 0;
        for (int i = 0; i < 3; i++) for (int k = 0; k < 20; k++) full = Comms.diveAdd(full, i, 517);
        if (full > GameConstants.MAX_SHARED_ARRAY_VALUE || Comms.diveCount(full, 0, 517) != 15 || Comms.diveCount(full, 2, 517) != 15) dv = false;
        int d1 = Comms.diveAdd(Comms.diveAdd(Comms.diveAdd(0, 1, 300), 1, 300), 0, 300);
        if (Comms.diveCount(d1, 1, 300) != 2 || Comms.diveCount(d1, 0, 300) != 1 || Comms.diveCount(d1, 2, 300) != 0) dv = false;
        if (Comms.diveCount(d1, 1, 301) != 0 || Comms.diveCount(Comms.diveAdd(d1, 1, 301), 1, 301) != 1 || Comms.diveCount(Comms.diveAdd(d1, 1, 301), 0, 301) != 0) dv = false;
        check(dv, "CONTACT: CT_DIVE counts per flag and round (cap 15, within 16 bits); another round's stamp reads 0 and restarts the counts");

        // 2. chainPoint: 9/16 tile a round toward D, each axis capped, stops on the zone tile next to D, null after arrival
        MapLocation L = new MapLocation(5, 5);
        check(Duck.chainPoint(L, new MapLocation(40, 5), 0).equals(L) && Duck.chainPoint(L, new MapLocation(40, 5), 16).equals(new MapLocation(14, 5))
              && Duck.chainPoint(L, new MapLocation(40, 8), 16).equals(new MapLocation(14, 8)), "CONTACT: chainPoint age 0 = L; age 16 = 9 tiles on, each axis capped");
        MapLocation D5 = new MapLocation(10, 5);
        check(Duck.chainPoint(L, D5, 7).equals(new MapLocation(8, 5)) && Duck.chainPoint(L, D5, 8).equals(new MapLocation(9, 5))
              && Duck.chainPoint(L, D5, 10).equals(new MapLocation(9, 5)) && Duck.chainPoint(L, D5, 11) == null,
              "CONTACT: chainPoint stops at Chebyshev 1 from D and is null once m > cheb (the arrival has passed)");

        // 3. the sensor on a fake controller: 40x30, our centres (5,7) (5,20) (12,25), theirs by rotation; writes only 34-36, 62, 63
        G.W = 40; G.H = 30; G.rc = BotTest.fakeRc(); G.us = Team.A; G.them = Team.B; G.testBc = 25000; G.idx = 10; Sym.cands = Sym.ROT;
        G.spawnCenters = new MapLocation[]{new MapLocation(5, 7), new MapLocation(5, 20), new MapLocation(12, 25)};
        java.util.Arrays.fill(BotTest.shared, 0); BotTest.walls.clear(); BotTest.moveOk = false; BotTest.buildOk = false;
        BotTest.writeOk = new boolean[64];
        for (int k : new int[]{Comms.CT, Comms.CT + 1, Comms.CT + 2, Comms.CT_AUX, Comms.CT_DIVE}) BotTest.writeOk[k] = true;
        BotTest.badWrite = false;
        for (int i = 0; i < 3; i++) { Duck.ctSeenAt[i] = -1; Duck.ctMissAt[i] = -1; }
        G.rngState = 0x2468ACE; int rng0 = G.rngState;
        int fid0 = 5 + 7 * 40;                                         // flag 0's id = location index of its spawn centre
        RobotInfo car = new RobotInfo(80, Team.B, 1000, new MapLocation(15, 12), true, 0, 0, 0);
        RobotInfo[] es = {car, enemy(81, 16, 13), enemy(82, 14, 11), enemy(83, 20, 14)};       // (20,14) is dist2 29 from the flag
        RobotInfo[] as = {ally(90, 16, 10), ally(91, 19, 15)};                                  // (19,15) is dist2 25 from the flag
        java.util.List<RobotInfo> all = new java.util.ArrayList<>(java.util.Arrays.asList(es)); all.addAll(java.util.Arrays.asList(as));
        BotTest.robots = all.toArray(new RobotInfo[0]);
        try {
            int aux1 = Comms.auxSet(Comms.auxSet(0, 0, 1, 0), 1, 2, 3);  // flag 0 had a miss; flag 1's bits must survive
            BotTest.shared[Comms.CT_AUX] = aux1;
            G.round = 300; G.me = new MapLocation(17, 12); Duck.enemies = es; Duck.allies = as;
            FlagInfo carried = new FlagInfo(new MapLocation(15, 12), Team.A, true, fid0);
            Duck.contactSight(carried);
            int a = BotTest.shared[Comms.CT_AUX];
            check(BotTest.shared[Comms.CT] == Comms.ctPack(Comms.enc(new MapLocation(15, 12)), 3) && Comms.auxOu(a, 0) == 2 && Comms.auxMiss(a, 0) == 0
                  && Comms.auxMiss(a, 1) == 2 && Comms.auxOu(a, 1) == 3 && Duck.ctSeenAt[0] == 300 && Duck.ctSeenCarried[0],
                  "CONTACT sensor: first sighting writes the tile and en20 3 (carrier included), ou20 2 (observer included), resets the misses ("
                  + Comms.ctEn(BotTest.shared[Comms.CT]) + "/" + Comms.auxOu(a, 0) + ")");
            BotTest.shared[Comms.OF_SEEN] = 300;                       // this robot's trackLost
            int w0 = BotTest.writeCount;
            G.me = new MapLocation(13, 12); Duck.enemies = new RobotInfo[]{car, es[2]}; Duck.allies = new RobotInfo[0];
            Duck.contactSight(carried);
            check(BotTest.writeCount == w0, "CONTACT sensor: a smaller view of the same tile in the same round writes nothing");
            RobotInfo[] es5 = {car, es[1], es[2], enemy(84, 16, 11), enemy(85, 14, 13)};
            all.add(es5[3]); all.add(es5[4]); BotTest.robots = all.toArray(new RobotInfo[0]);
            G.me = new MapLocation(15, 14); Duck.enemies = es5; Duck.allies = new RobotInfo[]{as[0]};
            Duck.contactSight(carried);
            check(Comms.ctEn(BotTest.shared[Comms.CT]) == 5 && Comms.auxOu(BotTest.shared[Comms.CT_AUX], 0) == 2,
                  "CONTACT sensor: a bigger view in the same round raises en20 to its max (5)");
            G.round = 301; G.me = new MapLocation(18, 12);
            RobotInfo car2 = new RobotInfo(80, Team.B, 1000, new MapLocation(16, 12), true, 0, 0, 0);
            BotTest.robots = new RobotInfo[]{car2, enemy(81, 17, 13)}; Duck.enemies = BotTest.robots; Duck.allies = new RobotInfo[0];
            Duck.contactSight(new FlagInfo(new MapLocation(16, 12), Team.A, true, fid0));
            check(BotTest.shared[Comms.CT] == Comms.ctPack(Comms.enc(new MapLocation(16, 12)), 5) && Comms.auxOu(BotTest.shared[Comms.CT_AUX], 0) == 1,
                  "CONTACT sensor: next round, live track: the new tile, en20 keeps its high-water mark (5), ou20 is this round's (1)");
            BotTest.shared[Comms.OF_SEEN] = 301;
            G.round = 301 + C.CT_HOLD + 1;
            Duck.contactSight(new FlagInfo(new MapLocation(16, 12), Team.A, true, fid0));
            check(Comms.ctEn(BotTest.shared[Comms.CT]) == 2, "CONTACT sensor: after CT_HOLD rounds unseen the count starts afresh (2)");
            G.round = 320; G.me = new MapLocation(6, 8); BotTest.robots = new RobotInfo[0]; Duck.enemies = new RobotInfo[0];
            BotTest.shared[Comms.CT_AUX] = Comms.auxSet(BotTest.shared[Comms.CT_AUX], 0, 1, 4);
            Duck.contactSight(new FlagInfo(new MapLocation(5, 7), Team.A, false, fid0));
            a = BotTest.shared[Comms.CT_AUX];
            check(BotTest.shared[Comms.CT] == 0 && Comms.auxMiss(a, 0) == 0 && Comms.auxOu(a, 0) == 0 && Comms.auxMiss(a, 1) == 2 && Comms.auxOu(a, 1) == 3,
                  "CONTACT sensor: a home sighting clears CT and that flag's aux bits only");
            java.util.Arrays.fill(BotTest.shared, 0);
            w0 = BotTest.writeCount;
            G.round = C.SETUP_ROUNDS + 3; G.me = new MapLocation(17, 12); BotTest.robots = es; Duck.enemies = es;
            Duck.contactSight(carried);
            check(BotTest.writeCount == w0 && BotTest.shared[Comms.CT] == 0, "CONTACT sensor: nothing before r204 (the A12 home re-stamp window)");

            // 4. trackGoal / chainGoal gates. Flag 0 last seen at (15,12): D = (27,4); age 4 -> P = (17,10)
            java.util.Arrays.fill(BotTest.shared, 0);
            G.round = 400; G.me = new MapLocation(17, 18); Duck.enemies = new RobotInfo[0]; Duck.allies = new RobotInfo[0];
            MapLocation l0 = new MapLocation(15, 12), P = new MapLocation(17, 10), D = new MapLocation(27, 4);
            check(D.equals(Duck.ctDest(l0)) && P.equals(Duck.chainPoint(l0, D, 4)), "CONTACT: test geometry (D " + Duck.ctDest(l0) + ", P " + Duck.chainPoint(l0, D, 4) + ")");
            ctTrack(0, l0, 3, 4, 0);
            MapLocation lead = Track.step(P.x, P.y, D.x, D.y, C.CT_LEAD);
            check(lead.equals(Duck.trackGoal(0, G.me, C.CT_DIVE_R2)) && Duck.ctEn == 3 && Duck.ctNeed == 3 + C.CT_EDGE,
                  "CONTACT gates: a u12 track 64 away leads by CT_LEAD steps toward its spawn (" + lead + "), need = en20 + CT_EDGE");
            G.me = new MapLocation(20, 12);
            check(P.equals(Duck.trackGoal(0, G.me, C.CT_DIVE_R2)), "CONTACT gates: within dist2 20 the goal is the predicted point itself");
            G.me = new MapLocation(17, 18);
            ctTrack(0, l0, C.CT_GROUP_MAX, 4, 0);
            check(Duck.trackGoal(0, G.me, C.CT_DIVE_R2) == null, "CONTACT gates: an observed group of CT_GROUP_MAX or more is left alone");
            ctTrack(0, l0, C.CT_GROUP_MAX - 1, 4, Comms.auxSet(0, 0, C.CT_MISS - 1, 0));
            check(Duck.trackGoal(0, G.me, C.CT_DIVE_R2) != null, "CONTACT gates: CT_GROUP_MAX - 1 and CT_MISS - 1 misses still pass");
            ctTrack(0, l0, 3, 4, Comms.auxSet(0, 0, C.CT_MISS, 0));
            check(Duck.trackGoal(0, G.me, C.CT_DIVE_R2) == null, "CONTACT gates: CT_MISS misses kill the track");
            G.me = new MapLocation(22, 9);                               // 16 from age 13's point (22,5), 10 from age 12's (21,6)
            ctTrack(0, l0, 3, C.CT_HOLD + 1, 0);
            check(Duck.trackGoal(0, G.me, C.CT_DIVE_R2) == null, "CONTACT gates: a track older than CT_HOLD is dead");
            ctTrack(0, l0, 3, C.CT_HOLD, 0);
            check(new MapLocation(21, 6).equals(Duck.trackGoal(0, G.me, C.CT_DIVE_R2)), "CONTACT gates: a track CT_HOLD old is live");
            G.me = new MapLocation(17, 18);
            ctTrack(0, l0, 3, 1, Comms.auxSet(0, 0, 0, 3 + C.CT_EDGE));
            check(Duck.trackGoal(0, G.me, C.CT_DIVE_R2) == null, "CONTACT gates: fresh contact with ou20 >= en20 + CT_EDGE needs no diver");
            ctTrack(0, l0, 3, 1, Comms.auxSet(0, 0, 0, 2 + C.CT_EDGE));
            check(Duck.trackGoal(0, G.me, C.CT_DIVE_R2) != null && Duck.ctNeed == 1, "CONTACT gates: one ou20 short leaves need 1");
            ctTrack(0, l0, 3, 2, Comms.auxSet(0, 0, 0, 7));
            check(Duck.trackGoal(0, G.me, C.CT_DIVE_R2) != null && Duck.ctNeed == 3 + C.CT_EDGE, "CONTACT gates: ou20 counts only while fresh (age <= 1)");
            ctTrack(0, l0, 3, 4, 0);
            check(Duck.trackGoal(0, new MapLocation(5, 25), C.CT_DIVE_R2) == null, "CONTACT gates: a predicted point beyond CT_DIVE_R2 is no dive");
            check(Duck.chainGoal() != null, "CONTACT chainGoal: a u12 track within dist2 100 is a dive goal");
            BotTest.shared[Comms.OF_LOST] = 1;
            check(Duck.chainGoal() == null, "CONTACT chainGoal: a lost flag's track is skipped");
            BotTest.shared[Comms.OF_LOST] = 0;
            G.idx = 1;
            check(Duck.chainGoal() == null, "CONTACT chainGoal: a defender dives only for its own flag");
            G.idx = 0;
            check(Duck.chainGoal() != null, "CONTACT chainGoal: ... and does for its own");
            G.idx = 10;
            G.round = C.SETUP_ROUNDS + 3; ctTrack(0, l0, 3, 0, 0);
            check(Duck.chainGoal() == null, "CONTACT chainGoal: nothing before r204");
            G.round = 400; ctTrack(0, l0, 3, 4, 0);
            // a negative sighting: P within CT_MISS_R2, flag not in view, age >= 1: one miss a robot a round, then the track dies
            G.me = new MapLocation(18, 11); w0 = BotTest.writeCount;
            check(Duck.trackGoal(0, G.me, C.CT_DIVE_R2) == null && Comms.auxMiss(BotTest.shared[Comms.CT_AUX], 0) == 1 && BotTest.writeCount == w0 + 1,
                  "CONTACT miss: P within CT_MISS_R2 and the flag not in view writes one miss");
            check(Duck.trackGoal(0, G.me, C.CT_DIVE_R2) == null && Comms.auxMiss(BotTest.shared[Comms.CT_AUX], 0) == 1 && BotTest.writeCount == w0 + 1,
                  "CONTACT miss: ... and none on a second call in the same round");
            G.round = 401;
            Duck.trackGoal(0, G.me, C.CT_DIVE_R2);
            check(Comms.auxMiss(BotTest.shared[Comms.CT_AUX], 0) == 2 && Duck.trackGoal(0, new MapLocation(17, 18), C.CT_DIVE_R2) == null,
                  "CONTACT miss: a second robot-round's miss kills the track");
            G.round = 400; ctTrack(0, l0, 3, 0, 0); w0 = BotTest.writeCount;
            check(Duck.trackGoal(0, G.me, C.CT_DIVE_R2) != null && BotTest.writeCount == w0, "CONTACT miss: none at age 0 (seen this round)");
            // the flag in my own view this turn: carried -> fight's carrier branch; dropped close -> fight; dropped farther -> its tile
            MapLocation fl = new MapLocation(16, 12);
            ctTrack(0, fl, 3, 0, Comms.auxSet(0, 0, 0, 1));
            Duck.ctSeenAt[0] = 400; Duck.ctSeenLoc[0] = fl; Duck.ctSeenCarried[0] = true; G.me = new MapLocation(17, 18);
            check(Duck.trackGoal(0, G.me, C.CT_DIVE_R2) == null, "CONTACT view: a carried flag in view is the fight's carrier branch, not a dive");
            Duck.ctSeenCarried[0] = false; G.me = new MapLocation(18, 13);
            check(Duck.trackGoal(0, G.me, C.CT_DIVE_R2) == null, "CONTACT view: a dropped flag within dist2 8 is fought over normally");
            G.me = new MapLocation(17, 18);
            check(fl.equals(Duck.trackGoal(0, G.me, C.CT_DIVE_R2)), "CONTACT view: a dropped flag farther away is the goal itself");
            Duck.ctSeenAt[0] = -1;

            // 5. claimDive: the cap per chain per round, reset next round, independent per flag
            BotTest.shared[Comms.CT_DIVE] = 0; G.round = 500;
            boolean c3 = Duck.claimDive(0, 3) && Duck.claimDive(0, 3) && Duck.claimDive(0, 3);
            check(c3 && !Duck.claimDive(0, 3), "CONTACT claimDive: need 3 lets three divers claim a chain in a round, not a fourth");
            check(Duck.claimDive(1, 3), "CONTACT claimDive: another flag's chain counts separately");
            G.round = 501;
            check(Duck.claimDive(0, 3) && Comms.diveCount(BotTest.shared[Comms.CT_DIVE], 0, 501) == 1 && Comms.diveCount(BotTest.shared[Comms.CT_DIVE], 1, 501) == 0,
                  "CONTACT claimDive: the next round starts afresh");
            // chainGoal: the nearer of two chains, its claim, its en20 in ctEn
            java.util.Arrays.fill(BotTest.shared, 0); G.round = 600; G.me = new MapLocation(17, 18);
            ctTrack(0, new MapLocation(14, 22), 6, 2, 0); ctTrack(1, l0, 3, 4, 0);   // flag 0: P (15,22) at 20; flag 1: led (19,8) at 104
            MapLocation cg = Duck.chainGoal();
            int cen = Duck.ctEn;
            check(new MapLocation(15, 22).equals(cg) && cen == 6 && Comms.diveCount(BotTest.shared[Comms.CT_DIVE], 0, 600) == 1
                  && Comms.diveCount(BotTest.shared[Comms.CT_DIVE], 1, 600) == 0, "CONTACT chainGoal: the nearer chain wins, is claimed and names its en20 (" + cg + ", " + cen + ")");
        } catch (GameActionException e) { check(false, "CONTACT: unexpected " + e); }
        check(!BotTest.badWrite, "CONTACT: the sensor, the gates and the claims write only slots 34-36, 62 and 63, within 16 bits");
        boolean of = true;
        for (int k = Comms.OF_LOC; k < Comms.OF_CARRY + 3; k++) if (BotTest.shared[k] != 0) of = false;
        check(of, "CONTACT: OF_LOC and OF_CARRY (13-19) never touched");
        check(G.rngState == rng0, "CONTACT: no G.rand draw in the sensor, the gates or the claims");

        // 6. Micro.dive (moveOk fake: movement ready, every non-wall step legal; no action)
        BotTest.moveOk = true; BotTest.buildOk = false; BotTest.walls.clear(); BotTest.robots = new RobotInfo[0];
        try {
            G.me = new MapLocation(10, 10); BotTest.lastMove = null;
            Micro.dive(new RobotInfo[]{enemy(1, 14, 10)}, new RobotInfo[0], new MapLocation(20, 10));
            check(BotTest.lastMove != null && BotTest.lastMove.dx == 1, "Micro.dive: a step gaining a tile through one extra threat beats staying (" + BotTest.lastMove + ")");
            G.me = new MapLocation(10, 10); BotTest.lastMove = null;
            Micro.dive(new RobotInfo[]{enemy(1, 13, 13), enemy(2, 14, 12)}, new RobotInfo[0], new MapLocation(18, 18));
            check(BotTest.lastMove == null, "Micro.dive: the diagonal step at Chebyshev 8 through two extra threats is not taken (" + BotTest.lastMove + ")");
            G.me = new MapLocation(10, 10); BotTest.lastMove = null;
            Micro.dive(new RobotInfo[]{enemy(1, 13, 13)}, new RobotInfo[0], new MapLocation(18, 18));
            check(BotTest.lastMove == Direction.NORTHEAST, "Micro.dive: ... through one extra threat it is (" + BotTest.lastMove + ")");
            G.me = new MapLocation(10, 10); BotTest.lastMove = null;
            Micro.dive(new RobotInfo[]{new RobotInfo(1, Team.B, 1000, new MapLocation(8, 10), true, 0, 0, 0)}, new RobotInfo[0], new MapLocation(20, 10));
            check(BotTest.lastMove == Direction.WEST, "Micro.dive: a visible carrier takes fight's carrier branch, whatever the goal (" + BotTest.lastMove + ")");
            G.me = new MapLocation(10, 10); BotTest.lastMove = null; Nav.target = null;
            BotTest.walls.add(new MapLocation(11, 9)); BotTest.walls.add(new MapLocation(11, 10)); BotTest.walls.add(new MapLocation(11, 11));
            int mv0 = Nav.moves;
            Micro.dive(new RobotInfo[0], new RobotInfo[0], new MapLocation(20, 10));
            check(BotTest.lastMove != null && BotTest.lastMove.dx != 1 && new MapLocation(20, 10).equals(Nav.target) && Nav.moves == mv0 + 1,
                  "Micro.dive: every progress tile walled: Nav bugs around the wall (" + BotTest.lastMove + ")");
        } catch (GameActionException e) { check(false, "Micro.dive: unexpected " + e); }
        BotTest.moveOk = false; BotTest.walls.clear();

        // 7. carrierTarget: a fresh OF_CARRY wins; a u12 track is the chase point with CONTACT on (no redirect, led beyond
        // dist2 20); a 12+ track is g_iter4's path (nothing); a jailed caller (G.me == null) counts no miss
        java.util.Arrays.fill(BotTest.shared, 0); BotTest.writeOk = null;
        try {
            G.round = 700; G.me = null; G.idx = 10;
            MapLocation lt = new MapLocation(12, 12), pt = new MapLocation(13, 11), from = new MapLocation(10, 12);
            ctTrack(0, lt, 3, 2, 0);
            check(pt.equals(Duck.chainPoint(lt, Duck.ctDest(lt), 2)), "CONTACT carrierTarget: test geometry (" + Duck.chainPoint(lt, Duck.ctDest(lt), 2) + ")");
            BotTest.shared[Comms.OF_CARRY] = 700; BotTest.shared[Comms.OF_LOC] = Comms.enc(new MapLocation(14, 12));
            check(new MapLocation(14, 12).equals(Duck.carrierTarget(from)), "CONTACT carrierTarget: a fresh carrier sighting wins over the track");
            BotTest.shared[Comms.OF_CARRY] = 0; BotTest.shared[Comms.OF_LOC] = 0;
            MapLocation got = Duck.carrierTarget(from);
            check(C.CONTACT ? pt.equals(got) : got == null, "CONTACT carrierTarget: a u12 track within dist2 20 is the chase point itself (" + got + ")");
            MapLocation far = new MapLocation(5, 12);
            got = Duck.carrierTarget(far);
            MapLocation led = Track.step(pt.x, pt.y, 27, 4, C.CT_LEAD);
            check(C.CONTACT ? led.equals(got) : got == null, "CONTACT carrierTarget: beyond dist2 20 the led point, no destination redirect (" + got + ")");
            ctTrack(0, lt, C.CT_GROUP_MAX, 2, 0);
            check(Duck.carrierTarget(far) == null, "CONTACT carrierTarget: a 12+ track takes g_iter4's path (no chase)");
            ctTrack(0, lt, 3, 2, 0); int w0 = BotTest.writeCount;
            check(pt.equals(Duck.trackGoal(0, new MapLocation(13, 12), C.CHASE_RADIUS2)) && BotTest.writeCount == w0,
                  "CONTACT carrierTarget: a jailed robot's spawn point next to P counts no miss");
        } catch (GameActionException e) { check(false, "CONTACT carrierTarget: unexpected " + e); }
        java.util.Arrays.fill(BotTest.shared, 0); G.testBc = -1; G.me = null; Duck.enemies = new RobotInfo[0]; Duck.allies = new RobotInfo[0];
    }
}
