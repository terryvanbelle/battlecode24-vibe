package cm;

import battlecode.schema.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.GZIPInputStream;

/** The upper-tier study's combat-micro analyser (2026-10-07; kept in the repo for the g7kite registration's reads, review
 *  2026-10-07: the code is the study's src6 version unchanged). args: replay ourTeam(A|B) tag; run it with run-cm.sh.
 *  Execution-order reconstruction as ReplayDump's step census: ranks from round 1's bytecode table, earlier ranks at their
 *  end-of-round tile, later ranks at the previous round's tile, HP through the round's attacks and heals in action order. */
public class Cm {
    static final int SN = 100;
    static final int[] ATK_SKILL = {0, 5, 7, 10, 30, 35, 60}, HEAL_SKILL = {0, 3, 5, 7, 10, 15, 25};
    static int us, W, H, lastRound, winner;
    static boolean[] wall, water;
    static Map<String,long[]> aggR = new TreeMap<>(), aggF = new TreeMap<>(), aggQ = new TreeMap<>();
    static ArrayList<int[]> fdec = new ArrayList<>();
    static BitSet[] atkR = new BitSet[100];
    // Cm6 additions (KITE_REACH_W premise check 2026-10-07): kills per rank, captures per side, T decisions with masks
    static BitSet[] killR = new BitSet[100];
    static ArrayList<int[]> caps = new ArrayList<>();          // {round, side}
    static ArrayList<long[]> tdec = new ArrayList<>();         // {R, rn, keyId, allyLo, allyHi, enemyLo, enemyHi, dmgSlot}
    static long mAlo, mAhi, mElo, mEhi;                        // masks of the current decision (within dist2 20 of the start tile)
    static String tag;
    static PrintStream out = System.out;
    static Map<Integer, Integer> rankOf = new HashMap<>();
    static boolean[] pAlive = new boolean[SN], pCarry = new boolean[SN], pUpgAtk = new boolean[3], pUpgHeal = new boolean[3];
    static int[] pX = new int[SN], pY = new int[SN], pHP = new int[SN], pACD = new int[SN], pMCD = new int[SN], pAtk = new int[SN], pHeal = new int[SN];
    static Map<Integer, Integer> carrying = new HashMap<>();   // robot id -> flag id
    @SuppressWarnings("unchecked") static List<Integer>[] deathR = new List[SN];
    static int[] pendingDec = new int[SN];
    static ArrayList<int[]> dec = new ArrayList<>();
    static ArrayList<int[]> atk = new ArrayList<>();
    static ArrayList<int[]> heal = new ArrayList<>();
    static ArrayList<int[]> wounded = new ArrayList<>();
    static ArrayList<int[]> episodes = new ArrayList<>();
    // episode state per rank
    static int[] epStart = new int[SN], epLast = new int[SN], epHits = new int[SN];
    @SuppressWarnings("unchecked") static Set<Integer>[] epAtk = new Set[SN];
    static int[] lastAttackers = new int[SN];
    static long[][] tot = new long[2][40];

    static int rankTeam(int k) { return k % 2 == 0 ? 1 : 2; }
    static int side(int k) { return rankTeam(k) == us ? 0 : 1; }
    static int d2(int ax, int ay, int bx, int by) { return (ax - bx) * (ax - bx) + (ay - by) * (ay - by); }
    static int hitOf(int k) { return Math.round((150 + (pUpgAtk[rankTeam(k)] ? 60 : 0)) * ((float) ATK_SKILL[Math.min(6, Math.max(0, pAtk[k]))] / 100 + 1)); }
    static int healOf(int k) { return Math.round((80 + (pUpgHeal[rankTeam(k)] ? 50 : 0)) * ((float) HEAL_SKILL[Math.min(6, Math.max(0, pHeal[k]))] / 100 + 1)); }

    public static void main(String[] a) throws Exception {
        us = a[1].equals("A") ? 1 : 2; tag = a[2];
        Arrays.fill(pendingDec, -1); Arrays.fill(epStart, -1);
        for (int k = 0; k < SN; k++) { deathR[k] = new ArrayList<>(); epAtk[k] = new HashSet<>(); atkR[k] = new BitSet(); killR[k] = new BitSet(); }
        byte[] raw; try (InputStream in = new FileInputStream(a[0])) { raw = readAll(in); }
        if ((raw[0] & 0xff) == 0x1f && (raw[1] & 0xff) == 0x8b) raw = readAll(new GZIPInputStream(new ByteArrayInputStream(raw)));
        GameWrapper gw = GameWrapper.getRootAsGameWrapper(ByteBuffer.wrap(raw));
        for (int i = 0; i < gw.eventsLength(); i++) {
            EventWrapper ev = gw.events(i);
            if (ev.eType() == Event.MatchHeader) { MatchHeader mh = (MatchHeader) ev.e(new MatchHeader()); GameMap m = mh.map(); W = m.size().x(); H = m.size().y(); wall = new boolean[W*H]; water = new boolean[W*H]; for (int q = 0; q < W*H; q++) { wall[q] = m.walls(q); water[q] = m.water(q); } }
            else if (ev.eType() == Event.Round) tick((Round) ev.e(new Round()));
            else if (ev.eType() == Event.MatchFooter) { MatchFooter mf = (MatchFooter) ev.e(new MatchFooter()); winner = mf.winner(); }
        }
        finish();
    }
    static byte[] readAll(InputStream in) throws IOException { ByteArrayOutputStream bo = new ByteArrayOutputStream(); byte[] b = new byte[1 << 16]; int n; while ((n = in.read(b)) > 0) bo.write(b, 0, n); return bo.toByteArray(); }

