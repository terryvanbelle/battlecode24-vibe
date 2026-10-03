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
