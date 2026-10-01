# HANDOFF.md

## State (update at every accept)

- Incumbent: **g_iter1** — ladder 1756 ± 88, rank 14 of 57, field score 73.1% (110 games vs all 55 bots).
- Previous: g_iter0 1510 ± 78, rank 24.
- In flight: gate2 (VM, `gauntlet/gate2.log`): paired mirror SPRT, candidate = g_iter1 + setup flag relocation.
- VM: `battlecode-dev2` in us-west2-a (the original `battlecode-dev` in us-west1-b is stopped and untouched;
  its zone had no e2-standard-8 capacity on 2026-09-30). Snapshot `battlecode-dev-snap-20260930` was the source.

- VM queue: `queue/pending/*.job` on battlecode-dev2 run in order by `tools/vm-queue.sh` (log
  `gauntlet/queue-runner.log`); idle filler = `tools/filler-pair.sh g_iter1 b1v2 40` (paired seeds, the stack's
  acceptance test), collected with `tools/collect-fillers.sh` at every task check.

## Gotchas

- 2024 replays have no robot stdout; use indicator strings (64 chars, first fields survive truncation).
- The engine's final tiebreak uses unseeded `Math.random()`; everything else is deterministic per seed.
- `examplefuncsplayer` ignores the engine seed (static `Random(6147)`), so its mirror games are identical
  across seeds.
- Driver game on DefaultSmall takes 2-4 minutes; never more than one game at a time here.