    static void tick(Round r) {
        int rn = r.roundId(); lastRound = rn;
        if (rankOf.isEmpty()) for (int j = 0; j < r.bytecodeIdsLength() && j < SN; j++) rankOf.put(r.bytecodeIds(j), j);
        boolean[] cAlive = new boolean[SN], cDied = new boolean[SN], cSp = new boolean[SN], cIn = new boolean[SN], struck = new boolean[SN], healed = new boolean[SN];
        int[] cX = new int[SN], cY = new int[SN], cHP = new int[SN], strikeT = new int[SN];
        Arrays.fill(strikeT, -1);
        Set<Integer> diedIds = new HashSet<>();
        for (int j = 0; j < r.diedIdsLength(); j++) { diedIds.add(r.diedIds(j)); Integer k = rankOf.get(r.diedIds(j)); if (k != null) { cDied[k] = true; deathR[k].add(rn); } }
        SpawnedBodyTable sb = r.spawnedBodies();
        if (sb != null) for (int j = 0; j < sb.robotIdsLength(); j++) { Integer k = rankOf.get(sb.robotIds(j)); if (k != null) cSp[k] = true; }
        VecTable locs = r.robotLocs();
        for (int j = 0; j < r.robotIdsLength(); j++) {
            Integer k = rankOf.get(r.robotIds(j)); if (k == null) continue;
            cIn[k] = true; cX[k] = locs.xs(j); cY[k] = locs.ys(j); cHP[k] = r.robotHealths(j); cAlive[k] = !cDied[k] && r.robotHealths(j) > 0;
        }
        int na = r.actionIdsLength();
        int[] aR = new int[na], aT = new int[na], aG = new int[na];
        for (int j = 0; j < na; j++) {
            Integer k = rankOf.get(r.actionIds(j)); int ac = r.actions(j);
            aR[j] = k == null ? -1 : k; aT[j] = ac; aG[j] = -1;
            if (k != null && (ac == Action.ATTACK || ac == Action.HEAL)) { Integer g = rankOf.get(r.actionTargets(j)); if (g != null) aG[j] = g; }
            if (k != null && ac == Action.ATTACK && aG[j] >= 0) { struck[k] = true; strikeT[k] = aG[j]; }
            if (k != null && ac == Action.HEAL && aG[j] >= 0) healed[k] = true;
        }
        if (rn > 200) {
            boolean[] aliveAt = new boolean[SN]; int[] hpAt = new int[SN], xAt = new int[SN], yAt = new int[SN];
            for (int k = 0; k < SN; k++) { aliveAt[k] = pAlive[k]; hpAt[k] = pAlive[k] ? pHP[k] : 1000; xAt[k] = pX[k]; yAt[k] = pY[k]; }
            int j = 0;
            for (int R = 0; R < SN; R++) {
                for (; j < na && (aR[j] < 0 || aR[j] < R); j++) applyAction(j, aR, aT, aG, rn, aliveAt, hpAt, xAt, yAt, cX, cY, cIn);
                pendingDec[R] = -1;
                int t = rankTeam(R);
                if (pAlive[R] && aliveAt[R] && !pCarry[R]) decision(R, t, rn, aliveAt, hpAt, xAt, yAt, cX, cY, cIn, struck, healed, strikeT);
                for (; j < na && (aR[j] < 0 || aR[j] <= R); j++) applyAction(j, aR, aT, aG, rn, aliveAt, hpAt, xAt, yAt, cX, cY, cIn);
                aliveAt[R] = (pAlive[R] || cSp[R]) && hpAt[R] > 0;
                if (cIn[R]) { xAt[R] = cX[R]; yAt[R] = cY[R]; }
            }
            for (; j < na; j++) applyAction(j, aR, aT, aG, rn, aliveAt, hpAt, xAt, yAt, cX, cY, cIn);
            // episodes: close on death / full HP / 10 quiet rounds
            for (int k = 0; k < SN; k++) {
                if (epStart[k] < 0) continue;
                if (cDied[k]) closeEp(k, rn, 2);
                else if (cIn[k] && cHP[k] >= 1000) closeEp(k, rn, 1);
                else if (rn - epLast[k] >= 10) closeEp(k, rn, 0);
            }
            // wounded transitions: end-of-round HP < 300 from >= 300, an enemy within dist2 20
            for (int k = 0; k < SN; k++) {
                if (!cAlive[k] || !cIn[k] || cHP[k] >= 300) continue;
                if (!(pAlive[k] && pHP[k] >= 300)) continue;
                int ne = 0, na20 = 0;
                for (int q = 0; q < SN; q++) { if (!cAlive[q] || !cIn[q] || q == k) continue; int dd = d2(cX[k], cY[k], cX[q], cY[q]); if (dd <= 20) { if (rankTeam(q) != rankTeam(k)) ne++; else na20++; } }
                if (ne > 0) wounded.add(new int[]{rn, k, ne, na20, pCarry[k] ? 1 : 0});
            }
        }
        { VecTable dl = r.digLocations(); if (dl != null) for (int q = 0; q < dl.xsLength(); q++) water[dl.xs(q) + dl.ys(q) * W] = true;
          VecTable fl = r.fillLocations(); if (fl != null) for (int q = 0; q < fl.xsLength(); q++) water[fl.xs(q) + fl.ys(q) * W] = false; }
        // carrying (end of round)
        for (int jj = 0; jj < na; jj++) {
            int id = r.actionIds(jj), ac = r.actions(jj), tg = r.actionTargets(jj);
            if (ac == Action.PICKUP_FLAG) carrying.put(id, tg);
            else if (ac == Action.PLACE_FLAG) carrying.values().removeIf(v -> v == id);
            else if (ac == Action.CAPTURE_FLAG) { carrying.remove(id); Integer ck = rankOf.get(id); if (ck != null) caps.add(new int[]{rn, side(ck)}); }
        }
        for (int id : diedIds) carrying.remove(id);
        for (int jj = 0; jj < r.robotIdsLength(); jj++) {
            Integer k = rankOf.get(r.robotIds(jj)); if (k == null) continue;
            pHP[k] = r.robotHealths(jj); pACD[k] = r.robotActionCooldowns(jj); pMCD[k] = r.robotMoveCooldowns(jj);
            pAtk[k] = r.attackLevels(jj); pHeal[k] = r.healLevels(jj); pX[k] = cX[k]; pY[k] = cY[k];
        }
        for (int k = 0; k < SN; k++) { pAlive[k] = cAlive[k]; pCarry[k] = false; }
        for (int id : carrying.keySet()) { Integer k = rankOf.get(id); if (k != null) pCarry[k] = true; }
        for (int jj = 0; jj < na; jj++) if (aR[jj] >= 0 && aT[jj] == Action.GLOBAL_UPGRADE) {
            int tg = r.actionTargets(jj), t = rankTeam(aR[jj]);
            if (tg == 0) pUpgAtk[t] = true; else if (tg == 1) pUpgHeal[t] = true;
        }
    }

