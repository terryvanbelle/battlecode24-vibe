# bcenv (anicolao/bcenv) — review for the Battlecode 2024 project

Reviewed: local clone at `/home/terryvanbelle/projects/vibe/reference/bcenv`,
commit `7f868dc` (2026-09-11, "Center the design on supervised NixOS competitor
environments", author Alex Nicolaou). `git pull` reported it current. The repo
has exactly **one commit** and **14 tracked files**. Every file was read in full.

## 1. Headline finding: there is no runnable environment

bcenv is a **design-and-research repository, not a tool**. Its own README says
so: "This project is in its initial design stage. There is no implementation or
runnable environment yet." Concretely:

- No Battlecode engine invocation, no match runner, no parallelism code.
- No Elo/rating code, no benchmark-bot management, no replay parser, no map tooling.
- No Dockerfile, no Nix flake, no VM image, no CI, no season integration of any kind.
- No mention of `.map24`, `.bc24`, flatbuffers, or a 2024 engine version anywhere
  in code. The only 2024 references are two footnoted citations (see section 6).
- The only executable code is a **Husky git-hook that enforces an append-only
  `PROMPTS.md` prompt record** (35 lines) plus its test (70 lines).

So, for the questions "how does it run games / manage bots / compute Elo / parse
replays / set up Docker": **it does not**. What bcenv does have is ~110 KB of
carefully-sourced design prose that (a) surveys 2014–2026 Battlecode postmortems
and prior AI/agentic Battlecode efforts, including Terry's own `battlecode22/25/26-vibe`
repos and anicolao's `battlecode2023` / `battlecode-2026`, and (b) sketches an
architecture for an autonomous Battlecode agent. That prose is the stealable
part, along with the prompt-record hook (which this 2024 project already appears
to be imitating, given `/home/terryvanbelle/projects/vibe/2024/PROMPTS.md` exists).

## 2. What bcenv is and is for

Stated goal (README, VISION): "A Battlecode environment for an AI agent to
independently compete in the annual Battlecode competition." The agent should go
from a new season's rules to a competition-ready bot with minimal human input:
learn the season, build a baseline, experiment, evaluate, iterate, compete.

VISION.md (13 lines) is a pure vision statement: independence = sustained
ownership of the rules-to-submission loop; the agent's work must leave a faithful
record of instructions, experiments, and evidence; the design must outlive any
single season or model; GPLv3.

Proposed (not built) architecture, from README and INITIAL_DESIGN_SKETCH.md:
an outer LLM **supervisor** in an isolated VM/container manages one or more
**competitor NixOS VMs**; each competitor boots a NixOS image, uses Nix to set
up the season's toolchain and Battlecode checkout, and runs its own inner LLM
development agent; separate **evaluation workers** run a pinned official engine
on frozen artifacts so competitor agents cannot tamper with scoring. Target cloud
is GCE; local disposable VMs from the same Nix modules.

## 3. Repository structure (complete)

```
bcenv/
  README.md                    63 lines  status, proposed env, workflow, contributing rules, license
  VISION.md                    13 lines  vision statement only
  AGENTS.md                     8 lines  rules for coding agents working on bcenv (prompt record)
  PROMPTS.md                   65 lines  verbatim, append-only record of the 10 user prompts so far
  HISTORICAL_LEARNINGS.md     527 lines  (73 KB) sourced survey, 104 footnotes
  INITIAL_DESIGN_SKETCH.md    332 lines  (41 KB) draft architecture, 30 footnotes
  LICENSE                               GPL-3.0-only
  package.json                          husky 9.1.7 devDependency; scripts prepare/check:prompts/test
  package-lock.json                     lockfile v3, husky only
  .gitignore                            node_modules/
  .husky/pre-commit                     `node scripts/check-prompts.mjs`
  .husky/pre-merge-commit               `node scripts/check-prompts.mjs`
  scripts/check-prompts.mjs    35 lines  the append-only PROMPTS.md enforcer
  scripts/check-prompts.test.mjs 70 lines  node:test harness that builds a temp git repo and
                                          checks 11 accept/reject cases against the hook
```

No `src/`, no `tools/`, no `flake.nix`, no `Dockerfile`, no `.github/`.

## 4. Every tool/script and what it does

### 4.1 `scripts/check-prompts.mjs` (the only tool)

Path: `/home/terryvanbelle/projects/vibe/reference/bcenv/scripts/check-prompts.mjs`

Runs as both `pre-commit` and `pre-merge-commit`. Logic:

1. `git ls-files --stage -- PROMPTS.md` must match `^100644 <sha> 0\tPROMPTS.md\n$`
   (regular, non-executable, staged, no merge-conflict stage).
2. Reads the **staged** blob via `git show :PROMPTS.md` (the index is authoritative,
   not the worktree).
3. If `HEAD` exists and tracks PROMPTS.md, reads `HEAD:PROMPTS.md` as `previous`.
   Handles an unborn branch (`git rev-parse --verify --quiet HEAD` exit 1) so the
   first commit works; any other git error fails the check.
4. Rejects unless `staged.subarray(0, previous.length).equals(previous)`
   (byte-exact prefix, i.e. strictly append-only).
5. Rejects unless the appended tail has non-whitespace content.
6. On any failure prints `Prompt record check failed: ...` and sets exit code 1.

Design consequences worth noting: every commit must append to PROMPTS.md, so
multi-commit tasks re-append the same prompt with a note; fast-forward merges
bypass it (no commit); it is a local hook, so not a server-side guarantee.

### 4.2 `scripts/check-prompts.test.mjs`

Path: `/home/terryvanbelle/projects/vibe/reference/bcenv/scripts/check-prompts.test.mjs`

`node --test` suite. Creates a `mkdtemp` git repo, scrubs inherited `GIT_*` env
vars, sets `HUSKY=1`, copies the hook and script in, installs husky via
`node_modules/husky/bin.js`, then asserts the hook rejects: missing initial
record, blank initial record, unchanged record (`--allow-empty`), unstaged
addition, whitespace-only addition, rewritten history + addition, truncated
record, deleted record; and accepts a valid initial record and a valid staged
append even when the worktree differs from the index. Also runs
`.husky/_/pre-merge-commit` directly and expects failure. Good, compact model
for testing git hooks hermetically.

### 4.3 npm scripts (`package.json`)

- `npm ci` → `prepare: husky` installs the hooks.
- `npm run check:prompts` → runs the enforcer against the current index.
- `npm test` → runs the hook test suite.

## 5. Requested operational topics, one by one

| Topic | What bcenv actually contains |
| --- | --- |
| Engine invocation | None. Sketch says: stable ops are `build`, `run`, `inspect`, `package`; season integration owns the actual command. |
| Parallelism | None. Cites Nudge (outercloudstudio/nudge) as a distributed-runner precedent; "how much worker concurrency is justified" is listed as an open decision. |
| Headless | None implemented. Sketch: game workers should need no network; evaluation runs in coordinator-managed containers. |
| Java version handling | None. Sketch: season bundle must pin "compiler/runtime versions"; pin Gradle explicitly because a Nix shell doesn't make Gradle downloads reproducible. |
| Benchmark bots | None. Design: opponents are frozen, provenance-recorded artifacts; distinguish fixed historical bots from maintained variants; refreshing a maintained opponent changes experiment identity; measure how much strategic behaviour the pool covers. |
| Ratings / Elo | None. Design explicitly *argues against* a fixed win-rate threshold; wants paired player-slot comparisons, per-map/opponent/slot breakdowns, clustered bootstrap over maps as a candidate uncertainty method, pre-declared promotion criteria, "inconclusive" as a valid result. |
| Replays | None. Design wants indexed access by match/turn/player/unit, derived findings pointing back to raw events, and labelling of privileged (full-replay) vs in-game information. Cites Terry's `.bc22` ASCII renderer and `battlecode26-vibe/tools/replaydump/ReplayDump.java` as reuse candidates. |
| Maps | None. Season bundle field: "Map files and hashes, legal player slots, supported seed controls". Evaluation ladder: diagnostic maps → development suite → protected holdout. |
| Docker / environment | None. Proposal only: Nix flake with supervisor and competitor NixOS modules, GCE image + local VM from the same modules, credentials injected at runtime, state persisted off boot disks. Notes anicolao 2023 used Docker (`devcon/Dockerfile`) and anicolao 2026 a macOS-only Nix shell. |

## 6. Battlecode 2024-specific content

There is **no 2024 support**: no engine version pin, no scaffold, no `.map24`
reader, no flatbuffers schema, no `.bc24` parser. The only 2024 material is:

1. [REDACTED: bcenv cites two Battlecode 2024 post-mortems here. Reading current-year post-mortems is forbidden (PROMPTS.md #1), so the summary was removed unread.]
2. Footnote [^58]/[^5]: **Battlecode 2024 specs v3.0.5** at
   `https://releases.battlecode.org/specs/battlecode24/3.0.5/specs.md.html`
   (Feb 1 2024), cited for its "substantial within-season changelog" — the point
   being that "2024" alone does not identify an engine; pin the exact release.
3. Footnote [^51]: **CodeClash's `battlecode24` arena adapter** at
   `github.com/CodeClash-ai/CodeClash/tree/f0694c64.../codeclash/arenas`. bcenv's
   inspection notes: the 2023/2024 images use Java scaffolds; setup invokes update
   commands that must be frozen for reproducibility; the 2025 adapter parses
   stdout text for outcomes and mishandles short output/ties/missing scores. This
   is the one external artifact bcenv points at that is *directly* a 2024 runner,
   and it is flagged as needing contract checks before trusting it as an evaluator.

For 2024 engine/scaffold/replay-format details, this repo is not a source; use
the official `battlecode/battlecode24` and `battlecode24-scaffold` repos and the
CodeClash adapter above.

## 7. What is worth stealing, with paths

### 7.1 The append-only prompt-record hook (verbatim-copyable)

Paths:
- `/home/terryvanbelle/projects/vibe/reference/bcenv/scripts/check-prompts.mjs`
- `/home/terryvanbelle/projects/vibe/reference/bcenv/scripts/check-prompts.test.mjs`
- `/home/terryvanbelle/projects/vibe/reference/bcenv/.husky/pre-commit`
- `/home/terryvanbelle/projects/vibe/reference/bcenv/.husky/pre-merge-commit`
- `/home/terryvanbelle/projects/vibe/reference/bcenv/package.json`

How to use: copy the four files plus a `package.json` with
`"devDependencies": {"husky": "9.1.7"}` and scripts
`prepare: husky`, `check:prompts: node scripts/check-prompts.mjs`,
`test: node --test scripts/check-prompts.test.mjs`; run `npm ci`. From then on
every commit must stage a strictly-appended, non-whitespace addition to
`PROMPTS.md`, and the first commit must include a non-empty record. If you'd
rather not require *every* commit to append (e.g. for pure refactors), the
single change is to relax step 5 (the non-whitespace-tail check) while keeping
step 4 (byte-prefix equality), which preserves the tamper-evidence property.
The test file is a nice hermetic pattern for any git-hook test: temp repo,
scrubbed `GIT_*` env, `XDG_CONFIG_HOME` redirected, `commit.gpgsign=false`.
Since this 2024 project already has a `PROMPTS.md`, this is the most immediately
applicable piece.

### 7.2 AGENTS.md conventions (copy and adapt)

Path: `/home/terryvanbelle/projects/vibe/reference/bcenv/AGENTS.md`

Eight bullet rules for coding agents: record every prompt verbatim, PROMPTS.md
is append-only, headings may carry a 3–6 word summary but never replace the
verbatim text, re-append when one prompt spans several commits, use a longer
fence if a prompt contains code fences, never bypass the hooks. Drop into the
2024 repo's `AGENTS.md`/`CLAUDE.md` as-is.

### 7.3 The season-bundle / experiment-manifest schemas (design, not code)

Path: `/home/terryvanbelle/projects/vibe/reference/bcenv/INITIAL_DESIGN_SKETCH.md`,
sections "Season integrations" and "Evaluation that supports decisions".

Two tables worth turning into YAML/JSON for the 2024 project:

- **Season bundle**: Identity (season, engine commit/release, image ids); Rules
  (archived specs with hashes, known patches); Toolchain (scaffold rev, Java
  version, Gradle version, build commands); Game execution (map files + hashes,
  player slots, seed controls, match command, resource limits); Evidence (replay
  schema + parser version, terminal-outcome mapping); Submission (required files,
  validation, destination).
- **Experiment manifest**: exact candidates, opponents (with provenance and
  fixed-vs-maintained flag), maps, player slots, seeds, repetitions, environment
  identity, budgets, intended comparison, and pre-declared promotion criteria.

How to use: write `season/bc24.yaml` and a per-run `experiment.yaml` with these
fields before building a runner; have the runner refuse to start without them and
stamp every result file with the manifest hash. This directly addresses the
"same matchup overwrites the same replay path" and "which snapshot was that
result from" problems bcenv documents in anicolao's 2023 and 2026 repos.

### 7.4 Job-accounting rules for the match runner (design)

Same file, "Account for every job". Keep game outcome (win/loss/tie + tie-break
reason) separate from job state (queued/running/completed/failed/cancelled/
unknown-after-interrupt). Persist raw terminal output, exit status, parser
diagnostics. Never silently drop short logs or failed matches. Reports show
scheduled/completed/excluded/retried/unresolved counts with an explicit
denominator. Retries are linked attempts, not replacements. This is a checklist
to apply to whatever `run_matches.sh`/`tournament.py` the 2024 project writes.

### 7.5 The catalogue of concrete failure modes to build regression fixtures for

Path: `/home/terryvanbelle/projects/vibe/reference/bcenv/HISTORICAL_LEARNINGS.md`,
section "Earlier work in the anicolao repositories" → "Tool contracts and evidence quality".

Documented producer/consumer bugs in anicolao's `battlecode-2026` tooling that a
2024 harness should test against from day one:
- Analyzer expected `Match Ended - Winner ID:`; the Java tool printed
  `Match Winner:` → winner field never populated, defaulted to "?" → "NO".
- Required metric `KingBDied` looked up unconditionally → `KeyError` instead of report.
- Replay IDs below a hard-coded threshold silently excluded.
- Team A's result treated as "our win" without resolving which slot we occupied.
- `check_submission.py` returned the same `False` for rejection and timeout.
- `review_scrimmages.py` fetched only page 1 and marked results reviewed
  regardless of replay-download success.
- Replay inspector with hard-coded action IDs and FlatBuffers offsets (schema
  identity matters — directly relevant to `.bc24` parsing).
Lesson stated verbatim: "the feedback system is itself experimental software."
Test parser and producer together; represent missing metrics as missing, not as
default values.

### 7.6 Pointers to Terry's own prior tools (bcenv cites them as reuse candidates)

bcenv does not contain these, but it identifies them with pinned commits:
- `terryvanbelle/battlecode22-vibe@b1d36b4` `tools/README.md` — `.bc22` native
  replay → ASCII board + event transcript + CSV metrics, with synthetic-replay tests.
- `terryvanbelle/battlecode26-vibe@f8b127a` `tools/replaydump/ReplayDump.java`,
  `tools/test_replaydump.py`, `tools/compare_gauntlets.py` (pairs games by
  opponent/map/side, reports outcome flips and round deltas; intersection-only,
  so also report unmatched jobs).
- `terryvanbelle/battlecode25-vibe@3e3e14d` `tools/bot_identity.py` (source vs
  snapshot comparison, git head + dirty state at run start), `tools/tournament.sh`
  (exports committed sources, per-bot compile check, forfeits, paired map
  matchups, **copies itself before running** so edits can't corrupt an in-flight
  shell script), `tools/agent-watchdog.sh` (external heartbeat + tmux recovery),
  `TRAINING_ALGORITHM.md`, `MULTI_AGENT.md`, `METHODS.md`, `OBJECTIVE.md`,
  `benchmarks/HISTORY.md`.
The 2025/2026 replay tools are the closest existing code to a `.bc24` parser
(same FlatBuffers approach, different schema), and are the natural starting point
for the 2024 project rather than anything in bcenv.

### 7.7 Strategy lessons from the survey that apply to 2024 specifically

- Keep a playable reference bot and shorten idea→real-game latency (Johnson 2017).
- Verify the exact submitted artifact contains the feature (Double J 2019;
  anicolao 2026 two-workspace confusion). Package via allowlist; keep analysis
  tools in a separate source set (anicolao 2026 `build.gradle` does this).
- Self-play against old versions systematically misses what external opponents
- [REDACTED line: referenced a 2024 post-mortem]
  0/20 vs three external bots). Import independent opponents early.
- Matchups are nontransitive (SPAARK 2025); a fixed head-to-head threshold is a
  weak acceptance rule.
- Small diagnostic maps for debugging, broad suite for validation, protected
  holdout for selection; relabel the holdout as dev data once it steers changes.
- Record hypothesis + expected observation before a comparison; attach a
  "manipulation check" (did the changed code path actually execute?).
- Optimising against a co-evolving rival produces co-adaptation, not strength
  (Terry 2025 objective revision, Sept 10 2026); measure against a frozen roster.

## 8. What NOT to expect from bcenv

- Do not look here for a match runner, Elo, Docker, Nix flake, or any 2024
  engine glue. None exist; the README and sketch both say the implementation
  "remains to be implemented".
- The design sketch is deliberately generic across seasons and heavier than a
  single-season bot project needs (supervisor VMs, GCE images, campaign
  journals, content-addressed artifact stores). Take the schemas and checklists
  (7.3–7.5), not the topology.
- Citations to anicolao's `battlecode2023` and `battlecode-2026` are to
  **private** repositories; the pinned URLs will 404 without access.

## 9. One-line verdict

bcenv is a well-sourced design document plus a 35-line git hook. For the 2024
project, steal the prompt-record hook and test verbatim, adopt the
season-bundle / experiment-manifest / job-accounting schemas as YAML, and use
the failure-mode catalogue as a regression-test list; get the actual 2024
engine, `.map24`, and `.bc24` handling from the official battlecode24 repos,
the CodeClash `battlecode24` arena adapter, and Terry's own 25/26-vibe replay
tools instead.
