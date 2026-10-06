package g_iter7;

import battlecode.common.*;

/**
 * S0b sensor (research/REWRITE_DESIGN.md 2.3, 2.4, 2.8, 2.9): one shared, predicted track per our flag in slots 25-33
 * (Comms.TRK_A/B/C), the round stamp in slot 23 and a private symmetry in slot 24. Nothing reads it to act yet (S1 will).
 *
 * Identity rules (2.9): every hook in Duck and G sits behind the static final C.TRACK, so with it off javac drops the
 * calls. This class never calls G.rand or G.nearest (ties go to the lower x, then the lower y), never writes slots 0-22,
 * Sym.cands, Nav or Micro state, never changes Duck.enemies/allies/flags, and every entry point catches its own
 * exceptions into exc. With C.TRACK on and no consumer, play is g_iter1's: only slots 23-33 and the indicator differ.
 *
 * Per turn (Duck): roundStart() first (every robot, jailed included); sight(f) inside sense()'s flag loop; then homes(),
 * negatives() and psym(). The track runs after setup (r > 200); psym() also runs in setup (from r4, after the broadcast
 * hints set slot 16) so the private symmetry has converged by r250, where psymOk is measured.
 *
 * S0b review amendments (2026-10-02; the design text of 2.3/2.4 lags these, see Comms' schema):
 *  - their CAPTURING is read once a round by the round-start runner and published in RT_STAMP [11]: every robot of ours
 *    uses the same return window in a round (a 50-round per-robot cache split the team for up to 49 rounds);
 *  - TRK_C keeps round & 15 of the last counted miss (one parity bit could not tell round r from r+2) and the drop lag;
 *  - a dropped flag is inferred re-grabbed from an empty drop tile only while it must still be lying there counted from
 *    the EARLIEST possible drop round (first round seen dropped - lag), so a drop first seen late raises no phantom;
 *  - a robot counts no miss on a track it inferred this turn, nor on an inferred track or presumed re-grab whose
 *    predicted point is still within TRK_INF_HOLD2 of L (the tile just seen empty): it would be one observation twice;
 *  - DROPPED -> LOST rewrites the round to the window end, so LOST's round is always the departure;
 *  - a fresh DROPPED track (from HOME or GONE) takes its speed class from their CAPTURING, as a fresh CARRIED one does;
 *  - PSYM keeps a per-robot terrain memory: every tile is checked once, on first sight, against our spawn tiles and its
 *    remembered image, only when the robot has moved to a tile it has not scanned from (terrain is static) and within
 *    TRK_PSYM_BC bytecodes a call.
 *
 * Indicator (G.endTurn under C.TRACK): "T" + headline() + writes, "e" + exc, "M" + missings.
 */