    static void closeEp(int k, int rn, int how) {
        episodes.add(new int[]{side(k), epStart[k], rn, epHits[k], epAtk[k].size(), how});
        epStart[k] = -1; epHits[k] = 0; epAtk[k].clear();
    }

    /** One action, applied in order; ATTACK and HEAL are recorded with the state just before them. */
    static void applyAction(int j, int[] aR, int[] aT, int[] aG, int rn, boolean[] aliveAt, int[] hpAt, int[] xAt, int[] yAt, int[] cX, int[] cY, boolean[] cIn) {
        int R = aR[j], T = aG[j];
        if (R < 0 || T < 0) return;
        if (aT[j] == Action.ATTACK) {
            int h0 = hpAt[T], hit = hitOf(R);
            // attack position: the start tile if the target is in reach of it, else the end tile
            int sx = pX[R], sy = pY[R];
            if (!(pAlive[R] && d2(sx, sy, xAt[T], yAt[T]) <= 4) && cIn[R]) { sx = cX[R]; sy = cY[R]; }
            int nOpt = 0, minHp = 99999; boolean killAvail = false;
            for (int k = 0; k < SN; k++) {
                if (!aliveAt[k] || rankTeam(k) == rankTeam(R)) continue;
                if (d2(sx, sy, xAt[k], yAt[k]) > 4) continue;
                nOpt++; if (hpAt[k] < minHp) minHp = hpAt[k]; if (hpAt[k] <= hit) killAvail = true;
            }
            boolean tReady = pACD[T] < 20, tFrozen = pACD[T] >= 30 && pMCD[T] >= 30;
            // target threat: its allies within dist2 10 of the attacker's tile (who can hit back)
            int back = 0;
            for (int k = 0; k < SN; k++) if (aliveAt[k] && rankTeam(k) == rankTeam(T) && d2(sx, sy, xAt[k], yAt[k]) <= 10) back++;
            boolean kill = h0 <= hit;
            boolean stand = pAlive[R] && d2(pX[R], pY[R], xAt[T], yAt[T]) <= 4;
            atkR[R].set(rn);
            if (kill) killR[R].set(rn);
            atk.add(new int[]{rn, R, T, h0, hit, nOpt, minHp, killAvail ? 1 : 0, tReady ? 1 : 0, tFrozen ? 1 : 0, pCarry[T] ? 1 : 0, kill ? 1 : 0, back, T > R ? 1 : 0, stand ? 1 : 0});
            hpAt[T] -= hit;
            if (hpAt[T] <= 0) { aliveAt[T] = false; if (pendingDec[T] >= 0) dec.get(pendingDec[T])[18] = 1; }
            if (pendingDec[T] >= 0) { int[] d = dec.get(pendingDec[T]); d[9]++; d[12] += hit; }
            if (epStart[T] < 0) { epStart[T] = rn; }
            epLast[T] = rn; epHits[T]++; epAtk[T].add(R);
            if (pendingDec[R] >= 0) { int[] d = dec.get(pendingDec[R]); if (kill) d[10] = 1; d[11] = T; }
        } else if (aT[j] == Action.HEAL) {
            int h0 = hpAt[T], amt = healOf(R);
            int tThreat = 0, hThreat = 0;
            int hx = pX[R], hy = pY[R];
            if (!(pAlive[R] && d2(hx, hy, xAt[T], yAt[T]) <= 4) && cIn[R]) { hx = cX[R]; hy = cY[R]; }
            for (int k = 0; k < SN; k++) {
                if (!aliveAt[k] || rankTeam(k) == rankTeam(R)) continue;
                if (d2(xAt[T], yAt[T], xAt[k], yAt[k]) <= 10) tThreat++;
                if (d2(hx, hy, xAt[k], yAt[k]) <= 10) hThreat++;
            }
            heal.add(new int[]{rn, R, T, h0, amt, tThreat, hThreat, pCarry[T] ? 1 : 0});
            hpAt[T] = Math.min(1000, hpAt[T] + amt);
        }
    }

