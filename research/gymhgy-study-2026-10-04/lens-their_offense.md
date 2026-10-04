# Lens 2: how Gymhgy.v10official captures our flags (g_iter4, existing games only)

## Data and method

- **Replays:** 394 g_iter4 vs Gymhgy filler replays still on the VM (157 wins, 237 losses; 78 maps, 63 of them with both a win and a loss).
- **Dumps:** `replay-dump --flags --defense` ran on the VM (niced, 2 at a time). A scratch dumper (`l2/java/l2/PosDump.java`, run on the driver) read the replays streamed read-only over ssh. It wrote out every robot's position on each round where one of our flags was off home.
- **Census cross-check:** 536 Gymhgy games (215 W / 321 L) from `research/fill/fillcensus-g_iter4-*.csv`, read at 19:08. The census has since grown to 993 games and was not re-read.
- **Chain:** a Gymhgy first grab of one of our flags from its home, through every drop and pickup, until CAPTURE or RETURN (home again). Some chains were still running at game end (OPEN). There are 3,553 chains: 795 CAPTURE, 2,710 RETURN, 48 OPEN.
- **Units:** "ours20" / "theirs20" = robots of each team within dist2 20 (vision) of the flag, whether carried or dropped, at the end of each round. "t" = rounds since the grab. Within-map deltas are win-mean minus loss-mean per map, averaged over maps that have both.

## Findings

1. **Gymhgy does not grab more in our losses. It converts more.**

   | | wins | losses | within-map delta |
   |---|---|---|---|
   | First grabs per game | 8.9 | 9.1 | +1.2 (31 maps higher in wins, 30 lower) |
   | Captures per chain | 0.29 | 0.41 | −0.17 (t −6.1; lower in wins on 48 of 62 maps) |
   | Median first capture | r344 | r348 | |
   | First capture by r400 | 58% | 60% | |

   The early rush is the same in both results: chains that start in r200-300 convert 0.40 in wins and 0.43 in losses. Later chains convert about twice as often in losses: 0.29 vs 0.15 at r400-600, and 0.22 vs 0.11 at r600-1000.

2. **What the relay looks like.** A capture chain in a loss lasts 52.6 rounds, with 7.4 hand-offs, 1.2 self re-picks and 0.65 re-grabs.
   - Gap from drop to re-pick (n=13,655): 1 round 57%, 2 rounds 21%, 3 rounds 17%, 4 rounds 4.4%.
   - A dropped flag returns home after exactly 4 rounds (2,661 cases), or 25 with CAPTURING (49 cases).
   - Speed: 0.56 tiles a round of net flag movement (0.68 per carried round). A lone carrier makes 0.50, so the relay buys only about 12% speed.
   - At each hand-off drop, 3.2 Gymhgy robots stand within dist2 2 of the flag, ready to pick it up. Ours within dist2 2: 0.14. Ours within dist2 8: 0.44 (at least one in only 20% of drops).
   - Ours within dist2 4 of the new carrier: 0.21 (12% of 6,584 re-pickups).

3. **The size of its group at the grab decides the chain. Our defenders' count at the grab does not.**
   - P(capture) by theirs20 at the grab: 0-2: 0.04 (n 694); 3-5: 0.11; 6-8: 0.22; 9-11: 0.30; 12+: 0.48 (n 789).
   - Inside each row, ours20 at the grab barely matters. In the 12+ row it is 0.42 with no ours near and 0.59 with 10+ ours near.
   - A group of the same size converts more in our losses: 12+: 0.54 vs 0.36; 6-8: 0.27 vs 0.14.
   - Within 10 tiles at the grab:

     | | ours | theirs |
     |---|---|---|
     | Capture chains | 10.7 | 17.9 |
     | Returned chains | 8.0 | 10.0 |

4. **We lose contact, but we stay close.**
   - In capture chains, ours20 falls from 4.3 at t0 to 2.0 at t5, 1.2 at t10 and 0.8 at t20, while theirs20 stays near 12. In returned chains ours20 rises from 3.4 to 4.2 (reinforced).
   - Capture chains have no robot of ours within vision of the flag on 63% of rounds. Median last contact is 10 rounds before the capture; 207 of 778 have no contact at all from t10 on.
   - On 46% of capture-chain rounds (losses), no robot of ours has seen the flag for 6+ rounds, so a 5-round carrier sighting (CARRY_FRESH) is stale. Returned chains: 5%.
   - Most of that time we are near but screened. On 44% of capture-chain rounds, ours are 5-10 tiles from the flag (dist2 21-100) but none is within dist2 20; on only 19% is none of ours within 10 tiles.
   - Within 10 tiles during the chain:

     | | ours | theirs |
     |---|---|---|
     | Capture chains | 8.1 | 22.0 |
     | Returned chains | 12.1 | 11.6 |

