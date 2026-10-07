# Synthesis

I read the four lens reports, checked their code claims in src/g_iter7/Micro.java and src/bot (Micro.java, Duck.java), and re-cut the 720-game census myself. Data: gauntlet/census-g_iter7-L1..L3.csv. I ran no games and edited no repo files.

Baseline: against the upper tier (180 games) we win 0.278, with kill share 0.374 +- 0.008 and capture difference -0.73 +- 0.11. Against the rest (540 games) we win 0.859, kill share 0.650, capture difference +1.75. The rest has little room left: its r2000 games (win 0.64, capture difference +0.29) and the mid bots andrewgopher 0.53, NotLLeon 0.61, hsmalladi 0.64 and Cyril 0.72.

WHAT STILL SEPARATES US FROM THE UPPER TIER
The gap is how lethal contact is, not local numbers and not target choice. At equal local balance our robots in contact die at 1.6-2.2 times the upper tier's rate (2-round hazard 0.082 vs 0.040 at b=+2). Local balance explains only 17% of the gap.

Two parts make it up:
1. Enemy stun volume.
   - Their stuns trigger 439 times a game on us vs our 200 on them (census), and catch 1.15 times as many victims per trigger.
   - 41-52% of our deaths come within 4-5 rounds of a freeze, vs 22-29% of theirs.
   - The volume comes from kill rewards and price. Kill rewards: our homeDeathShare is 0.595 vs 0.312 in rest games; they earn 14.8k crumbs a game from kills vs our 2.6k. Price: their build-6 specialists pay about 54 crumbs a stun vs our 88, and the builders family is parked.
   - Our bank sits at 195-290 crumbs in both tiers, below the 300-crumb field-stun gate. The only open handle is how we spend those scarce crumbs.
   - This is a feedback loop: about 60% of our deaths happen on our territory and each pays them 30 crumbs, which funds more of their stuns.
2. Tempo while not ready. HEAL_HOLD fixed what robots do when ready; where they stand while recharging is what is left.
   - After a strike we stay in enemy reach 0.252 of the time vs 0.111 for the upper bots and 0.178 for the rest bots. This is a code defect, verified: the kite score counts threats out to dist2 10, so stepping out of reach earns nothing.
   - Their stand hits on our recharging robots are 0.189 of their hits vs our 0.111 on theirs.
   - Strike turns: 3,152 a game for them vs 1,859 for us.
   - When our strike is ready next turn we close to one-step range 0.02 of the time vs 0.41-0.46 for every other bot.

At parity, so not levers (the lenses agree):
- target choice (lowest-HP 0.958 vs 0.921; this refutes FOCUS_STEP)
- finishing wounded robots (0.434 vs 0.438)
- follow-up on a frozen victim (0.53 vs 0.50)
- yield per one-step stun (5.45 vs 5.41 victims)
- upgrades (identical order and timing)
- map crumbs (98% collected)
- the dam drop at r201-220
- how long robots stay in enemy vision

AGREEMENTS
- Micro and position both point at the kite score: indifference to being in reach, adjacency as the tie-break, and wounded robots stopping at exactly dist2 20. All three are in the code.
- Traps and economy: the stun gap is volume, our stuns are limited by crumbs, and technique is at parity.
- Micro and traps: kill conversion follows fight state and stuns, not target choice.
- Micro and position: our heals reach robots in contact less often. We pick the lowest-HP target 0.93 of the time vs 0.74-0.76 for both tiers, and our wounded robots in contact are healed the next round 0.17-0.18 of the time vs 0.20-0.25.

DISAGREEMENT, resolved against SPREAD
- Position finds 0.57-0.90 more of our robots caught per enemy stun, plus more adjacency. Traps finds one-step stuns and dist2-13 clumping at parity.
- Position's own split puts the excess in slow, pre-laid traps, so it happens while robots are moving, not in fight formation.
- My per-opponent census check: in the upper tier the excess is almost all jmerle (7.21 vs 4.07 victims per trigger, 90% of them fast: that is its placement). chenyx512 (-0.20) and kuma (-0.28) show none. The excess is larger in rest games (5.41 vs 4.39), which we win.
- Position's claim that adjacency outweighs the +50 band bonus holds only with 5 or more adjacent allies; the mean is about 2.

RANKING (expected band gain x P(delivery))
1. C.KITE_REACH_W: about +1.2 pp x 0.55.
2. C.RC_BAND, stacked on lever 1: about +1.5 pp x 0.3 (about 0.45 if lever 1 fires).
3. C.STUN_MIN13: about +1.0 pp x 0.35.
4. C.HEAL_FRONT: about +0.5 pp x 0.35.

- None of the four needs comms slots. Levers 1, 2 and 4 are a few lines in Micro.java; lever 3 is a few lines in Duck.placeCombatTrap.
- On its own, none is likely to reach t_all 2.3 at 720 pairs, which needs about +0.12 capture difference. The realistic route to shipping is the 1+2 stack. It works the same tempo axis that gave HEAL_HOLD a capture t of 6.46. Levers 3 and 4 then go on top, as stacking near-misses is allowed.
- Order: build g7kite and g7sg in parallel, since they touch independent subsystems, on one shared 12-cell set with one g_iter7 twin block. Then g7kiterc on top of g7kite, then g7hf as a layer.

