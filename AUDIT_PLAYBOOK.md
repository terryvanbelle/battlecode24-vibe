# Correctness audit of the bot and the measurement pipeline, with follow-through

You are on a long-running bot-training project (e.g. a Battlecode season). Progress has stalled, or a basic capability just turned out broken. Your job:

1. **Audit** the bot **and** the measurement pipeline for correctness, at the level of the defect found: read-only, every finding verified by an agent trying to refute it.
2. **Follow it through** to a measured gain and permanent guards.

The audit alone is worth little; the gain comes from the follow-through, in this order.

**Where this comes from (Battlecode 2024).**
- About 50 experiments over two days produced no gain.
- The owner asked how symmetry detection worked. From the code: it narrowed the candidates with the broadcast hints of rounds 1-3, then picked one in a fixed order; a terrain check existed, uncalled. It was wrong in about 30% of games.
- The owner: "This is basic stuff", "do an audit to determine if anything else is broken", "add more tests to keep this from happening again".
- The audit used 8 agents, ran no games, and confirmed 41 of 42 findings.
- The finding most consequential for decisions was a tool defect: paired tests never shared the engine seed, so identical code flipped 15-29% of paired games; most gained/lost tallies behind earlier verdicts were engine noise (diluted, not biased). The one critical bot defect was an alert that was on almost all game.
- One build combining the critical and major bot fixes (and one minor) beat the incumbent: +33 net wins over 234 seed pairs (p < 0.001), +29 over 239 on fresh seeds; rating ~1762 to ~1918, rank 28 to 13.
- With no targeted tactic it beat an opponent that rush and flank builds had failed to move (seeded pairs +18 of 80; the owner then declared it defeated). The crack was in our own basics.

*(2024: ...)* marks an example, not an instruction: replace it with this year's equivalent. *(installed 2024)* and *(recommended; not done in 2024)* say whether 2024 actually did a step.

## 0. Parameters (fill these in first)

| Parameter | Role | 2024 example |
|---|---|---|
| REPO | project root | `~/projects/vibe/2024` |
| INCUMBENT | frozen copy of the current best build | `src/g_iter1` |
| WORKING | the editable bot; every change sits behind an on/off switch, and with all switches at their defaults it plays exactly like INCUMBENT | `src/bot`, switches in `C.java` |
| AUDIT SCOPE | INCUMBENT, WORKING at its defaults, every live arm with its switches on, any in-progress fix for TRIGGER. A defect that lives only behind a switch is still a finding | `src/g_iter1`, `src/bot` and its arms, `tools/` |
| RULES | rules summary, each rule checked against the engine | `RULES.md` |
| ENGINE | engine API or source, decompiler, compute-cost table | `engine.jar` via `javap`, `MethodCosts.txt` |
| REPLAY READER | a game as text: summary, unit life, board at a round, logs, shared state, compute, navigation | `tools/replay-dump.sh` |
| REPLAYS | existing replays of our builds; test fixtures | `diag/**/*.bc24`, `test/fixtures` |
| PIPELINE | every tool a decision rests on, and its tests: runner, gauntlet, recorder and labels, rating fit, census, paired evaluator, delivery checks, band test, filler and tallies, queue, pruning | `scrim.sh`, `gauntlet.sh`, `elo.py`, `eval-paired.py`, `delivery-gate.sh`, `filler-pair.sh`, `vm-queue.sh`, `test_tools.py` |
| PAIRED TEST; DELIVERY GATE | candidate vs INCUMBENT on shared cells; the pre-test check that a mechanism fires and the basics hold | `band-test.sh` + `eval-paired.py`; `delivery-gate.sh` |
| BASICS | the foundations | symmetry, movement, combat, economy, bytecode, exceptions |
| METHOD, HANDOFF | year-agnostic training algorithm; state file | `TRAINING_ALGORITHM.md`, `HANDOFF.md` |
| SCRATCH | where agents may build offline harnesses | session scratchpad |
| TRIGGER | the basic defect: what, how found, how often wrong, any bar the owner set | the symmetry guess |