    static int hpb(int hp) { return hp >= 700 ? 0 : hp >= 300 ? 1 : 2; }

    static void decision(int R, int t, int rn, boolean[] aliveAt, int[] hpAt, int[] xAt, int[] yAt, int[] cX, int[] cY, boolean[] cIn, boolean[] struck, boolean[] healed, int[] strikeT) {
        int sx = pX[R], sy = pY[R];
        int minD0 = Integer.MAX_VALUE, nE10 = 0, nE20 = 0, nA10 = 0, nA20 = 0, nA2 = 0; boolean car = false;
        mAlo = mAhi = mElo = mEhi = 0;
        for (int k = 0; k < SN; k++) {
            if (k == R || !aliveAt[k]) continue;
            int dd = d2(sx, sy, xAt[k], yAt[k]);
            if (rankTeam(k) != t) {
                if (dd < minD0) minD0 = dd;
                if (dd <= 10) nE10++;
                if (dd <= 20) { nE20++; if (pCarry[k]) car = true; if (k < 64) mElo |= 1L << k; else mEhi |= 1L << (k - 64); }
            } else {
                if (dd <= 10) nA10++;
                if (dd <= 20) { nA20++; if (k < 64) mAlo |= 1L << k; else mAhi |= 1L << (k - 64); }
                if (dd <= 2) nA2++;
            }
        }
        if (minD0 > 20 || car) return;
        int ex = cIn[R] ? cX[R] : sx, ey = cIn[R] ? cY[R] : sy;
        boolean moved = ex != sx || ey != sy;
        int minD1 = Integer.MAX_VALUE, nE10e = 0;
        for (int k = 0; k < SN; k++) {
            if (k == R || !aliveAt[k] || rankTeam(k) == t) continue;
            int dd = d2(ex, ey, xAt[k], yAt[k]);
            if (dd < minD1) minD1 = dd;
            if (dd <= 10) nE10e++;
        }
        int cat = minD0 <= 4 ? 0 : minD0 <= 10 ? 1 : 2;
        int aS = pACD[R] < 20 ? 0 : pACD[R] < 30 ? 1 : 2, mR = pMCD[R] < 20 ? 1 : 0;
        int rdy = aS * 2 + mR;   // 0/1 action ready (move no/yes), 2/3 action ready next turn, 4/5 later
        int hp = hpAt[R];
        int neb = cat == 2 ? Math.min(nE20, 4) : Math.min(nE10, 3);              // R20: enemies in vision (cap 4); else threats (cap 3)
        int bal = cat == 2 ? (nA20 + 1) - nE20 : (nA10 + 1) - nE10;
        int bb = bal <= -1 ? 0 : bal == 0 ? 1 : bal <= 2 ? 2 : 3;
        int ch;
        if (struck[R]) ch = !moved ? 0 : minD0 > 4 ? 1 : minD1 > 4 ? 2 : 3;
        else if (healed[R]) ch = 4;
        else ch = minD1 < minD0 ? 5 : minD1 == minD0 ? 6 : 7;
        int phase = rn <= 600 ? 0 : 1;
        int[] d = new int[]{R, rn, side(R), cat, rdy, hpb(hp), neb, bb, ch, 0, 0, -1, 0, Math.min(minD1, 99), hp, phase, nE10e, nA2, 0};
        if (mR == 1 && (cat <= 1 || aS == 1)) neighbourScan(R, t, rn, sx, sy, cat, rdy, hp, phase, ch, aliveAt, hpAt, xAt, yAt, strikeT, d);
        dec.add(d);
        pendingDec[R] = dec.size() - 1;
    }

