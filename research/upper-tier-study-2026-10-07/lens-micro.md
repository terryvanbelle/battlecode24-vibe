# Lens micro

## Our kite/hold scoring has a defect: it scores standing in an enemy's reach the same as stepping one tile out of it. After striking, or while recharging, our robots stay within reach (dist2 <= 4) about 2.5 times as often as the upper tier, and more often than the rest of the band too, so this is our policy and not a result of losing. (strong)

Evidence: Source: scratch analyser on all 720 g_iter7 control replays (180 upper-tier games). It rebuilds each robot's turn in execution order, and it reproduced ReplayDump's step census exactly on a test replay: 3839/2451 and 4893/1763.

Strike turns = a robot starts in reach with action and move both ready, HP >= 300, and a free tile out of reach adds no threat. Share that end in reach:
- upper games: us 0.252 +- 0.006 vs them 0.111 +- 0.005
- rest games: us 0.283 +- 0.005 vs them 0.178 +- 0.009
- Over all strike turns: 0.384 vs 0.218 (upper) and 0.432 vs 0.295 (rest).
- The gap holds in every game-length bin (<1000, 1000-1999, r2000) and in both phases (r<=600 and later).
- Per opponent: all 5 upper bots end in reach less than we do (kuma 0.18 vs our 0.21 is the closest; chenyx512 0.02 vs 0.21). 11 of 15 rest bots do too; the exceptions are CyrilSharma 0.63, HugoIngelsson 0.44, clbarrell 0.42 and SampleProvider ~parity, all bots we beat.

Code cause (src/g_iter7/Micro.java:230-234): the kite branch counts threats within dist2 10. One step from dist2 <= 4 can never get beyond dist2 10 of that enemy, so stepping out of reach earns nothing. Adjacency (+10 per ally), crumbs (+40) and the +1 for staying decide the tile instead.

Replaying our kite score reproduces this. In 21-24% of free-exit strike turns, every best-scoring tile is in reach (about 314 a game in upper games, 335 in rest). We then end in reach 99.9% of the time; upper bots in the same situations kite out 80% of the time, rest bots 73-76%.

The same gap appears when recharging in reach (free exit): we stay 0.141 vs upper 0.055 vs rest 0.119.

Confound: The share is measured against a free tile that adds no threat, so being boxed in does not explain it. Our clustering (the adjacency bonus) helps create these situations, and that is part of the same policy.

Within-opponent correlation of the stay share with kill share is -0.14 +- 0.08 (upper) and -0.07 +- 0.04 (rest), with wins -0.50 +- 0.19 (rest). This is correlation only: staying is also more common in crowded fights.

## Staying in reach after a strike costs more than it gains, but only modestly per decision. Matched-class upper bound: about 25 fewer deaths per upper-tier game (about 3%) if our stays had the upper tier's kite outcomes. (moderate)

Evidence: Class compared: upper games, strike turn with a free exit where our kite score stays in reach.

Our stays vs their kites:
- hit before our next turn: 0.328 vs 0.160
- died within 3 rounds: 0.048 vs 0.017
- strike within the next 2 turns: 0.480 vs 0.369

Our own kites (where our score already picks an exit): hits 0.102, d3 0.026, strikes 0.363.

Split by whether the robot can strike again next turn (180-game subset):
- not ready next turn: in-reach ends us 0.243 vs them 0.112; staying costs +0.15 hits and +0.026 d3 for +0.12 strikes
- ready next turn (about 1/3 of strike turns): us 0.280 vs them 0.084; staying costs +0.23 hits and +0.06 d3 for +0.15 strikes
- HP < 300: our stays die 40-51% vs their kites 20-23%.

Rest games, same class: our stays hit 0.234 / d3 0.023 / strikes 0.489 vs our kites 0.075 / 0.013 / 0.380.

Confound: The 'their kite' and 'our kite' counterfactuals come from other robots in the same coarse class, so selection remains.