**HARD RULES** bind every agent. Copy the project's own, naming every forbidden path. The 2024 set:
- Never read other teams' source code (a named directory); watching their games is allowed. Never read this year's post-mortems, first- or second-hand.
- Ladder games against external bots are scrimmages on random maps and sides; diagnostics may choose maps and sides (owner, 2024 prompt 178), and those games stay out of the ladder.
- Games in volume run only on the compute machine, through its queue; the session machine plays one diagnostic game at a time.
- Record every owner prompt verbatim. Run the unit tests after every change. Push after every commit.
- No statistical test of an arm until a diagnostic game shows its mechanism firing and its delivery gate passes.

## 1. When to run

Make the audit the first step of plateau escalation, before ablations, sweeps, structural changes or a rewrite. Run it when any of these holds:
- **A basic is found broken.** Treat it as a sample: one "this is basic stuff" defect means there are others. Do not fix the trigger alone and move on. **Audit the trigger's own repair too.** *(2024: the symmetry repair arm still handed consumers a guess while undecided, ignored the flag ids, and passed a gate that accepted 10% wrong.)*
- **The owner asks "how does X work?"** Answer from the code, not docs or memory. "It guesses" where an exact method exists is the trigger. *(2024: this found the symmetry defect.)*
- **Many arms in a row read neutral or rejected**, across areas *(2024: about 50)*. An arm is one candidate change tested against the incumbent.
- **Paired tests cannot separate arms from noise:** arms disagree with the incumbent about as often as identical code disagrees with itself, or nobody has measured that on the verdict harness.
- **Results stop making sense:** ablations of established features read neutral; instruments disagree; a gate passes a build you know is wrong; the rating stays flat across accepted changes.
- **A basics bar fails, or is not measured.**
- After a rewrite or large merge, and once a season after the main features exist *(recommended; not done in 2024)*.

**State the bar with the trigger.** A quantity that can be determined exactly after a bounded number of observations, and is not, is a bug. *(2024 owner: "You should be able to determine symmetry 100% accurately after a limited number of observations. If you can't then that's a bug.")* Compute the achievable bound offline over every map and side; set the bar from it, not from the current build's rate.

Start no new strategy arms until the §3 identity control passes; any verdict before it is suspect.

## 2. The audit: one read-only workflow

**Shape** *(2024: one workflow, 8 agents)*: five lens auditors in parallel; two new verifiers, each refuting half the findings; one synthesizer. Use a workflow tool if available, else subagents.

**Budget:** hours and ~2M tokens *(2024: 4.2 h, 1.78M)*. It needs no compute machine: keep the queue busy meanwhile (trigger-fix diagnostics, controls); never idle waiting.

**Check the corpus first;** with no games, measurements rest on existing files. Ensure full replays (not just results) of INCUMBENT and each live arm on diverse maps (small, large, walled, water), both sides, including losses and captures against us, plus the latest census and survey; queue a diagnostic batch for gaps. *(2024: several findings stayed "frequency unmeasured" for lack of a replay.)*

**Allowed:** read code; run REPLAY READER on existing replays; decompile the engine; analyse existing results; build offline harnesses in SCRATCH *(2024: a mock controller driving the real bot classes; scans of all 78 engine maps, both sides)*. **Not allowed:** edits, commits, new games, the compute machine.

### 2.1 Context block (fill it in; it opens every agent's prompt)

```
Repository: <REPO> (<GAME> <YEAR>, engine <VERSION>). The incumbent bot is the frozen package <INCUMBENT>
(<WORKING> with every switch at its default plays identically to it). AUDIT SCOPE: <AUDIT SCOPE>; a defect that
lives only behind a switch is still a finding. <RULES> is the engine-checked rules digest; the engine API is in
<ENGINE> (inspect it with <DECOMPILER>); compute costs are in <COST TABLE>.
Replays you may read with <REPLAY READER> (modes: <MODES>): local replays in <REPLAYS> (games of our builds, the
incumbent among them) and <FIXTURES>.
WHY THIS AUDIT: <TRIGGER>. "This is basic stuff." Find anything else that is broken at that level: code that never
runs, rules misread, data written but never read, behaviour that contradicts the intent stated in comments,
instruments that mismeasure.
HARD RULES: <HARD RULES, naming every forbidden path so no recursive grep or find enters it>. READ-ONLY: do not
modify any project file, do not commit, do not run new games, do not touch <COMPUTE MACHINE>. Offline harnesses in
<SCRATCH> are allowed (mock controller, scans over every engine map, decompiled engine classes, analyses of
existing results).
Report findings only, each with evidence (file:line, the replay command you ran and its output, the RULES/API line
it contradicts, or harness output) and the impact, quantified (games, rounds, units, share affected).
```

