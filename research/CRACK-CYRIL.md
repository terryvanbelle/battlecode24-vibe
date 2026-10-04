# CRACK-CYRIL.md: the next ladder target, CyrilSharma.finalBot

Waffle ranks below g_iter3 (1977 vs 1964, 2026-10-03), so per the owner's standing order (PROMPTS 158-159) the target
moves to the bot just above: CyrilSharma.finalBot, 2020 (rank 11). It is also the bot above us we do best against:
g_iter3 10-14 on the band seeds (g_iter2 7-17); hsmalladi, andrewgopher and andli28 3-21.

Evidence (48 replays: g_iter2 and g_iter3 band and confirmation games; source never read):

- **Its offense, like waffle's:** fast raids (first grab r217-342), 6-58 carrier trips per loss, first capture r245-563;
  its capturing carriers have 0.1-2.9 of our ducks near them. It re-grabs 75% of its drops of our flags in our losses,
  72% in our wins: the relay matters less than against waffle (87% / 77%) and does not separate outcomes.
- **Distance gradient** (our 144 flags by r200 distance to its nearest flag): < 20 tiles 82% captured (median r327),
  20-28 72% (r494), 28-36 58% (r615), 36+ 39% (r619). g_iter3's relocation is already pointed the right way.
- **Its defense beats our offense:** our trips fail (Asteroids 11 trips 0 captures, Decision 15 / 1, Puzzle 19 / 1);
  it keeps 6+ ducks near its flags at r300 (survey: "guards flags"). Its defenders near its flag at our first grab
  average ~2.3 in our wins and losses alike.
- **Economy (survey):** digs in setup, banks ~3900 crumbs at r250 (ours ~180), stun-heavy, heals more than it attacks.

First candidate: **g2crb** (g_iter3 + C.BUDGET_V1: bank through setup so carrier and combat stuns are funded when the
raid lands; waffle showed our stuns starved at ~190 crumbs). 5(a) queued; then delivery on the Cyril pool.

**g2crb 5(a)** (8 mirror maps vs g2cr's): bank at r200 3860-13570 crumbs (g2cr 155-190 on 6 of 8 maps), no setup traps,
the bank spent by r250 (210-660 left), stuns by r400 50-196 (g2cr 30-192); carriers caught higher on 4 maps, lower on
2. Pre-registered delivery on the Cyril pool, bars on the mechanism's own signature (TRAINING_ALGORITHM §3.5; the
downstream effect is for the paired filler and band tests, the lesson of g2cstun's capture bars):
`DGPOOL=CyrilSharma.finalBot DGTAG=-cyril BASE=g_iter3 tools/delivery-gate.sh g2crb 'fire:crumbs200>2000>=0.9
rel:stun400>=1.3 rel:carrierStunned>=1.2 nw:kills>=0.95 mean:overruns<=0'`.

**g2crb Cyril delivery: FAIL** (bank at r200 in 100% of games ok; stun400 +12%, bar +30%; carriers caught 12.6 vs 13.5,
bar +20%). Without setup traps the bank only replaces g_iter3's setup stuns. Descriptive: wins 9 vs 12 (gained 4, lost
7), our captures 0.83 vs 1.21, traps hit 174 vs 203: banking hurts here. Parked. Base note: g_iter3 went 12-12 vs Cyril
on these 24 cells. The filler now builds the g_iter3 vs Cyril baseline on fresh seeds.

**g_iter3 vs Cyril (48 replays, 22-26):** the losses look like waffle's fast losses: Cyril grabs at r218-331 and
captures first by ~r277-440, mostly on small maps (AceOfSpades, Asteroids, HungerGames, Randy, Checkered, StackGame)
where relocation cannot add distance and carrier stuns cannot slow a short chain enough. Next arm: **g3def2** (g_iter3
with 2 defenders per flag, RETEST arm 9; Sym.SCOUT_FIRST 6 so the scouts are not defenders). 5(a) on 8 mirror maps
including two small ones (defend300 must rise).
g3def2 5(a): all six defenders defend at r150 and r300 (fires); defend300 4-18 (noisy: carried flags are not
counted); it lost all 8 mirror games to g_iter3 (no weight at 5(a), a warning that three fewer field ducks cost).
Cyril delivery (RETEST arm 9 bars, kills guard SE-aware): `DGPOOL=CyrilSharma.finalBot DGTAG=-cyril BASE=g_iter3
tools/delivery-gate.sh g3def2 'rel:defend300>=1.25 rel:enemyFirstGrabs<=0.85 nw:kills>=0.95 mean:overruns<=0'`.

