# Synthesis

WHAT DECIDES THESE GAMES. The fight trade decides them, and every lens traces its own loss class back to it. Census2 (1,520 games, 449 won) has 1,071 losses: 410 level-sum (38% of losses, 27% of games), 343 more-flags and 318 capture-all. Our kill share kills/(kills+deaths) is 0.369 per game (505 kills vs 862 deaths). It stays the same in every phase (fights lens: 0.375 +- 0.009 in r200-400, 0.378 in r400-1000, 0.381 in r1000-2000). Wins run higher in every length bin: 0.515 vs 0.369 under r800, 0.411 vs 0.335 at r800-1999, 0.429 vs 0.364 at r2000. Within maps, win rate rises 2.55 per unit of kill share (linear probability, my calculation). That is correlation only and inflated by reverse causation, so I count about half of it as causal.

The fight feeds each loss class:
(1) Level sum. Three lenses each split the level gap into skills: exact XP accounting, the --levels samples, and a regression. The gap is heal XP (-27.6 of -35.3 levels in r200-800; -31.4 of -39.4 at r2000) plus attack XP lost to jail. We earn as much attack XP as andli28 but lose 41.5 vs 21.4 levels to jail by r800. Digs explain only ~13 levels (setup digs plus the end dump). The end level gap tracks the death gap (r -0.86, -0.13 levels per extra death; correlation). Heal XP needs robots that are damaged but alive. Andli28 spends 45.8% of its robot-rounds at 300-999 HP, we spend 32.3%, and heal coverage is at parity.
(2) Captures. Conversion per chain is equal in every window. We lose on how many chains we start, about half of its count after r600, because our presence at its flag homes falls from 11% to 4.5% of samples.
(3) Economy. Its kill rewards (14,330 vs 2,972 crumbs by r2000) buy about 290 of its ~590 stuns at its price of 50 crumbs per stun (ours 84-95).
At map level, win rate correlates with our paid-kill share (+0.66) and with (deaths-kills) per 1,000 rounds (-0.62). It does not correlate with andli's setup level (-0.06) or its r250 bank (+0.09). The fight is lost at one decision: two lenses with independent samples (240 replays; 8 traces plus code) find our step-in converts at half andli28's rate. Our step-in strike kills its target within 2 rounds 22.5% of the time vs 43.8%. Damage episodes end in a kill 10.2% vs 20.7% (r201-450) and 19.5% vs 37.8% (later). We step in at 300-699 HP on 39.8% of one-step-from-enemy decisions vs 15.1% for andli28, and that share is the same in our wins and losses (0.394 vs 0.400), so it is policy. 44% of our step-in strikes hit a ready enemy (andli28: 16%) and 21.6% hit an enemy at 450 HP or less (35.3%). The code shows why. Micro.fight engages whenever action-ready, HP >= RETREAT_HP 300, and 'strong' holds (/home/terryvanbelle/projects/vibe/2024/src/g_iter7/Micro.java:182,224). 'Strong' compares allies within dist2 20 against every visible enemy, and we usually see 7-11 allies to 3.5-4.5 enemies. The engage tile is then the fewest-threat reaching tile, whatever enemy it reaches.

WHERE THE LENSES AGREE.
- The fight is upstream of all three loss classes (all five lenses).
- The level gap is heal and jail XP, not digs (economy, traces, fights).
- Hit-to-kill conversion is half andli28's (fights, traces).
- The stun gap is volume (price 1.85x x crumbs 1.62x), not follow-up. Our follow-up lifts victim deaths 4.5x vs its 2x (fights, economy, traces).
- The flag state is frozen late: 2 of 63 level games at r1900 see a capture after it, and we win 17.8% of the 539 games tied at r1800 (economy, flags).
- Presence at its flags and chain starts are outcomes of the fight (flags, maps).