### 2.2 The lenses (one auditor each: context block + lens text)

**1. Dead and unreachable code**
```
LENS: dead and unreachable code. Every method in <AUDIT SCOPE>: who calls it? Branches that can never fire given
the constants in <CONSTANTS FILE>; counters that never increment (check log/indicator output in replays);
shared-state slots written but never read, or read but never written; unused constants; methods returning the
same value on every path; comments promising behaviour the code lacks; mechanisms whose trigger never occurs on
the real map pool (dead in practice).
```
*(2024: the terrain symmetry check had no caller; two trap mechanisms almost never fired, so their ablations compared near-identical builds.)*

**2. Rules and API**
```
LENS: rules and API. Check every game-mechanic assumption in <AUDIT SCOPE> against <RULES> and the engine API:
distance metrics and radii, cooldowns, the legality of each action, the objective's lifecycle (pickup, drop,
return windows, capture, removal), spawning, special actions (cost, preconditions, trigger radii), terrain
changes, resources, upgrades and their timing, phase restrictions, map edges, and what engine-issued ids,
broadcasts and orderings actually encode. Anything that misreads a rule or can never succeed (e.g. an action that
is always illegal) is a finding, and so is information the engine gives away that the bot ignores.
```
*(2024: an enemy flag's id was its spawn centre's index, stored and never decoded. One sighting leaves only the true enemy spawn set in 418 of 468 cases, never ruling out the truth.)*