5. **What happens to our robots that were near the flag at the grab (within dist2 20).**

   | | still near | alive, out of the flag's vision, an enemy within dist2 20 (still fighting) | caught by a Gymhgy stun | dead |
   |---|---|---|---|---|
   | Capture chains, t5 (2,501 robots) | 24% | 47% | 14% | 11% |
   | Returned chains, t5 | 65% | 22% | 4% | 7% |
   | Capture chains, t10 | 5% | 45% | 26% | 17% |

6. **Its rear-guard stun screen.**
   - Its stuns triggered near capture chains catch 15.3 of ours per chain in losses vs 10.3 in wins (within-map −5.4, t −4.5; lower in wins on 41 of 53 maps).
   - It builds 2.1 stuns within dist2 20 of the flag per capture chain in losses.
   - Example (Canals loss): stuns built at (16,34) r268, (18,33) r271, on the drop tile (12,36) r275, and (16,33) r277. Our pursuers set them off at r272, r275, r277, r283 and r284.

7. **Respawns do not reach the flag.**

   | | respawns per chain | distance from the flag (tiles) | share that reach it |
   |---|---|---|---|
   | Capture chains | 13.2 | 17.2 | 12% |
   | Returned chains | 4.9 | 10.7 | 42% |

   Per game, the share of respawns that reach the chain is 0.37 in wins vs 0.30 in losses (t 4.2, higher in wins on 41 of 60 maps).

8. **Bodies were available.**
   - At the grab, 16.4 of ours were on our half but more than 10 tiles from the flag (our alert radius is dist2 100). Another 17.2 were on its half and 5.7 were jailed.
   - 40.6 of ours could have reached the capture tile, in a straight line ignoring walls, before the flag did (23.0 in half the time). At the capture only 3.5 were within dist2 64 of it.
   - Over the last 10 rounds before the capture: ours20 1.0, theirs20 13.7.

9. **What breaks a chain in practice.**
   - 88% of returned chains (2,381 of 2,710) end with a carrier killed and nobody re-grabbing within 4 rounds. At that moment ours20 is 8.1 vs theirs20 4.0.
   - 12% (329) end with a voluntary drop nobody re-picks. At that drop, ours within dist2 8 = 2.6, vs 0.44 at capture-chain drops.
   - Capture chains lose 0.61 carriers on average; 63% lose none.

10. **Which of our flags fall.**
    - 81% of our flags are captured in losses, 46% in wins.
    - The first flag captured is the one nearest its spawns (BFS) in 54% of 331 games (chance: 33%).
    - Within a game, captured flags sit 3.1 tiles nearer its spawns (207 games; nearer in 65%).
    - Conversion by BFS distance from our flag's home to its nearest spawn: under 20: 0.50; 20-27: 0.29; 28-35: 0.21; 36-47: 0.13.
    - Canals: the centre flag (BFS 18-19 from its centre spawn) fell by r255-332 in all 5 games, wins included. Its first grab was always at our flag's home. The far flags (BFS 36) decided every game.
    - Relocation is a hypothesis only. In 92 games with mixed placement, relocated flags were captured 57% of the time and flags left at the spawn centre 43%. This is confounded by which flags fail to relocate.

**Census cross-check (536 games).** Its captures run 2.36 in losses vs 1.34 in wins. Unopposed captures (mean chasers20 under 0.5) are 2.18 vs 1.31, so about 85% of the extra captures in losses are unopposed. Its escorts20 is 9.5 vs 8.3 (within-map t −3.6), its relayPickups 32 vs 24.5, and enemyFirstGrabs 9.45 vs 8.93 (no difference).

## Traces

### Canals loss: `gauntlet/20261004-172515-scrim-g_iter4-fill1791134708/losses/Gymhgy.v10official__Canals__botA.bc24` (we are A)

**Flag 1193, r268-297.**
- Our army (19 within dist2 20) was fighting at our flag (15,39) beside our centre spawn.
- It picked the flag up out of the melee at r268. The flag lay at (14,37) from r269 to r271, picked up again at r272 (the 3-round gap).
- Ours20 fell 17 → 13 → 5 → 0 by r280, with the stun screen above firing. The flag was captured at (15,20) on r297 after 9 pickups and no carrier deaths.

**Flag 1678, r460-519.**
- Board at r459: about 15 of its robots at our corner flag (2,56); about 20 of ours idle at the captured centre spawn, about 20 tiles away.
- Grab with 13 of theirs vs 6 of ours. Ours20 was 0 from r463 to the capture, apart from 1 at r493-497.
- Over r463-475, 6-9 of ours were within 10 tiles, every one with indicator note `fight`, at 6-10 tiles from the flag, against 14-20 of theirs. Our nearest robot stayed 6-9 tiles away for about 50 rounds.
- The convoy walked 34 tiles (13 pickups, 1 carrier death) and captured at r519.

