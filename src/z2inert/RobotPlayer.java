package z2inert;

import battlecode.common.*;

/**
 * Entry point. One static turn loop per robot with the bytecode monitor built in:
 * overrun = the round changed while our turn ran; near miss = more than 90% of the limit used.
 * Every exception is caught and counted (a caught exception silently abandons the rest of a turn).
 */
public strictfp class RobotPlayer {
    public static void run(RobotController rc) {
        G.init(rc);
        while (true) {
            int startRound = rc.getRoundNum();
            try {
                G.startTurn();
                Duck.turn();
            } catch (GameActionException e) {
                G.exceptions++;
                if (C.DEBUG) e.printStackTrace();
            } catch (Exception e) {
                G.exceptions++;
                if (C.DEBUG) e.printStackTrace();
            }
            try {
                int used = Clock.getBytecodeNum();
                if (rc.getRoundNum() != startRound) { G.overruns++; used = GameConstants.BYTECODE_LIMIT; }
                else if (used > C.NEAR_MISS_BC) G.nearMisses++;
                if (used > G.maxBc) G.maxBc = used;
                G.endTurn();
            } catch (Exception e) {
                G.exceptions++;
            }
            Clock.yield();
        }
    }
}
