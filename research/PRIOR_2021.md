# What the 2021 practice season (battlecode21-vibe) teaches a new year

Source: `/home/terryvanbelle/projects/vibe/reference/battlecode21-vibe` (read-only clone;
448 commits, 2026-09-16 to 2026-09-23, one human directing one Claude Code session).
All paths below are relative to that repo unless absolute. Season-specific units and
rules (Enlightenment Centers, politicians, slanderers, muckrakers, influence, votes) are
mentioned only as one-liners where a measurement needs them.

Reading order in the source repo, if you go back to it: `CLAUDE.md` -> `METHOD.md`
(portable method, 292 lines) -> `TRAINING_ALGORITHM.md` (the loop, 654 lines) ->
`POSTMORTEM.md` -> `HANDOFF.md` -> `LEARNINGS.md` -> `DESIGN.md` -> `tools/README.md` ->
`TRAINING_LOG.md` (6,006 lines; head and tail suffice, the middle is per-iteration).

---

## 1. The training method and how well it worked

### 1.1 Outcome, in numbers

| measure | value | where |
|---|---|---|
| Duration | 8 days, 448 commits | `git log` |
| Iterations numbered | 0-63 (88 `## Iteration` headings incl. sub-entries) | `TRAINING_LOG.md` |
| Accepted snapshots | 13 (`src/g_iter1` .. `src/g_iter13`); `g_iter1` accepted "by construction" | `src/` |
| Accept rate | ~12 accepts of ~62 real candidates, ~20%; after `g_iter12`: 1 accept in 11 arms | `TRAINING_LOG.md` "Final ledger" |
| Start | 29% on the first random-map scrimmage block (14/48), 7% vs the top bot | log, 2026-09-17 22:00 |
| Finish | **334/432 (77.3%)** on a fixed field of 8 external bots over 9 blocks; 33% vs the top bot, 46% vs #2, 67-100% vs the other six | `POSTMORTEM.md` |
| Elo ladder | rank 3 of 21 rated bots (Elo 1742) after 1,248 scrimmages; 45 of 66 roster bots never met | `progress/ELO.md` |
| Benchmark pool | 96 GitHub repos, ~460 compiled bot packages, 65 name-selected "final" bots | `BENCHMARK.md`, `tools/ladder-bots.txt` |
| Game cost | ~6 CPU-min; 64x64 map 68 s amortised at 6 parallel, 32x32 31 s; ~53 games/h on 8 vCPU; a 200-game SPRT ~3.8 h | log "Engine speed: measured, not assumed" |

The changes that actually moved the external ladder were few and all of one kind:
fixing the opening deployment (+22 pts self-play, ladder 29% -> 40%), removing a rule
that stalled the economy on a non-threat (68.8%), removing three caps/defects that
bound (59.1%, 84.4%, 79.2%), a comms defect that left the broadcast channel silent
(65.6%), and one cheap-defence rule (neutral in self-play, +8 pts on the ladder against
the bot it targeted). Everything that was a *reallocation* of spending was rejected.

### 1.2 The iteration loop (final form, `TRAINING_ALGORITHM.md` sections 4-4.7)

1. **Select a target**: a lost game against a `target`-tier external bot, an absolute
   degeneracy visible in our own replays, or a capability gap. Keep a functional-area
   map; after 3 consecutive rejects in one area the next attempt must leave it.
2. **Trace** the motivating replay with the replay reader before hypothesising.
3. **Pre-register** in the log before touching code: the mechanism, the decision-point
   counter that proves it fired, reachability, trigger frequency, price, ledger check,
   the gate, the falsifier, and (for a numeric change) the dose ladder with a zero arm.
4. **Implement**, one change per candidate.
5. **Diagnostic game first** (`tools/run-dev.sh`, fixed engine seed, one logged game on
   a cheap map where the mechanism can fire) and grep the `@tag` counters. No test
   starts until the counters show the mechanism firing at the claimed rate. This rule
   was adopted after three candidates were implemented, compiled and *entirely inert*.
6. **Gate**: SPRT mirror against the incumbent snapshot on random maps and sides
   (section 1.3).
7. **On accept only**: `tools/snapshot.sh g_iterN`; archetype regression gauntlet;
   a 48-game scrimmage block against externals (= "submitting"); record, Elo, re-tier;
   block study + onset table for the next hypothesis; update log/handoff; commit, push.
8. **Withdraw** if the submitted build's block Wilson upper bound falls below the
   previous submission's point estimate.

Hyperparameters that survived: `MaxRejectsPerArea=3`, `SwingEvery=4` (one structural
attempt per four incremental ones), roster/regression after every accept, an unused-API
sweep at iteration 5 then every 10.

### 1.3 Gates, and how the gate evolved (this is the most transferable lesson)

The project went through four accept instruments in three days. Each replacement was
forced by measurement, and the sequence is worth knowing so a new year skips the first
three:

| # | instrument | why it was dropped |
|---|---|---|
| 1 | 24-game head-to-head vs last snapshot on a 12-map "quick set", both sides, gate +5 wins (2x the measured noise sd of 2.4) | resolves only 18-21% effects; real accepts are 8-10% of cells |
| 2 | 72-cell paired roster check (8 external bots x 4 screen maps x 2 sides) vs the incumbent's record on the same cells; McNemar on flipped cells (net margin ~2*sqrt(D)); futility stop at 36 cells if 3 behind | user rule 2026-09-17: external bots may only be played as random-map/random-side scrimmages |
| 3 | 48-cell "panel": 24 mirror cells + 24 cells vs three own archetypes, gate +5 | eight rejects in a row; 24 mirror cells resolve nothing below ~75%; archetype half capped at +4 because the incumbent already won 20/24 |
| 4 | **SPRT mirror** (`tools/mirror.sh` + `tools/sprt.py`): candidate vs incumbent, random map and random side per game, batches of 16, H0 p=0.50 vs H1 p=0.58, alpha=beta=0.05, LLR bounds +/-2.94, cap 240 games | kept; the very next candidate was accepted at 68.8% |

SPRT policy details worth copying verbatim:
- ACCEPT -> snapshot. REJECT -> revert. Inconclusive at the cap but >= 53% over >= 200
  games -> keep *provisionally* in `src/bot`, no snapshot; build the next candidate on the
  provisional stack and run its SPRT against the *incumbent*; snapshot when the stack
  ACCEPTs; a stack that REJECTs loses its newest member. (Separating a true 55% from 50%
  needs ~800 games; three true 55% changes stack to ~65%, resolvable in 60-100.)
- Random maps, not a chosen set, because the ladder is played on the whole corpus.
- Small maps are used for *diagnostics* only (2.2x cheaper), never for the gate: it
  would bias every decision toward them, and the worst deficits were on large maps.
- `W0`/`L0` resume an interrupted gate without replaying the finished batches.

### 1.4 Paired cells, determinism and the noise floor

- The engine was deterministic apart from a final coin flip on a full tie. Two runs of
  the same build on the same cells flipped 0 of 72. Any code change, even a
  policy-identical one, perturbs event order and flips 11-17% of cells with margins of
  -2..+2. Measured once per epoch with an inert-constant build: that is the noise floor.
  State gates in games on a named cell set; never quote `W-L` as if it were wins.
- The dev runner's engine seed is fixed, so a diagnostic rerun is like-for-like and a
  baseline run of the incumbent on the same seed is what an arm is read against.
  **But a fixed-seed single game is a filter, never a verdict**: iterations 52, 60, 62,
  63 each won their sparring seed(s) and none carried to the gate or the ladder.
- Never read a running batch as a result: games that end early finish first, so a
  batch's early tally is its annihilations. A 0-5 read mid-batch that ended 11-5 cost a
  voided batch.

### 1.5 Ladder / Elo against external bots (`tools/scrim.sh`, `tools/elo.py`)

Rules the user imposed, all of which a real contest also imposes:
- External bots are played **only as scrimmages**: random map from the released corpus,
  random side, rotating opponents (never the same one twice in a row, each at most
  ceil(N/pool) per block). `gauntlet.sh` refuses an external opponent unless `SCRIM=1`
  is set by `scrim.sh`.
- **No external-vs-external games** (VM waste). Elo (`K=32` from 1500) is computed
  from our games only; a bot we never met is unrated. Our team is one player `us`
  across builds (`us:<build>` in `progress/games.csv`), as on the real ladder.
- **No ladder gating of accepts**: you cannot scrimmage without submitting. A block is
  played only by the incumbent or a just-accepted build; a candidate under trial never
  plays externals. Self-play is the whole gate; the ladder is the consequence.
- Challenge pool: first `elo.py --pool 6 --explore 2` (six rated bots just above us
  plus two least-met). Exploration widened the field 9 -> 21 bots and dropped our rank
  4 -> 18 (the ladder becoming accurate), but consecutive blocks no longer shared a
  field. Final policy: a **fixed field** of the 8 most-played rated bots
  (`elo.py --established 8`) so block win rates chain and are comparable.
- Tiers by our win rate (`BENCHMARK.md`): `locked` <20% (score only, no replay/log/
  trace), `target` 20-50% (primary loss source), `peer` 50-90% (regression), `solved`
  >90% twice. The user's stated purpose: time allocation, not a wall. Tier check is
  enforced in tooling (`tools/tier-check.sh`, fail-closed) after one breach.

### 1.6 Benchmarks: found, downloaded and compiled without reading them

- **Found**: GitHub search on "battlecode 2021", "battlecode21", "bc21" and the
  season's unit names (log, Phase 0). 96 repos. No discovery script was committed; the
  search was done by hand with `gh`/web. (A new year should script it: `gh search repos
  "battlecode 2024" --limit 200`, `"battlecode24"`, `"bc24"`, plus unit names.)
- **Downloaded**: shallow clones under `~/projects/vibe/bc21-benchmarks/<owner>_<repo>`,
  outside the git repo, never committed.
