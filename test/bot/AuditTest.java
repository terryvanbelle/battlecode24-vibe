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
        check(C.REG_FIX && C.ALERT_FIX && C.REACH_FIX && C.REACH_FAST && C.NAV_FIX && Sym.OBSERVE && !C.TRACK && C.RELOCATE_FLAGS && C.RELOC_V2 && C.CARRIER_STUN && !C.DEST_CAMP && !C.BUDGET_V1 && !C.ESCORT_TIGHT && C.FLAG_LOST && !C.DEF_TETHER && C.PICKUP_AFTER_MOVE && !C.RELOC_STALL_MOVES && !C.CARRY_PREDICT && !C.STUN_AHEAD && !C.FILL_STEP && !C.RELOC_SPREAD && !C.ALERT_NEAREST && !C.INIT_FAST && !C.STUN_FRONT && !C.STUN_WARY && C.CRUMB_STEP && C.POST_SETUP_CRUMBS && !C.BUILDERS && !C.RELAY && !C.RELAY_THREAT && !C.LEVEL_FARM && !C.DAM_FIRST && !C.CONTACT && C.RELOC_CLIMB && C.CLIMB_R2 == 400 && C.HEAL_HOLD && C.HOLD_R2 == 10 && !C.TERR_MICRO && !C.LATE_BANK,
              "src/bot plays as the incumbent g_iter7 (g_iter6 + heal hold; the track sensor and the contact dive off)");

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

        // LATE_BANK (arm g7bank, TACTICS T17): the bank line, the dig arithmetic against the engine's, the jail-penalty mirror,
        // the level read, the stun classes and the dump tile
        check(!(C.LATE_BANK && C.LEVEL_FARM), "LATE_BANK never with LEVEL_FARM");
        check(Duck.bankLine(1000) == 0 && Duck.bankLine(1400) == 0 && Duck.bankLine(1401) == 4 && Duck.bankLine(1650) == 1000
              && Duck.bankLine(1900) == 2000 && Duck.bankLine(1950) == 2000, "LATE_BANK: the bank line is 0 to r1400, +400 a hundred rounds, the cap from r1900");
        check(Duck.digNeed(0) == 5 && Duck.digNeed(5) == 5 && Duck.digNeed(7) == 3 && Duck.digNeed(14) == 1, "LATE_BANK: digs to the next whole build level");
        int[] digCost = {20, 18, 17, 16}; boolean costOk = true;
        for (int lv = 0; lv <= 3; lv++)
            costOk &= Duck.digCost(lv) == digCost[lv] && Duck.digCost(lv) == (int) Math.round(GameConstants.DIG_COST * (1 + 0.01 * SkillType.BUILD.getSkillEffect(lv)))
                      && Duck.digCd(lv) == 20 - lv && Duck.digCd(lv) == (int) Math.round(GameConstants.DIG_COOLDOWN * (1 + 0.01 * SkillType.BUILD.getCooldown(lv)));
        check(costOk, "LATE_BANK: dig cost 20/18/17/16 and cooldown 20/19/18/17 at build 0-3, as the engine's");
        check(Duck.digDoneRound(1901, 0, 20, 5) == 1909 && Duck.digDoneRound(1992, 0, 20, 5) == 2000 && Duck.digDoneRound(1993, 0, 20, 5) == 2001
              && Duck.digDoneRound(1993, 0, 19, 5) == 2000 && Duck.digDoneRound(1900, 0, 18, 5) == 1907
              && Duck.digDoneRound(1990, 15, 20, 1) == 1991 && Duck.digDoneRound(1990, 5, 18, 1) == 1990,
              "LATE_BANK: the round of a level's last dig, one dig on each ready turn");
        boolean durOk = true;
        for (int b = 1; b <= 3; b++) for (int at = 0; at <= 6; at++) for (int h = 0; h <= 6; h++) {
            boolean penalised = !(at >= b && at >= h) && (b >= at && b >= h);   // InternalRobot.jailedPenalty: attack, else build, else heal
            if (Duck.digDurable(b, at, h) == penalised) durOk = false;
        }
        check(durOk && Duck.digDurable(1, 3, 3) && !Duck.digDurable(3, 2, 3) && Duck.digDurable(2, 1, 4) && !Duck.digDurable(1, 0, 0),
              "LATE_BANK: a dug level is durable exactly when the jail penalty never takes build");
        G.rc = BotTest.fakeRc(); java.util.Arrays.fill(BotTest.shared, 0);
        try {
            G.round = 1400; Duck.lateAt = -1;
            check(!Duck.lateLevel(), "LATE_BANK: nothing up to r1400");
            G.round = 1401; Duck.lateAt = -1;
            check(Duck.lateLevel(), "LATE_BANK: 0-0 at r1401 is on");
            BotTest.shared[Comms.EF_STATE] = 2; Duck.lateAt = -1;
            check(!Duck.lateLevel(), "LATE_BANK: 1-0 is off");
            BotTest.shared[Comms.OF_LOST] = 1; Duck.lateAt = -1;
            check(Duck.lateLevel(), "LATE_BANK: 1-1 is on");
            BotTest.shared[Comms.EF_STATE + 1] = 2;
            check(Duck.lateLevel(), "LATE_BANK: the flag counts are read once a turn");
            Duck.lateAt = -1;
            check(Duck.lateOn() == (C.LATE_BANK && Duck.lateLevel()), "LATE_BANK: lateOn follows the switch");
        } catch (GameActionException e) { check(false, "LATE_BANK lateLevel: unexpected " + e); }
        // stun classes, 1000 crumbs (base 300): me (10,10), homes far away; a close enemy at dist2 2, a far one at dist2 18
        G.W = 40; G.H = 30; G.me = new MapLocation(10, 10); BotTest.buildOk = true; BotTest.walls.clear();
        java.util.Arrays.fill(BotTest.shared, 0);
        for (int i = 0; i < 3; i++) BotTest.shared[Comms.OF_HOME + i] = Comms.enc(new MapLocation(25, 25 + i));
        RobotInfo[] closeE = {enemy(1, 11, 11)}, farE = {enemy(1, 13, 13)};
        try {
            G.round = 1500; Duck.enemies = closeE; Duck.lbPaced = false;
            check(Duck.lateTrapAllowed() && !Duck.lbPaced, "LATE_BANK: r1500, a close stun above the line (400) builds off the pace");
            G.round = 1800;
            check(Duck.lateTrapAllowed() && Duck.lbPaced, "LATE_BANK: r1800, a close stun below the line (1600) takes the team pace");
            Duck.lbPaced = false; BotTest.shared[Comms.LB_PACE] = 1790;
            check(!Duck.lateTrapAllowed() && !Duck.lbPaced, "LATE_BANK: a paced stun 10 rounds ago: the next close stun waits");
            // class C at r1500 with the pace free, where the line (400) and the pace would both pass a close stun: a dropped
            // `close` test in either branch, or a close radius out to the far enemy's dist2 18, builds here
            G.round = 1500; BotTest.shared[Comms.LB_PACE] = 0; Duck.enemies = farE; Duck.lbPaced = false;
            check(!Duck.lateTrapAllowed() && !Duck.lbPaced, "LATE_BANK: class C (no enemy within BANK_CLOSE_R2, no alert) waits where a close stun builds");
            G.round = 1800; BotTest.shared[Comms.LB_PACE] = 1790;
            BotTest.shared[Comms.OF_ALERT] = 1795; BotTest.shared[Comms.OF_HOME] = Comms.enc(new MapLocation(14, 10));
            check(Duck.lateTrapAllowed(), "LATE_BANK: class A (a fresh alert on a home at dist2 16) builds");
            BotTest.shared[Comms.OF_ALERT] = 1780;
            check(!Duck.lateTrapAllowed(), "LATE_BANK: a 20-round-old alert on that home is not class A");
            BotTest.shared[Comms.OF_ALERT] = 1795; BotTest.shared[Comms.OF_LOST] = 1;
            check(!Duck.lateTrapAllowed(), "LATE_BANK: a lost flag's alert is not class A");
            BotTest.writeOk = new boolean[64]; BotTest.writeOk[Comms.LB_PACE] = true; BotTest.badWrite = false;
            Duck.lbPaced = true; Duck.lateBuilt();
            check(BotTest.shared[Comms.LB_PACE] == 1800 && !Duck.lbPaced && !BotTest.badWrite, "LATE_BANK: a paced build stamps LB_PACE and clears the claim");
            int wc = BotTest.writeCount; Duck.lateBuilt();
            check(BotTest.writeCount == wc, "LATE_BANK: an unpaced build writes nothing");
        } catch (GameActionException e) { check(false, "LATE_BANK lateTrapAllowed: unexpected " + e); }
        // dump tile: me (10,10), one home at (20,20)
        G.W = 30; G.H = 30; G.me = new MapLocation(10, 10);
        MapLocation[] dHomes = {new MapLocation(20, 20), null, null}; FlagInfo[] noFl = new FlagInfo[0];
        try {
            check(Duck.digSiteOk(new MapLocation(11, 11), dHomes, noFl), "LATE_BANK: (11,11) is a dump tile");
            check(!Duck.digSiteOk(new MapLocation(10, 11), dHomes, noFl), "LATE_BANK: odd x+y is off the checkerboard");
            BotTest.walls.add(new MapLocation(11, 12));
            check(!Duck.digSiteOk(new MapLocation(11, 11), dHomes, noFl), "LATE_BANK: a wall beside the tile would close a lane");
            BotTest.walls.clear();
            check(!Duck.digSiteOk(new MapLocation(11, 11), new MapLocation[]{new MapLocation(12, 12), null, null}, noFl), "LATE_BANK: not inside a home's ring");
            check(!Duck.digSiteOk(new MapLocation(11, 11), dHomes, new FlagInfo[]{new FlagInfo(new MapLocation(13, 11), Team.A, false, 3)}),
                  "LATE_BANK: not within RING_RADIUS2 of a flag in view");
            G.me = new MapLocation(1, 1);
            check(!Duck.digSiteOk(new MapLocation(0, 0), dHomes, noFl), "LATE_BANK: a neighbour off the map");
        } catch (GameActionException e) { check(false, "LATE_BANK digSiteOk: unexpected " + e); }
        // a combat stun aimed at water goes beside it; any other refusal keeps the tile (me (10,10), aimed east at (11,10))
        G.W = 30; G.H = 30; G.me = new MapLocation(10, 10); Duck.enemies = new RobotInfo[0];
        MapLocation east = new MapLocation(11, 10);
        try {
            check(Duck.dumpSide(TrapType.STUN, east).equals(east), "LATE_BANK: a stun on dry land stays");
            Duck.enemies = new RobotInfo[]{enemy(1, 12, 10)};
            check(Duck.dumpSide(TrapType.STUN, east).equals(east), "LATE_BANK: an enemy beside the tile is no reason to move the stun");
            Duck.enemies = new RobotInfo[0]; BotTest.water.add(east);
            check(Duck.dumpSide(TrapType.STUN, east).equals(new MapLocation(11, 11)), "LATE_BANK: a stun aimed at water goes left of it");
            BotTest.walls.add(new MapLocation(11, 11));
            check(Duck.dumpSide(TrapType.STUN, east).equals(new MapLocation(11, 9)), "LATE_BANK: right of it when the left is blocked");
            check(Duck.dumpSide(TrapType.EXPLOSIVE, east).equals(east), "LATE_BANK: an explosive builds on water");
            G.me = new MapLocation(0, 5);
            check(Duck.dumpSide(TrapType.STUN, new MapLocation(-1, 5)).equals(new MapLocation(-1, 5)), "LATE_BANK: a tile off the map stays");
        } catch (GameActionException e) { check(false, "LATE_BANK dumpSide: unexpected " + e); }
        BotTest.water.clear(); BotTest.walls.clear();
        // lateDig: levels are committed team-wide. Bank 450, keep 300, two robots at build XP 0 (a level: 5 x 20 + 300)
        int keepBc = G.testBc; Team keepThem = G.them;
        G.me = new MapLocation(10, 10); G.them = Team.B; G.testBc = 25000; java.util.Arrays.fill(BotTest.shared, 0);
        for (int i = 0; i < 3; i++) BotTest.shared[Comms.OF_HOME + i] = Comms.enc(new MapLocation(25, 25 + i));
        BotTest.buildOk = true; BotTest.digOk = true; BotTest.attackLevel = 3; BotTest.writeOk = new boolean[64]; BotTest.writeOk[Comms.LB_OWED] = true; BotTest.badWrite = false;
        try {
            G.round = 1950; Duck.lateAt = -1; BotTest.crumbs = 450; BotTest.buildXp = 0; Duck.myOwed = 0; BotTest.lastDig = null;
            Duck.lateDig();
            check(BotTest.lastDig != null && BotTest.crumbs == 430 && BotTest.shared[Comms.LB_OWED] == 80 && Duck.myOwed == 80,
                  "LATE_BANK: robot 1 starts a level and owes its other 4 digs");
            Duck.myOwed = 0; BotTest.buildXp = 0; BotTest.lastDig = null;   // robot 2
            Duck.lateDig();
            check(BotTest.lastDig == null && BotTest.crumbs == 430 && BotTest.shared[Comms.LB_OWED] == 80 && Duck.myOwed == 0,
                  "LATE_BANK: robot 2 may not start (430 - 80 owed < 100 + 300)");
            Duck.myOwed = 80; BotTest.buildXp = 1; G.round = 1952;          // robot 1 again: its level needs 80 + 300 of the whole bank
            Duck.lateDig();
            check(BotTest.lastDig != null && BotTest.crumbs == 410 && BotTest.shared[Comms.LB_OWED] == 60 && Duck.myOwed == 60,
                  "LATE_BANK: robot 1 digs on and owes 3 digs");
            BotTest.buildXp = 4; BotTest.buildOk = false;                    // two trap builds, no action left this turn
            Duck.lateDig();
            check(BotTest.shared[Comms.LB_OWED] == 20 && Duck.myOwed == 20, "LATE_BANK: trap-build XP lowers the share to the rest of the level");
            BotTest.buildXp = 5;
            Duck.lateDig();
            check(BotTest.shared[Comms.LB_OWED] == 0 && Duck.myOwed == 0, "LATE_BANK: a trap build that completes the level frees the share");
            Duck.myOwed = 60; BotTest.shared[Comms.LB_OWED] = 60; BotTest.buildXp = 7; G.me = null;
            Duck.lateDig();
            check(BotTest.shared[Comms.LB_OWED] == 0 && Duck.myOwed == 0, "LATE_BANK: a jailed robot frees its share");
            G.me = new MapLocation(10, 10); Duck.myOwed = 60; BotTest.shared[Comms.LB_OWED] = 60; BotTest.shared[Comms.EF_STATE] = 2; Duck.lateAt = -1;
            Duck.lateDig();
            check(BotTest.shared[Comms.LB_OWED] == 0 && Duck.myOwed == 0, "LATE_BANK: a capture that ends the level game frees the share");
            BotTest.shared[Comms.EF_STATE] = 0; BotTest.buildOk = true; BotTest.crumbs = 1000; BotTest.buildXp = 0; BotTest.lastDig = null;
            G.round = 1993; Duck.lateAt = -1;
            Duck.lateDig();
            check(BotTest.lastDig == null && Duck.myOwed == 0, "LATE_BANK: r1993: a level of 5 digs (to r2001) is not started");
            G.round = 1992; Duck.lateAt = -1;
            Duck.lateDig();
            check(BotTest.lastDig != null && Duck.myOwed == 80 && !BotTest.badWrite, "LATE_BANK: r1992: its 5 digs end at r2000");
        } catch (GameActionException e) { check(false, "LATE_BANK lateDig: unexpected " + e); }
        G.testBc = keepBc; G.them = keepThem; G.me = new MapLocation(10, 10); Duck.myOwed = 0;
        BotTest.crumbs = -1; BotTest.buildXp = 0; BotTest.attackLevel = 0; BotTest.digOk = false; BotTest.lastDig = null; BotTest.water.clear();
        BotTest.buildOk = false; BotTest.walls.clear(); java.util.Arrays.fill(BotTest.shared, 0); Duck.enemies = new RobotInfo[0];
        BotTest.writeOk = null; BotTest.badWrite = false; Duck.lateAt = -1; Duck.lbPaced = false;

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

        // RELOC_CLIMB (audit BOT3(a)): the step goes to the visible tile farthest from the nearest live enemy centre, never into
        // another flag's dist2 64 or beyond CLIMB_R2 of the spawn centre; null at a local maximum; ties to the nearer tile
        G.W = 60; G.H = 40; Duck.relocNe = 1; Duck.relocEc[0] = new MapLocation(40, 20);
        MapLocation cm = new MapLocation(10, 10), csc = new MapLocation(10, 10);
        MapLocation[] cOthers = {new MapLocation(7, 4), new MapLocation(30, 30)};
        MapLocation cBest = Duck.climbStep(cm, new MapLocation[]{new MapLocation(12, 10), new MapLocation(8, 10), new MapLocation(7, 9),
                new MapLocation(6, 12), new MapLocation(0, 30), null}, csc, cOthers);
        check(new MapLocation(6, 12).equals(cBest), "RELOC_CLIMB: the farthest admissible tile (not one beside another flag, not beyond CLIMB_R2): " + cBest);
        check(Duck.climbStep(cm, new MapLocation[]{new MapLocation(12, 10), new MapLocation(11, 11), new MapLocation(10, 10)}, csc, cOthers) == null,
              "RELOC_CLIMB: no tile beats the one we stand on: a local maximum (null)");
        Duck.relocEc[0] = new MapLocation(10, 30); MapLocation[] cFar = {new MapLocation(50, 35), new MapLocation(30, 30)};
        check(new MapLocation(13, 7).equals(Duck.climbStep(cm, new MapLocation[]{new MapLocation(11, 9), new MapLocation(13, 7), new MapLocation(9, 9)}, csc, cFar)),
              "RELOC_CLIMB: the farther tile wins over nearer ones");
        check(new MapLocation(9, 9).equals(Duck.climbStep(cm, new MapLocation[]{new MapLocation(11, 9), new MapLocation(9, 9)}, csc, cFar)),
              "RELOC_CLIMB: an exact tie in score and distance goes to the lower x");
        check(Duck.climbStep(cm, new MapLocation[]{new MapLocation(11, 9), new MapLocation(9, 9)}, csc, new MapLocation[]{new MapLocation(10, 5), null}) == null,
              "RELOC_CLIMB: every better tile within dist2 64 of another flag: stay (null)");
        Duck.relocNe = 0;
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
