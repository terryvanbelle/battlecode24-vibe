# TRAINING_LOG.md — append-only record of every attempt

Times are PDT. One entry per attempt: target, trace, pre-registration, counters, gate numbers, decision,
lesson, next. Corrections are dated entries, never edits. The closed-directions ledger and the
functional-area map are at the end and are the only parts edited in place.

## Phase 0 (2026-09-30)

- Engine battlecode24 3.0.6 built from source (maven artefacts return 403); seed patch honoured
  (robot ids change with `-Dbc.game.seed`); determinism verified: the same seed twice gives identical
  per-round metrics.
- Replay reader `tools/replaydump/ReplayDump.java` with summary / `--every` / `--metrics` / `--from --to` /
  `--robot` / `--map-at` / `--logs` / `--bytecode` / `--navstats` / `--flags`; integrity-tested on a
  committed fixture replay (`tools/test_tools.py`).
- 2024 replays carry no robot stdout: counters go in indicator strings.
- Benchmarks: 73 repos with 2024 bots found and cloned (BENCHMARK.md); blind compile running.
- Compute VM `battlecode-dev` could not start: `ZONE_RESOURCE_POOL_EXHAUSTED` for e2-standard-8 in
  us-west1-b; retrying every 5 minutes.

## Iteration 0 — foundation bot (g_iter0)

Architecture (`src/bot`): `RobotPlayer` (turn loop, bytecode monitor, exception counter), `G` (per-robot
globals, xorshift rng from the id, rng tie-breaks), `C` (constants), `Comms` (shared-array schema),
`Nav` (greedy + bug with per-robot handedness, stall exit, fill water when stuck), `Micro` (kite and
strike on a dist² 10 threat radius, flag carriers then lowest HP), `Duck` (setup: crumbs then explore,
gather at the dam from r150; defenders 0-2 trap their flag; main: pick up / carry flags to the nearest
spawn tile, fight, heal, go to the nearest known enemy flag or broadcast hint; upgrades attack > heal >
capture).

Diagnostic (DefaultSmall, seed 7, vs examplefuncsplayer): **won by CAPTURE at r746**; first capture r436;
kills 424 / deaths 12; 248 stun traps built (35 hit by the enemy... enemy triggered 207 of ours);
bytecode max 13,981 of 25,000, 0 overruns, 0 exceptions; nav ABA oscillation 9.1% (example bot 15.9%).
Accepted by construction as the first rung.

## Iteration 1 — stack: symmetry, trap rings, advance, float spending, carrier chase (pre-registration, 2026-09-30)