- **Compiled without display**: `tools/bench-compile.sh` drives
  `tools/benchcompile/BenchCompiler.java` (one JVM per repo; source roots = parent of
  every dir holding `RobotPlayer.java`, capped at the repo; whole-set compile first,
  then per-top-level-directory passes to a fixpoint with the output dir on the
  classpath). Diagnostics go to a log file only; stdout is one count line. Output:
  `_classes/<owner>_<repo>/` (one dir per repo so packages cannot collide) and
  `manifest.tsv` with `name package classdir repo commit`, name = `<owner>.<package>`.
- **Selected by name only**: `tools/bench-select.py` scores package names
  (`final` > `postqual` > `qual` > `seeding` > version number > `sprint2` > `sprint`),
  excludes test/template/donothing names, one primary per repo (65). All versions of a
  repo are kept as rungs.
- **Duds**: an opponent whose code the sandbox refuses to instrument is recorded as
  `dud`, never a win (`gauntlet.sh`).
- Allowed reading of a benchmark repo: `version.txt`, `gradle.properties`,
  `build.gradle`, `README`, directory listings. Nothing else, ever.

### 1.7 VM usage (`SETUP.md`, `tools/vm*.sh`)

- Two GCP boxes: `claude-driver` (e2-small, 2 vCPU/2 GB) hosts only the session,
  analysis, plots; `battlecode-dev` (e2-standard-8, 8 vCPU/31 GB, 20 GB disk) runs every
  game. Two concurrent games on the driver swapped it into 50-minute games at 40% I/O
  wait; a game JVM is 1.1-1.4 GB.
- Mirrored layout on both machines (`~/jdk/...`, `~/projects/vibe/<year>`,
  `~/projects/vibe/bc<yy>-benchmarks/{_classes,manifest.tsv}`) so `tools/lib.sh` works
  unchanged on either.
- `vm.sh` caches the VM IP and uses plain ssh (gcloud ssh re-pushes keys, 30 s);
  `ensure_vm` starts a stopped instance. `vm-sync.sh` = tar over ssh (no rsync on VM),
  pushes `src tools test progress BENCHMARK.md` every run and JDK/engine/benchmark
  classes when missing. `vm-run.sh <log> '<cmd>'` syncs then `setsid nohup ... & disown`.
  `vm-tail.sh`, `vm-collect.sh <run-id>` (tar back), `vm-stop.sh` (refuses while any
  game runs; VM shared with other years).
- Cap of 7 concurrent games; 6 parallel gave 3.7x, not 6x. Disk fills at 4-8 MB per
  kept replay; prune run dirs after fetching their study; check `df` before a gate.

---

## 2. What worked and what did not

### 2.1 Worked (top 15, with measurements)

1. **SPRT on random maps as the only accept gate** (1.3). Eight consecutive panel
   rejects, then an accept at 68.8% on the first SPRT candidate.
2. **Diagnostic game before any test.** Caught 0-firing mechanisms three times
   (opening capture: 0 fires; garrison guards: posted then walked away; collapse screen:
   397 holds, 0 moves). One logged game (~5 min) vs a wasted night.
3. **Fix the opening.** Four 1-cost scouts then a 107 economy unit at r9, vs opponents
   spending the whole 150 start on a 130 economy unit at r1: +22 pts (71.9%) and the
   first change to carry to the ladder (29% -> 40%). Iteration 34.
4. **Never let a rule stop the economy for something that cannot hurt it.** "Danger =
   any enemy in sensor range" gated all production; a 1-influence scout six tiles away
   switched the economy off. Distinguishing a real threat: 68.8%. Iteration 27.
5. **Remove the constraint that binds before tuning.** Slanderer cap (59.1%), spare
   branch's guard sink (84.4%, the largest), capturer cap + two comms bugs (79.2%).
6. **Instrument what the bot *hears*, not only what it does.** Six intake counters on a
   centre found that a spawn ORDER after every build occupied the broadcast flag, so the
   centre broadcast its map to nobody; captured centres were born deaf. Iteration 48,
   65.6%, ladder 35/48 -> 38/48.
7. **Timestamped knowledge claims** (`MapState.claimEnemy/claimOwn`, round/32 stamp,
   newer wins whoever relays). Fixed 520 units sent at a centre already ours *and*
   home knowing no enemy centre for 1,200 rounds.
8. **Mine every paid-for game** (`scrim-study.sh`, `log-scan.sh`, `econ-scan.sh`).
   Both the `danger` finding and the cap finding came from reading the opponent's side
   of losses already in hand, within an hour, invisible to every aggregate kept.
9. **Correlation/onset as hypothesis generator, diagnostic as filter** (`correlate.py`,
   `onset.py`, `polarity.py`). Within-opponent point-biserial at r200; earliest onset
   first. Correctly ranked real patterns; could not tell marker from lever (see 2.2).
10. **Sparring archetypes that reproduce a specific opponent** (`src/arch_hunt`,
    `src/arch_lemon`; one `ARCHETYPE` switch in `C.java`, set by `snapshot.sh name N`).
    The two that beat the current build were the only readable partners; both
    reproduced the top two bots' loss shapes. `arch_lemon` = hunt + attackers sized to
    the target: 0 centres to 8 by r500, the exact shape of all 12 losses to the top bot.
