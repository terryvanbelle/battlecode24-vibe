# Prior season: what the Battlecode 2025 practice project teaches us

Source: `/home/terryvanbelle/projects/vibe/2025` (GitHub `terryvanbelle/battlecode25-vibe`),
run 2026-09-06 to 2026-09-16, about 2,140 commits. This was the third of five practice seasons.
Its sister projects were `battlecode22-vibe` (128 iterations) and `battlecode26-vibe` (about 195
iterations). Paths below are relative to that root unless they are absolute. Benchmark-bot
source was NOT read. Throughout, `v3` and `TSPAARKHS` are the downloaded BC25 finalist bots.

## 0. The verdict in five bullets

- **Setup:** three isolated Claude lineages (alice, bob, carol) worked under a coordinator for
  about 4 days. They met only in a twice-daily round-robin. Their best builds scored
  **17.3% / 4.7% / 24.7% against `v3`** (Novice division 2nd place) and **0-1.3% against
  `TSPAARKHS`** (HS tournament winner). Source: `benchmarks/HISTORY.md`.
- **Why it failed:** the lineages mostly co-adapted to each other. In the same window their
  tournament shares climbed 20+ points (`tournaments/HISTORY.md`). `OBJECTIVE.md` calls this
  "a joint local optimum: two bots co-adapted to each other's specific weaknesses".
- **The switch:** bob was retired 09-10 04:17 and alice and carol later on 09-10 (formalised
  09-14). One lineage, **darla**, was then started with access to everything all three had
  measured. It was effectively carol's code plus a siege mechanic, after a from-scratch
  economy was abandoned after 10 versions with 7 defects (`agents/darla/progress/milestones.txt`).
- **darla's result:** the first build hit **34.0%** against v3. After **7 accepted iterations out
  of about 193 candidate "arms"** (about 3.6% accept rate) it reached **56.0% (84/150)**.
  `TSPAARKHS` never rose above **0.7%** (`benchmarks/HISTORY.md`, `agents/darla/DESIGN.md`).
- **The takeaway for 2024:** start with a single lineage. Get an **external yardstick** (real
  finalist or scrimmage bots) into the loop on day one. Treat self-play and self-lineage
  instruments as regression checks only, never as the measure of strength.

---

## 1. The training method

### 1.1 Documents that define it
| file | role |
|---|---|
| `TRAINING_ALGORITHM.md` (858 lines) | A year-agnostic loop synthesised from BC22 and BC26. It covers Phase 0, Iteration 0, the Gauntlet, 20 measurement doctrines, the iteration loop, stall handling and logging. |
| `TRAINING_CASES.md` (323) | The narrative evidence behind each doctrine. It was split out so the rules stay short. |
| `METHODS.md` (1,065) / `METHODS_EVIDENCE.md` (2,554) | 86 numbered cross-lineage practices ("§N"), curated by the coordinator. Rules are read first; evidence is looked up with grep only. |
| `MULTI_AGENT.md` (623) | Protocol for the multi-agent setup: isolation, tournament, VM sharing, git discipline and agent cycling. |
| `OBJECTIVE.md` | The 09-10 pivot from "beat the rival lineage" to "absolute strength". |
| `agents/<name>/{AGENT.md,RULES.md,LEARNINGS.md,CLOSURE_MAP.md or CLOSED.md,TRAINING_LOG.md}` | Per-lineage files: the charter, an engine digest, lessons, a closed-directions ledger and an append-only log (18k-25k lines each). |
| `agents/darla/DESIGN.md` (11,643 lines) | darla's single lab notebook. Every arm has a registration before launch and a result after. |

### 1.2 Phase 0 (`TRAINING_ALGORITHM.md` §Phase 0), all year-agnostic
1. Digest the spec into `RULES.md`. 2. Probe the engine directly with `javap` against the
**pinned** jar and write probe bots; never infer from behaviour. Sweep the `RobotController`
API for methods the bot never calls. Do this at iteration 5, every 10 iterations after that,
and whenever the loop stalls. (BC26 lost 81 iterations to an unused mechanic, and BC25
repeated the mistake at iteration 29.) 3. Look for radius asymmetries. 4. Build the
match and replay infrastructure first. Calling `java` directly instead of through the Gradle
daemon cut per-game time from 10-30 s to about 2 s. 5. Verify determinism. 6. Wire bytecode
monitoring into iteration 0. 7. Audit play symmetry, meaning any fixed compass-order
tie-break, and run a mirror match. 8. Learn the map-symmetry contract.

### 1.3 The iteration loop (`TRAINING_ALGORITHM.md` §The iteration loop)
- **Hyperparameters:** WinPct 60%, NearMissMargin 5 points, MaxNearMissRefinements 3,
  MaxConsecutiveRejects 3 (then change functional area), ReproSampleSize 8.
