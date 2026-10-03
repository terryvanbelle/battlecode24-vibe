# HANDOFF.md

## State (update at every accept)

- Incumbent: **g_iter3** (since 2026-10-03; code in `src/g_iter3`, and `src/bot` with its defaults plays the same).
  It is g_iter2 plus the waffle crack (research/CRACK-WAFFLE.md): C.CARRIER_STUN (a duck beside an enemy carrying our
  flag builds a stun near it, ahead on its way home; a frozen carrier cannot move) and C.RELOCATE_FLAGS + C.RELOC_V2
  (flags carried in setup away from the nearest enemy spawn under every live symmetry). Seeded band pairs vs g_iter2:
  +11, then +11 on the confirmation seeds; pooled 473 pairs net +22 (sign p 0.017), capture delta +0.25 +- 0.07,
  upper tier +0.18 +- 0.09; basics PASS. Ladder (progress/ELO.md): **1977 +- 33, rank 12 of 75**, above waffle (1964).
  Previous: g_iter2 1920 (audit fixes + observed symmetry, research/AUDIT-2026-10-02.md), g_iter1 1736, g_iter0.
- **Ladder target: CyrilSharma.finalBot** (2020, rank 11; g_iter3 10-14; CLAUDE rule 14). Defeated: ColtG5 (declared by
  the owner, PROMPTS 157), winkelmantanner.waffle (g_iter3 ranks above it; paired waffle read finishing on the filler).
- Measurement: paired tests share the engine seed per cell (`tools/scrim.sh` 4th field) since 2026-10-02 23:00 UTC; an
  identity control reads 0 of 80 discordant. Verdicts before that were mostly engine noise (research/RETEST.md).
- In flight: the last waffle filler seeds (g_iter2 vs g_iter3, victory read at 240 paired games), then the CyrilSharma
  study; g2crb (g_iter3 + BUDGET_V1 setup bank for carrier stuns) 5(a) queued. Re-test arms 2-8 (RETEST.md) all closed.
- VM: `battlecode-dev2` in us-west2-a (the original `battlecode-dev` in us-west1-b is stopped and untouched;
  its zone had no e2-standard-8 capacity on 2026-09-30). Snapshot `battlecode-dev-snap-20260930` was the source.
- VM queue: `queue/pending/*.job` on battlecode-dev2 run in order by `tools/vm-queue.sh` (log
  `gauntlet/queue-runner.log`); idle filler = `FILLPOOL=winkelmantanner.waffle tools/filler-pair.sh g_iter2 g_iter3 40`
  (finishing the waffle read; next: the CyrilSharma pool), collected with `tools/collect-fillers.sh g_iter2 g_iter3`.
- Every build and test block gets `tools/basics.py` (CLAUDE rule 15); unit tests include the dead-code and arm-intent
  checks.

## Gotchas

- 2024 replays have no robot stdout; use indicator strings (64 chars, first fields survive truncation).
- The engine's final tiebreak uses unseeded `Math.random()`; everything else is deterministic per seed.
- `examplefuncsplayer` ignores the engine seed (static `Random(6147)`), so its mirror games are identical
  across seeds.
- Driver game on DefaultSmall takes 2-4 minutes; never more than one game at a time here.
