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

### Iteration 10 — specialisation: REJECT (closed)
| attackers (never heal) | gained-lost vs control, 2 band seeds |
|---|---|
| 30% (sp3) | 19-22 |
| 50% (sp5) | 18-17 |
| 70% (sp7) | 12-30 |
In real band games attackers reach mastery only partly (4-13 of 25 in two kuma losses: deaths and the jail
penalty keep resetting attack XP), and fewer healers means more deaths and a lower level sum. Healing is valuable
(also T8); closed.

## Iteration 11 (queued) — army cohesion
C.GROUP_MIN: outside combat after setup, a duck with fewer than GROUP_MIN allies in view moves to the centroid of
the visible allies instead of pushing (not defenders, carriers or chasers). Arms gr4, gr8 on the band (cohesion).
Diagnostic gr8 vs g_iter1 DefaultMedium seed 4: won on level sum r2000, 0 pickups vs 15 (maybe too passive).
- gr4 band seed 515151: 9-11.

## T12 (traced 2026-10-01) — flank raid on a lightly held flag
Survey vs the 16 that beat us: kills 224 vs deaths 226 (fights even), but they pick up our flags 17 times a game to
our 4, and they reach r200 with ~2720 crumbs (us 195), ~1 setup trap (us 27), no explosives. Traced CyrilSharma on
Islands at its first pickup (r275): the armies fight centre-left while a column of ~8 raiders comes from the east to
our top flag (few defenders there); another group probes the bottom flag.
Adoption arm: C.RUSH_FLANK — the rush squad targets the enemy flag farthest from the army's target (the army takes
the flag nearest our spawn centroid), with the relay on. Diagnostic fl10 vs g_iter1 Islands seed 4: **won by CAPTURE
r1771, first capture r296, 84 pickups vs 11, kills 457-226**, 0 overruns. Dose arms fl6/fl10/fl15 on the band (flank).
- gr4 seed 2: 11-12 -> two seeds 20-23. gr8 seed 1: **4-18**. Cohesion hurts in proportion to how hard it is
  enforced: tempo and spread win flags (consistent with T12). Closed.

## Basic-capability census (owner prompt 56, 2026-10-01)
New reader mode `--capabilities` (crumbs gathered by r200/r400, first round in enemy territory, robots in the enemy
half at r250/r300, first enemy-flag sighting, pickups, captures, carrier deaths/rounds/moves, enemy carriers killed,
traps built/hit, mean alive) and tools/capability-census.sh / capability-summary.py. 700 g_iter1 games
(research/CAPABILITY_CENSUS.md). A first-pass bug (carrier deaths always 0: the flag's drop event precedes the death
record) was caught by a number looking odd and fixed, with invariant tests.
Vs the 15 bots that beat us: fights roughly even (212-220 in our losses); robots in the enemy half at r250 **10 vs
24** (flips to 18 vs 13 in our wins); first enemy-flag sighting r271 vs r232; pickups 4 vs 17; capture rate 0.06 vs
0.13; we kill their carriers 11 times a game, they kill ours 3; crumbs gathered by r400 3900 vs 4950.
Workflow wf_7819c1e0-5b6 (12 decomposers, 3 method designers, synthesis, 3 adversarial critics, revision) is
classifying the 12 tactics as elementary vs infrastructure-heavy against these numbers.
- fl10 band seed 616161: 13-10 -> two seeds 20-23 (neutral). fl6 seed 515151: 7-11.

### T12 flank-raid adoption: REJECT (dose curve)
| raiders | gained-lost, 2 band seeds |
|---|---|
| 6 (fl6) | 19-21 |
| 10 (fl10) | 20-23 |
| 15 (fl15) | see below; seed 1 2-16 |
A raid squad bolted onto g_iter1 does not pay, and taking 15 ducks from the main army costs heavily. The opponents'
flank raids presumably rest on other capabilities (a main army that holds with fewer bodies; twice our presence in
the enemy half): consistent with the owner's hypothesis (prompt 56).

## Tactic levels (owner prompt 56) — result and the new programme
Workflow wf_7819c1e0-5b6 (20 agents, 3 adversarial critiques) -> research/TACTIC_LEVELS.md (raw outputs:
research/tactic-levels-workflow.json). Answer: tactics can be graded, by TL-1 (symptom screen -> payoff graph of
below-par root capabilities -> delivery mini-block). None of the 12 is elementary; T4, T7 (root: setup crumb budget)
and T2 (root: flag pressure) are intermediate; T3, T5, T10 composite; T6, T8, T9 symptoms (retired); T1, T11, T12
undetermined. 10 of 11 Adoption arms never reproduced the opponents' state, so the ~25 neutral readings say little
about the tactics. Roots shared across tactics: setup crumb budget (bank 175 vs 2909 at r200 vs beaters), C11 presence
in the enemy half (10 vs 24 at r250), flag pressure (pickups 4 vs 17).
Defects found by the decomposers: (a) ducks idle at a broadcast hint (the hint is up to dist2 100 from the flag,
vision is 20; redrawn every 100 rounds); (b) the relay likely lets the dropper re-pick its own drop (219 drops per
capture); (c) Sym.observe is never called; (d) the T10 detour only runs with no enemy in view.
Programme: Phase 0 instrumentation; Phase 1 blocks 1 (setup budget + paced floor, C.BUDGET_V1) and 2 (hint sweep,
C.HINT_SWEEP); Phase 2 C11 presence, flag-pressure defence, fight traps on the freed bank; Phase 3 carrier protection.
Diagnostic b12 (both blocks) vs g_iter1 DefaultMedium seed 4: crumbs200 5240 vs 75, traps200 0 vs 25, fills200 2 vs
65, inEnemy250 23 vs 0, firstFlagSight r244 vs r331, kills 256-193; crumbs250 210 (spent on combat traps), 0 overruns.
Queued (blocks12): b12, b2hint, b1budget on the band (2 seeds) with census and survey of their replays.

### Closed-directions ledger (2026-10-01; re-open conditions per research/TACTIC_LEVELS.md)
| direction | measurement | kind | re-open when |
|---|---|---|---|
| flag relocation (T2 adoption) | 81 vs 84/110; 20-21 vs arch_rush10 | completed, engine-blocked | home defence away from spawn works |
| relay (T3) | 81 vs 84/110 | defective implementation | sparing hand-off with path distance (Phase 3) |
| setup digging (T4) | 84, 82 vs 84/110 | starved (median 0 digs) | block 1 base |
| bank + spend (T5) | c5bank 82, c6pair 82 vs 84/110 | starved | blocks 1, 3, 5 |
| aggression (T6) | e1 83, e2 81 vs 84 | symptom | - |
| water instead of stun (T7) | 175 vs 165/480 (p~0.24) | wrong usage | additive moat on block 1 base |
| retreat threshold (T8) | flat <= 300, worse above | symptom | - |
| more defenders / wider alert / fortress / flag-tile stun | 9-25, 12-31, 16-29, 19-23 | refuted (bodies/traps at the flag) | a stated difference from all four |
| crumb detour (T10) | 21-22 | not reproduced | blocks 1 and 3 |
| specialisation (T11) | 19-22, 18-17, 12-30 | different mechanism | XP-gated specialist with a beater source |
| flank squad (T12) | 19-21, 20-23, 10-26 | cheap layer only | Phase 4 |
| micro v2 (smooth scoring) | 1-25, 1-26 | refuted | a scored micro built from logged fight states |
| cohesion | 20-23, gr8 4-18 | refuted | - |
| upgrade order heal/capture first | 22-26, 14-33 | refuted (ATTACK first is load-bearing) | - |
| re-grab loose flags in fights anywhere (b2rg) | 11-26 | refuted: regrabs +4.8 but each is a dead carrier, kills -37 | regrab on our half only; with CAPTURING |
| CAPTURING at r1200 instead of HEALING (b2u) | step 5(a): late captures 0 vs 3 over 3 cells | refuted before a gate: HEALING at r1200 is load-bearing | - |
| flag-pressure family (re-grab, carrier step/heal, intercept, dest camp, escort, escort+re-grab pair) | no delivery or 11-26 | closed by the 3-rejects rule | shared carrier tracking with prediction, or a dedicated group |
| hold a front line instead of marching on flags (S1) | stage A 0/7, 1/7 | refuted | - |
| forward drift in the hold branch (g1drift 40/80) | 40: delivery fail; 80: band 11-24 (p 0.04), kills -62 | refuted | - |
| combat-trap quantity/aim | 20-22, 16-18, 15-23 | priced ~0 at today's bank | block 1 base (block 5) |
- Phase 0 instrumentation (part 1): `--capabilities` adds postPickups split into firstGrabs (flag taken from its
  default spot), regrabs (after a carrier death) and relayPickups (after a voluntary drop), the mean distance of a
  carrier's death from its own team's nearest spawn centre, and robots within 2 tiles of the dam at r199. Tested
  (kinds sum to post-setup pickups). Examples: CyrilSharma (Islands) 5 first grabs + 5 re-grabs, carriers die ~19
  tiles from their spawn; hsmalladi (DefaultSmall) 6 relay pickups of 12.

### Phase 1 blocks — first readings (band, 2 seeds, paired capability deltas via tools/arm-deltas.py)
| arm | wins gained-lost | key capability deltas vs control (all cells) |
|---|---|---|
| b12 (budget + hint) | 12-25 | crumbs200 +5398 (27.7 SE), traps200 -42, fills200 -51, gathered400 -1455 (-6.8 SE), firstFlagSight +15, kills -61 (-2.7 SE) |
| b2hint (hint sweep) | 21-25 | firstFlagSight -1 +- 10 (no change), everything else ~0 |
| b1budget (budget) | 16-26 | crumbs200 +5409, gathered400 -1373 (-6.6 SE), firstFlagSight **+40 (3.0 SE later)**, kills -60 (-2.6 SE), crumbs250 only +32 |
Reading: the budget block delivered its signature (bank at r200) but cutting setup FILLS broke navigation over water:
arrival and flag sighting ~40 rounds later, ~1400 fewer crumbs gathered by r400, worse fights; and the bank drained
on post-dam fills anyway. Setup fills are C2 infrastructure, not waste. Hint idling (block 2) was not costing
anything measurable: closed. Next: b1v2 = no setup traps (dam + ring) and the paced floor, fills unrestricted (queued).
New tool: tools/arm-deltas.py (paired deltas with SE per capability column, all and beater cells).

## Process correction (owner prompts 66-67, 2026-10-01)
The owner flagged that arms went to ladder/band tests without reproducing the intended behaviour. Correct: step 5
(Diagnose) had shrunk to "one game where a counter fires", and 10 of 11 adoption arms were judged on wins without
delivering. Now enforced in tools: tools/delivery-gate.sh (24-cell mini-block, census + survey, pre-registered
checks -> gauntlet/delivery-<arm>.PASS/FAIL, logic in tools/delivery-check.py, unit-tested) and tools/band-test.sh
(refuses without PASS; override only with a written NO_DELIVERY_REASON). TRAINING_ALGORITHM §3 step 5, CLAUDE.md
rule 13. The running b1v2 band job (started 17:44, no delivery check) was stopped by PID.

### b1v2 (block 1 v2: no setup traps, paced floor, fills unrestricted) — step 5
(a) Logged diagnostic vs g_iter1, DefaultMedium seed 4: traps200 0 vs 26, crumbs200 1940 vs 75, fills200 112 vs 62,
    inEnemy250 27 vs 14, firstFlagSight r330 vs r594, kills 214-150, 0 overruns. Fires.
(b) Pre-registered delivery checks: median:traps200<=6, median:crumbs200>=1500, median:fills200>=20 (bank target
    1500, not 2500: fills spend part of it by design). Band test only if (b) passes (queued as one job).
(b) Delivery gate: **PASS** (24 band games: traps200 median 0, crumbs200 4780, fills200 65.5).
Band test (seeds 515151, 616161), paired vs the g_iter1 control: wins **24 gained, 20 lost (+4, non-inferior)**.
Capability deltas (all cells): crumbs200 +4662 (23.4 SE), traps200 -42, fills200 +7 (fills preserved), stun400 +10
(10 SE), **inEnemy250 +3.05 (5.7 SE; +3.40 on beater cells)**, captured +0.12 (1.8 SE), gathered400 +37 (0.4 SE),
kills/deaths ~0. Block 1 v2 **ADVANCES to the stack as base B1** (own metric >= 2 SE, wins non-inferior). Not a new
incumbent: that needs a power-sized test of the stack (~8 seeds). Next on B1: block 5 (fight traps on the freed bank),
T4 retry (B1 bank condition met), flag-pressure lever from the Phase 0 census.

## Block 4 (flag pressure) — Phase 0 decision and the z1hold arm on B1
Phase 0 census of g_iter1 band games vs the beaters (333 games, means per game): their post-setup pickups 20.9 =
first grabs 7.2 + **re-grabs after a carrier death 9.8** + relay pickups 3.9; ours 5.4 = 4.4 + 1.0 + 0. Their carrier
deaths 14.5 per game, ~23.5 tiles from their own spawn (mid-field, not at their spawn). Dam staging at r199: us 41.9,
them 38.1 (at par). Decision rule (TACTIC_LEVELS §3 block 4): deaths do not cluster at their spawn; re-grabs >= 30% of
first grabs -> arm z1hold: when our flag lies dropped within dist2 20, ducks converge on it and target enemies in
pickup range of it right after carriers. Counter `zh` (indicator compacted: dead counters dropped).
Step 5(a): vs arch_rush10 (DefaultSmall s4) it never picked up our flags: no chance to fire, games identical (not a
valid diagnostic). Vs arch_rush (s3): zh fires; their re-grabs 13 vs 11 (b1v2), first grabs 10 vs 16, our pickups 12
vs 24 (the hold pulls bodies home). One game. Step 5(b) pre-registered: mean:enemyRegrabs<=6.4 (control 9.19 on band
games, -30%), mean:postPickups>=5.4 (control 7.74, guard). New census columns enemyRegrabs, enemyFirstGrabs.

## Idle filler redesigned (owner prompt 73)
Audit: 17 filler blocks (~2000 games of g_iter1) had been played and none recorded; the post-block step was manual.
Now: (1) all 20 unrecorded g_iter1 band/filler runs recorded -> **g_iter1 1853 +- 20 over 2350 games**; (2) the filler
plays g_iter1 and the current stack (b1v2) on the SAME fresh seed, 40 games each, with census
(tools/filler-pair.sh), so idle time accumulates the stack's power-sized paired test (~8 seeds x 120 = 960 games);
(3) tools/collect-fillers.sh records them and prints the running tally (tools/filler-tally.py, tested), run at every
task check (CLAUDE.md rule 14); (4) scrim.sh RUNTAG tags filler runs (*-fill<seed>).
- z1hold delivery gate (24 band games): **FAIL**: enemy re-grabs mean 9.0 (target <= 6.4), our pickups 5.2 (guard
  >= 5.4). band-test.sh refused; no ladder time spent. Next: trace why ducks do not stop re-grabs within the 4-round
  window (do they reach the dropped flag in time?).