### Canals win: `gauntlet/20261004-185652-scrim-g_iter4-fill1791140205/replays/Gymhgy.v10official__Canals__botB.bc24` (we are B)

**Flag 128, r349-410.** Grab with 13 of theirs vs 2 of ours, beside our spawn.
- Our respawns appeared 2-6 tiles from the flag. Our nearest robot was adjacent (1.0) on most rounds, and ours20 reached 6-9 by r368.
- 6 carriers were killed. After 11 pickups and 10 tiles of progress, the flag went home at r410.

## What would have broken the chain

- **Killing the receiver is not available.** Ours are within reach of a new carrier at only 12% of re-pickups.
- **A stun at the drop does not break it on its own.** A stun freezes for 3 turns, and receivers can still pick up on round 4; 3.2 receivers are within dist2 2 and 12 escorts are near. Our carrierStunned is already about 9 a game in wins and losses alike.
- **What does break chains:** about local parity within 10 tiles plus carrier kills (finding 9). In capture chains we have about 8 within 10 tiles against 22, and 16 idle on our own half.
- **Levers, in that order:**
  1. **Recall:** a wider recall when a big group hits a flag.
  2. **Dive:** ducks within 10 tiles close on the flag instead of kiting the screen.
  3. **Respawn onto the chain:** respawns go to the chain.
  4. **Keep the track:** keep tracking it through drops and vision loss, at 0.56 tiles a round toward its nearest spawn.
  5. **Destination meet:** meet it at its destination with mass.

## Levers (census column, chosen-map diagnostic)

- **L1 Recall:** when 8+ enemies are seen within dist2 100 of a flag, or a grab happens, ducks on our half go to the flag even beyond ALERT_RADIUS2 100. (The 16.4 of finding 8.)
  - Column: new `near100AtGrab10`, ours within dist2 100 of the flag 10 rounds after each first grab (now 8.1 vs 22.0 in capture chains).
- **L2 Dive:** in fight mode, a duck within dist2 100 of our carried or dropped flag scores tiles toward the flag (to get vision and reach the carrier) above kiting. C.INTERCEPT is the closest switch, but it needs a fresh sighting, which 46% of capture-chain rounds lack.
  - Columns: new `flagContact20` (share of enemy-chain rounds with at least one of ours within dist2 20 of the flag; now 0.37 in capture chains, 0.89 in returned) and `screened`, the 44% figure of finding 4.
- **L3 Respawn onto the chain:** during a live or predicted chain, respawn at the zone nearest the flag's predicted path and walk to it.
  - Column: new `respawnArrive` (now 0.12 in capture chains; per game 0.30 L / 0.37 W).
- **L4 Track through relays:** a dropped own flag away from home refreshes the carried sighting, and an unseen chain is predicted at 0.56 tiles a round toward its nearest spawn (C.CARRY_PREDICT assumes 0.5).
  - Columns: existing `enemyUnseenRounds` (80 L vs 74 W); new `stale6`, the 46% figure of finding 4.
- **L5 Destination meet with mass:** for a chain with 9+ escorts, ducks that can arrive first gather 2-4 tiles in front of its nearest spawn zone (C.DEST_CAMP is the nearest switch).
  - Column: new `capMeet64` (now 3.5).

**Chosen-map diagnostics** (census W/total, then its captures per game):
- KingQuacksCastle side A: 0/7, 3.0, unopposed 2.86
- Joker B: 0/5
- Snake B: 0/5
- DefaultHuge B: 0/4
- GaltonBoard A: 0/5; GaltonBoard B: 0/5
- Foxes B: 0/9
- Puzzle A: 1/8
- **Control:** Canals A (2/6) and B (2/3). The centre flag always falls; the far flags decide, so the diagnostic should read whether the far-flag chains turn into RETURNs.
- **L5:** Valentine A (1/8, the longest chains, about 12 hand-offs).

## Not measured

- **Intent:** our robots' intent was checked only in one trace window, via indicator notes. Gymhgy's intent (deliberate drop timing, stun on the drop tile) is inferred from games only.
- **Distances:** BFS distances ignore water, dams and digging. Positions are end of round.
- **Before the grab:** positions are recorded only every 10 rounds plus the round before the grab, so warning time before grabs is not measured.
- **Coverage:** census games without a replay are not in the chain data. Replays were listed at 19:02, and older ones had been pruned.
- **Counterfactuals:** whether recall, dive or meet would win the local fight cannot be read from replays and needs diagnostic games.
- **New columns:** the census columns proposed above are not built.