Staying feeds follow-up kills: the target dies within 2 rounds 0.38 after a stay vs 0.31 after a kite (upper games), and 0.56 vs 0.45 in rest games. That is selection-confounded but a real risk for the rest of the band.

The effect is small: about 300-400 affected decisions per game.

## Tempo signature: the upper tier gets many more free hits on our recharging robots, and lands more of its hits standing than stepping in. We hit mostly by stepping in, which leaves our robot in reach on cooldown. (strong)

Evidence: Upper games:
- step-in hits as a share of all hits: us 0.685 +- 0.005 vs them 0.513 +- 0.007
- stand hits on a victim whose action cooldown is >= 20: them 0.189 (1227 a game) vs us 0.111 (649 a game)
- strike turns (start in reach, both cooldowns ready): them 3152 a game vs us 1859
- hits per game: 6478 vs 5874 (+10%); kill blows 753 vs 452 (+67%)
- stepper dies within 3 rounds: us 0.118 vs them 0.074

Rest games: step-in share 0.640 vs 0.496 (we are the outlier again); stand hits on recharging victims 0.156 vs 0.182.

Per opponent, all 5 upper bots take a larger stand-hit share than we do (andli28 0.211 vs 0.083, chenyx 0.158 vs 0.108, jmerle 0.161 vs 0.140, Strequals 0.148 vs 0.129).

Confound: Stepper deaths reverse by tier (rest: us 0.063 vs them 0.146), so that part follows relative strength.

The step-in share is high in both tiers, which points to policy. Part of the stand-hit volume comes from our step-ins as such; the ENGAGE_HP line touches that and is parked.

## Recharge distance: when its strike is ready next turn and the nearest enemy is at dist2 11-20, our robot almost never closes to one-step range. Nearly every other bot does. The strike gain measured for closing is small at class level. (moderate)

Evidence: Class: ready next turn, nearest enemy at dist2 11-20, a free tile one step from reach of at most one enemy. Share ending within dist2 10:
- us 0.021 vs upper 0.458 (1603 decisions a game)
- us 0.024 vs rest 0.412
- All 5 upper bots (0.18-0.65) and 14 of 15 rest bots are far above our 0.013-0.045; hsmalladi 0.034 is the exception. Same in every length bin.

Code cause: the kite branch charges -1000 per threat and gives +50 for the dist2 11-20 band, so a recharging robot always stays out of threat range. Our cycle runs strike, retreat to 11-20, approach (or heal: R20 ready robots heal 27-33%), then strike.

Payoff:
- Upper closers strike within 2 turns 0.43 vs their holders 0.11, at +0.10 hits taken.
- Class average strikes within 2 turns: them 0.277 vs us 0.235 (upper games), 0.250 vs 0.242 (rest).
- At dist2 5-10 the upper tier is split: Strequals and chenyx hold close 0.69-0.79, while andli28 and jmerle (0.21-0.27) retreat like us (0.24-0.33).
- Within-opponent correlation with kill share: R20 closing +0.87 +- 0.21 (upper), +0.12 +- 0.13 (rest). But R10 retreating also correlates positively (+0.54 +- 0.11, upper).

Confound: Within-team comparisons are selection-biased: bots close when closing pays. Cross-team class averages carry fight state.

The positive correlations of both closing at R20 and retreating at R10 suggest that 'having space or initiative' drives both.

HEAL_HOLD forbids heals at dist2 <= 10, so closing would cut our heals (a level-sum risk).

## Target selection and focus fire are at parity, which refutes FOCUS_STEP. The gap in step-in conversion is fight state, not target choice. (strong)

Evidence: Lowest-HP enemy in reach chosen (when 2 or more are in reach): us 0.958 vs upper 0.921 vs rest 0.856.
Available kill taken: 0.990 vs 0.983.

Full-HP step-in strikes:
- our target is 25 HP above the weakest enemy any free reaching tile offered; upper 29
- when our engage score's tile reaches a weaker target than the best, the upper bots' struck targets sit just as far from the best (336 vs 369 HP)
- killable target available: taken 0.860 vs 0.869