### Block 4 v2 — escort-first targeting (b1z2)
Trace of a z1hold delivery loss (CyrilSharma, ORV): every time we kill their carrier an adjacent raider re-picks the
flag within 1-5 rounds (r252 -> r253, r255 -> r257, r261 -> r262, captured r313): the pack always has an escort in
pickup range, so converging on the dropped flag (z1hold) cannot work. Lever: targeting. C.Z2ESCORT: when an enemy
carrier is in view with escorts within dist2 2, hit an escort in range first (lowest HP), unless the carrier is within
dist2 36 of an enemy spawn centre. Counter `es`. (Also seen: arch_rush's relay is the predicted dropper-re-picks defect.)
Step 5(a) vs arch_rush DefaultSmall s3 (same cell as b1v2): es fires; enemy re-grabs 1 vs 11, enemy first grabs 3
vs 16; won by CAPTURE r1277 (3 captures) vs b1v2's level-sum game with 0. One game. Step 5(b) pre-registered as for
z1hold: mean:enemyRegrabs<=6.4, mean:postPickups>=5.4; band test only on PASS.
- b1z2 step 5(b): **FAIL** (enemyRegrabs mean 6.5 vs <= 6.4 pre-registered; our pickups 7.9 ok). Band test refused.
  Not rescued by moving the bar. Fix: escorts were defined as within dist2 2 of the carrier, but a raider within
  dist2 8 can step in and pick up in the same turn -> C.Z2_ESC_R2 = 8 (arm b1z2b). Step 5(a) vs arch_rush s3: es
  fires; enemy re-grabs 4 (b1v2 11), our pickups 5; level-sum win. Same pre-registered checks for step 5(b).
- Filler tally (b1v2 vs g_iter1): 2 seeds, 79 paired games, 6-6.
- b1z2b step 5(b): **FAIL** (enemyRegrabs mean 9.2 vs <= 6.4; our pickups 6.7 ok). Band test refused.

### Instrument fix: paired delivery checks (2026-10-01, pre-registered BEFORE any re-run)
The absolute bar (arm mean on 24 cells vs the band-wide g_iter1 mean 9.19) was (1) noisy: per-game re-grabs vary so
a 24-game mean has SE ~1.6, so a 30% cut is ~1.7 SE and b1z2 6.5 / b1z2b 9.2 on the same cells are within noise of
each other; (2) the wrong comparison: the z-arms are built on B1 (b1v2), not g_iter1. New: delivery-gate BASE=<bot>
plays the base's mini-block on the same seed once (cached) and `rel:` checks compare arm vs base cell by cell
(delivery-check.py, tested). Pre-registered for the escort arms, decided now, before the runs:
`rel:enemyRegrabs<=0.7 rel:postPickups>=0.8` with BASE=b1v2. Both b1z2 and b1z2b are re-gated under it; the band test
runs only on PASS. The earlier absolute FAILs stand in the log.
- Paired re-gates (BASE b1v2 mini-block, 24 shared cells): b1z2 **FAIL** (re-grabs 5.38 vs 8.25 ok; our pickups
  5.50 vs 7.88 below the 0.8 guard); b1z2b **PASS** (re-grabs 4.83 vs 8.25, -3.42 +- 2.07; our pickups 6.92 vs 7.88).
  b1z2b band test running. Note: b1z2b's arm mini-block re-played on the same cells read 9.2 before and 4.83 now
  (engine seeds differ per play): the single-mean bar was noise; paired is the right instrument.
- Filler tally (b1v2 vs g_iter1): 6 seeds, 238 paired games, 12-18 (-1.1 SE).

### b1z2b (escort-first, radius 8) on the band — ADVANCES to the stack as B2
Wins (2 seeds): vs its base b1v2 17 gained, 18 lost (non-inferior); vs g_iter1 22-19. Own metric vs b1v2, paired on
234 band cells: **opponent re-grabs -4.79 +- 0.64 (-7.5 SE; base 11.15)**, opponent pickups -5.0 (-4.8 SE), opponent
first grabs -0.2 (n.s.), **opponent captures +0.01 (n.s.)**, our kills -24 (-1.2 SE), inEnemy250 ~0. Advances under
the two-tier rule (own metric >= 2 SE, wins non-inferior). Caveat recorded: fewer re-grabs did not yet turn into
fewer captures against us; the flag-pressure root is not closed. Stack: B2 = b1v2 + Z2ESCORT (radius 8).
Filler now plays g_iter1, b1v2 and b1z2b on each seed (tally per candidate). B1 tally so far: 7 seeds, 278 paired
games, 14-22 (-1.3 SE).

## T4 retry on B2 (b2dig5: SETUP_DIGS 5, DIG_RESERVE 1000)
Step 5(a) vs b1z2b, DefaultMedium s4: digs200 92 vs 0 (fires), level200 5 vs 0, fills200 111 vs 111, build mastery 0
(no build-4 cap); but crumbs200 190 vs 1970 (92 digs ~1840 crumbs): the dose eats B1's bank. Won on level sum.
Step 5(b) pre-registered (BASE=b1z2b): fire:digs200>0>=0.9 mean:level200>=3 rel:fills200<=1.1 rel:crumbs200>=0.5.
If the bank guard fails, the next arm is a lower dose, not a looser bar.
- b2dig5 step 5(b): **FAIL**. digs fire 92%, level200 15.7 (ok); but fills200 118 vs base 74 (+44 +- 9) and
  crumbs200 1133 vs 4940. Mechanism: our Nav fills water whenever the direct step and its two neighbours are blocked
  (Nav.fillToward) instead of taking the diagonal detours a checkerboard leaves open, so our own digging makes our own
  ducks spend 30 crumbs per tile. T4 is now blocked on C2 navigation (water-aware routing), not on the bank; a lower
  dose would not fix it. Not band-tested.
- Filler tallies vs g_iter1: B1 (b1v2) 11 seeds 32-31 (+0.1 SE); B2 (b1z2b) 3 seeds 12-8 (+0.9 SE).

## C2 navigation: avoidable fills (FILL_SMART) on B2
Measurement first (counter in Nav.fillToward, B2 build b2meas vs g_iter1, DefaultMedium s4): 128 fills, of which 33
(26%) had a free land step that did not lose distance to the target. Arm b2fs (C.FILL_SMART: take that step instead
of filling). Step 5(a), same cell: fills 106 vs 128, crumbs200 2180 vs 1940, firstFlagSight r299 vs r330, inEnemy250
24 vs 27, deaths 159 vs 247 (won vs lost). Step 5(b) pre-registered (BASE=b1z2b): rel:fills200<=0.9
rel:crumbs200>=1.0 rel:inEnemy250>=0.9.

### 2026-10-02 — b2fs (B2 + FILL_SMART) band verdict: ADVANCES as B3
Band, 2 seeds, paired against B2 (b1z2b) on identical cells, 234 games.
- Wins: seed 1 41 -> 39, seed 2 44 -> 45; net -1 (non-inferior, bar is -5).
- Own metric (survey): fills200 -8.9 +- 0.7 (-12.2 SE); crumbs200 +265 +- 29 (+9.2 SE); stun400 +3.4 (+3.6 SE).
  crumbs250 flat: the saved crumbs are spent by round 250, not hoarded.
- Census: inEnemy250 -0.5 (-0.9 SE). The delivery-block gain (+5.5) did not hold on the band. Everything else within 1.5 SE;
  carrierDeathDist +1.1 (+2.1 SE, n=168).
- Stack is now B1 b1v2 -> B2 b1z2b -> B3 b2fs. Filler now pairs g_iter1 vs b1v2,b1z2b,b2fs.
- Next: T4 setup-digging retry on B3, now that the navigator avoids needless fills (b2dig5 failed because we filled our own digs).
Ops: local disk hit 100% (gauntlet replays 2.6 GB). 2026-09-30 replays copied to the VM at ~/archive/bc24-local (1111 files, count verified);
local copies deleted with the user's approval (prompt 84); 1.5 GB free afterwards.

## T4 retry on B3 (b3dig5: B3 + SETUP_DIGS 5, DIG_RESERVE 1000)
Step 5(a) vs b2fs, g_iter1 opponent, DefaultMedium s4: digs200 101 vs 0 (fires); fills200 102 vs 104 (the self-fill
problem that failed b2dig5, +44 fills, does not appear with FILL_SMART); level200 1 vs 0; crumbs200 229 vs 2180 (the dose
still eats the bank); our pickups 0 vs 8. Both won on level sum.
Step 5(b) pre-registered (BASE=b2fs), same bars as b2dig5: fire:digs200>0>=0.9 mean:level200>=3 rel:fills200<=1.1
rel:crumbs200>=0.5. If only the bank guard fails, the next arm is a lower dose (SETUP_DIGS 2), not a looser bar.
- b3dig5 step 5(b): **FAIL** (2 of 4 bars). digs fire 92%, level200 15.4 (ok); fills200 99.3 vs 58.3 (+41 +- 10) and
  crumbs200 1261 vs 5014 (-3753 +- 604). FILL_SMART did not stop the self-fills; the single diagnostic cell (+0 fills)
  was not representative. Not band-tested (band-test.sh refused, as designed).
- Measurement (new tools/fill-origin.py: every fill classified by the tile's origin), the 24 delivery games:
  b3dig5 fills 99.3 = own digs 45.9 + natural 53.4; B3 fills 58.3 = natural 58.2. Half of our ~92 digs are filled back by
  our own ducks, median ~25 rounds later (later traffic, not the digger), at 20+30 crumbs per round trip for nothing.
  Natural fills are unchanged, so the cost is purely siting: setupDig digs the first even tile next to the duck, often
  on the lane every duck walks from spawn to the dam.
- Next arms (siting, dose kept at 5): b3site1 DIG_SITE=1 (only tiles with >= 3 wall/off-map neighbours) and
  b3site2 DIG_SITE=2 (only tiles farther from the enemy flag than our nearest spawn centre). Step 5(a) on Battlecode24
  vs g_iter1 (the delivery block's worst self-fill map: 128 own fills).
- Step 5(a) siting, Battlecode24 s4 vs g_iter1 (digs200 / own-dig fills / fills200 / crumbs200 / level200):
  b3dig5 235/133/193/1867/47; b3site1 (wall-hugging) 198/88/147/6607/34; b3site2 (behind spawn) 163/46/112/5515/26.
  Siting alone halves the self-fill share (57% -> 28%) but leaves 46 per game: not enough for the fill bar.
- New arm b3own = b3site2 + NO_FILL_OWN (Nav.fillToward, setup only: skip a tile matching our dig signature, even
  parity and behind our spawn, unless 8 turns without progress; the checkerboard keeps a diagonal route). Step 5(a),
  same cell: 139/8/65/6751/22; ownSkips counter fires (indicator f../../n); bytecode max 15.1k, 0 turns at limit.
  Lost this one game on flag captures (single game; the block decides).
  Step 5(b) pre-registered (BASE=b2fs), the same four bars: fire:digs200>0>=0.9 mean:level200>=3 rel:fills200<=1.1
  rel:crumbs200>=0.5.

### 2026-10-02 — warning on B3 (b2fs) from the filler
On the 3 filler seeds where all four builds played the same 120 cells: B3 vs g_iter1 3-15; B2 vs g_iter1 10-11;
B3 vs B2 6-17 (net -11, -2.3 SE); B1 vs g_iter1 5-13 (these seeds are hard on B1 too). Band + filler together, B3 vs B2:
24-36 (-1.5 SE). Not conclusive; B3 advanced on non-inferiority (band net -1 of 234).
Pre-registered demotion rule: at 6 filler seeds (240 paired games) of B3 vs B2 (`tools/filler-tally.py b1z2b b2fs`),
if net <= -2 SE, B3 is demoted (FILL_SMART leaves the stack), the filler candidate list drops b2fs, and the digging
arms are re-based on B2 (NO_FILL_OWN does not depend on FILL_SMART). Otherwise B3 stays.
- b3own step 5(b): **PASS** (BASE=b2fs, 24 games): digs fire 92%; level200 11.9; fills200 59.3 vs 58.3 (+1.0 +- 4.1,
  the self-fill cost is gone: b3dig5 was +41); crumbs200 2874 vs 5014 (-2140 +- 500; above the 0.5 bar, the dig cost
  itself, ~140 digs x 20). Band test started (gauntlet/20261002-031317-scrim-b3own = seed 1).

### 2026-10-02 — B3 band profile: where the games are lost (census/survey of the b2fs band block, 234 games)
- Fights are won: kills 453 vs 256, stun400 82 vs 63 (block 5's target of 72 is already met through B1's bank:
  crumbs200 5453 vs 3517, spent to 261 by r250 while opponents still hold 3510).
- Flags are lost: captured 1.11 vs 1.94; we lose 106 of 151 losses by CAPTURE. First grabs are close (6.3 vs 7.7);
  persistence is not: re-grabs 1.9 vs 6.8, relay pickups 0 vs 4.4, carrier deaths per pickup 0.85 vs 0.62.
- Rule check (RULES.md): a flag dropped at a carrier's death returns after 4 rounds and cannot be picked up the
  round it drops, so a re-grab needs a duck within reach in rounds 1-4. Our Micro.fight never moves toward a loose
  flag (it only closes on enemy carriers).
- Arm b2rg = B2 + REGRAB (Micro.fight: a visible enemy flag on the ground within dist2 13 becomes the movement goal,
  just below an enemy carrier; pick it up before striking; counter rg in the indicator). Based on B2, not B3, while
  B3's demotion rule is pending. Step 5(a): DefaultMedium and DefaultLarge s4 vs g_iter1, against b1z2b.
- Also fixed: research/census-b2fs.csv and survey-b2fs.csv had been committed empty (copied while the disk was full).
- b2rg step 5(a) vs g_iter1, s4 (pickups / first grabs / re-grabs / captured; b2rg vs b1z2b):
  DefaultLarge 19/4/15/0 vs 8/6/2/0; DefaultMedium 12/5/7/0 vs 4/2/2/0. rg counter fires (up to rg2 per duck).
  Every carrier still dies (carrierDeaths = pickups) against the g_iter1 defence; both games lost on level sum as before.
  Step 5(b) pre-registered (BASE=b1z2b): rel:regrabs>=1.5 rel:pickups>=1.0 rel:captured>=0.8 rel:inEnemy250>=0.9
  (captured bar loose: its 24-game noise is about +-0.25 of the mean).
- b3own band (2 seeds, paired vs B3 b2fs, 234 games): **REJECTED**. Wins 39->38 and 45->33 (16 gained, 29 lost,
  net -13; bar -5). Delivery held on the band: digs200 +91 (+28 SE), level200 +10.5 (+16.5 SE), fills200 +3.2 only.
  The cost: crumbs200 -2219 and stun400 -20.8 (-14.6 SE); crumbs250 equal (the bank is spent by r250 either way, so
  every dig crumb is a stun not built). captured -0.13 (-1.9 SE), inEnemy250 -1.0 (-1.8 SE).
  Reading: T4's level-sum and trap-discount benefit does not pay for 20 stuns; stuns are load-bearing (ablation 16-26).
  T4 is closed on the current economy. Reopen only if the bank is not spent by r250 (it is, on stuns), or for a
  dig that also costs nothing in stuns (no such variant exists: each dig is 20 crumbs).
### 2026-10-02 — ops check after a session reset (owner prompt 89)
Driver: repo clean and in sync with origin; unit tests OK; cron loop 9c63c476 (task check, 30 min) alive. VM
battlecode-dev2 RUNNING (up 1 d 12 h, load ~24, disk 86%); queue runner alive 23 h; filler running; b2rg queued.
Driver disk 98% again (the b3own band replays). tools/vm-collect.sh now leaves replays on the VM unless REPLAYS=1.
- Ops (owner prompt 92, standing permission to delete what we no longer need, never anything on GitHub): all 928
  local replays deleted after a path+size check against the VM (gauntlet/ and ~/archive/bc24-local). Driver disk
  98% -> 92% (2.4 GB free). gauntlet/ is gitignored; nothing tracked was touched.
- Filler, 4 seeds: B3 vs B2 10-21 (-2.0 SE); B1 vs g_iter1 now 69-86 over 1034 (-1.4 SE); B2 vs g_iter1 55-47 (+0.8 SE).
- b2rg step 5(b): **PASS** (BASE=b1z2b, 24 games): regrabs 7.62 vs 1.46 (+6.2 +- 1.3); pickups 14.5 vs 7.6;
  captured 1.12 vs 1.12 (+0.00 +- 0.17); inEnemy250 15.2 vs 12.4. Band test started (gauntlet/20261002-04*-scrim-b2rg).
  Pickups doubled but captures did not move: the extra carriers die before home. Conversion per pickup is the gap.
- Filler, 5 seeds: B3 vs B2 15-26 (-1.7 SE).

### 2026-10-02 — C9 carrier protection, first arm (b2rgc = b2rg + CARRY_SAFE + CARRIER_HEAL)
Rules (RULES.md): a carrier cannot act, only move; its move cooldown is +20 (two rounds a tile; 12 with CAPTURING).
Duck.carryFlag stepped by Nav.moveTo with no threat term; Micro.tryHeal healed the lowest HP, carrier or not.
- CARRY_SAFE: with enemies in view, among steps that shorten the way home, the one with the fewest enemies within
  dist2 10, only when safer than the straight step (counter safeSteps).
- CARRIER_HEAL: a hurt allied carrier in heal range is healed before anyone else (counter carrierHeals).
- Indicator: "zh" (Z1HOLD, off in the stack) replaced by "cs<safeSteps>/<carrierHeals>".
Step 5(a): DefaultLarge and DefaultMedium s4 vs g_iter1, against b2rg (19 and 12 pickups there, every carrier died).
Noted for later: CAPTURING also stretches a dropped flag's return from 4 to 25 rounds, which multiplies the re-grab
window; capture-first lost 14-33 before re-grabbing existed, so it is worth a retry on a REGRAB base.
- b2rgc step 5(a), DefaultLarge / DefaultMedium s4 vs g_iter1 (b2rgc vs b2rg): counters fire (safeSteps up to 9 per
  duck, carrierHeals up to 4), but the intended effect does not appear: carrier deaths per pickup 1.0 in all four games,
  0 captures; rounds carried per pickup 5.8 vs 8.9 and 3.5 vs 4.4; moves carried per pickup 2.7 vs 3.6 and 1.1 vs 1.7.
  Pickups doubled again (38 vs 19, 17 vs 12). carrierDeathDist (to the carrier's own nearest spawn) 40.8 vs 33.2:
  more pickups deep in their base. Reading: our carriers die within ~3 moves of a pickup, inside the enemy's respawn
  zone; no step choice or heal saves a carrier there. **Parked without a delivery block** (step 5: the behaviour,
  not just the counter, has to show).
- The lever that fits this: CAPTURING (dropped flag returns in 25 rounds, not 4; carrier move cd 12, not 20). On the
  B3 band 52% of games pass r1200, 82% pass r600. b2rg's delivery games: 56 of 349 enemy-flag pickups and 8 of 27
  captures came after r1200. Arm b2rgu = b2rg + UPGRADE_ORDER 3 (ATTACK r600 > CAPTURING r1200 > HEALING r1800;
  ATTACK first is load-bearing, so it stays). New census columns regrabsLate / capturedLate (after r1200), tested.
  Step 5(a) on DefaultLarge: no enemy-flag pickup after r1200 by either build, so the cell cannot show it; re-run on
  Tunnels and Battlecode24 vs g_iter1 (the delivery block's maps with the most late pickups).
- b2rg band (2 seeds, paired vs B2 b1z2b, 234 games): **REJECTED**. Wins 41->35 and 44->35 (11 gained, 26 lost,
  net -15; bar -5). regrabs +4.8 (+9.1 SE), pickups +4.8, but carrierDeaths +4.8 (+5.9 SE) and captured -0.04:
  every extra re-grab ends in a dead carrier. kills -37 (-1.9 SE): a duck holding a flag cannot fight, so re-grabbing
  inside their base takes fighters out of the fight they were winning. First grabs and enemy grabs unchanged.
  Ledger: "re-grab anywhere (REGRAB r2 13)" closed. Open variant: re-grab only flags that have reached our half
  (a carrier there has a chance), or only with CAPTURING (b2rgu, now judged against B2, not b2rg).
- Arm b2rgh = b2rg + REGRAB_HALF (re-grab only loose flags nearer our spawn centres than theirs, by Sym's mirror).
  Step 5(a) queued behind b2rgu's diagnostics (driver plays one game at a time, ~19 min each on large maps).
- b2rgu step 5(a), Tunnels / Battlecode24 s4 vs g_iter1 (b2rgu vs b2rg): CAP bought at r1200 as intended. Tunnels:
  b2rgu 1 late capture (0 for b2rg), the chain r1418 pickup, r1426 drop, r1428 re-grab, r1432 drop, r1433 re-grab,
  r1446 capture: re-grabs within 1-2 rounds (the 4-round window would have done) but the last carrier went 11 tiles in
  13 rounds (~22 without CAPTURING's cd 12). Battlecode24: no late re-grab by us; g_iter1 captured 2 vs 1.
  Reading: the useful part of CAPTURING is carrier speed, and ordinary pickupFlags already re-grabs within 1-2 rounds,
  so the upgrade does not need REGRAB (rejected). b2rgu parked; new arm **b2u = B2 + UPGRADE_ORDER 3** (one switch).
- Tools: tools/delivery-gate.sh re-censuses a cached base run when a pre-registered check names a column the cache
  predates (regrabsLate/capturedLate). New tools/diag-batch.sh: step 5(a) games in parallel through the VM queue,
  own builds only as opponents (refuses others). Batch u-h queued: b2u vs b1z2b on Tunnels, Battlecode24,
  DefaultLarge; b2rgh vs b2rg on DefaultLarge, DefaultMedium (all s4 vs g_iter1).
- B3 demotion rule applied at 6 filler seeds (as pre-registered): B3 vs B2 16-29, net -13, **-1.94 SE**: above the
  -2 SE line, so B3 is **not demoted** by the rule's letter. The 7th seed reads 20-35 (-2.02 SE); band + filler together
  38-54 (-1.7 SE). B3 stays in the filler; the stack decision moves to the power-sized test (~960 paired games).
  New arms are built on B2 (b2u, b2rgh) because B3's value is unresolved.
- Filler vs g_iter1: B1 72-97 over 1114 (-1.9 SE); B2 64-59 over 757 (+0.5 SE); B3 11-32 over 279 (-3.2 SE). The stack
  has not yet beaten the incumbent g_iter1 on paired games; B2 is level with it.
- Filler B2 vs B1: 83-56 over 757 paired games (**+2.3 SE**): Z2ESCORT is a real gain on top of B1. B1 itself vs g_iter1
  is 72-97 (-1.9 SE). So B1's budget change (BUDGET_V1) looks negative and Z2ESCORT positive. New arm **g1z2** =
  src/bot (behaviourally g_iter1) + Z2ESCORT r2 8, without BUDGET_V1. Step 5(a) batch queued (g1z2 vs the g_iter1
  mirror on DefaultMedium, DefaultLarge, Tunnels s4), plus a byte-identity control g1copy (snapshot of src/bot, all
  switches off) to confirm src/bot still plays as g_iter1 after today's switch additions.
- Identity control: g1copy (snapshot of src/bot, all switches off) reproduces the g_iter1 mirror exactly on
  DefaultMedium and Tunnels s4 (every census and survey value equal). src/bot is still behaviourally g_iter1.
- b2u step 5(a) (b2u vs b1z2b, s4 vs g_iter1; captured / capturedLate): Battlecode24 0/0 vs 2/2, DefaultLarge 0/0 vs
  0/0, Tunnels 0/0 vs 1/1. The builds are identical to r1200; after it, swapping HEALING for CAPTURING cost the late
  captures instead of adding them. **Closed without a delivery block**: HEALING at r1200 is load-bearing for late fights.
- b2rgh step 5(a): DefaultLarge identical to B2 (no loose flag on our half, never fires); DefaultMedium regrabs 5,
  pickups 13, carrier deaths 13, captured 0 (g_iter1 1). Fires, but no conversion shows; **parked**.
- g1z2 step 5(a) (vs the g_iter1 mirror): escort hits fire (es up to 8 per duck; 20 and 14 ducks on DefaultLarge and
  DefaultMedium, 1 on Tunnels). Enemy re-grabs small either way (g_iter1 re-grabs little). Step 5(b) pre-registered
  with BASE=g_iter1, the bars B2's escort block passed: rel:enemyRegrabs<=0.7 rel:postPickups>=0.8. Queued with the
  band test.
- Filler: B3 vs g_iter1 now 11-35 over 319 (-3.5 SE).
- g1z2 step 5(b): **FAIL** (BASE=g_iter1): enemyRegrabs 8.33 vs 11.25 (-2.9 +- 2.3; ratio 0.74, bar 0.7); postPickups
  6.04 vs 6.46 ok. Not band-tested. Next arm with the same bars, not a looser bar: g1z2w (escort radius dist2 13,
  untried; 2 failed, 8 passed on B1). Step 5(a) batch queued.
- Filler: B2 vs g_iter1 72-72 over 877 (0.0 SE); B1 77-102 over 1234 (-1.9 SE); B3 14-37 over 359 (-3.2 SE).

### 2026-10-02 — how they capture: the carrier walks home unopposed (new --defense mode, B3 band, 234 games)
New `replay-dump --defense` (one line per post-setup flag trip: defenders near the flag at pickup, mean chasers within
dist2 20 of the carrier per round, outcome; tested) and tools/defense-profile.py. Raw trips: research/trips-b2fs.csv.
- Capture losses (106 of 151 losses): their first pickup median r268, first capture median r365, end median r749.
- Their trips on our flags: 4402, 10.3% captured (ours on theirs: 13.8%). First grabs with no defender within dist2 20
  at the pickup: 18%, captured 16%; with 1-2 defenders 5%; with 3+ 9%.
- Their 453 captured trips: 64% start from a re-grab or relay, not a first grab; median 39 rounds carried (~20 tiles);
  **47% had on average under 0.5 of our ducks within dist2 20 of the carrier**; failed trips last a median 3 rounds.
  We kill most carriers at once; the ones that get clear walk home with nobody following.
- Code reason: Duck's chase (carrierTarget from the OF carry slots) runs only in fieldTarget, i.e. when no enemy is in
  view. A duck in any fight ignores the carrier alert.
- Arm g1icpt = g_iter1 + INTERCEPT (in a fight, a fresh carrier alert within dist2 225 makes the fight goal-directed
  toward the carrier or its interception point: Micro.fight(enemies, allies, goal), the rushers' pushing fight; strikes
  in reach and a visible carrier keep priority). Counter "ch<chases>/<intercepts>".
- New census columns chasers20 (our mean ducks within dist2 20 of an enemy carrier per carried round) and
  enemyCaptured (their captures, on our row, for rel: checks). Step 5(a) batch queued: 4 maps s4 vs g_iter1 mirror.
- g1z2w step 5(a): escort hits fire on 20 / 9 / 1 ducks (DefaultLarge / DefaultMedium / Tunnels) vs g1z2's 20 / 14 / 1.
  Radius 13 does not deliver more than radius 8, so it cannot be expected to clear the bar g1z2 missed. **Parked.**
- g1icpt step 5(a) vs the g_iter1 mirror (chasers20 / enemyCaptured / enemy pickups): Battlecode24 2.86/1/5 vs 1.71/2/6;
  DefaultMedium 4.77/0/7 vs 3.04/0/3; DefaultLarge 1.21/0/15 vs 1.36/0/5; Tunnels 5.30/1/10 vs 7.16/0/9. Intercepts fire
  (up to 88 per duck). Chasing up on 2 of 4; enemy pickups up on 2 (ducks pulled off flags?).
  Step 5(b) pre-registered (BASE=g_iter1): rel:chasers20>=1.2 rel:enemyFirstGrabs<=1.2. Queued with the band test.
- Filler: B3 vs g_iter1 19-44 over 439 (-3.1 SE).
- g1icpt step 5(b): **FAIL** (BASE=g_iter1): chasers20 2.40 vs 2.31 (+0.09 +- 0.43; bar x1.2); enemyFirstGrabs 7.67 vs
  8.00 ok. Intercepts fire constantly but do not put more ducks near carriers. Reason: the carrier alert is fresh only
  while someone sees the carrier, which is exactly when ducks are already near; the unopposed carriers (47% of their
  captures) are the ones nobody sees, and their alert is stale after CARRY_FRESH = 5 rounds. Not band-tested.
- Arm g1camp = g_iter1 + DEST_CAMP: a stale carrier alert (age > 5) still names the destination (enemy spawn centre
  nearest the last sighting). The carrier needs ~2 rounds a tile, so a duck that can reach that centre first waits there
  while age <= 2*dist + 10. Applies where carrierTarget applies (no enemy in view). Counter "ch../../<camps>".
  Comms.carriedAge/carriedLast added. Step 5(a) batch queued (4 maps vs g_iter1, with g1copy mirrors).
- Filler: B2 vs g_iter1 78-76 over 957 (+0.2 SE); B1 87-113 over 1354 (-1.8 SE); B3 22-47 over 479 (-3.0 SE).
- g1camp step 5(a) vs the g1copy/g_iter1 mirror (enemyCaptured, chasers20): Battlecode24 1 vs 2, 1.93 vs 1.71 (a carrier
  unseen for 45 rounds died at r1859 where the mirror's scored at r1829); DefaultLarge 0 vs 0, 0.82 vs 1.36;
  DefaultMedium 0 vs 0, 3.04 vs 3.29; Tunnels 1 vs 0, 1.30 vs 7.16. Camps fire (33-50 ducks per game). Tunnels trace:
  the carrier re-grabbed our flag at (12,21) and walked 12 tiles east to (23,22) in 28 rounds with no chaser; our ducks
  near the pickup were fighting (enemies in view), so neither carrierTarget nor campTarget ran. Not reproduced: not gated.
- INTERCEPT acts only inside fights, DEST_CAMP only outside them, and our ducks are nearly always in a fight: arm
  **g1icamp** = g_iter1 + INTERCEPT + DEST_CAMP (in a fight, a stale alert's destination within dist2 225 becomes the
  fight's goal). Step 5(a) batch queued (4 maps s4 + DefaultMedium/Tunnels s5 with g1copy mirrors).
- g1icamp step 5(a) vs mirrors (enemyCaptured, chasers20): Battlecode24 s4 1 vs 2, 1.55 vs 1.71; DefaultLarge s4 0/0,
  1.06 vs 1.36; DefaultMedium s4 1 vs 0, 2.75 vs 3.04; Tunnels s4 0/0, 7.00 vs 7.16; DefaultMedium s5 0/0, 7.64 vs 5.25;
  Tunnels s5 0/0, 9.67 vs 7.43. Enemy captures 2 vs 2. **Not reproduced; not gated.** Interception family (g1icpt,
  g1camp, g1icamp) closed for now: the unopposed carriers are not reachable by local rules that only see a fight.
- Mirror image of their method: escorts. New census column escorts20 (own robots within dist2 20 of an own carrier per
  carried round; tested). B3 band (232 games; 2 lost to a parallel-compile race): ours 4.85, theirs 6.76, with their
  pickups 20.0 vs 8.2 and captures 1.93 vs 1.12. Arm **g1esc** = g_iter1 + ESCORT_CARRIER (roadmap block 6's first
  step: in a fight, a visible own carrier within dist2 20 makes its next homeward tile the fight goal; counter "et").
  Step 5(a) batch queued (6 cells + a DefaultLarge s5 mirror).
- Filler: B1 vs g_iter1 97-126 over 1472 (-1.9 SE); B2 84-86 over 1075 (-0.2 SE); B3 33-55 over 597 (-2.3 SE).
- g1esc step 5(a) vs mirrors (escorts20; et fires on 16-31 ducks where a carrier exists): DefaultLarge s4 3.43 vs 4.44,
  s5 3.78 vs 5.20; Tunnels s4 3.79 vs 5.91, s5 3.21 vs 5.69; DefaultMedium s4 6.97 vs 6.51. Escort density went DOWN;
  wins 4/7 vs the mirrors' 6/7. Reading: the goal "the carrier's next homeward tile" (roadmap block 6) puts escorts in
  the carrier's path. **Not reproduced; not gated.** Variant g1esc2 (ESCORT_BEHIND: goal one tile behind the carrier, so
  escorts trail and never block) queued for step 5(a).
- g1esc2 step 5(a) vs mirrors (escorts20): DefaultLarge s4 4.43 vs 4.44, s5 5.00 vs 5.20; DefaultMedium s4 5.19 vs 6.51;
  Tunnels s4 6.91 vs 5.91, s5 6.23 vs 5.69: mean 5.55 vs 5.55. Captures +1 Tunnels, -1 DefaultMedium; wins 5/7 vs 6/7.
  **Not delivered; not gated.**

### 2026-10-02 — leaving the flag-pressure / carrier area (TRAINING_ALGORITHM §4: 3 consecutive rejects in one area)
Today's arms in this area, none delivered and won: b2rg (delivered regrabs, lost 11-26), b2rgc, b2rgu, b2u, b2rgh,
g1z2 (delivery 0.74 vs bar 0.7), g1z2w, g1icpt, g1camp, g1icamp, g1esc, g1esc2. What was learned stays: their captures
are carriers that get clear (47% unopposed) and chains of re-grabs/relays (64%); our escorts 4.85 vs their 6.76;
local rules that only see a fight do not reach the carriers nobody sees. Re-open condition: a mechanism that changes
what is seen (shared carrier tracking with prediction) or who is free to act (a dedicated interception/escort group).
Next per §4: plateau escalation steps 3-4 (re-read field games; re-read ADVICE and other years), and a structural
swing (none in the last 4 attempts).

### 2026-10-02 — plateau escalation steps 3-5 (re-read games, re-read advice, jointly necessary pairs)
- Field games re-read through the band survey split by outcome (B3 band; us/them in games we won / lost):
  kills 676/186 and 331/294 (our kill lead is against the weak; vs beaters fights are at parity); inEnemy300 19.3/13.8
  and 15.7/21.8 (beaters hold more of our half than we of theirs); gathered400 6125/6500 and 6317/8359 (beaters gather
  ~2000 more map crumbs by r400); level200 0/1.6 and 0/7.0; stun400 83/32 and 81/81.
- Aggression re-open check (ADVICE §29: re-test when a fixed defect blocked it): e1aggr/e2aggr were re-run clean on the
  restored trap placement (83/110, 81/110 vs 84): the closure stands.
- Jointly necessary pair (ADVICE §29): REGRAB alone delivered re-grabs but lost (lone re-grabbers die deep in their
  base); ESCORT_BEHIND alone moved nothing (nobody acts on the drop). Together they are the enemy's own convoy (64% of
  their captures come from re-grab/relay chains; escorts 6.76 vs our 4.85). Arm g1escrg = g_iter1 + ESCORT_CARRIER +
  ESCORT_BEHIND + REGRAB; per-half counters et and rg. Step 5(a) batch queued (7 cells vs g_iter1).
- g1escrg step 5(a) vs the g_iter1 mirrors, 7 cells (pickups / captured / regrabs / escorts20 / enemyCaptured / won):
  DefaultLarge s4 16/0/14/4.63/0/0 vs 13/0/5/4.44/0/1; s5 19/1/12/3.27/0/1 vs 7/0/1/5.20/0/1; DefaultMedium s4
  13/0/10/6.81/0/1 vs 12/2/4/6.51/0/1; s5 27/3/24/7.18/0/1 vs 1/0/0/-/0/1; Tunnels s4 15/1/11/10.45/1/1 vs 8/0/3/5.91/0/1;
  s5 22/0/15/6.52/1/0 vs 6/1/2/5.69/0/1; Battlecode24 s4 identical (no carrier). Both halves fire (et on 21-43 ducks,
  rg on 3-12). Totals: regrabs 86 vs 15, captured 5 vs 3, enemyCaptured 4 vs 2, wins 4/7 vs 6/7; escorts 6.34 vs 5.55
  on the 5 cells where both carried. First flag-pressure arm whose behaviour shows; the cost shows too (enemy captures).
  Step 5(b) pre-registered (BASE=g_iter1): rel:regrabs>=2.0 rel:escorts20>=1.1 rel:captured>=1.0 rel:enemyCaptured<=1.3.
- Filler: B1 vs g_iter1 113-138 over 1631 (-1.6 SE); B2 98-104 over 1274 (-0.4 SE); B3 45-68 over 757 (-2.2 SE).
- g1escrg step 5(b): **FAIL** (BASE=g_iter1): regrabs 9.08 vs 1.46 ok; escorts20 4.37 vs 4.46 FAIL (no convoy forms on
  the band); captured 1.04 vs 1.21 FAIL; enemyCaptured 1.92 vs 2.12 ok. The pair fails: re-grabs without conversion
  again. Flag-pressure area stays closed.

### 2026-10-02 — structural attempt S1: hold the line (designed from the outcome split, ADVICE §14/§30)
Premise (B3 band, games we lose): fights at parity (kills 331 vs 294), beaters hold more of our half (inEnemy300 21.8
vs 15.7) and their carriers walk home through it unopposed (47% of their captures); we win the level-sum tiebreak 20-4.
Doctrine: after setup the army holds front points between our spawn centres and their mirror images instead of
marching on enemy flags; raiders and carriers must cross it; games without captures go to the tiebreak we win.
Switch C.HOLD_LINE (fieldTarget only: chase, defend, alert, dropped flag and escort keep priority), HOLD_UNTIL,
HOLD_DEPTH_TENTHS; counter "hl".
Stage budget: A diagnostic (army at the line; enemy pickups/captures down) -> B delivery mini-block
(rel:enemyCaptured<=0.7, rel:enemyFirstGrabs<=0.8, pre-registered then) -> C band test. Closing criterion (written
now): two stage-B failures across depth variants close S1.
Arms g1line5 (midline) and g1line4 (0.4 of the way), hold all game; stage A batch queued (7 cells each).
- S1 stage A (7 cells each vs g_iter1; won / our pickups / enemy pickups / their inEnemy300): g1line5 0/7 wins, 0 pickups
  on all cells, enemy pickups 2-20, their inEnemy300 25-36; g1line4 1/7 wins, 3 pickups in total, enemy pickups 2-27.
  A static line does not stop an advancing opponent: it walks into our half (inEnemy300 25-36 vs the usual ~16) and we
  lose the level-sum tiebreaks too. **S1 closed at stage A** (premise failure, stronger than the written criterion).
  Ledger: "hold a front line instead of marching on flags" refuted 0-7 / 1-7 in stage A.

### 2026-10-02 — defect source: stillness in fights
- navstats over the B3 band (234 games; us / them): moves per robot-round 0.668 / 0.762; still rounds 32.7% / 23.2%;
  **after setup 36.1% / 22.0%**, of which with no enemy within dist2 20 only 4.6% / 6.2%. Our stillness is in fights:
  the kite/hold branch parks out of reach (minD 11-20 band, +1 for CENTER) while opponents keep moving, consistent with
  them gaining ground in our half (inEnemy300 21.8 vs 15.7 in our losses). New navstats fields stillPost /
  stillPostNoEnemy and census column stillPost (tested).
- Arm g1drift = g_iter1 + HOLD_DRIFT 40: in the kite/hold branch only, +40 for a tile closer to the field target, -40
  for one farther (below the 50 safe-band bonus and far below the 1000 per threat: a safe forward creep).
  Micro.objective = fieldTarget() in fights when on. Step 5(a) batch queued (7 cells).
- g1drift step 5(a) vs the g_iter1 mirrors, 7 cells (won / stillPost / inEnemy300 / their inEnemy300 / kills-deaths):
  Battlecode24 s4 1 / 50.9 / 28 / 11 / 190-203 vs 0 / 63.6 / 6 / 29 / 202-340 (won 1-0 on captures instead of losing 0-2);
  DefaultLarge s4 1 / 30.6 / 39 / 36 vs 1 / 38.2 / 18 / 24; s5 **0** / 34.1 / 39 / 24 / 290-366 vs 1 / 35.3 / 14 / 16 / 395-322;
  DefaultMedium s4 1 / 49.7 / 7 vs 1 / 48.7 / 1; s5 1 / 49.4 / 31 vs 1 / 58.6 / 22; Tunnels s4 1 / 55.9 / 4 vs 1 / 58.9 / 0;
  s5 **0** / 51.7 / 0 vs 1 / 60.1 / 0. Means: stillPost 46.0 vs 51.9; inEnemy300 21.1 vs 8.7; their inEnemy300 14.7 vs
  15.1; wins 5/7 vs 6/7. The mechanism delivers (less parking, more forward presence).
  Step 5(b) pre-registered (BASE=g_iter1): rel:stillPost<=0.92 rel:inEnemy300>=1.2 rel:enemyCaptured<=1.2.
- g1drift step 5(b): **FAIL** (BASE=g_iter1): stillPost 27.0 vs 31.85 (-4.85 +- 1.13, ok, strongly); inEnemy300 12.42 vs
  12.67 (-0.25 +- 2.02) FAIL; enemyCaptured 1.92 vs 2.12 ok. Against the band, less parking does not buy ground (beaters
  push back); against g_iter1 it doubled. Not band-tested. Same bars, stronger dose: g1drift80 (HOLD_DRIFT 80: above
  the 50 safe-band bonus, far below the 1000 per threat), step 5(a) on 4 cells queued.
- Filler: B1 vs g_iter1 123-155 over 1790 (-1.9 SE); B2 109-123 over 1433 (-0.9 SE); B3 62-82 over 955 (-1.7 SE).
- Ops 12:12 UTC: the VM disk filled (49 GB, gauntlet/ 35 GB of replays); the job write for drift80 failed and the queue
  runner spun on failing fillers 11:55-12:13 (log lines only; no incomplete runs left). Under the owner's standing
  delete permission (prompt 92): removed 14,728 replays (*.bc24 only, older than 30 min) from closed-arm scrim runs and
  censused filler runs; kept every results.csv, summary, log and census file, the stack builds' runs (g_iter0/1, b1v2,
  b1z2b, b2fs) and the dg909090 delivery-gate base runs (re-censused when a new column appears). VM disk 100% -> 45%.
  The drift80 batch was re-queued. Standing guard: tools/vm-prune.sh (disk > 80%: replays older than 60 min of censused
  arm and filler runs; keeps tools/keep-replays.txt builds and gate bases), run at the start of every filler; tested.
- g1drift80 step 5(a), 4 cells vs the mirrors (stillPost / inEnemy300 / won / captured / enemyCaptured, means or sums):
  43.5 / 23.8 / 3 of 4 / 3 / 3 vs drift40 46.5 / 24.5 / 2 / 1 / 1 and mirror 54.4 / 10.5 / 3 / 1 / 2. Stillness follows
  the dose. Step 5(b) with drift40's bars unchanged: rel:stillPost<=0.92 rel:inEnemy300>=1.2 rel:enemyCaptured<=1.2.
- Filler: B3 vs g_iter1 68-84 over 995 (-1.3 SE).
- g1drift80 step 5(b): **PASS** (BASE=g_iter1): stillPost 27.61 vs 31.85 (-4.24 +- 1.87); inEnemy300 15.46 vs 12.67
  (+2.79 +- 2.73); enemyCaptured 2.12 vs 2.12. Band test started (gauntlet/20261002-125637-scrim-g1drift80 = seed 1).

### 2026-10-02 — big-picture diagnosis: why nothing has passed g_iter1 (owner prompt 111)
Field (progress/ELO.md): g_iter1 1811 +- 15, rank 23 of 71; a cliff above: 12 bots at 2058-2415 beat B3 83-100%
(top three 0-2%); the 20-bot band is mostly peers at 1800-1910 where we are already about even.
1. Test power vs effect size: a band test (234 paired games, ~40 discordant) has 1 SE ~ 6.5 net games ~ 2.8 points of
   win rate ~ 20 Elo; only changes worth ~40+ Elo are visible. Elementary changes are smaller. The non-inferiority rule
   then admitted blocks that were slightly negative: B1 is -30 over 1870 paired games vs g_iter1 (-1.8 SE), and B2/B3
   inherited it, so the stack drifted down (B1 1783, B2 1804, B3 1789 vs g_iter1 1811).
2. Proxies that do not convert: regrabs x6 (b2rg), levels +10 (b3own), stillness -4 points (g1drift), inEnemy250 +3
   (b1v2) all delivered and none moved wins. Delivery checks that a behaviour happens, not that it is the binding term;
   the proxies came from "beaters have more X" correlations.
3. Architecture ceiling: g_iter1 is local reactive rules; after setup our ducks have an enemy in view ~95% of the time
   they stand still (stillPostNoEnemy 4.6 of 36.1 points), so most strategic levers (chase, camp, line, escort outside
   fights, crumbs, hint sweep) act on the rare turns with no fight. Micro.fight has no team objective, knows flags only
   when a carrier is visible, and nothing is tracked once unseen (alerts stale after 5 rounds).
4. The deficit vs the tier above is many-sided at once (fights at parity, ~2000 fewer crumbs gathered, less ground,
   re-grab/relay chains and escorts 4.9 vs 6.8, early raids): a coordinated system, not a missing tactic. Pieces added
   one at a time onto local rules cannot reproduce it; they are jointly necessary and need shared state.
5. Instruments aimed at the wrong place: the band and the step 5(a) games vs g_iter1 measure us against peers or
   ourselves (g1drift doubled ground vs g_iter1, none vs the band); the bots that define the gap are a small share of cells.
Implications (proposed): a staged rewrite of the decision layer (team roles and shared targets in the 64-slot array,
micro taking a team objective), keeping nav/micro/traps/setup plumbing; judge it against the tier above as well as the
band; stop stacking on non-inferiority and drop B1 (base new work on g_iter1).
- Correction to point 5 of the diagnosis: the band is not mostly peers. It holds 11 of the 12 bots rated 2050+
  (tools/upper-tier.txt); we win ~9% of those cells, so both arms lose them alike and they yield few discordant pairs.
  Wins there are an insensitive measure; capture difference per game is the sensitive one (SE ~0.11 per 128 games).

### 2026-10-02 — decision-layer rewrite started (owner prompt 112: "try it out, but keep statistics")
- Design by judge panel (workflow bc24-rewrite-design: four angles, three judge lenses, synthesis) -> research/REWRITE_DESIGN.md.
- Evaluation protocol pre-registered before any code: research/REWRITE_EVAL.md; milestone table progress/REWRITE.md.
- New tools/eval-paired.py (paired wins with exact sign test and capture-difference deltas, split all / upper tier /
  rest; tested) and tools/upper-tier.txt. Baseline g_iter1 on the band seeds: all 82/234 (capture diff -0.88),
  upper 11/128 (-2.04), rest 71/106 (+0.53). No earlier build reaches 2 SE vs g_iter1 on either measure.
- g1drift80 band (2 seeds vs the g_iter1 control on identical cells, tools/eval-paired.py): **REJECTED**.
  all: wins 82->69, 11 gained 24 lost, net -13 (sign p=0.041), capture diff delta -0.19 +- 0.09; upper: 11->6, capture
  delta -0.12 +- 0.11; rest: 71->63, capture delta -0.28 +- 0.16. inEnemy250 +2.3 (+4.3 SE) but inEnemy300 +0.6, kills
  -62 (-2.7 SE): drifting forward buys early ground that is gone by r300 and costs fights. Drift closed (ledger).
- Owner prompt 113 (agrees with diagnosis point 4; in a tournament enemy systems grow gradually and can be countered
  while weak, practice sessions present them mature): curriculum added to research/REWRITE_EVAL.md (process only, bars
  unchanged): a rung slice in tools/eval-paired.py (--rung tools/next-rung.txt; g_iter1 17/60 there), one-component
  archetype sparring partners built from observed field behaviour, and the opponent order archetype -> rung -> band.
- Ops: tools/band-test.sh takes SEEDS and TAG; g_iter1 control queued on the confirmation seeds 717171 + 818181.
- Design panel finished (8 agents): designs Situation Board (24/30 combined), Objective-Driven Micro (23.5), Task-claimed
  squads (19.5), Tide (12). Synthesis -> research/REWRITE_DESIGN.md ("shared flag tracks and bounded responders"):
  persistent flag-keyed tracks with prediction in slots 23-48; a capped auction of free or jailed responders (CUT) that
  cut off the predicted carrier path; later an escort convoy (ESC, conditional on its premise). Every duck without an
  objective plays g_iter1. Stages S0a (premise from replay truth, no bot code) -> S0b (sensor only, identity) -> S1
  (CUT, two doses) -> M1 band -> S2 (one tuning arm) -> S3 (ESC) -> M2; falsifiers F1-F5, premise bars P1-P6, budget
  (<= 10 delivery blocks, 3 band runs, 9 arms) and closing criteria C1-C7 fixed before any number. Panel record:
  research/rewrite-panel.json.
- Queued the S1 step-5(a) baseline early: g_iter1 vs the archetypes g1escrg (convoy with re-grabs) and arch_rush10 (fast
  raid) on the design's 7 cells (diag/arch-base), so every later arm game pairs with g_iter1 on the same opponent and cell.

### 2026-10-02 — focus: one unambiguous win over a higher-ranked bot (owner prompt 120)
Owner: pick one strategy, focus all energy on it; immediate goal a single, unambiguous victory over a higher-ranked
opponent; the bots above are a wall, find a little crack to widen. Plan and pre-registered victory criterion:
research/CRACK.md. Target **ColtG5.Goob_final** (rank 13, Elo 1909): g_iter1 85-135 (38.6%), and 102 of its 135 wins
are the more-flags tiebreak at r2000 (one flag decides the game). Victory = >= 60% of >= 120 scrims (random maps and
sides) and paired net >= +2 SE vs g_iter1 on the same cells. The filler now plays g_iter1 vs ColtG5 (FILLPOOL; "-" = no
candidate) to build the baseline and the replays for study. The band-wide rewrite (REWRITE_DESIGN) is paused as a
programme; its S0a tools (in review) serve the study, and its stages return only if they are the crack.
- Crack screen 1 vs ColtG5 (37 identical cells): rush and flank builds reach their flags (pickups x12-30) but none
  converts (captures 0.45-0.81 vs g_iter1 0.68). Counter-raid closed for ColtG5. Trip study (165 g_iter1 games): 56% of
  their captured trips unopposed; 15+ round trips captured 82% with < 0.5 chasers vs 25% with 1.5-3. Crack chosen: CUT
  (REWRITE_DESIGN) aimed at ColtG5; premise, delivery and decision on ColtG5 scrims (research/CRACK.md).

### 2026-10-02 — S0a instruments landed (workflow: implement, three-lens review, fix)
- New: replay-dump --comm (stored shared array per round; our slot 0 = 50 at r1), --track (offline 2.4 tracker per
  trip: unseenRounds, predErr/predErrSym, destHit/destHitSym, tKnow, reachAll/reachFree/reachInFight, escorts8,
  defDied10; own trips ownEscorts20, convoyReachFree; game rows symOk), --track-log, --calc; --capabilities columns
  enemyUnseenRounds, unopposedCaps, longTrips25, longCaps25, longCapRate, loneDeaths, trickleDeaths, symOk, psymOk,
  maxBcK, overruns; --defense gains tKnow (appended). tools/premise.py computes P1-P4, P6, D with SEs and routing.
  Review: 20 issues reproduced, 13 fixed, 6 duplicates, 1 partly (tracker-belief and responder columns need the bot's
  slots 23-48: deferred to S0b/S1). Tests green; new fixture test/fixtures/bot-vs-example-DefaultSmall-b1.bc24.
- **Readings pinned before any premise number** (premise.py prints the alternative beside each): a blank destHit counts
  as 0 in P3; a trip with unseen rounds but no predErr counts as a P1 failure; chaser thresholds use the exact mean;
  the home tile counts toward tKnow only while the belief is HOME; an empty group or both rates 0 is NO DATA (reported,
  never routes a closure).
- Premise runs queued: (1) design S0a as pinned: g_iter1 band runs 20261001-011402 + 20261001-021054; (2) the crack:
  every g_iter1-vs-ColtG5 replay on the VM at run time (band runs, crack777001, filler runs), routing for CUT vs ColtG5.
- **S0a premise, design sample** (g_iter1 band runs 011402 + 021054, 234 games; research/premise/premise-band.txt):
  P1 median predErr 2.0 tiles PASS; P2 0.794 +- 0.033 PASS; **P3 destHit 0.761 +- 0.025 FAIL** (0.807 among trips with a
  destination; under Sym.best() 0.491); P4 PASS (convoyReachFree 4; escort ratio 2.79); P6 0.44 +- 0.05 PASS.
  D: 81% of U seen at pickup; witnesses forgot (95) far more often than died (30); symOk 0.662 (Sym.best() wrong in a
  third of band games). Routing: one tracker revision (velocity, heading-led destination), then re-measure P1/P3.
- **S0a premise, the crack** (every g_iter1-vs-ColtG5 replay on the VM: 515 games; one replay excluded as corrupt, written
  at 11:54 during the disk-full incident, gzip "unexpected end of file"; research/premise/premise-colt.txt):
  P1 1.0 tiles PASS; P2 295/389 = 0.758 +- 0.022 PASS; P3 0.892 +- 0.013 PASS (Sym.best() 0.655); P4 PASS (ratio 3.56);
  P6 their 25+ round trips captured 0.506 with chasers >= 1 vs 0.882 with < 0.5 = 0.57 +- 0.05 PASS. symOk 0.720.
  **ROUTING: all bars pass, S0b next.** psymOk >= 0.95 is an S0b bar (symOk < 0.9 in both samples).
- Owner question (prompt 125) on the symmetry check. Finding: g_iter1's Sym uses only the enemy-flag broadcast hints at
  rounds 1-3 (each within dist2 100 of an enemy spawn centre), which often leave 2-3 symmetries consistent; best() then
  picks ROT > FX > FY by fixed order, i.e. guesses. Sym.observe (walls must mirror walls) exists but has no callers, and
  as written needs a tile and its image in one duck's vision. Correctness (symOk from --track): band 155/234 (66%),
  ColtG5 games 371/515 (72%); always wrong on some maps (StackGame, AceOfSpades, Snake, Tunnels, Soccer, TreeSearch,
  MazeRunner, HungerGames, Decision, ...). Win rate with a wrong guess vs a right one: vs ColtG5 25.0 +- 3.6% vs
  39.9 +- 2.5% (144 vs 371 games); band 29.1 +- 5.1% vs 37.4 +- 3.9%. Correlational and confounded by map (the same maps
  are always wrong), so only a fix tested on identical cells can price it. Candidate repair arm: observation-based
  symmetry (spawn-zone check on image tiles, dam/wall checks near the axis in setup, shared slot 16).

### 2026-10-02 — symmetry repair, audit, regression tests (owner prompts 127-128: "fix it; 100% after a limited number
of observations, else it is a bug; audit for anything else broken; add tests")
- src/bot/Sym.java rewritten: decided by observation, never guessed. (2) geometry at start: a symmetry that maps one of our
  spawn centres into our own spawn zones is impossible; (3) spawn zones: the image of our centre must be THEIR spawn-zone
  tile (one sight confirms or eliminates); (4) per-duck memory of every tile seen (wall, spawn zone; both immutable): a tile
  seen now must match its remembered image. Never empties the set (contradictions counted); results AND-merged through
  slot 16 by Comms.syncSym. Dead Sym.observe removed. Switch Sym.OBSERVE (default off in src/bot until the arm is judged),
  hook in RobotPlayer before Duck.turn.
- test/bot/SymTest.java: 300 random symmetric maps (30-60 a side, random wall density, a valid spawn zone); simulated
  vision disks through the real pruning code: the true symmetry is never eliminated, the set never empties, no
  contradictions, and every map is decided within 13 disks (bar 60); plus hand cases (wall vs open image eliminates FX;
  a contradiction is counted not applied; a centre on the FX axis rules FX out). OK.
- Arm g1sym = g_iter1 + the new Sym (OBSERVE on), nothing else (112 diff lines: Sym.java and the hook). Verification batch
  queued: 14 maps where the old guess was always wrong + DefaultMedium/DefaultLarge vs g_iter1: symOk and the round slot 16
  is decided, from the replays.
- tools/deadcode.py: lists methods never called and constants never read in a bot package (comments and strings
  ignored); on src/g_iter1 it reports Sym.observe (the bug), G.onMap, Nav.stepToward. Tested on a synthetic package.
  To be wired into unit-tests.sh for src/bot once the S0b workflow (editing src/bot now) has finished.
- Correctness audit launched (workflow: five lenses: dead code, rules/API, comms, behaviour in replays, measurement tools;
  two adversarial verifiers; ranked report with fixes and regression tests). Read-only on src/g_iter1 and tools.
- g1sym verification 1 (16 maps vs g_iter1, s4): symOk 14/16; decided at r1 on 4 maps, r5-62 on 6, r204-206 on 3; never
  on DefaultLarge (correct anyway); **Snake decided r269 and Soccer r1807** (both correct in the end, wrong at r250).
  Cause (Soccer board): terrain symmetric under ROT and FX alike; the candidates differ only by one row of the middle
  spawn-zone image, deep on their side, so no passive observation on our side can decide it. Fixes: (a) the spawn check
  covers all 27 of our spawn tiles (each must map onto THEIR spawn tile), not just centres; (b) **a scout**: after
  setup, while undecided, duck idx 3 walks to the nearest distinguishing tile (an image of one of our spawn tiles under
  one candidate that is not an image under another; one look eliminates at least one). SymTest adds a Soccer-like case
  (geometry cannot separate ROT/FX; the scout target is distinguishing; one look decides FX). OK.
  Verification 2 queued: the rebuilt g1sym on 24 maps.
- g1sym verification 2 (24 maps): 22 right at r250; Bunkers r292, Snake r270, Soccer r911. Spawn-centre error of the
  wrong candidate (from round-1 boards): Soccer ROT 1 tile, Snake ROT 1 tile, Bunkers FX 3 tiles; Canals, DefaultLarge,
  Hockey, Funnel have two candidates with error 0 (equivalent: nothing to observe). Changes: equivalent candidates
  collapse (counted as decided); three scouts (idx 3-5) that stop once the distinguishing tile is in sight; O(1)
  spawn-tile bitset (nested loops would cost ~35k bytecode).
- Verification 3 (24 maps): 22/24 right at r250 (Snake decided r253, Soccer r863); equivalent maps decided at r1. **But
  bytecode overruns: Bunkers r201 and Soccer r438**, and peaks 23.9-24.5k on Digging, HungerGames, MazeRunner, Snake (g_iter1
  ~15k): update() ran before the turn and scanned the whole vision disk regardless of budget. Fix: scout() before the
  turn (it needs the move), update() after it, only with >= 6000 left, every loop stops at 2500 left (partial memory is
  fine); scout target recomputed only with budget. Also tools/diag-batch.sh now runs at most 8 games at once (24 at once
  starved the VM's sshd for minutes: a task check hung). Verification 4 queued (10 maps incl. the overrun ones).
- **Verification 4 was invalid**: g1sym ran with Sym.OBSERVE OFF (bytecode max ~14.9k = g_iter1; symmetry never decided
  on 9/10 maps). Cause: inserting the BC_START line moved OBSERVE's trailing comment, so the sed that flips the switch
  for the arm matched nothing, silently. Repaired the source line; the arm is now built by a regex that asserts one
  match. New guard: tools/arm-intent.txt (arm, File.CONSTANT=value) checked by test_tools.py against the arm's source;
  shown to fail when the intent and the source disagree. Verification 5 queued (same 10 maps, OBSERVE really on).
- Verification 5 (OBSERVE really on; 10 maps): **0 overruns**, max 22.7k (the 2500 guard), symOk 8/10 at r250; Snake
  decided r253, Soccer r1059. But undecided ducks spent their spare budget every turn rescanning the vision disk
  (Soccer: mean 14.8k a turn, 309 turns near 90%, 56k robot turns vs ~100k). Fix: each tile is processed once per duck
  (a pair is compared when its second tile is first seen, so nothing is lost). Verification 6 queued.
- Verification 6 (10 maps): 0 overruns; mean bytecode on undecided Soccer 6.3k a turn (was 14.8k), others 2.3-3.2k;
  27 near-90% turns per map (the first full disk), all under the guard; symOk 8/10 at r250 (Snake decided r253, Soccer
  r1059; both final answers correct). Soccer's 56k robot turns = the game ended ~r1100, not starvation.
- Judging g1sym (symmetry repair) in play: the filler now pairs g1sym with g_iter1 on identical random cells vs ColtG5
  (FILLPOOL=ColtG5.Goob_final, filler-pair g_iter1 g1sym). Band: delivery gate pre-registered (BASE=g_iter1):
  mean:symOk>=0.9 mean:overruns<=0, then the band test, judged by REWRITE_EVAL's bars and eval-paired by tier.
- Owner pasted a summary of symmetry checking (fixed features such as walls and ruins; eliminate as tiles are sensed;
  track checked locations; bitmasks; rotation first and scouts toward the centre). Ours already eliminates on walls and
  spawn zones, processes each tile once, uses bitmasks, and keeps rotation only as the last-resort default. Ruins do not
  exist in 2024. Added the one fixed feature we ignored: **the dam during setup** (dam(t) must equal dam(image) when
  both tiles were seen before r200; ducks stand at the dam from r150, i.e. the centre). SymTest: a setup dam vs an open
  FY image eliminates FY; a post-setup sighting never compares dams. Synced: the head-to-head and band test use this
  version; the g1sym delivery gate already running used the version without the dam check (information only, setup).
- Owner prompt 134 jobs (queue order): g1sym delivery gate (gate only); **head-to-head g1sym vs g_iter1, 120 games,
  random maps and sides** (tools/mirror.sh PAIRED=0, replays kept); rush question: arch_rush10s and fl10s (the screen's
  rush/flank builds + the symmetry repair) on the screen's exact 37 cells vs ColtG5, plus a symOk re-census of all six
  screen runs; then the g1sym band test. Code reading first: g_iter1 picks targets from known enemy flag positions,
  then the fuzzy broadcast hints, and only last from the symmetry; but its carrier interception sends far ducks to the
  carrier's destination = the enemy spawn centre by symmetry.
- g1sym delivery gate (BASE=g_iter1, 24 band games): **PASS**: mean symOk 0.90 (bar 0.9), overruns 0.
- Owner prompt 137 ("the basics -- symmetry, movement, combat, economy -- are the foundation; make sure they are and
  remain solid; if progress stalls, check the basics"): new CLAUDE rule 15 and memory basics-first. tools/basics.py, the
  basics battery: absolute bars (symOk >= 0.90, overruns 0, exceptions 0) and checks against a base (stillPost,
  kill/death, trapsHit, gathered400, floating crumbs at r250; worse by > 2 SE = FAIL); new census column exceptions.
  Tested on synthetic blocks (a clean block passes; a symmetry miss, an overrun and a kill/death collapse fail).
  To run on every delivery block and band test; g_iter1 itself will fail the symmetry bar (0.66-0.72).
- **Head-to-head g1sym vs g_iter1** (owner prompt 134; tools/mirror.sh unpaired, 120 games, random maps and sides):
  **63-57 (52.5%)**, SPRT inconclusive (LLR -0.59). Split by g_iter1's own symOk in each game (86 replays matched):
  g_iter1 wrong: g1sym won 15/29 (52%); g_iter1 right: 28/57 (49%). The repair does not change results head to head.
- Owner question 138 ("was g_iter1 relying much on symmetry?"): no. In g_iter1 the symmetry is used in two places only:
  carrierTarget's interception destination for far ducks (Duck.java:183) and fieldTargetFrom's last resort after known
  enemy flag positions and the broadcast hints (Duck.java:216). The army and rushers target known flags and the hints,
  which exist whenever an enemy flag is on the ground. Hence the head-to-head shows no dependence on symOk, and the
  earlier 25% vs 40% split vs ColtG5 was a map effect (the always-ambiguous maps are also harder maps). The repair is
  still the foundation for anything that uses destinations (the crack's CUT predicts the carrier's destination spawn).
- Test fix: test_tools selected the --capabilities section of the shared dump by field count (47); the exceptions column
  made it 48 and the section came back empty. It now picks the section by a column name.
- **Rush question** (owner prompt 134): did the faulty symmetry send rushes to the wrong spot? Code: rushers target known
  flags, then the broadcast hints; symmetry only last. Data: (1) the screen's rush/flank builds + the repair, same 37
  cells vs ColtG5: arch_rush10s 13 vs 12 wins (net +1, capture delta +0.08 +- 0.19), fl10s 14 vs 12 (net +2, +0.22 +- 0.23);
  (2) on the 14 cells where the original guessed wrong, the repair did not raise rush pickups (arch_rush10 34.8 -> 12.4,
  fl10 26.1 -> 18.6), captures 0.50 -> 0.43 and 0.64 -> 0.64, wins 3 -> 4 and 5 -> 4. **Answer: no.** The original builds
  do look worse on wrong-symmetry maps (g_iter1 too: 1/11 wins vs 13/26), which is the map effect, not the symmetry.
  The fall in pickups on those cells is an interaction: the three scouts (idx 3-5) are rushers in these builds and leave
  the rush while the map is undecided (to fix if a rush build is ever used again: scouts outside the rusher range).

### 2026-10-02 — correctness audit (research/AUDIT-2026-10-02.md; 5 lenses, 2 adversarial verifiers; 41 of 42 confirmed)
Bot defects (g_iter1 and src/bot): **A1 critical**: the own-flag alert fires on any enemy seen by a duck near the flag
(an enemy can be ~dist2 80 away), is fresh in 1057-1686 of 1800 post-setup rounds, switches off all three defenders'
defend(), parks field ducks on the flag tile for up to 1238 rounds, sends every respawn to one zone, and stalls the
offence on Tunnels (first enemy flag sight r1299-1674). A2: enemy flag ids are the exact enemy spawn-centre index and are
never decoded (one id decides the symmetry in 418/468 cases). A3: symmetry guessed (fixed order) and handed to consumers
while undecided. A4: any visible enemy, even behind a wall, takes the whole turn (16-36% of enemy-in-vision robot-rounds
locked idle on walled maps). A5: EF_STATE 1 never expires when our carrier dies (a flag out of targeting for 1052 rounds).
A6: EF_LOC keeps a drop tile after the flag returned home (up to 397 rounds). A7: Nav resets bug state whenever the target
moves (chasers and escorts cannot round a wall). A8: dam and float traps never fire on most maps, so their ablations were
no-ops. Minor: A9 edge ping-pong, A10-A15.
Measurement defects: **B2 major**: paired tests never shared the engine seed (identical code flips 15-29% of cells; every
gained/lost tally was mostly engine noise, the arms all sat at that floor). **B1**: symOk credits a guess; the g1sym gate
passed at exactly 0.90. B3-B13 minor (repeated-cell name collisions, keys by package name, early census fetch, ...).
Plan (owner prompt 140: "confirm the findings"): (1) measurement first: scrim cells now carry their engine seed (a second
RNG stream, so a SEED's cell sequence is unchanged), filler-tally pairs only on shared seeds (legacy pairs reported
separately), census columns symDecidedRound and symWrong (from replay truth), basics.py symmetry bars from the audit
(symWrong 0; decided by r201 on setup-decidable maps in 95%; by r400 on the 15 post-setup maps in 90%; symOk descriptive);
an identity control (g_iter1 vs a byte-identical copy on shared seeds must read 0 discordant). (2) Each bot defect is
confirmed in a diagnostic replay, fixed behind its own switch with a unit test, measured, and the fixes then combined into
one basics build judged against g_iter1 on shared seeds (head to head, band, ColtG5).
- g1sym band test (2 seeds, legacy unseeded cells; eval-paired vs the g_iter1 control): all net -6 (17-23, sign p 0.43),
  capture delta +0.01 +- 0.09; upper +1 (+0.08 +- 0.10); rest -7; rung 0. Neutral, as the head-to-head (63-57). The
  symmetry repair is a foundation, not a win by itself in g_iter1 (which barely consumes symmetry).
- Measurement fixes landed (audit B1/B2): scrim cells carry engine seeds (second RNG stream; DRY=1 prints cells);
  filler-tally pairs on (opp, map, side, seed) and reports legacy pairs apart; census columns symDecidedRound and
  symWrong; basics.py symmetry bars from the audit, and a basic that the census does not measure now FAILS ("not
  measured") instead of passing silently. Tests: identical seeded cells for two bots on one SEED; legacy fallback;
  basics on synthetic blocks. Queued: re-census of the g1sym band runs with the new columns; the identity control
  (g_iter1 vs the byte-identical g1copy on shared seeds, 40 band + 40 ColtG5; expected 0 discordant).
- **Identity control on shared engine seeds** (audit B2 fix): g_iter1 vs the byte-identical g1copy, 40 band games:
  gained 0, lost 0 (0 discordant). Seeded pairing is exact; from here a discordant pair is the change, not the engine.
- S0b sensor committed (workflow: implement, 3 reviews, 12 issues fixed incl. a major PSYM non-convergence on FloodGates):
  src/bot/Track.java + hooks behind C.TRACK; with it off the classes compile to identical bytecode; with it on (g1trk)
  play is identical to g_iter1 (every/survey/flags/slots 0-22 equal); added ~700 bytecode a turn after setup.
- Dead-code check wired into tools/unit-tests.sh for src/bot (Nav.stepToward removed; the design's reserved slot
  constants AUC/AUC_SLOTS/OWN_C allowed by name).
- **A1 confirmed on our own replay with new census columns** (alertWrites, alertNoThreat, maxParkOnHome): g_iter1 on
  Tunnels wrote 2121 alerts, 1769 (83%) with no enemy within dist2 20 of any of its flag homes; one robot stood on a flag
  home tile 1164 rounds in a row. Fix C.ALERT_FIX (alert only for an enemy within dist2 20 of the home, with the threat's
  location in slots 49-51; defenders gate on their own flag; responders go to the threat, never the flag tile).
  test/bot/AuditTest.java covers it. Arm g1alert; diagnostic batch queued (paired with g1copy on 7 cells).
- A5/A6 fix C.REG_FIX: our jailed carrier's flag becomes "dropped" (slot 52-54 drop round), a pickup that captures at
  once is reported as captured; after the return window (4, or 25 with our CAPTURING) the drop tile becomes the learned
  home (slots 55-57) or is cleared (targeting falls back to the hints); a drop tile seen empty is cleared. AuditTest:
  dropped not carried; window kept; home after the window; cleared without a home. Arm g1reg.
- A5/A6 confirmed on replays with new census columns efStaleCarry / efStaleLoc: b2rg on Battlecode24 1056 rounds of
  "carried by us" with no carrier of ours (the audit's 1052); g_iter1 games 2-5 stale-carry rounds and 23-42 rounds
  with the registry location > 2 tiles from the flag. g1reg diagnostic queued (paired with the A1 batch's g1copy games).
- A4 fix C.REACH_FIX (Micro.engageable: an enemy takes the turn only if within dist2 8 or reachable in 3 moves by a
  BFS over passable sensed tiles; falls back to "yes" under 8000 bytecode left), A7/A9 fix C.NAV_FIX (Nav.resetNeeded:
  bug state kept when the target moves <= dist2 8; one edge flip per call), A2 (Sym.observeEnemyCentre: a flag id is
  their spawn-centre index; under Sym.OBSERVE, from sightings and the registry). AuditTest: A2 (the audit's 59x59
  example leaves FY), A7 (reset only on a real goal change), A4 (wall: not engageable; open or close: engageable). Two
  test-harness defects found by the tests themselves: Clock.getBytecodesLeft() returns 0 outside the engine (now
  G.bcLeft() with a test override) and BotTest's fake RobotController answered onTheMap=false (now in-bounds).
  Arms g1reach, g1nav; g1sym refreshed with A2. Diagnostic batch queued (7 cells each, paired with g1copy).
- **A1 confirmed fixed** (g1alert vs g_iter1, 7 cells, paired with the byte-identical g1copy): alerts without a threat
  305-2324 a game -> 0-81 (the residue is end-of-round vs mid-round positions); longest stand on our own flag tile
  162-1466 rounds -> 1-121. Wins 6/7 vs 6/7; Battlecode24 won 1-0 where g_iter1 lost 0-2; Tunnels s4 first enemy-flag
  sight r569 vs r958 and a capture win; DefaultLarge s5 lost where g_iter1 won.
- **A5/A6 confirmed fixed** (g1reg, same cells): "carried by us" with no carrier of ours up to 1393 rounds a game (DefaultLarge
  s4) -> at most 15 (about 1 per pickup: the death round before the jailed robot's next turn); stale registry location up
  to 87 -> 0-1. Wins 6/7 vs 6/7.
- Next: a combined basics build (ALERT_FIX + REG_FIX + REACH_FIX + NAV_FIX + observed symmetry with flag ids) judged
  against g_iter1 on shared engine seeds: a fresh seeded g_iter1 band control (the old one drew random engine seeds),
  then the band test, head-to-head and ColtG5 fillers.
- Identity control complete: g_iter1 vs g1copy on shared engine seeds, 40 band + 40 ColtG5 games: 0 discordant (ColtG5
  also replays identically on a shared seed). g1sym vs g_iter1 on ColtG5, shared seeds only: 120 pairs, 2 gained, 7 lost
  (9 discordant, n.s.); possible cost of the three scouts while undecided.
- A4/A7 diagnostics (7 cells each, exact seed-paired with g1copy): **g1reach (A4)**: post-setup stillness Tunnels
  37.1/26.6 vs 58.9/60.1, DefaultMedium 31.2/30.8 vs 48.7/58.6, Battlecode24 49.1 vs 63.6 (mechanism confirmed); wins
  1 gained 2 lost (Battlecode24 won where the copy lost; DefaultMedium s5 and Tunnels s5 lost 0-1); peak bytecode 23.5k
  (the BFS; 0 overruns, guarded at 8000 left). **g1nav (A7/A9)**: stillness unchanged; 0 gained 2 lost (DefaultLarge s4,
  Tunnels s4); Tunnels s5 first enemy-flag sight r1526 vs r642. No improvement visible; suspect until measured at power.
  The combined g1basics band test (seeded) decides; its overrun bar matters with A4 near the limit.

### 2026-10-03 00:45 UTC — **g1basics (all audit fixes + observed symmetry) beats g_iter1** (seeded pairs, band)
Delivery gate PASS (BASE=g_iter1, seeded): symWrong 0, overruns 0, alertNoThreat 23 vs 549 a game, efStaleCarry 18 vs
498. Band test, 2 seeds, exact seed-paired with the seeded g_iter1 control (eval-paired):
  all   234: wins 78 -> 111, gained 44 lost 11, **net +33 (sign p < 0.001)**, capture diff -0.89 -> -0.23, delta +0.66 +- 0.11
  upper 128: wins 13 -> 21, net +8 (p 0.115), capture delta **+0.34 +- 0.12 (t +2.8)**
  rest  106: wins 65 -> 90, net +25 (p < 0.001), capture delta +1.04 +- 0.17
  rung   60: wins 15 -> 25, net +10 (p 0.041), capture delta +0.55 +- 0.23
REWRITE_EVAL criteria: (a) all-cell net >= +2 SE: +33 = +4.4 SE, met; (b) upper capture delta >= +2 SE with all-cell
non-inferior: +2.8 SE, met. Confirmation (two more seeds for both, seeded) queued before g1basics replaces g_iter1.
Basics battery: g1basics PASS every bar (symmetry decided by r201 in 194/196 setup-decidable games and by r400 on 38/38
post-setup maps; 0 overruns, max 24.7k -- a thin margin, the A4 BFS; 0 exceptions; stillPost 28.1 vs 36.0; floating
crumbs at r250 177 vs 231); g_iter1 FAILS the symmetry bars (decided by r201 in 123/196, r400 on 5/38).
ColtG5 (the crack), seeded filler pairs: g1basics vs g_iter1 80 pairs, gained 26 lost 8, net +18 (+3.1 SE); g1basics
won 46/80 = 57.5% vs g_iter1 35.0% on the same seeds. Victory criterion (research/CRACK.md): >= 60% of >= 120 and paired
net >= +2 SE: the paired part is met; the win rate is 57.5% over 80 so far (the filler continues).
- Corrections (rule 8, audit A8/B2): the ablations ab1nodam (dam-front traps, 20-16) and ab3nofloat (idle-bank stun traps,
  19-20) are VOID: the mechanisms rarely fire (bank < 700 in setup on 64 of 75 maps; bank > 1500 after setup almost never),
  so those blocks compared near-identical builds; and every pair before 2026-10-02 23:00 UTC was unseeded (engine noise
  15-29% of cells). research/TACTIC_LEVELS.md annotated. Earlier "closed" directions judged on unseeded pairs are
  re-test candidates on the new base. Tools: gauntlet.sh run-id guard fixed (B13); vm-queue logs the job's exit status (B12)
  -- both take effect on the VM at the next sync, after the confirmation run.

### 2026-10-03 02:15 UTC — **g1basics confirmed; promoted to incumbent g_iter2**
Confirmation seeds 717171 + 818181 (fresh, seeded, both builds): all 239 pairs: wins 78 -> 107, gained 35 lost 6, net +29
(p < 0.001), capture delta +0.68 +- 0.10; upper 132: 12 -> 21, net +9 (p 0.049), capture delta +0.46 +- 0.12 (t +3.8);
rung 60: 12 -> 22, net +10 (p 0.006). Pooled 4 seeds (473 pairs): gained 79, lost 17, net +62. REWRITE_EVAL (a) and (b)
hold on the pool: g1basics replaces g_iter1. src/bot defaults now play as g_iter2 (ALERT_FIX, REG_FIX, REACH_FIX,
NAV_FIX, Sym.OBSERVE on; TRACK off); src/g_iter2 frozen (identical to src/g1basics but for one comment); CLAUDE rule 16.
Ladder (4 band runs + ColtG5 fillers recorded; elo.py): **g1basics 1918 +- 35, rank 13 of 74**, above ColtG5 (1877, rank 14;
record vs g1basics 61-83); g_iter1 1762 +- 11, rank 28. Next above: winkelmantanner 1997, CyrilSharma 2040, hsmalladi 2072.
ColtG5 victory criterion at 120 games: 71/120 = 59.2% (p 0.055 vs 50%), paired vs g_iter1 +31 (+4.3 SE): one win short of
the 60% bar. Pre-registered now, before more data: the final evaluation is at 240 ColtG5 games with the same bars
(>= 60% and two-sided p < 0.05; paired net >= +2 SE). Open: A7 (NAV_FIX) showed no gain alone; to ablate on seeded pairs;
peak bytecode 24.7k (A4 BFS) to bring down.
- First arms on the new base g_iter2 (all paired on seeded band cells against g_iter2's own seeded runs = the g1basics
  band runs): g2nonav (NAV_FIX off: is A7/A9 earning its place? ablation, band test directly), g2bc (REACH_BC 8000 ->
  11000: buy back bytecode headroom from the 24.7k peak; gate mean:overruns<=0 rel:stillPost<=1.05), g2z2 (Z2ESCORT on,
  radius 8: B2's escort-first targeting, +2.3 SE vs B1 on unseeded pairs; gate rel:enemyRegrabs<=0.7 rel:postPickups>=0.8).
  The A4 guard is now the named constant C.REACH_BC (no behaviour change; AuditTest OK).
- Owner prompt 150 ("old approaches you discarded are worth revisiting"): every verdict before 2026-10-02 23:00 UTC was
  unseeded (15-29% engine noise) and many ran on top of the fixed defects. A triage workflow (three lenses: bug
  interaction, statistical noise, strategic fit; one synthesis) is building research/RETEST.md: a re-test queue on g_iter2
  with switch settings and pre-registered delivery bars.
- g2nonav (NAV_FIX off) vs g_iter2, 234 seeded pairs: net -1 (21-22), capture delta +0.04 +- 0.09; upper +2 (+0.17 +- 0.12).
  A7/A9 is neutral in play; kept (it repairs a documented defect at no cost). **Basics: 1 overrun (max 25.0k) in this
  block**: g_iter2's bytecode margin is too thin (A4's BFS on top of symmetry observation); g2bc (REACH_BC 11000) running.
- Re-test triage done (workflow: three lenses + synthesis): research/RETEST.md, a 12-arm queue on g_iter2 with switch
  settings, pre-registered delivery bars, and the decision rule (adopt as a stack candidate at all-cell net >= +2 SE or
  upper capture delta >= +2 SE with net >= -5; close for good after two delivery failures or band net <= -2 SE; else park;
  confirmation seeds before stacking). Top: g2z2 (queued), g2icamp (INTERCEPT + DEST_CAMP), g2fstun, g2escrg, g2rgh,
  g2alert400, g2water, g2up3, g2def2, g2fort, g2csafe, g2z1. It notes exact-harness verdicts (shared seeds) stand as
  measurements except where A1 changed what the switch does (defence knobs).
- Tool: delivery-gate.sh tags its outputs (PASS/FAIL, census) when an explicit pool is given (DGPOOL), so a ColtG5 block
  never overwrites the band block that band-test.sh reads (found by the triage).
- Arms 2-8 built (arm-intent lines added); one step-5(a) batch queued: each vs the g_iter2 mirror on 4 maps, the two
  flag-defence arms also vs arch_rush10, with the g_iter2 baselines on the same seeds.
- g2bc (REACH_BC 11000): gate PASS and band test **identical to g_iter2** (0 discordant of 234; same max 22.0/22.8/24.7k
  per-game peaks): the BFS start threshold never mattered. The overrun (g2nonav, Divergent vs Strequals, r1887, robot
  B#11972) was a crowded late fight: the BFS (started with >= 8000 left, up to ~6k) followed by Micro.fight's loops over
  many robots. Fix: an in-search bail-out (C.REACH_BC_STOP, default 0 = g_iter2) so the search never leaves the fight
  with less than 13000; arm g2bc2 (REACH_BC 15000, REACH_BC_STOP 13000) queued (gate: overruns 0, stillPost <= 1.1).
  Note: the g_iter2 per-game peak is ~22k by design (symmetry observation spends spare budget while undecided).
- Owner noticed progress/ did not show g_iter2 (prompts 154-155): the promoted build's games were recorded under its arm
  name g1basics, and progress.png / accepted_builds only plot us:g_iterN; HANDOFF.md still named g_iter1 and README named
  no bot. Fixed: the 600 g1basics games relabelled us:g_iter2 in progress/games.csv (same code; run ids keep g1basics),
  ladder refit (g_iter2 1918 +- 35, rank 13 of 74), progress.png / elo.png / field-score charts regenerated (accepted
  builds g_iter0 -> g_iter1 -> g_iter2: ~1350 -> 1762 -> 1918; field score 52 -> 73 -> 78%); HANDOFF.md and README.md
  updated; the ColtG5 filler now plays the g_iter2 package so new games carry the right label. Still stale and being
  regenerated on the VM: progress/SURVEY.md and survey.csv (tactics survey queued) and progress/ONSET.md and
  onset-ladder.png (onset on a g_iter2 band run queued). Memory: promotion checklist.
- g2z2 delivery **FAIL** (enemyRegrabs 7.38 vs 8.88, bar 6.21; postPickups ok). Parked; a second attempt needs a 5(a)
  trace and a fresh seed (RETEST rule).
- Step 5(a) judged for arms 2-8 (8 diagnostic games each, indicator counters summed per robot from the replays):
  - fire: g2escrg (rg 14/16, et 731/201), g2alert400 (chases 474-3201 vs 0-2894; park <= 18), g2water (14 water traps
    on Battlecode24), g2fstun (home-tile stuns rebuilt after r400 on 3 of 4 maps and triggered 4-6 times), g2up3
    (CAPTURING at r1200; no late re-grab in 4 games: 12 more diagnostic games queued before its gate).
  - **g2rgh does not fire** (rg 0 on all 4 maps): our carriers die within a few tiles of the pickup (DefaultMedium: every
    drop at x 36-41 on a 44-wide map), so a loose enemy flag on our half never happens against the mirror. Parked: no
    premise, not a bug. This also says conversion dies at the enemy base, not on the way home.
  - **g2icamp: A11(a) confirmed and fixed.** Pre-fix, camps ran 14137 / 10889 robot-turns on DefaultLarge / Medium, and
    most came after our flag was home (flag home r379, then 1361 camp-turns in r400-449). Fix behind C.DEST_CAMP (g_iter2
    play unchanged): a duck that sees our flag i not carried clears OF_CARRY/OF_LOC (Comms.clearCarried), with the audit's
    regression test in AuditTest. Rebuilt g2icamp, 8 more games: camps 0 everywhere, intercepts 9-3441 a game. One
    carrier walked 26 rounds unseen and captured (Battlecode24 s5): never seen, so camps cannot trigger; DEST_CAMP as
    designed only covers carriers that are seen and then lost. Gates queued (ColtG5 block recorded, then band gate).
  - Tool fix: diag-batch.sh output names now carry the opponent (bot-vs-opp-map-seed): the arch_rush10 games had
    overwritten the mirror games of the same bot, map and seed (g_iter2/g2fstun/g2alert400 on DefaultMedium and Tunnels).
- Delivery gates (24 seeded cells vs g_iter2): **g2escrg FAIL** (regrabs 12.1 vs 1.8 ok; escorts20 5.06 vs 4.76, bar
  5.23; captured 1.42 vs 1.46, bar 1.46), **g2alert400 FAIL** (enemyFirstGrabs 4.88 vs 5.54, bar 4.71; chasers20 +51% ok),
  **g2water FAIL** (fired in 42% of games, need 50%; kills -15.6%, -2.6 SE), **g2fstun FAIL** (enemyCarrierKills 11.5 vs
  12.4, bar 13.6; kills -13%). All parked under the RETEST rule (second attempt only after a trace explains the failure).
- g2bc2 (BFS bail-out at 13000) band vs g_iter2, 234 seeded pairs: net +7 (23-16, p 0.34), capture delta +0.02 +- 0.09;
  overruns 0. **Basics FAIL: stillPost 29.99 vs 28.08 (+1.91 +- 0.89)**: the bail-out answers "engage" in crowded fights,
  which sends ducks into the hold branch. Not promoted.
- Replacement: C.REACH_FAST (arm g2fast), the same search at a fraction of the bytecode. Tiles in attack range of an
  enemy are marked once (13 offsets per enemy) instead of testing every enemy at every reached tile, and onTheMap /
  canSenseLocation become a bounds test (the 7x7 grid is inside vision). AuditTest checks it against the reference on
  3000 random wall/enemy layouts (all agree). 5(a): per-game peak bytecode fell on 5 of 6 maps (Divergent 21.0k -> 17.7k
  and 20.9k -> 18.2k, DefaultMedium 22.6k -> 19.0k, Tunnels 20.5k -> 18.1k) and the mean fell 5-10%. Gate queued
  (overruns 0, maxBcK <= 0.95, stillPost <= 1.05, kills >= 0.95), then band.
- **ColtG5 declared defeated by the owner** (PROMPTS 157; g_iter2 83-61 = 58% on the ladder, rated 1918 vs 1877). New
  standing rule (PROMPTS 158-159): when the target ranks below us, pick the next target, usually the bot just above.
  **New target: winkelmantanner.waffle** (1997, rank 12): g_iter2 10-14 against it, and **all 14 losses were by all three
  flags** (median r715); g_iter1 was 47-177. It is both the nearest bot above and the one we do best against (CyrilSharma
  7-17, hsmalladi 5-19). The filler now plays it (`FILLPOOL=winkelmantanner.waffle filler-pair.sh g_iter2 - 40`);
  CLAUDE.md rule 14 and tools/next-rung.txt updated. The final ColtG5 filler pair (g_iter2 vs g_iter1, 40 games): +16 (17-1).
- g2icamp (A11(a) fixed) delivery **FAIL twice** (ColtG5 block: chasers20 +12%, bar +20%; band: enemyFirstGrabs 7.00 vs
  5.54, +26%, bar +20%; enemyUnseenRounds not down): closed under the RETEST rule.
- g2fast delivery PASS and band **identical to g_iter2 on all 234 cells** (0 gained, 0 lost; basics PASS, 0 overruns,
  per-game peak 23.0k): the cheap search changes bytecode only. Folded into src/bot (C.REACH_FAST default true) and
  src/g_iter2 re-snapshotted (same play; delivery base caches stay valid).
- g2up3 (CAPTURING second): 12 more diagnostic games showed late re-grabs (Battlecode24 s6: 4, with a late capture), so
  it went to its gate: **FAIL** (48 cells: regrabsLate 1.69 vs 1.12 ok, capturedLate 0.33 vs 0.44, kills -6.6% = -2.9 SE).
  HEALING at r1200 is load-bearing, as the original 3-game read said. Re-test arms 2-8 are done: none delivered.
- **waffle study** (research/CRACK-WAFFLE.md): death-relay re-grab chains (87% of its drops of our flags re-grabbed in
  our losses, 77% in our wins, mostly the next round); it loses fights 1:2 and still converts; local balance at its first
  grab is the same in wins and losses; our stuns near drops do not raise resets (17% vs 18%); capture rate of our flags
  falls with distance to its spawn (< 20 tiles 88%, median r358; 28-36 tiles 50%). TACTICS.md T13 (re-grab chain) added,
  T2 (relocation) re-opened; tactics survey regenerated on g_iter2's band (2012 games) and spliced into TACTICS.md;
  progress/SURVEY.md and survey.csv updated.
- g2reloc (C.RELOCATE_FLAGS + C.RELOC_V2): spots wait for observed symmetry (or r40) and maximise the distance to the
  nearest enemy spawn under every live symmetry; audit A12 fixed with it (OF_HOME rewritten from the r200 placement).
  New census columns flagDistMin / flagDistMean (r200 own flags to the nearest enemy spawn centre; unit-tested).
  5(a) round 1 (8 maps): flags farther on Islands (min 19.8 -> 32.2 tiles) and Fountain (17.0 -> 22.4), but **3
  overruns at r41-43 on Battlecode24** (symmetry undecided until r50: grid search over 9 candidate centres) and **all
  flags reset on Tunnels** (a stall-drop 4 tiles from another flag's spawn left that defender with no legal drop; still
  carrying at r200, the engine's unchecked drop broke the 36 spacing). Fixed: coarser grid with > 3 centres; an illegal
  spot walks to the nearest legal tile in view (senseLegalStartingFlagPlacement) instead of back to spawn. Round 2 queued
  (12 maps, g_iter2 mirrors alongside).
- g2reloc 5(a), rounds 3-4: RELOC_V2 redesigned after two traced failures. Round 3 (most exposed flag first, spot within
  15 tiles of its spawn, staying put allowed) fixed DefaultMedium but Whirlpool and Gated got worse: an earlier flag took
  a spot beside a later flag's spawn and forced it closer (25.6 -> 23.0 tiles), and a stalled carrier dropped short.
  Fixes: moved spots stay 8+ tiles from every unplaced flag's spawn (so each later flag can stay), a stalled carrier walks
  home. AuditTest gained DefaultMedium and Whirlpool layout tests (the Whirlpool one verified to fail without the fix).
  Round 4 (12 maps vs the g_iter2 mirror): nearest-flag distance up on 8 maps (Fountain +9.7, Tunnels +8.3, Soccer +6.7,
  Puzzle +6.0, DefaultLarge +5.2), worse on none; sum 315 vs 272 tiles (x1.16); 0 overruns, no r200 resets. Waffle
  delivery block and band delivery queued (pre-registered in research/CRACK-WAFFLE.md).
- progress/ONSET.md and onset-ladder.png regenerated from g_iter2's band (112 games; the VM lacks matplotlib, so
  onset.py ran on the driver from the VM's study.tsv). Ladder refit with 200 waffle filler games: g_iter2 1915 +- 27
  (rank 14 of 75), waffle 1977 directly above; g_iter2 vs waffle 96-128 (43%).
- **g2reloc delivery FAIL** (pre-registered, research/CRACK-WAFFLE.md). Waffle block (24 scrims, same cells): flagDistMin
  25.5 vs 21.7 (+18%, ok); enemyCaptured 2.25 vs 2.46 (-9% +- 11%, bar -15%) FAIL. Band delivery: flagDistMin +20% ok,
  kills 425 vs 486 (-12.5%, 1.0 SE) FAIL. Descriptive, waffle block: wins 8 vs 5 of 24 (gained 5, lost 2), our captures
  1.50 vs 1.04, games 1318 vs 951 rounds. Trace: the dose was small (+4 tiles under the 15-tile walk bound); the
  distance gradient predicts about -10% captures for that, which is what was measured. Second attempt (RETEST rule):
  g2reloc2, walk bound 25 tiles (C.RELOC_R2 625; grid step scales with the radius to bound bytecode), 5(a) queued; its
  gates will run on a fresh seed.
- g2reloc2 5(a): no larger dose (nearest-flag sum 310 vs 315 tiles over 12 maps; the limit is geometry, spacing and
  keeping each unplaced flag's own spot, not the walk bound). Not gated; relocation parked as a modest lever.
- g2rush10 (RUSHERS 10 on g_iter2) 5(a): does not fire (enemy-half presence at r250 8/20/25/21/2/12/6/1 vs the mirror's
  8/20/28/26/2/11/18/0): g_iter2's army already heads for their flags and rushers stop to fight what they meet. Parked.
- Waffle blocks pre-registered and queued: g2z2w (escort-first targeting rebuilt on g_iter2; 5(a) diag first, gate
  held until it shows the es counter firing) and g2alert400 (alert radius 400) on the waffle pool.
- Waffle pool, delivery blocks (24 scrims, same cells as the g_iter2 base): **g2alert400 FAIL** (enemyCaptured 2.42 vs
  2.46; chasers20 +6%, bar +10%) and **g2z2w FAIL** (escort-first fired on all 6 diagnostic maps, es 8-44, but waffle's
  re-grabs were unchanged, 21.1 vs 21.1; enemyCaptured 2.58 vs 2.46). Caution on reading these blocks: every arm
  "gained" (7-8 wins vs 5 of 24) because the base scored low on these cells (21% vs 43% overall) and any code change
  re-draws the games: regression to the mean, not effect.
- Why chain-breaking by attack fails: at each waffle drop (30 drops in 3 losses) it has 0-5 ducks within dist2 2 of the
  flag and 1-9 within dist2 8 (step in and pick up the same turn); killing the ball in the 1-round window is beyond
  attacks. One stun does not cover the 4-round reset window either (escorts thaw at +4, a stunned carrier needs 2-3
  rounds to kill). Waffle can afford its 2:1 losses: respawn sustains ~2 deaths a round and the chain costs ~1 carrier
  per 2 tiles. What decides the race is time: its first two captures come by ~r275-550 in our losses.
- Waffle's first grab is our nearest flag in 276 of 563 games, the second nearest in 160, the farthest in 127: not
  predictable enough to pre-position on.
- New census columns carrierStunBuilds / carrierStunned (our stuns built within dist2 8 of an enemy carrying our flag;
  our triggered stuns that caught one). Baseline vs waffle: carriers caught 4.7 a game in losses, 7.6 in wins, almost
  all by traps already in place (0-4 built for it). Arm g2cstun (C.CARRIER_STUN): a duck with a carrier within dist2 18
  builds a stun within dist2 8 of it, ahead on its way home (a frozen carrier cannot move for ~4 turns). 5(a) queued.
- **g2cstun waffle block: FAIL** on enemyCaptured (2.29 vs 2.46, -7%, bar -15%) with the mechanism delivered (carriers
  caught 15.0 vs 4.8) and **wins 13 vs 5 of 24 (gained 8, lost 0; sign p ~ 0.008)**, our captures 1.62 vs 1.04, kills
  585 vs 402, games 1269 vs 951 rounds. Trace: freezing carriers delays waffle's captures more than it prevents them.
  New census columns captured600 / enemyCaptured600 (unit-tested). Second attempt pre-registered before looking at the
  new column (research/CRACK-WAFFLE.md): fresh seed 919191, bars carrierStunned >= 1.3 and enemyCaptured600 <= 0.8.
- **g2cstun second attempt: FAIL** (seed 919191, 21 cells): carriers caught 15.0 vs 5.0 ok; enemyCaptured600 1.19 vs
  1.33 (-11%, bar -20%). Wins 14 vs 10 (gained 5, lost 1); pooled over both blocks 13 gained, 1 lost (sign p ~ 0.002).
  Under TRAINING_ALGORITHM §3.5 a failed delivery is never judged on wins: logged as not delivered. Question for the
  owner (BRIEFING): judge delivery on the mechanism's own signature (frozen carriers, passed twice at x3) rather than on
  a downstream capture bar? Fix and re-diagnose: g2cr = carrier stun + relocation, 5(a) queued, waffle block
  pre-registered on fresh seed 929292.
- g2cr 5(a): both mechanisms fire on 8 mirror maps (carrier-stun builds 2-42, carriers caught 3-24, flags placed as in
  g2reloc, 0 overruns). **g2cr waffle delivery PASS** (seed 929292, 22 cells): carrier stuns built in 100% of games,
  carriers caught 27.3 vs 5.9, flagDistMin 29.7 vs 25.5 (bar 29.3), enemyCaptured600 1.27 vs 1.59 (-20%, bar -20%), 0
  overruns; descriptive: wins 12 vs 8 (gained 6, lost 2), our captures 2.00 vs 1.55. Filler now pairs g2cr with g_iter2
  against waffle (CLAUDE rule 14); band delivery + band test queued.
- **g2cr band delivery FAIL** (guard): kills 390 vs 486 (-20%, 1.4 SE); enemyCaptured 1.88 vs 1.96 ok; wins 11 vs 9
  (gained 2, lost 0). Per round kills -9%, k/d 2.56 vs 2.92, games shorter (1070 vs 1239 rounds). The band test stays
  blocked; the waffle filler (its own delivery passed) continues. Relocation's band cost, from the same blocks
  (g2reloc alike): enemy first grabs 7.7-8.2 vs 5.5, defenders near flags at r300 5-7 vs 10, setup traps 36 vs 41,
  trickle deaths 18.5-25 vs 12.8: a flag moved away from our spawn loses the free defence of respawns and the
  defenders spend setup carrying. Against waffle the longer chain wins; against light raiders it costs. Next: g2cstun's
  band guard alone (information only, no band test chained), and a relocation that moves only the exposed flag.
- g2cstun band guard (information): kills 454 vs 486 (-6.5% +- 15%) "FAIL" with k/d 2.93 vs 2.92, wins 2-1, enemy
  captures unchanged: band-neutral. The point guard rel:kills>=0.95 on 24 cells fails about a third of neutral arms.
  Tool: delivery-check.py gains `nw:` (not worse by more than 2 paired SE) for guards, unit-tested; used from now on, not
  to re-judge past blocks. g2cr band guard second attempt pre-registered on fresh seed 939393 with nw: guards, band test
  chained.
- Waffle filler, first g2cr pairs (2 fresh seeds, 80 paired games): g2cr 46-34 (57.5%) vs waffle, g_iter2 38-42 on the
  same cells; paired gained 19, lost 11, net +8 (+1.5 SE). Ladder refit: g2cr 1999 +- 77 (80 games), waffle 1955.
  Victory read at 240 paired games (>= 60%, p < 0.05, net >= +2 SE).
- **g2cr band test vs g_iter2** (234 seeded pairs, seeds 515151 + 616161): wins 111 -> 122 (gained 25, lost 14, net +11,
  p 0.11); capture difference -0.23 -> +0.10, **delta +0.33 +- 0.10 (t 3.4)**; upper tier net +4, **capture delta +0.30
  +- 0.13 (t 2.3)**; rest net +7 (+0.37 +- 0.14). Basics PASS (symWrong 0, sym setup 196/196, late 37/38, 0 overruns, 0
  exceptions, all relative checks ok). REWRITE_EVAL criterion (b) met (upper capture >= +2 SE with net >= -5):
  confirmation seeds 717171 + 818181 queued against g_iter2's confirmation runs. The band cost feared from relocation
  (first grabs +40% in one 24-cell block) does not show in the outcome over 234 pairs.
- Waffle filler: 120 paired games, g2cr 66-54 (55.0%) vs g_iter2 55-65 on the same cells, paired net +11 (+1.7 SE).
- g2cr's fast losses to waffle (all three flags by r262-500, small maps): at r200 waffle banks 1830-4260 crumbs and
  spends ~3000 in the next 75 rounds; we hold ~190 and hover at 50-240, so carrier stuns are starved when the raid
  lands (15-20 stun builds r200-400). Arm g2crb = g2cr + C.BUDGET_V1 (nothing discretionary in setup, fills only when
  stalled, a floor from 1500 at r200 falling 10 a round holds back rings and idle traps; combat and carrier stuns draw
  on the bank at once). BUDGET_V1 was last judged unseeded on the buggy base. 5(a) queued behind g2cr's confirmation.

### 2026-10-03 10:30 UTC — **g2cr confirmed; promoted to incumbent g_iter3; waffle ranks below us**
- Confirmation seeds 717171 + 818181 vs g_iter2's confirmation runs: net +11 (25-14), capture delta +0.17 +- 0.09, upper
  +0.07 +- 0.13; basics PASS. Pooled over 4 seeds (473 pairs): **net +22 (sign p 0.017)**, capture delta **+0.25 +- 0.07
  (t 3.8)**, upper +0.18 +- 0.09 (t 2.0), rest +15 (+0.33 +- 0.10): REWRITE_EVAL criterion (a) on the pooled result.
- Promotion: src/bot defaults CARRIER_STUN, RELOCATE_FLAGS, RELOC_V2 on; src/g_iter3 snapshotted (identical to g2cr but
  comments); AuditTest's incumbent check updated. Ladder: band runs recorded as g_iter3, the 160 g2cr filler games
  relabelled g_iter3, the 120 "g1basics" ColtG5 filler games relabelled g_iter2 (same code); refit: **g_iter3 1977 +-
  33, rank 12 of 75**, waffle 1964 (rank 13), g_iter2 1920. Waffle filler: g2cr 95-65 (59.4%, p 0.022) vs g_iter2 73-87
  on the same 160 cells (net +22, +2.8 SE); the 240-game read finishes with the g_iter3 package.
- Waffle ranks below us: new target (CLAUDE rule 14) **CyrilSharma.finalBot** (2020, rank 11; g_iter3 10-14, the best
  record against any bot above us). CLAUDE rules 14/16, HANDOFF, README, REWRITE.md, TACTICS (T13 accepted), memory.
- CyrilSharma study started (research/CRACK-CYRIL.md): fast raids like waffle but a weaker relay (re-grab share 75% in
  our losses, 72% in our wins); distance gradient of our flags' capture rate 82% (< 20 tiles) -> 39% (36+); its flag
  guards beat our carriers; it banks ~3900 crumbs at r250. First candidate g2crb (g_iter3 + BUDGET_V1), 5(a) queued.
- **Waffle victory read PASSED** (pre-registered in research/CRACK-WAFFLE.md): 240 games on six fresh filler seeds,
  g_iter3 (g2cr code) **146-94 = 60.8%, p = 0.001**; g_iter2 on the same cells 108-132 (45.0%), paired net +38.
- g2crb (g_iter3 + BUDGET_V1) Cyril delivery FAIL: the bank arrives (100% of games) but stuns by r400 +12% (bar +30%)
  and carriers caught 12.6 vs 13.5; descriptive wins 9 vs 12, our captures 0.83 vs 1.21. Parked. g_iter3 12-12 vs Cyril
  on those cells. Filler switched to the Cyril pool (g_iter3 baseline on fresh seeds).
- g_iter3 vs Cyril, 48 replays (22-26): losses are fast captures on small maps (first grab r218-331, first capture
  ~r277-440), where relocation and carrier stuns have little room. Arm g3def2 (2 defenders per flag, scouts moved to
  idx 6-8) built on g_iter3, 5(a) queued.
- g3def2 Cyril delivery FAIL (enemyFirstGrabs -6%, bar -15%; wins 8 vs 12, our captures halved): closed. Our offense vs
  Cyril: our carriers die alone (at our drops, ours within dist2 8: 1.14, Cyril's 3.53) and we re-grab 19% of drops
  (Cyril re-grabs 75% of its). g3escrg (convoy + re-grab on g_iter3) built; 5(a) queued.
- g3escrg Cyril delivery FAIL (regrabs x5.6 ok; escorts20 3.06 vs 3.71, bar x1.1); descriptive wins 13 vs 12, captures
  1.50 vs 1.21. Trace: escorts hand control to fight micro and fall behind. New switch C.ESCORT_TIGHT (fight moves stay
  within dist2 8 of our carrier, or close in); second attempt g3escrg2, 5(a) queued.
- g3escrg2 5(a): ESCORT_TIGHT alone left escorts20 flat (3.80 vs 4.04); ESCORT_R2 > 20 is inert (carrier must be in
  vision); C.ESCORT_FAR_R2 225 (join our carrier from the registry out of fights) lifts escorts20 to 4.35-8.65 (mean
  6.1 vs 4.0). Cyril second attempt queued on fresh seed 919191.
- g3escrg2 Cyril delivery PASS (second attempt, seed 919191): regrabs 11.2 vs 3.3, escorts20 5.34 vs 3.10, kills
  unchanged; descriptive wins 7 vs 9 of 21, captures 0.95 vs 0.86. Filler now pairs g3escrg2 with g_iter3 vs Cyril;
  band guard (nw: guards, BASE g_iter3) + band test queued. g_iter3 vs Cyril baseline 108-236 (31%).
- g3escrg2 band test vs g_iter3: net +6 (p 0.39), capture delta +0.18 +- 0.08, upper +0.20 +- 0.12: no criterion met.
  Basics FAIL on kill/death (2.13 vs 3.11, -2.8 SE): the convoy's price in bodies. Not promotable; the Cyril paired
  filler continues for information.
- Owner (PROMPTS 168): cracking an opponent overrides the basics veto. Recorded as CLAUDE.md rule 15's exception: an arm
  that passes its pre-registered victory read against the target is not vetoed by a relative basics check its tactic
  pays by design (g3escrg2's kill/death); the absolute bars (symmetry, overruns, exceptions) still stop it. Memory added.
- Cyril filler, g3escrg2 vs g_iter3: 80 paired games, gained 16, lost 13, net +3 (+0.6 SE): not cracking Cyril so far.
- Owner (PROMPTS 169): a prompt that captures the correctness audit, for future years. Written by two workflows
  (three drafts from different angles, two judges, synthesis, an adversarial completeness critic: 13 gaps and 9 errors,
  all applied and verified, 3 residual overstatements fixed by hand): `AUDIT_PROMPT.md` (a ~530-word paste-in) and
  `AUDIT_PLAYBOOK.md` (the full procedure: parameters, triggers, the five-lens read-only audit with adversarial
  verification, then tools first with identity controls, bot fixes behind switches, one combined build judged by exact
  paired tests and confirmed, permanent guards, open findings, re-tests). TRAINING_ALGORITHM.md amended: the audit is
  the first plateau-escalation step; the mirror gate shares one engine seed per cell; Phase 0 adds the identity
  control and the basics battery; step 4 runs the basics battery and the dead-code/switch-intent checks.
- Owner (PROMPTS 173): cross-year ADVICE.md updated (battlecode-vibe, commit 11faab4 on its default branch and main):
  one paragraph at the top of §29 "The plateau": audit correctness of the bot and the instruments before any
  escalation (lenses, evidence + fix + regression test, refutation, instruments first with an identity control, fixes
  behind switches, exact paired tests, confirmation, permanent checks). No year or this-year document references.
- g3escrg2 closed: 240 paired filler games vs Cyril, net +8 (+1.0 SE), 32% vs g_iter3's 30%. Filler back to the g_iter3
  baseline vs Cyril. Next: a measurement study of Cyril's flag defence before any new arm.
- Cyril defence study (workflow, 5 lenses + synthesis + critic; research/CRACK-CYRIL.md): the kill box at its flags is
  symmetric (not its edge); no trap ring, equal guards; its offense decides games (no Cyril capture -> 84% wins, any ->
  18%); its captures come from relays (70% re-grab legs) and long unwatched carries (159 vs 87 carrier-rounds per game in
  losses vs wins; capturing trips ~71 rounds with ~1 of ours near). The critic refuted the jail gate and flagged the
  respawn-stream premise as inflated. Next arm g3camp (DEST_CAMP alone on g_iter3) aimed at the unwatched walk.
- g3camp (DEST_CAMP on g_iter3) Cyril delivery FAIL: unwatched carrier-rounds -15% (bar -20%), unopposed captures halved
  (ok), Cyril captures -18%, wins 12-12. Trace: campers pick the straight-line nearest enemy spawn while Cyril's carrier
  heads to another nearly equidistant one. C.CAMP_SPLIT (split over spawns within 1.3x) + AuditTest; g3camp2 5(a) queued.
- g3camp2 (CAMP_SPLIT) Cyril second attempt FAIL (unseen -15%, unopposed caps -8%, Cyril captures equal, wins 8 vs 9):
  camp line closed. Five lines vs Cyril near neutral: per AUDIT_PLAYBOOK §1, run the correctness audit (second one) on
  g_iter3 and the tools, open findings of the first audit in scope.

### 2026-10-03 21:00 UTC — second correctness audit (AUDIT_PLAYBOOK.md)
- Workflow: five lenses, two verifiers matched by id, synthesis; 56 findings, all 56 reproduced (0 rejected, 0
  unverified): 20 bot and 17 tool defects after dedup. research/AUDIT-2026-10-03.md.
- **MEAS1 (critical):** delivery `rel:` was a point bar: 16 of 28 FAILs (3 of 5 Cyril closures) and 7 of 15 PASSes were
  decided inside 1 SE. **MEAS2:** the claim "seeded pairing is exact" (this log, 2026-10-02) holds only for code that
  changes nothing; behaviour-changing arms disagree on ~17-29% of pairs.
- **Bot:** BOT1 captured own flags never recognised (defenders, rings, alerts and respawns serve an empty home in 82% of
  Cyril games); BOT2 defenders leave their flag in fights (17% of Cyril first grabs with the defender > dist2 20 away);
  BOT3 relocation abandons ~20% of flags; BOT4 post-setup economy a third of Cyril's; BOT5 pickup only at turn start.
- Fixed so far (tools first, with tests): delivery-check three-way verdict (PASS >= +1 SE, FAIL < -2 SE, else
  INCONCLUSIVE; nw: guards INCONCLUSIVE when blind to a 20% drop; < 18 shared cells INCONCLUSIVE; exit 3) and
  delivery-gate auto-extension 24 -> 48 -> 96 cells; eval-paired and filler-tally print identical games and discordance;
  basics FAILs a check its base did not measure (MEAS12); keep-replays protects g_iter3 / g2cr (MEAS12). Corrections
  appended to HANDOFF, RETEST.md and CRACK-CYRIL.md. MEAS3 (replay names without the seed) is on the open list: nine
  consumers parse the name and cached bases use the old one.
- Audit BOT1/BOT2 as switches with unit tests: C.FLAG_LOST (shared lost bits in slot 58, last-seen rounds 59-61; lost
  flags skipped by alerts, spawn choice and responders; defenders re-home) and C.DEF_TETHER (defenders fight within
  dist2 20 of home unless closing on their own flag's carrier; chase only their own flag). Arms g3lost and g3tether fire
  at 5(a) vs Cyril. New census columns defNearAtGrab20 and capturedHomeRounds (unit-tested). Cyril deliveries queued.
- g3lost Cyril delivery PASS (three-way: capturedHomeRounds -73%, +2.7 SE; wins 14 vs 12, Cyril captures 1.42 vs 1.62):
  filler pairs it with g_iter3 vs Cyril; band guard + test queued. g3tether INCONCLUSIVE at 24 and 48 cells (defenders
  near the flag at Cyril's first grabs +7%, bar +15%), auto-extending to 96. Fixed delivery-gate's extension tag (the
  48-cell block was labelled n96).
- g3tether Cyril delivery FAIL at 96 cells (defenders near the flag at Cyril's first grabs 5.60 vs 5.64, -2.3 SE): a
  powered failure, closed. g3lost band guard PASS after one extension (48 cells: kills +19%, enemy captures -15%); band
  test running.
- g3lost band test vs g_iter3: net +1, capture delta +0.14 +- 0.06 (t 2.2), upper tier +0.22 +- 0.09 (t 2.5): criterion
  (b) met; basics PASS (identical games 86/234: it changes play only after a capture). Confirmation seeds queued.

### 2026-10-04 00:15 UTC — **g3lost confirmed; promoted to incumbent g_iter4** (second audit, BOT1)
- Confirmation seeds 717171 + 818181 vs g_iter3's: net +8, capture delta +0.14 +- 0.06, upper +0.27 +- 0.09 (t 2.9; upper
  wins 24 -> 33, p 0.035); basics PASS. Pooled 473 pairs: upper +0.24 +- 0.06 (t 3.8), all +0.14 +- 0.05 (t 3.1), net
  +9: criterion (b). First Cyril filler pair g3lost vs g_iter3: +4 of 40 (6-2).
- Promotion: C.FLAG_LOST default on; src/g_iter4 snapshotted (identical to g3lost but a comment); AuditTest incumbent
  check; band runs recorded and g3lost games relabelled g_iter4; keep-replays gains g_iter4 and g3lost; ladder refit:
  g_iter4 1932 +- 39 (rank 12), g_iter3 1901 (dragged by many filler losses to Cyril), waffle 1903, Cyril 2032.
  CLAUDE rule 16, HANDOFF, README, REWRITE.md updated. The filler pairs g_iter4 with g_iter3 against Cyril.
- Audit BOT5 and BOT3(b) as switches on g_iter4: C.PICKUP_AFTER_MOVE (step to a loose enemy flag one step away and pick
  it up before striking or healing; after any move try a pickup; note "pickstep"; AuditTest) -> arm g4pick; and
  C.RELOC_STALL_MOVES (relocation counts only movement-ready turns and gives up to the best tile reached, not the spawn
  centre) -> arm g4reach. 5(a) queued for both (g4reach on the maps the audit traced: Hurricane, Diagonal, Starfish,
  Waterworld, BedWars, Fusbol, EvilGrin, Divergent, with g_iter4 mirrors alongside).
- Indicator string reformatted (audit MEAS5: it overflowed 64 chars on 70-75% of turns, cutting off the note and the last
  counters, so g4pick's "pickstep" note never showed and per-robot counter sums undercounted). Now: note first, then
  o, x, ch/ic/cp, et, ct, rg, pk (after-move pickups), es, cs, hl; bytecode peak, advances and fills dropped (the census
  reads bytecode from the engine). tools/side-indsum.sh parses each counter on its own. g4pick rebuilt; 5(a) re-run with
  g_iter4 mirrors alongside.
- g4pick 5(a): regrabs ~10x vs g_iter4 mirrors (the pickup lands the turn after the step: a dropped flag cannot be picked
  up the round it drops), captures 7 vs 3 in 6 games; Cyril delivery queued (rel:regrabs>=2.0, nw:kills). g4reach 5(a):
  flag distance +1% only; not gated until BOT3(a) (reachable spots) is built.
- g4pick Cyril delivery PASS (regrabs x5.3, +3.7 SE): filler pairs it with g_iter4 vs Cyril; band guard + test queued.
  g4econ 5(a): no change in crumbs gathered (r200 identical, r400 equal in sum), parked. g_iter4 vs g_iter3 on Cyril:
  +7 of 120 paired (+1.5 SE).
- g4pick band test vs g_iter4: net +9 (p 0.12), capture +0.12 +- 0.09, upper +0.16 +- 0.12; basics PASS. Not yet a
  criterion; extended with seeds 717171 + 818181, the pooled result pre-registered to decide.
- g4pick pooled 473 pairs: net +6, capture +0.08 +- 0.06 (t 1.3), upper +0.11 +- 0.09: the pre-registered bar is not met,
  parked (Cyril filler -2 of 80). Filler back to the g_iter4 baseline vs Cyril. Next from the audit: BOT7 (carrier stuns
  built behind the carrier) and BOT10 (carrier sightings expire after 5 rounds).
- Audit BOT10 and BOT7 as switches on g_iter4 with tests: C.CARRY_PREDICT (a stale carrier sighting, age 6-59, becomes a
  predicted point age/2 steps toward its nearest spawn, chased like a fresh one; A11(a) clearing extended to it) -> arm
  g4pred; C.STUN_AHEAD (carrier stuns only from ahead of the carrier) -> arm g4ahead. 5(a) vs Cyril queued.
- g4ahead parked at 5(a) (carrier-stun builds halved, catches halved: 27 vs 51 on the same 6 cells). g4pred's
  prediction fires (pr 31-1861 a game); Cyril delivery queued (rel:enemyUnseenRounds<=0.85, rel:chasers20>=1.1,
  nw:kills). Indicator: pr (predicted chases) replaced the always-zero hl.
- g4pred INCONCLUSIVE at the 96-cell maximum (unseen carrier-rounds unchanged, chasers +8%): parked. g_iter4 vs Cyril
  217-367 (37%; g_iter3 ~30%). Next: the decision-affecting tool findings (MEAS11 Elo convergence first).
- Audit MEAS11 fixed: elolib.fit iterates to tolerance (about 24,000 MM iterations, 8 s) and warns if capped; the old
  3,000-iteration cap had every rating 30-46 points low (differences unchanged). Synthetic-ladder test added.
  upper-tier.txt re-derived on the converged fit at 2050+: waffle dropped (we beat it ~62%), 11 bots; REWRITE_EVAL and
  eval-paired note it. Converged ladder: g_iter4 1954 +- 23 (rank 12), CyrilSharma 2056 (rank 10), waffle 1928 (16).
- Audit BOT8 behind C.FILL_STEP (step onto the tile just filled in the same turn); indicator fs (fill-steps) replaces the
  dead es; arm g4fill, 5(a) on water maps with g_iter4 mirrors queued. Audit MEAS11 done (above).
- Tool bug found and fixed: tools/side-indsum.sh read our side from scrimmage names (__botA); on diag-batch names it fell
  to the wrong team and summed g_iter4's old-format indicator (no pk/pr/fs). Diag files now default to side A. Re-summed:
  **g4fill fires 100-387 same-turn fill-steps a game** on water maps (replay check: #10535 fills (35,7) at r3 and stands
  on it that round); **g4pick's after-move pickups fired directly, 5-99 a game** (correcting the earlier guess that the
  pickup landed the next turn; its parked verdict rested on the band and Cyril results and stands). g4fill delivery
  (band, rel:stillPost<=0.97, nw:kills) queued.
- g4fill INCONCLUSIVE at 96 cells (band stillPost -2%, bar -3%; kills +1.4%): the fix fires only on water maps and the
  band-wide bar was too strict (on the six water maps of its 5(a), stillPost -7.6%). Following the first audit's lesson,
  the small correctness fixes go into one combined build: **g4bundle** = FILL_STEP + PICKUP_AFTER_MOVE + RELOC_STALL_MOVES
  (each fires; none harmful so far), 5(a) queued; delivery will gate on rel:regrabs>=2.0 with nw:kills, then band test.
- g4bundle 5(a) (6 maps vs g_iter4): after-move pickups 7-60 and fill-steps 46-405 a game. Band delivery
  (rel:regrabs>=2.0, nw:kills) and band test queued.
- g4bundle band test vs g_iter4 (234 pairs): net +2, capture +0.08 +- 0.09; upper (11 bots now) net -5, -0.13 +- 0.13;
  rest +7, +0.28 +- 0.13 (t 2.2); basics PASS (k/d 2.53 vs 3.09, -1.6 SE). No criterion: parked. Better against weaker
  bots, worse against the top: more re-grab attempts cost carriers against strong defenders (as with the convoy).
- Audit BOT16 behind C.RELOC_SPREAD (arm g4spread): the relocation spot scan pauses at 4,000 bytecodes left and resumes
  next turn (AuditTest: paused-every-column spots equal one-go spots; a broken resume fails it). 5(a), 6 maps vs g_iter4
  mirrors: max bytecode and turns at 90%+ unchanged (Bunkers 50, Waterworld 42). New `replay-dump --near90` lists those
  turns: **all are round 1, one per robot (22.8-22.9k of 25k)**, G.init's 27x27 spawn-centre loop. Fix C.INIT_FAST (arm
  g4init: the same centres from a bitset; AuditTest 500 random layouts). It frees round-1 bytecode, so Sym.update can
  observe on round 1 (a behaviour change, hence an arm). 5(a) with g4spread on TwistedTreeline and English (the audit's
  23.6k relocation turns) queued.
- Audit BOT9 behind C.ALERT_NEAREST (arm g4alert): field ducks answer the nearest live fresh alert within ALERT_RADIUS2,
  not the freshest; respawns split over all live fresh alerts by index; indicator counter an (turns on a shadowed alert).
  5(a): 8 shared-seed scrimmages vs Cyril for g4alert and g_iter4 queued.
- Cyril read on 1,259 g_iter4 filler games (36%): losses MORE_FLAG_CAPTURES 406, CAPTURE 292, LEVEL_SUM 105; wins
  LEVEL_SUM 188, MORE_FLAG_CAPTURES 139, CAPTURE 128. We capture nothing in 467 of 803 losses. Flag distance does not
  separate wins from losses at game level (mean 31.3 vs 31.2 tiles); enemy unseen carrier-rounds do (95 vs 141).
- g4init / g4spread 5(a) (6 maps incl. TwistedTreeline and English, vs g_iter4 mirrors, seed 5): **g4init changes
  nothing** (round 1 still at 22.9k on Bunkers and TwistedTreeline; symmetry decided on the same rounds). The round-1
  near-limit turns are Sym.update's terrain fill, which by design runs until 2,500 bytecodes are left (BC_STOP), not
  G.init; no overrun risk. INIT_FAST stays off (no effect). g4spread lowers English's peak 23.3k -> 22.8k and changes
  nothing else; BOT16 is fixed behind C.RELOC_SPREAD, kept for a later combined build, not gated alone.
- New census columns stunTrig / stunVictims / enemyStunTrig / enemyStunVictims (robots within dist2 13 of each triggered
  post-setup stun). One g_iter4 win vs Cyril (TwistedTreeline): its 434 stuns caught 2,594 of ours (6.0 each); our 255
  caught 1,103 of its (4.3 each). Census over all 1,291 g_iter4-vs-Cyril replays queued.
- g4alert 5(a) (7 shared-seed cells vs Cyril, g_iter4 on the same cells): an fires 122-3,019 turns a game (g_iter4 0);
  wins 2 vs 3, Cyril captures 11 vs 14, unseen carrier-rounds 348 vs 943 (Capacitance alone 377 for g_iter4). Delivery,
  pre-registered: `DGPOOL=CyrilSharma.finalBot DGTAG=-cyril BASE=g_iter4 tools/delivery-gate.sh g4alert
  'rel:enemyUnseenRounds<=0.85 nw:kills>=0.95 mean:overruns<=0'` (responders at shadowed alerts should see carriers leave).
- g4alert Cyril delivery: INCONCLUSIVE at 24, 48 and 96 cells (96: unseen carrier-rounds 111 vs 117, -4%, bar -15%,
  margin -1.4 SE; kills guard PASS). Answering the nearest alert does not put eyes on carriers. Parked (not closed).
- **Stun geometry (TACTICS T14, research/CRACK-CYRIL.md).** `replay-dump --trapgeo` on a g_iter4 win vs Cyril
  (TwistedTreeline): its 434 post-setup stuns were built two tiles from our nearest duck (median dist2 5) with 5.2 of ours
  in the stun radius, triggered after a median 6 rounds (1 round when built within dist2 4), 6.0 frozen each; ours: 282,
  median 66 rounds, 4.3 each, half with no enemy in vision (flag rings). New census column stunVictimsFast (victims of
  stuns triggered within 10 rounds of the build): Cyril 1,757, g_iter4 312.
- Adoption arm **g4front** (C.STUN_FRONT: the reachable tile one enemy step from triggering with the most enemies within
  dist2 13, at least 3). 5(a), 8 Cyril cells (seed 777040, same cells as g_iter4's alert diag): stuns built within dist2 8
  of an enemy 50-90% of ours (g_iter4 35-66%), median latency 7-33 rounds (g_iter4 21-138), victims per game +9%; wins
  4/8 vs 3/7. Delivery pre-registered: `DGPOOL=CyrilSharma.finalBot DGTAG=-cyril BASE=g_iter4 tools/delivery-gate.sh
  g4front 'rel:stunVictimsFast>=1.5 nw:kills>=0.95 mean:overruns<=0'`.
- Neutralization arm **g4wary** (C.STUN_WARY): an enemy cannot build on or beside one of our robots and a stun fires
  when one of ours enters a tile within dist2 2 of it, so every tile within dist2 2 of one of our robots now is clear; a
  fight step pays WARY_COST 150 when a tile within dist2 2 of it had a visible enemy beside it in the last 8 rounds and is
  beside none of ours now (indicator wy = turns the penalty changed the step). 5(a) on the same 8 cells queued.
- Tool bug fixed: capability-census.sh died after the header under pipefail when a run had no losses/ folder (the
  1,298-replay stun census came back empty); regression test added. Census re-queued.
- g4wary 5(a) (same 8 Cyril cells): **0 of 7 won** (g_iter4 3 of 7 there), Cyril captured 2-3 flags in every game, we
  captured none; wy dodges 665-4,610 a game. Trace: the penalty applied to every fight step, including closing on an
  enemy carrier (20000 - 10 x dist2: 150 = 15 dist2 units) and every advance near their line, so chasers fell off
  carriers and attackers stalled. Second attempt (pre-registered, one retry after a traced failure) **g4wary2**: the
  penalty only among engage tiles (an enemy in reach), 60 (< one threat unit, 100), i.e. pick the safer reaching tile.
  g4front fast stun victims on the 5(a) cells: mean 536 vs g_iter4 336 (x1.6); Cyril's on us unchanged (~2,000).
- **g4front Cyril delivery: FAIL** (24 cells): stunVictimsFast 481 vs 382 (+26%, bar +50%, margin -2.3 SE); kills guard
  PASS. Descriptive (not judged): wins 7 vs 14 (a lucky base block: g_iter4 is ~36% vs Cyril). Trace: total stun victims
  unchanged (1,075 vs 1,042 a game) and stuns triggered unchanged (206 vs 196): placement only moves stuns earlier; the
  volume is set by crumbs. Cyril triggers 433 a game.
- Stun census over 1,520 g_iter4-vs-Cyril games (research/fill/stuncensus3-g_iter4-cyril.csv): Cyril builds 475 traps to
  our 247 and triggers 454 stuns to our 192; its stuns freeze 1,826 of ours within 10 rounds of the build to our 363.
  Within a map, our wins come with fewer Cyril stuns (P = 0.63) and fewer of ours frozen (0.60). **Economy is the root:
  gathered200 is level (4,373 vs 4,209) but gathered400 is 5,663 vs Cyril's 8,376** (audit BOT4: in r201-400 it collects
  3,572 crumbs to our 838, 1,408 of them from our half). g4econ's crumb detour runs only with no enemy in view, rare after
  r200, and was tried only in mirrors.
- Arm **g4crumb** (C.CRUMB_STEP: fight steps take a crumb tile when otherwise level, +40 against 100 per threat; with
  C.POST_SETUP_CRUMBS idle detours; indicator cr). 5(a) on the 8 Cyril cells queued (signature gathered400).
- g4wary2 5(a) (8 Cyril cells): wy fires 190-724 a game; our ducks frozen per Cyril stun 6.2 vs g_iter4's 6.5 (-5%);
  wins 2/8 vs 3/7. The neutralization does not deliver; parked after its second attempt.
- **Crumb accounting (TwistedTreeline replay, both gathered 2,400):** after setup Cyril had ~27.7k crumbs (bank 2,200,
  passive 18,000, kill rewards 268 x 30) and built 432 stuns, ~54 crumbs each net of digs and fills; we had ~24.7k
  (kill rewards 224 x 30) for ~274 stuns and 23 explosives, ~77 a stun. Engine SkillType: build level L cuts trap cost
  10/15/20/30/40/50% and needs 5L build actions (traps or digs); level 4 in attack or heal caps build at 3. Our stuns are
  spread over the whole army (most builders at level 0-1); Cyril ends with three build-6 ducks (`--trapgeo` attributes
  ~40% of its stuns to them, nearest-robot attribution). New census columns deathsHome / enemyDeathsHome (kill rewards:
  268 vs 224 here) and gatheredAll.
- Arm **g4builder** (C.BUILDERS): idx 9, 19, ..., 49 build every combat stun (front placement, up to 3 a turn), dig
  toward 30 build actions in setup (reserve 300), and stop attacking at 74 XP and healing at 99 XP so build stays
  uncapped. 5(a) queued.
- g4crumb 5(a) (8 Cyril cells): gathered400 mean 7,057 vs 5,129 (+38%): Ambush 6,000 vs 2,300 (Cyril 3,400 vs 7,100),
  Joker 23,700 vs 14,500 (Cyril 18,600 vs 27,800); two maps without loose crumbs played identically. The idle detours do
  it; the fight-step bonus fires rarely (cr 0-8). Wins 3/7 vs 3/7. Delivery pre-registered: `DGPOOL=CyrilSharma.finalBot
  DGTAG=-cyril BASE=g_iter4 tools/delivery-gate.sh g4crumb 'rel:gathered400>=1.15 nw:kills>=0.95 mean:overruns<=0'`.
- g4builder 5(a) (8 Cyril cells): builders reach build 4-6 (build masters 0-8 a game); where games run long the volume
  jumps (Rivers: 436 stuns triggered vs g_iter4's 215 on the cell, fast victims 1,505 vs 232, won 2-0). But 1/7 won:
  Ambush, StackGame and Joker ended early (rounds 720 / 631 / 900 vs 1,211 / 1,228 / 1,888). Trace: setup digs with
  reserve 300 took the bank (Joker 662 at r200 vs 3,857), setup traps fell (14 vs 24, 31 vs 48), stun400 fell (25 vs 38),
  and non-builders built no combat stuns early. Second attempt **g4builder2** (pre-registered): the default dig reserve
  (1,000) and non-builders keep the old combat stun when no ally at build level 3+ is in view. 5(a) queued.
- **g4crumb Cyril delivery: INCONCLUSIVE** at 24, 48 and 96 cells (96: gathered400 7,057 vs 6,049, +17%, bar +15%,
  margin +0.3 SE; kills guard PASS, detectable drop 6%). Parked (not closed): the crumbs come, but not clearly past the bar.
- g4builder2 5(a) (same cells): 1/7 won again; Ambush, Capacitance and StackGame still end early (705 / 776 / 993 rounds),
  setup digs smaller (8-36, Joker 150), traps200 still low on Ambush (16 vs 24). Untested hypothesis: our flag defenders
  reach build 6 from ring traps, so near our flags every non-builder sees a "builder" and stops placing combat stuns just
  when Cyril raids. The pre-registered second attempt is spent: the builder line is parked; a version that keeps every
  duck's combat stuns and only adds the discount is a different design for later.
- Owner (PROMPTS 177): if the lines of attack on Cyril run out, open the opponent list. Records vs the bots above us
  (g_iter4, band and delivery games): Gymhgy.v10official (2099, rank 9) 11-21 over 32 games (band seeds alone 11-13);
  every other bot above us 4-21%. The idle filler now builds a g_iter4-vs-Gymhgy baseline (FILLPOOL=Gymhgy.v10official)
  while the last Cyril economy arm runs. Gymhgy (survey): moves flags in setup, digs in setup, relays carriers, heals more
  than it attacks; its stun output is level with ours (166 triggered a game to our ~170), unlike Cyril's.
- Arm **g4econ2** (g4crumb + builders as an addition: every duck keeps g_iter4's combat stuns, builders add discounted
  front stuns, BUILDER_SEEN_LEVEL 99). 5(a) (8 Cyril cells): fast stun victims 4,056 vs 2,349 on the same cells (+73%;
  Rivers 1,574 vs 232, Joker 993 vs 369, Gated 399 vs 278); builder setup digs still cut setup traps on Ambush (16 vs 24);
  wins 1/7 (the cells flip for nearly every arm; g_iter4 won Capacitance, which seven of eight arms lost). Delivery
  pre-registered: `DGPOOL=CyrilSharma.finalBot DGTAG=-cyril BASE=g_iter4 tools/delivery-gate.sh g4econ2
  'rel:stunVictimsFast>=1.3 nw:kills>=0.95 mean:overruns<=0'`.
- **g4econ2 Cyril delivery: PASS** (24 cells): stunVictimsFast 589 vs 382 (+54%, bar +30%, margin +1.1 SE); kills guard
  PASS but kills 449 vs 525 (-14%, detectable drop 18%): watch it in the band test. The filler now pairs g4econ2 with
  g_iter4 against Cyril; band guard (nw:kills, nw:enemyCaptured) + band test queued after an 80-game g_iter4-vs-Gymhgy
  baseline (two explicit runs).
- Gymhgy trace (Canals loss, `--defense`): its offense relays every ~2 rounds (27 trips: 22 hand-offs, 3 captures, by
  r271 and r371) with 0.95 of ours within dist2 20; its defense puts 5.4 chasers on each of our trips (31 trips, 24 first
  grabs, 29 carriers dead). Against it we grab often (16 first grabs a game vs Cyril's 4) but carriers die. Candidate
  levers if it becomes the target: Z2ESCORT (hit the adjacent receivers first) and Z1HOLD, both never re-gated.
- Gymhgy 5(a), 8 shared cells (seed 777050) for g_iter4 / g4z2 (Z2ESCORT) / g4z1 (Z1HOLD): wins 3 / 3 / 5. g4z2 cuts
  Gymhgy's re-grabs 4.9 -> 2.9 a game and changes nothing else. **g4z1**: our ducks within dist2 20 of its carrier 1.95 ->
  2.95, Gymhgy captures by r600 1.75 -> 1.12, unseen carrier-rounds 91 -> 69, games 1,313 -> 1,795 rounds (EvilGrin and
  KingQuacksCastle no longer lost early). Delivery pre-registered on the Gymhgy pool: `DGPOOL=Gymhgy.v10official
  DGTAG=-gym BASE=g_iter4 tools/delivery-gate.sh g4z1 'rel:chasers20>=1.25 nw:kills>=0.95 mean:overruns<=0'`; g4z1 5(a)
  on the Cyril cells too (Z1HOLD was never re-gated on the fixed base, RETEST arm 12).
- g4econ2 band guard: INCONCLUSIVE at 24, PASS at 48 cells (kills 479 vs 580, -17%, -1.4 SE, detectable drop 17%;
  enemyCaptured 1.54 vs 1.44, PASS). The kill cost is consistent (Cyril -14%, band -17%): builders stop attacking at 74 XP
  and ducks detour for crumbs. Band test running.
- **g4econ2 band test vs g_iter4** (234 seeded pairs, identical 16): net +3 (19-16), capture delta -0.08 +- 0.09; upper
  net -1, -0.20 +- 0.12 (t -1.6); rest +4; basics PASS (gathered400 +24%, trapsHit +11%, k/d level). No REWRITE_EVAL
  criterion: not promotable on the band. Its remaining case is a Cyril crack, read by the paired filler (g4econ2 vs
  g_iter4 against Cyril, now the idle filler).
- **g4z1 Gymhgy delivery: FAIL** (24 cells): chasers20 1.94 vs 2.37 (-18%, bar +25%, -3.3 SE); kills guard INCONCLUSIVE
  (+20%). Descriptive (not judged): wins 11 vs 8, Gymhgy captures by r600 1.25 vs 1.46, unseen carrier-rounds 69 vs 84.
  Trace: chasers20 counts our ducks near THEIR CARRIER; Z1HOLD puts ducks on our DROPPED flag, and when the relay picks it
  up the carrier walks away from them, so the column measured the wrong thing (the 5(a) rise on 8 cells did not hold).
  New census column dropGuard (our robot-rounds within dist2 8 of our flag lying dropped away from home): Canals, ours 7
  all game, Gymhgy's 641. Second attempt (pre-registered, fresh seed 919191): `SEED=919191 DGPOOL=Gymhgy.v10official
  DGTAG=-gym2 BASE=g_iter4 tools/delivery-gate.sh g4z1 'rel:dropGuard>=2.0 nw:kills>=0.95 mean:overruns<=0'`.
- g4z1 5(a) on the 8 Cyril cells (7 shared with g_iter4): no signature. dropGuard 204 vs 286 robot-rounds (lower), enemy
  re-grabs 12.9 vs 14.4, Cyril captures identical (2.00), wins 2 vs 3. Against Cyril our flag drops mostly where its
  carrier dies among our defenders, so we already stand around it (g_iter4's dropGuard vs Cyril 286 a game; vs Gymhgy on
  Canals 7). Z1HOLD is a Gymhgy (voluntary relay) lever only; not gated against Cyril.
- **g4z1 Gymhgy second attempt: FAIL** (seed 919191; INCONCLUSIVE at 24, FAIL at 48 cells): dropGuard 195 vs 166 (+18%,
  bar x2.0, -2.1 SE); kills guard INCONCLUSIVE. The hold barely adds ducks beside our dropped flag. Z1HOLD closed against
  Gymhgy (two attempts). Next Gymhgy lever, our offense: we grab its flags 16-22 times a game and wins come with more
  re-grabs (6.5 vs 4.2); g4pick (PICKUP_AFTER_MOVE, regrabs x10 in mirrors, parked on the band) 5(a) on the Gymhgy cells.
- g4pick 5(a) on the 8 Gymhgy cells: regrabs 20.4 vs 4.1 a game, pickups 45.6 vs 24.8, carrier deaths 40.6 vs 19.6, but
  captures 1.50 vs 1.62 and captures by r600 0.25 vs 0.62; wins 4 vs 3. The same pattern as against Cyril and the band:
  more re-grabs, not more captures. Not gated.
- Tool fix: vm-prune deleted step-5(a) diagnostic replays an hour after they ran (their run label is not the keep-list
  build), so g_iter4's Gymhgy diagnostic was gone before g4pick could be paired with it (the earlier census copy served).
  Runs named *-diag-* now keep their replays DIAG_AGE (1,440) minutes; regression test extended.
- **g4econ2 vs Cyril, paired filler stopped for futility at 120 pairs:** net +1 (18-17, +0.2 SE), g4econ2 39-71 (35%) vs
  g_iter4's 36%; the pre-registered read (>= 60% over 240) was out of reach. Not a crack.
- **Target change (owner PROMPTS 177): CyrilSharma -> Gymhgy.v10official** (2099, rank 9; g_iter4 43% over 181 games).
  Cyril's lines are exhausted for now (closing note in research/CRACK-CYRIL.md). CLAUDE rule 14, HANDOFF, the target
  memory, tools/next-rung.txt (waffle removed: below us) and the filler (FILLPOOL=Gymhgy.v10official, g_iter4 baseline)
  updated.
- g4relay 5(a) (RELAY + PICKUP_AFTER_MOVE; 8 Gymhgy cells, 7 Cyril cells): relays fire (124 relay pickups a game vs
  Gymhgy, 25 vs Cyril) but the flag moves LESS while carried (carrier moves 232 vs 290; Cyril 55 vs 99): handing over
  whenever an ally stands ahead costs a round per hand-off. Captures 1.50 vs 1.62 (Cyril 0.86 vs 1.29), kills down.
  Second attempt (pre-registered) **g4relay2**: C.RELAY_THREAT, hand over only below 600 HP or with an enemy in view
  (the purpose: a fresh carrier before the old one dies). 5(a) on the Gymhgy cells queued.
- **g4relay2 5(a) on the 8 Gymhgy cells: wins 6 vs 3, captures 2.50 vs 1.62 (+54%)**, relay pickups 70 a game, carrier
  moves 308 vs 290 (no longer slowed), kills 363 vs 349. The first arm to lift captures against Gymhgy. g_iter4 baseline
  vs Gymhgy now 147-212 (40.9%) over 359 filler games. Delivery pre-registered: `DGPOOL=Gymhgy.v10official DGTAG=-gym
  BASE=g_iter4 tools/delivery-gate.sh g4relay2 'fire:relayPickups>0>=0.9 rel:captured>=1.2 nw:kills>=0.95
  mean:overruns<=0'` (the relay exists to turn grabs into captures).
- **Owner PROMPTS 178: maps and sides may now be chosen against external bots.** CLAUDE rule 4, BENCHMARK.md,
  TRAINING_ALGORITHM §6, AUDIT_PLAYBOOK's rule list, gauntlet.sh and scrim.sh updated: the ladder, Elo and pre-registered
  victory reads stay on random scrimmages (chosen cells would bias them; such runs are never post-blocked); diagnostics,
  5(a) checks and arm studies may choose. tools/diag-batch.sh now takes an external opponent from the benchmark manifest
  and an optional side (`<bot>:<opp>:<map>:<seed>[:<side>]`; side B swaps the teams; the external bot is silenced;
  replay names carry __bot<side> so the census tools read our side); DRY=1 and regression tests.
- **g4relay2 Gymhgy delivery: FAIL** at 96 cells (INCONCLUSIVE at 24 and 48): relays fire in 96% of games, but captures
  1.68 vs 1.65 (+2%, bar +20%, -2.0 SE); kills guard PASS. The 5(a)'s +54% on 8 cells was noise. Relay line closed against
  Gymhgy (two attempts: g4relay, g4relay2). A Gymhgy loss study (workflow: five lenses, synthesis, critic) is running to
  find levers and chosen-map cells for diagnostics (PROMPTS 178).
- **Gymhgy loss study** (workflow: five measurement lenses over 1,174-1,507 games and up to 981 replays, synthesis,
  critic; reports in research/gymhgy-study-2026-10-04/, summary in research/CRACK-GYMHGY.md). The critic rejected seven of
  the synthesis's claims (unopposed captures are the relay's base rate; the centre-crumb class effect is real but not
  caused by crumb share; earlier CAPTURING already failed in the wrong direction). Corrected top lever: a late level farm
  for the level-sum tiebreak (we lose 0-0 level sums 28-48 by a median 17 levels and never dig late). Side A deficit vs
  Gymhgy (-4.6 points) is not ours: vs Cyril side A is +2.1.
- Arm **g4farm** (C.LEVEL_FARM: from r1500, flag counts level, idle ducks below 15 build XP dig checkerboard tiles on our
  half away from our flags, reserve 300; indicator lf; census digsLate, levelGain1500). Chosen-map 5(a) vs Gymhgy (owner
  PROMPTS 178): DefaultSmall A/B, EndAround A/B, GravitationalWaves A, Fusbol A, Hurricane A, control Mountain A, seeds
  781101/781102, g_iter4 on the same cells; plus a same-cell replay to test Gymhgy's determinism. g4crumb chosen-map
  diagnostic on centre-crumb maps (GaltonBoard A/B, AceOfSpades A/B, KingQuacksCastle A, HungerGames A, StarryNight A,
  control MIT B) queued before it.
- **g4crumb chosen-map 5(a) vs Gymhgy (centre-crumb maps, 16 shared cells, seeds 781001/781002): wins 7 vs 1** (all 6
  discordant pairs for g4crumb, sign p ~0.03; GaltonBoard x2, HungerGames, KingQuacksCastle, MIT, StarryNight). Crumbs
  r201-400 ours 10,950 vs 4,181, Gymhgy's 13,725 vs 18,481; its captures by r600 1.56 vs 2.19; stuns triggered 282 vs 137;
  games 1,396 vs 872 rounds. The study's critic had found no between-map link of crumb share and wins; the same-cell
  pairing shows a large effect on this class. New tools: scrim.sh MAPFILE and delivery-gate.sh DGMAPS (map-class blocks;
  never ladder games), census gathered201to400, tools/maps-centre-crumb.txt (the 17 maps). Delivery pre-registered:
  `DGPOOL=Gymhgy.v10official DGMAPS=tools/maps-centre-crumb.txt DGTAG=-gymcrumb BASE=g_iter4 tools/delivery-gate.sh
  g4crumb 'rel:gathered201to400>=1.5 nw:kills>=0.95 mean:overruns<=0'`.
- g4farm 5(a) (16 chosen cells vs Gymhgy, g_iter4 on the same cells): the farm barely fires (digsLate 0-11 a game,
  levelGain1500 unchanged): the late bank sits at 160-260 crumbs, under the 300 reserve, because traps spend the rest.
  Also: **Gymhgy is deterministic under a fixed engine seed** (the same g_iter4 cell played twice gives identical
  replays on DefaultSmall and EndAround), so paired cells against it are exact for code that changes nothing. Second
  attempt **g4farm2**: FARM_XP 5 (level 1 for every duck: ~250 digs, ~50 levels, the passive income of r1500-2000),
  no reserve; 5(a) on the same cells queued.
- g4crumb map-class gate (Gymhgy, centre-crumb maps) at 48 cells: signature PASS (gathered201to400 8,538 vs 4,885, +1.2 SE
  past x1.5), kills guard INCONCLUSIVE with kills UP 39% (335 vs 241): extending to 96. Two tool defects surfaced:
  (1) the nw guard's INCONCLUSIVE rule used 2 SE alone and ignored the direction of the difference; it now uses the worst
  plausible drop (lower 2-SE bound of arm - base, as a share of the base), tests added; (2) **audit MEAS3 fixed**: only
  17 of 24 and 26 of 48 cells paired, because a 17-map class has 34 distinct cells and repeated cells overwrote each
  other. Replay names now carry the engine seed (opp__map__s<seed>__bot<side>); delivery-check and eval-paired pair on full
  names when both sides have seeds and on seedless names against older runs; arm-deltas and premise.py read both forms;
  regression tests. Synced to the VM after the running gate finishes.
- g4crumb map-class gate at 96: INCONCLUSIVE (gathered201to400 9,006 vs 5,476, +0.9 SE past x1.5; kills +23%, guard
  INCONCLUSIVE under the old rule) on only 34 shared cells (MEAS3 collisions). **VOID**: it rested on the two tool defects
  fixed above (AUDIT_PLAYBOOK step 1). Re-run with the fixed tools on a fresh seed: `SEED=919191 DGPOOL=Gymhgy.v10official
  DGMAPS=tools/maps-centre-crumb.txt DGTAG=-gymcrumb2 BASE=g_iter4 tools/delivery-gate.sh g4crumb ...` (same bars).
- **g4farm2 5(a)** (16 stalemate cells vs Gymhgy): farm digs 63 a game (0 where captures are not level), level gain
  r1500-2000 37 vs 27 (EndAround +25-42, Hurricane s1 +25, Fusbol s1 +22); wins 6 vs 5: EndAround three level-sum losses
  became wins; Hurricane s2 and Mountain s1 became losses (level gain flat: digging takes the action a heal would use).
  Delivery pre-registered on the 23-map stalemate class (tools/maps-stalemate-gymhgy.txt): `DGPOOL=Gymhgy.v10official
  DGMAPS=tools/maps-stalemate-gymhgy.txt DGTAG=-gymfarm BASE=g_iter4 tools/delivery-gate.sh g4farm2
  'rel:levelGain1500>=1.25 nw:enemyCaptured<=1.1 mean:overruns<=0'`.
- **g4crumb Gymhgy delivery on the centre-crumb maps: PASS** (re-run, seed 919191, 48 cells all paired): gathered201to400
  8,371 vs 4,423 (+2.6 SE past x1.5); kills 290 vs 228, guard PASS (worst plausible drop 5%). The filler now pairs
  g4crumb with g_iter4 against Gymhgy on random maps and sides (the victory read; CLAUDE rule 14); band guard and band
  test queued.
- g4farm2 stalemate-class gate: INCONCLUSIVE at 24 cells (levelGain1500 45 vs 36, +27%, bar +25%, +0.3 SE; enemy captures
  guard PASS); extending to 48.
- **g4farm2 Gymhgy stalemate-class gate: INCONCLUSIVE** at 24, 48 and 96 cells (96: levelGain1500 43.1 vs 35.0, +23%, bar
  +25%, -0.5 SE; enemy-captures guard PASS). Parked, not closed: the farm adds ~8 levels a game late against a median
  level-sum margin of 17. Stronger doses (FARM_ROUND 1200, FARM_XP 10) are the next variant if revisited.
- g4crumb band guard: PASS (24 cells: kills 572 vs 510, worst plausible drop 6%; enemy captures 1.54 vs 1.88). Band test
  running; the Gymhgy paired filler starts when the queue is idle.