11. **Unit tests on pure bot logic and on the tuning constants' invariants**
    (`test/bot/*Test.java`, plain mains, no JUnit), plus tests on the analysis
    pipeline (`tools/test_metrics.py`). The integrity check "coverage in [0,1000]" read
    76,165 and exposed a header/value column misalignment that had reversed a finding.
12. **Bytecode monitor in the turn loop from day one** (`Robot.loop`: round number
    before/after `turn()`, `Clock.getBytecodeNum()` near-miss, `@bc` line every 50
    turns) plus `@bcprof` stage profiling. Iteration 49's extra flag reads put the home
    centre over 20k; it silently lost 213-404 rounds per game and a +10% fix gated at
    53% until profiled.
13. **Fixed-field scrimmage blocks** made the first like-for-like ladder comparison
    possible (Iteration 43: 35/48 vs 31/48, losing a game against five of eight
    opponents; direction consistent, so rejected).
14. **Tool-enforced rules instead of reader discipline** (`tier-check.sh` fail-closed;
    `gauntlet.sh` refuses externals outside `scrim.sh`; refuses to recompile a class
    tree games are reading; `scrim.sh` refuses to run when `games.csv` is missing rather
    than silently falling back to the wrong pool).
15. **Snapshot-as-package + `src/bot` byte-identical to the incumbent between
    candidates** (`snapshot.sh` rewrites the package line). Reverting = copying the
    snapshot back with sed; `git checkout` cannot undo a committed change.

### 2.2 Did not work (top 15, with measurements)

1. **Every spending reallocation**: guards scaled to threat, hunters, deposits, surplus
   investment, capture reserve, big guards, rich interceptors: all rejected (e.g. capture
   reserve 41.2%; big guards 4/12 mirror, every loss on votes). Repairs transfer;
   reallocations do not.
2. **The 24-cell / 48-cell paired panels as gates** (1.3): could not accept anything
   buildable. Eight rejects in a row were the instrument, not the bot.
3. **Manufacturing a correlated metric.** Coverage lead correlated +0.42..+0.58 from
   r200; "scouts keep sweeping" raised coverage 50.6% -> 65.9% and centres 2 -> 4 at
   r400 in the diagnostic, then gated 124-116 (51.7%). Scout caps doubled (Iteration
   63): mirror seed won 7-1, ladder 35/48 with the strong bots 2/12. Coverage marks a
   winning position; it is not a lever.
4. **Rebuilding the opponent's allocation as a bot** (`src/arch_big`, their measured
   51/30/19 economy/scout/army split): lost 0-24 to our own bot. The allocation was
   downstream of their advantage.
5. **"Obviously a bug" fixes that lose.** Broadcasting a captured centre so the team
   stops treating it as neutral fixed a measured defect (41 of 54 capture builds walked
   to ground already held) and lost 45.1% alone, because an aborted capturer became a
   guard and kept its influence. Accepted only bundled with a change that used the
   freed capacity.
6. **Unblocking a measured economy pause** (Iteration 39/43): a defect real and large
   (one centre blocked 1,192 of 1,500 rounds, zero economy units on 160k banked), priced
   by mirror 40%, two archetypes (one cannot create the condition, one cannot lose), and
   the ladder -4/48. Feeding units into a siege cost more than the idle bank.
7. **Chip captures** (several cheap attackers at a centre too expensive to buy): 61
   chips delivered, flips fell 14 -> 2, centres 2 vs 6 at r1500.
8. **Holding fresh captures with floors, banks or sentinels** (Iteration 52, 15-33 =
   31.2%): attackers sized to the target simply grew their overshoot.
9. **Target-aware attack** (53): bought every cheap shell, 49 gained / 44 lost, 238
   attackers aborting at contested tiles; null on the fixed seeds when switched on (61).
10. **Reinterpreting a mirror null as "aligns us with the winners"**: forbidden after
    the counter-example in item 5; METHOD 5b now requires a pre-registered second arm
    against an archetype that has the property, decided *before* the gate runs.
11. **Reading a locked bot's replays by accident** (disclosure, 2026-09-20): the tier
    table had gone stale (`bench-roster.py` crashed on a nonexistent column) and the
    reader did not check. Fixed by tooling, not by resolve.
12. **A wait predicate that matched nothing** (`pgrep -f gauntlet.sh` while the script
    re-execs as `.reexec-gauntlet.<pid>`): the queued SPRT started immediately, deleted
    `build/classes` under 48 running games, and voided a ladder block.
13. **Challenge pool silently falling back to a fixed roster** for three days because
    `progress/games.csv` was never synced to the VM: every block challenged the wrong
    bots; "nearest above us" never actually ran.
14. **A cost-aware pathfinder written before measuring**: our units averaged 86 moves
    each vs the opponent's 84 and stepped onto bad tiles half as often as the bot beating
    us. Reverted unrun.