- **Steps:** (1) Pick a target: a loss, or a sign of absolute degeneracy. (2) Trace the replay
  instead of theorising. (3) State the hypothesis with pre-registered variables and a
  falsifier, plus three cheap pre-checks. (4) Implement and verify the mechanism with counters
  in indicator strings. (5) Evaluate in stages: a **one-map identity check** (were the arms
  byte-identical?), a cheap repro sample, the full gauntlet plus a head-to-head against the
  last snapshot, then accept, near-miss or reject. (6) Run the atomic post-accept routine:
  charts, roster history, an archived replay and one commit.
- **Stall handling, in order of preference:** ablate accepted features, try high-risk
  structural changes, re-read cross-year research, and consider a from-scratch rewrite.
  Keep a **closed-directions ledger** with re-open conditions that can be checked.

### 1.4 How darla actually gated (this is what worked)
Numbers come from `agents/darla/DESIGN.md` and the scripts in `agents/darla/tools/`.
1. `make-arm.sh <name> '<sed>' '<expected>'` clones `src/darla` into `src/<arm>` and changes
   one constant or clause. It **proves** the diff is non-empty and that the package line was
   rewritten.
2. A 12-map probe reads the mechanism counters from indicator strings. If the trigger fires
   too rarely, the arm is closed without a full run.
3. **Screens:**
   - `roster-screen.sh`: pinned 25 maps × 3 frozen lineages × 2 sides, 150 games.
   - `head-to-head.sh`: all 75 maps against a reference, 150 games.
   - `paired-roster.sh`: 75 maps × 3 lineages × 2 sides, **450 keys paired on (opponent, map, side)**.
   - `widen.sh` / `widen2.sh`: the same against mid-lineage snapshots, to broaden the opponents.
4. **Statistic:** a McNemar z over the *discordant* keys between arm and shipped build
   (wins it flipped against losses it flipped). This is valid because the engine is
   deterministic: the 450 or 150 games form a census, not a sample.
5. **Accept rule** from 09-14 on (owner): z > 2 on **either** the paired roster or the `v3`
   census (150 keys, `tools/benchmark-arm.sh`), with at least 50% against each lineage.
   Both numbers are always reported.
6. `accept-iteration.sh <arm> <N>` freezes `src/darla_iterN`, promotes the arm and prints the
   diff. A promotion test and a `v3` benchmark follow on every accept. The script
   deliberately does not commit; the notebook write-up is the human step.
- Each accept's size: iteration 1 was 97-53 head-to-head (+3.59 sd; 32 swept maps won against
  10 lost). Iteration 4 was **z +3.26 across 1,350 paired games** on three opponent sets.
  Iteration 5 was z +2.75 across four instruments. Iteration 7 was v3 72→84, 16 against 4
  discordant keys, z +2.68.

### 1.5 Ladder and Elo against external bots
- **No Elo or TrueSkill was used.** Everything is win counts on deterministic census sets.
  Standing among the three lineages came from `tools/tournament.sh` (round-robin, 450 games)
  and `tools/tournament-report.py`, which writes `tournaments/HISTORY.md`, a league table.
- **External ladder:** `tools/benchmark.sh` plays each lineage's committed bot against
  `TSPAARKHS` and `v3` on 75 maps × 2 sides (150 games per pairing). By default it saves no
  replay. `tools/benchmark-history.py` regenerates `benchmarks/HISTORY.md`, which lists
  **records only**: a row appears only when a complete run beat that lineage's previous best.
  119 run directories exist, 78 of them complete; 48 of those scored unshipped candidates.
- The lesson on bracketing: pick one benchmark near parity (`v3`, about 43% at the time) and
  one far above (`TSPAARKHS`, about 0%). The owner ruled on 09-14 that the too-strong bot
  "is not a target"; tuning toward it overfits a regime that will not occur.

### 1.6 How benchmark bots were found, downloaded and compiled
- The owner authorised it on 2026-09-07, so no script exists. See the commit message for
  "tools: benchmark against downloaded BC25 finalist bots, scores only" (`git log` in the 2025
  repo). The coordinator searched the web, then downloaded and compiled these without reading
  them: `TSPAARKHS` (HS tournament winner), `SPAARK`, `quals_current_submission`, and `v3`
  (Novice division 2nd). They were deliberately **never committed**. They lived only at
  `battlecode-dev:~/bc25-benchmarks/bench/src/<pkg>` (125 MB) and `.gitignore` blocked them.
  `WIPE-RUNBOOK.md` notes that "re-downloading is not scripted".
- **Compiling:** `~/bc25-benchmarks/bench` is a copy of the Gradle scaffold. The benchmark
  packages sit in `src/`. `benchmark.sh` uses `git archive HEAD` to copy our bot package into
  the same `src/`, runs `./gradlew build` once, and then plays raw `java` games off
  `build/classes`.
