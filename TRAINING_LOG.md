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

## Defence against the rush partner (paired mirrors, OPP=arch_rush10, REF=g_iter1, SEED 777)

- d1def2 (2 defenders per flag): **SPRT_REJECT 9-25** discordant after 80 pairs.
- d2alert (alert radius dist2 400): **SPRT_REJECT 12-31** after 80 pairs.
  Bodies pulled home cost more than they save (ADVICE: standing defences that cost no actions beat bodies).
- Next (q7): d3fort (ring out to dist2 13, posts two tiles out on alternate laps, no crumb reserve; diagnostic
  vs arch_rush10 DefaultSmall seed 3: 48 stun + 11 explosive by r200 vs ~15-24 before, 0 overruns) and a2reloc
  (T2 relocation, neutral on the field, now priced against the rush it is meant to answer).

### Iteration 3 — setup digging (T4 copy): REJECT, closed

Field blocks on the shared cells: a3dig5 84/110, a3dig10 82/110 vs the g_iter1 control 84/110. No dose response.
The r50-r200 level_sum onset marks strong opponents (they dig) rather than a lever for us. Ledger: re-open only
if a build-level specialist (cheap traps) becomes part of a trap-heavy defence.

## Iteration 4 (queued) — bank through setup, spend on combat traps

Block study of the g_iter1 control vs the 14 top bots: at r250 they hold a median 2398 crumbs (us 240); by r400
they have built 90 traps (us 57) and we trigger 53 of theirs (they 32 of ours). They spend in the fight.
Arm c4bank: no dam traps (DAM_TRAP_ROUND 999), flag rings only above a 1000 bank, fills keep 500, combat traps
with an enemy within dist2 13 from 2 enemies, explosive when the bank >= 1500 and 4+ enemies; the trap goes on the
adjacent tile nearest the enemy centroid (new placement code, default-equivalent in src/bot). Counter `ct`.
Diagnostic (DefaultMedium seed 4 vs g_iter1, before FILL_RESERVE): bank still 230 at r200 because 99 fills cost
~3000 crumbs; FILL_RESERVE 500 added. Won on level sum 405-391, kills 234-169. Field block queued (q8).
- d3fort (fortress ring): SPRT_INCONCLUSIVE **16-29** after 96 pairs (LLR -2.68, near the reject bound).
  Three defence arms in a row read negative vs arch_rush10. Rule: three rejects in one area -> leave it; and run a
  control-versus-control gate to prove the harness fair: z0inert (src/bot with default constants, functionally
  g_iter1) vs g_iter1 with OPP=arch_rush10, SEED 777, queued (q9) after c4bank's field block.
- a2reloc vs arch_rush10 (paired): SPRT_INCONCLUSIVE 20-21: relocation is neutral against a rush too. Closed.

### Iteration 4 — c4bank: REJECT
Field block on the shared cells: 80/110 vs the g_iter1 control 84/110; game by game 3 gained, 7 lost.

## Plateau audit (2026-09-30) and iteration 5 (aggression dose)

Since g_iter1: relocation, relay, rush offence, setup digging, more defenders, wider alert, fortress ring and
bank-then-spend traps are all neutral or negative. Defect census over 40 g_iter1 control replays: 0 fatal
exceptions, 0 caught exceptions (indicator x), 0 turns at the bytecode limit, ABA 9.4% (kiting, by design),
still 32.6%. No defect to repair. Behaviour gap vs the top 14 at r400: we heal more (1282 vs 1005) and attack
less (807 vs 975). Dose ladder on aggression (field blocks, shared cells): e1aggr ADVANCE_MARGIN 3 -> 1;
e2aggr + ENGAGE_MAX_THREAT 1 -> 2. Queued (q10) after the inert control gate (q9).

### Harness check (2026-09-30) — an instrument error found

- q9 "inert" control z0inert vs g_iter1 (OPP=arch_rush10): SPRT_INCONCLUSIVE **18-32**. It was not inert: the
  iteration-4 rewrite of placeCombatTrap was claimed default-equivalent and is not (it picks the adjacent tile
  nearest the enemy centroid and only if closer than we stand; g_iter1 takes the tile in the direction of the
  centroid). The reading prices that placement change at about -14 discordant in 96 pairs vs the partner.
