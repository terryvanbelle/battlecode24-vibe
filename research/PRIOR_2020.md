# PRIOR_2020.md -- what battlecode20-vibe did, what to steal, what to avoid

Source: `/home/terryvanbelle/projects/vibe/2020` (git, 672 commits, 2026-09-23 to 2026-09-30; GitHub
`terryvanbelle/battlecode20-vibe`). Read: README, CLAUDE, SETUP, TRAINING_ALGORITHM, LEARNINGS, DESIGN, RESEARCH,
HANDOFF, BENCHMARK, PROMPTS (selected), TRAINING_LOG (head, ledger, tail, key method sections), every script under
`tools/`, `test/`, and `src/bot`. Nothing in the 2020 repo was modified. 2020 rules/units are kept to one line each
(section 6); everything else is the transferable content.

Path shorthand below: `2020/` = `/home/terryvanbelle/projects/vibe/2020/`.

---

## 1. The training method actually used

### 1.1 Scale and timeline

- 8 calendar days, 672 commits (42 / 133 / 184 / 121 / 104 / 32 / 38 / 18 per day). One human (advisory, ~81 prompts,
  recorded verbatim in `2020/PROMPTS.md`) directing one Claude Code session under a `/loop 30m` cron ("task check. If the
  VM is idle and nothing is in the workqueue, start a new idea. Otherwise, carry on as before").
- 15,383 scrimmage games against 65 external bots recorded in `2020/progress/games.csv`; ~87 numbered iterations plus
  ~19 "R" candidates plus dozens of staged builds (61 `src/cand*`, 41 `src/r*`, 10 `src/arch_*`, 33 `src/pup_*`
  packages); 20 accepted snapshots `src/g_iter0` .. `src/g_iter19`. Roughly one candidate in five or six was accepted.
- Phase 0 (engine build, runner, replay reader, ladder tooling, unit tests, iteration-0 bot) was done on day 1 by
  porting the 2021 project's tools with season substitutions.

### 1.2 The three instruments (never confused with each other)

From `2020/TRAINING_ALGORITHM.md` section 1:

| instrument | question | blind spot |
|---|---|---|
| mirror gate (candidate vs incumbent snapshot, random map/side/seed, paired, SPRT) | is this one change better than what it replaces | anything both builds share; anything the incumbent never punishes |
| archetype spars (`src/arch_*`, hand-built one-trick opponents) and replay puppets (`src/pup_*`) | does it survive a rush / siege / hunt the mirror never mounts | everything else |
| scrimmage ladder (rated blocks vs external field under contest rules) | are we actually stronger; what the field punishes | map/side draw noise; single games |

The gate decides, the ladder is the consequence: every accepted build is "submitted" (a 48-game block per submission,
several blocks) and earns its own rating.

### 1.3 The iteration loop (five-stage funnel, cheapest first) -- `TRAINING_ALGORITHM.md` section 4

1. **Read.** Trace the motivating replay with the replay reader before forming a hypothesis; enumerate the mechanisms
   that could produce the symptom; check the same symptom in a second game.
2. **Pre-register** in `TRAINING_LOG.md` before touching code: mechanism; the **decision-point counter** (a `@tag` log
   line) that proves it fired; reachability; trigger frequency; price (costed against what it displaces); gate;
   falsifier; for a numeric change a dose ladder with a byte-identical zero arm.
3. **Diagnose.** One logged game on a small map where the mechanism can fire; grep the counters. *No test starts until
   the mechanism demonstrably fires.* (Three 2021 candidates and several 2020 ones were compiled and completely inert.)
   Check `over=` bytecode per robot type.
4. **Gate.** Paired mirror under SPRT (1.4 below). ACCEPT -> snapshot; REJECT -> revert; sign-test p<0.10 -> provisional
   (stack the next candidate on it, no snapshot).
5. **Submit.** `tools/snapshot.sh g_iterN`; regression vs archetypes; 48-game scrimmage blocks; `tools/post-block.sh`
   (record, refit ladder, regenerate charts, study, correlate, onset); update ledger and HANDOFF; commit and push.
   Withdraw if the rating's 95% upper bound falls below the previous submission's rating.

Candidate sources rotate (section 3): absolute degeneracy in our own replays; the census of a scrimmage block
(`correlate.py`, `onset.py` -- act on the earliest onset, never late-round correlations); the capability gap (a doctrine
in the field's games we never produce, an unused API method, a perennial lever from `RESEARCH.md`). A functional-area
map is kept in the log; three consecutive rejects in one area force the next candidate into another area. At least one
structural attempt in every four.

### 1.4 The gate as it ended up (evolution matters -- copy the final form)

- **SPRT**: `2020/tools/sprt.py`, H0 p=0.50 vs H1 p=0.58, alpha=beta=0.05, batches of 16, cap 320 pairs.
- **Fresh engine seed per game** (found 2026-09-24): the engine seeded every sandboxed RNG from the map file, so the
  same pairing on the same map and side replayed the *same game*; 491 of 2,825 ladder games were exact repeats and a
  240-game gate held at most 104 distinct games. Fix: patch the engine to honour `-Dbc.game.seed` (in
  `tools/build-engine.sh`), draw a random seed per game, record it, dedupe on (teamA, teamB, map, seed) in the rating.
- **Paired mirror** (found 2026-09-25): under a fixed seed the engine is deterministic, so every cell (map, side, seed)
  is played twice -- candidate vs incumbent and incumbent vs itself -- and only **discordant pairs** enter the SPRT.
  Gate 40c read 34-46 (REJECT) unpaired when 74 of 80 pairs were concordant and the discordant were 4-2 *for* the
  candidate. `2020/tools/paired.sh` + `mirror.sh PAIRED=1`.
- **Cap rule**: at 320 pairs read the discordant pairs by sign test in either direction; p<0.01 with >=12 discordant
  pairs is ACCEPT/REJECT; p<0.10 for the candidate keeps it provisional.
- **Against an archetype**: `OPP=arch_rush tools/mirror.sh` -- both builds play the archetype on the same cells; must
  ACCEPT there and must not REJECT in the plain mirror.
- **Against a puppet**: `OPP=pup_<base> tools/paired.sh` with cells from `tools/puppet.sh cells` (fixture x cutoff).
- **Control runs for diagnostics**: read a candidate's intermediate against the incumbent playing *itself* on the same
  map/side/seed, never against the other side of the same game (side effect was 7-18% of the measured quantity).
- **The judge changed in the last two days**: the mirror and the ladder disagreed in sign (gate g13 REJECT 24-39 while
  g_iter13 was +30 on the ladder). From 2026-09-29 a field-facing candidate was judged by **ladder head-to-head**: three
  48-game band blocks of the candidate alternating with three CONTROL blocks of the incumbent in the same period, both
  fitted as their own players (`us:<build>_h2h`). Never compare against the incumbent's pooled history (winner's curse:
  g_iter13's acceptance games rated 1791, its later games 1738). Tightened again 2026-09-30 after six head-to-head
  acceptances summing +311 moved the ladder +12: first head-to-head must reach +43 (one SE at 144 a side), a second
  head-to-head confirms, accept only if the pooled 288-a-side lower one-SE bound is above zero.

### 1.5 The ladder / Elo (owner-approved design, 2026-09-24)

- **Batch Bradley-Terry** on the Elo scale (`2020/tools/elolib.py`, 76 lines, MM iterations, Fisher-information SE,
  weak prior = one virtual win and loss vs a 1500 anchor). **Each of our builds is its own player.** Play order is
  irrelevant. The prior sequential K=32 Elo with one shared `us` rating was fooled: 96 calibration wins over unplaced
  bots lifted `us` from rank 65 to rank 4 above bots with 104-9 records against us.
- Ladder is built from **our games only**; external bots never play each other.
- **Grade of a build** (`tools/elo.py --build B`): rating +- 95%, rank, **field score** (expected score vs every ladder
  bot, one game each), and "vs higher" (field score against only the bots rated above it). Raw win rates are never
  compared across builds (each met a different pool).
- **Pool = the band**: the 8 rated bots nearest the playing build's rating on either side (`elo.py --band 8 --as B`);
  `scrim.sh` uses it by default. Earlier "just above us" pool drifted to bots beating us 70-99% where a 48-game block
  moved on noise. Never-played bots are placed by a calibration block (`POOLSIZE=0 EXPLORE=48 N=96`, two games each).
- Ladder field = one bot per repo (`2020/tools/ladder-bots.txt`, 65 bots) chosen by name from 285 compiled packages.
- Charts: `progress/elo.png` (every rating with 95% CI), `progress/field-score-1w.png`/`-4w.png` (rating fitted with
  R0 + a*ln(1+t/tau) and projected, mapped to field score, plus rank panel) -- the owner watched this chart.
- A **league backtest** (`tools/league.sh`/`league.py`) checked whether self-play against a fixed population of our
  archetypes ranks builds like the ladder: Spearman 0.85 across big gaps, useless within 70 Elo; 6 of 11 archetypes
  saturated at 88-100%. Conclusion: our own archetypes cannot replace the ladder.

### 1.6 Benchmarks: found, compiled and played without reading a line

- **Discovery**: GitHub searched by repo name, description, creation date and year-specific API identifiers; results
  listed (name, language, date, description only) in `2020/.bc20-search.txt`; 96 repos shallow-cloned to
  `~/projects/vibe/bc20-benchmarks/<owner>_<repo>` (outside the repo). `_repos.txt` lists them.
- **Compile blind**: `2020/tools/bench-compile.sh` drives `2020/tools/benchcompile/BenchCompiler.java` (one JVM per
  repo, `javax.tools` API; source roots = parents of any `RobotPlayer.java`; single pass, then per-top-level-package
  fixpoint with the output dir on the classpath; diagnostics to `_logs/<repo>.log` only; stdout = counts). Writes
  `manifest.tsv` (`name package classdir repo commit`, name = `<owner>.<package>`). 285 packages from 68 repos compiled.
- **Pick each repo's final bot by name only**: `2020/tools/bench-select.py` (final > postqual > qual > seeding > highest
  version > sprint; junk regex for test/template/donothing).
- **Roster table** in `BENCHMARK.md` regenerated by `2020/tools/bench-roster.py` between HTML-comment markers, with
  tiers locked/target/peer/solved by our latest submission's win rate.
- Rules (binding): never read source (`cat`/`grep`/editor forbidden under the benchmark tree; compilation prints only
  counts); silence their stdout (`-Dbc.engine.silence-a/b=true`); play them only through `scrim.sh` (random map, random
  side, rotating opponents; `gauntlet.sh` refuses external opponents unless `SCRIM=1`); never choose a map or side
  against one. The "no replay review under 20% win rate" rule was retired on day 4 (PROMPTS 45) -- the ladder allocates
  attention better; `tier-check.sh` became informational.
- Sandbox failure of an opponent is recorded as `dud`, never a win (`gauntlet.sh` greps "Error instrumenting").

### 1.7 How the VM was used

- Two GCP machines (`2020/SETUP.md`): `claude-driver` e2-small (session, repo, replay analysis, plots, at most one
  diagnostic game) and `battlecode-dev` e2-standard-8 (every gauntlet, gate, block; shared with other years; 20 GB disk).
- Identical layout on both (`~/jdk/<jdk>`, `~/projects/vibe/<year>`, benchmark classes + manifest). `tools/lib.sh` works
  unchanged on both.
- `tools/vm.sh` (ssh helpers, IP cache, `ensure_vm` starts a stopped instance), `vm-sync.sh` (tar over ssh, stages then
  renames `src tools test progress` atomically; pushes JDK/engine/benchmarks once), `vm-run.sh <log> '<cmd>'` (sync then
  `setsid nohup ... & disown`), `vm-tail.sh`, `vm-collect.sh <run>` (tar back one run dir), `vm-stop.sh` (refuses while
  games run). Bare `java`, no Gradle daemon; `-Xmx512m -XX:+UseSerialGC`; `timeout` per game; 6-7 games in parallel.
- Runs on the VM write `gauntlet/<run-id>/{results.csv,summary.txt,losses/,replays/}`; the driver collects and posts.
- A game cost ~1 CPU-minute in 2020 (2500-3000 rounds, 10-30 robots); a 240-game gate under an hour; a 48-game block
  ~8 minutes at 6 parallel.

### 1.8 How well it worked

- Start: g_iter1/g_iter2 at rank ~65 of 66 rated (field score ~65%). Day 2 calibration: g_iter3/g_iter5 rank 15-16 of
  71 (field ~69%). g_iter12 (day 4, copied the field's rush): 1823 +- 46, rank 13, field 80.2%, the only step outside
  the error bars (+79). End: **g_iter19 1752 +- 29 over 672 games, 11th of 65 ladder bots (10 above), field score
  81.4%, 23.9% vs the ten above.** Refit at shutdown: g_iter13 through g_iter19 all 1740-1752, i.e. the last six
  acceptances were within noise.
- Accept rate: 20 snapshots from well over 100 candidates; most acceptances came from repairs of our own defects
  (stall exits, seat walk, bank thresholds) and one copied tactic; reallocations rarely transferred.
- Biggest measured lesson on method: **the mirror's gain did not transfer** -- g_iter7 over g_iter6 read 64.6% in the
  mirror and +2.5 points against the field ("what the mirror rewards is what the incumbent lacks, and the field lacks
  less of it"). Three consecutive mirror acceptances gained 55-65% in the mirror and 2-3 points on the ladder.
- Structural programs (citadel, plateau, enclosure 36 stages, lattice 17 stages, R1 rewrite) each cost a day and were
  closed or ended level; the binding constraint (post-flood economy) was correctly identified on day 2 and never solved.

---

## 2. What worked and what did not

### 2.1 Worked (with the measurement)

1. **Batch Bradley-Terry with each build its own player** -- corrected a rank-4-of-66 artefact to the true rank 15;
   order-independent; unit-tested (`tools/test_tools.py`). `2020/tools/elolib.py`.
2. **Fresh seed per game + dedupe on (A,B,map,seed)** -- ratings' intervals had been overstated; 29% of one build's games
   were repeats. `gauntlet.sh` seed column, `elolib.dedupe`.
3. **Paired mirror on discordant pairs** -- turned a 34-46 "REJECT" into the truth (4-2 for the candidate on 6 discordant
   pairs); gate 41b accepted 14-0 discordant in 320 pairs. `tools/paired.sh`.
4. **Decision-point counters (`@tag` lines) grepped before any gate** -- caught inert candidates repeatedly (Iterations
   83, 46, R14, R15 "never fires").
5. **Control runs** (incumbent vs itself, same seed) for reading diagnostics -- exposed a 7-18% side effect that had been
   read as signal both ways.
6. **The band pool** (nearest 8 either side) -- blocks became informative; the "just above us" pool had drifted to
   70-99% opponents.
7. **Calibration blocks** (two games vs every never-played bot) -- placed all 65 bots in one 96-game block.
8. **Copying a tactic the field beats us with** (the rush) -- the only ladder step outside error bars (+79, 53 fast wins
   vs 18). Scorecard in TRAINING_LOG "Copying tactics".
9. **Repairs of own defects transferred**: stall exit for fixed-station roles (Iteration 8, 39-9), seat walk (41b, flood-
   round losses 6-7/96 -> 1/96), bank thresholds 300/700 -> 200 (43b, 30-8 paired).
10. **Archetypes that reproduce a field bot's win** (`arch_rush2` kills the incumbent at r166 like poortho at r130) --
    a deterministic harness for defence work; `OPP=` gating against it.
11. **Replay puppet** (owner's idea, PROMPTS 59): replay one side of a recorded loss exactly to a cutoff, then hand over
    -- turned specific losses into reproducible cells (R7 diagnostic: survives 6 of 6 recorded assaults, then +22 on the
    head-to-head). Heavy to build (937-line `puppet.py`, chain-model of the engine's RNG); worth it only if the new
    engine is deterministic and the replay carries full state.
12. **One command after every block** (`post-block.sh`: collect, record, refit, roster, study, correlate, onset, charts)
    -- nothing stale, owner always had a current chart.
13. **Onset over merged blocks** (`onset-merged.sh`): one block's noise floor 0.30, twenty blocks' 0.07-0.08.
14. **Refuse-rather-than-fallback guards** in scripts: `scrim.sh` refuses when `games.csv` is missing (silent fallback
    had played the wrong pool for four days in 2021-style tooling); `gauntlet.sh` refuses to recompile a class tree in
    use; `run_game` refuses a puppet without its fixture; `scrim-record.py` refuses self-play into the ladder.
15. **Alternating head-to-head control blocks** -- exposed the winner's curse (1791 acceptance vs 1738 after) and stopped
    accepting on stale pooled numbers.

### 2.2 Did not work (with the measurement)

1. **Unpaired mirror gate with map-file seed** -- overconfident every time; 104 distinct games in a 240-game gate.
2. **Sequential Elo with one shared rating** -- play-order artefacts (rank 4 of 66 from easy wins).
3. **Mirror as the judge of field-facing changes** -- 64.6% mirror -> +2.5 field; gate g13 REJECT while the ladder said
   +30. Use the mirror as a regression screen and the ladder head-to-head as the judge.
4. **Accepting on point estimates at +-85** -- six acceptances (+311 summed) were +12 on the ladder.
5. **Judging a diagnostic by two chosen maps** -- plateau roles won Prison by 15% and Soup by 10%, lost the random-map
   mirror 42-54, 25-39, 15-33. Run ten random maps and count deaths and role churn first.
6. **"Nearest free tile" claim schemes with re-picks** -- claim races, rounds lost; settle a claim once or assign
   deterministically (plateau, Iterations 18-20).
7. **Fixed station lists without a stall exit** -- six of sixteen units idle all game (Iteration 5, 8-24).
8. **Reallocations of a scarce resource** (more of X earlier) -- Iterations 15, 26, 35, 44 all priced below the gate;
   the mirror punished any pre-crisis diversion.
9. **A league of our own archetypes as a ladder proxy** -- saturated, Spearman 0.85 only across 150-Elo gaps.
10. **The 20% review rule** -- locked exactly the games with the most information; retired by the owner on day 4.
11. **Big structural programs run as day-long stage sequences without a paper design first** -- citadel (10 sweeps,
    4-9/24), plateau (7 sweeps), enclosure (36 stages, closed 715 below the incumbent), lattice (17 stages, never >6
    vaporators). HANDOFF's own advice: design the next structural candidate on paper from census numbers before coding.
12. **Late "spend the idle bank" mechanisms without a producer that survives** -- Iterations 9, 13, 16, 23, 24, 30, 36:
    every form bought units that had nowhere useful to stand.
13. **Guards/defences that cost anything before the crisis** -- priced out by the mirror at 12-27% while their ladder
    arms were null (31, 37, 47d).
14. **Early-round measurement read from one game against the opponent's side** (Iteration 36) -- noise both ways.
15. **Delegating candidate builds to agents that took hours** -- owner questioned it (PROMPTS 71-72); the useful agent
    pattern was read-only replay investigators feeding a designer, not build-and-gate delegation.

---

## 3. Tooling worth stealing (path, purpose, dependencies, year-specificity)

Everything lives under `2020/tools/` unless noted. "YS" = year-specific parts; "generic" = copy verbatim after renaming
paths/JDK. All shell scripts source `lib.sh`. Python tools are stdlib except where noted; matplotlib/numpy live in
`tools/.venv` (`elo.py`, `onset.py`, `field-score.py` re-exec themselves into it).

### 3.1 Core runner (copy first)

| script | lines | purpose | deps | YS |
|---|---|---|---|---|
| `lib.sh` | 67 | JDK/classpath export, `engine_cp`, `team_url`, `run_game` (bare `java`, `timeout`, `-Xmx512m -XX:+UseSerialGC`, all `-Dbc.*` flags, `GAME_SEED`, `GAME_CONFIG`, `GAME_OPTS`), `engine_busy`, `parse_result` (`RESULT <A|B> <round> <reason>`), `compile_src` | JDK | the `-Dbc.*` flag names, replay-limit flag, `parse_result` regexes (engine stdout format), JDK version |
| `run-match.sh` | 16 | one headless game, prints RESULT | lib.sh | no |
| `run-dev.sh` | 29 | same from a private per-process compile (`build/dev-classes-$$`); `LOG_OUT=file` keeps engine stdout; resolves manifest names | lib.sh, manifest | manifest path only |
| `gauntlet.sh` | 130 | BOT x OPPONENTS x MAPS x both sides in parallel (`xargs -P`); fresh seed per game recorded as 8th column; `CELLS=file` plays given cells; `CLASSES=` private tree; refuses recompiling a tree in use; refuses external opponents unless `SCRIM=1`; re-execs itself from an unlinked copy so editing it mid-run is safe; keeps losses, deletes wins unless `KEEP_ALL=1`; detects `dud`/`unknown`/timeouts; writes `results.csv`, `summary.txt` | lib.sh | map list file, silence flags, "Error instrumenting" grep |
| `snapshot.sh` | 12 | freeze `src/bot` as `src/<name>` by sed on `package`/`import` lines; optional `ARCHETYPE = n` substitution | none | no |
| `build-engine.sh` | 87 | clone engine source, patch rotted build, patch `LiveMap.getSeed` to honour `-Dbc.game.seed`, build with old Gradle, stage `engine/{engine.jar,lib,maps,VERSION}` and `tools/<year>-maps.txt` | JDK, Gradle, python3 | almost entirely (keep the shape: patch list as a python `patch()` block, `printClasspath` task, VERSION stamp) |

### 3.2 Gate

| script | lines | purpose | YS |
|---|---|---|---|
| `sprt.py` | 19 | SPRT verdict from (wins, losses); `--p0 --p1 --alpha --beta`; prints LLR and bounds | no (draws would need a trinomial version) |
| `paired.sh` | 54 | cells `map side seed [fixture cutoff]`; plays candidate game and control game per cell with the same seed; deletes concordant replays; counts `LOGTAG` firings; prints pair table + sign test p; `OPP=` third build | no |
| `mirror.sh` | 53 | the gate driver: random cells in batches of 16 (python one-liner seeded per batch), `PAIRED=1` -> `paired.sh`, else `gauntlet.sh`; runs `sprt.py` after each batch; `W0/L0` resume; own class tree `build/mirror-classes` | map list only |
| `pair-batch.sh` | 26 | puppet pairs in parallel on the VM, one class tree per game (deleted after: 129 trees filled the disk) | puppet-specific |
| `compare.py` | 60 | game-by-game diff of two gauntlet runs: identical cells, flips each way, sweeps, by side/map | no |
| `league.sh` / `league.py` | 25/68 | fixed-population self-play backtest vs ladder (Spearman, pair agreement) | no |

### 3.3 Ladder

| script | lines | purpose | YS |
|---|---|---|---|
| `elolib.py` | 76 | `load`, `dedupe`, `fit` (Bradley-Terry MM, SE), `expected`, `field_score`, `current_build`, `ladder_bots`, `append`; games.csv schema `run,seq,teamA,teamB,map,winner,rounds,reason,seed` | no |
| `elo.py` | 120 | ranking table -> `progress/ELO.md`, `elo.png`; `--build B` grade; `--band N --as B` pool; `--explore K`; legacy `--pool/--challenge/--established`; Wilson CI; "our last run" per bot | no |
| `scrim.sh` | 59 | the only way to play externals: pool from `elo.py --band`, cell generator (rotating opponents, never twice in a row, each <= ceil(N/pool)), random map+side, `SCRIM=1 KEEP_ALL=1 gauntlet.sh`; refuses without games.csv | map list |
| `scrim-record.py` | 33 | run dir -> games.csv rows, idempotent per run id, refuses self-play/puppets | no |
| `post-block.sh` | 22 | collect, record, refit, field-score chart, roster, study, correlate, onset, onset-merged | no |
| `field-score.py` | 109 | rating-over-time fit R0+a*ln(1+t/tau), projected with 95% band, mapped to field score / vs-higher / rank; 4-panel PNG; PDT conversion | numpy, matplotlib; project start date hard-coded |
| `bench-roster.py` | 98 | regenerate roster table with tiers in BENCHMARK.md | manifest path |
| `tier-check.sh` | 29 | opponent tier from a replay filename (informational) | no |
| `ladder-bots.txt` | 65 | the field, one per repo | yes (contents) |

### 3.4 Benchmarks

| script | purpose | YS |
|---|---|---|
| `bench-compile.sh` + `benchcompile/BenchCompiler.java` (43 + 86) | blind compile of every repo, manifest.tsv, logs only to files | the `battlecode.common` filter string and `RobotPlayer.java` root rule are year-agnostic in practice |
| `bench-select.py` (47) | name-only choice of each repo's final bot | no |

### 3.5 Replay reader and analysis pipeline

| script | lines | purpose | YS |
|---|---|---|---|
| `replaydump/ReplayDump.java` + `replay-dump.sh` | 438 + 13 | replay -> text: `--every` aggregates, `--from/--to` event window, `--robot ID`, `--map/--map-at` ASCII board, `--logs REGEX --logs-team`, `--metrics` CSV (31 columns per team per row), `--bytecode`, `--navstats` (moves, A-B-A oscillation, coverage, first enemy-HQ contact, idle units), `--threat`, season-specific board/ring flags; compiled on demand cached by source sha | the flatbuffer schema, action codes, type table, COST/LIMIT arrays, board glyphs: rewrite the event handlers, keep the structure and the flag set |
| `scrim-study.sh` / `scrim-study.py` | 45/44 | every replay of a block -> `study.tsv` (both sides every 50 rounds to r1200) and `nav.tsv`; medians us vs them | column list |
| `correlate.py` | 87 | point-biserial correlation of each metric (ours and us-minus-them) with the result, raw and within-opponent; medians in wins/losses; prints r200 separately | column list |
| `onset.py` | 169 | correlation curve per metric by round; **onset** = first round it reaches +0.30 and holds; anti-onset; running-mean variants; markdown + PNG | column list |
| `onset-merged.sh` | 27 | onset over every block of one build (only for submissions with >=200 games) | no |
| `statlib.py` / `polarity.py` / `derived.py` | 68/30/18 | pointbiserial, noise floor, onset/anti-onset, within-group; per-metric orientation so "positive = good for us"; derived columns | polarity table |
| `test_metrics.py` | 99 | 36 checks on the above incl. an end-to-end synthetic block | no |
| `log-scan.sh` | 18 | all our `@tag` lines for every game of a block into one tarball | no |
| `window-census.sh`, `late-race.sh`, `lattice-diag.sh`, `citadel-diag.sh`, `enc-read.py`, `enc-acct.py` | small | per-question census scripts built from `--metrics`/`--ring` output | yes (keep as patterns) |
| `mapinfo/MapInfo.java` -> `mapdata.csv` | 59 | map corpus table (size, HQ distance, symmetry, resource totals, season fields) | mostly |

### 3.6 Puppet (replay one side exactly)

`puppet.sh` (151), `puppet.py` (937), `puppet/RawEvents.java` (127), `src/puppet/{Puppet,Script}.java`,
`test/puppet/{PuppetTest.java,golden.properties}`. Design in `2020/DESIGN.md` "Puppet": fixture is a `.properties`
file handed to the engine as its `-c` config; robots read it via `System.getProperty`; identity by (type, first-turn
round, tile); one act per turn; message attribution by replaying the engine's transaction-id RNG; handover at a cutoff;
misconfiguration resigns; `check` diffs the replayed game against the recording round by round. Entirely engine-specific;
reuse the *idea* and the guard tests only if the 2024 engine is deterministic under a seed and its replay records per-robot
actions.

### 3.7 Tests

`unit-tests.sh` (16 lines): javac `src/bot` + `test/bot` against the engine jar, run every `*Test` main (no JUnit),
then the puppet contract test, then `test_metrics.py` and `test_tools.py`. Bot tests: `test/bot/{NavTest,CommsTest,
MapStateTest}.java` (18/28/76 lines; `check(bool, "what")` + exit code). Tool tests: 80 checks in `test_tools.py`
(SPRT boundaries, Elo properties incl. order-independence and "easy wins must not lift us", recorder idempotence,
selector scoring, puppet encode/decode/diff/guards). Run after every change to bot or tool (CLAUDE.md rule 6).

---

## 4. Bot architecture patterns worth reusing (`2020/src/bot/`, 1,800 lines total)

Layout (`DESIGN.md`): `RobotPlayer` (switch on type -> `Robot` subclass -> `loop()`), `Robot` (base: turn loop,
bytecode monitor, RNG, sensing cache, shared helpers), one controller per type, `Nav`, `MapState`, `Comms`, `Debug`,
`C` (constants, each annotated with the measurement that set it).

- **Turn loop + bytecode monitor** -- `Robot.java:loop()` (lines ~45-60): record round before `turn()`, catch every
  exception (`Debug.exception`), overrun = round changed, near-miss = used > 90% of limit, print
  `@bc t=<type> used= max= near= over=` every `C.BC_REPORT_EVERY` turns and on every overrun; the replay dumper's
  `--bytecode` cross-checks with the engine's own per-robot counts. Also `@bcprof` lines in `Miner`/`Landscaper`
  breaking a turn into phases when it exceeds 8000-8500.
- **Deterministic per-robot RNG** -- LCG seeded from the id (`Robot.nextInt`); no `Math.random`. Every direction/target
  tie-break is relative to the robot's own geometry, never a fixed compass order (play symmetry; `senseNearbyRobots`
  order was row-major).
- **Sensing cache** -- `Robot.sense()`: one `senseNearbyRobots` per turn bucketed into fixed arrays (`enemies[64]`,
  `friends[64]`), nearest enemy, our HQ info; side effects (home, enemy HQ sighting, symmetry pruning) hang off it.
- **Navigation** -- `Nav.java` (89 lines): greedy step minimising Chebyshev then Euclidean to the target, a 6-tile
  "recent" ring buffer as an oscillation guard, `noProgress`/`stuck` counters, bug wall-following with per-robot
  handedness entered after 2 blocked or 4 non-improving steps and left once closer than ever; `stalled()` after a
  per-robot `stallLimit` so every role can have a stall exit; `legal()` folds season hazards (water) into one place.
  Counters `steps/blocked/bugSteps` for `@nav` logs. Tested geometry in `NavTest`.
- **Map knowledge / symmetry** -- `MapState.java` (134 lines, all static = per robot): probed origin from
  `rc.onTheMap` at sensor radius (`Robot.probeEdges`, 5 bytecodes a probe; corpus turned out to use origin (0,0) so
  `C.ASSUME_ORIGIN` short-circuits it); three symmetry hypotheses as a bitmask, pruned allocation-free by comparing
  remembered terrain at image indices (`observe`), by an image tile seen empty (`pruneEmpty`), and fixed by sighting
  (`sightEnemyHQ`); never left at zero hypotheses; `enemyHQGuess()` = sighted else image under lowest surviving
  hypothesis. Tested thoroughly in `MapStateTest`.
- **Exploration** -- sector grid (`MapState.SECTOR=8`, `sectorSeen`/`sectorBad`, `nextSector(from, salt)` nearest unseen
  with a per-robot salt so units fan out; a stalled walk marks the sector bad). Miner's `explore` target is sticky and
  never alternates with a resource target (Iteration 50: alternating targets reset the stall counter every turn).
- **Comms schema** -- `Comms.java` (43 lines): fixed-width int messages `[type, p1..p5, auth]`; `auth` = hash of
  payload + round + team salt so an opponent's or a stale message never authenticates (tested: zero message never
  authenticates in 2000 rounds). Readers read on a residue mod 3 to spread bytecode; anything that must reach every
  unit is re-posted periodically by the HQ and newborns scan back several blocks (LEARNINGS: a message posted once was
  seen by a third of the units and by nobody born later). Roles handed out by the one robot that knows the count (the
  school posts `LATTICE_ORDER <id>` three rounds running).
- **Micro/targeting scoring** -- `Robot.shootDrone()`: rank-then-distance scoring `score = rank*1000 + d2`, ranks derived
  from what the shot achieves (cargo lands alive / next lift prevented / payload drowns), pre-registered and logged as
  `@shoot kind=<rank>`. `Robot.fleeFrom`, `climb` (highest safe adjacent tile), `tryBuild(type, toward)` with relative
  tie-break and hazard filters. 2020 had little unit-vs-unit combat, so there is no kite/strike library to copy.
- **Roles by birth order / spawn geometry** -- builder = HQ's first miner (identified by birth round), rusher = second;
  landscaper roles seat -> helper -> attacker chosen by what is free; every role with a station list has a stall exit and
  a "bad station" memory.
- **Constants file** -- `C.java`: every number carries the iteration and measurement that set it; unit tests assert
  invariants between constants (`NavTest`).
- **Debug** -- `Debug.java` trivial on purpose (reflection-flavoured calls get the class rejected by the sandbox and take
  the bot down); `@tag k=v` at the decision point, once per decision, so firing is a grep count; the gauntlet silences the
  opponent so our lines are the only logs in a replay.
- **Archetypes** -- `src/arch_*` are full copies of the bot with a behaviour switch (`ARCHETYPE` constant set by
  `snapshot.sh name N`) or hand-edited; `arch_rush2` was the one that reproduced a field bot's win exactly.

---

## 5. Gotchas and process rules to adopt on day one

Process (from `CLAUDE.md`, `TRAINING_ALGORITHM.md`, `HANDOFF.md`, `LEARNINGS.md`):

1. Games in volume run only on the VM; the driver plays at most one diagnostic. Every concurrent run gets its own class
   tree (`CLASSES=build/<name>`); a runner must refuse to recompile a tree that live games read.
2. Seed every game with a fresh engine seed and record it; dedupe ratings on (A, B, map, seed). Verify determinism on
   day one (same seed twice -> identical replay; different seed -> different game).
3. Pair the mirror from the first gate; SPRT on discordant pairs only; cap + sign test; provisional stacking.
4. Judge field-facing changes on alternating head-to-head ladder blocks against the incumbent, never against the
   incumbent's pooled history (winner's curse). Require one SE on the first head-to-head and a confirming second.
5. Ratings: batch Bradley-Terry, each build its own player, from our games only; grade = rating +- CI, rank, field score;
   never compare raw win rates across builds.
6. Pool = band around the playing build; place never-played bots by a two-game calibration block; refuse to run a block
   if the ladder history is missing rather than fall back to a fixed roster.
7. No test before a diagnostic shows the mechanism firing (grep the pre-registered counter). Read a diagnostic against a
   control run (incumbent vs itself, same seed), not against the opponent's side.
8. Before a gate run the candidate on ten random maps and count deaths and role churn against the incumbent.
9. Pre-register in the log before coding; never reinterpret a null after seeing it; act on early onsets only.
10. Unit tests for bot and tools after every change; every instrument is run first on a case whose answer is known
    (every instrument built was wrong on first use).
11. `post-block.sh` after every block; nothing stale stays (regenerate or delete charts/docs).
12. Record every owner prompt verbatim in `PROMPTS.md`; push after every commit; keep HANDOFF's state block current
    enough that a fresh session can resume mid-gate by run id.
13. Never read benchmark source; compile through the blind compiler; silence their stdout; play them only as
    scrimmages (random map, random side). Review any game (the 20% rule is retired).
14. Three consecutive rejects in one functional area -> next candidate from another area; one structural attempt in
    four; design a multi-session structural program on paper from census numbers before coding it.
15. Keep a closed-directions ledger (measurement, kind: refuted / priced below the gate / blocked / engine-impossible,
    re-open condition) and a functional-area map at the end of the log.

Operational gotchas (all cost real time in 2020):

- `pgrep -f <pattern>` matches your own ssh/command line; use the `[b]racket` trick. `pgrep -c battlecode.server.Main`
  counts two processes per game (the `timeout` wrapper).
- Kill the driver script (`mirror.sh`/`gauntlet.sh`), not its `xargs`; `gauntlet.sh` re-execs from a temp copy, so wait on
  `summary.txt`, not a process name. Never edit a running bash script in place.
- Never read a partial batch as a result (games ending early are biased toward one outcome).
- `vm-sync.sh` replaces `src tools test progress` on the VM at every `vm-run.sh`; anything a run writes there is lost.
  Sync by staging + rename, never `rm -rf && tar -x` (a gate lost ten cells to the missing-tools window).
- The VM disk fills: prune posted blocks' `replays/` and `losses/`, delete per-game class trees, `df -h` before a gate.
- `( while ...; do sleep; done; cmd ) &` from a tool call dies with the call's shell; use the tool's background mode.
- Wrap every game in `timeout`; a hung diagnostic ran 83 minutes invisibly.
- `git checkout` cannot undo a committed change to `src/bot`; restore from the snapshot with a `sed` on the package line.
- A candidate's ladder arm must carry its own label, never the incumbent's; a short arm must never re-tier the roster
  or overwrite the submission's merged onset table (>=200-game rule).
- Static fields are per robot (each robot is its own sandbox): never assume another robot's view; cache per robot.
- A `Debug` class that touches reflection (`getClass`, stack traces) gets the whole bot rejected at spawn.
- Static initialisers and large literal tables can blow the first turn's bytecode; build tables lazily on first use.
- `senseNearbyRobots` order is not by distance; sort or scan for the nearest explicitly.
- Sandbox-rejected opponents are `dud`, not wins; grep the engine output for the instrumentation error.
- Timezone: run ids are UTC machine stamps; dates in logs/charts in the owner's timezone; convert explicitly.
- Any "nearest tile" target needs a reachability filter and a stall exit; any role parked near a spawner will eventually
  stand on every spawn tile and stop production without a log line (count idle units per role at a fixed round).

---

## 6. 2020-specific content, one line each (omit otherwise)

- Season: "Soup" -- miners, landscapers (dirt), delivery drones, a rising flood, a shared blockchain for comms, 7-int
  messages, no indicator strings, engine built from source with three patches (official artefacts gone).
- Perennial mechanics that did carry across years and are in `RESEARCH.md`: symmetric maps + inference, bug nav on
  bytecode budgets, sectors instead of coordinates for comms, kite after every attack, emergent over commanded
  coordination, identify and deny the scarce resource, adapt rush/turtle per map from measured signals.
- Post-mortems of the current year were never read (first- or second-hand); `RESEARCH.md` was built from other years.

---

## 7. Suggested day-one copy list for 2024 (in order)

1. `tools/lib.sh`, `run-match.sh`, `run-dev.sh`, `gauntlet.sh`, `snapshot.sh` -- adapt flag names and result regexes to
   the 2024 engine; keep the seed column, `CELLS=`, `CLASSES=`, dud detection, re-exec trick.
2. `tools/sprt.py`, `paired.sh`, `mirror.sh`, `compare.py` -- verbatim (check whether 2024 has draws; if so extend SPRT).
3. `tools/elolib.py`, `elo.py`, `scrim.sh`, `scrim-record.py`, `post-block.sh`, `field-score.py`, `bench-roster.py`,
   `tier-check.sh` -- verbatim apart from paths and the project start date.
4. `tools/bench-compile.sh` + `benchcompile/BenchCompiler.java`, `bench-select.py` -- verbatim (change the API package
   filter if the 2024 API package differs from `battlecode.common`).
5. `tools/vm.sh`, `vm-sync.sh`, `vm-run.sh`, `vm-tail.sh`, `vm-collect.sh`, `vm-stop.sh` -- verbatim, change
   `REMOTE_REPO` and the year's benchmark dir.
6. `tools/statlib.py`, `polarity.py`, `derived.py`, `correlate.py`, `onset.py`, `onset-merged.sh`, `scrim-study.sh/.py`,
   `test_metrics.py`, `test_tools.py`, `unit-tests.sh` -- verbatim structure; replace the metric column list and
   polarity table for the 2024 replay reader.
7. `tools/replaydump/ReplayDump.java` -- keep the flag set and the per-team CSV/`--navstats`/`--bytecode`/`--logs`
   design; rewrite the event handlers against the 2024 schema.
8. `src/bot/{RobotPlayer,Robot,Nav,MapState,Comms,Debug,C}.java` and `test/bot/*Test.java` -- keep the turn loop,
   monitor, RNG, sensing cache, Nav, symmetry/sector code, authenticated fixed-width messages, constants-with-provenance;
   drop everything about water, dirt and the blockchain.
9. `CLAUDE.md`, `TRAINING_ALGORITHM.md` sections 0-7 (with the head-to-head judge already in), `BENCHMARK.md` rules,
   the ledger/functional-area-map format from `TRAINING_LOG.md`, and the HANDOFF "state block first" convention.
