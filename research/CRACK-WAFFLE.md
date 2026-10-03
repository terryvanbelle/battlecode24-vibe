# CRACK-WAFFLE.md: the next ladder target, winkelmantanner.waffle

Owner, 2026-10-03 (PROMPTS 157-159): ColtG5 is defeated; take on the next opponent, usually the bot just above us.
waffle is rated 1997 (rank 12), directly above g_iter2 (1918). It is also the bot above us we do best against: g_iter2
10-14 (42%), against CyrilSharma 7-17 and hsmalladi 5-19. g_iter1 was 47-177.

Evidence from g_iter2's 24 seeded band and confirmation games against waffle (replays reviewed; source never read).

## How waffle wins

- **Every loss is by all three flags** (14 of 14, median r715). Its first pickup comes at r247 (ours r340), its first
  capture at r337 (ours r352). It banks crumbs through setup (1895 at r250; ours 222) and spends them on stuns in the
  first fight (82 stuns by r400; ours 59). It lays 17 traps in setup (ours 32) and keeps about one duck per flag (Bunkers
  r238: ~35 of its ducks stream at one of our flags, its three flags hold one duck each).
- **It loses the fight and still takes the flag.** Islands, r200-400: waffle 104 deaths, us 48. Its carriers die
  within ~3 rounds, but the flag moves anyway because an escort re-grabs it.
- **Re-grab chain (death relay).** Of waffle's drops of our flags (its carrier died), it re-grabbed **87% in our losses**
  (344 of 396) and **77% in our wins** (229 of 297); most re-grabs come 1 round after the drop, the earliest the rules
  allow (RULES.md: a dropped flag can be picked up from the next round; it returns home after 4 end-of-round ticks).
  A chain needing n hand-offs reaches home with probability p^n: 0.87^10 = 25%, 0.77^10 = 7%. Small changes in p or n
  compound.
- **Local balance at its first grab does not separate wins from losses**: our ducks / its ducks within dist2 400 of the
  grabbed flag, median ~0.85 in both. Our army is often split (Islands: 17 of our 38 alive within 20 tiles).
- **Our stuns as placed today do not break chains**: drops with one of our stuns triggered within dist2 25 in the last
  4 rounds reset 17% of the time (32 of 193), others 18% (88 of 500). A stun freezes enemies within dist2 13 for ~4
  turns, so in principle it is the only denial that acts inside the 1-round gap, but ours fire at the wrong time/place.
- **Distance decides how fast our flags fall** (our 72 flags, by distance at r200 to waffle's nearest flag ~ spawn):

  | distance (tiles) | flags | captured | median capture round |
  |---|---|---|---|
  | < 20 | 24 | 88% | 358 |
  | 20-28 | 22 | 77% | 485 |
  | 28-36 | 18 | 50% | 476 |
  | 36-48 | 5 | 60% | 836 |
  | 48+ | 3 | 67% | 1334 |

  The game ends at its third capture, so our nearest flags set the clock.

## Cracks, in the order we test them

1. **g2reloc: flags far from the nearest enemy spawn** (C.RELOCATE_FLAGS + C.RELOC_V2). Longer chains: each extra
   hand-off multiplies its success by ~0.87, and our offense gets more time. Iteration 2's relocation (a2reloc,
   neutral) chose its spot from one guessed symmetry's centroid, wrong on about a third of map-sides before the
   observation fix; V2 waits for observed symmetry (decided at r1 in 54% of games, by r30 in 76%) or r40, then
   maximises the distance to the nearest enemy centre under every live symmetry. Audit A12 fixed with it (OF_HOME =
   the engine's r200 placement). New census columns flagDistMin / flagDistMean measure delivery.
2. **Chain breaking**: a stun built on the carrier's path (the ball walks into it; frozen escorts cannot re-grab
   for ~4 rounds, which is the reset window). Needs a trace first: why do today's stuns near drops not help?
3. **Counter-raid**: waffle leaves ~1 duck per flag. Our carries against it are mostly unopposed (0.3-0.6 of its
   ducks near our capturing trips), so the race is ours to win if we start earlier.

Escort-first targeting (C.Z2ESCORT) attacks the same chain but failed its band delivery (enemyRegrabs -17%, bar
-30%); a waffle-pool block is the fair test if (1) and (2) do not crack it.

## Measurement

- The idle filler plays waffle on fresh seeds: `FILLPOOL=winkelmantanner.waffle tools/filler-pair.sh g_iter2 <arm> 40`
  (both builds on the same seed and cells), tallied by `tools/collect-fillers.sh g_iter2 <arm>`.
- Victory read, pre-registered as for ColtG5: g_iter2-line build vs waffle at least 60% over 240 games, two-sided
  p < 0.05, paired net >= +2 SE.

## Pre-registered tests (written before any waffle block of the arm)

**g2reloc** (after step 5(a) shows flags moved without overruns or r200 resets):
1. Waffle delivery block, which gates: `DGPOOL=winkelmantanner.waffle DGTAG=-waffle BASE=g_iter2 tools/delivery-gate.sh
   g2reloc 'rel:flagDistMin>=1.15 rel:enemyCaptured<=0.85 mean:overruns<=0'` (24 scrims, random maps and sides, both
   builds on the same cells).