**3. Shared state and coordination**
```
LENS: shared state and coordination. <COMMS FILE>: slot layout and encodings (range on the largest map), index
claiming across deaths and respawns, staleness and clearing, same-round write races, registries of enemy objects
(ids, states, clearing when the object or its tracker dies, moves or is captured), alerts about our own assets
(real trigger versus documented meaning), merged team knowledge (e.g. inference candidates). Find values that go
stale forever, collide, are never cleared, or mean something other than the schema says.
```
*(2024: the own-flag alert fired on any enemy seen near the flag, fresh in 1057-1686 of 1800 rounds; it switched off every defender and parked units on the flag tile up to 1238 rounds. "Carried by us" never expired when our carrier died; a dropped flag's tile outlived its return home.)*

**4. Behaviour in games**
```
LENS: behaviour in games. In the local replays of every build in <AUDIT SCOPE> (either side), look for basic
failures: units idle or stuck for long stretches, oscillation, freezing on enemies they cannot reach, units not
spawning when they could, exceptions, compute overruns and near misses, resources floating unspent, defenders not
defending, the army parked or marching to a wrong place (e.g. wrong enemy bases from a wrong inference), special
actions placed where they cannot trigger, upgrades not bought, objectives in reach never taken. Quantify each with
replay output.
```
*(2024: any visible enemy took the whole turn, even behind a wall: 16-36% of enemy-in-view robot-rounds frozen on walled maps, one robot for 164 rounds. Navigation dropped wall-following whenever the target moved.)*

**5. The measurement pipeline**
```
LENS: the measurement pipeline our decisions rest on: <PIPELINE>. Look for paired cells that do not share the
engine seed, wrong side/team mapping, mis-paired cells, silently dropped games (name collisions, results read
before a job finished), wrong denominators, results credited to the wrong build (keyed by name, not code), caches
keyed incompletely or used for the wrong pool, gates that score a lucky guess as correct, counters of the wrong
event, fits that stop before converging, swallowed exit codes, guards that never run, anything that could make a
decision wrong. From existing results, estimate how often identical code flips a paired cell on each verdict tool
path. Use the tool tests as a guide but do not trust them.
```
*(2024: unshared seeds, the most consequential for decisions; a symmetry metric that scored a guess as correct, gated at 10% wrong; repeated cells overwriting each other's replays; results keyed by package name; a rating fit stopping 17-27 points low; a queue logging "exit 0" for every job.)*

**Extra lenses** *(recommended; not done in 2024)*, folded into the five for a small bot.

**6. Beliefs about hidden state** (own agent, so the next symmetry guess need not be named by the trigger)
```
LENS: beliefs about hidden state. For each thing the bot infers (map symmetry, enemy base locations, enemy
strength, resource locations): how is it formed; is it observed or guessed; can any evidence path rule out the
truth; while undecided, do consumers get a single guess or every remaining candidate; what evidence does the team
already hold and not use? With an offline harness over every engine map and both sides, compute what a correct
method can decide and by when. That bound is the bar.
```
*(2024, the audit's harness: 268 of 298 wrong-symmetry cases fall before setup ends on immutable evidence; the other 30, on 15 maps, need one post-setup sighting. While undecided, act on all remaining candidates.)*

**7. Budgets, extremes and the test harness:** worst-case compute (largest map, most units) and low-budget behaviour; exception handlers that drop the rest of a turn; edges, corners, extreme map sizes, first and last rounds, tiebreaks; test fakes whose defaults differ from the engine. *(2024, found later by the follow-through's tests: the fake controller called every tile off the map; the bytecode clock read 0 outside the engine.)*

### 2.3 Finding schema (every field required)

`id` (stable, lens-N), `title`, `severity` (critical | major | minor), `where`, `evidence`, `impact`, `fix`, `regression_test` ("a test that would have caught it and will keep it fixed"). Impact covers games and decisions: a defect that cost no games but voided an experiment is major *(2024: the trap no-ops, "Major (decisions)")*. A finding without a fix and a regression test is not finished.

### 2.4 Adversarial verification

Each half of the findings goes to a new verifier with the context block plus:
```
You are an adversarial verifier. For EACH finding below, try to refute it: re-read the code and the rule, re-run
the replay command and any harness, check whether the behaviour is intended or harmless. Mark real=true only if you
reproduced the defect yourself; give the severity you would assign, and correct any figure that comes out
differently. Default to real=false when you cannot reproduce it. Echo each finding's id.
```
- Verdict: `id`, `title`, `real`, `reproduced_how`, `severity` (critical | major | minor | not-a-bug), `note`. Confirmed only if `real` and not `not-a-bug`.
- Match verdicts by id; a finding without one is UNVERIFIED, never dropped; compute the rejected list by id *(recommended; not done in 2024: verdicts were matched by exact title, and a rejected list computed by object identity told the synthesizer all 42 were rejected or unverified)*.
- Keep every correction (downgrades, revised figures, stale claims) for the appendix. *(2024: 41 of 42 confirmed; several severities and figures corrected.)*

### 2.5 Synthesis

One agent gets the context block, the confirmed findings with verdicts, and the ids and titles of the rejected and unverified ones, then:
```
Write the audit report (markdown) from the confirmed findings below: deduplicate, rank by impact on games and on
decisions (critical, major, minor), and for each give: what is broken, evidence, impact, the fix, and the regression
test that keeps it fixed. Separate (A) bot behaviour defects (each fix must be tested as a switch arm against
<INCUMBENT> before adoption) from (B) tool/measurement defects (fix directly, with a test). End with a short list of
systematic tests that would catch this whole class of problem in future (e.g. a dead-code check over the bot,
invariants checked on every replay). Return only the markdown.
```
The report, in order: (1) method line (read-only; sources and harnesses; no games; every finding reproduced independently); (2) summary table (ID, severity, class Bot/Tool/Instrument, one line); (3) the trigger's requirement: offline bound and bar; (4) **A, bot defects** and **B, tool and instrument defects**, each with Where, Broken, Evidence, Impact, Fix, Regression test (a tool defect voiding past verdicts ranks with the worst bot bug); (5) systematic tests for the class; (6) appendix: corrections, figures not re-derived, every rejected or unverified finding by name.

Save it as a dated research document *(2024: `research/AUDIT-2026-10-02.md`)*, log it, commit and push.

## 3. Fix the measurement first (list B)

If the instruments are wrong, every verdict is worthless, bot-fix verdicts included. Fix the tool defects that decide verdicts and prove them with the identity control before judging any bot fix; fix the cheap rest in the same pass. *(2024: seed pairing and the symmetry instrument were fixed with tests and proven first; a queue and a runner defect were fixed later without tests; several minor ones stayed open.)*

1. **Fix each tool defect with a test.** Shell tools get stub-engine tests of their failure paths: exit codes, repeated cells, same-second runs *(recommended; not done in 2024)*.
2. **Run the identity controls** through the compute queue and log them:
   - a byte-identical copy of INCUMBENT against INCUMBENT on shared engine seeds reads 0 discordant pairs **on every verdict tool path** (paired test, filler, delivery gate, ablation) **and every verdict opponent pool**; a pass on another harness proves nothing here *(2024: an earlier copy read 0-0 on a sibling harness keyed on the seed while the band and filler tools never shared seeds; after the fix, 0 of 80 (40 band, 40 vs the key external bot), against 15-29% of cells before)*;
   - the same seed twice gives an identical replay;
   - a logging- or indicator-only change matches the base replay except in that output *(2024: the tracking sensor, on, played identically to the incumbent)*.
3. **Replace gates that cannot tell deciding from guessing** with columns from replay truth *(installed 2024: symWrong, the truth was ever ruled out; symDecidedRound)*. A basic the census does not measure reports FAIL ("not measured"), never a silent pass *(installed 2024)*.
4. **Add provenance:** every result row carries the engine seed *(installed 2024)* and a code hash; tools refuse to pool across hashes; PASS files record the source hash; every census reports "K of M games" and fails on a mismatch; every fit asserts convergence *(recommended; not done in 2024)*.
5. **Correct the record.** Mark VOID every verdict resting on a broken tool or a mechanism that never fired, in the log and every document citing it; regenerate or delete stale charts and documents. *(2024: two trap ablations voided; every pair before the seed fix reclassified as unseeded.)*

## 4. Each bot fix behind its own switch (list A, in rank order)

1. **Triage by severity.** Every critical and major bot defect, and any minor one touching a basic, gets its column, switch and diagnostic now and goes into the combined build. For a defect that only voids decisions, mark its verdicts VOID and decide the mechanism's intent. Other minor defects go on the open list (§8) and are fixed before any arm depending on them is judged. *(2024: the build carried the alert, flag-id, observed-symmetry, reachability, registry, navigation and minor edge-ping-pong fixes; the trap no-ops were voided; two minor defects were fixed later, when arms needed them; others remain open.)*
2. **Measure before fixing.** Add a census or contract column measuring the defect from replay truth; confirm it on the incumbent's replays, and run the basics battery on the incumbent. *(2024 columns: symWrong, symDecidedRound, alertNoThreat, maxParkOnHome, efStaleCarry, efStaleLoc, stillPost, exceptions. On one incumbent replay 83% of alerts had no enemy near any flag home; symmetry read 66-72% correct even on a metric that credited guesses.)*
3. **Put the fix behind a switch, off by default.** Off, WORKING plays identically to INCUMBENT: same bytecode, or an identical replay on the same seed.
4. **Write the regression tests from the audit:** unit tests on the mock controller *(installed 2024: AuditTest)*; property tests over every engine map and side where logic depends on the map *(recommended; not done in 2024: SymTest, written with the trigger fix before the audit, uses 300 random synthetic maps; the engine-map scans stayed in scratch)*. A test exposing a test-harness bug is a tool defect: fix the harness.
5. **Add an arm-intent line** per switch the arm sets, checked by the unit tests against the built source *(installed 2024 after a moved comment made the edit meant to turn a switch on miss, so the arm ran with it off)*.
6. **Run a diagnostic batch:** a handful of cells, exact seed-paired with the byte-identical copy. Judge only the mechanism: it fires and the defect's column falls; wins on a handful of cells are noise. *(2024, 7 cells per fix: alerts with no threat 305-2324 a game to 0-81; longest stand on our own flag tile 162-1466 rounds to 1-121; post-setup stillness on one walled map 59-60% to 27-37%.)*
7. **Keep correct fixes that show no gain alone.** They are foundations: carry them into the combined build and ablate later. *(2024: the symmetry repair alone went 63-57 head to head, as the incumbent barely used symmetry; the navigation fix showed nothing in its diagnostic.)*

## 5. One combined build, judged exactly, then confirmed

1. **Build one arm with every fix triaged in** (§4.1), with its arm-intent lines.
2. **Refresh the control:** if the incumbent's control runs drew random engine seeds, run a fresh seeded control on the same cells.
3. **Pass the DELIVERY GATE first:** defect columns at target, 0 overruns, 0 exceptions, basics passing. *(2024: symWrong 0; alerts with no threat 23 a game against 549; stale-carry rounds 18 against 498.)*
4. **Write down the decision rule and confirmation seeds before running.** *(2024: pass if all-cell net ≥ +2 SE, or the capture difference against the stronger opponents ≥ +2 SE with all cells no worse.)*
5. **Run PAIRED TEST against INCUMBENT** on shared engine seeds, against the external bots rated nearest ours. Never read a running batch.
6. **Confirm on fresh seeds** against the same bars; promote only if both pass. *(2024: +33 over 234 pairs, +4.4 SE; confirmation +29 over 239; against the key opponent +18 over 80 seeded pairs.)*
7. **Promote:** freeze the snapshot; make the fixes WORKING's defaults; relabel the arm's ladder and filler games under the incumbent's name (same code), refit ratings, regenerate every chart; update handoff, README and project rules in one commit; push and verify via the remote API that it landed; run ladder blocks; update the tactics record. *(2024: the owner caught 600 games of the promoted build filed under the arm's name, invisible to the charts.)*
8. **Ablate the fixes one at a time on seeded pairs**, and watch margins they thinned. *(2024: the navigation fix ablated neutral and was kept; the reachability search left a thin bytecode margin, and the first ablation block hit an overrun.)*

## 6. Permanent guards (install them in the same pass)

**Basics battery on every build and test block** *(installed 2024)*:
- **Absolute bars:** inferred quantities never wrong and decided within the offline bound; 0 overruns; 0 exceptions. *(2024: symWrong = 0; symmetry decided by round 201 in ≥ 95% of games on maps where possible, by round 400 in ≥ 90% on the rest.)*
- **Relative checks** against a same-block control: movement (time standing still); combat (kills per death, traps hit); economy (resources gathered, left unspent).
- **Every guard, in the battery and the DELIVERY GATE, fails only when worse by more than 2 SE** (paired where cells are shared); never a point-ratio guard on a small block. *(2024: the battery used 2 SE from the start; the gate's `rel:kills>=0.95` on 24 cells failed about a third of neutral arms and gave way to `nw:` guards.)*
- **One exception** *(installed 2024, owner rule)*: an arm that passed its pre-registered victory read against the ladder target is not vetoed by a relative check its tactic pays by design; log the cost. Absolute bars still stop it.
- A failed bar stops all work built on that basic until fixed.

**Unit tests:** a dead-code check for uncalled methods and unused constants *(installed 2024)*, extended to write-only fields, constant returns and unreachable branches *(recommended; not done in 2024)*; the arm-intent check *(installed 2024)*; indicator and log strings within the engine's limit, and a note set on every turn exit path *(recommended; not done in 2024)*.

**Shared-state contracts on every replay:** each slot's documented meaning becomes an assertion against replay truth; a violation fails the delivery gate. *(2024: columns for the alert and flag registry installed; a gate-failing contracts mode recommended; not done.)*

**Proof the mechanism fires before any ablation, tuning or gate:** each switch declares its firing column, and the tools refuse a test when it fires in too few games. *(2024: delivery `fire:` checks and the band test's refusal without a PASS installed; refusing unfired ablations, which would have voided the two trap ablations, recommended; not done.)*

**Property tests** over every engine map and side, and a rules-to-code test per rule that limits game state *(recommended; not done in 2024)*.

**Provenance** as in §3.4, and stub-engine failure-path tests for every shell tool *(seeds installed 2024; the rest recommended; not done)*.

**Identity controls rerun** on every verdict path and pool when the runner, cell generator, census or evaluator changes *(recommended; not done in 2024)*.

**Project rule:** "Basics first: when progress stalls, check the basics first" *(installed 2024)*. *(2024 owner: "The basics (symmetry, movement, combat, economy) are the foundation for everything else. If they're not solid, nothing built on them will be effective. Make sure they are and remain solid. If you're having trouble making progress, check your basics.")*

## 7. Revisit what was discarded

1. **Triage every closed direction:** did it run through a now-fixed defect? Was its verdict within noise? Does it fit the strategy?
2. **Weigh old verdicts by how they were measured:** exact-harness verdicts stand unless a fixed defect changed what the switch does; unseeded verdicts are diluted, not biased: |z| ≥ 2.5 stands, |z| < 2 is no evidence either way.
3. **Queue survivors as arms on the new incumbent**, each through a diagnostic game, delivery gate and paired test, under a **pre-registered re-test rule** *(2024: adopt as a stack candidate at all-cell net ≥ +2 SE, or upper capture delta ≥ +2 SE with net ≥ -5; confirmation seeds before stacking; else park)*. A failed delivery gets one second attempt, only after a trace explains it and on a fresh seed; close for good after two delivery failures or a paired all-cell net ≤ -2 SE. Never read a gain off a small block where the base happened to score low *(2024: on one 24-game block the base won 21% against 43% overall, and every arm "gained", 7-8 wins vs 5, because any code change re-draws the games: regression to the mean)*.

Expect most to fail again; the point is that targeted work can now succeed. *(2024: most re-tests did not deliver, but on the fixed base a targeted counter to one strong opponent worked.)*

## 8. Done when

1. The dated audit report is committed, logged and pushed.
2. Every tool defect is fixed with a test (cheap ones never deferred) or is on the open list (item 7); identity controls read 0 discordant on every verdict path and pool, logged.
3. Every triaged bot defect has a column, switch, regression and property tests, arm-intent line, and diagnostic numbers showing it firing.
4. The combined build has a delivery PASS, a paired result, a fresh-seed confirmation and a promotion commit, or was rejected with ablations.
5. The guards are wired into the unit tests, basics battery and delivery gate; the basics-first rule is in the project rules.
6. Void verdicts are marked, the re-test queue is written, no stale document or chart remains.
7. Every finding not fixed is listed in HANDOFF (ID, severity, status, owner, waive reason) and checked at every task check until fixed with a test or explicitly waived *(recommended; not done in 2024)*.
8. METHOD is amended in the same commit *(recommended; not done in 2024)*: the audit is the first plateau-escalation step and any broken basic triggers it; the mirror gate reads "one engine seed per cell, shared by both builds"; day-one setup adds an identity control on the verdict harness and the basics battery; every build runs the basics battery, dead-code check and arm-intent check.

Do not skip a phase because the previous one looked decisive.

## Why it worked (keep all of these)

- **The trigger was treated as a sample:** "find everything else broken at that level".
- **The instruments were audited, not just the bot.** The defect that mattered most for decisions was measurement noise.
- **Strict evidence:** read-only; every claim backed by file:line, a replay command and output, a rule line, or harness output.
- **Verifiers rejected by default** and kept only defects they reproduced.
- **Every finding carried its fix and regression test**, so the follow-through was mechanical.
- **The order was fixed:** (1) the measurement defects that decide verdicts, proven by an identity control; (2) each fix behind a switch, with a column showing it firing; (3) one combined build on exact pairs, confirmed on fresh seeds; (4) guards, so the class cannot return.
- **Basics before tactics.** Single fixes looked neutral; the combined build was decisive and, with no targeted tactic, beat an opponent that targeted builds had failed to move. Tactics on broken basics could not show their worth.