Targets: (a) self-play standoff: every g_iter0 mirror game ran to r2000 on level sum, no kills r400-r1500,
13k crumbs floating by r1500 (speed1 run, 8 games); (b) T1 flag rush: calibration losses end r380-r630 with
our spawn-held flags carried off (hsmalladi.finalbot DefaultSmall traced).
Mechanisms and counters (indicator string of every robot): `adv` Micro advances (allies+1 >= enemies+3);
`ft` float stun traps (bank > 1500, idle); `ch` carrier chase/intercept turns; replay counts of stun/explosive
traps for the defender ring and the dam line.
Diagnostic (DefaultSmall seed 11 vs g_iter0): won by CAPTURE r1241 (was: every mirror game to r2000);
adv 3-125 per robot, ft 0 (bank now spent in fights), ch 0 (g_iter0 never carries our flags: the mirror
cannot see T1; the field can). 0 overruns, 0 exceptions, bytecode max 12k.
Gate: paired mirror SPRT vs g_iter0 as a regression screen; the judge for T1 is the ladder (head-to-head
block vs g_iter0's calibration band). Falsifier: ladder rating not above g_iter0's.

### Iteration 1 — result: ACCEPT (g_iter1)

- Mirror screen (paired SPRT vs g_iter0, gate1): ACCEPT 27-1 discordant after 48 pairs.
- Field judge (same 55-bot field, two games each, random maps/sides): **g_iter1 1756 ± 88, rank 14 of 57,
  field score 73.1%** (81-29) against g_iter0 1510 ± 78, rank 24, 51.5% (56-53). +246 Elo, far outside both
  intervals. Losses before r600: 26 (g_iter0) -> 6 (g_iter1).
- Lesson: the stack mixed a defect repair (standoff), a spend-the-float rule and a T1 defence (carrier chase);
  attribution between them is open. Next: the T2 offence copy (flag relocation) as iteration 2.

## Iteration 2 — T2 and T3 offence copies: setup flag relocation, flag relay (pre-registration)

Motivation: g_iter1's fastest losses (andli28 on Battlefield r297; jmerle on Pancakes r374) lose spawn-held
flags within 20-40 rounds of the dam opening, and lose flag races to relay carriers.
- Relocation (C.RELOCATE_FLAGS): defenders 0-2 carry their flag in setup to spots far from the enemy centroid
  (grid search, 8+ apart, one spot per turn for bytecode) and publish the new home (slots 20..22); alerts,
  defence and respawns use the home. Counter `rl`. Diagnostic DefaultHuge seed 11: flags placed r23/r44/r76,
  0 overruns after spreading the search (first version hit the limit on 2 turns in round 1-2).
- Relay (C.RELAY): a carrier that can act drops the flag on the adjacent tile nearest home when an ally is next
  to it; allies pick up visible enemy flags first thing in their turn. Counter `ry`.
Screen: gate2 (paired mirror vs g_iter1, the relocation-only code with the round-1 overrun) read 29-33
discordant after 128 pairs: no regression beyond noise; the mirror cannot price a defence against rushes.
Judge: three field blocks on identical cells (scrim SEED=424242, all 55 bots x2): a2reloc, a2relay, and a
g_iter1 control in the same period. Accept the arm whose rating beats the g_iter1 control block.
- gate2 final: SPRT_INCONCLUSIVE 36-40 discordant after 160 pairs (concordant 40-44): relocation is neutral
  in self-play, as expected for a field-facing defence; the field blocks decide (queued, run h2h2).

## Iteration 3 (prepared) — T4 offence copy: setup checkerboard digging (dose ladder)

Onset table of g_iter1's block: level_sum (us-them) is the earliest predictor (+0.37 at r100, +0.40 at r200);
top bots dig 60-86 tiles in setup. C.SETUP_DIGS (0 = off), DIG_RESERVE 1000, parity x+y even, never within
dist2 8 of a spawn centre. Counter `dg`. Diagnostic a3dig5 vs g_iter1 DefaultMedium seed 11: 63 digs by r100,
won on level sum at r2000 (421 vs 419); 0 overruns. Dose arms 0/5/10 to be built on the iteration-2 winner.

## Rush offence and sparring archetype (owner prompt 6, 2026-09-30)

"If self-play can't show the value of a rush defence, implement a rush offence, then use self-play to develop
the defence." `C.RUSHERS` ducks after the defenders rush: target = a friendly carrier in view (escort, feeds the
relay), else a visible enemy flag, else the nearest known/broadcast flag; `Micro.fight(enemies, allies, goal)`
keeps the strike logic but scores tiles by -150 per threatening enemy - 4 x dist2 to the goal. Counter `rs`.
- v1 (walk + strike, no micro) vs g_iter1, DefaultSmall seed 3: LOST, 759 deaths to 86, never carried a flag out.
- v2 (pushing micro) same cell: WON by CAPTURE r756, first capture r361, 219 pickups (relay active).
`src/arch_rush` = RUSHERS 47. Queued (run rush3): archetype check vs g_iter1 on 16 random maps x 2 sides
(a partner must land 25-75% to rank builds), then a field block on the same cells (SEED 424242) as an
offence arm. Next: defence work as paired mirrors with OPP=arch_rush.

### Iteration 2 — result: REJECT (both arms), kept switched off

Field blocks on identical cells (scrim SEED 424242, 55 bots x 2, same period): a2reloc 81/110, a2relay 81/110,
g_iter1 control 84/110. Neither arm beats the control; direction negative, inside noise. Closed as shipped
features: C.RELOCATE_FLAGS=false, C.RELAY=false in src/bot (code kept; arch_rush keeps the relay, which fed its
captures). Ledger: relocation (T2 copy) priced at -3/110 on the field; re-open if a rush defence makes flags at
spawn more costly or the archetype shows spawn-held flags falling first.
- Archetype check (archcheck, 16 random maps x 2 sides): **arch_rush 5/32 (15.6%) vs g_iter1** - below the
  25-75% band a sparring partner needs; the local seed-3 win was a filter, not a verdict. Hybrids queued (rush4):
  arch_rush10 / arch_rush20 = g_iter1 behaviour + a 10/20-duck rush squad with the relay.
- Hybrid checks (rush4, same 16 maps x 2 vs g_iter1): **arch_rush10 17/32 (53%)**, arch_rush20 12/32 (38%).
  Both inside the sparring band; arch_rush10 is also an offence candidate (level with the incumbent in self-play).
- Queued (q5): arch_rush10 field block on the iteration-2 cells (SEED 424242; compare with the g_iter1 control
  84/110); then defence gates vs the archetype, paired, OPP=arch_rush10, REF=g_iter1, 96 pairs each:
  d1def2 (2 defenders per flag), d2alert (alert radius dist2 100 -> 400).
- arch_rush field block (all-out rush as offence): 62/110, 1537 +- 91: far below g_iter1 (1830-1888). Closed as
  an offence doctrine.
- arch_rush10 field block (10-duck squad + relay), identical cells: 84/110 = the g_iter1 control 84/110; game-by-
  game 7 flips each way, scattered by opponent: neutral. Kept as the sparring partner only, not shipped.
