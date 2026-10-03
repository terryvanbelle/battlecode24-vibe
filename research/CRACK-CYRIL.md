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