- Contaminated by the new placement: c4bank (80/110), z0inert, e1aggr (82/110), e2aggr (83/110). The aggression
  dose must be re-run on the restored placement. d1def2, d2alert, d3fort were built before the change (valid).
- Fix: C.TRAP_PLACEMENT_V2=false restores g_iter1's placement exactly. Queued (q11): z1copy (byte-identical copy
  of g_iter1; the harness must read 0 discordant) and z2inert (src/bot, defaults; must read ~0).
- Lesson: "default-equivalent" is a claim to verify with a paired inert gate, never an assumption.
- q11 harness verified: z1copy (byte-identical g_iter1) **0-0 discordant** in 48 pairs (concordant 29-19); z2inert
  (src/bot defaults, TRAP_PLACEMENT_V2 off) **0-0**. The paired OPP harness is exact; src/bot is inert again.
- Re-queued on the restored placement (q12, field blocks on the shared cells): e1aggr, e2aggr, c5bank (c4bank's
  bank settings without the new placement).

### Iteration 5 — aggression dose: REJECT
Clean field blocks (restored placement, shared cells): e1aggr 83/110 (4 gained, 5 lost), e2aggr 81/110 (3, 6)
vs g_iter1 control 84/110. No dose response. The attack/heal gap vs top bots is a symptom, not a knob.
- c5bank (clean bank settings): 82/110 vs control 84 (4 gained, 6 lost): banking alone is neutral.
- c6pair (bank + combat stun from 1 enemy, no reserve) diagnostic DefaultMedium seed 4: bank only 560 at r200
  because 77 fills (~2300 crumbs) still happen above the 500 reserve; 19 setup traps. Field block running (q13).
- T7 adoption arm t7water (combat trap = water trap when allies+1 < enemies) queued after it (q14).
- c6pair field block: 82/110 vs control 84 (4 gained, 6 lost): the bank+spend pair is neutral too.

## Iteration 6 (queued) — T8 sustain: retreat threshold dose ladder
Survey: 10 of the 16 bots that beat us heal more than they attack by r400. Knob: C.RETREAT_HP (below it a duck
minimises threat and backs toward allies/healers): 300 (g_iter1) -> 500 (t8hp500) -> 700 (t8hp700). Field blocks on
the shared cells after t7water (q15).
- t7water (T7 adoption: water trap when outnumbered): 85/110 vs control 84 (6 gained, 5 lost): neutral.
- T8 retreat dose: RETREAT_HP 500 -> 82/110 (4, 6), 700 -> 80/110 (3, 7) vs 300 (control) 84. A monotone dose
  response in the WRONG direction for the sustain hypothesis: more retreat is worse. Extending the ladder the other
  way: 150 (t8hp150), 0 (t8hp0, never retreat) queued (q16).
- T8 ladder extended: RETREAT_HP 150 -> 83/110, 0 -> 85/110. Full ladder 0:85 150:83 300:84 500:82 700:80.

## Instrument change (2026-10-01): the field judge moves to the rating band

Since g_iter1 every arm has landed within +-4 games of 110 on the full-field cells; any two builds flip ~10 games
each way, so the instrument resolves ~5 games and half of each block is spent on bots we always beat.
TRAINING_ALGORITHM §6 already says pool = the band. New judge: the 20 bots nearest the incumbent's rating
(`tools/band-20261001.txt`, from `elo.py --band 20 --as g_iter1_c2`), 120 games per seed, two seeds (SEED 515151
and 616161), identical cells for every arm. Queued (q17): g_iter1 control, t7water, t8hp0, s1stack (both).

## Iteration 7 (structural swing, in progress) — micro v2: smooth tile scoring

C.MICRO_V2 (off in src/bot): one score per tile: +100 reach (+60 if the reachable enemy dies to one hit),
-threat weight per enemy within dist2 10 (120 hurt / 15 strong / 45 weak), +6 per ally within dist2 8, approach
when ready, back off when not, pull toward the carrier and (when strong) toward the field objective (V2_GOAL per
tile); targeting: a kill this turn, then carriers, then lowest HP.
Diagnostics vs g_iter1 DefaultSmall seed 4 (single games, filters only): no objective pull -> kills 125-49 but
0 flag pickups, lost on captures r966; pull 20 -> kills 304-233, pickups 2 vs 29, lost on flag count; pull 60 ->
kills 145-232, lost r1035. Paired mirrors vs g_iter1 queued (q18): m2g20, m2g60, 96 pairs each.

