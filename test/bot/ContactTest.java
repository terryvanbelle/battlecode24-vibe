package bot;

import battlecode.common.*;

/** C.CONTACT (arm g4contact) on BotTest's fake controller: the track, the gates, the dive and the hooks in Duck.sense() and
 *  Duck.turn(). tools/unit-tests.sh runs it twice: on src/bot as it is (the switch off: the hooks are compiled out and must
 *  write nothing, the rest still holds) and on a copy of src/bot with the switch on, which pins the hooks the arm plays. */
public class ContactTest {
    static int fails = 0;
    static void check(boolean ok, String what) { if (!ok) { fails++; System.out.println("FAIL " + what); } }
    static RobotInfo enemy(int id, int x, int y) { return new RobotInfo(id, Team.B, 1000, new MapLocation(x, y), false, 0, 0, 0); }

    public static void main(String[] a) {
        contactTests();
        hookTests();
        System.out.println("ContactTest (C.CONTACT " + (C.CONTACT ? "on" : "off") + "): " + (fails == 0 ? "OK" : "FAILED " + fails));
        if (fails > 0) System.exit(1);
    }

    /** C.CONTACT: our flag i's track in the shared array: CT[i] at l with en20 `en`, last seen `age` rounds ago; CT_AUX = aux. */
    static void ctTrack(int i, MapLocation l, int en, int age, int aux) {
        BotTest.shared[Comms.CT + i] = Comms.ctPack(Comms.enc(l), en); BotTest.shared[Comms.OF_SEEN + i] = G.round - age; BotTest.shared[Comms.CT_AUX] = aux;
    }

    static RobotInfo ally(int id, int x, int y) { return new RobotInfo(id, Team.A, 1000, new MapLocation(x, y), false, 0, 0, 0); }

    /** Packing, chainPoint, the sensor, the gates, the dive claim, Micro.dive and the carrierTarget extension, calling the
     *  code directly (it is there with the switch off too). */
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
            // a chain with its cap full is skipped before its geometry (review 2026-10-05: a refused claim paid all of it): the
            // farther chain with room is the goal; with both full nothing is computed (ctDest's cache untouched) or written
            int full0 = 0;
            for (int k = 0; k < 6 + C.CT_EDGE; k++) full0 = Comms.diveAdd(full0, 0, 600);
            BotTest.shared[Comms.CT_DIVE] = full0;
            cg = Duck.chainGoal();
            check(new MapLocation(19, 8).equals(cg) && Duck.ctEn == 3 && Comms.diveCount(BotTest.shared[Comms.CT_DIVE], 1, 600) == 1
                  && Comms.diveCount(BotTest.shared[Comms.CT_DIVE], 0, 600) == 6 + C.CT_EDGE, "CONTACT chainGoal: the nearer chain full, the farther one with room is claimed (" + cg + ")");
            for (int k = 1; k < 3 + C.CT_EDGE; k++) BotTest.shared[Comms.CT_DIVE] = Comms.diveAdd(BotTest.shared[Comms.CT_DIVE], 1, 600);
            Duck.ctEcAt = -1; w0 = BotTest.writeCount;
            check(Duck.chainGoal() == null && Duck.ctEcAt == -1 && BotTest.writeCount == w0, "CONTACT chainGoal: every chain full: no geometry, no write");
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
            // review 2026-10-05: robots, not walls, on every progress tile (canMove false there): the score decides, never Nav,
            // which ignores threats (it stepped NORTH next to three enemies). Staying (0 threats, 3 allies adjacent) wins.
            BotTest.walls.clear(); G.me = new MapLocation(10, 10); BotTest.lastMove = null; Nav.target = null;
            RobotInfo[] wall3 = {ally(91, 11, 9), ally(92, 11, 10), ally(93, 11, 11)}, three = {enemy(1, 9, 14), enemy(2, 10, 14), enemy(3, 11, 14)};
            BotTest.robots = new RobotInfo[]{wall3[0], wall3[1], wall3[2], three[0], three[1], three[2]};
            mv0 = Nav.moves;
            Micro.dive(three, wall3, new MapLocation(16, 10));
            check(BotTest.lastMove == null && Nav.moves == mv0 && Nav.target == null,
                  "Micro.dive: robots on every progress tile: no Nav, no step toward the three enemies (" + BotTest.lastMove + ")");
            BotTest.robots = new RobotInfo[0];
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
            BotTest.shared[Comms.OF_LOST] = 1;
            check(Duck.carrierTarget(from) == null, "CONTACT carrierTarget: a lost flag's track is not chased");
            BotTest.shared[Comms.OF_LOST] = 0; G.idx = 1;
            check(Duck.carrierTarget(from) == null, "CONTACT carrierTarget: a defender does not chase another flag's track");
            G.idx = 0; got = Duck.carrierTarget(from);
            check(C.CONTACT ? pt.equals(got) : got == null, "CONTACT carrierTarget: ... and chases its own (" + got + ")");
            G.idx = 10;
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

