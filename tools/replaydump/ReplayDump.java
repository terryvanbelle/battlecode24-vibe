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
 *     --navstats          movement statistics per team
 *     --flags             every flag event with round, flag team, actor and location
 *
 * Replay facts (engine 3.0.6, GameMaker.MatchMaker): every Round lists every spawned robot plus the robots that
 * died that round (at their death tile); jailed robots are absent. Team ids 1 = A, 2 = B. Location index =
 * x + y*width. PLACE_FLAG's action id is the FLAG id (not a robot); PICKUP/CAPTURE target the flag id; flag id =
 * location index of the flag's original spawn-zone centre; centres alternate A,B in GameMap.spawnLocations.
 * Robot stdout is NOT stored in 2024 replays (GameMaker line ~604 commented out); use indicator strings.
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
    static int[] osc = new int[3], robotRounds = new int[3], stillRounds = new int[3];
    static boolean[][] visited = new boolean[3][];

    // ---- options
    static int every = 0, metricsEvery = 0, from = -1, to = -1, robot = Integer.MIN_VALUE, mapAt = -1;   // robot: MIN_VALUE = none (water-trap digs have actor id -1)
    static Pattern logs = null; static int logTeam = 0;
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
            kRegrabsLate = new int[3], kCapturedLate = new int[3];   // after r1200 (the CAPTURING window of upgrade order 3)
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
                case "--navstats": navstats = true; summary = false; break;
                case "--flags": flagsMode = true; summary = false; break;
                case "--survey": survey = true; summary = false; break;
                case "--levels": levelsMode = true; summary = false; break;
                case "--capabilities": capMode = true; summary = false; break;
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

    static boolean inWindow(int r) { return from >= 0 && r >= from && r <= to; }

    static void round(Round r) {
        int rn = r.roundId();
        // team resources
        for (int j = 0; j < r.teamIdsLength(); j++) crumbsNow[r.teamIds(j)] = r.teamResourceAmounts(j);
        // spawns
        SpawnedBodyTable sb = r.spawnedBodies();
        if (sb != null) for (int j = 0; j < sb.robotIdsLength(); j++) {
            int id = sb.robotIds(j), t = sb.teamIds(j);
            team.put(id, t); cSpawns[t]++;
            if (inWindow(rn) || id == robot) out.printf("r%d SPAWN %s#%d at (%d,%d)%n", rn, tname(t), id, sb.locs().xs(j), sb.locs().ys(j));
        }
        // robot state
        Arrays.fill(alive, 0); Arrays.fill(hpSum, 0); Arrays.fill(roundMaxBc, 0);
        VecTable locs = r.robotLocs();
        Map<Integer, Integer> hpNow = new HashMap<>();
        for (int j = 0; j < r.robotIdsLength(); j++) {
            int id = r.robotIds(j), t = team.getOrDefault(id, 0), x = locs.xs(j), y = locs.ys(j), hp = r.robotHealths(j);
            hpNow.put(id, hp);
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
            } else if (pl != null && hp > 0) stillRounds[t]++;
            if (hp > 0) robotRounds[t]++;
            visited[t][idx(x, y)] = true;
            int ci = idx(x, y); if (crumbs[ci] > 0) { kGathered[t] += crumbs[ci]; crumbs[ci] = 0; }   // stepping on crumbs collects them
            if (hp > 0 && rn > 200 && side[ci] == 3 - t && kFirstEnemySide[t] == 0) kFirstEnemySide[t] = rn;
            if (hp > 0 && side[ci] == 3 - t && (rn == 250 || rn == 300)) { if (rn == 250) kInEnemy250[t]++; else kInEnemy300[t]++; }
            if (carrying.containsKey(id)) { kCarrierRounds[t]++; if (moved) kCarrierMoves[t]++; }
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
        // bytecodes
        for (int j = 0; j < r.bytecodeIdsLength(); j++) {
            int id = r.bytecodeIds(j), t = team.getOrDefault(id, 0), bc = r.bytecodesUsed(j);
            if (t == 0) continue;
            if (bc > maxBc[t]) maxBc[t] = bc;
            if (bc > roundMaxBc[t]) roundMaxBc[t] = bc;
            if (bc >= BYTECODE_LIMIT) { turnsAtLimit[t]++; if (bytecode) out.printf("r%d %s#%d at the bytecode limit (%d)%n", r.roundId(), tname(t), id, bc); } else if (bc >= BYTECODE_LIMIT * 9 / 10) turnsNear[t]++;
            bcSum[t] += bc; bcTurns[t]++;
        }
        // actions
        for (int j = 0; j < r.actionIdsLength(); j++) {
            int id = r.actionIds(j), a = r.actions(j), tgt = r.actionTargets(j);
            int t = team.getOrDefault(id, 0);
            String desc = null;
            switch (a) {
                case Action.ATTACK: cAttacks[t]++; desc = "attacks #" + tgt; break;
                case Action.HEAL: cHeals[t]++; desc = "heals #" + tgt; break;
                case Action.DIG: if (id >= 0) cDigs[t]++; water[tgt] = true; desc = "digs (" + tgt % W + "," + tgt / W + ")"; break;
                case Action.FILL: cFills[t]++; water[tgt] = false; desc = "fills (" + tgt % W + "," + tgt / W + ")"; break;
                case Action.EXPLOSIVE_TRAP: case Action.WATER_TRAP: case Action.STUN_TRAP:
                    cTrapsHit[t]++; desc = "triggers " + ACTION[a] + " at (" + tgt % W + "," + tgt / W + ")"; break;
                case Action.PICKUP_FLAG:
                    if (rn > 200) {
                        kPostPickups[t]++;
                        int[] fl = flagLoc.get(tgt), fh = flagHome.get(tgt);
                        if (fl != null && fh != null && fl[0] == fh[0] && fl[1] == fh[1]) kFirstGrabs[t]++;
                        else if (Boolean.TRUE.equals(lastDropByDeath.get(tgt))) { kRegrabs[t]++; if (rn > 1200) kRegrabsLate[t]++; }
                        else kRelayPickups[t]++;
                    }
                    if (rn <= 200) sOwnFlagPickupsSetup[t]++;
                    else if (sFirstPickup[t] == 0) sFirstPickup[t] = rn;
                    cPickups[t]++; carrying.put(id, tgt); flagLoc.put(tgt, null);
                    desc = "picks up flag " + tname(flagTeam.getOrDefault(tgt, 0)) + tgt; flagEvent(rn, "PICKUP", id, tgt); break;
                case Action.PLACE_FLAG: {    // id is the FLAG id
                    int fx = tgt % W, fy = tgt / W;
                    boolean wasCarried = carrying.containsValue(id);
                    if (rn == 200 || (rn > 200 && !wasCarried)) { flagHome.put(id, new int[]{fx, fy}); lastDropByDeath.remove(id); }   // default set / returned home
                    { for (Map.Entry<Integer, Integer> ce : carrying.entrySet()) if (ce.getValue() == id && rn > 200) {
                        int holder = ce.getKey(); boolean dies = false;
                        for (int q = 0; q < r.diedIdsLength(); q++) if (r.diedIds(q) == holder) dies = true;
                        if (!dies) sDrops[team.getOrDefault(holder, 0)]++;
                        else {
                            int ht = team.getOrDefault(holder, 0); kCarrierDeaths[ht]++;
                            int[] hl = lastLoc.get(holder);
                            if (hl != null) { int bd = Integer.MAX_VALUE;   // distance to the carrier team's nearest spawn centre
                                for (Map.Entry<Integer, Integer> fe : flagTeam.entrySet()) if (fe.getValue() == ht) {
                                    int cx = fe.getKey() % W, cy = fe.getKey() / W; bd = Math.min(bd, (hl[0] - cx) * (hl[0] - cx) + (hl[1] - cy) * (hl[1] - cy)); }
                                kCarrierDeathDistSum[ht] += (int) Math.round(Math.sqrt(bd)); kCarrierDeathN[ht]++; }
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
                    cCaptures[t]++; if (rn > 1200) kCapturedLate[t]++; carrying.remove(id); flagLoc.put(tgt, null);
                    if (firstCapture[t] < 0) firstCapture[t] = rn;
                    desc = "CAPTURES flag " + tname(flagTeam.getOrDefault(tgt, 0)) + tgt; flagEvent(rn, "CAPTURE", id, tgt); break;
                case Action.GLOBAL_UPGRADE: sUpgrades[t] += (sUpgrades[t].isEmpty() ? "" : "+") + (tgt == 0 ? "ATK" : tgt == 1 ? "HEAL" : "CAP") + "@" + rn; cUpgrades[t]++; desc = "buys upgrade " + tgt; break;
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
            trapTeam.put(tid, tt); trapType.put(tid, ty); trapLoc.put(tid, new int[]{tl.xs(j), tl.ys(j)});
            if (tt >= 1 && tt <= 2 && ty >= 0 && ty < 3) cTraps[tt][ty]++;
            if (inWindow(rn)) out.printf("r%d TRAP %s builds %s at (%d,%d)%n", rn, tname(tt), BUILD[ty], tl.xs(j), tl.ys(j));
        }
        for (int j = 0; j < r.trapTriggeredIdsLength(); j++) {
            int tid = r.trapTriggeredIds(j);
            if (inWindow(rn)) { int[] l = trapLoc.get(tid); out.printf("r%d TRAP %s %s at (%d,%d) triggered%n", rn,
                    tname(trapTeam.getOrDefault(tid, 0)), BUILD[trapType.getOrDefault(tid, 0)], l == null ? -1 : l[0], l == null ? -1 : l[1]); }
            trapLoc.remove(tid);
        }
        // deaths
        for (int j = 0; j < r.diedIdsLength(); j++) {
            int id = r.diedIds(j), t = team.getOrDefault(id, 0);
            cDeaths[t]++; if (firstDeath[t] < 0) firstDeath[t] = rn;
            Integer f = carrying.remove(id);
            if (f != null) kCarrierDeaths[t]++;   // only if no drop event preceded (normally the PLACE event counts it)
            int[] l = lastLoc.get(id);
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
        }
        if (every > 0 && rn % every == 0) printEvery(rn);
        if (metricsEvery > 0 && rn % metricsEvery == 0) for (int t = 1; t <= 2; t++) out.println(metricRow(rn, t));
        if (rn == mapAt) printMap(rn);
        if (survey) surveyTick(rn);
        if (rn == 199 && capMode) for (Map.Entry<Integer, int[]> e : lastLoc.entrySet()) {
            int t = team.getOrDefault(e.getKey(), 0); if (t == 0) continue;
            int[] l = e.getValue(); boolean near = false;
            for (int dx = -2; dx <= 2 && !near; dx++) for (int dy = -2; dy <= 2 && !near; dy++) {
                int x = l[0] + dx, y = l[1] + dy;
                if (x >= 0 && y >= 0 && x < W && y < H && dam[idx(x, y)]) near = true; }
            if (near) kDamStage199[t]++;
        }
        for (int t = 1; t <= 2; t++) { kAliveSum[t] += alive[t]; if (rn == 200) kGathered200[t] = kGathered[t]; if (rn == 400) kGathered400[t] = kGathered[t]; }
        kRounds++;
        totalRounds = rn;
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
        if (capMode) {
            out.println("team,name,won,rounds,wintype,gathered200,gathered400,firstEnemySide,inEnemy250,inEnemy300,firstFlagSight,pickups,captured,carrierDeaths,carrierRounds,carrierMoves,enemyCarrierKills,trapsBuilt,trapsHit,kills,deaths,meanAlive,postPickups,firstGrabs,regrabs,relayPickups,carrierDeathDist,damStage199,enemyRegrabs,enemyFirstGrabs,regrabsLate,capturedLate");
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
                        + "," + kRegrabs[o] + "," + kFirstGrabs[o] + "," + kRegrabsLate[t] + "," + kCapturedLate[t]);
            }
        }
        if (levelsMode) for (int t = 1; t <= 2; t++) {   // last known (attack/build/heal) levels per robot
            Map<String, Integer> hist = new TreeMap<>(); int n = 0, atk4 = 0, heal4 = 0, build4 = 0, atkSum = 0, healSum = 0;
            for (Map.Entry<Integer, int[]> e : levels.entrySet()) {
                if (team.getOrDefault(e.getKey(), 0) != t) continue;
                int[] l = e.getValue(); n++; atkSum += l[0]; healSum += l[2];
                if (l[0] >= 4) atk4++; if (l[2] >= 4) heal4++; if (l[1] >= 4) build4++;
                hist.merge("a" + l[0] + "b" + l[1] + "h" + l[2], 1, Integer::sum);
            }
            out.printf("levels %s: robots=%d meanAtk=%.2f meanHeal=%.2f atkMastery=%d healMastery=%d buildMastery=%d  %s%n", tname(t), n,
                    n > 0 ? (double) atkSum / n : 0.0, n > 0 ? (double) healSum / n : 0.0, atk4, heal4, build4, hist);
        }
        if (bytecode) for (int t = 1; t <= 2; t++)
            out.printf("bytecode %s: max=%d mean=%.0f turnsAtLimit=%d turnsNear90=%d turns=%d%n", tname(t), maxBc[t],
                    bcTurns[t] > 0 ? (double) bcSum[t] / bcTurns[t] : 0.0, turnsAtLimit[t], turnsNear[t], bcTurns[t]);
        if (navstats) {
            int passable = 0; for (int i = 0; i < W * H; i++) if (!wall[i]) passable++;
            for (int t = 1; t <= 2; t++) {
                int cov = 0; for (boolean v : visited[t]) if (v) cov++;
                out.printf("nav %s: moves=%d robotRounds=%d movesPerRobotRound=%.3f oscillationABA=%.1f%% stillRounds=%.1f%% coverage=%.1f%%%n",
                        tname(t), cMoves[t], robotRounds[t], robotRounds[t] > 0 ? (double) cMoves[t] / robotRounds[t] : 0.0,
                        cMoves[t] > 0 ? 100.0 * osc[t] / cMoves[t] : 0.0, robotRounds[t] > 0 ? 100.0 * stillRounds[t] / robotRounds[t] : 0.0,
                        100.0 * cov / Math.max(1, passable));
            }
        }
    }
}