    static final int[] DX = {1, 1, 0, -1, -1, -1, 0, 1, 0}, DY = {0, 1, 1, 1, 0, -1, -1, -1, 0};
    /** Tiles R could stand on after its move: the 8 neighbours (free: on the map, no wall, no water, nobody on it at R's turn) and
     *  its own tile (index 8). */
    static void neighbourScan(int R, int t, int rn, int sx, int sy, int cat, int rdy, int hp, int phase, int ch, boolean[] aliveAt, int[] hpAt, int[] xAt, int[] yAt, int[] strikeT, int[] d) {
        int my = hitOf(R);
        int[] th = new int[9], mnD = new int[9], inR = new int[9], low = new int[9], adj = new int[9]; boolean[] free = new boolean[9], kil = new boolean[9];
        for (int i = 0; i < 9; i++) {
            int x = sx + DX[i], y = sy + DY[i];
            if (i < 8) {
                if (x < 0 || y < 0 || x >= W || y >= H || wall[x + y * W] || water[x + y * W]) continue;
                boolean occ = false;
                for (int k = 0; k < SN; k++) if (k != R && aliveAt[k] && xAt[k] == x && yAt[k] == y) { occ = true; break; }
                if (occ) continue;
            }
            free[i] = true; mnD[i] = Integer.MAX_VALUE; low[i] = 99999;
            for (int k = 0; k < SN; k++) {
                if (k == R || !aliveAt[k]) continue;
                int dd = d2(x, y, xAt[k], yAt[k]);
                if (rankTeam(k) != t) { if (dd <= 10) th[i]++; if (dd <= 4) { inR[i]++; if (hpAt[k] < low[i]) low[i] = hpAt[k]; if (hpAt[k] <= my) kil[i] = true; } if (dd < mnD[i]) mnD[i] = dd; }
                else if (dd <= 2) adj[i]++;
            }
        }
        if (cat == 0) {
            boolean outAvail = false, outNoWorse = false;
            for (int i = 0; i < 8; i++) if (free[i] && mnD[i] > 4) { outAvail = true; if (th[i] <= th[8]) outNoWorse = true; }
            // our kite/hold score (Micro.fight, goal == null, not hurt): -th*1000 + adj*10 + (minD 11..20 ? 50 : 0) (+1 for staying)
            boolean hurt = hp < 300; int best = Integer.MIN_VALUE; boolean bestOut = false, bestIn = false;
            for (int i = 0; i < 9; i++) {
                if (!free[i]) continue;
                int sc = -th[i] * 1000 + adj[i] * 10 + (mnD[i] >= 11 && mnD[i] <= 20 ? 50 : 0) - (hurt && mnD[i] < 20 ? 200 : 0) + (i == 8 ? 1 : 0);
                if (sc > best) { best = sc; bestOut = mnD[i] > 4; bestIn = mnD[i] <= 4; }
                else if (sc == best) { if (mnD[i] > 4) bestOut = true; else bestIn = true; }
            }
            int kiteStay = bestIn && !bestOut ? 1 : bestIn ? 2 : 0;    // 1: every best tile in reach; 2: tie; 0: a best tile out of reach
            String key = d[2] + "," + phase + "," + rdy + "," + d[5] + "," + (outAvail ? 1 : 0) + "," + (outNoWorse ? 1 : 0) + "," + kiteStay;
            fdec.add(new int[]{0, ch, R, rn, aggKeyId('R', key)});
        } else if (cat == 1 && rdy == 1) {
            int nReach = 0, bestLow = 99999; boolean killAvail = false; int minThReach = 99, ourPickLow = -1, ourPickBest = Integer.MIN_VALUE;
            for (int i = 0; i < 8; i++) {
                if (!free[i] || inR[i] == 0) continue;
                nReach++; if (low[i] < bestLow) bestLow = low[i]; if (kil[i]) killAvail = true;
                int sc = 10000 - th[i] * 100 + adj[i] * 10;                  // our engage score
                if (sc > ourPickBest) { ourPickBest = sc; ourPickLow = low[i]; }
            }
            int struckHp = strikeT[R] >= 0 ? hpAt[strikeT[R]] : -1;
            int ourGap = ourPickLow < 0 ? -1 : ourPickLow - bestLow;          // how much higher our engage tile's weakest target is
            int gb = ourGap < 0 ? 9 : ourGap == 0 ? 0 : ourGap <= 150 ? 1 : 2;
            String key = d[2] + "," + phase + "," + d[5] + "," + (nReach == 0 ? 0 : nReach == 1 ? 1 : 2) + "," + (killAvail ? 1 : 0) + "," + gb;
            fdec.add(new int[]{1, ch, R, rn, aggKeyId('Q', key), struckHp, bestLow, struckHp >= 0 && struckHp <= my ? 1 : 0});
        }
        if (cat >= 1 && rdy == 3) {     // ready next turn: is a tile one step from an enemy (dist2 5-10) free, and how threatened?
            boolean closeAvail = false; int closeTh = 99;
            for (int i = 0; i < 9; i++) if (free[i] && mnD[i] >= 5 && mnD[i] <= 10) { closeAvail = true; closeTh = Math.min(closeTh, th[i]); }
            String key = d[2] + "," + phase + "," + cat + "," + d[5] + "," + (closeAvail ? 1 : 0) + "," + (closeTh <= 1 ? 1 : 0) + "," + d[7];
            fdec.add(new int[]{2, ch, R, rn, aggKeyId('P', key)});
        }
        if (cat <= 1) {   // Cm6 T decisions: base kite score vs the KITE_REACH_W 300 score, which tiles are best, where it ended
            boolean hurt = hp < 300; int best = Integer.MIN_VALUE, bestL = Integer.MIN_VALUE;
            boolean bIn = false, bOut = false, lIn = false, lOut = false, anyIn = false, anyOut = false; int inRmaxIn = 0;
            for (int i = 0; i < 9; i++) {
                if (!free[i]) continue;
                boolean in = mnD[i] <= 4; if (in) anyIn = true; else anyOut = true;
                int sc = -th[i] * 1000 + adj[i] * 10 + (mnD[i] >= 11 && mnD[i] <= 20 ? 50 : 0) - (hurt && mnD[i] < 20 ? 200 : 0) + (i == 8 ? 1 : 0);
                int sl = sc - inR[i] * 300;
                if (sc > best) { best = sc; bIn = in; bOut = !in; } else if (sc == best) { if (in) bIn = true; else bOut = true; }
                if (sl > bestL) { bestL = sl; lIn = in; lOut = !in; } else if (sl == bestL) { if (in) lIn = true; else lOut = true; }
            }
            for (int i = 0; i < 9; i++) if (free[i] && mnD[i] <= 4) {   // the in-reach tile(s) at the base best score: their reach count
                int sc = -th[i] * 1000 + adj[i] * 10 + (mnD[i] >= 11 && mnD[i] <= 20 ? 50 : 0) - (hurt && mnD[i] < 20 ? 200 : 0) + (i == 8 ? 1 : 0);
                if (sc == best && inR[i] > inRmaxIn) inRmaxIn = inR[i];
            }
            int ks = bIn && !bOut ? 1 : bIn ? 2 : 0, lv = lIn && !lOut ? 1 : lIn ? 2 : 0;
            int struckR = d[8] <= 3 ? 1 : 0, endIn = d[13] <= 4 ? 1 : 0;
            int h6 = hp >= 850 ? 0 : hp >= 700 ? 1 : hp >= 500 ? 2 : hp >= 300 ? 3 : hp >= 150 ? 4 : 5;
            String key = d[2] + "," + cat + "," + struckR + "," + (rdy / 2) + "," + h6 + "," + ks + "," + lv + "," + endIn + "," + Math.min(inRmaxIn, 4) + "," + (anyOut ? 1 : 0);
            tdec.add(new long[]{R, rn, aggKeyId('T', key), mAlo, mAhi, mElo, mEhi, dec.size()});
        }
    }
    static Map<String,Integer> keyIds = new HashMap<>(); static ArrayList<String> keyNames = new ArrayList<>();
    static int aggKeyId(char ty, String key) { String kk = ty + "," + key; Integer id = keyIds.get(kk); if (id == null) { id = keyNames.size(); keyIds.put(kk, id); keyNames.add(kk); } return id; }