**g3def2 Cyril delivery: FAIL** (defend300 8.1 vs 6.5 ok; enemyFirstGrabs 7.42 vs 7.88, -6%, bar -15%). Descriptive:
wins 8 vs 12 (gained 3, lost 7), our captures 0.62 vs 1.21. Closed (as T1's 2-defender test, 9-25).

**Our offense vs Cyril:** wins 11.4 pickups -> 1.50 captures (13%), losses 9.4 -> 0.65 (7%); our ducks in its half at
r300 18.3 vs 9.5. When our carrier dies (74 drops, 16 games) we re-grab 19%: at the drop ours within dist2 2 / 8 average
0.26 / 1.14, Cyril's 1.64 / 3.53 (97% of drops have one of its ducks within dist2 8). Our carriers travel alone.
Next: **g3escrg** (convoy: ESCORT_CARRIER + ESCORT_BEHIND + REGRAB on g_iter3; T13 adoption).
g3escrg 5(a) (4 mirror maps): escort turns 407-1386, re-grab tries 11-41, regrabs 10-38 a game, 0 overruns: fires.
Cyril delivery: `DGPOOL=CyrilSharma.finalBot DGTAG=-cyril BASE=g_iter3 tools/delivery-gate.sh g3escrg 'rel:regrabs>=2.0
rel:escorts20>=1.1 nw:kills>=0.95 mean:overruns<=0'`.

**Baseline (filler, fresh seeds):** g_iter3 vs Cyril 42-102 over all games (29%); the 12-12 delivery-base block was a
lucky draw. Ladder: Cyril 2040, g_iter3 1959. Still the best target above us (hsmalladi, andrewgopher, andli28 3-21,
NotLLeon 4-20, Gymhgy 8-16 on the band seeds).

**g3escrg Cyril delivery: FAIL** (regrabs 8.67 vs 1.54 ok; escorts20 3.06 vs 3.71, bar x1.1). Descriptive: wins 13 vs
12 (gained 6, lost 5), our captures 1.50 vs 1.21, pickups 20.4 vs 11.0, carrier deaths 15.8 vs 6.7. Trace: the escort
branch hands control to the fight micro, where the engage score (10000) beats the goal pull, so escorts stop to fight
and the carrier walks on alone (the same failure as g2escrg on the band). Fix: C.ESCORT_TIGHT (escorts' moves stay
within dist2 8 of our carrier, or close in, while they fight). Second attempt g3escrg2: 5(a) queued, then the Cyril
block on fresh seed 919191 with the same bars.
g3escrg2 5(a): ESCORT_TIGHT alone did not raise escorts20 (3.80 vs 4.04 on the same 4 maps); a wider ESCORT_R2 cannot
matter (the carrier must be in vision, dist2 20). Added C.ESCORT_FAR_R2 225 (out of a fight, join our carrier from the
registry, EF_STATE 1 / EF_LOC): escorts20 4.35-8.65 (mean 6.1 vs 4.0), regrabs 7-57, 0 overruns. Second attempt:
`SEED=919191 DGPOOL=CyrilSharma.finalBot DGTAG=-cyril2 BASE=g_iter3 tools/delivery-gate.sh g3escrg2 'rel:regrabs>=2.0
rel:escorts20>=1.1 nw:kills>=0.95 mean:overruns<=0'`.

**g3escrg2 Cyril delivery (second attempt, seed 919191, 21 cells): PASS** (regrabs 11.2 vs 3.3; escorts20 5.34 vs 3.10;
kills 541 vs 543). Descriptive: wins 7 vs 9 (gained 1, lost 3), our captures 0.95 vs 0.86, pickups 20.0 vs 12.7,
carrier deaths 15.8 vs 8.8. Now: the Cyril filler pairs it with g_iter3 toward 240 games; band guard + band test queued.

**g3escrg2 band test vs g_iter3** (234 seeded pairs; control = g2cr's band runs, the same code): net +6 (20-14, p 0.39),
capture delta +0.18 +- 0.08 (t 2.1), upper +0.20 +- 0.12 (t 1.7): neither REWRITE_EVAL criterion. **Basics FAIL:
kill/death 2.13 vs 3.11 (-0.97 +- 0.35)**: the convoy trades bodies for flag progress (carrier deaths roughly double),
which the k/d check reads as a combat loss. Not promotable; the Cyril filler continues to learn whether it cracks
Cyril, and any conflict with the k/d check goes to the owner, not around the rule.

**g3escrg2 paired filler vs Cyril, closed at 240 games:** gained 38, lost 30, net +8 (+1.0 SE); g3escrg2 78-162 (32%)
vs the g_iter3 baseline 198-466 (30%). The convoy doubles pickups and re-grabs but also carrier deaths: more attempts,
not more captures. Next: measure Cyril's flag defence before building (who kills our carriers and where; its traps
around its flags; guards per flag over time).

## Defence study (workflow: 5 measurement lenses over 630-750 g_iter3/g2cr replays vs Cyril, synthesis, critic)

Robust after the critic (overlapping analyses of the same game pool; event counts are clustered within games):
- **Our carriers die at once next to its flag** (median 5 rounds, 2 moves, 4 tiles from its home; 90% of trips in losses
  end with a dead carrier; attacks, not traps: 95% of deaths have hits, only 17% follow a Cyril stun). Its flags never
  move (all on spawn centres) and ~80% of its respawns go to the threatened flag's zone. But this kill box is
  **symmetric**: its carriers die just as fast at our flags (median 3 rounds, 1 move). Not its edge.
- **No ring of traps, no extra guards, no hard geometry** at its flags (83-89% of our first grabs have no live Cyril trap
  within 4 tiles; defend300 6.56 vs our 6.76; capture rate equal for all three of its flags).
- **Its offense decides the games:** no Cyril capture -> we win 84% (119-23); any -> 18% (107-501). Its chains start
  more often (0.67 vs 0.38 per 100 post-setup rounds) and capture twice as often (24% vs 12%); 70% of its captures are
  re-grab legs; capturing trips carry 7.2 of its escorts within dist2 20 vs our 3.8.
- **Long unwatched carries:** its carrier-rounds with none of ours within dist2 20: 159 per loss vs 87 per win
  (within-map AUC 0.19); unopposed captures 1.00 vs 0.38 per game; its capturing trips last ~71 rounds with ~1 of ours
  within dist2 20.
- **Stun grenades:** 96% of its stun builds trigger, each freezing ~5.8 of our ducks (equal in wins and losses).
- **Earliest within-map separator:** our deaths in our own half r250-400, 16.3 (wins) vs 24.4 (losses), kills equal.
- One extra capture would turn 126 of 498 losses into flag ties (we win ties ~63% on level sum).

Not supported after the critic: the jail-timing gate (predicts only an escape proxy, not captures); the respawn-stream
dose-response as stated (inflated by respawns after the carrier was already dead; needs a recompute with respawns
counted only while the carrier lives). Already tried: INTERCEPT + DEST_CAMP (g2icamp, band delivery FAIL x2),
CAPTURING second (g2up3 FAIL).

**Next arm: g3camp** (g_iter3 + C.DEST_CAMP alone, A11(a) fixed). Trace of g2icamp's failure: on the band and in mirrors
carriers are rarely seen and then lost, so camps never fired legitimately (0 in 8 mirror games after the fix), while
INTERCEPT pulled fighters off and raised first grabs against us (+26%). Against Cyril the premise is specific: its
carrier is seen at the grab (our defenders are there) and then walks ~71 rounds unwatched toward a spawn our ducks can
reach first (1 tile a round vs its 0.5). INTERCEPT stays off.
g3camp 5(a) (6 scrimmages vs Cyril, random maps/sides): camps fire (174-3711 camp turns a game) but 0-6, Cyril's carriers
still walk unwatched (85-679 rounds a game) and capture unopposed up to 3 times (Joker). Trace (Joker r815): four pairs of
our campers wait 7-9 tiles out around Cyril's top-right spawn while its carrier is beside our flag on the far left and
captures 6 rounds later: campers go to a spawn, not to where the carrier goes. Delivery block (signature bars):
`DGPOOL=CyrilSharma.finalBot DGTAG=-cyril BASE=g_iter3 tools/delivery-gate.sh g3camp 'rel:enemyUnseenRounds<=0.8
rel:unopposedCaps<=0.8 nw:kills>=0.95 mean:overruns<=0'`.

**g3camp Cyril delivery: FAIL** (enemyUnseenRounds 114 vs 135, -15%, bar -20%; unopposedCaps 0.39 vs 0.78, ok; kills ok).
Descriptive: Cyril's captures 1.33 vs 1.62 (-18%), wins 12-12 (4-4). Trace: on Joker the carrier of our flag at (1,2)
captured at Cyril's spawn (33,19), 36.2 tiles, not the straight-line nearest (36,8), 35.5; our campers went to the
nearest. Fix C.CAMP_SPLIT: campers split by id over every enemy spawn within 1.3x the nearest distance (AuditTest).
Second attempt g3camp2: 5(a) (6 scrimmages) then `SEED=919191 DGPOOL=CyrilSharma.finalBot DGTAG=-cyril2 BASE=g_iter3
tools/delivery-gate.sh g3camp2 'rel:enemyUnseenRounds<=0.8 rel:unopposedCaps<=0.8 nw:kills>=0.95 mean:overruns<=0'`.
**g3camp2 second attempt: FAIL** (seed 919191, 21 cells: unseen 114 vs 134, -15%, bar -20%; unopposedCaps 0.57 vs 0.62,
bar -20%; kills ok). Descriptive: Cyril's captures 1.57 vs 1.57, wins 8 vs 9. The camp line is closed (two delivery
failures).