This answers the andli28 critic's open question 7: FOCUS has no reach.

The step-in conversion gap (target dies within 2 rounds 0.228 vs 0.395, upper games, HP >= 700) reverses in rest games (0.365 vs 0.311).
Attackers per fatal episode: 3.46 vs 3.62.

Confound: Follow-up depends on numbers near the target, which is an outcome of the wider fight.

## Finishing wounded enemies (chasing) is not a gap. The kill-conversion gap sits in their robots getting healed back to full and in stuns. (strong)

Evidence: Robots that fall below 300 HP with an enemy within dist2 20 and die within 5 rounds (upper games): theirs 0.438 vs ours 0.434, parity. In rest games we finish better (0.600 vs 0.367 for ours).

Upper games:
- their damage episodes end with the robot healed to full 0.316 vs ours 0.176 (rest parity 0.241 vs 0.236)
- hits on targets at <= 450 HP: 0.332 vs 0.221 (rest parity 0.300 vs 0.297)
- attacks per kill blow: 13.0 vs 8.6
- their hits on frozen (stunned) targets: 0.134 vs our 0.049 (rest 0.080 vs 0.070); that belongs to the trap lens

Confound: Episode outcomes flip by tier, so they are partly an outcome of who is winning.

## Heal-target policy differs from both tiers. We heal the lowest-HP ally in range, which sends heals to hurt robots out of the fight. The upper and rest tiers both put more heals into threatened front-line robots. (moderate)

Evidence: Source: v5 scratch run, 180-game subset (40 upper).

- Multi-candidate heals going to the lowest-HP candidate: us 0.931 vs upper 0.736 vs rest 0.762.
- In mixed threatened/safe candidate sets, a threatened target chosen: 0.494 vs 0.580 vs 0.637.
- Share of heals on threatened allies at HP >= 700: us 0.095 +- 0.004 vs upper 0.169 +- 0.015; rest games us 0.118 vs them 0.204.
- Share of heals on safe allies below 300 HP: us 0.158 vs upper 0.063; rest games 0.134 vs 0.059.
- A healed threatened ally strikes within 2 turns 0.44-0.51; a healed safe ally 0.09-0.18 (both teams).
- Hit-threshold crossing per heal is the same (0.49 vs 0.49), so no threshold-aware healing.
- Heal target dies within 3 rounds: 0.060 vs 0.026 (upper), but 0.037 vs 0.078 in rest games: that one flips by tier.

Confound: How much a heal is worth depends on the target's role, so 'strikes within 2 turns' measures the target's situation, not the heal's effect.

HEAL_HOLD already limits who may heal. The subset is small: 8 games per upper bot.

## Context: HEAL_HOLD closed the action-choice tempo gap the last study found. The micro gap left is where robots stand while they are not ready, not what they do when ready. (strong)

Evidence: Census, 720 games:
- healThreat10: us 0.126 vs upper 0.204 vs rest 0.359
- readyHeld20: us 0.288 vs upper 0.309 (rest 0.328 vs 0.259)

With both cooldowns ready one step from an enemy at full HP, we step in 0.506 vs upper 0.477 (parity).

At 300-699 HP we step in 0.618 vs upper 0.370 and vs rest 0.517. This is the parked ENGAGE_HP line and is not re-proposed; the upper tier is split on it (jmerle 0.81, Strequals 0.56).

Confound: None for the descriptive shares.

## Operational: the driver's root disk is full (99 MB free of 30 GB). (strong)

Evidence: Output of df -h /. A copy of my 156 MB of results to the driver failed with 'No space left on device'; I deleted the partial copy and ran all aggregation on the VM. Earlier studies' scratch folders (gymstudy 308 MB, criteria 245 MB, lens3 191 MB, fights 166 MB, flaglens 154 MB) sit in the shared session scratchpad. I deleted none of them.

