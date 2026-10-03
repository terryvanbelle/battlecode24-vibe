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