**Plateau against Cyril** (five lines near neutral: bank, two defenders, convoy, camp, camp-split). Per AUDIT_PLAYBOOK.md
§1 the next step is a correctness audit of g_iter3 and the tools (much is new since the 2026-10-02 audit: carrier stun,
relocation V2, A11/A12 fixes, new census columns, nw: guards), with the open findings of the first audit in scope.

**Correction (second audit, research/AUDIT-2026-10-03.md, MEAS1).** Three of the five Cyril closures failed inside 1 SE
(g3def2 enemyFirstGrabs -0.65 SE, g3camp enemyUnseenRounds -0.34 SE, g3camp2 -0.23 / -0.32 SE): they are INCONCLUSIVE,
not neutral, and the plateau is not established. g2crb never delivered its premise (BOT11: the bank is gone by r205).

**Audit fixes as arms (second audit, BOT1 / BOT2).**
- g3lost (C.FLAG_LOST): 5(a) vs Cyril fires (HungerGames: flag 1 captured r327, last seen r315, lost bit set at r376,
  no alerts for it afterwards). Delivery: `DGPOOL=CyrilSharma.finalBot DGTAG=-cyril BASE=g_iter3 tools/delivery-gate.sh
  g3lost 'rel:capturedHomeRounds<=0.5 nw:kills>=0.95 mean:overruns<=0'` (new census column: our robot-rounds within
  dist2 8 of a captured own flag's home).
- g3tether (C.DEF_TETHER): 5(a) vs Cyril fires (123-311 tether turns a game). Delivery: `DGPOOL=CyrilSharma.finalBot
  DGTAG=-cyril BASE=g_iter3 tools/delivery-gate.sh g3tether 'rel:defNearAtGrab20>=1.15 nw:kills>=0.95 mean:overruns<=0'`
  (new column: our robots within dist2 20 of our flag at enemy first grabs).
Verdicts are three-way now (PASS 1 SE beyond the bar; INCONCLUSIVE extends the block to 48 / 96 cells).
- **g3lost Cyril delivery: PASS** (capturedHomeRounds 597 vs 2234, margin +2.7 SE; kills guard PASS, detectable drop
  14%). Descriptive: wins 14 vs 12 (gained 4, lost 2), Cyril's captures 1.42 vs 1.62. The filler now pairs g3lost with
  g_iter3 vs Cyril toward 240 games; band guard + band test queued.
- g3tether: INCONCLUSIVE at 24 cells (defNearAtGrab20 5.29 vs 5.31) and 48 (6.30 vs 5.90, +7%, bar +15%); extending to 96.
- **g3tether Cyril delivery: FAIL** at 96 cells (68 shared): defNearAtGrab20 5.60 vs 5.64, margin -2.3 SE (24 and 48
  cells had been INCONCLUSIVE). Tethering defenders in fights does not put more bodies at the flag when Cyril grabs.
  Closed.
- g3lost band guard: INCONCLUSIVE at 24, PASS at 48 cells (kills 580 vs 488, enemyCaptured 1.44 vs 1.69); band test
  running.
- **g3lost band test vs g_iter3** (234 seeded pairs; identical 86/234): net +1, capture delta +0.14 +- 0.06 (t 2.2), upper
  +0.22 +- 0.09 (t 2.5): REWRITE_EVAL criterion (b) met; basics PASS. Confirmation seeds queued; the Cyril paired filler
  follows when the queue is idle.

**Second-audit fixes on g_iter4.**
- g4pick (C.PICKUP_AFTER_MOVE, BOT5) 5(a), 6 mirror maps vs g_iter4 mirrors: the after-move pickup counter stays 0 (a
  just-dropped flag cannot be picked up until the next round), but stepping to a loose flag instead of striking lifts
  pickups 3-10x and regrabs ~10x (Bunkers 38 vs 4, DefaultLarge 34 vs 3, Islands 74 vs 4); captures 7 vs 3 over the 6
  games. Delivery: `DGPOOL=CyrilSharma.finalBot DGTAG=-cyril BASE=g_iter4 tools/delivery-gate.sh g4pick
  'rel:regrabs>=2.0 nw:kills>=0.95 mean:overruns<=0'`.
- g4reach (C.RELOC_STALL_MOVES, BOT3(b)) 5(a), 8 traced maps: flag distance barely moves (mean of means +1%; Divergent +3
  tiles, BedWars -1). The stall fix alone recovers little; BOT3(a), scoring only reachable spots, is the larger part and
  is not built yet. Not gated.
- **g4pick Cyril delivery: PASS** (regrabs 9.54 vs 1.79, margin +3.7 SE; kills guard PASS, detectable drop 17%). The
  filler now pairs g4pick with g_iter4 vs Cyril; band guard + band test queued.
- g4econ (POST_SETUP_CRUMBS + GATHER_ROUND 185, BOT4) 5(a): gathered200 identical to g_iter4 on all 6 maps, gathered400
  equal in sum (57,100 each); kills lower in two games (ducks detour for crumbs). Does not deliver in mirrors; parked.
- g_iter4 vs g_iter3 vs Cyril (paired filler): 120 games, net +7 (+1.5 SE).
- g4pick band test vs g_iter4 (234 pairs): net +9 (18-9, p 0.12), capture delta +0.12 +- 0.09, upper +0.16 +- 0.12; basics
  PASS. No criterion met; extended with the confirmation seeds (pre-registered: the pooled 4 seeds must meet REWRITE_EVAL
  (a) or (b), else no promotion), since 234-pair blocks are underpowered for a few-point effect (audit MEAS2).
- g4pick pooled 473 pairs vs g_iter4: net +6 (p 0.53), capture +0.08 +- 0.06, upper +0.11 +- 0.09 (confirmation seeds alone
  net -3); Cyril paired filler -2 of 80. Pre-registered bar not met: parked. More re-grabs without more captures, as with
  the convoy.
- g4ahead (C.STUN_AHEAD, BOT7) 5(a), 6 scrimmages vs Cyril on the same cells as g4pred: carrier-stun builds halved (45 vs
  99) but carriers caught halved too (27 vs 51): stuns "behind" still catch carriers (escorts walk into them). Parked.
- g4pred (C.CARRY_PREDICT, BOT10) 5(a): chase totals unchanged (15,772 vs 15,666), unseen carrier-rounds a little lower
  (mean 64 vs 77); the indicator could not separate predicted chases, so a pr counter replaced the always-zero hl and the
  check is re-running.
- g4pred 5(a) re-run with the pr counter (identical play): predicted chases fire, 31-1861 a game. Delivery:
  `DGPOOL=CyrilSharma.finalBot DGTAG=-cyril BASE=g_iter4 tools/delivery-gate.sh g4pred 'rel:enemyUnseenRounds<=0.85
  rel:chasers20>=1.1 nw:kills>=0.95 mean:overruns<=0'`.
- g4pred Cyril delivery: INCONCLUSIVE at 24, 48 and 96 cells (96: unseen carrier-rounds 116.1 vs 116.5, chasers20 +8%, bar
  +10%; kills guard PASS). The prediction fires but does not put eyes back on carriers. Parked (not closed).
- Baseline after the second audit: g_iter4 vs Cyril 217-367 (37%), up from g_iter3's ~30%.