### Band verdicts (q17, 20 band bots, seeds 515151 + 616161, 240 games per build)
| build | wins/240 | paired vs control (gained-lost) |
|---|---|---|
| g_iter1 (control) | 84 | - |
| t7water | 91 | 8-7 and 13-7 -> 21-14 |
| t8hp0 | 82 | 7-9 and 10-10 -> 17-19 |
| s1stack | 86 | 6-9 and 13-8 -> 19-17 |
t7water over every cell so far (field + band): 27 gained, 19 lost, sign test p ~ 0.3: weak positive, below the
provisional bar (p < 0.10). Two more band seeds for g_iter1 and t7water queued (q19). t8hp0 and the stack: closed.

### Iteration 7 — micro v2: REJECT (closed)
Paired mirrors vs g_iter1 (q18): m2g20 **SPRT_REJECT 1-25**, m2g60 **SPRT_REJECT 1-26** after 48 pairs. The
lexicographic engage/kite micro is far better than this smooth scoring. Ledger: closed; re-open only with a new
premise (e.g. a scored micro built from logged fight states, not hand weights).

## Iteration 8 — T1 neutralization: a stun trap on the flag tile (queued)
Survey (vs the top 12, our losses, 320 games): they pick up our flags 17 times a game (median), we pick up theirs 3;
first pickups tie at ~r270. A stun trap triggers when an enemy enters a tile within dist2 2 of it, which is exactly
the pickup range, so a stun trap ON the flag tile freezes the raider (cooldowns 40) at the moment of the grab.
g_iter1 skipped the flag tile (dh == 0). C.FLAG_TILE_STUN: defenders build it first and rebuild it when it fires.
Counter `fs`. Diagnostic vs arch_rush10 DefaultSmall seed 4: placed in round 1 by every defender, rebuilt later
(fs2); partner pickups 8. Queued (q20): paired gate vs arch_rush10, then band seeds 515151 + 616161.
- t7water band seeds 3-4 (717171, 818181): 10-15 and 12-4. Over 4 band seeds: wins 175/480 vs control 165/480;
  paired 43 gained, 33 lost; with the field block 49-38, sign test p ~ 0.24. Lean positive, below the provisional
  bar (p < 0.10). Kept off; candidate to stack with the next change that clears its gate.

## Throughput audit (owner question, 2026-10-01)
VM /proc/stat since boot (12.6 h): user+system ~70%, idle ~29%, steal <1%. During runs the 8 vCPUs are saturated
(7 games, load ~19, idle < 5% in a 1-minute sample), so more parallel games would not help; the loss is idle time
between runs (results collected and recorded on the driver, launches waiting for a task check, 30 s polling loops,
batch tails). Fix: a standing queue on the VM (tools/vm-queue.sh, tools/vm-enqueue.sh) with an idle filler (band
blocks of the incumbent on random seeds, which also tighten its rating). Smoke-tested live.

### Iteration 8 — flag-tile stun (T1 neutralization)
- Paired gate vs arch_rush10 (q20): SPRT_INCONCLUSIVE 20-25 after 96 pairs. Band seed 515151: 37 vs control 43
  (9 gained, 15 lost). Second seed running; heading for REJECT.

### T10 (new tactic, traced 2026-10-01) — the centre crumb windfall
NotLLeon on HungerGames: bank 2590 at r200 -> 17950 at r225 (the map holds 22,200 crumbs, many behind the dam),
then 148 stun traps by r350 vs our 47; we triggered 130 of them. Correction after a diagnostic (t10crumbs vs g_iter1,
HungerGames seed 4): our ducks do collect the centre crumbs by walking over them, and g_iter1 spends them at once
(~80 traps in 25 rounds); the difference is that they bank the windfall and spend it over the fight. Arm t10crumbs
(C.POST_SETUP_CRUMBS: idle ducks detour to visible crumbs after setup) queued on the band (2 seeds).
- n1flagstun band seed 616161: 41 vs 39 (10 gained, 8 lost). Two seeds: 19 gained, 23 lost: **REJECT**.
- t10crumbs band seed 515151: 41 vs 43 (9, 11). Second seed running.