public strictfp class Track {
    public static final int HOME = 0, CARRIED = 1, DROPPED = 2, MISSING = 3, LOST = 4, GONE = 5;
    static final char[] LETTER = {'H', 'C', 'D', 'M', 'L', 'G'};
    static final int[] PRIO = {0, 5, 3, 4, 2, 1};          // headline order: C > M > D > L > G > H
    public static final int NO_DEST = 3;                    // TRK_B [12..11] = 3: destination unknown
    /** Speed classes, carrier moves per round: 1/2 (flag move cooldown 20), 5/6 (12 with their CAPTURING), 1 (relay). */
    public static final int SLOW = 0, CAP = 1, RELAY = 2;
    /** TRK_C drop lag 7: seven rounds or more, or no lower bound on the drop round (then no drop-tile inference). */
    public static final int LAG_UNKNOWN = 7;
    /** floor(sqrt(C.TRK_MISS_R2)): a point within TRK_MISS_R2 is within this many tiles on each axis (BotTest checks). */
    static final int MISS_R = 3;

    public static int exc, writes, missings;                // indicator e, T (slot writes 24-33 by this robot), M
    public static int psymTiles;                            // tiles this robot has put in its PSYM terrain memory

    // per-turn cache of slots 16 and 23-33 (roundStart loads it; this robot's own writes keep it in step; nobody else
    // runs during our turn, so it stays exact for the whole turn)
    static int loaded = -1, sym16, psymMask;
    static final int[] ta = new int[3], tb = new int[3], tc = new int[3];
    static final int[] seen = {-1, -1, -1};                 // round this robot last saw our flag i (any state) in sense()
    static final int[] infAt = {-1, -1, -1};                // round this robot inferred track i (MISSING or a re-grab)
    static final int[] pAt = {-1, -1, -1}, px = new int[3], py = new int[3];   // predicted point of track i, this turn
    static final MapLocation[] homeAt = new MapLocation[3]; // OF_HOME is fixed once setup ends
    static final MapLocation[] dst = new MapLocation[3];
    static int dstSym = -1;
    static boolean theyCap;                                 // RT_STAMP [11] of this round

    // ------------------------------------------------------------------ slot packing (2.3, amended)
    public static int packA(int loc, int state, boolean conf) { return (loc & 0xFFF) | ((state & 7) << 12) | (conf ? 0x8000 : 0); }
    public static int aLoc(int v) { return v & 0xFFF; }
    public static int aState(int v) { return (v >> 12) & 7; }
    public static boolean aConf(int v) { return (v & 0x8000) != 0; }
    public static int packB(int round, int dest, int cls, boolean inferred) { return (round & 0x7FF) | ((dest & 3) << 11) | ((cls & 3) << 13) | (inferred ? 0x8000 : 0); }
    public static int bRound(int v) { return v & 0x7FF; }
    public static int bDest(int v) { return (v >> 11) & 3; }
    public static int bCls(int v) { return (v >> 13) & 3; }
    public static boolean bInf(int v) { return (v & 0x8000) != 0; }
    /** TRK_C: [2..0] esc8, [6..3] en20, [8..7] misses, [12..9] round & 15 of the last counted miss, [15..13] drop lag. */
    public static int packC(int esc8, int en20, int misses, int missRound, int lag) {
        return (esc8 > 7 ? 7 : esc8) | ((en20 > 15 ? 15 : en20) << 3) | ((misses > 3 ? 3 : misses) << 7) | ((missRound & 15) << 9)
                | ((lag > LAG_UNKNOWN ? LAG_UNKNOWN : lag < 0 ? 0 : lag) << 13);
    }
    public static int cEsc8(int v) { return v & 7; }
    public static int cEn20(int v) { return (v >> 3) & 15; }
    public static int cMiss(int v) { return (v >> 7) & 3; }
    public static int cMissR(int v) { return (v >> 9) & 15; }
    public static int cLag(int v) { return (v >> 13) & 7; }
    /** RT_STAMP: [10..0] round, [11] their CAPTURING as read by that round's round-start runner. */
    public static int packStamp(int round, boolean cap) { return (round & 0x7FF) | (cap ? 0x800 : 0); }
    public static int stampRound(int v) { return v & 0x7FF; }
    public static boolean stampCap(int v) { return (v & 0x800) != 0; }

    // ------------------------------------------------------------------ prediction (2.4)
    public static int cheb(int ax, int ay, int bx, int by) {
        int dx = ax - bx, dy = ay - by;
        if (dx < 0) dx = -dx;
        if (dy < 0) dy = -dy;
        return dx > dy ? dx : dy;
    }
    static int d2(int ax, int ay, int bx, int by) { int dx = ax - bx, dy = ay - by; return dx * dx + dy * dy; }
    /** Carrier moves made in `age` rounds: age/2, age*5/6 or age. */
    public static int movesDone(int age, int cls) { if (age <= 0) return 0; return cls == RELAY ? age : cls == CAP ? age * 5 / 6 : age / 2; }
    /** Rounds the carrier needs for n moves: ceil(n / speed). */
    public static int roundsFor(int n, int cls) { return cls == RELAY ? n : cls == CAP ? (6 * n + 4) / 5 : 2 * n; }
    /** step(L, D, n): n greedy 8-way steps from L toward D, each axis capped at its distance. */
    public static MapLocation step(int lx, int ly, int dx, int dy, int n) {
        int ex = dx - lx, ey = dy - ly, mx = ex < 0 ? -ex : ex, my = ey < 0 ? -ey : ey;
        if (mx > n) mx = n;
        if (my > n) my = n;
        return new MapLocation(lx + (ex < 0 ? -mx : mx), ly + (ey < 0 ? -my : my));
    }
    /** P = step(L, D, min(N, movesDone(age))), N = cheb(L, D) - 1: stops on the zone tile next to the centre D. */
    public static MapLocation predict(int lx, int ly, int dx, int dy, int cls, int age) {
        int n = cheb(lx, ly, dx, dy) - 1, m = movesDone(age, cls);
        if (n < 0) n = 0;
        return step(lx, ly, dx, dy, m < n ? m : n);
    }
    /** Return window of our dropped flag: 4 rounds, 25 if they have CAPTURING (RULES.md; GlobalUpgrade.CAPTURING). */
    static int window() { return GameConstants.FLAG_DROPPED_RESET_ROUNDS + (theyCap ? GlobalUpgrade.CAPTURING.flagReturnDelayChange : 0); }

    /** Fills px[i], py[i] with where track i says our flag is now, once per turn (this robot's writes and a PSYM change
     *  clear it): HOME/GONE and DROPPED inside the window -> L; a presumed re-grab after the window -> predict() from the
     *  window end; CARRIED/MISSING/LOST -> predict() from (L, r0). False if the track has no location. */
    static boolean pred(int i) {
        int r = G.round;
        if (pAt[i] == r) return px[i] >= 0;
        pAt[i] = r;
        px[i] = -1;
        int a = ta[i], l = aLoc(a);
        if (l == 0) return false;
        int lx = (l - 1) >> 6, ly = (l - 1) & 63, s = aState(a);
        px[i] = lx;
        py[i] = ly;
        if (s == HOME || s == GONE) return true;
        int b = tb[i], r0 = bRound(b), dj = bDest(b);
        if (s == DROPPED) { r0 += window(); if (r < r0) return true; }
        if (dj == NO_DEST) return true;
        MapLocation D = dests()[dj];
        if (D == null) return true;
        MapLocation P = predict(lx, ly, D.x, D.y, bCls(b), r - r0);
        px[i] = P.x;
        py[i] = P.y;
        return true;
    }

    // ------------------------------------------------------------------ private symmetry and destinations
    /** Best symmetry of PSYM (slot 24, within slot 16; 0 = unwritten = slot 16), ROT > FX > FY as Sym.best(). */
    static int psymBest() {
        int s16 = sym16 & 7;
        if (s16 == 0) s16 = 7;
        int m = psymMask & s16;
        if (m == 0) m = s16;
        return (m & Sym.ROT) != 0 ? Sym.ROT : (m & Sym.FX) != 0 ? Sym.FX : Sym.FY;
    }
    /** Enemy spawn centres: image j of our spawn centre j under the private symmetry (index j is TRK_B's destination). */
    static MapLocation[] dests() {
        int s = psymBest();
        if (s != dstSym) {
            for (int j = 0; j < 3; j++) dst[j] = G.spawnCenters[j] == null ? null : Sym.image(G.spawnCenters[j], s);
            dstSym = s;
        }
        return dst;
    }
    /** Index of the enemy centre nearest (x, y), skipping `skip`; with `from` set, centres the carrier moved away from
     *  since `from` are rejected. Ties: lower x, then lower y (never array order, never G.rand). NO_DEST if none. */
    static int nearestDest(int x, int y, int skip, MapLocation from) {
        MapLocation[] ds = dests();
        int best = NO_DEST, bd = Integer.MAX_VALUE, bx = 0, by = 0;
        for (int j = 0; j < 3; j++) {
            MapLocation c = ds[j];
            if (j == skip || c == null) continue;
            int d = d2(x, y, c.x, c.y);
            if (from != null && d > d2(from.x, from.y, c.x, c.y)) continue;
            if (best == NO_DEST || d < bd || (d == bd && (c.x < bx || (c.x == bx && c.y < by)))) { best = j; bd = d; bx = c.x; by = c.y; }
        }
        return best;
    }
    static MapLocation home(int i) throws GameActionException {
        if (homeAt[i] == null) homeAt[i] = Comms.flagHome(i);
        return homeAt[i];
    }

    // ------------------------------------------------------------------ writes (only on change)
    static void setA(int i, int v) throws GameActionException { if (ta[i] != v) { ta[i] = v; pAt[i] = -1; G.rc.writeSharedArray(Comms.TRK_A + i, v); writes++; } }
    static void setB(int i, int v) throws GameActionException { if (tb[i] != v) { tb[i] = v; pAt[i] = -1; G.rc.writeSharedArray(Comms.TRK_B + i, v); writes++; } }
    static void setC(int i, int v) throws GameActionException { if (tc[i] != v) { tc[i] = v; G.rc.writeSharedArray(Comms.TRK_C + i, v); writes++; } }

    // ------------------------------------------------------------------ entry points (each catches its own exceptions)
    /** Every robot, every turn, first thing: load the cache; the first of ours to run each round (normally idx 0, jailed
     *  or not) reads their upgrades, expires tracks and writes RT_STAMP last (with their CAPTURING in [11]), so if it
     *  throws the next robot redoes the round start. Every later robot of the round takes theyCap from the stamp. */
    public static void roundStart() {
        if (G.round <= C.SETUP_ROUNDS) return;
        try {
            RobotController rc = G.rc;
            int stamp = rc.readSharedArray(Comms.RT_STAMP);
            sym16 = rc.readSharedArray(Comms.SYM);
            psymMask = rc.readSharedArray(Comms.PSYM) & 7;
            for (int i = 0; i < 3; i++) {
                ta[i] = rc.readSharedArray(Comms.TRK_A + i);
                tb[i] = rc.readSharedArray(Comms.TRK_B + i);
                tc[i] = rc.readSharedArray(Comms.TRK_C + i);
                pAt[i] = -1;
            }
            loaded = G.round;
            if (stampRound(stamp) != G.round) {
                boolean cap = false;
                if (G.round >= 600) for (GlobalUpgrade u : rc.getGlobalUpgrades(G.them)) if (u == GlobalUpgrade.CAPTURING) cap = true;
                theyCap = cap;                              // upgrade points come at r600, r1200, r1800
                expire();
                rc.writeSharedArray(Comms.RT_STAMP, packStamp(G.round, cap));
            } else theyCap = stampCap(stamp);
        } catch (Exception e) {
            exc++;
            if (C.DEBUG) e.printStackTrace();
        }
    }

    /** GONE once the predicted arrival plus TRK_EXPIRE has passed (CARRIED, MISSING, LOST, or DROPPED after its window). */
    static void expire() throws GameActionException {
        int r = G.round;
        for (int i = 0; i < 3; i++) {
            int a = ta[i], s = aState(a), l = aLoc(a);
            if ((s != CARRIED && s != MISSING && s != LOST && s != DROPPED) || l == 0) continue;
            int b = tb[i], base = bRound(b), dj = bDest(b);
            if (s == DROPPED) { base += window(); if (r < base) continue; }
            MapLocation D = dj == NO_DEST ? null : dests()[dj];
            if (D == null) continue;
            int n = cheb((l - 1) >> 6, (l - 1) & 63, D.x, D.y) - 1;
            if (n < 0) n = 0;
            if (r > base + roundsFor(n, bCls(b)) + C.TRK_EXPIRE) setA(i, packA(l, GONE, aConf(a)));
        }
    }

    /** A flag in view (Duck.sense's loop): our flag carried -> CARRIED; on its home tile -> HOME (re-stamped once
     *  HOME_CONFIRM old); elsewhere on the ground -> DROPPED (round = first seen dropped there; lag = rounds since the
     *  previous information, which bounds the drop round from below). */
    public static void sight(FlagInfo f) {
        if (loaded != G.round) return;
        try {
            if (f.getTeam() != G.us) return;
            int i = Comms.ourFlagIndex(f.getID());
            if (i < 0) return;
            int r = G.round;
            seen[i] = r;
            MapLocation F = f.getLocation();
            int fl = Comms.enc(F), a = ta[i], b = tb[i], s = aState(a);
            if (f.isPickedUp()) { carried(i, F, fl, a, b, s, r); return; }
            if (F.equals(home(i))) {
                if (s != HOME || aLoc(a) != fl || r - bRound(b) >= C.HOME_CONFIRM) {
                    setA(i, packA(fl, HOME, false));
                    setB(i, packB(r, NO_DEST, SLOW, false));
                    setC(i, 0);
                }
                return;
            }
            int dj = bDest(b), r0 = bRound(b), cls = bCls(b), lag = cLag(tc[i]);
            boolean conf = aConf(a);
            if (s != DROPPED || aLoc(a) != fl) {            // a new drop, first seen now
                // the flag was not lying here at the previous information's round (home, carried, or lying elsewhere),
                // so the drop came at or after it; an inferred, LOST or GONE round is no such bound
                boolean bound = s == HOME || s == MISSING || s == DROPPED || (s == CARRIED && !bInf(b));
                lag = bound ? r - r0 : LAG_UNKNOWN;
                r0 = r;
                if (s == HOME || s == GONE) cls = theyCap ? CAP : SLOW;   // a fresh track: as carried() and MISSING
                if (s == HOME || s == GONE || dj == NO_DEST) { dj = nearestDest(F.x, F.y, -1, null); conf = false; }
            }
            setA(i, packA(fl, DROPPED, conf));
            setB(i, packB(r0, dj, cls, false));
            setC(i, packC(cEsc8(tc[i]), cEn20(tc[i]), 0, 0, lag));
        } catch (Exception e) {
            exc++;
            if (C.DEBUG) e.printStackTrace();
        }
    }

    /** CARRIED sighting: location, round, escorts within dist2 8 and enemies within dist2 20 of the carrier (itself
     *  excluded), misses cleared; speed class from a gap of 4+ rounds; destination = the enemy centre nearest the
     *  carrier, rejecting centres it moved away from since the last positive information (sets the confirmed bit). A
     *  second observer of the same sighting in the same round does nothing (the escort loop is the costly part, 2.8). */
    static void carried(int i, MapLocation F, int fl, int a, int b, int s, int r) throws GameActionException {
        int L = aLoc(a), r0 = bRound(b);
        if (s == CARRIED && L == fl && r0 == r) return;
        int e8 = 0, e20 = 0;
        for (RobotInfo e : Duck.enemies) {
            int d = F.distanceSquaredTo(e.location);
            if (d == 0 || d > 20) continue;
            e20++;
            if (d <= 8) e8++;
        }
        int cls = bCls(b), dj = bDest(b);
        boolean conf = aConf(a);
        MapLocation from = s == HOME ? home(i) : Comms.dec(L);
        if ((s == CARRIED || s == LOST) && !bInf(b) && r - r0 >= 4 && from != null)
            cls = 10 * cheb(from.x, from.y, F.x, F.y) >= 9 * (r - r0) ? RELAY : theyCap ? CAP : SLOW;
        else if (s == HOME || s == GONE) cls = theyCap ? CAP : SLOW;
        if (s != GONE && from != null && !from.equals(F)) {
            int j = nearestDest(F.x, F.y, -1, from);
            if (j != NO_DEST) { dj = j; conf = true; }
            else { dj = nearestDest(F.x, F.y, -1, null); conf = false; }
        } else if (s == HOME || s == GONE || dj == NO_DEST) {   // a fresh track that has not moved: nearest centre
            dj = nearestDest(F.x, F.y, -1, null);
            conf = false;
        }                                                   // else it has not moved since the last information: keep D
        setA(i, packA(fl, CARRIED, conf));
        setB(i, packB(r, dj, cls, false));
        setC(i, packC(e8, e20, 0, 0, 0));
    }

    /** After sense(), for our flags this robot did not see this turn:
     *  - HOME with home in view -> MISSING (inferred, the departure kept at the last home confirmation);
     *  - DROPPED with home in view after the window (r > r0 + window: it would be back) -> re-grabbed unseen (CARRIED,
     *    inferred, r0 = window end);
     *  - DROPPED with the drop tile in view while the flag must still be lying there, counted from the earliest possible
     *    drop round r0 - lag (lag LAG_UNKNOWN: never), or with home in view as well (in neither place) -> re-grabbed unseen
     *    (CARRIED, inferred, r0 = round - 1). Between the two an empty drop tile alone is ambiguous: no write.
     *  MISSING never fires from GONE. */
    public static void homes() {
        if (loaded != G.round || G.me == null) return;
        try {
            RobotController rc = G.rc;
            int r = G.round;
            for (int i = 0; i < 3; i++) {
                if (seen[i] == r) continue;
                int a = ta[i], s = aState(a);
                if (s == HOME) {
                    MapLocation h = home(i);
                    if (h == null || !rc.canSenseLocation(h)) continue;
                    int r0 = aLoc(a) == 0 ? C.SETUP_ROUNDS : bRound(tb[i]);   // never confirmed: the flags sat home at the end of setup
                    if (r0 < C.SETUP_ROUNDS) r0 = C.SETUP_ROUNDS;
                    setA(i, packA(Comms.enc(h), MISSING, false));
                    setB(i, packB(r0, nearestDest(h.x, h.y, -1, null), theyCap ? CAP : SLOW, true));
                    setC(i, 0);
                    missings++;
                    infAt[i] = r;
                } else if (s == DROPPED) {
                    int b = tb[i], r0 = bRound(b), win = window(), lag = cLag(tc[i]);
                    MapLocation h = home(i), L = Comms.dec(aLoc(a));
                    boolean homeSeen = h != null && rc.canSenseLocation(h);
                    if (homeSeen && r > r0 + win) { inferCarried(i, a, b, r0 + win); infAt[i] = r; continue; }   // r == r0 + win: maybe on its way back
                    if (L == null || !rc.canSenseLocation(L)) continue;
                    if (homeSeen || (lag < LAG_UNKNOWN && r < r0 - lag + win)) { inferCarried(i, a, b, r - 1); infAt[i] = r; }
                }
            }
        } catch (Exception e) {
            exc++;
            if (C.DEBUG) e.printStackTrace();
        }
    }
    static void inferCarried(int i, int a, int b, int r0) throws GameActionException {
        setA(i, packA(aLoc(a), CARRIED, aConf(a)));
        setB(i, packB(r0, bDest(b), bCls(b), true));
        setC(i, packC(cEsc8(tc[i]), cEn20(tc[i]), 0, 0, 0));
    }

    /** Negative sightings: this robot has the predicted point within TRK_MISS_R2 and does not see the flag -> one miss
     *  (at most one a round: TRK_C keeps round & 15 of the last). At 1 miss the destination switches to the other enemy
     *  centre nearest L; at 3 LOST (a DROPPED track's round becomes the window end, the presumed departure). No miss on a
     *  track this robot inferred this turn, nor on an inferred track or a presumed re-grab (DROPPED after its window)
     *  predicted within TRK_INF_HOLD2 of L: that ground was just seen empty, so it would count one observation twice. */
    public static void negatives() {
        if (loaded != G.round || G.me == null) return;
        try {
            int r = G.round, mx = G.me.x, my = G.me.y;
            for (int i = 0; i < 3; i++) {
                if (seen[i] == r || infAt[i] == r) continue;
                int a = ta[i], s = aState(a);
                if (s != CARRIED && s != MISSING && s != DROPPED) continue;
                int b = tb[i], c = tc[i], m = cMiss(c);
                if (s == DROPPED && r < bRound(b) + window()) continue;   // inside the window homes() checks the drop tile
                if (m > 0 && cMissR(c) == (r & 15)) continue;
                int l = aLoc(a), lx = (l - 1) >> 6, ly = (l - 1) & 63, dj = bDest(b);
                if (l == 0) continue;
                MapLocation D = dj == NO_DEST ? null : dests()[dj];   // P lies in the L-D box: far from it, no prediction needed
                int x0 = lx, x1 = lx, y0 = ly, y1 = ly;
                if (D != null) { if (D.x < x0) x0 = D.x; else x1 = D.x; if (D.y < y0) y0 = D.y; else y1 = D.y; }
                if (mx < x0 - MISS_R || mx > x1 + MISS_R || my < y0 - MISS_R || my > y1 + MISS_R) continue;
                if (!pred(i)) continue;
                int x = px[i], y = py[i];
                if (d2(mx, my, x, y) > C.TRK_MISS_R2) continue;
                if ((bInf(b) || s == DROPPED) && d2(x, y, lx, ly) <= C.TRK_INF_HOLD2) continue;
                m++;
                setC(i, packC(cEsc8(c), cEn20(c), m, r, cLag(c)));
                if (m == 1) {
                    int j = nearestDest(lx, ly, dj, null);
                    if (j != NO_DEST) { setB(i, packB(bRound(b), j, bCls(b), bInf(b))); setA(i, packA(l, s, false)); }
                }
                if (m >= 3) {
                    int nb = tb[i];
                    if (s == DROPPED) setB(i, packB(bRound(nb) + window(), bDest(nb), bCls(nb), bInf(nb)));
                    setA(i, packA(l, LOST, aConf(ta[i])));
                }
            }
        } catch (Exception e) {
            exc++;
            if (C.DEBUG) e.printStackTrace();
        }
    }

    // ------------------------------------------------------------------ private symmetry (2.4, amended)
    // Vision-disc offsets (dist2 <= 20), one char each: (dx + 8) << 4 | (dy + 8). DISC: the whole disc, nearest first.
    // NEW[(ddx + 1) * 3 + ddy + 1]: the tiles that come into view after a one-tile move by (ddx, ddy).
    static final String DISC = "\u0088\u0078\u0087\u0089\u0098\u0077\u0079\u0097\u0099\u0068\u0086\u008A\u00A8\u0067\u0069\u0076\u007A\u0096"
            + "\u009A\u00A7\u00A9\u0066\u006A\u00A6\u00AA\u0058\u0085\u008B\u00B8\u0057\u0059\u0075\u007B\u0095\u009B\u00B7"
            + "\u00B9\u0056\u005A\u0065\u006B\u00A5\u00AB\u00B6\u00BA\u0048\u0084\u008C\u00C8\u0047\u0049\u0074\u007C\u0094"
            + "\u009C\u00C7\u00C9\u0055\u005B\u00B5\u00BB\u0046\u004A\u0064\u006C\u00A4\u00AC\u00C6\u00CA";
    static final String[] NEW = {
            "\u0056\u0065\u0048\u0084\u0047\u0049\u0074\u0094\u0055\u0046\u004A\u0064\u00A4",
            "\u0048\u0047\u0049\u0055\u005B\u0046\u004A\u0064\u006C",
            "\u005A\u006B\u0048\u008C\u0047\u0049\u007C\u009C\u005B\u0046\u004A\u006C\u00AC",
            "\u0084\u0074\u0094\u0055\u00B5\u0046\u0064\u00A4\u00C6",
            "",
            "\u008C\u007C\u009C\u005B\u00BB\u004A\u006C\u00AC\u00CA",
            "\u00A5\u00B6\u0084\u00C8\u0074\u0094\u00C7\u00C9\u00B5\u0064\u00A4\u00C6\u00CA",
            "\u00C8\u00C7\u00C9\u00B5\u00BB\u00A4\u00AC\u00C6\u00CA",
            "\u00AB\u00BA\u008C\u00C8\u007C\u009C\u00C7\u00C9\u00BB\u006C\u00AC\u00C6\u00CA"};
    // per-robot terrain memory, bit t = x + y * W: tiles scanned, walls among them, our spawn tiles, positions whose scan
    // completed (stepping back onto one costs nothing: ducks walk the same paths again and again)
    static long[] mSeen, mWall, mOurs, mDone;
    static int scanX = -1, scanY = -1, pendK, pendX, pendY;   // last completed scan; a scan cut by the budget
    static String pend;

    static void initMemory() {
        int W = G.W, H = G.H, n = (W * H + 63) >>> 6;
        mSeen = new long[n];
        mWall = new long[n];
        mOurs = new long[n];
        mDone = new long[n];
        if (G.spawns != null) {
            for (MapLocation q : G.spawns) { int t = q.x + q.y * W; mOurs[t >>> 6] |= 1L << t; }
        } else {
            for (MapLocation c : G.spawnCenters) if (c != null)
                for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) {
                    int x = c.x + dx, y = c.y + dy;
                    if (x >= 0 && y >= 0 && x < W && y < H) { int t = x + y * W; mOurs[t >>> 6] |= 1L << t; }
                }
        }
    }

    /** Private symmetry (slot 24): starts from slot 16 (read only) and is pruned by evidence the true symmetry always
     *  passes, so it never loses it. Each tile this robot sees is checked once, on first sight (per-robot memory): under a
     *  surviving symmetry s, the tile is their spawn zone iff its image is one of our spawn tiles, and a wall iff its
     *  remembered image is. A tile and its image need not be in view together. Scans only after a move to a tile it has
     *  not completed a scan from (terrain is static; a one-tile move brings 9-13 tiles into view, a jump the whole
     *  disc) and spends at most TRK_PSYM_BC bytecodes a call; a cut scan resumes next turn if the robot has not moved,
     *  else its rest is dropped (lossy, bounded). Never written empty; never touches Sym.cands or slot 16. */
    public static void psym() {
        if (G.me == null || G.round <= 3) return;
        try {
            RobotController rc = G.rc;
            int v16 = rc.readSharedArray(Comms.SYM) & 7;
            if (v16 == 0) v16 = 7;
            int raw = rc.readSharedArray(Comms.PSYM) & 7, p = raw == 0 ? v16 : raw & v16;
            if (p == 0) p = v16;
            if (p == Sym.ROT || p == Sym.FX || p == Sym.FY) return;
            int mx = G.me.x, my = G.me.y;
            if (pend != null && (mx != pendX || my != pendY)) { scanX = pendX; scanY = pendY; pend = null; }
            if (pend == null) {
                if (mx == scanX && my == scanY) return;
                if (mSeen != null) {                                    // a scan completed here before: its disc is in memory
                    int t = mx + my * G.W;
                    if ((mDone[t >>> 6] & (1L << t)) != 0) { scanX = mx; scanY = my; return; }
                }
                int ddx = mx - scanX, ddy = my - scanY;
                pend = scanX >= 0 && ddx >= -1 && ddx <= 1 && ddy >= -1 && ddy <= 1 ? NEW[(ddx + 1) * 3 + ddy + 1] : DISC;
                pendK = 0;
                pendX = mx;
                pendY = my;
            }
            if (mSeen == null) initMemory();
            int stop = Clock.getBytecodesLeft() - C.TRK_PSYM_BC, before = p, W = G.W, H = G.H, k = pendK, bx = mx - 8, by = my - 8;
            int last = W * H - 1, w1 = W - 1, hw = (H - 1) * W;    // images of t = x + y*W: ROT last - t, FX w1 - x + y*W, FY x + hw - y*W
            String offs = pend;
            int n = offs.length();
            long[] sm = mSeen, wm = mWall, om = mOurs;
            Team them = G.them;
            for (; k < n; k++) {
                if (Clock.getBytecodesLeft() < stop) break;
                int c = offs.charAt(k), tx = bx + (c >> 4), ty = by + (c & 15);
                if (tx < 0 || ty < 0 || tx >= W || ty >= H) continue;
                int row = ty * W, t = tx + row, w = t >>> 6;
                long bit = 1L << t;
                if ((sm[w] & bit) != 0) continue;
                MapInfo mi = rc.senseMapInfo(new MapLocation(tx, ty));
                sm[w] |= bit;
                boolean wall = mi.isWall(), theirs = mi.getSpawnZoneTeamObject() == them;
                if (wall) wm[w] |= bit;
                psymTiles++;
                // under s: t is their spawn zone iff image(t) is ours; t is a wall iff a remembered image(t) is
                if ((p & Sym.ROT) != 0 && p != Sym.ROT) {
                    int j = last - t, v = j >>> 6;
                    long jb = 1L << j;
                    if (((om[v] & jb) != 0) != theirs || ((sm[v] & jb) != 0 && ((wm[v] & jb) != 0) != wall)) p &= ~Sym.ROT;
                }
                if ((p & Sym.FX) != 0 && p != Sym.FX) {
                    int j = w1 - tx + row, v = j >>> 6;
                    long jb = 1L << j;
                    if (((om[v] & jb) != 0) != theirs || ((sm[v] & jb) != 0 && ((wm[v] & jb) != 0) != wall)) p &= ~Sym.FX;
                }
                if ((p & Sym.FY) != 0 && p != Sym.FY) {
                    int j = tx + hw - row, v = j >>> 6;
                    long jb = 1L << j;
                    if (((om[v] & jb) != 0) != theirs || ((sm[v] & jb) != 0 && ((wm[v] & jb) != 0) != wall)) p &= ~Sym.FY;
                }
                if (p == Sym.ROT || p == Sym.FX || p == Sym.FY) { k = n; break; }
            }
            if (k >= n) {
                scanX = pendX; scanY = pendY; pend = null;
                int t = mx + my * W;
                mDone[t >>> 6] |= 1L << t;
            } else pendK = k;
            if (p != before) {
                rc.writeSharedArray(Comms.PSYM, p);
                writes++;
                psymMask = p;
                pAt[0] = pAt[1] = pAt[2] = -1;
            }
        } catch (Exception e) {
            exc++;
            if (C.DEBUG) e.printStackTrace();
        }
    }

    /** Indicator letter: the most urgent state among this robot's cached tracks ('-' before the first load). */
    public static char headline() {
        if (loaded < 0) return '-';
        int best = HOME, bp = -1;
        for (int i = 0; i < 3; i++) {
            int s = aState(ta[i]);
            if (s > GONE) continue;
            if (PRIO[s] > bp) { bp = PRIO[s]; best = s; }
        }
        return LETTER[best];
    }
}