    /** The fake world of the sensor tests: 40x30, our centres (5,7) (5,20) (12,25), theirs by rotation; every slot writable. */
    static void world() {
        G.W = 40; G.H = 30; G.rc = BotTest.fakeRc(); G.us = Team.A; G.them = Team.B; G.testBc = 25000; G.idx = 10; Sym.cands = Sym.ROT;
        G.spawnCenters = new MapLocation[]{new MapLocation(5, 7), new MapLocation(5, 20), new MapLocation(12, 25)};
        java.util.Arrays.fill(BotTest.shared, 0); BotTest.walls.clear(); BotTest.moveOk = false; BotTest.buildOk = false; BotTest.health = 1000;
        BotTest.writeOk = new boolean[64]; java.util.Arrays.fill(BotTest.writeOk, true);
        BotTest.robots = new RobotInfo[0]; BotTest.flagsInView = new FlagInfo[0];
        for (int i = 0; i < 3; i++) { Duck.ctSeenAt[i] = -1; Duck.ctMissAt[i] = -1; }
    }

    /** The real hooks (review 2026-10-05: the tests called contactSight directly and set OF_SEEN by hand, so a hook moved
     *  after trackLost, a dropped HP gate or a frozen diver passed): Duck.sense() over two rounds, the fight-branch gate
     *  contactDive, and one whole Duck.turn(). */
    static void hookTests() {
        world();
        int fid0 = 5 + 7 * 40;
        try {
            // 8. Duck.sense(): the sensor reads OF_SEEN before this robot's trackLost stamps it
            G.round = 300; G.me = new MapLocation(17, 12);
            RobotInfo car = new RobotInfo(80, Team.B, 1000, new MapLocation(15, 12), true, 0, 0, 0);
            BotTest.robots = new RobotInfo[]{car, enemy(81, 16, 13), enemy(82, 14, 11), enemy(83, 20, 14), ally(90, 16, 10), ally(91, 19, 15)};
            BotTest.flagsInView = new FlagInfo[]{new FlagInfo(new MapLocation(15, 12), Team.A, true, fid0)};
            Duck.sense();
            int ct = BotTest.shared[Comms.CT], aux = BotTest.shared[Comms.CT_AUX];
            check(BotTest.shared[Comms.OF_SEEN] == 300, "CONTACT sense(): trackLost stamps OF_SEEN with the sighting's round");
            check(C.CONTACT ? ct == Comms.ctPack(Comms.enc(new MapLocation(15, 12)), 3) && Comms.auxOu(aux, 0) == 2 : ct == 0 && aux == 0,
                  "CONTACT sense(): first sighting: CT = the carrier's tile, en20 3, ou20 2 (switch off: nothing) (" + Comms.ctEn(ct) + "/" + Comms.auxOu(aux, 0) + ")");
            G.round = 301; G.me = new MapLocation(18, 12);
            BotTest.robots = new RobotInfo[]{new RobotInfo(80, Team.B, 1000, new MapLocation(16, 12), true, 0, 0, 0), enemy(81, 17, 13)};
            BotTest.flagsInView = new FlagInfo[]{new FlagInfo(new MapLocation(16, 12), Team.A, true, fid0)};
            Duck.sense();
            ct = BotTest.shared[Comms.CT]; aux = BotTest.shared[Comms.CT_AUX];
            check(!C.CONTACT || (ct == Comms.ctPack(Comms.enc(new MapLocation(16, 12)), 3) && Comms.auxOu(aux, 0) == 1),
                  "CONTACT sense(): next round: en20 keeps its high-water mark (3), ou20 is this round's alone (1), not last round's 2 ("
                  + Comms.ctEn(ct) + "/" + Comms.auxOu(aux, 0) + ")");
            G.me = new MapLocation(16, 14);
            BotTest.robots = new RobotInfo[]{BotTest.robots[0], BotTest.robots[1], ally(92, 15, 15), ally(93, 17, 15)};
            Duck.sense();
            check(!C.CONTACT || Comms.auxOu(BotTest.shared[Comms.CT_AUX], 0) == 3, "CONTACT sense(): a second observer in the same round raises ou20 to its max (3)");
            G.round = 301 + C.CT_HOLD + 1; G.me = new MapLocation(18, 12);
            BotTest.robots = new RobotInfo[]{new RobotInfo(80, Team.B, 1000, new MapLocation(17, 12), true, 0, 0, 0), enemy(81, 18, 13)};
            BotTest.flagsInView = new FlagInfo[]{new FlagInfo(new MapLocation(17, 12), Team.A, true, fid0)};
            Duck.sense();
            check(!C.CONTACT || Comms.ctEn(BotTest.shared[Comms.CT]) == 2, "CONTACT sense(): seen again after CT_HOLD rounds, en20 is a fresh count (2), not the old mark ("
                  + Comms.ctEn(BotTest.shared[Comms.CT]) + ")");
            G.round = 330; G.me = new MapLocation(6, 8); BotTest.robots = new RobotInfo[0];
            BotTest.flagsInView = new FlagInfo[]{new FlagInfo(new MapLocation(5, 7), Team.A, false, fid0)};
            Duck.sense();
            check(BotTest.shared[Comms.CT] == 0 && Comms.auxOu(BotTest.shared[Comms.CT_AUX], 0) == 0 && BotTest.shared[Comms.OF_SEEN] == 330,
                  "CONTACT sense(): seen at home: no track, aux bits clear, OF_SEEN stamped");
            check(BotTest.shared[Comms.CT_DIVE] == 0 && (C.CONTACT || BotTest.shared[Comms.CT_AUX] == 0),
                  "CONTACT sense(): no dive claim in sense(); with the switch off no contact slot is ever written");

            // 9. contactDive, turn()'s gate: healthy (RETREAT_HP), able to move (a frozen duck must not take a slot), CT_BC left
            world(); BotTest.moveOk = true;
            G.round = 400; MapLocation me0 = new MapLocation(17, 18), l0 = new MapLocation(15, 12);
            Duck.enemies = new RobotInfo[]{enemy(1, 18, 20)}; Duck.allies = new RobotInfo[0]; BotTest.robots = Duck.enemies;
            ctTrack(0, l0, 3, 4, 0);
            int w0 = BotTest.writeCount;
            G.me = me0; BotTest.health = C.RETREAT_HP - 1;
            check(!Duck.contactDive() && BotTest.writeCount == w0, "CONTACT contactDive: a hurt duck keeps g_iter4's micro (no claim)");
            BotTest.health = 1000; BotTest.moveOk = false;
            check(!Duck.contactDive() && BotTest.writeCount == w0, "CONTACT contactDive: a duck that cannot move takes no diver slot");
            BotTest.moveOk = true; G.testBc = C.CT_BC - 1;
            check(!Duck.contactDive() && BotTest.writeCount == w0, "CONTACT contactDive: under CT_BC bytecodes left, no dive");
            G.testBc = C.CT_BC; BotTest.health = C.RETREAT_HP; BotTest.lastMove = null; int ic = Duck.intercepts;
            boolean dove = Duck.contactDive();
            check(dove && "dive3".equals(G.note) && Duck.intercepts == ic + 1 && Comms.diveCount(BotTest.shared[Comms.CT_DIVE], 0, 400) == 1 && BotTest.lastMove != null,
                  "CONTACT contactDive: at RETREAT_HP, able to move and CT_BC left: claims, dives, notes dive<en20> (" + G.note + ", " + BotTest.lastMove + ")");

            // 10. one whole Duck.turn() in a fight beside a live u12 track: the hook sits in the fight branch
            world(); BotTest.moveOk = true;
            G.round = 400; G.me = me0; Duck.carriedFlagId = -1;
            BotTest.robots = new RobotInfo[]{enemy(1, 18, 19)};
            ctTrack(0, l0, 3, 4, 0);
            Duck.turn();
            check(C.CONTACT ? "dive3".equals(G.note) && Comms.diveCount(BotTest.shared[Comms.CT_DIVE], 0, 400) == 1
                            : G.note.startsWith("fight") && BotTest.shared[Comms.CT_DIVE] == 0,
                  "CONTACT turn(): a fight next to a u12 track dives with the switch on, fights as g_iter4 with it off (" + G.note + ")");
        } catch (GameActionException e) { check(false, "CONTACT hooks: unexpected " + e); }
        java.util.Arrays.fill(BotTest.shared, 0); G.testBc = -1; G.me = null; BotTest.moveOk = false; BotTest.health = 1000; BotTest.writeOk = null;
        BotTest.robots = new RobotInfo[0]; BotTest.flagsInView = new FlagInfo[0]; Duck.enemies = new RobotInfo[0]; Duck.allies = new RobotInfo[0];
    }
}
