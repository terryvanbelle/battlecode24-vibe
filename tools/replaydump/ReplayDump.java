package replaydump;

import battlecode.schema.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Battlecode 2024 replay (.bc24) -> text. The microscope for single games.
 *
 *   tools/replay-dump.sh game.bc24                    summary (default)
 *     --every N           per-team aggregates every N rounds (table)
 *     --metrics [N]       per-team CSV every N rounds (default 1); columns in METRIC_COLS
 *     --from R --to R     event window (actions, spawns, deaths, traps, flags)
 *     --robot ID          one robot's whole life (location, hp, cooldowns, actions, indicator)
 *     --map-at R          ASCII board at the end of round R
 *     --logs REGEX        indicator strings matching REGEX [--team A|B]
 *     --bytecode          per-team bytecode maxima and turns at the limit
 *     --near90            as --bytecode, plus every turn at 90%+ of the limit (round, robot, count)
 *     --trapgeo           per post-setup trap: TG,team,type,buildRound,nearestEnemyD2,enemies13,enemies8,ownWithin2,trigRound,latency,victims13,
 *                         builderBuildLevel (nearest own robot's build level)
 *     --navstats          movement statistics per team
 *     --flags             every flag event with round, flag team, actor and location
 *     --levels            per team, the robots' last known levels at the end: means, masteries, buildSum (the sum of the
 *                         team's build levels: C.FINAL_COMPLETE's 5(a) targeting bar) and an a/b/h histogram
 *     --defense           one line per post-setup flag trip: defenders near the flag at pickup, chasers near the carrier, outcome,
 *                         tKnow (as --track, from the defending side; blank = never, or that side not tracked under --team)
 *     --comm R[-R2]       the stored shared array: round,team,s0..s63 for rounds R..R2 [--team A|B]; a last line
 *                         '# commStored K/N' counts the rounds whose Round carried a full CommTable (K < N: values were
 *                         carried forward on the other rounds)
 *     --track             S0a offline flag tracker (research/REWRITE_DESIGN.md 2.4/2.5/2.11), one CSV row per post-setup trip
 *                         [--team A|B = the tracked ("our") side; default both, rows tagged]. See TRACK_COLS below.
 *     --track-log         --track plus one '#' line per round per tracked flag away from home (belief vs truth)
 *     --dive-note P       the indicator-string prefix --capabilities diveTurns counts (default dive; a test hook, so the
 *                         dive plumbing runs on replays that predate C.CONTACT)
 *     --contact-d0        g4contact premise check D0 (convoy plan section 6): the arm's flag sensor replayed on what one
 *                         team's robots could see, one CSV row per chain on that team's flags [--team A|B = the tracked
 *                         side; default both, rows tagged]. See D0_COLS below; tools/contact-d0.py pools the rows into the
 *                         plan's four routes.
 *     --recall-d0         recall premise check (Gymhgy study L1): one RD row per chain on a tracked team's flags at t = 0, 10,
 *                         20 after the grab: where that team's free robots were and what they were doing [--team A|B;
 *                         default both]. See RD_COLS below; tools/recall-d0.py pools the rows.
 *   ReplayDump --calc     (no replay) reads queries from stdin and prints the 2.4 prediction / 2.5 intercept time:
 *                           P lx ly dx dy cls age            -> px py     (P(now) = step(L, D, min(N, movesDone(age))))
 *                           T lx ly dx dy cls age mx my      -> t         (alive duck at m; -1 = infeasible or t > 30)
 *                           J lx ly dx dy cls age left cx cy [cx cy..] -> t (jailed: spawn centre nearest Q + jail time left)
 *                           W lx ly windowLeft mx my         -> t         (dropped flag inside its return window)
 *                           N px py cx cy [cx cy..]          -> cx cy     (nearest centre; ties: lower x, then lower y)
 *                           D r0 win rn                      -> 1|0       (a flag first seen dropped at r0 still lies there
 *                                                                           at the end of round rn: rn < r0 + win)
 *                         and the g4contact rules (convoy plan sections 5-6):
 *                           C lx ly dx dy age                -> px py | - (the arm's chainPoint; - = null, the predicted
 *                                                                           arrival has passed)
 *                           X g0 outcome o20_1 o100_1 .. o20_k o100_k  -> the CHAIN_COLS fields, comma-separated (one
 *                                                                           scripted chain added to a running ChainTally:
 *                                                                           outcome CAPTURE|RETURN|OPEN, k = T open rounds)
 *                           V g0                             -> the same  (one dive turn added to that tally: g0 of its
 *                                                                           nearest chain, -1 = none within dist2 144)
 *
 * --capabilities columns appended for S0a (each describes the OTHER team's trips on this team's flags, like chasers20;
 * blank when the event is absent, so tools/delivery-check.py skips the game):
 *   enemyUnseenRounds  post-setup enemy carrier-rounds with none of ours within dist2 20 (blank: no enemy carrier-round)
 *   unopposedCaps      enemy captures whose trip had mean chasers20 < 0.5, unrounded (blank: no finished enemy trip)
 *   longTrips25, longCaps25, longCapRate  enemy trips of 25+ rounds, captures among them, and their share (all three blank
 *                      without a 25+ round enemy trip, design 2.11)
 *   loneDeaths         our deaths with at most 1 of ours (alive at the end of that round) within dist2 20 (blank: no deaths)
 *   trickleDeaths      our deaths within 30 rounds of the robot's spawn with at most 2 of ours within dist2 20 (blank: no deaths)
 *   symOk              1 if Sym.best() of this team's slot 16 at r250 (ROT > FX > FY; 0 = unwritten = all) maps this
 *                      team's spawn centres onto the other team's; blank without a stored shared array. Meaningful for our
 *                      builds only (other bots use slot 16 for something else or not at all).
 *   psymOk             as symOk with slot 24 (PSYM; 0 = unwritten, read as slot 16) in place of slot 16; blank unless slot
 *                      23 (RT_STAMP) was ever written, i.e. without the 2.3 tracker slots. Meaningful for our builds only,
 *                      as symOk (an external bot may write slot 23 for its own purposes)
 *   maxBcK             the team's largest bytecode count in one turn, in thousands (1 decimal)
 *   flagDistMin/Mean   at r200, own flags' distance in tiles to the nearest enemy spawn centre (min, mean over the 3)
 *   flagSpreadMin/Max  at r200, the smallest / largest distance in tiles between two of the team's own flags (2026-10-06:
 *                      do the bots that beat us keep their flags together?)
 *   paidKillShare      the other team's deaths on its own territory / all its deaths: a proxy for the share of our kills
 *                      that pay the +30 kill reward (the killer stands within 2 tiles of the victim; RULES.md); blank without
 *                      a death. homeDeathShare: the same for our deaths on our territory (kills that pay them) (2026-10-06, T15)
 *   healThreat10       post-setup heals made with an enemy within dist2 10 of the healer (end-of-round positions) / all
 *                      post-setup heals; readyHeld20: post-setup robot-rounds with an enemy within dist2 20 in which the robot
 *                      ends the round with action cooldown < 10 (a strike ready) / all such robot-rounds (2026-10-06: the
 *                      upper tier heals under threat 25% of the time to our 45% and holds a ready strike 0.34 to our 0.11)
 *   spawnNear20        post-setup spawns that end their round with an enemy within dist2 20 / all post-setup spawns;
 *                      spawnDeath10: post-setup deaths within 10 rounds of the robot's spawn / all post-setup deaths (2026-10-06:
 *                      vs the upper tier 59% of our spawns end beside an enemy, theirs 24%; SPAWN_SAFE's signature)
 *   ringStunsPost      post-setup stun traps the team built within dist2 13 of one of its flag homes; fieldStunsPost: its other
 *                      post-setup stun traps (2026-10-06, andli28 study lever 5, arm g7ring)
 *   carrierDeathsSpawn post-setup deaths of the team's flag carriers within dist2 64 of an enemy spawn centre and more than
 *                      dist2 100 from the carried flag's home: carriers killed walking past an enemy spawn (2026-10-06)
 *   carrierStunBuilds  post-setup stun traps we built within dist2 8 of an enemy carrying our flag; carrierStunned: our
 *                      triggered stuns that caught such a carrier (within dist2 13)
 *   captured600        flags captured by the end of r600 (enemyCaptured600: the other team's)
 *   defNearAtGrab20    mean of our robots within dist2 20 of our flag at each enemy first grab (blank: no grabs)
 *   capturedHomeRounds robot-rounds our robots spend within dist2 8 of a captured own flag's home
 *   stunTrig           our stun traps triggered after setup; stunVictims: enemy robots within dist2 13 of them (end of round)
 *   enemyStunTrig      the same for the opponent's stuns; enemyStunVictims: our robots they caught
 *   stunVictimsEsc     stunVictims of our stuns triggered within dist2 36 of our own carrier (escort stuns); enemyStunVictimsEsc theirs
 *   stunVictimsFast    stunVictims of our stuns triggered within 10 rounds of their build (the fight they were built for); enemy... theirs
 *   deathsHome         our robots killed on our own territory (flood fill from our flags, not through walls/dams): each paid
 *                      its killer +30 crumbs; enemyDeathsHome: theirs on theirs (our kill reward)
 *   gatheredAll        map crumbs our robots collected over the whole game
 *   dropGuard          robot-rounds our robots spend within dist2 8 of one of our flags lying dropped away from home
 *   digsLate           our dig actions after r1500; levelGain1500: our level sum at the end minus at r1500 (blank if shorter)
 *   gathered201to400   map crumbs our robots collected in r201-400 (gathered400 - gathered200, as one column for delivery bars)
 *   stunTrig250        our stuns triggered in r201-250; kills250 / deaths250: our kills and deaths by the end of r250
 *   levelGain1200      our level sum at the end minus at r1200 (blank if shorter); levelGapEnd: ours minus theirs at the end
 *   bank1900           our crumbs at the end of r1900 (blank if shorter); C.LATE_BANK's bank signature
 *   step census (C.ENGAGE_HP's signature, andli28 study 2026-10-06; the execution-order reconstruction of that study's
 *   analyser): every robot is replayed at its own turn. Execution order = round 1's bytecode table (fixed for the game);
 *   robots that act earlier in the round stand at their end-of-round tile, the rest at their tile from the end of the
 *   previous round; HP runs through the round's attacks and heals in action order (hit 150, +60 from the round after the
 *   team's ATTACK upgrade, times the attacker's level effect at the end of the previous round, as the engine; heals alike).
 *   A decision: a post-setup turn of a robot alive at the end of the previous round, not carrying a flag, ready to act and to
 *   move (both cooldowns < 20 at the end of the previous round), alive at its turn, with no enemy within dist2 4 and some
 *   enemy within dist2 10 at its turn, and no enemy carrier (carrying at the end of the previous round) within dist2 20 (the
 *   carrier branch is not gated). A step-in strike: a decision in which the robot attacks.
 *   stepMid            step-in strikes / decisions at 300-699 HP at the robot's turn (blank without such a decision)
 *   stepLethal         step-in strikes at 300-699 HP that end on a lethal tile (the hits of every enemy alive at the turn within
 *                      dist2 10 of the end tile sum to the robot's HP or more) with no enemy within dist2 4 of it that the
 *                      robot's hit kills (C.ENGAGE_HP's unsafe tile: the arm takes none in a plain fight)
 *   stepDeaths         step-in strikes (any HP) after which the robot dies before its next turn: it dies that round, or it dies
 *                      the next round and the hits on it after its strike and before its next turn reach its HP at the turn
 *   killShare          kills / (kills + deaths) over the game (blank without either)
 *   stepMidN, stepDec  stepMid's numerator and denominator: step-in strikes and decisions at 300-699 HP (counts, so a share
 *                      pools over games: sum stepMidN / sum stepDec; review 2026-10-06)
 *   stepLethalAvoid    the stepLethal step-ins whose start tile was not lethal (the hits of every enemy alive at the turn within
 *                      dist2 10 of the start tile stay under the robot's HP; no enemy is within dist2 4 of it at a decision, so
 *                      no kill exempts it): the avoidable ones. When every tile is lethal C.ENGAGE_HP's kite score may still
 *                      pick a refused reaching tile, and striking there beats staying (review 2026-10-06)
 *   overruns           turns at or over the bytecode limit (25000), as --bytecode turnsAtLimit
 *   g4contact chain census (convoy plan section 5; ChainTally). A chain runs from an enemy first grab of one of our flags (the
 *   firstGrabs test: picked up from its home tile) to its CAPTURE, its return home (RETURN: a post-setup PLACE_FLAG without a
 *   carrier; also a first grab of the flag while its chain is open, i.e. a carrier dropped it on its own home tile and it was
 *   re-grabbed before the reset: that chain ends RETURN at the re-grab round, as the reset would have ended it and as the
 *   arm's contactHome ends a track on a flag lying home, and the re-grab opens the next chain) or the game end (OPEN). The
 *   flag's tile at the end of a round is its carrier's while carried, else where it lies. g0 (the grab group) = the carrier
 *   team's robots within dist2 20 of the flag at the end of the grab round, carrier included. At the end of each open round
 *   t = 1..T (T = the chain's last open round): ours20 / ours100 = our robots within dist2 20 / 100 of the flag.
 *   noContact10u12     among chains with g0 < 12 and T >= 1, the share with ours20 = 0 at every t = 1..min(10, T) (a short
 *                      chain counts over its own window; blank: no such chain)
 *   contact20u12       over the flag-rounds t >= 1 of g0 < 12 chains, the share with ours20 >= 1; screened20u12: the share
 *                      with ours20 = 0 and ours100 >= 1 (both blank without such a flag-round)
 *   chainsU12          chains with g0 < 12; chains12p: g0 >= 12; they sum to enemyFirstGrabs (both blank without a chain)
 *   capRateU12         captures / closed chains (CAPTURE or RETURN, re-grab RETURNs included; OPEN excluded) among g0 < 12
 *                      chains; capRate12p the same for g0 >= 12 (blank: no closed chain in the band)
 *   diveTurns          our post-setup robot-turns whose indicator string starts with "dive" (C.CONTACT's note); blank without
 *                      a g0 < 12 chain, so a fire check skips the game
 *   diveLeak12         per dive turn, the open chain on our flags whose flag lies nearest the diver within dist2 144 (end of
 *                      round, a chain grabbed that round included; ties: lower x, then lower y): the share whose chain has
 *                      g0 >= 12; diveNoChain: the share with no chain within dist2 144 (both blank without a dive turn)
 *   Not built (no replay holds them and their encodings are not pinned yet): trkLat, trkHit20, trkFalse, trkDest, trkExc,
 *   the auction/responder columns (cutFire ... escRegrabs) and --defense hunters20.
 *
 * Replay facts (engine 3.0.6, GameMaker.MatchMaker): every Round lists every spawned robot plus the robots that
 * died that round (at their death tile); jailed robots are absent. Team ids 1 = A, 2 = B. Location index =
 * x + y*width. PLACE_FLAG's action id is the FLAG id (not a robot); PICKUP/CAPTURE target the flag id; flag id =
 * location index of the flag's original spawn-zone centre; centres alternate A,B in GameMap.spawnLocations.
 * Robot stdout is NOT stored in 2024 replays (GameMaker line ~604 commented out); use indicator strings.
 * Round.teamCommunication holds both teams' 64 shared-array slots after every round (CommTable team1/team2). Round 1's
 * bytecode table lists all 100 robots in execution order A0,B0,A1,B1,..., which is the order our bot claims its index
 * (slot 0), so robot idx = position / 2 there; idx 0-2 are the flag defenders.
 */
public class ReplayDump {
    static final String[] ACTION = {"ATTACK", "HEAL", "DIG", "FILL", "EXPLOSIVE_TRAP", "WATER_TRAP", "STUN_TRAP",
            "PICKUP_FLAG", "PLACE_FLAG", "CAPTURE_FLAG", "GLOBAL_UPGRADE", "DIE_EXCEPTION"};
    static final String[] BUILD = {"EXPLOSIVE", "WATER", "STUN", "DIG", "FILL"};
    static final String[] WIN = {"CAPTURE", "MORE_FLAG_CAPTURES", "LEVEL_SUM", "MORE_BREAD", "COIN_FLIP", "RESIGNATION"};
    static final int BYTECODE_LIMIT = 25000;
    static final String METRIC_COLS = "round,team,alive,hp,crumbs,captured,carrying,deaths,kills,attacks,heals,"
            + "traps_built,traps_expl,traps_water,traps_stun,traps_hit,digs,fills,pickups,level_sum,moves,max_bc,spawned";

    // ---- static map info
    static int W, H, maxRounds;
    static String mapName, teamA = "A", teamB = "B";
    static boolean[] wall, water, dam;
    static int[] terr;   // territory by flood fill (see header)
    /** deathsHome: our robots killed on our own territory (the killer, standing on its enemy's territory, earns +30 crumbs);
     *  enemyDeathsHome: theirs on their territory (our kill reward). 2026-10-04: Cyril out-builds us 475 to 247 traps. */
    static int[] kDeathsHome = new int[3];
    /** dropGuard: robot-rounds (after setup, end of round) our robots spend within dist2 8 of one of our flags lying dropped
     *  away from its home (the window in which the enemy can re-grab it); C.Z1HOLD's direct signature (2026-10-04). */
    static int[] kDropGuard = new int[3];
    /** digsLate: our dig actions after r1500; levelGain1500: our level sum at the end minus at the end of r1500 (blank if the
     *  game ended before r1500). C.LEVEL_FARM's signature (2026-10-04). */
    static int[] kDigsLate = new int[3], kLevel1500 = {-1, -1, -1};
    /** stunTrig250: our stuns (built any time) triggered in r201-250; kills250 / deaths250: our kills and deaths by the end of
     *  r250 (the dam-drop skirmish, the Gymhgy study's earliest win/loss separator; C.DAM_FIRST's signature). */
    static int[] kStunTrig250 = new int[3], kKills250 = new int[3], kDeaths250 = new int[3];
    /** levelGain1200: our level sum at the end minus at the end of r1200 (blank if shorter); levelGapEnd: ours minus theirs at the end. */
    static int[] kLevel1200 = {-1, -1, -1};
    /** bank1900: our crumbs at the end of r1900 (blank if shorter), not filtered on flag counts; C.LATE_BANK's signature (2026-10-06). */
    static int[] kBank1900 = {-1, -1, -1};
    static int[] spawnZone;            // 0 none, 1 A, 2 B
    static int[] crumbs;               // remaining crumbs per tile
    static Map<Integer, Integer> flagTeam = new HashMap<>();   // flag id -> team (1/2)
    static int winner, winType, totalRounds;

    // ---- per-robot state
    static Map<Integer, Integer> team = new HashMap<>();
    static Map<Integer, int[]> lastLoc = new HashMap<>();
    static Map<Integer, int[]> levels = new HashMap<>();      // id -> {atk, build, heal}
    static Map<Integer, Integer> carrying = new HashMap<>();   // robot id -> flag id
    static Map<Integer, int[]> flagLoc = new HashMap<>();     // flag id -> {x,y} or null when carried/captured
    static Map<Integer, Integer> trapTeam = new HashMap<>(), trapType = new HashMap<>();
    static Map<Integer, int[]> trapLoc = new HashMap<>();

    // ---- per-team cumulative counters [team 1..2]
    static int[] cSpawns = new int[3], cDeaths = new int[3], cAttacks = new int[3], cHeals = new int[3], cDigs = new int[3],
            cFills = new int[3], cPickups = new int[3], cCaptures = new int[3], cMoves = new int[3], cTrapsHit = new int[3],
            cUpgrades = new int[3], cExc = new int[3];
    static int[][] cTraps = new int[3][3];
    static int[] crumbsNow = new int[3];
    static int[] firstCapture = new int[3], firstDeath = new int[3];
    static int[] maxBc = new int[3], turnsAtLimit = new int[3], turnsNear = new int[3];
    static long[] bcSum = new long[3], bcTurns = new long[3];
    // per-round snapshots for the current round
    static int[] alive = new int[3], hpSum = new int[3], roundMaxBc = new int[3];

    // ---- nav stats
    static Map<Integer, int[]> prevLoc = new HashMap<>(), prev2Loc = new HashMap<>();
    static int[] osc = new int[3], robotRounds = new int[3], stillRounds = new int[3], robotRoundsPost = new int[3], stillPost = new int[3], stillPostNoEnemy = new int[3];
    static boolean[][] visited = new boolean[3][];

    // ---- options
    static int every = 0, metricsEvery = 0, from = -1, to = -1, robot = Integer.MIN_VALUE, mapAt = -1;   // robot: MIN_VALUE = none (water-trap digs have actor id -1)
    static Pattern logs = null; static int logTeam = 0;
    static boolean defMode = false;
    static Map<Integer, int[]> trip = new HashMap<>();   // --defense: holder -> {startRound, first(1/0), fx, fy, near20, near64, chaserSum, rounds}
    static boolean near90 = false;   // --near90: as --bytecode, plus one line per turn at 90%+ of the limit
    static boolean bytecode = false, navstats = false, flagsMode = false, summary = true, survey = false, levelsMode = false, capMode = false;
    // --capabilities: basic-capability census per team (research/TACTIC_LEVELS.md)
    static int[] side;                                   // tile -> team whose spawn centres are nearer (1/2)
    static int[] kGathered = new int[3], kGathered200 = new int[3], kGathered400 = new int[3], kFirstEnemySide = new int[3],
            kInEnemy250 = new int[3], kInEnemy300 = new int[3], kFirstFlagSight = new int[3], kCarrierDeaths = new int[3],
            kCarrierMoves = new int[3], kCarrierRounds = new int[3];
    static long[] kAliveSum = new long[3]; static int kRounds;
    // Phase 0 (research/TACTIC_LEVELS.md): post-setup pickups split by kind; carrier-death distance; dam staging
    static int[] kPostPickups = new int[3], kFirstGrabs = new int[3], kRegrabs = new int[3], kRelayPickups = new int[3],
            kDamStage199 = new int[3], kCarrierDeathDistSum = new int[3], kCarrierDeathN = new int[3],
            kRegrabsLate = new int[3], kCapturedLate = new int[3], kChaseSum = new int[3], kChaseRounds = new int[3], kEscortSum = new int[3];   // after r1200 (the CAPTURING window of upgrade order 3)
    static Map<Integer, int[]> flagHome = new HashMap<>();     // flag -> default location (set at r200 and on returns)
    static Map<Integer, Boolean> lastDropByDeath = new HashMap<>();
    // --survey: per-team tactic features (TACTICS.md survey, tools/tactics-survey.py)
    static int[] sOwnFlagPickupsSetup = new int[3], sDigs200 = new int[3], sFills200 = new int[3], sTraps200 = new int[3],
            sCrumbs200 = new int[3], sCrumbs250 = new int[3], sLevel200 = new int[3], sFirstPickup = new int[3], sDrops = new int[3],
            sHeals400 = new int[3], sAtk400 = new int[3], sAlive400 = new int[3], sDefend300 = new int[3];
    static int[][] sTraps400 = new int[3][3];
    static String[] sUpgrades = {"", "", ""};
    static int[] sFlagMoveDist = new int[3];
    static PrintStream out = System.out;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) { System.err.println("usage: ReplayDump <replay.bc24> [flags]"); System.exit(2); }
        if (args[0].equals("--calc")) { calc(); return; }
        String file = args[0];
        for (int i = 1; i < args.length; i++) {
            String a = args[i];
            switch (a) {
                case "--every": every = Integer.parseInt(args[++i]); summary = false; break;
                case "--metrics": metricsEvery = (i + 1 < args.length && args[i + 1].matches("\\d+")) ? Integer.parseInt(args[++i]) : 1; summary = false; break;
                case "--from": from = Integer.parseInt(args[++i]); summary = false; break;
                case "--to": to = Integer.parseInt(args[++i]); summary = false; break;
                case "--robot": robot = Integer.parseInt(args[++i]); summary = false; break;
                case "--map-at": mapAt = Integer.parseInt(args[++i]); summary = false; break;
                case "--logs": logs = Pattern.compile(args[++i]); summary = false; break;
                case "--team": logTeam = args[++i].equalsIgnoreCase("A") ? 1 : 2; break;
                case "--bytecode": bytecode = true; summary = false; break;
                case "--near90": bytecode = true; near90 = true; summary = false; break;
                case "--navstats": navstats = true; summary = false; break;
                case "--flags": flagsMode = true; summary = false; break;
                case "--survey": survey = true; summary = false; break;
                case "--trapgeo": trapGeo = true; summary = false; break;
                case "--levels": levelsMode = true; summary = false; break;
                case "--capabilities": capMode = true; summary = false; break;
                case "--defense": defMode = true; summary = false; break;
                case "--track": trackMode = true; summary = false; break;
                case "--track-log": trackMode = trackLog = true; summary = false; break;
                case "--contact-d0": d0Mode = true; summary = false; break;
                case "--recall-d0": rdMode = true; summary = false; break;
                case "--dive-note": diveNote = args[++i]; break;
                case "--comm": {
                    String[] p = args[++i].split("-");
                    commFrom = Integer.parseInt(p[0]); commTo = p.length > 1 ? Integer.parseInt(p[1]) : commFrom;
                    commMode = true; summary = false; break;
                }
                case "--summary": summary = true; break;
                default: System.err.println("unknown flag " + a); System.exit(2);   // unknown flags are hard errors
            }
        }
        if (from >= 0 && to < 0) to = Integer.MAX_VALUE;
        if (to >= 0 && from < 0) from = 0;
        run(load(file));
    }

    static ByteBuffer load(String file) throws IOException {
        byte[] raw;
        try (InputStream in = new FileInputStream(file)) { raw = readAll(in); }
        if (raw.length > 2 && (raw[0] & 0xff) == 0x1f && (raw[1] & 0xff) == 0x8b)
            raw = readAll(new GZIPInputStream(new ByteArrayInputStream(raw)));
        return ByteBuffer.wrap(raw);
    }

    static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        byte[] b = new byte[1 << 16]; int n;
        while ((n = in.read(b)) > 0) bo.write(b, 0, n);
        return bo.toByteArray();
    }

    static String tname(int t) { return t == 1 ? "A" : t == 2 ? "B" : "?"; }
    static int idx(int x, int y) { return x + y * W; }

    static void run(ByteBuffer bb) {
        GameWrapper gw = GameWrapper.getRootAsGameWrapper(bb);
        if (metricsEvery > 0) out.println(METRIC_COLS);
        for (int i = 0; i < gw.eventsLength(); i++) {
            EventWrapper ev = gw.events(i);
            switch (ev.eType()) {
                case Event.GameHeader: {
                    GameHeader gh = (GameHeader) ev.e(new GameHeader());
                    for (int t = 0; t < gh.teamsLength(); t++) {
                        TeamData td = gh.teams(t);
                        if (td.teamId() == 1) teamA = td.name(); else if (td.teamId() == 2) teamB = td.name();
                    }
                    break;
                }
                case Event.MatchHeader: header((MatchHeader) ev.e(new MatchHeader())); break;
                case Event.Round: round((Round) ev.e(new Round())); break;
                case Event.MatchFooter: {
                    MatchFooter mf = (MatchFooter) ev.e(new MatchFooter());
                    winner = mf.winner(); winType = mf.winType(); totalRounds = mf.totalRounds();
                    break;
                }
                default: break;
            }
        }
        finish();
    }

    static void header(MatchHeader mh) {
        GameMap m = mh.map();
        mapName = m.name(); W = m.size().x(); H = m.size().y(); maxRounds = mh.maxRounds();
        int n = W * H;
        wall = new boolean[n]; water = new boolean[n]; dam = new boolean[n]; spawnZone = new int[n]; crumbs = new int[n];
        for (int i = 0; i < n; i++) { wall[i] = m.walls(i); water[i] = m.water(i); dam[i] = m.divider(i); }
        VecTable sp = m.spawnLocations();
        for (int j = 0; j < sp.xsLength(); j++) {
            int t = (j % 2 == 0) ? 1 : 2, cx = sp.xs(j), cy = sp.ys(j);
            flagTeam.put(idx(cx, cy), t);
            flagLoc.put(idx(cx, cy), new int[]{cx, cy});
            for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) {
                int x = cx + dx, y = cy + dy;
                if (x >= 0 && y >= 0 && x < W && y < H) spawnZone[idx(x, y)] = t;
            }
        }
        List<int[]> cA = new ArrayList<>(), cB = new ArrayList<>();
        for (int j = 0; j < sp.xsLength(); j++) (j % 2 == 0 ? cA : cB).add(new int[]{sp.xs(j), sp.ys(j)});
        centres[1] = cA.toArray(new int[0][]); centres[2] = cB.toArray(new int[0][]);
        terr = new int[n];   // engine territory (GameWorld floodFillTeam): 8-connected flood from each team's flags, not
        for (int t = 1; t <= 2; t++) {   // through walls or dams; 1 / 2 one team's, 3 both (counted for neither)
            ArrayDeque<Integer> q = new ArrayDeque<>(); boolean[] seen = new boolean[n];
            for (int[] c : centres[t]) { int ci = idx(c[0], c[1]); seen[ci] = true; q.add(ci); }
            while (!q.isEmpty()) {
                int ci = q.poll(); terr[ci] |= t;
                int cx = ci % W, cy = ci / W;
                for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) {
                    int x = cx + dx, y = cy + dy; if (x < 0 || y < 0 || x >= W || y >= H) continue;
                    int ni = idx(x, y); if (seen[ni] || wall[ni] || dam[ni]) continue;
                    seen[ni] = true; q.add(ni);
                }
            }
        }
        side = new int[n];
        for (int i = 0; i < n; i++) {
            int x = i % W, y = i / W, best = Integer.MAX_VALUE, bt = 0;
            for (Map.Entry<Integer, Integer> e : flagTeam.entrySet()) {
                int cx = e.getKey() % W, cy = e.getKey() / W, d = (x - cx) * (x - cx) + (y - cy) * (y - cy);
                if (d < best) { best = d; bt = e.getValue(); }
            }
            side[i] = bt;
        }
        VecTable rp = m.resourcePiles();
        for (int j = 0; j < rp.xsLength(); j++) crumbs[idx(rp.xs(j), rp.ys(j))] = m.resourcePileAmounts(j);
        visited[1] = new boolean[n]; visited[2] = new boolean[n];
        Arrays.fill(firstCapture, -1); Arrays.fill(firstDeath, -1);
        if (summary) {
            int nw = 0, nwa = 0, nd = 0, tc = 0; for (int i = 0; i < n; i++) { if (wall[i]) nw++; if (water[i]) nwa++; if (dam[i]) nd++; tc += crumbs[i]; }
            out.printf("map %s %dx%d symmetry=%d seed=%d walls=%d water=%d dam=%d crumbs=%d maxRounds=%d%n",
                    mapName, W, H, m.symmetry(), m.randomSeed(), nw, nwa, nd, tc, maxRounds);
        }
    }

    static boolean defHeader = false;
    static boolean tripsOn() { return defMode || trackMode || capMode; }
    /** The S0a offline tracker runs for --track and for --defense (its tKnow column). */
    static boolean s0a() { return trackMode || defMode; }
    static void endTrip(int rn, int holder, String outcome) {
        int[] tr = trip.remove(holder);
        if (tr == null) return;
        int ct = team.getOrDefault(holder, 0);
        double mc = tr[7] > 0 ? (double) tr[6] / tr[7] : 0.0;
        if (defMode) {
            TripX tx = tripX.get(holder);
            if (!defHeader) { out.println("round,carrierTeam,first,fx,fy,defNear20,defNear64,tripRounds,meanChasers20,outcome,tKnow"); defHeader = true; }
            out.printf("%d,%s,%d,%d,%d,%d,%d,%d,%.1f,%s,%s%n", tr[0], tname(ct), tr[1], tr[2], tr[3], tr[4], tr[5], rn - tr[0], mc, outcome,
                    tx != null && tx.tKnow >= 0 ? String.valueOf(tx.tKnow) : "");
        }
        if (capMode && ct > 0) {   // the defending team's view of this trip
            int dt = 3 - ct; boolean capd = outcome.equals("CAPTURE");
            kEnemyTrips[dt]++;
            if (capd && mc < 0.5) kUnopposed[dt]++;
            if (rn - tr[0] >= 25) { kLong25[dt]++; if (capd) kLongCaps25[dt]++; }
            if (tr[1] == 1) { kDefAtGrabSum[dt] += tr[4]; kDefAtGrabN[dt]++; }   // defNearAtGrab20 (audit 2026-10-03 BOT2)
        }
        TripX x = tripX.remove(holder);
        if (x != null) {
            x.outcome = outcome; x.chasers = mc; x.chaserSum = tr[6]; x.chaserRounds = tr[7]; x.tripRounds = rn - tr[0]; x.defNear20 = tr[4];
            int[] l = lastLoc.get(holder);
            if (outcome.equals("CAPTURE") && l != null && centres[ct] != null) { int[] z = nearestOf(centres[ct], l[0], l[1]); x.zone = z[0] + z[1] * W; }
            tripDone.add(x);
        }
    }

    static boolean inWindow(int r) { return from >= 0 && r >= from && r <= to; }

    static void round(Round r) {
        int rn = r.roundId();
        // shared array (stored after every round; carried forward if a round lacks it)
        CommTable ct = r.teamCommunication();
        if (ct != null && ct.team1Length() >= 64 && ct.team2Length() >= 64) commStored++;
        if (ct != null) {
            commSeen = true;
            for (int k = 0; k < ct.team1Length() && k < 64; k++) comm[1][k] = ct.team1(k);
            for (int k = 0; k < ct.team2Length() && k < 64; k++) comm[2][k] = ct.team2(k);
        }
        if (commSeen && rn <= 250) for (int t = 1; t <= 2; t++) { sym250[t] = comm[t][16]; psym250[t] = comm[t][24]; }
        if (commSeen) for (int t = 1; t <= 2; t++) {   // audit B1: decided (every survivor correct) vs wrong (no survivor correct)
            int m = comm[t][16] & 7, ok = correctSyms(t);
            if (m == 0 || ok < 0) continue;
            if ((m & ok) == 0) symWrongT[t] = 1;
            else if ((m & ~ok) == 0 && symDecided[t] < 0) symDecided[t] = rn;
        }
        if (commSeen && rn > 1) for (int t = 1; t <= 2; t++) if (comm[t][23] != 0) trkSlots[t] = true;
        if (commMode && commSeen && rn >= commFrom && rn <= commTo) {
            if (!commHeader) { StringBuilder h = new StringBuilder("round,team"); for (int k = 0; k < 64; k++) h.append(",s").append(k); out.println(h); commHeader = true; }
            for (int t = 1; t <= 2; t++) {
                if (logTeam != 0 && logTeam != t) continue;
                StringBuilder sb2 = new StringBuilder().append(rn).append(',').append(tname(t));
                for (int k = 0; k < 64; k++) sb2.append(',').append(comm[t][k]);
                out.println(sb2);
            }
        }
        // execution order of the first round = creation order A0,B0,A1,B1,... = our bot's slot-0 index
        if (!execDone && r.bytecodeIdsLength() > 0) {
            for (int j = 0; j < r.bytecodeIdsLength(); j++) { execIdx.put(r.bytecodeIds(j), j / 2); execTeam.put(r.bytecodeIds(j), j % 2 == 0 ? 1 : 2); }
            execDone = true;
        }
        // team resources
        for (int j = 0; j < r.teamIdsLength(); j++) crumbsNow[r.teamIds(j)] = r.teamResourceAmounts(j);
        // spawns
        SpawnedBodyTable sb = r.spawnedBodies();
        if (sb != null) for (int j = 0; j < sb.robotIdsLength(); j++) {
            int id = sb.robotIds(j), t = sb.teamIds(j);
            team.put(id, t); cSpawns[t]++; lastSpawn.put(id, rn);
            if (capMode && rn > 200 && t > 0) spawnsNow.add(id);
            if (execDone && execTeam.containsKey(id) && execTeam.get(id) != t) execBroken = true;   // the A,B alternation did not hold
            if (inWindow(rn) || id == robot) out.printf("r%d SPAWN %s#%d at (%d,%d)%n", rn, tname(t), id, sb.locs().xs(j), sb.locs().ys(j));
        }
        // robot state
        Arrays.fill(alive, 0); Arrays.fill(hpSum, 0); Arrays.fill(roundMaxBc, 0);
        VecTable locs = r.robotLocs();
        Map<Integer, Integer> hpNow = new HashMap<>();
        nowLoc.clear();
        for (int j = 0; j < r.robotIdsLength(); j++) {
            int id = r.robotIds(j), t = team.getOrDefault(id, 0), x = locs.xs(j), y = locs.ys(j), hp = r.robotHealths(j);
            hpNow.put(id, hp);
            if (t > 0 && hp > 0) nowLoc.put(id, new int[]{x, y, t});
            if (capMode) acdNow.put(id, r.robotActionCooldowns(j));
            levels.put(id, new int[]{r.attackLevels(j), r.buildLevels(j), r.healLevels(j)});
            if (t == 0) continue;
            if (hp > 0) { alive[t]++; hpSum[t] += hp; }
            int[] pl = lastLoc.get(id);
            boolean moved = pl != null && (pl[0] != x || pl[1] != y);
            if (moved) {
                cMoves[t]++;
                int[] p2 = prevLoc.get(id);
                if (p2 != null && p2[0] == x && p2[1] == y) osc[t]++;
                prevLoc.put(id, pl);
            } else if (pl != null && hp > 0) {
                stillRounds[t]++;
                if (rn > 200) {
                    stillPost[t]++;
                    boolean seen = false;                       // an enemy within vision (dist2 20) last we knew
                    for (Map.Entry<Integer, int[]> re : lastLoc.entrySet()) {
                        if (team.getOrDefault(re.getKey(), 0) != 3 - t) continue;
                        int dx = re.getValue()[0] - x, dy = re.getValue()[1] - y;
                        if (dx * dx + dy * dy <= 20) { seen = true; break; }
                    }
                    if (!seen) stillPostNoEnemy[t]++;
                }
            }
            if (hp > 0) { robotRounds[t]++; if (rn > 200) robotRoundsPost[t]++; }
            visited[t][idx(x, y)] = true;
            if (capMode && hp > 0 && rn > 200) for (int[] h : capturedHomes[t]) {   // capturedHomeRounds (audit BOT1)
                int dx = h[0] - x, dy = h[1] - y; if (dx * dx + dy * dy <= 8) kCapturedHomeRounds[t]++;
            }
            int ci = idx(x, y); if (crumbs[ci] > 0) { kGathered[t] += crumbs[ci]; crumbs[ci] = 0; }   // stepping on crumbs collects them
            if (hp > 0 && rn > 200 && side[ci] == 3 - t && kFirstEnemySide[t] == 0) kFirstEnemySide[t] = rn;
            if (hp > 0 && side[ci] == 3 - t && (rn == 250 || rn == 300)) { if (rn == 250) kInEnemy250[t]++; else kInEnemy300[t]++; }
            if (carrying.containsKey(id)) { kCarrierRounds[t]++; if (moved) kCarrierMoves[t]++; }
            if (capMode && carrying.containsKey(id) && rn > 200) {   // chasers: the other team's robots within dist2 20 of a carrier
                int c = 0;
                for (Map.Entry<Integer, int[]> re : lastLoc.entrySet()) {
                    if (team.getOrDefault(re.getKey(), 0) != 3 - t) continue;
                    int dx = re.getValue()[0] - x, dy = re.getValue()[1] - y;
                    if (dx * dx + dy * dy <= 20) c++;
                }
                kChaseSum[3 - t] += c; kChaseRounds[3 - t]++;
                int e = 0;                                         // escorts: own robots within dist2 20 of the carrier
                for (Map.Entry<Integer, int[]> re : lastLoc.entrySet()) {
                    if (re.getKey() == id || team.getOrDefault(re.getKey(), 0) != t) continue;
                    int dx = re.getValue()[0] - x, dy = re.getValue()[1] - y;
                    if (dx * dx + dy * dy <= 20) e++;
                }
                kEscortSum[t] += e;
            }
            if (tripsOn() && trip.containsKey(id)) {
                int[] tr = trip.get(id); int c = 0;
                for (Map.Entry<Integer, int[]> re : lastLoc.entrySet()) {
                    if (team.getOrDefault(re.getKey(), 0) != 3 - t) continue;
                    int dx = re.getValue()[0] - x, dy = re.getValue()[1] - y;
                    if (dx * dx + dy * dy <= 20) c++;
                }
                tr[6] += c; tr[7]++;
            }
            if (hp > 0 && rn > 200 && kFirstFlagSight[t] == 0) for (Map.Entry<Integer, int[]> fe : flagLoc.entrySet()) {
                int[] fl = fe.getValue();
                if (fl != null && flagTeam.getOrDefault(fe.getKey(), 0) == 3 - t && (fl[0] - x) * (fl[0] - x) + (fl[1] - y) * (fl[1] - y) <= 20) { kFirstFlagSight[t] = rn; break; }
            }
            lastLoc.put(id, new int[]{x, y});
            if (carrying.containsKey(id)) flagLoc.put(carrying.get(id), null);
            if (id == robot) out.printf("r%d #%d (%d,%d) hp=%d mcd=%d acd=%d lv=%d/%d/%d xp=%d/%d/%d%s%n", rn, id, x, y, hp,
                    r.robotMoveCooldowns(j), r.robotActionCooldowns(j), r.attackLevels(j), r.buildLevels(j), r.healLevels(j),
                    r.attacksPerformed(j), r.buildsPerformed(j), r.healsPerformed(j), carrying.containsKey(id) ? " FLAG" : "");
        }
        // audit A1 contracts: an own-flag alert written this round with no enemy within dist2 20 of any of that team's flag
        // homes; robots standing on one of their own flag home tiles (consecutive rounds, per robot)
        if (rn > 200) for (int t = 1; t <= 2; t++) {
            List<int[]> homes = new ArrayList<>();
            for (Map.Entry<Integer, int[]> fh : flagHome.entrySet()) if (flagTeam.getOrDefault(fh.getKey(), 0) == t && fh.getValue() != null) homes.add(fh.getValue());
            if (homes.isEmpty()) continue;
            if (commSeen) for (int i = 0; i < 3; i++) if (comm[t][10 + i] == rn) {
                boolean threat = false;
                for (Map.Entry<Integer, int[]> re : lastLoc.entrySet()) {
                    if (team.getOrDefault(re.getKey(), 0) != 3 - t) continue;
                    for (int[] h : homes) { int dx = re.getValue()[0] - h[0], dy = re.getValue()[1] - h[1]; if (dx * dx + dy * dy <= 20) { threat = true; break; } }
                    if (threat) break;
                }
                kAlertWrites[t]++; if (!threat) kAlertNoThreat[t]++;
            }
            for (Map.Entry<Integer, int[]> re : lastLoc.entrySet()) {
                int id = re.getKey();
                if (team.getOrDefault(id, 0) != t) continue;
                boolean on = false;
                for (int[] h : homes) if (re.getValue()[0] == h[0] && re.getValue()[1] == h[1]) on = true;
                int run = on ? parkRun.getOrDefault(id, 0) + 1 : 0;
                parkRun.put(id, run);
                if (run > kMaxPark[t]) kMaxPark[t] = run;
            }
        }
        // audit A5/A6 contracts on the enemy-flag registry (slots EF_ID 1-3, EF_LOC 4-6, EF_STATE 7-9)
        if (rn > 200 && commSeen) for (int t = 1; t <= 2; t++) for (int i = 0; i < 3; i++) {
            int fid = comm[t][1 + i] - 1, st = comm[t][7 + i], enc = comm[t][4 + i];
            if (fid < 0 || flagTeam.getOrDefault(fid, 0) != 3 - t) continue;
            int holder = -1; for (Map.Entry<Integer, Integer> ce : carrying.entrySet()) if (ce.getValue() == fid) holder = ce.getKey();
            boolean ourCarrier = holder >= 0 && team.getOrDefault(holder, 0) == t;
            if (st == 1 && !ourCarrier) kEfStaleCarry[t]++;
            if (st == 0 && enc != 0) {
                int[] truth = holder >= 0 ? lastLoc.get(holder) : flagLoc.get(fid);
                if (truth == null) truth = flagHome.get(fid);
                int ex = (enc - 1) / 64, ey = (enc - 1) % 64;
                if (truth != null && Math.max(Math.abs(truth[0] - ex), Math.abs(truth[1] - ey)) > 2) kEfStaleLoc[t]++;
            }
        }
        if (capMode) for (int sid : spawnsNow) {   // spawnNear20: a post-setup spawn that ends its round with an enemy within dist2 20
            int[] q = nowLoc.get(sid); if (q == null) continue;
            kSpawnPost[q[2]]++;
            for (int[] o : nowLoc.values()) if (o[2] == 3 - q[2] && d2(o[0], o[1], q[0], q[1]) <= VISION2) { kSpawnNear[q[2]]++; break; }
        }
        spawnsNow.clear();
        if (capMode && rn > 200) for (Map.Entry<Integer, int[]> e : nowLoc.entrySet()) {   // readyHeld20: near an enemy, action ready
            int[] q = e.getValue(); boolean near = false;
            for (int[] o : nowLoc.values()) if (o[2] == 3 - q[2] && d2(o[0], o[1], q[0], q[1]) <= VISION2) { near = true; break; }
            if (!near) continue;
            kNear20[q[2]]++; if (acdNow.getOrDefault(e.getKey(), 99) < 10) kReady20[q[2]]++;
        }
        // bytecodes
        for (int j = 0; j < r.bytecodeIdsLength(); j++) {
            int id = r.bytecodeIds(j), t = team.getOrDefault(id, 0), bc = r.bytecodesUsed(j);
            if (t == 0) continue;
            if (bc > maxBc[t]) maxBc[t] = bc;
            if (bc > roundMaxBc[t]) roundMaxBc[t] = bc;
            if (bc >= BYTECODE_LIMIT) { turnsAtLimit[t]++; if (bytecode) out.printf("r%d %s#%d at the bytecode limit (%d)%n", r.roundId(), tname(t), id, bc); } else if (bc >= BYTECODE_LIMIT * 9 / 10) { turnsNear[t]++; if (near90) out.printf("r%d %s#%d near the limit (%d)%n", r.roundId(), tname(t), id, bc); }
            bcSum[t] += bc; bcTurns[t]++;
        }
        // actions
        for (int j = 0; j < r.actionIdsLength(); j++) {
            int id = r.actionIds(j), a = r.actions(j), tgt = r.actionTargets(j);
            int t = team.getOrDefault(id, 0);
            String desc = null;
            switch (a) {
                case Action.ATTACK: cAttacks[t]++; desc = "attacks #" + tgt; break;
                case Action.HEAL: cHeals[t]++; desc = "heals #" + tgt;
                    if (capMode && rn > 200 && t > 0 && nowLoc.containsKey(id)) {   // healThreat10: a heal with an enemy within dist2 10
                        int[] h = nowLoc.get(id); kHealPost[t]++;
                        for (int[] o : nowLoc.values()) if (o[2] == 3 - t && d2(o[0], o[1], h[0], h[1]) <= 10) { kHealThreat[t]++; break; }
                    }
                    break;
                case Action.DIG: if (id >= 0) { cDigs[t]++; if (rn > 1500) kDigsLate[t]++; } water[tgt] = true; desc = "digs (" + tgt % W + "," + tgt / W + ")"; break;
                case Action.FILL: cFills[t]++; water[tgt] = false; desc = "fills (" + tgt % W + "," + tgt / W + ")"; break;
                case Action.EXPLOSIVE_TRAP: case Action.WATER_TRAP: case Action.STUN_TRAP:
                    cTrapsHit[t]++; desc = "triggers " + ACTION[a] + " at (" + tgt % W + "," + tgt / W + ")"; break;
                case Action.PICKUP_FLAG:
                    if (rn > 200) {
                        kPostPickups[t]++;
                        int[] fl = flagLoc.get(tgt), fh = flagHome.get(tgt);
                        if (fl != null && fh != null && fl[0] == fh[0] && fl[1] == fh[1]) { kFirstGrabs[t]++; if (chainsOn()) chainStart(rn, tgt, 3 - t); }
                        else if (Boolean.TRUE.equals(lastDropByDeath.get(tgt))) { kRegrabs[t]++; if (rn > 1200) kRegrabsLate[t]++; }
                        else kRelayPickups[t]++;
                    }
                    if (rn <= 200) sOwnFlagPickupsSetup[t]++;
                    else if (sFirstPickup[t] == 0) sFirstPickup[t] = rn;
                    if (tripsOn() && rn > 200) {
                        int[] fl0 = flagLoc.get(tgt), fh0 = flagHome.get(tgt);
                        int[] at = fl0 != null ? fl0 : lastLoc.get(id);
                        int n20 = 0, n64 = 0;
                        if (at != null) for (Map.Entry<Integer, int[]> re : lastLoc.entrySet()) {
                            if (team.getOrDefault(re.getKey(), 0) != 3 - t) continue;
                            int dx = re.getValue()[0] - at[0], dy = re.getValue()[1] - at[1], d2 = dx * dx + dy * dy;
                            if (d2 <= 20) n20++; if (d2 <= 64) n64++;
                        }
                        boolean first = fl0 != null && fh0 != null && fl0[0] == fh0[0] && fl0[1] == fh0[1];
                        trip.put(id, new int[]{rn, first ? 1 : 0, at == null ? -1 : at[0], at == null ? -1 : at[1], n20, n64, 0, 0});
                        if (s0a()) startTrack(rn, id, t, tgt, first, at);
                    }
                    cPickups[t]++; carrying.put(id, tgt); flagLoc.put(tgt, null);
                    desc = "picks up flag " + tname(flagTeam.getOrDefault(tgt, 0)) + tgt; flagEvent(rn, "PICKUP", id, tgt); break;
                case Action.PLACE_FLAG: {    // id is the FLAG id
                    int fx = tgt % W, fy = tgt / W;
                    boolean wasCarried = carrying.containsValue(id);
                    if (rn == 200 || (rn > 200 && !wasCarried)) { flagHome.put(id, new int[]{fx, fy}); lastDropByDeath.remove(id); }   // default set / returned home
                    if (rn > 200 && !wasCarried && chainsOn()) chainEnd(rn, id, "RETURN", new int[]{fx, fy});
                    { for (Map.Entry<Integer, Integer> ce : carrying.entrySet()) if (ce.getValue() == id && rn > 200) {
                        int holder = ce.getKey(); boolean dies = false;
                        for (int q = 0; q < r.diedIdsLength(); q++) if (r.diedIds(q) == holder) dies = true;
                        if (tripsOn()) endTrip(rn, holder, dies ? "DIED" : "DROP");
                        if (!dies) sDrops[team.getOrDefault(holder, 0)]++;
                        else {
                            int ht = team.getOrDefault(holder, 0); kCarrierDeaths[ht]++;
                            int[] hl = lastLoc.get(holder);
                            if (hl != null) { int bd = Integer.MAX_VALUE;   // distance to the carrier team's nearest spawn centre
                                for (Map.Entry<Integer, Integer> fe : flagTeam.entrySet()) if (fe.getValue() == ht) {
                                    int cx = fe.getKey() % W, cy = fe.getKey() / W; bd = Math.min(bd, (hl[0] - cx) * (hl[0] - cx) + (hl[1] - cy) * (hl[1] - cy)); }
                                kCarrierDeathDistSum[ht] += (int) Math.round(Math.sqrt(bd)); kCarrierDeathN[ht]++;
                                int ed = Integer.MAX_VALUE; int[] fh = flagHome.get(id);   // carrierDeathsSpawn: on the way past an enemy spawn
                                for (Map.Entry<Integer, Integer> fe : flagTeam.entrySet()) if (fe.getValue() == 3 - ht) ed = Math.min(ed, d2(hl[0], hl[1], fe.getKey() % W, fe.getKey() / W));
                                if (ed <= 64 && (fh == null || d2(hl[0], hl[1], fh[0], fh[1]) > 100)) kCarrierDeathsSpawn[ht]++; }
                        }
                        lastDropByDeath.put(id, dies); } }   // the drop event precedes the death record
                    flagLoc.put(id, new int[]{fx, fy});
                    carrying.values().remove(id);
                    desc = null;
                    if (inWindow(rn)) out.printf("r%d FLAG %s%d placed at (%d,%d)%n", rn, tname(flagTeam.getOrDefault(id, 0)), id, fx, fy);
                    flagEvent(rn, "PLACE", -1, id);
                    break;
                }
                case Action.CAPTURE_FLAG:
                    { int ft = flagTeam.getOrDefault(tgt, 0); int[] hm = flagHome.get(tgt); if (ft >= 1 && ft <= 2 && hm != null) capturedHomes[ft].add(hm); }
                    if (chainsOn()) chainEnd(rn, tgt, "CAPTURE", lastLoc.get(id));
                    cCaptures[t]++; if (rn > 1200) kCapturedLate[t]++; if (rn <= 600) kCaptured600[t]++; if (tripsOn()) endTrip(rn, id, "CAPTURE"); carrying.remove(id); flagLoc.put(tgt, null);
                    capturedFlags.add(tgt);
                    if (firstCapture[t] < 0) firstCapture[t] = rn;
                    desc = "CAPTURES flag " + tname(flagTeam.getOrDefault(tgt, 0)) + tgt; flagEvent(rn, "CAPTURE", id, tgt); break;
                case Action.GLOBAL_UPGRADE: if (tgt == 2 && t > 0) hasCap[t] = true; sUpgrades[t] += (sUpgrades[t].isEmpty() ? "" : "+") + (tgt == 0 ? "ATK" : tgt == 1 ? "HEAL" : "CAP") + "@" + rn; cUpgrades[t]++; desc = "buys upgrade " + tgt; break;
                case Action.DIE_EXCEPTION: cExc[t]++; desc = "DIES OF EXCEPTION"; break;
                default: desc = "action" + a;
            }
            if (desc != null && (inWindow(rn) || id == robot)) out.printf("r%d %s#%d %s%n", rn, tname(t), id, desc);
        }
        // digs from water traps are also in digLocations; keep terrain in sync from the vectors
        VecTable dl = r.digLocations();
        if (dl != null) for (int j = 0; j < dl.xsLength(); j++) water[idx(dl.xs(j), dl.ys(j))] = true;
        VecTable fl = r.fillLocations();
        if (fl != null) for (int j = 0; j < fl.xsLength(); j++) water[idx(fl.xs(j), fl.ys(j))] = false;
        // traps
        VecTable tl = r.trapAddedLocations();
        for (int j = 0; j < r.trapAddedIdsLength(); j++) {
            int tid = r.trapAddedIds(j), tt = r.trapAddedTeams(j), ty = r.trapAddedTypes(j);
            trapTeam.put(tid, tt); trapType.put(tid, ty); trapLoc.put(tid, new int[]{tl.xs(j), tl.ys(j)}); trapRound.put(tid, rn);
            if (tt >= 1 && tt <= 2 && ty >= 0 && ty < 3) cTraps[tt][ty]++;
            if (capMode && rn > 200 && tt >= 1 && tt <= 2 && "STUN".equals(BUILD[ty])) {   // ringStunsPost / fieldStunsPost
                boolean ring = false;
                for (Map.Entry<Integer, int[]> fh : flagHome.entrySet())
                    if (flagTeam.getOrDefault(fh.getKey(), 0) == tt && fh.getValue() != null && d2(fh.getValue()[0], fh.getValue()[1], tl.xs(j), tl.ys(j)) <= 13) { ring = true; break; }
                if (ring) kRingStuns[tt]++; else kFieldStuns[tt]++;
            }
            if (inWindow(rn)) out.printf("r%d TRAP %s builds %s at (%d,%d)%n", rn, tname(tt), BUILD[ty], tl.xs(j), tl.ys(j));
            if (trapGeo && rn > 200 && tt >= 1 && tt <= 2) {   // nearest enemy and enemies around the trap at the end of the build round
                int nd = Integer.MAX_VALUE, e13 = 0, e8 = 0, own = 0, bd = Integer.MAX_VALUE, blv = -1;
                for (Map.Entry<Integer, int[]> qe : nowLoc.entrySet()) {
                    int[] q = qe.getValue();
                    int dd = d2(q[0], q[1], tl.xs(j), tl.ys(j));
                    if (q[2] == 3 - tt) { nd = Math.min(nd, dd); if (dd <= 13) e13++; if (dd <= 8) e8++; }
                    else {
                        if (dd <= 2) own++;
                        int[] lv = levels.get(qe.getKey());   // the builder: the nearest own robot (end of round), its build level
                        if (dd < bd && lv != null) { bd = dd; blv = lv[1]; }
                    }
                }
                trapBuilt.put(tid, new int[]{rn, nd == Integer.MAX_VALUE ? -1 : nd, e13, e8, own, blv});
            }
            if (capMode && rn > 200 && tt >= 1 && tt <= 2 && "STUN".equals(BUILD[ty]) && carrierNear(3 - tt, tl.xs(j), tl.ys(j), 8)) kCarrierStunBuilds[tt]++;
        }
        for (int j = 0; j < r.trapTriggeredIdsLength(); j++) {
            int tid = r.trapTriggeredIds(j);
            if (inWindow(rn)) { int[] l = trapLoc.get(tid); out.printf("r%d TRAP %s %s at (%d,%d) triggered%n", rn,
                    tname(trapTeam.getOrDefault(tid, 0)), BUILD[trapType.getOrDefault(tid, 0)], l == null ? -1 : l[0], l == null ? -1 : l[1]); }
            if (rdMode && rn > 200) { int[] l = trapLoc.get(tid); int tt = trapTeam.getOrDefault(tid, 0);   // --recall-d0 (any mode mix)
                if (l != null && tt >= 1 && tt <= 2 && "STUN".equals(BUILD[trapType.getOrDefault(tid, 0)])) {
                    int v = 0; for (int[] q : nowLoc.values()) if (q[2] == 3 - tt && d2(q[0], q[1], l[0], l[1]) <= 13) v++;
                    rdEv.add(new int[]{0, tt, l[0], l[1], v}); } }
            if (capMode && rn > 200) { int[] l = trapLoc.get(tid); int tt = trapTeam.getOrDefault(tid, 0);
                if (l != null && tt >= 1 && tt <= 2 && "STUN".equals(BUILD[trapType.getOrDefault(tid, 0)]) && carrierNear(3 - tt, l[0], l[1], 13)) kCarrierStunned[tt]++;
                if (l != null && tt >= 1 && tt <= 2 && "STUN".equals(BUILD[trapType.getOrDefault(tid, 0)])) {
                    kStunTrig[tt]++; if (rn <= 250) kStunTrig250[tt]++;
                    boolean esc = carrierNear(tt, l[0], l[1], 36);   // an escort stun: tt's own carrier within dist2 36
                    boolean fast = rn - trapRound.getOrDefault(tid, -1000) <= 10;
                    for (int[] q : nowLoc.values()) if (q[2] == 3 - tt && d2(q[0], q[1], l[0], l[1]) <= 13) {
                        kStunVictims[tt]++; if (esc) kStunVictimsEsc[tt]++; if (fast) kStunVictimsFast[tt]++; }
                } }
            if (trapGeo && trapBuilt.containsKey(tid)) {
                int[] b = trapBuilt.remove(tid), l = trapLoc.get(tid); int tt = trapTeam.getOrDefault(tid, 0), v = 0;
                if (l != null) for (int[] q : nowLoc.values()) if (q[2] == 3 - tt && d2(q[0], q[1], l[0], l[1]) <= 13) v++;
                out.printf("TG,%s,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d%n", tname(tt), BUILD[trapType.getOrDefault(tid, 0)], b[0], b[1], b[2], b[3], b[4], rn, rn - b[0], v, b[5]);
            }
            trapLoc.remove(tid);
        }
        // deaths
        Set<Integer> diedNow = new HashSet<>();
        for (int j = 0; j < r.diedIdsLength(); j++) diedNow.add(r.diedIds(j));
        deathTile.clear();
        for (int j = 0; j < r.diedIdsLength(); j++) {
            int id = r.diedIds(j), t = team.getOrDefault(id, 0);
            cDeaths[t]++; if (firstDeath[t] < 0) firstDeath[t] = rn;
            { int[] dhl = lastLoc.get(id); if (t > 0 && dhl != null && terr != null && terr[idx(dhl[0], dhl[1])] == t) kDeathsHome[t]++; }
            Integer f = carrying.remove(id);
            if (f != null) kCarrierDeaths[t]++;   // only if no drop event preceded (normally the PLACE event counts it)
            if (tripsOn() && trip.containsKey(id)) endTrip(rn, id, "DIED");
            int[] l = lastLoc.get(id);
            deathRound.put(id, rn);
            if (capMode && l != null) deathTile.put(id, l);   // a diver killed after its turn still dived (diveLeak12)
            if (rdMode && t > 0 && l != null && rn > 200) rdEv.add(new int[]{1, t, l[0], l[1], 1});
            if (capMode && t > 0 && l != null) {   // lone / trickle deaths: own robots alive at the end of the round within dist2 20
                int nb = 0;
                for (Map.Entry<Integer, int[]> re : lastLoc.entrySet()) {
                    if (team.getOrDefault(re.getKey(), 0) != t || diedNow.contains(re.getKey())) continue;
                    if (d2(re.getValue()[0], re.getValue()[1], l[0], l[1]) <= 20) nb++;
                }
                if (nb <= 1) kLoneDeaths[t]++;
                Integer sp0 = lastSpawn.get(id);
                if (sp0 != null && rn - sp0 <= 30 && nb <= 2) kTrickleDeaths[t]++;
                if (rn > 200) { kDeathPost[t]++; if (sp0 != null && rn - sp0 <= 10) kSpawnDeath10[t]++; }
            }
            if (s0a()) for (TripX x : witnessWatch) if (rn <= x.start + 10 && x.witnesses.contains(id)) x.defDied10++;
            if (f != null && l != null) flagLoc.put(f, l);
            if (inWindow(rn) || id == robot) out.printf("r%d DIES %s#%d at (%d,%d)%s%n", rn, tname(t), id, l == null ? -1 : l[0], l == null ? -1 : l[1], f != null ? " dropping flag" : "");
            lastLoc.remove(id); prevLoc.remove(id);
        }
        // indicator strings
        for (int j = 0; j < r.indicatorStringIdsLength(); j++) {
            int id = r.indicatorStringIds(j), t = team.getOrDefault(id, 0);
            String s = r.indicatorStrings(j);
            if (logs != null && (logTeam == 0 || logTeam == t) && logs.matcher(s).find()) out.printf("r%d %s#%d %s%n", rn, tname(t), id, s);
            else if (id == robot) out.printf("r%d #%d says: %s%n", rn, id, s);
            if (rdMode && t > 0) { int sp = s.indexOf(' '); lastNote.put(id, sp < 0 ? s : s.substring(0, sp)); }
            if (capMode && rn > 200 && t > 0 && s.startsWith(diveNote)) {   // diveTurns; resolved to a chain in chainTick, after g0
                int[] at = nowLoc.containsKey(id) ? nowLoc.get(id) : deathTile.get(id);
                if (at != null) divers.add(new int[]{t, at[0], at[1]});
            }
        }
        if (every > 0 && rn % every == 0) printEvery(rn);
        if (metricsEvery > 0 && rn % metricsEvery == 0) for (int t = 1; t <= 2; t++) out.println(metricRow(rn, t));
        if (rn == mapAt) printMap(rn);
        if (survey) surveyTick(rn);
        if (rn == 200 && capMode) flagDistTick();
        if (rn == 199 && capMode) for (Map.Entry<Integer, int[]> e : lastLoc.entrySet()) {
            int t = team.getOrDefault(e.getKey(), 0); if (t == 0) continue;
            int[] l = e.getValue(); boolean near = false;
            for (int dx = -2; dx <= 2 && !near; dx++) for (int dy = -2; dy <= 2 && !near; dy++) {
                int x = l[0] + dx, y = l[1] + dy;
                if (x >= 0 && y >= 0 && x < W && y < H && dam[idx(x, y)]) near = true; }
            if (near) kDamStage199[t]++;
        }
        for (int t = 1; t <= 2; t++) { kAliveSum[t] += alive[t]; if (rn == 200) kGathered200[t] = kGathered[t]; if (rn == 400) kGathered400[t] = kGathered[t]; }
        if (rn == 1500) for (int t = 1; t <= 2; t++) kLevel1500[t] = levelSum(t);
        if (rn == 1200) for (int t = 1; t <= 2; t++) kLevel1200[t] = levelSum(t);
        if (rn == 1900) for (int t = 1; t <= 2; t++) kBank1900[t] = crumbsNow[t];
        if (rn == 250) for (int t = 1; t <= 2; t++) { kKills250[t] = cDeaths[3 - t]; kDeaths250[t] = cDeaths[t]; }
        if (rn > 200) for (Map.Entry<Integer, int[]> fe : flagLoc.entrySet()) {   // dropGuard: own robots beside an own dropped flag
            int[] dgl = fe.getValue(), dgh = flagHome.get(fe.getKey()); int ft = flagTeam.getOrDefault(fe.getKey(), 0);
            if (dgl == null || dgh == null || ft < 1 || ft > 2 || (dgl[0] == dgh[0] && dgl[1] == dgh[1]) || capturedFlags.contains(fe.getKey())) continue;
            for (int[] q : nowLoc.values()) if (q[2] == ft && d2(q[0], q[1], dgl[0], dgl[1]) <= 8) kDropGuard[ft]++;
        }
        if (s0a() || chainsOn()) tick(rn);
        if (s0a()) witnessWatch.removeIf(x -> rn >= x.start + 10);
        if (capMode) stepTick(r, rn);
        kRounds++;
        totalRounds = rn;
    }

    // =================================================================================================================
    // S0a instruments (research/REWRITE_DESIGN.md 2.4, 2.5, 2.11): the shared array, and an offline replay of the
    // 2.4 flag-track rules on what one team's robots could see (vision dist2 20 around every alive robot at the end of
    // the round; a flag, carried or not, is seen when its tile is in vision). One belief per (tracked team, our flag),
    // updated once per round after setup; a second belief takes destinations from Sym.best() of slot 16 instead of the
    // true enemy spawn centres.
    // =================================================================================================================
    static final int S_HOME = 0, S_CARRIED = 1, S_DROPPED = 2, S_MISSING = 3, S_LOST = 4, S_GONE = 5;
    static final String[] SNAME = {"HOME", "CARRIED", "DROPPED", "MISSING", "LOST", "GONE"};
    static final int VISION2 = 20, HOME_CONFIRM = 8, TRK_MISS_R2 = 10, TRK_EXPIRE = 10, OBJ_FREE_R2 = 10, CUT_TMAX = 30,
            JAIL_ROUNDS = 25, DEFENDERS = 3, ESC8 = 8;
    /* --track columns. One row per finished post-setup trip (a carrier's pickup to its CAPTURE / DROP / DIED, as --defense),
     * per tracked team ("us"), plus one kind=game row per tracked team (round = last round, outcome WON/LOST, symOk).
     *  team            the tracked side; kind = their (the other team carries one of our flags) | own (we carry) | game
     *  round, flag, first, outcome, tripRounds   as --defense (first = picked up from its home tile); meanChasers20 as
     *                  --defense (robots of the non-carrying team within dist2 20, mean over carried rounds) but 4 decimals,
     *                  which classifies exactly against 0.5 and 1 (n <= 2000 rounds); chaserSum / chaserRounds = the exact
     *                  numerator and denominator, so any rounding (e.g. --defense's 1 decimal) can be reproduced
     *  defNear20       robots of the non-carrying team within dist2 20 of the flag at the pickup; defDied10 = how many of
     *                  those died (were jailed) within 10 rounds of the pickup
     *  their rows only:
     *  unseenRounds    carried rounds (end-of-round states, pickup round included) with none of ours within dist2 20
     *  predErr         median Chebyshev error of the 2.4 prediction P(now) over ALL unseen rounds (blank only when there is
     *                  none); while the belief is HOME, P(now) is the home tile (the bot has not noticed the trip).
     *                  predErrSym = the same with destinations from Sym.best() of our slot 16. predN (diagnostic) = how
     *                  many of those rounds had a belief away from HOME
     *  destHit         CAPTURE only: the destination the belief held for most rounds from tKnow on, counting only rounds
     *                  with a live 2.4 state (CARRIED, MISSING, DROPPED: the states responders act on; ties: the later), is
     *                  the zone the carrier captured in; destHitSym = the same under Sym.best(); blank without such a round
     *                  (premise.py counts blank as a miss)
     *  tKnow           rounds after the pickup until one of ours is within dist2 20 of the carrier (tKnowBy = sight), or of
     *                  the flag's home tile while the 2.4 belief was HOME at the start of that round, so that the sensing
     *                  fires MISSING (tKnowBy = home); blank = never. An empty home tile while the belief is already away
     *                  from HOME (a re-grab from a drop tile; a stale LOST/GONE track) carries no news about this trip.
     *                  tKnowState = the 2.4 state then (CARRIED or MISSING by construction); tKnowLive = 1 when it was live
     *  reachAll        at tKnow, our robots with idx >= 3 (the defenders never bid) with t <= 30 by the 2.5 rule on that
     *                  belief; alive from their tile, jailed from the spawn centre nearest Q plus 25 - (round - death)
     *  reachFree       those of them with no enemy within dist2 10, plus the jailed ones; reachInFight = (all - free) / all
     *  escorts8        at tKnow, the carrier's team-mates within dist2 8 of it
     *  own rows only:
     *  ownEscorts20    mean of our robots within dist2 20 of our carrier per carried round
     *  convoyReachFree at the end of the pickup round (every trip, also one that ends in that round): ours alive then
     *                  within Chebyshev 10 of the carrier's tile, outside dist2 20, no enemy within dist2 10
     *  symOk           as --capabilities, for the tracked team */
    static final String TRACK_COLS = "team,kind,round,flag,first,outcome,tripRounds,meanChasers20,chaserSum,chaserRounds,defNear20,defDied10,"
            + "unseenRounds,predN,predErr,predErrSym,destHit,destHitSym,tKnow,tKnowBy,tKnowState,tKnowLive,reachAll,reachFree,reachInFight,"
            + "escorts8,ownEscorts20,convoyReachFree,symOk";
    static final int N_TRACK = TRACK_COLS.split(",").length, N_THEIR = 14;   // N_THEIR: unseenRounds..escorts8
    static int[][] comm = new int[3][64];
    static boolean commSeen, commMode, commHeader, trackMode, trackLog, execDone, execBroken;
    static int[] sym250 = {-1, -1, -1}, psym250 = {0, 0, 0};
    static int[] kEfStaleCarry = new int[3], kEfStaleLoc = new int[3];
    static int[] kAlertWrites = new int[3], kAlertNoThreat = new int[3], kMaxPark = new int[3];
    static Map<Integer, Integer> parkRun = new HashMap<>();
    static int[] symDecided = {-1, -1, -1}, symWrongT = {0, 0, 0}, correctSymsCache = {-2, -2, -2};
    /** Symmetries (bitmask ROT 1, FX 2, FY 4) that map team t's spawn centres exactly onto the other team's; -1 unknown. */
    static int correctSyms(int t) {
        if (correctSymsCache[t] != -2) return correctSymsCache[t];
        if (centres[t] == null || centres[3 - t] == null) return -1;
        int ok = 0; for (int s = 1; s <= 4; s <<= 1) if (maskOk(t, s) == 1) ok |= s;
        return correctSymsCache[t] = ok;
    }
    static boolean[] trkSlots = new boolean[3];
    static int commStored;
    static int commFrom = -1, commTo = -1;
    static Map<Integer, Integer> execIdx = new HashMap<>(), execTeam = new HashMap<>(), lastSpawn = new HashMap<>(), deathRound = new HashMap<>();
    static boolean[] hasCap = new boolean[3];
    static Set<Integer> capturedFlags = new HashSet<>();
    static int[][][] centres = new int[3][][];
    static int[] kEnemyCarried = new int[3], kEnemyUnseen = new int[3], kEnemyTrips = new int[3], kUnopposed = new int[3],
            kLong25 = new int[3], kLongCaps25 = new int[3], kLoneDeaths = new int[3], kTrickleDeaths = new int[3];
    static Map<Integer, Belief> bel = new HashMap<>(), belSym = new HashMap<>();   // flag id -> belief of the flag's team
    static Map<Integer, TripX> tripX = new HashMap<>();                             // holder -> open trip
    static List<TripX> tripDone = new ArrayList<>(), witnessWatch = new ArrayList<>(), startedNow = new ArrayList<>();

    static boolean tracked(int t) { return logTeam == 0 || logTeam == t; }
    static int cheb(int ax, int ay, int bx, int by) { return Math.max(Math.abs(ax - bx), Math.abs(ay - by)); }
    static int d2(int ax, int ay, int bx, int by) { int dx = ax - bx, dy = ay - by; return dx * dx + dy * dy; }
    /** movesDone(age, class) = age/2, age*5/6 or age (carrier move cooldown 20, 12 with CAPTURING, 10 for a relay). */
    static int movesDone(int age, int cls) { if (age <= 0) return 0; return cls == 2 ? age : cls == 1 ? age * 5 / 6 : age / 2; }
    /** ceil(n / v): rounds the carrier needs for n moves. */
    static int roundsFor(int n, int cls) { return cls == 2 ? n : cls == 1 ? (6 * n + 4) / 5 : 2 * n; }
    /** step(L, D, n): n greedy 8-way steps from L toward D, each axis capped at its distance. */
    static int[] step(int lx, int ly, int dx, int dy, int n) {
        return new int[]{lx + Integer.signum(dx - lx) * Math.min(Math.abs(dx - lx), n), ly + Integer.signum(dy - ly) * Math.min(Math.abs(dy - ly), n)};
    }
    /** P = step(L, D, min(N, movesDone(age))), N = cheb(L, D) - 1: stops on the zone tile next to the centre D. */
    static int[] predictFrom(int lx, int ly, int dx, int dy, int cls, int age) {
        return step(lx, ly, dx, dy, Math.min(Math.max(0, cheb(lx, ly, dx, dy) - 1), movesDone(age, cls)));
    }
    /** Tie-free order for the nearest-centre choices (2.9 Obj.nearestDet): equal distances go to the lower x, then the
     *  lower y, so the choice never depends on array order (the true centres and the Sym.best() images list the same
     *  centres in different orders when centres sit on a symmetry axis). */
    static boolean better(int d, int[] c, int bd, int[] best) {
        return best == null || d < bd || (d == bd && (c[0] < best[0] || (c[0] == best[0] && c[1] < best[1])));
    }
    static int[] nearestOf(int[][] cs, int x, int y) {
        int[] best = null; int bd = Integer.MAX_VALUE;
        for (int[] c : cs) { int d = d2(x, y, c[0], c[1]); if (better(d, c, bd, best)) { bd = d; best = c; } }
        return best;
    }
    /** 2.5 step 4, carried flag: first n (from movesDone(age) to N, step max(1, N/12)) with tauD <= tauC gives t = tauD;
     *  -1 when none, or when that t exceeds the CUT cap 30. jailLeft < 0: alive at m; else jailed, from the spawn centre
     *  nearest Q plus the jail time left. */
    static int interceptCarried(int lx, int ly, int dx, int dy, int cls, int age, int mx, int my, int jailLeft, int[][] spawnCentres) {
        int nN = cheb(lx, ly, dx, dy) - 1, s = Math.max(1, nN / 12);
        for (int n = movesDone(age, cls); n <= nN; n += s) {
            int[] q = step(lx, ly, dx, dy, n);
            int tc = roundsFor(n, cls) - age;
            int[] me = jailLeft >= 0 ? nearestOf(spawnCentres, q[0], q[1]) : new int[]{mx, my};
            int td = cheb(me[0], me[1], q[0], q[1]) * 6 / 5 + 1 + Math.max(0, jailLeft);
            if (td <= tc) return td <= CUT_TMAX ? td : -1;
        }
        return -1;
    }
    /** 2.5 step 4, dropped inside the return window: t = cheb(me, L), a candidate only if t <= the window left. */
    static int interceptDropped(int lx, int ly, int left, int mx, int my, int jailLeft, int[][] spawnCentres) {
        int[] me = jailLeft >= 0 ? nearestOf(spawnCentres, lx, ly) : new int[]{mx, my};
        int t = cheb(me[0], me[1], lx, ly) + Math.max(0, jailLeft);
        return t <= left && t <= CUT_TMAX ? t : -1;
    }

    static int symBest(int mask) { if (mask <= 0) mask = 7; return (mask & 1) != 0 ? 1 : (mask & 2) != 0 ? 2 : (mask & 4) != 0 ? 4 : 1; }
    static int[] image(int x, int y, int s) { return s == 1 ? new int[]{W - 1 - x, H - 1 - y} : s == 2 ? new int[]{W - 1 - x, y} : new int[]{x, H - 1 - y}; }
    static int[][] imagesUnder(int t, int s) { int[][] r = new int[centres[t].length][]; for (int i = 0; i < r.length; i++) r[i] = image(centres[t][i][0], centres[t][i][1], s); return r; }
    /** symOk: Sym.best() of team t's slot 16 at r250 maps t's centres onto the other team's; -1 without a stored array. */
    static int symOk(int t) { return sym250[t] < 0 ? -1 : maskOk(t, sym250[t]); }
    /** psymOk: the same with slot 24 (PSYM; 0 = unwritten -> slot 16); -1 unless the tracker slots were ever written. */
    static int psymOk(int t) { return sym250[t] < 0 || !trkSlots[t] ? -1 : maskOk(t, psym250[t] != 0 ? psym250[t] & 7 : sym250[t]); }
    static int maskOk(int t, int mask) {
        if (centres[t] == null || centres[3 - t] == null) return -1;
        Set<Integer> want = new HashSet<>(), got = new HashSet<>();
        for (int[] c : centres[3 - t]) want.add(c[0] + c[1] * W);
        for (int[] c : imagesUnder(t, symBest(mask))) got.add(c[0] + c[1] * W);
        return want.equals(got) ? 1 : 0;
    }

    static class Belief {
        final int us, flag; final boolean sym;
        int state = S_HOME, startState = S_HOME, lx, ly, r0 = 200, dx = -1, dy = -1, cls, misses, missRound = -1, homeConf = 200;
        boolean inferred;
        Belief(int us, int flag, boolean sym) { this.us = us; this.flag = flag; this.sym = sym; int[] h = flagHome.get(flag); lx = h[0]; ly = h[1]; }
    }
    static int window(int carrierTeam) { return hasCap[carrierTeam] ? 25 : 4; }
    static int[][] dests(Belief b) { return b.sym ? imagesUnder(b.us, symBest(commSeen ? comm[b.us][16] : 0)) : centres[3 - b.us]; }
    static void setNearest(Belief b, int x, int y) { int[] c = nearestOf(dests(b), x, y); b.dx = c[0]; b.dy = c[1]; }
    /** End-of-round model of the return window: a flag first seen dropped at the end of round r0 lies there at the end of
     *  rounds r0 .. r0+win-1 and is back home at the end of r0+win (replays: PLACE r1060 -> home PLACE r1064). */
    static boolean inDropWindow(int r0, int rn, int win) { return rn < r0 + win; }
    /** The belief's P(now): HOME -> home; DROPPED inside the window -> the drop tile; a presumed re-grab after it -> the
     *  2.4 formula from the window end; every other state -> the formula from (L, r0). */
    static int[] predict(Belief b, int rn) {
        if (b.state == S_HOME || b.dx < 0) return new int[]{b.lx, b.ly};
        if (b.state == S_DROPPED) {
            int win = window(3 - b.us), we = b.r0 + win;
            return inDropWindow(b.r0, rn, win) ? new int[]{b.lx, b.ly} : predictFrom(b.lx, b.ly, b.dx, b.dy, b.cls, rn - we);
        }
        return predictFrom(b.lx, b.ly, b.dx, b.dy, b.cls, rn - b.r0);
    }
    static boolean near(List<int[]> al, int x, int y, int r2, int skipId) {
        for (int[] a : al) if (a[2] != skipId && d2(a[0], a[1], x, y) <= r2) return true;
        return false;
    }
    static int count(List<int[]> al, int x, int y, int r2, int skipId) {
        int n = 0; for (int[] a : al) if (a[2] != skipId && d2(a[0], a[1], x, y) <= r2) n++;
        return n;
    }

    /** One round of the 2.4 rules for flag b.flag. fl = the flag's true tile (the carrier's when carried), null once captured. */
    static void beliefTick(Belief b, int rn, int[] fl, boolean carried, List<int[]> ours) {
        int them = 3 - b.us, win = window(them);
        int[] home = flagHome.get(b.flag);
        b.startState = b.state;
        // round start: GONE once the predicted arrival plus TRK_EXPIRE has passed
        boolean predicting = b.state == S_CARRIED || b.state == S_MISSING || b.state == S_LOST || (b.state == S_DROPPED && !inDropWindow(b.r0, rn, win));
        if (predicting && b.dx >= 0) {
            int base = b.state == S_DROPPED ? b.r0 + win : b.r0;
            if (rn > base + roundsFor(Math.max(0, cheb(b.lx, b.ly, b.dx, b.dy) - 1), b.cls) + TRK_EXPIRE) b.state = S_GONE;
        }
        if (fl != null && near(ours, fl[0], fl[1], VISION2, -1)) {   // a direct sighting
            if (carried) {
                if ((b.state == S_CARRIED || b.state == S_LOST) && !b.inferred && rn - b.r0 >= 4)   // speed over a gap of 4+ rounds
                    b.cls = cheb(b.lx, b.ly, fl[0], fl[1]) >= 0.9 * (rn - b.r0) ? 2 : hasCap[them] ? 1 : 0;
                else if (b.state == S_HOME || b.state == S_GONE) b.cls = hasCap[them] ? 1 : 0;
                // destination: the enemy centre nearest the sighting, rejecting centres it moved away from since L
                int[][] ds = dests(b); int[] best = null; int bd = Integer.MAX_VALUE;
                boolean ref = b.state != S_GONE && (b.lx != fl[0] || b.ly != fl[1]);
                for (int pass = 0; pass < 2 && best == null; pass++)
                    for (int[] c : ds) {
                        int d = d2(fl[0], fl[1], c[0], c[1]);
                        if (pass == 0 && ref && d > d2(b.lx, b.ly, c[0], c[1])) continue;
                        if (better(d, c, bd, best)) { bd = d; best = c; }
                    }
                b.dx = best[0]; b.dy = best[1];
                b.state = S_CARRIED; b.lx = fl[0]; b.ly = fl[1]; b.r0 = rn; b.inferred = false; b.misses = 0;
            } else if (home != null && fl[0] == home[0] && fl[1] == home[1]) {
                if (b.state != S_HOME) { b.state = S_HOME; b.homeConf = rn; b.dx = b.dy = -1; }
                else if (rn - b.homeConf >= HOME_CONFIRM) b.homeConf = rn;   // the home witness re-stamps every 8 rounds
                b.lx = home[0]; b.ly = home[1]; b.r0 = b.homeConf; b.inferred = false; b.misses = 0;
            } else {
                if (b.state != S_DROPPED || b.lx != fl[0] || b.ly != fl[1]) { b.state = S_DROPPED; b.lx = fl[0]; b.ly = fl[1]; b.r0 = rn; }
                if (b.dx < 0) setNearest(b, fl[0], fl[1]);
                b.inferred = false; b.misses = 0;
            }
            return;
        }
        boolean homeSensed = home != null && near(ours, home[0], home[1], VISION2, -1);   // and the flag is not there
        if (homeSensed && b.state == S_HOME) {                       // MISSING: departure kept at the last confirmation
            b.state = S_MISSING; b.inferred = true; b.lx = home[0]; b.ly = home[1]; b.r0 = b.homeConf; b.misses = 0;
            b.cls = hasCap[them] ? 1 : 0; setNearest(b, home[0], home[1]);
        } else if (b.state == S_DROPPED) {
            if (!inDropWindow(b.r0, rn, win)) { if (homeSensed) { b.state = S_CARRIED; b.inferred = true; b.r0 = b.r0 + win; b.misses = 0; } }
            else if (near(ours, b.lx, b.ly, VISION2, -1)) { b.state = S_CARRIED; b.inferred = true; b.r0 = rn - 1; b.misses = 0; }
        }
        // negative sightings: one of ours within dist2 10 of P sees no flag; at 1 miss switch D, at 3 LOST
        boolean neg = b.state == S_CARRIED || b.state == S_MISSING || (b.state == S_DROPPED && !inDropWindow(b.r0, rn, win));
        if (neg && b.dx >= 0 && b.missRound != rn) {
            int[] p = predict(b, rn);
            if (near(ours, p[0], p[1], TRK_MISS_R2, -1)) {
                b.misses++; b.missRound = rn;
                if (b.misses == 1) {   // the other enemy centre most consistent with L: the nearest to L
                    int[] best = null; int bd = Integer.MAX_VALUE;
                    for (int[] c : dests(b)) { if (c[0] == b.dx && c[1] == b.dy) continue; int d = d2(b.lx, b.ly, c[0], c[1]); if (better(d, c, bd, best)) { bd = d; best = c; } }
                    if (best != null) { b.dx = best[0]; b.dy = best[1]; }
                }
                if (b.misses >= 3) b.state = S_LOST;
            }
        }
    }

    static class TripX {
        int holder, ct, flag, start, first, defNear20, defDied10, tripRounds, zone = -1, chaserSum, chaserRounds;
        String outcome = ""; double chasers;
        Set<Integer> witnesses = new HashSet<>();
        int[] pickLoc;
        int ticks, unseen, predN, tKnow = -1, tKnowLive = -1, reachAll, reachFree, escorts8; double reachInFight = Double.NaN;
        String tKnowState = "", tKnowBy = "";
        List<Integer> err = new ArrayList<>(), errSym = new ArrayList<>();
        Map<Integer, int[]> votes = new HashMap<>(), votesSym = new HashMap<>();
        long escSum; int escN, convoy = -1;
    }
    static void startTrack(int rn, int id, int t, int flag, boolean first, int[] at) {
        TripX x = new TripX(); x.holder = id; x.ct = t; x.flag = flag; x.start = rn; x.first = first ? 1 : 0;
        int[] pl = lastLoc.get(id); x.pickLoc = pl != null ? pl : at;   // the carrier's tile at the end of the pickup round
        if (at != null) for (Map.Entry<Integer, int[]> re : lastLoc.entrySet())
            if (team.getOrDefault(re.getKey(), 0) == 3 - t && d2(re.getValue()[0], re.getValue()[1], at[0], at[1]) <= 20) x.witnesses.add(re.getKey());
        tripX.put(id, x); witnessWatch.add(x); startedNow.add(x);
    }
    static boolean live(Belief b) { return b != null && b.dx >= 0 && (b.state == S_CARRIED || b.state == S_MISSING || b.state == S_DROPPED); }
    /** destHit votes: only rounds with a live 2.4 state (the states responders act on, 2.5 step 1). */
    static void vote(Map<Integer, int[]> v, Belief b, int rn) {
        if (!live(b)) return;
        int[] e = v.computeIfAbsent(b.dx + b.dy * W, k -> new int[2]); e[0]++; e[1] = rn;
    }
    static int majority(Map<Integer, int[]> v) {   // most rounds; ties -> the more recent
        int best = -1, bc = -1, br = -1;
        for (Map.Entry<Integer, int[]> e : v.entrySet()) { int[] c = e.getValue(); if (c[0] > bc || (c[0] == bc && c[1] > br)) { best = e.getKey(); bc = c[0]; br = c[1]; } }
        return best;
    }

    /** End of round rn (after every event of the round): alive robots per team as {x, y, id}. */
    @SuppressWarnings("unchecked")
    static List<int[]>[] aliveLists() {
        List<int[]>[] al = new List[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
        for (Map.Entry<Integer, int[]> e : lastLoc.entrySet()) {
            int t = team.getOrDefault(e.getKey(), 0);
            if (t > 0) al[t].add(new int[]{e.getValue()[0], e.getValue()[1], e.getKey()});
        }
        return al;
    }
    static void tick(int rn) {
        if (rn <= 200) return;
        List<int[]>[] al = aliveLists();
        if (capMode) for (Map.Entry<Integer, Integer> ce : carrying.entrySet()) {
            int h = ce.getKey(), ct = team.getOrDefault(h, 0); int[] c = lastLoc.get(h);
            if (ct == 0 || c == null) continue;
            kEnemyCarried[3 - ct]++;
            if (!near(al[3 - ct], c[0], c[1], VISION2, -1)) kEnemyUnseen[3 - ct]++;
        }
        if (chainsOn()) chainTick(rn, al);
        if (!s0a()) return;
        Map<Integer, Integer> holderOf = new HashMap<>();
        for (Map.Entry<Integer, Integer> ce : carrying.entrySet()) holderOf.put(ce.getValue(), ce.getKey());
        for (Map.Entry<Integer, Integer> fe : flagTeam.entrySet()) {
            int f = fe.getKey(), us = fe.getValue();
            if (!tracked(us) || !flagHome.containsKey(f)) continue;
            int[] fl; boolean carried = false;
            if (capturedFlags.contains(f)) fl = null;
            else if (holderOf.containsKey(f)) { fl = lastLoc.get(holderOf.get(f)); carried = fl != null; }
            else fl = flagLoc.get(f);
            Belief b = bel.computeIfAbsent(f, k -> new Belief(us, k, false));
            beliefTick(b, rn, fl, carried, al[us]);
            beliefTick(belSym.computeIfAbsent(f, k -> new Belief(us, k, true)), rn, fl, carried, al[us]);
            if (trackLog && (b.state != S_HOME || fl == null || carried)) {
                int[] p = predict(b, rn);
                out.printf("# r%d %s flag=%d %s%s L=(%d,%d) r0=%d D=(%d,%d) cls=%d miss=%d P=(%d,%d) truth=%s%s err=%s%n", rn, tname(us), f, SNAME[b.state],
                        b.inferred ? "*" : "", b.lx, b.ly, b.r0, b.dx, b.dy, b.cls, b.misses, p[0], p[1],
                        fl == null ? "gone" : "(" + fl[0] + "," + fl[1] + ")", carried ? (near(al[us], fl[0], fl[1], VISION2, -1) ? " carried,seen" : " carried,unseen") : "",
                        fl == null ? "-" : String.valueOf(cheb(p[0], p[1], fl[0], fl[1])));
            }
        }
        for (TripX x : startedNow) if (tracked(x.ct) && x.pickLoc != null) x.convoy = convoyAt(x, x.pickLoc, al);   // also trips that ended this round
        startedNow.clear();
        for (TripX x : tripX.values()) tripTick(x, rn, al);
    }
    /** convoyReachFree: the carrier's team-mates alive at the end of the pickup round within Chebyshev 10 of its tile, outside
     *  dist2 20, with no enemy within dist2 10. */
    static int convoyAt(TripX x, int[] c, List<int[]>[] al) {
        int n = 0;
        for (int[] a : al[x.ct]) {
            if (a[2] == x.holder) continue;
            if (cheb(a[0], a[1], c[0], c[1]) <= 10 && d2(a[0], a[1], c[0], c[1]) > 20 && !near(al[3 - x.ct], a[0], a[1], OBJ_FREE_R2, -1)) n++;
        }
        return n;
    }
    static void tripTick(TripX x, int rn, List<int[]>[] al) {
        Integer f = carrying.get(x.holder); int[] c = lastLoc.get(x.holder);
        if (f == null || c == null) return;
        int ct = x.ct, us = 3 - ct;
        x.ticks++;
        if (tracked(us)) {                 // their trip, seen from the defending side
            boolean seen = near(al[us], c[0], c[1], VISION2, -1);
            Belief b = bel.get(f), bs = belSym.get(f);
            int[] home = flagHome.get(f);
            if (!seen) {                   // every unseen round is scored; a HOME belief predicts the home tile
                x.unseen++;
                if (b != null && b.state != S_HOME) x.predN++;
                int[] p = b != null ? predict(b, rn) : home, ps = bs != null ? predict(bs, rn) : home;
                if (p != null) x.err.add(cheb(p[0], p[1], c[0], c[1]));
                if (ps != null) x.errSym.add(cheb(ps[0], ps[1], c[0], c[1]));
            }
            if (x.tKnow < 0) {             // home counts only when it is news: the belief was HOME, so the sensing fired MISSING
                boolean byHome = !seen && b != null && b.startState == S_HOME && home != null && near(al[us], home[0], home[1], VISION2, -1);
                if (seen || byHome) knowAt(x, rn, b, c, seen, al);
            }
            if (x.tKnow >= 0) { vote(x.votes, b, rn); vote(x.votesSym, bs, rn); }
        }
        if (tracked(ct)) {                 // our trip, seen from the carrying side
            x.escSum += count(al[ct], c[0], c[1], 20, x.holder); x.escN++;
        }
    }
    /** tKnow: reach counts by the 2.5 rule on the belief of that round. By construction that belief is live (a sighting
     *  sets CARRIED, the home criterion requires the HOME -> MISSING transition); if it is not (no belief for the flag), a
     *  fresh one is used, CARRIED at the carrier or MISSING with the true departure, and tKnowLive = 0. */
    static void knowAt(TripX x, int rn, Belief b, int[] c, boolean seen, List<int[]>[] al) {
        int us = 3 - x.ct;
        x.tKnow = rn - x.start; x.tKnowBy = seen ? "sight" : "home";
        boolean live = live(b);
        x.tKnowState = b == null ? "" : SNAME[b.state]; x.tKnowLive = live ? 1 : 0;
        Belief rb = b;
        if (!live) {
            rb = new Belief(us, x.flag, false);
            if (seen) { rb.state = S_CARRIED; rb.lx = c[0]; rb.ly = c[1]; rb.r0 = rn; }
            else { rb.state = S_MISSING; rb.r0 = x.start; }
            rb.cls = hasCap[x.ct] ? 1 : 0; setNearest(rb, rb.lx, rb.ly);
        }
        int win = window(x.ct), all = 0, free = 0;
        boolean byExec = !execTeam.isEmpty() && !execBroken;
        Map<Integer, Integer> roster = byExec ? execTeam : team;
        for (Map.Entry<Integer, Integer> e : roster.entrySet()) {
            int id = e.getKey();
            if (e.getValue() != us || (byExec && execIdx.getOrDefault(id, DEFENDERS) < DEFENDERS)) continue;   // defenders never bid
            if (carrying.containsKey(id)) continue;                                                            // nor do carriers (2.5)
            int[] l = lastLoc.get(id);
            int jail = l != null ? -1 : Math.max(0, JAIL_ROUNDS - (rn - deathRound.getOrDefault(id, -100000)));
            int mx = l != null ? l[0] : 0, my = l != null ? l[1] : 0, t;
            if (rb.state == S_DROPPED && inDropWindow(rb.r0, rn, win)) t = interceptDropped(rb.lx, rb.ly, rb.r0 + win - rn, mx, my, jail, centres[us]);
            else {
                int r0 = rb.state == S_DROPPED ? rb.r0 + win : rb.r0;
                t = interceptCarried(rb.lx, rb.ly, rb.dx, rb.dy, rb.cls, rn - r0, mx, my, jail, centres[us]);
            }
            if (t < 0) continue;
            all++;
            if (l == null || !near(al[x.ct], l[0], l[1], OBJ_FREE_R2, -1)) free++;
        }
        x.reachAll = all; x.reachFree = free; x.reachInFight = all > 0 ? (all - free) / (double) all : Double.NaN;
        x.escorts8 = count(al[x.ct], c[0], c[1], ESC8, x.holder);
    }
    static String median(List<Integer> v) {
        if (v.isEmpty()) return "";
        List<Integer> s = new ArrayList<>(v); Collections.sort(s); int n = s.size();
        double m = n % 2 == 1 ? (double) s.get(n / 2) : (s.get(n / 2 - 1) + s.get(n / 2)) / 2.0;
        return String.format("%.1f", m);
    }
    static void printTrack() {
        out.println(TRACK_COLS);
        List<TripX> rows = new ArrayList<>(tripDone);
        rows.sort((a, b) -> a.start != b.start ? Integer.compare(a.start, b.start) : Integer.compare(a.holder, b.holder));
        for (int us = 1; us <= 2; us++) {
            if (!tracked(us)) continue;
            int so = symOk(us);
            StringBuilder g = new StringBuilder(tname(us) + ",game," + totalRounds + ",,," + (winner == us ? "WON" : "LOST"));
            for (int k = 6; k < N_TRACK - 1; k++) g.append(',');
            out.println(g.append(',').append(so >= 0 ? String.valueOf(so) : ""));
            for (TripX x : rows) {
                boolean their = x.ct == 3 - us;
                StringBuilder sb = new StringBuilder();
                sb.append(tname(us)).append(',').append(their ? "their" : "own").append(',').append(x.start).append(',').append(x.flag).append(',')
                  .append(x.first).append(',').append(x.outcome).append(',').append(x.tripRounds).append(',').append(String.format("%.4f", x.chasers)).append(',')
                  .append(x.chaserSum).append(',').append(x.chaserRounds).append(',').append(x.defNear20).append(',').append(x.defDied10).append(',');
                if (their) {
                    boolean capd = x.outcome.equals("CAPTURE");
                    int m = majority(x.votes), ms = majority(x.votesSym);
                    sb.append(x.unseen).append(',').append(x.predN).append(',').append(median(x.err)).append(',').append(median(x.errSym)).append(',')
                      .append(capd && m >= 0 ? (m == x.zone ? "1" : "0") : "").append(',').append(capd && ms >= 0 ? (ms == x.zone ? "1" : "0") : "").append(',');
                    if (x.tKnow >= 0) sb.append(x.tKnow).append(',').append(x.tKnowBy).append(',').append(x.tKnowState).append(',').append(x.tKnowLive).append(',')
                      .append(x.reachAll).append(',').append(x.reachFree).append(',').append(Double.isNaN(x.reachInFight) ? "" : String.format("%.2f", x.reachInFight))
                      .append(',').append(x.escorts8).append(',');
                    else sb.append(",,,,,,,,");
                    sb.append(",,");
                } else {
                    for (int k = 0; k < N_THEIR; k++) sb.append(',');   // the their-trip columns unseenRounds..escorts8
                    sb.append(x.escN > 0 ? String.format("%.2f", (double) x.escSum / x.escN) : "").append(',').append(x.convoy >= 0 ? String.valueOf(x.convoy) : "").append(',');
                }
                sb.append(so >= 0 ? String.valueOf(so) : "");
                out.println(sb);
            }
        }
    }

    /** --calc: the prediction and intercept rules on synthetic inputs (tools/test_tools.py). */
    static void calc() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        ChainTally tally = new ChainTally();   // X and V add to one running tally
        for (String line; (line = in.readLine()) != null; ) {
            String[] p = line.trim().split("\\s+");
            if (p.length == 0 || p[0].isEmpty()) continue;
            if (p[0].equals("X")) {            // the outcome is a word, the rest are numbers
                List<int[]> rs = new ArrayList<>();
                for (int i = 3; i + 1 < p.length; i += 2) rs.add(new int[]{Integer.parseInt(p[i]), Integer.parseInt(p[i + 1])});
                tally.add(Integer.parseInt(p[1]), p[2], rs); out.println(tally.cols()); continue;
            }
            int[] a = new int[p.length - 1];
            for (int i = 1; i < p.length; i++) a[i - 1] = Integer.parseInt(p[i]);
            switch (p[0]) {
                case "P": { int[] q = predictFrom(a[0], a[1], a[2], a[3], a[4], a[5]); out.println(q[0] + " " + q[1]); break; }
                case "T": out.println(interceptCarried(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], -1, null)); break;
                case "J": {
                    int[][] cs = new int[(a.length - 7) / 2][];
                    for (int i = 0; i < cs.length; i++) cs[i] = new int[]{a[7 + 2 * i], a[8 + 2 * i]};
                    out.println(interceptCarried(a[0], a[1], a[2], a[3], a[4], a[5], 0, 0, a[6], cs)); break;
                }
                case "W": out.println(interceptDropped(a[0], a[1], a[2], a[3], a[4], -1, null)); break;
                case "N": {
                    int[][] cs = new int[(a.length - 2) / 2][];
                    for (int i = 0; i < cs.length; i++) cs[i] = new int[]{a[2 + 2 * i], a[3 + 2 * i]};
                    int[] c = nearestOf(cs, a[0], a[1]); out.println(c[0] + " " + c[1]); break;
                }
                case "D": out.println(inDropWindow(a[0], a[2], a[1]) ? 1 : 0); break;
                case "C": { int[] q = chainPoint(a[0], a[1], a[2], a[3], a[4]); out.println(q == null ? "-" : q[0] + " " + q[1]); break; }
                case "V": tally.dive(a[0]); out.println(tally.cols()); break;
                default: out.println("?");
            }
        }
    }

    // =================================================================================================================
    // g4contact census and premise check D0 (convoy plan sections 5 and 6). One chain per enemy first grab of a flag (the
    // firstGrabs test) until its CAPTURE, its return home (RETURN; also a re-grab from the home tile, chainStart) or the game
    // end (OPEN); the flag's tile at the end of a round is its carrier's while carried, else where it lies. Every count uses
    // the robots alive at the end of the round (aliveLists), as the S0a tracker does.
    // =================================================================================================================
    static final int U12 = 12, NC_WINDOW = 10, CONTACT_R2 = 20, SCREEN_R2 = 100, LEAK_R2 = 144;
    static final String CHAIN_COLS = "noContact10u12,contact20u12,screened20u12,chainsU12,chains12p,capRateU12,capRate12p,diveTurns,diveLeak12,diveNoChain";
    static class Chain {
        int flag, dt, grab, g0 = -1;               // dt = the defending team (the flag's); g0 -1 = not yet set
        String outcome = "OPEN";
        List<int[]> rounds = new ArrayList<>();    // t = 1..T: {ours20, ours100} at the end of each open round
        List<String> rd = new ArrayList<>();       // --recall-d0 rows (t = 0, 10, 20), printed with the outcome
        int[] rdc = new int[4];                    // --recall-d0: kills, deaths, stun victims, enemy stun victims near the flag
        // --contact-d0 only
        int seenT0, enMax = -1, live, unseen, noPoint;
        int[] elig = new int[4];                   // elig12, elig10, elig8, elig12r144
        @SuppressWarnings("unchecked") List<Integer>[] err = new List[D0_HOLD + 1];   // by age 1..D0_HOLD
        Chain() { for (int a = 0; a <= D0_HOLD; a++) err[a] = new ArrayList<>(); }
    }
    static boolean d0Mode;
    static String diveNote = "dive";                       // --dive-note
    static boolean chainsOn() { return capMode || d0Mode || rdMode; }
    static Map<Integer, Chain> chains = new TreeMap<>();   // flag id -> its open chain (TreeMap: a fixed order for the diver ties)
    static List<Chain> chainsDone = new ArrayList<>();
    static ChainTally[] chainTally = {null, new ChainTally(), new ChainTally()};   // by defending team
    static List<int[]> divers = new ArrayList<>();          // this round's dive turns: {team, x, y}
    static Map<Integer, int[]> deathTile = new HashMap<>(); // this round's deaths: id -> tile

    /** The census tally of one team's chains (the other team's first grabs of its flags) and of its dive turns. Pure: the
     *  census and --calc X / V feed it alike, and tools/test_tools.py checks it against a python reference. */
    static class ChainTally {
        int u12, p12, closedU12, closedP12, capU12, capP12, ncChains, ncNone, flagRounds, contact, screened, dives, diveLeak, diveNone;
        /** One finished chain: its grab group g0, outcome (CAPTURE, RETURN, OPEN) and {ours20, ours100} for t = 1..T. */
        void add(int g0, String outcome, List<int[]> rs) {
            boolean small = g0 < U12, closed = !outcome.equals("OPEN"), capd = outcome.equals("CAPTURE");
            if (small) { u12++; if (closed) closedU12++; if (capd) capU12++; }
            else { p12++; if (closed) closedP12++; if (capd) capP12++; }
            if (!small) return;
            if (!rs.isEmpty()) { ncChains++; if (noContact(rs)) ncNone++; }   // T = 0: no open round to judge
            for (int[] o : rs) { flagRounds++; if (o[0] >= 1) contact++; else if (o[1] >= 1) screened++; }
        }
        /** One dive turn: g0 of the chain it was resolved to, -1 = no chain within dist2 144. */
        void dive(int g0) { dives++; if (g0 < 0) diveNone++; else if (g0 >= U12) diveLeak++; }
        /** The CHAIN_COLS fields. */
        String cols() {
            return share(ncNone, ncChains) + "," + share(contact, flagRounds) + "," + share(screened, flagRounds)
                    + "," + (u12 + p12 > 0 ? u12 + "," + p12 : ",") + "," + share(capU12, closedU12) + "," + share(capP12, closedP12)
                    + "," + (u12 > 0 ? String.valueOf(dives) : "") + "," + share(diveLeak, dives) + "," + share(diveNone, dives);
        }
    }
    static String share(int n, int d) { return d > 0 ? String.format("%.3f", (double) n / d) : ""; }
    /** The chain's signature test: ours20 = 0 at the end of every round t = 1..min(10, T). */
    static boolean noContact(List<int[]> rs) {
        for (int t = 0; t < rs.size() && t < NC_WINDOW; t++) if (rs.get(t)[0] > 0) return false;
        return true;
    }
    static void chainStart(int rn, int flag, int dt) {
        // a flag dropped on its own home tile and re-grabbed passes the first-grab test while its chain is open: it was home,
        // so that chain ends RETURN here (the reset PLACE_FLAG would have ended it so) and the re-grab opens the next
        if (chains.containsKey(flag)) chainEnd(rn, flag, "RETURN", flagHome.get(flag));
        Chain c = new Chain(); c.flag = flag; c.dt = dt; c.grab = rn;
        chains.put(flag, c);
    }
    /** at: the flag's tile, for g0 when the chain ends in its grab round (before chainTick set it). */
    static void chainEnd(int rn, int flag, String outcome, int[] at) {
        Chain c = chains.remove(flag);
        if (c == null) return;
        if (c.g0 < 0 && at != null) { c.g0 = 0; for (int[] q : nowLoc.values()) if (q[2] == 3 - c.dt && d2(q[0], q[1], at[0], at[1]) <= CONTACT_R2) c.g0++; }
        c.g0 = Math.max(0, c.g0);
        c.outcome = outcome;
        chainTally[c.dt].add(c.g0, outcome, c.rounds);
        chainsDone.add(c);
    }
    static int[] flagTile(int flag, Map<Integer, Integer> holderOf) {
        Integer h = holderOf.get(flag);
        return h != null ? lastLoc.get(h) : flagLoc.get(flag);
    }
    /** End of round rn: g0 of the chains grabbed this round, ours20 / ours100 of the others, the D0 sensor, then this round's
     *  dive turns (after g0, so a chain grabbed this round is a candidate). */
    static void chainTick(int rn, List<int[]>[] al) {
        Map<Integer, Integer> holderOf = new HashMap<>();
        for (Map.Entry<Integer, Integer> ce : carrying.entrySet()) holderOf.put(ce.getValue(), ce.getKey());
        if (d0Mode && rn >= D0_FROM) d0Sense(rn, al, holderOf);
        for (Chain c : chains.values()) {
            int[] f = flagTile(c.flag, holderOf);
            if (f == null) continue;
            if (c.g0 < 0) c.g0 = count(al[3 - c.dt], f[0], f[1], CONTACT_R2, -1);
            if (d0Mode) d0Tick(c, rn, f, al);
            if (rdMode && rn - c.grab <= 20) {
                for (int[] e : rdEv) if (d2(e[2], e[3], f[0], f[1]) <= SCREEN_R2) {
                    if (e[0] == 1) c.rdc[e[1] == c.dt ? 1 : 0]++; else c.rdc[e[1] == c.dt ? 2 : 3] += e[4]; }
                if ((rn - c.grab) % 10 == 0) rdTick(c, rn, f, al);
            }
            if (rn > c.grab) c.rounds.add(new int[]{count(al[c.dt], f[0], f[1], CONTACT_R2, -1), count(al[c.dt], f[0], f[1], SCREEN_R2, -1)});
        }
        rdEv.clear();
        for (int[] d : divers) {
            Chain best = null; int[] bf = null; int bd = Integer.MAX_VALUE;
            for (Chain c : chains.values()) {
                int[] f = c.dt == d[0] ? flagTile(c.flag, holderOf) : null;
                if (f == null) continue;
                int dd = d2(f[0], f[1], d[1], d[2]);
                if (dd <= LEAK_R2 && better(dd, f, bd, bf)) { best = c; bf = f; bd = dd; }
            }
            chainTally[d[0]].dive(best == null ? -1 : best.g0);
        }
        divers.clear();
    }

    /* --contact-d0 columns (convoy plan section 6). The arm's sensor (Duck.contactSight) is replayed on every flag of the
     * tracked team at the end of every round from r204 on (the arm senses only after the r201-203 home re-stamp window):
     * observers = our robots within dist2 20 of the flag; observed en = the max over the observers of the enemies within
     * dist2 20 of both the flag and that observer (senseNearbyRobots(flag, 20, them) returns only tiles the caller can
     * sense; the carrier is included). A sighting off home keeps the high-water mark of en (cap 15) when the track was live
     * (last sighting within D0_HOLD rounds), else starts afresh; a sighting of the flag on its home tile, not carried, ends
     * the track (contactHome). age = rounds since the last sighting; live = a track with age <= D0_HOLD. P = chainPoint(L,
     * D, age) (--calc C) with L = the last sighting and D = the true enemy spawn centre nearest L (ties: lower x, then lower
     * y). Not modelled: the miss rule (CT_MISS), the diver cap and its need > 0 gate.
     *  team            the tracked side (the chain is on its flag)
     *  grab, flag, g0, outcome   the chain as in the census (g0 = the carrier team within dist2 20 of the flag at the end of
     *                  the grab round; outcome CAPTURE / RETURN / OPEN, RETURN including a chain whose flag was dropped on
     *                  its own home tile and re-grabbed before the reset, closed at the re-grab round)
     *  T               the chain's open rounds after the grab (t = 1..T)
     *  seenT0          1 when the sensor saw the flag at the end of the grab round
     *  noContact10     1 when ours20 = 0 at every t = 1..min(10, T), the census's per-chain test (blank: T = 0)
     *  enObsMax        the largest en of a live track over t = 0..T (blank: never a live track)
     *  liveRounds      rounds t >= 1 with a live track; unseenLive: those with age >= 1 (no observer that round)
     *  noPoint         unseen live rounds whose chainPoint is null (the predicted arrival has passed)
     *  elig12, elig10, elig8   eligible rounds at CT_GROUP_MAX 12 / 10 / 8: unseen live rounds with en < 12 / 10 / 8 and
     *                  one of ours within dist2 100 (CT_DIVE_R2) of P; elig12r144: en < 12 and one of ours within dist2 144
     *  err1..err12     the Chebyshev error from P to the flag's true tile on the unseen live rounds of that age, ';'-joined */
    static final int D0_FROM = 204, D0_HOLD = 12, D0_SPEED16 = 9, D0_DIVE_R2 = 100, D0_EN_CAP = 15;   // C.CT_HOLD, C.CT_SPEED16, C.CT_DIVE_R2
    static final int[] D0_GROUP = {12, 10, 8};
    static final String D0_COLS = "team,grab,flag,g0,outcome,T,seenT0,noContact10,enObsMax,liveRounds,unseenLive,noPoint,elig12,elig10,elig8,elig12r144,"
            + "err1,err2,err3,err4,err5,err6,err7,err8,err9,err10,err11,err12";
    static class D0Track { int lx, ly, en, seen = -1; boolean on; }
    static Map<Integer, D0Track> d0 = new HashMap<>();   // flag id -> the arm's track of it (CT[i] and OF_SEEN[i])
    /** The arm's chainPoint(L, D, age): m = age * 9 / 16 tiles; null once m > cheb(L, D) (the predicted arrival has passed);
     *  else step(L, D, min(m, cheb - 1)), L when that is <= 0. */
    static int[] chainPoint(int lx, int ly, int dx, int dy, int age) {
        int m = age * D0_SPEED16 >> 4, n = cheb(lx, ly, dx, dy) - 1;
        if (m > n + 1) return null;
        int s = Math.min(m, n);
        return s <= 0 ? new int[]{lx, ly} : step(lx, ly, dx, dy, s);
    }
    static void d0Sense(int rn, List<int[]>[] al, Map<Integer, Integer> holderOf) {
        for (Map.Entry<Integer, Integer> fe : flagTeam.entrySet()) {
            int f = fe.getKey(), us = fe.getValue();
            if (!tracked(us) || capturedFlags.contains(f)) continue;
            int[] F = flagTile(f, holderOf), home = flagHome.get(f);
            if (F == null) continue;
            int en = -1;                                    // -1: no observer
            for (int[] o : al[us]) {
                if (d2(o[0], o[1], F[0], F[1]) > VISION2) continue;
                int n = 0;
                for (int[] e : al[3 - us]) if (d2(e[0], e[1], F[0], F[1]) <= VISION2 && d2(e[0], e[1], o[0], o[1]) <= VISION2) n++;
                en = Math.max(en, n);
            }
            if (en < 0) continue;
            D0Track k = d0.computeIfAbsent(f, x -> new D0Track());
            // on a home sighting the track ends; else seen (read before this round's write) is the last sighting off home
            if (!holderOf.containsKey(f) && home != null && F[0] == home[0] && F[1] == home[1]) { k.on = false; k.seen = rn; continue; }
            boolean live = k.on && rn - k.seen <= D0_HOLD;
            k.en = Math.min(D0_EN_CAP, live ? Math.max(k.en, en) : en); k.lx = F[0]; k.ly = F[1]; k.on = true; k.seen = rn;
        }
    }
    static void d0Tick(Chain c, int rn, int[] F, List<int[]>[] al) {
        D0Track k = d0.get(c.flag);
        int age = k != null && k.on ? rn - k.seen : -1;
        if (rn == c.grab) c.seenT0 = age == 0 ? 1 : 0;
        if (age < 0 || age > D0_HOLD) return;           // no live track
        c.enMax = Math.max(c.enMax, k.en);
        if (rn == c.grab) return;
        c.live++;
        if (age == 0) return;                           // seen this round
        c.unseen++;
        int[] D = nearestOf(centres[3 - c.dt], k.lx, k.ly), P = chainPoint(k.lx, k.ly, D[0], D[1], age);
        if (P == null) { c.noPoint++; return; }
        c.err[age].add(cheb(P[0], P[1], F[0], F[1]));
        boolean r100 = near(al[c.dt], P[0], P[1], D0_DIVE_R2, -1);
        for (int g = 0; g < D0_GROUP.length; g++) if (r100 && k.en < D0_GROUP[g]) c.elig[g]++;
        if (k.en < D0_GROUP[0] && near(al[c.dt], P[0], P[1], LEAK_R2, -1)) c.elig[3]++;
    }
    /* --recall-d0 columns (Gymhgy study L1, research/gymhgy-study-2026-10-04/lens-their_offense.md): at t = 0, 10 and 20
     * rounds after a chain's grab (end of round, robots alive then), the defending team's robots by Chebyshev distance to
     * the flag's tile; free = no enemy within dist2 20 of the robot (none in its vision).
     *  team, grab, flag, g0, outcome, T   the chain as in --contact-d0
     *  t               rounds after the grab of this snapshot (0, 10, 20; a chain closed before t has no row for it)
     *  enemy20         the carrier team within dist2 20 of the flag at t; enemy10: within Chebyshev 10
     *  ours20, ours100 the defending team within dist2 20 / 100 of the flag
     *  alive           the defending team's robots on the map
     *  free10, free20, free30, freeFar   free robots at Chebyshev distance <= 10, 11-20, 21-30, > 30 from the flag
     *  busy10, busy20, busy30, busyFar   robots with an enemy within dist2 20 (in a fight), by the same distance bands
     *  nFight..nOther  the free robots within 30 by their last indicator note (first word; G.note): fight, tether, chase,
     *                  icpt, defend, threat, explore, gather/hold, other
 *  kills100, deaths100, stunVict100, enStunVict100   since the grab round (through t): the carrier team's deaths and the
 *                  defending team's deaths within dist2 100 of the flag (its tile that round), and the robots frozen there by
 *                  the defending team's stun traps (victims within dist2 13 of the trap) and by the carrier team's */
    static boolean rdMode;
    static Map<Integer, String> lastNote = new HashMap<>();
    static List<int[]> rdEv = new ArrayList<>();   // this round's stun triggers {0, team, x, y, victims} and deaths {1, team, x, y, 1}
    static final String RD_COLS = "team,grab,flag,g0,outcome,T,t,enemy20,enemy10,ours20,ours100,alive,free10,free20,free30,freeFar,busy10,busy20,busy30,busyFar,"
            + "nFight,nTether,nChase,nIcpt,nDefend,nThreat,nExplore,nGather,nOther,kills100,deaths100,stunVict100,enStunVict100";
    static final String[] RD_NOTES = {"fight", "tether", "chase", "icpt", "defend", "threat", "explore", "gather"};
    static void rdTick(Chain c, int rn, int[] F, List<int[]>[] al) {
        int[] band = new int[4], busy = new int[4], notes = new int[RD_NOTES.length + 1]; int en10 = 0;
        for (int[] e : al[3 - c.dt]) if (cheb(e[0], e[1], F[0], F[1]) <= 10) en10++;
        for (int[] o : al[c.dt]) {
            boolean free = true;
            for (int[] e : al[3 - c.dt]) if (d2(e[0], e[1], o[0], o[1]) <= VISION2) { free = false; break; }
            int d = cheb(o[0], o[1], F[0], F[1]), b = d <= 10 ? 0 : d <= 20 ? 1 : d <= 30 ? 2 : 3;
            if (!free) { busy[b]++; continue; }
            band[b]++;
            if (d > 30) continue;
            String n = lastNote.getOrDefault(o[2], ""); if (n.equals("hold")) n = "gather";
            int k = RD_NOTES.length; for (int i = 0; i < RD_NOTES.length; i++) if (RD_NOTES[i].equals(n)) { k = i; break; }
            notes[k]++;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(rn - c.grab).append(',').append(count(al[3 - c.dt], F[0], F[1], VISION2, -1)).append(',').append(en10).append(',').append(count(al[c.dt], F[0], F[1], VISION2, -1))
          .append(',').append(count(al[c.dt], F[0], F[1], SCREEN_R2, -1)).append(',').append(al[c.dt].size());
        for (int b : band) sb.append(',').append(b);
        for (int b : busy) sb.append(',').append(b);
        for (int k : notes) sb.append(',').append(k);
        for (int k : c.rdc) sb.append(',').append(k);
        c.rd.add(sb.toString());
    }
    static void printRd() {
        out.println(RD_COLS);
        List<Chain> rows = new ArrayList<>(chainsDone);
        rows.sort((a, b) -> a.grab != b.grab ? Integer.compare(a.grab, b.grab) : Integer.compare(a.flag, b.flag));
        for (Chain c : rows) {
            if (!tracked(c.dt)) continue;
            for (String r : c.rd) out.println(tname(c.dt) + "," + c.grab + "," + c.flag + "," + c.g0 + "," + c.outcome + "," + c.rounds.size() + "," + r);
        }
    }
    static void printD0() {
        out.println(D0_COLS);
        List<Chain> rows = new ArrayList<>(chainsDone);
        rows.sort((a, b) -> a.grab != b.grab ? Integer.compare(a.grab, b.grab) : Integer.compare(a.flag, b.flag));
        for (Chain c : rows) {
            if (!tracked(c.dt)) continue;
            StringBuilder sb = new StringBuilder();
            sb.append(tname(c.dt)).append(',').append(c.grab).append(',').append(c.flag).append(',').append(c.g0).append(',').append(c.outcome)
              .append(',').append(c.rounds.size()).append(',').append(c.seenT0).append(',').append(c.rounds.isEmpty() ? "" : noContact(c.rounds) ? "1" : "0")
              .append(',').append(c.enMax >= 0 ? String.valueOf(c.enMax) : "").append(',').append(c.live).append(',').append(c.unseen).append(',').append(c.noPoint);
            for (int e : c.elig) sb.append(',').append(e);
            for (int a = 1; a <= D0_HOLD; a++) {
                sb.append(',');
                for (int i = 0; i < c.err[a].size(); i++) sb.append(i > 0 ? ";" : "").append(c.err[a].get(i));
            }
            out.println(sb);
        }
    }

    /** flagDistMin / flagDistMean: at r200, each own flag's distance (tiles) to the nearest enemy spawn centre (a flag id
     *  is the location index of its spawn centre). Far flags need longer enemy relay chains (2026-10-03, waffle). */
    static double[] kFlagDistMin = {-1, -1, -1}, kFlagDistMean = {-1, -1, -1};
    /** carrierStunBuilds / carrierStunned: our stun traps built within dist2 8 of an enemy robot carrying our flag, and our
     *  triggered stuns that caught one (within dist2 13), post-setup (2026-10-03, waffle: a frozen carrier cannot move). */
    static int[] kCarrierStunBuilds = new int[3], kCarrierStunned = new int[3];
    /** captured600 / enemyCaptured600: flags captured by the end of r600 (a slowed relay chain shows here first). */
    static int[] kCaptured600 = new int[3];
    /** stunTrig / stunVictims: our stun traps triggered post-setup, and enemy robots within dist2 13 of them at the end of that
     *  round (the trap sets their cooldowns to 40); enemyStunTrig / enemyStunVictims: the same for theirs on us (2026-10-04,
     *  Cyril study: each of its stuns froze ~5.8 of ours). */
    static int[] kStunTrig = new int[3], kStunVictims = new int[3], kStunVictimsEsc = new int[3], kStunVictimsFast = new int[3];
    static Map<Integer, Integer> trapRound = new HashMap<>();   // trap id -> build round
    static Map<Integer, int[]> nowLoc = new HashMap<>();
    /** --trapgeo: one line per post-setup trap when it triggers (untriggered ones at the end, trigRound -1):
     *  TG,team,type,buildRound,nearestEnemyD2,enemies13,enemies8,ownWithin2,trigRound,latency,victims13,builderBuildLevel
     *  (builder = the nearest own robot at the end of the build round; -1 unknown) */
    static boolean trapGeo = false;
    static Map<Integer, int[]> trapBuilt = new HashMap<>();   // robots on the map this round: id -> {x, y, team}
    /** defNearAtGrab20: mean of our robots within dist2 20 of our flag at each enemy first grab (from home).
     *  capturedHomeRounds: robot-rounds our robots spend within dist2 8 of the home of one of our captured flags. */
    static int[] kDefAtGrabSum = new int[3], kDefAtGrabN = new int[3], kCapturedHomeRounds = new int[3];
    @SuppressWarnings("unchecked") static List<int[]>[] capturedHomes = new List[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
    static boolean carrierNear(int carrierTeam, int x, int y, int r2) {
        for (Map.Entry<Integer, Integer> c : carrying.entrySet()) {
            if (team.getOrDefault(c.getKey(), 0) != carrierTeam) continue;
            int[] l = lastLoc.get(c.getKey()); if (l == null) continue;
            int dx = l[0] - x, dy = l[1] - y; if (dx * dx + dy * dy <= r2) return true;
        }
        return false;
    }

    static void flagDistTick() {
        for (int t = 1; t <= 2; t++) {
            double mn = Double.MAX_VALUE, sum = 0; int n = 0;
            for (Map.Entry<Integer, int[]> e : flagLoc.entrySet()) {
                if (flagTeam.getOrDefault(e.getKey(), 0) != t || e.getValue() == null) continue;
                int[] l = e.getValue(); double best = Double.MAX_VALUE;
                for (Integer k : flagTeam.keySet()) {
                    if (flagTeam.get(k) != 3 - t) continue;
                    int cx = k % W, cy = k / W;
                    best = Math.min(best, Math.sqrt((l[0] - cx) * (l[0] - cx) + (l[1] - cy) * (l[1] - cy)));
                }
                if (best == Double.MAX_VALUE) continue;
                mn = Math.min(mn, best); sum += best; n++;
            }
            if (n > 0) { kFlagDistMin[t] = mn; kFlagDistMean[t] = sum / n; }
            List<int[]> own = new ArrayList<>();
            for (Map.Entry<Integer, int[]> e : flagLoc.entrySet()) if (flagTeam.getOrDefault(e.getKey(), 0) == t && e.getValue() != null) own.add(e.getValue());
            double lo = Double.MAX_VALUE, hi = -1;
            for (int i = 0; i < own.size(); i++) for (int j = i + 1; j < own.size(); j++) {
                double d = Math.sqrt(d2(own.get(i)[0], own.get(i)[1], own.get(j)[0], own.get(j)[1])); lo = Math.min(lo, d); hi = Math.max(hi, d); }
            if (hi >= 0) { kFlagSpreadMin[t] = lo; kFlagSpreadMax[t] = hi; }
        }
    }
    static double[] kFlagSpreadMin = {-1, -1, -1}, kFlagSpreadMax = {-1, -1, -1};
    static int[] kCarrierDeathsSpawn = new int[3];
    static int[] kHealPost = new int[3], kHealThreat = new int[3], kNear20 = new int[3], kReady20 = new int[3];
    static int[] kRingStuns = new int[3], kFieldStuns = new int[3];
    static Map<Integer, Integer> acdNow = new HashMap<>();
    static int[] kSpawnPost = new int[3], kSpawnNear = new int[3], kDeathPost = new int[3], kSpawnDeath10 = new int[3];
    static List<Integer> spawnsNow = new ArrayList<>();

    // ---- --capabilities step census (C.ENGAGE_HP signature, andli28 study 2026-10-06; see the header). Robots by their position
    // in round 1's bytecode table (the fixed execution order); p* = their state at the end of the previous round.
    static final int SN = 100;
    static final int[] ATK_SKILL = {0, 5, 7, 10, 30, 35, 60}, HEAL_SKILL = {0, 3, 5, 7, 10, 15, 25};   // SkillType effects, levels 0-6
    static Map<Integer, Integer> rankOf = new HashMap<>();
    static boolean[] pAlive = new boolean[SN], pCarry = new boolean[SN], pUpgAtk = new boolean[3], pUpgHeal = new boolean[3];
    static int[] pX = new int[SN], pY = new int[SN], pHP = new int[SN], pACD = new int[SN], pMCD = new int[SN], pAtk = new int[SN], pHeal = new int[SN];
    static int[] kStepDec = new int[3], kStepMidS = new int[3], kStepLethal = new int[3], kStepDeaths = new int[3], kStepLethalAvoid = new int[3];
    static List<int[]> stepPending = new ArrayList<>();   // {rank, HP left after the hits that followed its step-in strike}
    static int rankTeam(int k) { return k % 2 == 0 ? 1 : 2; }
    /** One hit of robot k (InternalRobot.getDamage) with its level and its team's upgrade at the end of the previous round. */
    static int hitOf(int k) { return Math.round((150 + (pUpgAtk[rankTeam(k)] ? 60 : 0)) * ((float) ATK_SKILL[Math.min(6, Math.max(0, pAtk[k]))] / 100 + 1)); }
    static int healOf(int k) { return Math.round((80 + (pUpgHeal[rankTeam(k)] ? 50 : 0)) * ((float) HEAL_SKILL[Math.min(6, Math.max(0, pHeal[k]))] / 100 + 1)); }

    /** The step census for round rn (called at the end of round(), so `carrying` is the end-of-round state). First the step-in
     *  strikes of the previous round still pending are resolved (died this round with the hits before its turn reaching its
     *  HP); then every robot is replayed at its own turn (ranks in order, actions applied in order); then the end-of-round
     *  state becomes p*. Levels of a robot not in this round's table (jailed) stay its last known; an upgrade counts from the
     *  round after its purchase. */
    static void stepTick(Round r, int rn) {
        if (rankOf.isEmpty()) for (int j = 0; j < r.bytecodeIdsLength() && j < SN; j++) rankOf.put(r.bytecodeIds(j), j);
        boolean[] cAlive = new boolean[SN], cDied = new boolean[SN], cSp = new boolean[SN], cIn = new boolean[SN], struck = new boolean[SN];
        int[] cX = new int[SN], cY = new int[SN];
        for (int j = 0; j < r.diedIdsLength(); j++) { Integer k = rankOf.get(r.diedIds(j)); if (k != null) cDied[k] = true; }
        SpawnedBodyTable sb = r.spawnedBodies();
        if (sb != null) for (int j = 0; j < sb.robotIdsLength(); j++) { Integer k = rankOf.get(sb.robotIds(j)); if (k != null) cSp[k] = true; }
        VecTable locs = r.robotLocs();
        for (int j = 0; j < r.robotIdsLength(); j++) {
            Integer k = rankOf.get(r.robotIds(j)); if (k == null) continue;
            cIn[k] = true; cX[k] = locs.xs(j); cY[k] = locs.ys(j); cAlive[k] = !cDied[k] && r.robotHealths(j) > 0;
        }
        int na = r.actionIdsLength();
        int[] aR = new int[na], aT = new int[na], aG = new int[na];   // actor rank (-1: not a robot), action, target rank (attack/heal)
        for (int j = 0; j < na; j++) {
            Integer k = rankOf.get(r.actionIds(j)); int a = r.actions(j);
            aR[j] = k == null ? -1 : k; aT[j] = a; aG[j] = -1;
            if (k != null && (a == Action.ATTACK || a == Action.HEAL)) { Integer g = rankOf.get(r.actionTargets(j)); if (g != null) aG[j] = g; }
            if (k != null && a == Action.ATTACK && aG[j] >= 0) struck[k] = true;
        }
        for (int[] p : stepPending) {   // hits before its turn this round; it died before its next turn if they used up its HP
            int R = p[0], left = p[1];
            for (int j = 0; j < na && (aR[j] < 0 || aR[j] < R); j++) if (aR[j] >= 0 && aT[j] == Action.ATTACK && aG[j] == R) left -= hitOf(aR[j]);
            if (cDied[R] && left <= 0) kStepDeaths[rankTeam(R)]++;
        }
        stepPending.clear();
        if (rn > 200) {
            boolean[] aliveAt = new boolean[SN]; int[] hpAt = new int[SN], xAt = new int[SN], yAt = new int[SN];
            for (int k = 0; k < SN; k++) { aliveAt[k] = pAlive[k]; hpAt[k] = pAlive[k] ? pHP[k] : 1000; xAt[k] = pX[k]; yAt[k] = pY[k]; }
            int j = 0;
            for (int R = 0; R < SN; R++) {
                for (; j < na && (aR[j] < 0 || aR[j] < R); j++) {   // the actions of every robot ranked before R
                    if (aR[j] < 0 || aG[j] < 0) continue;
                    if (aT[j] == Action.ATTACK) { hpAt[aG[j]] -= hitOf(aR[j]); if (hpAt[aG[j]] <= 0) aliveAt[aG[j]] = false; }
                    else if (aT[j] == Action.HEAL) hpAt[aG[j]] = Math.min(1000, hpAt[aG[j]] + healOf(aR[j]));
                }
                int t = rankTeam(R);
                if (pAlive[R] && !pCarry[R] && pACD[R] < 20 && pMCD[R] < 20 && aliveAt[R]) {   // ready to act and to move at its turn
                    int minD = Integer.MAX_VALUE; boolean car = false;
                    for (int k = 0; k < SN; k++) {
                        if (k == R || !aliveAt[k] || rankTeam(k) == t) continue;
                        int dd = d2(pX[R], pY[R], xAt[k], yAt[k]);
                        if (dd < minD) minD = dd;
                        if (dd <= VISION2 && pCarry[k]) car = true;
                    }
                    if (minD > 4 && minD <= 10 && !car) {           // a decision: one step from reach, no enemy carrier in view
                        int hp = hpAt[R]; boolean mid = hp >= 300 && hp < 700;
                        if (mid) { kStepDec[t]++; if (struck[R]) kStepMidS[t]++; }
                        if (struck[R]) {
                            int ex = cIn[R] ? cX[R] : pX[R], ey = cIn[R] ? cY[R] : pY[R], my = hitOf(R), sum = 0, sum0 = 0; boolean kill = false;
                            for (int k = 0; k < SN; k++) {
                                if (k == R || !aliveAt[k] || rankTeam(k) == t) continue;
                                int dd = d2(ex, ey, xAt[k], yAt[k]);
                                if (dd <= 10) sum += hitOf(k);
                                if (dd <= 4 && hpAt[k] <= my) kill = true;
                                if (d2(pX[R], pY[R], xAt[k], yAt[k]) <= 10) sum0 += hitOf(k);   // the start tile (stepLethalAvoid)
                            }
                            if (mid && sum >= hp && !kill) { kStepLethal[t]++; if (sum0 < hp) kStepLethalAvoid[t]++; }
                            if (cDied[R]) kStepDeaths[t]++;
                            else {
                                int left = hp;
                                for (int q = 0; q < na; q++) if (aR[q] > R && aT[q] == Action.ATTACK && aG[q] == R) left -= hitOf(aR[q]);
                                stepPending.add(new int[]{R, left});
                            }
                        }
                    }
                }
                aliveAt[R] = (pAlive[R] || cSp[R]) && hpAt[R] > 0;   // its turn is over: its end-of-round tile from here on
                if (cIn[R]) { xAt[R] = cX[R]; yAt[R] = cY[R]; }
            }
        }
        for (int j = 0; j < r.robotIdsLength(); j++) {
            Integer k = rankOf.get(r.robotIds(j)); if (k == null) continue;
            pHP[k] = r.robotHealths(j); pACD[k] = r.robotActionCooldowns(j); pMCD[k] = r.robotMoveCooldowns(j);
            pAtk[k] = r.attackLevels(j); pHeal[k] = r.healLevels(j); pX[k] = cX[k]; pY[k] = cY[k];
        }
        for (int k = 0; k < SN; k++) { pAlive[k] = cAlive[k]; pCarry[k] = false; }
        for (int id : carrying.keySet()) { Integer k = rankOf.get(id); if (k != null) pCarry[k] = true; }
        for (int j = 0; j < na; j++) if (aR[j] >= 0 && aT[j] == Action.GLOBAL_UPGRADE) {
            int tg = r.actionTargets(j), t = rankTeam(aR[j]);
            if (tg == 0) pUpgAtk[t] = true; else if (tg == 1) pUpgHeal[t] = true;
        }
    }

    static void surveyTick(int rn) {
        for (int t = 1; t <= 2; t++) {
            if (rn == 200) {
                sDigs200[t] = cDigs[t]; sFills200[t] = cFills[t]; sTraps200[t] = cTraps[t][0] + cTraps[t][1] + cTraps[t][2];
                sCrumbs200[t] = crumbsNow[t]; sLevel200[t] = levelSum(t);
                int d = 0;
                for (Map.Entry<Integer, int[]> e : flagLoc.entrySet()) {
                    if (flagTeam.getOrDefault(e.getKey(), 0) != t || e.getValue() == null) continue;
                    int k = e.getKey(), cx = k % W, cy = k / W; int[] l = e.getValue();
                    d += (int) Math.round(Math.sqrt((l[0] - cx) * (l[0] - cx) + (l[1] - cy) * (l[1] - cy)));
                }
                sFlagMoveDist[t] = d;
            }
            if (rn == 250) sCrumbs250[t] = crumbsNow[t];
            if (rn == 300) {   // own robots within dist2 20 of own uncarried flags
                int c = 0;
                for (Map.Entry<Integer, int[]> e : flagLoc.entrySet()) {
                    if (flagTeam.getOrDefault(e.getKey(), 0) != t || e.getValue() == null) continue;
                    int[] f = e.getValue();
                    for (Map.Entry<Integer, int[]> re : lastLoc.entrySet()) {
                        if (team.getOrDefault(re.getKey(), 0) != t) continue;
                        int[] l = re.getValue(); int dx = l[0] - f[0], dy = l[1] - f[1];
                        if (dx * dx + dy * dy <= 20) c++;
                    }
                }
                sDefend300[t] = c;
            }
            if (rn == 400) { sHeals400[t] = cHeals[t]; sAtk400[t] = cAttacks[t]; sAlive400[t] = alive[t];
                for (int k = 0; k < 3; k++) sTraps400[t][k] = cTraps[t][k]; }
        }
    }

    static void flagEvent(int rn, String what, int actor, int flag) {
        if (!flagsMode) return;
        int[] l = actor >= 0 ? lastLoc.get(actor) : flagLoc.get(flag);
        out.printf("r%d %-8s flag=%s%d actor=%s loc=%s%n", rn, what, tname(flagTeam.getOrDefault(flag, 0)), flag,
                actor >= 0 ? tname(team.getOrDefault(actor, 0)) + "#" + actor : "-", l == null ? "?" : "(" + l[0] + "," + l[1] + ")");
    }

    static int levelSum(int t) {
        int s = 0;
        for (Map.Entry<Integer, int[]> e : levels.entrySet()) if (team.getOrDefault(e.getKey(), 0) == t) { int[] l = e.getValue(); s += l[0] + l[1] + l[2]; }
        return s;
    }

    static int carryingCount(int t) { int c = 0; for (int id : carrying.keySet()) if (team.getOrDefault(id, 0) == t) c++; return c; }

    static String metricRow(int rn, int t) {
        int o = 3 - t;
        return rn + "," + tname(t) + "," + alive[t] + "," + hpSum[t] + "," + crumbsNow[t] + "," + cCaptures[t] + "," + carryingCount(t) + ","
                + cDeaths[t] + "," + cDeaths[o] + "," + cAttacks[t] + "," + cHeals[t] + ","
                + (cTraps[t][0] + cTraps[t][1] + cTraps[t][2]) + "," + cTraps[t][0] + "," + cTraps[t][1] + "," + cTraps[t][2] + ","
                + cTrapsHit[o] + "," + cDigs[t] + "," + cFills[t] + "," + cPickups[t] + "," + levelSum(t) + "," + cMoves[t] + ","
                + roundMaxBc[t] + "," + cSpawns[t];
    }

    static void printEvery(int rn) {
        out.printf("r%-5d", rn);
        for (int t = 1; t <= 2; t++) {
            int o = 3 - t;
            out.printf(" | %s alive=%2d hp=%5d cr=%5d cap=%d carry=%d K/D=%d/%d atk=%d heal=%d traps=%d/%d/%d hit=%d dig=%d lv=%d",
                    tname(t), alive[t], alive[t] > 0 ? hpSum[t] / alive[t] : 0, crumbsNow[t], cCaptures[t], carryingCount(t), cDeaths[o], cDeaths[t],
                    cAttacks[t], cHeals[t], cTraps[t][0], cTraps[t][1], cTraps[t][2], cTrapsHit[o], cDigs[t], levelSum(t));
        }
        out.println();
    }

    static void printMap(int rn) {
        char[][] g = new char[H][W];
        for (int y = 0; y < H; y++) for (int x = 0; x < W; x++) {
            int i = idx(x, y);
            char c = '.';
            if (wall[i]) c = '#'; else if (water[i]) c = '~'; else if (dam[i] && rn <= 200) c = '=';
            else if (spawnZone[i] == 1) c = 'S'; else if (spawnZone[i] == 2) c = 's';
            else if (crumbs[i] > 0) c = '*';
            g[y][x] = c;
        }
        for (Map.Entry<Integer, int[]> e : trapLoc.entrySet()) { int[] l = e.getValue(); int t = trapTeam.get(e.getKey());
            char c = "xwt".charAt(trapType.get(e.getKey()) % 3); g[l[1]][l[0]] = t == 1 ? Character.toUpperCase(c) : c; }
        for (Map.Entry<Integer, int[]> e : flagLoc.entrySet()) { int[] l = e.getValue(); if (l == null) continue;
            g[l[1]][l[0]] = flagTeam.getOrDefault(e.getKey(), 1) == 1 ? 'F' : 'f'; }
        for (Map.Entry<Integer, int[]> e : lastLoc.entrySet()) { int[] l = e.getValue(); int t = team.getOrDefault(e.getKey(), 0);
            boolean c = carrying.containsKey(e.getKey());
            g[l[1]][l[0]] = t == 1 ? (c ? 'Q' : 'A') : (c ? 'q' : 'b'); }
        out.printf("board at end of round %d (%s=A upper, %s=B lower): # wall ~ water = dam S/s spawn * crumbs F/f flag A/b robot Q/q carrier X/x expl W/w water T/t stun%n", rn, teamA, teamB);
        for (int y = H - 1; y >= 0; y--) out.printf("%2d %s%n", y, new String(g[y]));
    }

    static void finish() {
        if (summary) {
            out.printf("teams A=%s B=%s%n", teamA, teamB);
            out.printf("winner %s (%s) by %s at round %d%n", tname(winner), winner == 1 ? teamA : teamB, winType >= 0 && winType < WIN.length ? WIN[winType] : "?", totalRounds);
            for (int t = 1; t <= 2; t++) {
                int o = 3 - t;
                out.printf("%s: captured=%d firstCapture=r%d pickups=%d spawns=%d deaths=%d kills=%d attacks=%d heals=%d traps(expl/water/stun)=%d/%d/%d enemyTrapsHit=%d digs=%d fills=%d upgrades=%d exceptions=%d levelSum=%d crumbs=%d moves=%d%n",
                        tname(t), cCaptures[t], firstCapture[t], cPickups[t], cSpawns[t], cDeaths[t], cDeaths[o], cAttacks[t], cHeals[t],
                        cTraps[t][0], cTraps[t][1], cTraps[t][2], cTrapsHit[t], cDigs[t], cFills[t], cUpgrades[t], cExc[t], levelSum(t), crumbsNow[t], cMoves[t]);
            }
        }
        if (survey && totalRounds < 400) { int keep = totalRounds; surveyTick(totalRounds < 250 ? 250 : totalRounds < 300 ? 300 : 400);
            if (totalRounds < 300) surveyTick(300); if (totalRounds < 400) surveyTick(400); totalRounds = keep; }
        if (survey) {
            out.println("team,name,won,rounds,wintype,captured,firstCapture,firstPickup,pickups,drops,setupFlagPickups,flagMoveDist,digs200,fills200,traps200,crumbs200,crumbs250,level200,defend300,atk400,heal400,expl400,water400,stun400,trapsHit,kills,deaths,upgrades");
            for (int t = 1; t <= 2; t++) {
                int o = 3 - t;
                out.println(tname(t) + "," + (t == 1 ? teamA : teamB) + "," + (winner == t ? 1 : 0) + "," + totalRounds + "," + (winType >= 0 && winType < WIN.length ? WIN[winType] : "?") + ","
                        + cCaptures[t] + "," + firstCapture[t] + "," + sFirstPickup[t] + "," + cPickups[t] + "," + sDrops[t] + "," + sOwnFlagPickupsSetup[t] + "," + sFlagMoveDist[t] + ","
                        + sDigs200[t] + "," + sFills200[t] + "," + sTraps200[t] + "," + sCrumbs200[t] + "," + sCrumbs250[t] + "," + sLevel200[t] + "," + sDefend300[t] + ","
                        + sAtk400[t] + "," + sHeals400[t] + "," + sTraps400[t][0] + "," + sTraps400[t][1] + "," + sTraps400[t][2] + "," + cTrapsHit[o] + "," + cDeaths[o] + "," + cDeaths[t] + "," + sUpgrades[t]);
            }
        }
        if (chainsOn()) for (Chain c : new ArrayList<>(chains.values())) chainEnd(totalRounds, c.flag, "OPEN", null);
        if (capMode) {
            out.println("team,name,won,rounds,wintype,gathered200,gathered400,firstEnemySide,inEnemy250,inEnemy300,firstFlagSight,pickups,captured,carrierDeaths,carrierRounds,carrierMoves,enemyCarrierKills,trapsBuilt,trapsHit,kills,deaths,meanAlive,postPickups,firstGrabs,regrabs,relayPickups,carrierDeathDist,damStage199,enemyRegrabs,enemyFirstGrabs,regrabsLate,capturedLate,chasers20,enemyCaptured,escorts20,stillPost,"
                    + "enemyUnseenRounds,unopposedCaps,longTrips25,longCaps25,longCapRate,loneDeaths,trickleDeaths,symOk,psymOk,maxBcK,overruns,exceptions,symDecidedRound,symWrong,alertWrites,alertNoThreat,maxParkOnHome,efStaleCarry,efStaleLoc,flagDistMin,flagDistMean,carrierStunBuilds,carrierStunned,captured600,enemyCaptured600,defNearAtGrab20,capturedHomeRounds,stunTrig,stunVictims,enemyStunTrig,enemyStunVictims,stunVictimsEsc,enemyStunVictimsEsc,stunVictimsFast,enemyStunVictimsFast,deathsHome,enemyDeathsHome,gatheredAll,dropGuard,digsLate,levelGain1500,gathered201to400,stunTrig250,kills250,deaths250,levelGain1200,levelGapEnd," + CHAIN_COLS + ",flagSpreadMin,flagSpreadMax,carrierDeathsSpawn,paidKillShare,homeDeathShare,healThreat10,readyHeld20,spawnNear20,spawnDeath10,bank1900,stepMid,stepLethal,stepDeaths,killShare,stepMidN,stepDec,stepLethalAvoid,ringStunsPost,fieldStunsPost");
            for (int t = 1; t <= 2; t++) {
                int o = 3 - t;
                if (totalRounds < 400) kGathered400[t] = kGathered[t];
                if (totalRounds < 200) kGathered200[t] = kGathered[t];
                out.println(tname(t) + "," + (t == 1 ? teamA : teamB) + "," + (winner == t ? 1 : 0) + "," + totalRounds + "," + (winType >= 0 && winType < WIN.length ? WIN[winType] : "?") + ","
                        + kGathered200[t] + "," + kGathered400[t] + "," + kFirstEnemySide[t] + "," + kInEnemy250[t] + "," + kInEnemy300[t] + "," + kFirstFlagSight[t] + ","
                        + cPickups[t] + "," + cCaptures[t] + "," + kCarrierDeaths[t] + "," + kCarrierRounds[t] + "," + kCarrierMoves[t] + "," + kCarrierDeaths[o] + ","
                        + (cTraps[t][0] + cTraps[t][1] + cTraps[t][2]) + "," + cTrapsHit[o] + "," + cDeaths[o] + "," + cDeaths[t] + "," + String.format("%.1f", kRounds > 0 ? (double) kAliveSum[t] / kRounds : 0.0)
                        + "," + kPostPickups[t] + "," + kFirstGrabs[t] + "," + kRegrabs[t] + "," + kRelayPickups[t] + ","
                        + (kCarrierDeathN[t] > 0 ? String.format("%.1f", (double) kCarrierDeathDistSum[t] / kCarrierDeathN[t]) : "") + "," + kDamStage199[t]
                        + "," + kRegrabs[o] + "," + kFirstGrabs[o] + "," + kRegrabsLate[t] + "," + kCapturedLate[t] + "," + String.format("%.2f", kChaseRounds[t] > 0 ? (double) kChaseSum[t] / kChaseRounds[t] : 0.0) + "," + cCaptures[o] + "," + String.format("%.2f", kChaseRounds[o] > 0 ? (double) kEscortSum[t] / kChaseRounds[o] : 0.0)
                        + "," + String.format("%.1f", robotRoundsPost[t] > 0 ? 100.0 * stillPost[t] / robotRoundsPost[t] : 0.0)
                        + "," + (kEnemyCarried[t] > 0 ? String.valueOf(kEnemyUnseen[t]) : "") + "," + (kEnemyTrips[t] > 0 ? String.valueOf(kUnopposed[t]) : "")
                        + "," + (kLong25[t] > 0 ? String.valueOf(kLong25[t]) : "") + "," + (kLong25[t] > 0 ? String.valueOf(kLongCaps25[t]) : "")
                        + "," + (kLong25[t] > 0 ? String.format("%.3f", (double) kLongCaps25[t] / kLong25[t]) : "")
                        + "," + (cDeaths[t] > 0 ? String.valueOf(kLoneDeaths[t]) : "") + "," + (cDeaths[t] > 0 ? String.valueOf(kTrickleDeaths[t]) : "")
                        + "," + (symOk(t) >= 0 ? String.valueOf(symOk(t)) : "") + "," + (psymOk(t) >= 0 ? String.valueOf(psymOk(t)) : "")
                        + "," + String.format("%.1f", maxBc[t] / 1000.0) + "," + turnsAtLimit[t] + "," + cExc[t]
                        + "," + (commSeen && symDecided[t] >= 0 ? String.valueOf(symDecided[t]) : "") + "," + (commSeen ? String.valueOf(symWrongT[t]) : "")
                        + "," + (commSeen ? String.valueOf(kAlertWrites[t]) : "") + "," + (commSeen ? String.valueOf(kAlertNoThreat[t]) : "") + "," + kMaxPark[t]
                        + "," + (commSeen ? String.valueOf(kEfStaleCarry[t]) : "") + "," + (commSeen ? String.valueOf(kEfStaleLoc[t]) : "")
                        + "," + (kFlagDistMin[t] >= 0 ? String.format("%.1f", kFlagDistMin[t]) : "") + "," + (kFlagDistMean[t] >= 0 ? String.format("%.1f", kFlagDistMean[t]) : "")
                        + "," + kCarrierStunBuilds[t] + "," + kCarrierStunned[t] + "," + kCaptured600[t] + "," + kCaptured600[o]
                        + "," + (kDefAtGrabN[t] > 0 ? String.format("%.2f", (double) kDefAtGrabSum[t] / kDefAtGrabN[t]) : "") + "," + kCapturedHomeRounds[t]
                        + "," + kStunTrig[t] + "," + kStunVictims[t] + "," + kStunTrig[o] + "," + kStunVictims[o]
                        + "," + kStunVictimsEsc[t] + "," + kStunVictimsEsc[o] + "," + kStunVictimsFast[t] + "," + kStunVictimsFast[o]
                        + "," + kDeathsHome[t] + "," + kDeathsHome[o] + "," + kGathered[t] + "," + kDropGuard[t]
                        + "," + kDigsLate[t] + "," + (kLevel1500[t] >= 0 ? String.valueOf(levelSum(t) - kLevel1500[t]) : "")
                        + "," + (kGathered400[t] - kGathered200[t]) + "," + kStunTrig250[t] + "," + kKills250[t] + "," + kDeaths250[t]
                        + "," + (kLevel1200[t] >= 0 ? String.valueOf(levelSum(t) - kLevel1200[t]) : "") + "," + (levelSum(t) - levelSum(o))
                        + "," + chainTally[t].cols()
                        + "," + (kFlagSpreadMax[t] >= 0 ? String.format("%.1f,%.1f", kFlagSpreadMin[t], kFlagSpreadMax[t]) : ",")
                        + "," + kCarrierDeathsSpawn[t]
                        + "," + share(kDeathsHome[o], cDeaths[o]) + "," + share(kDeathsHome[t], cDeaths[t])
                        + "," + share(kHealThreat[t], kHealPost[t]) + "," + share(kReady20[t], kNear20[t])
                        + "," + share(kSpawnNear[t], kSpawnPost[t]) + "," + share(kSpawnDeath10[t], kDeathPost[t])
                        + "," + (kBank1900[t] >= 0 ? String.valueOf(kBank1900[t]) : "")
                        + "," + share(kStepMidS[t], kStepDec[t]) + "," + kStepLethal[t] + "," + kStepDeaths[t] + "," + share(cDeaths[o], cDeaths[o] + cDeaths[t])
                        + "," + kStepMidS[t] + "," + kStepDec[t] + "," + kStepLethalAvoid[t] + "," + kRingStuns[t] + "," + kFieldStuns[t]);
            }
        }
        if (trapGeo) for (Map.Entry<Integer, int[]> e : trapBuilt.entrySet()) { int[] b = e.getValue();
            out.printf("TG,%s,%s,%d,%d,%d,%d,%d,-1,-1,0,%d%n", tname(trapTeam.getOrDefault(e.getKey(), 0)), BUILD[trapType.getOrDefault(e.getKey(), 0)], b[0], b[1], b[2], b[3], b[4], b[5]); }
        if (trackMode) printTrack();
        if (d0Mode) printD0();
        if (rdMode) printRd();
        if (commMode) out.println("# commStored " + commStored + "/" + kRounds);
        if (levelsMode) for (int t = 1; t <= 2; t++) {   // last known (attack/build/heal) levels per robot
            Map<String, Integer> hist = new TreeMap<>(); int n = 0, atk4 = 0, heal4 = 0, build4 = 0, atkSum = 0, healSum = 0, buildSum = 0;
            for (Map.Entry<Integer, int[]> e : levels.entrySet()) {
                if (team.getOrDefault(e.getKey(), 0) != t) continue;
                int[] l = e.getValue(); n++; atkSum += l[0]; healSum += l[2]; buildSum += l[1];
                if (l[0] >= 4) atk4++; if (l[2] >= 4) heal4++; if (l[1] >= 4) build4++;
                hist.merge("a" + l[0] + "b" + l[1] + "h" + l[2], 1, Integer::sum);
            }
            out.printf("levels %s: robots=%d meanAtk=%.2f meanHeal=%.2f atkMastery=%d healMastery=%d buildMastery=%d buildSum=%d  %s%n", tname(t), n,
                    n > 0 ? (double) atkSum / n : 0.0, n > 0 ? (double) healSum / n : 0.0, atk4, heal4, build4, buildSum, hist);
        }
        if (bytecode) for (int t = 1; t <= 2; t++)
            out.printf("bytecode %s: max=%d mean=%.0f turnsAtLimit=%d turnsNear90=%d turns=%d%n", tname(t), maxBc[t],
                    bcTurns[t] > 0 ? (double) bcSum[t] / bcTurns[t] : 0.0, turnsAtLimit[t], turnsNear[t], bcTurns[t]);
        if (navstats) {
            int passable = 0; for (int i = 0; i < W * H; i++) if (!wall[i]) passable++;
            for (int t = 1; t <= 2; t++) {
                int cov = 0; for (boolean v : visited[t]) if (v) cov++;
                out.printf("nav %s: moves=%d robotRounds=%d movesPerRobotRound=%.3f oscillationABA=%.1f%% stillRounds=%.1f%% coverage=%.1f%% stillPost=%.1f%% stillPostNoEnemy=%.1f%%%n",
                        tname(t), cMoves[t], robotRounds[t], robotRounds[t] > 0 ? (double) cMoves[t] / robotRounds[t] : 0.0,
                        cMoves[t] > 0 ? 100.0 * osc[t] / cMoves[t] : 0.0, robotRounds[t] > 0 ? 100.0 * stillRounds[t] / robotRounds[t] : 0.0,
                        100.0 * cov / Math.max(1, passable),
                        robotRoundsPost[t] > 0 ? 100.0 * stillPost[t] / robotRoundsPost[t] : 0.0,
                        robotRoundsPost[t] > 0 ? 100.0 * stillPostNoEnemy[t] / robotRoundsPost[t] : 0.0);
            }
        }
    }
}
