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
