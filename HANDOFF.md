# HANDOFF.md

## State (update at every accept)

- Incumbent: **g_iter2** (since 2026-10-03; code in `src/g_iter2`, and `src/bot` with its defaults plays the same).
  It is g_iter1 plus the correctness audit's basic fixes (research/AUDIT-2026-10-02.md: A1 own-flag alert, A2 flag-id
  symmetry, A4 unreachable enemies, A5/A6 stale enemy-flag registry, A7/A9 navigation) and symmetry decided by observation.
  Seeded band pairs vs g_iter1: +33 then +29 on fresh seeds (p < 0.001); upper-tier capture difference +2.8 / +3.8 SE.
  Ladder (progress/ELO.md): **1918 +- 35, rank 13 of 74**, above ColtG5 (1877). Previous: g_iter1 1762 (rank 28), g_iter0.
- Measurement: paired tests share the engine seed per cell (`tools/scrim.sh` 4th field) since 2026-10-02 23:00 UTC; an
  identity control reads 0 of 80 discordant. Verdicts before that were mostly engine noise (research/RETEST.md).
- In flight: the re-test queue on g_iter2 (research/RETEST.md: g2z2, g2icamp, g2fstun, ...), g2bc2 (a bytecode bail-out
  in the reachability search after one overrun), the ColtG5 "unambiguous victory" read at 240 games (research/CRACK.md).
- VM: `battlecode-dev2` in us-west2-a (the original `battlecode-dev` in us-west1-b is stopped and untouched;
  its zone had no e2-standard-8 capacity on 2026-09-30). Snapshot `battlecode-dev-snap-20260930` was the source.
- VM queue: `queue/pending/*.job` on battlecode-dev2 run in order by `tools/vm-queue.sh` (log
  `gauntlet/queue-runner.log`); idle filler = `FILLPOOL=ColtG5.Goob_final tools/filler-pair.sh g_iter1 g1basics 40`
  (seeded pairs vs ColtG5 for the crack; g1basics is g_iter2's code), collected with `tools/collect-fillers.sh`.
- Every build and test block gets `tools/basics.py` (CLAUDE rule 15); unit tests include the dead-code and arm-intent
  checks.

## Gotchas

- 2024 replays have no robot stdout; use indicator strings (64 chars, first fields survive truncation).
- The engine's final tiebreak uses unseeded `Math.random()`; everything else is deterministic per seed.
- `examplefuncsplayer` ignores the engine seed (static `Random(6147)`), so its mirror games are identical
  across seeds.
- Driver game on DefaultSmall takes 2-4 minutes; never more than one game at a time here.
