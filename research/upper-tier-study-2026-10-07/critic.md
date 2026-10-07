# Critic

- **C.KITE_REACH_W (g7kite): leave enemy reach when you cannot strike this turn**: keep=True, rank 1. Survives, and there is a cleaner test than the lens used. Ties in g_iter7's kite score are broken by coin flip, so in tied decisions about half the robots end in reach at random. Source: cmscratch/out3 R rows, side 0, strike turns with a free exit and kiteStay=2; n about 2.5k per HP bin in upper games and 10k in rest games. Ending in reach rather than out of it gives (deaths within 3 rounds, strikes within 2 turns):
- HP 300-699: deaths +0.052 +- 0.011 (upper) / +0.037 +- 0.005 (rest); strikes -0.013 +- 0.017 / +0.016 +- 0.012. Leaving is a clear win.
- HP < 300: deaths +0.21 +- 0.02 / +0.16 +- 0.01; strikes about 0. Leaving is a clear win.
- HP >= 700: strikes +0.074 +- 0.021 (upper) / +0.133 +- 0.011 (rest); deaths only +0.021 +- 0.005 / +0.006 +- 0.002.
The non-random forced-stay class shows the same pattern in every length bin.

Problem: as specified (all HP) it loses band-wide. At HP >= 700 in rest games, forced stays occur 183 times a game; removing them costs about 24 strikes (about 2.8 kills at our 0.114 kills per strike) to save about 1.1 deaths. Rest games are 75% of the band and must not lose. In upper games the same cut nets about +2 deaths saved (9 strikes, about 0.7 kills, for 2.6 deaths).
Fix: gate the term by HP: C.KITE_REACH_HP = 700, applied only when rc.getHealth() < 700, with hurt semantics untouched. Change the signature's denominator to HP < 700, hurt robots included (the lens used HP >= 300).

Where the gain is: about -20 to -28 deaths a game in upper games (-3%) and about -17 in rest games (-4.5%). Most of it is hurt robots. The hurt branch's -200 penalty for minD < 20 cannot be met from inside reach, so hurt robots stay in reach about 20% of the time. The || hurt clause is therefore the bulk of the lever; the lens's estimate of 1-2 deaths a game for HURT_EDGE undercounts it.

Caveats:
- d3 is a 3-round window, so some saved hurt robots may die later. Check 10-round survival in the Cm decision records before the 5(a).
- ENGAGE_HP cut band deaths 12% and still came out capture-neutral (t -1.12). The shipping rule is capture delta, so P(ships alone) is about 0.2.
- 5(a) power: killShare has a per-game SD of 0.11-0.13, so a 12-cell mean has SE about 0.03 against an expected +0.01. Bar (c) is a coin flip; use it as a guard (not below -0.03).
- Not a closed line: RETREAT_HP 500/700 removed engagement, and this lever does not. Bytecode is trivial.
- **C.RC_BAND (g7kiterc = KITE_REACH_W + RC_BAND): hold at one-step range while the strike recharges**: keep=True, rank 3. Survives only as a conditional third lever, and its tempo gain is overstated by about 2x.
- Tempo gap: in the lens's own class (out3 P rows: ready next turn, nearest enemy at dist2 11-20, a free close tile with at most one threat) our holders already strike within 2 turns 0.236-0.266 (local balance bins 2-3, upper games). Upper closers reach 0.41-0.49 and upper holders 0.09-0.13. The lens compared upper closers with upper holders (0.43 vs 0.11); the gap that matters for us is about +0.14-0.17 per decision.
- Exposure: upper closers took a hit before their next turn 0.085 when locally strong (balance bin 3), rising to 0.35-0.41 at balance <= 0. Those hits came from our robots, who hold a ready strike less (readyHeld20 0.29). Our closers will face more.
- Weak gate: nearAllies + 1 >= nearEnemies counts the whole vision and is almost always true. Gate on local balance instead (allies within 10 vs threats, bin >= 2).
- No unbiased evidence: our score never accepts a threatened tile, so our 2% closers come from the rush and chase branches and are not comparable. The only indirect support is the tie data under lever 1: at HP >= 700, staying close pays +7-13 pp strikes for 0.6-2.1 pp deaths. That is consistent with RC_HP 700 and with T18.
- Next turn the robot steps in and strikes, which is the T18 move; RC_HP 700 confines it to healthy robots.
- Dose too weak: RC_BAND 60 beats the 11-20 band bonus (50) plus the +1 for staying by only 9. One more adjacent ally (+10) or a crumb (+40) flips it, so it may barely fire and bar (b) >= 0.25 can fail by construction. Start at a dose of 100-150.
- HEAL_HOLD then blocks heals at dist2 5-10 (the heal400 guard covers this).
- Bar (c), killShare >= +0.01 with 7/12 cells positive, is a coin flip at 12 cells.
- It depends on lever 1, preferably HP-gated: lever 1 handles HP < 700 and this lever HP >= 700, a clean split by HP.
EV about +1 pp x 0.2.
- **C.STUN_MIN13 (g7sg): spend the scarce field-stun crumbs on big groups**: keep=True, rank 2. Survives, but the dose and the signature need correcting.