- **Rules that grew over time:** first, scores only (no `-Dbc.server.save-file`). On 09-14,
  replays against `v3` were allowed through a separate opt-in `tools/benchmark-replay.sh`.
  Source reading was never allowed. Chaining: `tools/cron-benchmark.sh` ran after the evening
  tournament via `tools/systemd/bc25-benchmark.service`.
- **For 2024 (where downloading bots is presumably allowed and wanted):** script the
  download (for example, git clone of the top teams' public 2024 repos), pin the commits in
  a manifest and make compiling one command. Decide on day one whether reading their code is
  allowed. 2025 treated this as a hard firewall because of its own ground rule: "no bot
  implementations downloaded from the web".

### 1.7 Multi-agent setup, and whether it paid off
- **Layout:** `agents/{alice,bob,carol}/` were separate Gradle workspaces with bot package
  `<name>` and snapshots `src/<name>_iterN/`. There was also a neutral `arena/`, shared
  strategy-neutral `tools/`, and a coordinator session that spawned the agents as subagents.
  Prompts: `tools/agent-prompts/{alice,bob,carol,resume}.md`.
- **Information channel:** a round-robin at 06:00 and 18:00 Pacific
  (`tools/systemd/bc25-tournament.timer` → `tools/cron-tournament.sh` → `tools/tournament.sh`).
  Each bot came from HEAD. Results in `tournaments/<run>/` were shared; replays on the VM were
  readable.
- **Isolation cost a great deal of engineering:**
  - `tools/isolation-sweep.sh` and a scratchpad root held at `u=wx` to stop leaks between
    lineages.
  - Rules against `pgrep -fa` and `ls ~` on the VM.
  - `tools/agent-commit.sh` commits through a private git index, because the shared
    `.git/index` let a sibling's commit pick up 1,716 lines of another lineage's work.
  - A duplicate-session detector in `gauntlet.sh`. Two sessions of one lineage ran at the same
    time **five times**.
  - `tools/agent-watchdog.sh` plus a systemd timer. When the account hit a usage limit, the
    coordinator's own restart loop was killed by the 429.
- **Did it pay off? No, and 2025 said so explicitly.**
  - Tournament rates climbed (alice 35%→59%, carol 19%→56%) while all three sat at 5-25%
    against `v3` and 0% against `TSPAARKHS`.
  - Tournament standings are zero-sum. bob "fell" 95.7%→34.7% while his frozen-roster
    strength rose 40→70.
  - Tokens: agents were 83% of spend. Contexts ran above 700k tokens. A cold start cost about
    77k tokens of mandatory reading.
  - The fix was to retire bob, then alice and carol, and run one lineage (darla) that pooled
    all their measurements. darla beat the best lineage's v3 score by 9 points with its first
    build and by 31 points at the end.
- **The one real benefit:** frozen retired lineages make good **opponents that we did not
  write**. darla's roster instrument (alice, bob, carol plus mid-lineage snapshots such as
  `alice_iter39 bob_iter18 carol_iter44`) came from them. Even so, that roster **did not
  generalise**. Two accepts worth +19 and +15 roster games moved v3 by z = +0.38
  (`agents/darla/tools/widen.sh` header).

### 1.8 VM usage
- **Driver** `claude-driver` (e2-small, us-west1-b, project `tvanbelle-vibecode`) runs Claude
  in tmux session `bc25` (launcher `~/bc25`). **Compute** `battlecode-dev` (e2-standard-8,
  31 GB RAM, **20 GB disk, which filled on 09-11 and killed three arms; use 50 GB**).
  Sources: `RESTART_SESSION.md`, `WIPE-RUNBOOK.md`.
- All games ran on the VM over plain ssh and scp with the gcloud key. `gcloud compute ssh`
  stalls under load. The workspace `src/` is rsynced to `~/battlecode25-vibe/<ws>/`. A
  generated `runner.sh` runs under `setsid`, so it survives the session dying.
- **Concurrency:** a flock slot semaphore, `GLOBAL_CAP` 7 BC25 games and `HARD_CAP` 8 on the
  machine, `MAXJOBS` 2-3 per run, and `-Xmx2g` per game.
- **systemd timers:**
  - tournament (twice daily);
  - benchmark (chained after the tournament);
  - `bc25-vm-prune` and `bc25-driver-prune`, because driver disk was tight: each run's
    `losses/*.bc25` is 40-70 MB;
  - an agent watchdog.

---

## 2. What worked and what did not

### 2.1 Top 12 things that worked (with measurements)
1. **One informed lineage instead of three isolated ones.** It reached 34.0% against v3 at
   once (the best lineage had 24.7%) and 56.0% after 7 accepts. Source: `benchmarks/HISTORY.md`.
2. **An external yardstick.** It was the only instrument that exposed the joint local
   optimum. It also exposed that roster gains do not transfer: +19 and +15 roster games gave
   z +0.38 on v3. Iteration 7 was the first accept made *on* the v3 census (+12 keys).
