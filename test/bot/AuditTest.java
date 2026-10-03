package bot;

import battlecode.common.*;

/** Regression tests for the 2026-10-02 correctness audit (research/AUDIT-2026-10-02.md). */
public class AuditTest {
    static int fails = 0;
    static void check(boolean ok, String what) { if (!ok) { fails++; System.out.println("FAIL " + what); } }
    static RobotInfo enemy(int id, int x, int y) { return new RobotInfo(id, Team.B, 1000, new MapLocation(x, y), false, 0, 0, 0); }

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
        check(C.REG_FIX && C.ALERT_FIX && C.REACH_FIX && C.NAV_FIX && Sym.OBSERVE && !C.TRACK,
              "src/bot plays as the incumbent g_iter2 (the audit fixes and observed symmetry on, the track sensor off)");

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

        System.out.println("AuditTest: " + (fails == 0 ? "OK" : "FAILED " + fails));
        if (fails > 0) System.exit(1);
    }
}
