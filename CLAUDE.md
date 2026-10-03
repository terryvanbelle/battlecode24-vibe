# battlecode24-vibe: session rules

Read `TRAINING_ALGORITHM.md` (the loop), `RULES.md` (the game, engine-checked), `HANDOFF.md` (state).

1. **Games in volume run only on the VM `battlecode-dev`** (`tools/vm-run.sh`). The driver (2 vCPU, 2 GB)
   hosts this session and may play one diagnostic game at a time.
2. **Push after every commit**; **record every user prompt verbatim in `PROMPTS.md`**.
3. **External bots' source is never read** (`BENCHMARK.md`). Their games may be reviewed.
4. **External bots are played only as scrimmages** (`tools/scrim.sh`). Never choose a map or side against one.
5. **No gate before a diagnostic game shows the mechanism firing.**
6. **`tools/unit-tests.sh` after every change** to the bot or any tool.
7. Bot changes need no approval. Never stop to wait for ideas.
8. Nothing stale stays: charts and documents that no longer match the data are regenerated or deleted.
9. **Post-mortems from 2024 are never read**, first- or second-hand.
10. 2024 replays do not contain robot stdout: in-game counters go into indicator strings (64 chars) and
    diagnostic logs come from engine stdout captured by `tools/run-dev.sh` (`LOG_OUT=`).
11. **TACTICS.md is updated after every ladder run** (owner standing order, PROMPTS 18) and must be comprehensive:
    `tools/post-block.sh` re-runs `tools/tactics-survey.py` and regenerates the measured section; every tactic an
    opponent that beats us uses gets a row with evidence, Adoption status and Neutralization status (PROMPTS 21).
12. **The VM runs a standing queue** (`tools/vm-queue.sh`, started once with `tools/vm-run.sh queue-runner
    'tools/vm-queue.sh'`). Submit work with `tools/vm-enqueue.sh <name> '<cmd>'`; set the idle filler with
    `FILLER=1 tools/vm-enqueue.sh`. Never launch a second experiment beside a running one with vm-run.sh; enqueue
    it. Measured 2026-10-01: CPUs saturated while games run (7 jobs on 8 vCPUs, load ~19) but ~29% of VM uptime idle
    between runs; the queue and filler remove that gap.
13. **TRAINING_ALGORITHM §3 step 5 is mandatory, no shortcuts** (PROMPTS 66-67): no band test, gate or ladder block
    for an arm until its delivery mini-block passes (`tools/delivery-gate.sh`; `tools/band-test.sh` refuses otherwise).
14. **Ladder target and idle filler** (PROMPTS 73, 120, 157-159): one ladder opponent at a time, usually the bot just
    above us (another higher bot if it is the better target); when it ranks below us, pick the next without asking.
    Defeated so far: ColtG5 (g_iter2, declared by the owner), winkelmantanner.waffle (g_iter3 1977 > waffle 1964; the
    crack in research/CRACK-WAFFLE.md). Target now: **CyrilSharma.finalBot** (2020, rank 11; g_iter3 10-14). The filler
    plays the target on fresh seeds: `FILLPOOL=<target> tools/filler-pair.sh <incumbent> <candidate or -> 40` (random
    maps and sides); at every task check run `tools/collect-fillers.sh <incumbent> <candidates>`. Update the filler's
    candidate whenever an arm against the target passes delivery.
15. **Basics first** (PROMPTS 127, 137): symmetry, movement, combat and economy (plus bytecode and exceptions) are the
    foundation. Every build and test block gets the basics battery; a failed basics bar stops work above it until fixed;
    when progress stalls, check the basics first. Unit tests include the dead-code check (tools/deadcode.py) and the
    arm-intent check (tools/arm-intent.txt). Symmetry must be decided by observation, never guessed.
16. **Incumbent g_iter3 since 2026-10-03** (g2cr: g_iter2 + carrier stun + flag relocation V2; seeded band pairs vs
    g_iter2 net +11 then +11 on fresh seeds, pooled +22 of 473, sign p 0.017, capture delta +0.25 +- 0.07; basics PASS).
    src/bot with its defaults plays as g_iter3 (src/g_iter3 is the frozen copy); new arms flip switches from these
    defaults and are paired against g_iter3 on shared engine seeds.