15. **Two of the first archetypes did not do what they were named for** (a "rusher"
    whose base never reached the rush price; an "expander" that took one centre, filled
    its own cap and idled 1,300 rounds, then, fixed, took 8 centres and lost on votes
    because it never bid). A partner is an instrument: run it on a known case first.

---

## 3. Tooling worth stealing or adapting

All under `tools/` in the source repo. "Year-specific" means the script parses
season-specific fields (replay schema, unit names, engine flags); the *design* is
portable in every case.

| script | purpose | deps | year-specific? | notes for reuse |
|---|---|---|---|---|
| `lib.sh` | JDK/classpath, `run_game` (bare `java`, `timeout`, `-Xmx512m -XX:+UseSerialGC`, headless flags), `parse_result`, `compile_src` | bash, JDK, staged `engine/` | flags and result-line regex | The one file every runner sources. Keep the `timeout` wrapper (a hung diagnostic ran 83 min unseen). |
| `run-match.sh`, `run-dev.sh` | one headless game; `run-dev.sh` compiles privately to `build/dev-classes`, resolves names through the manifest, `LOG_OUT=` keeps engine stdout (bot `@tag` lines) | `lib.sh` | no | Fixed engine seed => like-for-like diagnostics. |
| `gauntlet.sh` | parallel runner: BOT x OPPONENTS x MAPS x both sides via `xargs -P`; or `CELLS=file` for exact cells; writes `results.csv` (opponent,map,side,winner,rounds,result,reason), `summary.txt`, keeps `losses/`, `KEEP_ALL=1` keeps wins; silences the opponent's stdout; records `dud`/`unknown`; re-execs from a private copy (bash reads scripts lazily); refuses to recompile a class tree in use; `CLASSES=` for a private tree | `lib.sh`, manifest | engine `-D` flags, `Error instrumenting` grep | The workhorse. `</dev/null` per game (xargs children inherited the config pipe as stdin). |
| `mirror.sh` + `sprt.py` | the accept gate (1.3); 19-line SPRT with `--p0/--p1/--alpha/--beta` | python3, `gauntlet.sh` | map list file only | Steal verbatim. `sprt.py` also usable one-sample against a known ladder rate. |
| `scrim.sh` + `scrim-record.py` + `elo.py`/`elolib.py` | contest-rule scrimmage block (random map/side, rotation, pool from Elo); `games.csv` append (idempotent per run id); Elo K=32, `--pool/--explore/--established/--build` (Wilson CI), writes `progress/ELO.md` + `elo.png` | python3, matplotlib in `tools/.venv` | no | `games.csv` columns: run,seq,teamA,teamB,map,winner,rounds,reason. |
| `snapshot.sh name [archetype]` | freeze `src/bot` as `src/<name>` with package rewrite and `ARCHETYPE = N` substitution; refuses to overwrite | sed | no | The archetype switch lives in the bot's `C.java`. |
| `bench-compile.sh` + `benchcompile/BenchCompiler.java`, `bench-select.py`, `bench-roster.py` | compile externals blind -> `manifest.tsv`; pick primaries by name; regenerate the tier table in `BENCHMARK.md` from `games.csv` + `history.csv` using the *most recent build's* record | JDK `javax.tools`, python3 | `RobotPlayer.java` discovery and `battlecode.common` filter are generic across years | Roots must never escape the repo (one bot at repo top level made the root the whole benchmark dir; 15 min at the heap limit). |
| `tier-check.sh` | enforces replay-access tiers from the roster table; fail-closed; `BENCH_TIER_OVERRIDE=1` | bash | no | Called by `replay-dump.sh`; keyed on the `<opp>__<map>__bot<side>.bc21` filename convention. |
| `replay-dump.sh` + `replaydump/ReplayDump.java` (471 lines) | replay -> text: `--every N` aggregates, `--from/--to` event window, `--robot ID`, `--map-at R` ASCII board, `--logs REGEX --logs-team`, `--metrics` CSV, `--bytecode`, `--navstats` (moves, A-B-A oscillation %, low-passability steps, coverage, first enemy-base contact, mean moves/unit), `--threat`, `--knowledge`, `--hits`, `--speeches`; compiled on demand, cached by source hash; `DUMP_XMX` (1 GB needed for long games) | JDK, engine jar (FlatBuffers schema) | **yes**: the schema and action types change every year; rewrite the parser, keep the mode list | The single most valuable instrument. Budget a day for the new year's version before any strategy work. |
| `scrim-study.sh` + `scrim-study.py`, `log-scan.sh`, `econ-scan.sh`, `loss-census.sh` | block study -> `study.tsv` (both sides' aggregates every 50 rounds to r700, `won` column) + `nav.tsv`; every `@tag` line of our side for a whole block in one VM pass; one bot counter at one round per game; one line per loss with how/when it was lost | `replay-dump.sh`, awk | column names | Run on wins as well as losses (cause vs feature of losing). |
| `polarity.py`, `derived.py`, `statlib.py`, `correlate.py`, `onset.py`, `test_metrics.py` | orient every metric so + always means good; derived cumulative gain/loss metrics; tested point-biserial, running mean, onset/anti-onset (must hold on the next sample), within-group stratification; per-round correlation tables; earliest-onset ranking -> `progress/ONSET.md` + `onset-ladder.png`; unit + end-to-end + live-data integrity tests | python3, matplotlib | metric names only | Read `progress/METRICS.md` for the reading rules (outcome leakage at r600; noise floor ~2/sqrt(n)). |
| `compare.py` | game-by-game diff of two runs on shared cells: identical, flips each way, sweeps, per side/map | python3 | no | For paired-cell decisions and for reading the *shape* of a difference. |
| `scan.sh`, `scan-cells.py`, `gauntlet-select.py`, `band.py`, `ladder.sh`, `track_history.py` | two-stage incremental tiering scan (3 maps both sides, then 4 more for the 0-100% band; decided cells never replayed); roster = 20-50% band; per-(opponent,map) history | python3 | no | Superseded once contest rules (scrimmage-only) came in, but the incremental-cell idea is the cheap way to tier 60+ bots. |
| `unit-tests.sh` | compile `src/bot` + `test/bot` against the engine jar, run every `*Test` main, then `test_metrics.py` | JDK, python3 | no | One command for bot and apparatus. |
| `build-engine.sh` | clone/patch/build the season engine from source when official artefacts are dead; stage `engine/{engine.jar,lib,maps}` and write the map list | JDK, gradle wrapper | **yes** | For 2024 check first whether the published engine jar is still downloadable; if not, this is the pattern. |
| `mapinfo/MapInfo.java` -> `mapdata.csv` | one row per built-in map: size, bases, neutrals, mean passability, symmetry, home-to-enemy distance | engine classes | yes | Cheap and useful for stratifying results by map class. |
| `bench-flags.sh`, `bench-throughput.sh` | time identical deterministic games under JVM flag sets; games/minute at full load | `lib.sh` | no | Measured: no JVM flag mattered; only parallelism and map size did. |
| `vm.sh`, `vm-sync.sh`, `vm-run.sh`, `vm-tail.sh`, `vm-collect.sh`, `vm-stop.sh` | remote execution (1.7) | gcloud, ssh, tar | project name/zone constants | Sync `progress/` too, or the pool selection silently degrades. |
| `test/bot/{Comms,Econ,MapState,Nav,Constants}Test.java` | flag round trips incl. mod-128 wrap and bucket monotonicity; sizing table monotone and never over budget; registry duplicates/removals/overflow and symmetry images self-inverse; Chebyshev = king-move count; relationships between tuning constants | engine jar | contents | Verify the tests bite by breaking an invariant on purpose. |
| `progress/` | `ELO.md`, `elo.png`, `ONSET.md`, `onset-ladder.png`, `METRICS.md`, `games.csv`, `history.csv` | | | Rule: nothing stale stays; regenerate or delete. |

Missing and worth adding on day one of a new year: a scripted benchmark discovery
(`gh search repos`), an end-to-end smoke test that runs each analysis script on a tiny
synthetic input, and a `preflight`/queue helper whose wait predicate is verified against
a live process.

---

## 4. Bot architecture patterns worth reusing

Source: `src/bot/` (13 files, ~1,800 lines, Java 8), described in `DESIGN.md`.

- **Layout**: `RobotPlayer.java` (17 lines: switch on type, construct controller, `loop()`);
  `Robot.java` (base: turn loop, bytecode monitor, per-robot LCG RNG seeded from id,
  shared sensing cache bucketed into enemies/friends/neutrals with nearest-enemy
  tracking, `absorb(flag)`, edge probing, crowd relief); one class per unit type;
  `Nav.java`; `Comms.java`; `MapState.java`; `Econ.java` (pure sizing arithmetic);
  `Debug.java` (16 lines); `Roles.java`; `C.java` (every tunable constant in one place,
  each with the measurement that set it, plus the `ARCHETYPE` switch and `DEBUG`).
- **Turn loop / bytecode monitor** (`Robot.loop`): record round before `turn()`; catch
  everything; if the round changed, count an overrun; else track max used and
  near-miss (> 90% of limit); print `@bc t= used= max= near= over=` every 50 turns or on
  an overrun. `EC.profLog` adds per-stage `Clock.getBytecodeNum()` checkpoints printed
  when a turn passes 75% of budget. Hot paths use arrays and unrolled loops, never
  `java.util` collections (counted as own bytecode).
- **Debug** (`Debug.java`): `@tag k=v` lines behind a compile-time flag; deliberately
  trivial because reflection-flavoured calls (`getClass`, `getStackTrace`) were rejected
  by the instrumenter with an unhelpful message and killed every robot at spawn. Log at
  the decision point (what was chosen and why), not only the outcome, so the replay
  reader can count it.
- **Determinism / play symmetry**: no `Math.random`; LCG per robot from `rc.getID()`;
  every direction/target tie-break relative to the robot's own geometry (toward target,
  toward map centre, by score), never compass order or "first sensed" (sense results
  are in row-major scan order; taking the first encodes an absolute-position bias).
