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
        check(C.REG_FIX == false && C.ALERT_FIX == false, "src/bot keeps g_iter1 behaviour: fixes are off by default");

        System.out.println("AuditTest: " + (fails == 0 ? "OK" : "FAILED " + fails));
        if (fails > 0) System.exit(1);
    }
}
