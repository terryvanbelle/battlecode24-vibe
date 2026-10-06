# End-game level dump (arm g7bank, C.LATE_BANK), 2026-10-06

Workflow: two independent designs (A: minimal, reuse LEVEL_FARM; B: copy andli28's observed bank-and-dump), a judge (merged, B as base), one implementer, two reviewers (engine rules; measurement), one fixer. TACTICS T17.

## Design A: C.LATE_BANK (arm g7dump): a capped bank from r1500 that the existing LEVEL_FARM digs out from r1800

g4farm (LEVEL_FARM from r1500, 15 build XP, reserve 300) failed only because no bank existed: "the late bank sits at 160-260 crumbs", so it dug 0-11 times a game. This design adds one off-by-default switch, C.LATE_BANK, which builds that bank, and moves the existing farmDig to a final dump.

- **Banking.** From r1500, while the flag counts are level, only discretionary spending is held back: field combat stuns (beyond dist2 ALERT_RADIUS2 = 100 of a live flag home of ours) and float stuns. The field-stun reserve rises 6 crumbs a round from TRAP_RESERVE 200 to BANK_RESERVE 2,000 at r1800. That is andli28's own banked amount (median 1,440 at r1400, about 2,000 at r1800).
- **What stays untouched.** Carrier stuns, defender ring traps, combat stuns near our flag homes and fills keep today's reserves. With a bank of about 2,000 they always have money, where today a bank of ~240 often cannot pay for a 300-crumb combat stun.
- **Dump.** The arm sets LEVEL_FARM with FARM_ROUND 1800, FARM_XP 15 and FARM_RESERVE 300. Idle non-defender ducks dig checkerboard tiles on our half. This matches andli28's window (its digs go from 87 to 209 between r1800 and r2000).
- **Expected effect.** In the median case the dump spends about 3,350 crumbs on about 185 digs, a net gain of about +22 to +25 levels at r2000 (range +10 to +30). That flips about 10 to 15 of the 59 level-sum losses (10 deficits are under 20 and 15 are under 25), roughly 5 to 7.5 points of win rate against andli28.
- **Bounded cost.** The bank can never sit above about 2,100, because field stuns resume above reserve + 100. The worst case, when nobody is free to dig, is about 20 field stuns not built plus a bank that only counts in the crumb tiebreak.
- **Size of the change.** One new switch, three new constants and two new small helpers in Duck.java. The incumbent's bytecode is unchanged: with LATE_BANK=false javac drops the new branches.

## Design B: C.LATE_BANK (arm g7bank): andli28's end-game level dump, copied. Bank from r1400 by holding back only the traps that would sit and wait; from r1901 every robot not in a fight digs toward its next build level.

I measured andli28's dump on 35 of the 59 level-sum-loss replays. I pulled them from the VM and read them with a scratch copy of ReplayDump in the scratchpad (rd/, rp/, rp2/, sim.py, ana2.py); no games were run and nothing under tools/ was changed.

**What andli28 does.** It switches on hard at round 1901. Its first late dig was at r1901 in all 35 replays, and in the 24 replays where I looked at r1880-1900 it dug nothing there. In r1901-2000 it makes a median 120 digs (range 42-199), spread over 36-45 robots at 1-9 digs each, some of them mid-fight. It almost stops building traps (median 9 in those 100 rounds) and spends its bank (median 2,125 at r1900, 58 at r2000). Because the digs are spread out and not aimed at level thresholds, that buys it only a median +11 levels in the last 100 rounds; we gain +5 there.

**Our side.**
- Our bank is a median 260 at r1900.
- In r1401-1900 our trap spending splits into three groups:
  - **Can be held back:** 2,475 crumbs (median). These are calm ring re-arms on a flag whose ring already holds 8 or more of our traps (8 is the median ring size over 270 snapshots), plus field stuns with no enemy within dist2 8.
  - **Close-enemy field stuns:** 2,350. Stuns with an enemy within dist2 8 trigger 98% of the time and freeze 4-6 enemies each.
  - **Flag fights and thin rings:** about 2,000.
- We never dig after setup, and about 25 of our ~38 spawned robots are at build 0 with attack level 3. So a build level costs 5 digs × 20 = 100 crumbs, and a build level of 3 or less is never the skill the jail penalty takes.

**The design.**
- **Stage 1 (r1401-1900, flag counts level):** only the held-back group waits behind a cap. The bank fills at their measured rate to andli's ~2,000. Close-enemy stuns keep priority on a rising line plus a paced floor, and flag defence keeps full access to the bank.
- **Stage 2 (r1901+, andli's exact round):** after its normal move and attack/heal, any robot with no enemy in vision uses its spare action on a checkerboard dig. A robot starts a level only if the bank can pay for the whole level, and digs stop at build level 3.

**Projection.** About 2,600 crumbs, or about 130 digs, giving **+22 levels net (range +10 to +27)**. That flips about 10-15 of the 59 level-sum losses (+20 flips 10, +24 flips 14). The design also avoids both known level-farm failures: g4farm2's digs took actions a heal would have used, and g4farm3's took robots off the front. Here a dig never replaces a move, an attack or a heal.

## Judge: merged

I chose Design B as the base and merged in parts of A. I checked both against the code, the engine and the data.

Data checks:
- **Deficits:** in the 59 level-sum losses the deficits are 10 under 20, 15 under 25 and 28 under 40.
- **Banks:** ours is 235-256 crumbs from r1400 to r2000; andli28's is 1,910 at r1900.
- **andli28's digs:** they stay flat at 89 until r1900 and reach 195 at r2000. That confirms B's finding that the dump starts at r1901, not r1800.

(1) Levels gained. Both project about +22.
- B's split of what gets held back was measured on 35 replays.
- A's 185 digs is optimistic:
  - its dig call, farmDig at Duck.java:79, runs before tryHeal and Nav.moveTo and returns when it digs, so a digging duck neither moves nor heals that turn;
  - its alertedFlag() gate stops every dig on the team during any fresh alert.
- Merged projection: about +20 (range +8 to +26), flipping 8-14 of the 59 losses.

(2) Risk to captures and fights. B is safer.
- A holds back every field stun beyond dist2 100 of a home, including close-enemy stuns (98% trigger, 4-6 victims), and halts them through r1800-2000.
- A's dig placement repeats both known failures: g4farm2 lost heals, and g4farm3 took ducks off the front.
- B holds back only the builds that would sit and wait. Close stuns keep a paced floor, and the dig uses only an action left over after the move and attack/heal.
- I took A's radius (ALERT_RADIUS2 = 100, the existing responder radius) for alerted flag fights. It is more conservative than B's 64 and needs no new constant.
- B's jail check (digDurable) matches the engine's jailedPenalty exactly, so a dug level is never the one the penalty takes on the next death.

(3) Implementation risk.
- A does not compile: C.BANK_PACE already exists (=10, used by BUDGET_V1).
- B needs one shared-array slot. Slot 40 is free: the AUC constants are never used, SPAWN_SAFE uses 37-39 and CONTACT uses 34-36, 62 and 63.
- B's single hook after Duck.turn() covers every early return in turn().
- I added fixes B lacked:
  - **Fallback gate:** the stun-tile fallback only runs after BANK_R2. Otherwise the arm would play differently from g_iter7 before r1400 and break the twin comparison.
  - **Bytecode guard:** lateDig skips with fewer than DIG_BC (3,000) bytecodes left.
  - **Testable helpers:** lateLevel() and lateTrapAllowed() don't read the switch, because the unit tests run on src/bot where LATE_BANK is false.
  - **No-farm check:** a test that LATE_BANK and LEVEL_FARM are never both on.
  - **digCost test:** digCost is a plain lookup tested against the engine formula. A static array would trip deadcode's constant parser.

(4) Measurability. B's raw bank1900 column plus digsLate is cleaner than A's bank1800, which builds a flag-count filter into the column. Stage 1 never digs, so digsLate counts exactly the dump digs (g_iter7 reads 0).
- The 5(a) combines both designs' bars: bank on at least 75% of level games, a dig count, a +15 level-gap effect over twins, and capture guards of ±2 with absolute bars for overruns and exceptions.
- I added a minimum-n rule: if fewer than 5 level games, a second pre-registered seed.
- The delivery bars allow for only 63 of 200 games ending level.

Files involved:
- /home/terryvanbelle/projects/vibe/2024/src/bot/C.java
- /home/terryvanbelle/projects/vibe/2024/src/bot/Comms.java
- /home/terryvanbelle/projects/vibe/2024/src/bot/Duck.java
- /home/terryvanbelle/projects/vibe/2024/src/bot/RobotPlayer.java
- /home/terryvanbelle/projects/vibe/2024/test/bot/AuditTest.java
- /home/terryvanbelle/projects/vibe/2024/tools/arm-intent.txt
- /home/terryvanbelle/projects/vibe/2024/tools/replaydump/ReplayDump.java
- /home/terryvanbelle/projects/vibe/2024/tools/test_tools.py

## Specification

ARM g7bank: C.LATE_BANK. Design B is the base. From A it takes three things: ALERT_RADIUS2 (100) as the class-A radius, LEVEL_FARM's checkerboard and keep-off rules, and the capture guards / minimum-n idea for the 5(a). It drops A's BANK_PACE name (it collides with the existing C.BANK_PACE=10), A's farmDig hook, and A's conditional census column.

== 0. Engine facts this relies on (checked in reference/battlecode24/engine) ==
- Dig cost: round(20*(1+0.01*BUILD.getSkillEffect(lv))), so 20/18/17/16 at build 0-3. Dig cooldown: 20/19/18/17, so one dig every 2 turns.
- jailedPenalty() takes from attack if atk>=build && atk>=heal; otherwise from build if build>=atk && build>=heal; otherwise from heal.
- incrementSkill stops build XP at 15 when attack or heal is level 4.
- MapInfo.getTrapType() shows only friendly traps.
- assertCanDig refuses water, wall, spawn tile, robot, flag, our own trap, and a robot holding a flag.

== 1. src/bot/C.java: new block right after the LEVEL_FARM block (after `FARM_ROUND = 1500, FARM_XP = 5, FARM_RESERVE = 0;`) ==
    // C.LATE_BANK (TACTICS T17, andli28's end-game level dump; arm g7bank). 59 of g_iter7's 145 losses to andli28 are level-sum
    // losses (median deficit 41). In those games andli28 holds ~1,900 crumbs at r1900 and digs only from r1901 (89 -> 195 digs),
    // while we hold ~250 and never dig after setup. While the flag counts are level (Duck.capturesLevel; needs C.FLAG_LOST):
    // after BANK_R1, builds that would sit and wait (re-arms of a ring holding RING_KEEP+ of our traps, combat stuns with no
    // enemy within BANK_CLOSE_R2, float stuns) need BANK_CAP more crumbs. Close stuns build above the rising bankLine or once
    // per BANK_GAP rounds team-wide (slot Comms.LB_PACE). Alerted flag fights, carrier stuns, thin rings and fills are unchanged.
    // After BANK_R2, a robot with a spare action and no enemy within DIG_HOLD_R2 digs a checkerboard tile toward its next build
    // level: whole levels only, build <= 3, and only a level the jail penalty never takes. Never combine with LEVEL_FARM,
    // STUN_FRONT or BUILDERS (their branches skip the gate).
    public static final boolean LATE_BANK = false;
    public static final int BANK_R1 = 1400, BANK_R2 = 1900, BANK_CAP = 2000, BANK_PACE100 = 400;
    public static final int BANK_CLOSE_R2 = 8, RING_KEEP = 8, BANK_GAP = 20;
    public static final int DIG_XP_MAX = 15, DIG_KEEP = 300, DIG_ALLIN = 1990, DIG_KEEP_END = 100, DIG_HOLD_R2 = 20, DIG_BC = 3000;
None of these names exist yet (checked with grep). Do NOT name anything BANK_PACE: that constant already exists (BUDGET_V1).

== 2. src/bot/Comms.java ==
- Add `public static final int LB_PACE = 40;` with a doc comment: "C.LATE_BANK: round of the team's last paced close stun (0 = none)".
- In the schema header, replace the line "40..48 reserved for S1/S3 ..." with "40 LB_PACE (C.LATE_BANK): round of the last paced close stun; 41..48 reserved for S1/S3 (responder auction); not written yet".
- Slot 40 is free today: AUC/AUC_SLOTS are declared but never used, SPAWN_SAFE uses 37-39, CONTACT uses 34-36, 62 and 63.

== 3. src/bot/Duck.java: new helpers after capturesLevel() (~line 412) ==
    static int lateAt = -1; static boolean lateLevel;
    /** C.LATE_BANK: after BANK_R1 with the flag counts level (capturesLevel read once per turn). Switch-free, for the tests. */
    static boolean lateLevel() throws GameActionException {
        if (G.round <= C.BANK_R1) return false;
        if (lateAt != G.round) { lateAt = G.round; lateLevel = capturesLevel(); }
        return lateLevel;
    }
    static boolean lateOn() throws GameActionException { return C.LATE_BANK && lateLevel(); }
    /** The rising bank line: 0 up to BANK_R1, BANK_PACE100 a hundred rounds after it, BANK_CAP from BANK_R2. Pure. */
    static int bankLine(int r) { return r <= C.BANK_R1 ? 0 : r >= C.BANK_R2 ? C.BANK_CAP : Math.min(C.BANK_CAP, (r - C.BANK_R1) * C.BANK_PACE100 / 100); }
    static boolean lbPaced;   // this combat build uses the team's paced close-stun slot
    static boolean lateTrapOk() throws GameActionException { lbPaced = false; return !lateOn() || lateTrapAllowed(); }
    /** For a combat stun that the old rule already allows (crumbs >= 100 + TRAP_RESERVE):
     *  - anything builds above the cap;
     *  - class B (an enemy within BANK_CLOSE_R2 of G.me) builds above the line, or on the team pace;
     *  - class A (an alerted flag fight) always builds;
     *  - class C waits. */
    static boolean lateTrapAllowed() throws GameActionException {
        int cr = G.rc.getCrumbs(), base = 100 + C.TRAP_RESERVE;
        if (cr >= base + C.BANK_CAP) return true;
        boolean close = Micro.enemyWithin(G.me, C.BANK_CLOSE_R2);
        if (close && cr >= base + bankLine(G.round)) return true;
        if (guardFight()) return true;
        if (close && G.round - G.rc.readSharedArray(Comms.LB_PACE) >= C.BANK_GAP) { lbPaced = true; return true; }
        return false;
    }
    /** Class A: G.me within ALERT_RADIUS2 of a live home whose alert is fresh (alertFresh: within the last 10 rounds). */
    static boolean guardFight() throws GameActionException {
        int lost = Comms.lostMask();
        for (int i = 0; i < 3; i++) {
            if ((lost >> i & 1) != 0 || !alertFresh(i)) continue;
            MapLocation h = Comms.flagHome(i);
            if (h != null && G.me.distanceSquaredTo(h) <= C.ALERT_RADIUS2) return true;
        }
        return false;
    }
    static void lateBuilt() throws GameActionException { if (lbPaced) { G.rc.writeSharedArray(Comms.LB_PACE, G.round); lbPaced = false; } }
    /** Our traps within RING_RADIUS2 of a home that this robot can sense. Tiles out of view count as empty, which errs toward re-arming. */
    static int ringCount(MapLocation home) throws GameActionException {
        int n = 0;
        for (MapInfo m : G.rc.senseNearbyMapInfos(home, C.RING_RADIUS2)) if (m.getTrapType() != TrapType.NONE) n++;
        return n;
    }
    static int digNeed(int xp) { return 5 - xp % 5; }
    static int digCost(int lv) { return lv <= 0 ? 20 : lv == 1 ? 18 : lv == 2 ? 17 : 16; }   // engine: round(20 x (1 + build%))
    /** The jail penalty never takes build at level `next` given attack/heal levels (mirrors InternalRobot.jailedPenalty). */
    static boolean digDurable(int next, int atk, int heal) { return next <= atk || next < heal; }
    /** Dump tile:
     *  - checkerboard (x+y even);
     *  - farther than RING_RADIUS2 from every home and from every flag in view;
     *  - all 4 orthogonal neighbours on the map and passable. */
    static boolean digSiteOk(MapLocation t, MapLocation[] homes, FlagInfo[] fl) throws GameActionException {
        if (((t.x + t.y) & 1) != 0) return false;
        for (MapLocation h : homes) if (h != null && h.distanceSquaredTo(t) <= C.RING_RADIUS2) return false;
        for (FlagInfo f : fl) if (f.getLocation().distanceSquaredTo(t) <= C.RING_RADIUS2) return false;
        for (int k = 0; k < 8; k += 2) { MapLocation n = t.add(G.DIRS[k]); if (!G.rc.onTheMap(n) || !G.rc.sensePassability(n)) return false; }
        return true;
    }
    /** C.LATE_BANK stage 2 (after BANK_R2): called from RobotPlayer after the whole turn, so it only uses a spare action. */
    static void lateDig() throws GameActionException {
        RobotController rc = G.rc;
        if (G.round <= C.BANK_R2 || !rc.isSpawned() || rc.hasFlag() || !rc.isActionReady() || !lateOn()) return;
        int xp = rc.getExperience(SkillType.BUILD);
        if (xp >= C.DIG_XP_MAX || !digDurable(xp / 5 + 1, rc.getLevel(SkillType.ATTACK), rc.getLevel(SkillType.HEAL))) return;
        if (rc.getCrumbs() < digNeed(xp) * digCost(xp / 5) + (G.round < C.DIG_ALLIN ? C.DIG_KEEP : C.DIG_KEEP_END)) return;
        if (G.bcLeft() < C.DIG_BC || rc.senseNearbyRobots(C.DIG_HOLD_R2, G.them).length > 0) return;
        FlagInfo[] fl = rc.senseNearbyFlags(-1);
        for (FlagInfo f : fl) if (f.getTeam() == G.them) return;     // a pickup keeps the action
        MapLocation me = rc.getLocation();
        MapLocation[] homes = {Comms.flagHome(0), Comms.flagHome(1), Comms.flagHome(2)};
        for (Direction d : G.DIRS) {
            MapLocation t = me.add(d);
            if (((t.x + t.y) & 1) != 0 || !rc.canDig(t) || !digSiteOk(t, homes, fl)) continue;
            rc.dig(t); farmDigs++; G.note = "dump"; return;
        }
    }
- No ourHalf restriction: B's capacity figure counts free robots on both halves, and the checkerboard keeps diagonal paths the same length.
- farmDigs stays the counter (indicator lf). LEVEL_FARM stays off.

== 4. Duck.java: edits to existing code ==
(a) Line 100 (spendFloat gate). Becomes:
    if (rc.isActionReady() && rc.getCrumbs() > Math.max(C.FLOAT_CRUMBS, G.bankFloor()) + (C.LATE_BANK && lateOn() ? C.BANK_CAP : 0)) spendFloat();
(b) defend(), inside `if (rc.isActionReady() && !(C.DAM_FIRST ...)) {`, before the `for (Direction d : G.DIRS)` ring loop:
    int bankHold = 0;   // C.LATE_BANK class C: a calm re-arm of a ring holding RING_KEEP+ of our traps waits for the bank
    if (C.LATE_BANK && lateOn() && rc.getCrumbs() < TrapType.EXPLOSIVE.buildCost + C.DEF_TRAP_RESERVE + C.BANK_CAP && ringCount(home) >= C.RING_KEEP) bankHold = C.BANK_CAP;
   In the build test, change `tt.buildCost + Math.max(C.DEF_TRAP_RESERVE, ...)` to `tt.buildCost + bankHold + Math.max(C.DEF_TRAP_RESERVE, ...)`.
   - No overflow: bankHold is 0 whenever bankFloor() can be MAX_VALUE (setup).
   - defend() runs only when its own flag's alert is not fresh, so every ring build in it counts as calm.
(c) placeCombatTrap (line 1248). Right after `if (!rc.isActionReady() || enemies.length < C.STUN_ENEMIES_MIN || rc.getCrumbs() < 100 + C.TRAP_RESERVE) return;` add:
    if (C.LATE_BANK && !lateTrapOk()) return;
   In the g_iter1 branch, after the WATER_WHEN_WEAK line, add:
    if (C.LATE_BANK && G.round > C.BANK_R2 && !rc.canBuild(tt, t)) {   // our dump water: stuns are land-only
        Direction dd = G.me.directionTo(c); MapLocation l = G.me.add(dd.rotateLeft());
        t = rc.canBuild(tt, l) ? l : G.me.add(dd.rotateRight());
    }
   - The round gate is required. Without it the arm differs from g_iter7 before r1400 and the twin comparison breaks.
   - Append `if (C.LATE_BANK) lateBuilt();` inside both successful-build blocks: the g_iter1 `if (rc.canBuild(tt, t)) {...}` and the V2 `if (best != null && ...) {...}`.
(d) Unchanged:
- carrierStun (line 47)
- Nav fills
- setup
- STUN_FRONT/BUILDERS branches (off; they return before the gate)
- LEVEL_FARM line 79

== 5. src/bot/RobotPlayer.java ==
After `Duck.turn();` add:
    if (C.LATE_BANK) Duck.lateDig();
- This single hook covers every early return in turn() (crumb detour, defend, fight, pickstep).
- javac drops it when the switch is off.

== 6. Arm ==
- src/g7bank = copy of src/bot with `package g7bank;` and `C.LATE_BANK = true`; nothing else differs (check with diff).
- tools/arm-intent.txt gets one line: `g7bank C.LATE_BANK=true`.

== 7. Census column (tools/replaydump/ReplayDump.java) ==
New column bank1900 = our team's crumbs at the end of round 1900, blank if the game ended earlier. It is not filtered on flag counts; analyses filter with rounds/captured/enemyCaptured.
- Field: `static int[] kBank1900 = {-1, -1, -1};` next to kLevel1200 (~line 168).
- Snapshot next to line 775-776: `if (rn == 1900) for (int t = 1; t <= 2; t++) kBank1900[t] = crumbsNow[t];`
- Header (line 1677): append `,bank1900` after spawnDeath10.
- Row: append `+ "," + (kBank1900[t] >= 0 ? String.valueOf(kBank1900[t]) : "")` after the spawnDeath10 share.
- Doc block (~line 104-107): `bank1900  our crumbs at the end of r1900 (blank if shorter); C.LATE_BANK's bank signature`.
- tools/test_tools.py line ~352: append 'bank1900' to NEW_CAP (not SIGNED).
- Baseline: g_iter7's median in its 59 level-sum losses is 256 (per-100 metrics); andli28's is 1,910.

== 8. Unit tests (test/bot/AuditTest.java), then tools/unit-tests.sh (~10 min) ==
Incumbent and switch checks:
- Line 94 incumbent check: add `&& !C.LATE_BANK`.
- New: check(!(C.LATE_BANK && C.LEVEL_FARM), "LATE_BANK never with LEVEL_FARM").
Pure-function checks:
- bankLine: (1000)=0, (1400)=0, (1401)=4, (1650)=1000, (1900)=2000, (1950)=2000.
- digNeed: (0)=5, (5)=5, (7)=3, (14)=1.
- digCost(lv) for lv 0..3 equals {20,18,17,16} AND (int)Math.round(GameConstants.DIG_COST*(1+0.01*SkillType.BUILD.getSkillEffect(lv))).
- digDurable, exhaustive over next 1..3, atk 0..6, heal 0..6: it equals the negation of "build is penalised" in a literal mirror of jailedPenalty (attack if a>=b&&a>=h; else build if b>=a&&b>=h). Spot values: (1,3,3) true, (3,2,3) false, (2,1,4) true, (1,0,0) false.
lateLevel, with fakeRc and shared zeroed; set Duck.lateAt=-1 before each call:
- r1400 false; r1401 true.
- EF_STATE=2: false.
- plus OF_LOST=1: true.
- check(Duck.lateOn() == (C.LATE_BANK && Duck.lateLevel())).
lateTrapAllowed setup:
- BotTest.buildOk=true (crumbs 1000), G.me=(10,10).
- Homes far away: OF_HOME+i = enc((25,25+i)) for i=0..2.
- Close enemy: Duck.enemies={enemy(1,11,11)} (dist2 2). Far enemy: {enemy(1,13,13)} (dist2 18).
lateTrapAllowed cases:
- r1500, close: true and !lbPaced (line 400).
- r1800, close, LB_PACE=0: true and lbPaced.
- r1800, close, LB_PACE=1790: false.
- r1800, far: false.
- r1800, far, OF_ALERT+0=1795, OF_HOME+0=enc((14,10)) (dist2 16): true.
- Same with OF_LOST=1: false.
- Then with BotTest.writeOk set for slot 40 only: lbPaced=true; lateBuilt() writes LB_PACE=G.round and clears lbPaced.
digSiteOk (G.W=G.H=30, G.me=(10,10), homes {(20,20),null,null}, flags none):
- (11,11) true.
- (10,11) false (parity).
- Wall at (11,12): false.
- Home (12,12): false.
- A flag at (13,11): false.
- G.me=(1,1), t=(0,0): false (edge).
Afterwards reset: buildOk, walls, shared, enemies, writeOk, lateAt.
ringCount has no test (fakeRc has no senseNearbyMapInfos).
deadcode: every new constant and method is referenced, and LB_PACE is used.

== 9. Expected effect (merged; projection rests on B's 35-replay split) ==
Bank:
- Class C held back: median 2,475 crumbs over r1401-1900, about 495 per 100 rounds against the line's 400.
- Bank at r1900: median about 1,900-2,200; bank-poor games about 1,000.
Dump:
- About 2,000 bank + 1,400 income - about 300 class A - about 500 paced B = about 2,600 crumbs.
- That buys about 130 digs, or about 26 build levels gross.
Costs, then net:
- Minus about 3-5 levels of held-back trap XP (mostly defenders' ring re-arms) and about 1-2 partial levels.
- Net about +20 levels at r2000 (range +8 to +26).
Flips:
- Verified deficits: 10 under 20, 15 under 25, 28 under 40.
- Expect 8-14 of the 59 level-sum losses to flip, about 4-7 points of win rate against andli28, before any capture cost.

== 10. Pre-registered 5(a) (rule 5 first) ==
Diagnostic game:
- One game: g7bank vs andli28.v9_USQuals_angle on EndAround, fixed seed, side A (tools/diag-batch.sh g7bank-diag g7bank:andli28.v9_USQuals_angle:EndAround:<seed>:A).
- It shows the mechanism firing if the flags are level at r1900, bank1900 >= 1,000, our first post-setup dig falls in r1901-1910 (replay dump --from 1895 --to 1910, or indicator lf > 0 with note "dump"), and overruns and exceptions are both 0.
- If the game is not level at r1900, play side B, then the next 5(a) map, until a level game shows it.
- A level game without the bank or the dig is a bug; fix it before the 5(a).
5(a) cells:
- 12 cells: {EndAround, FloodGates, TreeSearch, Battlecode24, Intercontinental, GravitationalWaves} x {A, B}.
- One seed S, written down before the run: `tools/diag-batch.sh g7bank-5a g7bank:andli28.v9_USQuals_angle:<map>:S:<side> ...`.
- g_iter7 twins on the same 12 cells (`tools/diag-batch.sh g7bank-5a-twin g_iter7:...`). Never entered in games.csv.
Definitions:
- L = arm games that reach r2000 with captured == enemyCaptured.
- P = cells where both arm and twin reach r2000 with flags level.
FIRES iff all of these hold:
(a) Bank: bank1900 >= 1,000 in >= 75% of L (twins about 256).
(b) Dump: digsLate >= 60 in >= 75% of L (twins 0; projection about 130).
(c) Effect: mean over P of (levelGapEnd_arm - levelGapEnd_twin) >= +15 (projection +20).
(d) Guards:
   - Sum of arm enemyCaptured <= twins' sum + 2.
   - Sum of arm captured >= twins' sum - 2.
   - Overruns 0 and exceptions 0 in every arm game (absolute bars).
Minimum n: if |L| < 5 or |P| < 5, add the same 12 cells on a second pre-registered seed and judge (a)-(d) over all 24, with guard margins +4/-4.
Logged without a bar: wins, levelGain1500, trapsBuilt, stunVictims, carrierStunBuilds, kills, deaths, fills r1900-2000, crumbs at r2000, maxBcK.
On FIRE, delivery:
    DGPOOL=andli28.v9_USQuals_angle DGTAG=-andlibank BASE=g_iter7 tools/delivery-gate.sh g7bank 'rel:bank1900>=2 mean:digsLate>=20 rel:levelGain1500>=1.15 nw:enemyCaptured<=1.1 mean:overruns<=0'
- Bar basis: 63 of 200 g_iter7 games end level at r2000; levelGain1500 base mean 18.6.
- Then the paired andli28 filler (`FILLPOOL=andli28.v9_USQuals_angle tools/filler-pair.sh g_iter7 g7bank 40`) is the victory read.
Dose ladder if it fires with the guards intact:
- BANK_R1 1200 with BANK_CAP 3000 and BANK_PACE100 430.
- DIG_HOLD_R2 10 only if digsLate falls short while banks are full.
Docs: TACTICS T17 Adoption cell becomes "in candidate: g7bank (C.LATE_BANK)".

## Review findings

- **bug** `/home/terryvanbelle/projects/vibe/2024/tools/arm-intent.txt`: Spec section 6 is missing from the working tree. src/g7bank does not exist, and tools/arm-intent.txt has no `g7bank C.LATE_BANK=true` line. The diagnostic and 5(a) commands all name g7bank (`tools/diag-batch.sh g7bank-diag g7bank:andli28...`), so run-dev.sh has no package to compile. Earlier arms (for example commit 4a24139, g7spawn) committed the src/<arm> copy and the arm-intent line together with the switch.
- **risk** `/home/terryvanbelle/projects/vibe/2024/src/bot/Duck.java`: The side-tile fallback fires on any canBuild failure after r1900, in every game, whether or not the flags are level and whether or not the tile is dump water. Other common failures also trigger it: the primary tile within dist2 2 of an enemy, or already holding our trap. In those cases the arm builds left or right stuns that g_iter7 skips. So in non-level games the arm and its twin differ in r1901-2000, which affects the all-cell capture guards in 5(a)(d) and the delivery nw:enemyCaptured. In level games it spends dump crumbs on extra stuns. The comment says the fallback is for 'our dump water', but the code does not check for water.
- **risk** `/home/terryvanbelle/projects/vibe/2024/test/bot/AuditTest.java`: The class-C test cannot fail on the plausible bugs it exists to catch. It runs at r1800 with LB_PACE still 1790, so both the line (1000 < 300+1600) and the pace (10 < 20) refuse whatever `close` says. Three mutations still pass: dropping `close &&` from the line branch, dropping it from the pace branch, and a wrong close radius (BANK_CLOSE_R2 >= 18, for example the vision radius). There is a similar gap in guardFight: no case puts a home within ALERT_RADIUS2 with a stale alert, so dropping `!alertFresh(i)` also passes.
- **nit** `/home/terryvanbelle/projects/vibe/2024/tools/test_tools.py`: The tool test only checks that bank1900 is blank or non-negative. A snapshot of the wrong team (`crumbsNow[3-t]`) or the wrong round would still pass. The FIX fixture runs the full 2000 rounds and the test already has its `--metrics 50` rows (`rows`), whose `crumbs` column comes from the same crumbsNow.
- **nit** `/home/terryvanbelle/projects/vibe/2024/src/bot/Duck.java`: The C.java comment promises 'whole levels only', but lateDig checks only the crumbs for the remaining digs, not the rounds left. One dig takes about 2 turns of cooldown at build 0-2, so a robot that still needs k digs must start by about r2000 - 2(k-1). After DIG_ALLIN (r1990, keep 100), robots that open a new level at r1993 or later pay for digs that cannot complete it.
- **nit** `/home/terryvanbelle/projects/vibe/2024/tools/delivery-check.py`: The rel: checks pair only cells where both arm and base have a value. In g_iter7's 200 andli28 games, 144 (72%) reached r1900, so bank1900 is non-blank in those, and 170 (85%) have levelGain1500. A 24-cell block therefore expects about 12-17 shared cells for rel:bank1900, below MIN_PAIRS 18. The gate will almost surely go INCONCLUSIVE and rerun at 48 cells with a fresh base under DGTAG -n48, wasting the first 24+24 games.
- **nit** `spec section 10 (5(a) bar (a)/(b))`: L is 'reaches r2000 with captured == enemyCaptured', but the bank fills only while the counts are level during r1401-1900, and the dump runs only while they are level after r1900. A game that was 0-1 until late and then equalised is in L but could not bank. Bars (a) and (b) would count it against the mechanism. The census has no level-at-round column, though the per-100 metrics rows do.
- **nit** `/home/terryvanbelle/projects/vibe/2024/test/bot/AuditTest.java`: The C.java comment says never to combine LATE_BANK with LEVEL_FARM, STUN_FRONT or BUILDERS (their branches return before the gate), but only LEVEL_FARM is checked. A later stack with STUN_FRONT or BUILDERS on would silently drop the class B/C holds for combat stuns.
- **risk** `src/bot/Duck.java`: The 'whole levels only' rule is checked per robot, so it does not hold for the team. At r1901 every eligible robot with xp%5==0 checks `crumbs >= 5*cost + DIG_KEEP` against the same bank, so all of them pass and start a level together. The bank then gets spread over N partial levels. In-progress robots have lower thresholds (320-380), so they keep digging on income, but any level still unfinished at r2000 is lost XP. Nothing checks that the level can still finish by r2000 either. At r1990 DIG_KEEP_END frees 200 crumbs, and a robot at xp%5==0 can start a 5-dig level at r1993+ (digs at 1993/95/97/99/2001) that cannot finish. I simulated it: a dig every 2 turns, income 14/round, bank 2,000 at r1901, robots from xp 0. N=20 eligible gives 32 whole levels plus 10 stranded XP; N=30 gives 30 levels plus 13; N=40 gives 24 levels plus 42 stranded XP. A commit-before-start rule gets 33 levels with 0 stranded in each case. With bank 1,000 and income 10: N=20 gives 16.3 vs 19, N=40 gives 13.1 vs 19. Spec §9 budgets 1-2 partial levels. This can cost 1-9 levels against the +15 bar in 5(a)(c).
- **bug** `src/bot/Duck.java`: The combat-stun reroute runs on any `canBuild` failure after r1900, in every game. It is not limited to dump water ('our dump water' in the comment) and it ignores whether the flag counts are level (lateOn). In the g_iter1 placement the active branch (TRAP_PLACEMENT_V2=false), a fighter that holds its position aims at the same t each turn. g_iter7 builds once, because t then holds our stun and canBuild fails. The arm instead builds a second stun on t.rotateLeft and a third on rotateRight: up to 3x the combat stuns. Refusals because an enemy is within dist2 2 of t are also rerouted to a side tile where g_iter7 built nothing. In non-level games after r1900 (lateOn false, so no class gate) the arm therefore spends more on stuns than g_iter7. That breaks the twin identity the 5(a) capture guards rely on, and in level games it drains dump crumbs through class A builds.
- **risk** `tools/arm-intent.txt`: Spec §6 is not implemented. There is no src/g7bank directory and tools/arm-intent.txt has no `g7bank C.LATE_BANK=true` line. The pre-registered diagnostic (`tools/diag-batch.sh g7bank-diag g7bank:andli28...`) and the 5(a) cells cannot run. The arm-intent unit check that would catch a missed sed is also not exercised.
- **nit** `test/bot/AuditTest.java`: The C.java comment says LATE_BANK must never be combined with LEVEL_FARM, STUN_FRONT or BUILDERS, and that it needs FLAG_LOST. The test guards only LEVEL_FARM. With STUN_FRONT or BUILDERS on, placeCombatTrap returns before the lateTrapOk gate, so class C stuns spend the bank unchecked. Without FLAG_LOST, lostMask stays 0 and capturesLevel reads 0-1 as level.
- **nit** `src/bot/Comms.java`: `AUC = 37, AUC_SLOTS = 12` still claims slots 37..48. That now overlaps LB_PACE=40 (and already overlapped SPAWN_SAFE 37..39), while the schema header says only 41..48 are reserved for S1/S3. A later auction built from these constants would overwrite the pace slot. This also breaks CLAUDE.md rule 8, which says nothing stale stays.

## Fixes applied

I applied all six findings; none was rejected. Findings 2 and 4 describe the same reroute bug, and findings 3 and 5 the same missing arm copy. Nothing is committed. `C.LATE_BANK` is still false in src/bot and true only in src/g7bank.

1. **Team-wide level commit (src/bot/Duck.java, src/bot/Comms.java, src/bot/C.java):** confirmed. The "whole levels" check ran per robot against one bank, so many robots could start levels together and leave them unfinished.
   - Added `Comms.LB_OWED = 41` and changed the schema line to "42..48 reserved for S1/S3".
   - `lateDig` now works like this:
     - Each robot keeps a share of `LB_OWED` (`Duck.myOwed`) equal to the crumbs still needed to finish its current level.
     - A robot that owes nothing may start only when `crumbs - LB_OWED >= rest of the level + keep` (`digAffordable`). This applies to any build XP, not only `xp % 5 == 0`, because trap builds also leave robots part-way through a level.
     - A robot already digging keeps the old check against the whole bank, as the finding asked.
     - Each dump dig lowers the share to what remains of the level, via `owe`.
   - **Different from the suggested fix:** instead of clearing `LB_OWED` at `DIG_ALLIN`, each robot sets its own share every round. Every robot runs code every round, even when jailed. A robot drops its share to 0 when:
     - it is jailed;
     - the flag counts stop being level;
     - its next level is no longer safe from the jail penalty or it has reached `DIG_XP_MAX`;
     - it can no longer finish by r2000;
     - a trap build completes its level.

     Trap-build XP also lowers the share. This frees stale crumbs in cases the r1990 clear would miss.
   - **Finish-by-r2000 check:** `digDoneRound(round, cooldown, digCd(lv), need) <= GAME_MAX_NUMBER_OF_ROUNDS`, with `digCd` = 20/19/18/17 for build levels 0-3. It simulates the engine's cooldown exactly, so a build-1 robot can still start at r1993 (it finishes at r2000). The suggested `round + 2*(need-1)` would refuse that level.
   - `lateDig` now uses `lateLevel()` and relies on the `if (C.LATE_BANK)` guard at its only call site in RobotPlayer. That lets the tests drive it with the switch off.

2. **Combat-stun reroute (findings 2 and 4):** confirmed. It fired on any build refusal after r1900, whether or not the flag counts were level. It is now `if (C.LATE_BANK && G.round > C.BANK_R2 && lateOn()) t = dumpSide(tt, t);`. `dumpSide` moves the stun only when the target tile can be sensed and is water. Any other refusal keeps the target tile, so the build fails as it does in g_iter7.

3. **Missing arm (findings 3 and 5):** confirmed. I created src/g7bank after all the fixes above. `diff -r src/bot src/g7bank` shows only the 9 package lines and `LATE_BANK = true`. I appended `g7bank C.LATE_BANK=true` to tools/arm-intent.txt. The arm compiles and its dead-code check shows 0 unreferenced.

4. **AuditTest gaps:** confirmed. Changes in test/bot/AuditTest.java:
   - The class-C case now runs at r1500 with `LB_PACE = 0` and the far enemy, asserting no build and no paced claim. After it, r1800 and `LB_PACE = 1790` are restored.
   - Added the 20-round-old alert case (`OF_ALERT = 1780`, assert no build). `OF_ALERT` is reset to 1795 before the lost-flag case so that case still tests something.

**New tests:**
- `digCd` matches the engine's cooldown formula.
- `digDoneRound` cases.
- `dumpSide` cases: dry tile, enemy beside the tile, water goes left, right when the left is blocked, explosive on water, off the map.
- `lateDig` with the finding's case: two robots at XP 0, bank 450, keep 300, and only one starts. Also: continuing a level, trap XP lowering the share, a trap completing the level, release when jailed, release after a capture, and r1993 refused while r1992 starts.

**Test harness:** test/bot/BotTest.java gained opt-in fields for water, crumbs, XP, levels and digging. Their defaults keep the old behaviour. The fake `canBuild` now also refuses off-map tiles, as the engine does.

**Mutation check:** I broke each of these in a scratch copy and the new tests failed every time:
- removing `close` from the line branch, or from the pace branch;
- `BANK_CLOSE_R2 = 20`;
- removing `alertFresh`;
- removing the team-wide commit;
- removing the finish check;
- rerouting on any refusal;
- removing the per-round share update;
- removing the release when jailed.

Rejected: None. All six findings were real. Findings 2 and 4 are the same bug, and findings 3 and 5 are the same missing step; each pair got one fix. Two parts of finding 1 were done differently from the suggestion, with the same goal:
- **No clear of LB_OWED at DIG_ALLIN.** Each robot drops its own share when it is jailed, the flag counts stop being level, its level is no longer eligible, it cannot finish by r2000, or a trap build completes the level. This also covers stale shares from captures and trap XP, which a single r1990 clear would miss.
- **Exact finish check.** It simulates the engine cooldown instead of using `round + 2*(need-1)`. Level-0 robots get the same cutoff (r1992 is the last start), but build-1 and build-2 robots are not refused levels they can still finish by r2000.