- **Navigation** (`Nav.java`, 100 lines): greedy step minimising
  `chebyshev(next, target) + 1/passability(next)` with a euclidean tie-break, a 6-tile
  recent-position ring as oscillation guard, a "best distance so far" counter; after 2
  blocked or 4 non-improving turns switch to bug wall-following with per-robot
  handedness until closer than ever before; `fleeFrom(threat)` maximises distance minus
  a terrain penalty. Measured A-B-A oscillation 0.1-3.4% of moves vs the example bot's
  5%. Cost-aware BFS was written, measured unnecessary, reverted.
- **Comms** (`Comms.java`, 62 lines): `[type:4][extra:6][x&127:7][y&127:7]` in a 24-bit
  flag; location decoded relative to the *reader* (unambiguous on maps <= 64 wide);
  logarithmic 6-bit buckets that round *up* on decode (never under-estimate a target);
  ownership claims carry a 6-bit sighting stamp (round/32). Protocol lessons: a newborn
  acts next round, so an order must persist two rounds; a persistent order occupies the
  broadcast channel, so put orders on it only when they carry information; a hub reads
  its children by id within a bytecode budget (capture-role children every turn, the
  rest round-robin, neighbours on the off-turn) and rebroadcasts the most useful fact;
  scouts cycle durable facts on odd rounds and fresh sightings on even rounds.