NOT RANKED
- JOIN_FIGHT (reserve #5): the join gap is only -0.03 to -0.055 and correlation-only, and the cross-opponent link vanishes in our wins (r +0.07). It diverts the 63-69% of the pool that is walking the default advance, which risks flag pressure against the rest. It is close to the GROUP_MIN line, which lost.
- SPREAD: see the disagreement above.
- STUN_PRESS: the realistic gain is about +17 strikes a game, not 30-40. It needs a comms ring and guessing which enemies are frozen; the andli28 synthesis already declined it.
- COUNTER_STUN: correlated with fight size, overlaps lever 3's allocation idea, and spends the 100-300 crumb band that carrier stuns rely on.
- HURT_EDGE: a real basics defect (rule 15), but worth only 1-2 deaths a game. It should ride along with the kite-branch stack rather than be a lever.
- BUILD_DEFER / BUILD_ROUTE: the largest economic handle (price is about half the log stun gap), but it reopens the parked builders family and needs the owner's approval.

OPERATIONAL
- The driver's root disk has 39 MB free (df -h /: 28G of 30G used). Builds, census writes and commits will fail until space is freed. Earlier studies' scratch folders in the session scratchpad are the obvious candidates: gymstudy 308 MB, criteria 245 MB, lens3 191 MB, fights 166 MB, flaglens 154 MB. I deleted nothing.
- The lenses' comms-slot proposals clash (traps uses 37-47, position 42-45); none of the four ranked levers uses comms.

FILES
- /home/terryvanbelle/projects/vibe/2024/src/g_iter7/Micro.java (kite branch :230-234, tryHeal :57-74)
- /home/terryvanbelle/projects/vibe/2024/src/bot/Micro.java (kite branch :276-280, tryHeal :57-74)
- /home/terryvanbelle/projects/vibe/2024/src/bot/Duck.java (placeCombatTrap :1429, placement tile :1452, guardFight :444)
- /home/terryvanbelle/projects/vibe/2024/tools/replaydump/ReplayDump.java (step-census reconstruction, the base for the new columns)
- /home/terryvanbelle/projects/vibe/2024/gauntlet/census-g_iter7-L1.csv, -L2.csv, -L3.csv

## 1. C.KITE_REACH_W (arm g7kite): leave enemy reach when you cannot strike this turn

Mechanism: Where: src/bot/Micro.java, Micro.fight, the kite/hold else-branch (lines 276-280; g_iter7 :230-234), only the goal == null form.

After the existing kite score, add:
  if (C.KITE_REACH_W > 0 && (!actReady || hurt)) score -= inRange * C.KITE_REACH_W;
- inRange (enemies within dist2 4 of the tile) is already computed in the tile loop.
- C.java: public static final int KITE_REACH_W = 0, so javac drops the term and the defaults stay byte-identical to g_iter7.

Arm 300:
- That beats adjacency (at most 80), the crumb bonus (40), the 11-20 band (50) and the +1 for staying, but stays below one threat (1000).
- So a robot that has struck, is recharging, or is hurt moves to a tile out of reach (dist2 5-10 of that enemy) whenever one adds no threat.
- (!actReady || hurt) never touches the engage or advance tiles. Those need actReady && !hurt, which includes the parked C.ENGAGE_HP refused tiles and weak-side step-ins.

Indicator 'kr': turns where the best tile without the term (tracked as STUN_WARY's bestRaw is) differs from the chosen one.

Dose ladder, only if the 5(a) signature passes and the falsifier does not trip: 1200 (accept one extra threat to leave reach; upper bots do this 62% of the time, we never do).

Unchanged: the carrier, loose-flag, rush (goal != null), engage and advance branches, HEAL_HOLD and RETREAT_HP.

Not a closed line: it changes no retreat threshold, engage gate, territory term, stun-avoidance term or drift.

Evidence: Code (verified by me):
- A non-ready robot's tiles score -1000 per enemy within dist2 10.
- From any tile within dist2 4 of an enemy, every adjacent tile is still within dist2 10 of it (at most (3,1) = 10).
- So leaving reach earns nothing; adjacency (+10 per ally), crumbs (+40) and the +1 for staying pick the tile.

Micro lens, all 720 control games (180 upper). Its execution-order reconstruction reproduced ReplayDump's step census exactly. Strike turns with a free exit (out of reach, no extra threat) that end in reach:
- upper games: us 0.252 +- 0.006 vs upper bots 0.111 +- 0.005
- rest games: us 0.283 +- 0.005 vs rest bots 0.178 +- 0.009
- the same in every length bin (<1000, 1000-1999, r2000) and in both phases
- all 5 upper bots and 11 of 15 rest bots are below us; the exceptions (Cyril 0.63, Hugo 0.44, clbarrell 0.42, SampleProvider) are bots we beat 72-100%, so this is policy, not a losing state

How the code produces it:
- In 21-24% of free-exit strike turns every best-scoring tile is in reach (about 314 a game in upper games, 335 in rest).
- There we end in reach 99.9% of the time; upper bots kite out 80%, rest bots 73-76%.
- Recharging in reach with a free exit, we stay 0.141 vs 0.055 (upper) and 0.119 (rest).

Consequence (upper games, matched class; selection remains), our stays vs their kites:
- hit before our next turn: 0.328 vs 0.160
- dead within 3 rounds: 0.048 vs 0.017 (below 300 HP: 0.40-0.51 vs 0.20-0.23)
- strikes within 2 turns: 0.480 vs 0.369

Tempo:
- enemy stand hits on our recharging robots: 0.189 of their hits (1,227 a game) vs ours 0.111 (649)
- step-in share of our hits: 0.685 vs 0.513 (rest games 0.640 vs 0.496)

The position lens finds the outcome independently: at equal local balance our contact hazard is twice theirs (0.082 vs 0.040 at b=+2), in our upper-tier wins too (0.066 vs 0.041).

Correlation only: within-opponent, the stay share correlates with kill share -0.14 +- 0.08 (upper) and -0.07 +- 0.04 (rest).

Size:
- upper bound about -25 deaths per upper-tier game (-3%) and -1 to -2% in the rest; kill share +0.005 to +0.01
- side effect: fewer of our deaths on our territory means fewer 30-crumb rewards funding their stuns
- EV about +1.2 pp (range +0.5 to +2) x P(delivery) about 0.55

Signature: New --capabilities column reachEndFree, with count columns reachEndFreeN and reachEndFreeD so it pools as sum N / sum D. It is built on ReplayDump's step-census reconstruction plus passability and occupancy at the robot's turn.

Definition:
- Denominator: post-setup turns of our robots alive and not carrying, both cooldowns ready, HP >= 300, with an enemy within dist2 4 at the robot's turn, where some legal adjacent tile is outside every enemy's dist2 4 and within dist2 10 of no enemy that was not already within dist2 10 of the start tile.
- Numerator: those turns that end within dist2 4 of an enemy.

g_iter7 baselines (720-game control):
- us: 0.252 (upper games) / 0.283 (rest)
- upper bots 0.111, rest bots 0.178

Arm target: <= 0.12 in both tiers.

Existing columns logged as consequences: deaths, killShare, stepDeaths, enemyStunTrig, homeDeathShare.

5(a): Diagnostic first (rule 5): g7kite vs andli28.v9_USQuals_angle on DefaultMedium, side A, seed S.
- 'kr' > 0 by r260
- --metrics 50 rows through r200 equal to the g_iter7 twin
- overruns 0 and exceptions 0

Cells: tools/diag-batch.sh, one seed S written into the registration before any game, not in tools/band-seeds.txt (313131-868686) and not 909090. The same 12 cells and the same g_iter7 twins serve all four levers.

Upper (U):
- IvanGeffner.kuma:DefaultMedium:S:A
- andli28.v9_USQuals_angle:FloodGates:S:B
- Strequals.duck0127v5:Fountain:S:A
- chenyx512.flagbot_final:Randy:S:B
- jmerle.camel_case_v21_final:QuestionableChess:S:A
- andli28.v9_USQuals_angle:Puzzle:S:B

Rest-of-band control (R), same maps, opposite sides:
- Gymhgy.v10official:DefaultMedium:S:B
- NotLLeon.v3:FloodGates:S:A
- hsmalladi.finalbot:Fountain:S:B
- andrewgopher.player22:Randy:S:A
- CyrilSharma.finalBot:QuestionableChess:S:B
- ColtG5.Goob_final:Puzzle:S:A

FIRES iff all of:
(a) Wiring: 'kr' > 0 in 12/12; metrics through r200 identical to the twin in 12/12; overruns 0 and exceptions 0.
(b) Signature: reachEndFree pooled <= 0.12 in U and in R separately (twins about 0.25 / 0.28), and lower than the twin in >= 5/6 cells of each half.
(c) Outcome sign: killShare mean (arm - twin) >= 0 over the 12, and the R half not below -0.02.
(d) Guards:
- enemyStunTrig summed <= 1.05x twins (retreat steps must not walk into more enemy stuns)
- enemyCaptured summed <= twins + 2 (R half <= twins + 1)
- captured summed >= twins - 2

Logged without a bar: deaths, kills, stepDeaths, healThreat10, readyHeld20, survey heal400, the analyser's standHitsRech.

Falsifier: (b) passes but pooled deaths are not below the twins and the killShare mean is <= 0. That means enemies simply step in and hit: close the line, do not dose up.

If (a) passes and (b) fails, the bot's sensing differs from the replay reconstruction: trace 2 games before any dose.

Delivery afterwards (rule 13):
BASE=g_iter7 tools/delivery-gate.sh g7kite 'rel:reachEndFree<=0.5 nw:enemyCaptured<=1.1 mean:overruns<=0'
No deaths bar: a 3% deaths change cannot resolve in 24-96 cells, and the band test judges body trades (PROMPTS 183).

Risk: 1. Staying feeds follow-up kills. The struck target dies within 2 rounds 0.38 after a stay vs 0.31 after a kite (upper games), and 0.56 vs 0.45 in rest games. That is selection-confounded, but it is the main rest-of-band risk.
2. On its own the lever cuts strikes within 2 turns by about 0.11-0.15 per affected decision. Once out of reach, a robot that is ready next turn still retreats to dist2 11-20 under the current score and loses a turn. Lever 2 is the fix.
3. If upper bots simply step in, hits taken do not fall. The falsifier catches this.
4. Retreat moves trigger enemy stuns in about 12% of cases; guarded by enemyStunTrig.
5. Small per decision (about 300-400 decisions a game). Alone, P(ships) is about 0.25.

Expected band gain x P(delivery) is about 1.2 pp x 0.55, about 0.65: the top expected value, and the cleanest change (one term, no state, no comms).

## 2. C.RC_BAND (arm g7kiterc = KITE_REACH_W 300 + RC_BAND 60): hold at one-step range while the strike recharges

Mechanism: Where: src/bot/Micro.java, Micro.fight. Compute once per turn:
  boolean rcOn = C.RC_BAND > 0 && goal == null && !actReady && !hurt
      && rc.getActionCooldownTurns() < 20
      && rc.getHealth() >= C.RC_HP
      && nearAllies + 1 >= nearEnemies;
rc.getActionCooldownTurns() < 20 means not ready now but ready next turn. C.RC_HP = 700.

In the kite/hold else-branch, when rcOn, score each tile as:
  int thEff = (th >= 1 && inRange == 0) ? th - 1 : th;
  score = -thEff * 1000 + adjAllies * 10
        + ((th == 1 && inRange == 0) ? C.RC_BAND : (minD >= 11 && minD <= 20 ? 50 : 0));
Then add the KITE_REACH term.

With RC_BAND 60:
- a tile in one-step range of exactly one enemy, and out of everyone's reach (dist2 5-10), beats the 11-20 band (50)
- two threats still cost 1000
- next turn the robot is ready at one-step range: it steps in and strikes, or strikes the enemy that stepped in

With lever 1 this gives a 2-turn cycle: strike and leave reach, hold close, strike. Today's cycle is strike, stay in reach, retreat to 11-20, approach or heal, strike.

Indicator 'rc': turns rcOn changed the chosen tile. Default 0 keeps g_iter7.

Dose ladder:
(a) R20-only: rcOn also needs the robot's start minD > 10. Every upper bot closes from dist2 11-20, while from dist2 5-10 the upper tier is split.
(b) RC_HP 500.

This is the open upper-tier-micro proposal #2 (RECHARGE_CLOSE), never tested. Not forward drift (that moves the army's goal), not zone holds, not ENGAGE_HP (it gates no step-in).

Evidence: Code (verified): the kite branch charges -1000 per enemy within dist2 10 and +50 for dist2 11-20, so a non-ready robot never accepts one-step range.

Micro lens, 720 games. Class: ready next turn, nearest enemy at dist2 11-20, a free close tile threatened by at most one enemy. Share ending within dist2 10:
- us 0.021 vs upper bots 0.458 (about 1,603 decisions a game)
- us 0.024 vs rest bots 0.412
- all 5 upper bots (0.18-0.65) and 14 of 15 rest bots are far above our 0.013-0.045 (hsmalladi 0.034 is the exception); same in every length bin

Tempo context (upper games):
- strike turns per game: them 3,152 vs us 1,859
- step-in share of our hits: 0.685 vs 0.513
- R20 ready robots heal 27-33% of the time instead of closing

Payoff evidence is weak:
- Upper closers strike within 2 turns 0.43 vs their holders 0.11, at +0.10 hits taken. That is a within-team selection comparison.
- Class averages are close: 0.277 vs 0.235 (upper games), 0.250 vs 0.242 (rest).
- Within-upper correlation of R20 closing with kill share is +0.87 +- 0.21, but retreating at R10 also correlates +0.54 +- 0.11. That points to an 'initiative' confound; correlation only.

Cross-lens:
- Traps: 88% of our triggers of their stuns happen on moves toward them, and 66% on strike turns. Closing is a move toward them, which is the main risk.
- Position: per-contact hazard at equal balance is twice theirs.

EV about +1.5 pp (range -1 to +3) x P(delivery) about 0.3; about 0.45 given that lever 1 fires.

Signature: New column rcClose20, with count columns rcClose20N and rcClose20D; built on the step census.

Definition:
- Denominator: post-setup turns of our robots alive and not carrying, move ready, action cooldown in [10, 20) at the turn (ready next turn), HP >= 300, nearest enemy at dist2 11-20, and some legal adjacent tile within dist2 10 of exactly one enemy and beyond dist2 4 of all.
- Numerator: those turns that end within dist2 10 of an enemy.

Baselines:
- us: 0.021 (upper games) / 0.024 (rest)
- upper bots 0.458, rest bots 0.412

Arm target: >= 0.25.

Existing columns as consequence and guard: killShare, deaths, enemyStunTrig (their stuns triggered by us: 439 a game in upper games, 164 in rest), survey heal400.

5(a): Runs only after g7kite's 5(a) fires.

Arm: g7kiterc. Twins: g7kite and g_iter7 on the same 12 cells and seed S as lever 1 (the g_iter7 twins are reused, so 24 new games).

Diagnostic first: g7kiterc vs Strequals.duck0127v5 on Fountain, side A, seed S.
- 'rc' > 0 by r260
- metrics through r200 identical to the twin
- overruns 0

FIRES iff all of:
(a) Wiring: 'rc' > 0 in 12/12; identical through r200; overruns 0 and exceptions 0.
(b) Signature: rcClose20 pooled >= 0.25 in U and in R separately (twins about 0.02), and above the g7kite twin in 12/12.
(c) Outcome: killShare mean (arm - g7kite twin) >= +0.01 over the 12 with >= 7/12 positive, and the U half >= 0.
(d) Guards, all against the g7kite twin:
- enemyStunTrig summed <= 1.08x (closing must not walk into their front stuns)
- survey heal400 summed >= 0.85x (HEAL_HOLD blocks heals at dist2 <= 10; heal XP is the level-sum gap)
- deaths summed <= 1.03x
- enemyCaptured <= twin + 2 (R half <= +1)
- captured >= twin - 2

Falsifier: (b) passes, but killShare mean <= 0, or deaths > 1.03x, or enemyStunTrig > 1.08x. Then one-step range only feeds their stand hits and stuns: close. If the stun guard alone fails, run dose (a), R20-only, once.

Delivery afterwards:
BASE=g7kite tools/delivery-gate.sh g7kiterc 'mean:rcClose20>=0.2 nw:enemyStunTrig<=1.1 nw:enemyCaptured<=1.1 mean:overruns<=0'

Risk: 1. One-step range (dist2 5-10) is where upper-tier front stuns sit. Their one-step stuns are 335 of 464 a game, and 88% of our triggers are moves toward them. A frozen robot loses the strike it was about to make. Guarded by enemyStunTrig.
2. The robot is exposed to enemy step-in strikes between turns (+0.10 hits taken among upper closers).
3. HEAL_HOLD: ready robots at dist2 <= 10 do not heal, so heals and heal XP fall. Level sum decides 82 of 720 games and 22 of the 130 upper-tier losses.
4. The payoff evidence is selection-biased and ambiguous at R10.
5. Depends on lever 1. If lever 1's falsifier trips (enemies just step in), this line almost surely fails too; do not run it alone.

Expected band gain x P(delivery) is about 1.5 x 0.3, about 0.45. Together with lever 1 it is the one stack with a plausible ship-size effect, on the precedent of HEAL_HOLD's tempo axis.

## 3. C.STUN_MIN13 (arm g7sg: STUN_MIN13 5, STUN_RICH 700): spend the scarce field-stun crumbs on big groups

Mechanism: Where: src/bot/Duck.java, placeCombatTrap, the g_iter1 placement branch (TRAP_PLACEMENT_V2 false), after 'MapLocation t = G.me.add(G.me.directionTo(c));' (line 1452) and before canBuild. Add:
  if (C.STUN_MIN13 > 0 && rc.getCrumbs() < C.STUN_RICH && !guardFight() && !carrierInView) {
      int n13 = 0;
      for (RobotInfo e : enemies) if (t.distanceSquaredTo(e.location) <= 13) n13++;
      if (n13 < C.STUN_MIN13) { stunSkips++; return; }
  }
carrierInView means any enemy in Duck.enemies hasFlag.

Indicator 'sg'. Default 0 keeps g_iter7.

The bank then holds between the 300 gate and STUN_RICH 700 until a robot sees 5 or more enemies near its stun tile. 700 is below FLOAT_CRUMBS 1500, so float stuns never spend the saved bank. At 700 or more the old rule applies, which caps the waiting.

Unchanged: carrierStun, defend() rings, dam traps, spendFloat, STUN_ENEMIES_MIN 3, TRAP_RESERVE 200, the placement tile, and the stun at an alerted home (the guardFight exemption already used by g7fc).

Dose ladder: 4, then 3. Dose 3 drops only the 0-2 class: 21% of field stuns, 3.36 victims each, median latency 36 rounds.

Differs from closed lines:
- g4front moved the tile within the same moment, toward the front, and lowered the reserve.
- ct1/ct2 lowered the vision threshold.
- ring reserve and the dam-line budget concern setup and flags.
- This lever changes only which moments get the crumbs.

Evidence: Economy lens, --trapgeo over 72 games (48 against the top four), about 13k of our field stuns. Victims per built stun, by enemies within dist2 13 of the tile at build:
- 0-2 enemies: 3.36 +- 0.06
- 3-4: 4.31 +- 0.04
- 5+: 6.93 +- 0.05
- class shares 0.21 / 0.39 / 0.40; median latency 36 / 10 / 3 rounds

Rest-of-band sample (36 games): 2.83 / 3.78 / 6.16. The gradient is band-wide.

Our stuns are limited by crumbs: the bank sits at 195-290 at every checkpoint from r250 to r2000 in both tiers, below the 300 gate (verified: 100 + TRAP_RESERVE 200). So every stun is built at the first chance after the bank refills.

Census, 720 games:
- we trigger about 200 stuns a game in both tiers (upper 200, rest 205); they trigger 439 (upper) and 164 (rest)
- victims per our trigger: 4.38 (upper games) / 4.39 (rest); theirs on us 5.05 / 5.41

The traps lens independently finds high-yield moments in big fights:
- counter-stuns catch 6.19 victims and 0.82 kills per trigger vs 4.49 and 0.64 for all our stuns (upper)
- kills per our trigger rise with nearby support from 0.17 to 1.30

Upper bound: +36% victims per crumb at dose 5, +11% at dose 3. Expected after waiting costs: +10-20% stun victims (about +90-180 a game), about +7-14 kills a game in both tiers.

Correlation only: enemies near the tile also measures how hot the fight is. g4front, which placed stuns on the tile with the most enemies (at least 3), left victims per stun unchanged against Cyril (5.2 vs 5.3, 24 cells). That tested placement within a moment, not the choice of moment, but it warns that build-time yield is partly fight heat.

EV about +1.0 pp x P(delivery) about 0.35.

Signature: Existing columns: our victims per triggered stun, sum stunVictims / sum stunTrig.
- g_iter7 baselines: 4.38 (upper games) / 4.39 (rest); their stuns on us 5.05 / 5.41
- arm target: >= 4.8 pooled, with stunVictims per game >= 1.05x twins (the saved crumbs are spent, not lost)
- this pools in setup traps triggered after r200 (about 45% of triggers), which dilutes it

Logged:
- stunVictimsFast / stunVictims (0.43 now, expected >= 0.50)
- trapsBuilt (0.85-1.05x)
- --trapgeo share of field stuns built with 5+ enemies within dist2 13 (0.40 now, expected >= 0.60)
- the metrics bank from r400 to r1500 (about 230 now, expected 300-600)

5(a): Arm: g7sg. Same 12 cells (6 upper U, 6 rest R), seed S and g_iter7 twins as lever 1.

Diagnostic first: g7sg vs andli28.v9_USQuals_angle on DefaultMedium, side A, seed S.
- 'sg' > 0 by r300
- metrics through r200 identical to the twin (placeCombatTrap runs only after setup)
- overruns 0

FIRES iff all of:
(a) Wiring: 'sg' > 0 in 12/12; identical through r200; overruns 0 and exceptions 0.
(b) Signature: sum stunVictims / sum stunTrig >= twin + 0.4, pooled in U and in R separately (twins 4.38 / 4.39), and higher in >= 4/6 cells of each half.
(c) stunVictims summed >= 1.05x twins pooled.
(d) Guards:
- carrierStunned summed >= 0.9x twins
- enemyFirstGrabs summed <= 1.1x twins (small raids on our flags must not walk past unstunned)
- enemyCaptured summed <= twins + 2 (R half <= +1)
- captured >= twins - 2

Logged: kills, deaths, killShare, trapsBuilt, stunVictimsFast, --trapgeo class shares, bank.

Falsifiers:
- (b) passes but (c) fails: waiting costs eat the yield. Run dose 4 once, then close.
- (b) fails: build-time yield is fight heat, not choice. Close.

Delivery afterwards:
BASE=g_iter7 tools/delivery-gate.sh g7sg 'rel:stunVictims>=1.08 nw:enemyCaptured<=1.1 mean:overruns<=0'
No kills guard (PROMPTS 183).

Risk: 1. Small skirmishes and small raids lose their early stun. Mitigated by the guardFight and carrier-in-view exemptions, plus the enemyFirstGrabs guard.
2. The yield gradient is partly fight heat; see the g4front caveat. P is held at 0.35, not 0.4.
3. The bank floats between 300 and 700, so about 400 crumbs sit idle at times. The cap and the 1500 float gate stop it from feeding float stuns.
4. The 0-2 class stuns are our slow traps. They still catch 3.36 enemies each, mostly in transit, and their deterrence is not counted.
5. Independent of levers 1, 2 and 4 (different subsystem), so it can run in parallel and stack.

Expected band gain x P(delivery) is about 1.0 x 0.35, about 0.35.

## 4. C.HEAL_FRONT (arm g7hf: HEAL_FRONT 300): heal threatened front-line allies before safe, badly hurt ones

Mechanism: Where: src/bot/Micro.java, tryHeal (lines 57-74).

Replace the lowest-HP loop with a lowest-key loop:
  int key = a.health - (C.HEAL_FRONT > 0 && enemyWithin(a.location, C.HOLD_R2) ? C.HEAL_FRONT : 0);
Pick the minimum key among allies with a.health < HEAL_HP_BELOW and rc.canHeal. enemyWithin uses Duck.enemies, which the healer sensed.

The HEAL_HOLD skip (hold && !a.hasFlag), CARRIER_HEAL and the attacker/builder exits are unchanged. Default 0 keeps g_iter7.

Arm 300:
- a threatened ally at 750 HP (key 450) beats a safe ally at 500, not one at 400
- a threatened ally below 300 HP always beats a safe one at the same HP

Indicator 'hf': heals whose target differs from the lowest-HP candidate.

Dose ladder: 1000 (threatened allies always first).

Changes whom we heal, not whether we heal, so heal XP is unchanged. Not the closed retreat-threshold (T8) or HEAL_HOLD lines.

Evidence: Micro lens, v5 subset of 180 games (40 upper):
- multi-candidate heals going to the lowest-HP candidate: us 0.931 vs upper 0.736 vs rest 0.762
- threatened target chosen in mixed candidate sets: 0.494 vs 0.580 / 0.637
- heals on threatened allies at HP >= 700: 0.095 +- 0.004 vs upper 0.169 +- 0.015; rest games 0.118 vs rest bots 0.204
- heals on safe allies below 300 HP: 0.158 vs 0.063; rest games 0.134 vs 0.059

Both tiers differ from us, so this is policy.

The position lens agrees on the outcome (720 games, all four win/loss groups): our wounded robots in contact are healed the next round 0.17-0.18 of the time vs 0.20-0.25. It attributes part of this to healer readiness and HEAL_HOLD.

Our damage episodes end healed to full 0.176 vs 0.316 (upper games), but 0.236 vs 0.241 in rest games, so that part is partly an outcome.

The value per heal is unmeasured. Healed threatened allies strike within 2 turns 0.44-0.51 vs 0.09-0.18 for safe ones; that is selection, not effect.

Plausible effect:
- deaths -1%
- kill share +0.003 to +0.008
- fewer of our deaths on our territory feeding their stun budget

EV about +0.5 pp x P(delivery) about 0.35.

Signature: New column healFront700.
- Definition: post-setup heals by our robots on an ally with HP >= 700 before the heal and an enemy within dist2 10 of that ally (step-census positions), divided by all post-setup heals.
- Baselines: 0.095 (upper games) / 0.118 (rest); upper bots 0.169, rest bots 0.204.
- Arm target: >= 0.14 (1.4x).

Logged:
- healSafe300 (0.158 now, expected <= 0.11)
- the position lens's next-round heal share of wounded robots in contact (0.17-0.18 now)
- survey heal400
- killShare, deaths, homeDeathShare

5(a): Arm: g7hf. Same 12 cells (6 upper U, 6 rest R), seed S and g_iter7 twins as lever 1.

Diagnostic first: g7hf vs IvanGeffner.kuma on DefaultMedium, side A, seed S.
- 'hf' > 0 by r300
- metrics through r200 identical to the twin
- overruns 0

FIRES iff all of:
(a) Wiring: 'hf' > 0 in 12/12; identical through r200; overruns 0 and exceptions 0.
(b) Signature: healFront700 pooled >= 1.4x twins in U and in R separately.
(c) survey heal400 summed >= 0.95x twins (heals are re-aimed, not lost).
(d) killShare mean (arm - twin) >= 0 over the 12, with the R half >= -0.02.
(e) Guards: enemyCaptured summed <= twins + 2; captured >= twins - 2.

Falsifier: (b) passes, the killShare mean is <= 0 and deaths are not lower. Then front heals do not change who dies: keep it only as a stack layer, no dose-up.

Delivery afterwards:
BASE=g_iter7 tools/delivery-gate.sh g7hf 'rel:healFront700>=1.4 nw:enemyCaptured<=1.1 mean:overruns<=0'

Risk: 1. Hurt robots that retreated are healed later and return to the fight later, at lower HP.
2. The value per heal is unmeasured: the strike gap between threatened and safe heal targets is selection.
3. The evidence is a 180-game subset, 8 games per upper bot.
4. The rest bots, whom we beat, also prioritise threatened allies, so the trait is not shown to win.
5. Heal targets dying within 3 rounds flips by tier (0.060 vs 0.026 upper, 0.037 vs 0.078 rest).
6. Very cheap and composable: a natural layer on levers 1-2, since the same front-line robots gain HP and leave reach.

Expected band gain x P(delivery) is about 0.5 x 0.35, about 0.18. It is ranked above JOIN_FIGHT (similar EV, but a flag-pressure risk against the rest) and STUN_PRESS (realistic +17 strikes a game, plus comms and frozen-enemy guessing).


## Review amendments (2026-10-07)

A code review of the g7kite build (src/bot Micro/C/G, ReplayDump's reach census, AuditTest) changed these clauses of lever 1's registration before any game. Each one replaces the clause it names. src/g7kite was re-snapshotted after the code changes (KITE_REACH_W 300, KITE_REACH_HP 700, KITE_REACH_CAP 2; tools/arm-intent.txt pins all three).

R1. Mechanism: the gate and the cap. Replaces the term in "Mechanism" (with the critic's HP gate):
  krGate = kiteReachGate(goal == null && carrier == null, actReady, hurt, rc.getHealth());   // HP < KITE_REACH_HP 700
  if (C.KITE_REACH_W > 0 && krGate) score -= Math.min(inRange, C.KITE_REACH_CAP) * C.KITE_REACH_W;   // KITE_REACH_CAP 2
- Carrier gate. A hurt robot skips the carrier branch, so with an enemy carrier in view it scored kite tiles with the term. It then stepped out of the carrier's reach, where g_iter7 often stays (a tie) and strikes the carrier first next turn. The census leaves out every turn with an enemy carrier within dist2 20, so neither reachEndFree nor reachHit could see this. The gate now matches the census exclusion, and the signature's definition does not change.
- Cap. inRange x 300 was not always below one threat. With 4 enemies in reach the term is 1200, so an exit with one more threat won (-5000 vs -5200). With 3 in reach, 900 plus an exit's adjacency and crumb edge (up to 121) could also pass 1000. Capped, the term is at most 600, and 600 + 80 + 40 + 1 < 1000, so the arm never pays a threat to leave reach. Among reaching tiles it still prefers one enemy in reach to two. It still takes every free exit the census counts (300 beats every non-threat term), so (b) is unchanged.
- Dose ladder. 1200 means the same as before: min(inRange, 2) x 1200 >= 1200 > 1000 pays one extra threat to leave reach.
- Unit tests (AuditTest, both switch states; the fake controller now accepts attacks, BotTest.attackOk):
  - strike first from reach, then kite out the same turn: kr +1 (switch off: stays)
  - 250 HP, enemy carrier in reach, strike ready: stays, no kr turn
  - two enemies in reach, no exit: moves to the tile with one in reach
  - four in reach, and the only exit adds a threat: stays (the cap)
  - each regression the review named fails its case: the gate read before the strike, the carrier gate removed, the cap removed

R2. Identity bar. Replaces "--metrics 50 rows through r200 equal (identical) to the g_iter7 twin" in the diagnostic and in (a):
  --metrics 50 rows through r200 equal to the g_iter7 twin's on every column except max_bc, for both teams, and our max_bc minus the twin's in [0, +200] in every row.
- Why max_bc differs: the src/bot indicator string carries five counters that frozen g_iter7 does not (kr, eh, fc, fcF, ss), and it is built every turn, setup included.
- Measured on diag/ehp5a/metrics.csv: g7ehp vs its g_iter7 twin, rows r50-r200, all 12 games. Every column is equal except our max_bc, which is +16 to +32 in every row.
- This changes no decision. fight() never runs in setup, and the string is set after Sym.update and finalDig.
- KITE_REACH_W 0 keeps src/bot play-identical to g_iter7, not byte-identical (line 81 above).
- The same bar replaces "identical through r200" in the diagnostics and (a) bars of levers 2-4.

R3. Signature baselines and validation. Replaces "validate against the Cm proxy 0.183 U / 0.203 R within +-0.01" and "twin proxy 0.183 / 0.203" in (b):
- Twin baselines: reachEndFree 0.182 U / 0.191 R. This is the committed census, which reads true water (water-trap digs included).
- The validation passed with the analyser's water (map and dig/fill vectors only):
  - 706 of the 720 g_iter7 control games match the analyser exactly, and the rest within 2 turns
  - pooled, both read 0.183 / 0.203
- The R gap (0.203 vs 0.191) is all in opponents that build water traps: dmtrung14 (0.479 -> 0.207), SampleProvider, hsmalladi, quesswho. hsmalladi is an R cell, so R's twin read uses true water.
- To rerun the +-0.01 check: REACH_VECWATER=1 tools/replay-dump.sh <replay> --capabilities gives the reach census the analyser's water. No other column changes.
- Checked 2026-10-07 on the VM on dmtrung14 Asteroids, SampleProvider Gated and hsmalladi Klein:
  - with the switch, reachEndFreeN, reachEndFreeD, reachHitN and reachKilledN equal the analyser-water census run for both teams
  - without it, they equal the true-water run
  - kills, deaths and stepMidN are the same either way

R4. Exposure read and falsifier. Amends bar (c) and the falsifier:
- Report reachEndFreeD (arm / twin) for each half next to (c)'s reachHit. The two denominators count different turns:
  - In the arm, a robot under 700 HP leaves reach after each strike. On the recharging turn the kite score (-1000 per threat, +50 band) pulls it out to dist2 11-20.
  - Its next strike is then a step-in strike, which starts out of reach, so the denominator never counts it.
  - Arm reach turns are mostly turns where an enemy stepped into us. Twin reach turns are mostly sustained contact.
  - So (c) can pass or fail on composition alone. The 0.81x / 0.75x projection held the tie-class population fixed.
- Exposure read E, which does not depend on that population and uses existing columns: enemy hits per our strike = sum of the opponent's attacks / sum of ours. Read attacks (cumulative) from the final-round rows of tools/replay-dump.sh <replay> --metrics 1. Pool per half and over the 12 cells, arm vs twin.
- Logged beside E:
  - opponent attacks per our robot-round alive (meanAlive x rounds, --capabilities)
  - stepMidN / stepDec and stepDeaths, arm vs twin: hits after our step-in strikes, which reachHit never sees
- Falsifier: as registered, and it also trips when (b) passes and E pooled over the 12 cells is not below the twins' (arm >= twin). Enemies simply step in and hit: close the line, do not dose up.

R5. Analyser reads: delivery item 10 and two logged items. These have no census column:
- delivery item 10: U and R enemy deaths near our robots (eD20)
- logged: stand hits on recharging robots
- logged: enemy deaths within 20 rounds
They are read with the study's analyser, which also runs the tie-break test. Its sources are now in research/upper-tier-study-2026-10-07/tools/: Cm.java (the study's src6 code unchanged), t6.py, agg3.py and r3.py (hard-coded paths turned into arguments), and run-cm.sh. Run the dumps on the VM, 3 at a time, with arm and twin in separate directories:
  T=research/upper-tier-study-2026-10-07/tools
  $T/run-cm.sh ~/cm/<tag>-arm diag/<tag>/g7kite-vs-*.bc24      (a delivery run: <run>/replays/*.bc24)
  $T/run-cm.sh ~/cm/<tag>-twin diag/<tag>/g_iter7-vs-*.bc24
  python3 $T/t6.py proj ~/cm/<tag>-arm    T rows per decision: eD20 (enemies within dist2 20 of the deciding robot dead within 20 rounds), k20, d3/d10/d20
  python3 $T/agg3.py ~/cm/<tag>-arm g     G rows: stand hits on recharging victims (share of hits and per game), us and them
  python3 $T/r3.py ~/cm/<tag>-arm         the randomized tie-break test (R rows)
Checked on the VM:
- run-cm.sh output is byte-identical to the study's out6 CSVs on two control games, under both gauntlet and diag-batch names
- t6.py sig, proj and reach on out6 match the study's saved outputs
- r3.py on out3 matches the critic's run, and gives the same result on out6 (Cm.java's R rows)
- agg3.py g reproduces the stand-hit shares 0.111 / 0.189 and 649 / 1,227 a game