The yield gradient is in victims, and victims scale with how hot the fight is. Source: txscratch per-trap records, all 720 control games, our post-setup field stuns only (not within dist2 13 of a home, no carrier near at build). Victims per stun vs victims dying within 4 rounds per stun, for 0-2 / 3-4 / 5+ enemies near the tile:
- upper games: victims 3.37 / 4.41 / 6.80; victim deaths 0.31 / 0.57 / 0.74
- rest games: victims 2.69 / 3.92 / 6.25; victim deaths 0.42 / 0.84 / 1.15
In kill terms, 5+ beats 3-4 by only +29% (upper) and +37% (rest), against +54% in victims. Per victim, the 3-4 class converts best (0.130 vs 0.109 in upper games).

Dose: dose 5 skips 67% of field stuns (63 a game upper, 96 rest), including the 3-4 class, which triggers in a median 5-6 rounds, for a small premium. Dose 3 skips only the 0-2 class: 25.5 a game upper and 34.7 rest, median latency 62-75 rounds. It captures about 2/3 of the reallocation upper bound (+21% field-stun victim deaths in both tiers, vs +33-37% at dose 5) with far less waiting. Run dose 3, then 4; not 5.

Signature: pooled victims per trigger rises by composition once low-victim stuns are skipped, even if total victims fall, so bar (b) passes almost automatically. Make the signature kill-linked (victim deaths within 4 rounds per triggered field stun, as a new census column) and keep total stunVictims >= 1.05x as the real test.

Leak: ring rebuilds need only 200 crumbs (RING_POST_RESERVE 100) against 300 for field stuns, and there are already about 82 ring stuns a game in upper games. With the bank held at 300-700, rings never go without, so part of the saved crumbs goes to rings rather than big groups. Log ringStunsPost / fieldStunsPost.

Checked, not a refutation: overlapping our own recent stuns does not lower yield (overlapping stuns catch more victims).

The causal premium of a stun in a hot fight is unidentified, because enemies near a hot fight die at high base rates anyway: correlation only. The g4front lesson that stun counts are bound by crumbs applies.

Not ct1/ct2 and not g4front. Independent of the micro levers, so it can run in parallel. EV about +0.7 pp x 0.35.
- **C.HEAL_FRONT (g7hf): heal threatened front-line allies before safe, badly hurt ones**: keep=False, rank 4. Refuted as a lever.
- Reach is tiny. In the 180-game v5 subset (out5 G rows), heals with a mixed threatened/safe candidate set are 23.7% (upper) and 23.1% (rest) of our heals. We pick the threatened ally in 49% of them vs 58% (upper bots) and 64% (rest bots), so the policy gap touches 2-3% of heals.
- No outcome signal. After our heals, threatened targets at HP >= 700 and safe targets below 300 HP die within 5 rounds at similar rates: 0.082 vs 0.087 (upper), 0.045 vs 0.064 (rest). The strike gap the lens cites is selection.
- It does not separate winners: the rest bots, which we beat 86% of the time, show the trait more strongly than the upper tier (0.637 vs 0.580).
- Under HEAL_HOLD our healers have no enemy within dist2 10, which geometrically limits threatened targets in heal range.
- The next-round heal gap the position lens found partly comes from HEAL_HOLD itself.
EV about 0.1-0.2 pp, unmeasured. At most a free layer on a stack, not one of the 3 levers.

## Missing

1. The tie-break test belongs in the standard method. g_iter7's kite score breaks ties by coin flip (Micro.java score == bestScore && G.rand(2)). That gives randomized within-team estimates for any kite-branch lever, with no cross-team selection. The scratch script is /tmp/critic/r3.py on the VM; it reads ~/cmscratch/out3. It turned lever 1 from correlation into causal evidence (within the 3-round window) and showed the effect splits by HP. Use it before building any micro arm.

2. The capture channel is unestimated. The shipping rule needs a capture delta (t_all >= 2.3), but all three surviving levers move deaths or kill share. ENGAGE_HP cut band deaths 12% and still came out capture-neutral (t -1.12). None of the reports links -3% to -5% deaths to captures. Before the band test, estimate within opponent x map how capture difference responds to net deaths, or at least to deaths in the first 600 rounds.

3. Death windows are too short. The d3 window (and the lens's 3-round hazards) may count deferred deaths as saved, especially for hurt robots, which are the bulk of lever 1. Check 10-round survival in the Cm decision records (dec arrays; diedIn with +10) before registering the 5(a).

4. 5(a) power. killShare has a per-game SD of 0.108 (upper) and 0.127 (rest). Over 12 paired cells the mean has SE about 0.03, against expected effects of +0.01. The outcome-sign bars (c) in levers 1 and 2 are coin flips. Make them guards (not below -0.03), and let the signature plus a pooled guard on enemyCaptured or enemyFirstGrabs, over 24 or more games, carry the read.

5. The rest of the band's headroom is in r2000 games: win 0.64, capture difference +0.29, stun triggers 289 theirs vs 276 ours. Report lever deltas there separately in delivery, because that is where a band-wide lever can lose.

6. Checked and fine:
   - Symmetry basics: symWrong is 0 in all 720 games; symOk != 1 in 18 games (Gauntlet, Soccer), of which we won 15; 2 were decided late.
   - The census baselines in the summary reproduce: kill share 0.374 / 0.650, capture difference -0.73 / +1.75, victims per trigger 4.38 / 4.39 ours vs 5.05 / 5.41 theirs.
   - Deaths per upper-tier game are 764, not about 850, so lever 1's -25 deaths is about -3.3%.

7. The driver's root disk is confirmed at 38 MB free. I wrote only two small scripts into the scratchpad's critic/ folder, and my VM analysers sit in /tmp/critic/.