- **Map knowledge / symmetry** (`MapState.java`, 143 lines): static per-robot state;
  bounds discovered by `rc.onTheMap` probes at sensor radius then walked inward
  (`Robot.probeEdges`); 3-bit hypothesis set {rotation, mirror-x, mirror-y}; `image(l,h)`;
  prune with a confirmed enemy base (must be the image of an own base) and with a sensed
  empty tile at the image of home; reset to all three on contradiction; registries of
  own/enemy/neutral bases with stamps, `claimEnemy/claimOwn` newest-wins, "a tile we own
  is never neutral again".
- **Exploration** (`Muckraker.pickExplore`): visit own bases not yet visited (courier
  hand-off), then candidate enemy positions under surviving hypotheses split among scouts
  by id, then random far points once bounds are known; before bounds are known, head
  for an unknown edge (split by id) with a sideways component so two scouts sweep
  different columns; re-pick when reached or after 12 rounds without moving; repulsion
  between scouts.
- **Micro** (`Politician.bestSpeech`): evaluate each legal area-effect radius once with
  distances cached, value = kills/conversions per conviction spent minus friendly
  waste, fire only above a flat value floor (a proportional floor made big units too
  fussy: measured 1.15-1.73 kills/speech vs theirs 0.91-1.27 but they fired 3.5x as
  often). Economy unit: hold a ring outside the spawn ring on the side away from the
  known enemy, flee anything hostile, relieve crowding (`Robot.spreadOut`), step off bad
  terrain. Anti-congestion mattered: a 269-influence attacker sat boxed in by 42 idle
  friends for 1,400 rounds.
- **Production as one ordered if/else chain** (`EC.build`): "the order is the
  strategy"; every branch gated by a named constant with its measurement in `C.java`;
  a "spare" branch so a base is never idle with money; explicit caps whose relationships
  are unit-tested.
- **Bidding** (season-specific, but the pattern recurs): adaptive step up on a lost
  auction / down on a won one, capped at a fraction of bank that rises through the game;
  a "safe" stop; initialise estimates in a *freshly captured* base from the team's
  visible totals rather than zero (seven young bases sat silent 300 rounds).

---

## 5. Gotchas and process rules to adopt on day one

Process (all earned by a measured failure in this repo):

1. **Phase 0 before strategy**: rules digest cross-checked against engine source (tag
   each fact with where it was verified); headless bare-`java` runner; parallel gauntlet;
   snapshot tool; compare tool; determinism check (same code twice must be identical);
   replay reader; bytecode monitor in the bot; mirror harness as the null; play-symmetry
   audit; charts. Iteration 0 = the smallest legal bot, snapshotted, so instruments are
   proven on something trivial.
2. **Never read benchmark source.** Enforce it in the compile script (log to file, print
   counts). Never review a game against a bot you beat < 20% of the time; enforce it in
   the replay tool, fail-closed, with an explicit override.
3. **External bots only as scrimmages** (random map, random side, rotation, no
   external-vs-external), never as a gate. A block is a submission; withdraw on a drop.
4. **SPRT mirror on random maps is the gate** (p0 .50, p1 .58, alpha=beta .05, batches
   of 16, cap 240, provisional keep at >= 53% over >= 200). Do not start with a fixed
   24- or 48-cell panel; it cannot accept what you can build.
5. **No test before a diagnostic game shows the pre-registered counters firing.**
   Diagnose on a cheap map only if the mechanism can fire there; sanity-check on one
   where it cannot.
6. **Pre-register** counters, gate, falsifier, dose ladder with a zero arm, and *which
   instrument decides*, before the first game. Never reinterpret a null. If a change is
   defensive or targets what externals do and the incumbent does not, pre-register a
   second arm against an archetype that has the property.