My scratch analyser and results, all on the VM and not committed:
- ~/cmscratch/src3/cm/Cm.java (v3, all 720 games) and ~/cmscratch/src5/cm/Cm.java (v5 adds heal candidates and next-turn readiness; 180-game subset)
- outputs ~/cmscratch/out3 and ~/cmscratch/out5
- aggregators agg3.py, base.py, peropp3.py, agg4.py, aggH.py, aggH2.py, corr.py
- driver copy: /tmp/claude-1000/-home-terryvanbelle-projects-vibe-2024/0c12d742-3a89-49d0-8e9e-654c983bb00e/scratchpad/cm/Cm.java

Confound: n/a

## Levers

- **C.KITE_REACH_W: leave enemy reach when you cannot strike this turn**: src/bot Micro.fight, kite/hold branch (goal == null), around Micro.java:231. Add:
  if (C.KITE_REACH_W > 0 && !actReady) score -= inRange * C.KITE_REACH_W;
inRange (enemies within dist2 4 of the tile) is already computed in the tile loop. Default 0 keeps g_iter7 byte-identical: a static final constant, so javac drops the term.

Arm: 300. That outweighs adjacency (at most 80), the crumb bonus (40), the 11-20 band (50) and the +1 for staying, but stays below one threat (1000). So a robot that has struck, or is recharging, steps to a tile out of reach (dist2 5-10) whenever one adds no threat. Applying it only when !actReady leaves the step-in strikes a weak-side robot can make through this branch untouched.

Dose ladder:
(a) 1000: also accept one extra threat to leave reach. Upper bots do that 62% of the time; we never do.
(b) exempt robots whose strike is ready next turn (rc.getActionCooldownTurns() < 20). The data favours applying it to them too: the upper tier kites them 92%.

Engage, advance, carrier and loose-flag branches, HEAL_HOLD and RETREAT_HP are unchanged. Not a closed line: retreat thresholds, ENGAGE_HP, stun-wary avoidance and territory micro all touch other terms. Signature: Needs ReplayDump --capabilities columns built on the step-census reconstruction plus wall/water/occupancy:
- reachEndStrike: strike turns (start in reach, both cooldowns ready, move ready) that end in reach.
- reachEndFree: the same, limited to turns where a free out-of-reach tile adds no threat.
- standHitsRech: enemy hits from attackers that began their turn in reach of our victim, whose action cooldown is >= 20, divided by all enemy hits.

g_iter7 baselines (720-game control):
- reachEndFree: 0.252 (upper games) / 0.283 (rest); the bots: upper 0.111, rest 0.178
- reachEndStrike: 0.384 / 0.432
- standHitsRech: 0.189 (upper) / 0.182 (rest)
- recharging-in-reach hold share with a free exit: 0.141
Expected: reachEndFree <= 0.12, reachEndStrike about 0.27-0.30, standHitsRech -10 to -15%.

Pre-registrable 5(a): g7kite (W 300) vs g_iter7 twins, one fixed seed. Setup is identical by construction: no fights before r201. 12 chosen cells (CLAUDE rule 4), sides alternating A/B:
- kuma on DefaultMedium and Puzzle
- andli28 on FloodGates
- Strequals on Fountain
- chenyx512 on Randy
- jmerle on QuestionableChess
- Gymhgy, CyrilSharma, NotLLeon, hsmalladi, andrewgopher and ColtG5, cycling the same maps

FIRES iff:
(a) setup identical in 12/12 and the arm's indicator counter 'kr' (kite tiles changed by the term) > 0 in 12/12
(b) reachEndFree pooled <= 0.12 and lower than the twin in >= 11/12 cells
(c) standHitsRech pooled <= 0.90x twins
(d) deaths per game pooled <= 0.97x twins, or kill share mean (arm - twin) >= +0.01 with >= 8/12 positive
(e) guards: enemyCaptured sum <= twins + 2; captured >= twins - 2; overruns 0; exceptions 0

Falsifier: (b) passes but (c) and (d) do not move, meaning enemies simply step in and hit. Close it rather than dose up.

