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