7. **Run a sparring partner on a known case before trusting it.** Two of five were
   inert or wrong on first run; the replay mode, the correlation script, and the
   knowledge tool were each wrong on first use too. Every instrument was caught by a
   number looking odd, never by inspection.
8. **Unit-test the bot's pure logic and the tuning-constant invariants; unit-test the
   analysis pipeline; run both after every change to either.** Verify the tests fail
   when an invariant is broken on purpose. Write the dull integrity checks first
   (value in range, cumulative never decreases, every `us_` column has a `th_` twin).
9. **Act on early-round metrics only** (r200 of 1500 here); by r600 everything
   correlates with winning. Use within-opponent correlation. A correlation earns a
   diagnostic game, not a code change. Expect markers (coverage) that are not levers.
10. **Repairs transfer; reallocations do not.** Look first for a cap, a gate or a defect
    that binds; "it is obviously a bug" is not evidence the fix helps (one fix lost
    45%; one measured, real economy stall was correctly left in place after four
    instruments priced its repair as a loss).
11. **Distrust the self-play proxy on a *series* of accepts that never move the
    ladder, never on one block.** Of three self-play accepts worth +19, +9, +22, only
    the one measured directly against opponent behaviour carried.
12. **Records**: append-only `TRAINING_LOG.md` with a closed-directions ledger (kind,
    measurement, re-open condition) and a functional-area map updated in place;
    `LEARNINGS.md` with a measurement per lesson; `HANDOFF.md` current state; `METHOD.md`
    portable; `PROMPTS.md` every user prompt verbatim; nothing stale stays in the repo;
    push after every commit. A fresh session must be able to resume from the log alone.
13. **Keep `src/bot` byte-identical to the incumbent snapshot between candidates**;
    revert by copying the snapshot back (`git checkout` cannot undo a committed change);
    `diff` against the snapshot to confirm. Keep rejected arms on branches.
14. **Check substitutions**: `str.replace` that matched nothing left three branches
    unchanged for a whole iteration. `assert old in s`.
15. **Measure before optimising**; time the engine yourself (no JVM flag mattered;
    parallelism and map size did) and plan evaluations in hours.

Infrastructure gotchas:

- Never run games on the session box; a game JVM is >1 GB. Cap concurrent games at
  ~(cores - 1); 6 parallel gave 3.7x on 8 vCPU.
- `pgrep -c <MainClass>` counts two per game (the `timeout` wrapper carries the class
  name); match `[j]ava .*MainClass`. Never `pkill -f`/`pgrep -f` a pattern that appears
  in your own command line (it killed the issuing shell, exit 144); write `roster-hol[d]`.
- A script that re-execs itself is invisible to `pgrep -f <name>`; **verify a wait
  predicate matches a live process before queueing behind it.** Prefer waiting on the
  run directory's `summary.txt`.
- Two runs must never share a class tree; give every runner its own `CLASSES=` and make
  the gauntlet refuse to recompile a tree that live games are reading.
- Killing a batch runner's `xargs` does not stop it; kill the script.
- xargs children inherit stdin; give each game `</dev/null` or a config-from-stdin
  engine hangs.
- Sync everything the remote scripts read (`progress/`, roster files), and fetch
  anything a remote run writes into a synced directory before the next sync wipes it.
- Replays are 4-8 MB each and a gate keeps every loss; prune after the study is fetched;
  check `df` before launching.
- The replay dumper needs ~1 GB heap for long games; a 256 MB default OOM'd and aborted
  a whole block study.
- Engine stdout of a diagnostic is buffered and written at the end; a hung game shows
  nothing, so wrap every game in `timeout`.
- Map files may not honour a custom round limit (2021: `rounds=400` still played 1500);
  smoke tests cannot be shortened that way. Check the new engine before relying on it.
- A robot over its bytecode budget loses whole rounds silently; read the monitor's
  `over=` for the base/HQ unit in every logged game before trusting a candidate.
- A robot built this round usually acts next round (engine iterates a snapshot of the
  execution order); any one-round signal to a newborn is lost. Verify in the new engine.
- Sensing calls may return a fixed scan order; picking the first result is a
  play-symmetry bug. Verify in the new engine.
- Keep the `Debug` class free of reflection; a rejected helper class kills every robot
  with an unhelpful message that the bot's own `catch` can hide.

---

## 6. One-paragraph verdict for 2024

Copy the *instrument stack* (lib/gauntlet/mirror+sprt/scrim+elo/snapshot/replay-dump
modes/study+onset/unit-tests/vm) and the *process rules* (diagnostic-first, pre-register,
SPRT gate, scrimmage-only externals, tier lock enforced in tooling, tests on bot and
apparatus) on day one; budget the first day for a 2024 `ReplayDump` and engine
verification, not strategy. Expect ~20% of candidates to be accepted, expect the
accepts that carry to be repairs of your own defects found by counting what the bot
*hears and decides* in logged games, and expect self-play to be blind to whatever the
external field does that your incumbent does not; build a sparring archetype for each
such behaviour and run it on a known case before you trust it.