WHERE THEY DISAGREE.
(a) Timing. The fights lens finds the dam-drop skirmish even (k/d 9.0/10.5 at r250) and the death share flat by phase. The traces lens finds the r200-400 death gap predicts the result (win 0.43/0.29/0.11 by tercile). These fit together: the mean rate is constant, but game-level variance in a collapse window around r300-430 (andli28's bank-funded stun surge, 57 vs 15 stuns in r250-400) separates games. That separation is correlation and map-confounded.
(b) Flag placement. The maps lens finds flags displaced 6+ tiles captured +0.16 more often (z 2.7, in a sample selected on 1-2 captures). The flags lens finds andli28's conversion falls from 26% to 8% with our flag's distance from its spawns. The two point opposite ways on a closed knob (relocation dose), so I excluded FLAGS_IN_RESPAWN_REACH.
(c) Heal hold. It cut our heals by r400 (from ~1,300 to ~950), and heal XP is the level gap, yet no level-gap change shows (n=16/27). Do not touch it; a lever that keeps more robots alive at mid HP works with it.
(d) STUN_PRESS. Its own lens shows follow-up is not the gap, so I did not rank it.

RANKING by expected gain x probability. Level-sum deficits in census2: 28 of 410 within 10 levels, 33 within 12, 46 within 15, 66 within 20.
1. FINAL_COMPLETE: ~+2.2 pp x 0.65. Mechanical: it targets XP already stranded in partial levels (about 1.6 digs per level vs g7bank's 5), with no bank.
2. ENGAGE_HP: +2 to +4 pp x 0.4. The root fix with the largest ceiling. It touches all three classes, but the step vs hold death gap is selection-biased.
3. FOCUS (kill-first engage tile): +1.5 to +3 x 0.3.
4. LATE_ALLIN: +1.5 to +2.5 x 0.3. It interacts with #1, so test after #1.
5. RING_POST: +1 to +2 x 0.3, with a capture risk. The 'only under threat' form cannot fire because defend() runs only with no engageable enemy (Duck.java:77); use the reserve dose instead.

Not ranked:
- SYM_SCOUT: a cheap basics hole (Soccer undecided at r400 in 14/16 games, Gauntlet in 4/17) worth only ~0.2 pp; fix it under rule 15.
- LEAD_HOLD: close to the closed zone and recall lines.
- ESCORT_STUN and BUILD_ROUTE: small, and BUILD_ROUTE overlaps the closed builders family.

The highest-value open question is not a lever. Vertical-symmetry maps run 0.195 (575 games) vs 0.366 rotational (logit -0.87, z -2.9 with controls). The penalty is andli28-specific (none vs the band, Gymhgy or the other upper-tier bots), holds when symmetry is decided early, and has no identified mechanism. Trace 4 vertical-map losses in which andli28 kept <=5 robots in our half at r250 against 4 rotational wins of the same kind (--map-at 250/400/600, --flags), on the VM. Ceiling about +6 pp.

Files:
- /home/terryvanbelle/projects/vibe/2024/src/g_iter7/Micro.java (strike-first :176, strong :182, hurt :183, engage :224, advance :227)
- /home/terryvanbelle/projects/vibe/2024/src/g_iter7/Duck.java (turn hooks :46-78, farmDig :354, capturesLevel :372, defend ring gate :1100, placeCombatTrap gate :1212)
- /home/terryvanbelle/projects/vibe/2024/src/g_iter7/C.java
- /home/terryvanbelle/projects/vibe/2024/gauntlet/census-g_iter7-andli2.csv
- /home/terryvanbelle/projects/vibe/2024/research/level-dump-2026-10-06/README.md (the g7bank 5(a) format reused below)

## 1. C.FINAL_COMPLETE: finish partial build levels in the last 50 rounds of a level game (no bank)

Mechanism: From FC_ROUND=1950, while Duck.capturesLevel() is true:
(1) Field combat stuns (placeCombatTrap) and spendFloat pause. Carrier stuns and defence of an alerted flag stay.
(2) Any duck whose action is ready computes b = build XP and d = 5 - b%5.
(3) It is eligible if b < 30 (b < 15 if its attack or heal level is >= 4, since level 4 elsewhere caps build at 3) and d <= K(round): K = 1 from r1950, 2 from r1960, 3 from r1975, 4 from r1985. The last start must still finish: d digs x 2 rounds <= 2000 - round.
(4) It digs an adjacent legal tile using LEVEL_FARM's checkerboard and keep-off-flag rules, but without farmDig's ourHalf requirement.
(5) Crumb gate: digCost + FC_KEEP[d], with FC_KEEP = {0,0,20,60,100}, so robots one dig from a level get paid first from the shared bank.
(6) Two hooks. A new hook before Duck.java:46 (carrierStun/placeCombatTrap) runs in fights only when d = 1; the existing farmDig slot at :78 runs for all eligible robots.
(7) Optional stack, as a second dose rather than in the first arm: LEVEL_SPRINT. From r1950 a duck within 10 XP of its next heal or attack level picks that action over the other when both are legal.
The arm is byte-identical to g_iter7 before r1950 and in any game not level at r1950.

Evidence: Economy lens XP dump (n=135 r2000 games): at r1950 our robots hold 6.0 +- 0.3 robots one dig from the next build level (115 crumbs), 13.8 +- 0.5 within two digs (421 crumbs) and 23.7 +- 0.6 within three (~1,000 crumbs). 113 of our 251 build XP at r2000 (45%) is stranded in partial levels. Our r1950-2000 level gain is +2.7. The final-window budget is ~840 crumbs (606 spent in r1950-2000 plus a ~233 floor). g7bank's 5(a) warns that part of late spending is flag defence, so plan on 500-800. The flag state is frozen late: of 63 games level at r1900, each side captured afterwards in only 2. Census2 level-sum losses (my count): 410 of 1,520, median deficit 42; 28 within 10 levels, 33 within 12, 46 within 15. So +10 to +13 levels flips about 28-40 games, +1.8 to +2.6 pp. Andli28's end dump is fixed play and adds +12.5 build levels whatever we do, so the gain is additive. Differs from the closed lines: g7bank failed because its bank never formed (1 of 7 tied games) and it dug whole levels at 5 digs each (+7 levels, up to 53 digs). This lever needs no bank and spends ~1.6 digs per level. g7dig was setup digs; g4farm2 farmed level 1 from zero XP.

Signature: Census, r2000 games level at r1950:
- digsLate (digs after r1500) 0 -> 20-40 per game.
- levelGain1500 +10 or more vs twins.
- levelGapEnd in LEVEL_SUM games: median -42 -> about -30.
- LEVEL_SUM losses per 1,520: 410 -> about 370-380.
- Every other census column identical in games not level at r1950.
Scratch XP dump or --levels build histogram: stranded partial build XP at r2000 113 -> <= 70.
Indicator: new counter 'fc' (FINAL_COMPLETE digs).

5(a): Diagnostic game: g7fc vs andli28 on EndAround, fixed seed, side A. EndAround is the best map for this: 85% of its games end on levels, median deficit -10, 11 of 23 within 15. The mechanism shows firing if the flags are level at r1950, the 'fc' note appears in r1950-1960, the --metrics 50 rows up to r1950 equal the twin's, and overruns and exceptions are 0. If the game is not level, play side B.

5(a) cells: {EndAround, MazeRunner, GravitationalWaves, FloodGates, DefaultMedium, Battlecode24} x {A,B} x 2 pre-written seeds = 24 games. The maps' level-sum shares are 0.85/0.85/0.52/0.38/0.63/0.46. g_iter7 twins on the same cells.

Definitions: L = arm games level at r1950 (from --metrics 50). P = cells where both arm and twin are level at r1950.

FIRES iff:
(a) Identity: arm and twin metrics equal through r1900 in 24/24 cells.
(b) digsLate >= 15 in >= 80% of L; 0 in arm games not level at r1950.
(c) Mean over P of (levelGapEnd arm - twin) >= +10, and the arm's level gain r1950-2000 >= +10 (twins ~+3).
(d) Stranded build XP at r2000 <= 70 mean over L.
(e) Arm enemy captures after r1950 <= twins + 1 pooled; overruns 0 and exceptions 0 in every arm game.

Minimum n: if |P| < 8, add a third seed.
Falsifier: (b) passes but the level gain is < +6, which means the ordering or crumb gate is broken; fix it and re-diagnose.
Delivery afterwards: DGPOOL=andli28.v9_USQuals_angle BASE=g_iter7 delivery-gate with mean:digsLate>=15, rel:levelGain1500>=1.4, nw:enemyCaptured<=1.1 and mean:overruns<=0.

Risk: - Late spending that defends flags is exempt, so the budget may be ~500 rather than 840 crumbs (+8 to +10 levels, still ~+1.8 pp).
- 1.6-2.7 robots per band are jailed at r1950.
- Fight digs at d=1 cost a few strikes in the last 50 rounds; harmless while flags are frozen.
- If LATE_ALLIN ships later, its deaths cost levels (0.24-0.37 levels per death), so judge the two together.
- Gain is capped at ~+3 pp by the deficit distribution: 84% of level-sum losses are >20 levels behind.

## 2. C.ENGAGE_HP: no step-in below two enemy hits of HP (hold ready in the dist2 11-20 band instead)

Mechanism: In Micro.fight (src/g_iter7/Micro.java), add boolean fresh = rc.getHealth() >= C.ENGAGE_HP.
- Engage branch (:224): add && (fresh || killTile || carrierInReach). killTile = an enemy within dist2 4 of l with health <= rc.getAttackDamage(); compute it in the existing inRange loop.
- Advance branch (:227): add && fresh.
- A mid-HP robot that fails these falls to the existing kite/hold branch, which prefers minD 11-20: out of one-step reach, still ready to strike anything that steps in.
- Unchanged: strike-first (:176), RETREAT_HP 300, the carrier and loose-flag branches, and HEAL_HOLD. A held robot at dist2 11-20 is outside HOLD_R2 10, so it heals and is healed (coverage 26% there vs 17.6% inside dist2 10).
- Zero arm: ENGAGE_HP = 300, byte-identical because 'fresh' then equals !hurt.
- Arm: 600. Dose ladder: 500 and 700, or round-aware 450 before r600 and 650 after, since the ATTACK upgrade at r600 raises a hit to ~210-250.
Differs from closed T8 and never-retreat: it changes only who initiates; RETREAT_HP and retreating are untouched.

Evidence: Fights lens, 120-240 replays, scratch analyser:
- Ready robots at 300-699 HP one step from an enemy step in and strike 39.8% +- 0.3% of the time vs andli28 15.1% +- 0.2%. The rate is identical in our wins and losses (0.394 vs 0.400), so it is policy.
- Died within 3 rounds: 0.175 after a step-in vs 0.053 holding. Andli28: 0.064 vs 0.017.
- 36% of our deaths had >= 300 HP at r-1, vs 28% of andli28's.
- 44% of our step-in strikes hit a ready enemy, vs 16%.
Code: 'strong' is vision-wide (:182) and nearly always true (traces: we see 7-11 allies to 3.5-4.5 enemies), so any action-ready robot at 300+ HP with a reaching tile engages.
Outcome link (correlation only): within-map linear-probability slope of win on kill share is 2.55 per unit (census2, my calculation; per-game SD within map 0.058).
Level link: the level gap tracks the death gap at -0.13 levels per extra death, and heal XP needs damaged-alive robots (andli28 45.8% of robot-rounds at 300-999 HP vs our 32.3%). So this lever also works on the level-sum class.
Upper bound: -130 deaths a game (1,075 mid-HP step-ins x 0.122). Realistic: -40 to -80 deaths at -0 to -5% kills, kill share +0.015 to +0.03, about +2 to +4 pp if half the slope is causal.

Signature: Census:
- readyHeld20 0.280 -> >= 0.31.
- Kill share kills/(kills+deaths) 0.369 -> >= 0.385. This is the primary metric: it is phase-constant (0.375-0.381), so not length-confounded. A 1-line killShare column is worth adding.
- healThreat10 unchanged (about 0.127).
- homeDeathShare down (0.534); spawnDeath10 unchanged.
Survey: heals r400-1000 up.
Scratch Fx analyser (VM ~/fxscratch):
- Mid-HP step-and-strike share 0.40 -> <= 0.22.
- Our deaths with >= 300 HP at r-1: 36% -> <= 30%.
- Deaths within 3 rounds of a step-in: 0.139 -> <= 0.10.
Indicator: new counter 'eh' (engage refusals at mid HP).

5(a): Diagnostic game: g7ehp vs andli28 on DefaultMedium (fight-heavy, 96% reach r2000, win 0.41), fixed seed, side A. The mechanism shows firing if 'eh' > 0 by r300, setup (r<=200) is identical to the twin, and overruns are 0.

5(a) cells: {DefaultMedium, Randy, Puzzle, FloodGates, Fountain, QuestionableChess} x {A,B}, one pre-written seed. These are mid win-rate, high fight-volume maps of both lengths. g_iter7 twins.

FIRES iff:
(a) 'eh' > 0 in 12/12 arm games.
(b) Fx mid-HP step-and-strike share pooled <= 0.25 (twins ~0.40), and lower than the twin on >= 10/12 cells.
(c) Kill share: mean over cells of (arm - twin) >= +0.02, with >= 8/12 cells positive. If 0 < mean < +0.02, add the same 12 cells on a second seed and judge on 24 cells.
(d) readyHeld20: mean (arm - twin) >= +0.02.
(e) Guards: sum of enemyCaptured <= twins + 2, sum of captured >= twins - 2, overruns 0 and exceptions 0.
Logged without a bar: wins, kills, deaths, healThreat10, levelGapEnd, homeDeathShare, survey heal400.
Delivery afterwards: andli28 pool, rel:readyHeld20>=1.08, kill share +0.015, nw:enemyCaptured<=1.1, mean:overruns<=0. No kills guard (PROMPTS 183).
Dose ladder 500/700 if (b) passes but (c) misses.

Risk: - Step vs hold death rates are a selection comparison, so the gain may be much smaller than the upper bound. Andli28's own step/hold ratio (3.8x) resembles ours (3.3x), which is consistent with exposure alone.
- Each blocked step-in is a lost strike (~8% of our strikes at full dose). If held robots do not recover them, the kill share barely moves.
- Holding may cede ground and lengthen andli28's presence in our half (paid-kill share 0.54).
- Micro changes have a mixed record here (MICRO_V2 1-25; e1aggr/e2aggr flat), though none tested an HP-gated engage.
- Probability the mechanism delivers a net kill-share gain: about 0.4.

## 3. C.FOCUS_STEP: pick the engage tile by its target (kill first, then lowest HP), not only by fewest threats

Mechanism: In Micro.fight's engage branch (:224-226), compute lowHP(l) = minimum health among enemies within dist2 4 of l (in the existing inRange loop), then:
score = 10000 - th*100 + adjAllies*10 + (1000 - lowHP)/C.FOCUS_W + (lowHP <= rc.getAttackDamage() ? C.KILL_BONUS : 0)
with FOCUS_W = 10 (at most +100, one threat) and KILL_BONUS = 150 (1.5 threats).
Strike-first (:176): when no enemy in reach is killable, one within dist2 8 is (health <= rc.getAttackDamage()), and movement is ready, skip the immediate strike, step to the reaching tile with the fewest threats, then strike it. bestTarget already takes lowest HP in reach.
Zero arm: FOCUS_STEP false, byte-identical.
Stack it on ENGAGE_HP; ENGAGE_HP's killTile exception is the same kill test, so it reuses that code.

Evidence: Traces lens (8 games, ~97k hits): damage episodes end in a kill 10.2% vs 20.7% (r201-450) and 19.5% vs 37.8% (r451+). Lower in 7 of 8 games early and 8 of 8 later. Hits on targets at <= 450 HP: 21.6% vs 35.3% (r201-450), 19.0% vs 26.8% later. 37% of our episodes are a single hit vs 30%. Andli28 heals 1.10-1.43 per one of our attacks.
Fights lens (240 replays, independent sample): our step-in strike kills its target within 2 rounds 22.5% vs 43.8%. Strikes on stunned targets are 8.5% of our step-ins vs 15.4%, and those kill 62%.
Code: the engage score ignores which enemy the tile reaches.
Level link: wasted hits become andli28 heal XP, and heal levels are ~77% of the level-sum deficit.
Outcome link (correlation): r200-400 death-gap terciles win 0.43/0.29/0.11.

Signature: New ReplayDump columns (2-column addition, from per-robot HP in the replay):
- hitLowShare r201-450: 0.22 -> >= 0.28.
- episodeKillRate r201-450: 0.10 -> >= 0.14; r451+: 0.195 -> >= 0.24.
- Attacks per kill r201-450: ~29 -> <= 22.
Census:
- Kill share up >= +0.015.
- kills250 - deaths250 up.
Metrics: andli28 heals per 100 rounds in r400-1000 down (642 now).
Indicator: counter 'fk' (focus or kill-step turns).

5(a): Diagnostic game: g7focus vs andli28 on DefaultMedium, the same seed and side as the ENGAGE_HP diagnostic so twins are shared. The mechanism shows firing if 'fk' > 0 by r300, the kill-step fires at least once (event dump --from 200 --to 400), and overruns are 0.

5(a): the same 12 cells and seed as ENGAGE_HP, g_iter7 twins.

FIRES iff:
(a) 'fk' > 0 in 12/12 games.
(b) hitLowShare r201-450 pooled >= 0.28 (twins ~0.22), and higher than the twin on >= 9/12 cells.
(c) episodeKillRate r201-450 pooled >= 0.14 (twins ~0.10).
(d) Kill share: mean (arm - twin) >= +0.015, with >= 8/12 cells positive.
(e) Guards: captures +-2 as for ENGAGE_HP; overruns 0 and exceptions 0.
If stacked on a delivered ENGAGE_HP, the twins are the ENGAGE_HP arm.

Risk: - Wounded andli28 robots fall back into their healers' cover, so focus tiles may carry more threats. The bonus is capped at one threat, but deaths may still rise.
- MICRO_V2, a full rewrite that included a kill bonus, lost 1-25. It is weak evidence against this term, which is small.
- Hitting stunned targets would need STUN_PRESS's trap ring; it is left out.
- Probability about 0.3.

## 4. C.LATE_ALLIN: score-aware endgame assault when tied (or behind) on flags from r1800

Mechanism: Fires when round >= ALLIN_ROUND (1800) and our captures (Comms.EF_STATE slots == 2) <= its captures (bitCount(Comms.lostMask())).
When it fires:
(1) Non-defender ducks ignore alerts and take one shared target slot: the live enemy flag home nearest our spawn centroid, written once per round by the first duck.
(2) A dropped enemy flag within dist2 100 becomes the movement goal (REGRAB-style, only in this window; CAPTURING makes drops stay 25 rounds).
(3) Within dist2 64 of the target, ADVANCE_MARGIN drops to 1.
(4) Defenders, the chase of an enemy carrier within CHASE_RADIUS2, carrier stuns and our own carry logic are unchanged.
(5) Once FINAL_COMPLETE ships: the all-in ends at r1950 in tied games so the level completion runs, or it fires only when behind or when our robots' estimated level deficit is >= 20. The estimate is the mean level of sighted enemies x 50 minus our level sum, accumulated over r1750-1800 in two shared slots.
Identical to g_iter7 before r1800 and in games ahead at r1800.

Evidence: Flags lens (all 1,520 replays): 539 games (35%) are tied at r1800 and we win 17.8% of them (358 level-sum losses). 283 games are behind by one and we win 2.5%.
In tied games over r1801-2000:
- Our chains run 0.23 per 100 rounds (g0 >= 8: 0.049) at 19.8% conversion; andli28's run 0.31.
- Late conversion is the best of the game: 38% at g0 >= 8.
- Re-grab after a drop in the CAPTURING window: 0.58 for us vs 0.71 for andli28.
Conversion by grab-group size is nearly the same curve for both bots (ours 4.6% at g0 0-1, 49% at 16+). So more and bigger late chains are what converts.
A capture by andli28 in a tied game only turns a likely level loss into a flag loss. The downside is the 46 tied games we now win on level or bread, plus the levels the assault's deaths cost.
The lens estimates +0.08 to +0.10 captures per tied game, +5 to +7 pp among tied games, about +2 pp overall (at most ~3).
Not closed: it is score- and time-gated. g7bank was about levels, CAPTURING-first moved the upgrade, and forward drift and never-retreat were all-game.

Signature: Census (capturedLate and regrabsLate count after r1200):
- capturedLate and regrabsLate up in r2000-length games tied at r1800.
- firstGrabs up in the same games.
- enemyCaptured in those games may rise by up to ~0.1.
- Games ahead at r1800 identical to the twin.
--flags on tied games:
- Our chains r1801-2000: 0.23 -> >= 0.45 per 100 rounds.
- g0 >= 8 chains: 0.049 -> >= 0.10.
- Re-grab in the CAPTURING window: 0.58 -> >= 0.68.
- P(we capture after r1800 | tied): 0.104 -> >= 0.18.
Indicator: note 'allin'.

5(a): Diagnostic game: g7allin vs andli28 on DefaultMedium, fixed seed. The mechanism shows firing if the game is tied at r1800 and 'allin' notes appear on turns after r1800 only, and --flags shows our grab group at the target flag >= 8 at least once. If the game is not tied, try side B, then GravitationalWaves.

5(a) cells: {EndAround, DefaultMedium, GravitationalWaves, Klein, Battlecode24, Swoop} x {A,B} x 2 pre-written seeds (24). These are high tied-at-r2000 shares with captures still possible. g_iter7 twins.

Definitions: T = arm games tied or behind at r1800.

FIRES iff:
(a) Identity through r1800 in 24/24 cells, and 'allin' present in >= 90% of T and absent elsewhere.
(b) Our first grabs in r1801-2000 pooled over T >= 2.0x the twins'; grabs with g0 >= 8 >= 2.0x.
(c) P(we capture after r1800) over T >= 0.25 (twins ~0.10).
(d) Wins in T >= twins' wins in T + 2, judged together with FINAL_COMPLETE if it has shipped; overruns 0 and exceptions 0.
Minimum n: |T| >= 10, else add a third seed.
No enemy-capture guard inside T, by design.

Risk: - Every earlier offence line failed to deliver presence at andli28's flags (convoy dive, zone holds, forward drift). Its flags sit on its spawn centres, and fresh spawns deal 37.5% of the hits on our carriers.
- The all-in costs levels through deaths and through actions taken from FINAL_COMPLETE.
- If andli28 also captures, a 1-1 tie returns to a level loss.
- Probability about 0.3.

## 5. C.RING_POST_RESERVE: after setup, ring rebuilds yield crumbs to field combat stuns

Mechanism: In Duck.defend() (src/g_iter7/Duck.java:1100), the ring build's crumb reserve becomes:
(G.round > C.SETUP_ROUNDS ? C.RING_POST_RESERVE : C.DEF_TRAP_RESERVE)
- Default RING_POST_RESERVE = 100, byte-identical to g_iter7.
- Arm 300: a ring stun needs >= 400 crumbs, an explosive >= 500, above placeCombatTrap's 300 gate (:1212). Field stuns then get first claim on the bank.
- Dose 1000: effectively no post-setup rings.
- Setup rings are unchanged; that is DAM_FIRST's job, a separate stack partner.
The traces lens's 'rebuild only with an enemy within dist2 20' form would almost never fire: defend() runs only when no engageable enemy is visible (Duck.java:47 vs :77). Use the reserve form.

Evidence: Traces lens (8 games; --trapgeo joined to events):
- 862 of our 1,631 post-setup stuns (53%, 108 a game) are ring rebuilds within dist2 13 of our flag homes. Andli28: 2%.
- Ring stuns catch 2.27 victims after a median 47-158 rounds (31.6 crumbs per victim).
- Field stuns catch 5.45 after 2-48 rounds (18.1 crumbs per victim).
- Rings take about 7,800 crumbs a game, ~45% of post-setup stun spending.
- The bank sits in [200,300) in 63% of 10-round samples after r300, so the defenders (idx 0-2, who act first each round and build at >= 200) pre-empt field stuns (>= 300).
Census2: fast-victim share 0.41 for us (375/917) vs 0.54 for andli28 (1,247/2,320). Andli28 triggers 2,320 victims a game to our 917.
Economy lens: the stun gap is price x crumbs, and our follow-up already matches andli28's (victim death lift 4.5x vs 2x), so more victims convert to kills at ~15% each.
Projection: +150 to +250 frozen enemies a game (+20-30%), about +25 to +40 kills.

Signature: Census:
- stunVictims up >= 15%.
- stunVictimsFast/stunVictims 0.41 -> >= 0.47.
- trapsBuilt about unchanged or up (field stuns cost more per stun).
- enemyCaptured and enemyFirstGrabs not up more than 10%.
- captured600 and enemyCaptured600 unchanged.
--trapgeo:
- Post-setup stuns within dist2 13 of our homes: ~108 -> <= 45 a game.
- Field stuns: ~96 -> >= 140.
- Median trigger latency of all our post-setup stuns down.
Indicator: defTraps after r200.

5(a): Diagnostic game: g7ring vs andli28 on Puzzle, fixed seed, side A. The mechanism shows firing if the post-setup ring builds (--trapgeo, within dist2 13 of our homes) are at most half the twin's by r800 and at least one extra field stun is built in r250-400. Overruns 0.

5(a) cells: {Puzzle, Fountain, QuestionableChess, Randy, DefaultMedium, Rivers} x {A,B}, one seed. These are fight maps where andli28 reaches our flags (enemyCaptured 0.4-2.0). g_iter7 twins.

FIRES iff:
(a) Post-setup ring stuns pooled <= 0.4x the twins'.
(b) Post-setup field stuns pooled >= 1.4x, and stunVictims arm/twin >= 1.15 pooled.
(c) stunVictimsFast/stunVictims >= 0.47 pooled (twins ~0.41).
(d) Guards: sum of enemyCaptured <= twins + 2; enemyFirstGrabs <= 1.1x twins; overruns 0 and exceptions 0.
Logged: kill share, kills250, deaths250, levelGapEnd (defenders lose cheap build XP).

Risk: - Ring stuns catch raiders at the moment of a grab, so their 2.27 victims may each be worth more than a field victim. Andli28's captures are long, unchased trips that the rings may slow.
- Field stuns cost ~99 crumbs vs ~72 for the build-6 defenders, so the gain per crumb is ~1.75x, not 2.4x.
- The extra field-stun yield is a selection effect (field stuns are built only with >= 3 enemies in vision).
- The Cyril stun-economy arms all came out neutral.
- Probability about 0.3. It also frees late crumbs that FINAL_COMPLETE can spend.