    static boolean diedIn(int k, int a, int b) { for (int x : deathR[k]) if (x >= a && x <= b) return true; return false; }

    static void finish() {
        // open episodes at the end are dropped
        Map<String, long[]> agg = new TreeMap<>();
        for (int[] d : dec) {
            String key = d[2] + "," + d[15] + "," + d[3] + "," + d[4] + "," + d[5] + "," + d[7];
            long[] v = agg.computeIfAbsent(key, k -> new long[9 * 9]);
            int c = d[8], o = c * 9;
            boolean died3 = diedIn(d[0], d[1], d[1] + 2);
            boolean tgtDied = d[11] >= 0 && diedIn(d[11], d[1], d[1] + 2);
            v[o]++; if (died3) v[o + 1]++; v[o + 2] += d[9]; if (d[9] > 0) v[o + 3]++; if (d[10] == 1) v[o + 4]++; if (tgtDied) v[o + 5]++;
            if (d[13] <= 10) v[o + 6]++;                // ends the turn within one step of an enemy (dist2 <= 10)
            if (d[13] <= 4) v[o + 7]++;                 // ends the turn in reach
            if (atkR[d[0]].get(d[1] + 1) || atkR[d[0]].get(d[1] + 2)) v[o + 8]++;   // strikes in one of its next two turns
        }
        for (Map.Entry<String, long[]> e : agg.entrySet()) {
            StringBuilder b = new StringBuilder("K," + tag + "," + e.getKey());
            for (long x : e.getValue()) b.append(',').append(x);
            out.println(b);
        }
        // R and Q rows: per key, per choice: n, hit before next turn (from the matching dec record), died3; Q also struck target died<=2, struck a killable
        Map<String,long[]> agg2 = new TreeMap<>();
        Map<Long,int[]> decByRR = new HashMap<>();
        for (int[] d : dec) decByRR.put(((long) d[0] << 32) | d[1], d);
        for (int[] f : fdec) {
            String kk = keyNames.get(f[4]);
            long[] v = agg2.computeIfAbsent(kk, k -> new long[9 * 6]);
            int o = f[1] * 6; int[] d = decByRR.get(((long) f[2] << 32) | f[3]);
            v[o]++;
            if (d != null && d[9] > 0) v[o + 1]++;
            if (diedIn(f[2], f[3], f[3] + 2)) v[o + 2]++;
            if (f[0] == 1) {
                if (d != null && d[11] >= 0 && diedIn(d[11], f[3], f[3] + 2)) v[o + 3]++;
                if (f[7] == 1) v[o + 4]++;
                if (f[5] >= 0 && f[6] < 99999) v[o + 5] += Math.max(0, f[5] - f[6]);
            } else {
                if (atkR[f[2]].get(f[3] + 1) || atkR[f[2]].get(f[3] + 2)) v[o + 3]++;
                if (d != null && d[13] <= 4) v[o + 4]++;
                if (d != null && d[13] <= 10) v[o + 5]++;
            }
        }
        for (Map.Entry<String, long[]> e : agg2.entrySet()) {
            StringBuilder b = new StringBuilder(e.getKey().substring(0, 1) + "," + tag + "," + e.getKey().substring(2));
            for (long x : e.getValue()) b.append(',').append(x);
            out.println(b);
        }
        // Cm6 T rows: per key: n, hit, d3, d5, d10, d20, str2, s10, k10, allyD10, enemyD10, nA20, nE20, dmg, allyD20, enemyD20, s20, k20, killedBeforeNextTurn, d2
        Map<String,long[]> aggT = new TreeMap<>();
        for (long[] q : tdec) {
            int R = (int) q[0], rn = (int) q[1];
            long[] v = aggT.computeIfAbsent(keyNames.get((int) q[2]), k -> new long[20]);
            int[] d = (int) q[7] < dec.size() ? dec.get((int) q[7]) : null;
            v[0]++;
            if (d != null && d[0] == R && d[1] == rn) { if (d[9] > 0) v[1]++; v[13] += d[12]; if (d[18] == 1) v[18]++; }
            if (diedIn(R, rn, rn + 1)) v[19]++;
            if (diedIn(R, rn, rn + 2)) v[2]++; if (diedIn(R, rn, rn + 4)) v[3]++; if (diedIn(R, rn, rn + 9)) v[4]++; if (diedIn(R, rn, rn + 19)) v[5]++;
            if (atkR[R].get(rn + 1) || atkR[R].get(rn + 2)) v[6]++;
            for (int x = rn + 1; x <= rn + 20; x++) { if (atkR[R].get(x)) { v[16]++; if (x <= rn + 10) v[7]++; } if (killR[R].get(x)) { v[17]++; if (x <= rn + 10) v[8]++; } }
            for (int k = 0; k < SN; k++) {
                boolean a = k < 64 ? ((q[3] >>> k) & 1) != 0 : ((q[4] >>> (k - 64)) & 1) != 0;
                boolean e = k < 64 ? ((q[5] >>> k) & 1) != 0 : ((q[6] >>> (k - 64)) & 1) != 0;
                if (a) { v[11]++; if (diedIn(k, rn, rn + 9)) v[9]++; if (diedIn(k, rn, rn + 19)) v[14]++; }
                if (e) { v[12]++; if (diedIn(k, rn, rn + 9)) v[10]++; if (diedIn(k, rn, rn + 19)) v[15]++; }
            }
        }
        for (Map.Entry<String, long[]> e : aggT.entrySet()) {
            StringBuilder b = new StringBuilder("T," + tag + "," + e.getKey().substring(2));
            for (long x : e.getValue()) b.append(',').append(x);
            out.println(b);
        }
        // Cm6 D rows: per side: deaths <=600, <=1000, all; captures <=600, <=1000, all; last round; won
        for (int s = 0; s < 2; s++) {
            long[] v = new long[6];
            for (int k = 0; k < SN; k++) if (side(k) == s) for (int x : deathR[k]) { if (x <= 600) v[0]++; if (x <= 1000) v[1]++; v[2]++; }
            for (int[] c : caps) if (c[1] == s) { if (c[0] <= 600) v[3]++; if (c[0] <= 1000) v[4]++; v[5]++; }
            StringBuilder b = new StringBuilder("D," + tag + "," + s);
            for (long x : v) b.append(',').append(x);
            b.append(',').append(lastRound).append(',').append(winner == 0 ? -1 : (winner == us ? (s == 0 ? 1 : 0) : (s == 0 ? 0 : 1)));
            out.println(b);
        }
        // attacks
        long[][] A = new long[2][24];
        for (int[] x : atk) {
            int s = side(x[1]); long[] v = A[s];
            v[0]++; v[1] += x[3]; if (x[11] == 1) v[2]++;
            if (x[5] >= 2) { v[3]++; if (x[3] == x[6]) v[4]++; }
            if (x[7] == 1) { v[5]++; if (x[11] == 1) v[6]++; }
            if (x[8] == 1) v[7]++; if (x[9] == 1) v[8]++; if (x[10] == 1) v[9]++;
            boolean td = diedIn(x[2], x[0], x[0] + 3); if (td) v[10]++;
            if (x[3] <= 450) v[11]++;
            v[12] += x[12];                                // target's allies within dist2 10 of the attacker
            if (x[13] == 1 && x[8] == 1) v[13]++;          // target acts later this round and is ready (can strike back now)
            if (x[0] > 600) { v[14]++; if (x[11] == 1) v[15]++; }
            if (x[3] >= 1000) v[16]++;                     // opens a fresh robot
            if (x[14] == 0) { v[17]++; if (diedIn(x[1], x[0], x[0] + 2)) v[18]++; if (x[8] == 0) v[20]++; }   // step-in hits; stepper died <=3r; on a recharging victim
            else if (x[8] == 0) v[19]++;                   // stand hits on a recharging victim
        }
        long[][] Hh = new long[2][16];
        for (int[] x : heal) {
            int s = side(x[1]); long[] v = Hh[s];
            v[0]++; v[1] += x[3]; v[2] += x[4]; v[3] += Math.max(0, x[3] + x[4] - 1000);
            if (x[5] > 0) v[4]++; if (x[6] > 0) v[5]++;
            if (diedIn(x[2], x[0], x[0] + 3)) v[6]++;
            if (x[3] < 500) v[7]++; if (x[3] >= 900) v[8]++;
            if (x[5] > 0 && x[3] < 500) v[9]++;
        }
        long[][] Wd = new long[2][8];
        for (int[] x : wounded) {
            int s = side(x[1]); long[] v = Wd[s];   // s = the wounded robot's side
            v[0]++; if (diedIn(x[1], x[0] + 1, x[0] + 5)) v[1]++; if (diedIn(x[1], x[0] + 1, x[0] + 2)) v[2]++;
            v[3] += x[2]; v[4] += x[3];
        }
        long[][] Ep = new long[2][10];
        for (int[] x : episodes) {
            long[] v = Ep[x[0]];   // victim side
            v[0]++; if (x[5] == 2) { v[1]++; v[2] += x[3]; v[3] += x[4]; } v[4] += x[3]; if (x[5] == 1) v[5]++; if (x[3] == 1) v[6]++;
            if (x[5] == 2 && x[4] >= 2) v[7]++;
        }
        for (int s = 0; s < 2; s++) {
            StringBuilder b = new StringBuilder("G," + tag + "," + s + "," + lastRound + "," + (winner == 0 ? -1 : (winner == us ? (s == 0 ? 1 : 0) : (s == 0 ? 0 : 1))));
            for (long x : A[s]) b.append(',').append(x);
            for (long x : Hh[s]) b.append(',').append(x);
            for (long x : Wd[s]) b.append(',').append(x);
            for (long x : Ep[s]) b.append(',').append(x);
            out.println(b);
        }
    }
}