3. **Paired McNemar on discordant keys, with pairing on (opponent, map, side).** Pairing on
   opponent as well tripled the number of keys to 450 without weakening the pairing. It was
   the only instrument able to resolve effects of 4-15 games (`paired-roster.sh` header).
   Iteration 4: z +3.26 over 1,350 paired games against six opponents.
4. **Determinism, then a mirror null.** Identical code split all 20 maps exactly 20/40 and
   swept none. So margins are counted in games, and the **error bar is over maps**, not
   binomial (`tools/map-resample.py`). Binomial overstated the spread about 2x.
5. **Swept maps as a signal immune to spawn side.** The identity
   `wins − losses = 2·(swept − swept_against)` holds (97−53 = 2·(32−10)). A swept map is a
   near-noise-free observation.
6. **Dose ladders that include a zero arm.** `RUIN_BAN_ROUNDS` 250/100/50/25/10 gave census
   scores 72/74/76/77/**84**. The curve was monotone and steepest at the end, which produced
   the iteration-7 accept. `SPLASH_FLOOR` showed an interior optimum.
7. **A cheap-kill ladder:** engine probe (0 games), then a census of replays already on disk,
   then a 2-3 game probe, then a screen (`METHODS.md` §1). Directions closed "on magnitude",
   by pricing a mechanism against the gap before screening it (§2): +1.7 against a needed +16.
8. **Mechanical guards on arm building:** `make-arm.sh` and the one-map identity check.
   Arm `darla14` forfeited 0/72 because a sed silently matched nothing; one lineage wasted
   250 games on arms whose effects were nil.
9. **A machine that is never idle:** `agents/darla/tools/arm-runner.sh`
   (queue in `progress/pending-arms.txt`), `idle-filler.sh` (fresh 25-map roster samples
   whenever the queue is empty), `status-line.sh`, and a `/loop 10m` heartbeat
   (`RESTART_SESSION.md` §7). About 193 arms went through in about 5 days.
10. **Tracing absolute degeneracy instead of guessing:**
    - bob: "327,000 credits unspent at round 2000" led to 3 accepts from counter dumps.
    - darla: a census of where soldier turns go found `S HOME` at 63-79% of turns. That led
      to "don't queue at a dry tower" (+12 games) and an "escape hatch" for stuck movement
      (+13, then +15 on 450 keys, then accepted).
11. **Pinning the engine jar and probing it with `javap`.** The VM's Gradle cache held a stale
    `1.0.0` jar beside `3.1.0`. `tools/engine-jar.sh` refuses the wrong one. An API sweep
    found a mechanic that neither we nor `v3` used.
12. **A frozen old-bot roster measured on a schedule.** It showed that head-to-head margins do
    not chain: parts worth +8 and +6 added up to +8, not +14. It also found a destructive
    feature pair: 76% and 82% alone, 56% together (`TRAINING_ALGORITHM.md` §5b). Resume by
    default instead of cold start saved about 77k tokens per restart
    (`tools/agent-prompts/resume.md`).

### 2.2 Top 12 things that did not work (with measurements)
1. **Isolated multi-agent lineages.** After four days: v3 at 17.3%, 4.7% and 24.7%;
   `TSPAARKHS` at 0-1.3%. Each lineage closed its "axes" against a rival that itself lost 75%
   of its games (`OBJECTIVE.md`).
2. **The tournament as a progress measure.** It is zero-sum and three-body. A pooled subset
   rate of 25.0% against 49.9% (z = 4.15) was misread as a defect of one bot; it was one
   pair's effect (doctrine 20, `tools/map-subset.py`).
3. **A self-play head-to-head as the accept gate.** It disagreed in sign with the roster
   twice in one day. `darla133` scored 88/150 against its predecessor (13 over the bar) and
   then came in 9 games below on the 450-key roster (z −1.08). `darla124`/`darla129` swept
   17-20 maps in the mirror and lost on the roster (`roster-screen.sh` header).
4. **Selection on a fixed evaluation pool.** On a held-out fresh sample the gain was about
   **60%** of what the in-pool instrument predicted: +9.2 against about +15, with about 23
   arms screened on one pool (DESIGN.md "owner's audit, 2026-09-13").
5. **Three-opponent random-map gauntlet screens.** The noise floor was about 6.7 points.
   Iteration 1's constants screened at +2 and +4 ("probably noise") but were +3.59 sd on a
   head-to-head. A fresh-sample replicate has sd 7.4 per 150 games, so a +5 effect needs
   about 35 samples.
6. **Binomial sd and "noise floor" talk under determinism.** One candidate was wrongly
   rejected and had to be accepted on review. `map-resample.py` replaced this reasoning on
   09-07.
7. **Thesis-first from-scratch design.** darla's tower-siege thesis was refuted on the first
   run: **96.5% of games were decided by paint coverage** and only 3.5% by destroying units.
   The from-scratch economy was abandoned after 10 versions with 7 defects, and darla was
   rebased on carol plus the siege. Measure the win condition before choosing a thesis.
8. **Positioning and navigation tweaks.**
   - "Head for nearest enemy paint": −38 (−6.33 sd).
   - Enemy-paint centroid: −22 (−3.67 sd). At 70% splashers: −7.84 sd.
   - Dispersed landmark: −10.78 sd.
   - Bug navigation: −14 games.
   Only the narrow "escape hatch" for stuck movement helped.
9. **Most single-constant tweaks.** On 09-14, 13 arms gave 13 closures and no accept. Overall
   about 186 of 193 arms were rejected. Many arms were **provable no-ops** (for example
   `darla19`, `darla54`, `darla64`, `darla69`, `darla70`) that a static read would have
   caught. Enumerate the candidate set before building a selector (bob #58b).
10. **Doctrine bloat.** 86 METHODS entries, 20 doctrines, logs of 18-25k lines, and about
    5,200 lines (about 77k tokens) of mandatory reading per cold start. Lineages spent
    sessions writing closure maps and meta-rules. darla's notebook reached 11.6k lines.
11. **Races in shared infrastructure.**
    - A check-then-act "is the VM busy" test: 7 games ran against a cap of 6, at load 10.3.
    - A run ID made from the timestamp to the second: two runs shared one directory and **450
      games were lost** (09-12).
    - `ls -t` picked the wrong run: 123/150 was reported instead of 352/450.
    - Editing a bash script while it ran killed the collation of a finished 450-game
      tournament.
12. **Disk and ops surprises.**
    - The 20 GB VM disk filled.
    - The driver hit ENOSPC mid-commit because of the 18 GB BC26 archive.
    - `cron-benchmark.sh` used the wrong regex for the run directory (six digits against
      `%H%M`), so 900 finished games were nearly lost.
    - 31 commits once sat unpushed.

---

## 3. Tooling worth stealing or adapting

Each entry gives the script and path, its purpose, dependencies, and whether it is specific to
2025. "Year-specific" means filenames, the engine version, the maps list or schema classes need
changing.

| script (2025 path) | purpose | deps | year-specific? |
|---|---|---|---|
| `tools/lib.sh` | VM helpers: `ensure_vm` (start the VM if stopped, cache its IP, one ssh probe), `gssh`/`gscp` (plain ssh with the gcloud key), `find_workspace` | gcloud, ssh | Only the VM name and `REMOTE_REPO`. **Steal as is.** |
| `tools/gauntlet.sh` (13 KB) | Parallel headless runner: BOT × OPPONENTS × MAPS × 2 sides on the VM. Random `NMAPS` sample or pinned `MAPS`; writes `results.csv`, `reasons.txt`, `summary.txt` and `losses/*.bc25`. Protections: self-reexec from a private copy (safe to edit mid-run), atomic `mkdir` of the run directory, a `bot.txt` identity, `maps.src` (sampled or pinned), and a duplicate-session alarm. | bash, flock, java 21 on the VM, `collate.sh`, `remote-slot.sh` | The `battlecode25` jar, `-Dbc.*` flags and the `add-opens` list. The 2024 server's `battlecode.server.Main` flags are probably the same family; verify with the 2024 scaffold's `build.gradle` `run` task. **Core steal.** |
| `tools/remote-slot.sh` | A counting semaphore across runners (`acquire_slot`): per-game flock slot files plus a one-line gate for the `HARD_CAP` pgrep check. The kernel releases slots on crash. | flock, pgrep | No. **Steal.** Test it with `tools/semaphore-test.sh`. |
| `tools/collate.sh`, `tools/gauntlet-collect.sh` | Turn `results.txt` into CSV and summary (overall, per opponent, swept-map score, exceptions). Recover a run whose driver died (`--list`, `<run-id>`). Marks unfinished runs `!! INCOMPLETE`. | bash, awk | No. |
| `tools/vm-match.sh` | Play one match on the VM and pull the replay and log back | lib.sh | Jar name. |
| `tools/tournament.sh` + `tools/cron-tournament.sh` + `tools/tournament-report.py` | Round-robin of the committed HEAD bots in random map order, so a truncated run is an unbiased subset. A bot that fails to compile forfeits. The report gives per-pair deltas and the `HISTORY.md` league table. | python3 stdlib, systemd timer | Bot names. **This is our local "tournament simulation"**; point it at our versions plus the external bots. |
| `tools/map-subset.py` | Splits a pooled rate on a map subset into per-pair rates, each against its complement, with a z test | python3 | No. |
| `tools/benchmark.sh`, `benchmark-arm.sh`, `benchmark-collate.sh`, `benchmark-collect.sh`, `benchmark-history.py`, `benchmark-replay.sh`, `cron-benchmark.sh` | Our bot (committed agent or an unshipped arm) against external bots on all maps × 2 sides. Scores only by default; `-replay` keeps replays. `HISTORY.md` holds records only, with a swept / swept-against column. | Benchmark bots staged in a Gradle scaffold on the VM | Paths and bot names. **Steal the structure.** In 2024 we can allow reading replays from the start. |
| `tools/bc25-maps.txt` | The 75-map pool, one per line (the official maps) | — | Yes. Regenerate from the 2024 client or engine map list. |
| `tools/map-resample.py` | Bootstrap and jackknife **over maps** for a gauntlet run; distance from the mirror null; reports from the workspace bot's perspective | python3 stdlib only | No. **Steal.** |
| McNemar on discordant keys | Done by hand in DESIGN.md from two `results.csv` files keyed on (opponent, map, side). **There is no single shared script.** Worth writing on day one: `paired.py runA runB` → n01, n10, z = (n10−n01)/√(n10+n01). | python3 | No. |
| `agents/darla/tools/make-arm.sh` | Clone the bot into `src/<arm>` with one sed edit. Fails if the package line, the `BUILD` constant or the intended change is missing, or if the diff is empty. | bash, sed, diff | Package name. **Steal.** |
| `agents/darla/tools/accept-iteration.sh` | Freeze `src/<bot>_iterN`, promote the arm, print the diff. Does not commit. | bash | No. |
| `agents/darla/tools/{head-to-head,paired-roster,roster-screen,replicate,widen,widen2}.sh` | Named evaluation drivers. Each takes a lock (`/tmp/darla-eval-driver.lock`), waits for the queue, calls `gauntlet.sh` with a fixed opponent and map set, and reads the run ID from gauntlet stdout, never from `ls -t`. | gauntlet.sh | Bot names. |
| `agents/darla/tools/{arm-runner,idle-filler,disk-guard,status-line,watch-state,jobs}.sh` | Keep the VM busy: a queue of arms, fallback absolute-strength sampling, disk pruning, a one-line RUNNING/WAITING/IDLE status | flock, setsid | No. **Steal.** |
| `tools/bot_identity.py` | Records which build actually played, by content: hashes `src/<bot>` against every `src/<bot>_iterN` with the package line normalised | python3 stdlib | No. |
| `tools/track_vs_old_bots.py`, `plot_vs_old_bots.py`, `plot_progress.py`, `progress_lib.py`, `agents/darla/tools/plot_arms.py` | Charts: `cumulative_iterations.png`, `vs_old_bots.png` (win % against a frozen roster over time, from committed `progress/vs_old_bots_history.csv`), an arm ladder and dose curves | matplotlib in `tools/.venv` | Workspace naming only. Originally from BC22 and BC26. |
| `tools/engine-jar.sh`, `tools/engine-javap.sh`, `tools/engine-facts.md` | Resolve the **pinned** engine jar from `arena/engine_version.txt` (refuse any mismatch) and `javap` classes on the VM | JDK | Jar name. **Steal.** |
| `tools/replay-dump.sh` + `tools/replaydump/ReplayDump.java` (36 KB) | Replay to text. See §3.1. | JDK 21 and the engine jar on the VM | **Yes.** Schema classes differ per year. |
| `tools/agent-commit.sh` | Commit through a private `GIT_INDEX_FILE`, restricted to one workspace, retrying on ref-lock races | git | Only needed if we run several agents. |
| `tools/vm-prune.sh`, `tools/driver-prune.sh`, `tools/systemd/*` | Keep the newest N runs and prune replay blobs; timers for tournament, benchmark and prune | systemd | Unit names. |
| `.claude/settings.json` | An allow-list for tools, git and gcloud ssh. Asks before WebFetch, curl, pip or sudo. | — | Paths. |

**Tests:** there are effectively no unit tests. The only test files are the scaffold's
`test/examplefuncsplayer/RobotPlayerTest.java` in each workspace. Correctness controls were
**runtime guards** instead: the `make-arm.sh` checks, the arm-to-arm identity check,
`semaphore-test.sh`, the determinism "promotion test" (a promoted build must reproduce its
arm's score exactly; this passed 6+ times), and the replay dumper's self-reporting footers.

### 3.1 The replay parser (`tools/replaydump/ReplayDump.java`), in detail
This is the most reusable piece for `.bc24`, since both formats are gzip-compressed
flatbuffers.
- **No generated code of our own.** It compiles against the flatbuffer classes
  **`battlecode.schema.*` that ship inside the engine jar**, plus `com.google.flatbuffers.*`
  from the same jar: `javac -classpath $BC_JAR ReplayDump.java`. The schema therefore always
  matches the pinned engine. For 2024, compile against `battlecode24-java-<ver>.jar`.
- **Container:** read the bytes and gunzip them if they start with `1f 8b`. Then
  `GameWrapper.getRootAsGameWrapper(ByteBuffer.wrap(bytes))` and loop over
  `gw.events(i)` → `EventWrapper`, switching on `ew.eType()`:
  - `Event.GameHeader`: the teams (`TeamData.teamId()`, `packageName()`).
  - `Event.MatchHeader`: the map (`GameMap`: width, height, walls, initial paint, ruins,
    symmetry, spawns).
  - `Event.Round`: `roundId`, `turns(i)`, `diedIds`, `teamIds`, `teamCoverageAmounts`.
  - `Event.MatchFooter`: `winner`, `winType`, `totalRounds`, `timelineMarkers`.
- **A trick worth knowing:** the class is declared `package com.google.flatbuffers;`. BC25's
  `Turn.actions` is a vector **union of structs**, and the generated accessor only offers the
  Table form. Being in the flatbuffers package gives access to protected `Table` methods,
  used like this:
  `o = turn.__offset(22)`, `__vector_len`, `__vector`, `Table.__indirect(vecStart+j*4, bb)`,
  then `new PaintAction().__init(pos, bb)` for each `turn.actionsType(j)`.
- **Per-turn state:** robot id, x and y, health, paint, move and action cooldowns,
  `bytecodesUsed`. Actions include Spawn, Upgrade, Paint, Unpaint, Splash, Attack, Mop,
  Transfer, Die and indicator strings.
- **Board reconstruction:** the initial map plus exact-location actions. Where the schema
  lacks a footprint (splash) the gap is **printed next to the engine's own coverage figure**,
  so a wrong view announces itself.
- **Outputs:** headers; per-team aggregates every N rounds (`--every`); every spawn, upgrade
  and death; a detailed action window (`--from/--to`); a per-robot track (`--robot`); ASCII
  maps (`--map N`, `--map-at R`, `--views`). A **final-round flush** exists because rounds
  after the last stride multiple were silently dropped before.
- **Hard-won schema gotchas:**
  - Deaths arrive as `DieAction` inside Turns, not in `Round.diedIds`. Otherwise "alive"
    counts are cumulative spawns.
  - `TimelineMarker.team()` is 0-based while everything else is 1-based.
  - Indicator strings print only inside `--from/--to`, and the footer reports
    seen/printed/withheld counts so "0 occurrences" cannot silently mean "didn't look".
  - Per-robot state in replays is recorded **after** the action (doctrine 18).
- **Wrapper `tools/replay-dump.sh`:** runs on the VM and caches compiled classes under a key
  made from the SHA of `ReplayDump.java` plus the engine version. That cut the time per
  replay from 71 s (ssh, scp, javac each time) to well under a second. `--vm '<glob>'` dumps
  replays already on the VM with no copying.
- **For 2024:** expect a different `Round` layout. Older engines stored per-round data in
  columnar arrays (robot ID vectors plus parallel action vectors) rather than per-Turn unions.
  Run `javap -p battlecode.schema.Round` / `Turn` / `Action` against the 2024 jar first, then
  port the event loop. The GameWrapper/EventWrapper skeleton, the gunzip step, the caching
  wrapper and the self-reporting footers carry over unchanged. Python `flatbuffers` with
  `flatc`-generated code from the 2024 `schema/battlecode.fbs` is an alternative, but using
  the jar's own classes guarantees the schema matches the engine.

---

## 4. Bot architecture patterns worth reusing
Full source: `agents/darla/src/darla/RobotPlayer.java` (959 lines, one file) and the
multi-file `agents/bob/src/bob/{RobotPlayer,G,Nav,Soldier,Splasher,Mopper,Tower}.java`.
- **Every turn goes through `monitorAndYield(startRound, state)`**
  (`darla/RobotPlayer.java:255`). A confirmed overrun is `endRound != startRound`. A near miss
  is more than 80% of `ROBOT_BYTECODE_LIMIT`. It tracks max bytecodes used and writes one
  indicator string with `[BUILD] bc=used/limit max= ov= nm=` and **every mechanism counter**,
  then `Clock.yield()`. Replays thereby become the census database.
- **A `BUILD` constant is stamped into the indicator string.** `make-arm.sh` rewrites it, so
  every replay says which arm played.
- **An RNG seeded from the robot ID**: `new Random(rc.getID()*7919+13)`. It is not correlated
  with team, which keeps play symmetric.
- **Try/catch around per-type dispatch**: `GameActionException` becomes state `GAE:...`, so
  one exception never kills a robot. Exceptions are counted per game in the gauntlet `EXC`
  column.
- **Tuning knobs are `static final` constants with comments recording their provenance**, for
  example `// ACCEPTED iteration 1: 97-53 ... (+3.59 sd)`. Each dose-able mechanism has a
  **zero value that gives byte-identical play**, so the zero arm doubles as the identity check.
- **Small fixed-capacity memory:** arrays keyed on `(x<<6)|y` with ring eviction, for example
  a ban list `banKey/banUntil` with `BAN_CAP` 8 and tower memory `TOWER_MEM` 12. Each counts
  its own saturation (`banPeak`) so capacity limits are measured, not guessed.
- **Stuck detection with an escape hatch:** counters `mvTry`/`mvStuck` plus a bug-direction
  fallback only when stuck. This was iteration 3, +15 on 450 keys; full bug navigation lost 14.
- **Self-calibrating thresholds beat fixed constants** for behaviour that varies by opponent.
  This was found independently in BC22 and BC26 (`TRAINING_ALGORITHM.md` §5).
- **Snapshots as sibling packages** (`src/<bot>_iterN`), all compiled into one build so the
  gauntlet can play any pair from `build/classes`. Separate arms become separate packages.

---

## 5. Gotchas and process rules for day one

### 5.1 Multi-agent against single lineage (what 2025 concluded)
- **Run one lineage.** 2025 retired all three isolated agents and got its whole gain from one
  lineage that could read everything. Isolation protected a measurement (the tournament),
  but the measurement was self-referential, and it cost large amounts of tooling and tokens.
- If more than one agent is used, use it for **parallel arms inside one lineage**: separate
  arms, one shared notebook, one accept authority. Do not run competing lineages. Never let
  two sessions own one workspace. `TaskStop` the old agent before any relaunch; a
  "completed" status is only a snapshot, since agents resume when their background children
  finish.
- **Prefer resuming over cold starts.** Cycle context at about 250k tokens and hand off
  through a short closure map and ledger, not the long log (`MULTI_AGENT.md` §Agent context
  cycling).
- **Retired or frozen bots are opponents, not a target.** A roster built from our own family
  shares our blind spots (doctrine 17). Rank opponents: external bots near parity first, then
  frozen self-snapshots, and self-play only as a catastrophe check.

### 5.2 Measurement rules to install on day one
1. **Verify determinism** (run twice, diff) and **run a mirror null**. Regenerate the mirror
   from the *current* baseline every time.
2. **Error bars are over maps**, and results are quoted in games and swept maps, not binomial sd.
3. **Pair everything** on (opponent, map, side) and use McNemar on discordant keys. Keep a
   **pinned** map set for pairing and **fresh random samples** as the held-out check.
4. **Pre-register** the gate, the falsifier counters and the dose ladder with a zero arm
   *before* launch. A falsifier must be a quantity the arm cannot satisfy by doing nothing.
5. **Get an external benchmark into the accept rule early.** The fresh held-out sample is the
   tiebreak, and expect in-pool gains to shrink to about 60% on held-out data.
6. Benchmark on **every accept**, and keep a records-only `HISTORY.md`.
7. **A one-map identity check before any full run.** Count byte-identical (opponent, map,
   side) cells between arms; if all are identical, the change never executed.
8. **Measure the win condition first.** In 2025, coverage decided 96.5% of games. Count
   `winType` across a gauntlet before choosing a thesis.
9. **Trace for absolute degeneracy** (unspent resources, stuck units, idle turns) rather than
   comparisons against the opponent. Check you are *behind* on a metric before optimising it.
10. **Run an API sweep** with `javap` at iterations 0, 5 and every 10 after. Pin the engine
    version in `engine_version.txt` and refuse mismatched jars.

### 5.3 Infrastructure gotchas
- **Raw `java battlecode.server.Main`** with `-Dbc.server.mode=headless`, not `./gradlew run`.
  Build **once** per run. Never build in a directory a gauntlet is running from, because it
  rewrites classes under running games.
- **Run IDs:** claim the directory with an atomic `mkdir`. Identify runs from the runner's
  stdout, never from `ls -t`. Record `bot.txt` and `maps.txt` at launch.
- **Scripts:** re-exec shell scripts from a private copy so they can be edited mid-run. Escape
  quotes in heredocs that go over ssh. `grep -q` under `pipefail` can fail *because* it
  matched.
- **Capacity:** a flock semaphore for game slots; 50 GB of VM disk; auto-prune `losses/*.bc24`.
- **Persistence:** `setsid` the runners, never re-run a finished run (collect it instead), and
  push often; 31 commits once sat unpushed.
- **Monitoring:** filter on failure signatures as well as success (`ARM ERROR`, `QUEUE IDLE`);
  a monitor that only watches for success is silent through a crash.
- **Git:** stage explicit paths only, never `git add -A`.
- **Idleness:** never stop to "wait on the gauntlet". Poll it and do non-blocking work.
  Two of three agents ended their first session idle.

### 5.4 2025-specific notes (one line each, do not transfer)
- BC25 units were soldier, mopper, splasher and paint/money/defense towers; the win condition
  was paint coverage.
- Accepted levers were a splasher-heavy mix, not queueing at dry towers, a movement escape
  hatch, the money-tower rule, and a short ruin-ban window.