## Plateau escalation step 1 (2026-10-01): ablate what g_iter1 carries
~16 arms since g_iter1 without an accept. TRAINING_ALGORITHM §4: ablate accepted features first (failure-mode
preventers may be worth most, thin-margin features nothing). Band arms, 2 seeds each, queued (ablate4):
ab1nodam (no dam-front traps), ab2noring (no flag trap ring), ab3nofloat (no idle-bank stun traps; doubles as the
T10 "bank the windfall" arm), ab4nocombat (no combat stun traps).
- t10crumbs band seed 616161: 40 vs 39 (12, 11). Two seeds 21 gained, 22 lost: **REJECT**.
- ab1nodam band seed 515151: 42 vs 43 (9, 10).

## Plateau escalation step 2: mechanics sweep — global upgrade order
g_iter1 buys ATTACK (r600) > HEALING (r1200) > CAPTURING (r1800). Arms (C.UPGRADE_ORDER): up1 healing first, up2
capturing first (enemy-dropped flags return after 25 rounds, our carriers move at +12). Band, 2 seeds, queued.

### Ablation results (band, seeds 515151 + 616161, paired vs the g_iter1 control)
| removed from g_iter1 | gained-lost | reading |
|---|---|---|
| dam-front traps (ab1nodam) | 20-16 | ~0 (slight lean to removing) |
| flag trap ring (ab2noring) | 20-19 | ~0 |
| idle-bank stun traps (ab3nofloat) | 19-20 | ~0 |
| combat stun traps (ab4nocombat) | 16-26 | **load-bearing** (p ~ 0.16) |
Of g_iter1's trap habits only combat stun traps carry value; setup and bank traps are worth about nothing either way.
- up1 (healing upgrade first) band seed 515151: 38 vs 43 (8, 13).

## Iteration 9 (queued) — push on the one load-bearing trap habit
Combat stun traps are load-bearing (ablation 16-26); top bots out-trap us in fights and we trigger more of theirs
(53 vs 32 by r400). Arms on the g_iter1 base, band 2 seeds each (combattraps): ct1 STUN_ENEMIES_MIN 3 -> 2;
ct2 -> 1 with TRAP_RESERVE 0; ct3 aim at the nearest enemy (the one about to step in) instead of the centroid.
- Upgrade order: up1 (healing first) 22-26 over 2 seeds; up2 (capturing first) seed 1 4-15. ATTACK first stays.
- up2 (capturing first) seed 2: 10-18; total **14-33 (p ~ 0.005)**: buying ATTACK first is worth a lot. The most
  significant reading of the plateau: the attack upgrade is a strong lever (it cannot be bought before r600).
- ct1 (combat traps from 2 enemies) seed 1: 8-12.

## Iteration 10 (queued) — skill specialisation (engine mechanic found by reading the code)
Engine (InternalRobot.incrementSkill): once any skill reaches level 4, the others stop gaining XP past level 3.
New reader mode `--levels`: in g_iter1's long games ~40 of our 50 ducks reach HEAL mastery first, so attack is
capped at level 3 (165 damage, ~18 cooldown) instead of up to level 6 (240 damage, ~8 cooldown: ~2.9x the damage
per turn). Our ducks average ~180 attacks in a long game, enough for level 6 (150 XP) if heal did not cap them.
Kuma shows the same cap; jonters.bling3 has 20 attack masters. The upgrade-order sweep also says damage is a
strong lever (capture-first 14-33).
C.ATTACKER_TENTHS: ducks with idx%10 below it never heal. Diagnostic sp5 vs g_iter1 DefaultSmall seed 4: 22 attack
masters vs 0, won on flags. Dose arms sp3/sp5/sp7, band 2 seeds each (specialise), after combattraps.
- ct1 (combat trap from 2 enemies): 8-12 and 12-10 -> 20-22. ct2 (from 1, no reserve): 7-11 and 9-7 -> 16-18.
  Quantity of combat traps is not the lever (they are load-bearing at the current amount).
- ct3 (combat trap toward the nearest enemy): 5-13 and 10-10 -> 15-23: worse; the centroid aim stays.