2. Band delivery (guard, so the band test may run): `DGTAG=-seeded BASE=g_iter2 tools/delivery-gate.sh g2reloc
   'rel:flagDistMin>=1.15 rel:kills>=0.95 mean:overruns<=0'`.
3. Then the filler pairs it with g_iter2 against waffle on fresh seeds to 240 games; adopt as the waffle candidate at
   paired net >= +2 SE, close at <= -2 SE. The band test (`SEEDS='515151 616161' TAG=-seeded`) must not be worse than
   net -5 before any promotion.

**g2rush10** (RUSHERS=10 on g_iter2; crack 3, queued behind g2reloc): the same waffle block with
`'rel:firstGrabs>=1.3 rel:captured>=1.15 rel:enemyCaptured<=1.15 mean:overruns<=0'`.

**Results so far** (2026-10-03): g2reloc delivery FAIL (waffle block: flags +18% farther, enemyCaptured -9%, bar -15%;
wins 8 vs 5 of 24); second attempt g2reloc2 with a 25-tile walk bound. g2rush10 does not fire at 5(a) (no added
presence in the enemy half: g_iter2's army already heads for their flags, and rushers stop to fight what they meet).

**g2z2w** (escort-first targeting, C.Z2ESCORT, rebuilt on the current g_iter2; its band delivery failed with enemyRegrabs
-17% where re-grabs are rare; waffle re-grabs ~22 times a game, so this is the fair test of chain breaking):
`DGPOOL=winkelmantanner.waffle DGTAG=-waffle BASE=g_iter2 tools/delivery-gate.sh g2z2w 'rel:enemyRegrabs<=0.8
rel:enemyCaptured<=0.85 mean:overruns<=0'`.

g2reloc2 (25-tile walk bound): 5(a) shows no larger dose (sum of nearest-flag distances 310 vs g2reloc 315 tiles over
12 maps): the binding limits are geometry, spacing and keeping each unplaced flag's own spot. Not gated; relocation
parked as a modest lever (+18% distance -> -9% waffle captures).

**g2alert400w** (C.ALERT_RADIUS2 400 on g_iter2, the existing g2alert400 build; fired at 5(a) with chases 474-3201 vs
0-2894; its band delivery failed on first grabs -12%, bar -15%). Against a deathball the question is whether a wider
response net masses on the threatened flag:
`DGPOOL=winkelmantanner.waffle DGTAG=-waffle BASE=g_iter2 tools/delivery-gate.sh g2alert400 'rel:enemyCaptured<=0.85
rel:chasers20>=1.1 mean:overruns<=0'`.

g2z2w and g2alert400 failed on the waffle pool (re-grabs unchanged 21.1 vs 21.1; captures 2.42 vs 2.46). At each drop
waffle has 1-9 ducks within dist2 8, so attacks cannot deny a re-grab; what matters is how fast the chain moves.

**g2cstun** (C.CARRIER_STUN: a duck with an enemy carrier of our flag within dist2 18 builds a stun within dist2 8 of
it, ahead on its way home; a frozen carrier cannot move for ~4 turns). 5(a): carrier-stun builds 2-32 a game vs 0-1,
carriers caught up on most maps (Islands 16 vs 7, DefaultMedium 10 vs 1). Waffle block:
`DGPOOL=winkelmantanner.waffle DGTAG=-waffle BASE=g_iter2 tools/delivery-gate.sh g2cstun 'rel:carrierStunned>=1.3
rel:enemyCaptured<=0.85 mean:overruns<=0'`.

**g2cstun first waffle block: FAIL on its pre-registered bar** (enemyCaptured 2.29 vs 2.46, -7%, bar -15%), with the
mechanism delivered (carriers caught 15.0 vs 4.8 a game) and the strongest outcome so far: **wins 13 vs 5 of 24, gained
8, lost 0** (sign p ~ 0.008; on the same cells g2reloc read 5-2, g2z2w 3-1, g2alert400 3-1), our captures 1.62 vs 1.04,
kills 585 vs 402, games 1269 vs 951 rounds. Trace: a frozen carrier delays waffle's captures more than it prevents them,
and the time goes to our offense; enemyCaptured over the whole game was the wrong proximate metric for a mechanism that
slows the chain.

**Second attempt (pre-registered before looking at the new column on the first block), fresh seed 919191:**
`SEED=919191 DGPOOL=winkelmantanner.waffle DGTAG=-waffle2 BASE=g_iter2 tools/delivery-gate.sh g2cstun
'rel:carrierStunned>=1.3 rel:enemyCaptured600<=0.8 mean:overruns<=0'` (enemyCaptured600: waffle's captures by r600, a
new census column). If it passes: band delivery `DGTAG=-seeded BASE=g_iter2 tools/delivery-gate.sh g2cstun
'rel:kills>=0.95 rel:enemyCaptured<=1.1 mean:overruns<=0'`, then the waffle filler pairs it with g_iter2 to 240 games
(victory read: >= 60% and p < 0.05, paired net >= +2 SE), and the band test for non-inferiority.