Band delivery (DGTAG-free band block):
rel:reachEndFree<=0.5 rel:deaths<=0.97 nw:enemyCaptured<=1.1 mean:overruns<=0 Expected: About 300-400 affected decisions a game in each tier. Per affected decision: about -0.15 to -0.23 hits taken and -0.02 to -0.06 deaths (HP >= 300; -0.2 deaths below 300 HP), at the cost of -0.11 to -0.15 strikes within 2 turns.

Upper bound: about -25 deaths per upper-tier game (-3%) and -1 to -2% deaths in the rest. Kill share +0.005 to +0.01. A second-order tempo gain is possible: enemies must step in to hit, which exposes them to our ready holders.

Realistic win effect: +0.5 to +2 pp band-wide. P(delivers) about 0.5, P(ships) about 0.25.

Risks: strike-and-stay feeds follow-up kills in rest games (target dies 0.56 after a stay vs 0.45 after a kite, selection-confounded). A small, clean change; it stacks with HEAL_HOLD.
- **C.RC_BAND: close to one-step range while the strike recharges (RECHARGE_CLOSE, measured form)**: src/bot Micro.fight kite/hold branch (goal == null). Applies to a robot that cannot strike this turn but can next turn: !actReady && rc.getActionCooldownTurns() < 20 (cooldown read after this turn's actions). It also requires !hurt, HP >= C.RC_HP (700) and strong (nearAllies + 1 >= nearEnemies). For such a robot, score each tile:
  int thEff = (th >= 1 && inRange == 0) ? th - 1 : th;
  score = -thEff*1000 + adjAllies*10 + ((th == 1 && inRange == 0) ? C.RC_BAND : (minD >= 11 && minD <= 20 ? 50 : 0)) (+ the KITE_REACH term if stacked).
Arm RC_BAND = 60, so a tile one step from reach of exactly one enemy (dist2 5-10, out of everyone's reach) beats the 11-20 band (50), while two threats still cost 1000. Default 0 keeps g_iter7 identical.

Next turn the robot is ready at one-step range: it steps in and strikes, or strikes the enemy that stepped in. That gives a 2-turn strike cycle instead of today's strike, retreat, approach-or-heal, strike.

Dose ladder:
(a) R20-only: apply only when the nearest enemy starts beyond dist2 10. Every upper bot closes there, while at R10 the upper tier is split.
(b) RC_HP 500.

It was the open upper-tier-micro proposal #2 and was never tested. It is distinct from forward drift (which moves the army goal) and from zone holds. Signature: g_iter7 baselines:
- rcClose20 (ready next turn, nearest enemy at dist2 11-20, a free one-threat close tile; ends within dist2 10): us 0.021 / 0.024 vs upper 0.458, rest 0.412
- rcClose10 (same, starting at dist2 5-10): us 0.345 vs upper 0.496 / rest 0.617
- strikes within 2 turns per R20 ready-next decision: 0.220 (upper 0.251)
- strike turns per game (start in reach, both cooldowns ready): 1859 vs upper 3152
- step-in share of our hits: 0.685 (upper 0.513)
- heals/attack: 1.00
New census columns rcClose20, strikeTurns, stepHitShare (the analyser's definitions).

Pre-registrable 5(a): g7rc vs g_iter7 twins on the same 12 cells and seed as KITE_REACH.

FIRES iff:
(a) setup identical in 12/12; the arm's indicator counter 'rc' > 0 in 12/12
(b) rcClose20 pooled >= 0.25 and above the twin in 12/12
(c) strikeTurns per game >= 1.08x twins pooled
(d) kill share mean (arm - twin) >= +0.01 with >= 7/12 positive
(e) guards: enemyCaptured <= twins + 2; captured >= twins - 2; heals per game >= 0.85x twins (HEAL_HOLD blocks heals at dist2 <= 10, which is a level-sum risk); overruns 0; exceptions 0

Falsifier: (b) and (c) pass, but kill share <= 0 and deaths rise more than 3%. That means one-step range just feeds the enemy's stand hits; close it.

Band delivery:
rel:rcClose20>=5 rel:strikeTurns>=1.05 nw:enemyCaptured<=1.1 mean:overruns<=0 Expected: Large volume: about 1100-1600 decisions a game in the class, and we are the extreme outlier (0.02 vs 0.41-0.46, against 5/5 upper and 14/15 rest bots).

The payoff evidence is weak and ambiguous:
- within the upper tier, closers strike within 2 turns +0.31 for +0.10 hits taken
- class averages are close (0.277 vs 0.235)
- the R10 behaviour is split among upper bots

Best case: +2-3% strikes and the earlier strike in each exchange, so kill share +0.01 to +0.02. Worst case: more hits taken at one-step range and fewer heals.

P(delivers) about 0.35, P(ships) about 0.15-0.2. Test it after or stacked on KITE_REACH: the two together turn strike, kite-out, hold close, strike into the upper tier's 2-turn cycle.
- **C.HEAL_FRONT: heal threatened front-line allies before safe, badly hurt ones**: src/bot Micro.tryHeal (Micro.java:57-74). Replace 'lowest HP in range' with the lowest key, where:
  key = a.health - (C.HEAL_FRONT > 0 && enemyWithin(a.location, 10) ? C.HEAL_FRONT : 0)
enemyWithin uses Duck.enemies, the healer's sensed enemies.

Arm 300: a threatened ally at 750 HP (key 450) beats a safe ally at 500, but not one at 400. Dose 1000: threatened allies always first. Carrier heals (CARRIER_HEAL) and HEAL_HOLD are unchanged, and default 0 is g_iter7.

Two effects: safe, badly hurt robots (which retreat below RETREAT_HP 300 anyway) are healed later, and front-line robots are kept topped up above the kill thresholds. Not a closed line: T8 tested retreat thresholds, and HEAL_HOLD decides whether to heal, not whom. Signature: New census columns (the analyser's v5 definitions): healFront700 (heals on allies at HP >= 700 with an enemy within dist2 10 of the ally / all post-setup heals), healSafe300 (heals on allies below 300 HP with no enemy within dist2 10 / all heals), healThrMixed (threatened targets chosen when the candidate set holds both kinds), and epHealedFull (our damage episodes ending at full HP / episodes).

Baselines (g_iter7, 180-game subset; epHealedFull over 720 games):
- healFront700: 0.095 (upper games) / 0.118 (rest); upper bots 0.169, rest 0.204
- healSafe300: 0.158 / 0.134; the bots 0.063 / 0.059
- healThrMixed: 0.49 vs 0.58 / 0.64
- epHealedFull: 0.176 vs 0.316 (upper)

Pre-registrable 5(a): g7hf (300) vs g_iter7 twins on the same 12 cells and seed.

FIRES iff:
(a) the arm's indicator counter 'hf' (heals re-targeted) > 0 in 12/12
(b) healFront700 pooled >= 1.4x twins and healSafe300 <= 0.7x twins
(c) epHealedFull pooled >= +0.03 over twins
(d) deaths per game <= 0.98x twins, or kill share mean >= +0.008 with >= 7/12 positive
(e) guards: heals per game >= 0.95x twins; enemyCaptured <= twins + 2; captured >= twins - 2; overruns 0; exceptions 0

Falsifier: (b) passes but (c) does not, meaning front robots still die before the extra heals matter. Close it. Expected: About 400 heals a game re-targeted at arm 300, up to 700 at dose 1000. Heal XP is unchanged, so there is no level-sum cost. The policy gap holds in both tiers and we are not ahead in the rest, but the value per heal is unmeasured.

Healed threatened allies strike within 2 turns 0.44-0.51 vs 0.13-0.18 for safe ones; that is selection, not effect. Plausible: our robots' episodes healed to full +2 to +4 pp, deaths -1%, kill share +0.003 to +0.008, wins +0 to +1 pp.

P(delivers) about 0.35, P(ships alone) about 0.1: a stack candidate behind KITE_REACH. Risk: hurt robots that retreated return to the fight later and at lower HP.
